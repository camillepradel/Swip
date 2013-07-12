// exécute une requête SPARQL sur un endpoint donné
function processSparqlQuery(sparqlQuery, sparqlEndpointUri, callback)
{
    $.ajax
    ({
        type: 'GET',
        dataType: "json",
        url: sparqlEndpointUri + 'sparql',
        data: {query: sparqlQuery, output:'json'}
    })
    .done(function(data) {
      callback(data);
    })
    .fail(function(jqXHR) {
		// teste avec une requête syntaxiquement juste pour savoir si l'exception levée
		// est due à une erreur de syntaxe ou à un indisponibilité du serveur SPARQL
		testQuery = "select * where\n"
					+ "{\n"
					+ "  ?a ?b ?c"
					+ "}limit 1\n";
		$.ajax({
			type: 'GET',
			dataType: "json",
			url: sparqlEndpointUri + 'sparql',
			data: {query: testQuery, output:'json'}
		})
		.done(function(){
			alert(jqXHR.responseText);
		})
		.fail(function(){
			alert("Le SPARQL enpoint est innacessible ou ne contient aucun triplet...");
		});
    });
}

// exécute une requête SPARQL sur un endpoint donné
function processSparqlPostQuery(sparqlQuery, sparqlEndpointUri, callback)
{
    $.ajax
    ({
        type: 'POST',
        url: sparqlEndpointUri + 'update',
        data: {update: sparqlQuery}
    })
    .done(function(data) {
      callback(data);
    })
    .fail(function(jqXHR) {
		// teste avec une requête syntaxiquement juste pour savoir si l'exception levée
		// est due à une erreur de syntaxe ou à un indisponibilité du serveur SPARQL
		testQuery = "select * where\n"
					+ "{\n"
					+ "  ?a ?b ?c"
					+ "}limit 1\n";
		$.ajax({
			type: 'GET',
			dataType: "json",
			url: sparqlEndpointUri + 'sparql',
			data: {query: testQuery, output:'json'}
		})
		.done(function(){
			alert(jqXHR.responseText);
		})
		.fail(function(){
			alert("Le SPARQL enpoint est innacessible ou ne contient aucun triplet...");
		});
    });
}

// extrait la partie locale d'une URI (si elle n'est pas trouvée, la fonction renvoie l'uri complète)
function getLocalName(uri) {
	var regex = /(\/|#)[^\/#]*$/;
	var localName = uri.match(regex);
	if (localName && localName.length != uri.length)
		return localName[0].substring(1);
	else
		return uri;
}

// renvoie la partie de l'URI qui n'est pas locale (et qui peut être remplacée par un préfixe)
// si cette partie n'est pas trouvée, la fonction renvoie une chaine de caractères vide.
function getBaseName(uri) {
	var localName = getLocalName(uri);
	var corresponding = true;
	var i = uri.length;
	var j = localName.length;
	do
	{
		i--;
		j--;
		if (uri[i] != localName[j]) {
			corresponding = false;
		}
	}while(corresponding && i > 0 && j > 0);
	
	if (i > 0)
		return uri.substring(0, i);
	else
		return "";
}