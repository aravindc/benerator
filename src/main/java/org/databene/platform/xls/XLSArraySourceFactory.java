/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.xls;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.factory.SourceFactory;
import org.databene.commons.Converter;
import org.databene.commons.HeavyweightTypedIterable;

/**
 * TODO Document class.<br/><br/>
 * Created: 19.07.2011 08:31:10
 * @since TODO version
 * @author Volker Bergmann
 */
public class XLSArraySourceFactory implements SourceFactory<Object[]> {
	
	Converter<String, ?> scriptConverter;
	
	public XLSArraySourceFactory(Converter<String, ?> scriptConverter) {
	    this.scriptConverter = scriptConverter;
    }

	public HeavyweightTypedIterable<Object[]> create(String uri, BeneratorContext context) {
		return new XLSLineIterable(uri, true, scriptConverter);
	}

}
