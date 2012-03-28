# -*- encoding : utf-8 -*-

# Klasa zalogowanego zdarzenia
#
class Log < ActiveRecord::Base

  belongs_to :user
  belongs_to :loggable, :polymorphic => true

  def to_s
    "log." + id + ": " + message + " by " + user.login
  end

end
