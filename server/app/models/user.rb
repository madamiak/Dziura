# -*- encoding : utf-8 -*-

require "digest/sha1"

# Klasa użytkownika
#
# Zawiera dodatkowe funkcje do hashowania hasła (SHA1) i uwierzytelniania użytkowników
#
# === Pola
# [login] login użytkownika, +string+, wymagane, musi być unikalny
# [password_hash] hash SHA1 hasła
# [role] rola użytkownika
# [unit] jednostka, z którą jest związany użytkownik
#
class User < ActiveRecord::Base

  belongs_to :unit

  before_save :hash_password

  # Pole na hasło
  #
  # Nie jest zapisywane w bazie, tylko używane do ustawiania hashu hasła
  # podczas tworzenia/aktualizacji
  attr_accessor :password

  validates_presence_of :login, :message => 'Login - pole obowiązkowe'
  validates_uniqueness_of :login, :message => 'Loginy użytkowników nie mogą się powtarzać'

  validates_presence_of :password, :message => 'Hasło - pole obowiązkowe', :if => :perform_password_validation?

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

  # Zwraca true, jeżeli konieczna jest walidacja hasła
  def perform_password_validation?
    self.new_record? ? true : !self.password.blank?
  end

end
