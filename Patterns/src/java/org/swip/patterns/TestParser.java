package org.swip.patterns;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.apache.log4j.Logger;
import org.swip.patterns.antlr.patternsDefinitionGrammarLexer;
import org.swip.patterns.antlr.patternsDefinitionGrammarParser;

public class TestParser {

    private static final Logger logger = Logger.getLogger(PatternsTextToRdf.class);

    public static void main(String args[]) throws org.antlr.runtime.RecognitionException, IOException {

        String patternsText = "prefixes\n"
                + "   dc: 	\"http://purl.org/dc/elements/1.1/\"\n"
                + "   foaf:	\"http://xmlns.com/foaf/0.1/\"\n"
                + "   mo: 	\"http://purl.org/ontology/mo/\"\n"
                + "   event: 	\"http://purl.org/NET/c4dm/event.owl#\"\n"
                + "   tl: 	\"http://purl.org/NET/c4dm/timeline.owl#\"\n"
                + "   time:	\"http://www.w3.org/2006/time#\"\n"
                + "   bio:	\"http://purl.org/vocab/bio/0.1/\"\n"
                + "   owl:	\"http://www.w3.org/2002/07/owl#\"\n"
                + "   blank:	\"Blank\"\n"
                + "   rdf:	\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\n\""
                + "   xsd:      \"http://www.w3.org/2001/XMLSchema#\"\n"
                + "   rel:	\"http://purl.org/vocab/relationship/\"\n"
                + "end prefixes\n"
                + "\n"
                + "pattern cd_info\n"
                + "	query\n"
                + "		[ 1_mo:MusicalManifestation	2_foaf:maker(3)				3_mo:MusicArtist;		]creator:1..2/3/*0..1/3*/\n"
                + "		[ 1							6_mo:producer(7)			7_foaf:Agent;			]producer:0..1/7\n"
                + "		[ 1							8_mo:composer(9)			9_foaf:Agent;			]composer:0..2/9/*0..1/9*/\n"
                + "		[ 1							10_mo:singer(11)			11_mo:Performer;		]vocal:0..1/11\n"
                + "		[ 1							12_mo:release_status(13)	13_mo:ReleaseStatus;	]status:0..1/13\n"
                + "		[ 1							14_mo:lyricist(15)			15_mo:MusicArtist;		]lyricist:0..1/15\n"
                + "	end query\n"
                + "	sentence\n"
                + "		-1- -status-[\" whose status is \"-13-\", \"] -creator-[\" created by \"-for-creator-[-3-\", \"]] -producer-[\" produced by \"-7-\", \"] -composer-[\" composed by \"-9-\", \"] -vocal-[\" vocalised by \"-11-\", \"] -lyricist-[\" whose lyricist is \"-15-]\n"
                + "	end sentence\n"
                + "end pattern";


        List<Pattern> l = new LinkedList<Pattern>();
        logger.info("Loading patterns:");
        logger.info("----------------\n");
        long time = System.currentTimeMillis();
        // read patterns on file and instantiate them
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(patternsText.getBytes()));
        patternsDefinitionGrammarLexer lexer = new patternsDefinitionGrammarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        patternsDefinitionGrammarParser parser = new patternsDefinitionGrammarParser(tokens);
        l = parser.patterns();
        // display loaded patterns
        logger.info("Patterns:\n");
        for (Pattern pattern : l) {
            logger.info(pattern.toString());
        }
        logger.info("Pattern loaded");
        logger.info("time for loading patterns: " + (System.currentTimeMillis() - time) + "ms");
        logger.info("================================================================");

    }
}
