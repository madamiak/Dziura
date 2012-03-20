function bindEditIssueForm() {
  $("#issue_edit").live("ajax:success", function(event, data, status, xhr) {
		$("#issue_edit").html(data.responseText);
	});
	
	 $("#issue_edit").live("ajax:error", function(event, data, status, xhr) {
		$("#issue_edit").html(data.responseText);
	});
}

$(document).ready(function() {
    $('#example').dataTable( {
        "bProcessing": true,
	"bRetrieve": true,
        "sAjaxSource": "/issues/by_pages.json",
	"sAjaxDataProp": "",
	"aoColumns": [
		{ "mDataProp": "id" },	     
		{ "mDataProp": "category_id" },      
		{ "mDataProp": "status_id" },
		{ "mDataProp": "unit_id" },
		{ "mDataProp": "created_at" },
		{ "mDataProp": "updated_at" } 
        ]
    } );
} );