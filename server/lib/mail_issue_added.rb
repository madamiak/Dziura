class MailIssueAdded < Struct.new(:issue_instance_id)
  def perform
    NotificationMailer.issue_added(issue_instance_id)
  end
end
