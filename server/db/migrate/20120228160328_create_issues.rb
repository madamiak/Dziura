# -*- encoding : utf-8 -*-
class CreateIssues < ActiveRecord::Migration
  def change
    create_table :issues do |t|
      t.decimal :longitude, { :precision => 15, :scale => 10 }
      t.decimal :latitude,  { :precision => 15, :scale => 10 }
      t.text :desc
      t.references :address
      t.references :status
      t.references :category
      t.references :unit

      t.timestamps
    end
    add_index :issues, :status_id
    add_index :issues, :category_id
    add_index :issues, :unit_id
  end
end
