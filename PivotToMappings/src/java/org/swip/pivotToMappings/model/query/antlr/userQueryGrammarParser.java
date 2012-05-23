// $ANTLR 3.4 /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g 2012-05-22 10:55:18

package org.swip.pivotToMappings.model.query.antlr;
import java.util.HashMap;
import org.swip.pivotToMappings.model.query.Query;
import org.swip.pivotToMappings.model.query.queryElement.*;
import org.swip.pivotToMappings.model.query.subquery.*;
import org.swip.pivotToMappings.exceptions.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class userQueryGrammarParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "COMMENT", "ID", "INT", "LITVALUE", "WS", "'('", "')'", "','", "'.'", "':'", "';'", "'<'", "'='", "'>'", "'?'", "'ASK'", "'AVG'", "'COUNT'", "'MAX'", "'MIN'", "'SUM'"
    };

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
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public userQueryGrammarParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public userQueryGrammarParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return userQueryGrammarParser.tokenNames; }
    public String getGrammarFileName() { return "/Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g"; }


    /** Map variable name to Integer object holding value */
    HashMap<String, Keyword> keywords = new HashMap<String, Keyword>();
    //HashMap<String, Variable> variables = new HashMap<String, Variable>();

    private void addRoleToQE(QueryElement qe, QeRole role) {
    	try {
    		qe.addRole(role);
    	} catch (QueryElementException ex) {
    		throw new QueryElementRuntimeException(ex.getMessage());
    	}
    }

    @Override
    public void reportError(RecognitionException re) {
    	throw new LexicalErrorRuntimeException("Lexical error at " + re.line + ":" + re.index + " - unexpected character: \"" + (char)re.getUnexpectedType() + "\"");
    }

    @Override
    public void recover(IntStream input, RecognitionException re) {
        	throw new LexicalErrorRuntimeException("Syntax error at " + re.line + ":" + re.index + " - unexpected character: \"" + (char)re.getUnexpectedType() + "\"");
    }



    // $ANTLR start "query"
    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:38:1: query returns [Query q] : subquerySet[$q] ( '.' subquerySet[$q] )* ( '.' )? ( ( 'COUNT' | 'MAX' | 'MIN' | 'AVG' | 'SUM' | 'ASK' ) ( '.' )? )? ;
    public final Query query() throws RecognitionException {
        Query q = null;


        try {
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:39:2: ( subquerySet[$q] ( '.' subquerySet[$q] )* ( '.' )? ( ( 'COUNT' | 'MAX' | 'MIN' | 'AVG' | 'SUM' | 'ASK' ) ( '.' )? )? )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:39:4: subquerySet[$q] ( '.' subquerySet[$q] )* ( '.' )? ( ( 'COUNT' | 'MAX' | 'MIN' | 'AVG' | 'SUM' | 'ASK' ) ( '.' )? )?
            {
            q = new Query();

            pushFollow(FOLLOW_subquerySet_in_query38);
            subquerySet(q);

            state._fsp--;


            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:39:40: ( '.' subquerySet[$q] )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==12) ) {
                    switch ( input.LA(2) ) {
                    case 21:
                        {
                        int LA1_3 = input.LA(3);

                        if ( (LA1_3==9) ) {
                            alt1=1;
                        }


                        }
                        break;
                    case 22:
                        {
                        int LA1_4 = input.LA(3);

                        if ( (LA1_4==9) ) {
                            alt1=1;
                        }


                        }
                        break;
                    case 23:
                        {
                        int LA1_5 = input.LA(3);

                        if ( (LA1_5==9) ) {
                            alt1=1;
                        }


                        }
                        break;
                    case 20:
                        {
                        int LA1_6 = input.LA(3);

                        if ( (LA1_6==9) ) {
                            alt1=1;
                        }


                        }
                        break;
                    case 24:
                        {
                        int LA1_7 = input.LA(3);

                        if ( (LA1_7==9) ) {
                            alt1=1;
                        }


                        }
                        break;
                    case ID:
                    case 18:
                        {
                        alt1=1;
                        }
                        break;

                    }

                }


                switch (alt1) {
            	case 1 :
            	    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:39:41: '.' subquerySet[$q]
            	    {
            	    match(input,12,FOLLOW_12_in_query42); 

            	    pushFollow(FOLLOW_subquerySet_in_query44);
            	    subquerySet(q);

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:39:63: ( '.' )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==12) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:39:64: '.'
                    {
                    match(input,12,FOLLOW_12_in_query50); 

                    }
                    break;

            }


            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:39:70: ( ( 'COUNT' | 'MAX' | 'MIN' | 'AVG' | 'SUM' | 'ASK' ) ( '.' )? )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( ((LA5_0 >= 19 && LA5_0 <= 24)) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:39:71: ( 'COUNT' | 'MAX' | 'MIN' | 'AVG' | 'SUM' | 'ASK' ) ( '.' )?
                    {
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:39:71: ( 'COUNT' | 'MAX' | 'MIN' | 'AVG' | 'SUM' | 'ASK' )
                    int alt3=6;
                    switch ( input.LA(1) ) {
                    case 21:
                        {
                        alt3=1;
                        }
                        break;
                    case 22:
                        {
                        alt3=2;
                        }
                        break;
                    case 23:
                        {
                        alt3=3;
                        }
                        break;
                    case 20:
                        {
                        alt3=4;
                        }
                        break;
                    case 24:
                        {
                        alt3=5;
                        }
                        break;
                    case 19:
                        {
                        alt3=6;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 3, 0, input);

                        throw nvae;

                    }

                    switch (alt3) {
                        case 1 :
                            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:39:72: 'COUNT'
                            {
                            match(input,21,FOLLOW_21_in_query56); 

                            q.setCount(true);

                            }
                            break;
                        case 2 :
                            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:39:102: 'MAX'
                            {
                            match(input,22,FOLLOW_22_in_query61); 

                            q.setMax(true);

                            }
                            break;
                        case 3 :
                            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:39:129: 'MIN'
                            {
                            match(input,23,FOLLOW_23_in_query67); 

                            q.setMin(true);

                            }
                            break;
                        case 4 :
                            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:39:155: 'AVG'
                            {
                            match(input,20,FOLLOW_20_in_query72); 

                            q.setAvg(true);

                            }
                            break;
                        case 5 :
                            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:39:181: 'SUM'
                            {
                            match(input,24,FOLLOW_24_in_query77); 

                            q.setSum(true);

                            }
                            break;
                        case 6 :
                            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:39:207: 'ASK'
                            {
                            match(input,19,FOLLOW_19_in_query82); 

                            q.setAsk(true);

                            }
                            break;

                    }


                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:39:233: ( '.' )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==12) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:39:234: '.'
                            {
                            match(input,12,FOLLOW_12_in_query88); 

                            }
                            break;

                    }


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return q;
    }
    // $ANTLR end "query"



    // $ANTLR start "subquerySet"
    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:42:1: subquerySet[Query q] : ( e1q23[$q] ':' q23End[$q, $e1q23.qe] ( ';' q23End[$q, $e1q23.qe] )* | e1q1[$q] );
    public final void subquerySet(Query q) throws RecognitionException {
        QueryElement e1q231 =null;

        QueryElement e1q12 =null;


        try {
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:43:2: ( e1q23[$q] ':' q23End[$q, $e1q23.qe] ( ';' q23End[$q, $e1q23.qe] )* | e1q1[$q] )
            int alt7=2;
            switch ( input.LA(1) ) {
            case 21:
                {
                int LA7_1 = input.LA(2);

                if ( (LA7_1==9) ) {
                    int LA7_8 = input.LA(3);

                    if ( (LA7_8==18) ) {
                        int LA7_12 = input.LA(4);

                        if ( (LA7_12==ID) ) {
                            int LA7_13 = input.LA(5);

                            if ( (LA7_13==10) ) {
                                switch ( input.LA(6) ) {
                                case 16:
                                    {
                                    int LA7_18 = input.LA(7);

                                    if ( (LA7_18==INT) ) {
                                        int LA7_21 = input.LA(8);

                                        if ( (LA7_21==13) ) {
                                            alt7=1;
                                        }
                                        else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                            alt7=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 7, 21, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 18, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 17:
                                    {
                                    int LA7_19 = input.LA(7);

                                    if ( (LA7_19==INT) ) {
                                        int LA7_21 = input.LA(8);

                                        if ( (LA7_21==13) ) {
                                            alt7=1;
                                        }
                                        else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                            alt7=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 7, 21, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 19, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 15:
                                    {
                                    int LA7_20 = input.LA(7);

                                    if ( (LA7_20==INT) ) {
                                        int LA7_21 = input.LA(8);

                                        if ( (LA7_21==13) ) {
                                            alt7=1;
                                        }
                                        else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                            alt7=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 7, 21, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 20, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                default:
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 7, 16, input);

                                    throw nvae;

                                }

                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 7, 13, input);

                                throw nvae;

                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 7, 12, input);

                            throw nvae;

                        }
                    }
                    else if ( (LA7_8==ID) ) {
                        int LA7_13 = input.LA(4);

                        if ( (LA7_13==10) ) {
                            switch ( input.LA(5) ) {
                            case 16:
                                {
                                int LA7_18 = input.LA(6);

                                if ( (LA7_18==INT) ) {
                                    int LA7_21 = input.LA(7);

                                    if ( (LA7_21==13) ) {
                                        alt7=1;
                                    }
                                    else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                        alt7=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 21, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 7, 18, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 17:
                                {
                                int LA7_19 = input.LA(6);

                                if ( (LA7_19==INT) ) {
                                    int LA7_21 = input.LA(7);

                                    if ( (LA7_21==13) ) {
                                        alt7=1;
                                    }
                                    else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                        alt7=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 21, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 7, 19, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 15:
                                {
                                int LA7_20 = input.LA(6);

                                if ( (LA7_20==INT) ) {
                                    int LA7_21 = input.LA(7);

                                    if ( (LA7_21==13) ) {
                                        alt7=1;
                                    }
                                    else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                        alt7=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 21, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 7, 20, input);

                                    throw nvae;

                                }
                                }
                                break;
                            default:
                                NoViableAltException nvae =
                                    new NoViableAltException("", 7, 16, input);

                                throw nvae;

                            }

                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 7, 13, input);

                            throw nvae;

                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 7, 8, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 7, 1, input);

                    throw nvae;

                }
                }
                break;
            case 22:
                {
                int LA7_2 = input.LA(2);

                if ( (LA7_2==9) ) {
                    int LA7_9 = input.LA(3);

                    if ( (LA7_9==18) ) {
                        int LA7_14 = input.LA(4);

                        if ( (LA7_14==ID) ) {
                            int LA7_15 = input.LA(5);

                            if ( (LA7_15==10) ) {
                                switch ( input.LA(6) ) {
                                case 16:
                                    {
                                    int LA7_18 = input.LA(7);

                                    if ( (LA7_18==INT) ) {
                                        int LA7_21 = input.LA(8);

                                        if ( (LA7_21==13) ) {
                                            alt7=1;
                                        }
                                        else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                            alt7=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 7, 21, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 18, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 17:
                                    {
                                    int LA7_19 = input.LA(7);

                                    if ( (LA7_19==INT) ) {
                                        int LA7_21 = input.LA(8);

                                        if ( (LA7_21==13) ) {
                                            alt7=1;
                                        }
                                        else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                            alt7=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 7, 21, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 19, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 15:
                                    {
                                    int LA7_20 = input.LA(7);

                                    if ( (LA7_20==INT) ) {
                                        int LA7_21 = input.LA(8);

                                        if ( (LA7_21==13) ) {
                                            alt7=1;
                                        }
                                        else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                            alt7=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 7, 21, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 20, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 13:
                                    {
                                    alt7=1;
                                    }
                                    break;
                                case EOF:
                                case 12:
                                case 19:
                                case 20:
                                case 21:
                                case 22:
                                case 23:
                                case 24:
                                    {
                                    alt7=2;
                                    }
                                    break;
                                default:
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 7, 17, input);

                                    throw nvae;

                                }

                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 7, 15, input);

                                throw nvae;

                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 7, 14, input);

                            throw nvae;

                        }
                    }
                    else if ( (LA7_9==ID) ) {
                        int LA7_15 = input.LA(4);

                        if ( (LA7_15==10) ) {
                            switch ( input.LA(5) ) {
                            case 16:
                                {
                                int LA7_18 = input.LA(6);

                                if ( (LA7_18==INT) ) {
                                    int LA7_21 = input.LA(7);

                                    if ( (LA7_21==13) ) {
                                        alt7=1;
                                    }
                                    else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                        alt7=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 21, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 7, 18, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 17:
                                {
                                int LA7_19 = input.LA(6);

                                if ( (LA7_19==INT) ) {
                                    int LA7_21 = input.LA(7);

                                    if ( (LA7_21==13) ) {
                                        alt7=1;
                                    }
                                    else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                        alt7=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 21, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 7, 19, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 15:
                                {
                                int LA7_20 = input.LA(6);

                                if ( (LA7_20==INT) ) {
                                    int LA7_21 = input.LA(7);

                                    if ( (LA7_21==13) ) {
                                        alt7=1;
                                    }
                                    else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                        alt7=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 21, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 7, 20, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 13:
                                {
                                alt7=1;
                                }
                                break;
                            case EOF:
                            case 12:
                            case 19:
                            case 20:
                            case 21:
                            case 22:
                            case 23:
                            case 24:
                                {
                                alt7=2;
                                }
                                break;
                            default:
                                NoViableAltException nvae =
                                    new NoViableAltException("", 7, 17, input);

                                throw nvae;

                            }

                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 7, 15, input);

                            throw nvae;

                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 7, 9, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 7, 2, input);

                    throw nvae;

                }
                }
                break;
            case 23:
                {
                int LA7_3 = input.LA(2);

                if ( (LA7_3==9) ) {
                    int LA7_9 = input.LA(3);

                    if ( (LA7_9==18) ) {
                        int LA7_14 = input.LA(4);

                        if ( (LA7_14==ID) ) {
                            int LA7_15 = input.LA(5);

                            if ( (LA7_15==10) ) {
                                switch ( input.LA(6) ) {
                                case 16:
                                    {
                                    int LA7_18 = input.LA(7);

                                    if ( (LA7_18==INT) ) {
                                        int LA7_21 = input.LA(8);

                                        if ( (LA7_21==13) ) {
                                            alt7=1;
                                        }
                                        else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                            alt7=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 7, 21, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 18, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 17:
                                    {
                                    int LA7_19 = input.LA(7);

                                    if ( (LA7_19==INT) ) {
                                        int LA7_21 = input.LA(8);

                                        if ( (LA7_21==13) ) {
                                            alt7=1;
                                        }
                                        else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                            alt7=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 7, 21, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 19, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 15:
                                    {
                                    int LA7_20 = input.LA(7);

                                    if ( (LA7_20==INT) ) {
                                        int LA7_21 = input.LA(8);

                                        if ( (LA7_21==13) ) {
                                            alt7=1;
                                        }
                                        else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                            alt7=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 7, 21, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 20, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 13:
                                    {
                                    alt7=1;
                                    }
                                    break;
                                case EOF:
                                case 12:
                                case 19:
                                case 20:
                                case 21:
                                case 22:
                                case 23:
                                case 24:
                                    {
                                    alt7=2;
                                    }
                                    break;
                                default:
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 7, 17, input);

                                    throw nvae;

                                }

                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 7, 15, input);

                                throw nvae;

                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 7, 14, input);

                            throw nvae;

                        }
                    }
                    else if ( (LA7_9==ID) ) {
                        int LA7_15 = input.LA(4);

                        if ( (LA7_15==10) ) {
                            switch ( input.LA(5) ) {
                            case 16:
                                {
                                int LA7_18 = input.LA(6);

                                if ( (LA7_18==INT) ) {
                                    int LA7_21 = input.LA(7);

                                    if ( (LA7_21==13) ) {
                                        alt7=1;
                                    }
                                    else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                        alt7=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 21, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 7, 18, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 17:
                                {
                                int LA7_19 = input.LA(6);

                                if ( (LA7_19==INT) ) {
                                    int LA7_21 = input.LA(7);

                                    if ( (LA7_21==13) ) {
                                        alt7=1;
                                    }
                                    else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                        alt7=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 21, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 7, 19, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 15:
                                {
                                int LA7_20 = input.LA(6);

                                if ( (LA7_20==INT) ) {
                                    int LA7_21 = input.LA(7);

                                    if ( (LA7_21==13) ) {
                                        alt7=1;
                                    }
                                    else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                        alt7=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 21, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 7, 20, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 13:
                                {
                                alt7=1;
                                }
                                break;
                            case EOF:
                            case 12:
                            case 19:
                            case 20:
                            case 21:
                            case 22:
                            case 23:
                            case 24:
                                {
                                alt7=2;
                                }
                                break;
                            default:
                                NoViableAltException nvae =
                                    new NoViableAltException("", 7, 17, input);

                                throw nvae;

                            }

                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 7, 15, input);

                            throw nvae;

                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 7, 9, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 7, 3, input);

                    throw nvae;

                }
                }
                break;
            case 20:
                {
                int LA7_4 = input.LA(2);

                if ( (LA7_4==9) ) {
                    int LA7_8 = input.LA(3);

                    if ( (LA7_8==18) ) {
                        int LA7_12 = input.LA(4);

                        if ( (LA7_12==ID) ) {
                            int LA7_13 = input.LA(5);

                            if ( (LA7_13==10) ) {
                                switch ( input.LA(6) ) {
                                case 16:
                                    {
                                    int LA7_18 = input.LA(7);

                                    if ( (LA7_18==INT) ) {
                                        int LA7_21 = input.LA(8);

                                        if ( (LA7_21==13) ) {
                                            alt7=1;
                                        }
                                        else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                            alt7=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 7, 21, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 18, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 17:
                                    {
                                    int LA7_19 = input.LA(7);

                                    if ( (LA7_19==INT) ) {
                                        int LA7_21 = input.LA(8);

                                        if ( (LA7_21==13) ) {
                                            alt7=1;
                                        }
                                        else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                            alt7=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 7, 21, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 19, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 15:
                                    {
                                    int LA7_20 = input.LA(7);

                                    if ( (LA7_20==INT) ) {
                                        int LA7_21 = input.LA(8);

                                        if ( (LA7_21==13) ) {
                                            alt7=1;
                                        }
                                        else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                            alt7=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 7, 21, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 20, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                default:
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 7, 16, input);

                                    throw nvae;

                                }

                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 7, 13, input);

                                throw nvae;

                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 7, 12, input);

                            throw nvae;

                        }
                    }
                    else if ( (LA7_8==ID) ) {
                        int LA7_13 = input.LA(4);

                        if ( (LA7_13==10) ) {
                            switch ( input.LA(5) ) {
                            case 16:
                                {
                                int LA7_18 = input.LA(6);

                                if ( (LA7_18==INT) ) {
                                    int LA7_21 = input.LA(7);

                                    if ( (LA7_21==13) ) {
                                        alt7=1;
                                    }
                                    else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                        alt7=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 21, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 7, 18, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 17:
                                {
                                int LA7_19 = input.LA(6);

                                if ( (LA7_19==INT) ) {
                                    int LA7_21 = input.LA(7);

                                    if ( (LA7_21==13) ) {
                                        alt7=1;
                                    }
                                    else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                        alt7=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 21, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 7, 19, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 15:
                                {
                                int LA7_20 = input.LA(6);

                                if ( (LA7_20==INT) ) {
                                    int LA7_21 = input.LA(7);

                                    if ( (LA7_21==13) ) {
                                        alt7=1;
                                    }
                                    else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                        alt7=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 21, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 7, 20, input);

                                    throw nvae;

                                }
                                }
                                break;
                            default:
                                NoViableAltException nvae =
                                    new NoViableAltException("", 7, 16, input);

                                throw nvae;

                            }

                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 7, 13, input);

                            throw nvae;

                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 7, 8, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 7, 4, input);

                    throw nvae;

                }
                }
                break;
            case 24:
                {
                int LA7_5 = input.LA(2);

                if ( (LA7_5==9) ) {
                    int LA7_8 = input.LA(3);

                    if ( (LA7_8==18) ) {
                        int LA7_12 = input.LA(4);

                        if ( (LA7_12==ID) ) {
                            int LA7_13 = input.LA(5);

                            if ( (LA7_13==10) ) {
                                switch ( input.LA(6) ) {
                                case 16:
                                    {
                                    int LA7_18 = input.LA(7);

                                    if ( (LA7_18==INT) ) {
                                        int LA7_21 = input.LA(8);

                                        if ( (LA7_21==13) ) {
                                            alt7=1;
                                        }
                                        else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                            alt7=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 7, 21, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 18, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 17:
                                    {
                                    int LA7_19 = input.LA(7);

                                    if ( (LA7_19==INT) ) {
                                        int LA7_21 = input.LA(8);

                                        if ( (LA7_21==13) ) {
                                            alt7=1;
                                        }
                                        else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                            alt7=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 7, 21, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 19, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 15:
                                    {
                                    int LA7_20 = input.LA(7);

                                    if ( (LA7_20==INT) ) {
                                        int LA7_21 = input.LA(8);

                                        if ( (LA7_21==13) ) {
                                            alt7=1;
                                        }
                                        else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                            alt7=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 7, 21, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 20, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                default:
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 7, 16, input);

                                    throw nvae;

                                }

                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 7, 13, input);

                                throw nvae;

                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 7, 12, input);

                            throw nvae;

                        }
                    }
                    else if ( (LA7_8==ID) ) {
                        int LA7_13 = input.LA(4);

                        if ( (LA7_13==10) ) {
                            switch ( input.LA(5) ) {
                            case 16:
                                {
                                int LA7_18 = input.LA(6);

                                if ( (LA7_18==INT) ) {
                                    int LA7_21 = input.LA(7);

                                    if ( (LA7_21==13) ) {
                                        alt7=1;
                                    }
                                    else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                        alt7=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 21, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 7, 18, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 17:
                                {
                                int LA7_19 = input.LA(6);

                                if ( (LA7_19==INT) ) {
                                    int LA7_21 = input.LA(7);

                                    if ( (LA7_21==13) ) {
                                        alt7=1;
                                    }
                                    else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                        alt7=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 21, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 7, 19, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 15:
                                {
                                int LA7_20 = input.LA(6);

                                if ( (LA7_20==INT) ) {
                                    int LA7_21 = input.LA(7);

                                    if ( (LA7_21==13) ) {
                                        alt7=1;
                                    }
                                    else if ( (LA7_21==EOF||LA7_21==12||(LA7_21 >= 19 && LA7_21 <= 24)) ) {
                                        alt7=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 7, 21, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 7, 20, input);

                                    throw nvae;

                                }
                                }
                                break;
                            default:
                                NoViableAltException nvae =
                                    new NoViableAltException("", 7, 16, input);

                                throw nvae;

                            }

                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 7, 13, input);

                            throw nvae;

                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 7, 8, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 7, 5, input);

                    throw nvae;

                }
                }
                break;
            case 18:
                {
                int LA7_6 = input.LA(2);

                if ( (LA7_6==ID) ) {
                    int LA7_7 = input.LA(3);

                    if ( (LA7_7==13) ) {
                        alt7=1;
                    }
                    else if ( (LA7_7==EOF||LA7_7==12||(LA7_7 >= 19 && LA7_7 <= 24)) ) {
                        alt7=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 7, 7, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 7, 6, input);

                    throw nvae;

                }
                }
                break;
            case ID:
                {
                int LA7_7 = input.LA(2);

                if ( (LA7_7==13) ) {
                    alt7=1;
                }
                else if ( (LA7_7==EOF||LA7_7==12||(LA7_7 >= 19 && LA7_7 <= 24)) ) {
                    alt7=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 7, 7, input);

                    throw nvae;

                }
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;

            }

            switch (alt7) {
                case 1 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:43:4: e1q23[$q] ':' q23End[$q, $e1q23.qe] ( ';' q23End[$q, $e1q23.qe] )*
                    {
                    pushFollow(FOLLOW_e1q23_in_subquerySet108);
                    e1q231=e1q23(q);

                    state._fsp--;


                    match(input,13,FOLLOW_13_in_subquerySet111); 

                    pushFollow(FOLLOW_q23End_in_subquerySet113);
                    q23End(q, e1q231);

                    state._fsp--;


                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:43:40: ( ';' q23End[$q, $e1q23.qe] )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==14) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:43:41: ';' q23End[$q, $e1q23.qe]
                    	    {
                    	    match(input,14,FOLLOW_14_in_subquerySet117); 

                    	    pushFollow(FOLLOW_q23End_in_subquerySet119);
                    	    q23End(q, e1q231);

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:44:4: e1q1[$q]
                    {
                    pushFollow(FOLLOW_e1q1_in_subquerySet127);
                    e1q12=e1q1(q);

                    state._fsp--;


                    q.addSubquery(new Q1(e1q12));

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
    // $ANTLR end "subquerySet"



    // $ANTLR start "e1q1"
    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:47:1: e1q1[Query q] returns [QueryElement qe] : keyword[$q] ;
    public final QueryElement e1q1(Query q) throws RecognitionException {
        QueryElement qe = null;


        Keyword keyword3 =null;


        try {
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:48:2: ( keyword[$q] )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:48:4: keyword[$q]
            {
            pushFollow(FOLLOW_keyword_in_e1q1149);
            keyword3=keyword(q);

            state._fsp--;


            qe = keyword3; addRoleToQE(qe,QeRole.E1Q1);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return qe;
    }
    // $ANTLR end "e1q1"



    // $ANTLR start "e1q23"
    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:51:1: e1q23[Query q] returns [QueryElement qe] : keyword[$q] ;
    public final QueryElement e1q23(Query q) throws RecognitionException {
        QueryElement qe = null;


        Keyword keyword4 =null;


        try {
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:52:2: ( keyword[$q] )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:52:4: keyword[$q]
            {
            pushFollow(FOLLOW_keyword_in_e1q23170);
            keyword4=keyword(q);

            state._fsp--;


            qe = keyword4; addRoleToQE(qe, QeRole.E1Q23);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return qe;
    }
    // $ANTLR end "e1q23"



    // $ANTLR start "q23End"
    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:56:1: q23End[Query q, QueryElement e1] : ( e2q3[$q] '=' q3End[$q, $e1, $e2q3.qe] ( ',' q3End[$q, $e1, $e2q3.qe] )* | e2q2[$q] );
    public final void q23End(Query q, QueryElement e1) throws RecognitionException {
        QueryElement e2q35 =null;

        QueryElement e2q26 =null;


        try {
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:57:2: ( e2q3[$q] '=' q3End[$q, $e1, $e2q3.qe] ( ',' q3End[$q, $e1, $e2q3.qe] )* | e2q2[$q] )
            int alt9=2;
            switch ( input.LA(1) ) {
            case 21:
                {
                int LA9_1 = input.LA(2);

                if ( (LA9_1==9) ) {
                    int LA9_8 = input.LA(3);

                    if ( (LA9_8==18) ) {
                        int LA9_13 = input.LA(4);

                        if ( (LA9_13==ID) ) {
                            int LA9_14 = input.LA(5);

                            if ( (LA9_14==10) ) {
                                switch ( input.LA(6) ) {
                                case 16:
                                    {
                                    int LA9_19 = input.LA(7);

                                    if ( (LA9_19==INT) ) {
                                        int LA9_23 = input.LA(8);

                                        if ( (LA9_23==16) ) {
                                            alt9=1;
                                        }
                                        else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                            alt9=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 9, 23, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 19, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 17:
                                    {
                                    int LA9_20 = input.LA(7);

                                    if ( (LA9_20==INT) ) {
                                        int LA9_23 = input.LA(8);

                                        if ( (LA9_23==16) ) {
                                            alt9=1;
                                        }
                                        else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                            alt9=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 9, 23, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 20, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 15:
                                    {
                                    int LA9_21 = input.LA(7);

                                    if ( (LA9_21==INT) ) {
                                        int LA9_23 = input.LA(8);

                                        if ( (LA9_23==16) ) {
                                            alt9=1;
                                        }
                                        else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                            alt9=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 9, 23, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 21, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                default:
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 9, 17, input);

                                    throw nvae;

                                }

                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 9, 14, input);

                                throw nvae;

                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 9, 13, input);

                            throw nvae;

                        }
                    }
                    else if ( (LA9_8==ID) ) {
                        int LA9_14 = input.LA(4);

                        if ( (LA9_14==10) ) {
                            switch ( input.LA(5) ) {
                            case 16:
                                {
                                int LA9_19 = input.LA(6);

                                if ( (LA9_19==INT) ) {
                                    int LA9_23 = input.LA(7);

                                    if ( (LA9_23==16) ) {
                                        alt9=1;
                                    }
                                    else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                        alt9=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 23, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 9, 19, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 17:
                                {
                                int LA9_20 = input.LA(6);

                                if ( (LA9_20==INT) ) {
                                    int LA9_23 = input.LA(7);

                                    if ( (LA9_23==16) ) {
                                        alt9=1;
                                    }
                                    else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                        alt9=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 23, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 9, 20, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 15:
                                {
                                int LA9_21 = input.LA(6);

                                if ( (LA9_21==INT) ) {
                                    int LA9_23 = input.LA(7);

                                    if ( (LA9_23==16) ) {
                                        alt9=1;
                                    }
                                    else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                        alt9=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 23, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 9, 21, input);

                                    throw nvae;

                                }
                                }
                                break;
                            default:
                                NoViableAltException nvae =
                                    new NoViableAltException("", 9, 17, input);

                                throw nvae;

                            }

                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 9, 14, input);

                            throw nvae;

                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 9, 8, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 1, input);

                    throw nvae;

                }
                }
                break;
            case 22:
                {
                int LA9_2 = input.LA(2);

                if ( (LA9_2==9) ) {
                    int LA9_9 = input.LA(3);

                    if ( (LA9_9==18) ) {
                        int LA9_15 = input.LA(4);

                        if ( (LA9_15==ID) ) {
                            int LA9_16 = input.LA(5);

                            if ( (LA9_16==10) ) {
                                switch ( input.LA(6) ) {
                                case 16:
                                    {
                                    int LA9_22 = input.LA(7);

                                    if ( (LA9_22==INT) ) {
                                        int LA9_23 = input.LA(8);

                                        if ( (LA9_23==16) ) {
                                            alt9=1;
                                        }
                                        else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                            alt9=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 9, 23, input);

                                            throw nvae;

                                        }
                                    }
                                    else if ( (LA9_22==ID||LA9_22==18||(LA9_22 >= 20 && LA9_22 <= 24)) ) {
                                        alt9=1;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 22, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 17:
                                    {
                                    int LA9_20 = input.LA(7);

                                    if ( (LA9_20==INT) ) {
                                        int LA9_23 = input.LA(8);

                                        if ( (LA9_23==16) ) {
                                            alt9=1;
                                        }
                                        else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                            alt9=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 9, 23, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 20, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 15:
                                    {
                                    int LA9_21 = input.LA(7);

                                    if ( (LA9_21==INT) ) {
                                        int LA9_23 = input.LA(8);

                                        if ( (LA9_23==16) ) {
                                            alt9=1;
                                        }
                                        else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                            alt9=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 9, 23, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 21, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case EOF:
                                case 12:
                                case 14:
                                case 19:
                                case 20:
                                case 21:
                                case 22:
                                case 23:
                                case 24:
                                    {
                                    alt9=2;
                                    }
                                    break;
                                default:
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 9, 18, input);

                                    throw nvae;

                                }

                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 9, 16, input);

                                throw nvae;

                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 9, 15, input);

                            throw nvae;

                        }
                    }
                    else if ( (LA9_9==ID) ) {
                        int LA9_16 = input.LA(4);

                        if ( (LA9_16==10) ) {
                            switch ( input.LA(5) ) {
                            case 16:
                                {
                                int LA9_22 = input.LA(6);

                                if ( (LA9_22==INT) ) {
                                    int LA9_23 = input.LA(7);

                                    if ( (LA9_23==16) ) {
                                        alt9=1;
                                    }
                                    else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                        alt9=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 23, input);

                                        throw nvae;

                                    }
                                }
                                else if ( (LA9_22==ID||LA9_22==18||(LA9_22 >= 20 && LA9_22 <= 24)) ) {
                                    alt9=1;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 9, 22, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 17:
                                {
                                int LA9_20 = input.LA(6);

                                if ( (LA9_20==INT) ) {
                                    int LA9_23 = input.LA(7);

                                    if ( (LA9_23==16) ) {
                                        alt9=1;
                                    }
                                    else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                        alt9=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 23, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 9, 20, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 15:
                                {
                                int LA9_21 = input.LA(6);

                                if ( (LA9_21==INT) ) {
                                    int LA9_23 = input.LA(7);

                                    if ( (LA9_23==16) ) {
                                        alt9=1;
                                    }
                                    else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                        alt9=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 23, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 9, 21, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case EOF:
                            case 12:
                            case 14:
                            case 19:
                            case 20:
                            case 21:
                            case 22:
                            case 23:
                            case 24:
                                {
                                alt9=2;
                                }
                                break;
                            default:
                                NoViableAltException nvae =
                                    new NoViableAltException("", 9, 18, input);

                                throw nvae;

                            }

                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 9, 16, input);

                            throw nvae;

                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 9, 9, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 2, input);

                    throw nvae;

                }
                }
                break;
            case 23:
                {
                int LA9_3 = input.LA(2);

                if ( (LA9_3==9) ) {
                    int LA9_9 = input.LA(3);

                    if ( (LA9_9==18) ) {
                        int LA9_15 = input.LA(4);

                        if ( (LA9_15==ID) ) {
                            int LA9_16 = input.LA(5);

                            if ( (LA9_16==10) ) {
                                switch ( input.LA(6) ) {
                                case 16:
                                    {
                                    int LA9_22 = input.LA(7);

                                    if ( (LA9_22==INT) ) {
                                        int LA9_23 = input.LA(8);

                                        if ( (LA9_23==16) ) {
                                            alt9=1;
                                        }
                                        else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                            alt9=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 9, 23, input);

                                            throw nvae;

                                        }
                                    }
                                    else if ( (LA9_22==ID||LA9_22==18||(LA9_22 >= 20 && LA9_22 <= 24)) ) {
                                        alt9=1;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 22, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 17:
                                    {
                                    int LA9_20 = input.LA(7);

                                    if ( (LA9_20==INT) ) {
                                        int LA9_23 = input.LA(8);

                                        if ( (LA9_23==16) ) {
                                            alt9=1;
                                        }
                                        else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                            alt9=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 9, 23, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 20, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 15:
                                    {
                                    int LA9_21 = input.LA(7);

                                    if ( (LA9_21==INT) ) {
                                        int LA9_23 = input.LA(8);

                                        if ( (LA9_23==16) ) {
                                            alt9=1;
                                        }
                                        else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                            alt9=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 9, 23, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 21, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case EOF:
                                case 12:
                                case 14:
                                case 19:
                                case 20:
                                case 21:
                                case 22:
                                case 23:
                                case 24:
                                    {
                                    alt9=2;
                                    }
                                    break;
                                default:
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 9, 18, input);

                                    throw nvae;

                                }

                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 9, 16, input);

                                throw nvae;

                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 9, 15, input);

                            throw nvae;

                        }
                    }
                    else if ( (LA9_9==ID) ) {
                        int LA9_16 = input.LA(4);

                        if ( (LA9_16==10) ) {
                            switch ( input.LA(5) ) {
                            case 16:
                                {
                                int LA9_22 = input.LA(6);

                                if ( (LA9_22==INT) ) {
                                    int LA9_23 = input.LA(7);

                                    if ( (LA9_23==16) ) {
                                        alt9=1;
                                    }
                                    else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                        alt9=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 23, input);

                                        throw nvae;

                                    }
                                }
                                else if ( (LA9_22==ID||LA9_22==18||(LA9_22 >= 20 && LA9_22 <= 24)) ) {
                                    alt9=1;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 9, 22, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 17:
                                {
                                int LA9_20 = input.LA(6);

                                if ( (LA9_20==INT) ) {
                                    int LA9_23 = input.LA(7);

                                    if ( (LA9_23==16) ) {
                                        alt9=1;
                                    }
                                    else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                        alt9=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 23, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 9, 20, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 15:
                                {
                                int LA9_21 = input.LA(6);

                                if ( (LA9_21==INT) ) {
                                    int LA9_23 = input.LA(7);

                                    if ( (LA9_23==16) ) {
                                        alt9=1;
                                    }
                                    else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                        alt9=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 23, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 9, 21, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case EOF:
                            case 12:
                            case 14:
                            case 19:
                            case 20:
                            case 21:
                            case 22:
                            case 23:
                            case 24:
                                {
                                alt9=2;
                                }
                                break;
                            default:
                                NoViableAltException nvae =
                                    new NoViableAltException("", 9, 18, input);

                                throw nvae;

                            }

                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 9, 16, input);

                            throw nvae;

                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 9, 9, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 3, input);

                    throw nvae;

                }
                }
                break;
            case 20:
                {
                int LA9_4 = input.LA(2);

                if ( (LA9_4==9) ) {
                    int LA9_8 = input.LA(3);

                    if ( (LA9_8==18) ) {
                        int LA9_13 = input.LA(4);

                        if ( (LA9_13==ID) ) {
                            int LA9_14 = input.LA(5);

                            if ( (LA9_14==10) ) {
                                switch ( input.LA(6) ) {
                                case 16:
                                    {
                                    int LA9_19 = input.LA(7);

                                    if ( (LA9_19==INT) ) {
                                        int LA9_23 = input.LA(8);

                                        if ( (LA9_23==16) ) {
                                            alt9=1;
                                        }
                                        else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                            alt9=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 9, 23, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 19, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 17:
                                    {
                                    int LA9_20 = input.LA(7);

                                    if ( (LA9_20==INT) ) {
                                        int LA9_23 = input.LA(8);

                                        if ( (LA9_23==16) ) {
                                            alt9=1;
                                        }
                                        else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                            alt9=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 9, 23, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 20, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 15:
                                    {
                                    int LA9_21 = input.LA(7);

                                    if ( (LA9_21==INT) ) {
                                        int LA9_23 = input.LA(8);

                                        if ( (LA9_23==16) ) {
                                            alt9=1;
                                        }
                                        else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                            alt9=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 9, 23, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 21, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                default:
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 9, 17, input);

                                    throw nvae;

                                }

                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 9, 14, input);

                                throw nvae;

                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 9, 13, input);

                            throw nvae;

                        }
                    }
                    else if ( (LA9_8==ID) ) {
                        int LA9_14 = input.LA(4);

                        if ( (LA9_14==10) ) {
                            switch ( input.LA(5) ) {
                            case 16:
                                {
                                int LA9_19 = input.LA(6);

                                if ( (LA9_19==INT) ) {
                                    int LA9_23 = input.LA(7);

                                    if ( (LA9_23==16) ) {
                                        alt9=1;
                                    }
                                    else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                        alt9=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 23, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 9, 19, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 17:
                                {
                                int LA9_20 = input.LA(6);

                                if ( (LA9_20==INT) ) {
                                    int LA9_23 = input.LA(7);

                                    if ( (LA9_23==16) ) {
                                        alt9=1;
                                    }
                                    else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                        alt9=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 23, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 9, 20, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 15:
                                {
                                int LA9_21 = input.LA(6);

                                if ( (LA9_21==INT) ) {
                                    int LA9_23 = input.LA(7);

                                    if ( (LA9_23==16) ) {
                                        alt9=1;
                                    }
                                    else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                        alt9=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 23, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 9, 21, input);

                                    throw nvae;

                                }
                                }
                                break;
                            default:
                                NoViableAltException nvae =
                                    new NoViableAltException("", 9, 17, input);

                                throw nvae;

                            }

                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 9, 14, input);

                            throw nvae;

                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 9, 8, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 4, input);

                    throw nvae;

                }
                }
                break;
            case 24:
                {
                int LA9_5 = input.LA(2);

                if ( (LA9_5==9) ) {
                    int LA9_8 = input.LA(3);

                    if ( (LA9_8==18) ) {
                        int LA9_13 = input.LA(4);

                        if ( (LA9_13==ID) ) {
                            int LA9_14 = input.LA(5);

                            if ( (LA9_14==10) ) {
                                switch ( input.LA(6) ) {
                                case 16:
                                    {
                                    int LA9_19 = input.LA(7);

                                    if ( (LA9_19==INT) ) {
                                        int LA9_23 = input.LA(8);

                                        if ( (LA9_23==16) ) {
                                            alt9=1;
                                        }
                                        else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                            alt9=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 9, 23, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 19, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 17:
                                    {
                                    int LA9_20 = input.LA(7);

                                    if ( (LA9_20==INT) ) {
                                        int LA9_23 = input.LA(8);

                                        if ( (LA9_23==16) ) {
                                            alt9=1;
                                        }
                                        else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                            alt9=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 9, 23, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 20, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 15:
                                    {
                                    int LA9_21 = input.LA(7);

                                    if ( (LA9_21==INT) ) {
                                        int LA9_23 = input.LA(8);

                                        if ( (LA9_23==16) ) {
                                            alt9=1;
                                        }
                                        else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                            alt9=2;
                                        }
                                        else {
                                            NoViableAltException nvae =
                                                new NoViableAltException("", 9, 23, input);

                                            throw nvae;

                                        }
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 21, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                default:
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 9, 17, input);

                                    throw nvae;

                                }

                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 9, 14, input);

                                throw nvae;

                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 9, 13, input);

                            throw nvae;

                        }
                    }
                    else if ( (LA9_8==ID) ) {
                        int LA9_14 = input.LA(4);

                        if ( (LA9_14==10) ) {
                            switch ( input.LA(5) ) {
                            case 16:
                                {
                                int LA9_19 = input.LA(6);

                                if ( (LA9_19==INT) ) {
                                    int LA9_23 = input.LA(7);

                                    if ( (LA9_23==16) ) {
                                        alt9=1;
                                    }
                                    else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                        alt9=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 23, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 9, 19, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 17:
                                {
                                int LA9_20 = input.LA(6);

                                if ( (LA9_20==INT) ) {
                                    int LA9_23 = input.LA(7);

                                    if ( (LA9_23==16) ) {
                                        alt9=1;
                                    }
                                    else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                        alt9=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 23, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 9, 20, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 15:
                                {
                                int LA9_21 = input.LA(6);

                                if ( (LA9_21==INT) ) {
                                    int LA9_23 = input.LA(7);

                                    if ( (LA9_23==16) ) {
                                        alt9=1;
                                    }
                                    else if ( (LA9_23==EOF||LA9_23==12||LA9_23==14||(LA9_23 >= 19 && LA9_23 <= 24)) ) {
                                        alt9=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 9, 23, input);

                                        throw nvae;

                                    }
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 9, 21, input);

                                    throw nvae;

                                }
                                }
                                break;
                            default:
                                NoViableAltException nvae =
                                    new NoViableAltException("", 9, 17, input);

                                throw nvae;

                            }

                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 9, 14, input);

                            throw nvae;

                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 9, 8, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 5, input);

                    throw nvae;

                }
                }
                break;
            case 18:
                {
                int LA9_6 = input.LA(2);

                if ( (LA9_6==ID) ) {
                    int LA9_10 = input.LA(3);

                    if ( (LA9_10==16) ) {
                        alt9=1;
                    }
                    else if ( (LA9_10==EOF||LA9_10==12||LA9_10==14||(LA9_10 >= 19 && LA9_10 <= 24)) ) {
                        alt9=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 9, 10, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 6, input);

                    throw nvae;

                }
                }
                break;
            case ID:
                {
                int LA9_7 = input.LA(2);

                if ( (LA9_7==EOF||LA9_7==LITVALUE||LA9_7==12||LA9_7==14||(LA9_7 >= 19 && LA9_7 <= 24)) ) {
                    alt9=2;
                }
                else if ( (LA9_7==16) ) {
                    alt9=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 7, input);

                    throw nvae;

                }
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;

            }

            switch (alt9) {
                case 1 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:57:4: e2q3[$q] '=' q3End[$q, $e1, $e2q3.qe] ( ',' q3End[$q, $e1, $e2q3.qe] )*
                    {
                    pushFollow(FOLLOW_e2q3_in_q23End189);
                    e2q35=e2q3(q);

                    state._fsp--;


                    match(input,16,FOLLOW_16_in_q23End192); 

                    pushFollow(FOLLOW_q3End_in_q23End194);
                    q3End(q, e1, e2q35);

                    state._fsp--;


                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:57:42: ( ',' q3End[$q, $e1, $e2q3.qe] )*
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( (LA8_0==11) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:57:43: ',' q3End[$q, $e1, $e2q3.qe]
                    	    {
                    	    match(input,11,FOLLOW_11_in_q23End198); 

                    	    pushFollow(FOLLOW_q3End_in_q23End200);
                    	    q3End(q, e1, e2q35);

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop8;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:58:4: e2q2[$q]
                    {
                    pushFollow(FOLLOW_e2q2_in_q23End208);
                    e2q26=e2q2(q);

                    state._fsp--;


                    q.addSubquery(new Q2(e1, e2q26));

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
    // $ANTLR end "q23End"



    // $ANTLR start "e2q2"
    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:61:1: e2q2[Query q] returns [QueryElement qe] : ( keyword[$q] | literal[$q] );
    public final QueryElement e2q2(Query q) throws RecognitionException {
        QueryElement qe = null;


        Keyword keyword7 =null;

        Literal literal8 =null;


        try {
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:62:2: ( keyword[$q] | literal[$q] )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==18||(LA10_0 >= 20 && LA10_0 <= 24)) ) {
                alt10=1;
            }
            else if ( (LA10_0==ID) ) {
                int LA10_2 = input.LA(2);

                if ( (LA10_2==LITVALUE) ) {
                    alt10=2;
                }
                else if ( (LA10_2==EOF||LA10_2==12||LA10_2==14||(LA10_2 >= 19 && LA10_2 <= 24)) ) {
                    alt10=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 2, input);

                    throw nvae;

                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;

            }
            switch (alt10) {
                case 1 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:62:4: keyword[$q]
                    {
                    pushFollow(FOLLOW_keyword_in_e2q2230);
                    keyword7=keyword(q);

                    state._fsp--;


                    qe = keyword7; addRoleToQE(qe, QeRole.E2Q2);

                    }
                    break;
                case 2 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:63:4: literal[$q]
                    {
                    pushFollow(FOLLOW_literal_in_e2q2238);
                    literal8=literal(q);

                    state._fsp--;


                    qe = literal8; addRoleToQE(qe, QeRole.E2Q2);

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
        return qe;
    }
    // $ANTLR end "e2q2"



    // $ANTLR start "e2q3"
    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:67:1: e2q3[Query q] returns [QueryElement qe] : keyword[$q] ;
    public final QueryElement e2q3(Query q) throws RecognitionException {
        QueryElement qe = null;


        Keyword keyword9 =null;


        try {
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:68:2: ( keyword[$q] )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:68:4: keyword[$q]
            {
            pushFollow(FOLLOW_keyword_in_e2q3261);
            keyword9=keyword(q);

            state._fsp--;


            qe = keyword9; addRoleToQE(qe, QeRole.E2Q3);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return qe;
    }
    // $ANTLR end "e2q3"



    // $ANTLR start "q3End"
    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:72:1: q3End[Query q, QueryElement e1, QueryElement e2] : e3q3[$q] ;
    public final void q3End(Query q, QueryElement e1, QueryElement e2) throws RecognitionException {
        QueryElement e3q310 =null;


        try {
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:73:2: ( e3q3[$q] )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:73:4: e3q3[$q]
            {
            pushFollow(FOLLOW_e3q3_in_q3End279);
            e3q310=e3q3(q);

            state._fsp--;


            q.addSubquery(new Q3(e1, e2, e3q310));

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
    // $ANTLR end "q3End"



    // $ANTLR start "e3q3"
    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:76:1: e3q3[Query q] returns [QueryElement qe] : ( keyword[$q] | literal[$q] );
    public final QueryElement e3q3(Query q) throws RecognitionException {
        QueryElement qe = null;


        Keyword keyword11 =null;

        Literal literal12 =null;


        try {
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:77:2: ( keyword[$q] | literal[$q] )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==18||(LA11_0 >= 20 && LA11_0 <= 24)) ) {
                alt11=1;
            }
            else if ( (LA11_0==ID) ) {
                int LA11_2 = input.LA(2);

                if ( (LA11_2==LITVALUE) ) {
                    alt11=2;
                }
                else if ( (LA11_2==EOF||(LA11_2 >= 11 && LA11_2 <= 12)||LA11_2==14||(LA11_2 >= 19 && LA11_2 <= 24)) ) {
                    alt11=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 11, 2, input);

                    throw nvae;

                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;

            }
            switch (alt11) {
                case 1 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:77:4: keyword[$q]
                    {
                    pushFollow(FOLLOW_keyword_in_e3q3302);
                    keyword11=keyword(q);

                    state._fsp--;


                    qe = keyword11; addRoleToQE(qe, QeRole.E3Q3);

                    }
                    break;
                case 2 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:78:4: literal[$q]
                    {
                    pushFollow(FOLLOW_literal_in_e3q3310);
                    literal12=literal(q);

                    state._fsp--;


                    qe = literal12; addRoleToQE(qe, QeRole.E3Q3);

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
        return qe;
    }
    // $ANTLR end "e3q3"



    // $ANTLR start "keyword"
    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:84:1: keyword[Query q] returns [Keyword k] : ( ( keywordAgg ) | ( keywordUnit ) ) ;
    public final Keyword keyword(Query q) throws RecognitionException {
        Keyword k = null;


        Keyword keywordAgg13 =null;

        Keyword keywordUnit14 =null;


        try {
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:85:2: ( ( ( keywordAgg ) | ( keywordUnit ) ) )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:86:3: ( ( keywordAgg ) | ( keywordUnit ) )
            {
            Keyword keyw = null; 

            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:87:3: ( ( keywordAgg ) | ( keywordUnit ) )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( ((LA12_0 >= 20 && LA12_0 <= 24)) ) {
                alt12=1;
            }
            else if ( (LA12_0==ID||LA12_0==18) ) {
                alt12=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;

            }
            switch (alt12) {
                case 1 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:87:5: ( keywordAgg )
                    {
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:87:5: ( keywordAgg )
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:87:6: keywordAgg
                    {
                    pushFollow(FOLLOW_keywordAgg_in_keyword343);
                    keywordAgg13=keywordAgg();

                    state._fsp--;


                    keyw = keywordAgg13;

                    }


                    }
                    break;
                case 2 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:87:43: ( keywordUnit )
                    {
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:87:43: ( keywordUnit )
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:87:45: keywordUnit
                    {
                    pushFollow(FOLLOW_keywordUnit_in_keyword351);
                    keywordUnit14=keywordUnit();

                    state._fsp--;


                    keyw = keywordUnit14;

                    }


                    }
                    break;

            }


             if(keyw != null) q.addQueryElement(keyw); 
            			  k = keyw;
            			

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return k;
    }
    // $ANTLR end "keyword"



    // $ANTLR start "keywordAgg"
    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:94:1: keywordAgg returns [Keyword k] : ( keywordAggCond | keywordAggMinMax );
    public final Keyword keywordAgg() throws RecognitionException {
        Keyword k = null;


        Keyword keywordAggCond15 =null;

        Keyword keywordAggMinMax16 =null;


        try {
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:95:2: ( keywordAggCond | keywordAggMinMax )
            int alt13=2;
            switch ( input.LA(1) ) {
            case 20:
            case 21:
            case 24:
                {
                alt13=1;
                }
                break;
            case 22:
                {
                int LA13_2 = input.LA(2);

                if ( (LA13_2==9) ) {
                    int LA13_4 = input.LA(3);

                    if ( (LA13_4==18) ) {
                        int LA13_5 = input.LA(4);

                        if ( (LA13_5==ID) ) {
                            int LA13_6 = input.LA(5);

                            if ( (LA13_6==10) ) {
                                switch ( input.LA(6) ) {
                                case 16:
                                    {
                                    int LA13_8 = input.LA(7);

                                    if ( (LA13_8==INT) ) {
                                        alt13=1;
                                    }
                                    else if ( (LA13_8==ID||LA13_8==18||(LA13_8 >= 20 && LA13_8 <= 24)) ) {
                                        alt13=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 13, 8, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 15:
                                case 17:
                                    {
                                    alt13=1;
                                    }
                                    break;
                                case EOF:
                                case 11:
                                case 12:
                                case 13:
                                case 14:
                                case 19:
                                case 20:
                                case 21:
                                case 22:
                                case 23:
                                case 24:
                                    {
                                    alt13=2;
                                    }
                                    break;
                                default:
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 13, 7, input);

                                    throw nvae;

                                }

                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 13, 6, input);

                                throw nvae;

                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 13, 5, input);

                            throw nvae;

                        }
                    }
                    else if ( (LA13_4==ID) ) {
                        int LA13_6 = input.LA(4);

                        if ( (LA13_6==10) ) {
                            switch ( input.LA(5) ) {
                            case 16:
                                {
                                int LA13_8 = input.LA(6);

                                if ( (LA13_8==INT) ) {
                                    alt13=1;
                                }
                                else if ( (LA13_8==ID||LA13_8==18||(LA13_8 >= 20 && LA13_8 <= 24)) ) {
                                    alt13=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 13, 8, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 15:
                            case 17:
                                {
                                alt13=1;
                                }
                                break;
                            case EOF:
                            case 11:
                            case 12:
                            case 13:
                            case 14:
                            case 19:
                            case 20:
                            case 21:
                            case 22:
                            case 23:
                            case 24:
                                {
                                alt13=2;
                                }
                                break;
                            default:
                                NoViableAltException nvae =
                                    new NoViableAltException("", 13, 7, input);

                                throw nvae;

                            }

                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 13, 6, input);

                            throw nvae;

                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 13, 4, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 2, input);

                    throw nvae;

                }
                }
                break;
            case 23:
                {
                int LA13_3 = input.LA(2);

                if ( (LA13_3==9) ) {
                    int LA13_4 = input.LA(3);

                    if ( (LA13_4==18) ) {
                        int LA13_5 = input.LA(4);

                        if ( (LA13_5==ID) ) {
                            int LA13_6 = input.LA(5);

                            if ( (LA13_6==10) ) {
                                switch ( input.LA(6) ) {
                                case 16:
                                    {
                                    int LA13_8 = input.LA(7);

                                    if ( (LA13_8==INT) ) {
                                        alt13=1;
                                    }
                                    else if ( (LA13_8==ID||LA13_8==18||(LA13_8 >= 20 && LA13_8 <= 24)) ) {
                                        alt13=2;
                                    }
                                    else {
                                        NoViableAltException nvae =
                                            new NoViableAltException("", 13, 8, input);

                                        throw nvae;

                                    }
                                    }
                                    break;
                                case 15:
                                case 17:
                                    {
                                    alt13=1;
                                    }
                                    break;
                                case EOF:
                                case 11:
                                case 12:
                                case 13:
                                case 14:
                                case 19:
                                case 20:
                                case 21:
                                case 22:
                                case 23:
                                case 24:
                                    {
                                    alt13=2;
                                    }
                                    break;
                                default:
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 13, 7, input);

                                    throw nvae;

                                }

                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 13, 6, input);

                                throw nvae;

                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 13, 5, input);

                            throw nvae;

                        }
                    }
                    else if ( (LA13_4==ID) ) {
                        int LA13_6 = input.LA(4);

                        if ( (LA13_6==10) ) {
                            switch ( input.LA(5) ) {
                            case 16:
                                {
                                int LA13_8 = input.LA(6);

                                if ( (LA13_8==INT) ) {
                                    alt13=1;
                                }
                                else if ( (LA13_8==ID||LA13_8==18||(LA13_8 >= 20 && LA13_8 <= 24)) ) {
                                    alt13=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 13, 8, input);

                                    throw nvae;

                                }
                                }
                                break;
                            case 15:
                            case 17:
                                {
                                alt13=1;
                                }
                                break;
                            case EOF:
                            case 11:
                            case 12:
                            case 13:
                            case 14:
                            case 19:
                            case 20:
                            case 21:
                            case 22:
                            case 23:
                            case 24:
                                {
                                alt13=2;
                                }
                                break;
                            default:
                                NoViableAltException nvae =
                                    new NoViableAltException("", 13, 7, input);

                                throw nvae;

                            }

                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 13, 6, input);

                            throw nvae;

                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 13, 4, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 3, input);

                    throw nvae;

                }
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;

            }

            switch (alt13) {
                case 1 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:96:3: keywordAggCond
                    {
                    pushFollow(FOLLOW_keywordAggCond_in_keywordAgg379);
                    keywordAggCond15=keywordAggCond();

                    state._fsp--;


                     k = keywordAggCond15;

                    }
                    break;
                case 2 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:97:3: keywordAggMinMax
                    {
                    pushFollow(FOLLOW_keywordAggMinMax_in_keywordAgg387);
                    keywordAggMinMax16=keywordAggMinMax();

                    state._fsp--;


                    k = keywordAggMinMax16;

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
        return k;
    }
    // $ANTLR end "keywordAgg"



    // $ANTLR start "keywordAggCond"
    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:99:1: keywordAggCond returns [Keyword k] : ( ( 'COUNT' | 'MAX' | 'MIN' | 'AVG' | 'SUM' ) '(' keywordUnit ')' ( '=' | '>' | '<' ) INT ) ;
    public final Keyword keywordAggCond() throws RecognitionException {
        Keyword k = null;


        Token INT18=null;
        Keyword keywordUnit17 =null;


        try {
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:100:2: ( ( ( 'COUNT' | 'MAX' | 'MIN' | 'AVG' | 'SUM' ) '(' keywordUnit ')' ( '=' | '>' | '<' ) INT ) )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:100:4: ( ( 'COUNT' | 'MAX' | 'MIN' | 'AVG' | 'SUM' ) '(' keywordUnit ')' ( '=' | '>' | '<' ) INT )
            {

            			String agg = "";
            			String cond = "";
            			Keyword keyw = null;
            		

            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:105:3: ( ( 'COUNT' | 'MAX' | 'MIN' | 'AVG' | 'SUM' ) '(' keywordUnit ')' ( '=' | '>' | '<' ) INT )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:105:4: ( 'COUNT' | 'MAX' | 'MIN' | 'AVG' | 'SUM' ) '(' keywordUnit ')' ( '=' | '>' | '<' ) INT
            {
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:105:4: ( 'COUNT' | 'MAX' | 'MIN' | 'AVG' | 'SUM' )
            int alt14=5;
            switch ( input.LA(1) ) {
            case 21:
                {
                alt14=1;
                }
                break;
            case 22:
                {
                alt14=2;
                }
                break;
            case 23:
                {
                alt14=3;
                }
                break;
            case 20:
                {
                alt14=4;
                }
                break;
            case 24:
                {
                alt14=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;

            }

            switch (alt14) {
                case 1 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:105:5: 'COUNT'
                    {
                    match(input,21,FOLLOW_21_in_keywordAggCond409); 

                    agg = "COUNT";

                    }
                    break;
                case 2 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:105:31: 'MAX'
                    {
                    match(input,22,FOLLOW_22_in_keywordAggCond414); 

                    agg = "MAX";

                    }
                    break;
                case 3 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:105:53: 'MIN'
                    {
                    match(input,23,FOLLOW_23_in_keywordAggCond419); 

                    agg = "MIN";

                    }
                    break;
                case 4 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:105:75: 'AVG'
                    {
                    match(input,20,FOLLOW_20_in_keywordAggCond424); 

                    agg = "AVG";

                    }
                    break;
                case 5 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:105:97: 'SUM'
                    {
                    match(input,24,FOLLOW_24_in_keywordAggCond429); 

                    agg="SUM";

                    }
                    break;

            }


            match(input,9,FOLLOW_9_in_keywordAggCond433); 

            pushFollow(FOLLOW_keywordUnit_in_keywordAggCond438);
            keywordUnit17=keywordUnit();

            state._fsp--;


            keyw = keywordUnit17;

            match(input,10,FOLLOW_10_in_keywordAggCond444); 

            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:108:3: ( '=' | '>' | '<' )
            int alt15=3;
            switch ( input.LA(1) ) {
            case 16:
                {
                alt15=1;
                }
                break;
            case 17:
                {
                alt15=2;
                }
                break;
            case 15:
                {
                alt15=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;

            }

            switch (alt15) {
                case 1 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:108:4: '='
                    {
                    match(input,16,FOLLOW_16_in_keywordAggCond450); 

                    cond = "=";

                    }
                    break;
                case 2 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:108:24: '>'
                    {
                    match(input,17,FOLLOW_17_in_keywordAggCond456); 

                    cond = ">";

                    }
                    break;
                case 3 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:108:44: '<'
                    {
                    match(input,15,FOLLOW_15_in_keywordAggCond462); 

                    cond = "<";

                    }
                    break;

            }


            INT18=(Token)match(input,INT,FOLLOW_INT_in_keywordAggCond468); 

            cond += (INT18!=null?INT18.getText():null);

            }


             if(keyw != null) keyw.setCond(agg, cond); k =keyw;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return k;
    }
    // $ANTLR end "keywordAggCond"



    // $ANTLR start "keywordAggMinMax"
    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:112:1: keywordAggMinMax returns [Keyword k] : ( ( 'MAX' | 'MIN' ) '(' keywordUnit ')' ) ;
    public final Keyword keywordAggMinMax() throws RecognitionException {
        Keyword k = null;


        Keyword keywordUnit19 =null;


        try {
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:113:2: ( ( ( 'MAX' | 'MIN' ) '(' keywordUnit ')' ) )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:113:4: ( ( 'MAX' | 'MIN' ) '(' keywordUnit ')' )
            {

            		String agg = "";
            		String cond = "";
            		Keyword keyw = null;
            	   

            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:118:4: ( ( 'MAX' | 'MIN' ) '(' keywordUnit ')' )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:118:5: ( 'MAX' | 'MIN' ) '(' keywordUnit ')'
            {
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:118:5: ( 'MAX' | 'MIN' )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==22) ) {
                alt16=1;
            }
            else if ( (LA16_0==23) ) {
                alt16=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;

            }
            switch (alt16) {
                case 1 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:118:6: 'MAX'
                    {
                    match(input,22,FOLLOW_22_in_keywordAggMinMax498); 

                    agg = "MAX";

                    }
                    break;
                case 2 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:118:28: 'MIN'
                    {
                    match(input,23,FOLLOW_23_in_keywordAggMinMax503); 

                    agg = "MIN";

                    }
                    break;

            }


            match(input,9,FOLLOW_9_in_keywordAggMinMax507); 

            pushFollow(FOLLOW_keywordUnit_in_keywordAggMinMax509);
            keywordUnit19=keywordUnit();

            state._fsp--;


            match(input,10,FOLLOW_10_in_keywordAggMinMax511); 

            keyw = keywordUnit19;

            }


             if(keyw != null) keyw.setCond(agg, cond); k =keyw;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return k;
    }
    // $ANTLR end "keywordAggMinMax"



    // $ANTLR start "keywordUnit"
    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:121:1: keywordUnit returns [Keyword k] : ( '?' )? ID ;
    public final Keyword keywordUnit() throws RecognitionException {
        Keyword k = null;


        Token ID20=null;

        try {
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:122:2: ( ( '?' )? ID )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:122:5: ( '?' )? ID
            {

            			String s = "";
            			boolean queried = false;
            			Integer keywordId = new Integer(0);
            		

            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:127:3: ( '?' )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==18) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:127:4: '?'
                    {
                    match(input,18,FOLLOW_18_in_keywordUnit540); 

                    queried = true;

                    }
                    break;

            }


            ID20=(Token)match(input,ID,FOLLOW_ID_in_keywordUnit548); 


            			String keywordValue = (ID20!=null?ID20.getText():null);
            			String key = (queried==true? "t": "f") + keywordId.toString() + keywordValue;
            			Keyword keyword = keywords.get(key);
            			if (keyword == null) {
            				keyword  = new Keyword(queried, keywordId, keywordValue);
            				keywords.put(key, keyword);
            			}
            			k = keyword;
            		

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return k;
    }
    // $ANTLR end "keywordUnit"



    // $ANTLR start "literal"
    // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:158:1: literal[Query q] returns [Literal l] : ID LITVALUE ;
    public final Literal literal(Query q) throws RecognitionException {
        Literal l = null;


        Token ID21=null;
        Token LITVALUE22=null;

        try {
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:159:2: ( ID LITVALUE )
            // /Users/Murloc/Documents/IRIT/grammaires/userQueryGrammar.g:159:4: ID LITVALUE
            {
            ID21=(Token)match(input,ID,FOLLOW_ID_in_literal590); 

            LITVALUE22=(Token)match(input,LITVALUE,FOLLOW_LITVALUE_in_literal592); 


            		try {
            			l = new Literal((ID21!=null?ID21.getText():null), (LITVALUE22!=null?LITVALUE22.getText():null).substring(1, (LITVALUE22!=null?LITVALUE22.getText():null).length()-1));
            			q.addQueryElement(l);
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
        	// do for sure before leaving
        }
        return l;
    }
    // $ANTLR end "literal"

    // Delegated rules


 

    public static final BitSet FOLLOW_subquerySet_in_query38 = new BitSet(new long[]{0x0000000001F81002L});
    public static final BitSet FOLLOW_12_in_query42 = new BitSet(new long[]{0x0000000001F40020L});
    public static final BitSet FOLLOW_subquerySet_in_query44 = new BitSet(new long[]{0x0000000001F81002L});
    public static final BitSet FOLLOW_12_in_query50 = new BitSet(new long[]{0x0000000001F80002L});
    public static final BitSet FOLLOW_21_in_query56 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_22_in_query61 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_23_in_query67 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_20_in_query72 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_24_in_query77 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_19_in_query82 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_12_in_query88 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_e1q23_in_subquerySet108 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_subquerySet111 = new BitSet(new long[]{0x0000000001F40020L});
    public static final BitSet FOLLOW_q23End_in_subquerySet113 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_14_in_subquerySet117 = new BitSet(new long[]{0x0000000001F40020L});
    public static final BitSet FOLLOW_q23End_in_subquerySet119 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_e1q1_in_subquerySet127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_keyword_in_e1q1149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_keyword_in_e1q23170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_e2q3_in_q23End189 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_q23End192 = new BitSet(new long[]{0x0000000001F40020L});
    public static final BitSet FOLLOW_q3End_in_q23End194 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_11_in_q23End198 = new BitSet(new long[]{0x0000000001F40020L});
    public static final BitSet FOLLOW_q3End_in_q23End200 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_e2q2_in_q23End208 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_keyword_in_e2q2230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_e2q2238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_keyword_in_e2q3261 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_e3q3_in_q3End279 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_keyword_in_e3q3302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_e3q3310 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_keywordAgg_in_keyword343 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_keywordUnit_in_keyword351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_keywordAggCond_in_keywordAgg379 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_keywordAggMinMax_in_keywordAgg387 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_keywordAggCond409 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_22_in_keywordAggCond414 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_23_in_keywordAggCond419 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_20_in_keywordAggCond424 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_24_in_keywordAggCond429 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_9_in_keywordAggCond433 = new BitSet(new long[]{0x0000000000040020L});
    public static final BitSet FOLLOW_keywordUnit_in_keywordAggCond438 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_10_in_keywordAggCond444 = new BitSet(new long[]{0x0000000000038000L});
    public static final BitSet FOLLOW_16_in_keywordAggCond450 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_17_in_keywordAggCond456 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_15_in_keywordAggCond462 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INT_in_keywordAggCond468 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_keywordAggMinMax498 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_23_in_keywordAggMinMax503 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_9_in_keywordAggMinMax507 = new BitSet(new long[]{0x0000000000040020L});
    public static final BitSet FOLLOW_keywordUnit_in_keywordAggMinMax509 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_10_in_keywordAggMinMax511 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_keywordUnit540 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_keywordUnit548 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_literal590 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_LITVALUE_in_literal592 = new BitSet(new long[]{0x0000000000000002L});

}