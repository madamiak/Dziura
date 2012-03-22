class MailIssueStatusChanged < Struct.new(:issue_id, :old_status)
  def perform
    NotificationMailer.issue_status_changed(issue_id, old_status)
  end
end
