class Issue < ActiveRecord::Base
  belongs_to :status
  belongs_to :category
  belongs_to :unit

  belongs_to :status
  has_many :logs, :as => :loggable
  has_many :issue_instances

  validates :status, :presence => true
  validates :unit, :presence => true
  validates :category, :presence => true

  # Nalezy wykonać to przy edycji z kontrolera, gdyz tutaj nie ma
  # dostepu do sesji.
  def log_status_change(user, old_status)
    l = logs.new :user => user, :message => status.get_log_message(old_status)
    l.save
  end
  
  # Dodaje zgloszenie do systemu
  # photo - string base64 format
  # category - obiekt category (nie samo id)
  # Rzuca wyjatek gdy nie odnajdzie pasujacej jednostki do punktu
  # TODO inne wyjatki
  def self.add_issue(desc, notificar_email, category, longitude, latitude, photo, marker_x, marker_y)
    Issue.transaction do
      unit = Unit.find_unit_by_point(longitude, latitude)
      
      raise Exception::NonUnitForPoint.new if unit.nil?
      
      precision_latitude = 0.0001
      precision_longitude = 0.0001

      i = Issue.where(:category_id => category.id, 
        :latitude => (latitude-precision_latitude)..(latitude+precision_latitude), 
        :longitude => (longitude-precision_longitude)..(longitude+precision_longitude)).first

      if i.nil?
        i = Issue.new :latitude => latitude, :longitude => longitude, 
          :category => category, :unit => unit, :status => Status.get_default_status
          
        # TODO pobranie adresu na podstawie latitude i logitude z googla i zapisanie adresu
        # do issue       
          
        i.save
      end

      issue_instance = i.issue_instances.build :latitude => latitude, 
        :longitude => longitude, :desc => desc, :notificar_email => notificar_email
      
      if !photo.nil?  
        photo = issue_instance.photos.build :photo => photo
        
        if !marker_x.nil? and !marker_y.nil?
          # TODO and sa w granicach obrazka (od 0 do width/height)
          
          photo.markers.build :x => marker_x, :y => marker_y
        end
      end
        
      issue_instance.save
      
      return issue_instance
    end
  end
end
