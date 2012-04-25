# -*- encoding : utf-8 -*-

require 'test_helper'

class StatusesControllerTest < ActionController::TestCase

  setup do
    # "Hack" do logowania
    User.create(:login => "test", :password => "test", :role => "admin")
    session[:current_user_id] = User.where(:role => "admin").first.id

    # Testowe dane
    @status = Status.new(:name => "Testowy status", :color => "abcdef")
    @status.save!

    @new_status = Status.new (:name => "Nowy testowy status", :color => "123456")
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:statuses)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create status" do
    assert_difference('Status.count') do
      post :create, :status => @new_status.attributes
    end

    assert_redirected_to status_path(assigns(:status))
  end

  test "should show status" do
    get :show, :id => @status.id
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => @status.id
    assert_response :success
  end

  test "should update status" do
    put :update, :id => @status.id, :status => @status.attributes
    assert_redirected_to status_path(assigns(:status))
  end

  test "should destroy status" do
    assert_difference('Status.count', -1) do
      delete :destroy, :id => @status.id
    end

    assert_redirected_to statuses_path
  end
end
