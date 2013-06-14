/*
 * Created on 13/giu/2013
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; 
 * either version 2 of the License.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 
 *  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.pdfsam.pdf;

import java.util.List;

/**
 * Service to load pdf documents
 * 
 * @author Andrea Vacondio
 * 
 */
public interface PdfLoadService {

    /**
     * @param toLoad
     *            a list of {@link PdfDocumentDescriptor} the service is requested to load
     * @return a list of {@link PdfDocumentDescriptor} the service has populated
     */
    List<PdfDocumentDescriptor> load(List<PdfDocumentDescriptor> toLoad);
}
