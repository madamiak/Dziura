class CreatePoints < ActiveRecord::Migration
  def change
    create_table :points do |t|
      t.integer :number
      t.decimal :longitude, { :precision => 15, :scale => 10 }
      t.decimal :latitude,  { :precision => 15, :scale => 10 }
      t.references :polygon

      t.timestamps
    end
    add_index :points, :polygon_id
  end
end
