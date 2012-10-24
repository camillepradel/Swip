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
        dataType: "text",
        url: 'http://192.168.250.91/NlToPivotGazetteer/resources/rest/gatherNamedEntities',
        data: {text: nlQuery, tagWithClass:false},
    }).done(function(data)
    {
        $.ajax
        ({
            type: 'GET',
            dataType: "json",
            url: 'http://192.168.250.91/NlToPivotParser/resources/rest/nlToDependenciesAndPivot',
            data: {nlQuery: data, 'lang': lang, pos:'treeTagger', dep:'malt'},
        }).done(function(data2)
        {   
            $('#searchField2').val(data2['pivotQuery']);
            $('#searchField2').focus();
            toggleSearch(true);
        }).fail(function(jqXHR, textStatus) {
            alert('Ajax error, please try again !');
            toggleSearch(true);
        });

    }).fail(function(jqXHR, textStatus) {
        alert('Ajax error, please try again !');
        toggleSearch(true);
    });
}

/**
 * Gets best mappings for a pivot query
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
        url: 'http://localhost:8080/PivotToMappings/PivotToMappingsWS/rest/generateBestMappings?pivotQuery=' + encodeURIComponent(pvQuery) + '&numMappings=' + encodeURIComponent(respNum) + '&kbName=cinemaDist'
    }).done(function(data)
    {
        toggleSearch(true);

        if(!$.isEmptyObject(data))
        {
            $('#logo').animate
            ({
                'margin-top': 0
            }, 'slow', function()
            {
                displayResults(data);
            });
        }
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
function processQuery(query, id)
{
    $.ajax
    ({
        type: 'GET',
        url: 'http://localhost:8080/PivotToMappings/PivotToMappingsWS/rest/processQuery?query=' + encodeURIComponent(query) + '&kbName=cinemaDist'
    }).done(function(data)
    {
        sparqlQueryResult(data, id);
    }).fail(function(jqXHR, textStatus) {
        alert('Ajax error, please try again !');
    });
}
