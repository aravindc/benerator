/*
 * (c) Copyright 2006-2013 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
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

package org.databene.benerator.wrapper;

import org.databene.commons.BeanUtil;
import org.databene.benerator.*;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.SequenceManager;

import java.util.*;

/**
 * Combines a a random number a source generator's products into a collection.<br/>
 * <br/>
 * Created: 07.07.2006 19:13:22
 * @since 0.1
 * @author Volker Bergmann
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CollectionGenerator<C extends Collection, I> extends CardinalGenerator<I, C> {

    /** The collection type to create */
    private Class<C> collectionType;

    // constructors ----------------------------------------------------------------------------------------------------

    public CollectionGenerator() {
        this(((Class<C>)List.class), null, 0, 30, SequenceManager.RANDOM_SEQUENCE);
    }

    public CollectionGenerator(Class<C> collectionType, Generator<I> source, 
    		int minSize, int maxSize, Distribution sizeDistribution) {
        super(source, false, minSize, maxSize, 1, sizeDistribution);
        this.collectionType = mapCollectionType(collectionType);
    }

    // configuration properties ----------------------------------------------------------------------------------------

    public Class<C> getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(Class<C> collectionType) {
        this.collectionType = collectionType;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    /** ensures consistency of the state */
    @Override
    public void init(GeneratorContext context) {
        if (collectionType == null)
            throw new InvalidGeneratorSetupException("collectionType", "undefined");
        super.init(context);
    }

    @Override
	public Class<C> getGeneratedType() {
        return collectionType;
    }

	@Override
	public ProductWrapper<C> generate(ProductWrapper<C> wrapper) {
        assertInitialized();
        Integer size = generateCardinal();
        if (size == null)
        	return null;
        C collection = BeanUtil.newInstance(collectionType);
        for (int i = 0; i < size; i++) {
        	ProductWrapper<I> item = generateFromSource();
        	if (item == null)
        		return null;
            collection.add(item.unwrap());
        }
        return wrapper.wrap(collection);
    }

    // implementation --------------------------------------------------------------------------------------------------

    /** maps abstract collection types to concrete ones */
    private static <C extends Collection> Class<C> mapCollectionType(Class<C> collectionType) {
        if (List.class.equals(collectionType))
            return (Class<C>) ArrayList.class;
        else if (Set.class.equals(collectionType))
            return (Class<C>) HashSet.class;
        else
            return collectionType;
    }

}
