// $ANTLR 3.3 Nov 30, 2010 12:45:30 /home/camille/ANTLRWorks/patternsDefinitionGrammar.g 2013-01-27 20:58:08

package org.swip.patterns.antlr;
import java.util.HashMap;
import java.util.LinkedList;
import org.swip.patterns.*;
import org.swip.patterns.sentence.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class patternsDefinitionGrammarParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ENDPREF", "ENDSENT", "ENDQUER", "ENDMAPCOND", "ENDPAT", "PREF", "PAT", "QUER", "SENT", "MAPCOND", "UNDERSCORE", "COLON", "MINUS", "LEFTSB", "RIGHTSB", "X", "N", "LOWERTHAN", "GREATERTHAN", "TWOPOINTS", "SLASH", "ID", "CITE", "INT", "COMMENT", "WS", "';'", "'('", "','", "')'", "'-['", "'-for-'"
    };
    public static final int EOF=-1;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
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
    public static final int INT=27;
    public static final int COMMENT=28;
    public static final int WS=29;

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
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:55:1: patterns returns [List<Pattern> ps] : prefixes ( pattern )+ ;
    public final List<Pattern> patterns() throws RecognitionException {
        List<Pattern> ps = null;

        Pattern pattern1 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:56:2: ( prefixes ( pattern )+ )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:56:4: prefixes ( pattern )+
            {
            pushFollow(FOLLOW_prefixes_in_patterns171);
            prefixes();

            state._fsp--;

            ps = new LinkedList<Pattern>();
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:58:3: ( pattern )+
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
            	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:58:4: pattern
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
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:61:1: prefixes : PREF ( prefix )+ ENDPREF ;
    public final void prefixes() throws RecognitionException {
        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:62:2: ( PREF ( prefix )+ ENDPREF )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:62:4: PREF ( prefix )+ ENDPREF
            {
            match(input,PREF,FOLLOW_PREF_in_prefixes196); 
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:63:3: ( prefix )+
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
            	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:63:3: prefix
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
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:67:1: prefix : ID COLON CITE ;
    public final void prefix() throws RecognitionException {
        Token CITE2=null;
        Token ID3=null;

        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:68:2: ( ID COLON CITE )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:68:4: ID COLON CITE
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
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:76:1: pattern returns [Pattern p] : PAT ID QUER ( subpattern )+ ENDQUER sentenceTemplate[p] ENDPAT ;
    public final Pattern pattern() throws RecognitionException {
        Pattern p = null;

        Token ID4=null;
        Subpattern subpattern5 = null;

        SentenceTemplate sentenceTemplate6 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:77:2: ( PAT ID QUER ( subpattern )+ ENDQUER sentenceTemplate[p] ENDPAT )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:77:4: PAT ID QUER ( subpattern )+ ENDQUER sentenceTemplate[p] ENDPAT
            {

            		pes = new HashMap<String, PatternElement>();
            		List<Subpattern> subpatterns = new LinkedList<Subpattern>();
            		
            match(input,PAT,FOLLOW_PAT_in_pattern245); 
            ID4=(Token)match(input,ID,FOLLOW_ID_in_pattern247); 
            patternName = (ID4!=null?ID4.getText():null);
            match(input,QUER,FOLLOW_QUER_in_pattern253); 
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:83:3: ( subpattern )+
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
            	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:83:4: subpattern
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

            		p = new Pattern(patternName, subpatterns);
            		Controller.getInstance().setPatternElementsForPattern(new LinkedList<PatternElement>(pes.values()), p);
            		
            pushFollow(FOLLOW_sentenceTemplate_in_pattern276);
            sentenceTemplate6=sentenceTemplate(p);

            state._fsp--;

            p.setSentenceTemplate(sentenceTemplate6);/*String sentence = $SENTENCE.text; sentence = StringManipulation.removeBlankBorders(sentence.substring(8,sentence.length()-12));*/
            match(input,ENDPAT,FOLLOW_ENDPAT_in_pattern284); 

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
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:98:1: subpattern returns [Subpattern sp] : ( patterntriple | subpatterncollection );
    public final Subpattern subpattern() throws RecognitionException {
        Subpattern sp = null;

        PatternTriple patterntriple7 = null;

        SubpatternCollection subpatterncollection8 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:99:2: ( patterntriple | subpatterncollection )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==MINUS||LA4_0==INT) ) {
                alt4=1;
            }
            else if ( (LA4_0==LEFTSB) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:99:4: patterntriple
                    {
                    pushFollow(FOLLOW_patterntriple_in_subpattern307);
                    patterntriple7=patterntriple();

                    state._fsp--;

                    sp = patterntriple7;

                    }
                    break;
                case 2 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:100:4: subpatterncollection
                    {
                    pushFollow(FOLLOW_subpatterncollection_in_subpattern314);
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
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:103:1: patterntriple returns [PatternTriple pt] : e1 e2 e3 ';' ;
    public final PatternTriple patterntriple() throws RecognitionException {
        PatternTriple pt = null;

        ClassPatternElement e19 = null;

        PropertyPatternElement e210 = null;

        PatternElement e311 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:104:2: ( e1 e2 e3 ';' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:104:4: e1 e2 e3 ';'
            {
            pushFollow(FOLLOW_e1_in_patterntriple333);
            e19=e1();

            state._fsp--;

            pushFollow(FOLLOW_e2_in_patterntriple335);
            e210=e2();

            state._fsp--;

            pushFollow(FOLLOW_e3_in_patterntriple337);
            e311=e3();

            state._fsp--;

            match(input,30,FOLLOW_30_in_patterntriple339); 

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
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:110:1: e1 returns [ClassPatternElement pe] : classPe ;
    public final ClassPatternElement e1() throws RecognitionException {
        ClassPatternElement pe = null;

        ClassPatternElement classPe12 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:111:2: ( classPe )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:111:4: classPe
            {
            pushFollow(FOLLOW_classPe_in_e1358);
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
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:114:1: e2 returns [PropertyPatternElement pe] : propertyPe ;
    public final PropertyPatternElement e2() throws RecognitionException {
        PropertyPatternElement pe = null;

        PropertyPatternElement propertyPe13 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:115:2: ( propertyPe )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:115:4: propertyPe
            {
            pushFollow(FOLLOW_propertyPe_in_e2376);
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
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:118:1: e3 returns [PatternElement pe] : ( classPe | literalPe );
    public final PatternElement e3() throws RecognitionException {
        PatternElement pe = null;

        ClassPatternElement classPe14 = null;

        LiteralPatternElement literalPe15 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:119:2: ( classPe | literalPe )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==MINUS) ) {
                alt5=1;
            }
            else if ( (LA5_0==INT) ) {
                int LA5_2 = input.LA(2);

                if ( (LA5_2==UNDERSCORE) ) {
                    int LA5_3 = input.LA(3);

                    if ( (LA5_3==LOWERTHAN) ) {
                        alt5=2;
                    }
                    else if ( (LA5_3==ID) ) {
                        alt5=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 5, 3, input);

                        throw nvae;
                    }
                }
                else if ( (LA5_2==30) ) {
                    alt5=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 5, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:119:4: classPe
                    {
                    pushFollow(FOLLOW_classPe_in_e3394);
                    classPe14=classPe();

                    state._fsp--;

                    pe = classPe14;

                    }
                    break;
                case 2 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:120:4: literalPe
                    {
                    pushFollow(FOLLOW_literalPe_in_e3401);
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
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:123:1: subpatterncollection returns [SubpatternCollection spc] : '[' ( subpattern )+ ']' ( ID ( COLON mio= INT ( TWOPOINTS (mao= INT | N ) )? ( SLASH pe= INT )? )? )? ;
    public final SubpatternCollection subpatterncollection() throws RecognitionException {
        SubpatternCollection spc = null;

        Token mio=null;
        Token mao=null;
        Token pe=null;
        Token ID17=null;
        Subpattern subpattern16 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:124:2: ( '[' ( subpattern )+ ']' ( ID ( COLON mio= INT ( TWOPOINTS (mao= INT | N ) )? ( SLASH pe= INT )? )? )? )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:124:4: '[' ( subpattern )+ ']' ( ID ( COLON mio= INT ( TWOPOINTS (mao= INT | N ) )? ( SLASH pe= INT )? )? )?
            {

            		List<Subpattern> sps = new LinkedList<Subpattern>();
            		String scName = "";
            		int minOccurrences = 1;
            		int maxOccurrences = 1;
            		PatternElement pivotElement = null;
            		
            match(input,LEFTSB,FOLLOW_LEFTSB_in_subpatterncollection424); 
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:131:7: ( subpattern )+
            int cnt6=0;
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( ((LA6_0>=MINUS && LA6_0<=LEFTSB)||LA6_0==INT) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:131:8: subpattern
            	    {
            	    pushFollow(FOLLOW_subpattern_in_subpatterncollection426);
            	    subpattern16=subpattern();

            	    state._fsp--;

            	    sps.add(subpattern16);

            	    }
            	    break;

            	default :
            	    if ( cnt6 >= 1 ) break loop6;
                        EarlyExitException eee =
                            new EarlyExitException(6, input);
                        throw eee;
                }
                cnt6++;
            } while (true);

            match(input,RIGHTSB,FOLLOW_RIGHTSB_in_subpatterncollection431); 
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:132:10: ( ID ( COLON mio= INT ( TWOPOINTS (mao= INT | N ) )? ( SLASH pe= INT )? )? )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==ID) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:132:11: ID ( COLON mio= INT ( TWOPOINTS (mao= INT | N ) )? ( SLASH pe= INT )? )?
                    {
                    ID17=(Token)match(input,ID,FOLLOW_ID_in_subpatterncollection443); 
                    scName = (ID17!=null?ID17.getText():null);
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:133:11: ( COLON mio= INT ( TWOPOINTS (mao= INT | N ) )? ( SLASH pe= INT )? )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==COLON) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:133:12: COLON mio= INT ( TWOPOINTS (mao= INT | N ) )? ( SLASH pe= INT )?
                            {
                            match(input,COLON,FOLLOW_COLON_in_subpatterncollection458); 
                            mio=(Token)match(input,INT,FOLLOW_INT_in_subpatterncollection462); 
                            minOccurrences = Integer.parseInt((mio!=null?mio.getText():null));
                            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:134:14: ( TWOPOINTS (mao= INT | N ) )?
                            int alt8=2;
                            int LA8_0 = input.LA(1);

                            if ( (LA8_0==TWOPOINTS) ) {
                                alt8=1;
                            }
                            switch (alt8) {
                                case 1 :
                                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:134:15: TWOPOINTS (mao= INT | N )
                                    {
                                    match(input,TWOPOINTS,FOLLOW_TWOPOINTS_in_subpatterncollection480); 
                                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:135:18: (mao= INT | N )
                                    int alt7=2;
                                    int LA7_0 = input.LA(1);

                                    if ( (LA7_0==INT) ) {
                                        alt7=1;
                                    }
                                    else if ( (LA7_0==N) ) {
                                        alt7=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 0, input);

                                        throw nvae;
                                    }
                                    switch (alt7) {
                                        case 1 :
                                            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:135:19: mao= INT
                                            {
                                            mao=(Token)match(input,INT,FOLLOW_INT_in_subpatterncollection502); 
                                            maxOccurrences = Integer.parseInt((mao!=null?mao.getText():null));

                                            }
                                            break;
                                        case 2 :
                                            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:135:74: N
                                            {
                                            match(input,N,FOLLOW_N_in_subpatterncollection505); 
                                            maxOccurrences = -1;

                                            }
                                            break;

                                    }


                                    }
                                    break;

                            }

                            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:136:16: ( SLASH pe= INT )?
                            int alt9=2;
                            int LA9_0 = input.LA(1);

                            if ( (LA9_0==SLASH) ) {
                                alt9=1;
                            }
                            switch (alt9) {
                                case 1 :
                                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:136:17: SLASH pe= INT
                                    {
                                    match(input,SLASH,FOLLOW_SLASH_in_subpatterncollection525); 
                                    pe=(Token)match(input,INT,FOLLOW_INT_in_subpatterncollection529); 

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
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:149:1: classPe returns [ClassPatternElement cpe] : ( MINUS )? INT ( UNDERSCORE qname )? ;
    public final ClassPatternElement classPe() throws RecognitionException {
        ClassPatternElement cpe = null;

        Token INT19=null;
        String qname18 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:150:2: ( ( MINUS )? INT ( UNDERSCORE qname )? )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:150:4: ( MINUS )? INT ( UNDERSCORE qname )?
            {

            		String qnameUri = null;
            		boolean qualifying = true;
            		
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:154:3: ( MINUS )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==MINUS) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:154:4: MINUS
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_classPe581); 
                    qualifying = false;

                    }
                    break;

            }

            INT19=(Token)match(input,INT,FOLLOW_INT_in_classPe589); 
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:155:6: ( UNDERSCORE qname )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==UNDERSCORE) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:155:7: UNDERSCORE qname
                    {
                    match(input,UNDERSCORE,FOLLOW_UNDERSCORE_in_classPe591); 
                    pushFollow(FOLLOW_qname_in_classPe593);
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
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:182:1: propertyPe returns [PropertyPatternElement ppe] : ( MINUS )? id= INT ( UNDERSCORE qname )? ( '(' ref= INT ( ',' ref2= INT )* ')' )? ;
    public final PropertyPatternElement propertyPe() throws RecognitionException {
        PropertyPatternElement ppe = null;

        Token id=null;
        Token ref=null;
        Token ref2=null;
        String qname20 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:183:2: ( ( MINUS )? id= INT ( UNDERSCORE qname )? ( '(' ref= INT ( ',' ref2= INT )* ')' )? )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:183:4: ( MINUS )? id= INT ( UNDERSCORE qname )? ( '(' ref= INT ( ',' ref2= INT )* ')' )?
            {

            		String qnameUri = null;
            		boolean qualifying = true;
            		List<Integer> referedElements = new LinkedList<Integer>();
            		
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:188:3: ( MINUS )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==MINUS) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:188:4: MINUS
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_propertyPe621); 
                    qualifying = false;

                    }
                    break;

            }

            id=(Token)match(input,INT,FOLLOW_INT_in_propertyPe631); 
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:189:9: ( UNDERSCORE qname )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==UNDERSCORE) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:189:10: UNDERSCORE qname
                    {
                    match(input,UNDERSCORE,FOLLOW_UNDERSCORE_in_propertyPe633); 
                    pushFollow(FOLLOW_qname_in_propertyPe635);
                    qname20=qname();

                    state._fsp--;

                    qnameUri = qname20;

                    }
                    break;

            }

            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:189:52: ( '(' ref= INT ( ',' ref2= INT )* ')' )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==31) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:189:53: '(' ref= INT ( ',' ref2= INT )* ')'
                    {
                    match(input,31,FOLLOW_31_in_propertyPe640); 
                    ref=(Token)match(input,INT,FOLLOW_INT_in_propertyPe643); 
                    referedElements.add(Integer.parseInt((ref!=null?ref.getText():null)));
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:189:116: ( ',' ref2= INT )*
                    loop16:
                    do {
                        int alt16=2;
                        int LA16_0 = input.LA(1);

                        if ( (LA16_0==32) ) {
                            alt16=1;
                        }


                        switch (alt16) {
                    	case 1 :
                    	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:189:117: ',' ref2= INT
                    	    {
                    	    match(input,32,FOLLOW_32_in_propertyPe648); 
                    	    ref2=(Token)match(input,INT,FOLLOW_INT_in_propertyPe651); 
                    	    referedElements.add(Integer.parseInt((ref2!=null?ref2.getText():null)));

                    	    }
                    	    break;

                    	default :
                    	    break loop16;
                        }
                    } while (true);

                    match(input,33,FOLLOW_33_in_propertyPe657); 

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
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:216:1: qname returns [String uri] : p= ID COLON s= ID ;
    public final String qname() throws RecognitionException {
        String uri = null;

        Token p=null;
        Token s=null;

        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:217:2: (p= ID COLON s= ID )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:217:4: p= ID COLON s= ID
            {
            p=(Token)match(input,ID,FOLLOW_ID_in_qname682); 
            match(input,COLON,FOLLOW_COLON_in_qname684); 
            s=(Token)match(input,ID,FOLLOW_ID_in_qname688); 

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
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:228:1: literalPe returns [LiteralPatternElement lpe] : INT UNDERSCORE LOWERTHAN qname GREATERTHAN ;
    public final LiteralPatternElement literalPe() throws RecognitionException {
        LiteralPatternElement lpe = null;

        Token INT21=null;
        String qname22 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:229:2: ( INT UNDERSCORE LOWERTHAN qname GREATERTHAN )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:229:4: INT UNDERSCORE LOWERTHAN qname GREATERTHAN
            {
            INT21=(Token)match(input,INT,FOLLOW_INT_in_literalPe708); 
            match(input,UNDERSCORE,FOLLOW_UNDERSCORE_in_literalPe710); 
            match(input,LOWERTHAN,FOLLOW_LOWERTHAN_in_literalPe712); 
            pushFollow(FOLLOW_qname_in_literalPe714);
            qname22=qname();

            state._fsp--;

            match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_literalPe716); 

            		try {
            			String key = (INT21!=null?INT21.getText():null);
            			if (pes.get(key) != null) {
            				throw new LiteralException("pattern element " + (INT21!=null?INT21.getText():null) + " refers different resources (literal elements can be used only once)");
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


    // $ANTLR start "sentenceTemplate"
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:263:1: sentenceTemplate[Pattern p] returns [SentenceTemplate st] : SENT ( subsentenceTemplate[p] )+ ENDSENT ;
    public final SentenceTemplate sentenceTemplate(Pattern p) throws RecognitionException {
        SentenceTemplate st = null;

        SubsentenceTemplate subsentenceTemplate23 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:264:2: ( SENT ( subsentenceTemplate[p] )+ ENDSENT )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:264:4: SENT ( subsentenceTemplate[p] )+ ENDSENT
            {
            List<SubsentenceTemplate> sstl = new LinkedList<SubsentenceTemplate>();
            match(input,SENT,FOLLOW_SENT_in_sentenceTemplate765); 
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:266:3: ( subsentenceTemplate[p] )+
            int cnt18=0;
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==MINUS||LA18_0==CITE||LA18_0==35) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:266:4: subsentenceTemplate[p]
            	    {
            	    pushFollow(FOLLOW_subsentenceTemplate_in_sentenceTemplate770);
            	    subsentenceTemplate23=subsentenceTemplate(p);

            	    state._fsp--;

            	    sstl.add(subsentenceTemplate23);

            	    }
            	    break;

            	default :
            	    if ( cnt18 >= 1 ) break loop18;
                        EarlyExitException eee =
                            new EarlyExitException(18, input);
                        throw eee;
                }
                cnt18++;
            } while (true);

            match(input,ENDSENT,FOLLOW_ENDSENT_in_sentenceTemplate778); 
            st = new SentenceTemplate(sstl);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return st;
    }
    // $ANTLR end "sentenceTemplate"


    // $ANTLR start "subsentenceTemplate"
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:271:1: subsentenceTemplate[Pattern p] returns [SubsentenceTemplate sst] : ( peInTemplate[p] | spcInTemplate[p] | staticStringInTemplate | forLoopInTemplate[p] );
    public final SubsentenceTemplate subsentenceTemplate(Pattern p) throws RecognitionException {
        SubsentenceTemplate sst = null;

        PeInTemplate peInTemplate24 = null;

        SpcInTemplate spcInTemplate25 = null;

        StaticStringInTemplate staticStringInTemplate26 = null;

        ForLoopInTemplate forLoopInTemplate27 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:272:2: ( peInTemplate[p] | spcInTemplate[p] | staticStringInTemplate | forLoopInTemplate[p] )
            int alt19=4;
            switch ( input.LA(1) ) {
            case MINUS:
                {
                int LA19_1 = input.LA(2);

                if ( (LA19_1==INT) ) {
                    alt19=1;
                }
                else if ( (LA19_1==ID) ) {
                    alt19=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 19, 1, input);

                    throw nvae;
                }
                }
                break;
            case CITE:
                {
                alt19=3;
                }
                break;
            case 35:
                {
                alt19=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:272:4: peInTemplate[p]
                    {
                    pushFollow(FOLLOW_peInTemplate_in_subsentenceTemplate799);
                    peInTemplate24=peInTemplate(p);

                    state._fsp--;

                    sst = peInTemplate24;

                    }
                    break;
                case 2 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:273:4: spcInTemplate[p]
                    {
                    pushFollow(FOLLOW_spcInTemplate_in_subsentenceTemplate807);
                    spcInTemplate25=spcInTemplate(p);

                    state._fsp--;

                    sst = spcInTemplate25;

                    }
                    break;
                case 3 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:274:4: staticStringInTemplate
                    {
                    pushFollow(FOLLOW_staticStringInTemplate_in_subsentenceTemplate815);
                    staticStringInTemplate26=staticStringInTemplate();

                    state._fsp--;

                    sst = staticStringInTemplate26;

                    }
                    break;
                case 4 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:275:4: forLoopInTemplate[p]
                    {
                    pushFollow(FOLLOW_forLoopInTemplate_in_subsentenceTemplate822);
                    forLoopInTemplate27=forLoopInTemplate(p);

                    state._fsp--;

                    sst = forLoopInTemplate27;

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
        return sst;
    }
    // $ANTLR end "subsentenceTemplate"


    // $ANTLR start "peInTemplate"
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:278:1: peInTemplate[Pattern p] returns [PeInTemplate peit] : '-' INT '-' ;
    public final PeInTemplate peInTemplate(Pattern p) throws RecognitionException {
        PeInTemplate peit = null;

        Token INT28=null;

        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:279:2: ( '-' INT '-' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:279:5: '-' INT '-'
            {
            match(input,MINUS,FOLLOW_MINUS_in_peInTemplate856); 
            INT28=(Token)match(input,INT,FOLLOW_INT_in_peInTemplate857); 
            match(input,MINUS,FOLLOW_MINUS_in_peInTemplate858); 
            peit = new PeInTemplate(p.getPatternElementById(Integer.parseInt((INT28!=null?INT28.getText():null))));

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return peit;
    }
    // $ANTLR end "peInTemplate"


    // $ANTLR start "spcInTemplate"
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:282:1: spcInTemplate[Pattern p] returns [SpcInTemplate spcit] : '-' ID '-[' ( subsentenceTemplate[p] )+ ']' ;
    public final SpcInTemplate spcInTemplate(Pattern p) throws RecognitionException {
        SpcInTemplate spcit = null;

        Token ID30=null;
        SubsentenceTemplate subsentenceTemplate29 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:283:2: ( '-' ID '-[' ( subsentenceTemplate[p] )+ ']' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:283:4: '-' ID '-[' ( subsentenceTemplate[p] )+ ']'
            {
            List<SubsentenceTemplate> sstl = new LinkedList<SubsentenceTemplate>();
            match(input,MINUS,FOLLOW_MINUS_in_spcInTemplate889); 
            ID30=(Token)match(input,ID,FOLLOW_ID_in_spcInTemplate891); 
            match(input,34,FOLLOW_34_in_spcInTemplate893); 
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:284:15: ( subsentenceTemplate[p] )+
            int cnt20=0;
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==MINUS||LA20_0==CITE||LA20_0==35) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:284:16: subsentenceTemplate[p]
            	    {
            	    pushFollow(FOLLOW_subsentenceTemplate_in_spcInTemplate895);
            	    subsentenceTemplate29=subsentenceTemplate(p);

            	    state._fsp--;

            	    sstl.add(subsentenceTemplate29);

            	    }
            	    break;

            	default :
            	    if ( cnt20 >= 1 ) break loop20;
                        EarlyExitException eee =
                            new EarlyExitException(20, input);
                        throw eee;
                }
                cnt20++;
            } while (true);

            match(input,RIGHTSB,FOLLOW_RIGHTSB_in_spcInTemplate900); 
            spcit = new SpcInTemplate(p.getSubpatternCollectionById((ID30!=null?ID30.getText():null)), sstl);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return spcit;
    }
    // $ANTLR end "spcInTemplate"


    // $ANTLR start "staticStringInTemplate"
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:289:1: staticStringInTemplate returns [StaticStringInTemplate ssit] : CITE ;
    public final StaticStringInTemplate staticStringInTemplate() throws RecognitionException {
        StaticStringInTemplate ssit = null;

        Token CITE31=null;

        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:290:2: ( CITE )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:290:5: CITE
            {
            CITE31=(Token)match(input,CITE,FOLLOW_CITE_in_staticStringInTemplate927); 
            String s = (CITE31!=null?CITE31.getText():null); ssit = new StaticStringInTemplate(s.substring(1, s.length()-1));

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ssit;
    }
    // $ANTLR end "staticStringInTemplate"


    // $ANTLR start "forLoopInTemplate"
    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:293:1: forLoopInTemplate[Pattern p] returns [ForLoopInTemplate flit] : '-for-' ID '-[' ( subsentenceTemplate[p] )+ ']' ;
    public final ForLoopInTemplate forLoopInTemplate(Pattern p) throws RecognitionException {
        ForLoopInTemplate flit = null;

        Token ID33=null;
        SubsentenceTemplate subsentenceTemplate32 = null;


        try {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:294:2: ( '-for-' ID '-[' ( subsentenceTemplate[p] )+ ']' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:294:4: '-for-' ID '-[' ( subsentenceTemplate[p] )+ ']'
            {
            List<SubsentenceTemplate> sstl = new LinkedList<SubsentenceTemplate>();
            match(input,35,FOLLOW_35_in_forLoopInTemplate961); 
            ID33=(Token)match(input,ID,FOLLOW_ID_in_forLoopInTemplate963); 
            match(input,34,FOLLOW_34_in_forLoopInTemplate965); 
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:295:19: ( subsentenceTemplate[p] )+
            int cnt21=0;
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==MINUS||LA21_0==CITE||LA21_0==35) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:295:20: subsentenceTemplate[p]
            	    {
            	    pushFollow(FOLLOW_subsentenceTemplate_in_forLoopInTemplate967);
            	    subsentenceTemplate32=subsentenceTemplate(p);

            	    state._fsp--;

            	    sstl.add(subsentenceTemplate32);

            	    }
            	    break;

            	default :
            	    if ( cnt21 >= 1 ) break loop21;
                        EarlyExitException eee =
                            new EarlyExitException(21, input);
                        throw eee;
                }
                cnt21++;
            } while (true);

            match(input,RIGHTSB,FOLLOW_RIGHTSB_in_forLoopInTemplate972); 
            flit = new ForLoopInTemplate(p.getSubpatternCollectionById((ID33!=null?ID33.getText():null)), sstl);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return flit;
    }
    // $ANTLR end "forLoopInTemplate"

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
    public static final BitSet FOLLOW_QUER_in_pattern253 = new BitSet(new long[]{0x0000000008030000L});
    public static final BitSet FOLLOW_subpattern_in_pattern258 = new BitSet(new long[]{0x0000000008030040L});
    public static final BitSet FOLLOW_ENDQUER_in_pattern268 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_sentenceTemplate_in_pattern276 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ENDPAT_in_pattern284 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_patterntriple_in_subpattern307 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_subpatterncollection_in_subpattern314 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_e1_in_patterntriple333 = new BitSet(new long[]{0x0000000008010000L});
    public static final BitSet FOLLOW_e2_in_patterntriple335 = new BitSet(new long[]{0x0000000008010000L});
    public static final BitSet FOLLOW_e3_in_patterntriple337 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_patterntriple339 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classPe_in_e1358 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_propertyPe_in_e2376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classPe_in_e3394 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalPe_in_e3401 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTSB_in_subpatterncollection424 = new BitSet(new long[]{0x0000000008030000L});
    public static final BitSet FOLLOW_subpattern_in_subpatterncollection426 = new BitSet(new long[]{0x0000000008070000L});
    public static final BitSet FOLLOW_RIGHTSB_in_subpatterncollection431 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_ID_in_subpatterncollection443 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_COLON_in_subpatterncollection458 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_INT_in_subpatterncollection462 = new BitSet(new long[]{0x0000000001800002L});
    public static final BitSet FOLLOW_TWOPOINTS_in_subpatterncollection480 = new BitSet(new long[]{0x0000000008100000L});
    public static final BitSet FOLLOW_INT_in_subpatterncollection502 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_N_in_subpatterncollection505 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_SLASH_in_subpatterncollection525 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_INT_in_subpatterncollection529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_classPe581 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_INT_in_classPe589 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_UNDERSCORE_in_classPe591 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_qname_in_classPe593 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_propertyPe621 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_INT_in_propertyPe631 = new BitSet(new long[]{0x0000000080004002L});
    public static final BitSet FOLLOW_UNDERSCORE_in_propertyPe633 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_qname_in_propertyPe635 = new BitSet(new long[]{0x0000000080000002L});
    public static final BitSet FOLLOW_31_in_propertyPe640 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_INT_in_propertyPe643 = new BitSet(new long[]{0x0000000300000000L});
    public static final BitSet FOLLOW_32_in_propertyPe648 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_INT_in_propertyPe651 = new BitSet(new long[]{0x0000000300000000L});
    public static final BitSet FOLLOW_33_in_propertyPe657 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_qname682 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_COLON_in_qname684 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_ID_in_qname688 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literalPe708 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_UNDERSCORE_in_literalPe710 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_LOWERTHAN_in_literalPe712 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_qname_in_literalPe714 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_GREATERTHAN_in_literalPe716 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SENT_in_sentenceTemplate765 = new BitSet(new long[]{0x0000000804010000L});
    public static final BitSet FOLLOW_subsentenceTemplate_in_sentenceTemplate770 = new BitSet(new long[]{0x0000000804010020L});
    public static final BitSet FOLLOW_ENDSENT_in_sentenceTemplate778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_peInTemplate_in_subsentenceTemplate799 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_spcInTemplate_in_subsentenceTemplate807 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_staticStringInTemplate_in_subsentenceTemplate815 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forLoopInTemplate_in_subsentenceTemplate822 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_peInTemplate856 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_INT_in_peInTemplate857 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_MINUS_in_peInTemplate858 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_spcInTemplate889 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_ID_in_spcInTemplate891 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_spcInTemplate893 = new BitSet(new long[]{0x0000000804010000L});
    public static final BitSet FOLLOW_subsentenceTemplate_in_spcInTemplate895 = new BitSet(new long[]{0x0000000804050000L});
    public static final BitSet FOLLOW_RIGHTSB_in_spcInTemplate900 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CITE_in_staticStringInTemplate927 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_forLoopInTemplate961 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_ID_in_forLoopInTemplate963 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_forLoopInTemplate965 = new BitSet(new long[]{0x0000000804010000L});
    public static final BitSet FOLLOW_subsentenceTemplate_in_forLoopInTemplate967 = new BitSet(new long[]{0x0000000804050000L});
    public static final BitSet FOLLOW_RIGHTSB_in_forLoopInTemplate972 = new BitSet(new long[]{0x0000000000000002L});

}