// fonction qui supprime le préfixe décrit par la ligne contenant la croix sur laquelle
// l'utilisateur a cliqué pour la supprimer
function removePrefix(event) {
	var parentNode = event.currentTarget.parentNode;
	var id = parentNode.children[1].innerHTML;
	$.ajax
	({
		type: 'POST',
		url: 'http://localhost/SWIP_Admin/php/deletePrefixes.php',
		data: { uri: id }
	})
	.done(function(data) {
		delete listPrefixes[id];
		parentNode.id = "deletedPrefix";
		$("#deletedPrefix").removeClass("prefixLine");
		$("#deletedPrefix").slideUp(200, function() {
			$("#deletedPrefix").remove();
			if (Object.size(listPrefixes) == 0)
			{
				$("#prefixes .listLabel").html("No prefixes founded.");
			}
		});
	})
	.fail(function(jqXHR) {
		alert("Error occured trying to delete " +
			  parentNode.children[0].innerHTML + " prefix : " + jqXHR.responseText);
	});
}

$(function (){
	// anime le bouton d'ajout de préfixe
	$('#addButton').mousedown(function(){
		this.src = "../img/addButtonClick.png";
	});
	
	$('#addButton').mouseup(function(){
		this.src = "../img/addButton.png";
	});
	
	// fonction d'ajout d'un nouveau préfixe
	$('#addButton').click(function(){
		// vérification de la validité des champs du formulaire
		var prefixVal = $("#newPrefix").val();
		var uriVal = $("#newURI").val();
		var prefix = prefixVal.replace(/\s+/g, '');
		var uri = uriVal.replace(/\s+/g, '');
		
		if (prefixVal != prefix || uriVal != uri)
		{
			$("#newPrefix").val(prefix);
			$("#newURI").val(uri);
			
			if (prefix == "")
				$("#newPrefix").css("border-color", "red"); // affichage de l'erreur
			
			if (uri == "")
				$("#newURI").css("border-color", "red"); // affichage de l'erreur
		} else {
			if (uri == "" || prefix == "")
			{
				$("#newPrefix").val(prefix);
				$("#newURI").val(uri);
				
				if (prefix == "")
					$("#newPrefix").css("border-color", "red"); // affichage de l'erreur
				
				if (uri == "")
					$("#newURI").css("border-color", "red"); // affichage de l'erreur
			} else {
				// si les champs sont corrects, on les vide et on envoie leur valeur à la page
				// addPrefixes.php pour qu'elles soient référencées dans la base de données
				$("#newPrefix").val("");
				$("#newURI").val("");
				
				$.ajax
				({
					type: 'POST',
					url: 'http://localhost/SWIP_Admin/php/addPrefixes.php',
					data: { uri: uri, prefix: prefix }
				})
				.done(function(data) {
					// si le préfixe a correctement été ajouté, on l'affiche dans la liste
					if (Object.size(listPrefixes) == 0)
						$("#prefixes .listLabel").html("Prefixes used:");
					
					listPrefixes[uri] = prefix;
					
					var lastLine = null;
					$.each($(".prefixLine"), function(i, line) {
						var texte = line.firstElementChild.innerHTML;
						var currentPrefix = texte.substring(0, texte.length - 1)
						if (prefix.toLowerCase() > currentPrefix.toLowerCase())
							lastLine = line;
					});
					
					if (lastLine == null) {
						$("#prefixes .listLabel")
							.after("<div class = 'prefixLine' id = 'currentAdding' style = 'display: none;'>" + 
								   "	<span class = 'prefix'>" + prefix + ": </span>\n" +
								   "	<span class = 'uri'>" + uri + "</span>\n" +
								   "	<img class = 'deletePrefix' src = '../img/croix.png' />\n" +
								   "</div>");
								   
						$("#currentAdding").slideDown(200, function(){$(this).removeAttr("style");});
					} else {
						lastLine.id = "insertAfter";
						$("#insertAfter")
							.after("<div id = 'currentAdding' class = 'prefixLine' style = 'display: none;'>\n" +
								   "	<span class = 'prefix'>" + prefix + ": </span>\n" +
								   "	<span class = 'uri'>" + uri + "</span>\n" +
								   "	<img class = 'deletePrefix' src = '../img/croix.png' />\n" +
								   "</div>\n");
						
						$("#currentAdding").slideDown(200, function(){$(this).removeAttr("style");});
						$("#insertAfter").removeAttr("id");
					}
						
					$('#currentAdding').mouseenter(function(eventObject){
						eventObject.currentTarget.children[2].style.display = "inline-block";
					});
					
					$('#currentAdding').mouseleave(function(eventObject){
						$('.deletePrefix').hide();
					});
					
					$('#currentAdding .deletePrefix').mousedown(function(){
						this.src = "../img/croixClick.png";
					});
					
					$('#currentAdding .deletePrefix').mouseup(function(){
						this.src = "../img/croix.png";
					});
					
					$('#currentAdding .deletePrefix').click(function(event){
						removePrefix(event);
					});
					
					$("#currentAdding").removeAttr("id");
				})
				.fail(function(jqXHR) {
					alert("Error occured trying to add " + prefix + " prefix : " + jqXHR.responseText);
				});
			}
		}
	});
	
	// si l'on tape sur entrée lorsque le focus est sur un des input du formulaire d'ajout
	// de préfixe, alors on exécute la même fonction que si l'on avait cliqué sur le bouton d'ajout
	$("#newPrefix").keypress(function(event){
		if (event.which == 13)
			$('#addButton').click();
	});
	
	$("#newURI").keypress(function(event){
		if (event.which == 13)
			$('#addButton').click();
	});
	
	// suite à une erreur dans le formulaire d'ajout, ces deux fonctions remmettent la couleur
	// du contour des input concernés à sa valeur par défaut.
	$("#newPrefix").on("inputchange", function(){
		if ($("#newPrefix").val().replace(/\s+/g, '').length != 0)
			$("#newPrefix").css("border-color", "");
	});
	
	$("#newURI").on("inputchange", function(){
		if ($("#newURI").val().replace(/\s+/g, '').length != 0)
			$("#newURI").css("border-color", "");
	});
	
	// lorsque la liste des préfixes a été chargée une première fois, on la met en forme
	// pour pouvoir l'éditer facilement
	$('#prefixes').bind("prefixLoaded", function(){
		// ajout d'une croix pour la suppression d'un préfixe
		$('.prefixLine').append("<img class = 'deletePrefix' src = '../img/croix.png' />\n");
		
		$('.deletePrefix').click(function(event){
			removePrefix(event);
		});
		
		// affichage de la croix lors du survol d'une ligne décrivant un préfixe
		$('.prefixLine').mouseenter(function(eventObject){
			eventObject.currentTarget.children[2].style.display = "inline-block";
		});
		
		$('.prefixLine').mouseleave(function(eventObject){
			$('.deletePrefix').hide();
		});
		
		// animation de la croix
		$('.deletePrefix').mousedown(function(){
			this.src = "../img/croixClick.png";
		});
		
		$('.deletePrefix').mouseup(function(){
			this.src = "../img/croix.png";
		});
	});
	
	// ajout des patrons désactivés dans la liste des patrons disponibles et
	// affichage du premier patrons s'ils sont tous désactivés
	$('#layout').bind("listGraphLoaded", function(){
		var sparqlQuery = "prefix patternOnt: <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
						+ "prefix xsd: <http://www.w3.org/2001/XMLSchema#>\n"
						+ "select distinct * where\n"
						+ "{\n"
						+ "  graph <" + $('#namedgraph').val() + ">\n"
						+ "  {\n"
						+ "    ?uri a patternOnt:Pattern.\n"
						+ "    ?uri patternOnt:patternHasID ?id.\n"
						+ "    ?uri patternOnt:patternIsActive \"false\"^^xsd:boolean"
						+ "  }\n"
						+ "} order by ?id";
		processSparqlQuery(sparqlQuery, $('#sparqlendpoint').val(), function(data){
			var displayFirst = ($("#selectPattern option").length == 0)? true : false;
			
			$.each(data.results.bindings, function(i, val) {
				var patternID = val.id.value;
				var patternURI = val.uri.value;
				
				$("#selectPattern option").each(function (i, val){
					if (val.innerHTML.toLowerCase() > patternID.toLowerCase()){
						val.id = "insertBefore";
					}
				});
				
				if ($("#insertBefore").length == 0) {
					$("#selectPattern").append("<option class = 'patternDisactivated' value = '" + patternURI + "'>" + patternID + "</option>\n");
				} else {
					$("#insertBefore").before("<option class = 'patternDisactivated' value = '" + patternURI + "'>" + patternID + "</option>\n");
					$("#insertBefore").removeAttr("id");
				}
				
				var div = "<div class = 'pattern' id = '" + patternID + "'>\n"
						+  "	<div class = 'graph'>\n"
						+  "	</div>\n"
						+  "   <h1>Descriptive sentence :</h1>\n"
						+  "	<p class = 'sentence'>\n"
						+  "	</p>\n"
						+  "</div>\n";
				$("#layout").append(div);
				
				$("#" + patternID + " .graph").css({
					"width": $('#graphWidth').val() + "px",
					"height": $('#graphHeight').val() + "px"
				});
				
				$("#" + patternID).hide();
			});
			
			$('#formSelectPattern').width($('#selectPattern').outerWidth());
			
			if (displayFirst)
				displayPattern();
		});
	});
	
	// (dés)activation du patron
	$('#layout').bind("graphLoaded", function(){
		var patternID = $('#selectPattern option:selected').text();
		var patternURI = $('#selectPattern option:selected').val();
		
		var sparqlQuery = "prefix patternOnt: <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
						+ "select distinct * where\n"
						+ "{\n"
						+ "  graph <" + $('#namedgraph').val() + ">\n"
						+ "  {\n"
						+ "    <" + patternURI + "> patternOnt:patternIsActive ?isActive.\n"
						+ "  }\n"
						+ "}";
		processSparqlQuery(sparqlQuery, $('#sparqlendpoint').val(), function(data){
			var content = "";
			if (data.results.bindings[0].isActive.value == "true")
				content = "disactivate pattern";
			else
				content = "activate pattern";
			
			if ($('#' + patternID + "_activate").length == 0)
				$('#' + patternID + " .graph").before("<button class = 'activateButton' id = '" + patternID + "_activate'>" + content + "</button>");
			else
				$('#' + patternID + "_activate").html(content);
			
			$('#' + patternID + "_activate").css({
				"display": "block",
				"marginLeft": "auto",
				"marginRight": "auto"
			});
			
			$('#' + patternID + "_activate").click(function(event){
				var activate = true;
				if(event.target.innerHTML == "disactivate pattern")
					activate = false;
				
				var sparqlQuery = "prefix patternOnt: <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
								+ "prefix xsd: <http://www.w3.org/2001/XMLSchema#>\n"
								+ "delete data\n"
								+ "{\n"
								+ "    graph <" + $('#namedgraph').val() + ">\n"
								+ "    {\n"
								+ "        <" + patternURI + "> patternOnt:patternIsActive " + !activate + ".\n"
								+ "    }\n"
								+ "};\n"
								+ "insert data\n"
								+ "{\n"
								+ "    graph <" + $('#namedgraph').val() + ">\n"
								+ "    {\n"
								+ "        <" + patternURI + "> patternOnt:patternIsActive \"" + activate + "\"^^xsd:boolean.\n"
								+ "    }\n"
								+ "}\n";
				processSparqlPostQuery(sparqlQuery, $('#sparqlendpoint').val(), function(){
					if (activate) {
						content = "disactivate pattern";
						$('option:selected').removeClass('patternDisactivated');
					} else {
						content = "activate pattern";
						$('option:selected').addClass('patternDisactivated');
					}
						
					$('#' + patternID + "_activate").html(content);
					alert("You need to clear cache memory !");
				});
			});
		});
	});
});