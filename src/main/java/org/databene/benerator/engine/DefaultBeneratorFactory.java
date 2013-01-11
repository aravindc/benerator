/*
 * (c) Copyright 2010-2012 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.BeneratorFactory;
import org.databene.benerator.Consumer;
import org.databene.benerator.engine.parser.xml.BeneratorParseContext;
import org.databene.benerator.factory.ComplexTypeGeneratorFactory;
import org.databene.benerator.factory.SimpleTypeGeneratorFactory;
import org.databene.commons.Converter;
import org.databene.commons.Validator;
import org.databene.commons.context.ContextAware;

/**
 * Default implementation of the abstract {@link BeneratorFactory} class.<br/><br/>
 * Created: 08.09.2010 15:45:25
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class DefaultBeneratorFactory extends BeneratorFactory {

	@Override
	public BeneratorContext createContext(String contextUri) {
		return new DefaultBeneratorContext();
	}
	
	@Override
    public BeneratorParseContext createParseContext(ResourceManager resourceManager) {
		return new BeneratorParseContext(resourceManager);
    }

	@Override
	public ComplexTypeGeneratorFactory getComplexTypeGeneratorFactory() {
		return ComplexTypeGeneratorFactory.getInstance();
	}

	@Override
	public SimpleTypeGeneratorFactory getSimpleTypeGeneratorFactory() {
		return SimpleTypeGeneratorFactory.getInstance();
	}
	
	@Override
	public <S, T> Converter<S, T> configureConverter(Converter<S, T> converter, BeneratorContext context) {
    	if (converter instanceof ContextAware)
    		((ContextAware) converter).setContext(context);
		return converter;
	}
	
	@Override
	public <T> Validator<T> configureValidator(Validator<T> validator, BeneratorContext context) {
    	if (validator instanceof ContextAware)
    		((ContextAware) validator).setContext(context);
		return validator;
	}
	
	@Override
	public Consumer configureConsumer(Consumer consumer, BeneratorContext context) {
    	if (consumer instanceof ContextAware)
    		((ContextAware) consumer).setContext(context);
    	return consumer;
	}

}
