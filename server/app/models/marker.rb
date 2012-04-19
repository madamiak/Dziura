# -*- encoding : utf-8 -*-

# Klasa znacznika na zdjęciu
#
# === Pola
# [x] pozycja X, +integer+, wymagane, zakres to [1, szer. zdjęcia w px]
# [y] pozycja Y, +integer+, wymagane, zakres to [1, wys. zdjęcia w px]
# [desc] opis, +string+
# [photo] zdjęcie
#
class Marker < ActiveRecord::Base

  belongs_to :photo

  validates :x, :y, :presence => true

end
