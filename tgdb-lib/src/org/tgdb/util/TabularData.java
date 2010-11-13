/*
 * TabularData.java
 *
 * Created on October 4, 2005, 9:17 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.util;

import java.util.HashMap;

/**
 *
 * @author heto
 */
public class TabularData {
    
    private String[][] data;
    
    private HashMap columnPositions;
    
    /** Creates a new instance of TabularData */
    public TabularData(String[][] data) {
        this.data = data;
        updateColumnPositions();
    }
    
    /**
     * Update the positions in a hashmap for fast lookup.
     */
    private void updateColumnPositions()
    {
        columnPositions = new HashMap();
        for (int i=0;i<data[0].length;i++)
        {
            String name = data[0][i];
            columnPositions.put(name, new Integer(i));
        }
    }
    
    /** 
     * Get the index of a column
     */
    public int indexOf(String columnName)
    {
        Integer pos = (Integer)columnPositions.get(columnName);
        
        if (pos == null)
            return -1;
        else
            return pos.intValue();
    }
    
    /** 
     * Get the value from a column name
     */
    public String getValue(String columnName, int row) throws ColumnNotFoundException
    {
        int index = indexOf(columnName);
        
        if (index==-1)
            throw new ColumnNotFoundException("Column \""+columnName+"\" was not found.");
        
        return data[row+1][index];
    }
    
    /** 
     * Get the value from a index
     */
    public String getValue(int column, int row) throws ColumnNotFoundException
    {
        if (column>getCols() || column<0)
            throw new ColumnNotFoundException("Column index \""+column+"\" was not found.");
        
        return data[row+1][column];
    }
    
    /**
     * Return the header row
     */
    public String[] getHeaderRow()
    {
        return data[0];
    }
    
    public int getRows()
    {
        return data.length-1;
    }
    
    public int getCols()
    {
        return data[0].length;
    }
    
    /** 
     * Return all row data as a string array.
     */
    public String[][] getData()
    {
        return data;
    }
}
