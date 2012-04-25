# -*- encoding : utf-8 -*-

# Klasa statusu zgłoszenia
#
# === Pola
# [name] nazwa, +string+, wymagane, nie może się powtarzać
# [color] kolor znacznika na mapie (format "01abcd"), +string+, wymagane
#
class Status < ActiveRecord::Base

  validates_presence_of :name, :message => 'Nazwa - pole obowiązkowe'
  validates_uniqueness_of :name, :message => 'Nazwy nie mogą się powtarzać'

  validates_presence_of :color, :message => 'Kolor - pole obowiązkowe'
  validates :color, :format => { :with => /\A[0-9A-Fa-f]{6}\Z/, :message => 'Błędny format koloru' }

  has_many :issues

  # Formatuje wiadomość w logach o zmiane statusu
  def get_log_message(old_status)
    I18n.t(:status_change_message, :old => old_status.name, :new => name )
  end

  # Zwraca domyślny status
  def self.get_default_status
    Status.find_or_create_by_name("Nowy", :color => "ff0000")
  end

end
