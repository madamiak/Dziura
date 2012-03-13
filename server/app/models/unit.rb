class Unit < ActiveRecord::Base
  has_many :polygons
  has_many :issues
  has_many :users
  belongs_to :address 
  
  validates :name, :address, :presence => true
  
  # Zwraca true jezeli punkt należy do tej jednostki, false przeciwnie
  def point_included?(x, y)
    polygons.each do |polygon|
      p = Point.new :longitude => x, :latitude => y
      return true if polygon.point_inside(p)      
    end
    
    return false
  end
  
  # Zwraca unit do ktorego nalezy punkt, nil przeciwnie
  def self.find_unit_by_point(x, y)
    Unit.all.each do |unit|
      return unit if unit.point_included?(x, y) 
    end

    return nil
  end 
end
