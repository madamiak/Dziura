# -*- encoding : utf-8 -*-

require 'test_helper'

class CategoryTest < ActiveSupport::TestCase

  test "unique names" do
    c1 = Category.create(:name => "kat")
    c1.save!
    c2 = Category.create(:name => "kat")
    assert(! c2.valid?)
  end

end
