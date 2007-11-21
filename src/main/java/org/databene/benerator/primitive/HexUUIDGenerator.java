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

package org.databene.benerator.primitive;

import org.databene.benerator.LightweightGenerator;
import org.databene.commons.NumberUtil;

import java.net.InetAddress;

/**
 * Creates UUIDs evaluating IP address, a JVM ID and timestamp.<br/>
 * <br/>
 * Created: 15.11.2007 10:52:55
 */
public class HexUUIDGenerator extends LightweightGenerator<String> {

    private static final String IP_ADDRESS;
    private static final String JVM_ID = NumberUtil.formatHex((int) (System.currentTimeMillis() >>> 8), 8);

    private static short counter = (short) 0;

    private String separator;
    private boolean dirty;

    // constructors ----------------------------------------------------------------------------------------------------

    static {
        int ipadd;
        try {
            ipadd = NumberUtil.toInt( InetAddress.getLocalHost().getAddress() );
        } catch (Exception e) {
            ipadd = 0;
        }
        IP_ADDRESS = NumberUtil.formatHex(ipadd, 8);
    }

    public HexUUIDGenerator() {
        this("");
    }

    public HexUUIDGenerator(String separator) {
        this.separator = separator;
        this.dirty = true;
    }

    // ptoperties ------------------------------------------------------------------------------------------------------

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
        this.dirty = true;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    public Class<String> getGeneratedType() {
        return String.class;
    }

    public void validate() {
        super.validate();
        this.dirty = false;
    }

    public String generate() {
        if (dirty)
            validate();
        long time = System.currentTimeMillis();
        short count;
        synchronized(HexUUIDGenerator.class) {
            if (counter < 0)
                counter = 0;
            count = counter++;
        }
        return new StringBuilder(36)
            .append(IP_ADDRESS).append(separator)
            .append(JVM_ID).append(separator)
            .append(NumberUtil.formatHex((short) (time >>> 32), 4)).append(separator)
            .append(NumberUtil.formatHex((int) time, 8)).append(separator)
            .append(NumberUtil.formatHex(count, 4))
            .toString();
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + "[ipAddress=" + IP_ADDRESS + ", jvmId=" + JVM_ID + ']';
    }
}
