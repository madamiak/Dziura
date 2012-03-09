require 'test_helper'

class IssueTest < ActiveSupport::TestCase
  def setup
    @s_old = Status.new :name => "old"
    @s_old.save
    @s_new = Status.new :name => "new"
    @s_new.save
    
    @c = Category.new :name => "dziury"
    @c.save
    
    @u = Unit.new 
    @u.save
    
    @i = Issue.new :status => @s_new, :category => @c, :unit => @u
    @i.save
    
    @u = User.new
    @u.save
  end
  
  test "log_status_change" do
    @i.log_status_change @u, @s_old
    l = Log.last
    
    assert_equal l.user, @u
    assert_equal l.message, I18n.t(:status_change_message, 
      :old => "old", :new => "new" ) 
    end
end
