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
    assert_equal l.message, I18n.t(:status_change_message, 
      :old => "old", :new => "new" )
  end


  # Sprawdzanie wyjątku NilArguments
  test "add_issue nil arguments" do

    assert_raise Exceptions::NilArguments do
      Issue.add_issue("", "", nil, "", "", nil, nil, nil)
    end

    assert_raise Exceptions::NilArguments do
      Issue.add_issue("", "", 1, nil, "", nil, nil, nil)
    end

    assert_raise Exceptions::NilArguments do
      Issue.add_issue("", "", 1, "", nil, nil, nil, nil)
    end

  end

  # Zgłoszenie poza obszarem jednostki
  test "add_issue outside unit area" do

    assert_raise Exceptions::NoUnitForPoint do
      Issue.add_issue("bla", "bla@bla.com", 1, "10", "10", nil, nil, nil)
    end

  end

  # Nieistniejąca kategoria
  test "add_issue invalid category" do

    assert_raise Exceptions::UnknownCategory do
      Issue.add_issue("bla", "bla@bla.com", -1, "10", "10", nil, nil, nil)
    end

  end

  # Nieprawidłowy e-mail
  test "add_issue invalid e-mail" do

    assert_raise Exceptions::IncorrectNotificarEmail do
      Issue.add_issue("bla", "bla", 1, "0", "0", nil, nil, nil)
    end

  end

  # Łączenie zgłoszeń
  test "add_issue joining issues" do

    # Wszystko OK
    i_1 = Issue.add_issue("bla", "bla@bla.com",
                          1, "0.0", "0.0",
                          nil, nil, nil)

    assert_not_nil(i_1)

    # Drugie zgłoszenie w pobliżu
    i_2 = Issue.add_issue("bla bla", "bla@bla.com",
                          1, "0.000001", "0.0",
                          nil, nil, nil)

    assert_not_nil(i_2)

    # Zgłoszenia powinny być złączone
    assert_equal(i_1.issue, i_2.issue)

    # Zgłoszenie poza obszarem złączania ma osobne issue
    i_3 = Issue.add_issue("bla", "bla@bla.com",
                          1, "0.99", "-0.99",
                          nil, nil, nil)

    assert_not_equal(i_1.issue, i_3.issue)

    # Zgłoszenie w pobliżu, ale w innej kategorii też ma mieć osobne issue
    i_4 = Issue.add_issue("bla", "bla@bla.com",
                          2, "0.000001", "0.0",
                          nil, nil, nil)
    assert_not_equal(i_1.issue, i_4.issue)

  end

end
