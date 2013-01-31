package org.swip.patterns.sentence;

import java.util.List;
import org.swip.patterns.SubpatternCollection;

/**
 * subpattern collection in template
 * @author camille
 */
public class SpcInTemplate extends SubsentenceTemplate{
    
    SubpatternCollection spc = null;
    List<SubsentenceTemplate> sstList = null;

    public SpcInTemplate(SubpatternCollection spc, List<SubsentenceTemplate> sstList) {
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
        String result = "-" + this.spc.getId() + "-[";
        for (SubsentenceTemplate sst : this.sstList) {
            result += sst.toString();
        }
        result += "] ";
        return result;
    }
}
