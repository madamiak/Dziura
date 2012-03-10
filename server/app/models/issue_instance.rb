class IssueInstance < ActiveRecord::Base
  belongs_to :address

  mount_uploader :photo, PhotoUploader

end
