# -*- encoding : utf-8 -*-

# Kontroler do kodowania wysyłanych zdjęć do formatu Base64
# Wykorzystywany w formularzu zgłoszenia szkody
class ImageConverterServiceController < ApplicationController
  skip_before_filter :require_login

  def upload
    if !params[:picture].nil?
      # TODO zmniejszanie obrazka     

      file = params[:picture]
      @picture = Base64.encode64(file.read)
    end
  end

end
