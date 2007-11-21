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

package org.databene.domain.address;

import org.databene.benerator.sample.WeightedCSVSampleGenerator;
import org.databene.region.Region;
import org.databene.region.RegionUtil;

import java.util.Map;
import java.util.HashMap;

/**
 * Generates a street name for a region.<br/>
 * <br/>
 * Created: 12.06.2006 00:08:28
 */
public class StreetNameGenerator extends WeightedCSVSampleGenerator<String> {

    private static final String BASE_NAME = "org/databene/domain/address/street";

    private static Map<Region, StreetNameGenerator> instances = new HashMap<Region, StreetNameGenerator>();

    private Region region;

    public StreetNameGenerator() {
        this(Region.getDefault());
    }

    public StreetNameGenerator(Region region) {
        super(RegionUtil.availableRegionUrl(BASE_NAME, region, ".csv"));
        assert(region != null);
        this.region = region;
        instances.put(region, this);
    }

    public static StreetNameGenerator getInstance(Region region) {
        StreetNameGenerator instance = instances.get(region);
        if (instance == null)
            instance = new StreetNameGenerator(region);
        return instance;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final StreetNameGenerator that = (StreetNameGenerator) o;
        return this.region.equals(that.region);
    }

    public int hashCode() {
        return region.hashCode();
    }

    public String toString() {
        return getClass().getSimpleName() + '[' + region + ']';
    }
}
