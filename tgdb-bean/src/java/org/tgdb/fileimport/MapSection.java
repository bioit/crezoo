/*
 * MapSection.java
 *
 * Created on October 19, 2005, 1:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.fileimport;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author heto
 */
public class MapSection {
    
    private String values[][];
    
    /** The structure to keep track of index of positions. */
    private HashMap columnPositions;
    
    /** Creates a new instance of MapSection */
    public MapSection(ArrayList arr) 
    {
        String[] hdr = (String[])arr.get(0);
        
        // Init the array
        values = new String[arr.size()][];
        
        for (int i=0;i<arr.size();i++)
        {
            String[] tmp = (String[])arr.get(i);
            values[i] = tmp;
        }
        
        updateColumnPositions();
    }
    
    /**
     * Update the positions in a hashmap for fast lookup.
     */
    private void updateColumnPositions()
    {
        columnPositions = new HashMap();
        for (int i=0;i<values[0].length;i++)
        {
            String name = values[0][i];
            columnPositions.put(name, new Integer(i));
        }
    }
    
    /**
     * Get the index of a column
     * @param columnName the name of the column
     * @return an int of the index.
     */
    public int indexOf(String columnName)
    {
        Integer pos = (Integer)columnPositions.get(columnName);
        
        if (pos == null)
            return -1;
        else
            return pos.intValue();
    }
    
    public String getColumnTitle(int col)
    {
        return values[0][col];
    }
    
    public String getValue(int col, int row)
    {
        return values[row+1][col];
    }
    
    public String getValue(String columnName, int row)
    {
        int col = indexOf(columnName);
        return getValue(col, row);
    }
    
    public int getNumberOfColumns()
    {
        return values[0].length;
    }
    
    public int getNumberOfRow()
    {
        return values.length-1;
    }
    
    public String toString()
    {
        String out = "";
        for (int i=0;i<getNumberOfColumns();i++)
        {
            if (i!=0)
                out+=",";
            out+=getColumnTitle(i);
        }
        out+="\n";

        for (int j=0;j<getNumberOfRow();j++)
        {
            for (int i=0;i<getNumberOfColumns();i++)
            {
                if (i!=0)
                    out+=",";
                out+=getValue(i, j);
            }
            out+="\n";
        }        
        return out;
    }
}
