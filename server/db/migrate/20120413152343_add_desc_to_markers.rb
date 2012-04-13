class AddDescToMarkers < ActiveRecord::Migration
  def change
    add_column :markers, :desc, :string

  end
end
