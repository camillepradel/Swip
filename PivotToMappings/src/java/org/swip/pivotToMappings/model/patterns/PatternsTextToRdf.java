/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.swip.pivotToMappings.model.patterns;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.log4j.Logger;
import org.swip.pivotToMappings.exceptions.LexicalErrorRuntimeException;
import org.swip.pivotToMappings.exceptions.PatternsParsingException;
import org.swip.pivotToMappings.exceptions.SyntaxErrorRuntimeException;
import org.swip.pivotToMappings.model.patterns.antlr.patternsDefinitionGrammarLexer;
import org.swip.pivotToMappings.model.patterns.antlr.patternsDefinitionGrammarParser;
import org.swip.pivotToMappings.model.patterns.patternElement.ClassPatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.KbPatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.LiteralPatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.PatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.PropertyPatternElement;
import org.swip.pivotToMappings.model.patterns.subpattern.PatternTriple;
import org.swip.pivotToMappings.model.patterns.subpattern.Subpattern;
import org.swip.pivotToMappings.model.patterns.subpattern.SubpatternCollection;

/**
 *
 * @author camille
 */
public class PatternsTextToRdf {

    private static final Logger logger = Logger.getLogger(PatternsTextToRdf.class);
    
    static InputStream in = null;
    
    static Model model = null;
    static String uriStart = null;
    // classes
    static Resource patternClass = null;
    static Resource patternTripleClass = null;
    static Resource subPatternCollectionClass = null;
    static Resource classPatternElementClass = null;
    static Resource literalPatternElementClass = null;
    static Resource propertyPatternElementClass = null;
    // object properties
    static Property isPatternMadeUpOfProp = null;
    static Property isMadeUpOfProp = null;
    static Property hasSubjectProp = null;
    static Property hasPropertyProp = null;
    static Property hasObjectProp = null;
    static Property targetsClassProp = null;
    static Property targetsPropertyProp = null;
    static Property targetsLiteralTypeProp = null;
    static Property patternHasAuthorProp = null;
    static Property refersToPatternElementProp = null;
    static Property hasDeterminingElementProp = null;
    // data properties
    static Property hasCardMinProp = null;
    static Property hasCardMaxProp = null;
    static Property hasSentenceTemplateProp = null;
    static Property isQualifyingProp = null;
    static Property subpatternCollectionhasNameProp = null;
    static Property patternElementHasIdProp = null;
    static int tripleCount = 1;

    static public String patternsTextToRdf(String setName, String authorUri, String patternsText) {
        List<Pattern> l = new LinkedList<Pattern>();
        try {
            logger.info("Loading patterns:");
            logger.info("----------------\n");
            long time = System.currentTimeMillis();
            // read patterns on file and instantiate them
            try {
                ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(patternsText.getBytes()));
                patternsDefinitionGrammarLexer lexer = new patternsDefinitionGrammarLexer(input);
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                patternsDefinitionGrammarParser parser = new patternsDefinitionGrammarParser(tokens);
                l = parser.patterns();
            } catch (RecognitionException ex) {
                logger.error(ex);
                throw new PatternsParsingException("RecognitionException: " + ex.getMessage());
            } catch (IOException ex) {
                logger.error(ex);
                throw new PatternsParsingException("IOException: " + ex.getMessage());
            } catch (SyntaxErrorRuntimeException ex) {
                logger.error(ex);
                throw new PatternsParsingException("Syntax error at " + ex.getMessage());
            } catch (LexicalErrorRuntimeException ex) {
                logger.error(ex);
                throw new PatternsParsingException("Syntax error at " + ex.getMessage());
            } catch (RuntimeException ex) {
                logger.error(ex);
                throw new PatternsParsingException(ex.getMessage());
            }
            // display loaded patterns
//            logger.info("Patterns:\n");
//            for (Pattern pattern : l) {
//                logger.info(pattern.toString());
//            }
            logger.info("Pattern loaded");
            logger.info("time for loading patterns: " + (System.currentTimeMillis() - time) + "ms");
            logger.info("================================================================");


            logger.info("Translating patterns:");
            logger.info("--------------------\n");
            model = ModelFactory.createDefaultModel();
 
            in = PatternsTextToRdf.class.getClassLoader().getResourceAsStream("SwipOntology.owl");
            if (in == null) {
                logger.error("SWIP ONTOLOGY PATH ERROR!!");
            }
            // read the RDF/XML file
            model.read(in, null);            
            
            uriStart = "http://swip.alwaysdata.net/patterns/" + setName + "#";
            patternClass = model.createResource("http://swip.alwaysdata.net/ontologies/SwipOntology#Pattern");
            patternTripleClass = model.createResource("http://swip.alwaysdata.net/ontologies/SwipOntology#PatternTriple");
            subPatternCollectionClass = model.createResource("http://swip.alwaysdata.net/ontologies/SwipOntology#SubpatternCollection");
            classPatternElementClass = model.createResource("http://swip.alwaysdata.net/ontologies/SwipOntology#ClassPatternElement");
            propertyPatternElementClass = model.createResource("http://swip.alwaysdata.net/ontologies/SwipOntology#PropertyPatternElement");
            literalPatternElementClass = model.createResource("http://swip.alwaysdata.net/ontologies/SwipOntology#LiteralPatternElement");
            hasSentenceTemplateProp = model.createProperty("http://swip.alwaysdata.net/ontologies/SwipOntology#hasSentenceTemplate");
            isPatternMadeUpOfProp = model.createProperty("http://swip.alwaysdata.net/ontologies/SwipOntology#isPatternMadeUpOf");
            isMadeUpOfProp = model.createProperty("http://swip.alwaysdata.net/ontologies/SwipOntology#isMadeUpOf");
            hasSubjectProp = model.createProperty("http://swip.alwaysdata.net/ontologies/SwipOntology#hasSubject");
            hasPropertyProp = model.createProperty("http://swip.alwaysdata.net/ontologies/SwipOntology#hasProperty");
            hasObjectProp = model.createProperty("http://swip.alwaysdata.net/ontologies/SwipOntology#hasObject");
            hasCardMinProp = model.createProperty("http://swip.alwaysdata.net/ontologies/SwipOntology#hasCardinalityMin");
            hasCardMaxProp = model.createProperty("http://swip.alwaysdata.net/ontologies/SwipOntology#hasCardinalityMax");
            targetsClassProp = model.createProperty("http://swip.alwaysdata.net/ontologies/SwipOntology#targetsClass");
            targetsPropertyProp = model.createProperty("http://swip.alwaysdata.net/ontologies/SwipOntology#targetsProperty");
            targetsLiteralTypeProp = model.createProperty("http://swip.alwaysdata.net/ontologies/SwipOntology#targetsLiteralType");
            isQualifyingProp = model.createProperty("http://swip.alwaysdata.net/ontologies/SwipOntology#isQualifying");
            subpatternCollectionhasNameProp = model.createProperty("http://swip.alwaysdata.net/ontologies/SwipOntology#subpatternCollectionhasName");
            patternHasAuthorProp = model.createProperty("http://swip.alwaysdata.net/ontologies/SwipOntology#patternHasAuthor");
            refersToPatternElementProp = model.createProperty("http://swip.alwaysdata.net/ontologies/SwipOntology#refersToPatternElement");
            hasDeterminingElementProp = model.createProperty("http://swip.alwaysdata.net/ontologies/SwipOntology#hasDeterminingElement");
            patternElementHasIdProp = model.createProperty("http://swip.alwaysdata.net/ontologies/SwipOntology#patternElementHasId");
            
            Resource authorResource = model.createResource(authorUri);
            for (Pattern p : l) {
                logger.info("pattern: " + p.getName());
                tripleCount = 1;
                Resource patternResource = model.createResource(uriStart + p.getName(), patternClass);
                patternResource.addProperty(patternHasAuthorProp, authorResource);
                patternResource.addLiteral(hasSentenceTemplateProp, model.createLiteral(p.getSentenceTemplate()));
                for (Subpattern sp : p.getSubpatterns()) {
                    logger.info("  - subpattern: " + ((SubpatternCollection)sp).getId());
                    patternResource.addProperty(isPatternMadeUpOfProp, processSubpattern(sp, p));
                }
            }
            OutputStream os = new ByteArrayOutputStream();
            model.write(os);
            return os.toString();



        } catch (PatternsParsingException ex) {
            logger.info("An error occured while parsing patterns:\n" + ex.getMessage());
            logger.info("Patterns loading aborted");
            logger.error(ex);
        }
        return "error";
    }

    private static Resource processSubpattern(Subpattern sp, Pattern pattern) {
        Resource result = null;
        String patternName = pattern.getName();
        if (sp instanceof SubpatternCollection) {
            SubpatternCollection spc = (SubpatternCollection)sp;
            result = model.createResource(uriStart + patternName + "_" + spc.getId(), subPatternCollectionClass);
            result.addLiteral(subpatternCollectionhasNameProp, spc.getId());
            result.addLiteral(hasCardMinProp, spc.getMinOccurrences())
                    .addLiteral(hasCardMaxProp, spc.getMaxOccurrences());
            for (Subpattern spIn : spc.getSubpatterns()) {
                result.addProperty(isMadeUpOfProp, processSubpattern(spIn, pattern));
            }
            result.addProperty(hasDeterminingElementProp,
                    model.createResource(generatePatternElementUri(spc.getPivotElement(), pattern)));
        } else if (sp instanceof PatternTriple) {
            PatternTriple pt = (PatternTriple)sp;
            result = model.createResource(uriStart + patternName + "_triple" + tripleCount++, patternTripleClass);
            result.addProperty(hasSubjectProp, processPatternElement(pt.getE1(), pattern));
            result.addProperty(hasPropertyProp, processPatternElement(pt.getE2(), pattern));
            result.addProperty(hasObjectProp, processPatternElement(pt.getE3(), pattern));
        }
        return result;
    }
    
    private static Resource processPatternElement(PatternElement pe, Pattern pattern) {
        Resource result = null;
        if (pe instanceof ClassPatternElement) {
            ClassPatternElement cpe = (ClassPatternElement)pe;
            result = model.createResource(generatePatternElementUri(pe, pattern), classPatternElementClass)
                    .addProperty(targetsClassProp, model.createResource(cpe.getUri()))
                    .addLiteral(isQualifyingProp, cpe.isQualifying());
        } else if (pe instanceof PropertyPatternElement) {
            PropertyPatternElement ppe = (PropertyPatternElement)pe;
            result = model.createResource(generatePatternElementUri(pe, pattern), propertyPatternElementClass)
                    .addProperty(targetsPropertyProp, model.createResource(ppe.getUri()))
                    .addLiteral(isQualifyingProp, ppe.isQualifying());
            for (Integer referedElement : ppe.getReferedElements()) {
                result.addProperty(refersToPatternElementProp,
                        model.createResource(generatePatternElementUri(pattern.getPatternElementById(referedElement), pattern)));
            }
        } else if (pe instanceof LiteralPatternElement) {
            LiteralPatternElement lpe = (LiteralPatternElement)pe;
            result = model.createResource(generatePatternElementUri(pe, pattern), literalPatternElementClass)
                    .addProperty(targetsLiteralTypeProp, model.createResource(lpe.getType()))
                    .addLiteral(isQualifyingProp, lpe.isQualifying());
        } else {
            return null;
        }
        result.addLiteral(patternElementHasIdProp, pe.getId());
//        if (pe.getMappingCondition() != null) {
//            
//        }
        
        return result;
    }
    
    private static String generatePatternElementUri(PatternElement sp, Pattern pattern) {
        logger.info(pattern.getName());
        logger.info(sp.getId());
        return uriStart + pattern.getName() + "_element" + sp.getId();
    }
}
