package org.swip.patterns;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class Controller {

    private static final Logger logger = Logger.getLogger(Controller.class);
    static Controller staticController = null;
    private Map<Pattern, List<PatternElement>> patternElements = new HashMap<Pattern, List<PatternElement>>();
    
    public Controller() {
    }

    public static Controller getInstance() {
        if (staticController == null) {
            staticController = new Controller();
        }
        return staticController;
    }

    public List<PatternElement> getPatternElementsForPattern(Pattern p) {
        return this.patternElements.get(p);
    }

    public void setPatternElementsForPattern(List<PatternElement> pes, Pattern p) {
        this.patternElements.put(p, pes);
    }
}
