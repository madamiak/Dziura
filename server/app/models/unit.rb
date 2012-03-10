class Unit < ActiveRecord::Base
  has_many :polygons
  belongs_to :address
end
