# -*- encoding : utf-8 -*-

require 'test_helper'

class StatusTest < ActiveSupport::TestCase

  test "unique names" do
    s1 = Status.new(:name => "stat", :color => "000000")
    s1.save!
    s2 = Status.new(:name => "stat", :color => "000000")
    assert(! s2.valid?)
  end

  test "color format" do
    s1 = Status.new(:name => "test", :color => "a12Def")
    assert(s1.valid?)

    s2 = Status.new(:name => "test", :color => "abCDef1")
    assert(! s2.valid?)

    s3 = Status.new(:name => "test", :color => "12345g")
    assert(! s3.valid?)

    s4 = Status.new(:name => "test", :color => "")
    assert(! s4.valid?)

    s5 = Status.new(:name => "test")
    assert(! s5.valid?)
  end

end
