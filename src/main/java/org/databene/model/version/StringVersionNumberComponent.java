/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

import java.util.HashMap;
import java.util.Map;

import org.databene.commons.comparator.IntComparator;

/**
 * {@link VersionNumberComponent} implementation for String-type number components.<br/>
 * <br/>
 * Created at 07.01.2009 19:07:55
 * @since 0.5.7
 * @author Volker Bergmann
 */

public class StringVersionNumberComponent extends VersionNumberComponent {
	
	static final String[] KEY_ORDER = {
		"alpha",
		"beta",
		"rc", "cr",
		"final", "ga",
		"sp"
	};
	
	static final Map<String, Integer> ordinals;
	
	static {
		ordinals = new HashMap<String, Integer>();
		for (int i = 0; i < KEY_ORDER.length; i++)
			ordinals.put(KEY_ORDER[i], i);
	}
	
	static final int FINAL_INDEX = ordinals.get("final");
	static final int SP_INDEX = ordinals.get("sp");
	
	private String key;
	
	public StringVersionNumberComponent(String key) {
		super();
		this.key = key;
	}

	public int compareTo(VersionNumberComponent that) {
		if (that == null)
			return -1;
		Integer thisIndexObject = ordinals.get(this.key.toLowerCase());
		int thisIndex = (thisIndexObject != null ? thisIndexObject.intValue() : FINAL_INDEX);
		if (that instanceof NumberVersionNumberComponent) {
			boolean number = ((NumberVersionNumberComponent) that).getNumber() == 0;
			if (number && thisIndex >= SP_INDEX)
				return 1;
			else if (number && thisIndex >= FINAL_INDEX)
				return 0;
			else
				return -1;
		}
		Integer thatIndexObject = ordinals.get((((StringVersionNumberComponent) that).key).toLowerCase());
		int thatIndex = (thatIndexObject != null ? thatIndexObject.intValue() : FINAL_INDEX);
		return IntComparator.compare(thisIndex, thatIndex);
	}
	
	@Override
	public String toString() {
		return key;
	}

	@Override
	public int hashCode() {
		return key.toLowerCase().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StringVersionNumberComponent that = (StringVersionNumberComponent) obj;
		return this.compareTo(that) == 0;
	}
	
}
