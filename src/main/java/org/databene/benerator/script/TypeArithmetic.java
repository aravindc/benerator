/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.script;

/**
 * Parent class for defining arithmetics for special JDK or custom data types.<br/>
 * <br/>
 * Created at 06.10.2009 10:28:30
 * @since 0.6.0
 * @author Volker Bergmann
 */

public abstract class TypeArithmetic<E> {
	
	protected Class<E> baseType;
	
    public TypeArithmetic(Class<E> baseType) {
	    this.baseType = baseType;
    }
    
	public Class<E> getBaseType() {
		return baseType;
	}
	
	public abstract E add(Object summand1, Object summand2) throws IllegalArgumentException, UnsupportedOperationException;

    public abstract Object subtract(Object minuend, Object subtrahend) throws IllegalArgumentException, UnsupportedOperationException;

    public abstract Object multiply(Object factor1, Object factor2) throws IllegalArgumentException, UnsupportedOperationException;

    public abstract Object divide(Object quotient, Object divisor) throws IllegalArgumentException, UnsupportedOperationException;

}
