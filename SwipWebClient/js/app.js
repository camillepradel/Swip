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

	/** 
	 * Submits the form when the "Search"
	 * button is clicked in the search field
	 **/
	$('#searchButton').click(function()
	{
		search($('#searchField').val());
	});

	/**
	 * Handles the double click on a query
	 **/
	$('.jqgrow').live('dblclick', function()
	{
		selectQuery($(this).attr('id'));
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
   			{name: 'descriptiveSentence', index: 'descriptiveSentence', width: 82, classes: 'descriptiveSentence'},
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
            $('#' + subgrid_id + ' .queryPre').text(results.content[row_id].sparqlQuery);
            $('#' + subgrid_id + ' .mappingPre').text(results.content[row_id].mappingDescription);
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


/**
 * Called when results have to be displayed
 **/
function displayResults(results)
{
	for(var i = 0; i < results.content.length; i++)
	{
		results.content[i].descriptiveSentence = results.content[i].descriptiveSentence.replace(/_selBeg_/g, '<select><option>');
		results.content[i].descriptiveSentence = results.content[i].descriptiveSentence.replace(/_selSep_/g, '</option><option>');
		results.content[i].descriptiveSentence = results.content[i].descriptiveSentence.replace(/_selEnd_/g, '</option></select>');
	}

	$('#results').css('display', 'block');
	fillTab(results);
}


/**
 * Called when the user has validated their input
 **/
function search(query)
{
	if(query != '')
	{
		toggleSearch(false);
		nlToPivot(query, 'fr');
	}
}


/**
 * Called when a query is selected
 **/
function selectQuery(id)
{
	/*
	TODO

	var descSentence = '<span class="title">Search :</span><br /><br /><span class="searchSpan"></span>'; 
	var sparqlQuery = '<span class="title">SPARQL Query :</span><pre class="queryPre"></pre>';
	var sparqlResult = '<span class="title">SPARQL Result :</span><div class="loading" style="display: block"><img src="img/loading_icon.gif" alt="Loading" /></div>'
	$('#results').append('<div id="query' + id + '" class="query">' + descSentence + '<br /><hr /><br />' + sparqlQuery + '<br /><hr /><br />' + sparqlResult + '</div>');
	$('#query' + id + ' .queryPre').text(parsedJson.content[id].sparqlQuery);
	$('#query' + id + ' .searchSpan').html(parsedJson.content[id].descriptiveSentence);
	$('#results').tabs('add', '#query' + id, 'Query #' + id);
	processQuery(parsedJson.content[id].sparqlQuery, id);*/

}


/**
 * Enabling or disabling the search feature
 **/
function toggleSearch(b)
{
	if(b)
	{
		$('#searchButton').removeAttr('disabled');
		$('#searchLoading').css('display', 'none');
	}
	else
	{
		$('#searchButton').attr('disabled', 'disabled');
		$('#searchLoading').css('display', 'block');
	}
}
