# -*- encoding : utf-8 -*-

# Klasa zdjęcia
#
# Ma pole photo, ktore jest stringiem z danymi obrazka zakodowanymi
# w formacie Base64
#
class Photo < ActiveRecord::Base

  belongs_to :issue_instance
  has_many :markers

  # Maksymalny rozmiar 1200 kB Base64 = ok. 400-600 kB zdekodowane
  validates :photo, :presence => true,
                    :length => { :maximum => 1200*1024 }

  validates :mime_type, :presence => true
  validates :mime_type, :format => { :with => /image\/jpeg|image\/png/,
                                     :message => "Dozwolone formaty to tylko JPG i PNG" }

  validates_associated :markers

  validate :image_and_markers_must_be_valid

  # Walidator
  # Sprawdza, czy zdjęcie jest faktycznie w podanym formacie i czy znaczniki
  # są w obszarze obrazka
  def image_and_markers_must_be_valid

    begin
      img = Magick::Image::read_inline(photo).first

      if mime_type == "image/png" && img.format != "PNG"
        return errors.add(:photo, "Niezgodny typ zdjęcia")
      elsif mime_type == "image/jpeg" && img.format != "JPEG"
        return errors.add(:photo, "Niezgodny typ zdjęcia")
      end

      markers.each do |m|
        if !m.x.between?(1, img.columns) || !m.y.between?(1, img.rows)
          return errors.add(:markers, "Znacznik poza obszarem zdjęcia")
        end
      end

    rescue
      errors.add(:photo, "Niepoprawny format zdjęcia!")
    end
  end

end
