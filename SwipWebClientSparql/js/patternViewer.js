/*
 * Utilitaires
 */

// création d'un évènement lancé lorsque la valeur d'un input text est modifié
$.event.special.inputchange = {
    setup: function() {
        var self = this, val;
        $.data(this, 'timer', window.setInterval(function() {
            val = self.value;
            if ( $.data( self, 'cache') != val ) {
                $.data( self, 'cache', val );
                $( self ).trigger( 'inputchange' );
            }
        }, 20));
    },
    teardown: function() {
        window.clearInterval( $.data(this, 'timer') );
    },
    add: function() {
        $.data(this, 'cache', this.value);
    }
};

// retourne la taille d'un tableau associatif
Object.size = function(obj) {
    var size = 0, key;
    for (key in obj) {
        if (obj.hasOwnProperty(key))
			size++;
    }
    return size;
};

/*
 * Variables globales
 */
systemInit = false;
animating = true; // anime le graphe avec arbor.js (avec true comme valeur)
dragging = false; // lorsque que l'on déplace un noeud, dragging passe à true et le graphe s'immobilise
listPrefixes = {}; // liste de tous les préfixes utilisés, de la forme {uri: prefixe, ...}
listCurrentSubpatterns = {}; // liste les noeuds des sous-pattrons du patron courant
graphCopy = "";
pathName = document.location.pathname;

imageDirPath = "";
if (pathName.split('/').length == 3)
	imageDirPath = "./img/";
else
	imageDirPath = "../img/";

// prechargement des images
function preloadImage(name)
{
	var image = new Image();
	image.src = imageDirPath + name;
}

/*
 * Initialisations arbor.js
 */
edgeLength = 0.3;

var arborOpts = {
	stiffness: 900, // rigidité des arrêtes
	repulsion: 150, // répulsion des points
	friction: 0.1, // amortissement
	gravity: true, // centre de gravité
	fps: 60, // frames per second
	dt: 0.015, // laps de temps sur lequel est basé la simulation
	precision: 0.6 // rapport exactitude - rapidité
};

sys = arbor.ParticleSystem(arborOpts);

var myRenderer = {
	init: function(system){
        system.screenSize(window.innerWidth, window.innerHeight);
		systemInit = true;
	},
	
	redraw: function() {
		/* cette fonction est appelée continuellement pour mettre à jour l'affichage
		   en fonction des coordonnées des points calculées par arbor.js */
		sys.eachNode(function(node, pt){
			if (animating && !dragging) {
				var currentNode = $("#" + node.name);
				var graphNode = currentNode.parent();
				var patternID = $('#selectPattern option:selected').text();
				
				// positionnement dans le div du graphe
				var maxTop = graphNode.position().top;
				var maxBottom = maxTop + graphNode.innerHeight() - currentNode.outerHeight(false);
				var maxLeft = graphNode.position().left + parseInt(graphNode.css('marginLeft'));
				var maxRight = maxLeft + graphNode.innerWidth() - currentNode.outerWidth(false);
				
				// ajustement des coordonnées pour que les noeuds ne sortent pas de la div
				var x = (pt.x >= maxLeft)? pt.x : maxLeft;
				x = (x <= maxRight)? x : maxRight;
				
				var y = (pt.y >= maxTop)? pt.y : maxTop;
				y = (y <= maxBottom)? y : maxBottom;
				
				var newPt = arbor.Point(x, y);
				var p = sys.fromScreen(newPt);
				node.p = p;
				pt = newPt;
				
				// déplacement des noeuds (et donc des arrêtes qui lui sont reliées)
				jsPlumb.animate(currentNode,{left: pt.x, top: pt.y}, {duration:0});
			}
		});
	},
	
	resize: function(){
		// cette fonction est appelée chaque fois que la taille de la fenêtre est modifiée
		// elle met à jour la taille du particle system utilisé par arbor.js et redessine le graphe
		particleSystem.screenSize($(".graph:visible").width, $(".graph:visible").height);
		that.redraw();
	}
};

sys.renderer = myRenderer;

/*
 * Positionement des noeuds avec arbor.js
 */
var arborNodeGetsPositionOfElement = function(elementId, renderer) {
	// récupère la position de la div représentant un noeud et met à jour son
	// point correspondant dans arbor.js
	var element = $('#' + elementId);
	var node = renderer.getNode(elementId);
	var left = parseInt(element.css('left'), 10);
	var top = parseInt(element.css('top'), 10);
	var newPt = arbor.Point(left, top);
	node.p = renderer.fromScreen(newPt);
};

/*
 * Initialisation jsPlumb
 */
jsPlumb.ready(function() {
	document.onselectstart = function () { return false; }; // chrome fix 
	
	var color = "gray";
	jsPlumb.importDefaults({
		ConnectorZIndex : 5,
		Connector : [ "Bezier", { curviness:100 } ], // connexions en ligne droite
		ConnectionsDetachable: false,
		DragOptions : { cursor: "pointer", zIndex: 2000 },
		PaintStyle : { strokeStyle: color, lineWidth: 2 },
		EndpointStyle : { radius: 1, fillStyle: color }, // rends invisible les ronds aux extrémités des connexions.
		HoverPaintStyle : { strokeStyle: "#ec9f2e" },
		EndpointHoverStyle : { fillStyle: "#ec9f2e" },
		Anchors :  "Continuous", // ancre les connexions de façon optimale tout autour du noeud
		Overlays: [ ["Arrow", { width: 12,
								length: 12,
								foldback:0.5,
								location: 1 } ] ] // place une flêche à l'extrémité de la connexion
	});
});

var setNodesDraggable = function(selector) {
	// permet de rendre un noeud draggable dans la div du graphe uniquement
	var patternID = $("#selectPattern option:selected").text();
	
	jsPlumb.draggable(jsPlumb.getSelector(selector), {
		containment:"#" + patternID + " .graph",
		start: function(event, ui) {
			dragging = true;
		},
		stop: function(event, ui) {
			arborNodeGetsPositionOfElement(event.target.id, sys);
			dragging = false;
		},
		drag:function(event){
			arborNodeGetsPositionOfElement(event.target.id, sys);
		}
	});
};

/*
 *	panneau des préfixes
 */
 
// chargement de la liste des préfixes
function loadPrefixes()
{
	// fait appel à listPrefixes.php qui liste l'ensemble des préfixes sauvés dans la base de données relationnelle swip
	$.ajax
	({
        type: 'GET',
        dataType: "json",
        url: 'http://localhost/SWIP_Admin/php/listPrefixes.php'
    })
    .done(function(data) {
		listPrefixes = data;
		
		// affiche la liste des préfixes et de leur URI dans la page
		if (Object.size(listPrefixes) != 0) {
			$("#prefixes .listLabel").html("Prefixes used:");
			$.each(listPrefixes, function(uri, prefix){
				$("#prefixes .panel").append("<p class = 'prefixLine' >\n" +
									  "    <span class = 'prefix'>" + prefix + ": </span>\n" +
									  "    <span class = 'uri'>" + uri + "</span>\n" +
									  "</p>\n");
			});
		}
		$('#prefixes').trigger("prefixLoaded");
    })
    .fail(function(jqXHR) {
		alert("Error occured trying to list prefixes : " + jqXHR.responseText);
    });
}

// identifie le préfixe de l'URI passé en paramètre
function addPrefix(uri)
{
	var baseName = getBaseName(uri);
	var prefixedURI = "";
	if (listPrefixes[baseName])
		prefixedURI = listPrefixes[baseName] + ":" + uri.substring(baseName.length);
	else
		prefixedURI = uri;
	
	return prefixedURI;
}

/*
 * intitialisations de l'affichage
 */
// construit la liste de tous les patrons activés et affiche le premier
function listPatterns() {
	var sparqlQuery = "prefix patternOnt: <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
					+ "prefix xsd: <http://www.w3.org/2001/XMLSchema#>\n"
					+ "select distinct * where\n"
					+ "{\n"
					+ "  graph <" + $('#namedgraph').val() + ">\n"
					+ "  {\n"
					+ "    ?uri a patternOnt:Pattern.\n"
					+ "    ?uri patternOnt:patternHasID ?id.\n"
					+ "    ?uri patternOnt:patternIsActive 'true'^^xsd:boolean"
					+ "  }\n"
					+ "}";
	processSparqlQuery(sparqlQuery, $('#sparqlendpoint').val(), function(data) {
		$("#layout").empty();
		
		var select = "<form id = 'formSelectPattern'>\n"
				   + "    <select id = 'selectPattern'>\n"
				   + "    </select>\n"
				   + "</form>\n";
		$("#layout").append(select);
		$('#selectPattern').change(function(){ displayPattern(); });
		
		if (data.results.bindings.length == 0) {
			$('#formSelectPattern').width($('#selectPattern').outerWidth());
			alert("Any active pattern founded...");
			$("#layout").trigger("listGraphLoaded");
		} else {
			var opts = "";
			var divs = "";
			$.each(data.results.bindings, function(i, val) {
				opts += "<option value = '" + val.uri.value + "'>" + val.id.value + "</option>\n";
				divs += "<div class = 'pattern' id = '" + val.id.value + "'>"
					 +  "	<div class = 'graph'>\n"
					 +  "	</div>\n"
					 +  "   <h1>Descriptive sentence :</h1>"
					 +  "	<p class = 'sentence'>\n"
					 +  "	</p>\n"
					 +  "</div>\n";
			});
			
			$("#selectPattern").append(opts);
			$("#layout").append(divs);
			
			$('#formSelectPattern').width($('#selectPattern').outerWidth());
			$('.graph').css({
				"width": $('#graphWidth').val() + "px",
				"height": $('#graphHeight').val() + "px"
			});
			
			$("#layout").trigger("listGraphLoaded");
			
			displayPattern();
		}
	});
}

/*
 * graphe représentant le patron
 */

// affichage d'un graphe
function displayPattern() {
	var patternID = $('#selectPattern option:selected').text();
	var patternURI = $('#selectPattern option:selected').val();
	var firstDisplay = $("#" + patternID + " .graph").children().length == 0;
	
	$(".pattern").hide();
	$("#" + patternID).show();
	
	sys.prune(function(node, from, to){ return true; }); // vide tous les points définis avec arbor.js
	
	var sparqlQuery = "prefix patternOnt: <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
					+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
					+ "select distinct * where\n"
					+ "{\n"
					+ "  graph <" + $('#namedgraph').val() + ">\n"
					+ "  {\n"
					//+ "    <" + patternURI + "> (patternOnt:patternHasDirectSubpattern/patternOnt:hasDirectSubpattern*) ?triple.\n"
					+ "    <" + patternURI + "> patternOnt:patternHasTriple ?triple.\n"
					+ "    ?triple patternOnt:hasSubject ?subject;\n"
					+ "            patternOnt:hasProperty ?property;\n"
					+ "            patternOnt:hasObject ?object.\n"
					+ "    ?subject patternOnt:targetsClass ?subjectTarget.\n"
					+ "    ?property patternOnt:targetsProperty ?propertyTarget.\n"
					+ "    ?object rdf:type ?objectType;\n"
					+ "    { ?object patternOnt:targetsLiteralType ?objectTarget. }\n"
					+ "    union\n"
					+ "    { ?object patternOnt:targetsClass ?objectTarget. }\n"
					+ "  }\n"
					+ "}";
	console.log(sparqlQuery);
	processSparqlQuery(sparqlQuery, $('#sparqlendpoint').val(), function(data){
		$.each(data.results.bindings, function(i, val) {
			var subject = {
				id: getLocalName(val.subject.value),
				patternElement: val.subject.value,
				ontologyElement: val.subjectTarget.value
			}
			
			var predicate = {
				id: getLocalName(val.property.value),
				patternElement: val.property.value,
				ontologyElement: val.propertyTarget.value
			}
			
			var object = {
				id: getLocalName(val.object.value),
				patternElement: val.object.value,
				ontologyElement: val.objectTarget.value,
				type: val.objectType.value
			}
			
			createTriplet(patternID, subject, predicate, object);
		});
		
		// positionnement des noeuds (utile pour retrouver des positions d'un graphe affiché antérieurement)
		if (!systemInit)
			sys.renderer.init(sys);
		
		if (!firstDisplay){
			$("#" + patternID + " .node").each(function(i, val){
				if (sys.getNode(val.id) === undefined)
					$(val).remove();
				
				arborNodeGetsPositionOfElement(val.id, sys);
			});
		}
		
		// rend tous les noeuds du graphe draggables
		setNodesDraggable("#" + patternID + " .node");
		
		loadSubPatternStructure();
	});
}

// crée tous les éléments nécessaires à l'affichage d'un triplet RDF
function createTriplet(patternID, subject, predicate, object) {
	// tente de mettre un préfixes aux URIs utilisées
	var subjectPrefixedName = addPrefix(subject.ontologyElement);
	var predicatePrefixedName = addPrefix(predicate.ontologyElement);
	var objectPrefixedName = addPrefix(object.ontologyElement);
	
	// si aucun préfixe n'a été trouvé, on entoure l'URI complète de chevrons
	if (subjectPrefixedName == subject.ontologyElement)
		subjectPrefixedName = "&lt;" + subjectPrefixedName + "&gt;";
	
	if (predicatePrefixedName == predicate.ontologyElement)
		predicatePrefixedName = "&lt;" + predicatePrefixedName + "&gt;";
		
	if (objectPrefixedName == object.ontologyElement)
		objectPrefixedName = "&lt;" + objectPrefixedName + "&gt;";
	
	// position initiale pour les éléments créés
	var top = $("#" + patternID + " .graph").height() / 2;
	var left = $("#" + patternID + " .graph").width() / 2;
	
	// crée les éléments html correspondants au triplet passé en paramètre
	html = "";
	if ($("#" + subject.id).length == 0) {
		html += "<div class = 'node class' id = '" + subject.id + "' "
			 + "style = 'left: " + left + "px; top: " + top + "px;'>\n"
			 +  subjectPrefixedName + "\n"
			 +  "</div>\n";
	} else {
		sys.addNode(subject.id);
	}
	
	if ($("#" + object.id).length == 0) {
		if(object.type == "http://swip.univ-tlse2.fr/ontologies/Patterns#LiteralPatternElement")
			html += "<div class = 'node literal' id = '" + object.id + "' "
				 + "style = 'left: " + left + "px; top: " + top + "px;'>\n"
				 +  objectPrefixedName + "\n"
				 +  "</div>\n";
		else
			html += "<div class = 'node class' id = '" + object.id + "' "
				 + "style = 'left: " + left + "px; top: " + top + "px;'>\n"
				 +  objectPrefixedName + "\n"
				 +  "</div>\n";
	} else {
		sys.addNode(object.id);
	}
	
	$("#" + patternID + " .graph").append(html);
	
	// connecte les éléments html avec arbor.js pour le positionnement et jsPlumb pour l'affichage des connexions
	sys.addEdge(subject.id, object.id, {length: edgeLength});
	
	var exist = false
	var objConnections = jsPlumb.getAllConnections();
	$.each(objConnections, function(i, tabConnections){
		$.each(tabConnections, function(j, connection){
			var source = connection.sourceId;
			var target = connection.targetId;
			var label = connection.overlays[1].getLabel();
			if (source == subject.id && target == object.id && label == predicatePrefixedName)
				exist = true;
		});
	});
	
	if (!exist)
		jsPlumb.connect({source:subject.id,
						 target:object.id,
						 detachable:false,
						 anchor: "Continuous",
						 overlays: [["Label", { cssClass: "predicateLabel",
												label: predicatePrefixedName } ]]
						});
}

/*
 * phrase descriptive d'un patron
 */

// chargement de la structure des sous patrons et construction de la phrase descriptive
function loadSubPatternStructure()
{
	// var patternID = $('#selectPattern option:selected').text();
	var patternURI = $('#selectPattern option:selected').val();
	
	// construction structure sous patrons
	var sparqlQuery = "prefix patternOnt: <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
					+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
					+ "select distinct * where\n"
					+ "{\n"
					+ "  graph <" + $('#namedgraph').val() + ">\n"
					+ "  {\n"
					//+ "    <" + patternURI + "> patternOnt:patternHasSubpattern ?subpattern.\n"
					+ "    <" + patternURI + "> patternOnt:isMadeUpOf ?subpattern.\n"
					+ "    ?subpattern a patternOnt:Subpattern.\n"
					+ "    ?subpattern patternOnt:subpatternCollectionhasName ?name.\n"
					+ "    optional{?subpattern patternOnt:hasDeterminingElement ?detElem.}\n"
					+ "    ?subpattern patternOnt:hasDirectSubpattern* ?triple.\n"
					+ "    ?triple patternOnt:hasSubject ?subject;\n"
					+ "            patternOnt:hasObject ?object.\n"
					+ "  }\n"
					+ "}";
	processSparqlQuery(sparqlQuery, $('#sparqlendpoint').val(), function(data){
		listCurrentSubpatterns = {};
		
		$.each(data.results.bindings, function(i, val) {
			var subpatternName = val.name.value;
			var subjectID = getLocalName(val.subject.value);
			var objectID = getLocalName(val.object.value);
			
			if (listCurrentSubpatterns[subpatternName] === undefined) {// test du undefined à vérifier !!!
				listCurrentSubpatterns[subpatternName] = {
					length: 0,
					each: function(handler) {
						$.each(listCurrentSubpatterns[subpatternName], function(property, value){
							if (property != "length" && property != "each" && property != "contains") {
								handler(property, value);
							}
						});
					},
					contains: function(elementID) {
						var exist = false;
						listCurrentSubpatterns[subpatternName].each(function(i, val){
							if (elementID == val)
								exist = true;
						});
						return exist;
					}
				};
			}
			
			var i = listCurrentSubpatterns[subpatternName].length;
			
			if (!listCurrentSubpatterns[subpatternName].contains(subjectID)) {
				listCurrentSubpatterns[subpatternName][i] = getLocalName(subjectID);
				i = listCurrentSubpatterns[subpatternName].length += 1;
			}
			
			if (!listCurrentSubpatterns[subpatternName].contains(objectID)) {
				listCurrentSubpatterns[subpatternName][i] = objectID;
				listCurrentSubpatterns[subpatternName].length += 1;
			}
		});
		
		loadSentence();
	});
}
 
// affichage de la phrase descriptive du patron
function loadSentence()
{
	var patternID = $('#selectPattern option:selected').text();
	var patternURI = $('#selectPattern option:selected').val();
	var sparqlQuery = "prefix patternOnt: <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
					+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
					+ "select distinct * where\n"
					+ "{\n"
					+ "  graph <" + $('#namedgraph').val() + ">\n"
					+ "  {\n"
					+ "    <" + patternURI + "> patternOnt:hasSentenceTemplateString ?sentence."
					+ "  }\n"
					+ "}";
	processSparqlQuery(sparqlQuery, $('#sparqlendpoint').val(), function(data){
		var sentenceString = data.results.bindings[0].sentence.value;
		var sentence = wrapSentence(sentenceString);
		$("#" + patternID + " .sentence").empty().append(sentence);
		bindSentenceElements();
		
		// lève un évènement qui signale que le graphe est chargé (récupéré par
		// admin.js lorsque l'utilisateur est connecté en tant qu'administrateur)
		$("#" + patternID).trigger("graphLoaded");
	});
}
 
// structure la phrase descriptive avec des balises span correspondant aux éléments ou aux sous-patrons
function wrapSentence(sentence)
{
	var nodesRefRegex = /(\-\d+\-)/g;
	sentence = sentence.replace(nodesRefRegex, "<span class = 'nodeRef'>$1</span>");
	
	var subPatternRefRegex = /(\-[a-zA-Z_-]+\-)/g;
	sentence = sentence.replace(subPatternRefRegex, "<span class = 'subPatternRef'>$1</span>");
	//console.log(matchRecursive(sentence, '[...]'));
	var stringRegex = /("[^"]+")/g;
	sentence = sentence.replace(stringRegex, "<span class = 'string'>$1</span>");
	
	return sentence;
}

// lie les éléments de la phrase avec les éléments du graphe
function bindSentenceElements()
{
	// liaison des références aux noeuds
	$(".sentence:visible .nodeRef").each(function(i, ref){
		var elementID = ref.innerHTML.replace(/^\-(\d+)\-$/, "$1");
		var patternID = $('#selectPattern option:selected').text();
		var targetID = patternID + "_element" + elementID;
		
		$(ref).hover(function(){
			if ($("#" + targetID).hasClass("literal")) {
				$("#" + targetID).addClass('literalSelected');
			} else {
				$("#" + targetID).addClass('classSelected');
			}
		}, function(){
			if ($("#" + targetID).hasClass("literal")) {
				$("#" + targetID).removeClass('literalSelected');
			} else {
				$("#" + targetID).removeClass('classSelected');
			}
		})
		.html($("#" + targetID).html());
	});
	
	// liaison des références aux sous-patrons
	$(".subPatternRef").each(function (i, ref){
		var subpatternName = ref.innerHTML.replace(/^\-(for\-)?([a-zA-Z_]+)\-$/, "$2");
		$(ref).hover(function(){
			if (listCurrentSubpatterns[subpatternName] != undefined){
				listCurrentSubpatterns[subpatternName].each(function(i, targetID){
					if ($("#" + targetID).hasClass("literal")) {
						$("#" + targetID).addClass("subPatternLiteralSelected");
					} else {
						$("#" + targetID).addClass("subPatternClassSelected");
					}
				});
			}
		}, function(){
			if (listCurrentSubpatterns[subpatternName] != undefined){
				listCurrentSubpatterns[subpatternName].each(function(i, targetID){
					if ($("#" + targetID).hasClass("literal")) {
						$("#" + targetID).removeClass("subPatternLiteralSelected");
					} else {
						$("#" + targetID).removeClass("subPatternClassSelected");
					}
				});
			}
		});
	});
}

$(function(){
	/*
	 * panneau de configurations
	 */
	
	// gestion de la taille du graphe
	var defaultWidth = $(window).width() - 80;
	$('#graphWidth').val(defaultWidth)
		.on('mousewheel', function(event){
			var delta = event.originalEvent.wheelDelta;
			var newVal = 0;
			if (delta > 0) {
				newVal = parseInt($('#graphWidth').val()) + 10;
			} else {
				newVal = parseInt($('#graphWidth').val()) - 10
				newVal = (newVal < 300)? 300 : newVal;
			}
			$('#graphWidth').val(newVal);
		})
		.on('inputchange', function(){
			$('.graph').css({ "width" : $('#graphWidth').val() });
			if (parseInt($('#graphWidth').val()) > defaultWidth - 20)
				$('.graph').css({ "marginLeft" : "29px" });
			else
				$('.graph').css({ "marginLeft" : "auto" });
		})
		.hover(function() {
			 $("body").css("overflow","hidden");
		}, function() {
			$("body").css("overflow","auto");
		});
	
	var defaultHeight = Math.floor(( 70 * $(window).height() ) / 100);
	$('#graphHeight').val(defaultHeight)
		.on('mousewheel', function(event){
			var delta = event.originalEvent.wheelDelta;
			var newVal = 0;
			if (delta > 0) {
				newVal = parseInt($('#graphHeight').val()) + 10;
			} else {
				newVal = parseInt($('#graphHeight').val()) - 10
				newVal = (newVal < 300)? 300 : newVal;
			}
			$('#graphHeight').val(newVal);
		})
		.on('inputchange', function(){
			$('.graph').css({ "height" : $('#graphHeight').val() });
		})
		.hover(function() {
			 $("body").css("overflow","hidden");
		}, function() {
			$("body").css("overflow","auto");
		});
	
	// liste automatique des noms de graphes à utiliser selon l'endpoint entré
	$('#namedgraph').click(function(){
		var sparqlQuery = "prefix patternOnt: <http://swip.univ-tlse2.fr/ontologies/Patterns#>"
						+ "select distinct ?graphName where\n"
						+ "{\n"
						+ "  graph ?graphName\n"
						+ "  {\n"
						+ "    select * where\n"
						+ "    {\n"
						+ "      ?a a patternOnt:Pattern.\n"
						+ "    }\n"
						+ "  }\n"
						+ "}\n";
		processSparqlQuery(sparqlQuery, $('#sparqlendpoint').val(), function(data){
			var selectedElem = $('#namedgraph option:selected').val();
			$('#namedgraph').empty();
			$.each(data.results.bindings, function(i, val) {
				if (selectedElem == val.graphName.value)
					$('#namedgraph').prepend("<option selected='selected'>" +
												val.graphName.value +
											 "</option>");
				else
					$('#namedgraph').prepend("<option>" +
												val.graphName.value +
											 "</option>");
			});
		});
	});
	
	// bouton de chargement des patrons
	$('#load').click(function(event){
		$('#configuration .showHideButton').click();
		listPatterns();
		$("#toggleMoving").show();
	});
	
	/*
	 * panneau des préfixes
	 */
	loadPrefixes();
	
	/*
	 * présentation des patrons
	 */
	
	// préchargement des ellipses
	preloadImage("ellipse_hover.png");
	preloadImage("ellipse_green.png");
	preloadImage("ellipse_blue.png");
	
	// bouton de lancement et d'arrêt du déplacement des noeuds dans le graphe
	$("#toggleMoving").click(function() {
		var labelButtonStop = "Stop moving";
		var labelButtonStart = "Start moving";
		
		if ($("#toggleMoving").text() == labelButtonStop) {
			animating = false;
			$("#toggleMoving").text(labelButtonStart);
		} else {
			var patternID = $('#selectPattern option:selected').text();
			$.each($('#' + patternID + '.node'), function(index, value) {
				arborNodeGetsPositionOfElement(value.id, sys);
			});
			animating = true;
			$("#toggleMoving").text(labelButtonStop);
		}
	});
	
	/*
	 * premier affichage
	 */
	$('#configuration .showHideButton').click();
});