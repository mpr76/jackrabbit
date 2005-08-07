/*
 * Copyright 2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.webdav.jcr.version;

import org.apache.log4j.Logger;
import org.apache.jackrabbit.webdav.version.*;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.jcr.ItemResourceConstants;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.jcr.DefaultItemCollection;
import org.apache.jackrabbit.webdav.*;

import javax.jcr.RepositoryException;
import javax.jcr.Item;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import java.util.ArrayList;

/**
 * <code>VersionHistoryItemCollection</code> represents a JCR version history.
 *
 * @see VersionHistory
 */
public class VersionHistoryItemCollection extends DefaultItemCollection
        implements VersionHistoryResource {

    private static Logger log = Logger.getLogger(VersionHistoryItemCollection.class);

    /**
     * Create a new <code>VersionHistoryItemCollection</code> resource.
     *
     * @param resourcePath
     * @param session
     * @param factory
     */
    public VersionHistoryItemCollection(DavResourceLocator resourcePath,
                                        DavSession session, DavResourceFactory factory,
                                        Item item) {
        super(resourcePath, session, factory, item);
        if (item == null || !(item instanceof VersionHistory)) {
            throw new IllegalArgumentException("VersionHistory item expected.");
        }
    }

    //----------------------------------------------< DavResource interface >---
    /**
     * @see org.apache.jackrabbit.webdav.DavResource#getSupportedMethods()
     */
    public String getSupportedMethods() {
        StringBuffer sb = new StringBuffer(ItemResourceConstants.METHODS);
        sb.append(", ").append(VersionHistoryResource.METHODS);
        return sb.toString();
    }

    /**
     * Removing a version resource is achieved by calling <code>removeVersion</code>
     * on the versionhistory item this version belongs to.
     *
     * @throws DavException if the version does not exist or if an error occurs
     * while deleting.
     * @see DavResource#removeMember(org.apache.jackrabbit.webdav.DavResource)
     */
    public void removeMember(DavResource member) throws DavException {
        if (exists()) {
            VersionHistory versionHistory = (VersionHistory) item;
            try {
                versionHistory.removeVersion(getResourceName(member.getHref(), true));
            } catch (RepositoryException e) {
                throw new JcrDavException(e);
            }
        } else {
            throw new DavException(DavServletResponse.SC_NOT_FOUND);
        }
    }
    //-----------------------------------< VersionHistoryResource interface >---
    /**
     * Return an array of {@link VersionResource}s representing all versions
     * present in the underlaying JCR version history.
     *
     * @return array of {@link VersionResource}s representing all versions
     * present in the underlaying JCR version history.
     * @throws DavException
     * @see org.apache.jackrabbit.webdav.version.VersionHistoryResource#getVersions()
     */
    public VersionResource[] getVersions() throws DavException {
        try {
            VersionIterator vIter = ((VersionHistory)item).getAllVersions();
            ArrayList l = new ArrayList();
            while (vIter.hasNext()) {
                DavResourceLocator versionLoc = getLocatorFromItem(vIter.nextVersion());
                DavResource vr = createResourceFromLocator(versionLoc);
                l.add(vr);
            }
            return (VersionResource[]) l.toArray(new VersionResource[l.size()]);
        } catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }
    
    //--------------------------------------------------------------------------
    /**
     * Fill the property set for this resource.
     */
    protected void initProperties() {
        super.initProperties();

        // change resourcetype defined by default item collection
        properties.add(new ResourceType(ResourceType.VERSION_HISTORY));
        
        // jcr specific property pointing to the node this history belongs to
        try {
            properties.add(new DefaultDavProperty(JCR_VERSIONABLEUUID, ((VersionHistory)item).getVersionableUUID()));
        } catch (RepositoryException e) {
            log.error(e.getMessage());
        }

        // required root-version property for version-history resource
        try {
            String rootVersionResourcePath = ((VersionHistory)item).getRootVersion().getPath();
            properties.add(new HrefProperty(VersionHistoryResource.ROOT_VERSION, getLocatorFromResourcePath(rootVersionResourcePath).getHref(true), true));
        } catch (RepositoryException e) {
            log.error(e.getMessage());
        }

        // required, protected version-set property for version-history resource
        try {
            VersionIterator vIter = ((VersionHistory)item).getAllVersions();
            addHrefProperty(VersionHistoryResource.VERSION_SET, vIter, true);
        } catch (RepositoryException e) {
            log.error(e.getMessage());
        }
    }
}