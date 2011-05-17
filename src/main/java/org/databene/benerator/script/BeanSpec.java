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

package org.databene.benerator.script;

/**
 * A bean specification which can declare if it wraps a 'value' or just represents a 'reference'.
 * This is used for managing scopes with 'local' objects and references to 'global' ones.<br/><br/>
 * Created: 13.04.2011 19:07:09
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class BeanSpec {
	
	private Object bean;
	private boolean reference;

	public BeanSpec(Object bean, boolean reference) {
		this.bean = bean;
		this.reference = reference;
	}

	public Object getBean() {
		return bean;
	}
	
	public boolean isReference() {
		return reference;
	}

	public static BeanSpec createReference(Object bean) {
		return new BeanSpec(bean, true);
	}

	public static BeanSpec createConstruction(Object bean) {
		return new BeanSpec(bean, false);
	}
	
	@Override
	public String toString() {
		return (reference ? "reference to " : "creation of ") + bean;
	}
	
}
