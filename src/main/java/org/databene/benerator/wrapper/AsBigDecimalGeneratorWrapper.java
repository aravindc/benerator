/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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

import java.math.BigDecimal;
import java.math.MathContext;

import org.databene.benerator.Generator;
import org.databene.commons.MathUtil;

/**
 * Converts the {@link Number} products of another {@link Generator} to {@link BigDecimal}.<br/>
 * <br/>
 * Created at 23.06.2009 22:58:26
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class AsBigDecimalGeneratorWrapper<E extends Number> extends GeneratorWrapper<E, BigDecimal> {

	private int fractionDigits;
	
    public AsBigDecimalGeneratorWrapper(Generator<E> source) {
	    this(source, null, null);
    }

    public AsBigDecimalGeneratorWrapper(Generator<E> source, BigDecimal min, BigDecimal precision) {
	    super(source);
	    if (precision != null) {
	    	this.fractionDigits = MathUtil.fractionDigits(precision.doubleValue());
	    	if (min != null)
	    		this.fractionDigits = Math.max(this.fractionDigits, MathUtil.fractionDigits(min.doubleValue()));
	    } else if (min != null)
	    	this.fractionDigits = MathUtil.fractionDigits(min.doubleValue());
	    else
	    	this.fractionDigits = 0;
    }

	public Class<BigDecimal> getGeneratedType() {
	    return BigDecimal.class;
    }

    public BigDecimal generate() {
	    E feed = source.generate();
	    if (feed == null)
	    	return null;
	    double d = feed.doubleValue();
		MathContext mathcontext = new MathContext(MathUtil.prefixDigits(d) + fractionDigits);
		return new BigDecimal(d, mathcontext);
    }

}
