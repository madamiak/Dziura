class ServicesController < ApplicationController
  skip_before_filter :require_login
  
  layout false
  
  #Tutaj umieszczamy wszystkie REST servisy dla clienta
  #Inne kontrolery to typowy CRUD, do ktorych dostep jest chroniony
  
  def issue
    if request.post?
      begin
        issue = Issue.add_issue(params[:desc], params[:notificar_email], 
          params[:issue][:category_id], params[:longitude], params[:latitude], 
          params[:photo], params[:marker_x], params[:marker_y])
        
        if !issue.nil?
          render :json => 'Zgłoszenie zostało przyjęte'
        else
          render :json => 'Error'
        end
      rescue Exceptions::NoUnitForPoint
        render :json => 'Nie ma jednostki dla takiego punktu', :status => 500
      rescue
        render :json => 'Jakis blad', :status => 500
      end
    end
  end
  
  def categories
    @categories = Category.all
    render :json => @categories.to_json( :only => [:name, :icon] )
  end
    
end
