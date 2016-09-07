/**
 * 
 */
package org.mmx.xdtl.runtime;

import java.util.EventListener;


/**
 * @author vsi
 */
public interface ConnectionManagerListener extends EventListener {
    public abstract void connectionOpened(ConnectionManagerEvent event);
}
