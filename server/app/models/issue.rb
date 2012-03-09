class Issue < ActiveRecord::Base
  belongs_to :status
  belongs_to :category
  belongs_to :unit
  
  belongs_to :status  
  has_many :logs, :as => :loggable
  
  validates :status, :presence => true
  validates :unit, :presence => true
  validates :category, :presence => true
  
  # Nalezy wykonać to przy edycji z kontrolera, gdyz tutaj nie ma 
  # dostepu do sesji.
  def log_status_change(user, old_status)
    l = logs.new :user => user, :message => status.get_log_message(old_status)
    l.save
  end
end
