# -*- encoding : utf-8 -*-

# Konfiguracja ścieżek dostępu do zasobów
#
Dziura::Application.routes.draw do

  ### Interfejs zgłaszającego

  root :to => 'notifies#index'

  match 'check_status' => 'notifies#check_status'
  match 'check_status/:id' => 'notifies#check_status'


  ### Panel admina

  match 'admin' => 'issues#index'
  match 'login' => 'main#login'
  match 'logout' => 'main#logout'

  match 'issues/by_rect' => 'issues#get_by_rect'
  match 'issues/by_pages' => 'issues#get_by_pages'
  match 'issues/map' => 'issues#show_map'
  match 'issues/print' => 'issues#print'

  resources :statuses
  resources :categories
  resources :users
  resources :units

  resources :issues do
    member do
      get 'detach'
      get 'join'
    end
  end


  ### Interfejs do wysyłania zgłoszeń + wspólne zasoby np. zdjęcia

  match 'res/issue' => 'services#issue', :as => :issue_add

  match 'res/issue_instances/:id' => 'issue_instances#get_by_id'

  match 'res/categories' => 'services#categories'
  match 'res/categories/:id' => 'services#category'
  match 'res/category_icon/:id' => 'services#category_icon'

  match 'res/upload' => 'image_converter_service#upload'

  match 'res/photos/:id' => 'photos#show'

end
