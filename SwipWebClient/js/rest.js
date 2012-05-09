/*====================================
 * rest.js
 *
 * Interface for querying the WS
 * using the REST protocol
 ====================================*/


/**
 * Translates a query from natural language
 * to pivot
 * Provides two handlers (nlToPivotSuccHandler & 
 * nlToPivotErrHandler)
 * @param nlQuery Query in natural language
 **/
function nlToPivot(nlQuery, lang)
{
    $.ajax
    ({
        type: 'GET',
        url: 'http://swipserver:8080/NlToPivot/NlToPivotWS/rest/translateQuery?nlQuery=' + encodeURIComponent(nlQuery) + '&lang=' + encodeURIComponent(lang)
    }).done(function(data)
    {
        pivotToSparql(data, 50);
    }).fail(function(jqXHR, textStatus) {
        toggleSearch(true);
        alert('Ajax error, please try again !');
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
    $.ajax
    ({
        type: 'GET',
        url: 'http://swipserver:8080/PivotToMappings/PivotToMappingsWS/rest/generateBestMappings?pivotQueryString=' + encodeURIComponent(pvQuery) + '&numMappings=' + encodeURIComponent(respNum) + '&kbName=music'
    }).done(function(data)
    {
        toggleSearch(true);

        $('#logo').animate
        ({
            'margin-top': 0
        }, 'slow', function()
        {
            displayResults(data);
        });
    }).fail(function(jqXHR, textStatus) {
        toggleSearch(true);
        alert('Ajax error, please try again !');
    });
}


/**
 * Processes a SPARQL query
 * Provides two handlers (processQuerySuccHandler & 
 * processQueryErrHandler)
 * @param query SPARQL query to process
 * @param id Query's ID (concerns the view)
 **/
/*function processQuery(query, id)
{
    TODO
}*/