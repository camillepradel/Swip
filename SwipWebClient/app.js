$(function()
{
	/** 
	 * Submits the form when the return key
	 * is pressed in the search field
	**/
	$("#searchField").keydown(function(event)
	{
    	if(event.keyCode == 13)
    	{
	        $("#searchButton").click();
	    }
	});
});


function fillTab(data)
{

	jQuery("#jqGrid").jqGrid
	({
		datatype: "jsonstring",
		datastr: data,
		loadonce: true,
		height: "auto",
		autowidth: true,
		shrinkToFit: true,
		jsonReader: 
		{
			root: "content",
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
            $("#" + subgrid_id).append(data.content[row_id].sparqlQuery);
        },
        pager: "#pager",
        rowNum: 3
	});
}


function search(query)
{
	if(query != '')
	{
		$.getJSON('./response.json', function(data)
		{
			$('#logo').animate
			({
				'margin-top': 0
			}, 'slow', function()
			{
				fillTab(data);
			});
			
		});
	}
}