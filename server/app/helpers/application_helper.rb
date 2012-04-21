# -*- encoding : utf-8 -*-

# Helper aplikacji
#
# Zawiera dodatkowe metody używane w widokach
module ApplicationHelper

  # Dołączanie JS Google Maps
  def maps_javascript_tag
    javascript_include_tag("http://maps.googleapis.com/maps/api/js?key=" +
                           Rails.application.config.maps[:api_key] + "&sensor=true")
  end

  # Włączanie CSS dla danego kontrolera
  def include_controller_assets

    controller_name = controller.controller_name

    html = ""

    # JS też można dodać w ten sposób
    #if Dziura::Application.assets.find_asset(controller_name + '.js') != nil
    #  html << javascript_include_tag (controller_name + '.js')
    #end

    if Dziura::Application.assets.find_asset(controller_name + '.css') != nil
      html << stylesheet_link_tag(controller_name + '.css')
    end

    raw(html)

  end

end
