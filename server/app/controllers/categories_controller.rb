# -*- encoding : utf-8 -*-

# CRUD dla kategorii
#
# Kod wygenerowany automatycznie przez szablon Rails.
#
# Jedyna zmiana to przekodowanie zdjęć do Base64 w POST i PUT.
#
class CategoriesController < ApplicationController
  before_filter :require_admin

  layout "admin"

  # GET /categories
  # GET /categories.json
  def index
    @categories = Category.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render :json => @categories }
    end
  end

  # GET /categories/1.json
  def show
    @category = Category.find(params[:id])

    respond_to do |format|
      format.html { render :layout => false } # show.html.erb
      format.json { render :json => @category }
    end
  end

  # GET /categories/new
  # GET /categories/new.json
  def new
    @category = Category.new

    respond_to do |format|
      format.html { render :layout => false }
      format.json { render :json => @category }
    end
  end

  # GET /categories/1/edit
  def edit
    @category = Category.find(params[:id])
    render :layout => false
  end

  # POST /categories
  # POST /categories.json
  def create

    icon = nil
    if !params[:category][:icon].blank?
      icon = Base64.encode64(params[:category][:icon].read)
    end

    @category = Category.new(:name => params[:category][:name], :icon => icon)

    respond_to do |format|
      if @category.save
        format.html { redirect_to @category, :notice => 'Kategoria została utworzona' }
        format.json { render :json => @category, :status => :created, :location => @category }
      else
        format.html { render :action => "new", :layout => false }
        format.json { render :json => @category.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /categories/1
  # PUT /categories/1.json
  def update

    icon = nil
    if !params[:category][:icon].blank?
      icon = Base64.encode64(params[:category][:icon].read)
    end

    @category = Category.find(params[:id])

    respond_to do |format|
      if @category.update_attributes(:name => params[:category][:name], :icon => icon)
        format.html { redirect_to @category, :notice => 'Kategoria została zaktualizowana' }
        format.json { head :no_content }
      else
        format.html { render :action => "edit", :layout => false }
        format.json { render :json => @category.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /categories/1
  # DELETE /categories/1.json
  def destroy
    @category = Category.find(params[:id])
    @category.destroy

    respond_to do |format|
      format.html { redirect_to categories_url }
      format.json { head :no_content }
    end
  end
end
