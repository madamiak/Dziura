# -*- encoding : utf-8 -*-

# Klasa punktu
#
# Para współrzędnych geograficznych; opisuje wierzchołek wielokąta.
#
# === Pola
# [number] nr w sekwencji
# [latitude] szerokość geograficzna, +BigDecimal+, wymagane
# [longitude] długość geograficzna, +BigDecimal+, wymagane
# [polygon] wielokąt
#
class Point < ActiveRecord::Base

  belongs_to :polygon

  validates :number, :presence => true
  validates :latitude, :presence => true
  validates :longitude, :presence => true

  validates_numericality_of :number, :longitude, :latitude
  validates_inclusion_of :longitude, :in => -180..180
  validates_inclusion_of :latitude, :in => -90..90

  # Porównanie (tylko po współrzędnych)
  def ==(other)
    return latitude == other.latitude && longitude == other.longitude
  end

end
