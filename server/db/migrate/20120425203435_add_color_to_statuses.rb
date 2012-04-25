class AddColorToStatuses < ActiveRecord::Migration
  def change
    add_column :statuses, :color, :string

  end
end
