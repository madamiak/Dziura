# -*- encoding : utf-8 -*-
class CreateAddresses < ActiveRecord::Migration
  def change
    create_table :addresses do |t|
      t.string :city
      t.string :street
      t.string :home_number
      t.string :additional_info
      t.string :zip

      t.timestamps
    end
  end
end
