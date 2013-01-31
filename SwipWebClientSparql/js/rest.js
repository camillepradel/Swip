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
            dataType: "jsonp",
            // url: 'http://192.168.250.91/SwipWorkflow/resources/rest/nlToPivot',
            // url: 'http://swip.univ-tlse2.fr/SwipWorkflow/resources/rest/nlToPivot',
            url: 'http://localhost:8080/SwipWorkflow/resources/rest/nlToPivotJSONP',
            data: {nlQuery: nlQuery, kb:'musicbrainz', lang: lang, pos:'treeTagger', dep:'malt', callback: '?'},
            crossDomain: true,
            beforeSend : function (xhr) {
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
                }, 'slow');
            }
        }).done(function(data2)
        {
            $('#result').empty();
            $('#result').append('<div class="nltopivot"></div>');
            $('#result .nltopivot').append('<p>NL query: ' + nlQuery + '</p>');
            $('#result .nltopivot').append('<p>Gazetteed query: ' + (data2['gazetteedQuery']) + '</p>');
            $('#result .nltopivot').append('<p>Pivot query: ' + (data2['pivotQuery']) + '</p>');
            pivotToSparql(data2['pivotQuery'])
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
function pivotToSparql(pvQuery)
{
    var shortSparqlEndpointUri = 'swip.univ-tlse2.fr:8080/musicbrainz';
    var sparqlEndpointUri = 'http://' + shortSparqlEndpointUri + "/";
    $.ajax
    ({
        type: 'GET',
        dataType: "jsonp",
        // url: 'http://192.168.250.91/PivotToMappings/resources/rest/generateBestMappingsJSONP',
        // url: 'http://swip.univ-tlse2.fr/PivotToMappings/resources/rest/generateBestMappingsJSONP',
        url: 'http://localhost:8080/PivotToMappingsSparql/resources/rest/generateBestMappingsJSONP',
        data: {pivotQuery: pvQuery, sparqlEndpointUri: shortSparqlEndpointUri, numMappings: 5, kb: 'musicbrainz', callback: '?'},
        crossDomain: true,
            beforeSend : function (xhr) {
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
                }, 'slow');
            }
    }).done(function(data)
    {
        var queryUri = data['queryUri'];
        // var queryUri = 'http://swip.univ-tlse2.fr:8080/musicbrainz/arranger-c-The_Heroes_of_Telemark-p-';//data['queryUri'];
        toggleSearch(true);
        $('#result').append('<div class="pivottomappings"></div>');
        $('#result .pivottomappings').append('<p>Query URI: ' + (queryUri) + '</p>');
        $('#result .pivottomappings').append('<div class="matching"></div>');
        $('#result .pivottomappings').append('<div class="elementmapping"></div>');
        $('#result .pivottomappings').append('<div class="triplemappings"></div>');
        $('#result .pivottomappings').append('<div class="spcollectionmappings"></div>');
        $('#result .pivottomappings').append('<div class="patternmappings"></div>');
        $('#result .pivottomappings').append('<div class="mappingsranking"></div>');
        $('#result .pivottomappings').append('<div class="kbclearing"></div>');
        $('#result .pivottomappings').append('<div class="queryprocessed"></div>');
        updateQueryState(queryUri, sparqlEndpointUri, function() {clearInterval(refreshIntervalId);});
        var refreshIntervalId = setInterval(function() { updateQueryState(queryUri, sparqlEndpointUri, function() {clearInterval(refreshIntervalId);}); }, 1000);
        
    }).fail(function(jqXHR, textStatus) {
        toggleSearch(true);
        alert('Ajax error, please try again !');
    });
}


/**
 * Processes a SPARQL query
 **/
function processQuery(sparqlQuery, sparqlEndpointUri, callback)
{
    $.ajax
    ({
        type: 'GET',
        dataType: "json",
        url: sparqlEndpointUri + 'sparql',
        data: {query: sparqlQuery, output:'json'},
    })
    .done(function(data) {
        callback(data);
    })
    .fail(function(jqXHR, textStatus) {
        alert('Ajax error, please try again !');
    });
}
