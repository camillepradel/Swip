// $ANTLR 3.4 /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g 2012-04-19 15:17:29
package  org.swip.pivotToMappings.model.query.antlr;

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class userQueryGrammarLexer extends Lexer {
    public static final int EOF=-1;
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
    public static final int COMMENT=4;
    public static final int ID=5;
    public static final int INT=6;
    public static final int KEYVALUE=7;
    public static final int LITVALUE=8;
    public static final int WS=9;

    // delegates
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public userQueryGrammarLexer() {} 
    public userQueryGrammarLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public userQueryGrammarLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "/Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g"; }

    // $ANTLR start "T__10"
    public final void mT__10() throws RecognitionException {
        try {
            int _type = T__10;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:4:7: ( '(' )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:4:9: '('
            {
            match('('); 

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
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:5:7: ( ')' )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:5:9: ')'
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
    // $ANTLR end "T__11"

    // $ANTLR start "T__12"
    public final void mT__12() throws RecognitionException {
        try {
            int _type = T__12;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:6:7: ( ',' )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:6:9: ','
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
    // $ANTLR end "T__12"

    // $ANTLR start "T__13"
    public final void mT__13() throws RecognitionException {
        try {
            int _type = T__13;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:7:7: ( '.' )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:7:9: '.'
            {
            match('.'); 

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
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:8:7: ( ':' )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:8:9: ':'
            {
            match(':'); 

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
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:9:7: ( ';' )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:9:9: ';'
            {
            match(';'); 

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
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:10:7: ( '<' )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:10:9: '<'
            {
            match('<'); 

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
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:11:7: ( '=' )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:11:9: '='
            {
            match('='); 

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
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:12:7: ( '>' )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:12:9: '>'
            {
            match('>'); 

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
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:13:7: ( '?' )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:13:9: '?'
            {
            match('?'); 

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
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:14:7: ( 'ASK' )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:14:9: 'ASK'
            {
            match("ASK"); 



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
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:15:7: ( 'AVG' )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:15:9: 'AVG'
            {
            match("AVG"); 



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
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:16:7: ( 'COUNT' )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:16:9: 'COUNT'
            {
            match("COUNT"); 



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
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:17:7: ( 'MAX' )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:17:9: 'MAX'
            {
            match("MAX"); 



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
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:18:7: ( 'MIN' )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:18:9: 'MIN'
            {
            match("MIN"); 



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
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:19:7: ( 'SUM' )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:19:9: 'SUM'
            {
            match("SUM"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__25"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:143:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:143:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:143:31: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0 >= '0' && LA1_0 <= '9')||(LA1_0 >= 'A' && LA1_0 <= 'Z')||LA1_0=='_'||(LA1_0 >= 'a' && LA1_0 <= 'z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
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
            	    break loop1;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:146:5: ( ( '0' .. '9' )+ )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:146:7: ( '0' .. '9' )+
            {
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:146:7: ( '0' .. '9' )+
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
            	    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:
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

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:150:5: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' | '/*' ( options {greedy=false; } : . )* '*/' )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0=='/') ) {
                int LA6_1 = input.LA(2);

                if ( (LA6_1=='/') ) {
                    alt6=1;
                }
                else if ( (LA6_1=='*') ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 1, input);

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
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:150:9: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
                    {
                    match("//"); 



                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:150:14: (~ ( '\\n' | '\\r' ) )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( ((LA3_0 >= '\u0000' && LA3_0 <= '\t')||(LA3_0 >= '\u000B' && LA3_0 <= '\f')||(LA3_0 >= '\u000E' && LA3_0 <= '\uFFFF')) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:
                    	    {
                    	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
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
                    	    break loop3;
                        }
                    } while (true);


                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:150:28: ( '\\r' )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0=='\r') ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:150:28: '\\r'
                            {
                            match('\r'); 

                            }
                            break;

                    }


                    match('\n'); 

                    _channel=HIDDEN;

                    }
                    break;
                case 2 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:151:9: '/*' ( options {greedy=false; } : . )* '*/'
                    {
                    match("/*"); 



                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:151:14: ( options {greedy=false; } : . )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0=='*') ) {
                            int LA5_1 = input.LA(2);

                            if ( (LA5_1=='/') ) {
                                alt5=2;
                            }
                            else if ( ((LA5_1 >= '\u0000' && LA5_1 <= '.')||(LA5_1 >= '0' && LA5_1 <= '\uFFFF')) ) {
                                alt5=1;
                            }


                        }
                        else if ( ((LA5_0 >= '\u0000' && LA5_0 <= ')')||(LA5_0 >= '+' && LA5_0 <= '\uFFFF')) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:151:42: .
                    	    {
                    	    matchAny(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop5;
                        }
                    } while (true);


                    match("*/"); 



                    _channel=HIDDEN;

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:154:5: ( ( ' ' | '\\t' | '\\n' | '\\r' )+ )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:154:9: ( ' ' | '\\t' | '\\n' | '\\r' )+
            {
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:154:9: ( ' ' | '\\t' | '\\n' | '\\r' )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( ((LA7_0 >= '\t' && LA7_0 <= '\n')||LA7_0=='\r'||LA7_0==' ') ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:
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
            	    if ( cnt7 >= 1 ) break loop7;
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
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

    // $ANTLR start "KEYVALUE"
    public final void mKEYVALUE() throws RecognitionException {
        try {
            int _type = KEYVALUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:157:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | ' ' )* )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:157:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | ' ' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:157:31: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | ' ' )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==' '||(LA8_0 >= '0' && LA8_0 <= '9')||(LA8_0 >= 'A' && LA8_0 <= 'Z')||LA8_0=='_'||(LA8_0 >= 'a' && LA8_0 <= 'z')) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:
            	    {
            	    if ( input.LA(1)==' '||(input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
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
            	    break loop8;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "KEYVALUE"

    // $ANTLR start "LITVALUE"
    public final void mLITVALUE() throws RecognitionException {
        try {
            int _type = LITVALUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:161:6: ( '<' ( . )* '>' )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:161:8: '<' ( . )* '>'
            {
            match('<'); 

            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:161:12: ( . )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0=='>') ) {
                    alt9=2;
                }
                else if ( ((LA9_0 >= '\u0000' && LA9_0 <= '=')||(LA9_0 >= '?' && LA9_0 <= '\uFFFF')) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:161:12: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);


            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LITVALUE"

    public void mTokens() throws RecognitionException {
        // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:8: ( T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | ID | INT | COMMENT | WS | KEYVALUE | LITVALUE )
        int alt10=22;
        alt10 = dfa10.predict(input);
        switch (alt10) {
            case 1 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:10: T__10
                {
                mT__10(); 


                }
                break;
            case 2 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:16: T__11
                {
                mT__11(); 


                }
                break;
            case 3 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:22: T__12
                {
                mT__12(); 


                }
                break;
            case 4 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:28: T__13
                {
                mT__13(); 


                }
                break;
            case 5 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:34: T__14
                {
                mT__14(); 


                }
                break;
            case 6 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:40: T__15
                {
                mT__15(); 


                }
                break;
            case 7 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:46: T__16
                {
                mT__16(); 


                }
                break;
            case 8 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:52: T__17
                {
                mT__17(); 


                }
                break;
            case 9 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:58: T__18
                {
                mT__18(); 


                }
                break;
            case 10 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:64: T__19
                {
                mT__19(); 


                }
                break;
            case 11 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:70: T__20
                {
                mT__20(); 


                }
                break;
            case 12 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:76: T__21
                {
                mT__21(); 


                }
                break;
            case 13 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:82: T__22
                {
                mT__22(); 


                }
                break;
            case 14 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:88: T__23
                {
                mT__23(); 


                }
                break;
            case 15 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:94: T__24
                {
                mT__24(); 


                }
                break;
            case 16 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:100: T__25
                {
                mT__25(); 


                }
                break;
            case 17 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:106: ID
                {
                mID(); 


                }
                break;
            case 18 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:109: INT
                {
                mINT(); 


                }
                break;
            case 19 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:113: COMMENT
                {
                mCOMMENT(); 


                }
                break;
            case 20 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:121: WS
                {
                mWS(); 


                }
                break;
            case 21 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:124: KEYVALUE
                {
                mKEYVALUE(); 


                }
                break;
            case 22 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:133: LITVALUE
                {
                mLITVALUE(); 


                }
                break;

        }

    }


    protected DFA10 dfa10 = new DFA10(this);
    static final String DFA10_eotS =
        "\7\uffff\1\23\3\uffff\5\30\5\uffff\3\30\2\uffff\4\30\1\44\1\45\1"+
        "\30\1\47\1\50\1\51\2\uffff\1\30\3\uffff\1\53\1\uffff";
    static final String DFA10_eofS =
        "\54\uffff";
    static final String DFA10_minS =
        "\1\11\6\uffff\1\0\3\uffff\5\40\5\uffff\3\40\2\uffff\12\40\2\uffff"+
        "\1\40\3\uffff\1\40\1\uffff";
    static final String DFA10_maxS =
        "\1\172\6\uffff\1\uffff\3\uffff\5\172\5\uffff\3\172\2\uffff\12\172"+
        "\2\uffff\1\172\3\uffff\1\172\1\uffff";
    static final String DFA10_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\uffff\1\10\1\11\1\12\5\uffff"+
        "\1\22\1\23\1\24\1\7\1\26\3\uffff\1\21\1\25\12\uffff\1\13\1\14\1"+
        "\uffff\1\16\1\17\1\20\1\uffff\1\15";
    static final String DFA10_specialS =
        "\7\uffff\1\0\44\uffff}>";
    static final String[] DFA10_transitionS = {
            "\2\22\2\uffff\1\22\22\uffff\1\22\7\uffff\1\1\1\2\2\uffff\1\3"+
            "\1\uffff\1\4\1\21\12\20\1\5\1\6\1\7\1\10\1\11\1\12\1\uffff\1"+
            "\13\1\17\1\14\11\17\1\15\5\17\1\16\7\17\4\uffff\1\17\1\uffff"+
            "\32\17",
            "",
            "",
            "",
            "",
            "",
            "",
            "\0\24",
            "",
            "",
            "",
            "\1\31\17\uffff\12\27\7\uffff\22\27\1\25\2\27\1\26\4\27\4\uffff"+
            "\1\27\1\uffff\32\27",
            "\1\31\17\uffff\12\27\7\uffff\16\27\1\32\13\27\4\uffff\1\27"+
            "\1\uffff\32\27",
            "\1\31\17\uffff\12\27\7\uffff\1\33\7\27\1\34\21\27\4\uffff\1"+
            "\27\1\uffff\32\27",
            "\1\31\17\uffff\12\27\7\uffff\24\27\1\35\5\27\4\uffff\1\27\1"+
            "\uffff\32\27",
            "\1\31\17\uffff\12\27\7\uffff\32\27\4\uffff\1\27\1\uffff\32"+
            "\27",
            "",
            "",
            "",
            "",
            "",
            "\1\31\17\uffff\12\27\7\uffff\12\27\1\36\17\27\4\uffff\1\27"+
            "\1\uffff\32\27",
            "\1\31\17\uffff\12\27\7\uffff\6\27\1\37\23\27\4\uffff\1\27\1"+
            "\uffff\32\27",
            "\1\31\17\uffff\12\27\7\uffff\32\27\4\uffff\1\27\1\uffff\32"+
            "\27",
            "",
            "",
            "\1\31\17\uffff\12\27\7\uffff\24\27\1\40\5\27\4\uffff\1\27\1"+
            "\uffff\32\27",
            "\1\31\17\uffff\12\27\7\uffff\27\27\1\41\2\27\4\uffff\1\27\1"+
            "\uffff\32\27",
            "\1\31\17\uffff\12\27\7\uffff\15\27\1\42\14\27\4\uffff\1\27"+
            "\1\uffff\32\27",
            "\1\31\17\uffff\12\27\7\uffff\14\27\1\43\15\27\4\uffff\1\27"+
            "\1\uffff\32\27",
            "\1\31\17\uffff\12\27\7\uffff\32\27\4\uffff\1\27\1\uffff\32"+
            "\27",
            "\1\31\17\uffff\12\27\7\uffff\32\27\4\uffff\1\27\1\uffff\32"+
            "\27",
            "\1\31\17\uffff\12\27\7\uffff\15\27\1\46\14\27\4\uffff\1\27"+
            "\1\uffff\32\27",
            "\1\31\17\uffff\12\27\7\uffff\32\27\4\uffff\1\27\1\uffff\32"+
            "\27",
            "\1\31\17\uffff\12\27\7\uffff\32\27\4\uffff\1\27\1\uffff\32"+
            "\27",
            "\1\31\17\uffff\12\27\7\uffff\32\27\4\uffff\1\27\1\uffff\32"+
            "\27",
            "",
            "",
            "\1\31\17\uffff\12\27\7\uffff\23\27\1\52\6\27\4\uffff\1\27\1"+
            "\uffff\32\27",
            "",
            "",
            "",
            "\1\31\17\uffff\12\27\7\uffff\32\27\4\uffff\1\27\1\uffff\32"+
            "\27",
            ""
    };

    static final short[] DFA10_eot = DFA.unpackEncodedString(DFA10_eotS);
    static final short[] DFA10_eof = DFA.unpackEncodedString(DFA10_eofS);
    static final char[] DFA10_min = DFA.unpackEncodedStringToUnsignedChars(DFA10_minS);
    static final char[] DFA10_max = DFA.unpackEncodedStringToUnsignedChars(DFA10_maxS);
    static final short[] DFA10_accept = DFA.unpackEncodedString(DFA10_acceptS);
    static final short[] DFA10_special = DFA.unpackEncodedString(DFA10_specialS);
    static final short[][] DFA10_transition;

    static {
        int numStates = DFA10_transitionS.length;
        DFA10_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA10_transition[i] = DFA.unpackEncodedString(DFA10_transitionS[i]);
        }
    }

    class DFA10 extends DFA {

        public DFA10(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 10;
            this.eot = DFA10_eot;
            this.eof = DFA10_eof;
            this.min = DFA10_min;
            this.max = DFA10_max;
            this.accept = DFA10_accept;
            this.special = DFA10_special;
            this.transition = DFA10_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | ID | INT | COMMENT | WS | KEYVALUE | LITVALUE );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA10_7 = input.LA(1);

                        s = -1;
                        if ( ((LA10_7 >= '\u0000' && LA10_7 <= '\uFFFF')) ) {s = 20;}

                        else s = 19;

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 10, _s, input);
            error(nvae);
            throw nvae;
        }

    }
 

}