# -*- encoding : utf-8 -*-
require 'test_helper'

class ServicesControllerTest < ActionController::TestCase

  setup do
    # "Hack" do logowania
    User.create(:login => "test", :password => "test", :role => "admin")
    session[:current_user_id] = User.where(:role => "admin").first.id

    Unit.create(:name => "test",
                :address => Address.new(:city => "test", :street => "abc", :home_number => 1),
                :polygons => [ Polygon.new( :points =>
      [ Point.new(:number => 1, :latitude => BigDecimal.new("0.0"), :longitude => BigDecimal.new("0.0")),
        Point.new(:number => 2, :latitude => BigDecimal.new("0.0"), :longitude => BigDecimal.new("10.0")),
        Point.new(:number => 3, :latitude => BigDecimal.new("10.0"), :longitude => BigDecimal.new("10.0")),
        Point.new(:number => 4, :latitude => BigDecimal.new("10.0"), :longitude => BigDecimal.new("0.0"))
      ] ) ] )

    c = Category.new
    c.id = 100
    c.name = "Dziura"
    c.save!
  end

  test "should get categories" do
    get :categories

    assert_response :success

    cats = JSON.parse(@response.body)
    assert_equal "Dziura", cats[0]['name']
    assert_equal 100, cats[0]['id']
  end

  test "should create issue" do
    assert_difference('Issue.count') do
      post :issue, { :latitude => "5.0", :longitude => "5.0", :category_id => "100" }
     end
  end

end
