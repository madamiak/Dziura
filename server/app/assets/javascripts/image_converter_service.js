$(document).ready(function() {
  $("#picture").bind("change", function() {
    console.log("upload submit");
    $("form").submit();
  });	
});
