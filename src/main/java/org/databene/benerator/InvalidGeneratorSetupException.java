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

package org.databene.benerator;

import org.databene.commons.ConfigurationError;

import java.util.List;
import java.util.Arrays;

/**
 * Indicates invalid setup of a Generator.<br/>
 * <br/>
 * Created: 21.12.2006 08:04:49
 */
public class InvalidGeneratorSetupException extends ConfigurationError {

    private List<PropertyMessage> propertyMessages;

    // constructors ----------------------------------------------------------------------------------------------------

    public InvalidGeneratorSetupException(PropertyMessage propertyMessage) {
        this(new PropertyMessage[] { propertyMessage });
    }

    public InvalidGeneratorSetupException(PropertyMessage... propertyMessages) {
        this(propertyMessages, null, null);
    }

    public InvalidGeneratorSetupException(String propertyName, String propertyMessage) {
        this(new PropertyMessage(propertyName, propertyMessage));
    }

    public InvalidGeneratorSetupException(String textMessage) {
        this(textMessage, (Throwable)null);
    }

    public InvalidGeneratorSetupException(Throwable cause) {
        this(null, cause);
    }

    public InvalidGeneratorSetupException(String textMessage, Throwable cause) {
        this(new PropertyMessage[0], textMessage, cause);
    }

    public InvalidGeneratorSetupException(PropertyMessage[] propertyMessages, String textMessage, Throwable cause) {
        super(textMessage, cause);
        this.propertyMessages = Arrays.asList(propertyMessages);
    }

    // interface -------------------------------------------------------------------------------------------------------

    public PropertyMessage[] getPropertyMessages() {
        PropertyMessage[] array = new PropertyMessage[propertyMessages.size()];
        return propertyMessages.toArray(array);
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder(this.getClass().getName()).append(": ");
        String textMessage = getMessage();
        if (textMessage != null)
            buffer.append(textMessage);
        if (propertyMessages.size() > 0)
            buffer.append(": ");
        for (int i = 0; i < propertyMessages.size(); i++) {
            PropertyMessage propertyMessage = propertyMessages.get(i);
            buffer.append(propertyMessage);
            if (i < propertyMessages.size())
                buffer.append(", ");
        }
        return buffer.toString();
    }
}
