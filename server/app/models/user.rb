# -*- encoding : utf-8 -*-

require "digest/sha1"

# Klasa użytkownika
#
# Zawiera dodatkowe funkcje do hashowania hasła (SHA1) i uwierzytelniania użytkowników
#
class User < ActiveRecord::Base

  belongs_to :unit

  before_save :hash_password
  attr_accessor :password

  validates :login, :presence => true, :uniqueness => true
  validates :password, :presence => true, :if => :perform_password_validation?

  # Sprawdza, czy podano poprawne dane logowania
  def self.authenticate(login, password)
    u = find(:first, :conditions => ["login = ?", login])

    return u if !u.nil? and u.valid_password?(password)
  end

  # Sprawdza, czy hasło zgadza się z podanym poprzez porównanie hashów
  def valid_password?(password)
    self.password_hash == self.class.hash_password(password)
  end

  # Zwraca hash podanego hasła
  def self.hash_password(password, salt = "gasdweaaa*&^#kgdwq3g")
    Digest::SHA1.hexdigest("#{salt}:#{password}")
  end

  # Hashuje hasło
  def hash_password
    self.password_hash = self.class.hash_password(self.password) unless self.password.blank?
  end

  def perform_password_validation?
    self.new_record? ? true : !self.password.blank?
  end

end
