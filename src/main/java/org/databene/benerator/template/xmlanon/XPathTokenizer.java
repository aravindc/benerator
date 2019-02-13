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

package org.databene.benerator.template.xmlanon;

import java.util.ArrayList;
import java.util.List;

/**
 * Tokenizes XPath expressions.<br><br>
 * Created: 27.02.2014 15:01:24
 * @since 0.9.0
 * @author Volker Bergmann
 */

public class XPathTokenizer {
	
	public static List<String> tokenize(String path) {
		ArrayList<String> list = new ArrayList<String>();
		int depth = 0;
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < path.length(); i++) {
			char c = path.charAt(i);
			switch (c) {
			case '[': depth++; builder.append(c); break;
			case ']': depth--; builder.append(c); break;
			case '/': if (depth == 0) {
						list.add(builder.toString());
						builder.delete(0, builder.length());
						break;
						}
				//$FALL-THROUGH$
			default: builder.append(c);
			}
		}
		if (builder.length() > 0)
			list.add(builder.toString());
		return list;
	}

	public static String merge(List<String> tokens, int from, int to) {
		StringBuilder builder = new StringBuilder();
		for (int i = from; i <= to; i++) {
			if (i > from)
				builder.append('/');
			builder.append(tokens.get(i));
		}
		return builder.toString();
	}
	
	public static String nodeName(String token) {
		int sep = token.indexOf('[');
		if (sep < 0)
			return token;
		else
			return token.substring(0, sep);
	}
	
}
