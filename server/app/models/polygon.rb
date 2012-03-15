class Polygon < ActiveRecord::Base
  belongs_to :unit

  has_many :points

  validate :must_have_at_least_3_points, :points_must_be_unique

  validates_associated :points

  def must_have_at_least_3_points
    if points.length < 3
      errors.add(:points, "Wielokąt musi mieć co najmniej 3 punkty")
    end
  end

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

  def print
    myPoints = self.points.sort_by(&:number)

    puts "Polygon:"
    for i in 0..myPoints.length-1
      puts "P#{myPoints[i].number} : #{myPoints[i].latitude}, #{myPoints[i].longitude}"
    end
  end

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
