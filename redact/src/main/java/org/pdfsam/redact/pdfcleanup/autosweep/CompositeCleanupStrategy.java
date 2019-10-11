/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package org.pdfsam.redact.pdfcleanup.autosweep;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ILocationExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IPdfTextLocation;

import java.util.*;


/**
 * This class is a composite pattern for {@code ICleanupStrategy}
 * It allows users to have multiple ICleanupStrategy implementations and bundle them as one.
 */
public class CompositeCleanupStrategy implements ICleanupStrategy {

    private Map<Integer, Set<IPdfTextLocation>> locations = new HashMap<>();
    private List<ICleanupStrategy> strategies = new ArrayList<>();

    public CompositeCleanupStrategy() {
    }

    public void add(ICleanupStrategy ies) {
        strategies.add(ies);
    }

    @Override
    public Collection<IPdfTextLocation> getResultantLocations() {
        locations.clear();

        // build return value
        Set<IPdfTextLocation> retval = new HashSet<>();
        for (int i = 0; i < strategies.size(); i++) {
            ILocationExtractionStrategy s = strategies.get(i);
            Collection<IPdfTextLocation> rects = s.getResultantLocations();
            retval.addAll(rects);
            locations.put(i, new HashSet<>(rects));
        }

        List<IPdfTextLocation> rectangles = new ArrayList<>(retval);
        java.util.Collections.sort(rectangles, new Comparator<IPdfTextLocation>() {
            @Override
            public int compare(IPdfTextLocation l1, IPdfTextLocation l2) {
                Rectangle r1 = l1.getRectangle();
                Rectangle r2 = l2.getRectangle();
                if (r1.getY() == r2.getY()) {
                    return r1.getX() == r2.getX() ? 0 : (r1.getX() < r2.getX() ? -1 : 1);
                } else {
                    return r1.getY() < r2.getY() ? -1 : 1;
                }
            }
        });

        // return
        return rectangles;
    }

    @Override
    public Color getRedactionColor(IPdfTextLocation location) {
        for (int i = 0; i < strategies.size(); i++) {
            if (locations.get(i).contains(location)) {
                return strategies.get(i).getRedactionColor(location);
            }
        }
        return ColorConstants.BLACK;
    }

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        for (ILocationExtractionStrategy s : strategies) {
            s.eventOccurred(data, type);
        }
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        Set<EventType> evts = new HashSet<>();
        for (ILocationExtractionStrategy s : strategies) {
            Set<EventType> se = s.getSupportedEvents();
            if (se != null)
                evts.addAll(se);
        }
        return evts.isEmpty() ? null : evts;
    }

    public ICleanupStrategy reset()
    {
        CompositeCleanupStrategy retval = new CompositeCleanupStrategy();
        for(ICleanupStrategy s : strategies)
        {
            retval.add(s.reset());
        }
        return retval;
    }
}
