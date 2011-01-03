/*
 * (c) Copyright 2008-2009 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.ftl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.databene.commons.converter.LiteralParser;

import freemarker.template.SimpleDate;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;

/**
 * FreeMarker method that sums Date, Time and millisecond values.<br/>
 * <br/>
 * Created at 14.10.2008 17:39:28
 * @since 0.5.6
 * @author Volker Bergmann
 */
public class DateSumMethod implements TemplateMethodModel {
    
    @SuppressWarnings("rawtypes")
	public TemplateModel exec(List args) {
		long sum = 0;
		for (Object arg : args) {
			arg = LiteralParser.parse((String) arg);
			if (arg instanceof Date)
				sum += ((Date) arg).getTime();
			else if (arg instanceof Timestamp)
				sum += ((Timestamp) arg).getTime();
			else if (arg instanceof Number)
				sum += ((Number) arg).longValue();
			else if (arg != null)
				throw new IllegalArgumentException("Not a supported date type: " + arg.getClass());
		}
		return new SimpleDate(new java.sql.Date(sum));
	}
}
