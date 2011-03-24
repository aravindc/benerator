/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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
import static org.databene.benerator.engine.parser.xml.DescriptorParserUtil.*;

import org.databene.benerator.Generator;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.statement.WaitStatement;
import org.databene.benerator.factory.GeneratorFactoryUtil;
import org.databene.benerator.primitive.DynamicLongGenerator;
import org.databene.benerator.util.ExpressionBasedGenerator;
import org.databene.commons.Expression;
import org.databene.commons.expression.ExpressionUtil;
import org.databene.model.data.Uniqueness;
import org.w3c.dom.Element;

/**
 * Parses a 'wait' element.<br/><br/>
 * Created: 21.02.2010 08:07:59
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class WaitParser extends AbstractBeneratorDescriptorParser {

	public WaitParser() {
	    super(EL_WAIT);
    }

	@Override
	public Statement parse(Element element, Statement[] parentPath, BeneratorParseContext context) {
		// check for constant value
		Expression<Long> duration  = parseLongAttribute(ATT_DURATION, element, null);
		if (duration != null)
			return new WaitStatement(new ExpressionBasedGenerator<Long>(duration, Long.class));
		
		// check for distribution
		Expression<Long> min  = parseLongAttribute(ATT_MIN, element, null);
		Expression<Long> max  = parseLongAttribute(ATT_MAX, element, null);
		Expression<Long> precision  = parseLongAttribute(ATT_PRECISION, element, null);
		String distSpec  = getAttribute(ATT_DISTRIBUTION, element);
		Expression<Distribution> distribution 
			= GeneratorFactoryUtil.getDistributionExpression(distSpec, Uniqueness.NONE, false);
		Generator<Long> durationGenerator = new DynamicLongGenerator(min, max, precision, 
				distribution, ExpressionUtil.constant(false));
	    return new WaitStatement(durationGenerator);
    }

}
