# -*- encoding : utf-8 -*-

require 'test_helper'

class CategoryTest < ActiveSupport::TestCase

  test "unique names" do
    c1 = Category.new(:name => "kat")
    c1.save!
    c2 = Category.new(:name => "kat")
    assert(! c2.valid?)
  end

end
