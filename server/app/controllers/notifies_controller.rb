# -*- encoding : utf-8 -*-

# Kontroler wyświetlający stronę do zgłaszania uszkodzeń
# i sprawdzania statusu zgłoszenia
#
class NotifiesController < ApplicationController
  skip_before_filter :require_login

  # GET /
  def index
  end

  # GET /check_status
  def check_status
    @id = nil
    if params.has_key?(:id)
      @id = params[:id]
    end
  end

end
