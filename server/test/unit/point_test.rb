require 'test_helper'

class PointTest < ActiveSupport::TestCase

  test "points should be the same" do
    p1 = Point.new(:number => 1,
                   :latitude => BigDecimal.new("11.23"),
                   :longitude => BigDecimal.new("45.67"))
    p2 = Point.new(:number => 2,
                   :latitude => BigDecimal.new("11.23"),
                   :longitude => BigDecimal.new("45.67"))
    assert (p1 == p2)
  end

end
