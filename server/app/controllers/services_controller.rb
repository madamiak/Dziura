class ServicesController < ApplicationController
  skip_before_filter :require_login
  
  layout false
  
  #Tutaj umieszczamy wszystkie REST servisy dla clienta
  #Inne kontrolery to typowy CRUD, do ktorych dostep jest chroniony
  
  def issue
    if request.post?
      issue = Issue.add_issue(params[:desc], params[:nitificar_email], 
        params[:issue][:category_id], params[:longitude], params[:latitude], 
        params[:photo], params[:marker_x], params[:marker_y])
      
      if !issue.nil?
        redirect_to root_url, :notice => 'Issue has been added.'
      else
        redirect_to root_url, :notice => 'Error.'
      end
    end
  end
  
  def categories
    @categories = Category.all
    render :json => @categories.to_json( :only => [:name, :icon] )
  end
    
end
