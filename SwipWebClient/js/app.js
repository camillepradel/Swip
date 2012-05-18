// Imports
// $.getScript('js/rest.js');
// $.getScript('js/descList.js');

$(function()
{
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
		translateQuery($('#searchField1').val());
	});
	$('#searchButton2').click(function()
	{
		search($('#searchField2').val());
	});

	/**
	 * Tabs management
	 **/
	var previousTab = 'tabs-1';
	$('#results').tabs
	({
		tabTemplate: "<li><a href='#{href}'>#{label}</a><span class='ui-icon ui-icon-close'>Remove Tab</span></li>",
		add: function(event, ui) 
		{
	        $('#results').tabs('select', '#' + ui.panel.id);
	    }
	});
	$('#results span.ui-icon-close').live('click', function() 
	{
		var index = $('li', $('#results')).index($(this).parent());
		$('#results').tabs('remove', index);
	});
});


/**
 * Fills the results tab with data extracted from
 * a JSON string
 **/
function fillTab(results)
{
	// For further information, see the JqGrid documentation
	jQuery('#jqGrid').jqGrid
	({
		datatype: 'jsonstring',
		datastr: results,
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
   			{name: 'descriptiveSentence.string', index: 'descriptiveSentence', width: 82, classes: 'descriptiveSentence'},
   			{name: 'relevanceMark',index: 'relevanceMark', width: 15, sorttype: 'number', classes: 'relevanceMark'}
   		],
   		multiselect: false,
	   	subGrid: true,
	   	subGridRowExpanded: function(subgrid_id, row_id) 
	   	{
	   		var queryPre = '<span class="title">SPARQL Query :</span><pre class="queryPre"></pre>';
	   		var mappingPre = '<span class="title">Mapping Description :</span><br /><br /><pre class="mappingPre"></pre>';
            $('#' + subgrid_id).addClass('subgrid');
            $('#' + subgrid_id).append(queryPre + '<br /><hr /><br />' + mappingPre);
            $('#' + subgrid_id + ' .queryPre').text(results.content[row_id - 1].sparqlQuery);
            $('#' + subgrid_id + ' .mappingPre').text(results.content[row_id - 1].mappingDescription);
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

	/**
	 * Handles the double click on a query
	 **/
	$('.jqgrow').die();
	$('.jqgrow').live('dblclick', function()
	{
		selectQuery($(this).attr('id'), results);
	});
}


/**
 * Called when results have to be displayed
 **/
function displayResults(results)
{

	var sentences = new DescList();
	for(var i = 0; i < results.content.length; i++)
		sentences.add(results.content[i].descriptiveSentence);
	
	var duplicates = sentences.checkCover();
	
	for(var i = 0; i < results.content.length; i++)
	{
		if($.inArray(i, duplicates) < 0)
			results.content[i].descriptiveSentence.string = sentences.getGeneralizedSentence(i);
	}

	var i = results.content.length - 1;
	while(i >= 0)
	{
		if($.inArray(i, duplicates) >= 0)
			results.content.splice(i, 1);
		i--;
	}

	// Displaying
	$('#results').css('display', 'block');
	fillTab(results);
}


/**
 * Called when the user has validated their input
 **/
function translateQuery(query)
{
	if(query != '')
	{
		toggleSearch(false);
		nlToPivot(query, 'fr');
	}
}

function search(query)
{
	if(query != '')
	{
		$('#results').css('display', 'none');
		$('#jqGrid').GridUnload();
		toggleSearch(false);
		pivotToSparql(query, 50);
	}
}


/**
 * Called when a query is selected
 **/
function selectQuery(id, results)
{
	if($('#query' + id).length)
	{
		$('#query' + id).html('');
		$('#results').tabs('select', '#query' + id);
	}
	else
	{
		$('#results').append('<div id="query' + id + '" class="query">');
		$('#results').tabs('add', '#query' + id, 'Query #' + id);
	}

	var descSentence = '<span class="title">Search :</span><br /><br /><span class="searchSpan"></span>'; 
	var sparqlQuery = '<span class="title">SPARQL Query :</span><pre class="queryPre"></pre>';
	var sparqlResult = '<span class="title">SPARQL Result :</span><span class="resultSpan"></span><div class="loading" style="display: block"><img src="img/loading_icon_blue.gif" alt="Loading" /><br /></div>'
	
	$('#query' + id).html(descSentence + '<br /><br /><hr /><br />' + sparqlResult + '<br /><hr /><br />' + sparqlQuery);
	$('#query' + id + ' .queryPre').text(results.content[id - 1].sparqlQuery);
	$('#query' + id + ' .searchSpan').html(results.content[id - 1].descriptiveSentence);

	processQuery(results.content[id - 1].sparqlQuery, id);
}


/**
 * Called when a query has been processed
 **/
function sparqlQueryResult(results, id)
{
    $('#query' + id + ' .loading').css('display', 'none');

    var resultString = '<br /><ul>';
    for(var i = 0; i < results.content.length; i++)
    {
    	resultString += '<li>' + results.content[i].res.split('#')[1] + '</li>';
    }
    resultString += '</ul>';

    $('#query' + id + ' .resultSpan').html(resultString);
}


/**
 * Enabling or disabling the search feature
 **/
function toggleSearch(b)
{
	if(b)
	{
		$('.searchButton').removeAttr('disabled');
		$('#searchLoading').css('display', 'none');
	}
	else
	{
		$('.searchButton').attr('disabled', 'disabled');
		$('#searchLoading').css('display', 'block');
	}
}
