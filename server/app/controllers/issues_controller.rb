# -*- encoding : utf-8 -*-

# CRUD dla zgłoszeń (Issues)
#
# Kod wygenerowany automatycznie przez szablon Rails i uzupełniony
# o dodatkowe funkcje.
#
class IssuesController < ApplicationController
  layout "admin"

  # GET /issues
  # GET /issues.json
  #
  # Wyświetla index.html.erb -- stronę zarządzania zgłoszeniami
  def index
    @issues = Issue.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render :json => @issue }
    end
  end

  # GET /issues/1
  # GET /issues/1.json
  #
  # Zwraca show.html.erb bez szablonu (sam kod HTML)
  def show
    @issue = Issue.find(params[:id])

    respond_to do |format|
      format.html { render :layout => 'bare' } # show.html.erb
      format.json { render :json => @issue }
    end
  end

  # GET /issues/new
  # GET /issues/new.json
  #
  # Zwraca new.html.erb bez szablonu (sam kod HTML)
  def new
    @issue = Issue.new

    respond_to do |format|
      format.html { render :layout => 'bare' } # new.html.erb
      format.json { render :json => @issue }
    end
  end

  # GET /issues/1/edit
  #
  # Zwraca edit.html.erb bez szablonu (sam kod HTML)
  def edit
    @issue = Issue.find(params[:id])
    render :layout => 'bare' # edit.html.erb
  end

  # POST /issues
  # POST /issues.json
  #
  # Dodaje nowe zgłoszenie
  def create
    @issue = Issue.new(params[:issue])

    respond_to do |format|
      if @issue.save
        format.html { redirect_to @issue, :notice => 'Zgłoszenie zostało utworzone.' }
        format.json { render :json => @issue, :status => :created, :location => @issue }
      else
        format.html { render :action => "new", :layout => 'bare' }
        format.json { render :json => @issue.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /issues/1
  # PUT /issues/1.json
  #
  # Aktualizuje dane zgłoszenie
  def update
    @issue = Issue.find(params[:id])

    respond_to do |format|

      if !@issue.nil?
        old_status = @issue.status
      end

      if @issue.update_attributes(params[:issue])
        # Logowanie zmian statusu
        if (@issue.status.id != old_status.id)
          @issue.log_status_change(current_user, old_status)
        end

        format.html { redirect_to @issue, :notice => 'Zgłoszenie zostało zaktualizowane.' }
        format.json { head :no_content }
      else
        format.html { render :action => "edit", :layout => 'bare' }
        format.json { render :json => @issue.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /issues/1
  # DELETE /issues/1.json
  #
  # Usuwa dane zgłoszenie
  def destroy
    @issue = Issue.find(params[:id])
    @issue.destroy

    respond_to do |format|
      format.html { redirect_to issues_url }
      format.json { head :no_content }
    end
  end

  # GET /issues/by_rect.json
  #
  # Zwraca listę zgłoszeń znajdujących się w prostokącie współrzędnych geogr.
  # plus odfiltrowane przez zadane kryteria
  #
  # Argumenty:
  #  sw_lat, sw_lng - płd.-zach. wierzchołek
  #  ne_lat, ne_lng - płn.-wsch. wierzchołek
  #  search - tablica z dodatkowymi kryteriami
  def get_by_rect
    params[:search] = {} if params[:search].nil?

    params[:search][:latitude_greater_than] = BigDecimal.new(params[:sw_lat])
    params[:search][:latitude_less_than] = BigDecimal.new(params[:ne_lat])
    params[:search][:longitude_greater_than] = BigDecimal.new(params[:sw_lng])
    params[:search][:longitude_less_than] = BigDecimal.new(params[:ne_lng])

    if !current_user.unit.nil?
      params[:search][:unit_id_equals] = current_user.unit.id
    end

    @issues = Issue.search(params[:search])

    respond_to do |format|
      format.json { render :json =>
        @issues.all.to_json(:include => {
          :address => { :only => [:city, :street, :home_number, :id] },
          :category =>  { :only => [:name, :id] },
          :status => { :only => [:name, :color, :id] },
          :unit => { :only => [:name, :id] } }) }
    end
  end

  # GET /issues/by_pages.json
  #
  # Zwraca listę zgłoszeń z podziałem na paginację.
  #
  # Argumenty:
  #  limit - ile na stronę
  #  offset - od którego zacząć
  def get_by_pages
    @issues = Issue.order(:created_at).limit(params[:limit]).offset(params[:offset])

    respond_to do |format|
      format.json { render :json => @issues }
    end
  end

  # GET /issues/1/detach
  #
  # ! 1 to id IssueInstance, a nie Issue !
  #
  # Odłącza daną instancję od zgłoszenia (tworzy osobne zgłoszenie)
  # Przekierowuje z powrotem do edycji oryginalnego zgłoszenia.
  def detach
    @issue_instance = IssueInstance.find(params[:id])

    issue_id = @issue_instance.issue.id

    @issue_instance.detach

    @issue = Issue.find(issue_id)

    render :action => "edit", :layout => 'bare'
  end

  # GET /issues/1/join/2
  #
  # Łączy dwa podane zgłoszenia. Przekierowuje do edycji złączonego zgłoszenia.
  def join
    @issue = Issue.find(params[:id])
    @other_issue = Issue.find(params[:other_id])

    @issue.join_with(@other_issue)

    render :action => "edit", :layout => 'bare'
  end

  # GET /issues/print?id=1&id=2&...
  #
  # Drukuje lub eksportuje do JSON dane zgłoszenia.
  def print
    ids = params[:id].split(',')
    @issues = Issue.find(ids)

    respond_to do |format|
      format.html { render :layout => false }
      format.json {
        json_data = @issues.to_json( :include => [ :address, :unit, :status, { :category => { :except => [:icon] } } ] )
        send_data json_data, :type => 'application/json', :disposition => 'attachment' }
    end
  end

end
