/*
  Copyright (C) 2000 by Prevas AB. All rights reserved.

  $Log$
  Revision 1.4  2004/02/04 08:37:17  heto
  Test av blobar..

  Revision 1.3  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.2  2002/10/18 11:41:26  heto
  Replaced Assertion.assert with Assertion.assertMsg

  Java 1.4 have a keyword "assert".

  Revision 1.1.1.1  2002/10/16 18:14:07  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.2  2001/05/31 05:39:41  frob
  Now accepts a delimiter with more than one char.

  Revision 1.1  2001/04/24 09:34:18  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:44  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.2.3  2001/04/19 09:59:59  frob
  Major changes: Parse() now requires a vector with file type definitions.
                 New static method for scanning file header: scanFileHeader()
                 Enhanced handling of rows with convertToDOS(), which converts
                 Unix and Mac rows to Dos format.
  Minor changes: Some methods renamed, made protected/private, etc.

  Revision 1.1.2.2  2001/04/03 10:35:14  frob
  Removed some old comments.

  Revision 1.1.2.1  2001/04/03 10:30:20  frob
  Class created as part of the new hierachy for parser classes.


*/

package se.arexis.agdb.util.FileImport;

import java.io.*;
import java.util.*; 
import se.arexis.agdb.util.*;


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
public abstract class AbstractFileParser
{
   /**
    * The name of the file this object reads data from   
    */
   private String mFileName;

   /**
    * The name of the objecttype the data belongs to. Read from the data
    * file 
    */
   private String mObjectTypeName;

   /**
    * The name of the formattype of the data. Read from the data file 
    */
   private String mFormatTypeName;

   /**
    * The version of the file format. Read from the data file
    */
   private int mFormatVersion;
   
   /**
    * The delimiter used to separate the columns in the data file. Read
    * from the file
    */
   private Character mFieldDelimiter;

   /**
    * The char used to comment lines in the data file
    */
   private static final char mCommentChar = '#';

   /**
    * The contents of the file represented as a string array. Each line of
    * the file represents one row in the array. 
    */
   private String[] mFileData;

   /**
    * The number of rows in the input file that actually contains
    * data. This is <I>not</I> the same as the total number of rows in the
    * file 
    */
   private int mDataRows = 0;
   

   /**
    * The mapping table that mapps data rows to file rows
    */
   private int[] mDataRow2FileRow;
   

   //////////////////////////////////////////////////////////////////////
   //
   // Constructors
   //
   //////////////////////////////////////////////////////////////////////

   
   /**
    * Empty constructor, should never be used directly
    *
    */
   public AbstractFileParser()
   {
      super();
   }
   
   
   /**
    * Creates a new AbstractFileParser instance.
    *
    * @param fileName The name of the input file this object should read
    * data from 
    * @exception AssertionException If no filename is given
    */
   public AbstractFileParser(String fileName)
      throws AssertionException
   {
      fileName(fileName);
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
    * @param fileTypeDefinitions A vector with valid file type definitions.
    * @exception InputDataFileException If anything wrong with the input
    *            file. 
    * @exception FileParserException If no file type definitions are passed.
    */
   public void Parse(Vector fileTypeDefinitions)
      throws InputDataFileException, FileParserException
   {
      try
      {
         // Ensure a vector with valid file type definitions were passed to
         // the method
         Assertion.assertMsg(fileTypeDefinitions != null,
                          "An Vector with known file type definitions " +
                          "has to be passed to the Parse()-method");
         Assertion.assertMsg(fileTypeDefinitions.size() > 0,
                         "Vector with file type definitions passed to " +
                         "Parse()-method contains no file type definitions.");
         
         // Ensure the file has the correct object- and format type
         validateFileType(fileTypeDefinitions);

         // Now parse the file
         parseInputFile();
      }
      catch (AssertionException e)
      {
         throw new FileParserException("Error parsing file: " +
                                       e.getMessage());
      }

   }

   
   /**
    * Returns the name of the objecttype the data belong to.
    *
    * @return The name of the object type
    */
   public String objectTypeName()
   {
      return mObjectTypeName;
   }


   /**
    * Returns the name of the formattype the file is structured in 
    *
    * @return The name of the formattype
    */
   public String formatTypeName()
   {
      return mFormatTypeName;
   }
   

   /**
    * Returns the version of the file format
    *
    * @return The version of the format
    */
   public int formatVersion()
   {
      return mFormatVersion;
   }
   

   /**
    * Returns the delimiter used to separate data fields in the file 
    *
    * @return The field delimiter
    */
   public Character fieldDelimiter()
   {
      return mFieldDelimiter;
   }
   

   /**
    * Returns the number of data rows in the file. Rows not containing data
    * are not counted
    *
    * @return The number of data rows
    */
   public int dataRows() 
   {
      return mDataRows;
   }


   /**
    * Returns the row number in the file that the data row with the given
    * number is located at. The method is zero-based, which means that the
    * first data row is row 0, the second row is row 1 and so on. The
    * returned value is also zero-based
    *
    * @param dataRow The number of a data row to look up
    * @return The row number in the file of the given data row. <BR>
    *         -1 If row was not found

    */
   public int dataRow2FileRow(int dataRow)
   {
      try
      {
         return mDataRow2FileRow[dataRow];
      }
      catch (IndexOutOfBoundsException e)
      {
         return -1;
      }
   }
   

   /**
    * Scans the file header of the given file and constructs a FileHeader
    * based on the found information.
    *
    * @param fileName The file to scan.
    * @return A FileHeader with the information found in the file.
    * @exception InputDataFileException If anything wrong with the given
    *            input file.
    */
   public static FileHeader scanFileHeader(String fileName)
      throws InputDataFileException
   {
      try
      {
         
      RandomAccessFile file = new RandomAccessFile
            (fileName, "r");
         String headerRow = file.readLine();
         FileHeader header = parseHeader(headerRow);
         return header;
      }
      catch (FileNotFoundException e)
      {
         throw new InputDataFileException("File not found: " + fileName);
      }
      catch (IOException e)
      {
         throw new InputDataFileException("Error accessing file: " + fileName);
      }
      
   }
   

   //////////////////////////////////////////////////////////////////////
   //
   // Protected section
   //
   //////////////////////////////////////////////////////////////////////
   
   
   /**
    * This method parses the file and builds an array of string objects
    * containg all the data in the file.
    *
    */
   protected void parseInputFile()
      throws InputDataFileException
   {
      try
      {
         // Read the inputfile into the string array
         readInputFile();

         // parse the header information 
         getHeaderData(fileData()[0]);
      }
      catch (Exception e)
      {
         throw new InputDataFileException(e.getMessage());
      }
   }
   


   /**
    * Determines if the given row is a data row
    *
    * @param dataRow The row to examine
    * @param rowNumber The position of the row in the file. First row is
    * row number 0
    * @return true if the row is a data row<BR>
    *         false if the row is not a data row
    */
   protected boolean isDataRow(String fileRow, int rowNumber)
   {
      // If row is not first row and row is more than one char and if first
      // char is not the comment char, it is a data row
      if (rowNumber != 0 && fileRow.length() > 0 &&
          fileRow.charAt(0) !=  commentChar()) 
      {
         return true;
      }
      return false;
   }   
   
   
   /**
    * Returns the name of the file this object reads data from
    *
    * @return The name of the file
    */
   protected String fileName()
   {
      return mFileName;
   }


   /**
    * Returns the comment char used to comment lines in the input file. 
    *
    * @return The comment char
    */
   protected static char commentChar()
   {
      return mCommentChar;
   }


   /**
    * Returns the total number of rows in the file
    *
    * @return Number of rows in the file
    */
   protected int fileRows()
   {
      return fileData().length;
   }


   /**
    * Returns the file array
    *
    * @return An array with the file rows
    */
   protected String[] fileData()
   {
      return mFileData;
   }
   
   
   //////////////////////////////////////////////////////////////////////
   //
   // Private section
   //
   //////////////////////////////////////////////////////////////////////



   /**
    * Sets the name of the file this object reads data from
    *
    * @param fileName The name of the file
    * @exception FileNotFoundException If no filename is given
    */
   private void fileName(String fileName)
      throws AssertionException
   {
      Assertion.assertMsg(fileName != null && fileName.length() > 0,
                       "No filename given, could not create the file parser");
      mFileName = fileName;
   }


   /**
    * Sets the name of the objecttype
    *
    * @param name The name of the objecttype
    */
   private void objectTypeName(String name)
   {
      mObjectTypeName = name;
   }

   
   /**
    * Sets the name of the formattype
    *
    * @param name The name of the formattype
    */
   private void formatTypeName(String name)
   {
      mFormatTypeName = name;
   }

   
   /**
    * Sets the version of the file format
    *
    * @param version The file format version
    */
   private void formatVersion(int version)
   {
      mFormatVersion = version;
   }
   

   /**
    * Sets the field delimiter used in the file
    *
    * @param delimiter The delimiter
    */
   private void fieldDelimiter(Character delimiter)
   {
      mFieldDelimiter = delimiter;
   }


   /**
    * Sets the number of data rows in the input file
    *
    * @param numberOfDataRows The number of rows in the file
    */
   private void dataRows(int numberOfDataRows)
   {
      mDataRows = numberOfDataRows;
   }


   /**
    * If the last character on the given row in a CR, a string where the 
    * CR is removed is returned . If there is no CR at the end, the
    * original string is returned 
    *
    * @param fileRow A string from which a trailing CR should be removed.
    * @return The string with the trailing CR is removed.
    */
   private String removeCR(String fileRow)
   {
      if (fileRow.charAt(fileRow.length() -1) == '\r')
      {
         return fileRow.substring(0,fileRow.length() - 1);
      }
      else
      {
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
    throws InputDataFileException
   {
       File inputFile = null;
       FileInputStream inputStream = null;
       try
       {
           inputFile = new File(fileName());
           inputStream = new FileInputStream(inputFile);
       }
       catch (Exception e)
       {
           Errors.log(e.getMessage());
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
    *
    * @exception InputDataFileException if an error occurs
    */
   private void readInputFile(FileInputStream inputStream, int length)
      throws InputDataFileException
   {
      try 
      {
         // Create a file and a stream from the given filename.
         //File inputFile = new File(fileName());
         //FileInputStream inputStream = new FileInputStream(inputFile);

         // Create a byte array with the same size as the file and read the
         // file into the array. If the size of the file is 0, an exception
         // will be raise.
         byte[] inputAsByte = new byte[length]; // (int) inputFile.length()
         Assertion.assertMsg(inputAsByte.length > 0, "Given input file (" +
                          fileName() + ") is empty");
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
         fileData(new String[tokenizer.countTokens()]);
         initDataRow2FileRow(tokenizer.countTokens());
         
         // Step through the tokens (eg all rows in the file) and add them
         // to the file array. Before each row is added, the CR at the end
         // of each row is removed. Also count the number of data rows, eg
         // rows that actually contains data. Header-, column-, comment-
         // and blanklines are not counted
         int rowIndex = 0;
         int dataRowCount = 0;
         String currentRow;
         while (tokenizer.hasMoreElements())
         {
            // Get the row, remove the CR and add it to the file array
            currentRow = removeCR(tokenizer.nextToken());
            fileData()[rowIndex] = currentRow;
            
            // If current row is a data row, map it against the file
            // row. Also increase the number of data rows 
            if (isDataRow(currentRow, rowIndex))
            {
               mapDataRow(dataRowCount, rowIndex);
               dataRowCount++;
            }

            rowIndex++;
         }
         // Store the number of data rows for future use
         dataRows(dataRowCount);
      }
      catch (FileNotFoundException e)
      {
         throw new InputDataFileException("File not found: " + fileName());
      }
      catch (IOException e)
      {
         throw new InputDataFileException("I/O exception when reading file"
                                          + fileName());
      }
      catch (AssertionException e)
      {
         throw new InputDataFileException(e.getMessage());
      }
   }


   /**
    * Parses the information in the passed string, which is supposed to be
    * a header row. Expects to find a row with the following structure:
    *
    * <P><CODE>
    *    objecttypename/formattypename/version/delimiter<BR>
    * </CODE>
    *
    * <P>Objecttypename and formattypename should be strings<BR>
    * Version should be an integer<BR>
    * Delimiter should be one character<BR>
    *
    * @param headerRow The string to parse
    * @exception InputDataFileException If the headerRow has an unknown format
    */
   private void getHeaderData(String headerRow)
      throws InputDataFileException
   {
      FileHeader header = parseHeader(headerRow);
      
      // Parse the fields from the header
      objectTypeName(header.objectTypeName());
      formatTypeName(header.formatTypeName());
      formatVersion(header.version());
      fieldDelimiter(header.delimiter());
   }


   /**
    * Constructs the mapping table for data rows => file rows
    *
    * @param rows The number of rows in the file
    */
   private void initDataRow2FileRow(int rows)
   {
      mDataRow2FileRow = new int[rows];
   }


   /**
    * Mapps the given data row to the given file row. Values should be
    * zero-based, eg row 1 is 0, row 2 is 1 and so on. This applies to bot
    * data rows and file rows.
    *
    * @param dataRow The data row number
    * @param fileRow The file row number
    */
   private void mapDataRow(int dataRow, int fileRow)
   {
      mDataRow2FileRow[dataRow] = fileRow;
   }
   

   /**
    * Initialises the file array with the given array
    *
    * @param data The array to initialise the file array with 
    */
   private void fileData(String[] data)
   {
      mFileData = data;
   }


   /**
    * Ensures the data file has the correct object- and format
    * type. Reads the header of the file and looks for a file type
    * definition with matching object- and format type in the vector passed
    * to the method.
    *
    * @param fileTypeDefinitions A vector with valid file type definitions.
    * @exception InputDataFileException If no format type definition in the
    *            given vector matches the object- and format type name found
    *            in the header of the file
    */
   private void validateFileType(Vector fileTypeDefinitions)
      throws InputDataFileException
   {
      // Get the header of the file
      FileHeader fileHeader = scanFileHeader(fileName());
      
      // Build an interator and iterate the given definitions
      Iterator defIterator = fileTypeDefinitions.iterator();
      FileTypeDefinition definition;
      while (defIterator.hasNext())
      {
         // Compare the current definition to the header object. If object-
         // and format type name is equal, the file is of the correct
         // type. Means we can exit.
         definition = (FileTypeDefinition) defIterator.next();
         if (definition.objectTypeName().equalsIgnoreCase(fileHeader.objectTypeName()) &&
             definition.formatTypeName().equalsIgnoreCase(fileHeader.formatTypeName()))
         {
            return;
         }

         // The file is of a incorrect type.
         throw new InputDataFileException("Input file contains unknown " +
                                          "object- and/or format type name: " +
                                          fileHeader.objectTypeName() + " " +
                                          fileHeader.formatTypeName());
      }
   }


   /**
    * Converts all line breaks of a string to look like DOS line breaks. 
    *
    * @param originalString The string to convert.
    * @return The converted string with all line breaks converted to DOS
    *         line breaks.
    */
   private String convertToDOS(String originalString)
   {
      final String DOS_LINE_BREAK = "\r\n";
      final byte UNIX_LINE_BREAK = 10;
      final byte MAC_LINE_BREAK = 13;
      int lineBreakPos;

      // Look for a Dos line break. If found we assume the string is in Dos
      // format allready. Return the string as it is
      lineBreakPos = originalString.indexOf(DOS_LINE_BREAK);
      if (lineBreakPos > -1)
      {
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
      if (lineBreakPos > -1)
      {
         StringBuffer newString = new StringBuffer("");

         // Loop the characters of the original string.
         for (int i = 0; i < originalString.length(); i++)
         {
            // If current char is a Unix line break, add a Mac line break
            if (originalString.charAt(i) == (char) UNIX_LINE_BREAK)
            {
               newString = newString.append((char) MAC_LINE_BREAK);
            }

            // Finally add the character from the original string
            newString = newString.append(originalString.charAt(i));
         }
         return newString.toString();
      }
      
      return originalString;
   }

   /**
    * Parses header information from a string and builds a FileHeader
    * object based on the parsed information.
    *
    * @param headerRow The string to parse. Should be a valid header string
    * @return A FileHeader object containing the information parsed from
    *         the string.
    * @exception InputDataFileException If anything wrong with the given string.
    */
   public static FileHeader parseHeader(String headerRow)
      throws InputDataFileException
   {
      try
      {
         // Ensure there is not a comment on the line
         Assertion.assertMsg(headerRow.charAt(0) != commentChar(),
                          "First line should not be a comment");
         
         // Build a tokenizer to used when parsing the fields from the row
         StringTokenizer aTokenizer =
            new StringTokenizer(headerRow, "/", false);

         // Ensure there are four fields in the header
         Assertion.assertMsg(aTokenizer.countTokens() == 4,
                          "File header has wrong format. First line must " +
                          "have the following layout: " +
                          "objecttypename/formattypename/version/delimiter");
         
         // Parse the fields from the header
         String objectTypeName = aTokenizer.nextToken();
         String formatTypeName = aTokenizer.nextToken();
         int formatVersion = Integer.parseInt(aTokenizer.nextToken());

         // Read the delimiter as a string. We will accept a delimiter with
         // more than one char, but only the first char will be used, the
         // rest of the string will be ignored.
         String delimiter = aTokenizer.nextToken();

         // Construct a new header object, based on the data in the string
         // and return it.
         FileHeader header = new FileHeader(objectTypeName, formatTypeName,
                                            formatVersion,
                                            delimiter.charAt(0));
         return header;
      }
      catch (NumberFormatException e)
      {
         throw new InputDataFileException("File version has wrong format, "
                                          + "should be an iteger.");
      }
      catch (AssertionException e)
      {
         throw new InputDataFileException(e.getMessage());
      }
   }

   
}

