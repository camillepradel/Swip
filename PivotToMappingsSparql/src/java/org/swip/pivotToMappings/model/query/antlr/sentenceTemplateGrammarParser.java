// $ANTLR 3.3 Nov 30, 2010 12:45:30 /home/camille/ANTLRWorks/sentenceTemplateGrammar.g 2013-01-26 13:55:16

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class sentenceTemplateGrammarParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ENDSENT", "SENT", "ENDIF", "UNDERSCORE", "LEFTSB", "RIGHTSB", "Space", "INT", "IFF", "ID", "NormalChar", "STRING", "SpecialChar", "'\"'"
    };
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


        public sentenceTemplateGrammarParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public sentenceTemplateGrammarParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return sentenceTemplateGrammarParser.tokenNames; }
    public String getGrammarFileName() { return "/home/camille/ANTLRWorks/sentenceTemplateGrammar.g"; }



    // $ANTLR start "sentenseTemplate"
    // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:12:1: sentenseTemplate : SENT ( Space )+ ( subsentenseTemplate ( Space )+ )+ ENDSENT ;
    public final void sentenseTemplate() throws RecognitionException {
        try {
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:13:2: ( SENT ( Space )+ ( subsentenseTemplate ( Space )+ )+ ENDSENT )
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:13:4: SENT ( Space )+ ( subsentenseTemplate ( Space )+ )+ ENDSENT
            {
            match(input,SENT,FOLLOW_SENT_in_sentenseTemplate57); 
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:13:9: ( Space )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==Space) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:13:9: Space
            	    {
            	    match(input,Space,FOLLOW_Space_in_sentenseTemplate59); 

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

            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:14:3: ( subsentenseTemplate ( Space )+ )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==UNDERSCORE||LA3_0==IFF||LA3_0==17) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:14:4: subsentenseTemplate ( Space )+
            	    {
            	    pushFollow(FOLLOW_subsentenseTemplate_in_sentenseTemplate65);
            	    subsentenseTemplate();

            	    state._fsp--;

            	    // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:14:24: ( Space )+
            	    int cnt2=0;
            	    loop2:
            	    do {
            	        int alt2=2;
            	        int LA2_0 = input.LA(1);

            	        if ( (LA2_0==Space) ) {
            	            alt2=1;
            	        }


            	        switch (alt2) {
            	    	case 1 :
            	    	    // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:14:24: Space
            	    	    {
            	    	    match(input,Space,FOLLOW_Space_in_sentenseTemplate67); 

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
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);

            match(input,ENDSENT,FOLLOW_ENDSENT_in_sentenseTemplate74); 

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
    // $ANTLR end "sentenseTemplate"


    // $ANTLR start "subsentenseTemplate"
    // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:18:1: subsentenseTemplate : ( qeInTemplate | spInTemplate | staticStringInTemplate );
    public final void subsentenseTemplate() throws RecognitionException {
        try {
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:19:2: ( qeInTemplate | spInTemplate | staticStringInTemplate )
            int alt4=3;
            switch ( input.LA(1) ) {
            case UNDERSCORE:
                {
                alt4=1;
                }
                break;
            case IFF:
                {
                alt4=2;
                }
                break;
            case 17:
                {
                alt4=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:19:4: qeInTemplate
                    {
                    pushFollow(FOLLOW_qeInTemplate_in_subsentenseTemplate92);
                    qeInTemplate();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:20:4: spInTemplate
                    {
                    pushFollow(FOLLOW_spInTemplate_in_subsentenseTemplate97);
                    spInTemplate();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:21:4: staticStringInTemplate
                    {
                    pushFollow(FOLLOW_staticStringInTemplate_in_subsentenseTemplate102);
                    staticStringInTemplate();

                    state._fsp--;


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
    // $ANTLR end "subsentenseTemplate"


    // $ANTLR start "qeInTemplate"
    // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:25:1: qeInTemplate : UNDERSCORE UNDERSCORE INT UNDERSCORE UNDERSCORE ;
    public final void qeInTemplate() throws RecognitionException {
        try {
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:26:2: ( UNDERSCORE UNDERSCORE INT UNDERSCORE UNDERSCORE )
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:26:5: UNDERSCORE UNDERSCORE INT UNDERSCORE UNDERSCORE
            {
            match(input,UNDERSCORE,FOLLOW_UNDERSCORE_in_qeInTemplate126); 
            match(input,UNDERSCORE,FOLLOW_UNDERSCORE_in_qeInTemplate128); 
            match(input,INT,FOLLOW_INT_in_qeInTemplate130); 
            match(input,UNDERSCORE,FOLLOW_UNDERSCORE_in_qeInTemplate132); 
            match(input,UNDERSCORE,FOLLOW_UNDERSCORE_in_qeInTemplate134); 

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
    // $ANTLR end "qeInTemplate"


    // $ANTLR start "spInTemplate"
    // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:29:1: spInTemplate : IFF ID UNDERSCORE LEFTSB staticStringInTemplate RIGHTSB ENDIF ID UNDERSCORE ;
    public final void spInTemplate() throws RecognitionException {
        try {
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:30:2: ( IFF ID UNDERSCORE LEFTSB staticStringInTemplate RIGHTSB ENDIF ID UNDERSCORE )
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:30:5: IFF ID UNDERSCORE LEFTSB staticStringInTemplate RIGHTSB ENDIF ID UNDERSCORE
            {
            match(input,IFF,FOLLOW_IFF_in_spInTemplate161); 
            match(input,ID,FOLLOW_ID_in_spInTemplate163); 
            match(input,UNDERSCORE,FOLLOW_UNDERSCORE_in_spInTemplate165); 
            match(input,LEFTSB,FOLLOW_LEFTSB_in_spInTemplate167); 
            pushFollow(FOLLOW_staticStringInTemplate_in_spInTemplate171);
            staticStringInTemplate();

            state._fsp--;

            match(input,RIGHTSB,FOLLOW_RIGHTSB_in_spInTemplate176); 
            match(input,ENDIF,FOLLOW_ENDIF_in_spInTemplate178); 
            match(input,ID,FOLLOW_ID_in_spInTemplate180); 
            match(input,UNDERSCORE,FOLLOW_UNDERSCORE_in_spInTemplate182); 

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
    // $ANTLR end "spInTemplate"


    // $ANTLR start "staticStringInTemplate"
    // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:35:1: staticStringInTemplate : '\"' ( NormalChar | Space )* '\"' ;
    public final void staticStringInTemplate() throws RecognitionException {
        try {
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:36:2: ( '\"' ( NormalChar | Space )* '\"' )
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:36:5: '\"' ( NormalChar | Space )* '\"'
            {
            match(input,17,FOLLOW_17_in_staticStringInTemplate197); 
            // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:36:9: ( NormalChar | Space )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==Space||LA5_0==NormalChar) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // /home/camille/ANTLRWorks/sentenceTemplateGrammar.g:
            	    {
            	    if ( input.LA(1)==Space||input.LA(1)==NormalChar ) {
            	        input.consume();
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            match(input,17,FOLLOW_17_in_staticStringInTemplate208); 

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
    // $ANTLR end "staticStringInTemplate"

    // Delegated rules


 

    public static final BitSet FOLLOW_SENT_in_sentenseTemplate57 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_Space_in_sentenseTemplate59 = new BitSet(new long[]{0x0000000000021480L});
    public static final BitSet FOLLOW_subsentenseTemplate_in_sentenseTemplate65 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_Space_in_sentenseTemplate67 = new BitSet(new long[]{0x0000000000021490L});
    public static final BitSet FOLLOW_ENDSENT_in_sentenseTemplate74 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qeInTemplate_in_subsentenseTemplate92 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_spInTemplate_in_subsentenseTemplate97 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_staticStringInTemplate_in_subsentenseTemplate102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNDERSCORE_in_qeInTemplate126 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_UNDERSCORE_in_qeInTemplate128 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_INT_in_qeInTemplate130 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_UNDERSCORE_in_qeInTemplate132 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_UNDERSCORE_in_qeInTemplate134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IFF_in_spInTemplate161 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_ID_in_spInTemplate163 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_UNDERSCORE_in_spInTemplate165 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_LEFTSB_in_spInTemplate167 = new BitSet(new long[]{0x0000000000021080L});
    public static final BitSet FOLLOW_staticStringInTemplate_in_spInTemplate171 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHTSB_in_spInTemplate176 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ENDIF_in_spInTemplate178 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_ID_in_spInTemplate180 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_UNDERSCORE_in_spInTemplate182 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_staticStringInTemplate197 = new BitSet(new long[]{0x0000000000024400L});
    public static final BitSet FOLLOW_set_in_staticStringInTemplate199 = new BitSet(new long[]{0x0000000000024400L});
    public static final BitSet FOLLOW_17_in_staticStringInTemplate208 = new BitSet(new long[]{0x0000000000000002L});

}