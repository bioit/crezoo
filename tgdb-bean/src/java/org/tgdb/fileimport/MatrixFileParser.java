

package org.tgdb.fileimport;



/**
 * MatrixFileParser extends the MugenFileParser with methods for
 * getting data from the object and for testing the object. The class also
 * implements it own version of the method that returns the number of
 * columns of data in the file.
 *
 * <P>The class expects a file with the following structure:
 * <P><PRE>
 *    otn/ftn/1/;
 *    Title;Column1;Column2;Column3
 *    Object1;data_1a;data_1b;data_2a;data_2b;data_3a;data_3b
 *    Object2;data_4a;data_4b;data_5a;data_5b;data_6a;data_6b
 * </PRE>
 *
 * <P>The first row is the header row (explained in the superclass). The
 * second row is the column title row. This row may contain any text, but
 * is always interpreted as the column title row. The first item on
 * the row is the name of the objects in the matrix. The rest of the items
 * are names of the actual data columns. This means that the second row
 * may contain one title column and any number of data columns.
 *
 * <P>The third and fourth row are data rows. Each row in the file
 * represents one object, and the name of the object is stored in the first
 * column on the row. The rest of the columns are data columns. As the file
 * represent a matrix, there should be twice as many data columns for each
 * object as there are data columns in the header. If there are three data
 * columns in the header, each row should contain six data rows.
 *
 * <P>
 * @see AbstractValueFileParser
 */
public class MatrixFileParser extends MugenFileParser {
    
    
    //////////////////////////////////////////////////////////////////////
    //
    // Constructors
    //
    //////////////////////////////////////////////////////////////////////
    
    
    /**
     * Creates a new MatrixFileParser instance.
     * @param fileName The name of the file this object reads data from
     * @throws org.tgdb.fileimport.InputDataFileException is thrown if input file reading fails
     * @exception AssertionException If inherited constructor could not be run
     */
    public MatrixFileParser(String fileName)
    throws AssertionException, InputDataFileException {
        super(fileName);
    }
    
    
    //////////////////////////////////////////////////////////////////////
    //
    // Public section
    //
    //////////////////////////////////////////////////////////////////////
    
    /**
     * Get the value in the matrix for a column name. Each column has two values
     * @param columnName the name of the column in file
     * @param row the row in data file. Excluding comments!
     * @return an array of two strings
     */
    public String[] getMatrixValue(String columnName, int row) {
        // Get the column index of the given column title
        int index = indexOf(columnName)-1;
        
        return getMatrixValue(index, row);
    }
    
    /**
     * Get the value in the matrix for a column number. Each column has two values
     * @param col the column number
     * @param row the row number
     * @return an array of strings
     */
    public String[] getMatrixValue(int col, int row) {
        // Get the right data row excluding comments
        int index = dataRows[row];
        
        // Get the right column number
        int colnr = col*2+1;
        
        String str1 = mValues[index][colnr];
        String str2 = mValues[index][colnr+1];
        
        String[] tmp = new String[2];
        tmp[0] = str1;
        tmp[1] = str2;
        
        return tmp;
    }
    
    /**
     * Get the value from the identity column. 
     * @param row the row number
     * @return a single String value
     */
    public String getIdValue(int row) {
        // Get the right data row excluding comments
        int index = dataRows[row];
        
        return mValues[index][0];
    }
    
    
    
    /**
     * Get the number of matrix columns. The id column is not included!
     * @return an int of the matrix columns
     */
    public int getNumberOfMatrixColumns() {
        return super.getNumberOfColumns()-1;
    }
    
    /**
     * Get the number of total columns in this data file
     * @return an int of the number of columns
     */
    public int getNumberOfColumns() {
        return super.getNumberOfColumns();
    }
    
    /**
     * Get the column title name
     * @param col the column number
     * @return a String value of the name
     */
    public String getColumnTitle(int col)
    {
        return mValues[1][col+1];
    }
    
    /**
     * Get the column title on the id column (first in file, single value column)
     * @return a Title name of the id column
     */
    public String getIdTitle()
    {
        return mValues[1][0];
    }
    
    
    /**
     * Tests the MatrixFileParser object by printint all its information
     *
     */
    public void test() {
        System.out.println("==================================================");
        System.out.println("Testing the MatrixFileParser using " + getFileName());
        System.out.println("--------------------------------------------------");
        
        String aColName;
        
        System.out.println("Columns="+getNumberOfColumns());
        System.out.println("MatrixColumns="+getNumberOfMatrixColumns());
        System.out.println("Rows="+getNumberOfDataRows());
        
        String idTitle = getIdTitle();
        
        // Loop all data rows
        for (int row = 0; row < getNumberOfDataRows() ; row++) {
            
            String idValue = getIdValue(row);
            
            System.out.println(idTitle+"="+idValue);
            
            for (int col = 0;col<getNumberOfMatrixColumns();col++)
            {
                String colTitle = getColumnTitle(col);
                
                String[] vals = getMatrixValue(colTitle, row);
                
                if (col!=0)
                    System.out.print("\t");
                System.out.print(vals[0]+";"+vals[1]);
            }
            System.out.println();
        }
        System.out.println("==================================================");
    }
    
    
    //////////////////////////////////////////////////////////////////////
    //
    // Protected section
    //
    //////////////////////////////////////////////////////////////////////
    
    
    
}
