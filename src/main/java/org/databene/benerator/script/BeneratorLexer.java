// $ANTLR 3.2 Sep 23, 2009 12:02:23 /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g 2010-02-23 10:48:07
 
	package org.databene.benerator.script;


import org.antlr.runtime.*;

import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class BeneratorLexer extends Lexer {
    public static final int CAST=15;
    public static final int DECIMALLITERAL=6;
    public static final int T__66=66;
    public static final int LT=61;
    public static final int STAR=48;
    public static final int QUALIFIEDNAME=19;
    public static final int AMP=50;
    public static final int LBRACE=31;
    public static final int BOOLEANLITERAL=8;
    public static final int Exponent=24;
    public static final int SUB=47;
    public static final int INVOCATION=17;
    public static final int EOF=-1;
    public static final int HexDigit=23;
    public static final int LPAREN=29;
    public static final int TYPE=10;
    public static final int LBRACKET=33;
    public static final int INDEX=12;
    public static final int RPAREN=30;
    public static final int SLASH=49;
    public static final int COMMA=36;
    public static final int IDENTIFIER=4;
    public static final int CARET=52;
    public static final int TILDE=40;
    public static final int BANGEQ=55;
    public static final int MONKEYS_AT=54;
    public static final int PLUS=46;
    public static final int BARBAR=45;
    public static final int RBRACKET=34;
    public static final int EQ=38;
    public static final int SHIFT_RIGHT2=58;
    public static final int AMPAMP=44;
    public static final int SUBINVOCATION=18;
    public static final int COMMENT=27;
    public static final int DOT=37;
    public static final int IdentifierPart=65;
    public static final int QUES=41;
    public static final int EQEQ=43;
    public static final int HexPrefix=22;
    public static final int GE=60;
    public static final int RBRACE=32;
    public static final int ARGUMENTS=14;
    public static final int PERCENT=53;
    public static final int LINE_COMMENT=28;
    public static final int NULL=9;
    public static final int IdentifierStart=64;
    public static final int BANG=39;
    public static final int INTLITERAL=5;
    public static final int SEMI=35;
    public static final int COLON=42;
    public static final int SHIFT_LEFT=59;
    public static final int BEANSPEC=21;
    public static final int WS=26;
    public static final int SHIFT_RIGHT=57;
    public static final int NEGATION=11;
    public static final int STRINGLITERAL=7;
    public static final int BEAN=20;
    public static final int GT=56;
    public static final int ARROW=63;
    public static final int FIELD=13;
    public static final int EscapeSequence=25;
    public static final int CONSTRUCTOR=16;
    public static final int BAR=51;
    public static final int LE=62;

    	@Override
    	public Token nextToken() {
    		while (true) {
    			state.token = null;
    			state.channel = Token.DEFAULT_CHANNEL;
    			state.tokenStartCharIndex = input.index();
    			state.tokenStartCharPositionInLine = input.getCharPositionInLine();
    			state.tokenStartLine = input.getLine();
    			state.text = null;
    			if ( input.LA(1)==CharStream.EOF ) {
    				return Token.EOF_TOKEN;
    			}
    			try {
    				mTokens();
    				if ( state.token==null ) {
    					emit();
    				}
    				else if ( state.token==Token.SKIP_TOKEN ) {
    					continue;
    				}
    				return state.token;
    			}
    			catch (RecognitionException re) {
    				reportError(re);
    				throw new RuntimeException(getClass().getSimpleName() + " error", re); // or throw Error
    			}
    		}
    	}



    // delegates
    // delegators

    public BeneratorLexer() {;} 
    public BeneratorLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public BeneratorLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "/Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g"; }

    // $ANTLR start "T__66"
    public final void mT__66() throws RecognitionException {
        try {
            int _type = T__66;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:38:7: ( 'new' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:38:9: 'new'
            {
            match("new"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__66"

    // $ANTLR start "TYPE"
    public final void mTYPE() throws RecognitionException {
        try {
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:230:14: ()
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:230:15: 
            {
            }

        }
        finally {
        }
    }
    // $ANTLR end "TYPE"

    // $ANTLR start "NEGATION"
    public final void mNEGATION() throws RecognitionException {
        try {
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:231:18: ()
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:231:19: 
            {
            }

        }
        finally {
        }
    }
    // $ANTLR end "NEGATION"

    // $ANTLR start "INDEX"
    public final void mINDEX() throws RecognitionException {
        try {
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:232:15: ()
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:232:16: 
            {
            }

        }
        finally {
        }
    }
    // $ANTLR end "INDEX"

    // $ANTLR start "FIELD"
    public final void mFIELD() throws RecognitionException {
        try {
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:233:15: ()
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:233:16: 
            {
            }

        }
        finally {
        }
    }
    // $ANTLR end "FIELD"

    // $ANTLR start "ARGUMENTS"
    public final void mARGUMENTS() throws RecognitionException {
        try {
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:234:19: ()
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:234:20: 
            {
            }

        }
        finally {
        }
    }
    // $ANTLR end "ARGUMENTS"

    // $ANTLR start "CAST"
    public final void mCAST() throws RecognitionException {
        try {
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:235:14: ()
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:235:15: 
            {
            }

        }
        finally {
        }
    }
    // $ANTLR end "CAST"

    // $ANTLR start "CONSTRUCTOR"
    public final void mCONSTRUCTOR() throws RecognitionException {
        try {
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:236:21: ()
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:236:22: 
            {
            }

        }
        finally {
        }
    }
    // $ANTLR end "CONSTRUCTOR"

    // $ANTLR start "INVOCATION"
    public final void mINVOCATION() throws RecognitionException {
        try {
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:237:20: ()
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:237:21: 
            {
            }

        }
        finally {
        }
    }
    // $ANTLR end "INVOCATION"

    // $ANTLR start "SUBINVOCATION"
    public final void mSUBINVOCATION() throws RecognitionException {
        try {
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:238:23: ()
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:238:24: 
            {
            }

        }
        finally {
        }
    }
    // $ANTLR end "SUBINVOCATION"

    // $ANTLR start "QUALIFIEDNAME"
    public final void mQUALIFIEDNAME() throws RecognitionException {
        try {
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:239:23: ()
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:239:24: 
            {
            }

        }
        finally {
        }
    }
    // $ANTLR end "QUALIFIEDNAME"

    // $ANTLR start "BEAN"
    public final void mBEAN() throws RecognitionException {
        try {
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:240:14: ()
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:240:15: 
            {
            }

        }
        finally {
        }
    }
    // $ANTLR end "BEAN"

    // $ANTLR start "BEANSPEC"
    public final void mBEANSPEC() throws RecognitionException {
        try {
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:241:18: ()
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:241:19: 
            {
            }

        }
        finally {
        }
    }
    // $ANTLR end "BEANSPEC"

    // $ANTLR start "BOOLEANLITERAL"
    public final void mBOOLEANLITERAL() throws RecognitionException {
        try {
            int _type = BOOLEANLITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:244:5: ( 'true' | 'false' )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0=='t') ) {
                alt1=1;
            }
            else if ( (LA1_0=='f') ) {
                alt1=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:244:9: 'true'
                    {
                    match("true"); 


                    }
                    break;
                case 2 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:245:9: 'false'
                    {
                    match("false"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BOOLEANLITERAL"

    // $ANTLR start "INTLITERAL"
    public final void mINTLITERAL() throws RecognitionException {
        try {
            int _type = INTLITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:249:5: ( '0' | '1' .. '9' ( '0' .. '9' )* | '0' ( '0' .. '7' )+ | HexPrefix ( HexDigit )+ )
            int alt5=4;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='0') ) {
                switch ( input.LA(2) ) {
                case 'x':
                    {
                    alt5=4;
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
                    {
                    alt5=3;
                    }
                    break;
                default:
                    alt5=1;}

            }
            else if ( ((LA5_0>='1' && LA5_0<='9')) ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:249:9: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:250:9: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:250:18: ( '0' .. '9' )*
                    loop2:
                    do {
                        int alt2=2;
                        int LA2_0 = input.LA(1);

                        if ( ((LA2_0>='0' && LA2_0<='9')) ) {
                            alt2=1;
                        }


                        switch (alt2) {
                    	case 1 :
                    	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:250:19: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop2;
                        }
                    } while (true);


                    }
                    break;
                case 3 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:251:9: '0' ( '0' .. '7' )+
                    {
                    match('0'); 
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:251:13: ( '0' .. '7' )+
                    int cnt3=0;
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( ((LA3_0>='0' && LA3_0<='7')) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:251:14: '0' .. '7'
                    	    {
                    	    matchRange('0','7'); 

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


                    }
                    break;
                case 4 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:252:9: HexPrefix ( HexDigit )+
                    {
                    mHexPrefix(); 
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:252:19: ( HexDigit )+
                    int cnt4=0;
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0>='0' && LA4_0<='9')||(LA4_0>='A' && LA4_0<='F')||(LA4_0>='a' && LA4_0<='f')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:252:19: HexDigit
                    	    {
                    	    mHexDigit(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt4 >= 1 ) break loop4;
                                EarlyExitException eee =
                                    new EarlyExitException(4, input);
                                throw eee;
                        }
                        cnt4++;
                    } while (true);


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INTLITERAL"

    // $ANTLR start "HexPrefix"
    public final void mHexPrefix() throws RecognitionException {
        try {
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:257:5: ( '0x' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:257:9: '0x'
            {
            match("0x"); 


            }

        }
        finally {
        }
    }
    // $ANTLR end "HexPrefix"

    // $ANTLR start "HexDigit"
    public final void mHexDigit() throws RecognitionException {
        try {
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:261:5: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:261:9: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "HexDigit"

    // $ANTLR start "DECIMALLITERAL"
    public final void mDECIMALLITERAL() throws RecognitionException {
        try {
            int _type = DECIMALLITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:265:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? | ( '0' .. '9' )+ Exponent | HexPrefix ( HexDigit )* ( () | ( '.' ( HexDigit )* ) ) ( 'p' | 'P' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            int alt15=3;
            alt15 = dfa15.predict(input);
            switch (alt15) {
                case 1 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:265:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )?
                    {
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:265:9: ( '0' .. '9' )+
                    int cnt6=0;
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( ((LA6_0>='0' && LA6_0<='9')) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:265:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt6 >= 1 ) break loop6;
                                EarlyExitException eee =
                                    new EarlyExitException(6, input);
                                throw eee;
                        }
                        cnt6++;
                    } while (true);

                    match('.'); 
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:265:27: ( '0' .. '9' )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( ((LA7_0>='0' && LA7_0<='9')) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:265:28: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);

                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:265:41: ( Exponent )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0=='E'||LA8_0=='e') ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:265:41: Exponent
                            {
                            mExponent(); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:266:9: ( '0' .. '9' )+ Exponent
                    {
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:266:9: ( '0' .. '9' )+
                    int cnt9=0;
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0>='0' && LA9_0<='9')) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:266:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

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

                    mExponent(); 

                    }
                    break;
                case 3 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:267:9: HexPrefix ( HexDigit )* ( () | ( '.' ( HexDigit )* ) ) ( 'p' | 'P' ) ( '+' | '-' )? ( '0' .. '9' )+
                    {
                    mHexPrefix(); 
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:267:19: ( HexDigit )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( ((LA10_0>='0' && LA10_0<='9')||(LA10_0>='A' && LA10_0<='F')||(LA10_0>='a' && LA10_0<='f')) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:267:20: HexDigit
                    	    {
                    	    mHexDigit(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop10;
                        }
                    } while (true);

                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:267:32: ( () | ( '.' ( HexDigit )* ) )
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0=='P'||LA12_0=='p') ) {
                        alt12=1;
                    }
                    else if ( (LA12_0=='.') ) {
                        alt12=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 12, 0, input);

                        throw nvae;
                    }
                    switch (alt12) {
                        case 1 :
                            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:267:34: ()
                            {
                            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:267:34: ()
                            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:267:35: 
                            {
                            }


                            }
                            break;
                        case 2 :
                            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:267:39: ( '.' ( HexDigit )* )
                            {
                            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:267:39: ( '.' ( HexDigit )* )
                            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:267:40: '.' ( HexDigit )*
                            {
                            match('.'); 
                            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:267:44: ( HexDigit )*
                            loop11:
                            do {
                                int alt11=2;
                                int LA11_0 = input.LA(1);

                                if ( ((LA11_0>='0' && LA11_0<='9')||(LA11_0>='A' && LA11_0<='F')||(LA11_0>='a' && LA11_0<='f')) ) {
                                    alt11=1;
                                }


                                switch (alt11) {
                            	case 1 :
                            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:267:45: HexDigit
                            	    {
                            	    mHexDigit(); 

                            	    }
                            	    break;

                            	default :
                            	    break loop11;
                                }
                            } while (true);


                            }


                            }
                            break;

                    }

                    if ( input.LA(1)=='P'||input.LA(1)=='p' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}

                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:267:75: ( '+' | '-' )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0=='+'||LA13_0=='-') ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:
                            {
                            if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                                input.consume();

                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                recover(mse);
                                throw mse;}


                            }
                            break;

                    }

                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:267:90: ( '0' .. '9' )+
                    int cnt14=0;
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( ((LA14_0>='0' && LA14_0<='9')) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:267:92: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt14 >= 1 ) break loop14;
                                EarlyExitException eee =
                                    new EarlyExitException(14, input);
                                throw eee;
                        }
                        cnt14++;
                    } while (true);


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DECIMALLITERAL"

    // $ANTLR start "Exponent"
    public final void mExponent() throws RecognitionException {
        try {
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:272:5: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:272:9: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:272:23: ( '+' | '-' )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0=='+'||LA16_0=='-') ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:272:38: ( '0' .. '9' )+
            int cnt17=0;
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( ((LA17_0>='0' && LA17_0<='9')) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:272:40: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt17 >= 1 ) break loop17;
                        EarlyExitException eee =
                            new EarlyExitException(17, input);
                        throw eee;
                }
                cnt17++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "Exponent"

    // $ANTLR start "STRINGLITERAL"
    public final void mSTRINGLITERAL() throws RecognitionException {
        try {
            int _type = STRINGLITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:276:5: ( '\\'' ( EscapeSequence | ~ ( '\\'' | '\\\\' | '\\r' | '\\n' ) )* '\\'' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:276:9: '\\'' ( EscapeSequence | ~ ( '\\'' | '\\\\' | '\\r' | '\\n' ) )* '\\''
            {
            match('\''); 
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:276:14: ( EscapeSequence | ~ ( '\\'' | '\\\\' | '\\r' | '\\n' ) )*
            loop18:
            do {
                int alt18=3;
                int LA18_0 = input.LA(1);

                if ( (LA18_0=='\\') ) {
                    alt18=1;
                }
                else if ( ((LA18_0>='\u0000' && LA18_0<='\t')||(LA18_0>='\u000B' && LA18_0<='\f')||(LA18_0>='\u000E' && LA18_0<='&')||(LA18_0>='(' && LA18_0<='[')||(LA18_0>=']' && LA18_0<='\uFFFF')) ) {
                    alt18=2;
                }


                switch (alt18) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:276:16: EscapeSequence
            	    {
            	    mEscapeSequence(); 

            	    }
            	    break;
            	case 2 :
            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:276:33: ~ ( '\\'' | '\\\\' | '\\r' | '\\n' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);

            match('\''); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRINGLITERAL"

    // $ANTLR start "EscapeSequence"
    public final void mEscapeSequence() throws RecognitionException {
        try {
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:281:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | ( '0' .. '7' ) ( '0' .. '7' ) | ( '0' .. '7' ) ) )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:281:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | ( '0' .. '7' ) ( '0' .. '7' ) | ( '0' .. '7' ) )
            {
            match('\\'); 
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:281:14: ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | ( '0' .. '7' ) ( '0' .. '7' ) | ( '0' .. '7' ) )
            int alt19=11;
            alt19 = dfa19.predict(input);
            switch (alt19) {
                case 1 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:282:18: 'b'
                    {
                    match('b'); 

                    }
                    break;
                case 2 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:283:18: 't'
                    {
                    match('t'); 

                    }
                    break;
                case 3 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:284:18: 'n'
                    {
                    match('n'); 

                    }
                    break;
                case 4 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:285:18: 'f'
                    {
                    match('f'); 

                    }
                    break;
                case 5 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:286:18: 'r'
                    {
                    match('r'); 

                    }
                    break;
                case 6 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:287:18: '\\\"'
                    {
                    match('\"'); 

                    }
                    break;
                case 7 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:288:18: '\\''
                    {
                    match('\''); 

                    }
                    break;
                case 8 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:289:18: '\\\\'
                    {
                    match('\\'); 

                    }
                    break;
                case 9 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:290:18: ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:290:18: ( '0' .. '3' )
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:290:19: '0' .. '3'
                    {
                    matchRange('0','3'); 

                    }

                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:290:29: ( '0' .. '7' )
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:290:30: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:290:40: ( '0' .. '7' )
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:290:41: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 10 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:291:18: ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:291:18: ( '0' .. '7' )
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:291:19: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:291:29: ( '0' .. '7' )
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:291:30: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 11 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:292:18: ( '0' .. '7' )
                    {
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:292:18: ( '0' .. '7' )
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:292:19: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end "EscapeSequence"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:296:5: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' ) )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:296:9: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||(input.LA(1)>='\f' && input.LA(1)<='\r')||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


                            skip();
                        

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:302:8: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:302:12: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:302:17: ( options {greedy=false; } : . )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0=='*') ) {
                    int LA20_1 = input.LA(2);

                    if ( (LA20_1=='/') ) {
                        alt20=2;
                    }
                    else if ( ((LA20_1>='\u0000' && LA20_1<='.')||(LA20_1>='0' && LA20_1<='\uFFFF')) ) {
                        alt20=1;
                    }


                }
                else if ( ((LA20_0>='\u0000' && LA20_0<=')')||(LA20_0>='+' && LA20_0<='\uFFFF')) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:302:44: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);

            match("*/"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "LINE_COMMENT"
    public final void mLINE_COMMENT() throws RecognitionException {
        try {
            int _type = LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:305:5: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r\\n' | '\\r' | '\\n' ) | '//' (~ ( '\\n' | '\\r' ) )* )
            int alt24=2;
            alt24 = dfa24.predict(input);
            switch (alt24) {
                case 1 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:305:9: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r\\n' | '\\r' | '\\n' )
                    {
                    match("//"); 

                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:305:14: (~ ( '\\n' | '\\r' ) )*
                    loop21:
                    do {
                        int alt21=2;
                        int LA21_0 = input.LA(1);

                        if ( ((LA21_0>='\u0000' && LA21_0<='\t')||(LA21_0>='\u000B' && LA21_0<='\f')||(LA21_0>='\u000E' && LA21_0<='\uFFFF')) ) {
                            alt21=1;
                        }


                        switch (alt21) {
                    	case 1 :
                    	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:305:14: ~ ( '\\n' | '\\r' )
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
                    	    break loop21;
                        }
                    } while (true);

                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:305:29: ( '\\r\\n' | '\\r' | '\\n' )
                    int alt22=3;
                    int LA22_0 = input.LA(1);

                    if ( (LA22_0=='\r') ) {
                        int LA22_1 = input.LA(2);

                        if ( (LA22_1=='\n') ) {
                            alt22=1;
                        }
                        else {
                            alt22=2;}
                    }
                    else if ( (LA22_0=='\n') ) {
                        alt22=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 22, 0, input);

                        throw nvae;
                    }
                    switch (alt22) {
                        case 1 :
                            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:305:30: '\\r\\n'
                            {
                            match("\r\n"); 


                            }
                            break;
                        case 2 :
                            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:305:39: '\\r'
                            {
                            match('\r'); 

                            }
                            break;
                        case 3 :
                            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:305:46: '\\n'
                            {
                            match('\n'); 

                            }
                            break;

                    }


                                    skip();
                                

                    }
                    break;
                case 2 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:309:9: '//' (~ ( '\\n' | '\\r' ) )*
                    {
                    match("//"); 

                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:309:14: (~ ( '\\n' | '\\r' ) )*
                    loop23:
                    do {
                        int alt23=2;
                        int LA23_0 = input.LA(1);

                        if ( ((LA23_0>='\u0000' && LA23_0<='\t')||(LA23_0>='\u000B' && LA23_0<='\f')||(LA23_0>='\u000E' && LA23_0<='\uFFFF')) ) {
                            alt23=1;
                        }


                        switch (alt23) {
                    	case 1 :
                    	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:309:14: ~ ( '\\n' | '\\r' )
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
                    	    break loop23;
                        }
                    } while (true);


                                    skip();
                                

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LINE_COMMENT"

    // $ANTLR start "NULL"
    public final void mNULL() throws RecognitionException {
        try {
            int _type = NULL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:315:5: ( 'null' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:315:11: 'null'
            {
            match("null"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NULL"

    // $ANTLR start "LPAREN"
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:316:7: ( '(' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:316:11: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LPAREN"

    // $ANTLR start "RPAREN"
    public final void mRPAREN() throws RecognitionException {
        try {
            int _type = RPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:317:7: ( ')' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:317:11: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RPAREN"

    // $ANTLR start "LBRACE"
    public final void mLBRACE() throws RecognitionException {
        try {
            int _type = LBRACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:318:7: ( '{' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:318:11: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LBRACE"

    // $ANTLR start "RBRACE"
    public final void mRBRACE() throws RecognitionException {
        try {
            int _type = RBRACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:319:7: ( '}' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:319:11: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RBRACE"

    // $ANTLR start "LBRACKET"
    public final void mLBRACKET() throws RecognitionException {
        try {
            int _type = LBRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:320:9: ( '[' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:320:11: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LBRACKET"

    // $ANTLR start "RBRACKET"
    public final void mRBRACKET() throws RecognitionException {
        try {
            int _type = RBRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:321:9: ( ']' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:321:11: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RBRACKET"

    // $ANTLR start "SEMI"
    public final void mSEMI() throws RecognitionException {
        try {
            int _type = SEMI;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:322:5: ( ';' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:322:9: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SEMI"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:323:6: ( ',' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:323:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "DOT"
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:324:5: ( '.' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:324:9: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOT"

    // $ANTLR start "EQ"
    public final void mEQ() throws RecognitionException {
        try {
            int _type = EQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:325:5: ( '=' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:325:9: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EQ"

    // $ANTLR start "BANG"
    public final void mBANG() throws RecognitionException {
        try {
            int _type = BANG;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:326:5: ( '!' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:326:9: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BANG"

    // $ANTLR start "TILDE"
    public final void mTILDE() throws RecognitionException {
        try {
            int _type = TILDE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:327:6: ( '~' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:327:9: '~'
            {
            match('~'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TILDE"

    // $ANTLR start "QUES"
    public final void mQUES() throws RecognitionException {
        try {
            int _type = QUES;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:328:5: ( '?' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:328:9: '?'
            {
            match('?'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "QUES"

    // $ANTLR start "COLON"
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:329:6: ( ':' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:329:9: ':'
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

    // $ANTLR start "EQEQ"
    public final void mEQEQ() throws RecognitionException {
        try {
            int _type = EQEQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:330:5: ( '==' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:330:9: '=='
            {
            match("=="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EQEQ"

    // $ANTLR start "AMPAMP"
    public final void mAMPAMP() throws RecognitionException {
        try {
            int _type = AMPAMP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:331:7: ( '&&' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:331:9: '&&'
            {
            match("&&"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AMPAMP"

    // $ANTLR start "BARBAR"
    public final void mBARBAR() throws RecognitionException {
        try {
            int _type = BARBAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:332:7: ( '||' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:332:9: '||'
            {
            match("||"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BARBAR"

    // $ANTLR start "PLUS"
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:333:5: ( '+' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:333:9: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PLUS"

    // $ANTLR start "SUB"
    public final void mSUB() throws RecognitionException {
        try {
            int _type = SUB;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:334:5: ( '-' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:334:9: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SUB"

    // $ANTLR start "STAR"
    public final void mSTAR() throws RecognitionException {
        try {
            int _type = STAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:335:5: ( '*' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:335:9: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STAR"

    // $ANTLR start "SLASH"
    public final void mSLASH() throws RecognitionException {
        try {
            int _type = SLASH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:336:6: ( '/' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:336:10: '/'
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

    // $ANTLR start "AMP"
    public final void mAMP() throws RecognitionException {
        try {
            int _type = AMP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:337:5: ( '&' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:337:9: '&'
            {
            match('&'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AMP"

    // $ANTLR start "BAR"
    public final void mBAR() throws RecognitionException {
        try {
            int _type = BAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:338:5: ( '|' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:338:9: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BAR"

    // $ANTLR start "CARET"
    public final void mCARET() throws RecognitionException {
        try {
            int _type = CARET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:339:6: ( '^' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:339:10: '^'
            {
            match('^'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CARET"

    // $ANTLR start "PERCENT"
    public final void mPERCENT() throws RecognitionException {
        try {
            int _type = PERCENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:340:8: ( '%' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:340:10: '%'
            {
            match('%'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PERCENT"

    // $ANTLR start "MONKEYS_AT"
    public final void mMONKEYS_AT() throws RecognitionException {
        try {
            int _type = MONKEYS_AT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:341:11: ( '@' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:341:13: '@'
            {
            match('@'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MONKEYS_AT"

    // $ANTLR start "BANGEQ"
    public final void mBANGEQ() throws RecognitionException {
        try {
            int _type = BANGEQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:342:7: ( '!=' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:342:9: '!='
            {
            match("!="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BANGEQ"

    // $ANTLR start "GT"
    public final void mGT() throws RecognitionException {
        try {
            int _type = GT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:343:5: ( '>' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:343:9: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GT"

    // $ANTLR start "SHIFT_RIGHT"
    public final void mSHIFT_RIGHT() throws RecognitionException {
        try {
            int _type = SHIFT_RIGHT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:344:12: ( '>>' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:344:16: '>>'
            {
            match(">>"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SHIFT_RIGHT"

    // $ANTLR start "SHIFT_RIGHT2"
    public final void mSHIFT_RIGHT2() throws RecognitionException {
        try {
            int _type = SHIFT_RIGHT2;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:345:13: ( '>>>' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:345:17: '>>>'
            {
            match(">>>"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SHIFT_RIGHT2"

    // $ANTLR start "SHIFT_LEFT"
    public final void mSHIFT_LEFT() throws RecognitionException {
        try {
            int _type = SHIFT_LEFT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:346:11: ( '<<' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:346:15: '<<'
            {
            match("<<"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SHIFT_LEFT"

    // $ANTLR start "GE"
    public final void mGE() throws RecognitionException {
        try {
            int _type = GE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:347:5: ( '>=' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:347:9: '>='
            {
            match(">="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GE"

    // $ANTLR start "LT"
    public final void mLT() throws RecognitionException {
        try {
            int _type = LT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:348:5: ( '<' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:348:9: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LT"

    // $ANTLR start "LE"
    public final void mLE() throws RecognitionException {
        try {
            int _type = LE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:349:5: ( '<=' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:349:9: '<='
            {
            match("<="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LE"

    // $ANTLR start "ARROW"
    public final void mARROW() throws RecognitionException {
        try {
            int _type = ARROW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:350:6: ( '->' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:350:10: '->'
            {
            match("->"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ARROW"

    // $ANTLR start "IDENTIFIER"
    public final void mIDENTIFIER() throws RecognitionException {
        try {
            int _type = IDENTIFIER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:353:5: ( IdentifierStart ( IdentifierPart )* )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:353:9: IdentifierStart ( IdentifierPart )*
            {
            mIdentifierStart(); 
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:353:25: ( IdentifierPart )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( ((LA25_0>='0' && LA25_0<='9')||(LA25_0>='A' && LA25_0<='Z')||LA25_0=='_'||(LA25_0>='a' && LA25_0<='z')) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:353:25: IdentifierPart
            	    {
            	    mIdentifierPart(); 

            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IDENTIFIER"

    // $ANTLR start "IdentifierStart"
    public final void mIdentifierStart() throws RecognitionException {
        try {
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:358:5: ( 'A' .. 'Z' | 'a' .. 'z' | '_' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "IdentifierStart"

    // $ANTLR start "IdentifierPart"
    public final void mIdentifierPart() throws RecognitionException {
        try {
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:365:5: ( 'A' .. 'Z' | 'a' .. 'z' | '_' | '0' .. '9' )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "IdentifierPart"

    public void mTokens() throws RecognitionException {
        // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:8: ( T__66 | BOOLEANLITERAL | INTLITERAL | DECIMALLITERAL | STRINGLITERAL | WS | COMMENT | LINE_COMMENT | NULL | LPAREN | RPAREN | LBRACE | RBRACE | LBRACKET | RBRACKET | SEMI | COMMA | DOT | EQ | BANG | TILDE | QUES | COLON | EQEQ | AMPAMP | BARBAR | PLUS | SUB | STAR | SLASH | AMP | BAR | CARET | PERCENT | MONKEYS_AT | BANGEQ | GT | SHIFT_RIGHT | SHIFT_RIGHT2 | SHIFT_LEFT | GE | LT | LE | ARROW | IDENTIFIER )
        int alt26=45;
        alt26 = dfa26.predict(input);
        switch (alt26) {
            case 1 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:10: T__66
                {
                mT__66(); 

                }
                break;
            case 2 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:16: BOOLEANLITERAL
                {
                mBOOLEANLITERAL(); 

                }
                break;
            case 3 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:31: INTLITERAL
                {
                mINTLITERAL(); 

                }
                break;
            case 4 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:42: DECIMALLITERAL
                {
                mDECIMALLITERAL(); 

                }
                break;
            case 5 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:57: STRINGLITERAL
                {
                mSTRINGLITERAL(); 

                }
                break;
            case 6 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:71: WS
                {
                mWS(); 

                }
                break;
            case 7 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:74: COMMENT
                {
                mCOMMENT(); 

                }
                break;
            case 8 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:82: LINE_COMMENT
                {
                mLINE_COMMENT(); 

                }
                break;
            case 9 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:95: NULL
                {
                mNULL(); 

                }
                break;
            case 10 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:100: LPAREN
                {
                mLPAREN(); 

                }
                break;
            case 11 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:107: RPAREN
                {
                mRPAREN(); 

                }
                break;
            case 12 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:114: LBRACE
                {
                mLBRACE(); 

                }
                break;
            case 13 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:121: RBRACE
                {
                mRBRACE(); 

                }
                break;
            case 14 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:128: LBRACKET
                {
                mLBRACKET(); 

                }
                break;
            case 15 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:137: RBRACKET
                {
                mRBRACKET(); 

                }
                break;
            case 16 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:146: SEMI
                {
                mSEMI(); 

                }
                break;
            case 17 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:151: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 18 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:157: DOT
                {
                mDOT(); 

                }
                break;
            case 19 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:161: EQ
                {
                mEQ(); 

                }
                break;
            case 20 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:164: BANG
                {
                mBANG(); 

                }
                break;
            case 21 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:169: TILDE
                {
                mTILDE(); 

                }
                break;
            case 22 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:175: QUES
                {
                mQUES(); 

                }
                break;
            case 23 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:180: COLON
                {
                mCOLON(); 

                }
                break;
            case 24 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:186: EQEQ
                {
                mEQEQ(); 

                }
                break;
            case 25 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:191: AMPAMP
                {
                mAMPAMP(); 

                }
                break;
            case 26 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:198: BARBAR
                {
                mBARBAR(); 

                }
                break;
            case 27 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:205: PLUS
                {
                mPLUS(); 

                }
                break;
            case 28 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:210: SUB
                {
                mSUB(); 

                }
                break;
            case 29 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:214: STAR
                {
                mSTAR(); 

                }
                break;
            case 30 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:219: SLASH
                {
                mSLASH(); 

                }
                break;
            case 31 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:225: AMP
                {
                mAMP(); 

                }
                break;
            case 32 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:229: BAR
                {
                mBAR(); 

                }
                break;
            case 33 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:233: CARET
                {
                mCARET(); 

                }
                break;
            case 34 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:239: PERCENT
                {
                mPERCENT(); 

                }
                break;
            case 35 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:247: MONKEYS_AT
                {
                mMONKEYS_AT(); 

                }
                break;
            case 36 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:258: BANGEQ
                {
                mBANGEQ(); 

                }
                break;
            case 37 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:265: GT
                {
                mGT(); 

                }
                break;
            case 38 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:268: SHIFT_RIGHT
                {
                mSHIFT_RIGHT(); 

                }
                break;
            case 39 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:280: SHIFT_RIGHT2
                {
                mSHIFT_RIGHT2(); 

                }
                break;
            case 40 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:293: SHIFT_LEFT
                {
                mSHIFT_LEFT(); 

                }
                break;
            case 41 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:304: GE
                {
                mGE(); 

                }
                break;
            case 42 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:307: LT
                {
                mLT(); 

                }
                break;
            case 43 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:310: LE
                {
                mLE(); 

                }
                break;
            case 44 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:313: ARROW
                {
                mARROW(); 

                }
                break;
            case 45 :
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:1:319: IDENTIFIER
                {
                mIDENTIFIER(); 

                }
                break;

        }

    }


    protected DFA15 dfa15 = new DFA15(this);
    protected DFA19 dfa19 = new DFA19(this);
    protected DFA24 dfa24 = new DFA24(this);
    protected DFA26 dfa26 = new DFA26(this);
    static final String DFA15_eotS =
        "\6\uffff";
    static final String DFA15_eofS =
        "\6\uffff";
    static final String DFA15_minS =
        "\1\60\2\56\3\uffff";
    static final String DFA15_maxS =
        "\1\71\1\170\1\145\3\uffff";
    static final String DFA15_acceptS =
        "\3\uffff\1\3\1\1\1\2";
    static final String DFA15_specialS =
        "\6\uffff}>";
    static final String[] DFA15_transitionS = {
            "\1\1\11\2",
            "\1\4\1\uffff\12\2\13\uffff\1\5\37\uffff\1\5\22\uffff\1\3",
            "\1\4\1\uffff\12\2\13\uffff\1\5\37\uffff\1\5",
            "",
            "",
            ""
    };

    static final short[] DFA15_eot = DFA.unpackEncodedString(DFA15_eotS);
    static final short[] DFA15_eof = DFA.unpackEncodedString(DFA15_eofS);
    static final char[] DFA15_min = DFA.unpackEncodedStringToUnsignedChars(DFA15_minS);
    static final char[] DFA15_max = DFA.unpackEncodedStringToUnsignedChars(DFA15_maxS);
    static final short[] DFA15_accept = DFA.unpackEncodedString(DFA15_acceptS);
    static final short[] DFA15_special = DFA.unpackEncodedString(DFA15_specialS);
    static final short[][] DFA15_transition;

    static {
        int numStates = DFA15_transitionS.length;
        DFA15_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA15_transition[i] = DFA.unpackEncodedString(DFA15_transitionS[i]);
        }
    }

    class DFA15 extends DFA {

        public DFA15(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 15;
            this.eot = DFA15_eot;
            this.eof = DFA15_eof;
            this.min = DFA15_min;
            this.max = DFA15_max;
            this.accept = DFA15_accept;
            this.special = DFA15_special;
            this.transition = DFA15_transition;
        }
        public String getDescription() {
            return "264:1: DECIMALLITERAL : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? | ( '0' .. '9' )+ Exponent | HexPrefix ( HexDigit )* ( () | ( '.' ( HexDigit )* ) ) ( 'p' | 'P' ) ( '+' | '-' )? ( '0' .. '9' )+ );";
        }
    }
    static final String DFA19_eotS =
        "\11\uffff\2\13\1\uffff\1\15\2\uffff";
    static final String DFA19_eofS =
        "\17\uffff";
    static final String DFA19_minS =
        "\1\42\10\uffff\2\60\1\uffff\1\60\2\uffff";
    static final String DFA19_maxS =
        "\1\164\10\uffff\2\67\1\uffff\1\67\2\uffff";
    static final String DFA19_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\2\uffff\1\13\1\uffff\1"+
        "\12\1\11";
    static final String DFA19_specialS =
        "\17\uffff}>";
    static final String[] DFA19_transitionS = {
            "\1\6\4\uffff\1\7\10\uffff\4\11\4\12\44\uffff\1\10\5\uffff\1"+
            "\1\3\uffff\1\4\7\uffff\1\3\3\uffff\1\5\1\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\10\14",
            "\10\15",
            "",
            "\10\16",
            "",
            ""
    };

    static final short[] DFA19_eot = DFA.unpackEncodedString(DFA19_eotS);
    static final short[] DFA19_eof = DFA.unpackEncodedString(DFA19_eofS);
    static final char[] DFA19_min = DFA.unpackEncodedStringToUnsignedChars(DFA19_minS);
    static final char[] DFA19_max = DFA.unpackEncodedStringToUnsignedChars(DFA19_maxS);
    static final short[] DFA19_accept = DFA.unpackEncodedString(DFA19_acceptS);
    static final short[] DFA19_special = DFA.unpackEncodedString(DFA19_specialS);
    static final short[][] DFA19_transition;

    static {
        int numStates = DFA19_transitionS.length;
        DFA19_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA19_transition[i] = DFA.unpackEncodedString(DFA19_transitionS[i]);
        }
    }

    class DFA19 extends DFA {

        public DFA19(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 19;
            this.eot = DFA19_eot;
            this.eof = DFA19_eof;
            this.min = DFA19_min;
            this.max = DFA19_max;
            this.accept = DFA19_accept;
            this.special = DFA19_special;
            this.transition = DFA19_transition;
        }
        public String getDescription() {
            return "281:14: ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | ( '0' .. '7' ) ( '0' .. '7' ) | ( '0' .. '7' ) )";
        }
    }
    static final String DFA24_eotS =
        "\2\uffff\2\4\2\uffff";
    static final String DFA24_eofS =
        "\6\uffff";
    static final String DFA24_minS =
        "\2\57\2\0\2\uffff";
    static final String DFA24_maxS =
        "\2\57\2\uffff\2\uffff";
    static final String DFA24_acceptS =
        "\4\uffff\1\2\1\1";
    static final String DFA24_specialS =
        "\2\uffff\1\1\1\0\2\uffff}>";
    static final String[] DFA24_transitionS = {
            "\1\1",
            "\1\2",
            "\12\3\1\5\2\3\1\5\ufff2\3",
            "\12\3\1\5\2\3\1\5\ufff2\3",
            "",
            ""
    };

    static final short[] DFA24_eot = DFA.unpackEncodedString(DFA24_eotS);
    static final short[] DFA24_eof = DFA.unpackEncodedString(DFA24_eofS);
    static final char[] DFA24_min = DFA.unpackEncodedStringToUnsignedChars(DFA24_minS);
    static final char[] DFA24_max = DFA.unpackEncodedStringToUnsignedChars(DFA24_maxS);
    static final short[] DFA24_accept = DFA.unpackEncodedString(DFA24_acceptS);
    static final short[] DFA24_special = DFA.unpackEncodedString(DFA24_specialS);
    static final short[][] DFA24_transition;

    static {
        int numStates = DFA24_transitionS.length;
        DFA24_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA24_transition[i] = DFA.unpackEncodedString(DFA24_transitionS[i]);
        }
    }

    class DFA24 extends DFA {

        public DFA24(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 24;
            this.eot = DFA24_eot;
            this.eof = DFA24_eof;
            this.min = DFA24_min;
            this.max = DFA24_max;
            this.accept = DFA24_accept;
            this.special = DFA24_special;
            this.transition = DFA24_transition;
        }
        public String getDescription() {
            return "304:1: LINE_COMMENT : ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r\\n' | '\\r' | '\\n' ) | '//' (~ ( '\\n' | '\\r' ) )* );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA24_3 = input.LA(1);

                        s = -1;
                        if ( ((LA24_3>='\u0000' && LA24_3<='\t')||(LA24_3>='\u000B' && LA24_3<='\f')||(LA24_3>='\u000E' && LA24_3<='\uFFFF')) ) {s = 3;}

                        else if ( (LA24_3=='\n'||LA24_3=='\r') ) {s = 5;}

                        else s = 4;

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA24_2 = input.LA(1);

                        s = -1;
                        if ( ((LA24_2>='\u0000' && LA24_2<='\t')||(LA24_2>='\u000B' && LA24_2<='\f')||(LA24_2>='\u000E' && LA24_2<='\uFFFF')) ) {s = 3;}

                        else if ( (LA24_2=='\n'||LA24_2=='\r') ) {s = 5;}

                        else s = 4;

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 24, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA26_eotS =
        "\1\uffff\3\41\2\51\2\uffff\1\55\11\uffff\1\57\1\61\3\uffff\1\63"+
        "\1\65\1\uffff\1\67\4\uffff\1\72\1\75\1\uffff\4\41\2\uffff\1\51\1"+
        "\uffff\1\51\15\uffff\1\104\5\uffff\1\105\3\41\1\51\3\uffff\1\111"+
        "\1\112\1\41\2\uffff\1\112";
    static final String DFA26_eofS =
        "\114\uffff";
    static final String DFA26_minS =
        "\1\11\1\145\1\162\1\141\2\56\2\uffff\1\52\11\uffff\2\75\3\uffff"+
        "\1\46\1\174\1\uffff\1\76\4\uffff\1\75\1\74\1\uffff\1\167\1\154\1"+
        "\165\1\154\1\56\1\uffff\1\56\1\uffff\1\56\15\uffff\1\76\5\uffff"+
        "\1\60\1\154\1\145\1\163\1\56\3\uffff\2\60\1\145\2\uffff\1\60";
    static final String DFA26_maxS =
        "\1\176\1\165\1\162\1\141\1\170\1\145\2\uffff\1\57\11\uffff\2\75"+
        "\3\uffff\1\46\1\174\1\uffff\1\76\4\uffff\1\76\1\75\1\uffff\1\167"+
        "\1\154\1\165\1\154\1\160\1\uffff\1\145\1\uffff\1\145\15\uffff\1"+
        "\76\5\uffff\1\172\1\154\1\145\1\163\1\160\3\uffff\2\172\1\145\2"+
        "\uffff\1\172";
    static final String DFA26_acceptS =
        "\6\uffff\1\5\1\6\1\uffff\1\12\1\13\1\14\1\15\1\16\1\17\1\20\1\21"+
        "\1\22\2\uffff\1\25\1\26\1\27\2\uffff\1\33\1\uffff\1\35\1\41\1\42"+
        "\1\43\2\uffff\1\55\5\uffff\1\4\1\uffff\1\3\1\uffff\1\7\1\10\1\36"+
        "\1\30\1\23\1\44\1\24\1\31\1\37\1\32\1\40\1\54\1\34\1\uffff\1\51"+
        "\1\45\1\50\1\53\1\52\5\uffff\1\47\1\46\1\1\3\uffff\1\11\1\2\1\uffff";
    static final String DFA26_specialS =
        "\114\uffff}>";
    static final String[] DFA26_transitionS = {
            "\2\7\1\uffff\2\7\22\uffff\1\7\1\23\3\uffff\1\35\1\27\1\6\1\11"+
            "\1\12\1\33\1\31\1\20\1\32\1\21\1\10\1\4\11\5\1\26\1\17\1\40"+
            "\1\22\1\37\1\25\1\36\32\41\1\15\1\uffff\1\16\1\34\1\41\1\uffff"+
            "\5\41\1\3\7\41\1\1\5\41\1\2\6\41\1\13\1\30\1\14\1\24",
            "\1\42\17\uffff\1\43",
            "\1\44",
            "\1\45",
            "\1\47\1\uffff\10\50\2\47\13\uffff\1\47\37\uffff\1\47\22\uffff"+
            "\1\46",
            "\1\47\1\uffff\12\52\13\uffff\1\47\37\uffff\1\47",
            "",
            "",
            "\1\53\4\uffff\1\54",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\56",
            "\1\60",
            "",
            "",
            "",
            "\1\62",
            "\1\64",
            "",
            "\1\66",
            "",
            "",
            "",
            "",
            "\1\71\1\70",
            "\1\73\1\74",
            "",
            "\1\76",
            "\1\77",
            "\1\100",
            "\1\101",
            "\1\47\1\uffff\12\102\7\uffff\6\102\11\uffff\1\47\20\uffff\6"+
            "\102\11\uffff\1\47",
            "",
            "\1\47\1\uffff\10\50\2\47\13\uffff\1\47\37\uffff\1\47",
            "",
            "\1\47\1\uffff\12\52\13\uffff\1\47\37\uffff\1\47",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\103",
            "",
            "",
            "",
            "",
            "",
            "\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "\1\106",
            "\1\107",
            "\1\110",
            "\1\47\1\uffff\12\102\7\uffff\6\102\11\uffff\1\47\20\uffff\6"+
            "\102\11\uffff\1\47",
            "",
            "",
            "",
            "\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "\1\113",
            "",
            "",
            "\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41"
    };

    static final short[] DFA26_eot = DFA.unpackEncodedString(DFA26_eotS);
    static final short[] DFA26_eof = DFA.unpackEncodedString(DFA26_eofS);
    static final char[] DFA26_min = DFA.unpackEncodedStringToUnsignedChars(DFA26_minS);
    static final char[] DFA26_max = DFA.unpackEncodedStringToUnsignedChars(DFA26_maxS);
    static final short[] DFA26_accept = DFA.unpackEncodedString(DFA26_acceptS);
    static final short[] DFA26_special = DFA.unpackEncodedString(DFA26_specialS);
    static final short[][] DFA26_transition;

    static {
        int numStates = DFA26_transitionS.length;
        DFA26_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA26_transition[i] = DFA.unpackEncodedString(DFA26_transitionS[i]);
        }
    }

    class DFA26 extends DFA {

        public DFA26(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 26;
            this.eot = DFA26_eot;
            this.eof = DFA26_eof;
            this.min = DFA26_min;
            this.max = DFA26_max;
            this.accept = DFA26_accept;
            this.special = DFA26_special;
            this.transition = DFA26_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__66 | BOOLEANLITERAL | INTLITERAL | DECIMALLITERAL | STRINGLITERAL | WS | COMMENT | LINE_COMMENT | NULL | LPAREN | RPAREN | LBRACE | RBRACE | LBRACKET | RBRACKET | SEMI | COMMA | DOT | EQ | BANG | TILDE | QUES | COLON | EQEQ | AMPAMP | BARBAR | PLUS | SUB | STAR | SLASH | AMP | BAR | CARET | PERCENT | MONKEYS_AT | BANGEQ | GT | SHIFT_RIGHT | SHIFT_RIGHT2 | SHIFT_LEFT | GE | LT | LE | ARROW | IDENTIFIER );";
        }
    }
 

}