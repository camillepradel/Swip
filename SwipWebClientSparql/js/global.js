// positionne les différents panneaux en fonction de la hauteur de leur bouton et les met hors d'écran
function initializePositionPanels(jqObject)
{
	var top = 0;
	jqObject.each(function(i, panel){
		var showHideButton = $(panel.parentNode).children('.showHideButton')[0];
		panel.style.top = top + "px";
		showHideButton.style.top = top + "px";
		
		var sizePanel = $(panel).outerWidth(true);
		panel.style.left = "-" + sizePanel + "px";
		
		// calcul de la hauteur du bouton à faire...
		top += 32;
	});
}

// affiche ou cache un panneau latéral
function showHide(event) {
	var showHideButton = event.target;
	var jqShowHideButton = $(showHideButton);
	
	var item = showHideButton.parentNode;
	var jqItem = $(item);
	
	var panel = jqItem.children('.panel');
	var jqPanel = $(panel);
	
	var sizePanel = $(panel).outerWidth(true);
	if (panel.is(":hidden")) {// affiche le panneau en cachant tous les autres
		$('.panel:visible').parent().children('.showHideButton').click();
		
		$('#panelLocked').show();
		jqShowHideButton.addClass('showHideButtonSelected');
		jqPanel.addClass('itemControlPanelSelected');
		jqPanel.show();
		jqPanel.animate({
			left: "0px"
		}, 500 );
	} else {// cache le panneau
		panel.animate({
			left: "-" + sizePanel + "px"
		}, 500, function(){
			jqShowHideButton.removeClass('showHideButtonSelected');
			panel.hide();
			if ($('.panel:visible').length == 0)
				$('#panelLocked').hide();
		});
	}
}

$(function(){
	// gestion des panneaux lattéraux
	$('#controlPanel').append("<div id = 'panelLocked'></div>");
	$('.showHideButton').click(showHide);
	initializePositionPanels($('.panel'));
});