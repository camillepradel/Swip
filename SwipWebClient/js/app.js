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

	var previousTab = 'tabs-1';
	$('#results').tabs
	({
		tabTemplate: "<li><a href='#{href}'>#{label}</a><span class='ui-icon ui-icon-close'>Remove Tab</span></li>",
		add: function(event, ui) 
		{
	        $('#results').tabs('select', '#' + ui.panel.id);
	    },
	    select: function(event, ui)
	    {
	    	if(ui.panel.id != 'tabs-1')
	    	{
	    		$('#' + ui.panel.id).css('height', $('#' + previousTab).css('height'));
	    	}
	    	previousTab = ui.panel.id;
	    }
	});

	$('#results span.ui-icon-close').live('click', function() 
	{
		var index = $('li', $('#results')).index($(this).parent());
		$('#results').tabs('remove', index);
	});
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
            $('#' + subgrid_id).append(queryPre + '<br /><hr /><br />' + mappingPre);
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
		$('#searchLoading').css('display', 'block');
		nlToPivot(query);
	}
}

function selectQuery(id)
{
	var descSentence = '<span class="title">Search :</span><br /><br /><span class="searchSpan"></span>'; 
	var sparqlQuery = '<span class="title">SPARQL Query :</span><pre class="queryPre"></pre>';
	var sparqlResult = '<span class="title">SPARQL Result :</span><div class="loading" style="display: block"><img src="img/loading_icon.gif" alt="Loading" /></div>'
	$('#results').append('<div id="query' + id + '" class="query">' + descSentence + '<br /><hr /><br />' + sparqlQuery + '<br /><hr /><br />' + sparqlResult + '</div>');
	$('#query' + id + ' .queryPre').text(parsedJson.content[id].sparqlQuery);
	$('#query' + id + ' .searchSpan').html(parsedJson.content[id].descriptiveSentence);
	$('#results').tabs('add', '#query' + id, 'Query #' + id);
	processQuery(parsedJson.content[id].sparqlQuery, id);

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
    	$('#searchLoading').css('display', 'none');

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

function processQuerySuccHandler(data, status, req, id)
{
	if (status == 'success')
	{
        console.log($(req.responseXML).find('processQueryResponse').text());
    	console.log(id);
    }
}

function nlToPivotErrHandler()
{
	$('#searchButton').removeAttr('disabled');
	console.log('SOAP Error (NlToPivot)');
}

function pivotToSparqlErrHandler()
{
	$('#searchButton').removeAttr('disabled');
	console.log('SOAP Error (PivotToSparql)');
}

function processQueryErrHandler()
{
	console.log('SOAP Error (SparlQuery)');
}