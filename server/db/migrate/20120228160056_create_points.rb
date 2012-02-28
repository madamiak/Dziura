class CreatePoints < ActiveRecord::Migration
  def change
    create_table :points do |t|
      t.float :longtitude
      t.float :latitude
      t.references :polygon

      t.timestamps
    end
    add_index :points, :polygon_id
  end
end
