class Photo < ActiveRecord::Base
  belongs_to :issue_instance
  has_many :markers
  mount_uploader :photo, PhotoUploader
end
