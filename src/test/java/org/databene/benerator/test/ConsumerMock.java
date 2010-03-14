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

package org.databene.benerator.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.databene.model.consumer.Consumer;

/**
 * Mock implementation of the {@link Consumer} interface to be used for testing.<br/><br/>
 * Created: 11.03.2010 12:51:40
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ConsumerMock<E> implements Consumer<E> {
	
	public static Map<Integer, ConsumerMock<?>> instances = new HashMap<Integer, ConsumerMock<?>>();
	
	private final int minDelay;
	private final int delayDelta;
	
	private final boolean storeProducts;
	public List<E> products;

	public volatile AtomicInteger startConsumingCount = new AtomicInteger();
	public volatile AtomicInteger finishConsumingCount = new AtomicInteger();
	public volatile AtomicInteger flushCount = new AtomicInteger();
	public volatile AtomicInteger closeCount = new AtomicInteger();

	private Random random;
	
	public ConsumerMock(boolean storeProducts) {
	    this(storeProducts, 0, 0, 0);
    }

	public ConsumerMock(boolean storeProducts, int id) {
	    this(storeProducts, id, 0, 0);
    }

	public ConsumerMock(boolean storeProducts, int id, int minDelay, int maxDelay) {
	    this.storeProducts = storeProducts;
	    this.minDelay = minDelay;
	    if (maxDelay > 0) {
	    	this.delayDelta = maxDelay - minDelay;
	    	random = new Random();
	    } else
	    	this.delayDelta = 0;
	    if (storeProducts)
	    	products = new ArrayList<E>();
	    instances.put(id, this);
    }

	public void startConsuming(E product) {
	    startConsumingCount.incrementAndGet();
	    if (storeProducts) {
	    	synchronized (products) {
	            products.add(product);
            }
	    }
	    if (random != null) {
	    	try {
	    		Thread.sleep(minDelay + random.nextInt(delayDelta));
	    	} catch (InterruptedException e) {
	    		// nothing to do
	    	}
	    }
    }

	public void finishConsuming(E product) {
	    finishConsumingCount.incrementAndGet();
    }

	public void flush() {
	    flushCount.incrementAndGet();
    }

	public void close() {
	    closeCount.incrementAndGet();
    }

}
