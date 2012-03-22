# -*- encoding : utf-8 -*-

class NotificationMailer < ActionMailer::Base

  default :from => "powiadomienia@dziura.com"

  def issue_added(issue_instance_id)
    @issue_instance = IssueInstance.find(issue_instance_id)
    mail(:to => @issue_instance.notificar_email, :subject => "Zgłoszenie nowej szkody w systemie Dziura")
  end

  def issue_status_changed(issue_id)
    #@issue_instance = IssueInstance.find(issue_instance_id)
    #mail(:to => issue_instance.notificar_email, :subject => "Zmiana statusu zgłoszenia \##{issue_instance.id} w systemie Dziura")
  end

end
