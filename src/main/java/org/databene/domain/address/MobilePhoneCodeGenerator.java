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

import org.databene.benerator.sample.WeightedSampleGenerator;
import org.databene.benerator.Generator;
import org.databene.benerator.LightweightGenerator;
import org.databene.benerator.primitive.regex.RegexStringGenerator;

/**
 * Generates mobile phone numbers.<br/>
 * <br/>
 * Created: 14.10.2007 21:28:35
 */
public class MobilePhoneCodeGenerator extends LightweightGenerator<PhoneNumber> {

    private Country country;
    private WeightedSampleGenerator<String> mobilePreCodeGenerator;
    private Generator<String> mobileLocalCodeGenerator;

    public MobilePhoneCodeGenerator(Country country) {
        this.country = country;
        mobilePreCodeGenerator = new WeightedSampleGenerator<String>();
        for (String code : country.getMobileCodes())
            mobilePreCodeGenerator.addValue(code);
        this.mobileLocalCodeGenerator = new RegexStringGenerator("[1-9]\\d{6,7}");
    }

    public PhoneNumber generate() {
        String preCode = mobilePreCodeGenerator.generate();
        String localCode = mobileLocalCodeGenerator.generate();
        return new PhoneNumber(country.getPhoneCode(), preCode, localCode, true);
    }

    public Class<PhoneNumber> getGeneratedType() {
        return PhoneNumber.class;
    }

}
