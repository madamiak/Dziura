var g_existing;
var g_marker;
var g_infowindow;

function initialize() {
  createMap(); // map_common.js

	g_existing = false;

	google.maps.event.addListener(g_map, 'click', function(event, isExisted) {
		placeMarker(event.latLng, g_existing);
	});
}

function setExisting(value) {
	g_existing = value;
}

function getPhotoInBase64() {
  var img = $("#upload_iframe").contents().find("img").attr("src");

  if( img != undefined ) {
    return img.split(',')[1];
  }
  
  return ''; 
}

function bindIssueForm() {
	$("#issue_form form").bind("submit", function() {
		$("#issue_form input[name=longitude]").val(g_marker.getPosition().lng());
		$("#issue_form input[name=latitude]").val(g_marker.getPosition().lat());
		$("#issue_form input[name=photo]").val(getPhotoInBase64 ());
	});

	$("#issue_form form").live("ajax:success", function(event, data, status, xhr) {
		$("#issue_message").html(data.responseText);
	});

	$("#issue_form form").live("ajax:error", function(event, data, status, xhr) {
		$("#issue_message").html(data.responseText);
	});
}

function placeMarker(location, isExisted) {
	if (isExisted == false) {
		g_marker = new google.maps.Marker({
			position: location,
			map: g_map,
			draggable: true
		});

		//pobieranie formularza zgloszenia szkody z serwera
		$.get('res/issue', function(data) {
			g_infowindow = new google.maps.InfoWindow({
				content: data
		  });

		  g_infowindow.open(g_map, g_marker);

		  google.maps.event.addListener(g_infowindow, 'domready', function() {
			  bindIssueForm();
		  });
    });
    
    google.maps.event.addListener(g_marker, 'click', function() {
		g_infowindow.open(g_map, g_marker);
	});

	setExisting(true);
	} 
	else {
		g_marker.setPosition(location);
	}
}
