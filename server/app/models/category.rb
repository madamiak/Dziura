# -*- encoding : utf-8 -*-
class Category < ActiveRecord::Base
  validates :name, :presence => true
  
  has_many :issues
end
