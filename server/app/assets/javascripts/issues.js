/**
 * issues.js - obsługa mapy i tabelki z wyświetlanymi zdarzeniami w panelu admina
 */

var g_issueMarkers = [];
var g_updateTimer = null;
var g_first = true;
var g_issues = new Array();
var g_mapClickListener = null;
var g_newIssueMarker = null;
var g_editId = null;
var flag = false;
// Z usługi Google :)
var g_iconSource = "http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|"

// Inicjalizacja - wywoływane przy ładowaniu strony
function initializeIssues()
{
  createMap(); // map_common.js

  if ($('#map_center').length > 0)
  {
    g_map.setCenter(new google.maps.LatLng(
      $('#map_center #lat').text(),
      $('#map_center #lng').text())
    );
  }

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
  $("input[name=id]").bind("keyup", updateIssues);

  // Nowe zgłoszenie
  $("#newIssueButton").bind("click", newIssue);

  // Drukowanie zgłoszeń
  $("#printButton").bind("click", printIssues);

  // Eksport zgłoszeń
  $("#exportButton").bind("click", exportIssues);

  $(window).resize(resizeIssues);
  resizeIssues();
}

// Dostosowywuje wysokosc mapki do okna
function resizeIssues() {
  var newHeight = $(window).height() * 0.5;
  $("#map_canvas").height(newHeight);
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

// Wyświetla informację, jak dodać nowe zgłoszenie i dodaje listener do mapy
function newIssue()
{
  alert('Kliknij na mapę w miejscu, gdzie chcesz dodać nowe zgłoszenie');

  g_mapClickListener = google.maps.event.addListener(g_map, 'click',
    function(event)
    {
      g_newIssueMarker = new google.maps.Marker
       ( {
          map: g_map,
          position: event.latLng
        } );
      initDialogWindow('/issues/new', 400, 600, initNewIssueDialog);
    }
  );
}

// Ustawia lat i lng w załadowanej formie zgłoszenia
function initNewIssueDialog(dialog)
{
  asynchronousSubmit('#issue_submit', updateIssues);
  asynchronousSubmit('#attach_submit', updateIssues);

  $('#issue_latitude').val(g_newIssueMarker.getPosition().lat());
  $('#issue_longitude').val(g_newIssueMarker.getPosition().lng());
  dialog.dialog('open');

  g_newIssueMarker.setMap(null);
  google.maps.event.removeListener(g_mapClickListener);
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

// Otwiera nowe okno z eksportem zgłoszeń
function exportIssues()
{
  var url = '/issues/print.json?id=';
  for (var i = 0; i < g_issues.length; i++)
  {
    url = url + g_issues[i].id + ',';
  }

  window.open(url);
}

// Zwraca parametry filtrowania
function getFilterParams()
{
  var params = {};
  params["search"] = {};

  if( $("input[name=id]").length>0 && $("input[name=id]").val() != 0 ) {
    params["search"]["id_equals"] = $("input[name=id]").val();
  }

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

    var icon = new google.maps.MarkerImage(
      g_iconSource + data[i].status.color,
      new google.maps.Size(21, 34),
      new google.maps.Point(0,0),
      new google.maps.Point(10, 34)
    );

    var marker = new google.maps.Marker
      ( {
        map: g_map,
        position: latLng,
        title: "" + data[i].id,
        icon: icon
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
    g_editId = marker.getTitle();

    // dialog z edycją zgłoszenia
    var dialog = initDialogWindow("/issues/" + g_editId + "/edit", 800, 600, initEditIssueDialog, updateIssues);
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
  if(!flag) {
  	flag = true;
	  $('#issues_table tbody tr').live("click", function(event) {
	  	event.stopPropagation();
	    g_editId = $(this).find("td").eq(0).text();
	
	    // dialog z edycją zgłoszenia
	    var dialog = initDialogWindow("/issues/" + g_editId + "/edit", 800, 600, initEditIssueDialog, updateIssues);
	  });
	}
}

function initEditIssueDialog(dialog)
{
  asynchronousSubmit('#issue_submit', null);
  asynchronousSubmit('#attach_submit', null, reinitDialog);
  asynchronousSubmit('.detach_submit', null, reinitDialog);

  dialog.dialog('open');
}

// Funkcja wywoływana po złączeniu/odłączeniu zgłoszenia,
//  żeby przeładować dialog i na nowo ustawić eventy funkcją initEditIssueDialog
function reinitDialog()
{
  setContentDialogWindowFromUrl( $('#'+idOfDialogForm),
                                 "/issues/" + g_editId + "/edit", initEditIssueDialog);
}
