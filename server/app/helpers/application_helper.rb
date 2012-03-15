# -*- encoding : utf-8 -*-
module ApplicationHelper

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
