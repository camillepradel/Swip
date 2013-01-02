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
            dataType: "json",
            url: 'http://192.168.250.91/SwipWorkflow/resources/rest/nlToPivot',
            data: {nlQuery: nlQuery, kb:'musicbrainz', lang: lang, pos:'treeTagger', dep:'malt'},
        }).done(function(data2)
        {   
            $('#searchField2').val(data2['pivotQuery']);
            $('#searchField2').focus();
            toggleSearch(true);
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
        dataType: "jsonp",
        url: 'http://192.168.250.91/PivotToMappings/resources/rest/generateBestMappingsJSONP',
        data: {pivotQuery: pvQuery, numMappings: respNum, kb: 'musicbrainz', callback: '?'},
        crossDomain: true,
    }).done(function(data)
    {
        toggleSearch(true);

        if(!$.isEmptyObject(data))
        {
            $('#swiplogo').animate
            ({
                // 'margin-left': '-404px',
                fontSize: 236
            },
            {
              step: function(now, fx) { //now is the animated value from initial css value
                  $(this).css('clip', 'rect(0, '+(414+1.67*now)+'px, 76px, '+(236-now)+'px)');
                  $(this).css('margin-left', (-325-0.334745763*now)+'px');
              }
            }, 4000);
            $('#halo').animate
            ({
                'width': '808px',
                'margin-left': '-404px'                
            }, 'fast');
            $('#logo').animate
            ({
                'margin-top': '10px'
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
function processQuery(sparqlQuery, id)
{
    $.ajax
    ({
        type: 'GET',
        dataType: "json",
        url: 'http://swip.univ-tlse2.fr:8080/musicbrainz/sparql',
        data: {query: sparqlQuery, output:'json'},
    }).done(function(data)
    {
        sparqlQueryResult(data, id);
    }).fail(function(jqXHR, textStatus) {
        alert('Ajax error, please try again !');
    });
}
