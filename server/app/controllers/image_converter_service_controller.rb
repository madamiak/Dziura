class ImageConverterServiceController < ApplicationController
  
  def upload
    if !params[:picture].nil?
      # TODO zmniejszanie obrazka     
    
      file = params[:picture]
      @picture = Base64.encode64(file.read)
    end
  end

end
