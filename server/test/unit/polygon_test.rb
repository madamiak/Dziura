require 'test_helper'

class PolygonTest < ActiveSupport::TestCase

  fixtures :polygons, :points

  test "points inside/outside 1. polygon" do

    poly = polygons(:polygon_1)
    poly.print()

    p = points(:p1_in)
    puts "Point inside: #{p.latitude}, #{p.longitude}"
    assert poly.pointInside(p)

    p = points(:p1_out)
    puts "Point outside: #{p.latitude}, #{p.longitude}"
    assert (!poly.pointInside(p))

  end

  test "points inside/outside 2. polygon" do

    poly = polygons(:polygon_2)
    poly.print()

    points_inside = [ points(:p2_in1), points(:p2_in2), points(:p2_in3) ]
    points_inside.each do |p|
      puts "Point inside: (#{p.latitude}, #{p.longitude})"
      assert poly.pointInside(p)
    end

    points_outside = [ points(:p2_out1), points(:p2_out2), points(:p2_out3), points(:p2_out4), points(:p2_out5) ]
    points_outside.each do |p|
      puts "Point outside: #{p.latitude}, #{p.longitude}"
      assert (!poly.pointInside(p))
    end

  end

end
