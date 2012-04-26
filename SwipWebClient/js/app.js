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

function fillTab(data)
{

	jQuery('#jqGrid').jqGrid
	({
		datatype: 'jsonstring',
		datastr: data,
		loadonce: true,
		height: 'auto',
		autowidth: true,
		shrinkToFit: true,
		jsonReader: 
		{
			root: 'content',
			repeatitems: false,
		},
	   	colNames: ['Desc','Rel'],
	   	colModel:
	   	[
   			{name: 'descriptiveSentence', index: 'descriptiveSentence', width: 80, classes: 'descriptiveSentence'},
   			{name: 'relevanceMark',index: 'relevanceMark', width: 20, sorttype: 'number', classes: 'relevanceMark'}
   		],
   		multiselect: false,
	   	subGrid: true,
	   	subGridRowExpanded: function(subgrid_id, row_id) 
	   	{
            $('#' + subgrid_id).append(data.content[row_id].sparqlQuery);
        },
        pager: '#pager',
        rowNum: 20
	});
}

function displayResults(result)
{
	$('#results').css('display', 'block');
	fillTab(result);
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
	/*console.log('#jqGrid_' + id);
	$('#results').tabs('add', '#jqGrid_' + id, 'Request');*/
	alert("Not implemented yet !");
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
			displayResults($.parseJSON($(req.responseXML).find('generateBestMappingsResponse').text()));
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