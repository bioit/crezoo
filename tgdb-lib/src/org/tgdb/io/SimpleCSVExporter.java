/*
 * SimpleCSVExporter.java
 *
 * Created on October 6, 2005, 2:38 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.io;

/**
 *
 * @author heto
 */
public class SimpleCSVExporter {
    
    private String[][] data;
    private char delimiter;
    
    private int rows;
    private int cols;
    
    private boolean useTextDelimiter;
    
    /** Creates a new instance of SimpleCSVExporter */
    public SimpleCSVExporter(String[][] data, char delimiter) 
    {
        this.data = data;
        this.delimiter = delimiter;
        
        rows = data.length;
        cols = data[0].length;
        
        useTextDelimiter = false;
    }
    
    public void useTextDelimiter(boolean useDelim)
    {
        useTextDelimiter = useDelim;
    }
    
    public String getCSV()
    {
        String out = "";
        
        for (int row=0;row<rows;row++)
        {
            for (int col=0;col<cols;col++)
            {
                if (col!=0)
                    out += delimiter;
                
                if (useTextDelimiter)
                    out += "\""+data[row][col]+"\"";
                else
                    out += data[row][col];
            }
            out +="\n";
        }
        return out;
    }
    
    
}
