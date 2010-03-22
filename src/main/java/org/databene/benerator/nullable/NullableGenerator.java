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

package org.databene.benerator.nullable;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.Resettable;
import org.databene.commons.ThreadAware;

import com.sun.xml.internal.ws.Closeable;

/**
 * Interface for classes that can generate <code>null</code> values.
 * For differing between a generated <code>null</code> and unavailability,
 * a {@link ProductWrapper} class is introduced. It may wrap a <code>null</code>
 * value that has been generated or may be <code>null</code> itself for 
 * declaring unavailability.<br/><br/>
 * Created: 26.01.2010 17:11:16
 * @since 0.6.0
 * @author Volker Bergmann
 */
public interface NullableGenerator<E> extends Resettable, Closeable, ThreadAware {
    Class<E> getGeneratedType();
	void init(GeneratorContext context) throws InvalidGeneratorSetupException;
    public ProductWrapper<E> generate(ProductWrapper<E> wrapper);
    public void close();
}
