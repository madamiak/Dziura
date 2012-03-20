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
        "sAjaxSource": "/arrays.txt"
    } );
} );