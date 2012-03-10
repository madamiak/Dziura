class CreateIssueInstances < ActiveRecord::Migration
  def change
    create_table :issue_instances do |t|
      t.text :desc
      t.float :longitude
      t.float :latitude
      t.string :notificar_email
      t.references :address
      t.string :photo

      t.timestamps
    end
    add_index :issue_instances, :address_id
  end
end
