/*====================================
 * soap.js
 *
 * Interface for querying the WS
 * using the SOAP protocol
 ====================================*/


/**
 * Translates a query from natural language
 * to pivot
 * Provides two handlers (nlToPivotSuccHandler & 
 * nlToPivotErrHandler)
 * @param nlQuery Query in natural language
 **/
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


/**
 * Gets best mappigns for a pivot query
 * Provides two handlers (pivotToSparqlSuccHandler & 
 * pivotToSparqlErrHandler)
 * @param pvQuery Query in pivot
 * @param respNum Size of the returned array
 **/
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


/**
 * Processes a SPARQL query
 * Provides two handlers (processQuerySuccHandler & 
 * processQueryErrHandler)
 * @param query SPARQL query to process
 * @param id Query's ID (concerns the view)
 **/
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