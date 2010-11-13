package org.tgdb.fileimport;
import java.io.*;
import java.util.*;

/**
 * AbstractFileParser parses a file and stores its contents in a string
 * array. The constructor takes a file name which is used to read input
 * data. When the <I>Parse()</I> method is called, the actual parsing takes
 * place. However, before the file is parsed, the the parser validates that
 * the file has valid contents. This is done by comparing the object- and
 * format type name of the file with the file type definitions passed to
 * the <I>Parse()</I> method. If the names found in the file matches at
 * least one of the file type definitions in the passed vector, parsing
 * will take place. If no matching definition is found, parsing will be
 * cancelled.
 * <P>
 * During the parsing process, all  rows of the file are read into a string
 * array. Each row of the file is represented by one string. As the file is
 * parsed, the number of actual data rows within the file are counted. A
 * data row is a row which contain real data (blank lines, comments,
 * headers etc are not counted). In order to verify if a row is a data row
 * or not, the method <I>isDataRow</I> is used. The method does some basic
 * verification of the row and tries to find out if it is a data
 * row. Additional checks of the row might be added by overriding the
 * method in subclasses. The AbstractFileParser object also knows the total
 * number of rows in the file.
 *
 * <P> The header of the file is also parsed and the header data are stored
 * within the object. This makes it possible to find out the object type,
 * the format type name, the format version and the delimiter used in the
 * input file. The parser expects a header with the following layout
 *
 * <P><CODE>
 *    objecttype name/formattype name/version/delimiter<BR>
 * </CODE>
 *
 * <P>Objecttype name: The objecttype that is stored in the file (string)<BR>
 * Formattype: The formattype that the data is structured in (string)<BR>
 * Version: The version of the format for the objecttype (int> <BR>
 * Delimiter: The character used to separate field in the file (char)<BR>
 *
 * <P>The input file is allowed to have comment rows. A comment row begin
 * with the character defined in the private member mCommentChar.
 *
 * <P>When using the data read from the file, one has to be able to
 * determine the index of the file row that the data was read
 * from. Whithout this feature, it is impossible to report errors in the
 * data in a correct way. To support this, the class implements a mapping
 * table which maps data row numbers to file row numbers. If one encounter
 * an error on a certain data row, one can call the <I>dataRow2FileRow</I>
 * method with a data row number as parameter. The method returns the file
 * row number that the data row is located at.
 *
 * <P>Finally, the class includes an abstract method called test() which
 * can be used to test the parser. For example, the method can be
 * implemented to print all the contents of the parser in order to verify
 * it has read data correctly.
 *
 * <P>
 * @author frob
 */
public abstract class AbstractFileParser {
    /**
     * The name of the file this object reads data from
     */
    private String mFileName;
    
    
    /**
     * The contents of the file represented as a string array. Each line of
     * the file represents one row in the array.
     */
    private String[] mFileData;
    
    //////////////////////////////////////////////////////////////////////
    //
    // Constructors
    //
    //////////////////////////////////////////////////////////////////////
    
    
    
    
    
    /**
     * Creates a new AbstractFileParser instance.
     * @param fileName the file name to open for parsing
     * @exception AssertionException If no filename is given
     */
    public AbstractFileParser(String fileName)
    throws AssertionException {
        setFileName(fileName);
    }
    
    
    //////////////////////////////////////////////////////////////////////
    //
    // Public section
    //
    //////////////////////////////////////////////////////////////////////
    
    
    /**
     * Tests the object
     *
     * @exception FileParserException If error when accessing FileParser
     * object
     */
    public abstract void test()
    throws FileParserException;
    
    
    /**
     * Parses the input file linked to this object. Before the actual
     * parsing takes place, the type of the linked file is validated. The
     * file must have a object- and format type name that matches one of the
     * file type definitions in the given vector.
     *
     * @exception InputDataFileException If anything wrong with the input
     *            file.
     * @exception FileParserException If no file type definitions are passed.
     */
    public void Parse()
    throws InputDataFileException, FileParserException {
        // Now parse the file
        parseInputFile();
    }
    
    
    
    
    //////////////////////////////////////////////////////////////////////
    //
    // Protected section
    //
    //////////////////////////////////////////////////////////////////////
    
    
    /**
     * This method parses the file and builds an array of string objects
     * containg all the data in the file.
     * @throws org.tgdb.fileimport.InputDataFileException If file parsing fails
     */
    protected void parseInputFile()
    throws InputDataFileException {
        try {
            // Read the inputfile into the string array
            readInputFile();
            
        
        } catch (Exception e) {
            throw new InputDataFileException(e.getMessage());
        }
    }
    
    
    
   
    
    
    /**
     * Returns the name of the file this object reads data from
     *
     * @return The name of the file
     */
    protected String getFileName() {
        return mFileName;
    }
    
    
   
    
    
    /**
     * Returns the total number of rows in the file
     *
     * @return Number of rows in the file
     */
    protected int getFileRows() {
        return mFileData.length;
    }
    
    /**
     * Returns the file data row
     * @param row the row number
     * @return a string of the row.
     */
    protected String getFileData(int row)
    {
        return mFileData[row];
    }
    
    //////////////////////////////////////////////////////////////////////
    //
    // Private section
    //
    //////////////////////////////////////////////////////////////////////
    
    
    
    /**
     * Sets the name of the file this object reads data from
     * @param fileName the file name
     * @throws org.tgdb.fileimport.AssertionException If something fails.
     */
    private void setFileName(String fileName)
    throws AssertionException {
        Assertion.assertMsg(fileName != null && fileName.length() > 0,
                "No filename given, could not create the file parser");
        mFileName = fileName;
    }
    
    
   
    
    
    
    
    
    
    
    
    /**
     * If the last character on the given row in a CR, a string where the
     * CR is removed is returned . If there is no CR at the end, the
     * original string is returned
     *
     * @param fileRow A string from which a trailing CR should be removed.
     * @return The string with the trailing CR is removed.
     */
    private String removeCR(String fileRow) {
        if (fileRow.charAt(fileRow.length() -1) == '\r') {
            return fileRow.substring(0,fileRow.length() - 1);
        } else {
            return fileRow;
        }
    }
    
    /**
     * Reads from file!
     *
     * Reads the input file and stores each line in the file as a string in
     * the file array. Each line added to the array is 'cleaned' from LF/CR
     * characters. The method also counts the number of data rows found in
     * the file.
     *
     * @exception InputDataFileException if an error occurs
     */
    private void readInputFile()
    throws InputDataFileException {
        File inputFile = null;
        FileInputStream inputStream = null;
        try {
            inputFile = new File(getFileName());
            inputStream = new FileInputStream(inputFile);
        } catch (Exception e) {
            InputDataFileException e2 = new InputDataFileException("Failed to read file");
            e2.initCause(e);
            throw e2;
        }
        readInputFile(inputStream,(int) inputFile.length());
    }
    
    
    /**
     * Read from stream!
     * 
     * Reads the input stream and stores each line in the "file" as a string in
     * the file array. Each line added to the array is 'cleaned' from LF/CR
     * characters. The method also counts the number of data rows found in
     * the file.
     * @param inputStream The stream to read from
     * @param length The number of bytes to read
     * @exception InputDataFileException if an error occurs
     */
    private void readInputFile(FileInputStream inputStream, int length)
    throws InputDataFileException {
        try {
            // Create a byte array with the same size as the file and read the
            // file into the array. If the size of the file is 0, an exception
            // will be raise.
            byte[] inputAsByte = new byte[length]; // (int) inputFile.length()
            Assertion.assertMsg(inputAsByte.length > 0, "Given input file (" +
                    getFileName() + ") is empty");
            inputStream.read(inputAsByte);
            
            // Build a string from the byte array and convert it to Dos format
            String inputAsStr = new String(inputAsByte);
            inputAsStr = convertToDOS(inputAsStr);
            
            // The contents of the file is now stored in inputAsStr. The
            // input should now be split into rows and each row should be
            // added to the file array (mFileData). To do this, we use a
            // tokenizer. Each token will represent one row in the file. A
            // row in the file ends with two chars, CR (13, \r) and LF (10,
            // \n). However, we can't use that pattern as separator in the
            // tokenizer, as this will remove any empty lines (which only
            // contains CR and LF). Hence we use just the LF as the separator
            // in the tokenizer. The CR remains on the line and is removed
            // before the row is added to the file array
            StringTokenizer tokenizer =
                    new StringTokenizer(inputAsStr, "\n", false);
            
            // Build the file array with the same size as there are tokens (eg
            // rows in the file). Also build the dataRow2FileArray with the
            // same size as there are rows.
            setFileData(new String[tokenizer.countTokens()]);

            
            // Step through the tokens (eg all rows in the file) and add them
            // to the file array. Before each row is added, the CR at the end
            // of each row is removed. Also count the number of data rows, eg
            // rows that actually contains data. Header-, column-, comment-
            // and blanklines are not counted
            int rowIndex = 0;
            String currentRow;
            while (tokenizer.hasMoreElements()) {
                // Get the row, remove the CR and add it to the file array
                currentRow = removeCR(tokenizer.nextToken());
                mFileData[rowIndex] = currentRow;
                
                rowIndex++;
            }

        } catch (FileNotFoundException e) {
            throw new InputDataFileException("File not found: " + getFileName());
        } catch (IOException e) {
            throw new InputDataFileException("I/O exception when reading file"
                    + getFileName());
        } catch (AssertionException e) {
            throw new InputDataFileException(e.getMessage());
        }
    }
    
   
    
    /**
     * Initialises the file array with the given array
     *
     * @param data The array to initialise the file array with
     */
    private void setFileData(String[] data) {
        mFileData = data;
    }
    
    
    /**
     * Converts all line breaks of a string to look like DOS line breaks.
     *
     * @param originalString The string to convert.
     * @return The converted string with all line breaks converted to DOS
     *         line breaks.
     */
    private String convertToDOS(String originalString) {
        final String DOS_LINE_BREAK = "\r\n";
        final byte UNIX_LINE_BREAK = 10;
        final byte MAC_LINE_BREAK = 13;
        int lineBreakPos;
        
        // Look for a Dos line break. If found we assume the string is in Dos
        // format allready. Return the string as it is
        lineBreakPos = originalString.indexOf(DOS_LINE_BREAK);
        if (lineBreakPos > -1) {
            return originalString;
        }
        
        // Replace all Mac line breaks with Unix line breaks
        originalString = originalString.replace((char) MAC_LINE_BREAK,
                (char) UNIX_LINE_BREAK);
        
        
        // Look for Unix line break. If found, replace with Dos line break. A
        // Dos line break contains two chars, carriage return (\r, 13) and
        // newline (\n, 10). A Unix line break is newline, while Mac line
        // break is carriage return. To convert a Unix line break to Dos, we
        // add a carriage return (Mac line break) before each Unix line break
        // (newline). This will create a Dos line break (carriage return +
        // newline).
        lineBreakPos = originalString.indexOf(UNIX_LINE_BREAK);
        if (lineBreakPos > -1) {
            StringBuffer newString = new StringBuffer("");
            
            // Loop the characters of the original string.
            for (int i = 0; i < originalString.length(); i++) {
                // If current char is a Unix line break, add a Mac line break
                if (originalString.charAt(i) == (char) UNIX_LINE_BREAK) {
                    newString = newString.append((char) MAC_LINE_BREAK);
                }
                
                // Finally add the character from the original string
                newString = newString.append(originalString.charAt(i));
            }
            return newString.toString();
        }
        
        return originalString;
    }
}