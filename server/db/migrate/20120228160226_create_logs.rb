class CreateLogs < ActiveRecord::Migration
  def change
    create_table :logs do |t|
      t.string :message
      t.references :user
      t.string :model_name
      t.integer :model_id

      t.timestamps
    end
    add_index :logs, :user_id
  end
end
