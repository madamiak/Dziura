# -*- encoding : utf-8 -*-

# Kontroler wystawiający usługi dla klienta mobilnego
# Inne kontrolery to typowy CRUD od strony administracyjnej, do których dostęp jest chroniony
class ServicesController < ApplicationController
  skip_before_filter :require_login

  layout false

  # POST /res/issue
  def issue
    if request.post?
      begin
        issue = Issue.add_issue(params[:desc], params[:notificar_email], 
          params[:category_id], params[:longitude], params[:latitude], 
          params[:photo], params[:marker_x], params[:marker_y])

        if !issue.nil?
          render :json => 'Zgłoszenie zostało przyjęte'
        else
          render :json => 'Error'
        end
      rescue Exceptions::NoUnitForPoint
        render :json => 'Nie ma jednostki dla takiego punktu', :status => 500
      rescue Exception => e
        render :json => 'Error: ' + e.message, :status => 500
      end
    end
  end

  # GET /res/categories
  def categories
    @categories = Category.all
    render :json => @categories.to_json( :only => [:name, :icon] )
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
