# -*- encoding : utf-8 -*-
class NotifiesController < ApplicationController
  skip_before_filter :require_login
  
  def index
  end

end
