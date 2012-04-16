# -*- encoding : utf-8 -*-

# Klasa zgłoszenia (zbiorczego)
#
# Oprócz pól modelu RoR, zawiera najważniejszą metodę w systemie:
# self.add_issue dodającą nowe zgłoszenie do systemu
#
class Issue < ActiveRecord::Base

  belongs_to :status
  belongs_to :category
  belongs_to :unit

  belongs_to :status

  belongs_to :address
  has_many :logs, :as => :loggable
  has_many :issue_instances, :validate => true

  validates :status, :presence => true
  validates :unit, :presence => true
  validates :category, :presence => true

  # Należy wykonać to przy edycji z kontrolera, gdyz tutaj nie ma
  # dostępu do sesji.
  def log_status_change(user, old_status)
    l = logs.new :user => user, :message => status.get_log_message(old_status)
    l.save

    Delayed::Job.enqueue(MailIssueStatusChanged.new(id, old_status))
  end

  # Dodaje zgłoszenie do systemu
  #
  # category_id - id kategorii zgłoszenia
  # longitude, latitude - współrzędne (string)
  # desc - opis (string; może być nil)
  # notificar_email - e-mail zgłaszającego (string; może być nil)
  # photo - lista ze zdjęciami w Base64 i znacznikami (może być nil)
  #
  # Rzuca NilArguments gdy longitude, latitude lub category_id jest nil
  # Rzuca NoUnitForPoint gdy nie odnajdzie pasującej jednostki do punktu
  # Rzuca UnknownCategory gdy nie odnajdzie category
  # Rzuca IncorrectNotificarEmail gdy notificar_email jest nieprawidłowy
  # Rzuca ActiveRecord::RecordInvalid gdy wystąpi inny błąd walidacji
  #
  # Zwraca obiekt przyjętego zgłoszenia (issue instace)
  # lub nil jeżeli wystąpił jakiś inny błąd
  def self.add_issue(category_id, longitude, latitude, desc, notificar_email, photos)
    raise Exceptions::NilArguments if category_id.nil? or longitude.nil? or
      latitude.nil?

    # <- transakcja
    Issue.transaction do
      longitude = BigDecimal.new(longitude)
      latitude = BigDecimal.new(latitude)

      begin
        category = Category.find(category_id)
      rescue ActiveRecord::RecordNotFound
        raise Exceptions::UnknownCategory.new
      end

      unit = Unit.find_unit_by_point(longitude, latitude)

      raise Exceptions::NoUnitForPoint.new if unit.nil?

      #TODO replace with get_close_issues
      p_lat = 0.0001
      p_lng = 0.0001

      i = Issue.where(:category_id => category.id,
        :latitude => (latitude - p_lat)..(latitude + p_lat),
        :longitude => (longitude - p_lng)..(longitude + p_lng)).first

      if i.nil?
        i = Issue.new :latitude => latitude, :longitude => longitude,
          :category => category, :unit => unit,
          :status => Status.get_default_status

        begin
          i.address = Address.create_by_position(latitude, longitude)
        rescue Exception => e
          logger.error "Błąd pobierania adresu: #{e.message}"
        end

        i.save!
      end

      issue_instance = i.issue_instances.build :latitude => latitude,
        :longitude => longitude, :desc => desc,
        :notificar_email => notificar_email

      begin
        i.save!
      rescue ActiveRecord::RecordInvalid => e
        raise Exceptions::IncorrectNotificarEmail.new if !issue_instance.errors.messages[:notificar_email].nil?
        raise e
      end

      if !photos.nil?
        photos.each do |p|
          photo = issue_instance.photos.build :photo => p[:image], :mime_type => p[:image_type]
          p[:markers].each do |m|
            photo.markers.build :x => m[:x], :y => m[:y], :desc => m[:desc]
          end
        end
        issue_instance.save!
      end

      Delayed::Job.enqueue(MailIssueAdded.new(issue_instance.id))

      return issue_instance

    end
    # transakcja ->

  end

  # Funkcja przepina IssueInstances z podanego issue do self, usuwajac puste Issue
  def join_with(other_issue)
    Issue.transaction do      
      raise Exceptions::UnknownIssue if other_issue == nil
      
      self.issue_instances<< other_issue.issue_instances
      self.save!
            
      other_issue.destroy
    end
  end
  
  # Zwraca tablice Issue znajdujacych sie blisko tego Issue. Przydatne do join_with
  def get_close_issues
    p_lat = 0.0001
    p_lng = 0.0001
  
    issues = Issue.where(:latitude => (latitude - p_lat)..(latitude + p_lat),
        :longitude => (longitude - p_lng)..(longitude + p_lng)).to_a
    
    issues.delete_if { |issue| issue.id == id }
  end
  
end
