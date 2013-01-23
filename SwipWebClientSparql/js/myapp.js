var QUERIES_PREFIX = "http://swip.univ-tlse2.fr/ontologies/Queries#"
var PATTERNS_PREFIX = "http://swip.univ-tlse2.fr/ontologies/Patterns#"
var GRAPH_PREFIX = "http://swip.univ-tlse2.fr:8080/musicbrainz/"

var PROCESSING_STATES = [
	{
		uri: QUERIES_PREFIX + "NotBegun",
		updateDisplay: function() {},
	},
	{
		uri: QUERIES_PREFIX + "InitializingQueryInterpretation",
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
		uri: QUERIES_PREFIX + "QueryProcessed",
		updateDisplay: function(queryUri, sparqlEndpointUri, stopRefresh) {
			diplayPatternMapping(queryUri, sparqlEndpointUri);
			stopRefresh();
		},
	},
]

var lastDisplayedProcessingStateNumber = 0;

function updateQueryState(queryUri, sparqlEndpointUri, stopRefresh) {
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
	});
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
		var table = "<h2>Matchings</h2>\n";
		table += "<table><tr><th>keyword</th><th>matched label</th><th>score</th></tr>";
		var currentKeywordValue = "";

		$.each(data.results.bindings, function(i, val) {

			table += "<tr><td>" + ((currentKeywordValue == val.keywordValue.value)? "" : val.keywordValue.value) + "</td><td><a href='" + val.resource.value + "' title='matched resource: " + val.resource.value + "'>" + val.matchedLabel.value + "</a></td><td>" + val.score.value + "</td></tr>";
			currentKeywordValue = val.keywordValue.value;
		});

		table += "</table>";
		$('#result .pivottomappings .matching').html(table);
	}
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
		
		list += " <ul> "
		var currentPattern = "";

		$.each(data.results.bindings, function(i, val) {
			if (currentPattern != val.p.value) {
				if (currentPattern != "") {
					list += "</table>";
				}
				currentPattern = val.p.value;
				list += "<li>Pattern " + currentPattern + "</li>";
				list += "<table><tr><th>pattern element</th><th>target</th><th>query element</th><th>matched label</th><th>score</th></tr>";
			}
			// if (!val.keywordValue) {
			// 	val.keywordValue = {value:"plop"};
			// }
			list += "<tr><td>" + val.pe.value.replace(currentPattern, "...") + "</td><td>" + val.target.value + "</td><td>" + (val.keywordValue? val.keywordValue.value : "<i>empty mapping</i>") + "</td><td>" + (val.label? ("<a href='" + val.resource.value + "'>" + val.label.value + "</a>") : "-") + "</td><td>" + (val.score? val.score.value : "-") + "</td></tr>";
		});

		list += "</table>";
		list += " </ul> ";
		$('#result .pivottomappings .elementmapping').html(list);
	}
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
					+ "SELECT * WHERE\n"
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
					+ "ORDER BY ?sp";
	// console.log(sparqlQuery);
	var html = "<h2>Subpattern collections mappings</h2>\n";
	var callback = function(data) {
		
		html += " <ul> "

		$.each(data.results.bindings, function(i, val) {
			var imagePath = "";
			switch (val.state.value) {
				case "http://swip.univ-tlse2.fr/ontologies/Queries#isNotMappedToQuery":
					imagePath = "img/not_mapped.svg";
					break;
				case "http://swip.univ-tlse2.fr/ontologies/Queries#isBeingMappedToQuery":
					imagePath = "img/being_mapped.svg";
					break;
				case "http://swip.univ-tlse2.fr/ontologies/Queries#toConsiderInMappingQuery":
					imagePath = "img/mapped.svg";
					break;
				case "http://swip.univ-tlse2.fr/ontologies/Queries#isMappedToQuery":
					imagePath = "img/mapped.svg";
					break;
			}
			html += "<li><img src='" + imagePath + "' width='32px'>Subpattern " + val.sp.value + "</li>";
		});

		html += " </ul> ";
		$('#result .pivottomappings .spcollectionmappings').html(html);
	}
	processQuery(sparqlQuery, sparqlEndpointUri, callback);
}

/**
 * 
 **/
function diplayPatternMapping(queryUri, sparqlEndpointUri)
{
	var sparqlQuery = "PREFIX patterns: <" + PATTERNS_PREFIX + ">\n"
					+ "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "PREFIX graph: <" + GRAPH_PREFIX + ">\n"
					+ "SELECT * WHERE\n"
					+ "{\n"
					+ "  GRAPH graph:patterns\n"
					+ "  {\n"
					+ "    ?p a patterns:Pattern.\n"
					+ "  }\n"
					+ "  GRAPH graph:queries\n"
					+ "  {\n"
					+ "    ?em a queries:ElementMapping.\n"
					+ "    ?pm queries:mappingHasPatternConstituent ?p;\n"
					+ "       queries:mappingHasQuery <" + queryUri + ">;\n"
					+ "       queries:mappingContainsMapping+ ?em.\n"
					+ "    ?em queries:emHasMatching ?m;\n"
					+ "        queries:emHasPatternElement ?pe.\n"
					+ "    ?m (queries:matchingHasKeyword / queries:queryElementHasValue) ?keywordValue;\n"
					+ "       queries:matchingHasScore ?score;\n"
					+ "       queries:matchingHasMatchedLabel ?label;\n"
					+ "       queries:matchingHasResource ?resource.\n"
					+ "  }\n"
					+ "}\n"
					+ "ORDER BY ?p ?pm LIMIT 50";
	console.log(sparqlQuery);
	var html = "<h2>Pattern mappings</h2>\n";
	var callback = function(data) {
		
		html += " <ul> "

		$.each(data.results.bindings, function(i, val) {
			var imagePath = "";
			switch (val.state.value) {
				case "http://swip.univ-tlse2.fr/ontologies/Queries#isNotMappedToQuery":
					imagePath = "img/not_mapped.svg";
					break;
				case "http://swip.univ-tlse2.fr/ontologies/Queries#isBeingMappedToQuery":
					imagePath = "img/being_mapped.svg";
					break;
				case "http://swip.univ-tlse2.fr/ontologies/Queries#toConsiderInMappingQuery":
					imagePath = "img/mapped.svg";
					break;
				case "http://swip.univ-tlse2.fr/ontologies/Queries#isMappedToQuery":
					imagePath = "img/mapped.svg";
					break;
			}
			html += "<li><img src='" + imagePath + "' width='32px'>Subpattern " + val.sp.value + " : " + val.state.value.replace("http://swip.univ-tlse2.fr/ontologies/Queries#", "") + "</li>";
		});

		html += " </ul> ";
		$('#result .pivottomappings .patternmappings').html(html);
	}
	// processQuery(sparqlQuery, sparqlEndpointUri, callback);
}