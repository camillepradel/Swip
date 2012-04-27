function nlToPivot(nlQuery)
{
	var wsUrl = 'http://swipserver:8080/NlToPivot/NlToPivotWS';

    var soapRequest = '<?xml version="1.0" ?><S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/"><S:Body><ns2:translateQuery xmlns:ns2="http://nlToPivot.swip.org/"><nlQuery><![CDATA[' + nlQuery + ']]></nlQuery></ns2:translateQuery></S:Body></S:Envelope>';

    $.ajax
    ({
        type: 'POST',
        url: wsUrl,
        contentType: 'text/xml',
        dataType: 'xml',
        data: soapRequest,
        success: nlToPivotSuccHandler,
        error: nlToPivotErrHandler
    });
}

function pivotToSparql(pvQuery, respNum)
{
	var wsUrl = 'http://swipserver:8080/PivotToMappings/PivotToMappingsWS';

    var soapRequest = '<?xml version="1.0" ?><S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/"><S:Body><ns2:generateBestMappings xmlns:ns2="http://pivotToMappings.swip.org/"><pivotQueryString><![CDATA[' + pvQuery + ']]></pivotQueryString><numMappings><![CDATA[' + respNum + ']]></numMappings></ns2:generateBestMappings></S:Body></S:Envelope>';

    $.ajax
    ({
        type: 'POST',
        url: wsUrl,
        contentType: 'text/xml',
        dataType: 'xml',
        data: soapRequest,
        success: pivotToSparqlSuccHandler,
        error: pivotToSparqlErrHandler
    });
}

function processQuery(query, id)
{
    var wsUrl = 'http://swipserver:8080/PivotToMappings/PivotToMappingsWS';

    var soapRequest = '<?xml version="1.0" ?><S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/"><S:Body><ns2:processQuery xmlns:ns2="http://pivotToMappings.swip.org/"><pivotQuery><![CDATA[' + query + ']]></pivotQuery></ns2:processQuery></S:Body></S:Envelope>';

    $.ajax
    ({
        type: 'POST',
        url: wsUrl,
        contentType: 'text/xml',
        dataType: 'xml',
        data: soapRequest,
        success: function(data, status, req)
        {
            processQuerySuccHandler(data, status, req, id);
        },
        error: processQueryErrHandler
    });
}