$(function()
{
	/** 
	 * Submits the form when the return key
	 * is pressed in the search field
	**/
	$('#searchField').keydown(function(event)
	{
    	if(event.keyCode == 13)
    	{
	        $('#searchButton').click();
	    }
	});

	$('#searchButton').click(function()
	{
		search($('#searchField').val());
	});

	$('.jqgrow').live('dblclick', function()
	{
		selectQuery($(this).attr('id'));
	});

	$('#results').tabs();
});

function fillTab()
{
	jQuery('#jqGrid').jqGrid
	({
		datatype: 'jsonstring',
		datastr: parsedJson,
		loadonce: true,
		height: 'auto',
		autowidth: true,
		shrinkToFit: true,
		jsonReader: 
		{
			root: 'content',
			repeatitems: false,
		},
	   	colNames: ['Id', 'Desc','Rel'],
	   	colModel:
	   	[
   			{name: 'id', index: 'id', width: 3, classes: 'id', sortable: false},
   			{name: 'descriptiveSentence', index: 'descriptiveSentence', width: 82, classes: 'descriptiveSentence'},
   			{name: 'relevanceMark',index: 'relevanceMark', width: 15, sorttype: 'number', classes: 'relevanceMark'}
   		],
   		multiselect: false,
	   	subGrid: true,
	   	subGridRowExpanded: function(subgrid_id, row_id) 
	   	{
	   		var queryPre = '<span class="title">SPARQL Query :</span><pre class="queryPre"></pre>';
	   		var mappingPre = '<span class="title">Mapping Description :</span><br /><br /><span class="mappingSpan"></span>';
            $('#' + subgrid_id).addClass('subgrid');
            $('#' + subgrid_id).append(queryPre + '<hr />' + mappingPre);
            $('#' + subgrid_id + ' .queryPre').text(parsedJson.content[row_id].sparqlQuery);
            $('#' + subgrid_id + ' .mappingSpan').text(parsedJson.content[row_id].mappingDescription);
        },
        gridComplete: function()
        {
			var ids = jQuery("#jqGrid").jqGrid('getDataIDs');
			for(var i = 0; i < ids.length; i++)
			{
				jQuery("#jqGrid").jqGrid('setRowData', ids[i], {id: ids[i]});
			}
		},
        pager: '#pager',
        rowNum: 20
	});
}

function displayResults()
{
	$('#results').css('display', 'block');
	fillTab();
}


function search(query)
{
	if(query != '')
	{
		$('#searchButton').attr('disabled', 'disabled');
		$('#loading').css('display', 'block');
		nlToPivot(query);
	}
}

function selectQuery(id)
{
	var sparqlQuery = '<span class="title">SPARQL Query :</span><pre class="queryPre"></pre>';
	var sparqlResult = '<span class="title">SPARQL Result :</span><pre class="resultPre">Not implemented yet !</pre>'
	$('#results').append('<div id="query' + id + '" class="query">' + sparqlQuery + '<hr />' + sparqlResult + '</div>');
	$('#query' + id + ' .queryPre').text(parsedJson.content[id].sparqlQuery);
	$('#query' + id).css('height', $('#tabs-1').css('height'));
	$('#results').tabs('add', '#query' + id, 'Query #' + id);

}

function nlToPivotSuccHandler(data, status, req) 
{
    if (status == 'success')
        pivotToSparql($(req.responseXML).find('translateQueryResponse').text(), 50);
}

function pivotToSparqlSuccHandler(data, status, req) 
{
    if (status == 'success')
    {
    	$('#loading').css('display', 'none');

        $('#logo').animate
		({
			'margin-top': 0
		}, 'slow', function()
		{
			$('#searchButton').removeAttr('disabled');
			parsedJson = $.parseJSON($(req.responseXML).find('generateBestMappingsResponse').text());
			displayResults();
		});
	}
}

function nlToPivotErrHandler()
{
	alert('SOAP Error (NlToPivot)');
}

function pivotToSparqlErrHandler()
{
	alert('SOAP Error (PivotToSparql)');
}