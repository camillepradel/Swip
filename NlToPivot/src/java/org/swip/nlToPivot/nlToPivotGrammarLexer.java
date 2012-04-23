// $ANTLR 3.4 /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g 2012-04-17 09:15:35
package  org.swip.nlToPivot;

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class nlToPivotGrammarLexer extends Lexer {
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
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public nlToPivotGrammarLexer() {} 
    public nlToPivotGrammarLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public nlToPivotGrammarLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "/Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g"; }

    // $ANTLR start "COLON"
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:4:7: ( ',' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:4:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COLON"

    // $ANTLR start "T__8"
    public final void mT__8() throws RecognitionException {
        try {
            int _type = T__8;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:5:6: ( ')' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:5:8: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__8"

    // $ANTLR start "T__9"
    public final void mT__9() throws RecognitionException {
        try {
            int _type = T__9;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:6:6: ( '))' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:6:8: '))'
            {
            match("))"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__9"

    // $ANTLR start "T__10"
    public final void mT__10() throws RecognitionException {
        try {
            int _type = T__10;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:7:7: ( ', ' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:7:9: ', '
            {
            match(", "); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__10"

    // $ANTLR start "T__11"
    public final void mT__11() throws RecognitionException {
        try {
            int _type = T__11;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:8:7: ( ', e' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:8:9: ', e'
            {
            match(", e"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__11"

    // $ANTLR start "T__12"
    public final void mT__12() throws RecognitionException {
        try {
            int _type = T__12;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:9:7: ( ', offsets(' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:9:9: ', offsets('
            {
            match(", offsets("); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__12"

    // $ANTLR start "T__13"
    public final void mT__13() throws RecognitionException {
        try {
            int _type = T__13;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:10:7: ( '\\'_\\'(e' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:10:9: '\\'_\\'(e'
            {
            match("'_'(e"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__13"

    // $ANTLR start "T__14"
    public final void mT__14() throws RecognitionException {
        try {
            int _type = T__14;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:11:7: ( 'active' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:11:9: 'active'
            {
            match("active"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__14"

    // $ANTLR start "T__15"
    public final void mT__15() throws RecognitionException {
        try {
            int _type = T__15;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:12:7: ( 'adj(e' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:12:9: 'adj(e'
            {
            match("adj(e"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__15"

    // $ANTLR start "T__16"
    public final void mT__16() throws RecognitionException {
        try {
            int _type = T__16;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:13:7: ( 'aspect(e' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:13:9: 'aspect(e'
            {
            match("aspect(e"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__16"

    // $ANTLR start "T__17"
    public final void mT__17() throws RecognitionException {
        try {
            int _type = T__17;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:14:7: ( 'by(e' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:14:9: 'by(e'
            {
            match("by(e"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__17"

    // $ANTLR start "T__18"
    public final void mT__18() throws RecognitionException {
        try {
            int _type = T__18;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:15:7: ( 'count(e' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:15:9: 'count(e'
            {
            match("count(e"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__18"

    // $ANTLR start "T__19"
    public final void mT__19() throws RecognitionException {
        try {
            int _type = T__19;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:16:7: ( 'lobj(e' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:16:9: 'lobj(e'
            {
            match("lobj(e"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__19"

    // $ANTLR start "T__20"
    public final void mT__20() throws RecognitionException {
        try {
            int _type = T__20;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:17:7: ( 'lsubj(e' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:17:9: 'lsubj(e'
            {
            match("lsubj(e"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__20"

    // $ANTLR start "T__21"
    public final void mT__21() throws RecognitionException {
        try {
            int _type = T__21;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:18:7: ( 'ne_tag(e' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:18:9: 'ne_tag(e'
            {
            match("ne_tag(e"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__21"

    // $ANTLR start "T__22"
    public final void mT__22() throws RecognitionException {
        try {
            int _type = T__22;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:19:7: ( 'none' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:19:9: 'none'
            {
            match("none"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__22"

    // $ANTLR start "T__23"
    public final void mT__23() throws RecognitionException {
        try {
            int _type = T__23;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:20:7: ( 'number(e' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:20:9: 'number(e'
            {
            match("number(e"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__23"

    // $ANTLR start "T__24"
    public final void mT__24() throws RecognitionException {
        try {
            int _type = T__24;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:21:7: ( 'of(e' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:21:9: 'of(e'
            {
            match("of(e"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__24"

    // $ANTLR start "T__25"
    public final void mT__25() throws RecognitionException {
        try {
            int _type = T__25;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:22:7: ( 'passive' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:22:9: 'passive'
            {
            match("passive"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__25"

    // $ANTLR start "T__26"
    public final void mT__26() throws RecognitionException {
        try {
            int _type = T__26;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:23:7: ( 'past' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:23:9: 'past'
            {
            match("past"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__26"

    // $ANTLR start "T__27"
    public final void mT__27() throws RecognitionException {
        try {
            int _type = T__27;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:24:7: ( 'plural' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:24:9: 'plural'
            {
            match("plural"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__27"

    // $ANTLR start "T__28"
    public final void mT__28() throws RecognitionException {
        try {
            int _type = T__28;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:25:7: ( 'present' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:25:9: 'present'
            {
            match("present"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__28"

    // $ANTLR start "T__29"
    public final void mT__29() throws RecognitionException {
        try {
            int _type = T__29;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:26:7: ( 'qual(e' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:26:9: 'qual(e'
            {
            match("qual(e"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__29"

    // $ANTLR start "T__30"
    public final void mT__30() throws RecognitionException {
        try {
            int _type = T__30;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:27:7: ( 'realisation(e' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:27:9: 'realisation(e'
            {
            match("realisation(e"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__30"

    // $ANTLR start "T__31"
    public final void mT__31() throws RecognitionException {
        try {
            int _type = T__31;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:28:7: ( 'semantic annotation ' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:28:9: 'semantic annotation '
            {
            match("semantic annotation "); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__31"

    // $ANTLR start "T__32"
    public final void mT__32() throws RecognitionException {
        try {
            int _type = T__32;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:29:7: ( 'simple' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:29:9: 'simple'
            {
            match("simple"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__32"

    // $ANTLR start "T__33"
    public final void mT__33() throws RecognitionException {
        try {
            int _type = T__33;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:30:7: ( 'sing' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:30:9: 'sing'
            {
            match("sing"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__33"

    // $ANTLR start "T__34"
    public final void mT__34() throws RecognitionException {
        try {
            int _type = T__34;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:31:7: ( 'time(e' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:31:9: 'time(e'
            {
            match("time(e"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__34"

    // $ANTLR start "T__35"
    public final void mT__35() throws RecognitionException {
        try {
            int _type = T__35;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:32:7: ( 'voice(e' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:32:9: 'voice(e'
            {
            match("voice(e"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__35"

    // $ANTLR start "T__36"
    public final void mT__36() throws RecognitionException {
        try {
            int _type = T__36;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:33:7: ( 'with(e' )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:33:9: 'with(e'
            {
            match("with(e"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__36"

    // $ANTLR start "WORDS"
    public final void mWORDS() throws RecognitionException {
        try {
            int _type = WORDS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:51:7: ( ( 'a' .. 'z' | 'A' .. 'Z' | ' ' | '-' )+ )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:51:9: ( 'a' .. 'z' | 'A' .. 'Z' | ' ' | '-' )+
            {
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:51:9: ( 'a' .. 'z' | 'A' .. 'Z' | ' ' | '-' )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==' '||LA1_0=='-'||(LA1_0 >= 'A' && LA1_0 <= 'Z')||(LA1_0 >= 'a' && LA1_0 <= 'z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:
            	    {
            	    if ( input.LA(1)==' '||input.LA(1)=='-'||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


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

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WORDS"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:53:5: ( ( '0' .. '9' )+ )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:53:7: ( '0' .. '9' )+
            {
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:53:7: ( '0' .. '9' )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0 >= '0' && LA2_0 <= '9')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


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


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:55:5: ( ( ' ' | '\\t' | '\\n' | '\\r' )+ )
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:55:9: ( ' ' | '\\t' | '\\n' | '\\r' )+
            {
            // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:55:9: ( ' ' | '\\t' | '\\n' | '\\r' )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0 >= '\t' && LA3_0 <= '\n')||LA3_0=='\r'||LA3_0==' ') ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:
            	    {
            	    if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


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


            skip();

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WS"

    public void mTokens() throws RecognitionException {
        // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:8: ( COLON | T__8 | T__9 | T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | WORDS | INT | WS )
        int alt4=33;
        alt4 = dfa4.predict(input);
        switch (alt4) {
            case 1 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:10: COLON
                {
                mCOLON(); 


                }
                break;
            case 2 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:16: T__8
                {
                mT__8(); 


                }
                break;
            case 3 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:21: T__9
                {
                mT__9(); 


                }
                break;
            case 4 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:26: T__10
                {
                mT__10(); 


                }
                break;
            case 5 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:32: T__11
                {
                mT__11(); 


                }
                break;
            case 6 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:38: T__12
                {
                mT__12(); 


                }
                break;
            case 7 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:44: T__13
                {
                mT__13(); 


                }
                break;
            case 8 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:50: T__14
                {
                mT__14(); 


                }
                break;
            case 9 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:56: T__15
                {
                mT__15(); 


                }
                break;
            case 10 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:62: T__16
                {
                mT__16(); 


                }
                break;
            case 11 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:68: T__17
                {
                mT__17(); 


                }
                break;
            case 12 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:74: T__18
                {
                mT__18(); 


                }
                break;
            case 13 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:80: T__19
                {
                mT__19(); 


                }
                break;
            case 14 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:86: T__20
                {
                mT__20(); 


                }
                break;
            case 15 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:92: T__21
                {
                mT__21(); 


                }
                break;
            case 16 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:98: T__22
                {
                mT__22(); 


                }
                break;
            case 17 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:104: T__23
                {
                mT__23(); 


                }
                break;
            case 18 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:110: T__24
                {
                mT__24(); 


                }
                break;
            case 19 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:116: T__25
                {
                mT__25(); 


                }
                break;
            case 20 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:122: T__26
                {
                mT__26(); 


                }
                break;
            case 21 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:128: T__27
                {
                mT__27(); 


                }
                break;
            case 22 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:134: T__28
                {
                mT__28(); 


                }
                break;
            case 23 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:140: T__29
                {
                mT__29(); 


                }
                break;
            case 24 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:146: T__30
                {
                mT__30(); 


                }
                break;
            case 25 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:152: T__31
                {
                mT__31(); 


                }
                break;
            case 26 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:158: T__32
                {
                mT__32(); 


                }
                break;
            case 27 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:164: T__33
                {
                mT__33(); 


                }
                break;
            case 28 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:170: T__34
                {
                mT__34(); 


                }
                break;
            case 29 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:176: T__35
                {
                mT__35(); 


                }
                break;
            case 30 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:182: T__36
                {
                mT__36(); 


                }
                break;
            case 31 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:188: WORDS
                {
                mWORDS(); 


                }
                break;
            case 32 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:194: INT
                {
                mINT(); 


                }
                break;
            case 33 :
                // /Users/Murloc/Documents/IRIT/grammaires/nlToPivotGrammar.g:1:198: WS
                {
                mWS(); 


                }
                break;

        }

    }


    protected DFA4 dfa4 = new DFA4(this);
    static final String DFA4_eotS =
        "\1\uffff\1\26\1\30\1\uffff\16\23\3\uffff\1\60\3\uffff\25\23\3\uffff"+
        "\3\23\1\uffff\3\23\1\uffff\2\23\1\uffff\14\23\1\uffff\4\23\1\140"+
        "\2\23\1\143\6\23\1\152\6\23\1\uffff\1\23\1\uffff\2\23\1\uffff\2"+
        "\23\1\uffff\3\23\2\uffff\1\23\1\uffff\1\172\1\23\2\uffff\2\23\1"+
        "\176\3\23\1\u0082\4\uffff\1\u0083\1\uffff\1\u0084\2\23\3\uffff\10"+
        "\23\1\uffff\10\23\1\u0097\1\uffff";
    static final String DFA4_eofS =
        "\u0098\uffff";
    static final String DFA4_minS =
        "\1\11\1\40\1\51\1\uffff\1\143\1\171\2\157\1\145\1\146\1\141\1\165"+
        "\2\145\1\151\1\157\1\151\1\11\3\uffff\1\145\3\uffff\1\164\1\152"+
        "\1\160\1\50\1\165\1\142\1\165\1\137\1\156\1\155\1\50\1\163\1\165"+
        "\1\145\2\141\3\155\1\151\1\164\3\uffff\1\151\1\50\1\145\1\uffff"+
        "\1\156\1\152\1\142\1\uffff\1\145\1\142\1\uffff\1\163\1\162\1\163"+
        "\2\154\1\141\1\160\1\147\1\145\1\143\1\150\1\166\1\uffff\1\143\1"+
        "\164\1\50\1\152\1\40\1\145\1\151\1\40\1\141\1\145\1\50\1\151\1\156"+
        "\1\154\1\40\1\50\1\145\1\50\1\145\1\164\1\50\1\uffff\1\50\1\uffff"+
        "\1\162\1\166\1\uffff\1\154\1\156\1\uffff\1\163\1\164\1\145\2\uffff"+
        "\1\50\1\uffff\1\40\1\50\2\uffff\1\50\1\145\1\40\1\164\1\141\1\151"+
        "\1\40\4\uffff\1\40\1\uffff\1\40\1\164\1\143\3\uffff\1\151\1\40\1"+
        "\157\1\141\2\156\1\50\1\156\1\uffff\1\157\1\164\1\141\1\164\1\151"+
        "\1\157\1\156\2\40\1\uffff";
    static final String DFA4_maxS =
        "\1\172\1\40\1\51\1\uffff\1\163\1\171\1\157\1\163\1\165\1\146\1\162"+
        "\1\165\1\145\2\151\1\157\1\151\1\40\3\uffff\1\157\3\uffff\1\164"+
        "\1\152\1\160\1\50\1\165\1\142\1\165\1\137\1\156\1\155\1\50\1\163"+
        "\1\165\1\145\2\141\1\155\1\156\1\155\1\151\1\164\3\uffff\1\151\1"+
        "\50\1\145\1\uffff\1\156\1\152\1\142\1\uffff\1\145\1\142\1\uffff"+
        "\1\164\1\162\1\163\2\154\1\141\1\160\1\147\1\145\1\143\1\150\1\166"+
        "\1\uffff\1\143\1\164\1\50\1\152\1\172\1\145\1\151\1\172\1\141\1"+
        "\145\1\50\1\151\1\156\1\154\1\172\1\50\1\145\1\50\1\145\1\164\1"+
        "\50\1\uffff\1\50\1\uffff\1\162\1\166\1\uffff\1\154\1\156\1\uffff"+
        "\1\163\1\164\1\145\2\uffff\1\50\1\uffff\1\172\1\50\2\uffff\1\50"+
        "\1\145\1\172\1\164\1\141\1\151\1\172\4\uffff\1\172\1\uffff\1\172"+
        "\1\164\1\143\3\uffff\1\151\1\40\1\157\1\141\2\156\1\50\1\156\1\uffff"+
        "\1\157\1\164\1\141\1\164\1\151\1\157\1\156\1\40\1\172\1\uffff";
    static final String DFA4_acceptS =
        "\3\uffff\1\7\16\uffff\1\40\1\37\1\41\1\uffff\1\1\1\3\1\2\25\uffff"+
        "\1\5\1\6\1\4\3\uffff\1\13\3\uffff\1\17\2\uffff\1\22\14\uffff\1\11"+
        "\25\uffff\1\15\1\uffff\1\20\2\uffff\1\24\2\uffff\1\27\3\uffff\1"+
        "\33\1\34\1\uffff\1\36\2\uffff\1\14\1\16\7\uffff\1\35\1\10\1\12\1"+
        "\21\1\uffff\1\25\3\uffff\1\32\1\23\1\26\10\uffff\1\30\11\uffff\1"+
        "\31";
    static final String DFA4_specialS =
        "\u0098\uffff}>";
    static final String[] DFA4_transitionS = {
            "\2\24\2\uffff\1\24\22\uffff\1\21\6\uffff\1\3\1\uffff\1\2\2\uffff"+
            "\1\1\1\23\2\uffff\12\22\7\uffff\32\23\6\uffff\1\4\1\5\1\6\10"+
            "\23\1\7\1\23\1\10\1\11\1\12\1\13\1\14\1\15\1\16\1\23\1\17\1"+
            "\20\3\23",
            "\1\25",
            "\1\27",
            "",
            "\1\31\1\32\16\uffff\1\33",
            "\1\34",
            "\1\35",
            "\1\36\3\uffff\1\37",
            "\1\40\11\uffff\1\41\5\uffff\1\42",
            "\1\43",
            "\1\44\12\uffff\1\45\5\uffff\1\46",
            "\1\47",
            "\1\50",
            "\1\51\3\uffff\1\52",
            "\1\53",
            "\1\54",
            "\1\55",
            "\2\24\2\uffff\1\24\22\uffff\1\21",
            "",
            "",
            "",
            "\1\56\11\uffff\1\57",
            "",
            "",
            "",
            "\1\61",
            "\1\62",
            "\1\63",
            "\1\64",
            "\1\65",
            "\1\66",
            "\1\67",
            "\1\70",
            "\1\71",
            "\1\72",
            "\1\73",
            "\1\74",
            "\1\75",
            "\1\76",
            "\1\77",
            "\1\100",
            "\1\101",
            "\1\102\1\103",
            "\1\104",
            "\1\105",
            "\1\106",
            "",
            "",
            "",
            "\1\107",
            "\1\110",
            "\1\111",
            "",
            "\1\112",
            "\1\113",
            "\1\114",
            "",
            "\1\115",
            "\1\116",
            "",
            "\1\117\1\120",
            "\1\121",
            "\1\122",
            "\1\123",
            "\1\124",
            "\1\125",
            "\1\126",
            "\1\127",
            "\1\130",
            "\1\131",
            "\1\132",
            "\1\133",
            "",
            "\1\134",
            "\1\135",
            "\1\136",
            "\1\137",
            "\1\23\14\uffff\1\23\23\uffff\32\23\6\uffff\32\23",
            "\1\141",
            "\1\142",
            "\1\23\14\uffff\1\23\23\uffff\32\23\6\uffff\32\23",
            "\1\144",
            "\1\145",
            "\1\146",
            "\1\147",
            "\1\150",
            "\1\151",
            "\1\23\14\uffff\1\23\23\uffff\32\23\6\uffff\32\23",
            "\1\153",
            "\1\154",
            "\1\155",
            "\1\156",
            "\1\157",
            "\1\160",
            "",
            "\1\161",
            "",
            "\1\162",
            "\1\163",
            "",
            "\1\164",
            "\1\165",
            "",
            "\1\166",
            "\1\167",
            "\1\170",
            "",
            "",
            "\1\171",
            "",
            "\1\23\14\uffff\1\23\23\uffff\32\23\6\uffff\32\23",
            "\1\173",
            "",
            "",
            "\1\174",
            "\1\175",
            "\1\23\14\uffff\1\23\23\uffff\32\23\6\uffff\32\23",
            "\1\177",
            "\1\u0080",
            "\1\u0081",
            "\1\23\14\uffff\1\23\23\uffff\32\23\6\uffff\32\23",
            "",
            "",
            "",
            "",
            "\1\23\14\uffff\1\23\23\uffff\32\23\6\uffff\32\23",
            "",
            "\1\23\14\uffff\1\23\23\uffff\32\23\6\uffff\32\23",
            "\1\u0085",
            "\1\u0086",
            "",
            "",
            "",
            "\1\u0087",
            "\1\u0088",
            "\1\u0089",
            "\1\u008a",
            "\1\u008b",
            "\1\u008c",
            "\1\u008d",
            "\1\u008e",
            "",
            "\1\u008f",
            "\1\u0090",
            "\1\u0091",
            "\1\u0092",
            "\1\u0093",
            "\1\u0094",
            "\1\u0095",
            "\1\u0096",
            "\1\23\14\uffff\1\23\23\uffff\32\23\6\uffff\32\23",
            ""
    };

    static final short[] DFA4_eot = DFA.unpackEncodedString(DFA4_eotS);
    static final short[] DFA4_eof = DFA.unpackEncodedString(DFA4_eofS);
    static final char[] DFA4_min = DFA.unpackEncodedStringToUnsignedChars(DFA4_minS);
    static final char[] DFA4_max = DFA.unpackEncodedStringToUnsignedChars(DFA4_maxS);
    static final short[] DFA4_accept = DFA.unpackEncodedString(DFA4_acceptS);
    static final short[] DFA4_special = DFA.unpackEncodedString(DFA4_specialS);
    static final short[][] DFA4_transition;

    static {
        int numStates = DFA4_transitionS.length;
        DFA4_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA4_transition[i] = DFA.unpackEncodedString(DFA4_transitionS[i]);
        }
    }

    class DFA4 extends DFA {

        public DFA4(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 4;
            this.eot = DFA4_eot;
            this.eof = DFA4_eof;
            this.min = DFA4_min;
            this.max = DFA4_max;
            this.accept = DFA4_accept;
            this.special = DFA4_special;
            this.transition = DFA4_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( COLON | T__8 | T__9 | T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | WORDS | INT | WS );";
        }
    }
 

}