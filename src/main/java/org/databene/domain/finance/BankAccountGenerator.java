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

package org.databene.domain.finance;

import org.databene.benerator.Generator;
import org.databene.benerator.primitive.regex.RegexStringGenerator;
import org.databene.benerator.util.LightweightGenerator;

/**
 * Generates {@link BankAccount}s.<br/><br/>
 * Created at 24.06.2008 08:36:32
 * @since 0.5.4
 * @author Volker Bergmann
 */
public class BankAccountGenerator extends LightweightGenerator<BankAccount> {
	
	// TODO v0.5.5 support Country/Region
	// TODO v0.5.5 support uniqueness
	// TODO v0.5.5 improve validity
	
	private Generator<Bank> bankGenerator = new BankGenerator();
	private Generator<String> accountNumberGenerator = new RegexStringGenerator("[0-9]{8}");

	public BankAccountGenerator() {
		super(BankAccount.class);
	}
	
	public BankAccount generate() {
		Bank bank = bankGenerator.generate();
		String accountNumber = accountNumberGenerator.generate();
		String iban = createIban(bank, accountNumber);
		return new BankAccount(bank, accountNumber, iban);
	}

	private String createIban(Bank bank, String accountNumber) {
		StringBuilder ibanBuilder = new StringBuilder("DE68000000000000000000");
		String bankCode = bank.getBankCode();
		ibanBuilder.replace(4, 4 + bankCode.length(), bankCode);
		ibanBuilder.replace(22 - accountNumber.length(), 22, bankCode);
		String iban = ibanBuilder.toString();
		return iban;
	}

}
