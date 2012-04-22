# -*- encoding : utf-8 -*-

require 'carrierwave/processing/rmagick'

# Klasa zdjęcia
#
# === Pola
# [photo] zdjęcie w formacie JPEG lub PNG, zakodowane w Base64, wymagane,
#         maks. rozmiar to 1200 kB (zakodowane) = ok. 400-600 kB zdekodowane
# [mime_type] typ MIME zdjęcia (image/jpeg albo image/png), ustalane automatycznie
# [markers] znaczniki na zdjęciu
#
class Photo < ActiveRecord::Base

  belongs_to :issue_instance
  has_many :markers

  validates :photo, :presence => true,
                    :length => { :maximum => 1200*1024, :message => 'Przekroczony max. rozmiar zdjęcia' }

  validates_associated :markers

  validate :image_and_markers_must_be_valid

  before_save :set_mime_type

  # Walidator
  #
  # Sprawdza, czy zdjęcie jest faktycznie w podanym formacie i czy znaczniki
  # są w obszarze obrazka
  def image_and_markers_must_be_valid

    begin
      img = Magick::Image::read_inline(photo).first

      if img.format != "PNG" && img.format != "JPEG"
        return errors.add(:photo, "Nieobsługiwany format zdjęcia!")
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

  # Ustawia automatycznie typu MIME przed zapisaniem obrazka
  def set_mime_type
    img = Magick::Image::read_inline(photo).first
    if img.format == "PNG"
      self.mime_type = "image/png"
    elsif img.format == "JPEG"
      self.mime_type = "image/jpeg"
    end
  end

end
