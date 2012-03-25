# -*- encoding : utf-8 -*-
require 'test_helper'

class UsersControllerTest < ActionController::TestCase

  setup do
    # "Hack" do logowania
    User.create(:login => "test", :password => "test", :role => "admin")
    session[:current_user_id] = User.where(:role => "admin").first.id

    # Testowe dane
    @user = User.new
    @user.login = "Użyszkodnik"
    @user.password = "p@ssw0rd"
    @user.role = "admin"
    @user.save!

    @new_user = User.new
    @new_user.login = "Nowy"
    @new_user.password = "p@ssw0rd2"
    @new_user.role = "test"
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:users)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  # powinno działać, a nie działa
  test "should create user" do
    assert_difference('User.count') do
      post :create, :user => @new_user.attributes
    end

    assert_redirected_to user_path(assigns(:user))
  end

  test "should show user" do
    get :show, :id => @user.id
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => @user.id
    assert_response :success
  end

  test "should update user" do
    put :update, :id => @user.id, :user => @user.attributes
    assert_redirected_to user_path(assigns(:user))
  end

  test "should destroy user" do
    assert_difference('User.count', -1) do
      delete :destroy, :id => @user.id
    end

    assert_redirected_to users_path
  end
end
