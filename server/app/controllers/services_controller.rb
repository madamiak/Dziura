# -*- encoding : utf-8 -*-

# Kontroler wystawiający usługi dla klienta mobilnego
#
# Umożliwia zgłoszenie uszkodzenia i pobieranie aktualnych danych kategorii.
#
class ServicesController < ApplicationController
  skip_before_filter :require_login

  layout false

  # POST /res/issue
  # POST /res/issue.json
  #
  # Zapytanie dodające nowe zgłoszenie. Wywołuje Issue.add_issue z przekazanymi
  # parametrami.
  #
  # Parametry zapytania to (JSON; parametry HTTP POST - analogicznie):
  #  "category_id": (id kategorii zgłoszenia),
  #  "latitude": (szer. geogr.),
  #  "longitude": (dł. geogr.),
  #  *** "desc": (opis),
  #  *** "notificar_email": (e-mail zgłaszającego),
  #  "photos": (lista zdjęć)
  #  [
  #   {
  #    "image": (zdjęcie w Base64),
  #    "image_type": (typ MIME zdjęcia, np. "image/jpeg"),
  #    *** "markers": (lista znaczników)
  #    [
  #     { "x": (poz. x), "y": (poz. y), "desc": (opis) },
  #     ...
  #    ]
  #   },
  #   ...
  #  ]
  # *** oznacza parametr opcjonalny.
  def issue
    if request.post?
      begin
        instance = Issue.add_issue(params[:category_id], params[:longitude], params[:latitude],
                                params[:desc], params[:notificar_email], params[:photos])

        if !instance.nil?
          render :json => { :message => 'Zgłoszenie zostało przyjęte', :id => instance.id }
        else
          render :json => { :message => 'Błąd', :id => nil }, :status => 500
        end
      rescue ActiveRecord::RecordInvalid => e
        msg = ""
        e.record.errors.each { |attr,err| msg += "#{attr} - #{err}\n" }
        render :json => { :message => 'Błąd: ' + msg, :id => nil }, :status => 500
      rescue Exception => e
        render :json => { :message => 'Błąd: ' + e.message, :id => nil }, :status => 500
      end
    end
  end

  # GET /res/categories
  def categories
    @categories = Category.all
    render :json => @categories.to_json( :only => [:id, :name] )
  end

  # GET /res/categories/1
  def category
    @category = Category.find(params[:id])
    render :json => @category.to_json( :only => [:id, :name] )
  end

  # GET /res/category_icon/1
  def category_icon
    category = Category.find(params[:id])
    if !category.nil? && !category.icon.nil?
      # typ ikony to na sztywno image/jpeg
      send_data Base64.decode64(category.icon), :type => 'image/jpeg', :disposition => 'inline'
    end
  end

end
