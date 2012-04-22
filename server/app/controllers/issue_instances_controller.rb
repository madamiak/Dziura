# -*- encoding : utf-8 -*-

# Kontroler pojedynczego zgłoszenia
#
# Służy jedynie do podglądu statusu zgłoszenia.
#
class IssueInstancesController < ApplicationController
  skip_before_filter :require_login

  layout false

  # GET /res/issue_instances/1
  # GET /res/issue_instances/1.json
  def get_by_id
    @issue_instances = IssueInstance.find(params[:id])

    respond_to do |format|
      format.html # get_by_id.html.erb
      format.json { render :json => @issue_instances, :except => [:issue_id] }
    end
  end

end
