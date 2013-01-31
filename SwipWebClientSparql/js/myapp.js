var QUERIES_PREFIX = "http://swip.univ-tlse2.fr/ontologies/Queries#"
var PATTERNS_PREFIX = "http://swip.univ-tlse2.fr/ontologies/Patterns#"
var GRAPH_PREFIX = "http://swip.univ-tlse2.fr:8080/musicbrainz/"
var waitingForAnswer = false;

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
			stopRefresh();
		},
	},
]

var lastDisplayedProcessingStateNumber = 0;

function updateQueryState(queryUri, sparqlEndpointUri, stopRefresh) {
	// if (!waitingForAnswer) {
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
	// } else {
	// 	// console.log("updateQueryState postponed");
	// }
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
	console.log(sparqlQuery);
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
					+ "    OPTIONAL\n"
					+ "    {\n"
					+ "      ?em queries:emHasMatching ?m.\n"
					+ "      ?m (queries:matchingHasKeyword / queries:queryElementHasValue) ?keywordValue;\n"
					+ "         queries:matchingHasScore ?score;\n"
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

/**
 * 
 **/
function diplayTripleInstanciation(queryUri, sparqlEndpointUri)
{
	var sparqlQuery = "PREFIX patterns: <" + PATTERNS_PREFIX + ">\n"
					+ "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "PREFIX graph: <" + GRAPH_PREFIX + ">\n"
					+ "SELECT * WHERE\n"
					+ "{\n"
					+ "  GRAPH graph:patterns\n"
					+ "  {\n"
					+ "    ?p a patterns:Pattern;\n"
					+ "       patterns:isPatternMadeUpOf ?triple.\n"
					+ "    ?triple a patterns:PatternTriple.\n"
					+ "  }\n"
					+ "  GRAPH graph:queries\n"
					+ "  {\n"
					+ "    ?ptm a queries:PatternTripleMapping;\n"
					+ "        queries:hasQuery <" + queryUri + ">;\n"
					+ "        queries:hasPatternTriple ?triple;\n"
					+ "        queries:hasElementMapping ?em.\n"
					+ "    ?em queries:emHasPatternElement ?pe;\n"
					+ "    OPTIONAL\n"
					+ "    {\n"
					+ "      ?em (queries:emHasMatching / queries:matchingHasKeyword / queries:queryElementHasValue) ?keywordValue.\n"
					+ "    }\n"
					+ "  }\n"
					+ "}\n"
					+ "ORDER BY ?p ?triple ?ptm ?pe";
	// console.log(sparqlQuery);

	var html = "<h2>Pattern triples mappings</h2>\n";
	var callback = function(data) {
		waitingForAnswer = false;
		
		html += " <ul> "
		var currentPattern = "";
		var currentTriple = "";
		var currentTripleMapping = "";

		$.each(data.results.bindings, function(i, val) {
			if (currentPattern != val.p.value) {
				if (currentPattern != "") {
					html += " </ul> ";
				}
				currentPattern = val.p.value;
				html += " <li>Pattern " + currentPattern + "</li> ";
				html += " <ul> ";
				currentTriple = "";
			}
			if (currentTriple != val.triple.value) {
				if (currentTriple != "") {
					html += " </ul> </ul> ";
				}
				currentTriple = val.triple.value;
				html += " <li>Pattern triple " + currentTriple + "</li> ";
				html += " <ul> ";
				currentTripleMapping = "";
			}
			if (currentTripleMapping != val.ptm.value) {
				if (currentTripleMapping != "") {
					html += " </ul> ";
				}
				currentTripleMapping = val.ptm.value;
				html += " <li>Pattern triple mapping " + currentTripleMapping + "</li> ";
				html += " <ul> ";
			}
			html += "<li>" + val.em.value + ": " + val.pe.value.replace(currentPattern, "...") + " -> " + (val.keywordValue? (val.keywordValue.value) : "<i>empty mapping</i>") + "</li>";
		});

		html += " </ul> ";
		html += " </ul> ";
		html += " </ul> ";
		html += " </ul> ";
		$('#result .pivottomappings .triplemappings').html(html);
	}
	waitingForAnswer = true;
	processQuery(sparqlQuery, sparqlEndpointUri, callback);
}

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
					+ "  GRAPH graph:queries\n"
					+ "  {\n"
					+ "    OPTIONAL { ?sp ?state <" + queryUri + "> . }\n"
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
				case "http://swip.univ-tlse2.fr/ontologies/Queries#isNotMappedToQuery":
					imagePath = "img/not_mapped.svg";
					break;
				case "http://swip.univ-tlse2.fr/ontologies/Queries#isBeingMappedToQuery":
					imagePath = "img/being_mapped.svg";
					break;
				case "http://swip.univ-tlse2.fr/ontologies/Queries#toConsiderInMappingQuery":
					imagePath = "img/mapped.svg";
					break;
				case "http://swip.univ-tlse2.fr/ontologies/Queries#allreadyCombinedForQuery":
					imagePath = "img/mapped.svg";
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
					// + "            queries:hasEmrMark ?emrmark;\n"
					+ "            queries:hasQcrMark ?qcrmark;\n"
					+ "            queries:hasPcrMark ?pcrmark;\n"
					+ "            queries:hasRelevanceMark ?rmark.\n"
					+ "            OPTIONAL {?pm (queries:hasDescriptiveSentence/queries:sentenceHasValue) ?sent.}\n"
					+ "      }\n"
					+ "} ORDER BY DESC(?rmark) ?pm OFFSET 0 LIMIT 10\n";
	console.log(sparqlQuery);
	var html = "<h2>Ranked interpretations</h2>\n";
	var callback = function(data) {
		waitingForAnswer = false;
		
		html += " <ul> "

		$.each(data.results.bindings, function(i, val) {
				html += "<li><a href='" + val.pm.value + "'>" + (val.sent? val.sent.value : "no descriptive sentence") + "</a> - " + val.rmark.value + "(" + /*val.emrmark.value +*/ " - " + val.qcrmark.value + " - " + val.pcrmark.value + ")</li>";
		});

		html += " </ul> ";
		$('#result .pivottomappings .queryprocessed').html(html);
	}
	waitingForAnswer = true;
	processQuery(sparqlQuery, sparqlEndpointUri, callback);
}