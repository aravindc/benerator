/*
 * (c) Copyright 2006 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.csv;

import org.databene.benerator.sample.WeightedCSVSampleGenerator;
import org.databene.commons.Converter;
import org.databene.commons.converter.NoOpConverter;
import org.databene.region.Region;
import org.databene.region.RegionUtil;

/**
 * Generates data from a regionalized csv file.
 * For different regions, different CSV versions may be provided by appending region suffixes,
 * similar to the JDK ResourceBundle handling.<br/>
 * <br/>
 * Created: 07.06.2007 17:14:04
 * @author Volker Bergmann
 */
public class RegionalCSVGenerator<E> extends WeightedCSVSampleGenerator<E> {

    private String baseName;
    private Region region;
    private String suffix;

    // constructors ----------------------------------------------------------------------------------------------------

    public RegionalCSVGenerator() {
        this(null, null);
    }

    public RegionalCSVGenerator(String baseName, String suffix) {
        this(baseName, Region.getDefault(), suffix);
    }

    public RegionalCSVGenerator(String baseName, Region region, String suffix) {
        this(baseName, region, suffix, NoOpConverter.getInstance());
    }

    public RegionalCSVGenerator(String baseName, Region region, String suffix, Converter<String, E> converter) {
        super(url(baseName, region, suffix), converter);
        this.baseName = baseName;
        this.region = region;
        this.suffix = suffix;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        super.setUrl(url(baseName, region, suffix));
        this.region = region;
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + '[' + baseName + ',' + region + ',' + suffix + ']';
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private static String url(String baseName, Region region, String suffix) {
        return RegionUtil.availableRegionUrl(baseName, region, suffix); // TODO v0.5 consider nested sets
    }

}
