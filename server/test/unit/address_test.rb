# -*- encoding : utf-8 -*-

require 'test_helper'

class AddressTest < ActiveSupport::TestCase

  test "geolocation" do

    latitude = BigDecimal.new("51.121480")
    longitude = BigDecimal.new("17.059611")

    addr = Address.create_by_position(latitude, longitude)

    assert_not_nil(addr)

    assert(addr.city = "Wrocław")
    assert(addr.street == "Bolesława Prusa")

  end

end
