# -*- encoding : utf-8 -*-

# Główny kontroler aplikacji
#
# Nie zawiera żadnych zasobów, ale zawiera funkcje pomocnicze.
#
class ApplicationController < ActionController::Base
  #protect_from_forgery

  before_filter :require_login

  helper_method :current_user, :logged_in?, :is_admin?

  # Zwraca obecnie zalogowanego użytkownika
  def current_user #:doc:
    @_current_user ||= session[:current_user_id] &&
      User.find_by_id(session[:current_user_id])
  end

  private

  # Czy zalogowano?
  def logged_in? #:doc:
    !!current_user
  end

  # Czy użytkownik jest adminem?
  def is_admin? #:doc:
    current_user.role == "admin" if logged_in?
  end

  # Powoduje, że potrzebne jest zalogowanie przy dostępie do danej strony
  def require_login #:doc:
    unless logged_in?
      flash[:error] = t(:login_required)
      redirect_to :login
    end
  end

  # Powoduje, że potrzebne są prawa admina przy dostępie do danej strony
  def require_admin #:doc:
    unless is_admin?
      respond_to do |format|
        format.html { render :text => t(:forbidden), :status => :forbidden }
      end
    end
  end 
end
