var map;

function initializeShow(unit_id)
{
  var myOptions =
  {
    center: new google.maps.LatLng(51.1101, 17.0324),
    zoom: 13,
    mapTypeId: google.maps.MapTypeId.ROADMAP
  };

  map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);

  jQuery.getJSON("/units/" + unit_id + ".json", unitDataReceived);
}

function unitDataReceived(data)
{
  for each (var poly in data.polygons)
  {
    var points = new google.maps.MVCArray();

    poly.points.sort(function(a, b){
      return a.number-b.number
    })
    for each (var pt in poly.points)
    {
      points.push(new google.maps.LatLng(pt.latitude, pt.longitude));
    }

    var polygon = new google.maps.Polygon
    ( {
      map: map,
      paths: points
    } );
  }
}

function initializeNew()
{
  var myOptions =
  {
    center: new google.maps.LatLng(51.1101, 17.0324),
    zoom: 13,
    mapTypeId: google.maps.MapTypeId.ROADMAP
  };

  map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);

  google.maps.event.addListener(map, 'click', mapClicked);
}

var polygonPoints = [];
var closePolygonListener;

function mapClicked(event)
{
  var pointNum = polygonPoints.length + 1;

  var marker = new google.maps.Marker
    ( {
        map: map,
        position: event.latLng
      } );

  polygonPoints.push(event.latLng);

  if (pointNum == 1)
  {
    closePolygonListener = google.maps.event.addListener(marker, 'click', closePolygon);
  }
}

function closePolygon(event)
{
  if (polygonPoints.length < 3)
  {
    alert("Wielokąt musi mieć co najmniej 3 punkty!");
    return;
  }

  var points = new google.maps.MVCArray(polygonPoints);

  var polygon = new google.maps.Polygon
    ( {
        map: map,
        paths: points
      } );


  var pointsExport = [];
  var num = 0;
  for each (var pt in polygonPoints)
  {
    var ptExport = new Object();
    ptExport.number = ++num;
    ptExport.latitude = pt.lat();
    ptExport.longitude = pt.lng();
    pointsExport.push(ptExport);
  }

  var polyExport = new Object();
  polyExport.points = pointsExport;

  var polygonsField = document.getElementById("polygons");

  polygonsExport = JSON.parse(polygonsField.value);
  polygonsExport.polygons.push(polyExport);

  polygonsField.value = JSON.stringify(polygonsExport);


  google.maps.event.removeListener(closePolygonListener);
  polygonPoints = [];
}
