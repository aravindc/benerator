/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.distribution.sequence;

import java.util.ArrayList;

import org.databene.benerator.util.RandomUtil;

/**
 * Helper class for the {@link ExpandGeneratorProxy}. 
 * It can buffer a number of values provided from a source and provide a randomly selected one.
 * The class is optimized for performance: As long as the source is available, the client 
 * can fetch a random value by calling randomElement(E feed) providing a new value from the 
 * source. The {@link ValueBucket} will then select a random element from its buffer, return 
 * it and replace the internal value with the new feed value. When the source is no longer 
 * available, a call to randomElement() will return the last value from the buffer and remove 
 * it (avoiding expensive shift operations that would result from choosing value from a random 
 * position).<br/>
 * <br/>
 * Created: 10.12.2009 15:10:33
 * @since 0.6.0
 * @author Volker Bergmann
 */
class ValueBucket<E> {
	
	private ArrayList<E> buffer;
	
	public ValueBucket(int capacity) {
		buffer = new ArrayList<E>();
	}
	
	// interface -------------------------------------------------------------------------------------------------------

	public boolean isEmpty() {
	    return buffer.isEmpty();
    }

	public void add(E feed) {
		buffer.add(feed);
	}
	
	public synchronized E getRandomElement() {
		return buffer.get(RandomUtil.randomIndex(buffer));
	}
	
	public synchronized E getAndReplaceRandomElement(E feed) {
		int index = RandomUtil.randomIndex(buffer);
		E result = buffer.get(index);
		buffer.set(index, feed);
		return result;
	}
	
	public synchronized E getAndRemoveRandomElement() {
		int lastIndex = buffer.size() - 1;
		E result = buffer.get(lastIndex);
		buffer.remove(lastIndex);
		return result;
	}

	public int size() {
	    return buffer.size();
    }
	
	// java.lang.Object overrides --------------------------------------------------------------------------------------

	@Override
	public String toString() {
		return getClass().getSimpleName() + buffer;
	}
	
}
