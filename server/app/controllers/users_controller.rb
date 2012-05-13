# -*- encoding : utf-8 -*-

# CRUD dla użytkowników
#
# Kod wygenerowany automatycznie przez szablon Rails.
#
class UsersController < ApplicationController
  before_filter :require_admin

  layout 'admin'

  # GET /users
  # GET /users.json
  def index
    @users = User.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render :json => @users }
    end
  end

  # GET /users/1.json
  def show
    @user = User.find(params[:id])

    respond_to do |format|
      format.html { render :layout => 'bare' } # show.html.erb
      format.json { render :json => @user }
    end
  end

  # GET /users/new
  # GET /users/new.json
  def new
    @user = User.new

    respond_to do |format|
      format.html { render :layout => 'bare' }
      format.json { render :json => @user }
    end
  end

  # GET /users/1/edit
  def edit
    @user = User.find(params[:id])
    render :layout => 'bare'
  end

  # POST /users
  # POST /users.json
  def create
    @user = User.new(params[:user])

    respond_to do |format|
      if @user.save
        format.html { redirect_to @user, :notice => 'Użytkownik został utworzony.' }
        format.json { render :json => @user, :status => :created, :location => @user }
      else
        format.html { render :action => "new", :layout => 'bare' }
        format.json { render :json => @user.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /users/1
  # PUT /users/1.json
  def update
    @user = User.find(params[:id])

    respond_to do |format|
      if @user.update_attributes(params[:user])
        format.html { redirect_to @user, :notice => 'Użytkownik został zaktualizowany.' }
        format.json { head :no_content }
      else
        format.html { render :action => "edit", :layout => 'bare' }
        format.json { render :json => @user.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /users/1
  # DELETE /users/1.json
  def destroy
    @user = User.find(params[:id])
    @user.destroy

    respond_to do |format|
      format.html { redirect_to users_url }
      format.json { head :no_content }
    end
  end
end
