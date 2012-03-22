class MailIssueAdded < Struct.new(:issue_id)
  def perform
    NotificationMailer.issue_status_changed(issue_id).deliver
  end
end
