# -*- encoding : utf-8 -*-
require 'test_helper'

class UserTest < ActiveSupport::TestCase
  def setup
    @u = User.new :login => "a@a.pl", :password => "tajnehaslo123", 
      :role => "admin"
    @u.save
  end
  
  test "password_encrypt" do    
    assert @u.password_hash != "tajnehaslo123"
    assert !@u.valid_password?("tajnehaslo124")
    assert @u.valid_password?("tajnehaslo123")
  end
  
  test "password_change" do
    @u.password = "test"
    assert @u.save
    
    assert @u.password_hash != "test"
  end
  
  test "authenticate" do
    assert User.authenticate("a@a.pl", "tajnehaslo123")
    assert !User.authenticate("a@a.pl", "tajnehaslo")
  end
  
  test "login and password presence" do
    @u = User.new
    assert !@u.save
    
    @u = User.new :login => "b@b.pl"
    assert !@u.save
    
    @u = User.new :password => "b@b.pl"
    assert !@u.save
  end
end
