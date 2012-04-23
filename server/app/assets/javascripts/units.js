/**
 * units.js - obsługa wielokątów na mapie przy zarządzaniu jednostkami
 */

/* Część dotycząca wyświetlania wielokątów jednostki */

// Inicjalizacja - wywoływane po załadowaniu strony
function initializeUnitShow(unit_id)
{
  var extraOptions = { disableDoubleClickZoom: true };
  createMap(extraOptions); // map_common.js

  jQuery.getJSON('/units/' + unit_id + '.json',
    function(data)
    {
      for (var i = 0; i < data.polygons.length; i++)
      {
        var poly = data.polygons[i];
        var points = new google.maps.MVCArray();

        poly.points.sort(function(a, b){
          return a.number-b.number
        })

        for (var j = 0; j < poly.points.length; ++j)
        {
          var pt = poly.points[j];
          points.push(new google.maps.LatLng(pt.latitude, pt.longitude));
        }

        var polygon = new google.maps.Polygon
        ( {
          map: g_map,
          paths: points
        } );
      }
    }
  );
  
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
function initializeUnitEdit(unit_id)
{
  var extraOptions = { disableDoubleClickZoom: true };
  createMap(extraOptions); // map_common.js

  google.maps.event.addListener(g_map, 'click', mapClicked);

  jQuery.getJSON('/units/' + unit_id + '.json',
    function(data)
    {
      for (var i = 0; i < data.polygons.length; i++)
      {
        var poly = data.polygons[i];

        var points = new google.maps.MVCArray();

        poly.points.sort(function(a, b){
          return a.number-b.number
        })

        for (var j = 0; j < poly.points.length; j++)
        {
          var pt = poly.points[j];
          points.push(new google.maps.LatLng(pt.latitude, pt.longitude));
        }

        createEditablePolygon(points);
      }

      updatePolygonsJSON();
    }
  );
  
  $(window).resize(resizeUnits);
  resizeUnits();
}

// Po kliknięciu na mapę
function mapClicked(event)
{
  var pointNum = g_polygonMarkers.length + 1;

  var marker = new google.maps.Marker
    ( {
        map: g_map,
        position: event.latLng,
        draggable: true,
        title: "" + pointNum
      } );

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
  var polygonsField = document.getElementById("polygons");

  var polygonsExport = [];

  for (var i = 0; i < g_polygons.length; i++)
  {
    var poly = g_polygons[i];
    var pointsExport = [];
    var num = 0;
    for (j = 0; j < poly.getPath().getLength(); j++)
    {
      var pt = poly.getPath().getAt(j);

      var ptExport = new Object();
      ptExport.number = ++num;
      ptExport.latitude = pt.lat();
      ptExport.longitude = pt.lng();
      pointsExport.push(ptExport);
    }

    var polyExport = new Object();
    polyExport.points = pointsExport;

    polygonsExport.push(polyExport);
  }

  polygonsField.value = JSON.stringify(polygonsExport);
}
