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

#  def add_issue (photo, desc, notificar_email, category_id, longitude, latitude)
#    i = issue.where(:category_id => category_id, :latitude => latitude, :longitude => longitude)
#
#    if !i.exist()
#      issue_new = issue.new :latitude => latitude, :longitude => longitude, :desc => desc, :category_id => category_id
#      issue_new.save
#    end
#
#    issue_instance_new = issue_instance.new :latitude => latitude, :longitude => longitude, :desc => desc, :notificar_email => notificar_email
#    issue_instance_new.save
#  end
end
