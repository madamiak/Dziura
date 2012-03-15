class Point < ActiveRecord::Base
  belongs_to :polygon

  validates :number, :presence => true
  validates :latitude, :presence => true
  validates :longitude, :presence => true

  def ==(other)
    return latitude == other.latitude && longitude == other.longitude
  end

end
