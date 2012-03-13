function ajaxFunction()
{
	var xmlhttp;
	if (window.XMLHttpRequest)
	{
		// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp = new XMLHttpRequest();
	}
	else if (window.ActiveXObject)
	{
		// code for IE6, IE5
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	else
	{
		alert("Your browser does not support XMLHTTP!");
	}
	xmlhttp.onreadystatechange = function()
	{
		if(xmlhttp.readyState == 4)
		{

		}
	}
}
