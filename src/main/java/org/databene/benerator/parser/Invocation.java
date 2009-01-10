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

import java.util.Arrays;

import org.databene.commons.ArrayFormat;
import org.databene.commons.Assert;

/**
 * Represents an invocation of something 
 * that is identified by a fully qualified name and has zero or more parameters.<br/>
 * <br/>
 * Created at 01.01.2009 13:30:12
 * @since 0.5.7
 * @author Volker Bergmann
 */

public class Invocation {
	
	private String fqName;
	private Object[] parameters;
	
	public Invocation(String fqName, Object... parameters) {
		Assert.notNull(fqName, "fqName");
		Assert.notNull(parameters, "parameters");
		this.fqName = fqName;
		this.parameters = parameters;
	}
	
	// properties ------------------------------------------------------------------------------------------------------
	
	public String getFqName() {
		return fqName;
	}

	public Object[] getParameters() {
		return parameters;
	}
	
	// java.lang.Object overrides --------------------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return fqName + '(' + ArrayFormat.format(", ", parameters) + ')';
	}

	@Override
	public int hashCode() {
		return ((fqName == null) ? 0 : fqName.hashCode()) * 31 + Arrays.hashCode(parameters);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Invocation that = (Invocation) obj;
		return (this.fqName.equals(that.fqName) && Arrays.equals(parameters, that.parameters));
	}
	
}
