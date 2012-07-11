// $ANTLR 3.3 Nov 30, 2010 12:45:30 /home/camille/ANTLRWorks/patternsDefinitionGrammar.g 2012-07-10 17:46:44
package  org.swip.pivotToMappings.model.patterns.antlr;

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class patternsDefinitionGrammarLexer extends Lexer {
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

    // $ANTLR start "T__31"
    public final void mT__31() throws RecognitionException {
        try {
            int _type = T__31;
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
    // $ANTLR end "T__31"

    // $ANTLR start "T__32"
    public final void mT__32() throws RecognitionException {
        try {
            int _type = T__32;
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
    // $ANTLR end "T__32"

    // $ANTLR start "T__33"
    public final void mT__33() throws RecognitionException {
        try {
            int _type = T__33;
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
    // $ANTLR end "T__33"

    // $ANTLR start "T__34"
    public final void mT__34() throws RecognitionException {
        try {
            int _type = T__34;
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
    // $ANTLR end "T__34"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:271:5: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:271:7: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:271:27: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
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
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:273:5: ( ( '0' .. '9' )+ )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:273:7: ( '0' .. '9' )+
            {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:273:7: ( '0' .. '9' )+
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
            	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:273:7: '0' .. '9'
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
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:276:2: ( '\"' (~ ( '\"' ) )+ '\"' )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:276:4: '\"' (~ ( '\"' ) )+ '\"'
            {
            match('\"'); 
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:276:8: (~ ( '\"' ) )+
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
            	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:276:9: ~ ( '\"' )
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
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:279:5: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' | '/*' ( options {greedy=false; } : . )* '*/' )
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
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:279:9: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
                    {
                    match("//"); 

                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:279:14: (~ ( '\\n' | '\\r' ) )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0>='\u0000' && LA4_0<='\t')||(LA4_0>='\u000B' && LA4_0<='\f')||(LA4_0>='\u000E' && LA4_0<='\uFFFF')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:279:14: ~ ( '\\n' | '\\r' )
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

                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:279:28: ( '\\r' )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0=='\r') ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:279:28: '\\r'
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
                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:280:9: '/*' ( options {greedy=false; } : . )* '*/'
                    {
                    match("/*"); 

                    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:280:14: ( options {greedy=false; } : . )*
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
                    	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:280:42: .
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
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:283:5: ( ( ' ' | '\\t' | '\\n' | '\\r' )+ )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:283:9: ( ' ' | '\\t' | '\\n' | '\\r' )+
            {
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:283:9: ( ' ' | '\\t' | '\\n' | '\\r' )+
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

    // $ANTLR start "SENTENCE"
    public final void mSENTENCE() throws RecognitionException {
        try {
            int _type = SENTENCE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:286:2: ( SENT ( options {greedy=false; } : . )+ ENDSENT )
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:286:4: SENT ( options {greedy=false; } : . )+ ENDSENT
            {
            mSENT(); 
            // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:286:9: ( options {greedy=false; } : . )+
            int cnt9=0;
            loop9:
            do {
                int alt9=2;
                alt9 = dfa9.predict(input);
                switch (alt9) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:286:37: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    if ( cnt9 >= 1 ) break loop9;
                        EarlyExitException eee =
                            new EarlyExitException(9, input);
                        throw eee;
                }
                cnt9++;
            } while (true);

            mENDSENT(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SENTENCE"

    public void mTokens() throws RecognitionException {
        // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:8: ( ENDPREF | ENDSENT | ENDQUER | ENDMAPCOND | ENDPAT | PREF | PAT | QUER | SENT | MAPCOND | UNDERSCORE | COLON | MINUS | LEFTSB | RIGHTSB | X | N | LOWERTHAN | GREATERTHAN | TWOPOINTS | SLASH | T__31 | T__32 | T__33 | T__34 | ID | INT | CITE | COMMENT | WS | SENTENCE )
        int alt10=31;
        alt10 = dfa10.predict(input);
        switch (alt10) {
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
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:159: T__31
                {
                mT__31(); 

                }
                break;
            case 23 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:165: T__32
                {
                mT__32(); 

                }
                break;
            case 24 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:171: T__33
                {
                mT__33(); 

                }
                break;
            case 25 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:177: T__34
                {
                mT__34(); 

                }
                break;
            case 26 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:183: ID
                {
                mID(); 

                }
                break;
            case 27 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:186: INT
                {
                mINT(); 

                }
                break;
            case 28 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:190: CITE
                {
                mCITE(); 

                }
                break;
            case 29 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:195: COMMENT
                {
                mCOMMENT(); 

                }
                break;
            case 30 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:203: WS
                {
                mWS(); 

                }
                break;
            case 31 :
                // /home/camille/ANTLRWorks/patternsDefinitionGrammar.g:1:206: SENTENCE
                {
                mSENTENCE(); 

                }
                break;

        }

    }


    protected DFA9 dfa9 = new DFA9(this);
    protected DFA10 dfa10 = new DFA10(this);
    static final String DFA9_eotS =
        "\16\uffff";
    static final String DFA9_eofS =
        "\16\uffff";
    static final String DFA9_minS =
        "\2\0\1\uffff\12\0\1\uffff";
    static final String DFA9_maxS =
        "\2\uffff\1\uffff\12\uffff\1\uffff";
    static final String DFA9_acceptS =
        "\2\uffff\1\1\12\uffff\1\2";
    static final String DFA9_specialS =
        "\1\0\1\7\1\uffff\1\10\1\3\1\4\1\5\1\1\1\11\1\6\1\12\1\13\1\2\1\uffff}>";
    static final String[] DFA9_transitionS = {
            "\145\2\1\1\uff9a\2",
            "\156\2\1\3\uff91\2",
            "",
            "\144\2\1\4\uff9b\2",
            "\40\2\1\5\uffdf\2",
            "\163\2\1\6\uff8c\2",
            "\145\2\1\7\uff9a\2",
            "\156\2\1\10\uff91\2",
            "\164\2\1\11\uff8b\2",
            "\145\2\1\12\uff9a\2",
            "\156\2\1\13\uff91\2",
            "\143\2\1\14\uff9c\2",
            "\145\2\1\15\uff9a\2",
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
            return "()+ loopback of 286:9: ( options {greedy=false; } : . )+";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA9_0 = input.LA(1);

                        s = -1;
                        if ( (LA9_0=='e') ) {s = 1;}

                        else if ( ((LA9_0>='\u0000' && LA9_0<='d')||(LA9_0>='f' && LA9_0<='\uFFFF')) ) {s = 2;}

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA9_7 = input.LA(1);

                        s = -1;
                        if ( (LA9_7=='n') ) {s = 8;}

                        else if ( ((LA9_7>='\u0000' && LA9_7<='m')||(LA9_7>='o' && LA9_7<='\uFFFF')) ) {s = 2;}

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA9_12 = input.LA(1);

                        s = -1;
                        if ( (LA9_12=='e') ) {s = 13;}

                        else if ( ((LA9_12>='\u0000' && LA9_12<='d')||(LA9_12>='f' && LA9_12<='\uFFFF')) ) {s = 2;}

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA9_4 = input.LA(1);

                        s = -1;
                        if ( (LA9_4==' ') ) {s = 5;}

                        else if ( ((LA9_4>='\u0000' && LA9_4<='\u001F')||(LA9_4>='!' && LA9_4<='\uFFFF')) ) {s = 2;}

                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA9_5 = input.LA(1);

                        s = -1;
                        if ( (LA9_5=='s') ) {s = 6;}

                        else if ( ((LA9_5>='\u0000' && LA9_5<='r')||(LA9_5>='t' && LA9_5<='\uFFFF')) ) {s = 2;}

                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA9_6 = input.LA(1);

                        s = -1;
                        if ( (LA9_6=='e') ) {s = 7;}

                        else if ( ((LA9_6>='\u0000' && LA9_6<='d')||(LA9_6>='f' && LA9_6<='\uFFFF')) ) {s = 2;}

                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA9_9 = input.LA(1);

                        s = -1;
                        if ( (LA9_9=='e') ) {s = 10;}

                        else if ( ((LA9_9>='\u0000' && LA9_9<='d')||(LA9_9>='f' && LA9_9<='\uFFFF')) ) {s = 2;}

                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA9_1 = input.LA(1);

                        s = -1;
                        if ( (LA9_1=='n') ) {s = 3;}

                        else if ( ((LA9_1>='\u0000' && LA9_1<='m')||(LA9_1>='o' && LA9_1<='\uFFFF')) ) {s = 2;}

                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA9_3 = input.LA(1);

                        s = -1;
                        if ( (LA9_3=='d') ) {s = 4;}

                        else if ( ((LA9_3>='\u0000' && LA9_3<='c')||(LA9_3>='e' && LA9_3<='\uFFFF')) ) {s = 2;}

                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA9_8 = input.LA(1);

                        s = -1;
                        if ( (LA9_8=='t') ) {s = 9;}

                        else if ( ((LA9_8>='\u0000' && LA9_8<='s')||(LA9_8>='u' && LA9_8<='\uFFFF')) ) {s = 2;}

                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA9_10 = input.LA(1);

                        s = -1;
                        if ( (LA9_10=='n') ) {s = 11;}

                        else if ( ((LA9_10>='\u0000' && LA9_10<='m')||(LA9_10>='o' && LA9_10<='\uFFFF')) ) {s = 2;}

                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA9_11 = input.LA(1);

                        s = -1;
                        if ( (LA9_11=='c') ) {s = 12;}

                        else if ( ((LA9_11>='\u0000' && LA9_11<='b')||(LA9_11>='d' && LA9_11<='\uFFFF')) ) {s = 2;}

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 9, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA10_eotS =
        "\1\uffff\5\25\5\uffff\1\37\1\40\3\uffff\1\42\10\uffff\6\25\4\uffff"+
        "\6\25\1\uffff\5\25\4\uffff\2\25\1\74\2\25\2\uffff\2\25\1\uffff\3"+
        "\25\1\104\2\25\1\107\1\uffff\1\110\3\uffff\1\25\1\uffff\3\25";
    static final String DFA10_eofS =
        "\116\uffff";
    static final String DFA10_minS =
        "\1\11\1\156\1\141\1\165\1\145\1\141\5\uffff\2\60\3\uffff\1\52\10"+
        "\uffff\1\144\1\145\1\164\1\145\1\156\1\160\4\uffff\1\40\1\146\1"+
        "\164\1\162\1\164\1\160\1\155\1\151\1\145\1\171\1\145\1\151\1\141"+
        "\3\uffff\1\170\1\162\1\60\2\156\2\uffff\1\145\1\156\1\uffff\1\143"+
        "\1\147\1\163\1\60\1\145\1\40\1\60\1\uffff\1\0\3\uffff\1\0\1\uffff"+
        "\3\0";
    static final String DFA10_maxS =
        "\1\172\1\156\1\162\1\165\1\145\1\141\5\uffff\2\172\3\uffff\1\57"+
        "\10\uffff\1\144\1\145\1\164\1\145\1\156\1\160\4\uffff\1\40\1\146"+
        "\1\164\1\162\1\164\1\160\1\163\1\151\1\145\1\171\1\145\1\151\1\162"+
        "\3\uffff\1\170\1\162\1\172\2\156\2\uffff\1\145\1\156\1\uffff\1\143"+
        "\1\147\1\163\1\172\1\145\1\40\1\172\1\uffff\1\uffff\3\uffff\1\uffff"+
        "\1\uffff\3\uffff";
    static final String DFA10_acceptS =
        "\6\uffff\1\13\1\14\1\15\1\16\1\17\2\uffff\1\22\1\23\1\24\1\uffff"+
        "\1\26\1\27\1\30\1\31\1\32\1\33\1\34\1\36\6\uffff\1\20\1\21\1\35"+
        "\1\25\15\uffff\1\2\1\3\1\4\5\uffff\1\1\1\5\2\uffff\1\10\7\uffff"+
        "\1\7\1\uffff\1\12\1\6\1\11\1\uffff\1\37\3\uffff";
    static final String DFA10_specialS =
        "\105\uffff\1\3\3\uffff\1\4\1\uffff\1\1\1\2\1\0}>";
    static final String[] DFA10_transitionS = {
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
            "",
            "",
            "",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "",
            "",
            "",
            "\1\41\4\uffff\1\41",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\43",
            "\1\44",
            "\1\45",
            "\1\46",
            "\1\47",
            "\1\50",
            "",
            "",
            "",
            "",
            "\1\51",
            "\1\52",
            "\1\53",
            "\1\54",
            "\1\55",
            "\1\56",
            "\1\62\2\uffff\1\57\1\61\1\uffff\1\60",
            "\1\63",
            "\1\64",
            "\1\65",
            "\1\66",
            "\1\67",
            "\1\71\20\uffff\1\70",
            "",
            "",
            "",
            "\1\72",
            "\1\73",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "\1\75",
            "\1\76",
            "",
            "",
            "\1\77",
            "\1\100",
            "",
            "\1\101",
            "\1\102",
            "\1\103",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "\1\105",
            "\1\106",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "",
            "\60\112\12\111\7\112\32\111\4\112\1\111\1\112\32\111\uff85"+
            "\112",
            "",
            "",
            "",
            "\60\112\12\111\7\112\32\111\4\112\1\111\1\112\4\111\1\113\25"+
            "\111\uff85\112",
            "",
            "\60\112\12\111\7\112\32\111\4\112\1\111\1\112\4\111\1\113\10"+
            "\111\1\114\14\111\uff85\112",
            "\60\112\12\111\7\112\32\111\4\112\1\111\1\112\3\111\1\115\1"+
            "\113\25\111\uff85\112",
            "\60\112\12\111\7\112\32\111\4\112\1\111\1\112\4\111\1\113\25"+
            "\111\uff85\112"
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
            return "1:1: Tokens : ( ENDPREF | ENDSENT | ENDQUER | ENDMAPCOND | ENDPAT | PREF | PAT | QUER | SENT | MAPCOND | UNDERSCORE | COLON | MINUS | LEFTSB | RIGHTSB | X | N | LOWERTHAN | GREATERTHAN | TWOPOINTS | SLASH | T__31 | T__32 | T__33 | T__34 | ID | INT | CITE | COMMENT | WS | SENTENCE );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA10_77 = input.LA(1);

                        s = -1;
                        if ( ((LA10_77>='\u0000' && LA10_77<='/')||(LA10_77>=':' && LA10_77<='@')||(LA10_77>='[' && LA10_77<='^')||LA10_77=='`'||(LA10_77>='{' && LA10_77<='\uFFFF')) ) {s = 74;}

                        else if ( (LA10_77=='e') ) {s = 75;}

                        else if ( ((LA10_77>='0' && LA10_77<='9')||(LA10_77>='A' && LA10_77<='Z')||LA10_77=='_'||(LA10_77>='a' && LA10_77<='d')||(LA10_77>='f' && LA10_77<='z')) ) {s = 73;}

                        else s = 21;

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA10_75 = input.LA(1);

                        s = -1;
                        if ( (LA10_75=='n') ) {s = 76;}

                        else if ( (LA10_75=='e') ) {s = 75;}

                        else if ( ((LA10_75>='0' && LA10_75<='9')||(LA10_75>='A' && LA10_75<='Z')||LA10_75=='_'||(LA10_75>='a' && LA10_75<='d')||(LA10_75>='f' && LA10_75<='m')||(LA10_75>='o' && LA10_75<='z')) ) {s = 73;}

                        else if ( ((LA10_75>='\u0000' && LA10_75<='/')||(LA10_75>=':' && LA10_75<='@')||(LA10_75>='[' && LA10_75<='^')||LA10_75=='`'||(LA10_75>='{' && LA10_75<='\uFFFF')) ) {s = 74;}

                        else s = 21;

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA10_76 = input.LA(1);

                        s = -1;
                        if ( (LA10_76=='d') ) {s = 77;}

                        else if ( (LA10_76=='e') ) {s = 75;}

                        else if ( ((LA10_76>='0' && LA10_76<='9')||(LA10_76>='A' && LA10_76<='Z')||LA10_76=='_'||(LA10_76>='a' && LA10_76<='c')||(LA10_76>='f' && LA10_76<='z')) ) {s = 73;}

                        else if ( ((LA10_76>='\u0000' && LA10_76<='/')||(LA10_76>=':' && LA10_76<='@')||(LA10_76>='[' && LA10_76<='^')||LA10_76=='`'||(LA10_76>='{' && LA10_76<='\uFFFF')) ) {s = 74;}

                        else s = 21;

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA10_69 = input.LA(1);

                        s = -1;
                        if ( ((LA10_69>='0' && LA10_69<='9')||(LA10_69>='A' && LA10_69<='Z')||LA10_69=='_'||(LA10_69>='a' && LA10_69<='z')) ) {s = 73;}

                        else if ( ((LA10_69>='\u0000' && LA10_69<='/')||(LA10_69>=':' && LA10_69<='@')||(LA10_69>='[' && LA10_69<='^')||LA10_69=='`'||(LA10_69>='{' && LA10_69<='\uFFFF')) ) {s = 74;}

                        else s = 72;

                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA10_73 = input.LA(1);

                        s = -1;
                        if ( (LA10_73=='e') ) {s = 75;}

                        else if ( ((LA10_73>='0' && LA10_73<='9')||(LA10_73>='A' && LA10_73<='Z')||LA10_73=='_'||(LA10_73>='a' && LA10_73<='d')||(LA10_73>='f' && LA10_73<='z')) ) {s = 73;}

                        else if ( ((LA10_73>='\u0000' && LA10_73<='/')||(LA10_73>=':' && LA10_73<='@')||(LA10_73>='[' && LA10_73<='^')||LA10_73=='`'||(LA10_73>='{' && LA10_73<='\uFFFF')) ) {s = 74;}

                        else s = 21;

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