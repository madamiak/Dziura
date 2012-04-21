# -*- encoding : utf-8 -*-

# CRUD dla statusów
#
# Kod wygenerowany automatycznie przez szablon Rails
#
class StatusesController < ApplicationController
  before_filter :require_admin

  layout "admin"

  # GET /statuses
  # GET /statuses.json
  def index
    @statuses = Status.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render :json => @statuses }
    end
  end

  # GET /statuses/1.json
  def show
    @status = Status.find(params[:id])

    respond_to do |format|
      format.json { render :json => @status }
    end
  end

  # GET /statuses/new
  # GET /statuses/new.json
  def new
    @status = Status.new

    respond_to do |format|
      format.html { render :layout => false }
      format.json { render :json => @status }
    end
  end

  # GET /statuses/1/edit
  def edit
    @status = Status.find(params[:id])
    render :layout => false
  end

  # POST /statuses
  # POST /statuses.json
  def create
    @status = Status.new(params[:status])

    respond_to do |format|
      if @status.save
        format.html { redirect_to @status, :notice => 'Status został utworzony.' }
        format.json { render :json => @status, :status => :created, :location => @status }
      else
        format.html { render :action => "new", :layout => false }
        format.json { render :json => @status.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /statuses/1
  # PUT /statuses/1.json
  def update
    @status = Status.find(params[:id])

    respond_to do |format|
      if @status.update_attributes(params[:status])
        format.html { redirect_to @status, :notice => 'Status został zaktualizowany.' }
        format.json { head :no_content }
      else
        format.html { render :action => "edit" }
        format.json { render :json => @status.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /statuses/1
  # DELETE /statuses/1.json
  def destroy
    @status = Status.find(params[:id])
    @status.destroy

    respond_to do |format|
      format.html { redirect_to statuses_url }
      format.json { head :no_content }
    end
  end
end
