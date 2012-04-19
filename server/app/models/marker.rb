# -*- encoding : utf-8 -*-

# Klasa markera na obrazku
#
class Marker < ActiveRecord::Base

  belongs_to :photo

  validates :x, :y, :presence => true

end
