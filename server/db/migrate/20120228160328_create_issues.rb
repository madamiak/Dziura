class CreateIssues < ActiveRecord::Migration
  def change
    create_table :issues do |t|
      t.float :longitude
      t.float :latitude
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
