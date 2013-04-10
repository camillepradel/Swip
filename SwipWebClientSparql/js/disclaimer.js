$(function()
{
	$('#disclaimer .showHideButton').click(function() {
        if ($("#disclaimer .openCloseIdentifier").is(":hidden")) {
            $("#disclaimer .content").animate({
                left: "-920px"
                }, 500 );
            $("#disclaimer .openCloseIdentifier").show();
        } else {
            $("#disclaimer .content").animate({
                left: "22px"
                }, 500 );
            $("#disclaimer .openCloseIdentifier").hide();
        }
	});
});

$(function()
{
	$('#configurations .showHideButton').click(function() {
        if ($("#configurations .openCloseIdentifier").is(":hidden")) {
            $("#configurations .content").animate({
                left: "-920px"
                }, 500 );
            $("#configurations .openCloseIdentifier").show();
        } else {
            $("#configurations .content").animate({
                left: "22px"
                }, 500 );
            $("#configurations .openCloseIdentifier").hide();
        }
	});
});