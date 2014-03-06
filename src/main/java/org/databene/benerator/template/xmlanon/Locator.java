/*
 * (c) Copyright 2014 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.template.xmlanon;

/**
 * Denominates XML elements in an XML file and provides their mapping 
 * to Benerator entity and attribute names.<br/><br/>
 * Created: 27.02.2014 14:11:24
 * @since 0.9.0
 * @author Volker Bergmann
 */

public class Locator {
	
	private String file;
	private String path;
	private String entityPath;
	private String entity;
	private String attribute;
	
	public Locator(String file, String path, String entityPath, String entity, String attribute) {
		this.file = file;
		this.path = path;
		this.entityPath = entityPath;
		this.entity = entity;
		this.attribute = attribute;
	}

	public String getFile() {
		return file;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getEntityPath() {
		return entityPath;
	}

	public String getEntity() {
		return entity;
	}

	public String getAttribute() {
		return attribute;
	}
	
	@Override
	public String toString() {
		return file + "[" + entityPath + "/" + attribute + "]";
	}
	
}