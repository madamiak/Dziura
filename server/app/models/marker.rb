# -*- encoding : utf-8 -*-

# Klasa markera na obrazku
#
class Marker < ActiveRecord::Base

  belongs_to :photo

  # TODO walidator, ktory sprawdza czy x,y nie sa wieksze od rozmiarow
  # obrazka

  validates :x, :y, :presence => true

end
