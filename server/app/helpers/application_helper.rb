# -*- encoding : utf-8 -*-
module ApplicationHelper

  # Włączanie CSS dla danego kontrolera
  def include_controller_assets

    controller_name = controller.controller_name

    html = ""

    # JS też można dodać w ten sposób
    #if Dziura::Application.assets.find_asset(controller_name + '.js') != nil
    #  html << javascript_include_tag (controller_name + '.js')
    #end

    if Dziura::Application.assets.find_asset(controller_name + '.css') != nil
      html << stylesheet_link_tag (controller_name + '.css')
    end

    raw(html)

  end

  # Wyświetlanie adresu (może się przydać w kilku miejscach)
  def display_address(address)

    html = ""

    html << "<b>Miasto: </b>"
    html << (address.city || "")
    html << "<br>"

    html << "<b>Kod pocztowy: </b>"
    html << (address.zip || "")
    html << "<br>"

    html << "<b>Ulica: </b>"
    html << (address.street || "")
    html << "<br>"

    html << "<b>Nr domu: </b>"
    html << (address.home_number || "")
    html << "<br>"

    html << "<b>Dodatkowe informacje: </b>"
    html << (address.additional_info || "")
    html << "<br>"

    raw(html)

  end

end
