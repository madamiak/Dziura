/**
 * notifies.js - obsługa mapy i podglądu statusu zgłoszenia w interfejsie dla zgłaszającego
 */


/* Strona z mapką */

// marker na mapce
var g_marker;

// Inicjalizacja - funkcja wywoływana przy ładowaniu strony
function initializeNotifies()
{
  createMap(); // map_common.js

  g_marker = null;

  google.maps.event.addListener(g_map, 'click',
    function(event)
    {
      placeMarker(event.latLng);
    }
  );
  
  $(window).resize(resize);
  resize();
}

// Dostosowywuje wysokosc mapki do okna
function resize() {
  var newHeight = $(window).height() * 0.8;
  $("#map_canvas").height(newHeight);
}

// Tworzy marker albo przesuwa istniejący do podanego położenia
function placeMarker(location)
{
  if (g_marker == null)
  {
    g_marker = new google.maps.Marker(
      {
        position: location,
        map: g_map,
        draggable: true
      }
    );

    displayDialog();

    google.maps.event.addListener(g_marker, 'click', displayDialog);
  }
  else
  {
    g_marker.setPosition(location);
  }
}

// Tworzy dialog z formą do zgłaszania
function displayDialog()
{
  var dialog = initDialogWindow('/res/issue', 450, 500, dialogLoaded);
}

// Ustawia formę i inne rzeczy w dialogu po załadowaniu
function dialogLoaded(dialog)
{
  $("#issue_message").addClass('ui-widget');
  $("#issue_message").hide();

  $("#issue_form form ").bind("submit",
    function()
    {
      $("#issue_form input[name=longitude]").val(g_marker.getPosition().lng());
      $("#issue_form input[name=latitude]").val(g_marker.getPosition().lat());
      $("#issue_form input[id=photos]").val(getPhotosInBase64());
    }
  );
  
  // Ukrywanie przycisku "Zglos" wewnatrz okna dialog
  $(":submit").hide();
  
  // Dodawanie przycisku "Zglos" do button_pane okna dialogowego
  var buttons = dialog.dialog("option", "buttons");
  $.extend(buttons, { "Zgłoś": function () { $(":submit").click(); } });
  dialog.dialog("option", "buttons", buttons);

  $("#issue_form form").live("ajax:success",
    function(event, data, status, xhr)
    {
      $("#issue_form").hide();

      $("#issue_message #header_fail").hide();
      $("#issue_message #fail").hide();

      $("#issue_message #header_ok").show();
      $("#issue_message #ok #id").text(data.id);
      $("#issue_message #ok #link").attr('href', '/check_status/' + data.id);
      $("#issue_message #ok").show();

      $("#issue_message").show();

      dialog.dialog(
        {
          buttons: {
            "Zamknij": function () { $(this).dialog('close'); }
          }
        }
      );
    }
  );

  $("#issue_form form").live("ajax:error",
    function(event, data, status, xhr)
    {
      data = JSON.parse(data.responseText);

      $("#issue_message #header_ok").hide();
      $("#issue_message #ok").hide();

      $("#issue_message #header_fail").show();
      $("#issue_message #fail #errors").text(data.message);
      $("#issue_message #fail").show();

      $("#issue_message").show();
    }
  );

  $('#selectable').selectable(
    {
      stop:
      function (event, ui)
      {
        var selectedElem = $('#selectable').children('.ui-selected').first();
        var index = selectedElem.index() + 1;
        if (index == 0)
        {
          $('#category_name_span').text('[wybierz]');
          $('#category_id').val('');
          return;
        }

        jQuery.getJSON('/res/categories/' + index + '.json',
          function(data)
          {
            $('#category_name_span').text(data.name);
            $('#category_id').val(data.id);
          }
        );
      }
    }
  );

  $('#photo').live('change',
    function()
    {
      $('#photo_preview').html('Trwa wczytywanie zdjęcia....');
      $('#photo_form').ajaxForm(
        {
          target: '#photo_preview'
        }).submit();
    }
  );

  dialog.dialog('open');
}

// Zwraca zuploadowane zdjęcie w Base64
function getPhotosInBase64()
{
  var img = $("#uploaded_image").attr("src");

  if( img != undefined )
  {
    return img.split(',')[1];
  }

  return '';
}


/* Strona z podglądem zgłoszenia */

// Inicjalizacja - wywoływane przy ładowaniu strony
// param_id to opcjonalne ID przekazane w zapytaniu
function initializeCheckStatus(param_id)
{
  if (param_id)
  {
    getIssueData(param_id);
    $('input[id=id]').val(param_id);
  }

  $('#get_data_button').bind("click",
    function()
    {
      var id = $('input[id=id]').val();
      getIssueData(id);
    }
  );
}

// Funkcja pobierająca dane zgłoszenia
function getIssueData(id)
{
  $.ajax(
    {
      'type': 'GET',
      'url': '/res/issue_instances/' + id,
      'error':
        function(xhr)
        {
          if (xhr.status == 404)
            $('#issue_data').html('Zgłoszenie o podanym ID nie istnieje!');
          else
            $('#issue_data').html('Przepraszamy, wystąpił błąd serwera');
        },
      'success':
        function(data)
        {
          $('#issue_data').html(data);
        }
     }
  );
}
