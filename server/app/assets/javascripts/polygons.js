/* Obsługa wielokątów na mapie przy zarządzaniu jednostkami */

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

// --------------------

/* Część dotycząca wyświetlania wielokątów jednostki */

function initializeShow(unit_id)
{
  createMap();

  jQuery.getJSON("/units/" + unit_id + ".json", showUnitDataReceived);
}

function showUnitDataReceived(data)
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
      map: map,
      paths: points
    } );
  }
}

// --------------------

/* Część dotycząca tworzenia i edycji wielokątów jednostki */

var polygonMarkers = [];
var polygons = [];
var closePolygonListener;


function initializeNew()
{
  createMap();

  google.maps.event.addListener(map, 'click', mapClicked);
}

function initializeEdit(unit_id)
{
  createMap();

  jQuery.getJSON("/units/" + unit_id + ".json", editUnitDataReceived);

  google.maps.event.addListener(map, 'click', mapClicked);
}

function editUnitDataReceived(data)
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

function mapClicked(event)
{
  var pointNum = polygonMarkers.length + 1;

  var marker = new google.maps.Marker
    ( {
        map: map,
        position: event.latLng,
        draggable: true,
        title: "" + pointNum
      } );

  polygonMarkers.push(marker);

  if (pointNum == 1)
  {
    closePolygonListener = google.maps.event.addListener(marker, 'click', closePolygon);
  }
}

function closePolygon(event)
{
  if (polygonMarkers.length < 3)
  {
    alert("Wielokąt musi mieć co najmniej 3 punkty!");
    return;
  }

  var points = new google.maps.MVCArray();

  for (var i = 0; i < polygonMarkers.length; i++)
  {
    var mk = polygonMarkers[i];
    points.push(mk.getPosition());
    mk.setMap(null);
  }

  createEditablePolygon(points);

  google.maps.event.removeListener(closePolygonListener);
  polygonMarkers = [];

  updatePolygonsJSON();
}

function createEditablePolygon(points)
{
  google.maps.event.addListener(points, 'insert_at', function(a) { updatePolygonsJSON() });
  google.maps.event.addListener(points, 'remove_at', function(a,b) { updatePolygonsJSON() });
  google.maps.event.addListener(points, 'set_at', function(a,b) { updatePolygonsJSON() });

  var polygon = new google.maps.Polygon
    ( {
        map: map,
        paths: points,
        editable: true
      } );

  polygons.push(polygon);

  addPolygonDeleteListener(polygon);
}

function addPolygonDeleteListener(polygon)
{
  google.maps.event.addListener(polygon, 'dblclick', function() {
    for (var i = 0; i < polygons.length; i++)
    {
      if (polygons[i] == polygon)
        polygons.splice(i, 1);
    }

    polygon.setMap(null);

    updatePolygonsJSON();
  });
}

function updatePolygonsJSON()
{
  var polygonsField = document.getElementById("polygons");

  var polygonsExport = [];

  for (var i = 0; i < polygons.length; i++)
  {
    var poly = polygons[i];
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
