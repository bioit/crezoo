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
public class ArxLoginForward extends ArxFrameException  {
    
    private String url;
    
    /** Creates a new instance of ArxLoginForward */
    public ArxLoginForward(String txt) {
        super(txt);
    }
    
    public ArxLoginForward(String txt, String url) {
        super(txt);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
