/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
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

package org.databene.benerator.engine.parser.xml;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.statement.TranscodeStatement;
import org.databene.benerator.engine.statement.TranscodingTaskStatement;
import org.databene.benerator.factory.DescriptorUtil;

import static org.databene.benerator.engine.parser.xml.DescriptorParserUtil.*;

import org.databene.commons.ArrayUtil;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Expression;
import org.databene.commons.expression.FallbackExpression;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.DataModel;
import org.databene.platform.db.DBSystem;
import org.w3c.dom.Element;

/**
 * Parses a &lt;transcode&gt; element.<br/><br/>
 * Created: 08.09.2010 16:13:13
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class TranscodeParser extends AbstractTranscodeParser {

	public TranscodeParser() {
	    super("transcode", TranscodingTaskStatement.class);
    }

    @Override
    public Statement parse(Element element, Statement[] parentPath, BeneratorParseContext context) {
		String table = getRawAttribute("table", element);
		TranscodingTaskStatement parent = (TranscodingTaskStatement) ArrayUtil.lastElement(parentPath);
		Expression<DBSystem> sourceEx   = parseSource(element, parent);
		Expression<String>   selectorEx = parseSelector(element, parent);
		Expression<DBSystem> targetEx   = parseTarget(element, parent);
		Expression<Long>     pageSizeEx = parsePageSize(element, parent);
	    Expression<ErrorHandler> errorHandlerEx = parseOnErrorAttribute(element, table);
	    return new TranscodeStatement(new TypeExpression(element), 
	    		parent, sourceEx, selectorEx, targetEx, pageSizeEx, errorHandlerEx);
    }

	private Expression<String> parseSelector(Element element, TranscodingTaskStatement parent) {
		return parseScriptableStringAttribute("selector", element);
	}

	@SuppressWarnings("unchecked")
    private Expression<Long> parsePageSize(Element element, Statement parent) {
	    Expression<Long> result = super.parsePageSize(element);
	    if (parent instanceof TranscodingTaskStatement)
			result = new FallbackExpression<Long>(result, ((TranscodingTaskStatement) parent).getPageSizeEx());
	    return result;
    }

	@SuppressWarnings("unchecked")
    private Expression<DBSystem> parseSource(Element element, Statement parent) {
	    Expression<DBSystem> result = super.parseSource(element);
	    if (parent instanceof TranscodingTaskStatement)
			result = new FallbackExpression<DBSystem>(result, ((TranscodingTaskStatement) parent).getSourceEx());
	    return result;
    }

	@SuppressWarnings("unchecked")
    private Expression<DBSystem> parseTarget(Element element, Statement parent) {
	    Expression<DBSystem> result = super.parseTarget(element);
	    if (parent instanceof TranscodingTaskStatement)
			result = new FallbackExpression<DBSystem>(result, ((TranscodingTaskStatement) parent).getTargetEx());
	    return result;
    }

	public class TypeExpression implements Expression<ComplexTypeDescriptor> {

		private Element element;
		private String tableName;

		public TypeExpression(Element element) {
			this.element = element;
			this.tableName = getRawAttribute("table", element);
		}

		public ComplexTypeDescriptor evaluate(Context context) {
		    ComplexTypeDescriptor parent = (ComplexTypeDescriptor) DataModel.getDefaultInstance().getTypeDescriptor(tableName);
		    ComplexTypeDescriptor type = new ComplexTypeDescriptor(tableName, parent);
		    DescriptorUtil.parseComponentConfig(element, type, (BeneratorContext) context);
		    return type;
		}

		public boolean isConstant() {
			return true;
		}

	}

}
