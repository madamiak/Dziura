/* Wyświetlanie issues na mapie */

var map;

function createMap()
{
  var myOptions =
  {
    center: new google.maps.LatLng(51.1101, 17.0324),
    zoom: 13,
    mapTypeId: google.maps.MapTypeId.ROADMAP,
    disableDoubleClickZoom: true
  };

  map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
}

var issueMarkers = [];
var updateTimer = null;
var first = true;

function initialize()
{
  createMap();

  google.maps.event.addListener(map, 'bounds_changed', function() {
    if (first)
    {
      updateIssues();
      first = false;
      return;
    }

    if (updateTimer != null)
      clearTimeout(updateTimer);

    updateTimer = setTimeout(updateIssues, 1000);
  });
}

function updateIssues()
{
  var ne = map.getBounds().getNorthEast();
  var sw = map.getBounds().getSouthWest();

  var params = { ne_lat: ne.lat(), ne_lng: ne.lng(),
  sw_lat: sw.lat(), sw_lng: sw.lng() };

  var urlParams = jQuery.param(params);
  jQuery.getJSON("/issues/by_rect.json?" + urlParams, issuesReceived);
}

function issuesReceived(data)
{
  for (var i = 0; i < issueMarkers.length; i++)
  {
    issueMarkers[i].setMap(null);
  }
  issueMarkers = [];

  for (var i = 0; i < data.length; i++)
  {
    var latLng = new google.maps.LatLng(data[i].latitude, data[i].longitude);

    var marker = new google.maps.Marker
      ( {
        map: map,
        position: latLng,
        title: "" + data[i].id
      } );

    addIssueClickListener(marker);
  }
}

function addIssueClickListener(marker)
{
  google.maps.event.addListener(marker, 'click', function() {
    var id = marker.getTitle();

    $.get('/issues/'+id+'/edit', function(data) {
			  infowindow = new google.maps.InfoWindow({
				  content: data
		  });

		  infowindow.open(map, marker);

		  google.maps.event.addListener(infowindow, 'domready', function() {
			  bindEditIssueForm();
		  });
    });

    infoWindow.open(map, marker);
  });
}
