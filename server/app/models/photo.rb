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
                    :length => { :maximum => 640*1024*1024 } # 400 kB max

  validates :mime_type, :presence => true
  validates :mime_type, :format => { :with => /image\/jpeg|image\/png/,
                                     :message => "Dozwolone formaty to tylko JPG i PNG" }

end
