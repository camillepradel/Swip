// $ANTLR 3.4 /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g 2012-05-22 10:55:19
package  org.swip.pivotToMappings.model.query.antlr;

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class userQueryGrammarLexer extends Lexer {
    public static final int EOF=-1;
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
    public static final int COMMENT=4;
    public static final int ID=5;
    public static final int INT=6;
    public static final int LITVALUE=7;
    public static final int WS=8;

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

    // $ANTLR start "T__9"
    public final void mT__9() throws RecognitionException {
        try {
            int _type = T__9;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:4:6: ( '(' )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:4:8: '('
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
    // $ANTLR end "T__9"

    // $ANTLR start "T__10"
    public final void mT__10() throws RecognitionException {
        try {
            int _type = T__10;
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
    // $ANTLR end "T__10"

    // $ANTLR start "T__11"
    public final void mT__11() throws RecognitionException {
        try {
            int _type = T__11;
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
    // $ANTLR end "T__11"

    // $ANTLR start "T__12"
    public final void mT__12() throws RecognitionException {
        try {
            int _type = T__12;
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
    // $ANTLR end "T__12"

    // $ANTLR start "T__13"
    public final void mT__13() throws RecognitionException {
        try {
            int _type = T__13;
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
    // $ANTLR end "T__13"

    // $ANTLR start "T__14"
    public final void mT__14() throws RecognitionException {
        try {
            int _type = T__14;
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
    // $ANTLR end "T__14"

    // $ANTLR start "T__15"
    public final void mT__15() throws RecognitionException {
        try {
            int _type = T__15;
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
    // $ANTLR end "T__15"

    // $ANTLR start "T__16"
    public final void mT__16() throws RecognitionException {
        try {
            int _type = T__16;
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
    // $ANTLR end "T__16"

    // $ANTLR start "T__17"
    public final void mT__17() throws RecognitionException {
        try {
            int _type = T__17;
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
    // $ANTLR end "T__17"

    // $ANTLR start "T__18"
    public final void mT__18() throws RecognitionException {
        try {
            int _type = T__18;
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
    // $ANTLR end "T__18"

    // $ANTLR start "T__19"
    public final void mT__19() throws RecognitionException {
        try {
            int _type = T__19;
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
    // $ANTLR end "T__19"

    // $ANTLR start "T__20"
    public final void mT__20() throws RecognitionException {
        try {
            int _type = T__20;
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
    // $ANTLR end "T__20"

    // $ANTLR start "T__21"
    public final void mT__21() throws RecognitionException {
        try {
            int _type = T__21;
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
    // $ANTLR end "T__21"

    // $ANTLR start "T__22"
    public final void mT__22() throws RecognitionException {
        try {
            int _type = T__22;
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
    // $ANTLR end "T__22"

    // $ANTLR start "T__23"
    public final void mT__23() throws RecognitionException {
        try {
            int _type = T__23;
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
    // $ANTLR end "T__23"

    // $ANTLR start "T__24"
    public final void mT__24() throws RecognitionException {
        try {
            int _type = T__24;
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
    // $ANTLR end "T__24"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:171:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | ' ' )* )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:171:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | ' ' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:171:31: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | ' ' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==' '||(LA1_0 >= '0' && LA1_0 <= '9')||(LA1_0 >= 'A' && LA1_0 <= 'Z')||LA1_0=='_'||(LA1_0 >= 'a' && LA1_0 <= 'z')) ) {
                    alt1=1;
                }


                switch (alt1) {
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
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:174:5: ( ( '0' .. '9' )+ )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:174:7: ( '0' .. '9' )+
            {
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:174:7: ( '0' .. '9' )+
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
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:178:5: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' | '/*' ( options {greedy=false; } : . )* '*/' )
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
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:178:9: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
                    {
                    match("//"); 



                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:178:14: (~ ( '\\n' | '\\r' ) )*
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


                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:178:28: ( '\\r' )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0=='\r') ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:178:28: '\\r'
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
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:179:9: '/*' ( options {greedy=false; } : . )* '*/'
                    {
                    match("/*"); 



                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:179:14: ( options {greedy=false; } : . )*
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
                    	    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:179:42: .
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
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:182:5: ( ( ' ' | '\\t' | '\\n' | '\\r' )+ )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:182:9: ( ' ' | '\\t' | '\\n' | '\\r' )+
            {
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:182:9: ( ' ' | '\\t' | '\\n' | '\\r' )+
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

    // $ANTLR start "LITVALUE"
    public final void mLITVALUE() throws RecognitionException {
        try {
            int _type = LITVALUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:189:6: ( '[' ( . )* ']' )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:189:8: '[' ( . )* ']'
            {
            match('['); 

            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:189:12: ( . )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==']') ) {
                    alt8=2;
                }
                else if ( ((LA8_0 >= '\u0000' && LA8_0 <= '\\')||(LA8_0 >= '^' && LA8_0 <= '\uFFFF')) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:189:12: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);


            match(']'); 

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
        // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:8: ( T__9 | T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | ID | INT | COMMENT | WS | LITVALUE )
        int alt9=21;
        switch ( input.LA(1) ) {
        case '(':
            {
            alt9=1;
            }
            break;
        case ')':
            {
            alt9=2;
            }
            break;
        case ',':
            {
            alt9=3;
            }
            break;
        case '.':
            {
            alt9=4;
            }
            break;
        case ':':
            {
            alt9=5;
            }
            break;
        case ';':
            {
            alt9=6;
            }
            break;
        case '<':
            {
            alt9=7;
            }
            break;
        case '=':
            {
            alt9=8;
            }
            break;
        case '>':
            {
            alt9=9;
            }
            break;
        case '?':
            {
            alt9=10;
            }
            break;
        case 'A':
            {
            switch ( input.LA(2) ) {
            case 'S':
                {
                int LA9_20 = input.LA(3);

                if ( (LA9_20=='K') ) {
                    int LA9_26 = input.LA(4);

                    if ( (LA9_26==' '||(LA9_26 >= '0' && LA9_26 <= '9')||(LA9_26 >= 'A' && LA9_26 <= 'Z')||LA9_26=='_'||(LA9_26 >= 'a' && LA9_26 <= 'z')) ) {
                        alt9=17;
                    }
                    else {
                        alt9=11;
                    }
                }
                else {
                    alt9=17;
                }
                }
                break;
            case 'V':
                {
                int LA9_21 = input.LA(3);

                if ( (LA9_21=='G') ) {
                    int LA9_27 = input.LA(4);

                    if ( (LA9_27==' '||(LA9_27 >= '0' && LA9_27 <= '9')||(LA9_27 >= 'A' && LA9_27 <= 'Z')||LA9_27=='_'||(LA9_27 >= 'a' && LA9_27 <= 'z')) ) {
                        alt9=17;
                    }
                    else {
                        alt9=12;
                    }
                }
                else {
                    alt9=17;
                }
                }
                break;
            default:
                alt9=17;
            }

            }
            break;
        case 'C':
            {
            int LA9_12 = input.LA(2);

            if ( (LA9_12=='O') ) {
                int LA9_22 = input.LA(3);

                if ( (LA9_22=='U') ) {
                    int LA9_28 = input.LA(4);

                    if ( (LA9_28=='N') ) {
                        int LA9_34 = input.LA(5);

                        if ( (LA9_34=='T') ) {
                            int LA9_38 = input.LA(6);

                            if ( (LA9_38==' '||(LA9_38 >= '0' && LA9_38 <= '9')||(LA9_38 >= 'A' && LA9_38 <= 'Z')||LA9_38=='_'||(LA9_38 >= 'a' && LA9_38 <= 'z')) ) {
                                alt9=17;
                            }
                            else {
                                alt9=13;
                            }
                        }
                        else {
                            alt9=17;
                        }
                    }
                    else {
                        alt9=17;
                    }
                }
                else {
                    alt9=17;
                }
            }
            else {
                alt9=17;
            }
            }
            break;
        case 'M':
            {
            switch ( input.LA(2) ) {
            case 'A':
                {
                int LA9_23 = input.LA(3);

                if ( (LA9_23=='X') ) {
                    int LA9_29 = input.LA(4);

                    if ( (LA9_29==' '||(LA9_29 >= '0' && LA9_29 <= '9')||(LA9_29 >= 'A' && LA9_29 <= 'Z')||LA9_29=='_'||(LA9_29 >= 'a' && LA9_29 <= 'z')) ) {
                        alt9=17;
                    }
                    else {
                        alt9=14;
                    }
                }
                else {
                    alt9=17;
                }
                }
                break;
            case 'I':
                {
                int LA9_24 = input.LA(3);

                if ( (LA9_24=='N') ) {
                    int LA9_30 = input.LA(4);

                    if ( (LA9_30==' '||(LA9_30 >= '0' && LA9_30 <= '9')||(LA9_30 >= 'A' && LA9_30 <= 'Z')||LA9_30=='_'||(LA9_30 >= 'a' && LA9_30 <= 'z')) ) {
                        alt9=17;
                    }
                    else {
                        alt9=15;
                    }
                }
                else {
                    alt9=17;
                }
                }
                break;
            default:
                alt9=17;
            }

            }
            break;
        case 'S':
            {
            int LA9_14 = input.LA(2);

            if ( (LA9_14=='U') ) {
                int LA9_25 = input.LA(3);

                if ( (LA9_25=='M') ) {
                    int LA9_31 = input.LA(4);

                    if ( (LA9_31==' '||(LA9_31 >= '0' && LA9_31 <= '9')||(LA9_31 >= 'A' && LA9_31 <= 'Z')||LA9_31=='_'||(LA9_31 >= 'a' && LA9_31 <= 'z')) ) {
                        alt9=17;
                    }
                    else {
                        alt9=16;
                    }
                }
                else {
                    alt9=17;
                }
            }
            else {
                alt9=17;
            }
            }
            break;
        case 'B':
        case 'D':
        case 'E':
        case 'F':
        case 'G':
        case 'H':
        case 'I':
        case 'J':
        case 'K':
        case 'L':
        case 'N':
        case 'O':
        case 'P':
        case 'Q':
        case 'R':
        case 'T':
        case 'U':
        case 'V':
        case 'W':
        case 'X':
        case 'Y':
        case 'Z':
        case '_':
        case 'a':
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'f':
        case 'g':
        case 'h':
        case 'i':
        case 'j':
        case 'k':
        case 'l':
        case 'm':
        case 'n':
        case 'o':
        case 'p':
        case 'q':
        case 'r':
        case 's':
        case 't':
        case 'u':
        case 'v':
        case 'w':
        case 'x':
        case 'y':
        case 'z':
            {
            alt9=17;
            }
            break;
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            {
            alt9=18;
            }
            break;
        case '/':
            {
            alt9=19;
            }
            break;
        case '\t':
        case '\n':
        case '\r':
        case ' ':
            {
            alt9=20;
            }
            break;
        case '[':
            {
            alt9=21;
            }
            break;
        default:
            NoViableAltException nvae =
                new NoViableAltException("", 9, 0, input);

            throw nvae;

        }

        switch (alt9) {
            case 1 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:10: T__9
                {
                mT__9(); 


                }
                break;
            case 2 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:15: T__10
                {
                mT__10(); 


                }
                break;
            case 3 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:21: T__11
                {
                mT__11(); 


                }
                break;
            case 4 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:27: T__12
                {
                mT__12(); 


                }
                break;
            case 5 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:33: T__13
                {
                mT__13(); 


                }
                break;
            case 6 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:39: T__14
                {
                mT__14(); 


                }
                break;
            case 7 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:45: T__15
                {
                mT__15(); 


                }
                break;
            case 8 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:51: T__16
                {
                mT__16(); 


                }
                break;
            case 9 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:57: T__17
                {
                mT__17(); 


                }
                break;
            case 10 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:63: T__18
                {
                mT__18(); 


                }
                break;
            case 11 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:69: T__19
                {
                mT__19(); 


                }
                break;
            case 12 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:75: T__20
                {
                mT__20(); 


                }
                break;
            case 13 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:81: T__21
                {
                mT__21(); 


                }
                break;
            case 14 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:87: T__22
                {
                mT__22(); 


                }
                break;
            case 15 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:93: T__23
                {
                mT__23(); 


                }
                break;
            case 16 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:99: T__24
                {
                mT__24(); 


                }
                break;
            case 17 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:105: ID
                {
                mID(); 


                }
                break;
            case 18 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:108: INT
                {
                mINT(); 


                }
                break;
            case 19 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:112: COMMENT
                {
                mCOMMENT(); 


                }
                break;
            case 20 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:120: WS
                {
                mWS(); 


                }
                break;
            case 21 :
                // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:1:123: LITVALUE
                {
                mLITVALUE(); 


                }
                break;

        }

    }


 

}