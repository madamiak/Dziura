# -*- encoding : utf-8 -*-
class UnitsController < ApplicationController
  before_filter :require_admin

  layout "admin"

  # GET /units
  # GET /units.json
  def index
    @units = Unit.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render :json => @units }
    end
  end

  # GET /units/1
  # GET /units/1.json
  def show
    @unit = Unit.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render :json => @unit.to_json(:include => { :polygons => { :include => :points }}) }
    end
  end

  # GET /units/new
  # GET /units/new.json
  def new
    @unit = Unit.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render :json => @unit }
    end
  end

  # GET /units/1/edit
  def edit
    @unit = Unit.find(params[:id])
  end

  # POST /units
  # POST /units.json
  def create
    @unit = Unit.new(params[:unit])

    polys = ActiveSupport::JSON.decode(params[:polygons])
    polys["polygons"].each do |poly|
      railsPoly = Polygon.new()

      poly["points"].each do |pt|
        railsPt = Point.new( :number => pt["number"],
                             :latitude => BigDecimal(pt["latitude"].to_s()),
                             :longitude => BigDecimal(pt["longitude"].to_s()) )
        railsPt.save()
        railsPoly.points << railsPt;
      end

      @unit.polygons << railsPoly;
    end

    respond_to do |format|
      if @unit.save
        format.html { redirect_to @unit, :notice => 'Unit was successfully created.' }
        format.json { render :json => @unit, :status => :created, :location => @unit }
      else
        format.html { render :action => "new" }
        format.json { render :json => @unit.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /units/1
  # PUT /units/1.json
  def update
    @unit = Unit.find(params[:id])

    respond_to do |format|
      if @unit.update_attributes(params[:unit])
        format.html { redirect_to @unit, :notice => 'Unit was successfully updated.' }
        format.json { head :no_content }
      else
        format.html { render :action => "edit" }
        format.json { render :json => @unit.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /units/1
  # DELETE /units/1.json
  def destroy
    @unit = Unit.find(params[:id])
    @unit.destroy

    respond_to do |format|
      format.html { redirect_to units_url }
      format.json { head :no_content }
    end
  end
end
