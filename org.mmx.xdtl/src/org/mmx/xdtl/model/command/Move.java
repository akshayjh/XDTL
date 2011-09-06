/**
 * 
 */
package org.mmx.xdtl.model.command;


/**
 * @author vsi
 */
public class Move extends FileTransfer{
    
    public Move(String cmd, String source, String target, String overwrite) {
        super(cmd, source, target, overwrite);
    }
}
