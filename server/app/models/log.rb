# -*- encoding : utf-8 -*-

# Klasa zalogowanego zdarzenia
#
# Obecnie logowane są zmiany statusów zgłoszeń, ale nie ma
# interfejsu do podglądu logów. W przyszłości można będzie
# rozwinąć dalej taką funkcjonalność.
#
class Log < ActiveRecord::Base

  belongs_to :user
  belongs_to :loggable, :polymorphic => true

  # Zwraca komunikat logu
  def to_s
    "log." + id + ": " + message + " by " + user.login
  end

end
