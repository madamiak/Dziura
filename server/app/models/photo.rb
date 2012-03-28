# -*- encoding : utf-8 -*-

# Klasa zdjęcia
#
# Ma pole photo, ktore jest stringiem.
# Zdjęcie zapisane jest w formacie base64
#
class Photo < ActiveRecord::Base

  belongs_to :issue_instance
  has_many :markers

  validates :photo, :presence => true

  # TODO validator photo czy nie za duzy rozmiar
  # i czy to jest zdjecie w ogole

end
