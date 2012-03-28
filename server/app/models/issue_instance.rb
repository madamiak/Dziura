# -*- encoding : utf-8 -*-

# Klasa pojedynczego zgłoszenia
#
class IssueInstance < ActiveRecord::Base

  belongs_to :address
  belongs_to :issue
  has_many :photos

  validates :issue, :presence => true

  validates :notificar_email, :format => { :with =>
    /\A([^@\s]+)@((?:[-a-z0-9]+\.)+[a-z]{2,})\Z/i, 
    :message => "Błędny adres e-mail" }, :if => "!notificar_email.blank?"

end
