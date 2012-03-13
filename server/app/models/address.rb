class Address < ActiveRecord::Base
  validates :city, :street, :home_number, :presence => true
end
