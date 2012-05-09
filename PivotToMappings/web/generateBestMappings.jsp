<%@ page import="org.swip.pivotToMappings.controller.Controller"%>
<%@ page import="org.swip.pivotToMappings.model.patterns.mapping.PatternToQueryMapping"%>

<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Collections"%>
<%@ page import="org.json.JSONObject"%>

<%

String pivotQueryString = request.getParameter("pivotQueryString");
int numMappings = Integer.parseInt(request.getParameter("numMappings"));

JSONObject rep = new JSONObject();

if(numMappings > 50) numMappings = 50;  // avoid to request the server with an infinite result
List<PatternToQueryMapping> bestMappings = Controller.getInstance().getBestMappings(pivotQueryString, numMappings);

if(bestMappings != null)
{
    Collections.reverse(bestMappings);
        
        ArrayList<JSONObject> queryResults = new ArrayList();
        
        for(PatternToQueryMapping ptqm : bestMappings)
        {
            JSONObject query = new JSONObject();
            query.put("descriptiveSentence", ptqm.getSentence());
            query.put("mappingDescription", ptqm.getStringDescription());
            query.put("relevanceMark", String.valueOf(ptqm.getRelevanceMark()));
            query.put("sparqlQuery", ptqm.getSparqlQuery());
            //query.put("validate", 0);

            queryResults.add(query);
        }
        
        rep.put("content", queryResults);
}

out.println(rep.toString());

%>