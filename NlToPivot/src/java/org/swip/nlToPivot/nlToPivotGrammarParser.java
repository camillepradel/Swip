// $ANTLR 3.4 /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g 2012-04-17 09:15:35

package  org.swip.nlToPivot;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class nlToPivotGrammarParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "COLON", "INT", "WORDS", "WS", "')'", "'))'", "', '", "', e'", "', offsets('", "'\\'_\\'(e'", "'active'", "'adj(e'", "'aspect(e'", "'by(e'", "'count(e'", "'lobj(e'", "'lsubj(e'", "'ne_tag(e'", "'none'", "'number(e'", "'of(e'", "'passive'", "'past'", "'plural'", "'present'", "'qual(e'", "'realisation(e'", "'semantic annotation '", "'simple'", "'sing'", "'time(e'", "'voice(e'", "'with(e'"
    };

    public static final int EOF=-1;
    public static final int T__8=8;
    public static final int T__9=9;
    public static final int T__10=10;
    public static final int T__11=11;
    public static final int T__12=12;
    public static final int T__13=13;
    public static final int T__14=14;
    public static final int T__15=15;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__19=19;
    public static final int T__20=20;
    public static final int T__21=21;
    public static final int T__22=22;
    public static final int T__23=23;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int T__29=29;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int COLON=4;
    public static final int INT=5;
    public static final int WORDS=6;
    public static final int WS=7;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public nlToPivotGrammarParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public nlToPivotGrammarParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return nlToPivotGrammarParser.tokenNames; }
    public String getGrammarFileName() { return "/Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g"; }


    	PivotQueryGraph pqg = new PivotQueryGraph();



    // $ANTLR start "qlfs"
    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:18:1: qlfs returns [PivotQueryGraph pivotQueryGraph] : ( qlf )+ EOF ;
    public final PivotQueryGraph qlfs() throws RecognitionException {
        PivotQueryGraph pivotQueryGraph = null;


        try {
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:19:2: ( ( qlf )+ EOF )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:19:4: ( qlf )+ EOF
            {
            pivotQueryGraph = pqg;

            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:20:3: ( qlf )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==13||(LA1_0 >= 15 && LA1_0 <= 21)||(LA1_0 >= 23 && LA1_0 <= 24)||(LA1_0 >= 29 && LA1_0 <= 31)||(LA1_0 >= 34 && LA1_0 <= 36)) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:20:3: qlf
            	    {
            	    pushFollow(FOLLOW_qlf_in_qlfs52);
            	    qlf();

            	    state._fsp--;


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


            match(input,EOF,FOLLOW_EOF_in_qlfs55); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return pivotQueryGraph;
    }
    // $ANTLR end "qlfs"



    // $ANTLR start "qlf"
    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:23:1: qlf : ( '\\'_\\'(e' id= INT ')' | 'realisation(e' id= INT ', offsets(' start= INT ', ' end= INT '))' | 'qual(e' id1= INT ', e' id2= INT ')' | 'of(e' id1= INT ', e' id2= INT ')' | 'by(e' id1= INT ', e' id2= INT ')' | 'with(e' id1= INT ', e' id2= INT ')' | 'count(e' id1= INT ', ' id2= INT ')' | 'adj(e' id1= INT ', ' adjValue= WORDS ')' | 'ne_tag(e' INT ', offsets(' INT ', ' INT '))' | 'number(e' INT ', ' ( 'plural' | 'sing' ) ')' | 'time(e' INT ', ' ( 'present' | 'past' | 'none' ) ')' | 'aspect(e' INT ', ' ( 'simple' ) ')' | 'voice(e' INT ', ' ( 'active' | 'passive' ) ')' | 'lobj(e' id1= INT ', e' id2= INT ')' | 'lsubj(e' id1= INT ', e' id2= INT ')' | 'semantic annotation ' INT );
    public final void qlf() throws RecognitionException {
        Token id=null;
        Token start=null;
        Token end=null;
        Token id1=null;
        Token id2=null;
        Token adjValue=null;

        try {
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:23:5: ( '\\'_\\'(e' id= INT ')' | 'realisation(e' id= INT ', offsets(' start= INT ', ' end= INT '))' | 'qual(e' id1= INT ', e' id2= INT ')' | 'of(e' id1= INT ', e' id2= INT ')' | 'by(e' id1= INT ', e' id2= INT ')' | 'with(e' id1= INT ', e' id2= INT ')' | 'count(e' id1= INT ', ' id2= INT ')' | 'adj(e' id1= INT ', ' adjValue= WORDS ')' | 'ne_tag(e' INT ', offsets(' INT ', ' INT '))' | 'number(e' INT ', ' ( 'plural' | 'sing' ) ')' | 'time(e' INT ', ' ( 'present' | 'past' | 'none' ) ')' | 'aspect(e' INT ', ' ( 'simple' ) ')' | 'voice(e' INT ', ' ( 'active' | 'passive' ) ')' | 'lobj(e' id1= INT ', e' id2= INT ')' | 'lsubj(e' id1= INT ', e' id2= INT ')' | 'semantic annotation ' INT )
            int alt2=16;
            switch ( input.LA(1) ) {
            case 13:
                {
                alt2=1;
                }
                break;
            case 30:
                {
                alt2=2;
                }
                break;
            case 29:
                {
                alt2=3;
                }
                break;
            case 24:
                {
                alt2=4;
                }
                break;
            case 17:
                {
                alt2=5;
                }
                break;
            case 36:
                {
                alt2=6;
                }
                break;
            case 18:
                {
                alt2=7;
                }
                break;
            case 15:
                {
                alt2=8;
                }
                break;
            case 21:
                {
                alt2=9;
                }
                break;
            case 23:
                {
                alt2=10;
                }
                break;
            case 34:
                {
                alt2=11;
                }
                break;
            case 16:
                {
                alt2=12;
                }
                break;
            case 35:
                {
                alt2=13;
                }
                break;
            case 19:
                {
                alt2=14;
                }
                break;
            case 20:
                {
                alt2=15;
                }
                break;
            case 31:
                {
                alt2=16;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;

            }

            switch (alt2) {
                case 1 :
                    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:23:7: '\\'_\\'(e' id= INT ')'
                    {
                    match(input,13,FOLLOW_13_in_qlf66); 

                    id=(Token)match(input,INT,FOLLOW_INT_in_qlf69); 

                    match(input,8,FOLLOW_8_in_qlf70); 

                    }
                    break;
                case 2 :
                    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:25:4: 'realisation(e' id= INT ', offsets(' start= INT ', ' end= INT '))'
                    {
                    match(input,30,FOLLOW_30_in_qlf78); 

                    id=(Token)match(input,INT,FOLLOW_INT_in_qlf81); 

                    match(input,12,FOLLOW_12_in_qlf82); 

                    start=(Token)match(input,INT,FOLLOW_INT_in_qlf85); 

                    match(input,10,FOLLOW_10_in_qlf87); 

                    end=(Token)match(input,INT,FOLLOW_INT_in_qlf91); 

                    match(input,9,FOLLOW_9_in_qlf92); 

                    pqg.addQuerySubstring("e"+(id!=null?id.getText():null), Integer.parseInt((start!=null?start.getText():null)), Integer.parseInt((end!=null?end.getText():null)));

                    }
                    break;
                case 3 :
                    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:27:4: 'qual(e' id1= INT ', e' id2= INT ')'
                    {
                    match(input,29,FOLLOW_29_in_qlf102); 

                    id1=(Token)match(input,INT,FOLLOW_INT_in_qlf105); 

                    match(input,11,FOLLOW_11_in_qlf107); 

                    id2=(Token)match(input,INT,FOLLOW_INT_in_qlf110); 

                    match(input,8,FOLLOW_8_in_qlf111); 

                    pqg.addQ2("e"+(id2!=null?id2.getText():null), "e"+(id1!=null?id1.getText():null));

                    }
                    break;
                case 4 :
                    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:29:4: 'of(e' id1= INT ', e' id2= INT ')'
                    {
                    match(input,24,FOLLOW_24_in_qlf121); 

                    id1=(Token)match(input,INT,FOLLOW_INT_in_qlf124); 

                    match(input,11,FOLLOW_11_in_qlf126); 

                    id2=(Token)match(input,INT,FOLLOW_INT_in_qlf129); 

                    match(input,8,FOLLOW_8_in_qlf130); 

                    pqg.addQ2("e"+(id2!=null?id2.getText():null), "e"+(id1!=null?id1.getText():null));

                    }
                    break;
                case 5 :
                    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:31:4: 'by(e' id1= INT ', e' id2= INT ')'
                    {
                    match(input,17,FOLLOW_17_in_qlf140); 

                    id1=(Token)match(input,INT,FOLLOW_INT_in_qlf143); 

                    match(input,11,FOLLOW_11_in_qlf145); 

                    id2=(Token)match(input,INT,FOLLOW_INT_in_qlf148); 

                    match(input,8,FOLLOW_8_in_qlf149); 

                    pqg.addQ2("e"+(id2!=null?id2.getText():null), "e"+(id1!=null?id1.getText():null));

                    }
                    break;
                case 6 :
                    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:33:4: 'with(e' id1= INT ', e' id2= INT ')'
                    {
                    match(input,36,FOLLOW_36_in_qlf159); 

                    id1=(Token)match(input,INT,FOLLOW_INT_in_qlf162); 

                    match(input,11,FOLLOW_11_in_qlf164); 

                    id2=(Token)match(input,INT,FOLLOW_INT_in_qlf167); 

                    match(input,8,FOLLOW_8_in_qlf168); 

                    pqg.addQ2("e"+((id1!=null?Integer.valueOf(id1.getText()):0) -1), "e"+(id2!=null?id2.getText():null));

                    }
                    break;
                case 7 :
                    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:35:4: 'count(e' id1= INT ', ' id2= INT ')'
                    {
                    match(input,18,FOLLOW_18_in_qlf178); 

                    id1=(Token)match(input,INT,FOLLOW_INT_in_qlf181); 

                    match(input,10,FOLLOW_10_in_qlf183); 

                    id2=(Token)match(input,INT,FOLLOW_INT_in_qlf186); 

                    match(input,8,FOLLOW_8_in_qlf187); 

                    pqg.setASubstringAsCount( "e"+(id1!=null?id1.getText():null), (id2!=null?Integer.valueOf(id2.getText()):0));

                    }
                    break;
                case 8 :
                    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:37:4: 'adj(e' id1= INT ', ' adjValue= WORDS ')'
                    {
                    match(input,15,FOLLOW_15_in_qlf198); 

                    id1=(Token)match(input,INT,FOLLOW_INT_in_qlf201); 

                    match(input,10,FOLLOW_10_in_qlf203); 

                    adjValue=(Token)match(input,WORDS,FOLLOW_WORDS_in_qlf206); 

                    match(input,8,FOLLOW_8_in_qlf207); 

                    pqg.addAdjToSubstring( "e"+(id1!=null?id1.getText():null), (adjValue!=null?adjValue.getText():null));

                    }
                    break;
                case 9 :
                    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:39:4: 'ne_tag(e' INT ', offsets(' INT ', ' INT '))'
                    {
                    match(input,21,FOLLOW_21_in_qlf217); 

                    match(input,INT,FOLLOW_INT_in_qlf218); 

                    match(input,12,FOLLOW_12_in_qlf219); 

                    match(input,INT,FOLLOW_INT_in_qlf220); 

                    match(input,10,FOLLOW_10_in_qlf222); 

                    match(input,INT,FOLLOW_INT_in_qlf223); 

                    match(input,9,FOLLOW_9_in_qlf224); 

                    }
                    break;
                case 10 :
                    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:40:4: 'number(e' INT ', ' ( 'plural' | 'sing' ) ')'
                    {
                    match(input,23,FOLLOW_23_in_qlf229); 

                    match(input,INT,FOLLOW_INT_in_qlf231); 

                    match(input,10,FOLLOW_10_in_qlf233); 

                    if ( input.LA(1)==27||input.LA(1)==33 ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    match(input,8,FOLLOW_8_in_qlf239); 

                    }
                    break;
                case 11 :
                    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:41:4: 'time(e' INT ', ' ( 'present' | 'past' | 'none' ) ')'
                    {
                    match(input,34,FOLLOW_34_in_qlf244); 

                    match(input,INT,FOLLOW_INT_in_qlf246); 

                    match(input,10,FOLLOW_10_in_qlf248); 

                    if ( input.LA(1)==22||input.LA(1)==26||input.LA(1)==28 ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    match(input,8,FOLLOW_8_in_qlf256); 

                    }
                    break;
                case 12 :
                    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:42:4: 'aspect(e' INT ', ' ( 'simple' ) ')'
                    {
                    match(input,16,FOLLOW_16_in_qlf261); 

                    match(input,INT,FOLLOW_INT_in_qlf263); 

                    match(input,10,FOLLOW_10_in_qlf265); 

                    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:42:23: ( 'simple' )
                    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:42:24: 'simple'
                    {
                    match(input,32,FOLLOW_32_in_qlf267); 

                    }


                    match(input,8,FOLLOW_8_in_qlf269); 

                    }
                    break;
                case 13 :
                    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:43:4: 'voice(e' INT ', ' ( 'active' | 'passive' ) ')'
                    {
                    match(input,35,FOLLOW_35_in_qlf274); 

                    match(input,INT,FOLLOW_INT_in_qlf276); 

                    match(input,10,FOLLOW_10_in_qlf278); 

                    if ( input.LA(1)==14||input.LA(1)==25 ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    match(input,8,FOLLOW_8_in_qlf284); 

                    }
                    break;
                case 14 :
                    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:44:4: 'lobj(e' id1= INT ', e' id2= INT ')'
                    {
                    match(input,19,FOLLOW_19_in_qlf289); 

                    id1=(Token)match(input,INT,FOLLOW_INT_in_qlf292); 

                    match(input,11,FOLLOW_11_in_qlf294); 

                    id2=(Token)match(input,INT,FOLLOW_INT_in_qlf297); 

                    match(input,8,FOLLOW_8_in_qlf298); 

                    }
                    break;
                case 15 :
                    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:45:4: 'lsubj(e' id1= INT ', e' id2= INT ')'
                    {
                    match(input,20,FOLLOW_20_in_qlf303); 

                    id1=(Token)match(input,INT,FOLLOW_INT_in_qlf306); 

                    match(input,11,FOLLOW_11_in_qlf308); 

                    id2=(Token)match(input,INT,FOLLOW_INT_in_qlf311); 

                    match(input,8,FOLLOW_8_in_qlf312); 

                    }
                    break;
                case 16 :
                    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:46:4: 'semantic annotation ' INT
                    {
                    match(input,31,FOLLOW_31_in_qlf317); 

                    match(input,INT,FOLLOW_INT_in_qlf318); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "qlf"

    // Delegated rules


 

    public static final BitSet FOLLOW_qlf_in_qlfs52 = new BitSet(new long[]{0x0000001CE1BFA000L});
    public static final BitSet FOLLOW_EOF_in_qlfs55 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_13_in_qlf66 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf69 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_8_in_qlf70 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_qlf78 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf81 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_qlf82 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf85 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_10_in_qlf87 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf91 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_9_in_qlf92 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_qlf102 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf105 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_11_in_qlf107 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf110 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_8_in_qlf111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_qlf121 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf124 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_11_in_qlf126 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf129 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_8_in_qlf130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_qlf140 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf143 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_11_in_qlf145 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf148 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_8_in_qlf149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_qlf159 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf162 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_11_in_qlf164 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf167 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_8_in_qlf168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_qlf178 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf181 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_10_in_qlf183 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf186 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_8_in_qlf187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_qlf198 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf201 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_10_in_qlf203 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_WORDS_in_qlf206 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_8_in_qlf207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_qlf217 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf218 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_qlf219 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf220 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_10_in_qlf222 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf223 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_9_in_qlf224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_qlf229 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf231 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_10_in_qlf233 = new BitSet(new long[]{0x0000000208000000L});
    public static final BitSet FOLLOW_set_in_qlf234 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_8_in_qlf239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_qlf244 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf246 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_10_in_qlf248 = new BitSet(new long[]{0x0000000014400000L});
    public static final BitSet FOLLOW_set_in_qlf249 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_8_in_qlf256 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_qlf261 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf263 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_10_in_qlf265 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_qlf267 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_8_in_qlf269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_qlf274 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf276 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_10_in_qlf278 = new BitSet(new long[]{0x0000000002004000L});
    public static final BitSet FOLLOW_set_in_qlf279 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_8_in_qlf284 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_qlf289 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf292 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_11_in_qlf294 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf297 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_8_in_qlf298 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_qlf303 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf306 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_11_in_qlf308 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf311 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_8_in_qlf312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_qlf317 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf318 = new BitSet(new long[]{0x0000000000000002L});

}