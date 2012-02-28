class CreateUsers < ActiveRecord::Migration
  def change
    create_table :users do |t|
      t.string :login
      t.string :password
      t.string :role
      t.references :unit

      t.timestamps
    end
    add_index :users, :unit_id
  end
end
