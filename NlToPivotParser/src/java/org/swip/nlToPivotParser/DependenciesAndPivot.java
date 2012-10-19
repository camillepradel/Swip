package org.swip.nlToPivotParser;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DependenciesAndPivot {
    
    private String dependencyTreeStringRepresentation = null;
    private String pivotQuery = null;

    public DependenciesAndPivot(String dependencyTreeStringRepresentation, String pivotQuery) {
        this.dependencyTreeStringRepresentation = dependencyTreeStringRepresentation;
        this.pivotQuery = pivotQuery;
    }

    public String getDependencyTreeStringRepresentation() {
        return dependencyTreeStringRepresentation;
    }

    public void setDependencyTreeStringRepresentation(String dependencyTreeStringRepresentation) {
        this.dependencyTreeStringRepresentation = dependencyTreeStringRepresentation;
    }

    public String getPivotQuery() {
        return pivotQuery;
    }

    public void setPivotQuery(String pivotQuery) {
        this.pivotQuery = pivotQuery;
    }
    
}
