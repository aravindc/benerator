/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
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

package org.databene.model.data;

import org.databene.model.operation.FirstArgSelector;
import org.databene.model.Operation;

/**
 * A FeatureDescriptor is composed og FeatureDetails, which have name, value, type and default value.<br/>
 * <br/>
 * Created: 03.08.2007 06:57:42
 */
public class FeatureDetail<E> {

    private String name;
    private Class<E> type;
    private E value;
    private E defaultValue;
    private Operation<E, E> combinator;
    private boolean constraint;

    public FeatureDetail(String name, Class<E> type, boolean constraint, E defaultValue) {
        this(name, type, constraint, defaultValue, new FirstArgSelector<E>());
    }

    public FeatureDetail(String name, Class<E> type, boolean constraint, E defaultValue, Operation<E, E> combinator) {
        this(name, type, constraint, defaultValue, null, combinator);
    }

    public FeatureDetail(String name, Class<E> type, boolean constraint, E defaultValue, E value, Operation<E, E> combinator) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.value = value;
        this.constraint = constraint;
        this.combinator = combinator;
    }

    public String getName() {
        return name;
    }

    public Class<E> getType() {
        return type;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }

    public Object getDefault() {
/*
        Object result = get(this.name);
        if (result == null)
            result = defaultValue;
        return result;
*/
        return defaultValue;
    }

    public E combineWith(E otherValue) {
        return combinator.perform(this.value, otherValue);
    }

    public boolean isConstraint() {
        return constraint;
    }

    public String getDescription() {
        return name + '=' + value + " (" + type + ')';
    }

    public String toString() {
        return name + '=' + value;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final FeatureDetail that = (FeatureDetail) o;
        if (!name.equals(that.name))
            return false;
        return !(value != null ? !value.equals(that.value) : that.value != null);
    }

    public int hashCode() {
        int result;
        result = name.hashCode();
        result = 29 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

/* support of defaults file is disabled

    private static final String DEFAULTS_FILENAME = "defaults.properties";

    private static final Log logger = LogFactory.getLog(FeatureDetail.class);

    private static Map<String, Object> defaults;

    static {
        defaults = new HashMap<String, Object>();
        readDefaults(DEFAULTS_FILENAME);
    }

    private static Object get(String detailName) {
        return defaults.get(detailName);
    }

    private static void readDefaults(String uri) {
        AttributeDescriptor template = new AttributeDescriptor(null);
        if (IOUtil.isURIAvailable(uri)) {
            try {
                Properties properties = IOUtil.readProperties(uri);
                for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                    String detailName = (String) entry.getKey();
                    logger.debug(detailName + "=" + entry.getValue());
                    Class detailType = template.getDetailType(detailName);
                    Object detailValue = AnyConverter.convert(entry.getValue(), detailType);
                    defaults.put(detailName, detailValue);
                }
            } catch (Exception e) {
                logger.error("Error in reading defaults file: " + uri, e);
            }
        } else
            logger.info("No defaults file exists: " + uri + ", using application defaults");
    }
*/
}
