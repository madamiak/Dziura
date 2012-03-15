class Issue < ActiveRecord::Base
  belongs_to :status
  belongs_to :category
  belongs_to :unit

  belongs_to :status
  has_many :logs, :as => :loggable
  has_many :issue_instances, :validate => true

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
  #
  # photo - string, zdjecie w base64
  # category - id category
  # longitude, latitude - string
  #
  # Rzuca NilArguments gdy lgn, lat lub category jest nil
  # Rzuca NoUnitForPoint gdy nie odnajdzie pasujacej jednostki do punktu
  # Rzuca ActiveRecord::RecordNotFound gdy nie odnajdzie category 
  # Rzuca ActiveRecord::RecordInvalid gdy cos nie przejdzie walidacji
  # (aktualnie jedynie notificar_email)
  def self.add_issue(desc, notificar_email, category, longitude, latitude,
    photo, marker_x, marker_y)
  
    raise Exceptions::NilArguments if category.nil? or longitude.nil? or
      latitude.nil? 
    
    Issue.transaction do
      longitude = BigDecimal.new(longitude)
      latitude = BigDecimal.new(latitude)
      
      category = Category.find(category)
      
      unit = Unit.find_unit_by_point(longitude, latitude)
      
      raise Exceptions::NoUnitForPoint.new if unit.nil?
      
      p_lat = 0.0001
      p_lng = 0.0001

      i = Issue.where(:category_id => category.id, 
        :latitude => (latitude - p_lat)..(latitude + p_lat), 
        :longitude => (longitude - p_lng)..(longitude + p_lng)).first

      if i.nil?
        i = Issue.new :latitude => latitude, :longitude => longitude, 
          :category => category, :unit => unit, 
          :status => Status.get_default_status
          
        # TODO pobranie adresu na podstawie latitude i logitude z googla 
        # i zapisanie adresu do issue       
          
        i.save!
      end

      issue_instance = i.issue_instances.build :latitude => latitude, 
        :longitude => longitude, :desc => desc, 
        :notificar_email => notificar_email
      
      if !photo.nil?  
        photo = issue_instance.photos.build :photo => photo
        
        if !marker_x.nil? and !marker_y.nil?        
          photo.markers.build :x => marker_x, :y => marker_y
        end
      end
        
      i.save!
      
      return issue_instance
    end
  end
end
