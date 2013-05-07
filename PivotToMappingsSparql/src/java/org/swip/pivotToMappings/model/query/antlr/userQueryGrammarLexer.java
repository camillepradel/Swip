// $ANTLR 3.3 Nov 30, 2010 12:45:30 /home/camille/ANTLRWorks/userQueryGrammar.g 2013-05-06 15:23:12
package  org.swip.pivotToMappings.model.query.antlr;

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

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
    public static final int INT=4;
    public static final int ID=5;
    public static final int LITVALUE=6;
    public static final int COMMENT=7;
    public static final int WS=8;
    public static final int KEYVALUE=9;

    // delegates
    // delegators

    public userQueryGrammarLexer() {;} 
    public userQueryGrammarLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public userQueryGrammarLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "/home/camille/ANTLRWorks/userQueryGrammar.g"; }

    // $ANTLR start "T__10"
    public final void mT__10() throws RecognitionException {
        try {
            int _type = T__10;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/userQueryGrammar.g:5:7: ( '.' )
            // /home/camille/ANTLRWorks/userQueryGrammar.g:5:9: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__10"

    // $ANTLR start "T__11"
    public final void mT__11() throws RecognitionException {
        try {
            int _type = T__11;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/userQueryGrammar.g:6:7: ( 'COUNT' )
            // /home/camille/ANTLRWorks/userQueryGrammar.g:6:9: 'COUNT'
            {
            match("COUNT"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__11"

    // $ANTLR start "T__12"
    public final void mT__12() throws RecognitionException {
        try {
            int _type = T__12;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/userQueryGrammar.g:7:7: ( 'ASK' )
            // /home/camille/ANTLRWorks/userQueryGrammar.g:7:9: 'ASK'
            {
            match("ASK"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__12"

    // $ANTLR start "T__13"
    public final void mT__13() throws RecognitionException {
        try {
            int _type = T__13;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/userQueryGrammar.g:8:7: ( ':' )
            // /home/camille/ANTLRWorks/userQueryGrammar.g:8:9: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__13"

    // $ANTLR start "T__14"
    public final void mT__14() throws RecognitionException {
        try {
            int _type = T__14;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/userQueryGrammar.g:9:7: ( ';' )
            // /home/camille/ANTLRWorks/userQueryGrammar.g:9:9: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__14"

    // $ANTLR start "T__15"
    public final void mT__15() throws RecognitionException {
        try {
            int _type = T__15;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/userQueryGrammar.g:10:7: ( '=' )
            // /home/camille/ANTLRWorks/userQueryGrammar.g:10:9: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__15"

    // $ANTLR start "T__16"
    public final void mT__16() throws RecognitionException {
        try {
            int _type = T__16;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/userQueryGrammar.g:11:7: ( ',' )
            // /home/camille/ANTLRWorks/userQueryGrammar.g:11:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__16"

    // $ANTLR start "T__17"
    public final void mT__17() throws RecognitionException {
        try {
            int _type = T__17;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/userQueryGrammar.g:12:7: ( '?' )
            // /home/camille/ANTLRWorks/userQueryGrammar.g:12:9: '?'
            {
            match('?'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__17"

    // $ANTLR start "T__18"
    public final void mT__18() throws RecognitionException {
        try {
            int _type = T__18;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/userQueryGrammar.g:13:7: ( '$' )
            // /home/camille/ANTLRWorks/userQueryGrammar.g:13:9: '$'
            {
            match('$'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__18"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/userQueryGrammar.g:137:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '\\'' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '\\'' )* )
            // /home/camille/ANTLRWorks/userQueryGrammar.g:137:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '\\'' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '\\'' )*
            {
            if ( input.LA(1)=='\''||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // /home/camille/ANTLRWorks/userQueryGrammar.g:137:36: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '\\'' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='\''||(LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/userQueryGrammar.g:
            	    {
            	    if ( input.LA(1)=='\''||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


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
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/userQueryGrammar.g:140:5: ( ( '0' .. '9' )+ )
            // /home/camille/ANTLRWorks/userQueryGrammar.g:140:7: ( '0' .. '9' )+
            {
            // /home/camille/ANTLRWorks/userQueryGrammar.g:140:7: ( '0' .. '9' )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='0' && LA2_0<='9')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/userQueryGrammar.g:140:7: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

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
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/userQueryGrammar.g:144:5: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' | '/*' ( options {greedy=false; } : . )* '*/' )
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
                    // /home/camille/ANTLRWorks/userQueryGrammar.g:144:9: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
                    {
                    match("//"); 

                    // /home/camille/ANTLRWorks/userQueryGrammar.g:144:14: (~ ( '\\n' | '\\r' ) )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( ((LA3_0>='\u0000' && LA3_0<='\t')||(LA3_0>='\u000B' && LA3_0<='\f')||(LA3_0>='\u000E' && LA3_0<='\uFFFF')) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // /home/camille/ANTLRWorks/userQueryGrammar.g:144:14: ~ ( '\\n' | '\\r' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop3;
                        }
                    } while (true);

                    // /home/camille/ANTLRWorks/userQueryGrammar.g:144:28: ( '\\r' )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0=='\r') ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // /home/camille/ANTLRWorks/userQueryGrammar.g:144:28: '\\r'
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
                    // /home/camille/ANTLRWorks/userQueryGrammar.g:145:9: '/*' ( options {greedy=false; } : . )* '*/'
                    {
                    match("/*"); 

                    // /home/camille/ANTLRWorks/userQueryGrammar.g:145:14: ( options {greedy=false; } : . )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0=='*') ) {
                            int LA5_1 = input.LA(2);

                            if ( (LA5_1=='/') ) {
                                alt5=2;
                            }
                            else if ( ((LA5_1>='\u0000' && LA5_1<='.')||(LA5_1>='0' && LA5_1<='\uFFFF')) ) {
                                alt5=1;
                            }


                        }
                        else if ( ((LA5_0>='\u0000' && LA5_0<=')')||(LA5_0>='+' && LA5_0<='\uFFFF')) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // /home/camille/ANTLRWorks/userQueryGrammar.g:145:42: .
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
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/userQueryGrammar.g:148:5: ( ( ' ' | '\\t' | '\\n' | '\\r' )+ )
            // /home/camille/ANTLRWorks/userQueryGrammar.g:148:9: ( ' ' | '\\t' | '\\n' | '\\r' )+
            {
            // /home/camille/ANTLRWorks/userQueryGrammar.g:148:9: ( ' ' | '\\t' | '\\n' | '\\r' )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( ((LA7_0>='\t' && LA7_0<='\n')||LA7_0=='\r'||LA7_0==' ') ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/userQueryGrammar.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


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
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "KEYVALUE"
    public final void mKEYVALUE() throws RecognitionException {
        try {
            int _type = KEYVALUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/userQueryGrammar.g:151:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | ' ' )* )
            // /home/camille/ANTLRWorks/userQueryGrammar.g:151:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | ' ' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // /home/camille/ANTLRWorks/userQueryGrammar.g:151:31: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | ' ' )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==' '||(LA8_0>='0' && LA8_0<='9')||(LA8_0>='A' && LA8_0<='Z')||LA8_0=='_'||(LA8_0>='a' && LA8_0<='z')) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/userQueryGrammar.g:
            	    {
            	    if ( input.LA(1)==' '||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


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
        }
    }
    // $ANTLR end "KEYVALUE"

    // $ANTLR start "LITVALUE"
    public final void mLITVALUE() throws RecognitionException {
        try {
            int _type = LITVALUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/userQueryGrammar.g:155:6: ( '<' ( . )* '>' )
            // /home/camille/ANTLRWorks/userQueryGrammar.g:155:8: '<' ( . )* '>'
            {
            match('<'); 
            // /home/camille/ANTLRWorks/userQueryGrammar.g:155:12: ( . )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0=='>') ) {
                    alt9=2;
                }
                else if ( ((LA9_0>='\u0000' && LA9_0<='=')||(LA9_0>='?' && LA9_0<='\uFFFF')) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/userQueryGrammar.g:155:12: .
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
        }
    }
    // $ANTLR end "LITVALUE"

    public void mTokens() throws RecognitionException {
        // /home/camille/ANTLRWorks/userQueryGrammar.g:1:8: ( T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | ID | INT | COMMENT | WS | KEYVALUE | LITVALUE )
        int alt10=15;
        alt10 = dfa10.predict(input);
        switch (alt10) {
            case 1 :
                // /home/camille/ANTLRWorks/userQueryGrammar.g:1:10: T__10
                {
                mT__10(); 

                }
                break;
            case 2 :
                // /home/camille/ANTLRWorks/userQueryGrammar.g:1:16: T__11
                {
                mT__11(); 

                }
                break;
            case 3 :
                // /home/camille/ANTLRWorks/userQueryGrammar.g:1:22: T__12
                {
                mT__12(); 

                }
                break;
            case 4 :
                // /home/camille/ANTLRWorks/userQueryGrammar.g:1:28: T__13
                {
                mT__13(); 

                }
                break;
            case 5 :
                // /home/camille/ANTLRWorks/userQueryGrammar.g:1:34: T__14
                {
                mT__14(); 

                }
                break;
            case 6 :
                // /home/camille/ANTLRWorks/userQueryGrammar.g:1:40: T__15
                {
                mT__15(); 

                }
                break;
            case 7 :
                // /home/camille/ANTLRWorks/userQueryGrammar.g:1:46: T__16
                {
                mT__16(); 

                }
                break;
            case 8 :
                // /home/camille/ANTLRWorks/userQueryGrammar.g:1:52: T__17
                {
                mT__17(); 

                }
                break;
            case 9 :
                // /home/camille/ANTLRWorks/userQueryGrammar.g:1:58: T__18
                {
                mT__18(); 

                }
                break;
            case 10 :
                // /home/camille/ANTLRWorks/userQueryGrammar.g:1:64: ID
                {
                mID(); 

                }
                break;
            case 11 :
                // /home/camille/ANTLRWorks/userQueryGrammar.g:1:67: INT
                {
                mINT(); 

                }
                break;
            case 12 :
                // /home/camille/ANTLRWorks/userQueryGrammar.g:1:71: COMMENT
                {
                mCOMMENT(); 

                }
                break;
            case 13 :
                // /home/camille/ANTLRWorks/userQueryGrammar.g:1:79: WS
                {
                mWS(); 

                }
                break;
            case 14 :
                // /home/camille/ANTLRWorks/userQueryGrammar.g:1:82: KEYVALUE
                {
                mKEYVALUE(); 

                }
                break;
            case 15 :
                // /home/camille/ANTLRWorks/userQueryGrammar.g:1:91: LITVALUE
                {
                mLITVALUE(); 

                }
                break;

        }

    }


    protected DFA10 dfa10 = new DFA10(this);
    static final String DFA10_eotS =
        "\2\uffff\2\16\6\uffff\1\16\5\uffff\2\16\1\uffff\2\16\1\27\1\16\1"+
        "\uffff\1\31\1\uffff";
    static final String DFA10_eofS =
        "\32\uffff";
    static final String DFA10_minS =
        "\1\11\1\uffff\2\40\6\uffff\1\40\5\uffff\2\40\1\uffff\4\40\1\uffff"+
        "\1\40\1\uffff";
    static final String DFA10_maxS =
        "\1\172\1\uffff\2\172\6\uffff\1\172\5\uffff\2\172\1\uffff\4\172\1"+
        "\uffff\1\172\1\uffff";
    static final String DFA10_acceptS =
        "\1\uffff\1\1\2\uffff\1\4\1\5\1\6\1\7\1\10\1\11\1\uffff\1\13\1\14"+
        "\1\15\1\12\1\17\2\uffff\1\16\4\uffff\1\3\1\uffff\1\2";
    static final String DFA10_specialS =
        "\32\uffff}>";
    static final String[] DFA10_transitionS = {
            "\2\15\2\uffff\1\15\22\uffff\1\15\3\uffff\1\11\2\uffff\1\16\4"+
            "\uffff\1\7\1\uffff\1\1\1\14\12\13\1\4\1\5\1\17\1\6\1\uffff\1"+
            "\10\1\uffff\1\3\1\12\1\2\27\12\4\uffff\1\12\1\uffff\32\12",
            "",
            "\1\22\17\uffff\12\21\7\uffff\16\21\1\20\13\21\4\uffff\1\21"+
            "\1\uffff\32\21",
            "\1\22\17\uffff\12\21\7\uffff\22\21\1\23\7\21\4\uffff\1\21\1"+
            "\uffff\32\21",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\22\17\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\32"+
            "\21",
            "",
            "",
            "",
            "",
            "",
            "\1\22\17\uffff\12\21\7\uffff\24\21\1\24\5\21\4\uffff\1\21\1"+
            "\uffff\32\21",
            "\1\22\17\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\32"+
            "\21",
            "",
            "\1\22\17\uffff\12\21\7\uffff\12\21\1\25\17\21\4\uffff\1\21"+
            "\1\uffff\32\21",
            "\1\22\17\uffff\12\21\7\uffff\15\21\1\26\14\21\4\uffff\1\21"+
            "\1\uffff\32\21",
            "\1\22\6\uffff\1\16\10\uffff\12\21\7\uffff\32\21\4\uffff\1\21"+
            "\1\uffff\32\21",
            "\1\22\17\uffff\12\21\7\uffff\23\21\1\30\6\21\4\uffff\1\21\1"+
            "\uffff\32\21",
            "",
            "\1\22\6\uffff\1\16\10\uffff\12\21\7\uffff\32\21\4\uffff\1\21"+
            "\1\uffff\32\21",
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
            return "1:1: Tokens : ( T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | ID | INT | COMMENT | WS | KEYVALUE | LITVALUE );";
        }
    }
 

}