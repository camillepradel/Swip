<%@ page import="org.swip.pivotToMappings.controller.Controller"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.swip.pivotToMappings.model.patterns.Pattern"%>
<%@ page import="org.json.JSONObject"%>
<%

java.util.List<Pattern> patterns = Controller.getInstance().getPatterns();
ArrayList<String> retList = new ArrayList<String>();
for(Pattern pat : patterns)
{
    retList.add(pat.toString());
}

JSONObject ret = new JSONObject();
ret.put("patterns", retList);
out.println(ret.toString());

%>