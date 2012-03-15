require 'test_helper'

class AddressTest < ActiveSupport::TestCase

  test "geolocation" do

    latitude = BigDecimal.new("51.121480")
    longitude = BigDecimal.new("17.059611")

    puts "Resolving #{latitude.to_s}, #{longitude.to_s}"

    addr = Address.create_by_position(latitude, longitude)

    assert_not_nil(addr)

    puts "Address:"
    addr.print()

    assert(addr.city = "Wrocław")
    assert(addr.street == "Bolesława Prusa")

  end

end
