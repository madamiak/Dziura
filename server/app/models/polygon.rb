class Polygon < ActiveRecord::Base
  belongs_to :unit

  has_many :points

  validate :has_points?

  def has_points?
    errors.add_to_base "Polygon must have at least 3 points" if self.points.size < 3
  end

  def print()
    myPoints = self.points.sort_by(&:number)

    puts "Polygon:"
    for i in 0..myPoints.length-1
      puts "P#{myPoints[i].number} : #{myPoints[i].latitude}, #{myPoints[i].longitude}"
    end
  end

  def pointInside(point)

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
