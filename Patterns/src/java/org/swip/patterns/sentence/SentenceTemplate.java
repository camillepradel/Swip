package org.swip.patterns.sentence;

import java.util.List;

public class SentenceTemplate {
    
    List<SubsentenceTemplate> sstList = null;

    public SentenceTemplate(List<SubsentenceTemplate> sstList) {
        this.sstList = sstList;
    }

    public List<SubsentenceTemplate> getSstList() {
        return sstList;
    }

    @Override
    public String toString() {
        String result = "";
        for (SubsentenceTemplate sst : this.sstList) {
            result += sst.toString();
        }
        return result;
    }
}
