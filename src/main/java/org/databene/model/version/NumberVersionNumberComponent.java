/*
 * (c) Copyright 2008-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.model.version;

import org.databene.commons.comparator.IntComparator;

/**
 * Number based {@link VersionNumberComponent}.<br/>
 * <br/>
 * Created at 22.12.2008 16:33:56
 * @since 0.5.7
 * @author Volker Bergmann
 */

public class NumberVersionNumberComponent extends VersionNumberComponent {
	
	private final String numberString;
	private final int number;

	public NumberVersionNumberComponent(String numberString) {
		this.numberString = numberString;
		this.number = Integer.parseInt(numberString);
	}

	public NumberVersionNumberComponent(int number) {
		this.numberString = String.valueOf(number);
		this.number = number;
	}

	public int compareTo(VersionNumberComponent that) {
		if (that == null)
			return IntComparator.compare(number, 0);
		if (!(that instanceof NumberVersionNumberComponent)) // numbers are more significant than markers like 'alpha'
			return 1;
		return IntComparator.compare(this.number, ((NumberVersionNumberComponent) that).number);
	}
	
	public int getNumber() {
		return number;
	}
	
	@Override
	public String toString() {
		return numberString;
	}

	@Override
	public int hashCode() {
		return number;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || this.getClass() != obj.getClass())
			return false;
		NumberVersionNumberComponent that = (NumberVersionNumberComponent) obj;
		return this.number == that.number;
	}
	
}
