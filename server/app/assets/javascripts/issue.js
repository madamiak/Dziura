function bindEditIssueForm() {
  $("#issue_edit").live("ajax:success", function(event, data, status, xhr) {
		$("#issue_edit").html(data.responseText);
	});
	
	 $("#issue_edit").live("ajax:error", function(event, data, status, xhr) {
		$("#issue_edit").html(data.responseText);
	});
}

$(document).ready(function() {
	
	var m = new mapTable;
	m.aa();
	
	$('#example').each(function() {
		$('#example').dataTable({
			"bProcessing" : true,
			"bRetrieve" : true,
                        "bFilter": false,
                        "bJQueryUI": true,
			//"sAjaxSource" : "/issues/by_pages.json",
			"sAjaxDataProp" : "",
			"aoColumns" : [ {
				"mDataProp" : "id"
			}, {
				"mDataProp" : "category.name"
			}, {
				"mDataProp" : "status.name"
			}, {
				"mDataProp" : "unit.name"
			}, {
				"mDataProp" : "created_at"
			}
                        , {
				"mDataProp" : "updated_at"
			} 
                    ]
		});
	});
});
function mapTable() {
	this.aa = function(){
		initialize();
		  $('#example tbody tr ').live( 'click', function () {
		        
		        var id = $(this).html();
		        id = id.substr(id.indexOf(">")+1);
		        id = id.substr(0,id.indexOf("<"));
		        
		    } );
	}
	
	this.setHihglightRow = function (issue_id){
		$('#example tbody tr ').each(function() {
			var id = $(this).html();
	        id = id.substr(id.indexOf(">")+1);
	        id = id.substr(0,id.indexOf("<"));
	        if (id==issue_id) {
	        	$(this).toggleClass('ui-state-hover');
	        }
		});
	}
}