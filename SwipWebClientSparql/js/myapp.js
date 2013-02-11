var QUERIES_PREFIX = "http://swip.univ-tlse2.fr/ontologies/Queries#"
var PATTERNS_PREFIX = "http://swip.univ-tlse2.fr/ontologies/Patterns#"
var GRAPH_PREFIX = "http://swip.univ-tlse2.fr:8080/musicbrainz/"
var RDF_PREFIX = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
var waitingForAnswer = false;
var subsentenceId = 0;

var PROCESSING_STATES = [
	{
		uri: QUERIES_PREFIX + "NotBegun",
		updateDisplay: function() {},
	},
	{
		uri: QUERIES_PREFIX + "PerformingMatching",
		updateDisplay: diplayMatching,
	},
	{
		uri: QUERIES_PREFIX + "PerformingElementMapping",
		updateDisplay: diplayElementMapping,
	},
	// {
	// 	uri: QUERIES_PREFIX + "PerformingPatternTripleMapping",
	// 	updateDisplay: diplayTripleInstanciation,
	// },
	{
		uri: QUERIES_PREFIX + "PerformingSpCollectionMapping",
		updateDisplay: diplaySpCollectionMapping,
	},
	{
		uri: QUERIES_PREFIX + "PerformingPatternMapping",
		updateDisplay: diplayPatternMapping,
	},
	{
		uri: QUERIES_PREFIX + "PerformingMappingRanking",
		updateDisplay: diplayMappingRanking,
	},
	{
		uri: QUERIES_PREFIX + "PerformingKbClearing",
		updateDisplay: diplayKbClearing,
	},
	{
		uri: QUERIES_PREFIX + "PerformingSentenceGeneration",
		updateDisplay: function() {},
	},
	{
		uri: QUERIES_PREFIX + "QueryProcessed",
		updateDisplay: function(queryUri, sparqlEndpointUri, stopRefresh) {
			diplayQueryProcessed(queryUri, sparqlEndpointUri);
			console.log("stopRefresh");
			stopRefresh();
		},
	},
]

var lastDisplayedProcessingStateNumber = 0;

function updateQueryState(queryUri, sparqlEndpointUri, stopRefresh) {
	if (!waitingForAnswer) {
		waitingForAnswer = true;
		getQueryProcessingState(queryUri, sparqlEndpointUri, function(processingState) {

			var processingStateNumber = 0;
			for(var i= 0; i < PROCESSING_STATES.length; i++) {
				if (PROCESSING_STATES[i].uri == processingState) {
					processingStateNumber = i;
					break;
				}
			}

			for(var state= lastDisplayedProcessingStateNumber; state <= processingStateNumber; state++) {
				PROCESSING_STATES[state].updateDisplay(queryUri, sparqlEndpointUri, stopRefresh);
			}

			lastDisplayedProcessingStateNumber = processingStateNumber;
			$('#state').text(processingState);
			waitingForAnswer = false;
		});
	} else {
		console.log("updateQueryState postponed");
	}
}


/**
 * 
 **/
function getQueryProcessingState(queryUri, sparqlEndpointUri, callback)
{
	var sparqlQuery = "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "PREFIX graph: <" + GRAPH_PREFIX + ">\n"
					+ "SELECT ?s\n"
					+ "WHERE\n"
					+ "{\n"
					+ "  GRAPH graph:queries\n"
					+ "  {\n"
					+ "    <" + queryUri + "> queries:queryHasProcessingState ?s.\n"
					+ "  }\n"
					+ "}";
	processQuery(sparqlQuery, sparqlEndpointUri, function(data) {
		callback(data.results.bindings[0].s.value);
	});
}

/**
 * 
 **/
function diplayMatching(queryUri, sparqlEndpointUri)
{
	var sparqlQuery = "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "PREFIX graph: <" + GRAPH_PREFIX + ">\n"
					+ "SELECT * WHERE\n"
					+ "{\n"
					+ "  GRAPH graph:queries\n"
					+ "  {\n"
					+ "    <" + queryUri + "> queries:queryHasQueryElement ?qe.\n"
					+ "    ?qe queries:queryElementHasValue ?keywordValue.\n"
					+ "    ?matching queries:matchingHasKeyword ?qe;\n"
					+ "              queries:matchingHasMatchedLabel ?matchedLabel;\n"
					+ "              queries:matchingHasScore ?score;\n"
					+ "              queries:matchingHasResource ?resource.\n"
					+ "  }\n"
					+ "}\n"
					+ "ORDER BY ?keywordValue DESC(?score)";
	// console.log(sparqlQuery);
	var callback = function(data) {
		waitingForAnswer = false;

		var table = "<h2>Matchings</h2>\n";
		table += "<table><tr><th>keyword</th><th>matched label</th><th>score</th><th>matching</th></tr>";
		var currentKeywordValue = "";

		$.each(data.results.bindings, function(i, val) {

			table += "<tr><td>" + ((currentKeywordValue == val.keywordValue.value)? "" : val.keywordValue.value) + "</td><td><a href='" + val.resource.value + "' title='matched resource: " + val.resource.value + "'>" + val.matchedLabel.value + "</a></td><td>" + val.score.value + "</td><td>" + "<a href='" + val.matching.value + "'>lien</a></td></tr>";
			currentKeywordValue = val.keywordValue.value;
		});

		table += "</table>";
		$('#result .pivottomappings .matching').html(table);
	}
	waitingForAnswer = true;
	processQuery(sparqlQuery, sparqlEndpointUri, callback);
}

/**
 * 
 **/
function diplayElementMapping(queryUri, sparqlEndpointUri)
{
	var sparqlQuery = "PREFIX patterns: <" + PATTERNS_PREFIX + ">\n"
					+ "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "PREFIX graph: <" + GRAPH_PREFIX + ">\n"
					+ "SELECT * WHERE\n"
					+ "{\n"
					+ "  GRAPH graph:patterns\n"
					+ "  {\n"
					+ "    ?p a patterns:Pattern.\n"
					+ "    ?p patterns:patternHasPatternElement ?pe.\n"
					+ "    ?pe patterns:targets ?target.\n"
					+ "  }\n"
					+ "  GRAPH graph:queries\n"
					+ "  {\n"
					+ "    ?em queries:mappingHasQuery <" + queryUri + ">;\n"
					+ "        queries:emHasPatternElement ?pe;\n"
					+ "        queries:emHasScore ?score.\n"
					+ "    OPTIONAL\n"
					+ "    {\n"
					+ "      ?em queries:emHasMatching ?m.\n"
					+ "      ?m (queries:matchingHasKeyword / queries:queryElementHasValue) ?keywordValue;\n"
					// + "         queries:matchingHasScore ?score;\n"
					+ "         queries:matchingHasMatchedLabel ?label;\n"
					+ "         queries:matchingHasResource ?resource.\n"
					+ "    }\n"
					+ "  }\n"
					+ "}\n"
					+ "ORDER BY ?p ?pe DESC(?score)";
	// console.log(sparqlQuery);
	var list = "<h2>Element mappings</h2>\n";
	var callback = function(data) {
		waitingForAnswer = false;
		
		list += " <ul> "
		var currentPattern = "";

		$.each(data.results.bindings, function(i, val) {
			if (currentPattern != val.p.value) {
				if (currentPattern != "") {
					list += "</table>";
				}
				currentPattern = val.p.value;
				list += "<li>Pattern " + currentPattern + "</li>";
				list += "<table><tr><th>pattern element</th><th>target</th><th>query element</th><th>matched label</th><th>score</th><th>element mapping</th></tr>";
			}
			// if (!val.keywordValue) {
			// 	val.keywordValue = {value:"plop"};
			// }
			list += "<tr><td>" + val.pe.value.replace(currentPattern, "...") + "</td><td>" + val.target.value + "</td><td>" + (val.keywordValue? val.keywordValue.value : "<i>empty mapping</i>") + "</td><td>" + (val.label? ("<a href='" + val.resource.value + "'>" + val.label.value + "</a>") : "-") + "</td><td>" + (val.score? val.score.value : "-") + "</td><td>" + "<a href='" + val.em.value + "'>lien</a></td></tr>";
		});

		list += "</table>";
		list += " </ul> ";
		$('#result .pivottomappings .elementmapping').html(list);
	}
	waitingForAnswer = true;
	processQuery(sparqlQuery, sparqlEndpointUri, callback);
}

// /**
//  * 
//  **/
// function diplayTripleInstanciation(queryUri, sparqlEndpointUri)
// {
// 	var sparqlQuery = "PREFIX patterns: <" + PATTERNS_PREFIX + ">\n"
// 					+ "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
// 					+ "PREFIX graph: <" + GRAPH_PREFIX + ">\n"
// 					+ "SELECT * WHERE\n"
// 					+ "{\n"
// 					+ "  GRAPH graph:patterns\n"
// 					+ "  {\n"
// 					+ "    ?p a patterns:Pattern;\n"
// 					+ "       patterns:isPatternMadeUpOf ?triple.\n"
// 					+ "    ?triple a patterns:PatternTriple.\n"
// 					+ "  }\n"
// 					+ "  GRAPH graph:queries\n"
// 					+ "  {\n"
// 					+ "    ?ptm a queries:PatternTripleMapping;\n"
// 					+ "        queries:hasQuery <" + queryUri + ">;\n"
// 					+ "        queries:hasPatternTriple ?triple;\n"
// 					+ "        queries:hasElementMapping ?em.\n"
// 					+ "    ?em queries:emHasPatternElement ?pe;\n"
// 					+ "    OPTIONAL\n"
// 					+ "    {\n"
// 					+ "      ?em (queries:emHasMatching / queries:matchingHasKeyword / queries:queryElementHasValue) ?keywordValue.\n"
// 					+ "    }\n"
// 					+ "  }\n"
// 					+ "}\n"
// 					+ "ORDER BY ?p ?triple ?ptm ?pe";
// 	// console.log(sparqlQuery);

// 	var html = "<h2>Pattern triples mappings</h2>\n";
// 	var callback = function(data) {
// 		waitingForAnswer = false;
		
// 		html += " <ul> "
// 		var currentPattern = "";
// 		var currentTriple = "";
// 		var currentTripleMapping = "";

// 		$.each(data.results.bindings, function(i, val) {
// 			if (currentPattern != val.p.value) {
// 				if (currentPattern != "") {
// 					html += " </ul> ";
// 				}
// 				currentPattern = val.p.value;
// 				html += " <li>Pattern " + currentPattern + "</li> ";
// 				html += " <ul> ";
// 				currentTriple = "";
// 			}
// 			if (currentTriple != val.triple.value) {
// 				if (currentTriple != "") {
// 					html += " </ul> </ul> ";
// 				}
// 				currentTriple = val.triple.value;
// 				html += " <li>Pattern triple " + currentTriple + "</li> ";
// 				html += " <ul> ";
// 				currentTripleMapping = "";
// 			}
// 			if (currentTripleMapping != val.ptm.value) {
// 				if (currentTripleMapping != "") {
// 					html += " </ul> ";
// 				}
// 				currentTripleMapping = val.ptm.value;
// 				html += " <li>Pattern triple mapping " + currentTripleMapping + "</li> ";
// 				html += " <ul> ";
// 			}
// 			html += "<li>" + val.em.value + ": " + val.pe.value.replace(currentPattern, "...") + " -> " + (val.keywordValue? (val.keywordValue.value) : "<i>empty mapping</i>") + "</li>";
// 		});

// 		html += " </ul> ";
// 		html += " </ul> ";
// 		html += " </ul> ";
// 		html += " </ul> ";
// 		$('#result .pivottomappings .triplemappings').html(html);
// 	}
// 	waitingForAnswer = true;
// 	processQuery(sparqlQuery, sparqlEndpointUri, callback);
// }

/**
 * 
 **/
function diplaySpCollectionMapping(queryUri, sparqlEndpointUri)
{
	var sparqlQuery = "PREFIX patterns: <" + PATTERNS_PREFIX + ">\n"
					+ "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "PREFIX graph: <" + GRAPH_PREFIX + ">\n"
					+ "SELECT ?sp (sample(?state) AS ?stateSample) WHERE\n"
					+ "{\n"
					+ "  GRAPH graph:patterns\n"
					+ "  {\n"
					+ "    ?sp a patterns:SubpatternCollection.\n"
					+ "  }\n"
					+ "  OPTIONAL\n"
					+ "  {\n"
					+ "    GRAPH graph:queries\n"
					+ "    {\n"
					+ "      ?sp ?state <" + queryUri + "> .\n"
					+ "    }\n"
					+ "  }\n"
					+ "}\n"
					+ "GROUP BY ?sp\n"
					+ "ORDER BY ?sp";
	// console.log(sparqlQuery);
	var html = "<h2>Subpattern collections mappings</h2>\n";
	var callback = function(data) {
		waitingForAnswer = false;
		
		html += " <ul> "

		$.each(data.results.bindings, function(i, val) {
			var imagePath = "";
			switch ((val.stateSample? val.stateSample.value : "")) {
				case "":
					imagePath = "img/interogation_mark.svg";
					break;
				case "http://swip.univ-tlse2.fr/ontologies/Queries#isNotMappedToQuery":
					imagePath = "img/not_mapped.svg";
					break;
				case "http://swip.univ-tlse2.fr/ontologies/Queries#isBeingMappedToQuery":
					imagePath = "img/being_mapped.svg";
					break;
				case "http://swip.univ-tlse2.fr/ontologies/Queries#toConsiderInMappingQuery":
					imagePath = "img/almost_mapped.svg";
					break;
				case "http://swip.univ-tlse2.fr/ontologies/Queries#allreadyCombinedForQuery":
					imagePath = "img/almost_mapped_2.svg";
					break;
				case "http://swip.univ-tlse2.fr/ontologies/Queries#isMappedToQuery":
					imagePath = "img/mapped.svg";
					break;
			}
			html += "<li><img src='" + imagePath + "' width='16px'>" + val.sp.value.replace("http://swip.univ-tlse2.fr/patterns/musicbrainz#", "") + "</li>";
		});

		html += " </ul> ";
		$('#result .pivottomappings .spcollectionmappings').html(html);
	}
	waitingForAnswer = true;
	processQuery(sparqlQuery, sparqlEndpointUri, callback);
}

/**
 * 
 **/
function diplayPatternMapping(queryUri, sparqlEndpointUri)
{
	var sparqlQuery = "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "PREFIX graph: <" + GRAPH_PREFIX + ">\n"
					+ "select (count(?pm) AS ?nb) where\n"
					+ "{\n"
					+ "  GRAPH graph:queries\n"
					+ "  {\n"
					+ "    ?pm a queries:PatternMapping;\n"
					+ "        queries:mappingHasQuery <" + queryUri + "> .\n"
					+ "  }\n"
					+ "}";
	// console.log(sparqlQuery);
	var html = "<h2>Pattern mappings</h2>\n";
	var callback = function(data) {
		waitingForAnswer = false;
		
		html += "number of mappings: "

		$.each(data.results.bindings, function(i, val) {
			html += val.nb.value;
		});

		html += " </ul> ";
		$('#result .pivottomappings .patternmappings').html(html);
	}
	waitingForAnswer = true;
	processQuery(sparqlQuery, sparqlEndpointUri, callback);
}

/**
 * 
 **/
function diplayMappingRanking(queryUri, sparqlEndpointUri)
{
	var html = "<h2>Mappings ranking</h2>\n";
	$('#result .pivottomappings .mappingsranking').html(html);
}

/**
 * 
 **/
function diplayKbClearing(queryUri, sparqlEndpointUri)
{
	var sparqlQuery = "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "PREFIX graph: <" + GRAPH_PREFIX + ">\n"
					+ "select (count(?pm) AS ?nb) where\n"
					+ "{\n"
					+ "  select distinct ?pm where\n"
					+ "  {\n"
					+ "  GRAPH graph:queries\n"
					+ "  {\n"
					+ "    ?pm a queries:PatternMapping;\n"
					+ "        queries:mappingHasQuery <" + queryUri + "> .\n"
					+ "  }\n"
					+ "  }\n"
					+ "}";
	// console.log(sparqlQuery);
	var html = "<h2>KB clearing</h2>\n";
	var callback = function(data) {
		waitingForAnswer = false;
		
		html += "number of mappings: "

		$.each(data.results.bindings, function(i, val) {
			html += val.nb.value;
		});

		html += " </ul> ";
		$('#result .pivottomappings .kbclearing').html(html);
	}
	waitingForAnswer = true;
	processQuery(sparqlQuery, sparqlEndpointUri, callback);
}

/**
 * 
 **/
function diplayQueryProcessed(queryUri, sparqlEndpointUri)
{
	var sparqlQuery = "PREFIX patterns: <" + PATTERNS_PREFIX + ">\n"
					+ "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "PREFIX graph: <" + GRAPH_PREFIX + ">\n"
					+ "SELECT * WHERE\n"
					+ "{\n"
					+ "      GRAPH graph:queries\n"
					+ "      {\n"
					+ "        ?pm a queries:PatternMapping;\n"
					+ "            queries:mappingHasQuery <" + queryUri + ">;\n"
					+ "        OPTIONAL { ?pm queries:hasEmrMark ?emrmark. }\n"
					+ "        OPTIONAL { ?pm queries:hasQcrMark ?qcrmark. }\n"
					+ "        OPTIONAL { ?pm queries:hasPcrMark ?pcrmark. }\n"
					+ "        OPTIONAL { ?pm queries:hasRelevanceMark ?rmark. }\n"

					+ "        OPTIONAL { ?pm queries:hasEmAvg ?avgscore. }\n"
					+ "        OPTIONAL { ?pm queries:hasEmFactor ?factor. }\n"
					+ "        OPTIONAL { ?pm queries:numberOfElementMappings ?nbem. }\n"
					+ "        OPTIONAL { ?pm queries:numberOfSignificantElementMappings ?nbsem. }\n"
					+ "      }\n"
					+ "} ORDER BY DESC(?rmark) ?pm OFFSET 0 LIMIT 10\n";
	// console.log(sparqlQuery);
	$('#result .pivottomappings .queryprocessed').append("<h2>Ranked interpretations</h2>\n");
	var callback = function(data) {
		waitingForAnswer = false;
		
		$('#result .pivottomappings .queryprocessed').append("<ol></ol>");

		$.each(data.results.bindings, function(i, val) {
			var mappingUri = val.pm.value;
			var emrmark = val.emrmark? val.emrmark.value : "no EM mark";
			var qcrmark = val.qcrmark? val.qcrmark.value : "no QC mark";
			var pcrmark = val.pcrmark? val.pcrmark.value : "no PC mark";
			var rmark = val.rmark? val.rmark.value : "no relevance mark";

			var avgscore = val.avgscore? val.avgscore.value : "no avgscore";
			var factor = val.factor? val.factor.value : "no factor";
			var nbem = val.nbem? val.nbem.value : "no nbem";
			var nbsem = val.nbsem? val.nbsem.value : "no nbsem";

			var html = "<li id='" + mappingUri.replace('urn:uuid:', '') + "'>" ;
			html += "<span class='mark' title='emrmark=avgscore*factor=" + avgscore + "*" + factor + "=" + emrmark + " - qcrmark=" + qcrmark + " - pcrmark=" + nbsem + "/" + nbem + "=" + pcrmark + "'>" + rmark + "</span><a href='" + mappingUri + "' class='descsent'>...</a>" ;
			html += "<div class='em'>Element mappings:</div>"
			html += "<div class='sparql'>SPARQL query:</div>"
			html += "</li>" ;
			$('#result .pivottomappings .queryprocessed ol').append(html);
			$('#'+mappingUri.replace('urn:uuid:', '')+' .em').click(function() {diplaysElementMappings(mappingUri, sparqlEndpointUri, mappingUri.replace('urn:uuid:', ''));});
			$('#'+mappingUri.replace('urn:uuid:', '')+' .sparql').click(function() {diplaysSparqlQuery(mappingUri, sparqlEndpointUri, mappingUri.replace('urn:uuid:', ''));});
			displayDescriptiveSentenceForPatternMapping(mappingUri, sparqlEndpointUri);
		});
	}
	waitingForAnswer = true;
	processQuery(sparqlQuery, sparqlEndpointUri, callback);
}


function displayDescriptiveSentenceForPatternMapping(mappingUri, sparqlEndpointUri)
{
	var sparqlQuery = "PREFIX patterns: <" + PATTERNS_PREFIX + ">\n"
					+ "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "PREFIX graph: <" + GRAPH_PREFIX + ">\n"
					+ "SELECT * WHERE\n"
					+ "{\n"
					+ "      GRAPH graph:queries\n"
					+ "      {\n"
					+ "        <" + mappingUri + "> queries:hasDescriptiveSubsentence ?node.\n"
					+ "        ?node queries:isMadeUpOfList ?firstList.\n"
					+ "      }\n"
					+ "}\n";
	// console.log(sparqlQuery);
	var callback = function(data) {	
		var html = "<span id='list_" + subsentenceId + "'></span>";
		$('#' + mappingUri.replace('urn:uuid:', '') + ' .descsent').html(html);
		displayDescriptiveSentenceList(data.results.bindings[0].firstList.value, data.results.bindings[0].node.value, sparqlEndpointUri, subsentenceId);
		subsentenceId++;
	}
	processQuery(sparqlQuery, sparqlEndpointUri, callback);
}


function displayDescriptiveSentenceList(listUri, parentNodeUri, sparqlEndpointUri, elementId)
{
	var sparqlQuery = "PREFIX patterns: <" + PATTERNS_PREFIX + ">\n"
					+ "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "PREFIX graph: <" + GRAPH_PREFIX + ">\n"
					+ "PREFIX rdf: <" + RDF_PREFIX + ">\n"
					+ "SELECT * WHERE\n"
					+ "{\n"
					+ "      GRAPH graph:queries\n"
					+ "      {\n"
					+ "        OPTIONAL {<" + listUri + "> rdf:first ?first.}\n"
					+ "        <" + listUri + "> rdf:rest ?rest.\n"
					+ "      }\n"
					+ "}\n";
	// console.log(sparqlQuery);
	var callback = function(data) {	
		// $('#list_' + elementId).append(" LIST ( ");
		if (data.results.bindings[0].first) {
			$('#list_' + elementId).append("<span id='list_" + subsentenceId + "'></span>");
			displayDescriptiveSentenceNode(data.results.bindings[0].first.value, parentNodeUri, sparqlEndpointUri, subsentenceId);
			subsentenceId++;
		}
		// $('#list_' + elementId).append(" / ");
		var restUri = data.results.bindings[0].rest.value;
		if (!endsWith(restUri, 'http://www.w3.org/1999/02/22-rdf-syntax-ns#nil')) {
			$('#list_' + elementId).append("<span id='list_" + subsentenceId + "'></span>");
			displayDescriptiveSentenceList(restUri, parentNodeUri, sparqlEndpointUri, subsentenceId);
			subsentenceId++;
		}
		// $('#list_' + elementId).append(" ) ");
		// console.log("elementId: " + elementId + " - subsentenceId: " + subsentenceId);
	}
	processQuery(sparqlQuery, sparqlEndpointUri, callback);
}


function displayDescriptiveSentenceNode(nodeUri, parentNodeUri, sparqlEndpointUri, elementId)
{
	var sparqlQuery = "PREFIX patterns: <" + PATTERNS_PREFIX + ">\n"
					+ "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "PREFIX graph: <" + GRAPH_PREFIX + ">\n"
					+ "PREFIX rdf: <" + RDF_PREFIX + ">\n"
					+ "SELECT * WHERE\n"
					+ "{\n"
					+ "      GRAPH graph:queries\n"
					+ "      {\n"
					+ "        OPTIONAL {<" + nodeUri + "> queries:hasStringValue ?stringValue.}\n"
					+ "        OPTIONAL {<" + nodeUri + "> queries:isMadeUpOfList ?list.}\n"
					// + "        OPTIONAL {<" + nodeUri + "> (queries:insertForSubsentenceOf/queries:hasDescriptiveSubsentence/queries:isMadeUpOfList-for) ?listFor.}\n"
					+ "        OPTIONAL {select distinct ?listFor where\n"
					+ "          {<" + nodeUri + "> queries:insertForSubsentenceOf ?plop.\n"
					+ "           <" + parentNodeUri + "> queries:isMadeUpOfList-for ?listFor.}\n"
					+ "          }\n"
					+ "      }\n"
					+ "}\n";
	console.log(sparqlQuery);
	var callback = function(data) {
		if (data.results.bindings.length > 0) {
			var stringValue = data.results.bindings[0].stringValue? data.results.bindings[0].stringValue.value : "";
			if (stringValue != "") {
				$('#list_' + elementId).append(stringValue);
			}
			var list = data.results.bindings[0].list? data.results.bindings[0].list.value : "";
			if (list != "") {
				$('#list_' + elementId).append("<span id='list_" + subsentenceId + "'></span>");
				displayDescriptiveSentenceList(list, nodeUri, sparqlEndpointUri, subsentenceId);
				subsentenceId++;
			}
			
			if (data.results.bindings[0].listFor) {
				for (var i=0,len=data.results.bindings.length; i<len; i++) {
					var listFor = data.results.bindings[i].listFor? data.results.bindings[i].listFor.value : "PROBLEM!!";
					$('#list_' + elementId).append("<span id='list_" + subsentenceId + "'></span>");
					displayDescriptiveSentenceList(listFor, nodeUri, sparqlEndpointUri, subsentenceId);
					subsentenceId++;
				}
			}
		}
	}
	processQuery(sparqlQuery, sparqlEndpointUri, callback);
}


function diplaysElementMappings(mappingUri, sparqlEndpointUri, elementId)
{
	var sparqlQuery = "PREFIX patterns: <" + PATTERNS_PREFIX + ">\n"
					+ "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "PREFIX graph: <" + GRAPH_PREFIX + ">\n"
					+ "PREFIX rdf: <" + RDF_PREFIX + ">\n"
					+ "SELECT * WHERE\n"
					+ "{\n"
					+ "  GRAPH graph:queries\n"
					+ "  {\n"
					+ "    <" + mappingUri + "> queries:mappingContainsMapping+ ?em.\n"
					+ "    ?em queries:emHasPatternElement ?pe;\n"
					+ "        queries:emHasScore ?score;\n"
					+ "    OPTIONAL\n"
					+ "    {\n"
					+ "      ?em queries:emHasMatching ?m.\n"
					+ "      ?m (queries:matchingHasKeyword / queries:queryElementHasValue) ?keywordValue;\n"
					// + "         queries:matchingHasScore ?score;\n"
					+ "         queries:matchingHasMatchedLabel ?label;\n"
					+ "         queries:matchingHasResource ?resource.\n"
					+ "    }\n"
					+ "  }\n"
					+ "  GRAPH graph:patterns\n"
					+ "  {\n"
					+ "    ?pe patterns:targets ?target.\n"
					+ "  }\n"
					+ "} order by ?pe\n";
	console.log(sparqlQuery);
	var callback = function(data) {		
		var html = "<table><tr><th>pattern element</th><th>target</th><th>query element</th><th>matched label</th><th>score</th><th>element mapping</th></tr>";

		$.each(data.results.bindings, function(i, val) {
			
			html += "<tr><td>" + val.pe.value + "</td><td>" + val.target.value + "</td><td>" + (val.keywordValue? val.keywordValue.value : "<i>empty mapping</i>") + "</td><td>" + (val.label? ("<a href='" + val.resource.value + "'>" + val.label.value + "</a>") : "-") + "</td><td>" + (val.score? val.score.value : "-") + "</td><td>" + "<a href='" + val.em.value + "'>lien</a></td></tr>";
		});

		html += "</table>";
		$('#' + elementId + " .em").append(html);
	}
	processQuery(sparqlQuery, sparqlEndpointUri, callback);
}


function diplaysSparqlQuery(mappingUri, sparqlEndpointUri, elementId)
{
	
		$('#' + elementId + " .sparql").append("<div><b>comming soon...</b></div>");
}


function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}