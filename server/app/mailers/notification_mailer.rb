# -*- encoding : utf-8 -*-

class NotificationMailer < ActionMailer::Base

  default :from => Rails.application.config.notification_email

  def issue_added(issue_instance_id)
    @issue_instance = IssueInstance.find(issue_instance_id)
    if (@issue_instance.notificar_email != nil)
      begin
        mail(:to => @issue_instance.notificar_email, :subject => "Zgłoszenie nowej szkody w systemie Dziura").deliver!
      rescue Exception => e
        Rails.logger.error e.to_s
      end
    end
  end

  def issue_status_changed(issue_id, old_status)
    @old_status = old_status
    issue = Issue.find(issue_id)
    issue.issue_instances.each do |ii|
      @issue_instance = ii
      if (@issue_instance.notificar_email != nil)
        begin
          mail(:to => @issue_instance.notificar_email, :subject => "Zmiana statusu zgłoszenia \##{issue_instance.id} w systemie Dziura").deliver!
        rescue Exception => e
          Rails.logger.error e.to_s
        end
      end
    end
  end

end
