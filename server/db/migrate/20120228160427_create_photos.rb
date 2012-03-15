# -*- encoding : utf-8 -*-
class CreatePhotos < ActiveRecord::Migration
  def change
    create_table :photos do |t|
      t.text :photo
      t.references :issue_instance

      t.timestamps
    end
    add_index :photos, :issue_instance_id
  end
end
