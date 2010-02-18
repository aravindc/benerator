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

package org.databene.platform.db;

import java.io.IOException;
import java.util.Map;

import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.IOUtil;
import org.databene.platform.db.dialect.UnknownDialect;

/**
 * Manages {@link DatabaseDialect}s.<br/><br/>
 * Created: 18.02.2010 16:32:55
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DatabaseDialectManager {

    public static DatabaseDialect getDialectForProduct(String productName) { 
        String filename = "org/databene/platform/db/databene.db_dialect.properties";
        try {
            Map<String, String> mappings = IOUtil.readProperties(filename);
            for (Map.Entry<String, String> entry : mappings.entrySet())
                if (productName.toLowerCase().contains(entry.getKey())) {
                    return (DatabaseDialect) BeanUtil.newInstance(entry.getValue());
                }
            return new UnknownDialect(productName);
        } catch (IOException e) {
            throw new ConfigurationError("Database dialect mapping not found: " + filename, e);
        }
    }

}
