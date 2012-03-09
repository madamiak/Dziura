class Status < ActiveRecord::Base
  def get_log_message(old_status)
    I18n.t(:status_change_message, :old => old_status.name, :new => name )
  end
end
