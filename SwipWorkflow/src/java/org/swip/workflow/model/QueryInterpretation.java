/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.swip.workflow.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author camille
 */
@XmlRootElement
public class QueryInterpretation {
    
   // int id=0;
    String relevanceMark = "0";
    String descriptiveSentence = null;
    String sparqlQuery = null;
    String mappingDescription = null;
    int validate = 0;

   

    public QueryInterpretation() {
    }

   
    public QueryInterpretation( String relevanceMark, String descriptiveSentence, String sparqlQuery, String mappingDescription, int validate) {
     //   this.id = id;
        this.relevanceMark = relevanceMark;
        this.descriptiveSentence = descriptiveSentence;
        this.sparqlQuery = sparqlQuery;
        this.mappingDescription = mappingDescription;
        this.validate = validate;
    }
 
//     public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
    public int getValidate() {
        return validate;
    }

    public void setValidate(int validate) {
        this.validate = validate;
    }
    public String getSparqlQuery() {
        return sparqlQuery;
    }

    public void setSparqlQuery(String SparqlQuery) {
        this.sparqlQuery = SparqlQuery;
    }

    public String getDescriptiveSentence() {
        return descriptiveSentence;
    }

    public void setDescriptiveSentence(String descriptiveSentence) {
        this.descriptiveSentence = descriptiveSentence;
    }

    public String getMappingDescription() {
        return mappingDescription;
    }

    public void setMappingDescription(String mappingDescription) {
        this.mappingDescription = mappingDescription;
    }

    public String getRelevanceMark() {
        return relevanceMark;
    }

    public void setRelevanceMark(String relevanceMark) {
        this.relevanceMark = relevanceMark;
    }   
    
}
