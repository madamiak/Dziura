# -*- encoding : utf-8 -*-

# Klasa kategorii
#
# Zawiera jedynie pola modelu RoR
#
# === Pola
# [name] nazwa, +string+, wymagane, nie może się powtarzać
# [icon] ikona, +string+, obrazek JPEG zakodowany w Base64
#
class Category < ActiveRecord::Base

  # has_many zamiast belongs ze względu na położenie klucza obcego
  has_many :issues

  validates :name, :presence => true, :uniqueness => true

end
