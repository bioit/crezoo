/*
 * Keyword.java
 *
 * Created on January 24, 2006, 12:41 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.search;

/**
 *
 * @author heto
 */
public class Keyword {
    
    private String word;
    
    /** Creates a new instance of Keyword */
    public Keyword(String word) 
    {
        this.word = word.toLowerCase();
    }
    
    public String getKeyword()
    {
        return word;
    }
}
