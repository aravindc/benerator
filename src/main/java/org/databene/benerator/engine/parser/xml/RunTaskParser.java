/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

import static org.databene.benerator.engine.DescriptorConstants.*;

import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.expression.ErrorHandlerExpression;
import org.databene.benerator.engine.expression.context.DefaultPageSizeExpression;
import org.databene.benerator.engine.statement.RunTaskStatement;
import org.databene.benerator.script.BeneratorScriptParser;
import org.databene.commons.ConfigurationError;
import org.databene.commons.ConversionException;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Expression;
import org.databene.task.PageListener;
import org.databene.task.Task;
import org.w3c.dom.Element;

/**
 * Parses a run-task descriptor.<br/><br/>
 * Created: 25.10.2009 00:55:16
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class RunTaskParser extends AbstractDescriptorParser {
	
	private static final DefaultPageSizeExpression DEFAULT_PAGE_SIZE = new DefaultPageSizeExpression();

	public RunTaskParser() {
	    super(EL_RUN_TASK);
    }

	@SuppressWarnings("unchecked")
    public RunTaskStatement parse(Element element, ResourceManager resourceManager) {
		try {
			Expression<Task> taskProvider = (Expression<Task>) BeanParser.parseBeanExpression(element);
			Expression<Long> count    = parseLongAttr(ATT_COUNT, element, 1);
			Expression<Long> pageSize = parseLongAttr(ATT_PAGESIZE, element, DEFAULT_PAGE_SIZE);
			Expression<Integer> threads  = parseIntAttr(ATT_THREADS, element, 1);
			Expression<PageListener> pager    = parsePager(element);
			Expression<ErrorHandler> errorHandler = parseErrorHandler(element);
			return new RunTaskStatement(taskProvider, count, pageSize, pager, threads, errorHandler);
		} catch (ConversionException e) {
			throw new ConfigurationError(e);
        }
	}

    private Expression<ErrorHandler> parseErrorHandler(Element element) {
    	String id = element.getAttribute(ATT_ID);
		Expression<String> level = parseStringAttr(ATT_ON_ERROR, element);
		return new ErrorHandlerExpression(id, level);
    }

	@SuppressWarnings("unchecked")
    private Expression<PageListener> parsePager(Element element) {
		String pagerSpec = element.getAttribute(ATT_PAGER);
		return (Expression<PageListener>) BeneratorScriptParser.parseBeanSpec(pagerSpec);
	}

}
