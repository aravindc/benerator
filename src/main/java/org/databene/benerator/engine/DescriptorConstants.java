/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General License.
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

package org.databene.benerator.engine;

import java.util.Collection;

import org.databene.commons.CollectionUtil;

/**
 * Collects element and attribute names used in Benerator's XML descriptor files.<br/>
 * <br/>
 * Created at 24.07.2009 07:39:19
 * @since 0.6.0
 * @author Volker Bergmann
 */

public interface DescriptorConstants {
	
    static final String EL_SETUP = "setup";
    static final String EL_BEAN = "bean";
	static final String EL_UPDATE_ENTITIES = "update-entities";
	static final String EL_CREATE_ENTITIES = "create-entities";
	static final String EL_CONSUMER = "consumer";
	static final String EL_COMMENT = "comment";
    static final String EL_DEFAULT_COMPONENTS = "defaultComponents";
    static final String EL_EVALUATE = "evaluate";
    static final String EL_EXECUTE = "execute";
    static final String EL_DATABASE = "database";
    static final String EL_ECHO = "echo";
    static final String EL_IMPORT = "import";
    static final String EL_INCLUDE = "include";
    static final String EL_PROPERTY = "property";
    static final String EL_RUN_TASK = "run-task";
    static final String EL_VARIABLE = "variable";
    static final String EL_REFERENCE = "reference";
    static final String EL_ID = "id";
    static final String EL_PART = "part";
    static final String EL_ATTRIBUTE = "attribute";
	
	static final String ATT_PASSWORD = "password";
	static final String ATT_USER = "user";
	static final String ATT_DRIVER = "driver";
	static final String ATT_URL = "url";
	static final String ATT_ID = EL_ID;
	static final String ATT_MESSAGE = "message";
	static final String ATT_SELECTOR = "selector";
    static final String ATT_SOURCE = "source";
    static final String ATT_REF = "ref";
    static final String ATT_VALUE = "value";
    static final String ATT_NAME = "name";
    static final String ATT_ON_ERROR = "onError";
    static final String ATT_CONSUMER = "consumer";
    static final String ATT_THREADS = "threads";
    static final String ATT_PAGESIZE = "pagesize";
    static final String ATT_PAGER = "pager";
    static final String ATT_COUNT = "count";
    static final String ATT_ASSERT = "assert";
    static final String ATT_TYPE = "type";
    static final String ATT_OPTIMIZE = "optimize";
    static final String ATT_ENCODING = "encoding";
    static final String ATT_TARGET = "target";
    static final String ATT_URI = "uri";
    static final String ATT_READ_ONLY = "readOnly";
    static final String ATT_FETCH_SIZE = "fetchSize";
    static final String ATT_BATCH = "batch";
    static final String ATT_SCHEMA = "schema";
	
    static final String ATT_CLASS = "class";
    static final String ATT_SPEC = "spec";

	static final Collection<String> CREATE_ENTITIES_EXT_SETUP = CollectionUtil
		.toSet(ATT_PAGESIZE, ATT_THREADS, ATT_CONSUMER, ATT_ON_ERROR);

	static final Collection<String> COMPONENT_TYPES = CollectionUtil.toSet(EL_ATTRIBUTE, EL_PART, EL_ID, EL_REFERENCE);

}
