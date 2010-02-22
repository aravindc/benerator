/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Represents a language.<br/>
 * <br/>
 * Created at 16.07.2009 19:20:15
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class Language {
	
	// TODO extract constants to other package?
	public static final Locale AFRIKAANS = new Locale("af");
	public static final Locale ALBANIAN = new Locale("sq");
	public static final Locale ARABIC = new Locale("ar");
	public static final Locale BELARUSIAN = new Locale("be");
	public static final Locale BOSNIAN = new Locale("bs");
	public static final Locale BULGARIAN = new Locale("bg");
	public static final Locale CATALAN = new Locale("ca");
	public static final Locale CZECH = new Locale("cs");
	public static final Locale CHINESE = Locale.CHINESE;
	public static final Locale DANISH = new Locale("da");
	public static final Locale GERMAN = Locale.GERMAN;
	public static final Locale DUTCH = new Locale("nl");
	public static final Locale GREEK = new Locale("el");
	public static final Locale ENGLISH = Locale.ENGLISH;
	public static final Locale ESTONIAN = new Locale("et");
	public static final Locale FINISH = new Locale("fi");
	public static final Locale FRENCH = Locale.FRENCH;
	public static final Locale HEBREW = new Locale("he");
	public static final Locale HINDI = new Locale("hi");
	public static final Locale CROATIAN = new Locale("hr");
	public static final Locale HUNGARIAN = new Locale("hu");
	public static final Locale INDONESIAN = new Locale("id");
	public static final Locale ITALIAN = Locale.ITALIAN; // it
	public static final Locale JAPANESE = Locale.JAPANESE; // ja
	public static final Locale KOREAN = Locale.KOREAN; // ko
	public static final Locale LATVIAN = new Locale("lv");
	public static final Locale LITHUANIAN = new Locale("lt");
	public static final Locale NORWEGIAN = new Locale("no");
	public static final Locale POLISH = new Locale("pl");
	public static final Locale PORTUGUESE = new Locale("pt");
	public static final Locale ROMANIAN = new Locale("ro");
	public static final Locale RUSSIAN = new Locale("ru");
	public static final Locale SLOVENIAN = new Locale("sl");
	public static final Locale SPANISH = new Locale("es");
	public static final Locale SWEDISH = new Locale("sv");
	public static final Locale THAI = new Locale("th");
	public static final Locale TURKISH = new Locale("tr");
	public static final Locale UKRAINIAN = new Locale("uk");
	public static final Locale VIETNAMESE = new Locale("vi");
	

	
	private Locale locale;
	private LanguageResourceBundle bundle;

	public static Language getInstance(Locale locale) {
		return new Language(locale);
	}

	public Language(Locale locale) {
	    this.locale = locale;
	    this.bundle = (LanguageResourceBundle) ResourceBundle.getBundle(LanguageResourceBundle.class.getName(), locale);
    }
	
	public Locale getLocale() {
    	return locale;
    }

	public String definiteArticle(int gender, boolean plural) {
		return bundle.getString("definite.article." + (plural ? "plural." : "singular.") + gender);
	}
	
	public String indefiniteArticle(int gender, boolean plural) {
		return bundle.getString("indefinite.article." + (plural ? "plural." : "singular.") + gender);
	}
	
}
