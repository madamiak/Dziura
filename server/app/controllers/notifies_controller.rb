# -*- encoding : utf-8 -*-

# Kontroler wyświetlający stronę do zgłaszania uszkodzeń
class NotifiesController < ApplicationController
  skip_before_filter :require_login

  # GET /
  def index
  end

end
