# -*- encoding : utf-8 -*-

require 'test_helper'

class StatusTest < ActiveSupport::TestCase

  test "unique names" do
    s1 = Category.create(:name => "kat")
    s1.save!
    s2 = Category.create(:name => "kat")
    assert(! s2.valid?)
  end

end
