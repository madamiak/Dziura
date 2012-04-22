# -*- encoding : utf-8 -*-

# Klasa zadania wysłania e-maila po zmianie statusu
#
# Określa zadanie wykonywane przez plugin +delayed_job+
#
class MailIssueStatusChanged < Struct.new(:issue_id, :old_status, :new_status)

  # Wywołuje metodę NotificationMailer
  def perform
    NotificationMailer.issue_status_changed(issue_id, old_status, new_status)
  end

end
