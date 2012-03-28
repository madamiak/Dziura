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
  # photo - string, zdjęcie w base64
  # category - id category
  # longitude, latitude - string
  #
  # Rzuca NilArguments gdy lgn, lat lub category jest nil
  # Rzuca NoUnitForPoint gdy nie odnajdzie pasującej jednostki do punktu
  # Rzuca UnknownCategory gdy nie odnajdzie category
  # Rzuca IncorrectNotificarEmail gdy notificar email jest nieprawidłowy
  # Rzuca ActiveRecord::RecordInvalid gdy wystąpi inny błąd walidacji
  def self.add_issue(desc, notificar_email, category, longitude, latitude,
    photo, marker_x, marker_y)

    raise Exceptions::NilArguments if category.nil? or longitude.nil? or
      latitude.nil?

    # <- transakcja
    Issue.transaction do
      longitude = BigDecimal.new(longitude)
      latitude = BigDecimal.new(latitude)

      begin
        category = Category.find(category)
      rescue ActiveRecord::RecordNotFound
        raise Exceptions::UnknownCategory.new
      end

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

        begin
          i.address = Address.create_by_position(latitude, longitude)
        rescue ActiveRecord::RecordInvalid => e
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
        raise Exceptions::IncorrectNotificarEmail.new if !i.errors.messages[:notificar_email].nil?
        raise e
      end

      if !photo.nil? and photo != ""
        photo = issue_instance.photos.build :photo => photo

        issue_instance.save!

        if !marker_x.nil? and !marker_y.nil?
          photo.markers.build :x => marker_x, :y => marker_y
        end
      end

      Delayed::Job.enqueue(MailIssueAdded.new(issue_instance.id))

      return issue_instance

    end
    # transakcja ->

  end

end
