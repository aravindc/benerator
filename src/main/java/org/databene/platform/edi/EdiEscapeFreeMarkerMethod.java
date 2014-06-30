/*
 * (c) Copyright 2014 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.edi;

import java.util.List;

import freemarker.template.TemplateMethodModel;

/**
 * Provides Edifact character escaping in FreeMarker templates.<br/><br/>
 * Created: 30.06.2014 11:14:26
 * @since 0.9.7
 * @author Volker Bergmann
 */

public class EdiEscapeFreeMarkerMethod implements TemplateMethodModel {

	private char componentSeparator = ':';
	private char elementSeparator = '+';
	private char escapeChar = '?';
	private char segmentSeparator = '\'';

	public char getComponentSeparator() {
		return componentSeparator;
	}

	public void setComponentSeparator(char componentSeparator) {
		this.componentSeparator = componentSeparator;
	}

	public char getElementSeparator() {
		return elementSeparator;
	}

	public void setElementSeparator(char elementSeparator) {
		this.elementSeparator = elementSeparator;
	}

	public char getEscapeChar() {
		return escapeChar;
	}

	public void setEscapeChar(char escapeChar) {
		this.escapeChar = escapeChar;
	}

	public char getSegmentSeparator() {
		return segmentSeparator;
	}

	public void setSegmentSeparator(char segmentSeparator) {
		this.segmentSeparator = segmentSeparator;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object exec(List args) {
		StringBuilder builder = new StringBuilder();
		for (int index = 0; index < args.size(); index++) {
			if (index > 0)
				builder.append(':');
			String s = String.valueOf(args.get(index));
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (needsEscaping(c))
					builder.append(escapeChar);
				builder.append(c);
			}
		}
		return builder.toString();
	}

	private boolean needsEscaping(char c) {
		return (c == componentSeparator || c == elementSeparator || 
				c == escapeChar || c == segmentSeparator);
	}

}
