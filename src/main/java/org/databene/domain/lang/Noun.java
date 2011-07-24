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

package org.databene.domain.lang;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.databene.commons.LocaleUtil;
import org.databene.commons.NullSafeComparator;
import org.databene.commons.StringUtil;
import org.databene.document.csv.CSVLineIterator;
import org.databene.webdecs.DataContainer;

/**
 * Represents a Noun.<br/>
 * <br/>
 * Created at 15.07.2009 22:46:33
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class Noun {
	
	private Language language;

	private String singular;
	private String plural;
	private int gender;
	
	public Noun(String singular, String plural, int gender, Language language) {
	    this.singular = singular;
	    this.plural = plural;
	    this.gender = gender;
	    this.language = language;
    }

	public String getSingular() {
    	return singular;
    }

	public String getPlural() {
    	return plural;
    }
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (singular != null) {
			builder.append(language.definiteArticle(gender, false)).append(' ').append(singular);
			if (plural != null)
				builder.append(", ");
		}
		if (plural != null)
			builder.append(language.definiteArticle(gender, true)).append(' ').append(plural);
		return builder.toString();
	}

	@Override
    public int hashCode() {
	    int pHash = (plural   == null ? 0 : plural.hashCode());
		int sHash = (singular == null ? 0 : singular.hashCode());
		return (pHash * 31 + sHash) * 31 + gender;
    }

	@Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    Noun that = (Noun) obj;
	    return NullSafeComparator.equals(this.singular, that.singular) && 
	    	NullSafeComparator.equals(this.plural, that.plural) && 
	    	this.gender == that.gender;
    }
	
	public static Collection<Noun> getInstances(Locale locale) throws IOException {
		Language language = Language.getInstance(locale);
		Set<Noun> nouns = new HashSet<Noun>(500);
		String url = LocaleUtil.availableLocaleUrl("/org/databene/domain/lang/noun", locale, ".csv");
		CSVLineIterator iterator = new CSVLineIterator(url, ',', true);
		DataContainer<String[]> container = new DataContainer<String[]>();
		while ((container = iterator.next(container)) != null) {
			String[] line = container.getData();
			String singular = (StringUtil.isEmpty(line[0]) ? null : line[0].trim());
			String plural;
			if (line.length > 1 && !StringUtil.isEmpty(line[1])) {
				plural = line[1].trim();
				if (plural.startsWith("-"))
					plural = singular + plural.substring(1);
			} else
				plural = null;
			int gender = (line.length >= 3 ? Integer.parseInt(line[2]) : 0);
			nouns.add(new Noun(singular, plural, gender, language));
		}
		return nouns;
	}
	
}
