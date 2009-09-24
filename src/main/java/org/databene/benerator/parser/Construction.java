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

package org.databene.benerator.parser;

import org.databene.commons.BeanUtil;
import org.databene.commons.Context;
import org.databene.commons.Expression;
import org.databene.commons.bean.ClassProvider;
import org.databene.commons.bean.DefaultClassProvider;

/**
 * Represents the construction of an object of a certain class name.<br/>
 * <br/>
 * Created at 01.01.2009 18:44:17
 * @since 0.5.7
 * @author Volker Bergmann
 */

public class Construction implements Expression {
	
	protected static ClassProvider defaultClassProvider = new DefaultClassProvider();
	
	protected ClassProvider classProvider;
	protected String className;
	protected Class<?> type;

	public Construction(String className, ClassProvider classProvider) {
		this.className = className;
		this.classProvider = classProvider;
	}

	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}

	public Class<?> getType() {
		if (type == null) {
			if (classProvider != null)
				type = classProvider.forName(className);
			else
				type = defaultClassProvider.forName(className);
		}
		return type;
	}
	
	public boolean classExists() {
		try {
			getType();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public Object evaluate(Context contex) {
		return BeanUtil.newInstance(getType());
	}

}
