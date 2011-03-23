/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.databene.benerator.script;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.databene.benerator.sample.WeightedSample;
import org.databene.commons.ArrayFormat;
import org.databene.commons.Assert;
import org.databene.commons.Expression;
import org.databene.commons.ParseException;
import org.databene.commons.StringUtil;
import org.databene.commons.expression.ConstantExpression;
import org.databene.commons.expression.ForNameExpression;
import org.databene.commons.expression.TypeConvertingExpression;
import org.databene.model.data.PrimitiveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses Benerator Script statements and converts expressions and statements to Java objects.<br/>
 * <br/>
 * Created at 05.10.2009 18:52:31
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class BeneratorScriptParser {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BeneratorScriptParser.class);
	private static final Expression<?>[] EMPTY_ARGUMENT_LIST = {};

    // interface -------------------------------------------------------------------------------------------------------

    public static WeightedSample<?>[] parseWeightedLiteralList(String text) throws ParseException {
        if (StringUtil.isEmpty(text))
            return new WeightedSample[0];
        try {
        	BeneratorParser parser = parser(text);
	        BeneratorParser.weightedLiteralList_return r = parser.weightedLiteralList();
	        checkForSyntaxErrors(text, "weightedLiteralList", parser, r);
	        if (r != null) {
	        	CommonTree tree = (CommonTree) r.getTree();
	        	if (LOGGER.isDebugEnabled())
	        		LOGGER.debug("parsed " + text + " to " + tree.toStringTree());
	            return convertWeightedLiteralList(tree);
	        } else
	        	return null;
        } catch (RuntimeException e) {
        	if (e.getCause() instanceof RecognitionException)
        		throw mapToParseException((RecognitionException) e.getCause(), text);
        	else
        		throw e;
        } catch (IOException e) {
        	throw new IllegalStateException("Encountered illegal state in weightedLiteralList parsing", e);
        } catch (RecognitionException e) {
        	e.printStackTrace();
        	throw mapToParseException(e, text);
        }
    }
	
    public static Expression<?> parseExpression(String text) throws ParseException {
        if (StringUtil.isEmpty(text))
            return null;
        try {
        	BeneratorParser parser = parser(text);
	        BeneratorParser.expression_return r = parser.expression();
	        checkForSyntaxErrors(text, "expression", parser, r);
	        	CommonTree tree = (CommonTree) r.getTree();
	        	if (LOGGER.isDebugEnabled())
	        		LOGGER.debug("parsed " + text + " to " + tree.toStringTree());
	            return convertNode(tree);
        } catch (RuntimeException e) {
        	if (e.getCause() instanceof RecognitionException)
        		throw mapToParseException((RecognitionException) e.getCause(), text);
        	else
        		throw e;
        } catch (IOException e) {
        	throw new IllegalStateException("Encountered illegal state in regex parsing", e);
        } catch (RecognitionException e) {
        	throw mapToParseException(e, text);
        }
    }

    public static WeightedTransition[] parseTransitionList(String text) throws ParseException {
        if (StringUtil.isEmpty(text))
            return null;
        try {
        	BeneratorParser parser = parser(text);
	        BeneratorParser.transitionList_return r = parser.transitionList();
	        checkForSyntaxErrors(text, "transitionList", parser, r);
	        if (r != null) {
	        	CommonTree tree = (CommonTree) r.getTree();
	        	if (LOGGER.isDebugEnabled())
	        		LOGGER.debug("parsed " + text + " to " + tree.toStringTree());
	            return convertTransitionList(tree);
	        } else
	        	return null;
        } catch (RuntimeException e) {
        	if (e.getCause() instanceof RecognitionException)
        		throw mapToParseException((RecognitionException) e.getCause(), text);
        	else
        		throw e;
        } catch (IOException e) {
        	throw new IllegalStateException("Encountered illegal state in regex parsing", e);
        } catch (RecognitionException e) {
        	e.printStackTrace();
        	throw mapToParseException(e, text);
        }
    }
	
    public static Expression<?>[] parseBeanSpecList(String text) throws ParseException {
        if (StringUtil.isEmpty(text))
            return null;
        try {
        	BeneratorParser parser = parser(text);
	        BeneratorParser.beanSpecList_return r = parser.beanSpecList();
	        checkForSyntaxErrors(text, "beanSpecList", parser, r);
	        if (r != null) {
	        	CommonTree tree = (CommonTree) r.getTree();
	        	if (LOGGER.isDebugEnabled())
	        		LOGGER.debug("parsed " + text + " to " + tree.toStringTree());
	            return convertBeanSpecList(tree);
	        } else
	        	return null;
        } catch (RuntimeException e) {
        	if (e.getCause() instanceof RecognitionException)
        		throw mapToParseException((RecognitionException) e.getCause(), text);
        	else
        		throw e;
        } catch (IOException e) {
        	throw new IllegalStateException("Encountered illegal state in regex parsing", e);
        } catch (RecognitionException e) {
        	throw mapToParseException(e, text);
        }
    }
	
	public static Expression<?> parseBeanSpec(String text) throws ParseException {
        if (StringUtil.isEmpty(text))
            return null;
        try {
        	BeneratorParser parser = parser(text);
	        BeneratorParser.beanSpec_return r = parser.beanSpec();
	        checkForSyntaxErrors(text, "beanSpec", parser, r);
	        if (r != null) {
	        	CommonTree tree = (CommonTree) r.getTree();
	        	if (LOGGER.isDebugEnabled())
	        		LOGGER.debug("parsed " + text + " to " + tree.toStringTree());
	        	return convertBeanSpec(tree);
	        } else
	        	return null;
        } catch (RuntimeException e) {
        	if (e.getCause() instanceof RecognitionException)
        		throw mapToParseException((RecognitionException) e.getCause(), text);
        	else
        		throw e;
        } catch (IOException e) {
        	throw new IllegalStateException("Encountered illegal state in regex parsing", e);
        } catch (RecognitionException e) {
        	throw mapToParseException(e, text);
        }
    }
	
	// private helpers -------------------------------------------------------------------------------------------------

	private static void checkForSyntaxErrors(String text, String type,
			BeneratorParser parser, ParserRuleReturnScope r) {
		if (parser.getNumberOfSyntaxErrors() > 0)
			throw new ParseException("Illegal " + type, text, -1, -1);
		CommonToken stop = (CommonToken) r.stop;
		if (stop.getStopIndex() < StringUtil.trimRight(text).length() - 1)
			throw new ParseException("Syntax error after " + stop.getText(), text);
	}
	
    private static BeneratorParser parser(String text) throws IOException {
	    BeneratorLexer lex = new BeneratorLexer(new ANTLRReaderStream(new StringReader(text)));
	    CommonTokenStream tokens = new CommonTokenStream(lex);
	    BeneratorParser parser = new BeneratorParser(tokens);
	    return parser;
    }
	
    private static ParseException mapToParseException(RecognitionException cause, String text) {
    	return new ParseException("Error parsing Benerator Script expression", cause, 
    			text, cause.line, cause.charPositionInLine);
    }

    private static WeightedSample<?>[] convertWeightedLiteralList(CommonTree node) throws ParseException {
    	if (!node.isNil())
    		return new WeightedSample[] { convertWeightedLiteral(node) };
    	else {
		    int childCount = node.getChildCount();
		    WeightedSample<?>[] transitions = new WeightedSample[childCount];
		    for (int i = 0; i < childCount; i++)
		    	transitions[i] = convertWeightedLiteral(childAt(i, node));
		    return transitions;
    	}
    }

    private static WeightedSample<?> convertWeightedLiteral(CommonTree node) throws ParseException {
		if (node.getType() == BeneratorLexer.CARET) {
			Expression<?> value = convertNode(childAt(0, node));
			Expression<Double> weight = null;
			if (node.getChildCount() > 1)
				weight = new TypeConvertingExpression<Double>(convertNode(childAt(1, node)), Double.class);
			else
				weight = new ConstantExpression<Double>(1.);
			return new WeightedSample<Object>(value.evaluate(null), weight.evaluate(null));
		} else
			return new WeightedSample<Object>(convertNode(node).evaluate(null), 1.); 
	}

    private static WeightedTransition[] convertTransitionList(CommonTree node) throws ParseException {
    	if (node.getType() == BeneratorLexer.ARROW)
    		return new WeightedTransition[] { convertTransition(node) };
    	else if (node.isNil()) {
		    int childCount = node.getChildCount();
		    WeightedTransition[] transitions = new WeightedTransition[childCount];
		    for (int i = 0; i < childCount; i++)
		    	transitions[i] = convertTransition(childAt(i, node));
		    return transitions;
    	} else
    		throw new ParseException("Unexpected token in transition list: ", node.getToken().getText(), 
    				node.getLine(), node.getCharPositionInLine());
    }

	private static WeightedTransition convertTransition(CommonTree node) throws ParseException {
		Assert.isTrue(node.getType() == BeneratorLexer.ARROW, "expected transition, found: " + node.getToken());
		Expression<?> from = convertNode(childAt(0, node));
		Expression<?> to = convertNode(childAt(1, node));
		Expression<Double> weight;
		if (node.getChildCount() > 2)
			weight = new TypeConvertingExpression<Double>(convertNode(childAt(2, node)), Double.class);
		else
			weight = new ConstantExpression<Double>(1.);
		return new WeightedTransition(from.evaluate(null), to.evaluate(null), weight.evaluate(null));
	}

    private static Expression<?>[] convertBeanSpecList(CommonTree node) throws ParseException {
    	if (node.getType() == BeneratorLexer.BEANSPEC)
    		return new Expression<?>[] { convertBeanSpec(node) };
    	else if (node.isNil()) {
		    int childCount = node.getChildCount();
		    Expression<?>[] specs = new Expression<?>[childCount];
		    for (int i = 0; i < childCount; i++)
		    	specs[i] = convertBeanSpec(childAt(i, node));
		    return specs;
    	} else
    		throw new ParseException("Unexpected token", node.getToken().getText(), node.getLine(), node.getCharPositionInLine());
    }

	private static Expression<?> convertBeanSpec(CommonTree node) throws ParseException {
		Assert.isTrue(node.getType() == BeneratorLexer.BEANSPEC, "BEANSPEC expected, found: " + node.getToken());
		node = childAt(0, node);
		if (node.getType() == BeneratorLexer.QUALIFIEDNAME)
			return new QNBeanSpecExpression(convertQualifiedNameToStringArray(node));
		else if (node.getType() == BeneratorLexer.IDENTIFIER)
			return new QNBeanSpecExpression(new String[] { node.getText() });
		else if (node.getType() == BeneratorLexer.BEAN)
			return convertBean(node);
		else
			return convertNode(node);
	}

	private static Expression<?> convertNode(CommonTree node) throws ParseException {
    	switch (node.getType()) {
			case BeneratorLexer.NULL: return new ConstantExpression<Object>(null);
			case BeneratorLexer.BOOLEANLITERAL: return convertBooleanLiteral(node);
			case BeneratorLexer.INTLITERAL: return convertIntLiteral(node);
			case BeneratorLexer.DECIMALLITERAL: return convertDecimalLiteral(node);
    		case BeneratorLexer.STRINGLITERAL: return convertStringLiteral(node);
    		case BeneratorLexer.IDENTIFIER: return convertIdentifier(node);
    		case BeneratorLexer.QUALIFIEDNAME: return convertQualifiedName(node);
    		case BeneratorLexer.TYPE: return convertType(node);
			case BeneratorLexer.CONSTRUCTOR: return convertCreator(node);
			case BeneratorLexer.BEAN: return convertBean(node);
    		case BeneratorLexer.INVOCATION: return convertInvocation(node);
    		case BeneratorLexer.SUBINVOCATION: return convertSubInvocation(node);
			case BeneratorLexer.INDEX: return convertIndex(node);
			case BeneratorLexer.FIELD: return convertField(node);
			case BeneratorLexer.CAST: return convertCast(node);
			case BeneratorLexer.NEGATION: return convertNegation(node);
			case BeneratorLexer.BANG: return convertLogicalComplement(node);
			case BeneratorLexer.TILDE: return convertBitwiseComplement(node);
			case BeneratorLexer.PLUS: return convertPlus(node);
			case BeneratorLexer.SUB: return convertMinus(node);
			case BeneratorLexer.STAR: return convertStar(node);
			case BeneratorLexer.SLASH: return convertSlash(node);
			case BeneratorLexer.PERCENT: return convertPercent(node);
			case BeneratorLexer.AMP: return convertAnd(node);
			case BeneratorLexer.BAR: return convertInclusiveOr(node);
			case BeneratorLexer.CARET: return convertExclusiveOr(node);
			case BeneratorLexer.EQEQ: return convertEquals(node);
			case BeneratorLexer.BANGEQ: return convertNotEquals(node);
			case BeneratorLexer.LT: return convertLess(node);
			case BeneratorLexer.LE: return convertLessOrEquals(node);
			case BeneratorLexer.GT: return convertGreater(node);
			case BeneratorLexer.GE: return convertGreaterOrEquals(node);
			case BeneratorLexer.SHIFT_LEFT: return convertShiftLeft(node);
			case BeneratorLexer.SHIFT_RIGHT: return convertShiftRight(node);
			case BeneratorLexer.SHIFT_RIGHT2: return convertShiftRight2(node);
			case BeneratorLexer.AMPAMP: return convertConditionalAnd(node);
			case BeneratorLexer.BARBAR: return convertConditionalOr(node);
			case BeneratorLexer.QUES: return convertConditionalExpression(node);
			case BeneratorLexer.EQ: return convertAssignment(node);
			default: throw new ParseException("Unknown token type", String.valueOf(node.getType()), 
					node.getLine(), node.getCharPositionInLine());
    	}
    }

	private static Expression<Boolean> convertBooleanLiteral(CommonTree node) {
		return new ConstantExpression<Boolean>(Boolean.parseBoolean(node.getText()));
    }

    private static Expression<String> convertStringLiteral(CommonTree node) {
		String rawString = node.getText();
		String text = rawString.substring(1, rawString.length() - 1);
		text = StringUtil.unescape(text);
		return new ConstantExpression<String>(text);
    }

    private static Expression<String> convertIdentifier(CommonTree node) {
		return new ConstantExpression<String>(node.getText());
    }

    private static Expression<?> convertQualifiedName(CommonTree node) {
    	return new QNExpression(convertQualifiedNameToStringArray(node));
    }

    private static String[] convertQualifiedNameToStringArray(CommonTree node) {
    	int childCount = node.getChildCount();
		String[] result = new String[childCount];
    	List<CommonTree> childNodes = getChildNodes(node);
    	for (int i = 0; i < childCount; i++)
    		result[i] = childNodes.get(i).getText();
		return result;
    }

    private static Expression<Class<?>> convertType(CommonTree node) {
    	String[] classNameParts = convertQualifiedNameToStringArray(childAt(0, node));
    	final String className = ArrayFormat.format(".", classNameParts);
		PrimitiveType primitiveType = PrimitiveType.getInstance(className);
		if (primitiveType != null)
			return new ConstantExpression<Class<?>>(primitiveType.getJavaType());
		else
			return new ForNameExpression(new ConstantExpression<String>(className));
    }

    private static Expression<? extends Number> convertIntLiteral(CommonTree node) {
		String text = node.getText();
		Number number = null;
		if (text.length() > 10)
			number = Long.parseLong(text);
		else if (text.length() == 10) {
			long l = Long.parseLong(text);
			if (l <= Integer.MAX_VALUE)
				number = (int) l;
			else
				number = l;
		} else
			number = Integer.parseInt(text);
		return new ConstantExpression<Number>(number);
    }

    private static Expression<Double> convertDecimalLiteral(CommonTree node) {
		return new ConstantExpression<Double>(Double.parseDouble(node.getText()));
    }

    private static Expression<?> convertCreator(CommonTree node) throws ParseException {
		List<CommonTree> childNodes = getChildNodes(node);
    	String className = parseQualifiedNameOfClass(childNodes.get(0));
    	Expression<?>[] params = parseArguments(childNodes.get(1));
    	return new ParameterizedConstruction<Object>(className, params);
    }

    private static Expression<?> convertBean(CommonTree node) throws ParseException {
		List<CommonTree> childNodes = getChildNodes(node);
    	String className = parseQualifiedNameOfClass(childNodes.get(0));
    	Assignment[] props = parseFieldAssignments(childNodes, 1);
    	return new BeanConstruction<Object>(className, props);
    }

    private static Assignment[] parseFieldAssignments(List<CommonTree> nodes, int firstIndex) throws ParseException {
    	Assignment[] assignments = new Assignment[nodes.size() - firstIndex];
    	for (int i = firstIndex; i < nodes.size(); i++) {
    		CommonTree assignmentNode = nodes.get(i);
    		CommonTree nameNode = childAt(0, assignmentNode);
    		String name = (nameNode.getType() == BeneratorLexer.QUALIFIEDNAME ? childAt(0, nameNode).getText() : nameNode.getText());
    		CommonTree exNode = childAt(1, assignmentNode);
    		Expression<?> ex = convertNode(exNode);
			assignments[i - firstIndex] = new Assignment(name, ex);
    	}
		return assignments;
    }

	private static Expression<?> convertInvocation(CommonTree node) throws ParseException {
    	String[] qn = convertQualifiedNameToStringArray(childAt(0, node));
    	Expression<?>[] argExpressions = parseArguments(childAt(1, node));
    	return new QNInvocationExpression(qn, argExpressions);
    }

    private static Expression<?> convertSubInvocation(CommonTree node) throws ParseException {
    	Expression<?> object = convertNode(childAt(0, node));
    	String methodMame = (String) convertNode(childAt(1, node)).evaluate(null);
    	Expression<?>[] argsExpressions = parseArguments(childAt(2, node));
    	return new InvocationExpression(object, methodMame, argsExpressions);
    }

    private static Expression<?> convertIndex(CommonTree node) throws ParseException {
		return new IndexExpression(convertNode(childAt(0, node)), convertNode(childAt(1, node)));
    }

    private static Expression<?> convertField(CommonTree node) throws ParseException {
    	return new FieldExpression(convertNode(childAt(0, node)), convertIdentifier(childAt(1, node)).evaluate(null));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Expression<?> convertCast(CommonTree node) throws ParseException {
    	Class<?> targetType = (Class<?>) convertNode(childAt(0, node)).evaluate(null);
		Expression<?> sourceExpression = convertNode(childAt(1, node));
		return new TypeConvertingExpression(sourceExpression, targetType);
    }

    private static Expression<?> convertNegation(CommonTree node) throws ParseException {
		return new NegationExpression(convertNode(childAt(0, node)));
    }

    private static Expression<?> convertLogicalComplement(CommonTree node) throws ParseException {
		return new LogicalComplementExpression(convertNode(childAt(0, node)));
    }

    private static Expression<?> convertBitwiseComplement(CommonTree node) throws ParseException {
		return new BitwiseComplementExpression(convertNode(childAt(0, node)));
    }

	private static String parseQualifiedNameOfClass(CommonTree node) {
		List<CommonTree> childNodes = getChildNodes(node);
		StringBuffer className = new StringBuffer();
		for (CommonTree childNode : childNodes) {
			if (className.length() > 0)
				className.append('.');
			className.append(childNode.getText());
		}
		return className.toString();
    }

    private static Expression<?>[] parseArguments(CommonTree node) throws ParseException {
		List<CommonTree> childNodes = getChildNodes(node);
		if (childNodes == null)
			return EMPTY_ARGUMENT_LIST;
		Expression<?>[] result = new Expression[childNodes.size()];
		for (int i = 0; i < childNodes.size(); i++) {
			CommonTree childNode = childNodes.get(i);
			result[i] = convertNode(childNode);
		}
		return result;
    }

	private static Expression<?> convertPlus(CommonTree node) throws ParseException {
		return new SumExpression(convertNode(childAt(0, node)), convertNode(childAt(1, node)));
    }

	private static Expression<?> convertMinus(CommonTree node) throws ParseException {
		return new SubtractionExpression(convertNode(childAt(0, node)), convertNode(childAt(1, node)));
    }

	private static Expression<?> convertStar(CommonTree node) throws ParseException {
		return new MultiplicationExpression(convertNode(childAt(0, node)), convertNode(childAt(1, node)));
    }

    private static Expression<?> convertSlash(CommonTree node) throws ParseException {
		return new DivisionExpression(convertNode(childAt(0, node)), convertNode(childAt(1, node)));
    }

    private static Expression<Object> convertPercent(CommonTree node) throws ParseException {
		return new ModuloExpression(convertNode(childAt(0, node)), convertNode(childAt(1, node)));
    }

    private static Expression<Object> convertShiftLeft(CommonTree node) throws ParseException {
		return new LeftShiftExpression(convertNode(childAt(0, node)), convertNode(childAt(1, node)));
    }

    private static Expression<Object> convertShiftRight(CommonTree node) throws ParseException {
		return new RightShiftExpression(convertNode(childAt(0, node)), convertNode(childAt(1, node)));
    }

    private static Expression<Object> convertShiftRight2(CommonTree node) throws ParseException {
		return new UnsignedRightShiftExpression(convertNode(childAt(0, node)), convertNode(childAt(1, node)));
    }

    private static Expression<Object> convertAnd(CommonTree node) throws ParseException {
		return new BitwiseAndExpression(convertNode(childAt(0, node)), convertNode(childAt(1, node)));
    }

    private static Expression<Object> convertInclusiveOr(CommonTree node) throws ParseException {
		return new BitwiseOrExpression(convertNode(childAt(0, node)), convertNode(childAt(1, node)));
    }

    private static Expression<Object> convertExclusiveOr(CommonTree node) throws ParseException {
		return new BitwiseExclusiveOrExpression(convertNode(childAt(0, node)), convertNode(childAt(1, node)));
    }

    private static Expression<Boolean> convertEquals(CommonTree node) throws ParseException {
		return new EqualsExpression(convertNode(childAt(0, node)), convertNode(childAt(1, node)));
    }
    
    private static Expression<Boolean> convertNotEquals(CommonTree node) throws ParseException {
		return new NotEqualsExpression(convertNode(childAt(0, node)), convertNode(childAt(1, node)));
    }
    
    private static Expression<Boolean> convertLess(CommonTree node) throws ParseException {
		return new LessExpression(convertNode(childAt(0, node)), convertNode(childAt(1, node)));
    }

    private static Expression<Boolean> convertLessOrEquals(CommonTree node) throws ParseException {
		return new LessOrEqualsExpression(convertNode(childAt(0, node)), convertNode(childAt(1, node)));
    }

    private static Expression<Boolean> convertGreater(CommonTree node) throws ParseException {
		return new GreaterExpression(convertNode(childAt(0, node)), convertNode(childAt(1, node)));
    }

    private static Expression<Boolean> convertGreaterOrEquals(CommonTree node) throws ParseException {
		return new GreaterOrEqualsExpression(convertNode(childAt(0, node)), convertNode(childAt(1, node)));
    }

    private static Expression<Boolean> convertConditionalOr(CommonTree node) throws ParseException {
		return new ConditionalOrExpression(convertNode(childAt(0, node)), convertNode(childAt(1, node)));
    }

    private static Expression<Boolean> convertConditionalAnd(CommonTree node) throws ParseException {
		return new ConditionalAndExpression(
				convertNode(childAt(0, node)), 
				convertNode(childAt(1, node)));
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Expression<?> convertConditionalExpression(CommonTree node) throws ParseException {
		return new ConditionalExpression(
				convertNode(childAt(0, node)),  // condition
				convertNode(childAt(1, node)),  // true alternative 
				convertNode(childAt(2, node))); // false alternative
    }
    
	private static Expression<?> convertAssignment(CommonTree node) throws ParseException {
		return new AssignmentExpression(
				convertQualifiedNameToStringArray(childAt(0, node)),
				convertNode(childAt(1, node)));
    }

    // CommonTree helpers ----------------------------------------------------------------------------------------------

    private static CommonTree childAt(int index, CommonTree node) {
	    return (CommonTree) node.getChild(index);
    }

	@SuppressWarnings("unchecked")
    private static List<CommonTree> getChildNodes(CommonTree node) {
	    return node.getChildren();
    }

}
