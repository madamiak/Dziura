# -*- encoding : utf-8 -*-

# Kontroler wystawiający usługi dla klienta mobilnego
#
# Umożliwia zgłoszenie uszkodzenia i pobieranie aktualnych danych kategorii.
#
class ServicesController < ApplicationController
  skip_before_filter :require_login

  layout false

  # POST /res/issue
  def issue
    if request.post?
      begin
        instance = Issue.add_issue(params[:category_id], params[:longitude], params[:latitude],
                                params[:desc], params[:notificar_email], params[:photos])

        if !instance.nil?
          render :json => { :message => 'Zgłoszenie zostało przyjęte', :id => instance.id }
        else
          render :json => { :message => 'Błąd', :id => nil }, :status => 500
        end
      rescue ActiveRecord::RecordInvalid => e
        render :json => { :message => 'Błąd: ' + e.errors, :id => nil }, :status => 500
      rescue Exception => e
        render :json => { :message => 'Błąd: ' + e.message, :id => nil }, :status => 500
      end
    end
  end

  # GET /res/categories
  def categories
    @categories = Category.all
    render :json => @categories.to_json( :only => [:id, :name] )
  end

  # GET /res/category_icon/1
  def category_icon
    category = Category.find(params[:id])
    if !category.nil? && !category.icon.nil?
      # typ ikony to na sztywno image/jpeg
      send_data Base64.decode64(category.icon), :type => 'image/jpeg', :disposition => 'inline'
    end
  end

end
