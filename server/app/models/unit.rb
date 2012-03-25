# -*- encoding : utf-8 -*-
class Unit < ActiveRecord::Base
  has_many :polygons
  has_many :issues
  has_many :users
  belongs_to :address 

  accepts_nested_attributes_for :address

  validates :name, :address, :presence => true

  # Ustawia wielokąty z danych w formacie JSON
  def set_polygons_json(json_data)

    new_polys = []

    begin
      polys = ActiveSupport::JSON.decode(json_data)
      polys.each do |poly|
        railsPoly = Polygon.new()

        poly['points'].each do |pt|
          railsPt = Point.new( :number => pt['number'],
                               :latitude => BigDecimal(pt['latitude'].to_s()),
                               :longitude => BigDecimal(pt['longitude'].to_s()) )
          railsPt.save()
          railsPoly.points << railsPt;
        end

        new_polys << railsPoly;
      end
    rescue
      self.errors << "Nieprawidłowe dane obszaru!"
      return false
    end

    self.polygons.clear
    self.polygons = new_polys
    return true
  end


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
