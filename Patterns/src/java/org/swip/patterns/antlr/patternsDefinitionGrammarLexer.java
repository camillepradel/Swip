// $ANTLR 3.3 Nov 30, 2010 12:45:30 /home/camille/ANTLRWorks/patternsDefinitionGrammar.g 2013-01-27 20:58:08
package  org.swip.patterns.antlr;

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class patternsDefinitionGrammarLexer extends Lexer {
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

    public patternsDefinitionGrammarLexer() {;} 
    public patternsDefinitionGrammarLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public patternsDefinitionGrammarLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "/home/camille/ANTLRWorks/patternsDefinitionGrammar.g"; }

    // $ANTLR start "ENDPREF"
    public final void mENDPREF() throws RecognitionException {
        try {
            int _type = ENDPREF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:5:9: ( 'end prefixes' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:5:11: 'end prefixes'
            {
            match("end prefixes"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ENDPREF"

    // $ANTLR start "ENDSENT"
    public final void mENDSENT() throws RecognitionException {
        try {
            int _type = ENDSENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:6:9: ( 'end sentence' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:6:11: 'end sentence'
            {
            match("end sentence"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ENDSENT"

    // $ANTLR start "ENDQUER"
    public final void mENDQUER() throws RecognitionException {
        try {
            int _type = ENDQUER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:7:9: ( 'end query' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:7:11: 'end query'
            {
            match("end query"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ENDQUER"

    // $ANTLR start "ENDMAPCOND"
    public final void mENDMAPCOND() throws RecognitionException {
        try {
            int _type = ENDMAPCOND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:8:12: ( 'end mapping conditions' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:8:14: 'end mapping conditions'
            {
            match("end mapping conditions"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ENDMAPCOND"

    // $ANTLR start "ENDPAT"
    public final void mENDPAT() throws RecognitionException {
        try {
            int _type = ENDPAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:9:8: ( 'end pattern' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:9:10: 'end pattern'
            {
            match("end pattern"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ENDPAT"

    // $ANTLR start "PREF"
    public final void mPREF() throws RecognitionException {
        try {
            int _type = PREF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:10:6: ( 'prefixes' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:10:8: 'prefixes'
            {
            match("prefixes"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PREF"

    // $ANTLR start "PAT"
    public final void mPAT() throws RecognitionException {
        try {
            int _type = PAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:11:5: ( 'pattern' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:11:7: 'pattern'
            {
            match("pattern"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PAT"

    // $ANTLR start "QUER"
    public final void mQUER() throws RecognitionException {
        try {
            int _type = QUER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:12:6: ( 'query' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:12:8: 'query'
            {
            match("query"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "QUER"

    // $ANTLR start "SENT"
    public final void mSENT() throws RecognitionException {
        try {
            int _type = SENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:13:6: ( 'sentence' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:13:8: 'sentence'
            {
            match("sentence"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SENT"

    // $ANTLR start "MAPCOND"
    public final void mMAPCOND() throws RecognitionException {
        try {
            int _type = MAPCOND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:14:9: ( 'mapping conditions' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:14:11: 'mapping conditions'
            {
            match("mapping conditions"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MAPCOND"

    // $ANTLR start "UNDERSCORE"
    public final void mUNDERSCORE() throws RecognitionException {
        try {
            int _type = UNDERSCORE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:15:12: ( '_' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:15:14: '_'
            {
            match('_'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "UNDERSCORE"

    // $ANTLR start "COLON"
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:16:7: ( ':' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:16:9: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COLON"

    // $ANTLR start "MINUS"
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:17:7: ( '-' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:17:9: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MINUS"

    // $ANTLR start "LEFTSB"
    public final void mLEFTSB() throws RecognitionException {
        try {
            int _type = LEFTSB;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:18:8: ( '[' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:18:10: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LEFTSB"

    // $ANTLR start "RIGHTSB"
    public final void mRIGHTSB() throws RecognitionException {
        try {
            int _type = RIGHTSB;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:19:9: ( ']' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:19:11: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RIGHTSB"

    // $ANTLR start "X"
    public final void mX() throws RecognitionException {
        try {
            int _type = X;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:20:3: ( 'x' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:20:5: 'x'
            {
            match('x'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "X"

    // $ANTLR start "N"
    public final void mN() throws RecognitionException {
        try {
            int _type = N;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:21:3: ( 'n' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:21:5: 'n'
            {
            match('n'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "N"

    // $ANTLR start "LOWERTHAN"
    public final void mLOWERTHAN() throws RecognitionException {
        try {
            int _type = LOWERTHAN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:22:11: ( '<' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:22:13: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LOWERTHAN"

    // $ANTLR start "GREATERTHAN"
    public final void mGREATERTHAN() throws RecognitionException {
        try {
            int _type = GREATERTHAN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:23:13: ( '>' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:23:15: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GREATERTHAN"

    // $ANTLR start "TWOPOINTS"
    public final void mTWOPOINTS() throws RecognitionException {
        try {
            int _type = TWOPOINTS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:24:11: ( '..' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:24:13: '..'
            {
            match(".."); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TWOPOINTS"

    // $ANTLR start "SLASH"
    public final void mSLASH() throws RecognitionException {
        try {
            int _type = SLASH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:25:7: ( '/' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:25:9: '/'
            {
            match('/'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SLASH"

    // $ANTLR start "T__30"
    public final void mT__30() throws RecognitionException {
        try {
            int _type = T__30;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:26:7: ( ';' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:26:9: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__30"

    // $ANTLR start "T__31"
    public final void mT__31() throws RecognitionException {
        try {
            int _type = T__31;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:27:7: ( '(' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:27:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__31"

    // $ANTLR start "T__32"
    public final void mT__32() throws RecognitionException {
        try {
            int _type = T__32;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:28:7: ( ',' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:28:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__32"

    // $ANTLR start "T__33"
    public final void mT__33() throws RecognitionException {
        try {
            int _type = T__33;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:29:7: ( ')' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:29:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__33"

    // $ANTLR start "T__34"
    public final void mT__34() throws RecognitionException {
        try {
            int _type = T__34;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:30:7: ( '-[' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:30:9: '-['
            {
            match("-["); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__34"

    // $ANTLR start "T__35"
    public final void mT__35() throws RecognitionException {
        try {
            int _type = T__35;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:31:7: ( '-for-' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:31:9: '-for-'
            {
            match("-for-"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__35"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:298:5: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:298:7: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:298:27: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
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
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:300:5: ( ( '0' .. '9' )+ )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:300:7: ( '0' .. '9' )+
            {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:300:7: ( '0' .. '9' )+
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
            	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:300:7: '0' .. '9'
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

    // $ANTLR start "CITE"
    public final void mCITE() throws RecognitionException {
        try {
            int _type = CITE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:303:2: ( '\"' (~ ( '\"' ) )+ '\"' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:303:4: '\"' (~ ( '\"' ) )+ '\"'
            {
            match('\"'); 
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:303:8: (~ ( '\"' ) )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>='\u0000' && LA3_0<='!')||(LA3_0>='#' && LA3_0<='\uFFFF')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:303:9: ~ ( '\"' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


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

            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CITE"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:306:5: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' | '/*' ( options {greedy=false; } : . )* '*/' )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='/') ) {
                int LA7_1 = input.LA(2);

                if ( (LA7_1=='/') ) {
                    alt7=1;
                }
                else if ( (LA7_1=='*') ) {
                    alt7=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 7, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:306:9: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
                    {
                    match("//"); 

                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:306:14: (~ ( '\\n' | '\\r' ) )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0>='\u0000' && LA4_0<='\t')||(LA4_0>='\u000B' && LA4_0<='\f')||(LA4_0>='\u000E' && LA4_0<='\uFFFF')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:306:14: ~ ( '\\n' | '\\r' )
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
                    	    break loop4;
                        }
                    } while (true);

                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:306:28: ( '\\r' )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0=='\r') ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:306:28: '\\r'
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
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:307:9: '/*' ( options {greedy=false; } : . )* '*/'
                    {
                    match("/*"); 

                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:307:14: ( options {greedy=false; } : . )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0=='*') ) {
                            int LA6_1 = input.LA(2);

                            if ( (LA6_1=='/') ) {
                                alt6=2;
                            }
                            else if ( ((LA6_1>='\u0000' && LA6_1<='.')||(LA6_1>='0' && LA6_1<='\uFFFF')) ) {
                                alt6=1;
                            }


                        }
                        else if ( ((LA6_0>='\u0000' && LA6_0<=')')||(LA6_0>='+' && LA6_0<='\uFFFF')) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:307:42: .
                    	    {
                    	    matchAny(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop6;
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
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:310:5: ( ( ' ' | '\\t' | '\\n' | '\\r' )+ )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:310:9: ( ' ' | '\\t' | '\\n' | '\\r' )+
            {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:310:9: ( ' ' | '\\t' | '\\n' | '\\r' )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( ((LA8_0>='\t' && LA8_0<='\n')||LA8_0=='\r'||LA8_0==' ') ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:
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
            	    if ( cnt8 >= 1 ) break loop8;
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
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

    public void mTokens() throws RecognitionException {
        // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:8: ( ENDPREF | ENDSENT | ENDQUER | ENDMAPCOND | ENDPAT | PREF | PAT | QUER | SENT | MAPCOND | UNDERSCORE | COLON | MINUS | LEFTSB | RIGHTSB | X | N | LOWERTHAN | GREATERTHAN | TWOPOINTS | SLASH | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | ID | INT | CITE | COMMENT | WS )
        int alt9=32;
        alt9 = dfa9.predict(input);
        switch (alt9) {
            case 1 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:10: ENDPREF
                {
                mENDPREF(); 

                }
                break;
            case 2 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:18: ENDSENT
                {
                mENDSENT(); 

                }
                break;
            case 3 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:26: ENDQUER
                {
                mENDQUER(); 

                }
                break;
            case 4 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:34: ENDMAPCOND
                {
                mENDMAPCOND(); 

                }
                break;
            case 5 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:45: ENDPAT
                {
                mENDPAT(); 

                }
                break;
            case 6 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:52: PREF
                {
                mPREF(); 

                }
                break;
            case 7 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:57: PAT
                {
                mPAT(); 

                }
                break;
            case 8 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:61: QUER
                {
                mQUER(); 

                }
                break;
            case 9 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:66: SENT
                {
                mSENT(); 

                }
                break;
            case 10 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:71: MAPCOND
                {
                mMAPCOND(); 

                }
                break;
            case 11 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:79: UNDERSCORE
                {
                mUNDERSCORE(); 

                }
                break;
            case 12 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:90: COLON
                {
                mCOLON(); 

                }
                break;
            case 13 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:96: MINUS
                {
                mMINUS(); 

                }
                break;
            case 14 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:102: LEFTSB
                {
                mLEFTSB(); 

                }
                break;
            case 15 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:109: RIGHTSB
                {
                mRIGHTSB(); 

                }
                break;
            case 16 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:117: X
                {
                mX(); 

                }
                break;
            case 17 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:119: N
                {
                mN(); 

                }
                break;
            case 18 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:121: LOWERTHAN
                {
                mLOWERTHAN(); 

                }
                break;
            case 19 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:131: GREATERTHAN
                {
                mGREATERTHAN(); 

                }
                break;
            case 20 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:143: TWOPOINTS
                {
                mTWOPOINTS(); 

                }
                break;
            case 21 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:153: SLASH
                {
                mSLASH(); 

                }
                break;
            case 22 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:159: T__30
                {
                mT__30(); 

                }
                break;
            case 23 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:165: T__31
                {
                mT__31(); 

                }
                break;
            case 24 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:171: T__32
                {
                mT__32(); 

                }
                break;
            case 25 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:177: T__33
                {
                mT__33(); 

                }
                break;
            case 26 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:183: T__34
                {
                mT__34(); 

                }
                break;
            case 27 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:189: T__35
                {
                mT__35(); 

                }
                break;
            case 28 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:195: ID
                {
                mID(); 

                }
                break;
            case 29 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:198: INT
                {
                mINT(); 

                }
                break;
            case 30 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:202: CITE
                {
                mCITE(); 

                }
                break;
            case 31 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:207: COMMENT
                {
                mCOMMENT(); 

                }
                break;
            case 32 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:215: WS
                {
                mWS(); 

                }
                break;

        }

    }


    protected DFA9 dfa9 = new DFA9(this);
    static final String DFA9_eotS =
        "\1\uffff\5\25\2\uffff\1\41\2\uffff\1\42\1\43\3\uffff\1\45\10\uffff"+
        "\6\25\7\uffff\6\25\1\uffff\5\25\4\uffff\2\25\1\77\2\25\2\uffff\2"+
        "\25\1\uffff\3\25\1\107\2\25\1\112\1\uffff\1\113\3\uffff";
    static final String DFA9_eofS =
        "\114\uffff";
    static final String DFA9_minS =
        "\1\11\1\156\1\141\1\165\1\145\1\141\2\uffff\1\133\2\uffff\2\60\3"+
        "\uffff\1\52\10\uffff\1\144\1\145\1\164\1\145\1\156\1\160\7\uffff"+
        "\1\40\1\146\1\164\1\162\1\164\1\160\1\155\1\151\1\145\1\171\1\145"+
        "\1\151\1\141\3\uffff\1\170\1\162\1\60\2\156\2\uffff\1\145\1\156"+
        "\1\uffff\1\143\1\147\1\163\1\60\1\145\1\40\1\60\1\uffff\1\60\3\uffff";
    static final String DFA9_maxS =
        "\1\172\1\156\1\162\1\165\1\145\1\141\2\uffff\1\146\2\uffff\2\172"+
        "\3\uffff\1\57\10\uffff\1\144\1\145\1\164\1\145\1\156\1\160\7\uffff"+
        "\1\40\1\146\1\164\1\162\1\164\1\160\1\163\1\151\1\145\1\171\1\145"+
        "\1\151\1\162\3\uffff\1\170\1\162\1\172\2\156\2\uffff\1\145\1\156"+
        "\1\uffff\1\143\1\147\1\163\1\172\1\145\1\40\1\172\1\uffff\1\172"+
        "\3\uffff";
    static final String DFA9_acceptS =
        "\6\uffff\1\13\1\14\1\uffff\1\16\1\17\2\uffff\1\22\1\23\1\24\1\uffff"+
        "\1\26\1\27\1\30\1\31\1\34\1\35\1\36\1\40\6\uffff\1\32\1\33\1\15"+
        "\1\20\1\21\1\37\1\25\15\uffff\1\2\1\3\1\4\5\uffff\1\1\1\5\2\uffff"+
        "\1\10\7\uffff\1\7\1\uffff\1\12\1\6\1\11";
    static final String DFA9_specialS =
        "\114\uffff}>";
    static final String[] DFA9_transitionS = {
            "\2\30\2\uffff\1\30\22\uffff\1\30\1\uffff\1\27\5\uffff\1\22\1"+
            "\24\2\uffff\1\23\1\10\1\17\1\20\12\26\1\7\1\21\1\15\1\uffff"+
            "\1\16\2\uffff\32\25\1\11\1\uffff\1\12\1\uffff\1\6\1\uffff\4"+
            "\25\1\1\7\25\1\5\1\14\1\25\1\2\1\3\1\25\1\4\4\25\1\13\2\25",
            "\1\31",
            "\1\33\20\uffff\1\32",
            "\1\34",
            "\1\35",
            "\1\36",
            "",
            "",
            "\1\37\12\uffff\1\40",
            "",
            "",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "",
            "",
            "",
            "\1\44\4\uffff\1\44",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\46",
            "\1\47",
            "\1\50",
            "\1\51",
            "\1\52",
            "\1\53",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\54",
            "\1\55",
            "\1\56",
            "\1\57",
            "\1\60",
            "\1\61",
            "\1\65\2\uffff\1\62\1\64\1\uffff\1\63",
            "\1\66",
            "\1\67",
            "\1\70",
            "\1\71",
            "\1\72",
            "\1\74\20\uffff\1\73",
            "",
            "",
            "",
            "\1\75",
            "\1\76",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "\1\100",
            "\1\101",
            "",
            "",
            "\1\102",
            "\1\103",
            "",
            "\1\104",
            "\1\105",
            "\1\106",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "\1\110",
            "\1\111",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "",
            "",
            ""
    };

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( ENDPREF | ENDSENT | ENDQUER | ENDMAPCOND | ENDPAT | PREF | PAT | QUER | SENT | MAPCOND | UNDERSCORE | COLON | MINUS | LEFTSB | RIGHTSB | X | N | LOWERTHAN | GREATERTHAN | TWOPOINTS | SLASH | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | ID | INT | CITE | COMMENT | WS );";
        }
    }
 

}