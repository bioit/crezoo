/*
 * MugenFileParser.java
 *
 * Created on October 17, 2005, 5:07 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.fileimport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author heto
 */
public abstract class MugenFileParser extends AbstractValueFileParser {
    
    /** the file header object contains info about the file. */
    private FileHeader hdr;
    
    /** The structure to keep track of index of positions. */
    private HashMap columnPositions;
    
    /** 
     * Handle the rows that supports data. No comments in this. An index over data.
     */
    protected int[] dataRows;
    
    /**
     * Creates a new instance of MugenFileParser
     * @param fileName the name of the file to import
     * @throws org.tgdb.fileimport.AssertionException 
     * @throws org.tgdb.fileimport.InputDataFileException 
     */
    public MugenFileParser(String fileName) throws AssertionException, InputDataFileException
    {
        super(fileName);
        
    }
    
    /**
     * Read the first rows of the file and get the header information.
     * This must be done early to detect delimiter used. This information
     * is stored in the file header.
     * @throws org.tgdb.fileimport.InputDataFileException is thrown if file was not readable.
     */
    private void readHeader() throws InputDataFileException
    {
        File inputFile = null;
        FileInputStream inputStream = null;
        try {
            inputFile = new File(getFileName());
            //inputStream = new FileInputStream(inputFile);
            
            
            FileReader fr = new FileReader(inputFile);
            BufferedReader reader = new BufferedReader(fr);
            
            String line = reader.readLine();
            
            hdr = new FileHeader(line);
            
            
        } catch (Exception e) {
            InputDataFileException e2 = new InputDataFileException("Failed to read file");
            e2.initCause(e);
            throw e2;
        }
    }
    
    /**
     * Parse the data file and insert values to object.
     * @throws org.tgdb.fileimport.InputDataFileException if parsing fails
     */
    public void Parse() throws InputDataFileException
    {
        try
        {
            // Read header info.
            readHeader();
            
            // Get and set the delimiter used in file
            String tmp = hdr.delimiter().toString();
            setDelimiter(tmp);
            
            // Parse data from input file
            parseInputFile();
            
            // initialize dependent data
            init();
        }
        catch (Exception e)
        {
            InputDataFileException i = new InputDataFileException();
            i.initCause(e);
            throw i;
        }
    }
    
//    public MugenFileParser() throws AssertionException
//    {
//        super();
//        init();
//    }
    
    /**
     * Initialize dependent data
     */
    private void init()
    {
        hdr = null;
        setCommentStr("#");
        updateColumnPositions();
        updateDataRowsIndex();
    }
    
    /**
     * Test the data file
     */
    public void test()
    {
        
    }
    
    /**
     * Update the positions in a hashmap for fast lookup.
     */
    private void updateColumnPositions()
    {
        columnPositions = new HashMap();
        for (int i=0;i<mValues[1].length;i++)
        {
            String name = mValues[1][i];
            columnPositions.put(name, new Integer(i));
        }
    }
    
    /** 
     * Update the index keeping track of the actual data rows in file. 
     * The comments are not included.
     */
    protected void updateDataRowsIndex()
    {
        ArrayList arr = new ArrayList();
        for (int i=2;i<getFileRows();i++)
        {
            if (isDataRow(mValues[i]))
                arr.add(new Integer(i));
        }
        
        // Initialize the new array
        dataRows = new int[arr.size()];
        for (int i=0;i<arr.size();i++)
        {
            dataRows[i] = ((Integer)arr.get(i)).intValue();
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
    
    
    
    /**
     * checks if the row is a data row. This means that the row is not commented out.
     * @param row the string array for the row.
     * @return a boolean, true if it is a data row, otherwise false
     */
    protected boolean isDataRow(String[] row)
    {
        boolean isData = true;
        
        if (row.length>0 && row[0].startsWith(getCommentStr()))
            isData = false;
        
        return isData;
    }
    
    /**
     * Get the header object for the file.
     * @return FileHeader object.
     */
    public FileHeader getHeader()
    {
        return hdr;
    }
    
    /**
     * Get the number of columns in file
     * @return an int of the size
     */
    protected int getNumberOfColumns()
    {
        return columnPositions.size();
    }
    
    /**
     * Get the title for a column number
     * @param col the column number
     * @return a string of the column name.
     */
    public String getColumnTitle(int col)
    {
        return mValues[1][col];
    }
    
    /**
     * Get all column titles
     * @return a string array of column names.
     */
    public String[] getColumnTitles()
    {
        return mValues[1];
    }
    
    /**
     * Get the number of data rows available. It is not the same as file rows.
     * @see getFileRows()
     * @return the number of data rows.
     */
    public int getNumberOfDataRows()
    {
        return dataRows.length;
    }
}
