# -*- encoding : utf-8 -*-
require 'test_helper'

class MainControllerTest < ActionController::TestCase

  setup do
    # "Hack" do logowania
    User.create(:login => "test", :password => "test", :role => "admin")
    session[:current_user_id] = User.where(:role => "admin").first.id
  end

  test "should get index" do
    get :index
    assert_response :success
  end

end
