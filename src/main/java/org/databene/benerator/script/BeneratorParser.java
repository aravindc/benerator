// $ANTLR 3.2 Sep 23, 2009 12:02:23 Benerator.g 2009-10-09 19:35:09

	package org.databene.benerator.script;


import org.antlr.runtime.*;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

@SuppressWarnings({"cast", "unused", "null"})
public class BeneratorParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "IDENTIFIER", "INTLITERAL", "DECIMALLITERAL", "STRINGLITERAL", "BOOLEANLITERAL", "NULL", "TYPE", "NEGATION", "INDEX", "FIELD", "ARGUMENTS", "CAST", "CONSTRUCTOR", "INVOCATION", "SUBINVOCATION", "QUALIFIEDNAME", "BEAN", "HexPrefix", "HexDigit", "Exponent", "EscapeSequence", "WS", "COMMENT", "LINE_COMMENT", "LPAREN", "RPAREN", "LBRACE", "RBRACE", "LBRACKET", "RBRACKET", "SEMI", "COMMA", "DOT", "EQ", "BANG", "TILDE", "QUES", "COLON", "EQEQ", "AMPAMP", "BARBAR", "PLUS", "SUB", "STAR", "SLASH", "AMP", "BAR", "CARET", "PERCENT", "MONKEYS_AT", "BANGEQ", "GT", "SHIFT_RIGHT", "SHIFT_RIGHT2", "SHIFT_LEFT", "GE", "LT", "LE", "IdentifierStart", "IdentifierPart", "'new'"
    };
    public static final int SHIFT_LEFT=58;
    public static final int INDEX=12;
    public static final int COMMA=35;
    public static final int TYPE=10;
    public static final int PERCENT=52;
    public static final int BEAN=20;
    public static final int HexDigit=22;
    public static final int BANG=38;
    public static final int CAST=15;
    public static final int LBRACKET=32;
    public static final int SHIFT_RIGHT2=57;
    public static final int BANGEQ=54;
    public static final int TILDE=39;
    public static final int LBRACE=30;
    public static final int AMPAMP=43;
    public static final int DOT=36;
    public static final int RBRACE=31;
    public static final int INTLITERAL=5;
    public static final int EscapeSequence=24;
    public static final int LE=61;
    public static final int RBRACKET=33;
    public static final int AMP=49;
    public static final int STRINGLITERAL=7;
    public static final int RPAREN=29;
    public static final int LPAREN=28;
    public static final int INVOCATION=17;
    public static final int PLUS=45;
    public static final int IdentifierPart=63;
    public static final int Exponent=23;
    public static final int ARGUMENTS=14;
    public static final int SLASH=48;
    public static final int NEGATION=11;
    public static final int WS=25;
    public static final int EQ=37;
    public static final int T__64=64;
    public static final int QUALIFIEDNAME=19;
    public static final int LT=60;
    public static final int GT=55;
    public static final int HexPrefix=21;
    public static final int COMMENT=26;
    public static final int SUBINVOCATION=18;
    public static final int CARET=51;
    public static final int LINE_COMMENT=27;
    public static final int EQEQ=42;
    public static final int BARBAR=44;
    public static final int FIELD=13;
    public static final int CONSTRUCTOR=16;
    public static final int SEMI=34;
    public static final int GE=59;
    public static final int IdentifierStart=62;
    public static final int BAR=50;
    public static final int SHIFT_RIGHT=56;
    public static final int EOF=-1;
    public static final int NULL=9;
    public static final int BOOLEANLITERAL=8;
    public static final int DECIMALLITERAL=6;
    public static final int QUES=40;
    public static final int COLON=41;
    public static final int MONKEYS_AT=53;
    public static final int STAR=47;
    public static final int IDENTIFIER=4;
    public static final int SUB=46;

    // delegates
    // delegators


        public BeneratorParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public BeneratorParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
            this.state.ruleMemo = new HashMap[65+1];
             
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    @Override
    public String[] getTokenNames() { return BeneratorParser.tokenNames; }
    @Override
    public String getGrammarFileName() { return "Benerator.g"; }


    protected void mismatch(IntStream input, int ttype, BitSet follow)
      throws RecognitionException
    {
      throw new MismatchedTokenException(ttype, input);
    }

    @Override
    public Object recoverFromMismatchedSet(IntStream input, RecognitionException e, BitSet follow)
      throws RecognitionException
    {
      throw e;
    }


    public static class assignment_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "assignment"
    // Benerator.g:96:1: assignment : IDENTIFIER '=' expression ;
    public final BeneratorParser.assignment_return assignment() throws RecognitionException {
        BeneratorParser.assignment_return retval = new BeneratorParser.assignment_return();
        retval.start = input.LT(1);
        int assignment_StartIndex = input.index();
        Object root_0 = null;

        Token IDENTIFIER1=null;
        Token char_literal2=null;
        BeneratorParser.expression_return expression3 = null;


        Object IDENTIFIER1_tree=null;
        Object char_literal2_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 1) ) { return retval; }
            // Benerator.g:101:5: ( IDENTIFIER '=' expression )
            // Benerator.g:101:9: IDENTIFIER '=' expression
            {
            root_0 = (Object)adaptor.nil();

            IDENTIFIER1=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_assignment90); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER1_tree = (Object)adaptor.create(IDENTIFIER1);
            adaptor.addChild(root_0, IDENTIFIER1_tree);
            }
            char_literal2=(Token)match(input,EQ,FOLLOW_EQ_in_assignment92); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal2_tree = (Object)adaptor.create(char_literal2);
            root_0 = (Object)adaptor.becomeRoot(char_literal2_tree, root_0);
            }
            pushFollow(FOLLOW_expression_in_assignment95);
            expression3=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression3.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 1, assignment_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "assignment"

    public static class beanSpecList_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "beanSpecList"
    // Benerator.g:103:1: beanSpecList : beanSpec ( ',' beanSpec )* ;
    public final BeneratorParser.beanSpecList_return beanSpecList() throws RecognitionException {
        BeneratorParser.beanSpecList_return retval = new BeneratorParser.beanSpecList_return();
        retval.start = input.LT(1);
        int beanSpecList_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal5=null;
        BeneratorParser.beanSpec_return beanSpec4 = null;

        BeneratorParser.beanSpec_return beanSpec6 = null;


        Object char_literal5_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 2) ) { return retval; }
            // Benerator.g:104:5: ( beanSpec ( ',' beanSpec )* )
            // Benerator.g:104:9: beanSpec ( ',' beanSpec )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_beanSpec_in_beanSpecList109);
            beanSpec4=beanSpec();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, beanSpec4.getTree());
            // Benerator.g:104:18: ( ',' beanSpec )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==COMMA) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // Benerator.g:104:19: ',' beanSpec
            	    {
            	    char_literal5=(Token)match(input,COMMA,FOLLOW_COMMA_in_beanSpecList112); if (state.failed) return retval;
            	    pushFollow(FOLLOW_beanSpec_in_beanSpecList115);
            	    beanSpec6=beanSpec();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, beanSpec6.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 2, beanSpecList_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "beanSpecList"

    public static class beanSpec_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "beanSpec"
    // Benerator.g:106:1: beanSpec : expression ;
    public final BeneratorParser.beanSpec_return beanSpec() throws RecognitionException {
        BeneratorParser.beanSpec_return retval = new BeneratorParser.beanSpec_return();
        retval.start = input.LT(1);
        int beanSpec_StartIndex = input.index();
        Object root_0 = null;

        BeneratorParser.expression_return expression7 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 3) ) { return retval; }
            // Benerator.g:107:5: ( expression )
            // Benerator.g:107:9: expression
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression_in_beanSpec132);
            expression7=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression7.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 3, beanSpec_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "beanSpec"

    public static class expression_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "expression"
    // Benerator.g:109:1: expression : conditionalExpression ;
    public final BeneratorParser.expression_return expression() throws RecognitionException {
        BeneratorParser.expression_return retval = new BeneratorParser.expression_return();
        retval.start = input.LT(1);
        int expression_StartIndex = input.index();
        Object root_0 = null;

        BeneratorParser.conditionalExpression_return conditionalExpression8 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 4) ) { return retval; }
            // Benerator.g:110:5: ( conditionalExpression )
            // Benerator.g:110:9: conditionalExpression
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalExpression_in_expression147);
            conditionalExpression8=conditionalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression8.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 4, expression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "expression"

    public static class conditionalExpression_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "conditionalExpression"
    // Benerator.g:113:1: conditionalExpression : conditionalOrExpression ( '?' expression ':' conditionalExpression )? ;
    public final BeneratorParser.conditionalExpression_return conditionalExpression() throws RecognitionException {
        BeneratorParser.conditionalExpression_return retval = new BeneratorParser.conditionalExpression_return();
        retval.start = input.LT(1);
        int conditionalExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal10=null;
        Token char_literal12=null;
        BeneratorParser.conditionalOrExpression_return conditionalOrExpression9 = null;

        BeneratorParser.expression_return expression11 = null;

        BeneratorParser.conditionalExpression_return conditionalExpression13 = null;


        Object char_literal10_tree=null;
        Object char_literal12_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 5) ) { return retval; }
            // Benerator.g:114:5: ( conditionalOrExpression ( '?' expression ':' conditionalExpression )? )
            // Benerator.g:114:9: conditionalOrExpression ( '?' expression ':' conditionalExpression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression167);
            conditionalOrExpression9=conditionalOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalOrExpression9.getTree());
            // Benerator.g:114:33: ( '?' expression ':' conditionalExpression )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==QUES) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // Benerator.g:114:34: '?' expression ':' conditionalExpression
                    {
                    char_literal10=(Token)match(input,QUES,FOLLOW_QUES_in_conditionalExpression170); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal10_tree = (Object)adaptor.create(char_literal10);
                    root_0 = (Object)adaptor.becomeRoot(char_literal10_tree, root_0);
                    }
                    pushFollow(FOLLOW_expression_in_conditionalExpression173);
                    expression11=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression11.getTree());
                    char_literal12=(Token)match(input,COLON,FOLLOW_COLON_in_conditionalExpression175); if (state.failed) return retval;
                    pushFollow(FOLLOW_conditionalExpression_in_conditionalExpression178);
                    conditionalExpression13=conditionalExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression13.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 5, conditionalExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "conditionalExpression"

    public static class conditionalOrExpression_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "conditionalOrExpression"
    // Benerator.g:117:1: conditionalOrExpression : conditionalAndExpression ( '||' conditionalAndExpression )* ;
    public final BeneratorParser.conditionalOrExpression_return conditionalOrExpression() throws RecognitionException {
        BeneratorParser.conditionalOrExpression_return retval = new BeneratorParser.conditionalOrExpression_return();
        retval.start = input.LT(1);
        int conditionalOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal15=null;
        BeneratorParser.conditionalAndExpression_return conditionalAndExpression14 = null;

        BeneratorParser.conditionalAndExpression_return conditionalAndExpression16 = null;


        Object string_literal15_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return retval; }
            // Benerator.g:118:5: ( conditionalAndExpression ( '||' conditionalAndExpression )* )
            // Benerator.g:118:9: conditionalAndExpression ( '||' conditionalAndExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression204);
            conditionalAndExpression14=conditionalAndExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression14.getTree());
            // Benerator.g:118:34: ( '||' conditionalAndExpression )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==BARBAR) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // Benerator.g:118:35: '||' conditionalAndExpression
            	    {
            	    string_literal15=(Token)match(input,BARBAR,FOLLOW_BARBAR_in_conditionalOrExpression207); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal15_tree = (Object)adaptor.create(string_literal15);
            	    root_0 = (Object)adaptor.becomeRoot(string_literal15_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression210);
            	    conditionalAndExpression16=conditionalAndExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression16.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 6, conditionalOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "conditionalOrExpression"

    public static class conditionalAndExpression_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "conditionalAndExpression"
    // Benerator.g:121:1: conditionalAndExpression : inclusiveOrExpression ( '&&' inclusiveOrExpression )* ;
    public final BeneratorParser.conditionalAndExpression_return conditionalAndExpression() throws RecognitionException {
        BeneratorParser.conditionalAndExpression_return retval = new BeneratorParser.conditionalAndExpression_return();
        retval.start = input.LT(1);
        int conditionalAndExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal18=null;
        BeneratorParser.inclusiveOrExpression_return inclusiveOrExpression17 = null;

        BeneratorParser.inclusiveOrExpression_return inclusiveOrExpression19 = null;


        Object string_literal18_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 7) ) { return retval; }
            // Benerator.g:122:5: ( inclusiveOrExpression ( '&&' inclusiveOrExpression )* )
            // Benerator.g:122:9: inclusiveOrExpression ( '&&' inclusiveOrExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression233);
            inclusiveOrExpression17=inclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression17.getTree());
            // Benerator.g:122:31: ( '&&' inclusiveOrExpression )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==AMPAMP) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // Benerator.g:122:32: '&&' inclusiveOrExpression
            	    {
            	    string_literal18=(Token)match(input,AMPAMP,FOLLOW_AMPAMP_in_conditionalAndExpression236); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal18_tree = (Object)adaptor.create(string_literal18);
            	    root_0 = (Object)adaptor.becomeRoot(string_literal18_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression239);
            	    inclusiveOrExpression19=inclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression19.getTree());

            	    }
            	    break;

            	default :
            	    break loop4;
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
            if ( state.backtracking>0 ) { memoize(input, 7, conditionalAndExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "conditionalAndExpression"

    public static class inclusiveOrExpression_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "inclusiveOrExpression"
    // Benerator.g:125:1: inclusiveOrExpression : exclusiveOrExpression ( '|' exclusiveOrExpression )* ;
    public final BeneratorParser.inclusiveOrExpression_return inclusiveOrExpression() throws RecognitionException {
        BeneratorParser.inclusiveOrExpression_return retval = new BeneratorParser.inclusiveOrExpression_return();
        retval.start = input.LT(1);
        int inclusiveOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal21=null;
        BeneratorParser.exclusiveOrExpression_return exclusiveOrExpression20 = null;

        BeneratorParser.exclusiveOrExpression_return exclusiveOrExpression22 = null;


        Object char_literal21_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 8) ) { return retval; }
            // Benerator.g:126:5: ( exclusiveOrExpression ( '|' exclusiveOrExpression )* )
            // Benerator.g:126:9: exclusiveOrExpression ( '|' exclusiveOrExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression261);
            exclusiveOrExpression20=exclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression20.getTree());
            // Benerator.g:126:31: ( '|' exclusiveOrExpression )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==BAR) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // Benerator.g:126:32: '|' exclusiveOrExpression
            	    {
            	    char_literal21=(Token)match(input,BAR,FOLLOW_BAR_in_inclusiveOrExpression264); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal21_tree = (Object)adaptor.create(char_literal21);
            	    root_0 = (Object)adaptor.becomeRoot(char_literal21_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression267);
            	    exclusiveOrExpression22=exclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression22.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 8, inclusiveOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "inclusiveOrExpression"

    public static class exclusiveOrExpression_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "exclusiveOrExpression"
    // Benerator.g:129:1: exclusiveOrExpression : andExpression ( '^' andExpression )* ;
    public final BeneratorParser.exclusiveOrExpression_return exclusiveOrExpression() throws RecognitionException {
        BeneratorParser.exclusiveOrExpression_return retval = new BeneratorParser.exclusiveOrExpression_return();
        retval.start = input.LT(1);
        int exclusiveOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal24=null;
        BeneratorParser.andExpression_return andExpression23 = null;

        BeneratorParser.andExpression_return andExpression25 = null;


        Object char_literal24_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return retval; }
            // Benerator.g:130:5: ( andExpression ( '^' andExpression )* )
            // Benerator.g:130:9: andExpression ( '^' andExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression288);
            andExpression23=andExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression23.getTree());
            // Benerator.g:130:23: ( '^' andExpression )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==CARET) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // Benerator.g:130:24: '^' andExpression
            	    {
            	    char_literal24=(Token)match(input,CARET,FOLLOW_CARET_in_exclusiveOrExpression291); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal24_tree = (Object)adaptor.create(char_literal24);
            	    root_0 = (Object)adaptor.becomeRoot(char_literal24_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression294);
            	    andExpression25=andExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression25.getTree());

            	    }
            	    break;

            	default :
            	    break loop6;
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
            if ( state.backtracking>0 ) { memoize(input, 9, exclusiveOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "exclusiveOrExpression"

    public static class andExpression_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "andExpression"
    // Benerator.g:133:1: andExpression : equalityExpression ( '&' equalityExpression )* ;
    public final BeneratorParser.andExpression_return andExpression() throws RecognitionException {
        BeneratorParser.andExpression_return retval = new BeneratorParser.andExpression_return();
        retval.start = input.LT(1);
        int andExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal27=null;
        BeneratorParser.equalityExpression_return equalityExpression26 = null;

        BeneratorParser.equalityExpression_return equalityExpression28 = null;


        Object char_literal27_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 10) ) { return retval; }
            // Benerator.g:134:5: ( equalityExpression ( '&' equalityExpression )* )
            // Benerator.g:134:9: equalityExpression ( '&' equalityExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_equalityExpression_in_andExpression315);
            equalityExpression26=equalityExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, equalityExpression26.getTree());
            // Benerator.g:134:28: ( '&' equalityExpression )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==AMP) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // Benerator.g:134:29: '&' equalityExpression
            	    {
            	    char_literal27=(Token)match(input,AMP,FOLLOW_AMP_in_andExpression318); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal27_tree = (Object)adaptor.create(char_literal27);
            	    root_0 = (Object)adaptor.becomeRoot(char_literal27_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_equalityExpression_in_andExpression321);
            	    equalityExpression28=equalityExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, equalityExpression28.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 10, andExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "andExpression"

    public static class equalityExpression_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "equalityExpression"
    // Benerator.g:137:1: equalityExpression : relationalExpression ( ( '==' | '!=' ) relationalExpression )* ;
    public final BeneratorParser.equalityExpression_return equalityExpression() throws RecognitionException {
        BeneratorParser.equalityExpression_return retval = new BeneratorParser.equalityExpression_return();
        retval.start = input.LT(1);
        int equalityExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set30=null;
        BeneratorParser.relationalExpression_return relationalExpression29 = null;

        BeneratorParser.relationalExpression_return relationalExpression31 = null;


        Object set30_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 11) ) { return retval; }
            // Benerator.g:138:5: ( relationalExpression ( ( '==' | '!=' ) relationalExpression )* )
            // Benerator.g:138:9: relationalExpression ( ( '==' | '!=' ) relationalExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_relationalExpression_in_equalityExpression343);
            relationalExpression29=relationalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalExpression29.getTree());
            // Benerator.g:138:30: ( ( '==' | '!=' ) relationalExpression )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==EQEQ||LA8_0==BANGEQ) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // Benerator.g:138:31: ( '==' | '!=' ) relationalExpression
            	    {
            	    set30=(Token)input.LT(1);
            	    set30=(Token)input.LT(1);
            	    if ( input.LA(1)==EQEQ||input.LA(1)==BANGEQ ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set30), root_0);
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_relationalExpression_in_equalityExpression355);
            	    relationalExpression31=relationalExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalExpression31.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 11, equalityExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "equalityExpression"

    public static class relationalExpression_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "relationalExpression"
    // Benerator.g:141:1: relationalExpression : shiftExpression ( ( '<=' | '>=' | '<' | '>' ) shiftExpression )* ;
    public final BeneratorParser.relationalExpression_return relationalExpression() throws RecognitionException {
        BeneratorParser.relationalExpression_return retval = new BeneratorParser.relationalExpression_return();
        retval.start = input.LT(1);
        int relationalExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set33=null;
        BeneratorParser.shiftExpression_return shiftExpression32 = null;

        BeneratorParser.shiftExpression_return shiftExpression34 = null;


        Object set33_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 12) ) { return retval; }
            // Benerator.g:142:5: ( shiftExpression ( ( '<=' | '>=' | '<' | '>' ) shiftExpression )* )
            // Benerator.g:142:9: shiftExpression ( ( '<=' | '>=' | '<' | '>' ) shiftExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_shiftExpression_in_relationalExpression377);
            shiftExpression32=shiftExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpression32.getTree());
            // Benerator.g:142:25: ( ( '<=' | '>=' | '<' | '>' ) shiftExpression )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==GT||(LA9_0>=GE && LA9_0<=LE)) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // Benerator.g:142:26: ( '<=' | '>=' | '<' | '>' ) shiftExpression
            	    {
            	    set33=(Token)input.LT(1);
            	    set33=(Token)input.LT(1);
            	    if ( input.LA(1)==GT||(input.LA(1)>=GE && input.LA(1)<=LE) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set33), root_0);
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_shiftExpression_in_relationalExpression397);
            	    shiftExpression34=shiftExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpression34.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 12, relationalExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "relationalExpression"

    public static class shiftExpression_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "shiftExpression"
    // Benerator.g:145:1: shiftExpression : additiveExpression ( ( '<<' | '>>>' | '>>' ) additiveExpression )* ;
    public final BeneratorParser.shiftExpression_return shiftExpression() throws RecognitionException {
        BeneratorParser.shiftExpression_return retval = new BeneratorParser.shiftExpression_return();
        retval.start = input.LT(1);
        int shiftExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set36=null;
        BeneratorParser.additiveExpression_return additiveExpression35 = null;

        BeneratorParser.additiveExpression_return additiveExpression37 = null;


        Object set36_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 13) ) { return retval; }
            // Benerator.g:146:5: ( additiveExpression ( ( '<<' | '>>>' | '>>' ) additiveExpression )* )
            // Benerator.g:146:9: additiveExpression ( ( '<<' | '>>>' | '>>' ) additiveExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_additiveExpression_in_shiftExpression419);
            additiveExpression35=additiveExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression35.getTree());
            // Benerator.g:146:28: ( ( '<<' | '>>>' | '>>' ) additiveExpression )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( ((LA10_0>=SHIFT_RIGHT && LA10_0<=SHIFT_LEFT)) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // Benerator.g:146:29: ( '<<' | '>>>' | '>>' ) additiveExpression
            	    {
            	    set36=(Token)input.LT(1);
            	    set36=(Token)input.LT(1);
            	    if ( (input.LA(1)>=SHIFT_RIGHT && input.LA(1)<=SHIFT_LEFT) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set36), root_0);
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression435);
            	    additiveExpression37=additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression37.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 13, shiftExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "shiftExpression"

    public static class additiveExpression_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "additiveExpression"
    // Benerator.g:149:1: additiveExpression : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* ;
    public final BeneratorParser.additiveExpression_return additiveExpression() throws RecognitionException {
        BeneratorParser.additiveExpression_return retval = new BeneratorParser.additiveExpression_return();
        retval.start = input.LT(1);
        int additiveExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set39=null;
        BeneratorParser.multiplicativeExpression_return multiplicativeExpression38 = null;

        BeneratorParser.multiplicativeExpression_return multiplicativeExpression40 = null;


        Object set39_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 14) ) { return retval; }
            // Benerator.g:150:5: ( multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* )
            // Benerator.g:150:9: multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression457);
            multiplicativeExpression38=multiplicativeExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression38.getTree());
            // Benerator.g:150:34: ( ( '+' | '-' ) multiplicativeExpression )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( ((LA11_0>=PLUS && LA11_0<=SUB)) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // Benerator.g:150:35: ( '+' | '-' ) multiplicativeExpression
            	    {
            	    set39=(Token)input.LT(1);
            	    set39=(Token)input.LT(1);
            	    if ( (input.LA(1)>=PLUS && input.LA(1)<=SUB) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set39), root_0);
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression469);
            	    multiplicativeExpression40=multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression40.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 14, additiveExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "additiveExpression"

    public static class multiplicativeExpression_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "multiplicativeExpression"
    // Benerator.g:153:1: multiplicativeExpression : unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* ;
    public final BeneratorParser.multiplicativeExpression_return multiplicativeExpression() throws RecognitionException {
        BeneratorParser.multiplicativeExpression_return retval = new BeneratorParser.multiplicativeExpression_return();
        retval.start = input.LT(1);
        int multiplicativeExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set42=null;
        BeneratorParser.unaryExpression_return unaryExpression41 = null;

        BeneratorParser.unaryExpression_return unaryExpression43 = null;


        Object set42_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 15) ) { return retval; }
            // Benerator.g:154:5: ( unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* )
            // Benerator.g:154:9: unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression491);
            unaryExpression41=unaryExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression41.getTree());
            // Benerator.g:154:25: ( ( '*' | '/' | '%' ) unaryExpression )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( ((LA12_0>=STAR && LA12_0<=SLASH)||LA12_0==PERCENT) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // Benerator.g:154:26: ( '*' | '/' | '%' ) unaryExpression
            	    {
            	    set42=(Token)input.LT(1);
            	    set42=(Token)input.LT(1);
            	    if ( (input.LA(1)>=STAR && input.LA(1)<=SLASH)||input.LA(1)==PERCENT ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set42), root_0);
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression507);
            	    unaryExpression43=unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression43.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 15, multiplicativeExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "multiplicativeExpression"

    public static class unaryExpression_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "unaryExpression"
    // Benerator.g:161:1: unaryExpression : ( '-' castExpression -> ^( NEGATION castExpression ) | '~' castExpression | '!' castExpression | castExpression );
    public final BeneratorParser.unaryExpression_return unaryExpression() throws RecognitionException {
        BeneratorParser.unaryExpression_return retval = new BeneratorParser.unaryExpression_return();
        retval.start = input.LT(1);
        int unaryExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal44=null;
        Token char_literal46=null;
        Token char_literal48=null;
        BeneratorParser.castExpression_return castExpression45 = null;

        BeneratorParser.castExpression_return castExpression47 = null;

        BeneratorParser.castExpression_return castExpression49 = null;

        BeneratorParser.castExpression_return castExpression50 = null;


        Object char_literal44_tree=null;
        Object char_literal46_tree=null;
        Object char_literal48_tree=null;
        RewriteRuleTokenStream stream_SUB=new RewriteRuleTokenStream(adaptor,"token SUB");
        RewriteRuleSubtreeStream stream_castExpression=new RewriteRuleSubtreeStream(adaptor,"rule castExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 16) ) { return retval; }
            // Benerator.g:162:5: ( '-' castExpression -> ^( NEGATION castExpression ) | '~' castExpression | '!' castExpression | castExpression )
            int alt13=4;
            switch ( input.LA(1) ) {
            case SUB:
                {
                alt13=1;
                }
                break;
            case TILDE:
                {
                alt13=2;
                }
                break;
            case BANG:
                {
                alt13=3;
                }
                break;
            case IDENTIFIER:
            case INTLITERAL:
            case DECIMALLITERAL:
            case STRINGLITERAL:
            case BOOLEANLITERAL:
            case NULL:
            case LPAREN:
            case 64:
                {
                alt13=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }

            switch (alt13) {
                case 1 :
                    // Benerator.g:162:9: '-' castExpression
                    {
                    char_literal44=(Token)match(input,SUB,FOLLOW_SUB_in_unaryExpression533); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SUB.add(char_literal44);

                    pushFollow(FOLLOW_castExpression_in_unaryExpression535);
                    castExpression45=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_castExpression.add(castExpression45.getTree());


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
                    // 162:28: -> ^( NEGATION castExpression )
                    {
                        // Benerator.g:162:31: ^( NEGATION castExpression )
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
                    // Benerator.g:163:9: '~' castExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal46=(Token)match(input,TILDE,FOLLOW_TILDE_in_unaryExpression553); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal46_tree = (Object)adaptor.create(char_literal46);
                    root_0 = (Object)adaptor.becomeRoot(char_literal46_tree, root_0);
                    }
                    pushFollow(FOLLOW_castExpression_in_unaryExpression556);
                    castExpression47=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, castExpression47.getTree());

                    }
                    break;
                case 3 :
                    // Benerator.g:164:9: '!' castExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal48=(Token)match(input,BANG,FOLLOW_BANG_in_unaryExpression566); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal48_tree = (Object)adaptor.create(char_literal48);
                    root_0 = (Object)adaptor.becomeRoot(char_literal48_tree, root_0);
                    }
                    pushFollow(FOLLOW_castExpression_in_unaryExpression569);
                    castExpression49=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, castExpression49.getTree());

                    }
                    break;
                case 4 :
                    // Benerator.g:165:9: castExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_castExpression_in_unaryExpression579);
                    castExpression50=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, castExpression50.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 16, unaryExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "unaryExpression"

    public static class castExpression_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "castExpression"
    // Benerator.g:168:1: castExpression : ( '(' type ')' postfixExpression -> ^( CAST type postfixExpression ) | postfixExpression );
    public final BeneratorParser.castExpression_return castExpression() throws RecognitionException {
        BeneratorParser.castExpression_return retval = new BeneratorParser.castExpression_return();
        retval.start = input.LT(1);
        int castExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal51=null;
        Token char_literal53=null;
        BeneratorParser.type_return type52 = null;

        BeneratorParser.postfixExpression_return postfixExpression54 = null;

        BeneratorParser.postfixExpression_return postfixExpression55 = null;


        Object char_literal51_tree=null;
        Object char_literal53_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_type=new RewriteRuleSubtreeStream(adaptor,"rule type");
        RewriteRuleSubtreeStream stream_postfixExpression=new RewriteRuleSubtreeStream(adaptor,"rule postfixExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 17) ) { return retval; }
            // Benerator.g:169:5: ( '(' type ')' postfixExpression -> ^( CAST type postfixExpression ) | postfixExpression )
            int alt14=2;
            alt14 = dfa14.predict(input);
            switch (alt14) {
                case 1 :
                    // Benerator.g:169:9: '(' type ')' postfixExpression
                    {
                    char_literal51=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_castExpression599); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAREN.add(char_literal51);

                    pushFollow(FOLLOW_type_in_castExpression601);
                    type52=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_type.add(type52.getTree());
                    char_literal53=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_castExpression603); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAREN.add(char_literal53);

                    pushFollow(FOLLOW_postfixExpression_in_castExpression605);
                    postfixExpression54=postfixExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_postfixExpression.add(postfixExpression54.getTree());


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
                    // 169:40: -> ^( CAST type postfixExpression )
                    {
                        // Benerator.g:169:43: ^( CAST type postfixExpression )
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
                    // Benerator.g:170:9: postfixExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_postfixExpression_in_castExpression625);
                    postfixExpression55=postfixExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, postfixExpression55.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 17, castExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "castExpression"

    public static class type_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "type"
    // Benerator.g:173:1: type : qualifiedName -> ^( TYPE qualifiedName ) ;
    public final BeneratorParser.type_return type() throws RecognitionException {
        BeneratorParser.type_return retval = new BeneratorParser.type_return();
        retval.start = input.LT(1);
        int type_StartIndex = input.index();
        Object root_0 = null;

        BeneratorParser.qualifiedName_return qualifiedName56 = null;


        RewriteRuleSubtreeStream stream_qualifiedName=new RewriteRuleSubtreeStream(adaptor,"rule qualifiedName");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 18) ) { return retval; }
            // Benerator.g:174:5: ( qualifiedName -> ^( TYPE qualifiedName ) )
            // Benerator.g:174:9: qualifiedName
            {
            pushFollow(FOLLOW_qualifiedName_in_type644);
            qualifiedName56=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_qualifiedName.add(qualifiedName56.getTree());


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
            // 174:23: -> ^( TYPE qualifiedName )
            {
                // Benerator.g:174:26: ^( TYPE qualifiedName )
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
            if ( state.backtracking>0 ) { memoize(input, 18, type_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "type"

    public static class postfixExpression_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "postfixExpression"
    // Benerator.g:176:1: postfixExpression : ( primary -> primary ) ( '[' expression ']' -> ^( INDEX $postfixExpression expression ) | '.' IDENTIFIER arguments -> ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments ) | '.' IDENTIFIER -> ^( FIELD $postfixExpression IDENTIFIER ) )* ;
    public final BeneratorParser.postfixExpression_return postfixExpression() throws RecognitionException {
        BeneratorParser.postfixExpression_return retval = new BeneratorParser.postfixExpression_return();
        retval.start = input.LT(1);
        int postfixExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal58=null;
        Token char_literal60=null;
        Token char_literal61=null;
        Token IDENTIFIER62=null;
        Token char_literal64=null;
        Token IDENTIFIER65=null;
        BeneratorParser.primary_return primary57 = null;

        BeneratorParser.expression_return expression59 = null;

        BeneratorParser.arguments_return arguments63 = null;


        Object char_literal58_tree=null;
        Object char_literal60_tree=null;
        Object char_literal61_tree=null;
        Object IDENTIFIER62_tree=null;
        Object char_literal64_tree=null;
        Object IDENTIFIER65_tree=null;
        RewriteRuleTokenStream stream_LBRACKET=new RewriteRuleTokenStream(adaptor,"token LBRACKET");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleTokenStream stream_RBRACKET=new RewriteRuleTokenStream(adaptor,"token RBRACKET");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_arguments=new RewriteRuleSubtreeStream(adaptor,"rule arguments");
        RewriteRuleSubtreeStream stream_primary=new RewriteRuleSubtreeStream(adaptor,"rule primary");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 19) ) { return retval; }
            // Benerator.g:177:5: ( ( primary -> primary ) ( '[' expression ']' -> ^( INDEX $postfixExpression expression ) | '.' IDENTIFIER arguments -> ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments ) | '.' IDENTIFIER -> ^( FIELD $postfixExpression IDENTIFIER ) )* )
            // Benerator.g:177:9: ( primary -> primary ) ( '[' expression ']' -> ^( INDEX $postfixExpression expression ) | '.' IDENTIFIER arguments -> ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments ) | '.' IDENTIFIER -> ^( FIELD $postfixExpression IDENTIFIER ) )*
            {
            // Benerator.g:177:9: ( primary -> primary )
            // Benerator.g:177:10: primary
            {
            pushFollow(FOLLOW_primary_in_postfixExpression667);
            primary57=primary();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_primary.add(primary57.getTree());


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
            // 177:18: -> primary
            {
                adaptor.addChild(root_0, stream_primary.nextTree());

            }

            retval.tree = root_0;}
            }

            // Benerator.g:178:9: ( '[' expression ']' -> ^( INDEX $postfixExpression expression ) | '.' IDENTIFIER arguments -> ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments ) | '.' IDENTIFIER -> ^( FIELD $postfixExpression IDENTIFIER ) )*
            loop15:
            do {
                int alt15=4;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==LBRACKET) ) {
                    alt15=1;
                }
                else if ( (LA15_0==DOT) ) {
                    int LA15_3 = input.LA(2);

                    if ( (LA15_3==IDENTIFIER) ) {
                        int LA15_4 = input.LA(3);

                        if ( (LA15_4==EOF||LA15_4==RPAREN||(LA15_4>=LBRACKET && LA15_4<=RBRACKET)||(LA15_4>=COMMA && LA15_4<=DOT)||(LA15_4>=QUES && LA15_4<=PERCENT)||(LA15_4>=BANGEQ && LA15_4<=LE)) ) {
                            alt15=3;
                        }
                        else if ( (LA15_4==LPAREN) ) {
                            alt15=2;
                        }


                    }


                }


                switch (alt15) {
            	case 1 :
            	    // Benerator.g:179:13: '[' expression ']'
            	    {
            	    char_literal58=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_postfixExpression696); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_LBRACKET.add(char_literal58);

            	    pushFollow(FOLLOW_expression_in_postfixExpression698);
            	    expression59=expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_expression.add(expression59.getTree());
            	    char_literal60=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_postfixExpression700); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_RBRACKET.add(char_literal60);



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
            	    // 179:32: -> ^( INDEX $postfixExpression expression )
            	    {
            	        // Benerator.g:179:35: ^( INDEX $postfixExpression expression )
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
            	    // Benerator.g:180:13: '.' IDENTIFIER arguments
            	    {
            	    char_literal61=(Token)match(input,DOT,FOLLOW_DOT_in_postfixExpression725); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(char_literal61);

            	    IDENTIFIER62=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_postfixExpression727); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER62);

            	    pushFollow(FOLLOW_arguments_in_postfixExpression729);
            	    arguments63=arguments();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_arguments.add(arguments63.getTree());


            	    // AST REWRITE
            	    // elements: arguments, IDENTIFIER, postfixExpression
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            	    root_0 = (Object)adaptor.nil();
            	    // 180:37: -> ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments )
            	    {
            	        // Benerator.g:180:40: ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments )
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
            	    // Benerator.g:181:13: '.' IDENTIFIER
            	    {
            	    char_literal64=(Token)match(input,DOT,FOLLOW_DOT_in_postfixExpression755); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(char_literal64);

            	    IDENTIFIER65=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_postfixExpression757); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER65);



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
            	    // 181:28: -> ^( FIELD $postfixExpression IDENTIFIER )
            	    {
            	        // Benerator.g:181:31: ^( FIELD $postfixExpression IDENTIFIER )
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
            if ( state.backtracking>0 ) { memoize(input, 19, postfixExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "postfixExpression"

    public static class primary_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "primary"
    // Benerator.g:185:1: primary : ( '(' expression ')' | literal | creator | qualifiedName arguments -> ^( INVOCATION qualifiedName arguments ) | qualifiedName );
    public final BeneratorParser.primary_return primary() throws RecognitionException {
        BeneratorParser.primary_return retval = new BeneratorParser.primary_return();
        retval.start = input.LT(1);
        int primary_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal66=null;
        Token char_literal68=null;
        BeneratorParser.expression_return expression67 = null;

        BeneratorParser.literal_return literal69 = null;

        BeneratorParser.creator_return creator70 = null;

        BeneratorParser.qualifiedName_return qualifiedName71 = null;

        BeneratorParser.arguments_return arguments72 = null;

        BeneratorParser.qualifiedName_return qualifiedName73 = null;


        Object char_literal66_tree=null;
        Object char_literal68_tree=null;
        RewriteRuleSubtreeStream stream_arguments=new RewriteRuleSubtreeStream(adaptor,"rule arguments");
        RewriteRuleSubtreeStream stream_qualifiedName=new RewriteRuleSubtreeStream(adaptor,"rule qualifiedName");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 20) ) { return retval; }
            // Benerator.g:186:5: ( '(' expression ')' | literal | creator | qualifiedName arguments -> ^( INVOCATION qualifiedName arguments ) | qualifiedName )
            int alt16=5;
            switch ( input.LA(1) ) {
            case LPAREN:
                {
                alt16=1;
                }
                break;
            case INTLITERAL:
            case DECIMALLITERAL:
            case STRINGLITERAL:
            case BOOLEANLITERAL:
            case NULL:
                {
                alt16=2;
                }
                break;
            case 64:
                {
                alt16=3;
                }
                break;
            case IDENTIFIER:
                {
                int LA16_4 = input.LA(2);

                if ( (synpred32_Benerator()) ) {
                    alt16=4;
                }
                else if ( (true) ) {
                    alt16=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 16, 4, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    // Benerator.g:186:9: '(' expression ')'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal66=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_primary799); if (state.failed) return retval;
                    pushFollow(FOLLOW_expression_in_primary802);
                    expression67=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression67.getTree());
                    char_literal68=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_primary804); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // Benerator.g:187:9: literal
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_literal_in_primary815);
                    literal69=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal69.getTree());

                    }
                    break;
                case 3 :
                    // Benerator.g:188:7: creator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_creator_in_primary823);
                    creator70=creator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, creator70.getTree());

                    }
                    break;
                case 4 :
                    // Benerator.g:189:9: qualifiedName arguments
                    {
                    pushFollow(FOLLOW_qualifiedName_in_primary833);
                    qualifiedName71=qualifiedName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_qualifiedName.add(qualifiedName71.getTree());
                    pushFollow(FOLLOW_arguments_in_primary835);
                    arguments72=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arguments.add(arguments72.getTree());


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
                    // 189:33: -> ^( INVOCATION qualifiedName arguments )
                    {
                        // Benerator.g:189:36: ^( INVOCATION qualifiedName arguments )
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
                    // Benerator.g:190:9: qualifiedName
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_qualifiedName_in_primary855);
                    qualifiedName73=qualifiedName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName73.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 20, primary_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "primary"

    public static class creator_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "creator"
    // Benerator.g:193:1: creator : ( 'new' qualifiedName arguments -> ^( CONSTRUCTOR qualifiedName arguments ) | 'new' qualifiedName '[' assignment ( ',' assignment )* ']' -> ^( BEAN qualifiedName ( assignment )* ) );
    public final BeneratorParser.creator_return creator() throws RecognitionException {
        BeneratorParser.creator_return retval = new BeneratorParser.creator_return();
        retval.start = input.LT(1);
        int creator_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal74=null;
        Token string_literal77=null;
        Token char_literal79=null;
        Token char_literal81=null;
        Token char_literal83=null;
        BeneratorParser.qualifiedName_return qualifiedName75 = null;

        BeneratorParser.arguments_return arguments76 = null;

        BeneratorParser.qualifiedName_return qualifiedName78 = null;

        BeneratorParser.assignment_return assignment80 = null;

        BeneratorParser.assignment_return assignment82 = null;


        Object string_literal74_tree=null;
        Object string_literal77_tree=null;
        Object char_literal79_tree=null;
        Object char_literal81_tree=null;
        Object char_literal83_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LBRACKET=new RewriteRuleTokenStream(adaptor,"token LBRACKET");
        RewriteRuleTokenStream stream_64=new RewriteRuleTokenStream(adaptor,"token 64");
        RewriteRuleTokenStream stream_RBRACKET=new RewriteRuleTokenStream(adaptor,"token RBRACKET");
        RewriteRuleSubtreeStream stream_arguments=new RewriteRuleSubtreeStream(adaptor,"rule arguments");
        RewriteRuleSubtreeStream stream_assignment=new RewriteRuleSubtreeStream(adaptor,"rule assignment");
        RewriteRuleSubtreeStream stream_qualifiedName=new RewriteRuleSubtreeStream(adaptor,"rule qualifiedName");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 21) ) { return retval; }
            // Benerator.g:194:5: ( 'new' qualifiedName arguments -> ^( CONSTRUCTOR qualifiedName arguments ) | 'new' qualifiedName '[' assignment ( ',' assignment )* ']' -> ^( BEAN qualifiedName ( assignment )* ) )
            int alt18=2;
            alt18 = dfa18.predict(input);
            switch (alt18) {
                case 1 :
                    // Benerator.g:194:9: 'new' qualifiedName arguments
                    {
                    string_literal74=(Token)match(input,64,FOLLOW_64_in_creator874); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_64.add(string_literal74);

                    pushFollow(FOLLOW_qualifiedName_in_creator876);
                    qualifiedName75=qualifiedName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_qualifiedName.add(qualifiedName75.getTree());
                    pushFollow(FOLLOW_arguments_in_creator878);
                    arguments76=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arguments.add(arguments76.getTree());


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
                    // 194:39: -> ^( CONSTRUCTOR qualifiedName arguments )
                    {
                        // Benerator.g:194:42: ^( CONSTRUCTOR qualifiedName arguments )
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
                    // Benerator.g:195:9: 'new' qualifiedName '[' assignment ( ',' assignment )* ']'
                    {
                    string_literal77=(Token)match(input,64,FOLLOW_64_in_creator898); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_64.add(string_literal77);

                    pushFollow(FOLLOW_qualifiedName_in_creator900);
                    qualifiedName78=qualifiedName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_qualifiedName.add(qualifiedName78.getTree());
                    char_literal79=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_creator902); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LBRACKET.add(char_literal79);

                    pushFollow(FOLLOW_assignment_in_creator904);
                    assignment80=assignment();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_assignment.add(assignment80.getTree());
                    // Benerator.g:195:44: ( ',' assignment )*
                    loop17:
                    do {
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( (LA17_0==COMMA) ) {
                            alt17=1;
                        }


                        switch (alt17) {
                    	case 1 :
                    	    // Benerator.g:195:45: ',' assignment
                    	    {
                    	    char_literal81=(Token)match(input,COMMA,FOLLOW_COMMA_in_creator907); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(char_literal81);

                    	    pushFollow(FOLLOW_assignment_in_creator909);
                    	    assignment82=assignment();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_assignment.add(assignment82.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop17;
                        }
                    } while (true);

                    char_literal83=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_creator913); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RBRACKET.add(char_literal83);



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
                    // 195:66: -> ^( BEAN qualifiedName ( assignment )* )
                    {
                        // Benerator.g:195:69: ^( BEAN qualifiedName ( assignment )* )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(BEAN, "BEAN"), root_1);

                        adaptor.addChild(root_1, stream_qualifiedName.nextTree());
                        // Benerator.g:195:90: ( assignment )*
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
            if ( state.backtracking>0 ) { memoize(input, 21, creator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "creator"

    public static class arguments_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "arguments"
    // Benerator.g:198:1: arguments : '(' ( expression ( ',' expression )* )? ')' -> ^( ARGUMENTS ( expression )* ) ;
    public final BeneratorParser.arguments_return arguments() throws RecognitionException {
        BeneratorParser.arguments_return retval = new BeneratorParser.arguments_return();
        retval.start = input.LT(1);
        int arguments_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal84=null;
        Token char_literal86=null;
        Token char_literal88=null;
        BeneratorParser.expression_return expression85 = null;

        BeneratorParser.expression_return expression87 = null;


        Object char_literal84_tree=null;
        Object char_literal86_tree=null;
        Object char_literal88_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 22) ) { return retval; }
            // Benerator.g:199:5: ( '(' ( expression ( ',' expression )* )? ')' -> ^( ARGUMENTS ( expression )* ) )
            // Benerator.g:199:9: '(' ( expression ( ',' expression )* )? ')'
            {
            char_literal84=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_arguments943); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(char_literal84);

            // Benerator.g:199:13: ( expression ( ',' expression )* )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( ((LA20_0>=IDENTIFIER && LA20_0<=NULL)||LA20_0==LPAREN||(LA20_0>=BANG && LA20_0<=TILDE)||LA20_0==SUB||LA20_0==64) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // Benerator.g:199:14: expression ( ',' expression )*
                    {
                    pushFollow(FOLLOW_expression_in_arguments946);
                    expression85=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression85.getTree());
                    // Benerator.g:199:25: ( ',' expression )*
                    loop19:
                    do {
                        int alt19=2;
                        int LA19_0 = input.LA(1);

                        if ( (LA19_0==COMMA) ) {
                            alt19=1;
                        }


                        switch (alt19) {
                    	case 1 :
                    	    // Benerator.g:199:26: ',' expression
                    	    {
                    	    char_literal86=(Token)match(input,COMMA,FOLLOW_COMMA_in_arguments949); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(char_literal86);

                    	    pushFollow(FOLLOW_expression_in_arguments951);
                    	    expression87=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expression.add(expression87.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop19;
                        }
                    } while (true);


                    }
                    break;

            }

            char_literal88=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_arguments957); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(char_literal88);



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
            // 199:49: -> ^( ARGUMENTS ( expression )* )
            {
                // Benerator.g:199:52: ^( ARGUMENTS ( expression )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ARGUMENTS, "ARGUMENTS"), root_1);

                // Benerator.g:199:64: ( expression )*
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
            if ( state.backtracking>0 ) { memoize(input, 22, arguments_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "arguments"

    public static class qualifiedName_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "qualifiedName"
    // Benerator.g:201:1: qualifiedName : IDENTIFIER ( '.' IDENTIFIER )* -> ^( QUALIFIEDNAME ( IDENTIFIER )* ) ;
    public final BeneratorParser.qualifiedName_return qualifiedName() throws RecognitionException {
        BeneratorParser.qualifiedName_return retval = new BeneratorParser.qualifiedName_return();
        retval.start = input.LT(1);
        int qualifiedName_StartIndex = input.index();
        Object root_0 = null;

        Token IDENTIFIER89=null;
        Token char_literal90=null;
        Token IDENTIFIER91=null;

        Object IDENTIFIER89_tree=null;
        Object char_literal90_tree=null;
        Object IDENTIFIER91_tree=null;
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 23) ) { return retval; }
            // Benerator.g:202:2: ( IDENTIFIER ( '.' IDENTIFIER )* -> ^( QUALIFIEDNAME ( IDENTIFIER )* ) )
            // Benerator.g:202:6: IDENTIFIER ( '.' IDENTIFIER )*
            {
            IDENTIFIER89=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedName981); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER89);

            // Benerator.g:202:17: ( '.' IDENTIFIER )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==DOT) ) {
                    int LA21_2 = input.LA(2);

                    if ( (LA21_2==IDENTIFIER) ) {
                        int LA21_3 = input.LA(3);

                        if ( (synpred37_Benerator()) ) {
                            alt21=1;
                        }


                    }


                }


                switch (alt21) {
            	case 1 :
            	    // Benerator.g:202:18: '.' IDENTIFIER
            	    {
            	    char_literal90=(Token)match(input,DOT,FOLLOW_DOT_in_qualifiedName984); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(char_literal90);

            	    IDENTIFIER91=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedName986); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER91);


            	    }
            	    break;

            	default :
            	    break loop21;
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
            // 202:35: -> ^( QUALIFIEDNAME ( IDENTIFIER )* )
            {
                // Benerator.g:202:38: ^( QUALIFIEDNAME ( IDENTIFIER )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(QUALIFIEDNAME, "QUALIFIEDNAME"), root_1);

                // Benerator.g:202:54: ( IDENTIFIER )*
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
            if ( state.backtracking>0 ) { memoize(input, 23, qualifiedName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "qualifiedName"

    public static class literal_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    }

    // $ANTLR start "literal"
    // Benerator.g:205:1: literal : ( INTLITERAL | DECIMALLITERAL | STRINGLITERAL | BOOLEANLITERAL | NULL );
    public final BeneratorParser.literal_return literal() throws RecognitionException {
        BeneratorParser.literal_return retval = new BeneratorParser.literal_return();
        retval.start = input.LT(1);
        int literal_StartIndex = input.index();
        Object root_0 = null;

        Token set92=null;

        Object set92_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 24) ) { return retval; }
            // Benerator.g:206:5: ( INTLITERAL | DECIMALLITERAL | STRINGLITERAL | BOOLEANLITERAL | NULL )
            // Benerator.g:
            {
            root_0 = (Object)adaptor.nil();

            set92=(Token)input.LT(1);
            if ( (input.LA(1)>=INTLITERAL && input.LA(1)<=NULL) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set92));
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
            if ( state.backtracking>0 ) { memoize(input, 24, literal_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "literal"

    // $ANTLR start synpred32_Benerator
    public final void synpred32_Benerator_fragment() throws RecognitionException {   
        // Benerator.g:189:9: ( qualifiedName arguments )
        // Benerator.g:189:9: qualifiedName arguments
        {
        pushFollow(FOLLOW_qualifiedName_in_synpred32_Benerator833);
        qualifiedName();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_arguments_in_synpred32_Benerator835);
        arguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred32_Benerator

    // $ANTLR start synpred37_Benerator
    public final void synpred37_Benerator_fragment() throws RecognitionException {   
        // Benerator.g:202:18: ( '.' IDENTIFIER )
        // Benerator.g:202:18: '.' IDENTIFIER
        {
        match(input,DOT,FOLLOW_DOT_in_synpred37_Benerator984); if (state.failed) return ;
        match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred37_Benerator986); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred37_Benerator

    // Delegated rules

    public final boolean synpred32_Benerator() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred32_Benerator_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred37_Benerator() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred37_Benerator_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA14 dfa14 = new DFA14(this);
    protected DFA18 dfa18 = new DFA18(this);
    static final String DFA14_eotS =
        "\10\uffff";
    static final String DFA14_eofS =
        "\5\uffff\1\2\2\uffff";
    static final String DFA14_minS =
        "\2\4\1\uffff\1\34\2\4\1\34\1\uffff";
    static final String DFA14_maxS =
        "\2\100\1\uffff\1\75\1\4\1\100\1\75\1\uffff";
    static final String DFA14_acceptS =
        "\2\uffff\1\2\4\uffff\1\1";
    static final String DFA14_specialS =
        "\10\uffff}>";
    static final String[] DFA14_transitionS = {
            "\6\2\22\uffff\1\1\43\uffff\1\2",
            "\1\3\5\2\22\uffff\1\2\11\uffff\2\2\6\uffff\1\2\21\uffff\1\2",
            "",
            "\1\2\1\5\2\uffff\1\2\3\uffff\1\4\3\uffff\1\2\1\uffff\13\2\1"+
            "\uffff\10\2",
            "\1\6",
            "\6\7\22\uffff\1\7\1\2\2\uffff\2\2\1\uffff\2\2\3\uffff\15\2"+
            "\1\uffff\10\2\2\uffff\1\7",
            "\1\2\1\5\2\uffff\1\2\3\uffff\1\4\3\uffff\1\2\1\uffff\13\2\1"+
            "\uffff\10\2",
            ""
    };

    static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
    static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
    static final char[] DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
    static final char[] DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
    static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
    static final short[] DFA14_special = DFA.unpackEncodedString(DFA14_specialS);
    static final short[][] DFA14_transition;

    static {
        int numStates = DFA14_transitionS.length;
        DFA14_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA14_transition[i] = DFA.unpackEncodedString(DFA14_transitionS[i]);
        }
    }

    class DFA14 extends DFA {

        public DFA14(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 14;
            this.eot = DFA14_eot;
            this.eof = DFA14_eof;
            this.min = DFA14_min;
            this.max = DFA14_max;
            this.accept = DFA14_accept;
            this.special = DFA14_special;
            this.transition = DFA14_transition;
        }
        @Override
        public String getDescription() {
            return "168:1: castExpression : ( '(' type ')' postfixExpression -> ^( CAST type postfixExpression ) | postfixExpression );";
        }
    }
    static final String DFA18_eotS =
        "\7\uffff";
    static final String DFA18_eofS =
        "\7\uffff";
    static final String DFA18_minS =
        "\1\100\1\4\1\34\1\4\2\uffff\1\34";
    static final String DFA18_maxS =
        "\1\100\1\4\1\44\1\4\2\uffff\1\44";
    static final String DFA18_acceptS =
        "\4\uffff\1\2\1\1\1\uffff";
    static final String DFA18_specialS =
        "\7\uffff}>";
    static final String[] DFA18_transitionS = {
            "\1\1",
            "\1\2",
            "\1\5\3\uffff\1\4\3\uffff\1\3",
            "\1\6",
            "",
            "",
            "\1\5\3\uffff\1\4\3\uffff\1\3"
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
        @Override
        public String getDescription() {
            return "193:1: creator : ( 'new' qualifiedName arguments -> ^( CONSTRUCTOR qualifiedName arguments ) | 'new' qualifiedName '[' assignment ( ',' assignment )* ']' -> ^( BEAN qualifiedName ( assignment )* ) );";
        }
    }
 

    public static final BitSet FOLLOW_IDENTIFIER_in_assignment90 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_EQ_in_assignment92 = new BitSet(new long[]{0x000040C0100003F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_assignment95 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_beanSpec_in_beanSpecList109 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_COMMA_in_beanSpecList112 = new BitSet(new long[]{0x000040C0100003F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_beanSpec_in_beanSpecList115 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_expression_in_beanSpec132 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression167 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_QUES_in_conditionalExpression170 = new BitSet(new long[]{0x000040C0100003F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression173 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_COLON_in_conditionalExpression175 = new BitSet(new long[]{0x000040C0100003F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalExpression178 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression204 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_BARBAR_in_conditionalOrExpression207 = new BitSet(new long[]{0x000040C0100003F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression210 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression233 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_AMPAMP_in_conditionalAndExpression236 = new BitSet(new long[]{0x000040C0100003F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression239 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression261 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_BAR_in_inclusiveOrExpression264 = new BitSet(new long[]{0x000040C0100003F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression267 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression288 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_CARET_in_exclusiveOrExpression291 = new BitSet(new long[]{0x000040C0100003F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression294 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression315 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_AMP_in_andExpression318 = new BitSet(new long[]{0x000040C0100003F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression321 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression343 = new BitSet(new long[]{0x0040040000000002L});
    public static final BitSet FOLLOW_set_in_equalityExpression346 = new BitSet(new long[]{0x000040C0100003F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression355 = new BitSet(new long[]{0x0040040000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression377 = new BitSet(new long[]{0x3880000000000002L});
    public static final BitSet FOLLOW_set_in_relationalExpression380 = new BitSet(new long[]{0x000040C0100003F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression397 = new BitSet(new long[]{0x3880000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression419 = new BitSet(new long[]{0x0700000000000002L});
    public static final BitSet FOLLOW_set_in_shiftExpression422 = new BitSet(new long[]{0x000040C0100003F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression435 = new BitSet(new long[]{0x0700000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression457 = new BitSet(new long[]{0x0000600000000002L});
    public static final BitSet FOLLOW_set_in_additiveExpression460 = new BitSet(new long[]{0x000040C0100003F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression469 = new BitSet(new long[]{0x0000600000000002L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression491 = new BitSet(new long[]{0x0011800000000002L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression494 = new BitSet(new long[]{0x000040C0100003F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression507 = new BitSet(new long[]{0x0011800000000002L});
    public static final BitSet FOLLOW_SUB_in_unaryExpression533 = new BitSet(new long[]{0x000040C0100003F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpression535 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_unaryExpression553 = new BitSet(new long[]{0x000040C0100003F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpression556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_unaryExpression566 = new BitSet(new long[]{0x000040C0100003F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpression569 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpression579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_castExpression599 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_type_in_castExpression601 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_RPAREN_in_castExpression603 = new BitSet(new long[]{0x000040C0100003F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_postfixExpression_in_castExpression605 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_postfixExpression_in_castExpression625 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_type644 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_postfixExpression667 = new BitSet(new long[]{0x0000001100000002L});
    public static final BitSet FOLLOW_LBRACKET_in_postfixExpression696 = new BitSet(new long[]{0x000040C0100003F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_postfixExpression698 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_RBRACKET_in_postfixExpression700 = new BitSet(new long[]{0x0000001100000002L});
    public static final BitSet FOLLOW_DOT_in_postfixExpression725 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_postfixExpression727 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_arguments_in_postfixExpression729 = new BitSet(new long[]{0x0000001100000002L});
    public static final BitSet FOLLOW_DOT_in_postfixExpression755 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_postfixExpression757 = new BitSet(new long[]{0x0000001100000002L});
    public static final BitSet FOLLOW_LPAREN_in_primary799 = new BitSet(new long[]{0x000040C0100003F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_primary802 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_RPAREN_in_primary804 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary815 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_creator_in_primary823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_primary833 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_arguments_in_primary835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_primary855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_creator874 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_creator876 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_arguments_in_creator878 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_creator898 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_creator900 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_LBRACKET_in_creator902 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_assignment_in_creator904 = new BitSet(new long[]{0x0000000A00000000L});
    public static final BitSet FOLLOW_COMMA_in_creator907 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_assignment_in_creator909 = new BitSet(new long[]{0x0000000A00000000L});
    public static final BitSet FOLLOW_RBRACKET_in_creator913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_arguments943 = new BitSet(new long[]{0x000040C0300003F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_arguments946 = new BitSet(new long[]{0x0000000820000000L});
    public static final BitSet FOLLOW_COMMA_in_arguments949 = new BitSet(new long[]{0x000040C0100003F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_arguments951 = new BitSet(new long[]{0x0000000820000000L});
    public static final BitSet FOLLOW_RPAREN_in_arguments957 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedName981 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_DOT_in_qualifiedName984 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedName986 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_set_in_literal0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_synpred32_Benerator833 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_arguments_in_synpred32_Benerator835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred37_Benerator984 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_synpred37_Benerator986 = new BitSet(new long[]{0x0000000000000002L});

}