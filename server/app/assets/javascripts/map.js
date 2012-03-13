var map;
var existing;
var marker;
var infowindow;
function initialize() {
	var myLatlng = new google.maps.LatLng(51.110,17.030);
	var myOptions = {
		zoom: 14,
		center: myLatlng,
		mapTypeId: google.maps.MapTypeId.ROADMAP
	}
	existing = false;
	map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
	google.maps.event.addListener(map, 'click', function(event, isExisted) {
		placeMarker(event.latLng, existing);
	});
}
function setExisting(value) {
	existing = value;
}
function placeMarker(location, isExisted) {
	if (isExisted == false) {
		marker = new google.maps.Marker({
				position: location,
				map: map,
				draggable: true
		});
		var contentString =
			'<div id="content">'+
				'<div id="bodyContent">'+
					'<form action="...">'+
						'<p>'+'<h3>Formularz zgłoszenia szkody</h3>'+
						'</p>'+
						'<p>Rodzaj szkody: &nbsp'+
							'<select name="nazwa">'+
								'<option>Dziura w jezdni</option>'+
								'<option>Przekrzywiony znak</option>'+
							'</select>'+
						'</p>'+
						'<p>'+
							'<input type="checkbox" name="agreed" />Chcę dostać informacje na e-mail'+'<br />'+
						'</p>'+
						'<p>'+
							'E-mail: &nbsp'+'<input name="email" />'+
						'</p>'+
						'<p>'+
							'<input type="button" value="Zgłoś">'
						'</p>'+
					'</form>'+
				'</div>'+
			'</div>';
		infowindow = new google.maps.InfoWindow({
			content: contentString
		});
		infowindow.open(map, marker);
		setExisting(true);
	}
}
