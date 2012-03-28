# -*- encoding : utf-8 -*-

require "digest/sha1"

# Klasa użytkownika
#
class User < ActiveRecord::Base

  belongs_to :unit

  before_save :hash_password
  attr_accessor :password

  validates :login, :presence => true, :uniqueness => true
  validates :password, :presence => true, :if => :perform_password_validation?

  def self.authenticate(login, password)
    u = find(:first, :conditions => ["login = ?", login])

    return u if !u.nil? and u.valid_password?(password)
  end

  def valid_password?(password)
    self.password_hash == self.class.hash_password(password)
  end

  def self.hash_password(password, salt = "gasdweaaa*&^#kgdwq3g")
    Digest::SHA1.hexdigest("#{salt}:#{password}")
  end

  def hash_password
    self.password_hash = self.class.hash_password(self.password) unless self.password.blank?
  end

  def perform_password_validation?
    self.new_record? ? true : !self.password.blank?
  end

end
