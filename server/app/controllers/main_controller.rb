# -*- encoding : utf-8 -*-

# Kontroler logowania/wylogowywania panelu zarządzania
#
class MainController < ApplicationController
  skip_before_filter :require_login, :only => [:login]

  layout "admin"

  # Logowanie
  def login
    if request.post?
      user = User.authenticate(params[:login], params[:password])

      if user != nil
        session[:current_user_id] = user.id
        flash[:notice] = t(:logged_in)
        redirect_to admin_url
      else
        flash[:error] = t(:incorrect_login_or_password)
      end
    end
  end

  # Po wylogowaniu
  def logout
    @_current_user = session[:current_user_id] = nil
    flash[:notice] = t(:logged_out)
    redirect_to root_url
  end
end
