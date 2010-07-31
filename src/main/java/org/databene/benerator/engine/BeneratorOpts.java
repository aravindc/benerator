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

package org.databene.benerator.engine;

import org.databene.commons.StringUtil;

/**
 * Provides support for Benerator's system property settings.<br/><br/>
 * Created: 30.07.2010 18:25:01
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class BeneratorOpts {
	
	public static final String OPTS_VALIDATE = "benerator.validate";
	public static final String OPTS_CACHE_SIZE = "benerator.cacheSize";

	private static final int DEFAULT_CACHE_SIZE = 100000;
	
	public static void setValidating(boolean validating) {
		System.setProperty(OPTS_VALIDATE, String.valueOf(validating));
	}

	public static boolean isValidating() {
		return !("false".equals(System.getProperty(OPTS_VALIDATE)));
	}

	public static int getCacheSize() {
		return parseIntProperty(OPTS_CACHE_SIZE, DEFAULT_CACHE_SIZE);
	}
	
	private static int parseIntProperty(String propertyKey, int defaultValue) {
		String propertyValue = System.getProperty(propertyKey);
		return (StringUtil.isEmpty(propertyValue) ? defaultValue : Integer.parseInt(propertyValue));
	}
	
}
