# -*- encoding : utf-8 -*-

require 'test_helper'

class UnitsControllerTest < ActionController::TestCase
  setup do
    # "Hack" do logowania
    User.create(:login => "test", :password => "test", :role => "admin")
    session[:current_user_id] = User.where(:role => "admin").first.id

    # Testowe dane
    @unit = Unit.new(:name => "Testowa jednostka")
    @unit.address = Address.create(:city => "Wrocław", :street => "Jakas",  :home_number => "1")
    @unit.save

    polygon = Polygon.new(:unit => @unit)

    Point.create([
              { :polygon_id => polygon, :number => 1,
                :latitude => BigDecimal.new("51.0518656872652"),
                :longitude => BigDecimal.new("16.96879945983892") },
              { :polygon_id => polygon, :number => 2,
                :latitude => BigDecimal.new("51.04366386205539"),
                :longitude => BigDecimal.new("16.994891989135795") },
              { :polygon_id => polygon, :number => 3,
                :latitude => BigDecimal.new("51.0540238209373"),
                :longitude => BigDecimal.new("17.027164328002982") },
              { :polygon_id => polygon, :number => 4 }
                 ])
    polygon.points << Point.all
    polygon.save

    @new_unit = Unit.new
    @new_unit.name = "Nowa testowa jednostka"
    @new_unit.address = Address.create(:city => "Wrocław", :street => "Testowa",  :home_number => "12")

    @new_unit_polygons = '[{"points":[{"number":1,"latitude":51.10605831884497,"longitude":17.026134359741263},{"number":2,"latitude":51.10228576421984,"longitude":17.06544481506353},{"number":3,"latitude":51.12082221437953,"longitude":17.073856222534232},{"number":4,"latitude":51.12556290272885,"longitude":17.033859121704154},{"number":5,"latitude":51.11866719520387,"longitude":17.01446138610845}]}]'
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:units)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create unit" do
    assert_difference('Unit.count') do
      post :create, { :unit => @new_unit.attributes, :polygons => @new_unit_polygons }
    end

    assert_redirected_to unit_path(assigns(:unit))
  end

  test "should show unit" do
    get :show, :id => @unit.id
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => @unit.id
    assert_response :success
  end

  test "should update unit" do
    put :update, { :id => @unit.id, :unit => @unit.attributes, :polygons => @new_unit_polygons }
    assert_redirected_to unit_path(assigns(:unit))
  end

  test "should destroy unit" do
    assert_difference('Unit.count', -1) do
      delete :destroy, :id => @unit.id
    end

    assert_redirected_to units_path
  end
end
