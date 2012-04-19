# -*- encoding : utf-8 -*-

# Klasa statusu zgłoszenia
#
# === Pola
# [name] nazwa, +string+, wymagane, nie może się powtarzać
#
class Status < ActiveRecord::Base

  # nazwa obowiązkowa i nie może się powtarzać
  validates :name, :presence => true, :uniqueness => true

  has_many :issues

  # Formatuje wiadomość w logach o zmiane statusu
  def get_log_message(old_status)
    I18n.t(:status_change_message, :old => old_status.name, :new => name )
  end

  # Zwraca domyślny status
  def self.get_default_status
    Status.find_or_create_by_name("Nowy")
  end

end
