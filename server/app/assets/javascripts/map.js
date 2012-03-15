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
		
		/*var contentString =
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
			'</div>';*/
		
		//pobieranie formularza zgloszenia szkody z serwera	
	  $.get('res/issue', function(data) {
      infowindow = new google.maps.InfoWindow({
			  content: data
		  });
		
		  infowindow.open(map, marker);	
		  
		  google.maps.event.addListener(infowindow, 'domready', function() {
		    $("#issue_form form").submit(function() {
		      $("#issue_form input[name=longitude]").val(marker.getPosition().lng());
          $("#issue_form input[name=latitude]").val(marker.getPosition().lat());
		    });
		  });
    });	
    
    //TODO jak sie zamknie okno, zeby potem mozna bylo je jeszcze raz otworzyc	
		
		setExisting(true);
	} else {
	 // TODO przenies marker na nowa pozycje po kliknieciu gdzies
	}
}
