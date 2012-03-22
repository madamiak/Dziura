# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended to check this file into your version control system.

ActiveRecord::Schema.define(:version => 20120322154532) do

  create_table "addresses", :force => true do |t|
    t.string   "city"
    t.string   "street"
    t.string   "home_number"
    t.string   "additional_info"
    t.string   "zip"
    t.datetime "created_at",      :null => false
    t.datetime "updated_at",      :null => false
  end

  create_table "categories", :force => true do |t|
    t.string   "name"
    t.string   "icon"
    t.datetime "created_at", :null => false
    t.datetime "updated_at", :null => false
  end

  create_table "delayed_jobs", :force => true do |t|
    t.integer  "priority",   :default => 0
    t.integer  "attempts",   :default => 0
    t.text     "handler"
    t.text     "last_error"
    t.datetime "run_at"
    t.datetime "locked_at"
    t.datetime "failed_at"
    t.string   "locked_by"
    t.string   "queue"
    t.datetime "created_at",                :null => false
    t.datetime "updated_at",                :null => false
  end

  add_index "delayed_jobs", ["priority", "run_at"], :name => "delayed_jobs_priority"

  create_table "issue_instances", :force => true do |t|
    t.text     "desc"
    t.decimal  "longitude",       :precision => 15, :scale => 10
    t.decimal  "latitude",        :precision => 15, :scale => 10
    t.string   "notificar_email"
    t.integer  "issue_id"
    t.datetime "created_at",                                      :null => false
    t.datetime "updated_at",                                      :null => false
  end

  create_table "issues", :force => true do |t|
    t.decimal  "longitude",   :precision => 15, :scale => 10
    t.decimal  "latitude",    :precision => 15, :scale => 10
    t.text     "desc"
    t.integer  "address_id"
    t.integer  "status_id"
    t.integer  "category_id"
    t.integer  "unit_id"
    t.datetime "created_at",                                  :null => false
    t.datetime "updated_at",                                  :null => false
  end

  add_index "issues", ["category_id"], :name => "index_issues_on_category_id"
  add_index "issues", ["status_id"], :name => "index_issues_on_status_id"
  add_index "issues", ["unit_id"], :name => "index_issues_on_unit_id"

  create_table "logs", :force => true do |t|
    t.string   "message"
    t.integer  "user_id"
    t.integer  "loggable_id"
    t.string   "loggable_type"
    t.datetime "created_at",    :null => false
    t.datetime "updated_at",    :null => false
  end

  add_index "logs", ["user_id"], :name => "index_logs_on_user_id"

  create_table "markers", :force => true do |t|
    t.integer  "x"
    t.integer  "y"
    t.integer  "photo_id"
    t.datetime "created_at", :null => false
    t.datetime "updated_at", :null => false
  end

  add_index "markers", ["photo_id"], :name => "index_markers_on_photo_id"

  create_table "photos", :force => true do |t|
    t.text     "photo"
    t.integer  "issue_instance_id"
    t.datetime "created_at",        :null => false
    t.datetime "updated_at",        :null => false
  end

  add_index "photos", ["issue_instance_id"], :name => "index_photos_on_issue_instance_id"

  create_table "points", :force => true do |t|
    t.integer  "number"
    t.decimal  "longitude",  :precision => 15, :scale => 10
    t.decimal  "latitude",   :precision => 15, :scale => 10
    t.integer  "polygon_id"
    t.datetime "created_at",                                 :null => false
    t.datetime "updated_at",                                 :null => false
  end

  add_index "points", ["polygon_id"], :name => "index_points_on_polygon_id"

  create_table "polygons", :force => true do |t|
    t.string   "name"
    t.integer  "unit_id"
    t.datetime "created_at", :null => false
    t.datetime "updated_at", :null => false
  end

  add_index "polygons", ["unit_id"], :name => "index_polygons_on_unit_id"

  create_table "statuses", :force => true do |t|
    t.string   "name"
    t.datetime "created_at", :null => false
    t.datetime "updated_at", :null => false
  end

  create_table "units", :force => true do |t|
    t.string   "name"
    t.integer  "address_id"
    t.datetime "created_at", :null => false
    t.datetime "updated_at", :null => false
  end

  create_table "users", :force => true do |t|
    t.string   "login"
    t.string   "password_hash"
    t.string   "role"
    t.integer  "unit_id"
    t.datetime "created_at",    :null => false
    t.datetime "updated_at",    :null => false
  end

  add_index "users", ["unit_id"], :name => "index_users_on_unit_id"

end
