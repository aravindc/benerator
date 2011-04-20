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

import org.databene.benerator.engine.BeneratorRootStatement;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.statement.BeepStatement;
import org.databene.benerator.engine.statement.IfStatement;
import org.databene.benerator.engine.statement.WhileStatement;
import org.w3c.dom.Element;

/**
 * Parses a &lt;beep/&gt; descriptor.<br/><br/>
 * Created: 14.09.2010 14:35:23
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class BeepParser extends AbstractBeneratorDescriptorParser {

	public BeepParser() {
	    super("beep", null, null, 
	    		BeneratorRootStatement.class, IfStatement.class, WhileStatement.class);
    }

	@Override
	public BeepStatement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
        return new BeepStatement();
    }

}
