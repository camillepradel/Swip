////////////////////////////////////////////////////////////////
// MANAGE LAYOUT (arbor.js) AND DISPLAY (jsPlumb) OF THE LATTICE
////////////////////////////////////////////////////////////////

var animating = true;
var dragging = false;
// particle system
var sys;

var arborNodeGetsPositionOfElement = function(elementId, renderer) {
  var element = $('#' + elementId);
  var node = renderer.getNode(elementId);
  var left = parseInt(element.css('left'), 10);
  var top = parseInt(element.css('top'), 10);
  var newPt = arbor.Point(left, top);
  node.p = renderer.fromScreen(newPt);
};

var getLocalName = function(uri) {
  var regex = /(\/|#)[^\/#]*$/;
  var localName = uri.match(regex);
  return localName[0].substring(1);
};

var setNodesDraggable = function(selector) {
  jsPlumb.draggable(jsPlumb.getSelector(selector), {
      start: function(event, ui) {
          dragging = true;
      },
      stop: function(event, ui) {
          arborNodeGetsPositionOfElement(event.target.id, sys);
          dragging = false;
      }
  });
};

var detailsDisplayWhenMouseHover = function(selector) {
  // node details display when mouse hover
  $(selector).hover(
    function() {
      $('#' + $(this).attr('id') + ' .nodeDetails').show();
    }, 
    function(event) {
      $('#' + $(this).attr('id') + ' .nodeDetails').hide();
    });
};

(function($){

  var Renderer = function(canvas){
    var particleSystem;

    var that = {


      init:function(system){
        particleSystem = system
        particleSystem.screenSize(1150, 600);//canvas.width, canvas.height) 
        // particleSystem.screenPadding(80) // leave an extra 80px of whitespace per side

      },
      
      redraw:function(){

        particleSystem.eachNode(function(node, pt){
          // node: {mass:#, p:{x,y}, name:"", data:{}}
          // pt:   {x:#, y:#}  node position in screen coords

          if (animating && !dragging) {
            // maintain position on constrained axes (fixed-top and fixed-left attributes)
            var fixedtop = $('#' + node.name).attr('fixed-top');
            if (fixedtop) {
              var newPt = arbor.Point(pt.x, parseInt(fixedtop));
              var p = particleSystem.fromScreen(newPt);
              node.p = p;
              pt = newPt;
            }
            var fixedleft = $('#' + node.name).attr('fixed-left');
            if (fixedleft) {
              var newPt = arbor.Point(parseInt(fixedleft), pt.y);
              var p = particleSystem.fromScreen(newPt);
              node.p = p;
              pt = newPt;
            }

            jsPlumb.animate($("#" + node.name),{left: pt.x, top: pt.y}, {duration:0});
          }
        })
      },      
    }
    return that
  }

  
  $(document).ready(function(){
    sys = arbor.ParticleSystem(100, 600, 1.5) // create the system with sensible repulsion/stiffness/friction
    sys.parameters({gravity:true}) // use center-gravity to make the graph settle nicely (ymmv)
    sys.renderer = Renderer("#viewport") // our newly created renderer will have its .init() method called shortly by sys...
    sys.fps(15);

    // add some nodes to the graph and watch it go...
    sys.addEdge('a','b');
    //sys.addEdge('a','c');
    //sys.addEdge('a','d');
    //sys.addEdge('a','e');
    //sys.addNode('query1', {alone:true, mass:.25})
    //sys.addNode('query2', {alone:true, mass:.25})
    //sys.addNode('query3', {alone:true, mass:.25})

    // or, equivalently:
    //
    // sys.graft({
    //   nodes:{
    //     f:{alone:true, mass:.25}
    //   }, 
    //   edges:{
    //     a:{ b:{},
    //         c:{},
    //         d:{},
    //         e:{}
    //     }
    //   }
    // })

    // jsPlumb
    jsPlumb.ready(function() {

      // chrome fix.
      document.onselectstart = function () { return false; };     

      var color = "gray";

      jsPlumb.importDefaults({
        ConnectorZIndex : 5,
        Connector : [ "Bezier", { curviness:100 } ],
        DragOptions : { cursor: "pointer", zIndex:2000 },
        PaintStyle : { strokeStyle:color, lineWidth:2 },
        EndpointStyle : { radius:6, fillStyle:color },
        HoverPaintStyle : {strokeStyle:"#ec9f2e" },
        EndpointHoverStyle : {fillStyle:"#ec9f2e" },      
        Anchors :  [ "TopCenter", "BottomCenter" ]
      });
      
    
      // jsPlumb.connect({source:"a", target:"b", detachable:false});
      // jsPlumb.connect({source:"a", target:"c", detachable:false});
      // jsPlumb.connect({source:"a", target:"d", detachable:false});
      // jsPlumb.connect({source:"a", target:"e", detachable:false});
      
      // setNodesDraggable(".node");

    }); 
    
  })

})(this.jQuery)


////////////////////
// END MANAGE LAYOUT
////////////////////








$(function()
{
  // stop-start button
  var labelButtonStop = "Stop moving!";
  var labelButtonStart = "Start moving";
  $("#particleSystemOnOff").text(labelButtonStop);
  $("#particleSystemOnOff").click(function() {
    if ($("#particleSystemOnOff").text() == labelButtonStop) {
      // particleSystem.stop() doesn'work
      animating = false;
      $("#particleSystemOnOff").text(labelButtonStart);
    } else {
      // each arbor node gets the position of its correspondinf HTML element
      $.each($('.node'), function(index, value) {
        arborNodeGetsPositionOfElement(value.id, sys);
      });
      animating = true;
      $("#particleSystemOnOff").text(labelButtonStop);
    }
  });

  $('#getQueries').click(function() {
    // get sparql queries from sparql server    
    var sparqlQuery = "PREFIX sp: <http://spinrdf.org/sp#>\n"
						+ "SELECT DISTINCT *\n"
						+ "WHERE\n"
						+ "{\n"
						+ "  GRAPH <" + $('#namedgraph').val() + ">\n"
						+ "  {\n"
						+ "    ?q a sp:Select;\n"
            + "       sp:text ?text.\n"
						+ "  }\n"
						+ "}";
    // console.log(sparqlQuery);
		processQuery(sparqlQuery, $('#sparqlendpoint').val(), function(data) {
      $.each(data.results.bindings, function(i, val) {
        var queryUri = val.q.value;
        var queryText = val.text.value.replace(/</g, '&lt;').replace(/>/g, '&gt;<br />').replace(/{/g, '{<br />').replace(/\.\s/g, '.<br />');
        var queryLocalName = getLocalName(queryUri);
        var html = "<div class='sparqlQuery node' id='" + queryLocalName + "' fixed-top='550'> q" + i
                  + "<div class='nodeDetails'>"
                  + "<div class='queryUri'>" + queryUri + "</div>"
                  + "<div class='queryText'>" + queryText + "</div>"
                  + "</div>"
                  + "</div>";
        $('#viewport').prepend(html);
        // sys.addNode(queryLocalName, {alone:true, mass:.25});
        // abstract sparql query
        var sparqlQuery = "PREFIX sp: <http://spinrdf.org/sp#>\n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "SELECT DISTINCT *\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH <" + $('#namedgraph').val() + ">\n"
                + "  {\n"
                + "    <" + queryUri + "> sp:where ?where.\n"
                + "    ?where (rdf:rest*/rdf:first) ?triple.\n"
                + "    ?triple sp:subject ?s;\n"
                + "            sp:predicate ?p;\n"
                + "            sp:object ?o.\n"
                + "    ?where (rdf:rest*/rdf:first) ?tripleS.\n"
                + "    ?tripleS sp:subject ?s;\n"
                + "             sp:predicate rdf:type;\n"
                + "             sp:object ?sType.\n"
                + "    ?where (rdf:rest*/rdf:first) ?tripleO.\n"
                + "    ?tripleO sp:subject ?o;\n"
                + "             sp:predicate rdf:type;\n"
                + "             sp:object ?oType.\n"
                + "  }\n"
                + "}";
        console.log(sparqlQuery);
        processQuery(sparqlQuery, $('#sparqlendpoint').val(), function(data) {
          var htmlAbstract = "<div class='abstractedSparqlQuery node' id='" + queryLocalName + "-abstract' fixed-top='450'> abstract q" + i + "<br />"
                    + "<div class='nodeDetails'>";
          $.each(data.results.bindings, function(i, val) {
            htmlAbstract += val.sType.value + ' ' + val.p.value + ' ' + val.oType.value + '<br />';
          });
          htmlAbstract += "</div></div>";
          $('#viewport').prepend(htmlAbstract);
          console.log(htmlAbstract);
          // manage display and interaction with arborjs and jsPlumb
          sys.addEdge(queryLocalName, queryLocalName + "-abstract");
          jsPlumb.connect({source:queryLocalName, target:queryLocalName + "-abstract", detachable:false});
          setNodesDraggable(".node");
          detailsDisplayWhenMouseHover(".node");
        });
      });
	  });

  });
});





/**
 * Processes a SPARQL query
 **/
function processQuery(sparqlQuery, sparqlEndpointUri, callback)
{
    $.ajax
    ({
        type: 'GET',
        dataType: "json",
        url: sparqlEndpointUri + 'sparql',
        data: {query: sparqlQuery, output:'json'},
    })
    .done(function(data) {
      callback(data);
    })
    .fail(function(jqXHR, textStatus) {
        alert('Ajax error, please try again !');
    });
}
