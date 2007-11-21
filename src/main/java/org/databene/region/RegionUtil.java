/*
 * (c) Copyright 2006 by Volker Bergmann. All rights reserved.
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

package org.databene.region;

import org.databene.commons.IOUtil;

import java.util.Locale;

/**
 * Provides utility methods for Region handling.<br/>
 * <br/>
 * Created: 07.06.2007 08:52:59
 */
public class RegionUtil {

    public static String availableLocaleUrl(String baseName, Locale locale, String suffix) {
        String localeString = locale.toString();
        do {
            String url = baseName;
            if (localeString != null && localeString.length() > 0)
                url += "_" + localeString;
            url += suffix;
            if (IOUtil.isURIAvailable(url))
                return url;
            localeString = reduceLocaleString(localeString);
        } while (localeString != null);
        return null;
    }

    public static String availableRegionUrl(String baseName, Region region, String suffix) {
        String localeString = region.toString();
        do {
            String url = baseName;
            if (localeString != null && localeString.length() > 0)
                url += "_" + localeString;
            url += suffix;
            if (IOUtil.isURIAvailable(url))
                return url;
            localeString = reduceLocaleString(localeString);
        } while (localeString != null);
        return null;
    }

    private static String reduceLocaleString(String localeString) {
        if (localeString == null || localeString.length() == 0)
            return null;
        int separatorIndex = localeString.indexOf('_');
        if (separatorIndex < 0)
            return "";
        return localeString.substring(0, separatorIndex);
    }
}
