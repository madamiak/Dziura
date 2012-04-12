class AddMimeTypeToPhotos < ActiveRecord::Migration
  def change
    add_column :photos, :mime_type, :string

  end
end
