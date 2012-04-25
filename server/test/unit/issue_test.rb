# -*- encoding : utf-8 -*-

require 'test_helper'

class IssueTest < ActiveSupport::TestCase

  def setup
    @s_old = Status.new :name => "old"
    @s_old.save
    @s_new = Status.new :name => "new"
    @s_new.save

    @cat_1 = Category.new :name => "dziury"
    @cat_1.save

    @cat_2 = Category.new :name => "znaki"
    @cat_2.save

    @cat_3 = Category.new :name => "cośtam"
    @cat_3.save

    @cat_4 = Category.new :name => "cośtam innego"
    @cat_4.save

    @points = [ Point.new(:number => 1, :latitude => BigDecimal.new("-1.0"), :longitude => BigDecimal.new("-1.0")),
                Point.new(:number => 2, :latitude => BigDecimal.new(" 1.0"), :longitude => BigDecimal.new("-1.0")),
                Point.new(:number => 3, :latitude => BigDecimal.new(" 1.0"), :longitude => BigDecimal.new(" 1.0")),
                Point.new(:number => 4, :latitude => BigDecimal.new("-1.0"), :longitude => BigDecimal.new(" 1.0")) ]

    @poly = Polygon.new :points => @points

    @addr = Address.new :city => "Wrocław", :street => "Jakaśtam", :home_number => 666

    @unit = Unit.new :name => "zdium", :address => @addr, :polygons => [ @poly ]
    @unit.save

    @i = Issue.new :status => @s_new, :category => @c, :unit => @unit
    @i.save

    @user = User.new :login => "a@a.pl", :password => "a"
    @user.save
  end

  test "log_status_change" do
    @i.log_status_change @u, @s_old
    l = Log.last

    assert_equal l.user, @u
    assert_equal l.message, I18n.t(:status_change_message, :old => "old", :new => "new" )
  end


  # Sprawdzanie wyjątku NilArguments
  test "add_issue nil arguments" do

    assert_raise Exceptions::NilArguments do
      Issue.add_issue(nil, "", "", "", "", nil)
    end

    assert_raise Exceptions::NilArguments do
      Issue.add_issue(@cat_1.id, nil, "", "", "", nil)
    end

    assert_raise Exceptions::NilArguments do
      Issue.add_issue(@cat_1.id, "", nil, "", "", nil)
    end

  end

  # Zgłoszenie poza obszarem jednostki
  test "add_issue outside unit area" do

    assert_raise Exceptions::NoUnitForPoint do
      Issue.add_issue(@cat_1.id, "10", "10", "bla", "bla@bla.com", nil)
    end

  end

  # Nieistniejąca kategoria
  test "add_issue invalid category" do

    assert_raise Exceptions::UnknownCategory do
      Issue.add_issue(15100900, "10", "10", "bla", "bla@bla.com", nil)
    end

  end

  # Nieprawidłowy e-mail
  test "add_issue invalid e-mail" do

    assert_raise Exceptions::IncorrectNotificarEmail do
      Issue.add_issue(@cat_1.id, "0", "0", "bla", "bla", nil)
    end

  end

  # Łączenie zgłoszeń
  test "add_issue joining issues" do

    # Wszystko OK
    i_1 = Issue.add_issue(@cat_1.id, "0.0", "0.0",
                          "bla", "bla@bla.com", nil)

    assert_not_nil(i_1)

    # Drugie zgłoszenie w pobliżu
    i_2 = Issue.add_issue(@cat_1.id, "0.000001", "0.0",
                          "bla bla", "bla@bla.com", nil)

    assert_not_nil(i_2)

    # Zgłoszenia powinny być złączone
    assert_equal(i_1.issue, i_2.issue)

    # Zgłoszenie poza obszarem złączania ma osobne issue
    i_3 = Issue.add_issue(@cat_1.id, "0.99", "-0.99",
                          "bla", "bla@bla.com", nil)

    assert_not_equal(i_1.issue, i_3.issue)

    # Zgłoszenie w pobliżu, ale w innej kategorii też ma mieć osobne issue
    i_4 = Issue.add_issue(@cat_2.id, "0.000001", "0.0",
                          "bla", "bla@bla.com", nil)
    assert_not_equal(i_1.issue, i_4.issue)

    # Zmiana statusu zgłoszenia
    i_1.issue.status = @s_old
    i_1.issue.save!

    # Nowe zgłoszenie w pobliżu
    i_5 = Issue.add_issue(@cat_1.id, "0.000001", "0.0",
                          "bla", "bla@bla.com", nil)
    assert_not_equal(i_1.issue, i_5.issue)

  end

  test "join_with" do

    # Instancje jednego issue
    i_11 = Issue.add_issue(@cat_1.id, "0.0", "0.0",
                          "bla", "bla@bla.com", nil)
    i_12 = Issue.add_issue(@cat_1.id, "0.000001", "0.0",
                          "bla bla", "bla@bla.com", nil)
    i_13 = Issue.add_issue(@cat_1.id, "-0.000001", "0.0",
                          "bla bla", "bla@bla.com", nil)

    # Instancje drugiego issue
    i_21 = Issue.add_issue(@cat_1.id, "0.500001", "0.000001",
                          "bla bla", "bla@bla.com", nil)
    i_22 = Issue.add_issue(@cat_1.id, "0.500001", "0.000001",
                           "bla bla", "bla@bla.com", nil)

    i_11.issue.join_with(i_21.issue)

    assert_equal(i_11.issue.issue_instances.all, [ i_11, i_12, i_13, i_21, i_22 ])
    assert_equal(i_21.issue.issue_instances.all, [])

  end

  test "get_close_issues" do

    # Kilka zgłoszeń w różnych kategoriach blisko siebie
    i_1 = Issue.add_issue(@cat_1.id, "0.0", "0.0",
                          "bla", "bla@bla.com", nil)
    i_2 = Issue.add_issue(@cat_2.id, "0.000001", "0.0",
                          "bla bla", "bla@bla.com", nil)
    i_3 = Issue.add_issue(@cat_3.id, "-0.000001", "0.0",
                          "bla bla", "bla@bla.com", nil)
    i_4 = Issue.add_issue(@cat_4.id, "0.000001", "0.000001",
                          "bla bla", "bla@bla.com", nil)

    assert_equal(i_1.issue.get_close_issues, [i_2.issue, i_3.issue, i_4.issue])

  end

  test "issue instance detach" do

    # Kilka zgłoszeń w różnych kategoriach blisko siebie
    i_1 = Issue.add_issue(@cat_1.id, "0.0", "0.0",
                           "bla", "bla@bla.com", nil)
    i_2 = Issue.add_issue(@cat_1.id, "0.000001", "0.0",
                           "bla bla", "bla@bla.com", nil)
    i_3 = Issue.add_issue(@cat_1.id, "-0.000001", "0.0",
                           "bla bla", "bla@bla.com", nil)

    i_3.detach

    # Dokładne sprawdzenie
    assert_equal(i_1.issue, i_2.issue)
    assert_not_equal(i_1.issue, i_3.issue)

    assert_equal(i_1.issue.issue_instances.all, [ i_1, i_2 ])
    assert_equal(i_3.issue.issue_instances.all, [ i_3 ])

  end

end
