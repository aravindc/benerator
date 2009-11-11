// $ANTLR 3.2 Sep 23, 2009 12:02:23 /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g 2009-11-11 14:56:31

	package org.databene.benerator.script;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

@SuppressWarnings("all")
public class BeneratorParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "IDENTIFIER", "INTLITERAL", "DECIMALLITERAL", "STRINGLITERAL", "BOOLEANLITERAL", "NULL", "TYPE", "NEGATION", "INDEX", "FIELD", "ARGUMENTS", "CAST", "CONSTRUCTOR", "INVOCATION", "SUBINVOCATION", "QUALIFIEDNAME", "BEAN", "BEANSPEC", "HexPrefix", "HexDigit", "Exponent", "EscapeSequence", "WS", "COMMENT", "LINE_COMMENT", "LPAREN", "RPAREN", "LBRACE", "RBRACE", "LBRACKET", "RBRACKET", "SEMI", "COMMA", "DOT", "EQ", "BANG", "TILDE", "QUES", "COLON", "EQEQ", "AMPAMP", "BARBAR", "PLUS", "SUB", "STAR", "SLASH", "AMP", "BAR", "CARET", "PERCENT", "MONKEYS_AT", "BANGEQ", "GT", "SHIFT_RIGHT", "SHIFT_RIGHT2", "SHIFT_LEFT", "GE", "LT", "LE", "ARROW", "IdentifierStart", "IdentifierPart", "'new'"
    };
    public static final int SHIFT_LEFT=59;
    public static final int INDEX=12;
    public static final int COMMA=36;
    public static final int TYPE=10;
    public static final int PERCENT=53;
    public static final int BEAN=20;
    public static final int HexDigit=23;
    public static final int ARROW=63;
    public static final int BANG=39;
    public static final int CAST=15;
    public static final int LBRACKET=33;
    public static final int SHIFT_RIGHT2=58;
    public static final int BANGEQ=55;
    public static final int TILDE=40;
    public static final int LBRACE=31;
    public static final int AMPAMP=44;
    public static final int DOT=37;
    public static final int RBRACE=32;
    public static final int INTLITERAL=5;
    public static final int EscapeSequence=25;
    public static final int LE=62;
    public static final int RBRACKET=34;
    public static final int AMP=50;
    public static final int STRINGLITERAL=7;
    public static final int RPAREN=30;
    public static final int LPAREN=29;
    public static final int INVOCATION=17;
    public static final int PLUS=46;
    public static final int IdentifierPart=65;
    public static final int Exponent=24;
    public static final int ARGUMENTS=14;
    public static final int SLASH=49;
    public static final int NEGATION=11;
    public static final int WS=26;
    public static final int EQ=38;
    public static final int QUALIFIEDNAME=19;
    public static final int LT=61;
    public static final int GT=56;
    public static final int T__66=66;
    public static final int HexPrefix=22;
    public static final int COMMENT=27;
    public static final int SUBINVOCATION=18;
    public static final int CARET=52;
    public static final int LINE_COMMENT=28;
    public static final int EQEQ=43;
    public static final int BARBAR=45;
    public static final int FIELD=13;
    public static final int CONSTRUCTOR=16;
    public static final int SEMI=35;
    public static final int GE=60;
    public static final int IdentifierStart=64;
    public static final int BAR=51;
    public static final int SHIFT_RIGHT=57;
    public static final int EOF=-1;
    public static final int NULL=9;
    public static final int BOOLEANLITERAL=8;
    public static final int DECIMALLITERAL=6;
    public static final int QUES=41;
    public static final int COLON=42;
    public static final int BEANSPEC=21;
    public static final int MONKEYS_AT=54;
    public static final int STAR=48;
    public static final int IDENTIFIER=4;
    public static final int SUB=47;

    // delegates
    // delegators


        public BeneratorParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public BeneratorParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
            this.state.ruleMemo = new HashMap[73+1];
             
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return BeneratorParser.tokenNames; }
    public String getGrammarFileName() { return "/Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g"; }


    protected void mismatch(IntStream input, int ttype, BitSet follow)
      throws RecognitionException
    {
      throw new MismatchedTokenException(ttype, input);
    }

    public Object recoverFromMismatchedSet(IntStream input, RecognitionException e, BitSet follow)
      throws RecognitionException
    {
      throw e;
    }


    public static class weightedLiteralList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "weightedLiteralList"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:96:1: weightedLiteralList : weightedLiteral ( ',' weightedLiteral )* ;
    public final BeneratorParser.weightedLiteralList_return weightedLiteralList() throws RecognitionException {
        BeneratorParser.weightedLiteralList_return retval = new BeneratorParser.weightedLiteralList_return();
        retval.start = input.LT(1);
        int weightedLiteralList_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal2=null;
        BeneratorParser.weightedLiteral_return weightedLiteral1 = null;

        BeneratorParser.weightedLiteral_return weightedLiteral3 = null;


        Object char_literal2_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 1) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:101:5: ( weightedLiteral ( ',' weightedLiteral )* )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:101:9: weightedLiteral ( ',' weightedLiteral )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_weightedLiteral_in_weightedLiteralList90);
            weightedLiteral1=weightedLiteral();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, weightedLiteral1.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:101:25: ( ',' weightedLiteral )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==COMMA) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:101:26: ',' weightedLiteral
            	    {
            	    char_literal2=(Token)match(input,COMMA,FOLLOW_COMMA_in_weightedLiteralList93); if (state.failed) return retval;
            	    pushFollow(FOLLOW_weightedLiteral_in_weightedLiteralList96);
            	    weightedLiteral3=weightedLiteral();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, weightedLiteral3.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 1, weightedLiteralList_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "weightedLiteralList"

    public static class weightedLiteral_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "weightedLiteral"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:103:1: weightedLiteral : literal ( '^' expression )? ;
    public final BeneratorParser.weightedLiteral_return weightedLiteral() throws RecognitionException {
        BeneratorParser.weightedLiteral_return retval = new BeneratorParser.weightedLiteral_return();
        retval.start = input.LT(1);
        int weightedLiteral_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal5=null;
        BeneratorParser.literal_return literal4 = null;

        BeneratorParser.expression_return expression6 = null;


        Object char_literal5_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 2) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:104:5: ( literal ( '^' expression )? )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:104:9: literal ( '^' expression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_literal_in_weightedLiteral113);
            literal4=literal();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, literal4.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:104:17: ( '^' expression )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==CARET) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:104:18: '^' expression
                    {
                    char_literal5=(Token)match(input,CARET,FOLLOW_CARET_in_weightedLiteral116); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal5_tree = (Object)adaptor.create(char_literal5);
                    root_0 = (Object)adaptor.becomeRoot(char_literal5_tree, root_0);
                    }
                    pushFollow(FOLLOW_expression_in_weightedLiteral119);
                    expression6=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression6.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 2, weightedLiteral_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "weightedLiteral"

    public static class transitionList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "transitionList"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:106:1: transitionList : transition ( ',' transition )* ;
    public final BeneratorParser.transitionList_return transitionList() throws RecognitionException {
        BeneratorParser.transitionList_return retval = new BeneratorParser.transitionList_return();
        retval.start = input.LT(1);
        int transitionList_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal8=null;
        BeneratorParser.transition_return transition7 = null;

        BeneratorParser.transition_return transition9 = null;


        Object char_literal8_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 3) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:107:5: ( transition ( ',' transition )* )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:107:9: transition ( ',' transition )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_transition_in_transitionList135);
            transition7=transition();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, transition7.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:107:20: ( ',' transition )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==COMMA) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:107:21: ',' transition
            	    {
            	    char_literal8=(Token)match(input,COMMA,FOLLOW_COMMA_in_transitionList138); if (state.failed) return retval;
            	    pushFollow(FOLLOW_transition_in_transitionList141);
            	    transition9=transition();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, transition9.getTree());

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 3, transitionList_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "transitionList"

    public static class transition_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "transition"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:109:1: transition : literal '->' literal ( '^' expression )? ;
    public final BeneratorParser.transition_return transition() throws RecognitionException {
        BeneratorParser.transition_return retval = new BeneratorParser.transition_return();
        retval.start = input.LT(1);
        int transition_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal11=null;
        Token char_literal13=null;
        BeneratorParser.literal_return literal10 = null;

        BeneratorParser.literal_return literal12 = null;

        BeneratorParser.expression_return expression14 = null;


        Object string_literal11_tree=null;
        Object char_literal13_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 4) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:110:5: ( literal '->' literal ( '^' expression )? )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:110:9: literal '->' literal ( '^' expression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_literal_in_transition157);
            literal10=literal();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, literal10.getTree());
            string_literal11=(Token)match(input,ARROW,FOLLOW_ARROW_in_transition159); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal11_tree = (Object)adaptor.create(string_literal11);
            root_0 = (Object)adaptor.becomeRoot(string_literal11_tree, root_0);
            }
            pushFollow(FOLLOW_literal_in_transition162);
            literal12=literal();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, literal12.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:110:31: ( '^' expression )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==CARET) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:110:32: '^' expression
                    {
                    char_literal13=(Token)match(input,CARET,FOLLOW_CARET_in_transition165); if (state.failed) return retval;
                    pushFollow(FOLLOW_expression_in_transition168);
                    expression14=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression14.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 4, transition_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "transition"

    public static class assignment_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "assignment"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:112:1: assignment : IDENTIFIER '=' expression ;
    public final BeneratorParser.assignment_return assignment() throws RecognitionException {
        BeneratorParser.assignment_return retval = new BeneratorParser.assignment_return();
        retval.start = input.LT(1);
        int assignment_StartIndex = input.index();
        Object root_0 = null;

        Token IDENTIFIER15=null;
        Token char_literal16=null;
        BeneratorParser.expression_return expression17 = null;


        Object IDENTIFIER15_tree=null;
        Object char_literal16_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 5) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:113:5: ( IDENTIFIER '=' expression )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:113:9: IDENTIFIER '=' expression
            {
            root_0 = (Object)adaptor.nil();

            IDENTIFIER15=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_assignment184); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER15_tree = (Object)adaptor.create(IDENTIFIER15);
            adaptor.addChild(root_0, IDENTIFIER15_tree);
            }
            char_literal16=(Token)match(input,EQ,FOLLOW_EQ_in_assignment186); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal16_tree = (Object)adaptor.create(char_literal16);
            root_0 = (Object)adaptor.becomeRoot(char_literal16_tree, root_0);
            }
            pushFollow(FOLLOW_expression_in_assignment189);
            expression17=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression17.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 5, assignment_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "assignment"

    public static class beanSpecList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "beanSpecList"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:115:1: beanSpecList : beanSpec ( ',' beanSpec )* ;
    public final BeneratorParser.beanSpecList_return beanSpecList() throws RecognitionException {
        BeneratorParser.beanSpecList_return retval = new BeneratorParser.beanSpecList_return();
        retval.start = input.LT(1);
        int beanSpecList_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal19=null;
        BeneratorParser.beanSpec_return beanSpec18 = null;

        BeneratorParser.beanSpec_return beanSpec20 = null;


        Object char_literal19_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:116:5: ( beanSpec ( ',' beanSpec )* )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:116:9: beanSpec ( ',' beanSpec )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_beanSpec_in_beanSpecList203);
            beanSpec18=beanSpec();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, beanSpec18.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:116:18: ( ',' beanSpec )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==COMMA) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:116:19: ',' beanSpec
            	    {
            	    char_literal19=(Token)match(input,COMMA,FOLLOW_COMMA_in_beanSpecList206); if (state.failed) return retval;
            	    pushFollow(FOLLOW_beanSpec_in_beanSpecList209);
            	    beanSpec20=beanSpec();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, beanSpec20.getTree());

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 6, beanSpecList_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "beanSpecList"

    public static class beanSpec_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "beanSpec"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:118:1: beanSpec : expression -> ^( BEANSPEC expression ) ;
    public final BeneratorParser.beanSpec_return beanSpec() throws RecognitionException {
        BeneratorParser.beanSpec_return retval = new BeneratorParser.beanSpec_return();
        retval.start = input.LT(1);
        int beanSpec_StartIndex = input.index();
        Object root_0 = null;

        BeneratorParser.expression_return expression21 = null;


        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 7) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:119:5: ( expression -> ^( BEANSPEC expression ) )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:119:9: expression
            {
            pushFollow(FOLLOW_expression_in_beanSpec225);
            expression21=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expression.add(expression21.getTree());


            // AST REWRITE
            // elements: expression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 119:20: -> ^( BEANSPEC expression )
            {
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:119:23: ^( BEANSPEC expression )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(BEANSPEC, "BEANSPEC"), root_1);

                adaptor.addChild(root_1, stream_expression.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 7, beanSpec_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "beanSpec"

    public static class expression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expression"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:121:1: expression : conditionalExpression ;
    public final BeneratorParser.expression_return expression() throws RecognitionException {
        BeneratorParser.expression_return retval = new BeneratorParser.expression_return();
        retval.start = input.LT(1);
        int expression_StartIndex = input.index();
        Object root_0 = null;

        BeneratorParser.conditionalExpression_return conditionalExpression22 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 8) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:122:5: ( conditionalExpression )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:122:9: conditionalExpression
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalExpression_in_expression248);
            conditionalExpression22=conditionalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression22.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 8, expression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "expression"

    public static class conditionalExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conditionalExpression"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:125:1: conditionalExpression : conditionalOrExpression ( '?' expression ':' conditionalExpression )? ;
    public final BeneratorParser.conditionalExpression_return conditionalExpression() throws RecognitionException {
        BeneratorParser.conditionalExpression_return retval = new BeneratorParser.conditionalExpression_return();
        retval.start = input.LT(1);
        int conditionalExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal24=null;
        Token char_literal26=null;
        BeneratorParser.conditionalOrExpression_return conditionalOrExpression23 = null;

        BeneratorParser.expression_return expression25 = null;

        BeneratorParser.conditionalExpression_return conditionalExpression27 = null;


        Object char_literal24_tree=null;
        Object char_literal26_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:126:5: ( conditionalOrExpression ( '?' expression ':' conditionalExpression )? )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:126:9: conditionalOrExpression ( '?' expression ':' conditionalExpression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression268);
            conditionalOrExpression23=conditionalOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalOrExpression23.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:126:33: ( '?' expression ':' conditionalExpression )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==QUES) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:126:34: '?' expression ':' conditionalExpression
                    {
                    char_literal24=(Token)match(input,QUES,FOLLOW_QUES_in_conditionalExpression271); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal24_tree = (Object)adaptor.create(char_literal24);
                    root_0 = (Object)adaptor.becomeRoot(char_literal24_tree, root_0);
                    }
                    pushFollow(FOLLOW_expression_in_conditionalExpression274);
                    expression25=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression25.getTree());
                    char_literal26=(Token)match(input,COLON,FOLLOW_COLON_in_conditionalExpression276); if (state.failed) return retval;
                    pushFollow(FOLLOW_conditionalExpression_in_conditionalExpression279);
                    conditionalExpression27=conditionalExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression27.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 9, conditionalExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "conditionalExpression"

    public static class conditionalOrExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conditionalOrExpression"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:129:1: conditionalOrExpression : conditionalAndExpression ( '||' conditionalAndExpression )* ;
    public final BeneratorParser.conditionalOrExpression_return conditionalOrExpression() throws RecognitionException {
        BeneratorParser.conditionalOrExpression_return retval = new BeneratorParser.conditionalOrExpression_return();
        retval.start = input.LT(1);
        int conditionalOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal29=null;
        BeneratorParser.conditionalAndExpression_return conditionalAndExpression28 = null;

        BeneratorParser.conditionalAndExpression_return conditionalAndExpression30 = null;


        Object string_literal29_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 10) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:130:5: ( conditionalAndExpression ( '||' conditionalAndExpression )* )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:130:9: conditionalAndExpression ( '||' conditionalAndExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression305);
            conditionalAndExpression28=conditionalAndExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression28.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:130:34: ( '||' conditionalAndExpression )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==BARBAR) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:130:35: '||' conditionalAndExpression
            	    {
            	    string_literal29=(Token)match(input,BARBAR,FOLLOW_BARBAR_in_conditionalOrExpression308); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal29_tree = (Object)adaptor.create(string_literal29);
            	    root_0 = (Object)adaptor.becomeRoot(string_literal29_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression311);
            	    conditionalAndExpression30=conditionalAndExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression30.getTree());

            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 10, conditionalOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "conditionalOrExpression"

    public static class conditionalAndExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conditionalAndExpression"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:133:1: conditionalAndExpression : inclusiveOrExpression ( '&&' inclusiveOrExpression )* ;
    public final BeneratorParser.conditionalAndExpression_return conditionalAndExpression() throws RecognitionException {
        BeneratorParser.conditionalAndExpression_return retval = new BeneratorParser.conditionalAndExpression_return();
        retval.start = input.LT(1);
        int conditionalAndExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal32=null;
        BeneratorParser.inclusiveOrExpression_return inclusiveOrExpression31 = null;

        BeneratorParser.inclusiveOrExpression_return inclusiveOrExpression33 = null;


        Object string_literal32_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 11) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:134:5: ( inclusiveOrExpression ( '&&' inclusiveOrExpression )* )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:134:9: inclusiveOrExpression ( '&&' inclusiveOrExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression334);
            inclusiveOrExpression31=inclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression31.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:134:31: ( '&&' inclusiveOrExpression )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==AMPAMP) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:134:32: '&&' inclusiveOrExpression
            	    {
            	    string_literal32=(Token)match(input,AMPAMP,FOLLOW_AMPAMP_in_conditionalAndExpression337); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal32_tree = (Object)adaptor.create(string_literal32);
            	    root_0 = (Object)adaptor.becomeRoot(string_literal32_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression340);
            	    inclusiveOrExpression33=inclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression33.getTree());

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 11, conditionalAndExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "conditionalAndExpression"

    public static class inclusiveOrExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "inclusiveOrExpression"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:137:1: inclusiveOrExpression : exclusiveOrExpression ( '|' exclusiveOrExpression )* ;
    public final BeneratorParser.inclusiveOrExpression_return inclusiveOrExpression() throws RecognitionException {
        BeneratorParser.inclusiveOrExpression_return retval = new BeneratorParser.inclusiveOrExpression_return();
        retval.start = input.LT(1);
        int inclusiveOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal35=null;
        BeneratorParser.exclusiveOrExpression_return exclusiveOrExpression34 = null;

        BeneratorParser.exclusiveOrExpression_return exclusiveOrExpression36 = null;


        Object char_literal35_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 12) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:138:5: ( exclusiveOrExpression ( '|' exclusiveOrExpression )* )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:138:9: exclusiveOrExpression ( '|' exclusiveOrExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression362);
            exclusiveOrExpression34=exclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression34.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:138:31: ( '|' exclusiveOrExpression )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==BAR) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:138:32: '|' exclusiveOrExpression
            	    {
            	    char_literal35=(Token)match(input,BAR,FOLLOW_BAR_in_inclusiveOrExpression365); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal35_tree = (Object)adaptor.create(char_literal35);
            	    root_0 = (Object)adaptor.becomeRoot(char_literal35_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression368);
            	    exclusiveOrExpression36=exclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression36.getTree());

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 12, inclusiveOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "inclusiveOrExpression"

    public static class exclusiveOrExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "exclusiveOrExpression"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:141:1: exclusiveOrExpression : andExpression ( '^' andExpression )* ;
    public final BeneratorParser.exclusiveOrExpression_return exclusiveOrExpression() throws RecognitionException {
        BeneratorParser.exclusiveOrExpression_return retval = new BeneratorParser.exclusiveOrExpression_return();
        retval.start = input.LT(1);
        int exclusiveOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal38=null;
        BeneratorParser.andExpression_return andExpression37 = null;

        BeneratorParser.andExpression_return andExpression39 = null;


        Object char_literal38_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 13) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:142:5: ( andExpression ( '^' andExpression )* )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:142:9: andExpression ( '^' andExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression389);
            andExpression37=andExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression37.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:142:23: ( '^' andExpression )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==CARET) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:142:24: '^' andExpression
            	    {
            	    char_literal38=(Token)match(input,CARET,FOLLOW_CARET_in_exclusiveOrExpression392); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal38_tree = (Object)adaptor.create(char_literal38);
            	    root_0 = (Object)adaptor.becomeRoot(char_literal38_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression395);
            	    andExpression39=andExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression39.getTree());

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 13, exclusiveOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "exclusiveOrExpression"

    public static class andExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "andExpression"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:145:1: andExpression : equalityExpression ( '&' equalityExpression )* ;
    public final BeneratorParser.andExpression_return andExpression() throws RecognitionException {
        BeneratorParser.andExpression_return retval = new BeneratorParser.andExpression_return();
        retval.start = input.LT(1);
        int andExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal41=null;
        BeneratorParser.equalityExpression_return equalityExpression40 = null;

        BeneratorParser.equalityExpression_return equalityExpression42 = null;


        Object char_literal41_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 14) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:146:5: ( equalityExpression ( '&' equalityExpression )* )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:146:9: equalityExpression ( '&' equalityExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_equalityExpression_in_andExpression416);
            equalityExpression40=equalityExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, equalityExpression40.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:146:28: ( '&' equalityExpression )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==AMP) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:146:29: '&' equalityExpression
            	    {
            	    char_literal41=(Token)match(input,AMP,FOLLOW_AMP_in_andExpression419); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal41_tree = (Object)adaptor.create(char_literal41);
            	    root_0 = (Object)adaptor.becomeRoot(char_literal41_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_equalityExpression_in_andExpression422);
            	    equalityExpression42=equalityExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, equalityExpression42.getTree());

            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 14, andExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "andExpression"

    public static class equalityExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "equalityExpression"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:149:1: equalityExpression : relationalExpression ( ( '==' | '!=' ) relationalExpression )* ;
    public final BeneratorParser.equalityExpression_return equalityExpression() throws RecognitionException {
        BeneratorParser.equalityExpression_return retval = new BeneratorParser.equalityExpression_return();
        retval.start = input.LT(1);
        int equalityExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set44=null;
        BeneratorParser.relationalExpression_return relationalExpression43 = null;

        BeneratorParser.relationalExpression_return relationalExpression45 = null;


        Object set44_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 15) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:150:5: ( relationalExpression ( ( '==' | '!=' ) relationalExpression )* )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:150:9: relationalExpression ( ( '==' | '!=' ) relationalExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_relationalExpression_in_equalityExpression444);
            relationalExpression43=relationalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalExpression43.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:150:30: ( ( '==' | '!=' ) relationalExpression )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==EQEQ||LA12_0==BANGEQ) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:150:31: ( '==' | '!=' ) relationalExpression
            	    {
            	    set44=(Token)input.LT(1);
            	    set44=(Token)input.LT(1);
            	    if ( input.LA(1)==EQEQ||input.LA(1)==BANGEQ ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set44), root_0);
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_relationalExpression_in_equalityExpression456);
            	    relationalExpression45=relationalExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalExpression45.getTree());

            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 15, equalityExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "equalityExpression"

    public static class relationalExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "relationalExpression"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:153:1: relationalExpression : shiftExpression ( ( '<=' | '>=' | '<' | '>' ) shiftExpression )* ;
    public final BeneratorParser.relationalExpression_return relationalExpression() throws RecognitionException {
        BeneratorParser.relationalExpression_return retval = new BeneratorParser.relationalExpression_return();
        retval.start = input.LT(1);
        int relationalExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set47=null;
        BeneratorParser.shiftExpression_return shiftExpression46 = null;

        BeneratorParser.shiftExpression_return shiftExpression48 = null;


        Object set47_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 16) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:154:5: ( shiftExpression ( ( '<=' | '>=' | '<' | '>' ) shiftExpression )* )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:154:9: shiftExpression ( ( '<=' | '>=' | '<' | '>' ) shiftExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_shiftExpression_in_relationalExpression478);
            shiftExpression46=shiftExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpression46.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:154:25: ( ( '<=' | '>=' | '<' | '>' ) shiftExpression )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==GT||(LA13_0>=GE && LA13_0<=LE)) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:154:26: ( '<=' | '>=' | '<' | '>' ) shiftExpression
            	    {
            	    set47=(Token)input.LT(1);
            	    set47=(Token)input.LT(1);
            	    if ( input.LA(1)==GT||(input.LA(1)>=GE && input.LA(1)<=LE) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set47), root_0);
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_shiftExpression_in_relationalExpression498);
            	    shiftExpression48=shiftExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpression48.getTree());

            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 16, relationalExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "relationalExpression"

    public static class shiftExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "shiftExpression"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:157:1: shiftExpression : additiveExpression ( ( '<<' | '>>>' | '>>' ) additiveExpression )* ;
    public final BeneratorParser.shiftExpression_return shiftExpression() throws RecognitionException {
        BeneratorParser.shiftExpression_return retval = new BeneratorParser.shiftExpression_return();
        retval.start = input.LT(1);
        int shiftExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set50=null;
        BeneratorParser.additiveExpression_return additiveExpression49 = null;

        BeneratorParser.additiveExpression_return additiveExpression51 = null;


        Object set50_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 17) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:158:5: ( additiveExpression ( ( '<<' | '>>>' | '>>' ) additiveExpression )* )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:158:9: additiveExpression ( ( '<<' | '>>>' | '>>' ) additiveExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_additiveExpression_in_shiftExpression520);
            additiveExpression49=additiveExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression49.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:158:28: ( ( '<<' | '>>>' | '>>' ) additiveExpression )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( ((LA14_0>=SHIFT_RIGHT && LA14_0<=SHIFT_LEFT)) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:158:29: ( '<<' | '>>>' | '>>' ) additiveExpression
            	    {
            	    set50=(Token)input.LT(1);
            	    set50=(Token)input.LT(1);
            	    if ( (input.LA(1)>=SHIFT_RIGHT && input.LA(1)<=SHIFT_LEFT) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set50), root_0);
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression536);
            	    additiveExpression51=additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression51.getTree());

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 17, shiftExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "shiftExpression"

    public static class additiveExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "additiveExpression"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:161:1: additiveExpression : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* ;
    public final BeneratorParser.additiveExpression_return additiveExpression() throws RecognitionException {
        BeneratorParser.additiveExpression_return retval = new BeneratorParser.additiveExpression_return();
        retval.start = input.LT(1);
        int additiveExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set53=null;
        BeneratorParser.multiplicativeExpression_return multiplicativeExpression52 = null;

        BeneratorParser.multiplicativeExpression_return multiplicativeExpression54 = null;


        Object set53_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 18) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:162:5: ( multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:162:9: multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression558);
            multiplicativeExpression52=multiplicativeExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression52.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:162:34: ( ( '+' | '-' ) multiplicativeExpression )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( ((LA15_0>=PLUS && LA15_0<=SUB)) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:162:35: ( '+' | '-' ) multiplicativeExpression
            	    {
            	    set53=(Token)input.LT(1);
            	    set53=(Token)input.LT(1);
            	    if ( (input.LA(1)>=PLUS && input.LA(1)<=SUB) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set53), root_0);
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression570);
            	    multiplicativeExpression54=multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression54.getTree());

            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 18, additiveExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "additiveExpression"

    public static class multiplicativeExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "multiplicativeExpression"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:165:1: multiplicativeExpression : unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* ;
    public final BeneratorParser.multiplicativeExpression_return multiplicativeExpression() throws RecognitionException {
        BeneratorParser.multiplicativeExpression_return retval = new BeneratorParser.multiplicativeExpression_return();
        retval.start = input.LT(1);
        int multiplicativeExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set56=null;
        BeneratorParser.unaryExpression_return unaryExpression55 = null;

        BeneratorParser.unaryExpression_return unaryExpression57 = null;


        Object set56_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 19) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:166:5: ( unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:166:9: unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression592);
            unaryExpression55=unaryExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression55.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:166:25: ( ( '*' | '/' | '%' ) unaryExpression )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( ((LA16_0>=STAR && LA16_0<=SLASH)||LA16_0==PERCENT) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:166:26: ( '*' | '/' | '%' ) unaryExpression
            	    {
            	    set56=(Token)input.LT(1);
            	    set56=(Token)input.LT(1);
            	    if ( (input.LA(1)>=STAR && input.LA(1)<=SLASH)||input.LA(1)==PERCENT ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set56), root_0);
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression608);
            	    unaryExpression57=unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression57.getTree());

            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 19, multiplicativeExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "multiplicativeExpression"

    public static class unaryExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unaryExpression"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:173:1: unaryExpression : ( '-' castExpression -> ^( NEGATION castExpression ) | '~' castExpression | '!' castExpression | castExpression );
    public final BeneratorParser.unaryExpression_return unaryExpression() throws RecognitionException {
        BeneratorParser.unaryExpression_return retval = new BeneratorParser.unaryExpression_return();
        retval.start = input.LT(1);
        int unaryExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal58=null;
        Token char_literal60=null;
        Token char_literal62=null;
        BeneratorParser.castExpression_return castExpression59 = null;

        BeneratorParser.castExpression_return castExpression61 = null;

        BeneratorParser.castExpression_return castExpression63 = null;

        BeneratorParser.castExpression_return castExpression64 = null;


        Object char_literal58_tree=null;
        Object char_literal60_tree=null;
        Object char_literal62_tree=null;
        RewriteRuleTokenStream stream_SUB=new RewriteRuleTokenStream(adaptor,"token SUB");
        RewriteRuleSubtreeStream stream_castExpression=new RewriteRuleSubtreeStream(adaptor,"rule castExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 20) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:174:5: ( '-' castExpression -> ^( NEGATION castExpression ) | '~' castExpression | '!' castExpression | castExpression )
            int alt17=4;
            switch ( input.LA(1) ) {
            case SUB:
                {
                alt17=1;
                }
                break;
            case TILDE:
                {
                alt17=2;
                }
                break;
            case BANG:
                {
                alt17=3;
                }
                break;
            case IDENTIFIER:
            case INTLITERAL:
            case DECIMALLITERAL:
            case STRINGLITERAL:
            case BOOLEANLITERAL:
            case NULL:
            case LPAREN:
            case 66:
                {
                alt17=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;
            }

            switch (alt17) {
                case 1 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:174:9: '-' castExpression
                    {
                    char_literal58=(Token)match(input,SUB,FOLLOW_SUB_in_unaryExpression634); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SUB.add(char_literal58);

                    pushFollow(FOLLOW_castExpression_in_unaryExpression636);
                    castExpression59=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_castExpression.add(castExpression59.getTree());


                    // AST REWRITE
                    // elements: castExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 174:28: -> ^( NEGATION castExpression )
                    {
                        // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:174:31: ^( NEGATION castExpression )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(NEGATION, "NEGATION"), root_1);

                        adaptor.addChild(root_1, stream_castExpression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:175:9: '~' castExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal60=(Token)match(input,TILDE,FOLLOW_TILDE_in_unaryExpression654); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal60_tree = (Object)adaptor.create(char_literal60);
                    root_0 = (Object)adaptor.becomeRoot(char_literal60_tree, root_0);
                    }
                    pushFollow(FOLLOW_castExpression_in_unaryExpression657);
                    castExpression61=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, castExpression61.getTree());

                    }
                    break;
                case 3 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:176:9: '!' castExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal62=(Token)match(input,BANG,FOLLOW_BANG_in_unaryExpression667); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal62_tree = (Object)adaptor.create(char_literal62);
                    root_0 = (Object)adaptor.becomeRoot(char_literal62_tree, root_0);
                    }
                    pushFollow(FOLLOW_castExpression_in_unaryExpression670);
                    castExpression63=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, castExpression63.getTree());

                    }
                    break;
                case 4 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:177:9: castExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_castExpression_in_unaryExpression680);
                    castExpression64=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, castExpression64.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 20, unaryExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "unaryExpression"

    public static class castExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "castExpression"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:180:1: castExpression : ( '(' type ')' postfixExpression -> ^( CAST type postfixExpression ) | postfixExpression );
    public final BeneratorParser.castExpression_return castExpression() throws RecognitionException {
        BeneratorParser.castExpression_return retval = new BeneratorParser.castExpression_return();
        retval.start = input.LT(1);
        int castExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal65=null;
        Token char_literal67=null;
        BeneratorParser.type_return type66 = null;

        BeneratorParser.postfixExpression_return postfixExpression68 = null;

        BeneratorParser.postfixExpression_return postfixExpression69 = null;


        Object char_literal65_tree=null;
        Object char_literal67_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_type=new RewriteRuleSubtreeStream(adaptor,"rule type");
        RewriteRuleSubtreeStream stream_postfixExpression=new RewriteRuleSubtreeStream(adaptor,"rule postfixExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 21) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:181:5: ( '(' type ')' postfixExpression -> ^( CAST type postfixExpression ) | postfixExpression )
            int alt18=2;
            alt18 = dfa18.predict(input);
            switch (alt18) {
                case 1 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:181:9: '(' type ')' postfixExpression
                    {
                    char_literal65=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_castExpression700); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAREN.add(char_literal65);

                    pushFollow(FOLLOW_type_in_castExpression702);
                    type66=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_type.add(type66.getTree());
                    char_literal67=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_castExpression704); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAREN.add(char_literal67);

                    pushFollow(FOLLOW_postfixExpression_in_castExpression706);
                    postfixExpression68=postfixExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_postfixExpression.add(postfixExpression68.getTree());


                    // AST REWRITE
                    // elements: type, postfixExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 181:40: -> ^( CAST type postfixExpression )
                    {
                        // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:181:43: ^( CAST type postfixExpression )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CAST, "CAST"), root_1);

                        adaptor.addChild(root_1, stream_type.nextTree());
                        adaptor.addChild(root_1, stream_postfixExpression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:182:9: postfixExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_postfixExpression_in_castExpression726);
                    postfixExpression69=postfixExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, postfixExpression69.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 21, castExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "castExpression"

    public static class type_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "type"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:185:1: type : qualifiedName -> ^( TYPE qualifiedName ) ;
    public final BeneratorParser.type_return type() throws RecognitionException {
        BeneratorParser.type_return retval = new BeneratorParser.type_return();
        retval.start = input.LT(1);
        int type_StartIndex = input.index();
        Object root_0 = null;

        BeneratorParser.qualifiedName_return qualifiedName70 = null;


        RewriteRuleSubtreeStream stream_qualifiedName=new RewriteRuleSubtreeStream(adaptor,"rule qualifiedName");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 22) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:186:5: ( qualifiedName -> ^( TYPE qualifiedName ) )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:186:9: qualifiedName
            {
            pushFollow(FOLLOW_qualifiedName_in_type745);
            qualifiedName70=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_qualifiedName.add(qualifiedName70.getTree());


            // AST REWRITE
            // elements: qualifiedName
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 186:23: -> ^( TYPE qualifiedName )
            {
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:186:26: ^( TYPE qualifiedName )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TYPE, "TYPE"), root_1);

                adaptor.addChild(root_1, stream_qualifiedName.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 22, type_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "type"

    public static class postfixExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "postfixExpression"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:188:1: postfixExpression : ( primary -> primary ) ( '[' expression ']' -> ^( INDEX $postfixExpression expression ) | '.' IDENTIFIER arguments -> ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments ) | '.' IDENTIFIER -> ^( FIELD $postfixExpression IDENTIFIER ) )* ;
    public final BeneratorParser.postfixExpression_return postfixExpression() throws RecognitionException {
        BeneratorParser.postfixExpression_return retval = new BeneratorParser.postfixExpression_return();
        retval.start = input.LT(1);
        int postfixExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal72=null;
        Token char_literal74=null;
        Token char_literal75=null;
        Token IDENTIFIER76=null;
        Token char_literal78=null;
        Token IDENTIFIER79=null;
        BeneratorParser.primary_return primary71 = null;

        BeneratorParser.expression_return expression73 = null;

        BeneratorParser.arguments_return arguments77 = null;


        Object char_literal72_tree=null;
        Object char_literal74_tree=null;
        Object char_literal75_tree=null;
        Object IDENTIFIER76_tree=null;
        Object char_literal78_tree=null;
        Object IDENTIFIER79_tree=null;
        RewriteRuleTokenStream stream_LBRACKET=new RewriteRuleTokenStream(adaptor,"token LBRACKET");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleTokenStream stream_RBRACKET=new RewriteRuleTokenStream(adaptor,"token RBRACKET");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_arguments=new RewriteRuleSubtreeStream(adaptor,"rule arguments");
        RewriteRuleSubtreeStream stream_primary=new RewriteRuleSubtreeStream(adaptor,"rule primary");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 23) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:189:5: ( ( primary -> primary ) ( '[' expression ']' -> ^( INDEX $postfixExpression expression ) | '.' IDENTIFIER arguments -> ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments ) | '.' IDENTIFIER -> ^( FIELD $postfixExpression IDENTIFIER ) )* )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:189:9: ( primary -> primary ) ( '[' expression ']' -> ^( INDEX $postfixExpression expression ) | '.' IDENTIFIER arguments -> ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments ) | '.' IDENTIFIER -> ^( FIELD $postfixExpression IDENTIFIER ) )*
            {
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:189:9: ( primary -> primary )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:189:10: primary
            {
            pushFollow(FOLLOW_primary_in_postfixExpression768);
            primary71=primary();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_primary.add(primary71.getTree());


            // AST REWRITE
            // elements: primary
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 189:18: -> primary
            {
                adaptor.addChild(root_0, stream_primary.nextTree());

            }

            retval.tree = root_0;}
            }

            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:190:9: ( '[' expression ']' -> ^( INDEX $postfixExpression expression ) | '.' IDENTIFIER arguments -> ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments ) | '.' IDENTIFIER -> ^( FIELD $postfixExpression IDENTIFIER ) )*
            loop19:
            do {
                int alt19=4;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==LBRACKET) ) {
                    alt19=1;
                }
                else if ( (LA19_0==DOT) ) {
                    int LA19_3 = input.LA(2);

                    if ( (LA19_3==IDENTIFIER) ) {
                        int LA19_4 = input.LA(3);

                        if ( (LA19_4==EOF||LA19_4==RPAREN||(LA19_4>=LBRACKET && LA19_4<=RBRACKET)||(LA19_4>=COMMA && LA19_4<=DOT)||(LA19_4>=QUES && LA19_4<=PERCENT)||(LA19_4>=BANGEQ && LA19_4<=LE)) ) {
                            alt19=3;
                        }
                        else if ( (LA19_4==LPAREN) ) {
                            alt19=2;
                        }


                    }


                }


                switch (alt19) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:191:13: '[' expression ']'
            	    {
            	    char_literal72=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_postfixExpression797); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_LBRACKET.add(char_literal72);

            	    pushFollow(FOLLOW_expression_in_postfixExpression799);
            	    expression73=expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_expression.add(expression73.getTree());
            	    char_literal74=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_postfixExpression801); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_RBRACKET.add(char_literal74);



            	    // AST REWRITE
            	    // elements: expression, postfixExpression
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            	    root_0 = (Object)adaptor.nil();
            	    // 191:32: -> ^( INDEX $postfixExpression expression )
            	    {
            	        // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:191:35: ^( INDEX $postfixExpression expression )
            	        {
            	        Object root_1 = (Object)adaptor.nil();
            	        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(INDEX, "INDEX"), root_1);

            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_expression.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;
            	case 2 :
            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:192:13: '.' IDENTIFIER arguments
            	    {
            	    char_literal75=(Token)match(input,DOT,FOLLOW_DOT_in_postfixExpression826); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(char_literal75);

            	    IDENTIFIER76=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_postfixExpression828); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER76);

            	    pushFollow(FOLLOW_arguments_in_postfixExpression830);
            	    arguments77=arguments();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_arguments.add(arguments77.getTree());


            	    // AST REWRITE
            	    // elements: postfixExpression, arguments, IDENTIFIER
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            	    root_0 = (Object)adaptor.nil();
            	    // 192:37: -> ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments )
            	    {
            	        // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:192:40: ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments )
            	        {
            	        Object root_1 = (Object)adaptor.nil();
            	        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SUBINVOCATION, "SUBINVOCATION"), root_1);

            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_IDENTIFIER.nextNode());
            	        adaptor.addChild(root_1, stream_arguments.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;
            	case 3 :
            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:193:13: '.' IDENTIFIER
            	    {
            	    char_literal78=(Token)match(input,DOT,FOLLOW_DOT_in_postfixExpression856); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(char_literal78);

            	    IDENTIFIER79=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_postfixExpression858); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER79);



            	    // AST REWRITE
            	    // elements: IDENTIFIER, postfixExpression
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            	    root_0 = (Object)adaptor.nil();
            	    // 193:28: -> ^( FIELD $postfixExpression IDENTIFIER )
            	    {
            	        // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:193:31: ^( FIELD $postfixExpression IDENTIFIER )
            	        {
            	        Object root_1 = (Object)adaptor.nil();
            	        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD, "FIELD"), root_1);

            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_IDENTIFIER.nextNode());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 23, postfixExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "postfixExpression"

    public static class primary_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "primary"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:197:1: primary : ( '(' expression ')' | literal | creator | qualifiedName arguments -> ^( INVOCATION qualifiedName arguments ) | qualifiedName );
    public final BeneratorParser.primary_return primary() throws RecognitionException {
        BeneratorParser.primary_return retval = new BeneratorParser.primary_return();
        retval.start = input.LT(1);
        int primary_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal80=null;
        Token char_literal82=null;
        BeneratorParser.expression_return expression81 = null;

        BeneratorParser.literal_return literal83 = null;

        BeneratorParser.creator_return creator84 = null;

        BeneratorParser.qualifiedName_return qualifiedName85 = null;

        BeneratorParser.arguments_return arguments86 = null;

        BeneratorParser.qualifiedName_return qualifiedName87 = null;


        Object char_literal80_tree=null;
        Object char_literal82_tree=null;
        RewriteRuleSubtreeStream stream_arguments=new RewriteRuleSubtreeStream(adaptor,"rule arguments");
        RewriteRuleSubtreeStream stream_qualifiedName=new RewriteRuleSubtreeStream(adaptor,"rule qualifiedName");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 24) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:198:5: ( '(' expression ')' | literal | creator | qualifiedName arguments -> ^( INVOCATION qualifiedName arguments ) | qualifiedName )
            int alt20=5;
            switch ( input.LA(1) ) {
            case LPAREN:
                {
                alt20=1;
                }
                break;
            case INTLITERAL:
            case DECIMALLITERAL:
            case STRINGLITERAL:
            case BOOLEANLITERAL:
            case NULL:
                {
                alt20=2;
                }
                break;
            case 66:
                {
                alt20=3;
                }
                break;
            case IDENTIFIER:
                {
                int LA20_4 = input.LA(2);

                if ( (synpred36_Benerator()) ) {
                    alt20=4;
                }
                else if ( (true) ) {
                    alt20=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 4, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:198:9: '(' expression ')'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal80=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_primary900); if (state.failed) return retval;
                    pushFollow(FOLLOW_expression_in_primary903);
                    expression81=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression81.getTree());
                    char_literal82=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_primary905); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:199:9: literal
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_literal_in_primary916);
                    literal83=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal83.getTree());

                    }
                    break;
                case 3 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:200:7: creator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_creator_in_primary924);
                    creator84=creator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, creator84.getTree());

                    }
                    break;
                case 4 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:201:9: qualifiedName arguments
                    {
                    pushFollow(FOLLOW_qualifiedName_in_primary934);
                    qualifiedName85=qualifiedName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_qualifiedName.add(qualifiedName85.getTree());
                    pushFollow(FOLLOW_arguments_in_primary936);
                    arguments86=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arguments.add(arguments86.getTree());


                    // AST REWRITE
                    // elements: arguments, qualifiedName
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 201:33: -> ^( INVOCATION qualifiedName arguments )
                    {
                        // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:201:36: ^( INVOCATION qualifiedName arguments )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(INVOCATION, "INVOCATION"), root_1);

                        adaptor.addChild(root_1, stream_qualifiedName.nextTree());
                        adaptor.addChild(root_1, stream_arguments.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:202:9: qualifiedName
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_qualifiedName_in_primary956);
                    qualifiedName87=qualifiedName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName87.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 24, primary_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "primary"

    public static class creator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "creator"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:205:1: creator : ( 'new' qualifiedName arguments -> ^( CONSTRUCTOR qualifiedName arguments ) | 'new' qualifiedName '[' assignment ( ',' assignment )* ']' -> ^( BEAN qualifiedName ( assignment )* ) );
    public final BeneratorParser.creator_return creator() throws RecognitionException {
        BeneratorParser.creator_return retval = new BeneratorParser.creator_return();
        retval.start = input.LT(1);
        int creator_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal88=null;
        Token string_literal91=null;
        Token char_literal93=null;
        Token char_literal95=null;
        Token char_literal97=null;
        BeneratorParser.qualifiedName_return qualifiedName89 = null;

        BeneratorParser.arguments_return arguments90 = null;

        BeneratorParser.qualifiedName_return qualifiedName92 = null;

        BeneratorParser.assignment_return assignment94 = null;

        BeneratorParser.assignment_return assignment96 = null;


        Object string_literal88_tree=null;
        Object string_literal91_tree=null;
        Object char_literal93_tree=null;
        Object char_literal95_tree=null;
        Object char_literal97_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LBRACKET=new RewriteRuleTokenStream(adaptor,"token LBRACKET");
        RewriteRuleTokenStream stream_RBRACKET=new RewriteRuleTokenStream(adaptor,"token RBRACKET");
        RewriteRuleTokenStream stream_66=new RewriteRuleTokenStream(adaptor,"token 66");
        RewriteRuleSubtreeStream stream_arguments=new RewriteRuleSubtreeStream(adaptor,"rule arguments");
        RewriteRuleSubtreeStream stream_assignment=new RewriteRuleSubtreeStream(adaptor,"rule assignment");
        RewriteRuleSubtreeStream stream_qualifiedName=new RewriteRuleSubtreeStream(adaptor,"rule qualifiedName");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 25) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:206:5: ( 'new' qualifiedName arguments -> ^( CONSTRUCTOR qualifiedName arguments ) | 'new' qualifiedName '[' assignment ( ',' assignment )* ']' -> ^( BEAN qualifiedName ( assignment )* ) )
            int alt22=2;
            alt22 = dfa22.predict(input);
            switch (alt22) {
                case 1 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:206:9: 'new' qualifiedName arguments
                    {
                    string_literal88=(Token)match(input,66,FOLLOW_66_in_creator975); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_66.add(string_literal88);

                    pushFollow(FOLLOW_qualifiedName_in_creator977);
                    qualifiedName89=qualifiedName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_qualifiedName.add(qualifiedName89.getTree());
                    pushFollow(FOLLOW_arguments_in_creator979);
                    arguments90=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arguments.add(arguments90.getTree());


                    // AST REWRITE
                    // elements: qualifiedName, arguments
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 206:39: -> ^( CONSTRUCTOR qualifiedName arguments )
                    {
                        // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:206:42: ^( CONSTRUCTOR qualifiedName arguments )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CONSTRUCTOR, "CONSTRUCTOR"), root_1);

                        adaptor.addChild(root_1, stream_qualifiedName.nextTree());
                        adaptor.addChild(root_1, stream_arguments.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:207:9: 'new' qualifiedName '[' assignment ( ',' assignment )* ']'
                    {
                    string_literal91=(Token)match(input,66,FOLLOW_66_in_creator999); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_66.add(string_literal91);

                    pushFollow(FOLLOW_qualifiedName_in_creator1001);
                    qualifiedName92=qualifiedName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_qualifiedName.add(qualifiedName92.getTree());
                    char_literal93=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_creator1003); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LBRACKET.add(char_literal93);

                    pushFollow(FOLLOW_assignment_in_creator1005);
                    assignment94=assignment();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_assignment.add(assignment94.getTree());
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:207:44: ( ',' assignment )*
                    loop21:
                    do {
                        int alt21=2;
                        int LA21_0 = input.LA(1);

                        if ( (LA21_0==COMMA) ) {
                            alt21=1;
                        }


                        switch (alt21) {
                    	case 1 :
                    	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:207:45: ',' assignment
                    	    {
                    	    char_literal95=(Token)match(input,COMMA,FOLLOW_COMMA_in_creator1008); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(char_literal95);

                    	    pushFollow(FOLLOW_assignment_in_creator1010);
                    	    assignment96=assignment();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_assignment.add(assignment96.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop21;
                        }
                    } while (true);

                    char_literal97=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_creator1014); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RBRACKET.add(char_literal97);



                    // AST REWRITE
                    // elements: qualifiedName, assignment
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 207:66: -> ^( BEAN qualifiedName ( assignment )* )
                    {
                        // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:207:69: ^( BEAN qualifiedName ( assignment )* )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(BEAN, "BEAN"), root_1);

                        adaptor.addChild(root_1, stream_qualifiedName.nextTree());
                        // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:207:90: ( assignment )*
                        while ( stream_assignment.hasNext() ) {
                            adaptor.addChild(root_1, stream_assignment.nextTree());

                        }
                        stream_assignment.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 25, creator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "creator"

    public static class arguments_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "arguments"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:210:1: arguments : '(' ( expression ( ',' expression )* )? ')' -> ^( ARGUMENTS ( expression )* ) ;
    public final BeneratorParser.arguments_return arguments() throws RecognitionException {
        BeneratorParser.arguments_return retval = new BeneratorParser.arguments_return();
        retval.start = input.LT(1);
        int arguments_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal98=null;
        Token char_literal100=null;
        Token char_literal102=null;
        BeneratorParser.expression_return expression99 = null;

        BeneratorParser.expression_return expression101 = null;


        Object char_literal98_tree=null;
        Object char_literal100_tree=null;
        Object char_literal102_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 26) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:211:5: ( '(' ( expression ( ',' expression )* )? ')' -> ^( ARGUMENTS ( expression )* ) )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:211:9: '(' ( expression ( ',' expression )* )? ')'
            {
            char_literal98=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_arguments1044); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(char_literal98);

            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:211:13: ( expression ( ',' expression )* )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( ((LA24_0>=IDENTIFIER && LA24_0<=NULL)||LA24_0==LPAREN||(LA24_0>=BANG && LA24_0<=TILDE)||LA24_0==SUB||LA24_0==66) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:211:14: expression ( ',' expression )*
                    {
                    pushFollow(FOLLOW_expression_in_arguments1047);
                    expression99=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression99.getTree());
                    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:211:25: ( ',' expression )*
                    loop23:
                    do {
                        int alt23=2;
                        int LA23_0 = input.LA(1);

                        if ( (LA23_0==COMMA) ) {
                            alt23=1;
                        }


                        switch (alt23) {
                    	case 1 :
                    	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:211:26: ',' expression
                    	    {
                    	    char_literal100=(Token)match(input,COMMA,FOLLOW_COMMA_in_arguments1050); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(char_literal100);

                    	    pushFollow(FOLLOW_expression_in_arguments1052);
                    	    expression101=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expression.add(expression101.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop23;
                        }
                    } while (true);


                    }
                    break;

            }

            char_literal102=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_arguments1058); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(char_literal102);



            // AST REWRITE
            // elements: expression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 211:49: -> ^( ARGUMENTS ( expression )* )
            {
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:211:52: ^( ARGUMENTS ( expression )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ARGUMENTS, "ARGUMENTS"), root_1);

                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:211:64: ( expression )*
                while ( stream_expression.hasNext() ) {
                    adaptor.addChild(root_1, stream_expression.nextTree());

                }
                stream_expression.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 26, arguments_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "arguments"

    public static class qualifiedName_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "qualifiedName"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:213:1: qualifiedName : IDENTIFIER ( '.' IDENTIFIER )* -> ^( QUALIFIEDNAME ( IDENTIFIER )* ) ;
    public final BeneratorParser.qualifiedName_return qualifiedName() throws RecognitionException {
        BeneratorParser.qualifiedName_return retval = new BeneratorParser.qualifiedName_return();
        retval.start = input.LT(1);
        int qualifiedName_StartIndex = input.index();
        Object root_0 = null;

        Token IDENTIFIER103=null;
        Token char_literal104=null;
        Token IDENTIFIER105=null;

        Object IDENTIFIER103_tree=null;
        Object char_literal104_tree=null;
        Object IDENTIFIER105_tree=null;
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 27) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:214:2: ( IDENTIFIER ( '.' IDENTIFIER )* -> ^( QUALIFIEDNAME ( IDENTIFIER )* ) )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:214:6: IDENTIFIER ( '.' IDENTIFIER )*
            {
            IDENTIFIER103=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedName1082); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER103);

            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:214:17: ( '.' IDENTIFIER )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==DOT) ) {
                    int LA25_2 = input.LA(2);

                    if ( (LA25_2==IDENTIFIER) ) {
                        int LA25_3 = input.LA(3);

                        if ( (synpred41_Benerator()) ) {
                            alt25=1;
                        }


                    }


                }


                switch (alt25) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:214:18: '.' IDENTIFIER
            	    {
            	    char_literal104=(Token)match(input,DOT,FOLLOW_DOT_in_qualifiedName1085); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(char_literal104);

            	    IDENTIFIER105=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedName1087); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER105);


            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);



            // AST REWRITE
            // elements: IDENTIFIER
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 214:35: -> ^( QUALIFIEDNAME ( IDENTIFIER )* )
            {
                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:214:38: ^( QUALIFIEDNAME ( IDENTIFIER )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(QUALIFIEDNAME, "QUALIFIEDNAME"), root_1);

                // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:214:54: ( IDENTIFIER )*
                while ( stream_IDENTIFIER.hasNext() ) {
                    adaptor.addChild(root_1, stream_IDENTIFIER.nextNode());

                }
                stream_IDENTIFIER.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 27, qualifiedName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "qualifiedName"

    public static class literal_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "literal"
    // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:217:1: literal : ( INTLITERAL | DECIMALLITERAL | STRINGLITERAL | BOOLEANLITERAL | NULL );
    public final BeneratorParser.literal_return literal() throws RecognitionException {
        BeneratorParser.literal_return retval = new BeneratorParser.literal_return();
        retval.start = input.LT(1);
        int literal_StartIndex = input.index();
        Object root_0 = null;

        Token set106=null;

        Object set106_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 28) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:218:5: ( INTLITERAL | DECIMALLITERAL | STRINGLITERAL | BOOLEANLITERAL | NULL )
            // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:
            {
            root_0 = (Object)adaptor.nil();

            set106=(Token)input.LT(1);
            if ( (input.LA(1)>=INTLITERAL && input.LA(1)<=NULL) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set106));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 28, literal_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "literal"

    // $ANTLR start synpred36_Benerator
    public final void synpred36_Benerator_fragment() throws RecognitionException {   
        // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:201:9: ( qualifiedName arguments )
        // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:201:9: qualifiedName arguments
        {
        pushFollow(FOLLOW_qualifiedName_in_synpred36_Benerator934);
        qualifiedName();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_arguments_in_synpred36_Benerator936);
        arguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred36_Benerator

    // $ANTLR start synpred41_Benerator
    public final void synpred41_Benerator_fragment() throws RecognitionException {   
        // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:214:18: ( '.' IDENTIFIER )
        // /Users/volker/Documents/databene/benerator/src/main/resources/org/databene/benerator/script/Benerator.g:214:18: '.' IDENTIFIER
        {
        match(input,DOT,FOLLOW_DOT_in_synpred41_Benerator1085); if (state.failed) return ;
        match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred41_Benerator1087); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred41_Benerator

    // Delegated rules

    public final boolean synpred41_Benerator() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred41_Benerator_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred36_Benerator() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred36_Benerator_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA18 dfa18 = new DFA18(this);
    protected DFA22 dfa22 = new DFA22(this);
    static final String DFA18_eotS =
        "\10\uffff";
    static final String DFA18_eofS =
        "\5\uffff\1\2\2\uffff";
    static final String DFA18_minS =
        "\2\4\1\uffff\1\35\2\4\1\35\1\uffff";
    static final String DFA18_maxS =
        "\2\102\1\uffff\1\76\1\4\1\102\1\76\1\uffff";
    static final String DFA18_acceptS =
        "\2\uffff\1\2\4\uffff\1\1";
    static final String DFA18_specialS =
        "\10\uffff}>";
    static final String[] DFA18_transitionS = {
            "\6\2\23\uffff\1\1\44\uffff\1\2",
            "\1\3\5\2\23\uffff\1\2\11\uffff\2\2\6\uffff\1\2\22\uffff\1\2",
            "",
            "\1\2\1\5\2\uffff\1\2\3\uffff\1\4\3\uffff\1\2\1\uffff\13\2\1"+
            "\uffff\10\2",
            "\1\6",
            "\6\7\23\uffff\1\7\1\2\2\uffff\2\2\1\uffff\2\2\3\uffff\15\2"+
            "\1\uffff\10\2\3\uffff\1\7",
            "\1\2\1\5\2\uffff\1\2\3\uffff\1\4\3\uffff\1\2\1\uffff\13\2\1"+
            "\uffff\10\2",
            ""
    };

    static final short[] DFA18_eot = DFA.unpackEncodedString(DFA18_eotS);
    static final short[] DFA18_eof = DFA.unpackEncodedString(DFA18_eofS);
    static final char[] DFA18_min = DFA.unpackEncodedStringToUnsignedChars(DFA18_minS);
    static final char[] DFA18_max = DFA.unpackEncodedStringToUnsignedChars(DFA18_maxS);
    static final short[] DFA18_accept = DFA.unpackEncodedString(DFA18_acceptS);
    static final short[] DFA18_special = DFA.unpackEncodedString(DFA18_specialS);
    static final short[][] DFA18_transition;

    static {
        int numStates = DFA18_transitionS.length;
        DFA18_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA18_transition[i] = DFA.unpackEncodedString(DFA18_transitionS[i]);
        }
    }

    class DFA18 extends DFA {

        public DFA18(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 18;
            this.eot = DFA18_eot;
            this.eof = DFA18_eof;
            this.min = DFA18_min;
            this.max = DFA18_max;
            this.accept = DFA18_accept;
            this.special = DFA18_special;
            this.transition = DFA18_transition;
        }
        public String getDescription() {
            return "180:1: castExpression : ( '(' type ')' postfixExpression -> ^( CAST type postfixExpression ) | postfixExpression );";
        }
    }
    static final String DFA22_eotS =
        "\7\uffff";
    static final String DFA22_eofS =
        "\7\uffff";
    static final String DFA22_minS =
        "\1\102\1\4\1\35\1\4\2\uffff\1\35";
    static final String DFA22_maxS =
        "\1\102\1\4\1\45\1\4\2\uffff\1\45";
    static final String DFA22_acceptS =
        "\4\uffff\1\2\1\1\1\uffff";
    static final String DFA22_specialS =
        "\7\uffff}>";
    static final String[] DFA22_transitionS = {
            "\1\1",
            "\1\2",
            "\1\5\3\uffff\1\4\3\uffff\1\3",
            "\1\6",
            "",
            "",
            "\1\5\3\uffff\1\4\3\uffff\1\3"
    };

    static final short[] DFA22_eot = DFA.unpackEncodedString(DFA22_eotS);
    static final short[] DFA22_eof = DFA.unpackEncodedString(DFA22_eofS);
    static final char[] DFA22_min = DFA.unpackEncodedStringToUnsignedChars(DFA22_minS);
    static final char[] DFA22_max = DFA.unpackEncodedStringToUnsignedChars(DFA22_maxS);
    static final short[] DFA22_accept = DFA.unpackEncodedString(DFA22_acceptS);
    static final short[] DFA22_special = DFA.unpackEncodedString(DFA22_specialS);
    static final short[][] DFA22_transition;

    static {
        int numStates = DFA22_transitionS.length;
        DFA22_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA22_transition[i] = DFA.unpackEncodedString(DFA22_transitionS[i]);
        }
    }

    class DFA22 extends DFA {

        public DFA22(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 22;
            this.eot = DFA22_eot;
            this.eof = DFA22_eof;
            this.min = DFA22_min;
            this.max = DFA22_max;
            this.accept = DFA22_accept;
            this.special = DFA22_special;
            this.transition = DFA22_transition;
        }
        public String getDescription() {
            return "205:1: creator : ( 'new' qualifiedName arguments -> ^( CONSTRUCTOR qualifiedName arguments ) | 'new' qualifiedName '[' assignment ( ',' assignment )* ']' -> ^( BEAN qualifiedName ( assignment )* ) );";
        }
    }
 

    public static final BitSet FOLLOW_weightedLiteral_in_weightedLiteralList90 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_COMMA_in_weightedLiteralList93 = new BitSet(new long[]{0x00000000000003E0L});
    public static final BitSet FOLLOW_weightedLiteral_in_weightedLiteralList96 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_literal_in_weightedLiteral113 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_CARET_in_weightedLiteral116 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_weightedLiteral119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_transition_in_transitionList135 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_COMMA_in_transitionList138 = new BitSet(new long[]{0x00000000000003E0L});
    public static final BitSet FOLLOW_transition_in_transitionList141 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_literal_in_transition157 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_ARROW_in_transition159 = new BitSet(new long[]{0x00000000000003E0L});
    public static final BitSet FOLLOW_literal_in_transition162 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_CARET_in_transition165 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_transition168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_assignment184 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_EQ_in_assignment186 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_assignment189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_beanSpec_in_beanSpecList203 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_COMMA_in_beanSpecList206 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_beanSpec_in_beanSpecList209 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_expression_in_beanSpec225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression268 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_QUES_in_conditionalExpression271 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression274 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_COLON_in_conditionalExpression276 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalExpression279 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression305 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_BARBAR_in_conditionalOrExpression308 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression311 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression334 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_AMPAMP_in_conditionalAndExpression337 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression340 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression362 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_BAR_in_inclusiveOrExpression365 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression368 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression389 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_CARET_in_exclusiveOrExpression392 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression395 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression416 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_AMP_in_andExpression419 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression422 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression444 = new BitSet(new long[]{0x0080080000000002L});
    public static final BitSet FOLLOW_set_in_equalityExpression447 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression456 = new BitSet(new long[]{0x0080080000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression478 = new BitSet(new long[]{0x7100000000000002L});
    public static final BitSet FOLLOW_set_in_relationalExpression481 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression498 = new BitSet(new long[]{0x7100000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression520 = new BitSet(new long[]{0x0E00000000000002L});
    public static final BitSet FOLLOW_set_in_shiftExpression523 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression536 = new BitSet(new long[]{0x0E00000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression558 = new BitSet(new long[]{0x0000C00000000002L});
    public static final BitSet FOLLOW_set_in_additiveExpression561 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression570 = new BitSet(new long[]{0x0000C00000000002L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression592 = new BitSet(new long[]{0x0023000000000002L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression595 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression608 = new BitSet(new long[]{0x0023000000000002L});
    public static final BitSet FOLLOW_SUB_in_unaryExpression634 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpression636 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_unaryExpression654 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpression657 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_unaryExpression667 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpression670 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpression680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_castExpression700 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_type_in_castExpression702 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_RPAREN_in_castExpression704 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_postfixExpression_in_castExpression706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_postfixExpression_in_castExpression726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_type745 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_postfixExpression768 = new BitSet(new long[]{0x0000002200000002L});
    public static final BitSet FOLLOW_LBRACKET_in_postfixExpression797 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_postfixExpression799 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_RBRACKET_in_postfixExpression801 = new BitSet(new long[]{0x0000002200000002L});
    public static final BitSet FOLLOW_DOT_in_postfixExpression826 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_postfixExpression828 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_arguments_in_postfixExpression830 = new BitSet(new long[]{0x0000002200000002L});
    public static final BitSet FOLLOW_DOT_in_postfixExpression856 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_postfixExpression858 = new BitSet(new long[]{0x0000002200000002L});
    public static final BitSet FOLLOW_LPAREN_in_primary900 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_primary903 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_RPAREN_in_primary905 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_creator_in_primary924 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_primary934 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_arguments_in_primary936 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_primary956 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_creator975 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_creator977 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_arguments_in_creator979 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_creator999 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_creator1001 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_LBRACKET_in_creator1003 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_assignment_in_creator1005 = new BitSet(new long[]{0x0000001400000000L});
    public static final BitSet FOLLOW_COMMA_in_creator1008 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_assignment_in_creator1010 = new BitSet(new long[]{0x0000001400000000L});
    public static final BitSet FOLLOW_RBRACKET_in_creator1014 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_arguments1044 = new BitSet(new long[]{0x00008180600003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_arguments1047 = new BitSet(new long[]{0x0000001040000000L});
    public static final BitSet FOLLOW_COMMA_in_arguments1050 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_arguments1052 = new BitSet(new long[]{0x0000001040000000L});
    public static final BitSet FOLLOW_RPAREN_in_arguments1058 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedName1082 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_DOT_in_qualifiedName1085 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedName1087 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_set_in_literal0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_synpred36_Benerator934 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_arguments_in_synpred36_Benerator936 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred41_Benerator1085 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_synpred41_Benerator1087 = new BitSet(new long[]{0x0000000000000002L});

}