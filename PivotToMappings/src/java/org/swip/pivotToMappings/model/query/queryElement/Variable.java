//package org.swip.pivotToMappings.model.query.queryElement;
//
//import controller.Log;
//import sparql.SparqlServer;
//
//public class Variable extends QueryElement {
//
//    int id;
//
//    public Variable(boolean queried, int id) {
//        this.queried = queried;
//        this.id = id;
//    }
//
//    public void match(SparqlServer server, Log log) {
//        throw new UnsupportedOperationException("match not yet implemented for variables");
//    }
//
//    @Override
//    public String toString() {
//        return "Variable{" + "queried=" + queried + " - id=" + id + '}';
//    }
//
//    @Override
//    public String getStringRepresentation() {
//        return (this.queried? "?": "") + "$" + this.id;
//    }
//
//}
