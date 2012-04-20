// $ANTLR 3.3 Nov 30, 2010 12:45:30 /home/camille/ANTLRWorks/nlToPivotGrammar.g 2011-12-06 00:34:30

package  org.swip.nlToPivot;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class nlToPivotGrammarParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "COLON", "INT", "WORDS", "WS", "'\\'_\\'(e'", "')'", "'realisation(e'", "', offsets('", "', '", "'))'", "'qual(e'", "', e'", "'of(e'", "'by(e'", "'with(e'", "'count(e'", "'adj(e'", "'ne_tag(e'", "'number(e'", "'plural'", "'sing'", "'time(e'", "'present'", "'past'", "'none'", "'aspect(e'", "'simple'", "'voice(e'", "'active'", "'passive'", "'lobj(e'", "'lsubj(e'", "'semantic annotation '"
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
    // delegators


        public nlToPivotGrammarParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public nlToPivotGrammarParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return nlToPivotGrammarParser.tokenNames; }
    public String getGrammarFileName() { return "/home/camille/ANTLRWorks/nlToPivotGrammar.g"; }


    	PivotQueryGraph pqg = new PivotQueryGraph();



    // $ANTLR start "qlfs"
    // /home/camille/ANTLRWorks/nlToPivotGrammar.g:18:1: qlfs returns [PivotQueryGraph pivotQueryGraph] : ( qlf )+ EOF ;
    public final PivotQueryGraph qlfs() throws RecognitionException {
        PivotQueryGraph pivotQueryGraph = null;

        try {
            // /home/camille/ANTLRWorks/nlToPivotGrammar.g:19:2: ( ( qlf )+ EOF )
            // /home/camille/ANTLRWorks/nlToPivotGrammar.g:19:4: ( qlf )+ EOF
            {
            pivotQueryGraph = pqg;
            // /home/camille/ANTLRWorks/nlToPivotGrammar.g:20:3: ( qlf )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==8||LA1_0==10||LA1_0==14||(LA1_0>=16 && LA1_0<=22)||LA1_0==25||LA1_0==29||LA1_0==31||(LA1_0>=34 && LA1_0<=36)) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/nlToPivotGrammar.g:20:3: qlf
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
        }
        return pivotQueryGraph;
    }
    // $ANTLR end "qlfs"


    // $ANTLR start "qlf"
    // /home/camille/ANTLRWorks/nlToPivotGrammar.g:23:1: qlf : ( '\\'_\\'(e' id= INT ')' | 'realisation(e' id= INT ', offsets(' start= INT ', ' end= INT '))' | 'qual(e' id1= INT ', e' id2= INT ')' | 'of(e' id1= INT ', e' id2= INT ')' | 'by(e' id1= INT ', e' id2= INT ')' | 'with(e' id1= INT ', e' id2= INT ')' | 'count(e' id1= INT ', ' id2= INT ')' | 'adj(e' id1= INT ', ' adjValue= WORDS ')' | 'ne_tag(e' INT ', offsets(' INT ', ' INT '))' | 'number(e' INT ', ' ( 'plural' | 'sing' ) ')' | 'time(e' INT ', ' ( 'present' | 'past' | 'none' ) ')' | 'aspect(e' INT ', ' ( 'simple' ) ')' | 'voice(e' INT ', ' ( 'active' | 'passive' ) ')' | 'lobj(e' id1= INT ', e' id2= INT ')' | 'lsubj(e' id1= INT ', e' id2= INT ')' | 'semantic annotation ' INT );
    public final void qlf() throws RecognitionException {
        Token id=null;
        Token start=null;
        Token end=null;
        Token id1=null;
        Token id2=null;
        Token adjValue=null;

        try {
            // /home/camille/ANTLRWorks/nlToPivotGrammar.g:23:5: ( '\\'_\\'(e' id= INT ')' | 'realisation(e' id= INT ', offsets(' start= INT ', ' end= INT '))' | 'qual(e' id1= INT ', e' id2= INT ')' | 'of(e' id1= INT ', e' id2= INT ')' | 'by(e' id1= INT ', e' id2= INT ')' | 'with(e' id1= INT ', e' id2= INT ')' | 'count(e' id1= INT ', ' id2= INT ')' | 'adj(e' id1= INT ', ' adjValue= WORDS ')' | 'ne_tag(e' INT ', offsets(' INT ', ' INT '))' | 'number(e' INT ', ' ( 'plural' | 'sing' ) ')' | 'time(e' INT ', ' ( 'present' | 'past' | 'none' ) ')' | 'aspect(e' INT ', ' ( 'simple' ) ')' | 'voice(e' INT ', ' ( 'active' | 'passive' ) ')' | 'lobj(e' id1= INT ', e' id2= INT ')' | 'lsubj(e' id1= INT ', e' id2= INT ')' | 'semantic annotation ' INT )
            int alt2=16;
            switch ( input.LA(1) ) {
            case 8:
                {
                alt2=1;
                }
                break;
            case 10:
                {
                alt2=2;
                }
                break;
            case 14:
                {
                alt2=3;
                }
                break;
            case 16:
                {
                alt2=4;
                }
                break;
            case 17:
                {
                alt2=5;
                }
                break;
            case 18:
                {
                alt2=6;
                }
                break;
            case 19:
                {
                alt2=7;
                }
                break;
            case 20:
                {
                alt2=8;
                }
                break;
            case 21:
                {
                alt2=9;
                }
                break;
            case 22:
                {
                alt2=10;
                }
                break;
            case 25:
                {
                alt2=11;
                }
                break;
            case 29:
                {
                alt2=12;
                }
                break;
            case 31:
                {
                alt2=13;
                }
                break;
            case 34:
                {
                alt2=14;
                }
                break;
            case 35:
                {
                alt2=15;
                }
                break;
            case 36:
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
                    // /home/camille/ANTLRWorks/nlToPivotGrammar.g:23:7: '\\'_\\'(e' id= INT ')'
                    {
                    match(input,8,FOLLOW_8_in_qlf66); 
                    id=(Token)match(input,INT,FOLLOW_INT_in_qlf69); 
                    match(input,9,FOLLOW_9_in_qlf70); 

                    }
                    break;
                case 2 :
                    // /home/camille/ANTLRWorks/nlToPivotGrammar.g:25:4: 'realisation(e' id= INT ', offsets(' start= INT ', ' end= INT '))'
                    {
                    match(input,10,FOLLOW_10_in_qlf78); 
                    id=(Token)match(input,INT,FOLLOW_INT_in_qlf81); 
                    match(input,11,FOLLOW_11_in_qlf82); 
                    start=(Token)match(input,INT,FOLLOW_INT_in_qlf85); 
                    match(input,12,FOLLOW_12_in_qlf87); 
                    end=(Token)match(input,INT,FOLLOW_INT_in_qlf91); 
                    match(input,13,FOLLOW_13_in_qlf92); 
                    pqg.addQuerySubstring("e"+(id!=null?id.getText():null), Integer.parseInt((start!=null?start.getText():null)), Integer.parseInt((end!=null?end.getText():null)));

                    }
                    break;
                case 3 :
                    // /home/camille/ANTLRWorks/nlToPivotGrammar.g:27:4: 'qual(e' id1= INT ', e' id2= INT ')'
                    {
                    match(input,14,FOLLOW_14_in_qlf102); 
                    id1=(Token)match(input,INT,FOLLOW_INT_in_qlf105); 
                    match(input,15,FOLLOW_15_in_qlf107); 
                    id2=(Token)match(input,INT,FOLLOW_INT_in_qlf110); 
                    match(input,9,FOLLOW_9_in_qlf111); 
                    pqg.addQ2("e"+(id2!=null?id2.getText():null), "e"+(id1!=null?id1.getText():null));

                    }
                    break;
                case 4 :
                    // /home/camille/ANTLRWorks/nlToPivotGrammar.g:29:4: 'of(e' id1= INT ', e' id2= INT ')'
                    {
                    match(input,16,FOLLOW_16_in_qlf121); 
                    id1=(Token)match(input,INT,FOLLOW_INT_in_qlf124); 
                    match(input,15,FOLLOW_15_in_qlf126); 
                    id2=(Token)match(input,INT,FOLLOW_INT_in_qlf129); 
                    match(input,9,FOLLOW_9_in_qlf130); 
                    pqg.addQ2("e"+(id2!=null?id2.getText():null), "e"+(id1!=null?id1.getText():null));

                    }
                    break;
                case 5 :
                    // /home/camille/ANTLRWorks/nlToPivotGrammar.g:31:4: 'by(e' id1= INT ', e' id2= INT ')'
                    {
                    match(input,17,FOLLOW_17_in_qlf140); 
                    id1=(Token)match(input,INT,FOLLOW_INT_in_qlf143); 
                    match(input,15,FOLLOW_15_in_qlf145); 
                    id2=(Token)match(input,INT,FOLLOW_INT_in_qlf148); 
                    match(input,9,FOLLOW_9_in_qlf149); 
                    pqg.addQ2("e"+(id2!=null?id2.getText():null), "e"+(id1!=null?id1.getText():null));

                    }
                    break;
                case 6 :
                    // /home/camille/ANTLRWorks/nlToPivotGrammar.g:33:4: 'with(e' id1= INT ', e' id2= INT ')'
                    {
                    match(input,18,FOLLOW_18_in_qlf159); 
                    id1=(Token)match(input,INT,FOLLOW_INT_in_qlf162); 
                    match(input,15,FOLLOW_15_in_qlf164); 
                    id2=(Token)match(input,INT,FOLLOW_INT_in_qlf167); 
                    match(input,9,FOLLOW_9_in_qlf168); 
                    pqg.addQ2("e"+(id2!=null?id2.getText():null), "e"+(id1!=null?id1.getText():null));

                    }
                    break;
                case 7 :
                    // /home/camille/ANTLRWorks/nlToPivotGrammar.g:35:4: 'count(e' id1= INT ', ' id2= INT ')'
                    {
                    match(input,19,FOLLOW_19_in_qlf178); 
                    id1=(Token)match(input,INT,FOLLOW_INT_in_qlf181); 
                    match(input,12,FOLLOW_12_in_qlf183); 
                    id2=(Token)match(input,INT,FOLLOW_INT_in_qlf186); 
                    match(input,9,FOLLOW_9_in_qlf187); 
                    pqg.setASubstringAsCount( "e"+(id1!=null?id1.getText():null));

                    }
                    break;
                case 8 :
                    // /home/camille/ANTLRWorks/nlToPivotGrammar.g:37:4: 'adj(e' id1= INT ', ' adjValue= WORDS ')'
                    {
                    match(input,20,FOLLOW_20_in_qlf197); 
                    id1=(Token)match(input,INT,FOLLOW_INT_in_qlf200); 
                    match(input,12,FOLLOW_12_in_qlf202); 
                    adjValue=(Token)match(input,WORDS,FOLLOW_WORDS_in_qlf205); 
                    match(input,9,FOLLOW_9_in_qlf206); 
                    pqg.addAdjToSubstring( "e"+(id1!=null?id1.getText():null), (adjValue!=null?adjValue.getText():null));

                    }
                    break;
                case 9 :
                    // /home/camille/ANTLRWorks/nlToPivotGrammar.g:39:4: 'ne_tag(e' INT ', offsets(' INT ', ' INT '))'
                    {
                    match(input,21,FOLLOW_21_in_qlf216); 
                    match(input,INT,FOLLOW_INT_in_qlf217); 
                    match(input,11,FOLLOW_11_in_qlf218); 
                    match(input,INT,FOLLOW_INT_in_qlf219); 
                    match(input,12,FOLLOW_12_in_qlf221); 
                    match(input,INT,FOLLOW_INT_in_qlf222); 
                    match(input,13,FOLLOW_13_in_qlf223); 

                    }
                    break;
                case 10 :
                    // /home/camille/ANTLRWorks/nlToPivotGrammar.g:40:4: 'number(e' INT ', ' ( 'plural' | 'sing' ) ')'
                    {
                    match(input,22,FOLLOW_22_in_qlf228); 
                    match(input,INT,FOLLOW_INT_in_qlf230); 
                    match(input,12,FOLLOW_12_in_qlf232); 
                    if ( (input.LA(1)>=23 && input.LA(1)<=24) ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    match(input,9,FOLLOW_9_in_qlf238); 

                    }
                    break;
                case 11 :
                    // /home/camille/ANTLRWorks/nlToPivotGrammar.g:41:4: 'time(e' INT ', ' ( 'present' | 'past' | 'none' ) ')'
                    {
                    match(input,25,FOLLOW_25_in_qlf243); 
                    match(input,INT,FOLLOW_INT_in_qlf245); 
                    match(input,12,FOLLOW_12_in_qlf247); 
                    if ( (input.LA(1)>=26 && input.LA(1)<=28) ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    match(input,9,FOLLOW_9_in_qlf255); 

                    }
                    break;
                case 12 :
                    // /home/camille/ANTLRWorks/nlToPivotGrammar.g:42:4: 'aspect(e' INT ', ' ( 'simple' ) ')'
                    {
                    match(input,29,FOLLOW_29_in_qlf260); 
                    match(input,INT,FOLLOW_INT_in_qlf262); 
                    match(input,12,FOLLOW_12_in_qlf264); 
                    // /home/camille/ANTLRWorks/nlToPivotGrammar.g:42:23: ( 'simple' )
                    // /home/camille/ANTLRWorks/nlToPivotGrammar.g:42:24: 'simple'
                    {
                    match(input,30,FOLLOW_30_in_qlf266); 

                    }

                    match(input,9,FOLLOW_9_in_qlf268); 

                    }
                    break;
                case 13 :
                    // /home/camille/ANTLRWorks/nlToPivotGrammar.g:43:4: 'voice(e' INT ', ' ( 'active' | 'passive' ) ')'
                    {
                    match(input,31,FOLLOW_31_in_qlf273); 
                    match(input,INT,FOLLOW_INT_in_qlf275); 
                    match(input,12,FOLLOW_12_in_qlf277); 
                    if ( (input.LA(1)>=32 && input.LA(1)<=33) ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    match(input,9,FOLLOW_9_in_qlf283); 

                    }
                    break;
                case 14 :
                    // /home/camille/ANTLRWorks/nlToPivotGrammar.g:44:4: 'lobj(e' id1= INT ', e' id2= INT ')'
                    {
                    match(input,34,FOLLOW_34_in_qlf288); 
                    id1=(Token)match(input,INT,FOLLOW_INT_in_qlf291); 
                    match(input,15,FOLLOW_15_in_qlf293); 
                    id2=(Token)match(input,INT,FOLLOW_INT_in_qlf296); 
                    match(input,9,FOLLOW_9_in_qlf297); 

                    }
                    break;
                case 15 :
                    // /home/camille/ANTLRWorks/nlToPivotGrammar.g:45:4: 'lsubj(e' id1= INT ', e' id2= INT ')'
                    {
                    match(input,35,FOLLOW_35_in_qlf302); 
                    id1=(Token)match(input,INT,FOLLOW_INT_in_qlf305); 
                    match(input,15,FOLLOW_15_in_qlf307); 
                    id2=(Token)match(input,INT,FOLLOW_INT_in_qlf310); 
                    match(input,9,FOLLOW_9_in_qlf311); 

                    }
                    break;
                case 16 :
                    // /home/camille/ANTLRWorks/nlToPivotGrammar.g:46:4: 'semantic annotation ' INT
                    {
                    match(input,36,FOLLOW_36_in_qlf316); 
                    match(input,INT,FOLLOW_INT_in_qlf317); 

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
        return ;
    }
    // $ANTLR end "qlf"

    // Delegated rules


 

    public static final BitSet FOLLOW_qlf_in_qlfs52 = new BitSet(new long[]{0x0000001CA27F4500L});
    public static final BitSet FOLLOW_EOF_in_qlfs55 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_8_in_qlf66 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf69 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_9_in_qlf70 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_10_in_qlf78 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf81 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_11_in_qlf82 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf85 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_qlf87 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf91 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_qlf92 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_qlf102 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf105 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_qlf107 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf110 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_9_in_qlf111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_qlf121 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf124 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_qlf126 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf129 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_9_in_qlf130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_qlf140 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf143 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_qlf145 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf148 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_9_in_qlf149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_qlf159 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf162 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_qlf164 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf167 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_9_in_qlf168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_qlf178 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf181 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_qlf183 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf186 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_9_in_qlf187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_qlf197 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf200 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_qlf202 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_WORDS_in_qlf205 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_9_in_qlf206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_qlf216 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf217 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_11_in_qlf218 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf219 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_qlf221 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf222 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_qlf223 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_qlf228 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf230 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_qlf232 = new BitSet(new long[]{0x0000000001800000L});
    public static final BitSet FOLLOW_set_in_qlf233 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_9_in_qlf238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_qlf243 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf245 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_qlf247 = new BitSet(new long[]{0x000000001C000000L});
    public static final BitSet FOLLOW_set_in_qlf248 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_9_in_qlf255 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_qlf260 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf262 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_qlf264 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_qlf266 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_9_in_qlf268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_qlf273 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf275 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_qlf277 = new BitSet(new long[]{0x0000000300000000L});
    public static final BitSet FOLLOW_set_in_qlf278 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_9_in_qlf283 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_qlf288 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf291 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_qlf293 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf296 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_9_in_qlf297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_qlf302 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf305 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_qlf307 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf310 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_9_in_qlf311 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_qlf316 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_qlf317 = new BitSet(new long[]{0x0000000000000002L});

}