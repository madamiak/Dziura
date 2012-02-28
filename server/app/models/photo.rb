class Photo < ActiveRecord::Base
  belongs_to :issue_instance
end
