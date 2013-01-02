$(function()
{
	$('#disclaimer .seemore').click(function() {
		$('#disclaimer .seemore').hide();
    	$("#disclaimer p").show();
		$('#disclaimer .seeless').show();
	});

	$('#disclaimer .seeless').click(function() {
		$('#disclaimer .seemore').show();
    	$("#disclaimer p").hide();
		$('#disclaimer .seeless').hide();
	});

	$('#disclaimer .close').click(function() {
		$('#disclaimer').hide();
	});
});