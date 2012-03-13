class IssueInstance < ActiveRecord::Base
  belongs_to :address
  belongs_to :issue
  has_many :photos
  # TODO walidacja emaila
end
