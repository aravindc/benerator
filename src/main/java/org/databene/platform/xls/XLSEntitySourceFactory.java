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

package org.databene.platform.xls;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.factory.SourceFactory;
import org.databene.commons.Converter;
import org.databene.model.data.Entity;
import org.databene.model.data.EntitySource;

/**
 * {@link SourceFactory} implementation which creates XLS entity sources.<br/><br/>
 * Created: 05.05.2010 15:08:03
 * @since 0.6.1
 * @author Volker Bergmann
 */
public class XLSEntitySourceFactory implements SourceFactory<Entity> {
	
	private String type; // TODO 
	private Converter<String, ?> scriptConverter;
	
	public XLSEntitySourceFactory(String type, Converter<String, ?> scriptConverter) {
		this.type = type;
	    this.scriptConverter = scriptConverter;
    }

	public EntitySource create(String id, BeneratorContext context) {
        XLSEntitySource source = new XLSEntitySource(id, scriptConverter);
        source.setContext(context);
        return source;
	}

}
