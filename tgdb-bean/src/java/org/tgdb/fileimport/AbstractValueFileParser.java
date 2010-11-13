package org.tgdb.fileimport;
import java.util.*;



/**
 * AbstractValueFileParser extends the AbstractFileParser. It implements an
 * extended version of the inherited <I>parseInputFile()</I> method which
 * loops the rows of the file and parses each row. The contents of the rows
 * are stored within the object.
 *
 * <P>The class expects to find a file with a table-like structure. Data in
 * the file should be stored in columns, separeted by one character (the
 * delimiter). While the first row of the file contains the header (handled
 * in AbstractFileParser) the second row should contain the column
 * titles. The second row is alway interpreted as the tile row, if it
 * contains any data, that data will be used as column titles. The rest of
 * the file is supposed to contain the date The first rows of a file might
 * look something like this:
 *
 * <P><PRE>
 *    Genotype/list/1/;
 *    IDENTITY;MARKER;ALLELE1;ALLELE2
 *    data1;data2;data3;data4
 *    data5;data6;data7;data8
 *  </PRE>
 *
 * <P>The first row is the header and the second row is the column
 * titles. This row is parsed and the titles are stored in the object. The
 * other rows are data rows.
 *
 * <P>The data in the file is stored in a matrix. The matrix will contain
 * as many rows as there are data rows found in the file. The number of
 * columns in the matrix will be the value returned from the method
 * columns(). By default this is the same number as there are column
 * titles. However, this way of calculating the number of columns might be
 * changed in any subclass by cverriding the method.
 *
 * <P>When the data rows are parsed, the number of createMatrix found on each row
 * is compared to the number of columns. If this is not the same value, the
 * parsing is aborted, as there are errors on the row (too many or too few
 * columns of data). Before the parsing is aborted, the class tries to fix
 * the error.
 *
 * <P>Subclasses may access the data by using the protected <I>createMatrix()</I>
 * method. In order to give other classes access to the data, a new method
 * has to be implemented in the subclasses.
 *
 * <P>The class implements a new version of the <I>isDataRow</I> method. The
 * method now tests if it is the second row that is checked. If it is, the
 * method will return false, as the second row of the file is the tile row.
 *
 * <P>
 * @author frob
 * @see AbstractFileParser
 */
public abstract class AbstractValueFileParser extends AbstractFileParser {
    
    /**
     * A matrix with all data createMatrix read from the input file
     */
    protected String[][] mValues;
    
    /**
     * The delimiter to split data
     */
    private String delimiter;
    
    /**
     * The string to comment out a row.
     */
    private String commentStr;
    
    
    
    //////////////////////////////////////////////////////////////////////
    //
    // Constructors
    //
    //////////////////////////////////////////////////////////////////////
    
    
    /**
     * Default constructor, should never be used
     *
     */
//    public AbstractValueFileParser() {
//        super();
//    }
    
    
    /**
     * Creates a new AbstractValueFileParser instance.
     *
     * @param fileName The file this object should read information from
     * @exception AssertionException If no filename is given
     */
    public AbstractValueFileParser(String fileName)
    throws AssertionException {
        super(fileName);
    }
    
    
    
    //////////////////////////////////////////////////////////////////////
    //
    // Public section
    //
    //////////////////////////////////////////////////////////////////////
    
    /**
     * Get the delimiter that is used in the file
     * @return a string of the delimiter
     */
    protected String getDelimiter() {
        return delimiter;
    }
    
    /**
     * Set the delimiter used to split file
     * @param delimiter The delimiter to split
     */
    protected void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Get the comment str used in file. Example: #, --, //
     * @return The comment string
     */
    protected String getCommentStr() {
        return commentStr;
    }

    /**
     * Set the comment str used in file. Example: #, --, //
     * @param commentStr the comment string
     */
    protected void setCommentStr(String commentStr) {
        this.commentStr = commentStr;
    }
    
    //////////////////////////////////////////////////////////////////////
    //
    // Protected section
    //
    //////////////////////////////////////////////////////////////////////
    
    
    
    
    /**
     * This method parses the file and builds a matrix of string objects
     * containg all the data in the file.
     *
     * @exception InputDataFileException If any errors when reading the file
     */
    protected void parseInputFile() throws InputDataFileException {
        
        // Run the inherited parse() method to read all rows of the
        // file. Also parses the header section
        super.parseInputFile();
        
        
        
        // Create the matrix used for storing the createMatrix read from the file
        initMatrix(super.getFileRows());
        
        // Variables used in the loop below
        String aRow;             // The current row from the data array
        int aCurrentDataRow = 0; // The row in value-matrix createMatrix should
        // be placed at
        
        // Loop all rows in the data array. Start on third row
        for (int aRowCount = 0; aRowCount < getFileRows(); aRowCount++) {
            // Get the row and check its size. If it's empty or a comment,
            // continue with next row
            aRow = getFileData(aRowCount);
            
            // Copy data from the row to the value matrix
            splitRow(aRow, aCurrentDataRow++);
        }
        
    }
    
    
    
    //////////////////////////////////////////////////////////////////////
    //
    // Private section
    //
    //////////////////////////////////////////////////////////////////////
    
    
    /**
     * Initialize the data structure with the right size.
     * @param rows the number of rows to use
     */
    private void initMatrix(int rows) {
        mValues = new String[rows][];
    }
    
    
    /**
     * Parses the given string to get the data. The data is inserted in the
     * value matrix on the given row.
     *
     * @param dataRow The row to get data from
     * @param rowIndex The row in the value matrix to insert data into
     */
    private void splitRow(String dataRow, int rowIndex) {
        // Create a tokenizer based on the row. Each token will represent one
        // column of data
//        StringTokenizer aTokenizer =
//                new StringTokenizer(dataRow, delimiter, false);
        
        String[] tmp = dataRow.split(delimiter, -1);
        
        mValues[rowIndex] = tmp;
        
        
//        int cols = aTokenizer.countTokens();
//        mValues[rowIndex] = new String[cols];
//        
//        // For each column on the row, add the contents of the column to the
//        // correct column on the given row in the value matrix
//        for (int aColumnIndex = 0; aColumnIndex < cols &&
//                aTokenizer.hasMoreTokens(); aColumnIndex++) 
//        {
//            String str = aTokenizer.nextToken();
//            mValues[rowIndex][aColumnIndex] = str;
//        }
    }
}
