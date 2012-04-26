$(function()
{
	$('#result').html('Lalouliloula');
	search();
});

function search()
{
	var wsUrl = "http://swipserver:8080/NlToPivot/NlToPivotWS?op=translateQuery";

    var soapRequest =
	'<?xml version="1.0" encoding="utf-8"?> \
	<soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" \
	xmlns:xsd="http://www.w3.org/2001/XMLSchema" \
	xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"> \
	<soap:Body> \
	<Hello xmlns="http://swipserver:8080/NlToPivot/NlToPivotWS?op=translateQuery"> \
	  <nlQuery>List all members of The Notwist</nlQuery> \
	</Hello> \
	</soap:Body> \
	</soap:Envelope>';

    $.ajax({
        type: "POST",
        url: wsUrl,
        contentType: "text/xml",
        dataType: "xml",
        data: soapRequest,
        success: processSuccess,
        error: processError
    });
    	
};

function processSuccess(data, status, req) {
    if (status == "success")
        $("#result").text($(req.responseXML).find("HelloResult").text());
}

function processError(data, status, req) {
    alert(req.responseText + " " + status);
}





/*<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns:xsd="http://www.w3.org/2001/XMLSchema"
xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <Hello xmlns="http://tempuri.org/">
      <name>string</name>
    </Hello>
  </soap:Body>
</soap:Envelope>*/