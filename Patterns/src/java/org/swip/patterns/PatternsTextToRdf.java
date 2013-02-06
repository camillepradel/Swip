package org.swip.patterns;

import org.swip.patterns.antlr.LexicalErrorRuntimeException;
import org.swip.patterns.antlr.patternsDefinitionGrammarParser;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.log4j.Logger;
import org.swip.patterns.antlr.patternsDefinitionGrammarLexer;
import org.swip.patterns.sentence.ForLoopInTemplate;
import org.swip.patterns.sentence.PeInTemplate;
import org.swip.patterns.sentence.SpcInTemplate;
import org.swip.patterns.sentence.StaticStringInTemplate;
import org.swip.patterns.sentence.SubsentenceTemplate;

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
    static Resource blankNodePatternElementClass = null;
    static Resource sentenceTemplateClass = null;
    static Resource peInTemplateClass = null;
    static Resource forLoopInTemplateClass = null;
    static Resource spcInTemplateClass = null;
    static Resource staticStringInTemplateClass = null;
    static Resource propertyPatternElementClass = null;
    static Resource listClass = null;
    // object properties
    static Property isPatternMadeUpOfProp = null;
    static Property patternHasSubpatternProp = null;
    static Property hasDirectSubpatternProp = null;
    static Property patternHasDirectSubpatternProp = null;
    static Property patternHasPatternElementProp = null;
    static Property isMadeUpOfProp = null;
    static Property isMadeUpOfListProp = null;
    static Property hasSubjectProp = null;
    static Property hasPropertyProp = null;
    static Property hasObjectProp = null;
    static Property targetsClassProp = null;
    static Property targetsPropertyProp = null;
    static Property targetsLiteralTypeProp = null;
    static Property patternHasAuthorProp = null;
    static Property targetsOntologyProp = null;
    static Property refersToPatternElementProp = null;
    static Property hasDeterminingElementProp = null;
    static Property hasSentenceTemplateProp = null;
    static Property sstTargetsPcProp = null;
    static Property rdfFirstProp = null;
    static Property rdfRestProp = null;
    // data properties
    static Property hasCardMinProp = null;
    static Property hasCardMaxProp = null;
    static Property isQualifyingProp = null;
    static Property subpatternCollectionhasNameProp = null;
    static Property patternElementHasIdProp = null;
    static Property hasMappingConditionsProp = null;
    static Property ssitHasValueProp = null;
    static Property hasSentenceTemplateStringProp = null;
    static int tripleCount = 1;
    static int staticStringCount = 1;
    static int listCount = 0;

    static public String patternsTextToRdf(String setName, String authorUri, String ontologyUri, String patternsText) {
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
            logger.info("Patterns:\n");
            for (Pattern pattern : l) {
                logger.info(pattern.toString());
            }
            logger.info("Pattern loaded");
            logger.info("time for loading patterns: " + (System.currentTimeMillis() - time) + "ms");
            logger.info("================================================================");


            logger.info("Translating patterns:");
            logger.info("--------------------\n");
            model = ModelFactory.createDefaultModel();

            in = PatternsTextToRdf.class.getClassLoader().getResourceAsStream("SwipPatterns.owl");
            if (in == null) {
                logger.error("SWIP ONTOLOGY PATH ERROR!!");
            }
            // read the RDF/XML file
            model.read(in, null);

            uriStart = "http://swip.univ-tlse2.fr/patterns/" + setName + "#";
            String patternOntologyUri = "http://swip.univ-tlse2.fr/ontologies/Patterns";
            patternClass = model.createResource(patternOntologyUri + "#Pattern");
            patternTripleClass = model.createResource(patternOntologyUri + "#PatternTriple");
            subPatternCollectionClass = model.createResource(patternOntologyUri + "#SubpatternCollection");
            classPatternElementClass = model.createResource(patternOntologyUri + "#ClassPatternElement");
            propertyPatternElementClass = model.createResource(patternOntologyUri + "#PropertyPatternElement");
            listClass = model.createResource("http://www.w3.org/1999/02/22-rdf-syntax-ns#List");
            literalPatternElementClass = model.createResource(patternOntologyUri + "#LiteralPatternElement");
            blankNodePatternElementClass = model.createResource(patternOntologyUri + "#BlankNodePatternElement");
            sentenceTemplateClass = model.createResource(patternOntologyUri + "#SentenceTemplate");
            peInTemplateClass = model.createResource(patternOntologyUri + "#PeInTemplate");
            forLoopInTemplateClass = model.createResource(patternOntologyUri + "#ForLoopInTemplate");
            spcInTemplateClass = model.createResource(patternOntologyUri + "#SpcInTemplate");
            staticStringInTemplateClass = model.createResource(patternOntologyUri + "#StaticStringInTemplate");
            hasSentenceTemplateProp = model.createProperty(patternOntologyUri + "#hasSentenceTemplate");
            hasSentenceTemplateStringProp = model.createProperty(patternOntologyUri + "#hasSentenceTemplateString");
            sstTargetsPcProp = model.createProperty(patternOntologyUri + "#sstTargetsPc");
            rdfFirstProp = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#first");
            rdfRestProp = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#rest");
            hasMappingConditionsProp = model.createProperty(patternOntologyUri + "#hasMappingConditions");
            ssitHasValueProp = model.createProperty(patternOntologyUri + "#ssitHasValue");
            isPatternMadeUpOfProp = model.createProperty(patternOntologyUri + "#isPatternMadeUpOf");
            patternHasSubpatternProp = model.createProperty(patternOntologyUri + "#patternHasSubpattern");
            hasDirectSubpatternProp = model.createProperty(patternOntologyUri + "#hasDirectSubpattern");
            patternHasDirectSubpatternProp = model.createProperty(patternOntologyUri + "#patternHasDirectSubpattern");
            patternHasPatternElementProp = model.createProperty(patternOntologyUri + "#patternHasPatternElement");
            isMadeUpOfProp = model.createProperty(patternOntologyUri + "#isMadeUpOf");
            isMadeUpOfListProp = model.createProperty(patternOntologyUri + "#isMadeUpOfList");
            hasSubjectProp = model.createProperty(patternOntologyUri + "#hasSubject");
            hasPropertyProp = model.createProperty(patternOntologyUri + "#hasProperty");
            hasObjectProp = model.createProperty(patternOntologyUri + "#hasObject");
            hasCardMinProp = model.createProperty(patternOntologyUri + "#hasCardinalityMin");
            hasCardMaxProp = model.createProperty(patternOntologyUri + "#hasCardinalityMax");
            targetsClassProp = model.createProperty(patternOntologyUri + "#targetsClass");
            targetsPropertyProp = model.createProperty(patternOntologyUri + "#targetsProperty");
            targetsLiteralTypeProp = model.createProperty(patternOntologyUri + "#targetsLiteralType");
            isQualifyingProp = model.createProperty(patternOntologyUri + "#isQualifying");
            subpatternCollectionhasNameProp = model.createProperty(patternOntologyUri + "#subpatternCollectionhasName");
            patternHasAuthorProp = model.createProperty(patternOntologyUri + "#patternHasAuthor");
            targetsOntologyProp = model.createProperty(patternOntologyUri + "#targetsOntology");
            refersToPatternElementProp = model.createProperty(patternOntologyUri + "#refersToPatternElement");
            hasDeterminingElementProp = model.createProperty(patternOntologyUri + "#hasDeterminingElement");
            patternElementHasIdProp = model.createProperty(patternOntologyUri + "#patternElementHasId");

            Resource authorResource = model.createResource(authorUri);
            logger.info("authorUri: " + authorUri);
            Resource ontologyResource = model.createResource(ontologyUri);
            logger.info("ontologyUri: " + ontologyUri);
            for (Pattern p : l) {
                logger.info("pattern: " + p.getName());
                tripleCount = 1;
                Resource patternResource = model.createResource(uriStart + p.getName(), patternClass);
                patternResource.addProperty(patternHasAuthorProp, authorResource);
                patternResource.addProperty(targetsOntologyProp, ontologyResource);
                patternResource.addProperty(hasSentenceTemplateProp, processSentenceTemplate(p, patternResource));
                patternResource.addLiteral(hasSentenceTemplateStringProp, p.getSentenceTemplate().toString());
                for (Subpattern sp : p.getSubpatterns()) {
                    logger.info("  - subpattern: " + ((SubpatternCollection) sp).getId());
                    patternResource.addProperty(patternHasDirectSubpatternProp, processSubpattern(sp, p, patternResource));
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

    private static Resource processSubpattern(Subpattern sp, Pattern pattern, Resource patternResource) {
        Resource result = null;
        String patternName = pattern.getName();
        if (sp instanceof SubpatternCollection) {
            SubpatternCollection spc = (SubpatternCollection) sp;
            result = model.createResource(generateSubpatternCollectionUri(spc, pattern), subPatternCollectionClass);
            result.addLiteral(subpatternCollectionhasNameProp, spc.getId());
            result.addLiteral(hasCardMinProp, spc.getMinOccurrences()).addLiteral(hasCardMaxProp, spc.getMaxOccurrences());
            for (Subpattern spIn : spc.getSubpatterns()) {
                Resource spResource = processSubpattern(spIn, pattern, patternResource);
                result.addProperty(hasDirectSubpatternProp, spResource);
                patternResource.addProperty(patternHasSubpatternProp, spResource);
            }
            if (spc.getMinOccurrences() == 0 || spc.getMaxOccurrences() > 1) {
                result.addProperty(hasDeterminingElementProp,
                        model.createResource(generatePatternElementUri(spc.getPivotElement(), pattern)));
            }
        } else if (sp instanceof PatternTriple) {
            PatternTriple pt = (PatternTriple) sp;
            result = model.createResource(uriStart + patternName + "_triple" + tripleCount++, patternTripleClass);

            Resource res = processPatternElement(pt.getE1(), pattern);
            result.addProperty(hasSubjectProp, res);
            patternResource.addProperty(patternHasPatternElementProp, res);

            res = processPatternElement(pt.getE2(), pattern);
            result.addProperty(hasPropertyProp, res);
            patternResource.addProperty(patternHasPatternElementProp, res);

            res = processPatternElement(pt.getE3(), pattern);
            result.addProperty(hasObjectProp, res);
            patternResource.addProperty(patternHasPatternElementProp, res);
        }
        return result;
    }

    private static Resource processPatternElement(PatternElement pe, Pattern pattern) {
        Resource result = model.createResource("http://mon.uri.moisie.fr/plop");
        if (pe instanceof KbPatternElement) {
            KbPatternElement kbpe = (KbPatternElement) pe;
            if (kbpe.getUri().equals("BlankNode")) {
                logger.info("BLANK NODE!!");
                result = model.createResource(generatePatternElementUri(pe, pattern), blankNodePatternElementClass);
            } else {
                if (pe instanceof ClassPatternElement) {
                    result = model.createResource(generatePatternElementUri(pe, pattern), classPatternElementClass).addProperty(targetsClassProp, model.createResource(kbpe.getUri())).addLiteral(isQualifyingProp, kbpe.isQualifying());
                } else if (pe instanceof PropertyPatternElement) {
                    PropertyPatternElement ppe = (PropertyPatternElement) pe;
                    result = model.createResource(generatePatternElementUri(pe, pattern), propertyPatternElementClass).addProperty(targetsPropertyProp, model.createResource(ppe.getUri())).addLiteral(isQualifyingProp, ppe.isQualifying());
                    for (Integer referedElement : ppe.getReferedElements()) {
                        result.addProperty(refersToPatternElementProp,
                                model.createResource(generatePatternElementUri(pattern.getPatternElementById(referedElement), pattern)));
                    }
                }
            }
        } else if (pe instanceof LiteralPatternElement) {
            LiteralPatternElement lpe = (LiteralPatternElement) pe;
            result = model.createResource(generatePatternElementUri(pe, pattern), literalPatternElementClass).addProperty(targetsLiteralTypeProp, model.createResource(lpe.getType())).addLiteral(isQualifyingProp, lpe.isQualifying());
        }

        result.addLiteral(patternElementHasIdProp, pe.getId());
        if (pe.getMappingCondition() != null) {
            result.addLiteral(hasMappingConditionsProp, pe.getMappingCondition());
        }

        return result;
    }

    private static String generatePatternElementUri(PatternElement sp, Pattern pattern) {
        return uriStart + pattern.getName() + "_element" + sp.getId();
    }

    private static String generateSubpatternCollectionUri(SubpatternCollection spc, Pattern pattern) {
        return uriStart + pattern.getName() + "_" + spc.getId();
    }

    private static Resource processSentenceTemplate(Pattern pattern, Resource patternResource) {
        String patternName = pattern.getName();
        Resource result = model.createResource(uriStart + patternName + "_sentenceTemplate", sentenceTemplateClass);

//        int size = pattern.getSentenceTemplate().getSstList().size();
//        RDFNode[] nodes = new RDFNode[size];
//        int i = 0;
//        for (SubsentenceTemplate sst : pattern.getSentenceTemplate().getSstList()) {
//            if (sst instanceof PeInTemplate) {
//                nodes[i++] = processPeInTemplate((PeInTemplate) sst, pattern);
//            } else if (sst instanceof StaticStringInTemplate) {
//                nodes[i++] = processStaticStringInTemplate((StaticStringInTemplate) sst, pattern);
//            } else if (sst instanceof SpcInTemplate) {
//                nodes[i++] = processSpcInTemplate((SpcInTemplate) sst, pattern);
//            } else if (sst instanceof ForLoopInTemplate) {
//                nodes[i++] = processForLoopInTemplate((ForLoopInTemplate) sst, pattern);
//            }
//        }
//        RDFList sstListRessource = model.createList(nodes);
//        result.addProperty(isMadeUpOfListProp, sstListRessource);
        
        int iter = 0;
        Resource previousList = null;
        Resource currentList = model.createResource(uriStart + patternName + "_list_" + ++listCount, listClass);
        result.addProperty(isMadeUpOfListProp, currentList);
        for (SubsentenceTemplate sst : pattern.getSentenceTemplate().getSstList()) {
            if (iter > 0) {
                currentList = model.createResource(uriStart + patternName + "_list_" + ++listCount, listClass);
            }
            if (sst instanceof PeInTemplate) {
                currentList.addProperty(rdfFirstProp, processPeInTemplate((PeInTemplate) sst, pattern));
            } else if (sst instanceof StaticStringInTemplate) {
                currentList.addProperty(rdfFirstProp, processStaticStringInTemplate((StaticStringInTemplate) sst, pattern));
            } else if (sst instanceof SpcInTemplate) {
                currentList.addProperty(rdfFirstProp, processSpcInTemplate((SpcInTemplate) sst, pattern));
            } else if (sst instanceof ForLoopInTemplate) {
                currentList.addProperty(rdfFirstProp, processForLoopInTemplate((ForLoopInTemplate) sst, pattern));
            }
            if (iter > 0) {
                previousList.addProperty(rdfRestProp, currentList);
            }
            previousList = currentList;
            iter++;
        }
        currentList.addProperty(rdfRestProp, model.createResource("http://www.w3.org/1999/02/22-rdf-syntax-ns#nil"));

        return result;
    }
    
    private static Resource processPeInTemplate(PeInTemplate peit, Pattern pattern) {
        String patternName = pattern.getName();
        Resource result = model.createResource(uriStart + patternName + "_peit_" + peit.getPe().getId(), peInTemplateClass);
        result.addProperty(sstTargetsPcProp, model.createResource(generatePatternElementUri(peit.getPe(), pattern)));
        return result;
    }
    
    private static Resource processStaticStringInTemplate(StaticStringInTemplate ssit, Pattern pattern) {
        String patternName = pattern.getName();
        Resource result = model.createResource(uriStart + patternName + "_ssit_" + staticStringCount++, staticStringInTemplateClass);
        result.addLiteral(ssitHasValueProp, ssit.getStringValue());        
        return result;
    }
    
    private static Resource processSpcInTemplate(SpcInTemplate spcit, Pattern pattern) {
        String patternName = pattern.getName();
        Resource result = model.createResource(uriStart + patternName + "_spcit_" + spcit.getSpc().getId(), spcInTemplateClass);
        
        result.addProperty(sstTargetsPcProp, model.createResource(generateSubpatternCollectionUri(spcit.getSpc(), pattern)));
        
//        int size = spcit.getSstList().size();
//        RDFNode[] nodes = new RDFNode[size];
//        int i = 0;
//        for (SubsentenceTemplate sst : spcit.getSstList()) {
//            if (sst instanceof PeInTemplate) {
//                nodes[i++] = processPeInTemplate((PeInTemplate) sst, pattern);
//            } else if (sst instanceof StaticStringInTemplate) {
//                nodes[i++] = processStaticStringInTemplate((StaticStringInTemplate) sst, pattern);
//            } else if (sst instanceof SpcInTemplate) {
//                nodes[i++] = processSpcInTemplate((SpcInTemplate) sst, pattern);
//            } else if (sst instanceof ForLoopInTemplate) {
//                nodes[i++] = processForLoopInTemplate((ForLoopInTemplate) sst, pattern);
//            }
//        }
//        RDFList sstListRessource = model.createList(nodes);
//        result.addProperty(isMadeUpOfListProp, sstListRessource);
        
        int iter = 0;
        Resource previousList = null;
        Resource currentList = model.createResource(uriStart + patternName + "_list_" + ++listCount, listClass);
        result.addProperty(isMadeUpOfListProp, currentList);
        for (SubsentenceTemplate sst : spcit.getSstList()) {
            if (iter > 0) {
                currentList = model.createResource(uriStart + patternName + "_list_" + ++listCount, listClass);
            }
            if (sst instanceof PeInTemplate) {
                currentList.addProperty(rdfFirstProp, processPeInTemplate((PeInTemplate) sst, pattern));
            } else if (sst instanceof StaticStringInTemplate) {
                currentList.addProperty(rdfFirstProp, processStaticStringInTemplate((StaticStringInTemplate) sst, pattern));
            } else if (sst instanceof SpcInTemplate) {
                currentList.addProperty(rdfFirstProp, processSpcInTemplate((SpcInTemplate) sst, pattern));
            } else if (sst instanceof ForLoopInTemplate) {
                currentList.addProperty(rdfFirstProp, processForLoopInTemplate((ForLoopInTemplate) sst, pattern));
            }
            if (iter > 0) {
                previousList.addProperty(rdfRestProp, currentList);
            }
            previousList = currentList;
            iter++;
        }
        currentList.addProperty(rdfRestProp, model.createResource("http://www.w3.org/1999/02/22-rdf-syntax-ns#nil"));

        return result;
    }
    
    private static Resource processForLoopInTemplate(ForLoopInTemplate flit, Pattern pattern) {
        String patternName = pattern.getName();
        Resource result = model.createResource(uriStart + patternName + "_flit_" + flit.getSpc().getId(), forLoopInTemplateClass);
        
        result.addProperty(sstTargetsPcProp, model.createResource(generateSubpatternCollectionUri(flit.getSpc(), pattern)));
        
        int size = flit.getSstList().size();
        RDFNode[] nodes = new RDFNode[size];
        int i = 0;
        for (SubsentenceTemplate sst : flit.getSstList()) {
            if (sst instanceof PeInTemplate) {
                nodes[i++] = processPeInTemplate((PeInTemplate) sst, pattern);
            } else if (sst instanceof StaticStringInTemplate) {
                nodes[i++] = processStaticStringInTemplate((StaticStringInTemplate) sst, pattern);
            } else if (sst instanceof SpcInTemplate) {
                nodes[i++] = processSpcInTemplate((SpcInTemplate) sst, pattern);
            } else if (sst instanceof ForLoopInTemplate) {
                nodes[i++] = processForLoopInTemplate((ForLoopInTemplate) sst, pattern);
            }
        }
        RDFList sstListRessource = model.createList(nodes);
        result.addProperty(isMadeUpOfListProp, sstListRessource);
        
        return result;
    }
}
