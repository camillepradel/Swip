package org.swip.pivotToMappings.model.query.queryElement;

import org.apache.log4j.Logger;
import org.swip.pivotToMappings.model.query.antlr.QueryElementException;

public abstract class QueryElement {
    
    private static final Logger logger = Logger.getLogger(QueryElement.class);

//    final static float trustMarkIncWhenEqualsWithoutStemming = (float) 2;
//    final static float trustMarkIncWhenEqualsWithStemming = (float) 1.6;
//    final static float trustMarkDiminutionWhenIncompatible = (float) 0.65;
//    final static float trustMarkDiminutionWhenInstance = (float) 0.95;
//    final static float trustMarkDiminutionWhenQueriedInstance = (float) 0.6;


    boolean queried = false;
    QeRoles roles = new QeRoles();

    
    protected QueryElement()
    {}
    
    public void addRole(QeRole role) throws QueryElementException {
        try {
            this.roles.addRole(role);
        } catch (QueryElementException ex) {
            logger.error(ex);
            throw new QueryElementException("role of \"" + this.toString() + "\" doesn't match with previous occurence(s).\n"
                                + "change keyword from one or use different id");
        }
    }

    public QeRoles getRoles() {
        return roles;
    }

    public boolean isQueried() {
        return this.queried;
    }

//    public abstract String getVarName();
        
    public abstract String getStringValue();
    public abstract String getStringForQueryUri();
    public abstract String getStringUri(String queryUri, String queriesNamedGraphUri);
}
