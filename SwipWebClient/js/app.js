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
            $('#' + subgrid_id + ' .queryPre').text(generateSparql(row_id - 1, results));
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

	$('.descriptiveSentence select').die();
	$('.descriptiveSentence select').live('change', function()
	{
		var reg = $(this).attr('id').match(/gen(\d+)_(\d+)/);
		updateSparql(parseInt(reg[1]), results);
	});
}


/**
 * Called when results have to be displayed
 **/
function displayResults(results)
{
	var sentences = new DescList();
	for(var i = 0; i < results.content.length; i++)
		sentences.add(results.content[i].descriptiveSentence, i + 1);
	
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

	for(var i = 0; i < results.content.length; i++)
	{
		results.content[i].descriptiveSentence.string = results.content[i].descriptiveSentence.string.replace(/_mappingId_/g, i);
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

		$('[id^="query"]').each(function()
		{
			$('#results').tabs('remove', $(this).attr('id'));
		});

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

	var sparqlQuery = '<span class="title">SPARQL Query :</span><pre class="queryPre"></pre>';
	var sparqlResult = '<span class="title">SPARQL Result :</span><span class="resultSpan"></span><div class="loading" style="display: block"><img src="img/loading_icon_blue.gif" alt="Loading" /><br /></div>'
	
	$('#query' + id).html(sparqlResult + '<br /><hr /><br />' + sparqlQuery);
	$('#query' + id + ' .queryPre').text(generateSparql(id - 1, results));

	processQuery(generateSparql(id - 1, results), id);
}


function updateSparql(mappingId, results)
{
	if($('#query' + (mappingId + 1)).length)
	{
		$('#results').tabs('remove', '#query' + mappingId);
	}

	$('#jqGrid_' + (mappingId + 1) + '.subgrid .queryPre').text(generateSparql(mappingId, results));
}

function generateSparql(mappingId, results)
{
	var ret = results.content[mappingId].sparqlQuery.string;
	var genIds = ret.match(/_gen\d+_/g);
	var selectedFields = [];

	if(genIds != null)
	{
		for(var i = 0; i < genIds.length; i++)
		{
			var genId = genIds[i].substr(4, 1);
			var selectedField = results.content[mappingId].sparqlQuery.uris[genId][document.getElementById('gen' + mappingId + '_' + genId).selectedIndex];
			selectedFields.push(results.content[mappingId].descriptiveSentence.gen[genId][document.getElementById('gen' + mappingId + '_' + genId).selectedIndex]);

			var reg = new RegExp('_gen' + genId + '_');
			ret = ret.replace(reg, selectedField);
		}

		var inds = ret.match(/\?\w+ \(<http:\/\/purl\.org\/dc\/elements\/1\.1\/title>\|rdfs:label\) "([\w\s]+)"@\w{2}\./);

		if(inds != null && inds.length > 1)
		{
			for(var i = 1; i < inds.length; i++)
			{
				if($.inArray(inds[i], selectedFields) < 0)
				{
					var reg = new RegExp('\\?\\w+ \\(<http:\\/\\/purl\\.org\\/dc\\/elements\\/1\\.1\\/title>\\|rdfs:label\\) "' + inds[i] + '"@\\w{2}\\.');
					ret = ret.replace(reg, '');
				}
			}
		}
	}

	return ret;
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
