package org.swip.patterns.sentence;

import java.util.List;
import org.swip.patterns.SubpatternCollection;

public class ForLoopInTemplate extends SubsentenceTemplate {
    
    SubpatternCollection spc = null;
    List<SubsentenceTemplate> sstList = null;

    public ForLoopInTemplate(SubpatternCollection spc, List<SubsentenceTemplate> sstList) {
        this.spc = spc;
        this.sstList = sstList;
    }

    public SubpatternCollection getSpc() {
        return spc;
    }

    public List<SubsentenceTemplate> getSstList() {
        return sstList;
    }

    @Override
    public String toString() {
        String result = "-for-" + this.spc.getId() + "-[";
        for (SubsentenceTemplate sst : this.sstList) {
            result += sst.toString();
        }
        result += "] ";
        return result;
    }    
}
