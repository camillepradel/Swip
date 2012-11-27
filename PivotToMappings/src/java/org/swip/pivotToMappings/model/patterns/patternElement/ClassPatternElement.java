package org.swip.pivotToMappings.model.patterns.patternElement;

import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.swip.pivotToMappings.controller.Controller;
import org.swip.pivotToMappings.model.patterns.mapping.ElementMapping;
import org.swip.pivotToMappings.model.patterns.mapping.InstanceAndClassElementMapping;
import org.swip.pivotToMappings.model.patterns.mapping.KbElementMapping;
import org.swip.pivotToMappings.model.query.Query;
import org.swip.pivotToMappings.model.query.queryElement.QueryElement;
import org.swip.pivotToMappings.model.query.subquery.Q2;
import org.swip.pivotToMappings.model.query.subquery.Subquery;
import org.swip.utils.sparql.SparqlServer;


public final class ClassPatternElement extends KbPatternElement {

    private static Logger logger = Logger.getLogger(ClassPatternElement.class);

    public ClassPatternElement() {
    }

    public ClassPatternElement(int id, String uri, boolean qualifying) {
        super(uri, qualifying);
        this.id = id;
    }

    @Override
    public String getDefaultStringForSentence(SparqlServer sparqlServer) {
        String label = sparqlServer.getALabel(this.getUri());
        return (label == null ? "no label found" : "un(e) " + label);
    }

    public void addInstanceAndClassMapping(QueryElement qeInst, QueryElement qeClass, float trustMark, String firstlyMatchedInstance, String firstlyMatchedClass, String bestLabelInstance, String bestLabelClass, ElementMapping impliedBy) {
        Controller.getInstance().addElementMappingForPatternElement(new InstanceAndClassElementMapping(this, qeInst, qeClass, trustMark, firstlyMatchedInstance, firstlyMatchedClass, bestLabelInstance, bestLabelClass, impliedBy), this);
    }

    public void checkForInstanceAndClass(Query userQuery, SparqlServer serv) {
        logger.info("checkForInstanceAndClass: " + this);
        // in two steps to prevent java.util.ConcurrentModificationException
        // step1: find out wich pairs (instance, class) must be added
        List<ElementMapping> emInstToAdd = new LinkedList<ElementMapping>();
        List<ElementMapping> emClassToAdd = new LinkedList<ElementMapping>();
        for (ElementMapping emInst : Controller.getInstance().getElementMappingsForPatternElement(this) ) {
            String firstlyMatchedInstance = ((KbElementMapping) emInst).getFirstlyMatchedOntResourceUri();
            for (ElementMapping emClass : Controller.getInstance().getElementMappingsForPatternElement(this)) {
                String firstlyMatchedClass = ((KbElementMapping) emClass).getFirstlyMatchedOntResourceUri();
                if (emInst != emClass && serv.isInstanceOfClass(firstlyMatchedInstance, firstlyMatchedClass)) {
                    String bestLabelInstance = ((KbElementMapping) emInst).getBestLabel();
                    String bestLabelClass = ((KbElementMapping) emClass).getBestLabel();
                    emInstToAdd.add(emInst);
                    emClassToAdd.add(emClass);
                }
            }
        }
        // step2: add them all to pattern element mappings list
        for (int i = 0; i < emInstToAdd.size(); i++) {
            KbElementMapping emInst = ((KbElementMapping) emInstToAdd.get(i));
            KbElementMapping emClass = ((KbElementMapping) emClassToAdd.get(i));
            String firstlyMatchedInstance = emInst.getFirstlyMatchedOntResourceUri();
            String firstlyMatchedClass = emClass.getFirstlyMatchedOntResourceUri();
            String bestLabelInstance = emInst.getBestLabel();
            String bestLabelClass = emClass.getBestLabel();
            // process trust mark
            QueryElement qeInst = emInst.getQueryElement();
            QueryElement qeClass = emClass.getQueryElement();
            float trustMark = (emInst.getTrustMark() + emClass.getTrustMark()) / (float)2.;
            if (qeInst == qeClass) {
                trustMark /= (float)2.;
            } else {
                for (Subquery sq : userQuery.getSubqueries()) {
                    if (sq instanceof Q2) {
                        QueryElement e1 = ((Q2)sq).getE1();
                        QueryElement e2 = ((Q2)sq).getE2();
                        if ( (e1==qeInst && e2 == qeClass) || (e2==qeInst && e1 == qeClass) ) {
                            trustMark *= (float)2.;
                        }
                    }
                }
            }
            this.addInstanceAndClassMapping(qeInst, qeClass, trustMark, firstlyMatchedInstance, firstlyMatchedClass, bestLabelInstance, bestLabelClass, null);
        }
    }
}
