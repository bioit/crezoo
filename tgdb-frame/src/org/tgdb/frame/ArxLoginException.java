/*
 * ArxLoginForward.java
 *
 * Created on November 3, 2005, 10:49 AM
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
public class ArxLoginException extends ArxFrameException  {
    
    /** Creates a new instance of ArxLoginForward */
    public ArxLoginException(String txt) {
        super(txt);   
    }
    
    public ArxLoginException(String msg, Exception e) {
        super(msg);
        initCause(e);
    }
}
