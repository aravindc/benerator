/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date-related implementation of the {@link VersionNumberComponent} interface,
 * which exhibits the same ordinal behavior as a snapshot version.<br/><br/>
 * Created: 12.02.2010 09:50:06
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DateVersionNumberComponent extends VersionNumberComponent {

	private final String dateString;
	private final Date date;

	public DateVersionNumberComponent(String dateString) throws ParseException {
		this.dateString = dateString;
		this.date = new SimpleDateFormat("yyyyMMdd").parse(dateString);
	}

	public int compareTo(VersionNumberComponent that) {
		if (that instanceof DateVersionNumberComponent)
			return this.date.compareTo(((DateVersionNumberComponent) that).date);
		else
			return StringVersionNumberComponent.SNAPSHOT.compareTo(that);
	}
	
	public Date getDate() {
		return date;
	}
	
	@Override
	public String toString() {
		return dateString;
	}

	@Override
	public int hashCode() {
		return date.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || this.getClass() != obj.getClass())
			return false;
		DateVersionNumberComponent that = (DateVersionNumberComponent) obj;
		return this.date.equals(that.date);
	}
	
}
