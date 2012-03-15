# -*- encoding : utf-8 -*-
class Status < ActiveRecord::Base
  validates :name, :presence => true
  
  has_many :issues
  
  def get_log_message(old_status)
    I18n.t(:status_change_message, :old => old_status.name, :new => name )
  end
  
  def self.get_default_status
    Status.find_or_create_by_name("Nowy")
  end
end
