// $ANTLR 3.3 Nov 30, 2010 12:45:30 /home/camille/ANTLRWorks/patternsDefinitionGrammar.g 2012-07-10 17:46:43

package org.swip.pivotToMappings.model.patterns.antlr;
import java.util.HashMap;
import java.util.LinkedList;
import org.swip.pivotToMappings.controller.Controller;
import org.swip.pivotToMappings.model.patterns.Pattern;
import org.swip.pivotToMappings.model.patterns.patternElement.ClassPatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.LiteralPatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.PropertyPatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.PatternElement;
import org.swip.pivotToMappings.model.patterns.subpattern.PatternTriple;
import org.swip.pivotToMappings.model.patterns.subpattern.Subpattern;
import org.swip.pivotToMappings.model.patterns.subpattern.SubpatternCollection;
import org.swip.pivotToMappings.exceptions.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class patternsDefinitionGrammarParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ENDPREF", "ENDSENT", "ENDQUER", "ENDMAPCOND", "ENDPAT", "PREF", "PAT", "QUER", "SENT", "MAPCOND", "UNDERSCORE", "COLON", "MINUS", "LEFTSB", "RIGHTSB", "X", "N", "LOWERTHAN", "GREATERTHAN", "TWOPOINTS", "SLASH", "ID", "CITE", "SENTENCE", "INT", "COMMENT", "WS", "';'", "'('", "','", "')'"
    };
    public static final int EOF=-1;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int ENDPREF=4;
    public static final int ENDSENT=5;
    public static final int ENDQUER=6;
    public static final int ENDMAPCOND=7;
    public static final int ENDPAT=8;
    public static final int PREF=9;
    public static final int PAT=10;
    public static final int QUER=11;
    public static final int SENT=12;
    public static final int MAPCOND=13;
    public static final int UNDERSCORE=14;
    public static final int COLON=15;
    public static final int MINUS=16;
    public static final int LEFTSB=17;
    public static final int RIGHTSB=18;
    public static final int X=19;
    public static final int N=20;
    public static final int LOWERTHAN=21;
    public static final int GREATERTHAN=22;
    public static final int TWOPOINTS=23;
    public static final int SLASH=24;
    public static final int ID=25;
    public static final int CITE=26;
    public static final int SENTENCE=27;
    public static final int INT=28;
    public static final int COMMENT=29;
    public static final int WS=30;

    // delegates
    // delegators


        public patternsDefinitionGrammarParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public patternsDefinitionGrammarParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return patternsDefinitionGrammarParser.tokenNames; }
    public String getGrammarFileName() { return "/home/camille/ANTLRWorks/patternsDefinitionGrammar.g"; }


    	/** Map variable name to Integer object holding value */
    	HashMap<String, String> prefixes = new HashMap<String, String>();
    	HashMap<String, PatternElement> pes = null;
    	
    	@Override
    	public void reportError(RecognitionException re) {
    		throw new LexicalErrorRuntimeException("Lexical error at " + re.line + ":" + re.index + " - unexpected character: \"" + (char)re.getUnexpectedType() + "\"");
    	}
    	
    	@Override
    	public void recover(IntStream input, RecognitionException re) {
    	    	throw new LexicalErrorRuntimeException("Syntax error at " + re.line + ":" + re.index + " - unexpected character: \"" + (char)re.getUnexpectedType() + "\"");
    	}
    	
    	String patternName = null;



    // $ANTLR start "patterns"
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:63:1: patterns returns [List<Pattern> ps] : prefixes ( pattern )+ ;
    public final List<Pattern> patterns() throws RecognitionException {
        List<Pattern> ps = null;

        Pattern pattern1 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:64:2: ( prefixes ( pattern )+ )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:64:4: prefixes ( pattern )+
            {
            pushFollow(FOLLOW_prefixes_in_patterns171);
            prefixes();

            state._fsp--;

            ps = new LinkedList<Pattern>();
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:66:3: ( pattern )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==PAT) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:66:4: pattern
            	    {
            	    pushFollow(FOLLOW_pattern_in_patterns180);
            	    pattern1=pattern();

            	    state._fsp--;

            	    ps.add(pattern1);

            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ps;
    }
    // $ANTLR end "patterns"


    // $ANTLR start "prefixes"
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:69:1: prefixes : PREF ( prefix )+ ENDPREF ;
    public final void prefixes() throws RecognitionException {
        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:70:2: ( PREF ( prefix )+ ENDPREF )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:70:4: PREF ( prefix )+ ENDPREF
            {
            match(input,PREF,FOLLOW_PREF_in_prefixes196); 
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:71:3: ( prefix )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==ID) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:71:3: prefix
            	    {
            	    pushFollow(FOLLOW_prefix_in_prefixes200);
            	    prefix();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);

            match(input,ENDPREF,FOLLOW_ENDPREF_in_prefixes205); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "prefixes"


    // $ANTLR start "prefix"
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:75:1: prefix : ID COLON CITE ;
    public final void prefix() throws RecognitionException {
        Token CITE2=null;
        Token ID3=null;

        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:76:2: ( ID COLON CITE )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:76:4: ID COLON CITE
            {
            ID3=(Token)match(input,ID,FOLLOW_ID_in_prefix217); 
            match(input,COLON,FOLLOW_COLON_in_prefix219); 
            CITE2=(Token)match(input,CITE,FOLLOW_CITE_in_prefix221); 

            		String prefixValue = (CITE2!=null?CITE2.getText():null);
            		prefixValue = prefixValue.substring(1, prefixValue.length()-1);
            		prefixes.put((ID3!=null?ID3.getText():null), prefixValue);
            		

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "prefix"


    // $ANTLR start "pattern"
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:84:1: pattern returns [Pattern p] : PAT ID QUER ( subpattern )+ ENDQUER SENTENCE ( mappinconditions )? ENDPAT ;
    public final Pattern pattern() throws RecognitionException {
        Pattern p = null;

        Token ID4=null;
        Token SENTENCE6=null;
        Subpattern subpattern5 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:85:2: ( PAT ID QUER ( subpattern )+ ENDQUER SENTENCE ( mappinconditions )? ENDPAT )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:85:4: PAT ID QUER ( subpattern )+ ENDQUER SENTENCE ( mappinconditions )? ENDPAT
            {

            		pes = new HashMap<String, PatternElement>();
            		List<Subpattern> subpatterns = new LinkedList<Subpattern>();
            		
            match(input,PAT,FOLLOW_PAT_in_pattern245); 
            ID4=(Token)match(input,ID,FOLLOW_ID_in_pattern247); 
            patternName = (ID4!=null?ID4.getText():null);
            match(input,QUER,FOLLOW_QUER_in_pattern253); 
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:91:3: ( subpattern )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>=MINUS && LA3_0<=LEFTSB)||LA3_0==INT) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:91:4: subpattern
            	    {
            	    pushFollow(FOLLOW_subpattern_in_pattern258);
            	    subpattern5=subpattern();

            	    state._fsp--;

            	    		
            	    			subpatterns.add(subpattern5);		
            	    		

            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);

            match(input,ENDQUER,FOLLOW_ENDQUER_in_pattern268); 
            SENTENCE6=(Token)match(input,SENTENCE,FOLLOW_SENTENCE_in_pattern272); 
            String sentence = (SENTENCE6!=null?SENTENCE6.getText():null); sentence = org.swip.pivotToMappings.utils.StringManipulation.removeBlankBorders(sentence.substring(8,sentence.length()-12));
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:97:3: ( mappinconditions )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==MAPCOND) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:97:4: mappinconditions
                    {
                    pushFollow(FOLLOW_mappinconditions_in_pattern279);
                    mappinconditions();

                    state._fsp--;


                    }
                    break;

            }

            match(input,ENDPAT,FOLLOW_ENDPAT_in_pattern285); 

            		p = new Pattern(patternName, subpatterns, sentence);
            		Controller.getInstance().setPatternElementsForPattern(new LinkedList<PatternElement>(pes.values()), p);
            		

            }

        }
        catch (Exception e) {
            throw new PatternRuntimeException("in pattern "+ patternName + "\n" + e.getMessage());
        }
        finally {
        }
        return p;
    }
    // $ANTLR end "pattern"


    // $ANTLR start "subpattern"
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:106:1: subpattern returns [Subpattern sp] : ( patterntriple | subpatterncollection );
    public final Subpattern subpattern() throws RecognitionException {
        Subpattern sp = null;

        PatternTriple patterntriple7 = null;

        SubpatternCollection subpatterncollection8 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:107:2: ( patterntriple | subpatterncollection )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==MINUS||LA5_0==INT) ) {
                alt5=1;
            }
            else if ( (LA5_0==LEFTSB) ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:107:4: patterntriple
                    {
                    pushFollow(FOLLOW_patterntriple_in_subpattern312);
                    patterntriple7=patterntriple();

                    state._fsp--;

                    sp = patterntriple7;

                    }
                    break;
                case 2 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:108:4: subpatterncollection
                    {
                    pushFollow(FOLLOW_subpatterncollection_in_subpattern319);
                    subpatterncollection8=subpatterncollection();

                    state._fsp--;

                    sp = subpatterncollection8;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return sp;
    }
    // $ANTLR end "subpattern"


    // $ANTLR start "patterntriple"
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:111:1: patterntriple returns [PatternTriple pt] : e1 e2 e3 ';' ;
    public final PatternTriple patterntriple() throws RecognitionException {
        PatternTriple pt = null;

        ClassPatternElement e19 = null;

        PropertyPatternElement e210 = null;

        PatternElement e311 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:112:2: ( e1 e2 e3 ';' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:112:4: e1 e2 e3 ';'
            {
            pushFollow(FOLLOW_e1_in_patterntriple338);
            e19=e1();

            state._fsp--;

            pushFollow(FOLLOW_e2_in_patterntriple340);
            e210=e2();

            state._fsp--;

            pushFollow(FOLLOW_e3_in_patterntriple342);
            e311=e3();

            state._fsp--;

            match(input,31,FOLLOW_31_in_patterntriple344); 

            		pt = new PatternTriple(e19, e210, e311);
            		

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return pt;
    }
    // $ANTLR end "patterntriple"


    // $ANTLR start "e1"
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:118:1: e1 returns [ClassPatternElement pe] : classPe ;
    public final ClassPatternElement e1() throws RecognitionException {
        ClassPatternElement pe = null;

        ClassPatternElement classPe12 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:119:2: ( classPe )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:119:4: classPe
            {
            pushFollow(FOLLOW_classPe_in_e1363);
            classPe12=classPe();

            state._fsp--;

            pe = classPe12;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return pe;
    }
    // $ANTLR end "e1"


    // $ANTLR start "e2"
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:122:1: e2 returns [PropertyPatternElement pe] : propertyPe ;
    public final PropertyPatternElement e2() throws RecognitionException {
        PropertyPatternElement pe = null;

        PropertyPatternElement propertyPe13 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:123:2: ( propertyPe )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:123:4: propertyPe
            {
            pushFollow(FOLLOW_propertyPe_in_e2381);
            propertyPe13=propertyPe();

            state._fsp--;

            pe = propertyPe13;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return pe;
    }
    // $ANTLR end "e2"


    // $ANTLR start "e3"
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:126:1: e3 returns [PatternElement pe] : ( classPe | literalPe );
    public final PatternElement e3() throws RecognitionException {
        PatternElement pe = null;

        ClassPatternElement classPe14 = null;

        LiteralPatternElement literalPe15 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:127:2: ( classPe | literalPe )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==MINUS) ) {
                alt6=1;
            }
            else if ( (LA6_0==INT) ) {
                int LA6_2 = input.LA(2);

                if ( (LA6_2==UNDERSCORE) ) {
                    int LA6_3 = input.LA(3);

                    if ( (LA6_3==LOWERTHAN) ) {
                        alt6=2;
                    }
                    else if ( (LA6_3==ID) ) {
                        alt6=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 6, 3, input);

                        throw nvae;
                    }
                }
                else if ( (LA6_2==31) ) {
                    alt6=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:127:4: classPe
                    {
                    pushFollow(FOLLOW_classPe_in_e3399);
                    classPe14=classPe();

                    state._fsp--;

                    pe = classPe14;

                    }
                    break;
                case 2 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:128:4: literalPe
                    {
                    pushFollow(FOLLOW_literalPe_in_e3406);
                    literalPe15=literalPe();

                    state._fsp--;

                    pe = literalPe15;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return pe;
    }
    // $ANTLR end "e3"


    // $ANTLR start "subpatterncollection"
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:131:1: subpatterncollection returns [SubpatternCollection spc] : '[' ( subpattern )+ ']' ( ID ( COLON mio= INT ( TWOPOINTS (mao= INT | N ) )? ( SLASH pe= INT )? )? )? ;
    public final SubpatternCollection subpatterncollection() throws RecognitionException {
        SubpatternCollection spc = null;

        Token mio=null;
        Token mao=null;
        Token pe=null;
        Token ID17=null;
        Subpattern subpattern16 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:132:2: ( '[' ( subpattern )+ ']' ( ID ( COLON mio= INT ( TWOPOINTS (mao= INT | N ) )? ( SLASH pe= INT )? )? )? )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:132:4: '[' ( subpattern )+ ']' ( ID ( COLON mio= INT ( TWOPOINTS (mao= INT | N ) )? ( SLASH pe= INT )? )? )?
            {

            		List<Subpattern> sps = new LinkedList<Subpattern>();
            		String scName = "";
            		int minOccurrences = 1;
            		int maxOccurrences = 1;
            		PatternElement pivotElement = null;
            		
            match(input,LEFTSB,FOLLOW_LEFTSB_in_subpatterncollection429); 
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:139:7: ( subpattern )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( ((LA7_0>=MINUS && LA7_0<=LEFTSB)||LA7_0==INT) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:139:8: subpattern
            	    {
            	    pushFollow(FOLLOW_subpattern_in_subpatterncollection431);
            	    subpattern16=subpattern();

            	    state._fsp--;

            	    sps.add(subpattern16);

            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
            } while (true);

            match(input,RIGHTSB,FOLLOW_RIGHTSB_in_subpatterncollection436); 
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:140:10: ( ID ( COLON mio= INT ( TWOPOINTS (mao= INT | N ) )? ( SLASH pe= INT )? )? )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==ID) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:140:11: ID ( COLON mio= INT ( TWOPOINTS (mao= INT | N ) )? ( SLASH pe= INT )? )?
                    {
                    ID17=(Token)match(input,ID,FOLLOW_ID_in_subpatterncollection448); 
                    scName = (ID17!=null?ID17.getText():null);
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:141:11: ( COLON mio= INT ( TWOPOINTS (mao= INT | N ) )? ( SLASH pe= INT )? )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==COLON) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:141:12: COLON mio= INT ( TWOPOINTS (mao= INT | N ) )? ( SLASH pe= INT )?
                            {
                            match(input,COLON,FOLLOW_COLON_in_subpatterncollection463); 
                            mio=(Token)match(input,INT,FOLLOW_INT_in_subpatterncollection467); 
                            minOccurrences = Integer.parseInt((mio!=null?mio.getText():null));
                            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:142:14: ( TWOPOINTS (mao= INT | N ) )?
                            int alt9=2;
                            int LA9_0 = input.LA(1);

                            if ( (LA9_0==TWOPOINTS) ) {
                                alt9=1;
                            }
                            switch (alt9) {
                                case 1 :
                                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:142:15: TWOPOINTS (mao= INT | N )
                                    {
                                    match(input,TWOPOINTS,FOLLOW_TWOPOINTS_in_subpatterncollection485); 
                                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:143:18: (mao= INT | N )
                                    int alt8=2;
                                    int LA8_0 = input.LA(1);

                                    if ( (LA8_0==INT) ) {
                                        alt8=1;
                                    }
                                    else if ( (LA8_0==N) ) {
                                        alt8=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 8, 0, input);

                                        throw nvae;
                                    }
                                    switch (alt8) {
                                        case 1 :
                                            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:143:19: mao= INT
                                            {
                                            mao=(Token)match(input,INT,FOLLOW_INT_in_subpatterncollection507); 
                                            maxOccurrences = Integer.parseInt((mao!=null?mao.getText():null));

                                            }
                                            break;
                                        case 2 :
                                            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:143:74: N
                                            {
                                            match(input,N,FOLLOW_N_in_subpatterncollection510); 
                                            maxOccurrences = -1;

                                            }
                                            break;

                                    }


                                    }
                                    break;

                            }

                            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:144:16: ( SLASH pe= INT )?
                            int alt10=2;
                            int LA10_0 = input.LA(1);

                            if ( (LA10_0==SLASH) ) {
                                alt10=1;
                            }
                            switch (alt10) {
                                case 1 :
                                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:144:17: SLASH pe= INT
                                    {
                                    match(input,SLASH,FOLLOW_SLASH_in_subpatterncollection530); 
                                    pe=(Token)match(input,INT,FOLLOW_INT_in_subpatterncollection534); 

                                    					     		 			pivotElement = pes.get((pe!=null?pe.getText():null));
                                    						     		 		if (pivotElement == null) {
                                    						     		 			throw new RuntimeException("element " + (pe!=null?pe.getText():null) + " used as pivot element was not found");
                                    						     		 		}						     		 		
                                    						     		 		

                                    }
                                    break;

                            }


                            }
                            break;

                    }


                    }
                    break;

            }


            		spc = new SubpatternCollection(sps, scName, minOccurrences, maxOccurrences, pivotElement);
            		

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return spc;
    }
    // $ANTLR end "subpatterncollection"


    // $ANTLR start "classPe"
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:157:1: classPe returns [ClassPatternElement cpe] : ( MINUS )? INT ( UNDERSCORE qname )? ;
    public final ClassPatternElement classPe() throws RecognitionException {
        ClassPatternElement cpe = null;

        Token INT19=null;
        String qname18 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:158:2: ( ( MINUS )? INT ( UNDERSCORE qname )? )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:158:4: ( MINUS )? INT ( UNDERSCORE qname )?
            {

            		String qnameUri = null;
            		boolean qualifying = true;
            		
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:162:3: ( MINUS )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==MINUS) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:162:4: MINUS
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_classPe586); 
                    qualifying = false;

                    }
                    break;

            }

            INT19=(Token)match(input,INT,FOLLOW_INT_in_classPe594); 
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:163:6: ( UNDERSCORE qname )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==UNDERSCORE) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:163:7: UNDERSCORE qname
                    {
                    match(input,UNDERSCORE,FOLLOW_UNDERSCORE_in_classPe596); 
                    pushFollow(FOLLOW_qname_in_classPe598);
                    qname18=qname();

                    state._fsp--;

                    qnameUri = qname18;

                    }
                    break;

            }

            		
            		String key = (INT19!=null?INT19.getText():null);
            		PatternElement pe = pes.get(key);
            		if (pe == null) {
            			if (qnameUri != null) {
            	        			cpe = new ClassPatternElement(Integer.parseInt((INT19!=null?INT19.getText():null)), qnameUri, qualifying);
            				pes.put(key, cpe);
            			} else {
            				throw new RuntimeException("pattern element " + (INT19!=null?INT19.getText():null) + " is not defined");
            			}
            		} else {
            			if (!(pe instanceof ClassPatternElement)) {
            				throw new RuntimeException("pattern element " + (INT19!=null?INT19.getText():null) + " was previously used as something else than a class");
            			}
            			cpe = (ClassPatternElement)pe;
            			if (qnameUri != null) {
            				if (!cpe.getUri().equals(qnameUri)) {
            					throw new RuntimeException("pattern element " + (INT19!=null?INT19.getText():null) + "_... doesn't refer the same resource\n" + 
            					"once \"" + cpe.getUri() + "\"\n" +
            					"and then \"" + qnameUri + "\"");
            				}
            			}						
            		}
            		

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return cpe;
    }
    // $ANTLR end "classPe"


    // $ANTLR start "propertyPe"
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:190:1: propertyPe returns [PropertyPatternElement ppe] : ( MINUS )? id= INT ( UNDERSCORE qname )? ( '(' ref= INT ( ',' ref2= INT )* ')' )? ;
    public final PropertyPatternElement propertyPe() throws RecognitionException {
        PropertyPatternElement ppe = null;

        Token id=null;
        Token ref=null;
        Token ref2=null;
        String qname20 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:191:2: ( ( MINUS )? id= INT ( UNDERSCORE qname )? ( '(' ref= INT ( ',' ref2= INT )* ')' )? )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:191:4: ( MINUS )? id= INT ( UNDERSCORE qname )? ( '(' ref= INT ( ',' ref2= INT )* ')' )?
            {

            		String qnameUri = null;
            		boolean qualifying = true;
            		List<Integer> referedElements = new LinkedList<Integer>();
            		
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:196:3: ( MINUS )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==MINUS) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:196:4: MINUS
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_propertyPe626); 
                    qualifying = false;

                    }
                    break;

            }

            id=(Token)match(input,INT,FOLLOW_INT_in_propertyPe636); 
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:197:9: ( UNDERSCORE qname )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==UNDERSCORE) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:197:10: UNDERSCORE qname
                    {
                    match(input,UNDERSCORE,FOLLOW_UNDERSCORE_in_propertyPe638); 
                    pushFollow(FOLLOW_qname_in_propertyPe640);
                    qname20=qname();

                    state._fsp--;

                    qnameUri = qname20;

                    }
                    break;

            }

            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:197:52: ( '(' ref= INT ( ',' ref2= INT )* ')' )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==32) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:197:53: '(' ref= INT ( ',' ref2= INT )* ')'
                    {
                    match(input,32,FOLLOW_32_in_propertyPe645); 
                    ref=(Token)match(input,INT,FOLLOW_INT_in_propertyPe648); 
                    referedElements.add(Integer.parseInt((ref!=null?ref.getText():null)));
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:197:116: ( ',' ref2= INT )*
                    loop17:
                    do {
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( (LA17_0==33) ) {
                            alt17=1;
                        }


                        switch (alt17) {
                    	case 1 :
                    	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:197:117: ',' ref2= INT
                    	    {
                    	    match(input,33,FOLLOW_33_in_propertyPe653); 
                    	    ref2=(Token)match(input,INT,FOLLOW_INT_in_propertyPe656); 
                    	    referedElements.add(Integer.parseInt((ref2!=null?ref2.getText():null)));

                    	    }
                    	    break;

                    	default :
                    	    break loop17;
                        }
                    } while (true);

                    match(input,34,FOLLOW_34_in_propertyPe662); 

                    }
                    break;

            }


            		String key = (id!=null?id.getText():null);
            		PatternElement pe = pes.get(key);
            		if (pe == null) {
            			if (qnameUri != null) {
                    				ppe = new PropertyPatternElement(Integer.parseInt(key), qnameUri, qualifying, referedElements);
            				pes.put(key, ppe);
            			} else {
            				throw new RuntimeException("pattern element " + key + " is not defined");
            			}
            		} else {
            			if (!(pe instanceof PropertyPatternElement)) {
            				throw new RuntimeException("pattern element " + key + "  was previously used as something else than a property");
            			}
            			ppe = (PropertyPatternElement)pe;
            			if (qnameUri != null) {
            				if (!ppe.getUri().equals(qnameUri)) {
            					throw new RuntimeException("pattern element " + key + "_... doesn't refer the same resource\n" + 
            					"once \"" + ppe.getUri() + "\"\n" +
            					"and then \"" + qnameUri + "\"");
            				}
            			}
            		}
            		

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ppe;
    }
    // $ANTLR end "propertyPe"


    // $ANTLR start "qname"
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:224:1: qname returns [String uri] : p= ID COLON s= ID ;
    public final String qname() throws RecognitionException {
        String uri = null;

        Token p=null;
        Token s=null;

        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:225:2: (p= ID COLON s= ID )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:225:4: p= ID COLON s= ID
            {
            p=(Token)match(input,ID,FOLLOW_ID_in_qname687); 
            match(input,COLON,FOLLOW_COLON_in_qname689); 
            s=(Token)match(input,ID,FOLLOW_ID_in_qname693); 

            		String ps = prefixes.get((p!=null?p.getText():null));
            		if (ps == null) {
            			throw new RuntimeException("prefix " + (p!=null?p.getText():null) + " is not declared");
            		}
            		uri = ps + (s!=null?s.getText():null);
            		//System.out.println((p!=null?p.getText():null) + ":" + (s!=null?s.getText():null) + " => " + ps + (s!=null?s.getText():null));
            		

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return uri;
    }
    // $ANTLR end "qname"


    // $ANTLR start "literalPe"
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:236:1: literalPe returns [LiteralPatternElement lpe] : INT UNDERSCORE LOWERTHAN qname GREATERTHAN ;
    public final LiteralPatternElement literalPe() throws RecognitionException {
        LiteralPatternElement lpe = null;

        Token INT21=null;
        String qname22 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:237:2: ( INT UNDERSCORE LOWERTHAN qname GREATERTHAN )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:237:4: INT UNDERSCORE LOWERTHAN qname GREATERTHAN
            {
            INT21=(Token)match(input,INT,FOLLOW_INT_in_literalPe713); 
            match(input,UNDERSCORE,FOLLOW_UNDERSCORE_in_literalPe715); 
            match(input,LOWERTHAN,FOLLOW_LOWERTHAN_in_literalPe717); 
            pushFollow(FOLLOW_qname_in_literalPe719);
            qname22=qname();

            state._fsp--;

            match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_literalPe721); 

            		try {
            			String key = (INT21!=null?INT21.getText():null);
            			if (pes.get(key) != null) {
            				throw new RuntimeException("pattern element " + (INT21!=null?INT21.getText():null) + " refers different resources (literal elements can be used only once)");
            			}
            			lpe = new LiteralPatternElement(Integer.parseInt(key), qname22);
            			pes.put(key, lpe);
            		} catch (LiteralException ex) {
                                   			throw new LiteralRuntimeException(ex.getMessage());
                            		}
                            				

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return lpe;
    }
    // $ANTLR end "literalPe"


    // $ANTLR start "mappinconditions"
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:252:1: mappinconditions : MAPCOND ( mappingcondition )+ ENDMAPCOND ;
    public final void mappinconditions() throws RecognitionException {
        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:253:2: ( MAPCOND ( mappingcondition )+ ENDMAPCOND )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:253:4: MAPCOND ( mappingcondition )+ ENDMAPCOND
            {
            match(input,MAPCOND,FOLLOW_MAPCOND_in_mappinconditions738); 
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:254:3: ( mappingcondition )+
            int cnt19=0;
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==INT) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:254:4: mappingcondition
            	    {
            	    pushFollow(FOLLOW_mappingcondition_in_mappinconditions743);
            	    mappingcondition();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt19 >= 1 ) break loop19;
                        EarlyExitException eee =
                            new EarlyExitException(19, input);
                        throw eee;
                }
                cnt19++;
            } while (true);

            match(input,ENDMAPCOND,FOLLOW_ENDMAPCOND_in_mappinconditions749); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "mappinconditions"


    // $ANTLR start "mappingcondition"
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:258:1: mappingcondition : INT COLON CITE ;
    public final void mappingcondition() throws RecognitionException {
        Token INT23=null;
        Token CITE24=null;

        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:259:2: ( INT COLON CITE )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:259:4: INT COLON CITE
            {
            INT23=(Token)match(input,INT,FOLLOW_INT_in_mappingcondition764); 
            match(input,COLON,FOLLOW_COLON_in_mappingcondition766); 
            CITE24=(Token)match(input,CITE,FOLLOW_CITE_in_mappingcondition768); 

            		String key = (INT23!=null?INT23.getText():null);
            		PatternElement pe = pes.get(key);
            		if (pe == null) {
            			throw new RuntimeException("error in mapping conditions: element " + key + " does not exist");
            		}
            		String condition = (CITE24!=null?CITE24.getText():null);
            		pe.setMappingCondition(condition.substring(1, condition.length()-1));
            		

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "mappingcondition"

    // Delegated rules


 

    public static final BitSet FOLLOW_prefixes_in_patterns171 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_pattern_in_patterns180 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_PREF_in_prefixes196 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_prefix_in_prefixes200 = new BitSet(new long[]{0x0000000002000010L});
    public static final BitSet FOLLOW_ENDPREF_in_prefixes205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_prefix217 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_COLON_in_prefix219 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_CITE_in_prefix221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PAT_in_pattern245 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_ID_in_pattern247 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_QUER_in_pattern253 = new BitSet(new long[]{0x0000000010030000L});
    public static final BitSet FOLLOW_subpattern_in_pattern258 = new BitSet(new long[]{0x0000000010030040L});
    public static final BitSet FOLLOW_ENDQUER_in_pattern268 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_SENTENCE_in_pattern272 = new BitSet(new long[]{0x0000000000002100L});
    public static final BitSet FOLLOW_mappinconditions_in_pattern279 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ENDPAT_in_pattern285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_patterntriple_in_subpattern312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_subpatterncollection_in_subpattern319 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_e1_in_patterntriple338 = new BitSet(new long[]{0x0000000010010000L});
    public static final BitSet FOLLOW_e2_in_patterntriple340 = new BitSet(new long[]{0x0000000010010000L});
    public static final BitSet FOLLOW_e3_in_patterntriple342 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_patterntriple344 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classPe_in_e1363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_propertyPe_in_e2381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classPe_in_e3399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalPe_in_e3406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTSB_in_subpatterncollection429 = new BitSet(new long[]{0x0000000010030000L});
    public static final BitSet FOLLOW_subpattern_in_subpatterncollection431 = new BitSet(new long[]{0x0000000010070000L});
    public static final BitSet FOLLOW_RIGHTSB_in_subpatterncollection436 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_ID_in_subpatterncollection448 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_COLON_in_subpatterncollection463 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_INT_in_subpatterncollection467 = new BitSet(new long[]{0x0000000001800002L});
    public static final BitSet FOLLOW_TWOPOINTS_in_subpatterncollection485 = new BitSet(new long[]{0x0000000010100000L});
    public static final BitSet FOLLOW_INT_in_subpatterncollection507 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_N_in_subpatterncollection510 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_SLASH_in_subpatterncollection530 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_INT_in_subpatterncollection534 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_classPe586 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_INT_in_classPe594 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_UNDERSCORE_in_classPe596 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_qname_in_classPe598 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_propertyPe626 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_INT_in_propertyPe636 = new BitSet(new long[]{0x0000000100004002L});
    public static final BitSet FOLLOW_UNDERSCORE_in_propertyPe638 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_qname_in_propertyPe640 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_32_in_propertyPe645 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_INT_in_propertyPe648 = new BitSet(new long[]{0x0000000600000000L});
    public static final BitSet FOLLOW_33_in_propertyPe653 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_INT_in_propertyPe656 = new BitSet(new long[]{0x0000000600000000L});
    public static final BitSet FOLLOW_34_in_propertyPe662 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_qname687 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_COLON_in_qname689 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_ID_in_qname693 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literalPe713 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_UNDERSCORE_in_literalPe715 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_LOWERTHAN_in_literalPe717 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_qname_in_literalPe719 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_GREATERTHAN_in_literalPe721 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MAPCOND_in_mappinconditions738 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_mappingcondition_in_mappinconditions743 = new BitSet(new long[]{0x0000000010000080L});
    public static final BitSet FOLLOW_ENDMAPCOND_in_mappinconditions749 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_mappingcondition764 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_COLON_in_mappingcondition766 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_CITE_in_mappingcondition768 = new BitSet(new long[]{0x0000000000000002L});

}