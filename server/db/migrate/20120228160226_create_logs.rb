# -*- encoding : utf-8 -*-
class CreateLogs < ActiveRecord::Migration
  def change
    create_table :logs do |t|
      t.string :message
      t.references :user
      t.references :loggable, :polymorphic => true

      t.timestamps
    end
    add_index :logs, :user_id
  end
end
