/**
 * image_converter_service.js - ładowanie zdjęć za pomocą kontrolera image_converter_service
 */

$(document).ready(function()
{
  $("#picture").bind("change", function() {
    $("form").submit();
  });
});
