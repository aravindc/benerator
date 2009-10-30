// $ANTLR 3.2 Sep 23, 2009 12:02:23 benerator/src/main/resources/org/databene/benerator/script/Benerator.g 2009-10-24 08:09:00

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
            this.state.ruleMemo = new HashMap[69+1];
             
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return BeneratorParser.tokenNames; }
    public String getGrammarFileName() { return "benerator/src/main/resources/org/databene/benerator/script/Benerator.g"; }


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


    public static class transitionList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "transitionList"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:96:1: transitionList : transition ( ',' transition )* ;
    public final BeneratorParser.transitionList_return transitionList() throws RecognitionException {
        BeneratorParser.transitionList_return retval = new BeneratorParser.transitionList_return();
        retval.start = input.LT(1);
        int transitionList_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal2=null;
        BeneratorParser.transition_return transition1 = null;

        BeneratorParser.transition_return transition3 = null;


        Object char_literal2_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 1) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:101:5: ( transition ( ',' transition )* )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:101:9: transition ( ',' transition )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_transition_in_transitionList90);
            transition1=transition();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, transition1.getTree());
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:101:20: ( ',' transition )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==COMMA) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:101:21: ',' transition
            	    {
            	    char_literal2=(Token)match(input,COMMA,FOLLOW_COMMA_in_transitionList93); if (state.failed) return retval;
            	    pushFollow(FOLLOW_transition_in_transitionList96);
            	    transition3=transition();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, transition3.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 1, transitionList_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "transitionList"

    public static class transition_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "transition"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:103:1: transition : literal '->' literal ( '[' expression ']' )? ;
    public final BeneratorParser.transition_return transition() throws RecognitionException {
        BeneratorParser.transition_return retval = new BeneratorParser.transition_return();
        retval.start = input.LT(1);
        int transition_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal5=null;
        Token char_literal7=null;
        Token char_literal9=null;
        BeneratorParser.literal_return literal4 = null;

        BeneratorParser.literal_return literal6 = null;

        BeneratorParser.expression_return expression8 = null;


        Object string_literal5_tree=null;
        Object char_literal7_tree=null;
        Object char_literal9_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 2) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:104:5: ( literal '->' literal ( '[' expression ']' )? )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:104:9: literal '->' literal ( '[' expression ']' )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_literal_in_transition112);
            literal4=literal();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, literal4.getTree());
            string_literal5=(Token)match(input,ARROW,FOLLOW_ARROW_in_transition114); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal5_tree = (Object)adaptor.create(string_literal5);
            root_0 = (Object)adaptor.becomeRoot(string_literal5_tree, root_0);
            }
            pushFollow(FOLLOW_literal_in_transition117);
            literal6=literal();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, literal6.getTree());
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:104:31: ( '[' expression ']' )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==LBRACKET) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:104:32: '[' expression ']'
                    {
                    char_literal7=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_transition120); if (state.failed) return retval;
                    pushFollow(FOLLOW_expression_in_transition123);
                    expression8=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression8.getTree());
                    char_literal9=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_transition125); if (state.failed) return retval;

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
            if ( state.backtracking>0 ) { memoize(input, 2, transition_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "transition"

    public static class assignment_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "assignment"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:106:1: assignment : IDENTIFIER '=' expression ;
    public final BeneratorParser.assignment_return assignment() throws RecognitionException {
        BeneratorParser.assignment_return retval = new BeneratorParser.assignment_return();
        retval.start = input.LT(1);
        int assignment_StartIndex = input.index();
        Object root_0 = null;

        Token IDENTIFIER10=null;
        Token char_literal11=null;
        BeneratorParser.expression_return expression12 = null;


        Object IDENTIFIER10_tree=null;
        Object char_literal11_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 3) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:107:5: ( IDENTIFIER '=' expression )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:107:9: IDENTIFIER '=' expression
            {
            root_0 = (Object)adaptor.nil();

            IDENTIFIER10=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_assignment142); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER10_tree = (Object)adaptor.create(IDENTIFIER10);
            adaptor.addChild(root_0, IDENTIFIER10_tree);
            }
            char_literal11=(Token)match(input,EQ,FOLLOW_EQ_in_assignment144); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal11_tree = (Object)adaptor.create(char_literal11);
            root_0 = (Object)adaptor.becomeRoot(char_literal11_tree, root_0);
            }
            pushFollow(FOLLOW_expression_in_assignment147);
            expression12=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression12.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 3, assignment_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "assignment"

    public static class beanSpecList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "beanSpecList"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:109:1: beanSpecList : beanSpec ( ',' beanSpec )* ;
    public final BeneratorParser.beanSpecList_return beanSpecList() throws RecognitionException {
        BeneratorParser.beanSpecList_return retval = new BeneratorParser.beanSpecList_return();
        retval.start = input.LT(1);
        int beanSpecList_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal14=null;
        BeneratorParser.beanSpec_return beanSpec13 = null;

        BeneratorParser.beanSpec_return beanSpec15 = null;


        Object char_literal14_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 4) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:110:5: ( beanSpec ( ',' beanSpec )* )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:110:9: beanSpec ( ',' beanSpec )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_beanSpec_in_beanSpecList161);
            beanSpec13=beanSpec();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, beanSpec13.getTree());
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:110:18: ( ',' beanSpec )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==COMMA) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:110:19: ',' beanSpec
            	    {
            	    char_literal14=(Token)match(input,COMMA,FOLLOW_COMMA_in_beanSpecList164); if (state.failed) return retval;
            	    pushFollow(FOLLOW_beanSpec_in_beanSpecList167);
            	    beanSpec15=beanSpec();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, beanSpec15.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 4, beanSpecList_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "beanSpecList"

    public static class beanSpec_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "beanSpec"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:112:1: beanSpec : expression -> ^( BEANSPEC expression ) ;
    public final BeneratorParser.beanSpec_return beanSpec() throws RecognitionException {
        BeneratorParser.beanSpec_return retval = new BeneratorParser.beanSpec_return();
        retval.start = input.LT(1);
        int beanSpec_StartIndex = input.index();
        Object root_0 = null;

        BeneratorParser.expression_return expression16 = null;


        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 5) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:113:5: ( expression -> ^( BEANSPEC expression ) )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:113:9: expression
            {
            pushFollow(FOLLOW_expression_in_beanSpec183);
            expression16=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expression.add(expression16.getTree());


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
            // 113:20: -> ^( BEANSPEC expression )
            {
                // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:113:23: ^( BEANSPEC expression )
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
            if ( state.backtracking>0 ) { memoize(input, 5, beanSpec_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "beanSpec"

    public static class expression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expression"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:115:1: expression : conditionalExpression ;
    public final BeneratorParser.expression_return expression() throws RecognitionException {
        BeneratorParser.expression_return retval = new BeneratorParser.expression_return();
        retval.start = input.LT(1);
        int expression_StartIndex = input.index();
        Object root_0 = null;

        BeneratorParser.conditionalExpression_return conditionalExpression17 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:116:5: ( conditionalExpression )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:116:9: conditionalExpression
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalExpression_in_expression206);
            conditionalExpression17=conditionalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression17.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 6, expression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "expression"

    public static class conditionalExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conditionalExpression"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:119:1: conditionalExpression : conditionalOrExpression ( '?' expression ':' conditionalExpression )? ;
    public final BeneratorParser.conditionalExpression_return conditionalExpression() throws RecognitionException {
        BeneratorParser.conditionalExpression_return retval = new BeneratorParser.conditionalExpression_return();
        retval.start = input.LT(1);
        int conditionalExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal19=null;
        Token char_literal21=null;
        BeneratorParser.conditionalOrExpression_return conditionalOrExpression18 = null;

        BeneratorParser.expression_return expression20 = null;

        BeneratorParser.conditionalExpression_return conditionalExpression22 = null;


        Object char_literal19_tree=null;
        Object char_literal21_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 7) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:120:5: ( conditionalOrExpression ( '?' expression ':' conditionalExpression )? )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:120:9: conditionalOrExpression ( '?' expression ':' conditionalExpression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression226);
            conditionalOrExpression18=conditionalOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalOrExpression18.getTree());
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:120:33: ( '?' expression ':' conditionalExpression )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==QUES) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:120:34: '?' expression ':' conditionalExpression
                    {
                    char_literal19=(Token)match(input,QUES,FOLLOW_QUES_in_conditionalExpression229); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal19_tree = (Object)adaptor.create(char_literal19);
                    root_0 = (Object)adaptor.becomeRoot(char_literal19_tree, root_0);
                    }
                    pushFollow(FOLLOW_expression_in_conditionalExpression232);
                    expression20=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression20.getTree());
                    char_literal21=(Token)match(input,COLON,FOLLOW_COLON_in_conditionalExpression234); if (state.failed) return retval;
                    pushFollow(FOLLOW_conditionalExpression_in_conditionalExpression237);
                    conditionalExpression22=conditionalExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression22.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 7, conditionalExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "conditionalExpression"

    public static class conditionalOrExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conditionalOrExpression"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:123:1: conditionalOrExpression : conditionalAndExpression ( '||' conditionalAndExpression )* ;
    public final BeneratorParser.conditionalOrExpression_return conditionalOrExpression() throws RecognitionException {
        BeneratorParser.conditionalOrExpression_return retval = new BeneratorParser.conditionalOrExpression_return();
        retval.start = input.LT(1);
        int conditionalOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal24=null;
        BeneratorParser.conditionalAndExpression_return conditionalAndExpression23 = null;

        BeneratorParser.conditionalAndExpression_return conditionalAndExpression25 = null;


        Object string_literal24_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 8) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:124:5: ( conditionalAndExpression ( '||' conditionalAndExpression )* )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:124:9: conditionalAndExpression ( '||' conditionalAndExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression263);
            conditionalAndExpression23=conditionalAndExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression23.getTree());
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:124:34: ( '||' conditionalAndExpression )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==BARBAR) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:124:35: '||' conditionalAndExpression
            	    {
            	    string_literal24=(Token)match(input,BARBAR,FOLLOW_BARBAR_in_conditionalOrExpression266); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal24_tree = (Object)adaptor.create(string_literal24);
            	    root_0 = (Object)adaptor.becomeRoot(string_literal24_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression269);
            	    conditionalAndExpression25=conditionalAndExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression25.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 8, conditionalOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "conditionalOrExpression"

    public static class conditionalAndExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conditionalAndExpression"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:127:1: conditionalAndExpression : inclusiveOrExpression ( '&&' inclusiveOrExpression )* ;
    public final BeneratorParser.conditionalAndExpression_return conditionalAndExpression() throws RecognitionException {
        BeneratorParser.conditionalAndExpression_return retval = new BeneratorParser.conditionalAndExpression_return();
        retval.start = input.LT(1);
        int conditionalAndExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal27=null;
        BeneratorParser.inclusiveOrExpression_return inclusiveOrExpression26 = null;

        BeneratorParser.inclusiveOrExpression_return inclusiveOrExpression28 = null;


        Object string_literal27_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:128:5: ( inclusiveOrExpression ( '&&' inclusiveOrExpression )* )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:128:9: inclusiveOrExpression ( '&&' inclusiveOrExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression292);
            inclusiveOrExpression26=inclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression26.getTree());
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:128:31: ( '&&' inclusiveOrExpression )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==AMPAMP) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:128:32: '&&' inclusiveOrExpression
            	    {
            	    string_literal27=(Token)match(input,AMPAMP,FOLLOW_AMPAMP_in_conditionalAndExpression295); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal27_tree = (Object)adaptor.create(string_literal27);
            	    root_0 = (Object)adaptor.becomeRoot(string_literal27_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression298);
            	    inclusiveOrExpression28=inclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression28.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 9, conditionalAndExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "conditionalAndExpression"

    public static class inclusiveOrExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "inclusiveOrExpression"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:131:1: inclusiveOrExpression : exclusiveOrExpression ( '|' exclusiveOrExpression )* ;
    public final BeneratorParser.inclusiveOrExpression_return inclusiveOrExpression() throws RecognitionException {
        BeneratorParser.inclusiveOrExpression_return retval = new BeneratorParser.inclusiveOrExpression_return();
        retval.start = input.LT(1);
        int inclusiveOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal30=null;
        BeneratorParser.exclusiveOrExpression_return exclusiveOrExpression29 = null;

        BeneratorParser.exclusiveOrExpression_return exclusiveOrExpression31 = null;


        Object char_literal30_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 10) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:132:5: ( exclusiveOrExpression ( '|' exclusiveOrExpression )* )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:132:9: exclusiveOrExpression ( '|' exclusiveOrExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression320);
            exclusiveOrExpression29=exclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression29.getTree());
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:132:31: ( '|' exclusiveOrExpression )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==BAR) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:132:32: '|' exclusiveOrExpression
            	    {
            	    char_literal30=(Token)match(input,BAR,FOLLOW_BAR_in_inclusiveOrExpression323); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal30_tree = (Object)adaptor.create(char_literal30);
            	    root_0 = (Object)adaptor.becomeRoot(char_literal30_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression326);
            	    exclusiveOrExpression31=exclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression31.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 10, inclusiveOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "inclusiveOrExpression"

    public static class exclusiveOrExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "exclusiveOrExpression"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:135:1: exclusiveOrExpression : andExpression ( '^' andExpression )* ;
    public final BeneratorParser.exclusiveOrExpression_return exclusiveOrExpression() throws RecognitionException {
        BeneratorParser.exclusiveOrExpression_return retval = new BeneratorParser.exclusiveOrExpression_return();
        retval.start = input.LT(1);
        int exclusiveOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal33=null;
        BeneratorParser.andExpression_return andExpression32 = null;

        BeneratorParser.andExpression_return andExpression34 = null;


        Object char_literal33_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 11) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:136:5: ( andExpression ( '^' andExpression )* )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:136:9: andExpression ( '^' andExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression347);
            andExpression32=andExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression32.getTree());
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:136:23: ( '^' andExpression )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==CARET) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:136:24: '^' andExpression
            	    {
            	    char_literal33=(Token)match(input,CARET,FOLLOW_CARET_in_exclusiveOrExpression350); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal33_tree = (Object)adaptor.create(char_literal33);
            	    root_0 = (Object)adaptor.becomeRoot(char_literal33_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression353);
            	    andExpression34=andExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression34.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 11, exclusiveOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "exclusiveOrExpression"

    public static class andExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "andExpression"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:139:1: andExpression : equalityExpression ( '&' equalityExpression )* ;
    public final BeneratorParser.andExpression_return andExpression() throws RecognitionException {
        BeneratorParser.andExpression_return retval = new BeneratorParser.andExpression_return();
        retval.start = input.LT(1);
        int andExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal36=null;
        BeneratorParser.equalityExpression_return equalityExpression35 = null;

        BeneratorParser.equalityExpression_return equalityExpression37 = null;


        Object char_literal36_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 12) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:140:5: ( equalityExpression ( '&' equalityExpression )* )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:140:9: equalityExpression ( '&' equalityExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_equalityExpression_in_andExpression374);
            equalityExpression35=equalityExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, equalityExpression35.getTree());
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:140:28: ( '&' equalityExpression )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==AMP) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:140:29: '&' equalityExpression
            	    {
            	    char_literal36=(Token)match(input,AMP,FOLLOW_AMP_in_andExpression377); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal36_tree = (Object)adaptor.create(char_literal36);
            	    root_0 = (Object)adaptor.becomeRoot(char_literal36_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_equalityExpression_in_andExpression380);
            	    equalityExpression37=equalityExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, equalityExpression37.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 12, andExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "andExpression"

    public static class equalityExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "equalityExpression"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:143:1: equalityExpression : relationalExpression ( ( '==' | '!=' ) relationalExpression )* ;
    public final BeneratorParser.equalityExpression_return equalityExpression() throws RecognitionException {
        BeneratorParser.equalityExpression_return retval = new BeneratorParser.equalityExpression_return();
        retval.start = input.LT(1);
        int equalityExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set39=null;
        BeneratorParser.relationalExpression_return relationalExpression38 = null;

        BeneratorParser.relationalExpression_return relationalExpression40 = null;


        Object set39_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 13) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:144:5: ( relationalExpression ( ( '==' | '!=' ) relationalExpression )* )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:144:9: relationalExpression ( ( '==' | '!=' ) relationalExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_relationalExpression_in_equalityExpression402);
            relationalExpression38=relationalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalExpression38.getTree());
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:144:30: ( ( '==' | '!=' ) relationalExpression )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==EQEQ||LA10_0==BANGEQ) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:144:31: ( '==' | '!=' ) relationalExpression
            	    {
            	    set39=(Token)input.LT(1);
            	    set39=(Token)input.LT(1);
            	    if ( input.LA(1)==EQEQ||input.LA(1)==BANGEQ ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set39), root_0);
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_relationalExpression_in_equalityExpression414);
            	    relationalExpression40=relationalExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalExpression40.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 13, equalityExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "equalityExpression"

    public static class relationalExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "relationalExpression"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:147:1: relationalExpression : shiftExpression ( ( '<=' | '>=' | '<' | '>' ) shiftExpression )* ;
    public final BeneratorParser.relationalExpression_return relationalExpression() throws RecognitionException {
        BeneratorParser.relationalExpression_return retval = new BeneratorParser.relationalExpression_return();
        retval.start = input.LT(1);
        int relationalExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set42=null;
        BeneratorParser.shiftExpression_return shiftExpression41 = null;

        BeneratorParser.shiftExpression_return shiftExpression43 = null;


        Object set42_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 14) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:148:5: ( shiftExpression ( ( '<=' | '>=' | '<' | '>' ) shiftExpression )* )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:148:9: shiftExpression ( ( '<=' | '>=' | '<' | '>' ) shiftExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_shiftExpression_in_relationalExpression436);
            shiftExpression41=shiftExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpression41.getTree());
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:148:25: ( ( '<=' | '>=' | '<' | '>' ) shiftExpression )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==GT||(LA11_0>=GE && LA11_0<=LE)) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:148:26: ( '<=' | '>=' | '<' | '>' ) shiftExpression
            	    {
            	    set42=(Token)input.LT(1);
            	    set42=(Token)input.LT(1);
            	    if ( input.LA(1)==GT||(input.LA(1)>=GE && input.LA(1)<=LE) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set42), root_0);
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_shiftExpression_in_relationalExpression456);
            	    shiftExpression43=shiftExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpression43.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 14, relationalExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "relationalExpression"

    public static class shiftExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "shiftExpression"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:151:1: shiftExpression : additiveExpression ( ( '<<' | '>>>' | '>>' ) additiveExpression )* ;
    public final BeneratorParser.shiftExpression_return shiftExpression() throws RecognitionException {
        BeneratorParser.shiftExpression_return retval = new BeneratorParser.shiftExpression_return();
        retval.start = input.LT(1);
        int shiftExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set45=null;
        BeneratorParser.additiveExpression_return additiveExpression44 = null;

        BeneratorParser.additiveExpression_return additiveExpression46 = null;


        Object set45_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 15) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:152:5: ( additiveExpression ( ( '<<' | '>>>' | '>>' ) additiveExpression )* )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:152:9: additiveExpression ( ( '<<' | '>>>' | '>>' ) additiveExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_additiveExpression_in_shiftExpression478);
            additiveExpression44=additiveExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression44.getTree());
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:152:28: ( ( '<<' | '>>>' | '>>' ) additiveExpression )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( ((LA12_0>=SHIFT_RIGHT && LA12_0<=SHIFT_LEFT)) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:152:29: ( '<<' | '>>>' | '>>' ) additiveExpression
            	    {
            	    set45=(Token)input.LT(1);
            	    set45=(Token)input.LT(1);
            	    if ( (input.LA(1)>=SHIFT_RIGHT && input.LA(1)<=SHIFT_LEFT) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set45), root_0);
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression494);
            	    additiveExpression46=additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression46.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 15, shiftExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "shiftExpression"

    public static class additiveExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "additiveExpression"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:155:1: additiveExpression : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* ;
    public final BeneratorParser.additiveExpression_return additiveExpression() throws RecognitionException {
        BeneratorParser.additiveExpression_return retval = new BeneratorParser.additiveExpression_return();
        retval.start = input.LT(1);
        int additiveExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set48=null;
        BeneratorParser.multiplicativeExpression_return multiplicativeExpression47 = null;

        BeneratorParser.multiplicativeExpression_return multiplicativeExpression49 = null;


        Object set48_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 16) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:156:5: ( multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:156:9: multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression516);
            multiplicativeExpression47=multiplicativeExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression47.getTree());
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:156:34: ( ( '+' | '-' ) multiplicativeExpression )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( ((LA13_0>=PLUS && LA13_0<=SUB)) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:156:35: ( '+' | '-' ) multiplicativeExpression
            	    {
            	    set48=(Token)input.LT(1);
            	    set48=(Token)input.LT(1);
            	    if ( (input.LA(1)>=PLUS && input.LA(1)<=SUB) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set48), root_0);
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression528);
            	    multiplicativeExpression49=multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression49.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 16, additiveExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "additiveExpression"

    public static class multiplicativeExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "multiplicativeExpression"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:159:1: multiplicativeExpression : unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* ;
    public final BeneratorParser.multiplicativeExpression_return multiplicativeExpression() throws RecognitionException {
        BeneratorParser.multiplicativeExpression_return retval = new BeneratorParser.multiplicativeExpression_return();
        retval.start = input.LT(1);
        int multiplicativeExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set51=null;
        BeneratorParser.unaryExpression_return unaryExpression50 = null;

        BeneratorParser.unaryExpression_return unaryExpression52 = null;


        Object set51_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 17) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:160:5: ( unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:160:9: unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression550);
            unaryExpression50=unaryExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression50.getTree());
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:160:25: ( ( '*' | '/' | '%' ) unaryExpression )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( ((LA14_0>=STAR && LA14_0<=SLASH)||LA14_0==PERCENT) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:160:26: ( '*' | '/' | '%' ) unaryExpression
            	    {
            	    set51=(Token)input.LT(1);
            	    set51=(Token)input.LT(1);
            	    if ( (input.LA(1)>=STAR && input.LA(1)<=SLASH)||input.LA(1)==PERCENT ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set51), root_0);
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression566);
            	    unaryExpression52=unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression52.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 17, multiplicativeExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "multiplicativeExpression"

    public static class unaryExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unaryExpression"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:167:1: unaryExpression : ( '-' castExpression -> ^( NEGATION castExpression ) | '~' castExpression | '!' castExpression | castExpression );
    public final BeneratorParser.unaryExpression_return unaryExpression() throws RecognitionException {
        BeneratorParser.unaryExpression_return retval = new BeneratorParser.unaryExpression_return();
        retval.start = input.LT(1);
        int unaryExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal53=null;
        Token char_literal55=null;
        Token char_literal57=null;
        BeneratorParser.castExpression_return castExpression54 = null;

        BeneratorParser.castExpression_return castExpression56 = null;

        BeneratorParser.castExpression_return castExpression58 = null;

        BeneratorParser.castExpression_return castExpression59 = null;


        Object char_literal53_tree=null;
        Object char_literal55_tree=null;
        Object char_literal57_tree=null;
        RewriteRuleTokenStream stream_SUB=new RewriteRuleTokenStream(adaptor,"token SUB");
        RewriteRuleSubtreeStream stream_castExpression=new RewriteRuleSubtreeStream(adaptor,"rule castExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 18) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:168:5: ( '-' castExpression -> ^( NEGATION castExpression ) | '~' castExpression | '!' castExpression | castExpression )
            int alt15=4;
            switch ( input.LA(1) ) {
            case SUB:
                {
                alt15=1;
                }
                break;
            case TILDE:
                {
                alt15=2;
                }
                break;
            case BANG:
                {
                alt15=3;
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
                alt15=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:168:9: '-' castExpression
                    {
                    char_literal53=(Token)match(input,SUB,FOLLOW_SUB_in_unaryExpression592); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SUB.add(char_literal53);

                    pushFollow(FOLLOW_castExpression_in_unaryExpression594);
                    castExpression54=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_castExpression.add(castExpression54.getTree());


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
                    // 168:28: -> ^( NEGATION castExpression )
                    {
                        // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:168:31: ^( NEGATION castExpression )
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
                    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:169:9: '~' castExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal55=(Token)match(input,TILDE,FOLLOW_TILDE_in_unaryExpression612); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal55_tree = (Object)adaptor.create(char_literal55);
                    root_0 = (Object)adaptor.becomeRoot(char_literal55_tree, root_0);
                    }
                    pushFollow(FOLLOW_castExpression_in_unaryExpression615);
                    castExpression56=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, castExpression56.getTree());

                    }
                    break;
                case 3 :
                    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:170:9: '!' castExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal57=(Token)match(input,BANG,FOLLOW_BANG_in_unaryExpression625); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal57_tree = (Object)adaptor.create(char_literal57);
                    root_0 = (Object)adaptor.becomeRoot(char_literal57_tree, root_0);
                    }
                    pushFollow(FOLLOW_castExpression_in_unaryExpression628);
                    castExpression58=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, castExpression58.getTree());

                    }
                    break;
                case 4 :
                    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:171:9: castExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_castExpression_in_unaryExpression638);
                    castExpression59=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, castExpression59.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 18, unaryExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "unaryExpression"

    public static class castExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "castExpression"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:174:1: castExpression : ( '(' type ')' postfixExpression -> ^( CAST type postfixExpression ) | postfixExpression );
    public final BeneratorParser.castExpression_return castExpression() throws RecognitionException {
        BeneratorParser.castExpression_return retval = new BeneratorParser.castExpression_return();
        retval.start = input.LT(1);
        int castExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal60=null;
        Token char_literal62=null;
        BeneratorParser.type_return type61 = null;

        BeneratorParser.postfixExpression_return postfixExpression63 = null;

        BeneratorParser.postfixExpression_return postfixExpression64 = null;


        Object char_literal60_tree=null;
        Object char_literal62_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_type=new RewriteRuleSubtreeStream(adaptor,"rule type");
        RewriteRuleSubtreeStream stream_postfixExpression=new RewriteRuleSubtreeStream(adaptor,"rule postfixExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 19) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:175:5: ( '(' type ')' postfixExpression -> ^( CAST type postfixExpression ) | postfixExpression )
            int alt16=2;
            alt16 = dfa16.predict(input);
            switch (alt16) {
                case 1 :
                    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:175:9: '(' type ')' postfixExpression
                    {
                    char_literal60=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_castExpression658); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAREN.add(char_literal60);

                    pushFollow(FOLLOW_type_in_castExpression660);
                    type61=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_type.add(type61.getTree());
                    char_literal62=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_castExpression662); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAREN.add(char_literal62);

                    pushFollow(FOLLOW_postfixExpression_in_castExpression664);
                    postfixExpression63=postfixExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_postfixExpression.add(postfixExpression63.getTree());


                    // AST REWRITE
                    // elements: postfixExpression, type
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 175:40: -> ^( CAST type postfixExpression )
                    {
                        // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:175:43: ^( CAST type postfixExpression )
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
                    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:176:9: postfixExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_postfixExpression_in_castExpression684);
                    postfixExpression64=postfixExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, postfixExpression64.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 19, castExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "castExpression"

    public static class type_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "type"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:179:1: type : qualifiedName -> ^( TYPE qualifiedName ) ;
    public final BeneratorParser.type_return type() throws RecognitionException {
        BeneratorParser.type_return retval = new BeneratorParser.type_return();
        retval.start = input.LT(1);
        int type_StartIndex = input.index();
        Object root_0 = null;

        BeneratorParser.qualifiedName_return qualifiedName65 = null;


        RewriteRuleSubtreeStream stream_qualifiedName=new RewriteRuleSubtreeStream(adaptor,"rule qualifiedName");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 20) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:180:5: ( qualifiedName -> ^( TYPE qualifiedName ) )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:180:9: qualifiedName
            {
            pushFollow(FOLLOW_qualifiedName_in_type703);
            qualifiedName65=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_qualifiedName.add(qualifiedName65.getTree());


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
            // 180:23: -> ^( TYPE qualifiedName )
            {
                // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:180:26: ^( TYPE qualifiedName )
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
            if ( state.backtracking>0 ) { memoize(input, 20, type_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "type"

    public static class postfixExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "postfixExpression"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:182:1: postfixExpression : ( primary -> primary ) ( '[' expression ']' -> ^( INDEX $postfixExpression expression ) | '.' IDENTIFIER arguments -> ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments ) | '.' IDENTIFIER -> ^( FIELD $postfixExpression IDENTIFIER ) )* ;
    public final BeneratorParser.postfixExpression_return postfixExpression() throws RecognitionException {
        BeneratorParser.postfixExpression_return retval = new BeneratorParser.postfixExpression_return();
        retval.start = input.LT(1);
        int postfixExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal67=null;
        Token char_literal69=null;
        Token char_literal70=null;
        Token IDENTIFIER71=null;
        Token char_literal73=null;
        Token IDENTIFIER74=null;
        BeneratorParser.primary_return primary66 = null;

        BeneratorParser.expression_return expression68 = null;

        BeneratorParser.arguments_return arguments72 = null;


        Object char_literal67_tree=null;
        Object char_literal69_tree=null;
        Object char_literal70_tree=null;
        Object IDENTIFIER71_tree=null;
        Object char_literal73_tree=null;
        Object IDENTIFIER74_tree=null;
        RewriteRuleTokenStream stream_LBRACKET=new RewriteRuleTokenStream(adaptor,"token LBRACKET");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleTokenStream stream_RBRACKET=new RewriteRuleTokenStream(adaptor,"token RBRACKET");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_arguments=new RewriteRuleSubtreeStream(adaptor,"rule arguments");
        RewriteRuleSubtreeStream stream_primary=new RewriteRuleSubtreeStream(adaptor,"rule primary");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 21) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:183:5: ( ( primary -> primary ) ( '[' expression ']' -> ^( INDEX $postfixExpression expression ) | '.' IDENTIFIER arguments -> ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments ) | '.' IDENTIFIER -> ^( FIELD $postfixExpression IDENTIFIER ) )* )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:183:9: ( primary -> primary ) ( '[' expression ']' -> ^( INDEX $postfixExpression expression ) | '.' IDENTIFIER arguments -> ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments ) | '.' IDENTIFIER -> ^( FIELD $postfixExpression IDENTIFIER ) )*
            {
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:183:9: ( primary -> primary )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:183:10: primary
            {
            pushFollow(FOLLOW_primary_in_postfixExpression726);
            primary66=primary();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_primary.add(primary66.getTree());


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
            // 183:18: -> primary
            {
                adaptor.addChild(root_0, stream_primary.nextTree());

            }

            retval.tree = root_0;}
            }

            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:184:9: ( '[' expression ']' -> ^( INDEX $postfixExpression expression ) | '.' IDENTIFIER arguments -> ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments ) | '.' IDENTIFIER -> ^( FIELD $postfixExpression IDENTIFIER ) )*
            loop17:
            do {
                int alt17=4;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==LBRACKET) ) {
                    alt17=1;
                }
                else if ( (LA17_0==DOT) ) {
                    int LA17_3 = input.LA(2);

                    if ( (LA17_3==IDENTIFIER) ) {
                        int LA17_4 = input.LA(3);

                        if ( (LA17_4==EOF||LA17_4==RPAREN||(LA17_4>=LBRACKET && LA17_4<=RBRACKET)||(LA17_4>=COMMA && LA17_4<=DOT)||(LA17_4>=QUES && LA17_4<=PERCENT)||(LA17_4>=BANGEQ && LA17_4<=LE)) ) {
                            alt17=3;
                        }
                        else if ( (LA17_4==LPAREN) ) {
                            alt17=2;
                        }


                    }


                }


                switch (alt17) {
            	case 1 :
            	    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:185:13: '[' expression ']'
            	    {
            	    char_literal67=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_postfixExpression755); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_LBRACKET.add(char_literal67);

            	    pushFollow(FOLLOW_expression_in_postfixExpression757);
            	    expression68=expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_expression.add(expression68.getTree());
            	    char_literal69=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_postfixExpression759); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_RBRACKET.add(char_literal69);



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
            	    // 185:32: -> ^( INDEX $postfixExpression expression )
            	    {
            	        // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:185:35: ^( INDEX $postfixExpression expression )
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
            	    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:186:13: '.' IDENTIFIER arguments
            	    {
            	    char_literal70=(Token)match(input,DOT,FOLLOW_DOT_in_postfixExpression784); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(char_literal70);

            	    IDENTIFIER71=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_postfixExpression786); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER71);

            	    pushFollow(FOLLOW_arguments_in_postfixExpression788);
            	    arguments72=arguments();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_arguments.add(arguments72.getTree());


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
            	    // 186:37: -> ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments )
            	    {
            	        // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:186:40: ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments )
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
            	    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:187:13: '.' IDENTIFIER
            	    {
            	    char_literal73=(Token)match(input,DOT,FOLLOW_DOT_in_postfixExpression814); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(char_literal73);

            	    IDENTIFIER74=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_postfixExpression816); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER74);



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
            	    // 187:28: -> ^( FIELD $postfixExpression IDENTIFIER )
            	    {
            	        // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:187:31: ^( FIELD $postfixExpression IDENTIFIER )
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
            	    break loop17;
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
            if ( state.backtracking>0 ) { memoize(input, 21, postfixExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "postfixExpression"

    public static class primary_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "primary"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:191:1: primary : ( '(' expression ')' | literal | creator | qualifiedName arguments -> ^( INVOCATION qualifiedName arguments ) | qualifiedName );
    public final BeneratorParser.primary_return primary() throws RecognitionException {
        BeneratorParser.primary_return retval = new BeneratorParser.primary_return();
        retval.start = input.LT(1);
        int primary_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal75=null;
        Token char_literal77=null;
        BeneratorParser.expression_return expression76 = null;

        BeneratorParser.literal_return literal78 = null;

        BeneratorParser.creator_return creator79 = null;

        BeneratorParser.qualifiedName_return qualifiedName80 = null;

        BeneratorParser.arguments_return arguments81 = null;

        BeneratorParser.qualifiedName_return qualifiedName82 = null;


        Object char_literal75_tree=null;
        Object char_literal77_tree=null;
        RewriteRuleSubtreeStream stream_arguments=new RewriteRuleSubtreeStream(adaptor,"rule arguments");
        RewriteRuleSubtreeStream stream_qualifiedName=new RewriteRuleSubtreeStream(adaptor,"rule qualifiedName");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 22) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:192:5: ( '(' expression ')' | literal | creator | qualifiedName arguments -> ^( INVOCATION qualifiedName arguments ) | qualifiedName )
            int alt18=5;
            switch ( input.LA(1) ) {
            case LPAREN:
                {
                alt18=1;
                }
                break;
            case INTLITERAL:
            case DECIMALLITERAL:
            case STRINGLITERAL:
            case BOOLEANLITERAL:
            case NULL:
                {
                alt18=2;
                }
                break;
            case 66:
                {
                alt18=3;
                }
                break;
            case IDENTIFIER:
                {
                int LA18_4 = input.LA(2);

                if ( (synpred34_Benerator()) ) {
                    alt18=4;
                }
                else if ( (true) ) {
                    alt18=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 18, 4, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:192:9: '(' expression ')'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal75=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_primary858); if (state.failed) return retval;
                    pushFollow(FOLLOW_expression_in_primary861);
                    expression76=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression76.getTree());
                    char_literal77=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_primary863); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:193:9: literal
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_literal_in_primary874);
                    literal78=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal78.getTree());

                    }
                    break;
                case 3 :
                    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:194:7: creator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_creator_in_primary882);
                    creator79=creator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, creator79.getTree());

                    }
                    break;
                case 4 :
                    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:195:9: qualifiedName arguments
                    {
                    pushFollow(FOLLOW_qualifiedName_in_primary892);
                    qualifiedName80=qualifiedName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_qualifiedName.add(qualifiedName80.getTree());
                    pushFollow(FOLLOW_arguments_in_primary894);
                    arguments81=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arguments.add(arguments81.getTree());


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
                    // 195:33: -> ^( INVOCATION qualifiedName arguments )
                    {
                        // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:195:36: ^( INVOCATION qualifiedName arguments )
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
                    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:196:9: qualifiedName
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_qualifiedName_in_primary914);
                    qualifiedName82=qualifiedName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName82.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 22, primary_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "primary"

    public static class creator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "creator"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:199:1: creator : ( 'new' qualifiedName arguments -> ^( CONSTRUCTOR qualifiedName arguments ) | 'new' qualifiedName '[' assignment ( ',' assignment )* ']' -> ^( BEAN qualifiedName ( assignment )* ) );
    public final BeneratorParser.creator_return creator() throws RecognitionException {
        BeneratorParser.creator_return retval = new BeneratorParser.creator_return();
        retval.start = input.LT(1);
        int creator_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal83=null;
        Token string_literal86=null;
        Token char_literal88=null;
        Token char_literal90=null;
        Token char_literal92=null;
        BeneratorParser.qualifiedName_return qualifiedName84 = null;

        BeneratorParser.arguments_return arguments85 = null;

        BeneratorParser.qualifiedName_return qualifiedName87 = null;

        BeneratorParser.assignment_return assignment89 = null;

        BeneratorParser.assignment_return assignment91 = null;


        Object string_literal83_tree=null;
        Object string_literal86_tree=null;
        Object char_literal88_tree=null;
        Object char_literal90_tree=null;
        Object char_literal92_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LBRACKET=new RewriteRuleTokenStream(adaptor,"token LBRACKET");
        RewriteRuleTokenStream stream_RBRACKET=new RewriteRuleTokenStream(adaptor,"token RBRACKET");
        RewriteRuleTokenStream stream_66=new RewriteRuleTokenStream(adaptor,"token 66");
        RewriteRuleSubtreeStream stream_arguments=new RewriteRuleSubtreeStream(adaptor,"rule arguments");
        RewriteRuleSubtreeStream stream_assignment=new RewriteRuleSubtreeStream(adaptor,"rule assignment");
        RewriteRuleSubtreeStream stream_qualifiedName=new RewriteRuleSubtreeStream(adaptor,"rule qualifiedName");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 23) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:200:5: ( 'new' qualifiedName arguments -> ^( CONSTRUCTOR qualifiedName arguments ) | 'new' qualifiedName '[' assignment ( ',' assignment )* ']' -> ^( BEAN qualifiedName ( assignment )* ) )
            int alt20=2;
            alt20 = dfa20.predict(input);
            switch (alt20) {
                case 1 :
                    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:200:9: 'new' qualifiedName arguments
                    {
                    string_literal83=(Token)match(input,66,FOLLOW_66_in_creator933); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_66.add(string_literal83);

                    pushFollow(FOLLOW_qualifiedName_in_creator935);
                    qualifiedName84=qualifiedName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_qualifiedName.add(qualifiedName84.getTree());
                    pushFollow(FOLLOW_arguments_in_creator937);
                    arguments85=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arguments.add(arguments85.getTree());


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
                    // 200:39: -> ^( CONSTRUCTOR qualifiedName arguments )
                    {
                        // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:200:42: ^( CONSTRUCTOR qualifiedName arguments )
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
                    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:201:9: 'new' qualifiedName '[' assignment ( ',' assignment )* ']'
                    {
                    string_literal86=(Token)match(input,66,FOLLOW_66_in_creator957); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_66.add(string_literal86);

                    pushFollow(FOLLOW_qualifiedName_in_creator959);
                    qualifiedName87=qualifiedName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_qualifiedName.add(qualifiedName87.getTree());
                    char_literal88=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_creator961); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LBRACKET.add(char_literal88);

                    pushFollow(FOLLOW_assignment_in_creator963);
                    assignment89=assignment();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_assignment.add(assignment89.getTree());
                    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:201:44: ( ',' assignment )*
                    loop19:
                    do {
                        int alt19=2;
                        int LA19_0 = input.LA(1);

                        if ( (LA19_0==COMMA) ) {
                            alt19=1;
                        }


                        switch (alt19) {
                    	case 1 :
                    	    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:201:45: ',' assignment
                    	    {
                    	    char_literal90=(Token)match(input,COMMA,FOLLOW_COMMA_in_creator966); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(char_literal90);

                    	    pushFollow(FOLLOW_assignment_in_creator968);
                    	    assignment91=assignment();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_assignment.add(assignment91.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop19;
                        }
                    } while (true);

                    char_literal92=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_creator972); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RBRACKET.add(char_literal92);



                    // AST REWRITE
                    // elements: assignment, qualifiedName
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 201:66: -> ^( BEAN qualifiedName ( assignment )* )
                    {
                        // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:201:69: ^( BEAN qualifiedName ( assignment )* )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(BEAN, "BEAN"), root_1);

                        adaptor.addChild(root_1, stream_qualifiedName.nextTree());
                        // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:201:90: ( assignment )*
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
            if ( state.backtracking>0 ) { memoize(input, 23, creator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "creator"

    public static class arguments_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "arguments"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:204:1: arguments : '(' ( expression ( ',' expression )* )? ')' -> ^( ARGUMENTS ( expression )* ) ;
    public final BeneratorParser.arguments_return arguments() throws RecognitionException {
        BeneratorParser.arguments_return retval = new BeneratorParser.arguments_return();
        retval.start = input.LT(1);
        int arguments_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal93=null;
        Token char_literal95=null;
        Token char_literal97=null;
        BeneratorParser.expression_return expression94 = null;

        BeneratorParser.expression_return expression96 = null;


        Object char_literal93_tree=null;
        Object char_literal95_tree=null;
        Object char_literal97_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 24) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:205:5: ( '(' ( expression ( ',' expression )* )? ')' -> ^( ARGUMENTS ( expression )* ) )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:205:9: '(' ( expression ( ',' expression )* )? ')'
            {
            char_literal93=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_arguments1002); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(char_literal93);

            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:205:13: ( expression ( ',' expression )* )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( ((LA22_0>=IDENTIFIER && LA22_0<=NULL)||LA22_0==LPAREN||(LA22_0>=BANG && LA22_0<=TILDE)||LA22_0==SUB||LA22_0==66) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:205:14: expression ( ',' expression )*
                    {
                    pushFollow(FOLLOW_expression_in_arguments1005);
                    expression94=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression94.getTree());
                    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:205:25: ( ',' expression )*
                    loop21:
                    do {
                        int alt21=2;
                        int LA21_0 = input.LA(1);

                        if ( (LA21_0==COMMA) ) {
                            alt21=1;
                        }


                        switch (alt21) {
                    	case 1 :
                    	    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:205:26: ',' expression
                    	    {
                    	    char_literal95=(Token)match(input,COMMA,FOLLOW_COMMA_in_arguments1008); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(char_literal95);

                    	    pushFollow(FOLLOW_expression_in_arguments1010);
                    	    expression96=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expression.add(expression96.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop21;
                        }
                    } while (true);


                    }
                    break;

            }

            char_literal97=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_arguments1016); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(char_literal97);



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
            // 205:49: -> ^( ARGUMENTS ( expression )* )
            {
                // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:205:52: ^( ARGUMENTS ( expression )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ARGUMENTS, "ARGUMENTS"), root_1);

                // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:205:64: ( expression )*
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
            if ( state.backtracking>0 ) { memoize(input, 24, arguments_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "arguments"

    public static class qualifiedName_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "qualifiedName"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:207:1: qualifiedName : IDENTIFIER ( '.' IDENTIFIER )* -> ^( QUALIFIEDNAME ( IDENTIFIER )* ) ;
    public final BeneratorParser.qualifiedName_return qualifiedName() throws RecognitionException {
        BeneratorParser.qualifiedName_return retval = new BeneratorParser.qualifiedName_return();
        retval.start = input.LT(1);
        int qualifiedName_StartIndex = input.index();
        Object root_0 = null;

        Token IDENTIFIER98=null;
        Token char_literal99=null;
        Token IDENTIFIER100=null;

        Object IDENTIFIER98_tree=null;
        Object char_literal99_tree=null;
        Object IDENTIFIER100_tree=null;
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 25) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:208:2: ( IDENTIFIER ( '.' IDENTIFIER )* -> ^( QUALIFIEDNAME ( IDENTIFIER )* ) )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:208:6: IDENTIFIER ( '.' IDENTIFIER )*
            {
            IDENTIFIER98=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedName1040); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER98);

            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:208:17: ( '.' IDENTIFIER )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==DOT) ) {
                    int LA23_2 = input.LA(2);

                    if ( (LA23_2==IDENTIFIER) ) {
                        int LA23_3 = input.LA(3);

                        if ( (synpred39_Benerator()) ) {
                            alt23=1;
                        }


                    }


                }


                switch (alt23) {
            	case 1 :
            	    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:208:18: '.' IDENTIFIER
            	    {
            	    char_literal99=(Token)match(input,DOT,FOLLOW_DOT_in_qualifiedName1043); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(char_literal99);

            	    IDENTIFIER100=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedName1045); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER100);


            	    }
            	    break;

            	default :
            	    break loop23;
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
            // 208:35: -> ^( QUALIFIEDNAME ( IDENTIFIER )* )
            {
                // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:208:38: ^( QUALIFIEDNAME ( IDENTIFIER )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(QUALIFIEDNAME, "QUALIFIEDNAME"), root_1);

                // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:208:54: ( IDENTIFIER )*
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
            if ( state.backtracking>0 ) { memoize(input, 25, qualifiedName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "qualifiedName"

    public static class literal_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "literal"
    // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:211:1: literal : ( INTLITERAL | DECIMALLITERAL | STRINGLITERAL | BOOLEANLITERAL | NULL );
    public final BeneratorParser.literal_return literal() throws RecognitionException {
        BeneratorParser.literal_return retval = new BeneratorParser.literal_return();
        retval.start = input.LT(1);
        int literal_StartIndex = input.index();
        Object root_0 = null;

        Token set101=null;

        Object set101_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 26) ) { return retval; }
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:212:5: ( INTLITERAL | DECIMALLITERAL | STRINGLITERAL | BOOLEANLITERAL | NULL )
            // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:
            {
            root_0 = (Object)adaptor.nil();

            set101=(Token)input.LT(1);
            if ( (input.LA(1)>=INTLITERAL && input.LA(1)<=NULL) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set101));
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
            if ( state.backtracking>0 ) { memoize(input, 26, literal_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "literal"

    // $ANTLR start synpred34_Benerator
    public final void synpred34_Benerator_fragment() throws RecognitionException {   
        // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:195:9: ( qualifiedName arguments )
        // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:195:9: qualifiedName arguments
        {
        pushFollow(FOLLOW_qualifiedName_in_synpred34_Benerator892);
        qualifiedName();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_arguments_in_synpred34_Benerator894);
        arguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred34_Benerator

    // $ANTLR start synpred39_Benerator
    public final void synpred39_Benerator_fragment() throws RecognitionException {   
        // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:208:18: ( '.' IDENTIFIER )
        // benerator/src/main/resources/org/databene/benerator/script/Benerator.g:208:18: '.' IDENTIFIER
        {
        match(input,DOT,FOLLOW_DOT_in_synpred39_Benerator1043); if (state.failed) return ;
        match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred39_Benerator1045); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred39_Benerator

    // Delegated rules

    public final boolean synpred39_Benerator() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred39_Benerator_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred34_Benerator() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred34_Benerator_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA16 dfa16 = new DFA16(this);
    protected DFA20 dfa20 = new DFA20(this);
    static final String DFA16_eotS =
        "\10\uffff";
    static final String DFA16_eofS =
        "\5\uffff\1\2\2\uffff";
    static final String DFA16_minS =
        "\2\4\1\uffff\1\35\2\4\1\35\1\uffff";
    static final String DFA16_maxS =
        "\2\102\1\uffff\1\76\1\4\1\102\1\76\1\uffff";
    static final String DFA16_acceptS =
        "\2\uffff\1\2\4\uffff\1\1";
    static final String DFA16_specialS =
        "\10\uffff}>";
    static final String[] DFA16_transitionS = {
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

    static final short[] DFA16_eot = DFA.unpackEncodedString(DFA16_eotS);
    static final short[] DFA16_eof = DFA.unpackEncodedString(DFA16_eofS);
    static final char[] DFA16_min = DFA.unpackEncodedStringToUnsignedChars(DFA16_minS);
    static final char[] DFA16_max = DFA.unpackEncodedStringToUnsignedChars(DFA16_maxS);
    static final short[] DFA16_accept = DFA.unpackEncodedString(DFA16_acceptS);
    static final short[] DFA16_special = DFA.unpackEncodedString(DFA16_specialS);
    static final short[][] DFA16_transition;

    static {
        int numStates = DFA16_transitionS.length;
        DFA16_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA16_transition[i] = DFA.unpackEncodedString(DFA16_transitionS[i]);
        }
    }

    class DFA16 extends DFA {

        public DFA16(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 16;
            this.eot = DFA16_eot;
            this.eof = DFA16_eof;
            this.min = DFA16_min;
            this.max = DFA16_max;
            this.accept = DFA16_accept;
            this.special = DFA16_special;
            this.transition = DFA16_transition;
        }
        public String getDescription() {
            return "174:1: castExpression : ( '(' type ')' postfixExpression -> ^( CAST type postfixExpression ) | postfixExpression );";
        }
    }
    static final String DFA20_eotS =
        "\7\uffff";
    static final String DFA20_eofS =
        "\7\uffff";
    static final String DFA20_minS =
        "\1\102\1\4\1\35\1\4\2\uffff\1\35";
    static final String DFA20_maxS =
        "\1\102\1\4\1\45\1\4\2\uffff\1\45";
    static final String DFA20_acceptS =
        "\4\uffff\1\1\1\2\1\uffff";
    static final String DFA20_specialS =
        "\7\uffff}>";
    static final String[] DFA20_transitionS = {
            "\1\1",
            "\1\2",
            "\1\4\3\uffff\1\5\3\uffff\1\3",
            "\1\6",
            "",
            "",
            "\1\4\3\uffff\1\5\3\uffff\1\3"
    };

    static final short[] DFA20_eot = DFA.unpackEncodedString(DFA20_eotS);
    static final short[] DFA20_eof = DFA.unpackEncodedString(DFA20_eofS);
    static final char[] DFA20_min = DFA.unpackEncodedStringToUnsignedChars(DFA20_minS);
    static final char[] DFA20_max = DFA.unpackEncodedStringToUnsignedChars(DFA20_maxS);
    static final short[] DFA20_accept = DFA.unpackEncodedString(DFA20_acceptS);
    static final short[] DFA20_special = DFA.unpackEncodedString(DFA20_specialS);
    static final short[][] DFA20_transition;

    static {
        int numStates = DFA20_transitionS.length;
        DFA20_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA20_transition[i] = DFA.unpackEncodedString(DFA20_transitionS[i]);
        }
    }

    class DFA20 extends DFA {

        public DFA20(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 20;
            this.eot = DFA20_eot;
            this.eof = DFA20_eof;
            this.min = DFA20_min;
            this.max = DFA20_max;
            this.accept = DFA20_accept;
            this.special = DFA20_special;
            this.transition = DFA20_transition;
        }
        public String getDescription() {
            return "199:1: creator : ( 'new' qualifiedName arguments -> ^( CONSTRUCTOR qualifiedName arguments ) | 'new' qualifiedName '[' assignment ( ',' assignment )* ']' -> ^( BEAN qualifiedName ( assignment )* ) );";
        }
    }
 

    public static final BitSet FOLLOW_transition_in_transitionList90 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_COMMA_in_transitionList93 = new BitSet(new long[]{0x00000000000003E0L});
    public static final BitSet FOLLOW_transition_in_transitionList96 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_literal_in_transition112 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_ARROW_in_transition114 = new BitSet(new long[]{0x00000000000003E0L});
    public static final BitSet FOLLOW_literal_in_transition117 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_LBRACKET_in_transition120 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_transition123 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_RBRACKET_in_transition125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_assignment142 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_EQ_in_assignment144 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_assignment147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_beanSpec_in_beanSpecList161 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_COMMA_in_beanSpecList164 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_beanSpec_in_beanSpecList167 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_expression_in_beanSpec183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression226 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_QUES_in_conditionalExpression229 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression232 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_COLON_in_conditionalExpression234 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalExpression237 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression263 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_BARBAR_in_conditionalOrExpression266 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression269 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression292 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_AMPAMP_in_conditionalAndExpression295 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression298 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression320 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_BAR_in_inclusiveOrExpression323 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression326 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression347 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_CARET_in_exclusiveOrExpression350 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression353 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression374 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_AMP_in_andExpression377 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression380 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression402 = new BitSet(new long[]{0x0080080000000002L});
    public static final BitSet FOLLOW_set_in_equalityExpression405 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression414 = new BitSet(new long[]{0x0080080000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression436 = new BitSet(new long[]{0x7100000000000002L});
    public static final BitSet FOLLOW_set_in_relationalExpression439 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression456 = new BitSet(new long[]{0x7100000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression478 = new BitSet(new long[]{0x0E00000000000002L});
    public static final BitSet FOLLOW_set_in_shiftExpression481 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression494 = new BitSet(new long[]{0x0E00000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression516 = new BitSet(new long[]{0x0000C00000000002L});
    public static final BitSet FOLLOW_set_in_additiveExpression519 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression528 = new BitSet(new long[]{0x0000C00000000002L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression550 = new BitSet(new long[]{0x0023000000000002L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression553 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression566 = new BitSet(new long[]{0x0023000000000002L});
    public static final BitSet FOLLOW_SUB_in_unaryExpression592 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpression594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_unaryExpression612 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpression615 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_unaryExpression625 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpression628 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpression638 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_castExpression658 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_type_in_castExpression660 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_RPAREN_in_castExpression662 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_postfixExpression_in_castExpression664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_postfixExpression_in_castExpression684 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_type703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_postfixExpression726 = new BitSet(new long[]{0x0000002200000002L});
    public static final BitSet FOLLOW_LBRACKET_in_postfixExpression755 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_postfixExpression757 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_RBRACKET_in_postfixExpression759 = new BitSet(new long[]{0x0000002200000002L});
    public static final BitSet FOLLOW_DOT_in_postfixExpression784 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_postfixExpression786 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_arguments_in_postfixExpression788 = new BitSet(new long[]{0x0000002200000002L});
    public static final BitSet FOLLOW_DOT_in_postfixExpression814 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_postfixExpression816 = new BitSet(new long[]{0x0000002200000002L});
    public static final BitSet FOLLOW_LPAREN_in_primary858 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_primary861 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_RPAREN_in_primary863 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary874 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_creator_in_primary882 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_primary892 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_arguments_in_primary894 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_primary914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_creator933 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_creator935 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_arguments_in_creator937 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_creator957 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_creator959 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_LBRACKET_in_creator961 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_assignment_in_creator963 = new BitSet(new long[]{0x0000001400000000L});
    public static final BitSet FOLLOW_COMMA_in_creator966 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_assignment_in_creator968 = new BitSet(new long[]{0x0000001400000000L});
    public static final BitSet FOLLOW_RBRACKET_in_creator972 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_arguments1002 = new BitSet(new long[]{0x00008180600003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_arguments1005 = new BitSet(new long[]{0x0000001040000000L});
    public static final BitSet FOLLOW_COMMA_in_arguments1008 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_arguments1010 = new BitSet(new long[]{0x0000001040000000L});
    public static final BitSet FOLLOW_RPAREN_in_arguments1016 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedName1040 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_DOT_in_qualifiedName1043 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedName1045 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_set_in_literal0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_synpred34_Benerator892 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_arguments_in_synpred34_Benerator894 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred39_Benerator1043 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_synpred39_Benerator1045 = new BitSet(new long[]{0x0000000000000002L});

}