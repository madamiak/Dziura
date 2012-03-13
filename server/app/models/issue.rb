class Issue < ActiveRecord::Base
  belongs_to :status
  belongs_to :category
  belongs_to :unit

  belongs_to :status
  has_many :logs, :as => :loggable

  validates :status, :presence => true
  validates :unit, :presence => true
  validates :category, :presence => true

  # Nalezy wykonać to przy edycji z kontrolera, gdyz tutaj nie ma
  # dostepu do sesji.
  def log_status_change(user, old_status)
    l = logs.new :user => user, :message => status.get_log_message(old_status)
    l.save
  end

  # Metoda sluzaca do dodawania nowego issue oraz issue_instance
  #
  # Metoda probuje dopasowac issue, jezeli to sie nie uda tworzy nowy issue.
  # Metoda zawsze tworzy nowy issue_instance i przypisuje go do odpowiedniego issue (dopasowanego lub nowego).
  ## czy ta metoda nie powinna byc raczej w issue_instance (przyjmuje argumenty z REST issue_instance)
  ## brakuje obslugi zdjec
  def add_issue (photo, desc, notificar_email, category_id, longitude, latitude)
    # dokladnosc dopasowania issue
    precision_latitude = 0.0001
    precision_longitude = 0.0001

    # szukanie istniejacego issue
    i = issue.where(:category_id => category_id, :latitude => (latitude-precision_latitude)..(latitude+precision_latitude), :longitude => (longitude-precision_longitude)..(longitude+precision_longitude))

    # jezeli nie dopasowano zadnego issue
    ## czy ponizszy zapis jest prawidlowy?
    if !i.exist()
      ## czy trzeba recznie sprawdzac i podawac do jakiego unit nalezy dany issue?
      ## czy ustawiac status_id, czy ustawi sie jakis domyslny?
      ## chyba warto sprawdzac poprawnosc category_id?
      i = issue.new :latitude => latitude, :longitude => longitude, :category_id => category_id
      i.save
    end

    # tworzenie nowego issue_instance
    ## jak przyporzadkowac issue_instance do issue?
    ## nalezy tworzyc obiekt address i uzupelnic go na podstawie danych z zewnetrznego API
    ## nie mam pojecia jak wykorzystac google api w railsach
    issue_instance_new = issue_instance.new :latitude => latitude, :longitude => longitude, :desc => desc, :notificar_email => notificar_email
    issue_instance_new.save
  end
end
