# -*- encoding : utf-8 -*-

require 'net/http'

# Klasa adresu
#
# Oprócz pól modelu RoR zawiera metodę self.create_by_position, wykorzystującą
# API geolokacji Google do zamiany współrzędnych geograficznych na adres
#
class Address < ActiveRecord::Base

  validates :city, :street, :home_number, :presence => true

  # Zapytanie GET z parametrami
  # Wykorzystywane tylko tutaj, stąd jako prywatna metoda
  def self.http_get(domain, path, params)

    if params.nil?
      return Net::HTTP.get_response(domain, path)
    end

    paramedPath = "#{path}?".concat(params.collect { |k,v| "#{k}=#{CGI::escape(v.to_s)}" }.join('&'))
    return Net::HTTP.get_response(domain, paramedPath)

  end

  private_class_method :http_get

  # Funkcja używa Geocoding API Google'a
  # Zwraca utworzony adres albo nil jeżeli Google nie zwróciło adresu
  # Wyjątek NetworkException jeżeli HTTP wyrzuci jakiś błąd
  # W additional_info jest jeszcze zwracany pełny adres
  #  na wypadek jakby coś nie wyszło z parsowaniem
  def self.create_by_position(latitude, longitude)

    params = { :latlng => latitude.to_s() + "," + longitude.to_s(),
               :region => "pl",
               :language => "pl",
               :sensor => "false" }
    response = http_get("maps.googleapis.com", "/maps/api/geocode/json", params)

    if response.code != "200"
      raise Exceptions::NetworkException.new()
    end

    data = ActiveSupport::JSON.decode(response.body)

    if (data["status"] != "OK")
      return nil
    end

    address = Address.new()

    # Google zwraca dużo danych, więc trzeba wybrać, to co nas interesuje
    data["results"].each do |r|

      next if ! r["types"].include? "street_address"

      r["address_components"].each do |c|

        if c["types"].include? "locality"
          address.city = c["long_name"]

        elsif c["types"] == [ "postal_code" ]
          address.zip = c["long_name"]

        elsif c["types"].include? "route"
          address.street = c["long_name"]

        elsif c["types"].include? "street_number"
          address.home_number = c["long_name"]

        end

      end

      address.additional_info = r["formatted_address"]

      break

    end

    address.save!

    return address

  end

end
