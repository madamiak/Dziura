var g_existing;
var g_marker;
var g_infowindow;

var idOfSelectableElement = "selectable";
var newIssueUrl = "/res/issue/";
    
$(function() {

    var category_id = $(" #category_id"), 
    notificar_email = $("#notificar_email"),
    desc = $("#desc"),
    allFields = $([]).add(notificar_email).add(desc),
    tips = $("#validateTips");
    
	
//    $( "#dialog").dialog({ 
//        width: 550, 
//        maxWidth: 550, 
//        minWidth: 550,
//        autoOpen: false,
//        modal: true,
//        open: function(event, ui) {
//            bindIssueForm();
//        },
//        buttons: {
//            'Zglos': function() {
//                $(this).dialog('close');
//            },
//            'Zamknij': function() {
//                $(this).dialog('close');
//            }
//        }
//    });
			

	
	
    function updateTips(t) {
        tips.text(t).effect("highlight",{},1500);
    }

    function checkLength(o,n,min,max) {

        if ( o.val().length > max || o.val().length < min ) {
            o.addClass('ui-state-error');
            updateTips("Length of " + n + " must be between "+min+" and "+max+".");
            return false;
        } else {
            return true;
        }

    }

    function checkRegexp(o,regexp,n) {

        if ( !( regexp.test( o.val() ) ) ) {
            o.addClass('ui-state-error');
            updateTips(n);
            return false;
        } else {
            return true;
        }

    }
});

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
    $("#issue_form form ").bind("submit", function() {
        $("#issue_form input[name=longitude]").val(g_marker.getPosition().lng());
        $("#issue_form input[name=latitude]").val(g_marker.getPosition().lat());
        $("#issue_form input[name=photo]").val(getPhotoInBase64 ());
        $("#issue_form input[name=category_id]").val(index + 1);
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
		
        makeDialog();
		
        //pobieranie formularza zgloszenia szkody z serwera
        //$.get('res/issue', function(data) {
        //console.log(data);
        //g_infowindow = new google.maps.InfoWindow({
        //content: data
        //});

        //g_infowindow.open(g_map, g_marker);

        //google.maps.event.addListener(g_infowindow, 'domready', function() {
        //bindIssueForm();
        //});
        //});
    
        google.maps.event.addListener(g_marker, 'click', function() {
            makeDialog();
        });

        setExisting(true);
    } 
    else {
        g_marker.setPosition(location);
    }
}

function makeDialog(){
    var dialog = initDialogWindow(newIssueUrl);
}

function makeSelectable(){
    bindIssueForm();
    $('#selectable').selectable({
        stop: function() {
            result = $( "#select-result" ).empty();
            $( ".ui-selected", this ).each(function() {
                index = $( "#selectable li" ).index( this );
                category_id = index + 1;
                result.append( category_id );
            });
        }
    });
}