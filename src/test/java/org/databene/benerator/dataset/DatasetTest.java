/*
 * (c) Copyright 2008-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.dataset;

import java.util.List;

import org.databene.benerator.dataset.Dataset;
import org.databene.benerator.dataset.DatasetUtil;
import org.databene.commons.ArrayFormat;
import org.databene.commons.ArrayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link Dataset} features.<br/><br/>
 * Created: 21.03.2008 14:20:59
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class DatasetTest {
    
    private static Logger logger = LoggerFactory.getLogger(DatasetTest.class);
    
    private static final String REGION = "org/databene/dataset/region";
    public static final String TYPE = "test";
    
    @Test
    public void testAtomicSet() {
        Dataset set = DatasetUtil.getDataset(REGION, "DE");
        assertEquals("DE", set.getName());
    }
    
    @Test
    public void testNestedSet() {
        Dataset eu = DatasetUtil.getDataset(REGION, "europe");
        assertNotNull(eu);
        Dataset centralEurope = DatasetUtil.getDataset(REGION, "central_europe");
        assertTrue(eu.getSubSets().contains(centralEurope));
        List<Dataset> atomicSubSets = eu.allAtomicSubSets();
        assertTrue(atomicSubSets.contains(DatasetUtil.getDataset(REGION, "DE")));
        assertTrue(atomicSubSets.contains(DatasetUtil.getDataset(REGION, "AT")));
        String[] dataFiles = DatasetUtil.getDataFiles("org/databene/domain/person/familyName_{0}.csv", "europe", REGION);
        logger.debug(ArrayFormat.format(dataFiles));
        assertTrue(ArrayUtil.contains("org/databene/domain/person/familyName_DE.csv", dataFiles));
    }
    
}
