# -*- encoding : utf-8 -*-

# Klasa zdjęcia
#
# Ma pole photo, ktore jest stringiem.
# Zdjęcie zapisane jest w formacie base64
#
class Photo < ActiveRecord::Base

  belongs_to :issue_instance
  has_many :markers

  validates :photo, :presence => true,
                    :length => { :maximum => 1200*1024 } # 1200 kB Base64 ~ 400-600 kB max

  validates :mime_type, :presence => true
  validates :mime_type, :format => { :with => /image\/jpeg|image\/png/,
                                     :message => "Dozwolone formaty to tylko JPG i PNG" }

end
