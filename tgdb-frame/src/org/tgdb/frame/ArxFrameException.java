/*
 * ArxFrameException.java
 *
 * Created on October 31, 2005, 1:17 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.frame;

/**
 *
 * @author heto
 */
public class ArxFrameException extends Exception {
    
    /** Creates a new instance of ArxFrameException */
    public ArxFrameException(String msg) {
        super(msg);
    }
    
    public ArxFrameException(String msg, Exception e) {
        super(msg);
        initCause(e);
    }
    
}
