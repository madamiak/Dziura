# -*- encoding : utf-8 -*-

# Klasa wielokątu (obszaru jednostki)
#
class Polygon < ActiveRecord::Base

  belongs_to :unit
  has_many :points

  validate :must_have_at_least_3_points, :points_must_be_unique
  validates_associated :points

  # Walidator
  # Wielokąt musi mieć >= 3 wierchołki
  def must_have_at_least_3_points
    if points.length < 3
      errors.add(:points, "Wielokąt musi mieć co najmniej 3 punkty")
    end
  end

  # Walidator
  # Wierzchołki muszą być unikalne
  def points_must_be_unique
    usedPoints = []
    usedNumbers = []
    points.each do |p|
      if usedPoints.index(p) != nil
        errors.add(:points, "Punkty muszą być unikalne")
        return
      end
      if usedNumbers.index(p.number) != nil
        errors.add(:points, "Punkty muszą mieć unikalne nry")
        return
      end
      usedPoints << p
      usedNumbers << p.number
    end
  end

  # Wyświetla postać wielokąta
  # Wykorzystywane w testach
  def print
    myPoints = self.points.sort_by(&:number)

    puts "Polygon:"
    for i in 0..myPoints.length-1
      puts "P#{myPoints[i].number} : #{myPoints[i].latitude}, #{myPoints[i].longitude}"
    end
  end

  # Zwraca true, jeżeli dany punkt należy do wielokąta
  # point to obiekt klasy Point
  def point_inside(point)

    myPoints = self.points.sort_by(&:number)

    for i in 0..myPoints.length-1
      if (point == myPoints[i])
        return true
      end
    end

    count = 0

    for i in 0..myPoints.length-1

      a = myPoints[i]
      b = myPoints[(i+1)%myPoints.length]

      x = a.longitude + (point.latitude - a.latitude) * (b.longitude - a.longitude) / (b.latitude - a.latitude)

      ab_x = [ a.longitude, b.longitude].sort();

      if ( (x >= point.longitude) && (x >= ab_x[0]) && (x <= ab_x[1]) )
        count = count + 1
      end

    end

    return count % 2 == 1

  end

end
