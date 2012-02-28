class CreatePolygons < ActiveRecord::Migration
  def change
    create_table :polygons do |t|
      t.string :name
      t.references :unit

      t.timestamps
    end
    add_index :polygons, :unit_id
  end
end
