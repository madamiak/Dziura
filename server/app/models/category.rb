# -*- encoding : utf-8 -*-

# Klasa kategorii
#
# Zawiera jedynie pola z bazy danych: name i icon
#
class Category < ActiveRecord::Base

  # has_many zamiast belongs ze względu na położenie klucza obcego
  has_many :issues

  # Nazwa obowiązkowa i nie może się powtarzać
  validates :name, :presence => true, :uniqueness => true

end
