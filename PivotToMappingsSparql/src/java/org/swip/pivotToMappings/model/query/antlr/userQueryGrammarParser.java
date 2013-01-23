// $ANTLR 3.3 Nov 30, 2010 12:45:30 /home/camille/ANTLRWorks/userQueryGrammar.g 2013-01-02 18:06:49

package org.swip.pivotToMappings.model.query.antlr;
import java.util.HashMap;
import org.swip.pivotToMappings.model.query.Query;
import org.swip.pivotToMappings.model.query.queryElement.*;
import org.swip.pivotToMappings.model.query.subquery.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class userQueryGrammarParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "INT", "ID", "LITVALUE", "COMMENT", "WS", "KEYVALUE", "'.'", "'COUNT'", "'ASK'", "':'", "';'", "'='", "','", "'?'", "'$'"
    };
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


        public userQueryGrammarParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public userQueryGrammarParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return userQueryGrammarParser.tokenNames; }
    public String getGrammarFileName() { return "/home/camille/ANTLRWorks/userQueryGrammar.g"; }


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
    // /home/camille/ANTLRWorks/userQueryGrammar.g:37:1: query returns [Query q] : subquerySet[$q] ( '.' subquerySet[$q] )* ( '.' )? ( 'COUNT' )? ( 'ASK' )? ;
    public final Query query() throws RecognitionException {
        Query q = null;

        try {
            // /home/camille/ANTLRWorks/userQueryGrammar.g:38:2: ( subquerySet[$q] ( '.' subquerySet[$q] )* ( '.' )? ( 'COUNT' )? ( 'ASK' )? )
            // /home/camille/ANTLRWorks/userQueryGrammar.g:38:4: subquerySet[$q] ( '.' subquerySet[$q] )* ( '.' )? ( 'COUNT' )? ( 'ASK' )?
            {
            q = new Query();
            pushFollow(FOLLOW_subquerySet_in_query38);
            subquerySet(q);

            state._fsp--;

            // /home/camille/ANTLRWorks/userQueryGrammar.g:38:40: ( '.' subquerySet[$q] )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==10) ) {
                    int LA1_1 = input.LA(2);

                    if ( (LA1_1==ID||(LA1_1>=17 && LA1_1<=18)) ) {
                        alt1=1;
                    }


                }


                switch (alt1) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/userQueryGrammar.g:38:41: '.' subquerySet[$q]
            	    {
            	    match(input,10,FOLLOW_10_in_query42); 
            	    pushFollow(FOLLOW_subquerySet_in_query44);
            	    subquerySet(q);

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            // /home/camille/ANTLRWorks/userQueryGrammar.g:38:63: ( '.' )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==10) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // /home/camille/ANTLRWorks/userQueryGrammar.g:38:64: '.'
                    {
                    match(input,10,FOLLOW_10_in_query50); 

                    }
                    break;

            }

            // /home/camille/ANTLRWorks/userQueryGrammar.g:38:70: ( 'COUNT' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==11) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /home/camille/ANTLRWorks/userQueryGrammar.g:38:71: 'COUNT'
                    {
                    match(input,11,FOLLOW_11_in_query55); 
                    q.setCount(true);

                    }
                    break;

            }

            // /home/camille/ANTLRWorks/userQueryGrammar.g:38:102: ( 'ASK' )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==12) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // /home/camille/ANTLRWorks/userQueryGrammar.g:38:103: 'ASK'
                    {
                    match(input,12,FOLLOW_12_in_query62); 
                    q.setAsk(true);

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
        }
        return q;
    }
    // $ANTLR end "query"


    // $ANTLR start "subquerySet"
    // /home/camille/ANTLRWorks/userQueryGrammar.g:41:1: subquerySet[Query q] : ( e1q23[$q] ':' q23End[$q, $e1q23.qe] ( ';' q23End[$q, $e1q23.qe] )* | e1q1[$q] );
    public final void subquerySet(Query q) throws RecognitionException {
        QueryElement e1q231 = null;

        QueryElement e1q12 = null;


        try {
            // /home/camille/ANTLRWorks/userQueryGrammar.g:42:2: ( e1q23[$q] ':' q23End[$q, $e1q23.qe] ( ';' q23End[$q, $e1q23.qe] )* | e1q1[$q] )
            int alt6=2;
            switch ( input.LA(1) ) {
            case 17:
                {
                int LA6_1 = input.LA(2);

                if ( (LA6_1==18) ) {
                    int LA6_2 = input.LA(3);

                    if ( (LA6_2==INT) ) {
                        int LA6_4 = input.LA(4);

                        if ( (LA6_4==ID) ) {
                            int LA6_3 = input.LA(5);

                            if ( (LA6_3==13) ) {
                                alt6=1;
                            }
                            else if ( (LA6_3==EOF||(LA6_3>=10 && LA6_3<=12)) ) {
                                alt6=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 6, 3, input);

                                throw nvae;
                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 6, 4, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 6, 2, input);

                        throw nvae;
                    }
                }
                else if ( (LA6_1==ID) ) {
                    int LA6_3 = input.LA(3);

                    if ( (LA6_3==13) ) {
                        alt6=1;
                    }
                    else if ( (LA6_3==EOF||(LA6_3>=10 && LA6_3<=12)) ) {
                        alt6=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 6, 3, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 1, input);

                    throw nvae;
                }
                }
                break;
            case 18:
                {
                int LA6_2 = input.LA(2);

                if ( (LA6_2==INT) ) {
                    int LA6_4 = input.LA(3);

                    if ( (LA6_4==ID) ) {
                        int LA6_3 = input.LA(4);

                        if ( (LA6_3==13) ) {
                            alt6=1;
                        }
                        else if ( (LA6_3==EOF||(LA6_3>=10 && LA6_3<=12)) ) {
                            alt6=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 6, 3, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 6, 4, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 2, input);

                    throw nvae;
                }
                }
                break;
            case ID:
                {
                int LA6_3 = input.LA(2);

                if ( (LA6_3==13) ) {
                    alt6=1;
                }
                else if ( (LA6_3==EOF||(LA6_3>=10 && LA6_3<=12)) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 3, input);

                    throw nvae;
                }
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // /home/camille/ANTLRWorks/userQueryGrammar.g:42:4: e1q23[$q] ':' q23End[$q, $e1q23.qe] ( ';' q23End[$q, $e1q23.qe] )*
                    {
                    pushFollow(FOLLOW_e1q23_in_subquerySet80);
                    e1q231=e1q23(q);

                    state._fsp--;

                    match(input,13,FOLLOW_13_in_subquerySet83); 
                    pushFollow(FOLLOW_q23End_in_subquerySet85);
                    q23End(q, e1q231);

                    state._fsp--;

                    // /home/camille/ANTLRWorks/userQueryGrammar.g:42:40: ( ';' q23End[$q, $e1q23.qe] )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==14) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // /home/camille/ANTLRWorks/userQueryGrammar.g:42:41: ';' q23End[$q, $e1q23.qe]
                    	    {
                    	    match(input,14,FOLLOW_14_in_subquerySet89); 
                    	    pushFollow(FOLLOW_q23End_in_subquerySet91);
                    	    q23End(q, e1q231);

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop5;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // /home/camille/ANTLRWorks/userQueryGrammar.g:43:4: e1q1[$q]
                    {
                    pushFollow(FOLLOW_e1q1_in_subquerySet99);
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
        }
        return ;
    }
    // $ANTLR end "subquerySet"


    // $ANTLR start "e1q1"
    // /home/camille/ANTLRWorks/userQueryGrammar.g:46:1: e1q1[Query q] returns [QueryElement qe] : keyword[$q] ;
    public final QueryElement e1q1(Query q) throws RecognitionException {
        QueryElement qe = null;

        Keyword keyword3 = null;


        try {
            // /home/camille/ANTLRWorks/userQueryGrammar.g:47:2: ( keyword[$q] )
            // /home/camille/ANTLRWorks/userQueryGrammar.g:47:4: keyword[$q]
            {
            pushFollow(FOLLOW_keyword_in_e1q1121);
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
        }
        return qe;
    }
    // $ANTLR end "e1q1"


    // $ANTLR start "e1q23"
    // /home/camille/ANTLRWorks/userQueryGrammar.g:50:1: e1q23[Query q] returns [QueryElement qe] : keyword[$q] ;
    public final QueryElement e1q23(Query q) throws RecognitionException {
        QueryElement qe = null;

        Keyword keyword4 = null;


        try {
            // /home/camille/ANTLRWorks/userQueryGrammar.g:51:2: ( keyword[$q] )
            // /home/camille/ANTLRWorks/userQueryGrammar.g:51:4: keyword[$q]
            {
            pushFollow(FOLLOW_keyword_in_e1q23142);
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
        }
        return qe;
    }
    // $ANTLR end "e1q23"


    // $ANTLR start "q23End"
    // /home/camille/ANTLRWorks/userQueryGrammar.g:55:1: q23End[Query q, QueryElement e1] : ( e2q3[$q] '=' q3End[$q, $e1, $e2q3.qe] ( ',' q3End[$q, $e1, $e2q3.qe] )* | e2q2[$q] );
    public final void q23End(Query q, QueryElement e1) throws RecognitionException {
        QueryElement e2q35 = null;

        QueryElement e2q26 = null;


        try {
            // /home/camille/ANTLRWorks/userQueryGrammar.g:56:2: ( e2q3[$q] '=' q3End[$q, $e1, $e2q3.qe] ( ',' q3End[$q, $e1, $e2q3.qe] )* | e2q2[$q] )
            int alt8=2;
            switch ( input.LA(1) ) {
            case 17:
                {
                int LA8_1 = input.LA(2);

                if ( (LA8_1==18) ) {
                    int LA8_2 = input.LA(3);

                    if ( (LA8_2==INT) ) {
                        int LA8_5 = input.LA(4);

                        if ( (LA8_5==ID) ) {
                            int LA8_4 = input.LA(5);

                            if ( (LA8_4==15) ) {
                                alt8=1;
                            }
                            else if ( (LA8_4==EOF||(LA8_4>=10 && LA8_4<=12)||LA8_4==14) ) {
                                alt8=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 8, 4, input);

                                throw nvae;
                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 8, 5, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 8, 2, input);

                        throw nvae;
                    }
                }
                else if ( (LA8_1==ID) ) {
                    int LA8_4 = input.LA(3);

                    if ( (LA8_4==15) ) {
                        alt8=1;
                    }
                    else if ( (LA8_4==EOF||(LA8_4>=10 && LA8_4<=12)||LA8_4==14) ) {
                        alt8=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 8, 4, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 8, 1, input);

                    throw nvae;
                }
                }
                break;
            case 18:
                {
                int LA8_2 = input.LA(2);

                if ( (LA8_2==INT) ) {
                    int LA8_5 = input.LA(3);

                    if ( (LA8_5==ID) ) {
                        int LA8_4 = input.LA(4);

                        if ( (LA8_4==15) ) {
                            alt8=1;
                        }
                        else if ( (LA8_4==EOF||(LA8_4>=10 && LA8_4<=12)||LA8_4==14) ) {
                            alt8=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 8, 4, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 8, 5, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 8, 2, input);

                    throw nvae;
                }
                }
                break;
            case ID:
                {
                int LA8_3 = input.LA(2);

                if ( (LA8_3==EOF||LA8_3==LITVALUE||(LA8_3>=10 && LA8_3<=12)||LA8_3==14) ) {
                    alt8=2;
                }
                else if ( (LA8_3==15) ) {
                    alt8=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 8, 3, input);

                    throw nvae;
                }
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // /home/camille/ANTLRWorks/userQueryGrammar.g:56:4: e2q3[$q] '=' q3End[$q, $e1, $e2q3.qe] ( ',' q3End[$q, $e1, $e2q3.qe] )*
                    {
                    pushFollow(FOLLOW_e2q3_in_q23End161);
                    e2q35=e2q3(q);

                    state._fsp--;

                    match(input,15,FOLLOW_15_in_q23End164); 
                    pushFollow(FOLLOW_q3End_in_q23End166);
                    q3End(q, e1, e2q35);

                    state._fsp--;

                    // /home/camille/ANTLRWorks/userQueryGrammar.g:56:42: ( ',' q3End[$q, $e1, $e2q3.qe] )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==16) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // /home/camille/ANTLRWorks/userQueryGrammar.g:56:43: ',' q3End[$q, $e1, $e2q3.qe]
                    	    {
                    	    match(input,16,FOLLOW_16_in_q23End170); 
                    	    pushFollow(FOLLOW_q3End_in_q23End172);
                    	    q3End(q, e1, e2q35);

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // /home/camille/ANTLRWorks/userQueryGrammar.g:57:4: e2q2[$q]
                    {
                    pushFollow(FOLLOW_e2q2_in_q23End180);
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
        }
        return ;
    }
    // $ANTLR end "q23End"


    // $ANTLR start "e2q2"
    // /home/camille/ANTLRWorks/userQueryGrammar.g:60:1: e2q2[Query q] returns [QueryElement qe] : ( keyword[$q] | literal[$q] );
    public final QueryElement e2q2(Query q) throws RecognitionException {
        QueryElement qe = null;

        Keyword keyword7 = null;

        Literal literal8 = null;


        try {
            // /home/camille/ANTLRWorks/userQueryGrammar.g:61:2: ( keyword[$q] | literal[$q] )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( ((LA9_0>=17 && LA9_0<=18)) ) {
                alt9=1;
            }
            else if ( (LA9_0==ID) ) {
                int LA9_2 = input.LA(2);

                if ( (LA9_2==LITVALUE) ) {
                    alt9=2;
                }
                else if ( (LA9_2==EOF||(LA9_2>=10 && LA9_2<=12)||LA9_2==14) ) {
                    alt9=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // /home/camille/ANTLRWorks/userQueryGrammar.g:61:4: keyword[$q]
                    {
                    pushFollow(FOLLOW_keyword_in_e2q2202);
                    keyword7=keyword(q);

                    state._fsp--;

                    qe = keyword7; addRoleToQE(qe, QeRole.E2Q2);

                    }
                    break;
                case 2 :
                    // /home/camille/ANTLRWorks/userQueryGrammar.g:62:4: literal[$q]
                    {
                    pushFollow(FOLLOW_literal_in_e2q2210);
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
        }
        return qe;
    }
    // $ANTLR end "e2q2"


    // $ANTLR start "e2q3"
    // /home/camille/ANTLRWorks/userQueryGrammar.g:66:1: e2q3[Query q] returns [QueryElement qe] : keyword[$q] ;
    public final QueryElement e2q3(Query q) throws RecognitionException {
        QueryElement qe = null;

        Keyword keyword9 = null;


        try {
            // /home/camille/ANTLRWorks/userQueryGrammar.g:67:2: ( keyword[$q] )
            // /home/camille/ANTLRWorks/userQueryGrammar.g:67:4: keyword[$q]
            {
            pushFollow(FOLLOW_keyword_in_e2q3233);
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
        }
        return qe;
    }
    // $ANTLR end "e2q3"


    // $ANTLR start "q3End"
    // /home/camille/ANTLRWorks/userQueryGrammar.g:71:1: q3End[Query q, QueryElement e1, QueryElement e2] : e3q3[$q] ;
    public final void q3End(Query q, QueryElement e1, QueryElement e2) throws RecognitionException {
        QueryElement e3q310 = null;


        try {
            // /home/camille/ANTLRWorks/userQueryGrammar.g:72:2: ( e3q3[$q] )
            // /home/camille/ANTLRWorks/userQueryGrammar.g:72:4: e3q3[$q]
            {
            pushFollow(FOLLOW_e3q3_in_q3End251);
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
        }
        return ;
    }
    // $ANTLR end "q3End"


    // $ANTLR start "e3q3"
    // /home/camille/ANTLRWorks/userQueryGrammar.g:75:1: e3q3[Query q] returns [QueryElement qe] : ( keyword[$q] | literal[$q] );
    public final QueryElement e3q3(Query q) throws RecognitionException {
        QueryElement qe = null;

        Keyword keyword11 = null;

        Literal literal12 = null;


        try {
            // /home/camille/ANTLRWorks/userQueryGrammar.g:76:2: ( keyword[$q] | literal[$q] )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( ((LA10_0>=17 && LA10_0<=18)) ) {
                alt10=1;
            }
            else if ( (LA10_0==ID) ) {
                int LA10_2 = input.LA(2);

                if ( (LA10_2==LITVALUE) ) {
                    alt10=2;
                }
                else if ( (LA10_2==EOF||(LA10_2>=10 && LA10_2<=12)||LA10_2==14||LA10_2==16) ) {
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
                    // /home/camille/ANTLRWorks/userQueryGrammar.g:76:4: keyword[$q]
                    {
                    pushFollow(FOLLOW_keyword_in_e3q3274);
                    keyword11=keyword(q);

                    state._fsp--;

                    qe = keyword11; addRoleToQE(qe, QeRole.E3Q3);

                    }
                    break;
                case 2 :
                    // /home/camille/ANTLRWorks/userQueryGrammar.g:77:4: literal[$q]
                    {
                    pushFollow(FOLLOW_literal_in_e3q3282);
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
        }
        return qe;
    }
    // $ANTLR end "e3q3"


    // $ANTLR start "keyword"
    // /home/camille/ANTLRWorks/userQueryGrammar.g:81:1: keyword[Query q] returns [Keyword k] : ( '?' )? ( '$' INT )? ID ;
    public final Keyword keyword(Query q) throws RecognitionException {
        Keyword k = null;

        Token INT13=null;
        Token ID14=null;

        try {
            // /home/camille/ANTLRWorks/userQueryGrammar.g:82:2: ( ( '?' )? ( '$' INT )? ID )
            // /home/camille/ANTLRWorks/userQueryGrammar.g:82:4: ( '?' )? ( '$' INT )? ID
            {

            		String s = "";
            		boolean queried = false;
            		Integer keywordId = new Integer(0);
            		
            // /home/camille/ANTLRWorks/userQueryGrammar.g:87:3: ( '?' )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==17) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // /home/camille/ANTLRWorks/userQueryGrammar.g:87:4: '?'
                    {
                    match(input,17,FOLLOW_17_in_keyword309); 
                    queried = true;

                    }
                    break;

            }

            // /home/camille/ANTLRWorks/userQueryGrammar.g:88:3: ( '$' INT )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==18) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // /home/camille/ANTLRWorks/userQueryGrammar.g:88:4: '$' INT
                    {
                    match(input,18,FOLLOW_18_in_keyword318); 
                    INT13=(Token)match(input,INT,FOLLOW_INT_in_keyword319); 

                    			keywordId = Integer.parseInt((INT13!=null?INT13.getText():null));
                    			if (keywordId <= 0) {
                    				throw new KeywordRuntimeException("id must be strictly greater than 0");
                    			}
                    		

                    }
                    break;

            }

            ID14=(Token)match(input,ID,FOLLOW_ID_in_keyword327); 

            			String keywordValue = (ID14!=null?ID14.getText():null);
            			String key = (queried==true? "t": "f") + keywordId.toString() + keywordValue;
            			Keyword keyword = keywords.get(key);
            			if (keyword == null) {
            				keyword  = new Keyword(queried, keywordId, keywordValue);
            				keywords.put(key, keyword);
            				q.addQueryElement(keyword);
            			}
            			k = keyword;
            		

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return k;
    }
    // $ANTLR end "keyword"


    // $ANTLR start "literal"
    // /home/camille/ANTLRWorks/userQueryGrammar.g:125:1: literal[Query q] returns [Literal l] : ID LITVALUE ;
    public final Literal literal(Query q) throws RecognitionException {
        Literal l = null;

        Token ID15=null;
        Token LITVALUE16=null;

        try {
            // /home/camille/ANTLRWorks/userQueryGrammar.g:126:2: ( ID LITVALUE )
            // /home/camille/ANTLRWorks/userQueryGrammar.g:126:4: ID LITVALUE
            {
            ID15=(Token)match(input,ID,FOLLOW_ID_in_literal367); 
            LITVALUE16=(Token)match(input,LITVALUE,FOLLOW_LITVALUE_in_literal369); 

            		try {
            			l = new Literal((ID15!=null?ID15.getText():null), (LITVALUE16!=null?LITVALUE16.getText():null).substring(1, (LITVALUE16!=null?LITVALUE16.getText():null).length()-1));
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
        }
        return l;
    }
    // $ANTLR end "literal"

    // Delegated rules


 

    public static final BitSet FOLLOW_subquerySet_in_query38 = new BitSet(new long[]{0x0000000000001C02L});
    public static final BitSet FOLLOW_10_in_query42 = new BitSet(new long[]{0x0000000000060020L});
    public static final BitSet FOLLOW_subquerySet_in_query44 = new BitSet(new long[]{0x0000000000001C02L});
    public static final BitSet FOLLOW_10_in_query50 = new BitSet(new long[]{0x0000000000001802L});
    public static final BitSet FOLLOW_11_in_query55 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_12_in_query62 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_e1q23_in_subquerySet80 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_subquerySet83 = new BitSet(new long[]{0x0000000000060020L});
    public static final BitSet FOLLOW_q23End_in_subquerySet85 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_14_in_subquerySet89 = new BitSet(new long[]{0x0000000000060020L});
    public static final BitSet FOLLOW_q23End_in_subquerySet91 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_e1q1_in_subquerySet99 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_keyword_in_e1q1121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_keyword_in_e1q23142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_e2q3_in_q23End161 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_q23End164 = new BitSet(new long[]{0x0000000000060020L});
    public static final BitSet FOLLOW_q3End_in_q23End166 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_16_in_q23End170 = new BitSet(new long[]{0x0000000000060020L});
    public static final BitSet FOLLOW_q3End_in_q23End172 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_e2q2_in_q23End180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_keyword_in_e2q2202 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_e2q2210 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_keyword_in_e2q3233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_e3q3_in_q3End251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_keyword_in_e3q3274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_e3q3282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_keyword309 = new BitSet(new long[]{0x0000000000040020L});
    public static final BitSet FOLLOW_18_in_keyword318 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_INT_in_keyword319 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_keyword327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_literal367 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_LITVALUE_in_literal369 = new BitSet(new long[]{0x0000000000000002L});

}