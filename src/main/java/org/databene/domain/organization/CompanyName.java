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

package org.databene.domain.organization;

/**
 * Assembles the parts of a company name, providing access to full name, short name and name parts 
 * like core name (which is the shortName), sector, location and legal form.<br/><br/>
 * Created: 10.10.2010 17:28:01
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class CompanyName {

	private String shortName;
	private String sector;
	private String location;
	private String legalForm;
	
    private String datasetName;
    
	public String getShortName() {
    	return shortName;
    }
	
	public void setShortName(String shortName) {
    	this.shortName = shortName;
    }
	
	public String getSector() {
    	return sector;
    }
	
	public void setSector(String sector) {
    	this.sector = sector;
    }
	
	public String getLocation() {
    	return location;
    }
	
	public void setLocation(String location) {
    	this.location = location;
    }
	
	public String getLegalForm() {
    	return legalForm;
    }
	
	public void setLegalForm(String legalForm) {
    	this.legalForm = legalForm;
    }
	
	public String getDatasetName() {
    	return datasetName;
    }
	
	public void setDatasetName(String datasetName) {
    	this.datasetName = datasetName;
    }

	public String getFullName() {
	    StringBuilder builder = new StringBuilder(shortName);
	    if (sector != null)
	    	builder.append(' ').append(sector);
	    if (location != null)
	    	builder.append(' ').append(location);
	    if (location != null)
	    	builder.append(' ').append(legalForm);
	    return builder.toString();
    }
	
	@Override
	public String toString() {
	    return getFullName();
	}

}
