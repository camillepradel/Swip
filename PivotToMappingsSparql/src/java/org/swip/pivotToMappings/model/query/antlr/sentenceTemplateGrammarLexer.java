// $ANTLR 3.3 Nov 30, 2010 12:45:30 /home/camille/ANTLRWorks/sentenceTemplateGrammar.g 2013-01-26 13:55:16

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class sentenceTemplateGrammarLexer extends Lexer {
    public static final int EOF=-1;
    public static final int T__17=17;
    public static final int ENDSENT=4;
    public static final int SENT=5;
    public static final int ENDIF=6;
    public static final int UNDERSCORE=7;
    public static final int LEFTSB=8;
    public static final int RIGHTSB=9;
    public static final int Space=10;
    public static final int INT=11;
    public static final int IFF=12;
    public static final int ID=13;
    public static final int NormalChar=14;
    public static final int STRING=15;
    public static final int SpecialChar=16;

    // delegates
    // delegators

    public sentenceTemplateGrammarLexer() {;} 
    public sentenceTemplateGrammarLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public sentenceTemplateGrammarLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "/home/camille/ANTLRWorks/sentenceTemplateGrammar.g"; }

    // $ANTLR start "ENDSENT"
    public final void mENDSENT() throws RecognitionException {
        try {
            int _type = ENDSENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:3:9: ( 'end sentence' )
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:3:11: 'end sentence'
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

    // $ANTLR start "SENT"
    public final void mSENT() throws RecognitionException {
        try {
            int _type = SENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:4:6: ( 'sentence' )
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:4:8: 'sentence'
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

    // $ANTLR start "ENDIF"
    public final void mENDIF() throws RecognitionException {
        try {
            int _type = ENDIF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:5:7: ( '_end_if_' )
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:5:9: '_end_if_'
            {
            match("_end_if_"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ENDIF"

    // $ANTLR start "UNDERSCORE"
    public final void mUNDERSCORE() throws RecognitionException {
        try {
            int _type = UNDERSCORE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:6:12: ( '_' )
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:6:14: '_'
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

    // $ANTLR start "LEFTSB"
    public final void mLEFTSB() throws RecognitionException {
        try {
            int _type = LEFTSB;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:7:8: ( '[' )
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:7:10: '['
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
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:8:9: ( ']' )
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:8:11: ']'
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

    // $ANTLR start "T__17"
    public final void mT__17() throws RecognitionException {
        try {
            int _type = T__17;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:9:7: ( '\"' )
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:9:9: '\"'
            {
            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__17"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:39:5: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' )* )
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:39:7: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:39:27: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='Z')||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
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
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:42:5: ( ( '0' .. '9' )+ )
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:42:7: ( '0' .. '9' )+
            {
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:42:7: ( '0' .. '9' )+
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
            	    // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:42:7: '0' .. '9'
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

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:46:5: ( '\"' ( . ~ ( '\\\\' | '\"' ) )* '\"' )
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:46:8: '\"' ( . ~ ( '\\\\' | '\"' ) )* '\"'
            {
            match('\"'); 
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:46:12: ( . ~ ( '\\\\' | '\"' ) )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0=='\"') ) {
                    int LA3_1 = input.LA(2);

                    if ( ((LA3_1>='\u0000' && LA3_1<='!')||(LA3_1>='#' && LA3_1<='[')||(LA3_1>=']' && LA3_1<='\uFFFF')) ) {
                        alt3=1;
                    }


                }
                else if ( ((LA3_0>='\u0000' && LA3_0<='!')||(LA3_0>='#' && LA3_0<='\uFFFF')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:46:13: . ~ ( '\\\\' | '\"' )
            	    {
            	    matchAny(); 
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
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

            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "SpecialChar"
    public final void mSpecialChar() throws RecognitionException {
        try {
            int _type = SpecialChar;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:50:5: ( '\"' | '\\\\' | '$' )
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:
            {
            if ( input.LA(1)=='\"'||input.LA(1)=='$'||input.LA(1)=='\\' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SpecialChar"

    // $ANTLR start "IFF"
    public final void mIFF() throws RecognitionException {
        try {
            int _type = IFF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:53:6: ( '_if_' )
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:53:8: '_if_'
            {
            match("_if_"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IFF"

    // $ANTLR start "Space"
    public final void mSpace() throws RecognitionException {
        try {
            int _type = Space;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:56:5: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:56:10: ( ' ' | '\\t' | '\\r' | '\\n' )
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "Space"

    // $ANTLR start "NormalChar"
    public final void mNormalChar() throws RecognitionException {
        try {
            int _type = NormalChar;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:60:5: (~ SpecialChar )
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:60:10: ~ SpecialChar
            {
            if ( (input.LA(1)>='\u0000' && input.LA(1)<='\u000F')||(input.LA(1)>='\u0011' && input.LA(1)<='\uFFFF') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NormalChar"

    public void mTokens() throws RecognitionException {
        // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:1:8: ( ENDSENT | SENT | ENDIF | UNDERSCORE | LEFTSB | RIGHTSB | T__17 | ID | INT | STRING | SpecialChar | IFF | Space | NormalChar )
        int alt4=14;
        alt4 = dfa4.predict(input);
        switch (alt4) {
            case 1 :
                // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:1:10: ENDSENT
                {
                mENDSENT(); 

                }
                break;
            case 2 :
                // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:1:18: SENT
                {
                mSENT(); 

                }
                break;
            case 3 :
                // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:1:23: ENDIF
                {
                mENDIF(); 

                }
                break;
            case 4 :
                // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:1:29: UNDERSCORE
                {
                mUNDERSCORE(); 

                }
                break;
            case 5 :
                // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:1:40: LEFTSB
                {
                mLEFTSB(); 

                }
                break;
            case 6 :
                // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:1:47: RIGHTSB
                {
                mRIGHTSB(); 

                }
                break;
            case 7 :
                // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:1:55: T__17
                {
                mT__17(); 

                }
                break;
            case 8 :
                // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:1:61: ID
                {
                mID(); 

                }
                break;
            case 9 :
                // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:1:64: INT
                {
                mINT(); 

                }
                break;
            case 10 :
                // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:1:68: STRING
                {
                mSTRING(); 

                }
                break;
            case 11 :
                // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:1:75: SpecialChar
                {
                mSpecialChar(); 

                }
                break;
            case 12 :
                // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:1:87: IFF
                {
                mIFF(); 

                }
                break;
            case 13 :
                // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:1:91: Space
                {
                mSpace(); 

                }
                break;
            case 14 :
                // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:1:97: NormalChar
                {
                mNormalChar(); 

                }
                break;

        }

    }


    protected DFA4 dfa4 = new DFA4(this);
    static final String DFA4_eotS =
        "\1\uffff\2\15\1\21\2\uffff\1\24\5\uffff\1\15\1\uffff\1\15\11\uffff"+
        "\2\15\1\uffff\4\15\1\40\1\uffff";
    static final String DFA4_eofS =
        "\41\uffff";
    static final String DFA4_minS =
        "\1\0\1\156\2\145\2\uffff\1\0\5\uffff\1\144\1\uffff\1\156\11\uffff"+
        "\1\40\1\164\1\uffff\1\145\1\156\1\143\1\145\1\60\1\uffff";
    static final String DFA4_maxS =
        "\1\uffff\1\156\1\145\1\151\2\uffff\1\uffff\5\uffff\1\144\1\uffff"+
        "\1\156\11\uffff\1\40\1\164\1\uffff\1\145\1\156\1\143\1\145\1\172"+
        "\1\uffff";
    static final String DFA4_acceptS =
        "\4\uffff\1\5\1\6\1\uffff\1\10\1\11\1\13\1\15\1\16\1\uffff\1\10\1"+
        "\uffff\1\3\1\14\1\4\1\5\1\6\1\7\1\12\1\11\1\15\2\uffff\1\1\5\uffff"+
        "\1\2";
    static final String DFA4_specialS =
        "\1\0\5\uffff\1\1\32\uffff}>";
    static final String[] DFA4_transitionS = {
            "\11\13\2\12\2\13\1\12\22\13\1\12\1\13\1\6\1\13\1\11\13\13\12"+
            "\10\7\13\32\7\1\4\1\11\1\5\1\13\1\3\1\13\4\7\1\1\15\7\1\2\7"+
            "\7\uff85\13",
            "\1\14",
            "\1\16",
            "\1\17\3\uffff\1\20",
            "",
            "",
            "\0\25",
            "",
            "",
            "",
            "",
            "",
            "\1\30",
            "",
            "\1\31",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\32",
            "\1\33",
            "",
            "\1\34",
            "\1\35",
            "\1\36",
            "\1\37",
            "\12\15\7\uffff\32\15\6\uffff\32\15",
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
            return "1:1: Tokens : ( ENDSENT | SENT | ENDIF | UNDERSCORE | LEFTSB | RIGHTSB | T__17 | ID | INT | STRING | SpecialChar | IFF | Space | NormalChar );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA4_0 = input.LA(1);

                        s = -1;
                        if ( (LA4_0=='e') ) {s = 1;}

                        else if ( (LA4_0=='s') ) {s = 2;}

                        else if ( (LA4_0=='_') ) {s = 3;}

                        else if ( (LA4_0=='[') ) {s = 4;}

                        else if ( (LA4_0==']') ) {s = 5;}

                        else if ( (LA4_0=='\"') ) {s = 6;}

                        else if ( ((LA4_0>='A' && LA4_0<='Z')||(LA4_0>='a' && LA4_0<='d')||(LA4_0>='f' && LA4_0<='r')||(LA4_0>='t' && LA4_0<='z')) ) {s = 7;}

                        else if ( ((LA4_0>='0' && LA4_0<='9')) ) {s = 8;}

                        else if ( (LA4_0=='$'||LA4_0=='\\') ) {s = 9;}

                        else if ( ((LA4_0>='\t' && LA4_0<='\n')||LA4_0=='\r'||LA4_0==' ') ) {s = 10;}

                        else if ( ((LA4_0>='\u0000' && LA4_0<='\b')||(LA4_0>='\u000B' && LA4_0<='\f')||(LA4_0>='\u000E' && LA4_0<='\u001F')||LA4_0=='!'||LA4_0=='#'||(LA4_0>='%' && LA4_0<='/')||(LA4_0>=':' && LA4_0<='@')||LA4_0=='^'||LA4_0=='`'||(LA4_0>='{' && LA4_0<='\uFFFF')) ) {s = 11;}

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA4_6 = input.LA(1);

                        s = -1;
                        if ( ((LA4_6>='\u0000' && LA4_6<='\uFFFF')) ) {s = 21;}

                        else s = 20;

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 4, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}