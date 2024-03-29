# -*- encoding : utf-8 -*-
class CreateUsers < ActiveRecord::Migration
  def change
    create_table :users do |t|
      t.string :login
      t.string :password_hash
      t.string :role
      t.references :unit

      t.timestamps
    end
    add_index :users, :unit_id
  end
end
