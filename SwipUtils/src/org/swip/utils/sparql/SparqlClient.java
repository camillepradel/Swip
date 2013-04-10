package org.swip.utils.sparql;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SparqlClient {

    /**
     * URI of the remote SPARQL server
     */
    private String endpointUri = null;

    public SparqlClient(String endpointUri) {
        this.endpointUri = endpointUri;
    }

    public String getEndpointUri() {
        return endpointUri;
    }

    /**
     * run a SPARQL query (select) on the remote server
     * @param queryString 
     */
    public Iterable<Map<String, String>> select(String queryString) {
        Document document = httpGetXmlContent(queryString);
        List<Map<String, String>> results = new LinkedList<Map<String, String>>();
        NodeList resultNodes = document.getElementsByTagName("result");
        for (int i = 0; i < resultNodes.getLength(); i++) {
            Node resultNode = resultNodes.item(i);
            if (resultNode != null && resultNode.getNodeType() == Node.ELEMENT_NODE) {
                Map<String, String> result = new HashMap<String, String>();
                results.add(result);
                NodeList bindingNodes = resultNode.getChildNodes();
                for (int j = 0; j < bindingNodes.getLength(); j++) {
                    Node bindingNode = bindingNodes.item(j);
                    if (bindingNode != null && bindingNode.getNodeType() == Node.ELEMENT_NODE && bindingNode.getNodeName().equals("binding")) {
                        String varName = bindingNode.getAttributes().getNamedItem("name").getTextContent();
                        String value = "";
                        NodeList bindingChildren = bindingNode.getChildNodes();
                        for (int k = 0; k < bindingChildren.getLength(); k++) {
                            Node bindingChild = bindingChildren.item(k);
                            if (bindingChild != null && bindingChild.getNodeType() == Node.ELEMENT_NODE) {
                                value = bindingChild.getTextContent();
                                break;
                            }
                        }
                        result.put(varName, value);
                    }
                }
            }
        }

        return results;
    }

    /**
     * run a SPARQL query (ask) on the remote server
     * @param queryString 
     */
    public boolean ask(String queryString) {
        Document document = httpGetXmlContent(queryString);
        NodeList nl = document.getElementsByTagName("boolean");
        Node n = nl.item(0);
        if (n != null && n.getTextContent().equals("true")) {
            return true;
        }
        return false;
    }

    private Document httpGetXmlContent(String queryString) {
        try {
            URIBuilder builder = new URIBuilder();
            if (this.endpointUri.startsWith("http://")) {
                builder.setScheme("http");
                builder.setHost(this.endpointUri.substring(7));
            } else {
                // TODO: raise exception
            }
            builder.setPath("sparql");
            builder.setParameter("query", queryString);
            builder.setParameter("output", "xml");
            URI uri = builder.build();

            DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return parser.parse(uri.toString());
        } catch (SAXException ex) {
            Logger.getLogger(SparqlClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SparqlClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(SparqlClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(SparqlClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * run a SPARQL update on the remote server
     * @param queryString 
     */
    public void update(String queryString) {
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = null;
            if (this.endpointUri.startsWith("http://")) {
                httpPost = new HttpPost(endpointUri + "update");
            } else {
                httpPost = new HttpPost(endpointUri + "update");
            }            
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("update", queryString));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            HttpResponse response2 = httpclient.execute(httpPost);

            try {
                HttpEntity entity2 = response2.getEntity();
                // do something useful with the response body
                // and ensure it is fully consumed
                EntityUtils.consume(entity2);
            } finally {
                httpPost.releaseConnection();
            }
        } catch (IOException ex) {
            Logger.getLogger(SparqlClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
