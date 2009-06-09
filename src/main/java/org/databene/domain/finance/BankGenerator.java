/*
 * (c) Copyright 2008-2009 by Volker Bergmann. All rights reserved.
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

package org.databene.domain.finance;

import org.databene.benerator.Generator;
import org.databene.benerator.primitive.regex.RegexStringGenerator;
import org.databene.benerator.util.LightweightGenerator;

/**
 * Generates {@link BankAccount}s.<br/><br/>
 * Created at 23.06.2008 11:08:48
 * @since 0.5.4
 * @author Volker Bergmann
 */
public class BankGenerator extends LightweightGenerator<Bank> {
	
	// TODO v0.6 support Country/Region
	// TODO v0.6 support uniqueness
	// TODO v0.6 improve validity
	
	private Generator<String> bankCodeGenerator;
	private Generator<String> nameGenerator;
	private Generator<String> bicGenerator;

	public BankGenerator() {
		this.bankCodeGenerator = new RegexStringGenerator("[0-9]{8}");
		this.nameGenerator = new RegexStringGenerator("(Deutsche Bank|Dresdner Bank|Commerzbank|Spardabank|HVB)");
		this.bicGenerator = new RegexStringGenerator("[A-Z]{4}DE[A-Z0-9]{2}");
	}
	
    public Class<Bank> getGeneratedType() {
	    return Bank.class;
    }

	public Bank generate() {
		return new Bank(nameGenerator.generate(), bankCodeGenerator.generate(), bicGenerator.generate());
	}

}
