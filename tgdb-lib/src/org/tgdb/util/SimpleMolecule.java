/*
 * SimpleMolecule.java
 *
 * Created on September 21, 2005, 8:45 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.util;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple molecule class. A molecule is considered to have a name, an amount of raw data and possibly also some tagged information.
 * The raw data should be separated with [at] characters.
 * @author lami
 */
public class SimpleMolecule {
    private String name;
    private String rawData;
    private ArrayList tag;
    private ArrayList value;
    private boolean idDone;
    
    private String data;
    private String comment;
    
    public SimpleMolecule(String name, String data, String comment)
    {
        tag = new ArrayList();
        value = new ArrayList();
        this.name = name;
        this.data = data;
        this.comment = comment;
        boolean idDone = false;
    }
    
    /**
     * Creates a new instance of SimpleMolecule
     * @param name The name of the molecule
     */
    public SimpleMolecule(String name) {
        tag = new ArrayList();
        value = new ArrayList();
        this.name = name;
        boolean idDone = false;
    }
    
    /**
     * Adds raw (mostly coordinate or atom) data to the molecule. Data rows should be separated with [at] character-
     * @param rawData The raw data string (should be rowseparated!)
     */
    public void addRawData(String rawData) {
        this.rawData = rawData;
    }
    
    /**
     * Adds a preformatted tag to the molecule.
     * @param tagName The name of the tag
     * @param tagValue The value of the tag
     */
    public void addTag(String tagName, String tagValue) {
        String s = tagName.toUpperCase();
        if(s.startsWith(">  <ID") && !idDone) {
            name = tagValue;
            idDone = true;
        }
        tag.add(s);
        value.add(tagValue);
    }
    
    /**
     * Adds a (non preformatted) SD-tag to the molecule
     * @param tagName The name of the tag
     * @param tagValue The value of the tag
     */
    public void addSDTag(String tagName, String tagValue) {
        if(tagName == "ARX")
            if(value.contains(tagValue))
                return;
            
        tag.add(">  <"+tagName+">  ("+name+")");
        value.add(tagValue);
    }
    
    /**
     * Returns the name of the molecule
     * @return The name of the molecule
     */
    public String getName() { return name; }
    
    public String getData() { return data; }
    
    public String getComment() { return comment; }
    
    /**
     * Returns the raw data (usually coordinates and atom info) for the molecule. One row per cell in the string-array.
     * @return An array of raw data rows in string format.
     */
    public String[] getRawData() { return rawData.split("@"); }
    
    /**
     * Returns the choosen tags for the molecule.
     * @param choice The tags to retrieve
     * @return The choosen tags for the molecule
     */
    public ArrayList[] getTags(String[] choice) {
        ArrayList[] tmp = new ArrayList[2];
        ArrayList tmpTag = new ArrayList();
        ArrayList tmpValue = new ArrayList();
        String s = null;
        
        // Go through all the tags choosen to be included
        // Start with the choices since it is likely that
        // they are fewer than the available tags
        for(int i=0;i<choice.length;i++) {
            for(int j=0;j<tag.size();j++) {
                s = (String)tag.get(j);
                // Get the tag if it is one that we want
                if(s.equals(choice[i])) {
                    tmpTag.add(s);
                    tmpValue.add(value.get(j));
                }
            }
        }                
        
        tmp[0] = tmpTag;
        tmp[1] = tmpValue;
        
        return tmp;
    }
    
    /**
     * Returns all tags for the molecule
     * @return The tags for the molecule.
     * ArrayList[0] = tag names
     * ArrayList[1] = tag values
     */
    public ArrayList[] getTags() {
        ArrayList[] tmp = new ArrayList[2];
        tmp[0] = tag;
        tmp[1] = value;
        return tmp;
    }
    
    /**
     * Prints the molecule information.
     */
    public void getMolInfo() {
        System.out.println("----- START MOL INFO ----------------------");
        System.out.println("Molecule name: "+name);
        System.out.println("Number of tags: "+tag.size());
        System.out.println("   ***** START TAG INFO *******************");
        Iterator i = tag.iterator();
        Iterator j = value.iterator();
        while(i.hasNext()) {
            System.out.println("   * Name: "+(String)i.next()+", Value: "+(String)j.next());
        }
        System.out.println("   ***** END TAG INFO *********************");
        System.out.println("   ***** START RAW DATA *******************");
        System.out.println(rawData);
        System.out.println("   ***** END RAW DATA *********************");
        System.out.println("----- END MOL INFO ------------------------");
    }
}
