/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.db;

import org.databene.benerator.util.ThreadSafeGenerator;

/**
 * Generates {@link Long} values from a database sequence.<br/>
 * <br/>
 * Created at 07.07.2009 18:54:53
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class DBSequenceGenerator extends ThreadSafeGenerator<Long> {
	
	private String name;
	private DBSystem source;

    public DBSequenceGenerator(String name) {
	    this(name, null);
    }

    public DBSequenceGenerator(String name, DBSystem source) {
	    this.name = name;
	    this.source = source;
    }
    
	public String getName() {
    	return name;
    }

	public void setName(String name) {
    	this.name = name;
    }

	public DBSystem getSource() {
    	return source;
    }

	public void setSource(DBSystem source) {
    	this.source = source;
    }

	public Class<Long> getGeneratedType() {
	    return Long.class;
    }

    public Long generate() {
	    return source.nextSequenceValue(name);
    }

}
