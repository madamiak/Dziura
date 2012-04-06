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
  
  # Jezeli istnieje taka sytuacja, gdy Issue zawiera kilka IssueInstances, ale
  # jedno z nich nie pasuje (a system uznal, ze to jest to samo zgloszenie) mozna
  # je odlaczyc
  #
  # Ta funkcja utworzy nowe Issue oraz przepnie do niego to IssueInstance. 
  # To Issue bedzie lekko przesuniete, aby markery sie nie nalozyly na siebie
  def detach  
    IssueInstance.transaction do
      new_issue = Issue.new :longitude => self.longitude + 0.00001, :latitude => self.latitude + 0.00001,
        :status => self.issue.status, :category => self.issue.category,
        :unit => self.issue.unit
        
      new_issue.address = Address.create_by_position(self.latitude, self.longitude)
      
      new_issue.save!
      
      self.issue = new_issue
      self.save
    end
  end
  
end
