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
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.databene.commons.ArrayBuilder;
import org.databene.commons.ConfigurationError;
import org.databene.commons.IOUtil;
import org.databene.commons.StringUtil;

/**
 * Creates and manages {@link Dataset}s.<br/><br/>
 * Created: 21.03.2008 13:46:54
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class DatasetFactory {
    
    protected static Map<String, Map<String, Dataset>> types = new HashMap<String, Map<String, Dataset>>();

    public static Dataset getDataset(String type, String name) {
        Map<String, Dataset> sets = types.get(type);
        if (sets == null)
            sets = parseDatasetTypeConfig(type);
        return getDataset(type, name, sets);
    }
    
    public static String[] getDataFiles(String filenamePattern, String datasetName, String nesting) {
        Dataset dataset = getDataset(nesting, datasetName);
        ArrayBuilder<String> builder = new ArrayBuilder<String>(String.class);
        if (dataset.getAtomicSubSets().size() == 0) {
            String filename = MessageFormat.format(filenamePattern, datasetName);
            if (IOUtil.isURIAvailable(filename))
                builder.append(filename);
            else
                throw new ConfigurationError("File not found: " + filename);
        } else
            for (Dataset atomicSet : dataset.getAtomicSubSets()) {
                String filename = MessageFormat.format(filenamePattern, atomicSet);
            if (IOUtil.isURIAvailable(filename))
                builder.append(filename);
        }
        return builder.toArray();
    }
    
    // private helpers -------------------------------------------------------------------------------------------------

    private static Dataset getDataset(String type, String name, Map<String, Dataset> sets) {
        Dataset dataset = sets.get(name);
        if (dataset == null) {
            dataset = new Dataset(type, name);
            sets.put(name, dataset);
        }
        return dataset;
    }

    private synchronized static Map<String, Dataset> parseDatasetTypeConfig(String nesting) {
        try {
            Map<String, Dataset> sets = new HashMap<String, Dataset>();
            Map<String, String> properties = IOUtil.readProperties(nesting + ".set.properties");
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                String name = entry.getKey();
                Dataset dataset = getDataset(nesting, name, sets);
                String[] subSetNames = StringUtil.tokenize(entry.getValue(), ',');
                for (String subSetName : subSetNames)
                    dataset.addSubSet(getDataset(nesting, subSetName, sets));
            }
            types.put(nesting, sets);
            return sets;
        } catch (IOException e) {
            throw new ConfigurationError("Setup for Dataset type failed: " + nesting, e);
        }
    }
}
