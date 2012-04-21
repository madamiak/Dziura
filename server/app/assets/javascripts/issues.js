/**
 * issues.js - obsługa mapy i tabelki z wyświetlanymi zdarzeniami w panelu admina
 */


function bindEditIssueForm() {
  $("#issue_edit").live("ajax:success", function(event, data, status, xhr) {
    $("#issue_edit").html(data.responseText);
  });

  $("#issue_edit").live("ajax:error", function(event, data, status, xhr) {
    $("#issue_edit").html(data.responseText);
  });
}

$(document).ready(function() {

  var m = new mapTable;
  m.aa();

  $('#example').each(function() {
    $('#example').dataTable({
      "bProcessing" : true,
      "bRetrieve" : true,
      "bFilter": false,
      "bJQueryUI": true,
      "aoColumns" : [ {
        "mDataProp" : "id"
      }, {
        "mDataProp" : "category.name"
      }, {
        "mDataProp" : "status.name"
      }, {
        "mDataProp" : "unit.name"
      }, {
        "mDataProp" : function ( source, type, val ) {
          if (! ('address' in source))
            return "";
          else
            return source.address.street+" "+source.address.home_number+", "+source.address.city;
        }
      }
      , {
        "mDataProp" : "created_at"
      }
      ]
    });
  });
});

function mapTable() {
  this.aa = function(){
    initialize();
    $('#example tbody tr ').live( 'click', function () {

      var id = $(this).html();
      id = id.substr(id.indexOf(">")+1);
      id = id.substr(0,id.indexOf("<"));

    } );
  }

  this.setHihglightRow = function (issue_id){
    $('#example tbody tr ').each(function() {
      var id = $(this).html();
      id = id.substr(id.indexOf(">")+1);
      id = id.substr(0,id.indexOf("<"));
      if (id==issue_id) {
        $(this).toggleClass('ui-state-hover');
      }
    });
  }
}

var g_issueMarkers = [];
var g_updateTimer = null;
var g_first = true;
var issues = new Array();

function initialize()
{
  createMap(); // map_common.js

  google.maps.event.addListener(g_map, 'bounds_changed', function() {
    if (g_first)
    {
      updateIssues();
      g_first = false;
      return;
    }

    if (g_updateTimer != null)
      clearTimeout(g_updateTimer);

    g_updateTimer = setTimeout(updateIssues, 1000);
  });

  $("select").bind("change", updateIssues);
  $("input[name=street]").bind("keyup", updateIssues);
  $("#printButton").bind("click", printIssues);
}

function printIssues() {
  var url = '/issues/print?id=';
  for (var i = 0; i < issues.length; i++) {
    url = url + issues[i].id + ',';
  }

  window.location = url;
}

function getFilterParams() {
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

function issuesReceived(data)
{
  issues = data;

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


    $('#example').dataTable().fnClearTable();
    $('#example').dataTable().fnAddData(data);
  }
}

function addIssueClickListener(marker)
{
	google.maps.event.addListener(marker, 'click', function() {
		var id = marker.getTitle();
		editIssueUrl = "/issues/" + id + "/edit";

		makeDialog();
  });
}

function makeDialog(){
    var dialog = initDialogWindow(editIssueUrl);
}

function addIssueMouseoverListener(marker){
	google.maps.event.addListener(marker, 'mouseover', function() {
	    var id = marker.getTitle();
	    var m = new mapTable;
		m.setHihglightRow(id);

	  });
}
function addIssueMouseoutListener(marker){
	google.maps.event.addListener(marker, 'mouseout', function() {
	    var id = marker.getTitle();
	    var m = new mapTable;
		m.setHihglightRow(id);
	  });
}
