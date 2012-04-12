# -*- encoding : utf-8 -*-

# Kontroler zdjęć
# Umożliwia pobieranie zdjęć zdekodowanych z formatu Base64
class PhotosController < ApplicationController
  skip_before_filter :require_login

  # GET /photos/1
  def show
    photo = Photo.find(params[:id])
    if !photo.nil? && !photo.photo.nil?
      send_data Base64.decode64(photo.photo), :type => photo.type, :disposition => 'inline'
    end
  end

end
