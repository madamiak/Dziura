# -*- encoding : utf-8 -*-

# Klasa zadania wysłania e-maila po dodaniu nowego zgłoszenia
#
# Określa zadanie wykonywane przez plugin +delayed_job+
#
class MailIssueAdded < Struct.new(:issue_instance_id)

  # Wywołuje metodę NotificationMailer
  def perform
    NotificationMailer.issue_added(issue_instance_id)
  end

end
