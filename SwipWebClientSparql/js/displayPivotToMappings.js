var QUERIES_PREFIX = "http://swip.univ-tlse2.fr/ontologies/Queries#";
var PATTERNS_PREFIX = "http://swip.univ-tlse2.fr/ontologies/Patterns#";
var RDF_PREFIX = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
var SP_PREFIX = "http://spinrdf.org/sp";
var waitingForAnswer = false;
var subsentenceId = 0;
var queriesNamedGraphUri = "";
var patternsNamedGraphUri = "";

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
	{
		uri: QUERIES_PREFIX + "PerformingSpCollectionMapping",
		updateDisplay: function() {},
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
		uri: QUERIES_PREFIX + "PerformingMappingsClearing",
		updateDisplay: diplayKbClearing,
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
		uri: QUERIES_PREFIX + "PerformingSparqlQueryGeneration",
		updateDisplay: function() {},
	},
	{
		uri: QUERIES_PREFIX + "PerformingQueryGeneration",
		updateDisplay: function(queryUri, sparqlEndpointUri, stopRefresh) {
			diplayMappingsAndSentences(queryUri, sparqlEndpointUri);
			console.log("stopRefresh");
			stopRefresh();
			$('.searchButton').removeAttr('disabled');
		},
	},
	{
		uri: QUERIES_PREFIX + "QueryProcessed",
		updateDisplay: function(queryUri, sparqlEndpointUri, stopRefresh) {},
	},
];

var usedPrefixes = {
	mo: "http://purl.org/ontology/mo/",
	foaf: "http://xmlns.com/foaf/0.1/",
	patterns_mb: "http://swip.univ-tlse2.fr/patterns/musicbrainz#",
};

var lastDisplayedProcessingStateNumber = 0;

function beforePivotToSparql() {
	lastDisplayedProcessingStateNumber = 0;
	animateLogo();
    $('.searchButton').attr('disabled', 'disabled');
    $('#processingstate').css('display', 'block');
}

function donePivotToSparql(data, sparqlEndpointUri, queriesNamedGraphUrii, patternsNamedGraphUrii) {
	queriesNamedGraphUri = queriesNamedGraphUrii;
	patternsNamedGraphUri = patternsNamedGraphUrii;
	$('#pivottomappings').empty();
    $('#pivottomappings').append('<h1>Pivot query to SPARQL translation</h1>');
    var queryUri = data['queryUri'];
    $('#result #pivottomappings').append('<p><b>Query URI:</b> <i>' + (queryUri) + '</i></p>');
    updateQueryState(queryUri, sparqlEndpointUri, function() {clearInterval(refreshIntervalId);});
    var refreshIntervalId = setInterval(function() { updateQueryState(queryUri, sparqlEndpointUri, function() {clearInterval(refreshIntervalId);}); }, 2000);
}

function failPivotToSparql() {
	$('.searchButton').removeAttr('disabled');
    alert('Ajax error, please try again !');
}

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

			for(var state= lastDisplayedProcessingStateNumber; state < processingStateNumber; state++) {
				$('#' + PROCESSING_STATES[state].uri.replace(QUERIES_PREFIX,"") + "_state").addClass("done");
				// console.log('#' + PROCESSING_STATES[state].uri.replace(QUERIES_PREFIX,"") + "_state");
				PROCESSING_STATES[state].updateDisplay(queryUri, sparqlEndpointUri, stopRefresh);
			}

			lastDisplayedProcessingStateNumber = processingStateNumber;
			waitingForAnswer = false;
		});
	} else {
		// console.log("updateQueryState postponed");
	}
}


/**
 * 
 **/
function getQueryProcessingState(queryUri, sparqlEndpointUri, callback)
{
	var sparqlQuery = "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "SELECT ?s\n"
					+ "WHERE\n"
					+ "{\n"
					+ "  GRAPH <" + queriesNamedGraphUri + ">\n"
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
	var html = "<h2 id='matchingresultsH' class='pointer'>Matching results</h2><div id='matchingresults'></div>";
	$('#pivottomappings').append(html);
	$('#matchingresultsH').click( function() {
		var sparqlQuery = "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "SELECT * WHERE\n"
					+ "{\n"
					+ "  GRAPH <" + queriesNamedGraphUri + ">\n"
					+ "  {\n"
					+ "    <" + queryUri + "> queries:queryHasQueryElement ?qe.\n"
					+ "    ?qe queries:queryElementHasValue ?keywordValue.\n"
					+ "    ?matching queries:matchingHasKeyword ?qe;\n"
					+ "              queries:matchingHasMatchedLabel ?matchedLabel;\n"
					+ "              queries:matchingHasScore ?score;\n"
					+ "              queries:matchingHasResource ?resource.\n"
					+ "  }\n"
					+ "}\n"
					// + "GROUP BY ?keywordValue DESC(?score)";
					+ "ORDER BY ?keywordValue DESC(?score)";
		console.log(sparqlQuery);
		var callback = function(data) {
		waitingForAnswer = false;

		var table = "<table><tr><th>keyword</th><th>matched label</th><th>score</th><th>URI</th></tr>";
		var currentKeywordValue = "";

		$.each(data.results.bindings, function(i, val) {

			var keyword = ((currentKeywordValue == val.keywordValue.value)? "" : "<a href='" + val.qe.value + "'>" + val.keywordValue.value + "</a>");
			var matchedLabel = "<a href='" + val.resource.value + "' title='matched resource: " + val.resource.value + "'>" + val.matchedLabel.value + "</a>";
			var score = val.score.value;
			var uri = "<a href='" + val.matching.value + "' name='" + val.matching.value + "'>link</a>";
			table += "<tr><td>" + keyword + "</td><td>" + matchedLabel + "</td><td>" + score + "</td><td>" + uri + "</td></tr></a>";
			currentKeywordValue = val.keywordValue.value;
		});

		table += "</table>";
		table += "<p>literal matchings (if any) are not displayed</p>";
		$('#matchingresults').html(table);
		$('#matchingresultsH').click( function() {
			$('#matchingresults').toggle();
		});
	}
	waitingForAnswer = true;
	processQuery(sparqlQuery, sparqlEndpointUri, callback);
	$('#matchingresultsH').unbind("click");
	});
}

/**
 * 
 **/
function diplayElementMapping(queryUri, sparqlEndpointUri)
{
	var html = "<h2 id='elementmappingresultsH' class='pointer'>Element mapping results</h2><div id='elementmappingresults'></div>";
	$('#pivottomappings').append(html);
	$('#elementmappingresultsH').click( function() {
		var sparqlQuery = "PREFIX patterns: <" + PATTERNS_PREFIX + ">\n"
					+ "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "SELECT * WHERE\n"
					+ "{\n"
					+ "  GRAPH <" + patternsNamedGraphUri + ">\n"
					+ "  {\n"
					+ "    ?p a patterns:Pattern.\n"
					+ "    ?p patterns:patternHasPatternElement ?pe.\n"
					+ "    ?pe patterns:targets ?target.\n"
					+ "  }\n"
					+ "  GRAPH <" + queriesNamedGraphUri + ">\n"
					+ "  {\n"
					+ "    ?em queries:mappingHasQuery <" + queryUri + ">;\n"
					+ "        queries:emHasPatternElement ?pe;\n"
					+ "        queries:emHasScore ?score.\n"
					+ "    OPTIONAL{ ?em queries:emHasMatching ?m. }\n"
					+ "    OPTIONAL{ ?em queries:emHasQueryElement ?qe.\n"
					+ "              ?qe queries:queryElementHasValue ?qeValue. }\n"
					+ "    OPTIONAL{ ?em queries:emHasMatchedLabel ?label. }\n"
					+ "    OPTIONAL{ ?em (queries:hasDescriptiveSubsentence/queries:hasStringValue) ?subsent. }\n"
					+ "    OPTIONAL{ ?em queries:emHasResource ?resource. }\n"
					+ "  }\n"
					+ "}\n"
					+ "ORDER BY ?p ?pe ?em DESC(?score)";
		// console.log(sparqlQuery);
		var callback = function(data) {
			waitingForAnswer = false;
			
			var list = " <ul> "
			var currentPattern = "";
			// var lastEm = "";
					var lastPeHtml = "";
					var lastEmHtml = "";
					var lastQeHTML = "";
					var lastLabelHtml = "";
					var lastSubsentHtml = "";
					var lastMatchingHtml = "";

			$.each(data.results.bindings, function(i, val) {
				if (currentPattern != val.p.value) {
					if (currentPattern != "") {
						list += "</table>";
					}
					currentPattern = val.p.value;
					list += "<li>Pattern " + formatUriForDisplay(currentPattern) + "</li>";
					list += "<table><tr><th>pattern element</th><th>target</th><th>em link</th><th>score</th><th>subsentence</th><th>query element</th><th>match link</th><th>matched label</th><th>res link</th></tr>";
				}
					var pe = val.pe.value;
					var shortPe = pe.replace(currentPattern, "...");
					var peHtml = "<a href='" + pe + "'>" + shortPe + "</a>";
					var target = val.target.value;
					var targetHtml = "<a href='" + target + "'>" + formatUriForDisplay(target) + "</a>";
					var qeHTML = val.qeValue? "<a href='" + val.qe.value + "'>" + val.qeValue.value + "</a>" : "-";//"<i>empty mapping</i>";
					var labelHtml = val.label? val.label.value : "-";
					var resHtml = val.resource? "<a href='" + val.resource.value + "'>link</a>" : "-";
					var subsentHtml = val.subsent? val.subsent.value : "-";
					var scoreHtml = (val.score? val.score.value : "-");
					var matchingHtml = val.m? "<a href='#" + val.m.value + "'>link</a>" : "-";
					var emHtml = "<a href='" + val.em.value + "'>link</a>";
					if (peHtml == lastPeHtml) {
						peHtml = "";
						targetHtml = "";

						if (emHtml == lastEmHtml) {
							emHtml = "";
							scoreHtml = "";
							subsentHtml = "";

							if (qeHTML == lastQeHTML) {
								qeHTML = "";
							} else {
								lastQeHTML = qeHTML;
							}
							if (labelHtml == lastLabelHtml && labelHtml != "-") {
								labelHtml = "";
							} else {
								lastLabelHtml = labelHtml;
							}
							if (matchingHtml == lastMatchingHtml && matchingHtml != "-") {
								matchingHtml = "";
							} else {
								lastMatchingHtml = matchingHtml;
							}

						} else {
						lastEmHtml = emHtml;
						lastQeHTML = qeHTML;
						lastMatchingHtml = matchingHtml;
						lastLabelHtml = labelHtml;
						}
					} else {
						lastPeHtml = peHtml;
						lastEmHtml = emHtml;
						lastQeHTML = qeHTML;
						lastMatchingHtml = matchingHtml;
						lastLabelHtml = labelHtml;
					}
				// var em = val.em.value;
				// if (em == lastEm) {
				// 	var peHtml = "";
				// 	var targetHtml = "";
				// 	var qeHTML = val.qeValue? "<a href='" + val.qe.value + "'>" + val.qeValue.value + "</a>" : "-";//"<i>empty mapping</i>";
				// 	var labelHtml = "";
				// 	var subsentHtml = "";
				// 	var scoreHtml = "";
				// 	var matchingHtml = "";
				// 	var emHtml = "";
				// } else {
				// 	var pe = val.pe.value;
				// 	var shortPe = pe.replace(currentPattern, "...");
				// 	var peHtml = "<a href='" + pe + "'>" + shortPe + "</a>";
				// 	var target = val.target.value;
				// 	var targetHtml = "<a href='" + target + "'>" + formatUriForDisplay(target) + "</a>";
				// 	var qeHTML = val.qeValue? "<a href='" + val.qe.value + "'>" + val.qeValue.value + "</a>" : "-";//"<i>empty mapping</i>";
				// 	var labelHtml = (val.label? ("<a href='" + val.resource.value + "'>" + val.label.value + "</a>") : "-");
				// 	var subsentHtml = val.subsent? val.subsent.value : "-";
				// 	var scoreHtml = (val.score? val.score.value : "-");
				// 	var matchingHtml = val.m? "<a href='#" + val.m.value + "'>link</a>" : "-";
				// 	var emHtml = "<a href='" + val.em.value + "'>link</a>";
				// }
				// lastEm = em;
				list += "<tr><td>" + peHtml + "</td><td>" + targetHtml + "</td><td>" + emHtml + "</td><td>" + scoreHtml + "</td><td>" + subsentHtml + "</td><td>" + qeHTML +  "</td><td>" + matchingHtml + "</td><td>" + labelHtml + "</td><td>" + resHtml + "</td></tr>";
			});

			list += "</table>";
			list += " </ul> ";
			$('#elementmappingresults').html(list);
			$('#elementmappingresultsH').click( function() {
				$('#elementmappingresults').toggle();
			});
		}
		waitingForAnswer = true;
		processQuery(sparqlQuery, sparqlEndpointUri, callback);
		$('#elementmappingresultsH').unbind("click");
	});	
}


/**
 * 
 **/
function diplayPatternMapping(queryUri, sparqlEndpointUri)
{
	var html = "<p id='patternmappingresults'></p>"
	$('#pivottomappings').append(html);

	var sparqlQuery = "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "select ?nb where\n"
					+ "{\n"
					+ "  GRAPH <" + queriesNamedGraphUri + ">\n"
					+ "  {\n"
					+ "    <" + queryUri + "> queries:hasNumPatternMappings ?nb.\n"
					+ "  }\n"
					+ "}";
	// console.log(sparqlQuery);
	var callback = function(data) {
		waitingForAnswer = false;
		
		var html = "<b>Total number of generated pattern mappings:</b> <i>"

		$.each(data.results.bindings, function(i, val) {
			html += val.nb.value;
		});

		html += " </i> ";
		$('#patternmappingresults').html(html);
	}
	waitingForAnswer = true;
	processQuery(sparqlQuery, sparqlEndpointUri, callback);
}

/**
 * 
 **/
function diplayMappingRanking(queryUri, sparqlEndpointUri)
{
	// var html = "<h2>Mappings ranking</h2>\n";
	// $('#result #pivottomappings .mappingsranking').html(html);
}

/**
 * 
 **/
function diplayKbClearing(queryUri, sparqlEndpointUri)
{
	var html = "<p id='kbclearingresults'></p>"
	$('#pivottomappings').append(html);

	var sparqlQuery = "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "select (count(?pm) AS ?nb) where\n"
					+ "{\n"
					+ "  GRAPH <" + queriesNamedGraphUri + ">\n"
					+ "  {\n"
					+ "    ?pm a queries:PatternMapping;\n"
					+ "        queries:mappingHasQuery <" + queryUri + "> .\n"
					+ "  }\n"
					+ "}";
	// console.log(sparqlQuery);
	var callback = function(data) {
		waitingForAnswer = false;
		
		var html = "<b>Number of pattern mappings after clearing the KB:</b> <i>"

		$.each(data.results.bindings, function(i, val) {
			html += val.nb.value;
		});

		html += " </i> ";
		$('#kbclearingresults').html(html);
	}
	waitingForAnswer = true;
	processQuery(sparqlQuery, sparqlEndpointUri, callback);
}

/**
 * 
 **/
function diplayMappingsAndSentences(queryUri, sparqlEndpointUri)
{
	var html = "<h2 id='interpretationresultsH' class='pointer'>Ranked interpretations</h2><div id='interpretationresults'></div>";
	$('#pivottomappings').append(html);
	displayInterpretations(queryUri, sparqlEndpointUri, 0, 5);	
}


function displayInterpretations(queryUri, sparqlEndpointUri, offset, limit)
{
	// console.log("displayInterpretations(" +queryUri + ", " +sparqlEndpointUri + ", " +offset + ", " + limit+ ")");
	var sparqlQuery = "PREFIX patterns: <" + PATTERNS_PREFIX + ">\n"
					+ "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "SELECT * WHERE\n"
					+ "{\n"
					+ "      GRAPH <" + queriesNamedGraphUri + ">\n"
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
					+ "} ORDER BY DESC(?rmark) ?pm OFFSET " + offset + " LIMIT " + limit;
	// console.log(sparqlQuery);
	var callback = function(data) {
		waitingForAnswer = false;
		
		var html = "<span class='previous pointer'>Previous</span> | <span class='next pointer'>Next</span>";
		html += "<ol start='" + (offset+1) + "''></ol>";
		$('#interpretationresults').html(html);

		$('#interpretationresults .previous').click( function() {displayInterpretations(queryUri, sparqlEndpointUri, offset-limit, limit);});
		$('#interpretationresults .next').click( function() {displayInterpretations(queryUri, sparqlEndpointUri, offset+limit, limit);});

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

			var elemId = mappingUri.replace('urn:uuid:', '');
			var html = "<li id='" + elemId + "'>" ;
			html += "<a href='" + mappingUri + "' class='mark' title='emrmark=avgscore*factor=" + avgscore + "*" + factor + "=" + emrmark + " - qcrmark=" + qcrmark + " - pcrmark=" + nbsem + "/" + nbem + "=" + pcrmark + "'>" + rmark + "</a>"
			html += "<a href='run SPARQL query' class='descsent'>...</a>" ;
			html += "<div class='emH pointer'>Element mappings:</div>"
			html += "<div class='em'></div>"
			html += "<div class='sparqlH pointer'>SPARQL query:</div>"
			html += "</li>" ;
			$('#interpretationresults ol').append(html);
			$('#'+elemId+' .emH').click(function() {diplaysElementMappings(mappingUri, sparqlEndpointUri, elemId);});
			$('#'+elemId+' .sparqlH').click(function() {displaysSparqlQuery(mappingUri, sparqlEndpointUri, elemId);});
			displayDescriptiveSentenceForPatternMapping(mappingUri, sparqlEndpointUri);

			$('#'+elemId+' .descsent').click( function(e){
				e.preventDefault();
				alert("You must first display the SPARQL query.");
			});
		});
	}
	waitingForAnswer = true;
	processQuery(sparqlQuery, sparqlEndpointUri, callback);
}


function displayDescriptiveSentenceForPatternMapping(mappingUri, sparqlEndpointUri)
{
	var sparqlQuery = "PREFIX patterns: <" + PATTERNS_PREFIX + ">\n"
					+ "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "SELECT * WHERE\n"
					+ "{\n"
					+ "      GRAPH <" + queriesNamedGraphUri + ">\n"
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
					+ "PREFIX rdf: <" + RDF_PREFIX + ">\n"
					+ "SELECT * WHERE\n"
					+ "{\n"
					+ "      GRAPH <" + queriesNamedGraphUri + ">\n"
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
					+ "PREFIX rdf: <" + RDF_PREFIX + ">\n"
					+ "SELECT * WHERE\n"
					+ "{\n"
					+ "      GRAPH <" + queriesNamedGraphUri + ">\n"
					+ "      {\n"
					+ "        OPTIONAL {<" + nodeUri + "> queries:hasStringValue ?stringValue.}\n"
					+ "        OPTIONAL {<" + nodeUri + "> queries:dsIsQueried ?queried.}\n"
					+ "        OPTIONAL {<" + nodeUri + "> queries:isMadeUpOfList ?list.}\n"
					// + "        OPTIONAL {<" + nodeUri + "> (queries:insertForSubsentenceOf/queries:hasDescriptiveSubsentence/queries:isMadeUpOfList-for) ?listFor.}\n"
					+ "        OPTIONAL {select distinct ?listFor where\n"
					+ "          {<" + nodeUri + "> queries:insertForSubsentenceOf ?plop.\n"
					+ "           <" + parentNodeUri + "> queries:isMadeUpOfList-for ?listFor.}\n"
					+ "          }\n"
					+ "      }\n"
					+ "}\n";
	// console.log(sparqlQuery);
	var callback = function(data) {
		if (data.results.bindings.length > 0) {
			var stringValue = data.results.bindings[0].stringValue? data.results.bindings[0].stringValue.value : "";
			var queried = data.results.bindings[0].queried? data.results.bindings[0].queried.value : "";
			if (stringValue != "") {
				if (queried == "true") {
					stringValue = "<b>" + stringValue + "</b>";
				}
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


function diplaysElementMappings(mappingUri, sparqlEndpointUri, elemId)
{
	var sparqlQuery = "PREFIX patterns: <" + PATTERNS_PREFIX + ">\n"
					+ "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "PREFIX rdf: <" + RDF_PREFIX + ">\n"
					+ "SELECT * WHERE\n"
					+ "{\n"
					+ "  GRAPH <" + queriesNamedGraphUri + ">\n"
					+ "  {\n"
					+ "    <" + mappingUri + "> queries:mappingContainsMapping+ ?em.\n"
					+ "    ?em queries:emHasPatternElement ?pe;\n"
					+ "        queries:emHasScore ?score;\n"
					+ "    OPTIONAL{ ?em queries:emHasMatching ?m. }\n"
					+ "    OPTIONAL{ ?em (queries:emHasQueryElement / queries:queryElementHasValue) ?qeValue. }\n"
					+ "    OPTIONAL{ ?em queries:emHasMatchedLabel ?label. }\n"
					+ "    OPTIONAL{ ?em queries:emHasResource ?resource. }\n"
					+ "  }\n"
					+ "  GRAPH <" + patternsNamedGraphUri + ">\n"
					+ "  {\n"
					+ "    ?pe patterns:targets ?target.\n"
					+ "  }\n"
					+ "} order by ?pe\n";
	// console.log(sparqlQuery);
	var callback = function(data) {		
		var html = "<table><tr><th>pattern element</th><th>target</th><th>query element</th><th>matched label</th><th>score</th><th>element mapping</th></tr>";
		var lastEm = "";

		$.each(data.results.bindings, function(i, val) {
			
			var em = val.em.value;
			if (em == lastEm) {
				html += "<tr><td></td><td></td><td>" + (val.qeValue? val.qeValue.value : "plop") + "</td><td></td><td></td><td></td></tr>";
			} else {
				html += "<tr><td>" + formatUriForDisplay(val.pe.value) + "</td><td>" + formatUriForDisplay(val.target.value) + "</td><td>" + (val.qeValue? val.qeValue.value : "-") + "</td><td>" + (val.label? ("<a href='" + val.resource.value + "'>" + val.label.value + "</a>") : "-") + "</td><td>" + (val.score? val.score.value : "-") + "</td><td>" + "<a href='" + val.em.value + "'>lien</a></td></tr>";
			}
			lastEm = em;
		});

		html += "</table>";
		$('#' + elemId + " .em").html(html);
		$('#'+elemId+' .emH').click( function() {
			$('#'+elemId+' .em').toggle();
		});
	}
	processQuery(sparqlQuery, sparqlEndpointUri, callback);
	$('#'+elemId+' .emH').unbind("click");
}


function displaysSparqlQuery(mappingUri, sparqlEndpointUri, elemId)
{
	var sparqlId = elemId + "_sparql";

	var html = "<textarea class='sparql' id='" + sparqlId + "'></textarea>"
	$('#' + elemId).append(html);

	$('#' + elemId +' .sparqlH').unbind("click");
	$('#'+elemId+' .sparqlH').click( function() {
		// $('#'+elemId+' .sparql').toggle();
		$('#'+elemId+' .CodeMirror').toggle();
	});

	// display the SELECT/ASK content
	var q = "SELECT * WHERE\n{\n";
	// catch projection attribute (variable of the select clause)
	var sparqlQuery = "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "SELECT ?s WHERE\n"
					+ "{\n"
					+ "  GRAPH <" + queriesNamedGraphUri + ">\n"
					+ "  {\n"
					+ "    <" + mappingUri + "> queries:hasSparqlQuery ?sq.\n"
					+ "    ?sq queries:hasSelectVar ?s.\n"
					+ "  }\n"
					+ "}\n";
	// console.log(sparqlQuery);
	var callback1 = function(data) {
		if (data.results.bindings[0]) {
			q = q.replace("*", "DISTINCT " + data.results.bindings[0].s.value );
		}		
	}
	processQuery(sparqlQuery, sparqlEndpointUri, callback1);

	// check if it is a count query
	var sparqlQuery2 = "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "ASK\n"
					+ "{\n"
					+ "  GRAPH <" + queriesNamedGraphUri + ">\n"
					+ "  {\n"
					+ "    <" + mappingUri + "> (queries:mappingHasQuery/a) queries:CountPivotQuery.\n"
					+ "  }\n"
					+ "}\n";
	// console.log(sparqlQuery2);
	var callback2 = function(data) {
		if (data.boolean == true) {
			q = q.replace("*", "(COUNT(DISTINCT *) as ?count)" );
		}		
	}
	processQuery(sparqlQuery2, sparqlEndpointUri, callback2);
	
	// check if it is a ask query
	var sparqlQuery4 = "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
					+ "ASK\n"
					+ "{\n"
					+ "  GRAPH <" + queriesNamedGraphUri + ">\n"
					+ "  {\n"
					+ "    <" + mappingUri + "> (queries:mappingHasQuery/a) queries:AskPivotQuery.\n"
					+ "  }\n"
					+ "}\n";
	// console.log(sparqlQuery2);
	var callback4 = function(data) {
		if (data.boolean == true) {
			q = q.replace("SELECT * WHERE", "ASK" );
		}		
	}
	processQuery(sparqlQuery4, sparqlEndpointUri, callback4);

	// display the WHERE content
	var sparqlQuery3 = "PREFIX queries: <" + QUERIES_PREFIX + ">\n"
                	+ "PREFIX sp:  <http://spinrdf.org/sp>\n"
					+ "SELECT * WHERE\n"
					+ "{\n"
					+ "  GRAPH <" + queriesNamedGraphUri + ">\n"
					+ "  {\n"
					+ "    <" + mappingUri + "> queries:hasSparqlQuery ?sq.\n"
					+ "    ?sq queries:hasTriple ?t.\n"
                	+ "    ?t sp:object    ?o ;\n"
                	+ "       sp:predicate ?p ;\n"
                	+ "       sp:subject   ?s .\n"
					+ "  }\n"
					+ "  BIND (isIRI(?o) AS ?oIsIri)\n"
					+ "  BIND (isIRI(?p) AS ?pIsIri)\n"
					+ "  BIND (isIRI(?s) AS ?sIsIri)\n"
					+ "} ORDER BY DESC(?o)\n";
	// console.log(sparqlQuery3);
	var callback3 = function(data) {	
		$.each(data.results.bindings, function(i, val) {
			var sInQ = (val.sIsIri.value == "true"? "<" : "") + val.s.value + (val.sIsIri.value == "true"? ">" : "");
			var pInQ = (val.pIsIri.value == "true"? "<" : "") + val.p.value + (val.pIsIri.value == "true"? ">" : "");
			var oInQ = (val.oIsIri.value == "true"? "<" : (val.o.value.charAt(0)=='?'? "" : '"')) + val.o.value + (val.oIsIri.value == "true"? ">" : (val.o.value.charAt(0)=='?'? "" : '"'));
			q += "\t" + sInQ + " " + pInQ + " " + oInQ + ".\n";
		});
		q += '}';

		sparqlEditor = CodeMirror.fromTextArea(document.getElementById(sparqlId), {
		        mode: "application/x-sparql-query",
	    		tabMode: "indent",
	    		matchBrackets: true,
		        // readOnly: "true",
		      });
		sparqlEditor.setValue(q);

		$('#'+elemId+' .descsent').unbind("click");
		$('#'+elemId+' .descsent').click( function(e){
			e.preventDefault();
			// window.open("http://swip.univ-tlse2.fr:8080/musicbrainz/sparql?query=" + encodeURIComponent(sparqlEditor.getValue()) + "&output=xml&stylesheet=%2Fxml-to-html.xsl");
			window.open("http://vtentacle.techfak.uni-bielefeld.de:443/sparql?default-graph-uri=&query=" + encodeURIComponent(sparqlEditor.getValue()) + "&format=text%2Fhtml&timeout=0&debug=on");
		});
	}
	processQuery(sparqlQuery3, sparqlEndpointUri, callback3);

	
}


function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}

function formatUriForDisplay(uri) {
	for (key in usedPrefixes) {
		uri = uri.replace(usedPrefixes[key], "<b>" + key + ":</b>");
	}
	return uri;
}