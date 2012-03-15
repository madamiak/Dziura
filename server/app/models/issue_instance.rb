class IssueInstance < ActiveRecord::Base
  belongs_to :address
  belongs_to :issue
  has_many :photos
  
  validates :notificar_email, :format => { :with =>
    /\A([^@\s]+)@((?:[-a-z0-9]+\.)+[a-z]{2,})\Z/i, 
    :message => "Uncorrect email" }, :if => "!notificar_email.blank?"
end
