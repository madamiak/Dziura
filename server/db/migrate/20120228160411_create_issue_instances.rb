class CreateIssueInstances < ActiveRecord::Migration
  def change
    create_table :issue_instances do |t|
      t.text :desc
      t.decimal :longitude, { :precision => 15, :scale => 10 }
      t.decimal :latitude,  { :precision => 15, :scale => 10 }
      t.string :notificar_email   
      t.references :issue   

      t.timestamps
    end
  end
end
