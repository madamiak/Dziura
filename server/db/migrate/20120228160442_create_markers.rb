class CreateMarkers < ActiveRecord::Migration
  def change
    create_table :markers do |t|
      t.integer :x
      t.integer :y
      t.references :photo

      t.timestamps
    end
    add_index :markers, :photo_id
  end
end
