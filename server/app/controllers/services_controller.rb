class ServicesController < ApplicationController
  skip_before_filter :require_login
  
  #Tutaj umieszczamy wszystkie REST servisy dla clienta
  #Inne kontrolery to typowy CRUD, do ktorych dostep jest chroniony
  
  def issue
    #Issue.add_issue(params[:photo]...)
  end
  
  def categories
    @categories = Category.all
    render :json => @categories.to_json( :only => [:name, :icon] )
  end
    
end
