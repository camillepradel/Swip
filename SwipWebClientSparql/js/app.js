
$(function()
{
	$('#searchField2').focus();

	/** 
	 * Submits the form when the return key
	 * is pressed in the search field
	 **/
	$('#searchField1').keydown(function(event)
	{
    	if(event.keyCode == 13)
    	{
	        $('#searchButton1').click();
	    }
	});
	$('#searchField2').keydown(function(event)
	{
    	if(event.keyCode == 13)
    	{
	        $('#searchButton2').click();
	    }
	});

	/** 
	 * Submits the form when the buttons
	 * are clicked in the search form
	 **/
	$('#searchButton1').click(function()
	{
		if($('#searchField1').val() != '')
		{
			nlToPivot($('#searchField1').val());
		}
	});
	$('#searchButton2').click(function()
	{
		if($('#searchField2').val() != '')
		{
			pivotToSparql($('#searchField2').val());
		}
	});
});




/**
 * Translates a query from natural language
 * to pivot
 * Provides two handlers (nlToPivotSuccHandler & 
 * nlToPivotErrHandler)
 * @param nlQuery Query in natural language
 **/
function nlToPivot(nlQuery)
{
    var wfwebservice = $('#configurations .wfwebservice').val();
    var gazwebservice = $('#configurations .gazwebservice').val();
    var dpwebservice = $('#configurations .dpwebservice').val();
    var ruleswebservice = $('#configurations .ruleswebservice').val();
    var kbname = $('#configurations .kbname').val();
    var lang = $('#configurations .lang').val();
    var pos = $('#configurations .pos').val();
    var dep = $('#configurations .dep').val();
    $.ajax
    ({
        type: 'GET',
        dataType: "jsonp",
        url: wfwebservice,
        data: {nlQuery: nlQuery, kb:kbname, lang: lang, pos:pos, dep:dep, gazwebservice:gazwebservice, dpwebservice:dpwebservice, ruleswebservice:ruleswebservice, callback: '?'},
        crossDomain: true,
        beforeSend : function (xhr) {
            animateLogo();
            $('.searchButton').attr('disabled', 'disabled');
            $('#result').empty();
            $('#result').append('<img src="img/loading_icon.gif" alt="Loading" />');
        }
    }).done(function(data2)
    {
        $('#result').empty();
        $('#result').append('<div id="nltopivot"></div><div id="pivottomappings"></div>');
        $('#nltopivot').append('<h1>NL to pivot query translation</h1>');
        $('#nltopivot').append('<p><b>Initial NL query:</b> <i>' + nlQuery + '</i></p>');
        $('#nltopivot').append('<p><b>Gazetteed query:</b> <i>' + (data2['gazetteedQuery']) + '</i></p>');
        $('#nltopivot').append('<p><b>Pivot query:</b> <i>' + (data2['pivotQuery']) + '</i></p>');
        $('#searchField2').val(data2['pivotQuery']);
        $('.searchButton').removeAttr('disabled');
    }).fail(function(jqXHR, textStatus) {
        alert('Ajax error, please try again !');
        $('.searchButton').removeAttr('disabled');
        $('#nltopivot img').css('display', 'none');
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
    var sparqlenpoint = $('#configurations .sparqlenpoint').val();
    var wsUrl = $('#configurations .ptswebservice').val();
    var sparqlEnpointServer = $('#configurations .sparqlenpointserver').val();
    var useFederatedSparql = $('#configurations .usefederatedsparql').attr('checked')? true : false;
    var useLarq = $('#configurations .uselarq').attr('checked')? true : false;
    var larqParams = $('#configurations .larqparams').val();
    var kbLocation = $('#configurations .kblocation').val();
    var queriesUri = $('#configurations .queriesuri').val();
    var patternsUri = $('#configurations .patternsuri').val();
    var nbmappings = $('#configurations .nbmappings').val();
    $.ajax
    ({
        type: 'GET',
        dataType: "jsonp",
        url: wsUrl,
        data: {pivotQuery: pvQuery, 
            sparqlEndpointUri: sparqlEnpointServer, 
            useFederatedSparql: useFederatedSparql, 
            useLarq: useLarq,  
            larqParams: larqParams, 
            kbLocation: kbLocation,
            queriesNamedGraphUri: queriesUri, 
            patternsNamedGraphUri: patternsUri, 
            numMappings: nbmappings, 
            callback: '?'},
        crossDomain: true,
        beforeSend : function (xhr) {
                beforePivotToSparql();
            }
    }).done(function(data) {
        donePivotToSparql(data, sparqlenpoint, queriesUri, patternsUri);        
    }).fail(function(jqXHR, textStatus) {
        failPivotToSparql();
    });
}

function animateLogo() {
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
    }, 'slow', function(){});
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
