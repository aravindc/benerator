/*
 * (c) Copyright 2006-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.domain.person;

import java.util.Locale;

import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.csv.WeightedDatasetCSVGenerator;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.commons.Encodings;

/**
 * Generates a given name for a person.<br/>
 * <br/>
 * Created: 09.06.2006 21:13:09
 * @since 0.1
 * @author Volker Bergmann
 */
public class GivenNameGenerator extends WeightedDatasetCSVGenerator<String> implements NonNullGenerator<String> {

    public GivenNameGenerator() {
        this(Locale.getDefault().getCountry(), Gender.MALE);
    }

    public GivenNameGenerator(String datasetName, Gender gender) {
        this(datasetName, 
            "/org/databene/dataset/region", 
            "/org/databene/domain/person/givenName", 
            gender);
    }

    public GivenNameGenerator(String datasetName, String nesting, String baseName, Gender gender) {
        super(String.class, genderBaseName(baseName, gender) + "_{0}.csv", datasetName, nesting, Encodings.UTF_8);
    }

    private static String genderBaseName(String baseName, Gender gender) {
        if (gender == Gender.FEMALE)
            return baseName + "_female";
        else if (gender == Gender.MALE)
            return baseName + "_male";
        else
            throw new IllegalArgumentException("Gender: " + gender);
    }

	public String generate() {
		return GeneratorUtil.generateNonNull(this);
	}

}
