/**
 * units.js - obsługa wielokątów na mapie przy zarządzaniu jednostkami
 */

/* Część dotycząca wyświetlania wielokątów jednostki */

// Inicjalizacja - wywoływane po załadowaniu strony
function initializeUnitShow()
{
  var extraOptions = { disableDoubleClickZoom: true };
  createMap(extraOptions); // map_common.js

  var data = JSON.parse( $('#polygons').text() || '[]' );

  var mapBounds = new google.maps.LatLngBounds();

  for (var i = 0; i < data.length; i++)
  {
    var poly = data[i];
    var points = new google.maps.MVCArray();

    poly.points.sort(function(a, b){
      return a.number-b.number
    });

    for (var j = 0; j < poly.points.length; ++j)
    {
      var pt = poly.points[j];
      var latLng = new google.maps.LatLng(pt.latitude, pt.longitude);
      points.push(latLng);
      mapBounds.extend(latLng);
    }

    var polygon = new google.maps.Polygon(
      {
        map: g_map,
        paths: points
      }
    );
  }

if (data.length > 0)
  g_map.fitBounds(mapBounds);

  $(window).resize(resizeUnits);
  resizeUnits();
}

// Dostosowywuje wysokosc mapki do okna
function resizeUnits() {
  var newHeight = $(window).height() * 0.6;
  $("#map_canvas").height(newHeight);
}

// --------------------

/* Część dotycząca tworzenia i edycji wielokątów jednostki */

var g_polygonMarkers = [];
var g_polygons = [];
var g_closePolygonListener;

// Inicjalizacja - wywoływane po załadowaniu strony
function initializeUnitNew()
{
  var extraOptions = { disableDoubleClickZoom: true };
  createMap(extraOptions); // map_common.js

  google.maps.event.addListener(g_map, 'click', mapClicked);

  $(window).resize(resizeUnits);
  resizeUnits();
}

// Inicjalizacja - wywoływane po załadowaniu strony
function initializeUnitEdit()
{
  var extraOptions = { disableDoubleClickZoom: true };
  createMap(extraOptions); // map_common.js

  google.maps.event.addListener(g_map, 'click', mapClicked);

  var data = JSON.parse( $('#original_polygons').text() );

  var mapBounds = new google.maps.LatLngBounds();

  for (var i = 0; i < data.length; i++)
  {
    var poly = data[i];

    var points = new google.maps.MVCArray();

    poly.points.sort(function(a, b){
      return a.number-b.number
    });

    for (var j = 0; j < poly.points.length; j++)
    {
      var pt = poly.points[j];
      var latLng = new google.maps.LatLng(pt.latitude, pt.longitude);
      points.push(latLng);
      mapBounds.extend(latLng);
    }

    createEditablePolygon(points);
  }

  if (data.length > 0)
    g_map.fitBounds(mapBounds);

  updatePolygonsJSON();

  $(window).resize(resizeUnits);
  resizeUnits();
}

// Po kliknięciu na mapę
function mapClicked(event)
{
  var pointNum = g_polygonMarkers.length + 1;

  var marker = new google.maps.Marker(
    {
      map: g_map,
      position: event.latLng,
      draggable: true,
      title: "" + pointNum
    }
  );

  g_polygonMarkers.push(marker);

  if (pointNum == 1)
  {
    g_closePolygonListener = google.maps.event.addListener(marker, 'click', closePolygon);
  }
}

// Zamyka wielokąt z zaznaczonych punktów
function closePolygon(event)
{
  if (g_polygonMarkers.length < 3)
  {
    alert("Wielokąt musi mieć co najmniej 3 punkty!");
    return;
  }

  var points = new google.maps.MVCArray();

  for (var i = 0; i < g_polygonMarkers.length; i++)
  {
    var mk = g_polygonMarkers[i];
    points.push(mk.getPosition());
    mk.setMap(null);
  }

  createEditablePolygon(points);

  google.maps.event.removeListener(g_closePolygonListener);
  g_polygonMarkers = [];

  updatePolygonsJSON();
}

// Tworzy wielokąt na mapie z danych punktów
function createEditablePolygon(points)
{
  google.maps.event.addListener(points, 'insert_at', function(a) { updatePolygonsJSON() });
  google.maps.event.addListener(points, 'remove_at', function(a,b) { updatePolygonsJSON() });
  google.maps.event.addListener(points, 'set_at', function(a,b) { updatePolygonsJSON() });

  var polygon = new google.maps.Polygon
    ( {
        map: g_map,
        paths: points,
        editable: true
      } );

  g_polygons.push(polygon);

  addPolygonDeleteListener(polygon);
}

// Dodaje listener do usuwania wielokąta po dwukliku
function addPolygonDeleteListener(polygon)
{
  google.maps.event.addListener(polygon, 'dblclick', function() {
    for (var i = 0; i < g_polygons.length; i++)
    {
      if (g_polygons[i] == polygon)
        g_polygons.splice(i, 1);
    }

    polygon.setMap(null);

    updatePolygonsJSON();
  });
}

// Aktualizuje ukryte pole z JSON'em wielokąta
function updatePolygonsJSON()
{
  var polygonsExport = [];

  for (var i = 0; i < g_polygons.length; i++)
  {
    var poly = g_polygons[i];
    var pointsExport = [];

    for (j = 0; j < poly.getPath().getLength(); j++)
    {
      var pt = poly.getPath().getAt(j);

      var ptExport = new Object();
      ptExport.number = 1+j;
      ptExport.latitude = pt.lat();
      ptExport.longitude = pt.lng();
      pointsExport.push(ptExport);
    }

    var polyExport = new Object();
    polyExport.points = pointsExport;

    polygonsExport.push(polyExport);
  }

  $('#polygons').val( JSON.stringify(polygonsExport) );
}
