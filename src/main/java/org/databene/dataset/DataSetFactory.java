/*
 * (c) Copyright 2008 by Volker Bergmann. All rights reserved.
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

package org.databene.dataset;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.databene.commons.ArrayBuilder;
import org.databene.commons.ConfigurationError;
import org.databene.commons.IOUtil;
import org.databene.commons.StringUtil;

/**
 * Creates and manages {@link DataSet}s.<br/><br/>
 * Created: 21.03.2008 13:46:54
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class DataSetFactory {
    
    protected static Map<String, Map<String, DataSet>> types = new HashMap<String, Map<String, DataSet>>();

    public static DataSet getDataSet(String type, String name) {
        Map<String, DataSet> sets = types.get(type);
        if (sets == null)
            sets = parseDataSetTypeConfig(type);
        return getDataSet(type, name, sets);
    }
    
    public static String[] getDataFiles(String dataSetType, String dataSetName, String baseName, String suffix) {
        DataSet dataSet = getDataSet(dataSetType, dataSetName);
        ArrayBuilder<String> builder = new ArrayBuilder<String>(String.class);
        if (dataSet.getAtomicSubSets().size() == 0) {
            String filename = baseName + '_' + dataSetName + suffix;
            if (IOUtil.isURIAvailable(filename))
                builder.append(filename);
            else
                throw new ConfigurationError("File not found: " + filename);
        } else
            for (DataSet atomicSet : dataSet.getAtomicSubSets()) {
            String filename = baseName + '_' + atomicSet.getName() + suffix;
            if (IOUtil.isURIAvailable(filename))
                builder.append(filename);
        }
        return builder.toArray();
    }
    
    // private helpers -------------------------------------------------------------------------------------------------

    private static DataSet getDataSet(String type, String name, Map<String, DataSet> sets) {
        DataSet dataSet = sets.get(name);
        if (dataSet == null) {
            dataSet = new DataSet(type, name);
            sets.put(name, dataSet);
        }
        return dataSet;
    }

    private synchronized static Map<String, DataSet> parseDataSetTypeConfig(String type) {
        try {
            Map<String, DataSet> sets = new HashMap<String, DataSet>();
            Map<String, String> properties = IOUtil.readProperties(type + ".set.properties");
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                String name = entry.getKey();
                DataSet dataSet = getDataSet(type, name, sets);
                String[] subSetNames = StringUtil.tokenize(entry.getValue(), ',');
                for (String subSetName : subSetNames)
                    dataSet.addSubSet(getDataSet(type, subSetName, sets));
            }
            types.put(type, sets);
            return sets;
        } catch (IOException e) {
            throw new ConfigurationError("Setup for DataSet type failed: " + type, e);
        }
    }
}
