# -*- encoding : utf-8 -*-

require 'test_helper'

class PolygonTest < ActiveSupport::TestCase

  test "points inside/outside 1. polygon" do

    # Punkty 1. testowego wielokąta
    polyCoords = [ { :lat => "0"    , :lng => "0"     },
                   { :lat => "1.23" , :lng => "12.34" },
                   { :lat => "10.23", :lng => "15.67" },
                   { :lat => "8.87" , :lng => "-1.23" } ]

    polyPoints = []
    i = 0
    polyCoords.each do |c|
      i = i + 1
      polyPoints.push( Point.new(:number => i,
                                 :longitude => BigDecimal.new(c[:lng]),
                                 :latitude => BigDecimal.new(c[:lat])) )
    end

    poly = Polygon.new(:points => polyPoints)

    # Sprawdzenie punktu w środku
    p = Point.new(:longitude => BigDecimal.new("5"), :latitude => BigDecimal.new("5"))
    assert poly.point_inside(p)

    # Sprawdzenie punktu na zewnątrz
    p = Point.new(:longitude => BigDecimal.new("-1"), :latitude => BigDecimal.new("-1"))
    assert !poly.point_inside(p)

  end

  test "points inside/outside 2. polygon" do

    # Punkty 2. wielokąta
    polyCoords = [ { :lat => "51.09544048621148" , :lng => "17.010341513061576" },
                   { :lat => "51.111501033635186", :lng => "17.02321611633306"  },
                   { :lat => "51.112039881416024", :lng => "17.040038931274466" },
                   { :lat => "51.121953559206204", :lng => "17.043815481567435" },
                   { :lat => "51.121953559206204", :lng => "17.06338487854009"  },
                   { :lat => "51.10977667851392" , :lng => "17.061839926147513" },
                   { :lat => "51.10460322710782" , :lng => "17.04621874084478"  },
                   { :lat => "51.09360772011177" , :lng => "17.02596269836431"  } ]

    polyPoints = []
    i = 0
    polyCoords.each do |c|
      i = i + 1
      polyPoints.push( Point.new(:number => i,
                                 :longitude => BigDecimal.new(c[:lng]),
                                 :latitude => BigDecimal.new(c[:lat])) )
    end

    poly = Polygon.new(:points => polyPoints)

    # Punkty testowe wewnątrz wielokąta
    insideCoords = [ { :lat => "51.11635043747407" , :lng => "17.051883566284232" },
                     { :lat => "51.104387654065874", :lng => "17.027164328002982" },
                     { :lat => "51.1030941947017"  , :lng => "17.033859121704154" } ]

    insideCoords.each do |c|
      p = Point.new(:latitude => BigDecimal.new(c[:lat]), :longitude => BigDecimal.new(c[:lng]))
      assert poly.point_inside(p)
    end

    # Punkty testowe na zewnątrz wielokąta
    outsideCoords = [ { :lat => "51.11538059741992" , :lng => "17.03394495239263"  },
                      { :lat => "51.102124076426925", :lng => "17.04939447631841"  },
                      { :lat => "51.10406429261789" , :lng => "17.011800634765677" },
                      { :lat => "51.0902654302129"  , :lng => "17.018838751220756" },
                      { :lat => "51.1180745473075"  , :lng => "17.070508825683646" } ]

    outsideCoords.each do |c|
      p = Point.new(:latitude => BigDecimal.new(c[:lat]), :longitude => BigDecimal.new(c[:lng]))
      assert (!poly.point_inside(p))
    end

  end

  test "polygon validation test" do

    points = [ Point.new(:number => 1, :latitude => BigDecimal.new("12.443"),
                         :longitude => BigDecimal.new("132.11")),
               Point.new(:number => 1, :latitude => BigDecimal.new("12.44322"),
                         :longitude => BigDecimal.new("132.11")),
               Point.new(:number => 2, :latitude => BigDecimal.new("-12.44322"),
                         :longitude => BigDecimal.new("-132.11")),
               Point.new(:number => 3, :latitude => BigDecimal.new("-12.44322"),
                         :longitude => BigDecimal.new("-132.11")),
               Point.new(:number => 3, :latitude => BigDecimal.new("2.44322"),
                         :longitude => BigDecimal.new("-132.11")) ]

    # za mało punktów
    p = Polygon.new()
    assert !p.valid?
    p = Polygon.new(:points => [ points[0] ])
    assert !p.valid?
    p = Polygon.new(:points => [ points[0], points[2] ])
    assert !p.valid?

    # take same nry
    p = Polygon.new(:points => [ points[0], points[1], points[2] ])
    assert !p.valid?

    # takie same współrzędne
    p = Polygon.new(:points => [ points[0], points[2], points[3] ])
    assert !p.valid?

    # wszystko ok
    p = Polygon.new(:points => [ points[0], points[2], points[4] ])
    assert p.valid?

  end

end
