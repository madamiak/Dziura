# -*- encoding : utf-8 -*-

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

  test "invalid point data" do

    p = Point.new :number => nil, :latitude => BigDecimal.new("11"), :longitude => BigDecimal.new("12")
    assert !p.valid?

    p = Point.new :number => 1, :latitude => BigDecimal.new("100"), :longitude => BigDecimal.new("179")
    assert !p.valid?

    p = Point.new :number => 1, :latitude => BigDecimal.new("-89"), :longitude => BigDecimal.new("-181")
    assert !p.valid?

  end

end
