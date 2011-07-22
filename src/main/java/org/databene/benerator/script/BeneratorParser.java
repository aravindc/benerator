// $ANTLR 3.3 Nov 30, 2010 12:45:30 /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g 2011-07-22 08:59:44

	package org.databene.benerator.script;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

@SuppressWarnings("all") // this is generated code
public class BeneratorParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "IDENTIFIER", "INTLITERAL", "DECIMALLITERAL", "STRINGLITERAL", "BOOLEANLITERAL", "NULL", "TYPE", "NEGATION", "INDEX", "FIELD", "ARGUMENTS", "CAST", "CONSTRUCTOR", "INVOCATION", "SUBINVOCATION", "QUALIFIEDNAME", "BEAN", "BEANSPEC", "HexPrefix", "HexDigit", "Exponent", "EscapeSequence", "WS", "COMMENT", "LINE_COMMENT", "LPAREN", "RPAREN", "LBRACE", "RBRACE", "LBRACKET", "RBRACKET", "SEMI", "COMMA", "DOT", "EQ", "BANG", "TILDE", "QUES", "COLON", "EQEQ", "AMPAMP", "BARBAR", "PLUS", "SUB", "STAR", "SLASH", "AMP", "BAR", "CARET", "PERCENT", "MONKEYS_AT", "BANGEQ", "GT", "SHIFT_RIGHT", "SHIFT_RIGHT2", "SHIFT_LEFT", "GE", "LT", "LE", "ARROW", "IdentifierStart", "IdentifierPart", "'new'"
    };
    public static final int EOF=-1;
    public static final int T__66=66;
    public static final int IDENTIFIER=4;
    public static final int INTLITERAL=5;
    public static final int DECIMALLITERAL=6;
    public static final int STRINGLITERAL=7;
    public static final int BOOLEANLITERAL=8;
    public static final int NULL=9;
    public static final int TYPE=10;
    public static final int NEGATION=11;
    public static final int INDEX=12;
    public static final int FIELD=13;
    public static final int ARGUMENTS=14;
    public static final int CAST=15;
    public static final int CONSTRUCTOR=16;
    public static final int INVOCATION=17;
    public static final int SUBINVOCATION=18;
    public static final int QUALIFIEDNAME=19;
    public static final int BEAN=20;
    public static final int BEANSPEC=21;
    public static final int HexPrefix=22;
    public static final int HexDigit=23;
    public static final int Exponent=24;
    public static final int EscapeSequence=25;
    public static final int WS=26;
    public static final int COMMENT=27;
    public static final int LINE_COMMENT=28;
    public static final int LPAREN=29;
    public static final int RPAREN=30;
    public static final int LBRACE=31;
    public static final int RBRACE=32;
    public static final int LBRACKET=33;
    public static final int RBRACKET=34;
    public static final int SEMI=35;
    public static final int COMMA=36;
    public static final int DOT=37;
    public static final int EQ=38;
    public static final int BANG=39;
    public static final int TILDE=40;
    public static final int QUES=41;
    public static final int COLON=42;
    public static final int EQEQ=43;
    public static final int AMPAMP=44;
    public static final int BARBAR=45;
    public static final int PLUS=46;
    public static final int SUB=47;
    public static final int STAR=48;
    public static final int SLASH=49;
    public static final int AMP=50;
    public static final int BAR=51;
    public static final int CARET=52;
    public static final int PERCENT=53;
    public static final int MONKEYS_AT=54;
    public static final int BANGEQ=55;
    public static final int GT=56;
    public static final int SHIFT_RIGHT=57;
    public static final int SHIFT_RIGHT2=58;
    public static final int SHIFT_LEFT=59;
    public static final int GE=60;
    public static final int LT=61;
    public static final int LE=62;
    public static final int ARROW=63;
    public static final int IdentifierStart=64;
    public static final int IdentifierPart=65;

    // delegates
    // delegators


        public BeneratorParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public BeneratorParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
            this.state.ruleMemo = new HashMap[74+1];
             
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return BeneratorParser.tokenNames; }
    public String getGrammarFileName() { return "/Users/volker/Documents/databene/benerator/src/main/g/Benerator.g"; }


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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:96:1: weightedLiteralList : weightedLiteral ( ',' weightedLiteral )* ;
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
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:101:5: ( weightedLiteral ( ',' weightedLiteral )* )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:101:9: weightedLiteral ( ',' weightedLiteral )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_weightedLiteral_in_weightedLiteralList90);
            weightedLiteral1=weightedLiteral();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, weightedLiteral1.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:101:25: ( ',' weightedLiteral )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==COMMA) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:101:26: ',' weightedLiteral
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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:103:1: weightedLiteral : literal ( '^' expression )? ;
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
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:104:5: ( literal ( '^' expression )? )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:104:9: literal ( '^' expression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_literal_in_weightedLiteral113);
            literal4=literal();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, literal4.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:104:17: ( '^' expression )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==CARET) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:104:18: '^' expression
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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:106:1: transitionList : transition ( ',' transition )* ;
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
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:107:5: ( transition ( ',' transition )* )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:107:9: transition ( ',' transition )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_transition_in_transitionList135);
            transition7=transition();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, transition7.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:107:20: ( ',' transition )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==COMMA) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:107:21: ',' transition
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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:109:1: transition : literal '->' literal ( '^' expression )? ;
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
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:110:5: ( literal '->' literal ( '^' expression )? )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:110:9: literal '->' literal ( '^' expression )?
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
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:110:31: ( '^' expression )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==CARET) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:110:32: '^' expression
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

    public static class beanSpecList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "beanSpecList"
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:112:1: beanSpecList : beanSpec ( ',' beanSpec )* ;
    public final BeneratorParser.beanSpecList_return beanSpecList() throws RecognitionException {
        BeneratorParser.beanSpecList_return retval = new BeneratorParser.beanSpecList_return();
        retval.start = input.LT(1);
        int beanSpecList_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal16=null;
        BeneratorParser.beanSpec_return beanSpec15 = null;

        BeneratorParser.beanSpec_return beanSpec17 = null;


        Object char_literal16_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 5) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:113:5: ( beanSpec ( ',' beanSpec )* )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:113:9: beanSpec ( ',' beanSpec )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_beanSpec_in_beanSpecList184);
            beanSpec15=beanSpec();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, beanSpec15.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:113:18: ( ',' beanSpec )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==COMMA) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:113:19: ',' beanSpec
            	    {
            	    char_literal16=(Token)match(input,COMMA,FOLLOW_COMMA_in_beanSpecList187); if (state.failed) return retval;
            	    pushFollow(FOLLOW_beanSpec_in_beanSpecList190);
            	    beanSpec17=beanSpec();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, beanSpec17.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 5, beanSpecList_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "beanSpecList"

    public static class beanSpec_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "beanSpec"
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:115:1: beanSpec : expression -> ^( BEANSPEC expression ) ;
    public final BeneratorParser.beanSpec_return beanSpec() throws RecognitionException {
        BeneratorParser.beanSpec_return retval = new BeneratorParser.beanSpec_return();
        retval.start = input.LT(1);
        int beanSpec_StartIndex = input.index();
        Object root_0 = null;

        BeneratorParser.expression_return expression18 = null;


        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:116:5: ( expression -> ^( BEANSPEC expression ) )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:116:9: expression
            {
            pushFollow(FOLLOW_expression_in_beanSpec206);
            expression18=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expression.add(expression18.getTree());


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
            // 116:20: -> ^( BEANSPEC expression )
            {
                // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:116:23: ^( BEANSPEC expression )
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
            if ( state.backtracking>0 ) { memoize(input, 6, beanSpec_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "beanSpec"

    public static class expression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expression"
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:118:1: expression : ( conditionalExpression | assignment );
    public final BeneratorParser.expression_return expression() throws RecognitionException {
        BeneratorParser.expression_return retval = new BeneratorParser.expression_return();
        retval.start = input.LT(1);
        int expression_StartIndex = input.index();
        Object root_0 = null;

        BeneratorParser.conditionalExpression_return conditionalExpression19 = null;

        BeneratorParser.assignment_return assignment20 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 7) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:119:5: ( conditionalExpression | assignment )
            int alt6=2;
            alt6 = dfa6.predict(input);
            switch (alt6) {
                case 1 :
                    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:119:9: conditionalExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_conditionalExpression_in_expression229);
                    conditionalExpression19=conditionalExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression19.getTree());

                    }
                    break;
                case 2 :
                    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:120:9: assignment
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_assignment_in_expression239);
                    assignment20=assignment();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, assignment20.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 7, expression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "expression"

    public static class assignment_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "assignment"
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:123:1: assignment : qualifiedName '=' expression ;
    public final BeneratorParser.assignment_return assignment() throws RecognitionException {
        BeneratorParser.assignment_return retval = new BeneratorParser.assignment_return();
        retval.start = input.LT(1);
        int assignment_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal22=null;
        BeneratorParser.qualifiedName_return qualifiedName21 = null;

        BeneratorParser.expression_return expression23 = null;


        Object char_literal22_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 8) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:124:5: ( qualifiedName '=' expression )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:124:9: qualifiedName '=' expression
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_qualifiedName_in_assignment258);
            qualifiedName21=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName21.getTree());
            char_literal22=(Token)match(input,EQ,FOLLOW_EQ_in_assignment260); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal22_tree = (Object)adaptor.create(char_literal22);
            root_0 = (Object)adaptor.becomeRoot(char_literal22_tree, root_0);
            }
            pushFollow(FOLLOW_expression_in_assignment263);
            expression23=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression23.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 8, assignment_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "assignment"

    public static class conditionalExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conditionalExpression"
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:126:1: conditionalExpression : conditionalOrExpression ( '?' expression ':' conditionalExpression )? ;
    public final BeneratorParser.conditionalExpression_return conditionalExpression() throws RecognitionException {
        BeneratorParser.conditionalExpression_return retval = new BeneratorParser.conditionalExpression_return();
        retval.start = input.LT(1);
        int conditionalExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal25=null;
        Token char_literal27=null;
        BeneratorParser.conditionalOrExpression_return conditionalOrExpression24 = null;

        BeneratorParser.expression_return expression26 = null;

        BeneratorParser.conditionalExpression_return conditionalExpression28 = null;


        Object char_literal25_tree=null;
        Object char_literal27_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:127:5: ( conditionalOrExpression ( '?' expression ':' conditionalExpression )? )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:127:9: conditionalOrExpression ( '?' expression ':' conditionalExpression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression278);
            conditionalOrExpression24=conditionalOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalOrExpression24.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:127:33: ( '?' expression ':' conditionalExpression )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==QUES) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:127:34: '?' expression ':' conditionalExpression
                    {
                    char_literal25=(Token)match(input,QUES,FOLLOW_QUES_in_conditionalExpression281); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal25_tree = (Object)adaptor.create(char_literal25);
                    root_0 = (Object)adaptor.becomeRoot(char_literal25_tree, root_0);
                    }
                    pushFollow(FOLLOW_expression_in_conditionalExpression284);
                    expression26=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression26.getTree());
                    char_literal27=(Token)match(input,COLON,FOLLOW_COLON_in_conditionalExpression286); if (state.failed) return retval;
                    pushFollow(FOLLOW_conditionalExpression_in_conditionalExpression289);
                    conditionalExpression28=conditionalExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression28.getTree());

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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:130:1: conditionalOrExpression : conditionalAndExpression ( '||' conditionalAndExpression )* ;
    public final BeneratorParser.conditionalOrExpression_return conditionalOrExpression() throws RecognitionException {
        BeneratorParser.conditionalOrExpression_return retval = new BeneratorParser.conditionalOrExpression_return();
        retval.start = input.LT(1);
        int conditionalOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal30=null;
        BeneratorParser.conditionalAndExpression_return conditionalAndExpression29 = null;

        BeneratorParser.conditionalAndExpression_return conditionalAndExpression31 = null;


        Object string_literal30_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 10) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:131:5: ( conditionalAndExpression ( '||' conditionalAndExpression )* )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:131:9: conditionalAndExpression ( '||' conditionalAndExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression315);
            conditionalAndExpression29=conditionalAndExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression29.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:131:34: ( '||' conditionalAndExpression )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==BARBAR) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:131:35: '||' conditionalAndExpression
            	    {
            	    string_literal30=(Token)match(input,BARBAR,FOLLOW_BARBAR_in_conditionalOrExpression318); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal30_tree = (Object)adaptor.create(string_literal30);
            	    root_0 = (Object)adaptor.becomeRoot(string_literal30_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression321);
            	    conditionalAndExpression31=conditionalAndExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression31.getTree());

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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:134:1: conditionalAndExpression : inclusiveOrExpression ( '&&' inclusiveOrExpression )* ;
    public final BeneratorParser.conditionalAndExpression_return conditionalAndExpression() throws RecognitionException {
        BeneratorParser.conditionalAndExpression_return retval = new BeneratorParser.conditionalAndExpression_return();
        retval.start = input.LT(1);
        int conditionalAndExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal33=null;
        BeneratorParser.inclusiveOrExpression_return inclusiveOrExpression32 = null;

        BeneratorParser.inclusiveOrExpression_return inclusiveOrExpression34 = null;


        Object string_literal33_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 11) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:135:5: ( inclusiveOrExpression ( '&&' inclusiveOrExpression )* )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:135:9: inclusiveOrExpression ( '&&' inclusiveOrExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression344);
            inclusiveOrExpression32=inclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression32.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:135:31: ( '&&' inclusiveOrExpression )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==AMPAMP) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:135:32: '&&' inclusiveOrExpression
            	    {
            	    string_literal33=(Token)match(input,AMPAMP,FOLLOW_AMPAMP_in_conditionalAndExpression347); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal33_tree = (Object)adaptor.create(string_literal33);
            	    root_0 = (Object)adaptor.becomeRoot(string_literal33_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression350);
            	    inclusiveOrExpression34=inclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression34.getTree());

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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:138:1: inclusiveOrExpression : exclusiveOrExpression ( '|' exclusiveOrExpression )* ;
    public final BeneratorParser.inclusiveOrExpression_return inclusiveOrExpression() throws RecognitionException {
        BeneratorParser.inclusiveOrExpression_return retval = new BeneratorParser.inclusiveOrExpression_return();
        retval.start = input.LT(1);
        int inclusiveOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal36=null;
        BeneratorParser.exclusiveOrExpression_return exclusiveOrExpression35 = null;

        BeneratorParser.exclusiveOrExpression_return exclusiveOrExpression37 = null;


        Object char_literal36_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 12) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:139:5: ( exclusiveOrExpression ( '|' exclusiveOrExpression )* )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:139:9: exclusiveOrExpression ( '|' exclusiveOrExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression372);
            exclusiveOrExpression35=exclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression35.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:139:31: ( '|' exclusiveOrExpression )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==BAR) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:139:32: '|' exclusiveOrExpression
            	    {
            	    char_literal36=(Token)match(input,BAR,FOLLOW_BAR_in_inclusiveOrExpression375); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal36_tree = (Object)adaptor.create(char_literal36);
            	    root_0 = (Object)adaptor.becomeRoot(char_literal36_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression378);
            	    exclusiveOrExpression37=exclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression37.getTree());

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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:142:1: exclusiveOrExpression : andExpression ( '^' andExpression )* ;
    public final BeneratorParser.exclusiveOrExpression_return exclusiveOrExpression() throws RecognitionException {
        BeneratorParser.exclusiveOrExpression_return retval = new BeneratorParser.exclusiveOrExpression_return();
        retval.start = input.LT(1);
        int exclusiveOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal39=null;
        BeneratorParser.andExpression_return andExpression38 = null;

        BeneratorParser.andExpression_return andExpression40 = null;


        Object char_literal39_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 13) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:143:5: ( andExpression ( '^' andExpression )* )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:143:9: andExpression ( '^' andExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression399);
            andExpression38=andExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression38.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:143:23: ( '^' andExpression )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==CARET) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:143:24: '^' andExpression
            	    {
            	    char_literal39=(Token)match(input,CARET,FOLLOW_CARET_in_exclusiveOrExpression402); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal39_tree = (Object)adaptor.create(char_literal39);
            	    root_0 = (Object)adaptor.becomeRoot(char_literal39_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression405);
            	    andExpression40=andExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression40.getTree());

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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:146:1: andExpression : equalityExpression ( '&' equalityExpression )* ;
    public final BeneratorParser.andExpression_return andExpression() throws RecognitionException {
        BeneratorParser.andExpression_return retval = new BeneratorParser.andExpression_return();
        retval.start = input.LT(1);
        int andExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal42=null;
        BeneratorParser.equalityExpression_return equalityExpression41 = null;

        BeneratorParser.equalityExpression_return equalityExpression43 = null;


        Object char_literal42_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 14) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:147:5: ( equalityExpression ( '&' equalityExpression )* )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:147:9: equalityExpression ( '&' equalityExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_equalityExpression_in_andExpression426);
            equalityExpression41=equalityExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, equalityExpression41.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:147:28: ( '&' equalityExpression )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==AMP) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:147:29: '&' equalityExpression
            	    {
            	    char_literal42=(Token)match(input,AMP,FOLLOW_AMP_in_andExpression429); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal42_tree = (Object)adaptor.create(char_literal42);
            	    root_0 = (Object)adaptor.becomeRoot(char_literal42_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_equalityExpression_in_andExpression432);
            	    equalityExpression43=equalityExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, equalityExpression43.getTree());

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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:150:1: equalityExpression : relationalExpression ( ( '==' | '!=' ) relationalExpression )* ;
    public final BeneratorParser.equalityExpression_return equalityExpression() throws RecognitionException {
        BeneratorParser.equalityExpression_return retval = new BeneratorParser.equalityExpression_return();
        retval.start = input.LT(1);
        int equalityExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set45=null;
        BeneratorParser.relationalExpression_return relationalExpression44 = null;

        BeneratorParser.relationalExpression_return relationalExpression46 = null;


        Object set45_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 15) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:151:5: ( relationalExpression ( ( '==' | '!=' ) relationalExpression )* )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:151:9: relationalExpression ( ( '==' | '!=' ) relationalExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_relationalExpression_in_equalityExpression454);
            relationalExpression44=relationalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalExpression44.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:151:30: ( ( '==' | '!=' ) relationalExpression )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==EQEQ||LA13_0==BANGEQ) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:151:31: ( '==' | '!=' ) relationalExpression
            	    {
            	    set45=(Token)input.LT(1);
            	    set45=(Token)input.LT(1);
            	    if ( input.LA(1)==EQEQ||input.LA(1)==BANGEQ ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set45), root_0);
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_relationalExpression_in_equalityExpression466);
            	    relationalExpression46=relationalExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalExpression46.getTree());

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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:154:1: relationalExpression : shiftExpression ( ( '<=' | '>=' | '<' | '>' ) shiftExpression )* ;
    public final BeneratorParser.relationalExpression_return relationalExpression() throws RecognitionException {
        BeneratorParser.relationalExpression_return retval = new BeneratorParser.relationalExpression_return();
        retval.start = input.LT(1);
        int relationalExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set48=null;
        BeneratorParser.shiftExpression_return shiftExpression47 = null;

        BeneratorParser.shiftExpression_return shiftExpression49 = null;


        Object set48_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 16) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:155:5: ( shiftExpression ( ( '<=' | '>=' | '<' | '>' ) shiftExpression )* )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:155:9: shiftExpression ( ( '<=' | '>=' | '<' | '>' ) shiftExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_shiftExpression_in_relationalExpression488);
            shiftExpression47=shiftExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpression47.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:155:25: ( ( '<=' | '>=' | '<' | '>' ) shiftExpression )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==GT||(LA14_0>=GE && LA14_0<=LE)) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:155:26: ( '<=' | '>=' | '<' | '>' ) shiftExpression
            	    {
            	    set48=(Token)input.LT(1);
            	    set48=(Token)input.LT(1);
            	    if ( input.LA(1)==GT||(input.LA(1)>=GE && input.LA(1)<=LE) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set48), root_0);
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_shiftExpression_in_relationalExpression508);
            	    shiftExpression49=shiftExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpression49.getTree());

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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:158:1: shiftExpression : additiveExpression ( ( '<<' | '>>>' | '>>' ) additiveExpression )* ;
    public final BeneratorParser.shiftExpression_return shiftExpression() throws RecognitionException {
        BeneratorParser.shiftExpression_return retval = new BeneratorParser.shiftExpression_return();
        retval.start = input.LT(1);
        int shiftExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set51=null;
        BeneratorParser.additiveExpression_return additiveExpression50 = null;

        BeneratorParser.additiveExpression_return additiveExpression52 = null;


        Object set51_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 17) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:159:5: ( additiveExpression ( ( '<<' | '>>>' | '>>' ) additiveExpression )* )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:159:9: additiveExpression ( ( '<<' | '>>>' | '>>' ) additiveExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_additiveExpression_in_shiftExpression530);
            additiveExpression50=additiveExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression50.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:159:28: ( ( '<<' | '>>>' | '>>' ) additiveExpression )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( ((LA15_0>=SHIFT_RIGHT && LA15_0<=SHIFT_LEFT)) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:159:29: ( '<<' | '>>>' | '>>' ) additiveExpression
            	    {
            	    set51=(Token)input.LT(1);
            	    set51=(Token)input.LT(1);
            	    if ( (input.LA(1)>=SHIFT_RIGHT && input.LA(1)<=SHIFT_LEFT) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set51), root_0);
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression546);
            	    additiveExpression52=additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression52.getTree());

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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:162:1: additiveExpression : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* ;
    public final BeneratorParser.additiveExpression_return additiveExpression() throws RecognitionException {
        BeneratorParser.additiveExpression_return retval = new BeneratorParser.additiveExpression_return();
        retval.start = input.LT(1);
        int additiveExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set54=null;
        BeneratorParser.multiplicativeExpression_return multiplicativeExpression53 = null;

        BeneratorParser.multiplicativeExpression_return multiplicativeExpression55 = null;


        Object set54_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 18) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:163:5: ( multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:163:9: multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression568);
            multiplicativeExpression53=multiplicativeExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression53.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:163:34: ( ( '+' | '-' ) multiplicativeExpression )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( ((LA16_0>=PLUS && LA16_0<=SUB)) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:163:35: ( '+' | '-' ) multiplicativeExpression
            	    {
            	    set54=(Token)input.LT(1);
            	    set54=(Token)input.LT(1);
            	    if ( (input.LA(1)>=PLUS && input.LA(1)<=SUB) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set54), root_0);
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression580);
            	    multiplicativeExpression55=multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression55.getTree());

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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:166:1: multiplicativeExpression : unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* ;
    public final BeneratorParser.multiplicativeExpression_return multiplicativeExpression() throws RecognitionException {
        BeneratorParser.multiplicativeExpression_return retval = new BeneratorParser.multiplicativeExpression_return();
        retval.start = input.LT(1);
        int multiplicativeExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set57=null;
        BeneratorParser.unaryExpression_return unaryExpression56 = null;

        BeneratorParser.unaryExpression_return unaryExpression58 = null;


        Object set57_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 19) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:167:5: ( unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:167:9: unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression602);
            unaryExpression56=unaryExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression56.getTree());
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:167:25: ( ( '*' | '/' | '%' ) unaryExpression )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( ((LA17_0>=STAR && LA17_0<=SLASH)||LA17_0==PERCENT) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:167:26: ( '*' | '/' | '%' ) unaryExpression
            	    {
            	    set57=(Token)input.LT(1);
            	    set57=(Token)input.LT(1);
            	    if ( (input.LA(1)>=STAR && input.LA(1)<=SLASH)||input.LA(1)==PERCENT ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set57), root_0);
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression618);
            	    unaryExpression58=unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression58.getTree());

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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:174:1: unaryExpression : ( '-' castExpression -> ^( NEGATION castExpression ) | '~' castExpression | '!' castExpression | castExpression );
    public final BeneratorParser.unaryExpression_return unaryExpression() throws RecognitionException {
        BeneratorParser.unaryExpression_return retval = new BeneratorParser.unaryExpression_return();
        retval.start = input.LT(1);
        int unaryExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal59=null;
        Token char_literal61=null;
        Token char_literal63=null;
        BeneratorParser.castExpression_return castExpression60 = null;

        BeneratorParser.castExpression_return castExpression62 = null;

        BeneratorParser.castExpression_return castExpression64 = null;

        BeneratorParser.castExpression_return castExpression65 = null;


        Object char_literal59_tree=null;
        Object char_literal61_tree=null;
        Object char_literal63_tree=null;
        RewriteRuleTokenStream stream_SUB=new RewriteRuleTokenStream(adaptor,"token SUB");
        RewriteRuleSubtreeStream stream_castExpression=new RewriteRuleSubtreeStream(adaptor,"rule castExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 20) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:175:5: ( '-' castExpression -> ^( NEGATION castExpression ) | '~' castExpression | '!' castExpression | castExpression )
            int alt18=4;
            switch ( input.LA(1) ) {
            case SUB:
                {
                alt18=1;
                }
                break;
            case TILDE:
                {
                alt18=2;
                }
                break;
            case BANG:
                {
                alt18=3;
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
                alt18=4;
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
                    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:175:9: '-' castExpression
                    {
                    char_literal59=(Token)match(input,SUB,FOLLOW_SUB_in_unaryExpression644); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SUB.add(char_literal59);

                    pushFollow(FOLLOW_castExpression_in_unaryExpression646);
                    castExpression60=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_castExpression.add(castExpression60.getTree());


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
                    // 175:28: -> ^( NEGATION castExpression )
                    {
                        // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:175:31: ^( NEGATION castExpression )
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
                    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:176:9: '~' castExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal61=(Token)match(input,TILDE,FOLLOW_TILDE_in_unaryExpression664); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal61_tree = (Object)adaptor.create(char_literal61);
                    root_0 = (Object)adaptor.becomeRoot(char_literal61_tree, root_0);
                    }
                    pushFollow(FOLLOW_castExpression_in_unaryExpression667);
                    castExpression62=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, castExpression62.getTree());

                    }
                    break;
                case 3 :
                    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:177:9: '!' castExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal63=(Token)match(input,BANG,FOLLOW_BANG_in_unaryExpression677); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal63_tree = (Object)adaptor.create(char_literal63);
                    root_0 = (Object)adaptor.becomeRoot(char_literal63_tree, root_0);
                    }
                    pushFollow(FOLLOW_castExpression_in_unaryExpression680);
                    castExpression64=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, castExpression64.getTree());

                    }
                    break;
                case 4 :
                    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:178:9: castExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_castExpression_in_unaryExpression690);
                    castExpression65=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, castExpression65.getTree());

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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:181:1: castExpression : ( '(' type ')' postfixExpression -> ^( CAST type postfixExpression ) | postfixExpression );
    public final BeneratorParser.castExpression_return castExpression() throws RecognitionException {
        BeneratorParser.castExpression_return retval = new BeneratorParser.castExpression_return();
        retval.start = input.LT(1);
        int castExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal66=null;
        Token char_literal68=null;
        BeneratorParser.type_return type67 = null;

        BeneratorParser.postfixExpression_return postfixExpression69 = null;

        BeneratorParser.postfixExpression_return postfixExpression70 = null;


        Object char_literal66_tree=null;
        Object char_literal68_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_type=new RewriteRuleSubtreeStream(adaptor,"rule type");
        RewriteRuleSubtreeStream stream_postfixExpression=new RewriteRuleSubtreeStream(adaptor,"rule postfixExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 21) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:182:5: ( '(' type ')' postfixExpression -> ^( CAST type postfixExpression ) | postfixExpression )
            int alt19=2;
            alt19 = dfa19.predict(input);
            switch (alt19) {
                case 1 :
                    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:182:9: '(' type ')' postfixExpression
                    {
                    char_literal66=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_castExpression710); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAREN.add(char_literal66);

                    pushFollow(FOLLOW_type_in_castExpression712);
                    type67=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_type.add(type67.getTree());
                    char_literal68=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_castExpression714); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAREN.add(char_literal68);

                    pushFollow(FOLLOW_postfixExpression_in_castExpression716);
                    postfixExpression69=postfixExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_postfixExpression.add(postfixExpression69.getTree());


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
                    // 182:40: -> ^( CAST type postfixExpression )
                    {
                        // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:182:43: ^( CAST type postfixExpression )
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
                    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:183:9: postfixExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_postfixExpression_in_castExpression736);
                    postfixExpression70=postfixExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, postfixExpression70.getTree());

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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:186:1: type : qualifiedName -> ^( TYPE qualifiedName ) ;
    public final BeneratorParser.type_return type() throws RecognitionException {
        BeneratorParser.type_return retval = new BeneratorParser.type_return();
        retval.start = input.LT(1);
        int type_StartIndex = input.index();
        Object root_0 = null;

        BeneratorParser.qualifiedName_return qualifiedName71 = null;


        RewriteRuleSubtreeStream stream_qualifiedName=new RewriteRuleSubtreeStream(adaptor,"rule qualifiedName");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 22) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:187:5: ( qualifiedName -> ^( TYPE qualifiedName ) )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:187:9: qualifiedName
            {
            pushFollow(FOLLOW_qualifiedName_in_type755);
            qualifiedName71=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_qualifiedName.add(qualifiedName71.getTree());


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
            // 187:23: -> ^( TYPE qualifiedName )
            {
                // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:187:26: ^( TYPE qualifiedName )
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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:189:1: postfixExpression : ( primary -> primary ) ( '[' expression ']' -> ^( INDEX $postfixExpression expression ) | '.' IDENTIFIER arguments -> ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments ) | '.' IDENTIFIER -> ^( FIELD $postfixExpression IDENTIFIER ) )* ;
    public final BeneratorParser.postfixExpression_return postfixExpression() throws RecognitionException {
        BeneratorParser.postfixExpression_return retval = new BeneratorParser.postfixExpression_return();
        retval.start = input.LT(1);
        int postfixExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal73=null;
        Token char_literal75=null;
        Token char_literal76=null;
        Token IDENTIFIER77=null;
        Token char_literal79=null;
        Token IDENTIFIER80=null;
        BeneratorParser.primary_return primary72 = null;

        BeneratorParser.expression_return expression74 = null;

        BeneratorParser.arguments_return arguments78 = null;


        Object char_literal73_tree=null;
        Object char_literal75_tree=null;
        Object char_literal76_tree=null;
        Object IDENTIFIER77_tree=null;
        Object char_literal79_tree=null;
        Object IDENTIFIER80_tree=null;
        RewriteRuleTokenStream stream_LBRACKET=new RewriteRuleTokenStream(adaptor,"token LBRACKET");
        RewriteRuleTokenStream stream_RBRACKET=new RewriteRuleTokenStream(adaptor,"token RBRACKET");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_arguments=new RewriteRuleSubtreeStream(adaptor,"rule arguments");
        RewriteRuleSubtreeStream stream_primary=new RewriteRuleSubtreeStream(adaptor,"rule primary");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 23) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:190:5: ( ( primary -> primary ) ( '[' expression ']' -> ^( INDEX $postfixExpression expression ) | '.' IDENTIFIER arguments -> ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments ) | '.' IDENTIFIER -> ^( FIELD $postfixExpression IDENTIFIER ) )* )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:190:9: ( primary -> primary ) ( '[' expression ']' -> ^( INDEX $postfixExpression expression ) | '.' IDENTIFIER arguments -> ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments ) | '.' IDENTIFIER -> ^( FIELD $postfixExpression IDENTIFIER ) )*
            {
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:190:9: ( primary -> primary )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:190:10: primary
            {
            pushFollow(FOLLOW_primary_in_postfixExpression778);
            primary72=primary();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_primary.add(primary72.getTree());


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
            // 190:18: -> primary
            {
                adaptor.addChild(root_0, stream_primary.nextTree());

            }

            retval.tree = root_0;}
            }

            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:191:9: ( '[' expression ']' -> ^( INDEX $postfixExpression expression ) | '.' IDENTIFIER arguments -> ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments ) | '.' IDENTIFIER -> ^( FIELD $postfixExpression IDENTIFIER ) )*
            loop20:
            do {
                int alt20=4;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==LBRACKET) ) {
                    alt20=1;
                }
                else if ( (LA20_0==DOT) ) {
                    int LA20_3 = input.LA(2);

                    if ( (LA20_3==IDENTIFIER) ) {
                        int LA20_4 = input.LA(3);

                        if ( (LA20_4==LPAREN) ) {
                            alt20=2;
                        }
                        else if ( (LA20_4==EOF||LA20_4==RPAREN||(LA20_4>=RBRACE && LA20_4<=RBRACKET)||(LA20_4>=COMMA && LA20_4<=DOT)||(LA20_4>=QUES && LA20_4<=PERCENT)||(LA20_4>=BANGEQ && LA20_4<=LE)) ) {
                            alt20=3;
                        }


                    }


                }


                switch (alt20) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:192:13: '[' expression ']'
            	    {
            	    char_literal73=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_postfixExpression807); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_LBRACKET.add(char_literal73);

            	    pushFollow(FOLLOW_expression_in_postfixExpression809);
            	    expression74=expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_expression.add(expression74.getTree());
            	    char_literal75=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_postfixExpression811); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_RBRACKET.add(char_literal75);



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
            	    // 192:32: -> ^( INDEX $postfixExpression expression )
            	    {
            	        // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:192:35: ^( INDEX $postfixExpression expression )
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
            	    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:193:13: '.' IDENTIFIER arguments
            	    {
            	    char_literal76=(Token)match(input,DOT,FOLLOW_DOT_in_postfixExpression836); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(char_literal76);

            	    IDENTIFIER77=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_postfixExpression838); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER77);

            	    pushFollow(FOLLOW_arguments_in_postfixExpression840);
            	    arguments78=arguments();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_arguments.add(arguments78.getTree());


            	    // AST REWRITE
            	    // elements: IDENTIFIER, arguments, postfixExpression
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            	    root_0 = (Object)adaptor.nil();
            	    // 193:37: -> ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments )
            	    {
            	        // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:193:40: ^( SUBINVOCATION $postfixExpression IDENTIFIER arguments )
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
            	    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:194:13: '.' IDENTIFIER
            	    {
            	    char_literal79=(Token)match(input,DOT,FOLLOW_DOT_in_postfixExpression866); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(char_literal79);

            	    IDENTIFIER80=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_postfixExpression868); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER80);



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
            	    // 194:28: -> ^( FIELD $postfixExpression IDENTIFIER )
            	    {
            	        // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:194:31: ^( FIELD $postfixExpression IDENTIFIER )
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
            	    break loop20;
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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:198:1: primary : ( '(' expression ')' | literal | creator | qualifiedName arguments -> ^( INVOCATION qualifiedName arguments ) | qualifiedName );
    public final BeneratorParser.primary_return primary() throws RecognitionException {
        BeneratorParser.primary_return retval = new BeneratorParser.primary_return();
        retval.start = input.LT(1);
        int primary_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal81=null;
        Token char_literal83=null;
        BeneratorParser.expression_return expression82 = null;

        BeneratorParser.literal_return literal84 = null;

        BeneratorParser.creator_return creator85 = null;

        BeneratorParser.qualifiedName_return qualifiedName86 = null;

        BeneratorParser.arguments_return arguments87 = null;

        BeneratorParser.qualifiedName_return qualifiedName88 = null;


        Object char_literal81_tree=null;
        Object char_literal83_tree=null;
        RewriteRuleSubtreeStream stream_arguments=new RewriteRuleSubtreeStream(adaptor,"rule arguments");
        RewriteRuleSubtreeStream stream_qualifiedName=new RewriteRuleSubtreeStream(adaptor,"rule qualifiedName");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 24) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:199:5: ( '(' expression ')' | literal | creator | qualifiedName arguments -> ^( INVOCATION qualifiedName arguments ) | qualifiedName )
            int alt21=5;
            switch ( input.LA(1) ) {
            case LPAREN:
                {
                alt21=1;
                }
                break;
            case INTLITERAL:
            case DECIMALLITERAL:
            case STRINGLITERAL:
            case BOOLEANLITERAL:
            case NULL:
                {
                alt21=2;
                }
                break;
            case 66:
                {
                alt21=3;
                }
                break;
            case IDENTIFIER:
                {
                int LA21_4 = input.LA(2);

                if ( (synpred37_Benerator()) ) {
                    alt21=4;
                }
                else if ( (true) ) {
                    alt21=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 21, 4, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:199:9: '(' expression ')'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal81=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_primary910); if (state.failed) return retval;
                    pushFollow(FOLLOW_expression_in_primary913);
                    expression82=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression82.getTree());
                    char_literal83=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_primary915); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:200:9: literal
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_literal_in_primary926);
                    literal84=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal84.getTree());

                    }
                    break;
                case 3 :
                    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:201:7: creator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_creator_in_primary934);
                    creator85=creator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, creator85.getTree());

                    }
                    break;
                case 4 :
                    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:202:9: qualifiedName arguments
                    {
                    pushFollow(FOLLOW_qualifiedName_in_primary944);
                    qualifiedName86=qualifiedName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_qualifiedName.add(qualifiedName86.getTree());
                    pushFollow(FOLLOW_arguments_in_primary946);
                    arguments87=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arguments.add(arguments87.getTree());


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
                    // 202:33: -> ^( INVOCATION qualifiedName arguments )
                    {
                        // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:202:36: ^( INVOCATION qualifiedName arguments )
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
                    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:203:9: qualifiedName
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_qualifiedName_in_primary966);
                    qualifiedName88=qualifiedName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName88.getTree());

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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:206:1: creator : ( 'new' qualifiedName arguments -> ^( CONSTRUCTOR qualifiedName arguments ) | 'new' qualifiedName '{' assignment ( ',' assignment )* '}' -> ^( BEAN qualifiedName ( assignment )* ) );
    public final BeneratorParser.creator_return creator() throws RecognitionException {
        BeneratorParser.creator_return retval = new BeneratorParser.creator_return();
        retval.start = input.LT(1);
        int creator_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal89=null;
        Token string_literal92=null;
        Token char_literal94=null;
        Token char_literal96=null;
        Token char_literal98=null;
        BeneratorParser.qualifiedName_return qualifiedName90 = null;

        BeneratorParser.arguments_return arguments91 = null;

        BeneratorParser.qualifiedName_return qualifiedName93 = null;

        BeneratorParser.assignment_return assignment95 = null;

        BeneratorParser.assignment_return assignment97 = null;


        Object string_literal89_tree=null;
        Object string_literal92_tree=null;
        Object char_literal94_tree=null;
        Object char_literal96_tree=null;
        Object char_literal98_tree=null;
        RewriteRuleTokenStream stream_66=new RewriteRuleTokenStream(adaptor,"token 66");
        RewriteRuleTokenStream stream_RBRACE=new RewriteRuleTokenStream(adaptor,"token RBRACE");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LBRACE=new RewriteRuleTokenStream(adaptor,"token LBRACE");
        RewriteRuleSubtreeStream stream_assignment=new RewriteRuleSubtreeStream(adaptor,"rule assignment");
        RewriteRuleSubtreeStream stream_arguments=new RewriteRuleSubtreeStream(adaptor,"rule arguments");
        RewriteRuleSubtreeStream stream_qualifiedName=new RewriteRuleSubtreeStream(adaptor,"rule qualifiedName");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 25) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:207:5: ( 'new' qualifiedName arguments -> ^( CONSTRUCTOR qualifiedName arguments ) | 'new' qualifiedName '{' assignment ( ',' assignment )* '}' -> ^( BEAN qualifiedName ( assignment )* ) )
            int alt23=2;
            alt23 = dfa23.predict(input);
            switch (alt23) {
                case 1 :
                    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:207:9: 'new' qualifiedName arguments
                    {
                    string_literal89=(Token)match(input,66,FOLLOW_66_in_creator985); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_66.add(string_literal89);

                    pushFollow(FOLLOW_qualifiedName_in_creator987);
                    qualifiedName90=qualifiedName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_qualifiedName.add(qualifiedName90.getTree());
                    pushFollow(FOLLOW_arguments_in_creator989);
                    arguments91=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arguments.add(arguments91.getTree());


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
                    // 207:39: -> ^( CONSTRUCTOR qualifiedName arguments )
                    {
                        // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:207:42: ^( CONSTRUCTOR qualifiedName arguments )
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
                    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:208:9: 'new' qualifiedName '{' assignment ( ',' assignment )* '}'
                    {
                    string_literal92=(Token)match(input,66,FOLLOW_66_in_creator1009); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_66.add(string_literal92);

                    pushFollow(FOLLOW_qualifiedName_in_creator1011);
                    qualifiedName93=qualifiedName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_qualifiedName.add(qualifiedName93.getTree());
                    char_literal94=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_creator1013); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LBRACE.add(char_literal94);

                    pushFollow(FOLLOW_assignment_in_creator1015);
                    assignment95=assignment();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_assignment.add(assignment95.getTree());
                    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:208:44: ( ',' assignment )*
                    loop22:
                    do {
                        int alt22=2;
                        int LA22_0 = input.LA(1);

                        if ( (LA22_0==COMMA) ) {
                            alt22=1;
                        }


                        switch (alt22) {
                    	case 1 :
                    	    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:208:45: ',' assignment
                    	    {
                    	    char_literal96=(Token)match(input,COMMA,FOLLOW_COMMA_in_creator1018); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(char_literal96);

                    	    pushFollow(FOLLOW_assignment_in_creator1020);
                    	    assignment97=assignment();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_assignment.add(assignment97.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop22;
                        }
                    } while (true);

                    char_literal98=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_creator1024); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RBRACE.add(char_literal98);



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
                    // 208:66: -> ^( BEAN qualifiedName ( assignment )* )
                    {
                        // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:208:69: ^( BEAN qualifiedName ( assignment )* )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(BEAN, "BEAN"), root_1);

                        adaptor.addChild(root_1, stream_qualifiedName.nextTree());
                        // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:208:90: ( assignment )*
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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:211:1: arguments : '(' ( expression ( ',' expression )* )? ')' -> ^( ARGUMENTS ( expression )* ) ;
    public final BeneratorParser.arguments_return arguments() throws RecognitionException {
        BeneratorParser.arguments_return retval = new BeneratorParser.arguments_return();
        retval.start = input.LT(1);
        int arguments_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal99=null;
        Token char_literal101=null;
        Token char_literal103=null;
        BeneratorParser.expression_return expression100 = null;

        BeneratorParser.expression_return expression102 = null;


        Object char_literal99_tree=null;
        Object char_literal101_tree=null;
        Object char_literal103_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 26) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:212:5: ( '(' ( expression ( ',' expression )* )? ')' -> ^( ARGUMENTS ( expression )* ) )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:212:9: '(' ( expression ( ',' expression )* )? ')'
            {
            char_literal99=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_arguments1054); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(char_literal99);

            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:212:13: ( expression ( ',' expression )* )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( ((LA25_0>=IDENTIFIER && LA25_0<=NULL)||LA25_0==LPAREN||(LA25_0>=BANG && LA25_0<=TILDE)||LA25_0==SUB||LA25_0==66) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:212:14: expression ( ',' expression )*
                    {
                    pushFollow(FOLLOW_expression_in_arguments1057);
                    expression100=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression100.getTree());
                    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:212:25: ( ',' expression )*
                    loop24:
                    do {
                        int alt24=2;
                        int LA24_0 = input.LA(1);

                        if ( (LA24_0==COMMA) ) {
                            alt24=1;
                        }


                        switch (alt24) {
                    	case 1 :
                    	    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:212:26: ',' expression
                    	    {
                    	    char_literal101=(Token)match(input,COMMA,FOLLOW_COMMA_in_arguments1060); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(char_literal101);

                    	    pushFollow(FOLLOW_expression_in_arguments1062);
                    	    expression102=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expression.add(expression102.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop24;
                        }
                    } while (true);


                    }
                    break;

            }

            char_literal103=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_arguments1068); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(char_literal103);



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
            // 212:49: -> ^( ARGUMENTS ( expression )* )
            {
                // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:212:52: ^( ARGUMENTS ( expression )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ARGUMENTS, "ARGUMENTS"), root_1);

                // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:212:64: ( expression )*
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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:214:1: qualifiedName : IDENTIFIER ( '.' IDENTIFIER )* -> ^( QUALIFIEDNAME ( IDENTIFIER )* ) ;
    public final BeneratorParser.qualifiedName_return qualifiedName() throws RecognitionException {
        BeneratorParser.qualifiedName_return retval = new BeneratorParser.qualifiedName_return();
        retval.start = input.LT(1);
        int qualifiedName_StartIndex = input.index();
        Object root_0 = null;

        Token IDENTIFIER104=null;
        Token char_literal105=null;
        Token IDENTIFIER106=null;

        Object IDENTIFIER104_tree=null;
        Object char_literal105_tree=null;
        Object IDENTIFIER106_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 27) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:215:2: ( IDENTIFIER ( '.' IDENTIFIER )* -> ^( QUALIFIEDNAME ( IDENTIFIER )* ) )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:215:6: IDENTIFIER ( '.' IDENTIFIER )*
            {
            IDENTIFIER104=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedName1092); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER104);

            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:215:17: ( '.' IDENTIFIER )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==DOT) ) {
                    int LA26_2 = input.LA(2);

                    if ( (LA26_2==IDENTIFIER) ) {
                        int LA26_3 = input.LA(3);

                        if ( (synpred42_Benerator()) ) {
                            alt26=1;
                        }


                    }


                }


                switch (alt26) {
            	case 1 :
            	    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:215:18: '.' IDENTIFIER
            	    {
            	    char_literal105=(Token)match(input,DOT,FOLLOW_DOT_in_qualifiedName1095); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(char_literal105);

            	    IDENTIFIER106=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedName1097); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER106);


            	    }
            	    break;

            	default :
            	    break loop26;
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
            // 215:35: -> ^( QUALIFIEDNAME ( IDENTIFIER )* )
            {
                // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:215:38: ^( QUALIFIEDNAME ( IDENTIFIER )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(QUALIFIEDNAME, "QUALIFIEDNAME"), root_1);

                // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:215:54: ( IDENTIFIER )*
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
    // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:218:1: literal : ( INTLITERAL | DECIMALLITERAL | STRINGLITERAL | BOOLEANLITERAL | NULL );
    public final BeneratorParser.literal_return literal() throws RecognitionException {
        BeneratorParser.literal_return retval = new BeneratorParser.literal_return();
        retval.start = input.LT(1);
        int literal_StartIndex = input.index();
        Object root_0 = null;

        Token set107=null;

        Object set107_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 28) ) { return retval; }
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:219:5: ( INTLITERAL | DECIMALLITERAL | STRINGLITERAL | BOOLEANLITERAL | NULL )
            // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:
            {
            root_0 = (Object)adaptor.nil();

            set107=(Token)input.LT(1);
            if ( (input.LA(1)>=INTLITERAL && input.LA(1)<=NULL) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set107));
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

    // $ANTLR start synpred37_Benerator
    public final void synpred37_Benerator_fragment() throws RecognitionException {   
        // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:202:9: ( qualifiedName arguments )
        // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:202:9: qualifiedName arguments
        {
        pushFollow(FOLLOW_qualifiedName_in_synpred37_Benerator944);
        qualifiedName();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_arguments_in_synpred37_Benerator946);
        arguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred37_Benerator

    // $ANTLR start synpred42_Benerator
    public final void synpred42_Benerator_fragment() throws RecognitionException {   
        // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:215:18: ( '.' IDENTIFIER )
        // /Users/volker/Documents/databene/benerator/src/main/g/Benerator.g:215:18: '.' IDENTIFIER
        {
        match(input,DOT,FOLLOW_DOT_in_synpred42_Benerator1095); if (state.failed) return ;
        match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred42_Benerator1097); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred42_Benerator

    // Delegated rules

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
    public final boolean synpred42_Benerator() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred42_Benerator_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA6 dfa6 = new DFA6(this);
    protected DFA19 dfa19 = new DFA19(this);
    protected DFA23 dfa23 = new DFA23(this);
    static final String DFA6_eotS =
        "\6\uffff";
    static final String DFA6_eofS =
        "\2\uffff\1\1\2\uffff\1\1";
    static final String DFA6_minS =
        "\1\4\1\uffff\1\35\1\4\1\uffff\1\35";
    static final String DFA6_maxS =
        "\1\102\1\uffff\1\76\1\4\1\uffff\1\76";
    static final String DFA6_acceptS =
        "\1\uffff\1\1\2\uffff\1\2\1\uffff";
    static final String DFA6_specialS =
        "\6\uffff}>";
    static final String[] DFA6_transitionS = {
            "\1\2\5\1\23\uffff\1\1\11\uffff\2\1\6\uffff\1\1\22\uffff\1\1",
            "",
            "\2\1\1\uffff\3\1\1\uffff\1\1\1\3\1\4\2\uffff\15\1\1\uffff\10"+
            "\1",
            "\1\5",
            "",
            "\2\1\1\uffff\3\1\1\uffff\1\1\1\3\1\4\2\uffff\15\1\1\uffff\10"+
            "\1"
    };

    static final short[] DFA6_eot = DFA.unpackEncodedString(DFA6_eotS);
    static final short[] DFA6_eof = DFA.unpackEncodedString(DFA6_eofS);
    static final char[] DFA6_min = DFA.unpackEncodedStringToUnsignedChars(DFA6_minS);
    static final char[] DFA6_max = DFA.unpackEncodedStringToUnsignedChars(DFA6_maxS);
    static final short[] DFA6_accept = DFA.unpackEncodedString(DFA6_acceptS);
    static final short[] DFA6_special = DFA.unpackEncodedString(DFA6_specialS);
    static final short[][] DFA6_transition;

    static {
        int numStates = DFA6_transitionS.length;
        DFA6_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA6_transition[i] = DFA.unpackEncodedString(DFA6_transitionS[i]);
        }
    }

    class DFA6 extends DFA {

        public DFA6(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 6;
            this.eot = DFA6_eot;
            this.eof = DFA6_eof;
            this.min = DFA6_min;
            this.max = DFA6_max;
            this.accept = DFA6_accept;
            this.special = DFA6_special;
            this.transition = DFA6_transition;
        }
        public String getDescription() {
            return "118:1: expression : ( conditionalExpression | assignment );";
        }
    }
    static final String DFA19_eotS =
        "\10\uffff";
    static final String DFA19_eofS =
        "\5\uffff\1\2\2\uffff";
    static final String DFA19_minS =
        "\2\4\1\uffff\1\35\2\4\1\35\1\uffff";
    static final String DFA19_maxS =
        "\2\102\1\uffff\1\76\1\4\1\102\1\76\1\uffff";
    static final String DFA19_acceptS =
        "\2\uffff\1\2\4\uffff\1\1";
    static final String DFA19_specialS =
        "\10\uffff}>";
    static final String[] DFA19_transitionS = {
            "\6\2\23\uffff\1\1\44\uffff\1\2",
            "\1\3\5\2\23\uffff\1\2\11\uffff\2\2\6\uffff\1\2\22\uffff\1\2",
            "",
            "\1\2\1\5\2\uffff\1\2\3\uffff\1\4\1\2\2\uffff\1\2\1\uffff\13"+
            "\2\1\uffff\10\2",
            "\1\6",
            "\6\7\23\uffff\1\7\1\2\1\uffff\3\2\1\uffff\2\2\3\uffff\15\2"+
            "\1\uffff\10\2\3\uffff\1\7",
            "\1\2\1\5\2\uffff\1\2\3\uffff\1\4\1\2\2\uffff\1\2\1\uffff\13"+
            "\2\1\uffff\10\2",
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
            return "181:1: castExpression : ( '(' type ')' postfixExpression -> ^( CAST type postfixExpression ) | postfixExpression );";
        }
    }
    static final String DFA23_eotS =
        "\7\uffff";
    static final String DFA23_eofS =
        "\7\uffff";
    static final String DFA23_minS =
        "\1\102\1\4\1\35\1\4\2\uffff\1\35";
    static final String DFA23_maxS =
        "\1\102\1\4\1\45\1\4\2\uffff\1\45";
    static final String DFA23_acceptS =
        "\4\uffff\1\1\1\2\1\uffff";
    static final String DFA23_specialS =
        "\7\uffff}>";
    static final String[] DFA23_transitionS = {
            "\1\1",
            "\1\2",
            "\1\4\1\uffff\1\5\5\uffff\1\3",
            "\1\6",
            "",
            "",
            "\1\4\1\uffff\1\5\5\uffff\1\3"
    };

    static final short[] DFA23_eot = DFA.unpackEncodedString(DFA23_eotS);
    static final short[] DFA23_eof = DFA.unpackEncodedString(DFA23_eofS);
    static final char[] DFA23_min = DFA.unpackEncodedStringToUnsignedChars(DFA23_minS);
    static final char[] DFA23_max = DFA.unpackEncodedStringToUnsignedChars(DFA23_maxS);
    static final short[] DFA23_accept = DFA.unpackEncodedString(DFA23_acceptS);
    static final short[] DFA23_special = DFA.unpackEncodedString(DFA23_specialS);
    static final short[][] DFA23_transition;

    static {
        int numStates = DFA23_transitionS.length;
        DFA23_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA23_transition[i] = DFA.unpackEncodedString(DFA23_transitionS[i]);
        }
    }

    class DFA23 extends DFA {

        public DFA23(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 23;
            this.eot = DFA23_eot;
            this.eof = DFA23_eof;
            this.min = DFA23_min;
            this.max = DFA23_max;
            this.accept = DFA23_accept;
            this.special = DFA23_special;
            this.transition = DFA23_transition;
        }
        public String getDescription() {
            return "206:1: creator : ( 'new' qualifiedName arguments -> ^( CONSTRUCTOR qualifiedName arguments ) | 'new' qualifiedName '{' assignment ( ',' assignment )* '}' -> ^( BEAN qualifiedName ( assignment )* ) );";
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
    public static final BitSet FOLLOW_beanSpec_in_beanSpecList184 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_COMMA_in_beanSpecList187 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_beanSpec_in_beanSpecList190 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_expression_in_beanSpec206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignment_in_expression239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_assignment258 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_EQ_in_assignment260 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_assignment263 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression278 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_QUES_in_conditionalExpression281 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression284 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_COLON_in_conditionalExpression286 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalExpression289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression315 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_BARBAR_in_conditionalOrExpression318 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression321 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression344 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_AMPAMP_in_conditionalAndExpression347 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression350 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression372 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_BAR_in_inclusiveOrExpression375 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression378 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression399 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_CARET_in_exclusiveOrExpression402 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression405 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression426 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_AMP_in_andExpression429 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression432 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression454 = new BitSet(new long[]{0x0080080000000002L});
    public static final BitSet FOLLOW_set_in_equalityExpression457 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression466 = new BitSet(new long[]{0x0080080000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression488 = new BitSet(new long[]{0x7100000000000002L});
    public static final BitSet FOLLOW_set_in_relationalExpression491 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression508 = new BitSet(new long[]{0x7100000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression530 = new BitSet(new long[]{0x0E00000000000002L});
    public static final BitSet FOLLOW_set_in_shiftExpression533 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression546 = new BitSet(new long[]{0x0E00000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression568 = new BitSet(new long[]{0x0000C00000000002L});
    public static final BitSet FOLLOW_set_in_additiveExpression571 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression580 = new BitSet(new long[]{0x0000C00000000002L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression602 = new BitSet(new long[]{0x0023000000000002L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression605 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression618 = new BitSet(new long[]{0x0023000000000002L});
    public static final BitSet FOLLOW_SUB_in_unaryExpression644 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpression646 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_unaryExpression664 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpression667 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_unaryExpression677 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpression680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpression690 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_castExpression710 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_type_in_castExpression712 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_RPAREN_in_castExpression714 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_postfixExpression_in_castExpression716 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_postfixExpression_in_castExpression736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_type755 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_postfixExpression778 = new BitSet(new long[]{0x0000002200000002L});
    public static final BitSet FOLLOW_LBRACKET_in_postfixExpression807 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_postfixExpression809 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_RBRACKET_in_postfixExpression811 = new BitSet(new long[]{0x0000002200000002L});
    public static final BitSet FOLLOW_DOT_in_postfixExpression836 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_postfixExpression838 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_arguments_in_postfixExpression840 = new BitSet(new long[]{0x0000002200000002L});
    public static final BitSet FOLLOW_DOT_in_postfixExpression866 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_postfixExpression868 = new BitSet(new long[]{0x0000002200000002L});
    public static final BitSet FOLLOW_LPAREN_in_primary910 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_primary913 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_RPAREN_in_primary915 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_creator_in_primary934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_primary944 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_arguments_in_primary946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_primary966 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_creator985 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_creator987 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_arguments_in_creator989 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_creator1009 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_creator1011 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_LBRACE_in_creator1013 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_assignment_in_creator1015 = new BitSet(new long[]{0x0000001100000000L});
    public static final BitSet FOLLOW_COMMA_in_creator1018 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_assignment_in_creator1020 = new BitSet(new long[]{0x0000001100000000L});
    public static final BitSet FOLLOW_RBRACE_in_creator1024 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_arguments1054 = new BitSet(new long[]{0x00008180600003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_arguments1057 = new BitSet(new long[]{0x0000001040000000L});
    public static final BitSet FOLLOW_COMMA_in_arguments1060 = new BitSet(new long[]{0x00008180200003F0L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_arguments1062 = new BitSet(new long[]{0x0000001040000000L});
    public static final BitSet FOLLOW_RPAREN_in_arguments1068 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedName1092 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_DOT_in_qualifiedName1095 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedName1097 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_set_in_literal0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_synpred37_Benerator944 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_arguments_in_synpred37_Benerator946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred42_Benerator1095 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_synpred42_Benerator1097 = new BitSet(new long[]{0x0000000000000002L});

}