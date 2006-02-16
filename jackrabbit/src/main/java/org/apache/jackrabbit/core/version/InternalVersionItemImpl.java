/*
 * Copyright 2004-2005 The Apache Software Foundation or its licensors,
 *                     as applicable.
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
package org.apache.jackrabbit.core.version;

import org.apache.jackrabbit.core.NodeId;

/**
 * Implements a <code>InternalVersionItem</code>.
 */
abstract class InternalVersionItemImpl implements InternalVersionItem {

    /**
     * the version manager
     */
    protected final AbstractVersionManager vMgr;

    /**
     * Creates a new Internal version item impl
     *
     * @param vMgr
     */
    protected InternalVersionItemImpl(AbstractVersionManager vMgr) {
        this.vMgr = vMgr;
    }

    /**
     * Returns the persistent version manager for this item
     *
     * @return the version manager.
     */
    protected AbstractVersionManager getVersionManager() {
        return vMgr;
    }

    /**
     * Returns the id of this item
     *
     * @return the id of this item.
     */
    public abstract NodeId getId();

    /**
     * returns the parent version item or <code>null</code>.
     *
     * @return the parent version item or <code>null</code>.
     */
    public abstract InternalVersionItem getParent();

}
