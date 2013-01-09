/*
 * (c) Copyright 2008-2012 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.consumer;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.databene.commons.CompositeFormatter;
import org.databene.model.data.Entity;

/**
 * Exports generated objects to the standard output.<br/><br/>
 * Created: 27.02.2008 11:40:37
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class ConsoleExporter extends FormattingConsumer {
	
	private Long limit;
	private String indent;
	
	private CompositeFormatter compositeFormatter;
	private PrintStream out = System.out;
	private Map<String, AtomicLong> counters;
	
	// Constructors ----------------------------------------------------------------------------------------------------
	
	public ConsoleExporter() {
		this(null);
	}
	
	public ConsoleExporter(Long limit) {
		this(limit, "");
	}
	
	public ConsoleExporter(Long limit, String indent) {
		this.limit = limit;
		this.indent = indent;
		this.counters = new HashMap<String, AtomicLong>();
		this.compositeFormatter = new CompositeFormatter(true, true);
		this.compositeFormatter.setDatePattern(getDatePattern());
		this.compositeFormatter.setTimestampPattern(getTimestampPattern());
	}
	
	// properties ------------------------------------------------------------------------------------------------------
	
	@Override
	public void setDatePattern(String datePattern) {
		super.setDatePattern(datePattern);
		compositeFormatter.setDatePattern(datePattern);
	}

	@Override
	public void setTimestampPattern(String timestampPattern) {
		super.setTimestampPattern(timestampPattern);
		compositeFormatter.setTimestampPattern(timestampPattern);
	}

	public void setLimit(Long limit) {
    	this.limit = limit;
    }
	
	public void setIndent(String indent) {
		this.indent = indent;
	}
	
	public void setFlat(boolean flat) {
		this.compositeFormatter.setFlat(flat);
	}
	
	public void setOut(PrintStream out) {
		this.out = out;
	}
	
	// Consumer interface implementation -------------------------------------------------------------------------------
	
	@Override
	public void startProductConsumption(Object object) {
		if (object instanceof Entity) {
			String entityType = ((Entity) object).type();
			AtomicLong counter = counters.get(entityType);
			if (counter == null) {
				counter = new AtomicLong(0);
				counters.put(entityType, counter);
			}
			long counterValue = counter.incrementAndGet();
			if (limit == null || limit < 0 || counterValue <= limit)
				out.println(indent + compositeFormatter.render(entityType + '[', (Entity) object, "]"));
			else
				out.print(".");
		} else {
			out.println(plainConverter.convert(object));
		}
    }
	
}
