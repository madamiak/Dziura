/**
 * issues.js - obsługa mapy i tabelki z wyświetlanymi zdarzeniami w panelu admina
 */

var g_issueMarkers = [];
var g_updateTimer = null;
var g_first = true;
var g_issues = new Array();

// Inicjalizacja - wywoływane przy ładowaniu strony
function initializeIssues()
{
  createMap(); // map_common.js

  // Aktualizacja zgłoszeń po zmianie widoku mapki
  google.maps.event.addListener(g_map, 'bounds_changed',
    function()
    {
      if (g_first)
      {
        updateIssues();
        g_first = false;
        return;
      }

      if (g_updateTimer != null)
        clearTimeout(g_updateTimer);

      g_updateTimer = setTimeout(updateIssues, 1000);
    }
  );

  // Ustawienie tabelki
  $('#issues_table').dataTable(
    {
      "bProcessing" : true,
      "bRetrieve" : true,
      "bFilter": false,
      "bJQueryUI": true,
      "aoColumns" :
      [
        { "mDataProp" : "id" },
        { "mDataProp" : "category.name" },
        { "mDataProp" : "status.name" },
        { "mDataProp" : "unit.name" },
        { "mDataProp" :
          function ( source, type, val )
          {
            if (! ('address' in source))
              return "";
            else
              return source.address.street+" "+source.address.home_number+", "+source.address.city;
          }
        } ,
        { "mDataProp" : "created_at" }
      ],
      "oLanguage":
      {
        "sProcessing":   "Proszę czekać...",
        "sLengthMenu":   "Pokaż _MENU_ pozycji",
        "sZeroRecords":  "Nie znaleziono żadnych pasujących indeksów",
        "sInfo":         "Pozycje od _START_ do _END_ z _TOTAL_ łącznie",
        "sInfoEmpty":    "Pozycji 0 z 0 dostępnych",
        "sInfoFiltered": "(filtrowanie spośród _MAX_ dostępnych pozycji)",
        "sInfoPostFix":  "",
        "sSearch":       "Szukaj:",
        "sUrl":          "",
        "oPaginate": {
          "sFirst":    "Pierwsza",
          "sPrevious": "Poprzednia",
          "sNext":     "Następna",
          "sLast":     "Ostatnia"
        }
      }
    }
  );

  // Aktualizacja po zmianie select boxa albo adresu
  $("select").bind("change", updateIssues);
  $("input[name=street]").bind("keyup", updateIssues);

  // Drukowanie zgłoszeń
  $("#printButton").bind("click", printIssues);
}

// Podświetla wiersz w tabelce
function setHihglightRow(issue_id)
{
  $('#issues_table tbody tr ').each(
    function()
    {
      var id = $(this).html();
      id = id.substr(id.indexOf(">")+1);
      id = id.substr(0,id.indexOf("<"));
      if (id==issue_id)
        $(this).toggleClass('ui-state-hover');
    }
  );
}

// Przechodzi do strony z wydrukiem zgłoszeń
function printIssues()
{
  var url = '/issues/print?id=';
  for (var i = 0; i < g_issues.length; i++)
  {
    url = url + g_issues[i].id + ',';
  }

  window.location = url;
}

// Zwraca parametry filtrowania
function getFilterParams()
{
  var params = {};
  params["search"] = {};

  if( $("select[name=category_id]").length>0 && $("select[name=category_id]").val() != 0 ) {
    params["search"]["category_id_equals"] = $("select[name=category_id]").val();
  }

  if( $("select[name=status_id]").length>0 && $("select[name=status_id]").val() != 0 ) {
    params["search"]["status_id_equals"] = $("select[name=status_id]").val();
  }

  if( $("select[name=unit_id]").length>0 && $("select[name=unit_id]").val() != 0 ) {
    params["search"]["unit_id_equals"] = $("select[name=unit_id]").val();
  }

  if( $("input[name=street]").length>0 && $("input[name=street]").val() != "" ) {
    params["search"]["address_street_contains"] = $("input[name=street]").val();
  }

  if( $("select[name=date]").length>0 && $("select[name=date]").val() != 0 ) {
    params["search"]["created_at_greater_than"] = $("select[name=date]").val();
  }

  return params;
}

// Pobiera zgłoszenia
function updateIssues()
{
  var ne = g_map.getBounds().getNorthEast();
  var sw = g_map.getBounds().getSouthWest();

  var params = { ne_lat: ne.lat(), ne_lng: ne.lng(),
  sw_lat: sw.lat(), sw_lng: sw.lng() };

  var urlParams = jQuery.param(params);
  var filterParams = jQuery.param(getFilterParams());
  jQuery.getJSON("/issues/by_rect.json?" + urlParams + "&" + filterParams, issuesReceived);
}

// Przetworzenie pobranych danych
function issuesReceived(data)
{
  g_issues = data;

  for (i in g_issueMarkers)
  {
    g_issueMarkers[i].setVisible(false);
  }
  g_issueMarkers = new Array();

  for (var i = 0; i < data.length; i++)
  {
    var latLng = new google.maps.LatLng(data[i].latitude, data[i].longitude);

    var marker = new google.maps.Marker
      ( {
        map: g_map,
        position: latLng,
        title: "" + data[i].id
      } );

    g_issueMarkers[data[i].id] = marker;

    addIssueClickListener(marker);
    addIssueMouseoverListener(marker);
    addIssueMouseoutListener(marker);
  }

  $('#issues_table').dataTable().fnClearTable();
  $('#issues_table').dataTable().fnAddData(data);
  addTableClickListener();
}

// Funkcje dodające zdarzenia do markerów na mapce

function addIssueClickListener(marker)
{
  google.maps.event.addListener(marker, 'click', function() {
    var id = marker.getTitle();
    editIssueUrl = "/issues/" + id + "/edit";

    // dialog z edycją zgłoszenia
    var dialog = initDialogWindow(editIssueUrl, 400, 500);
    dialog.dialog('open');
  });
}

function addIssueMouseoverListener(marker)
{
  google.maps.event.addListener(marker, 'mouseover', function() {
    setHihglightRow(marker.getTitle());
  });
}

function addIssueMouseoutListener(marker)
{
  google.maps.event.addListener(marker, 'mouseout', function() {
    setHihglightRow(marker.getTitle());
  });
}

function addTableClickListener()
{
  var issueId;
  var editIssueUrlFromTable;
  $('#issues_table tbody tr').click(function() {
    issueId = $(this).find("td").eq(0).text();
   
    editIssueUrlFromTable = "/issues/" + issueId + "/edit";

    // dialog z edycją zgłoszenia
    var dialog = initDialogWindow(editIssueUrlFromTable, 400, 500);
    dialog.dialog('open');
  });
}
