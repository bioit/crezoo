/*
  Copyright (C) 2000 by Prevas AB. All rights reserved.

  $Log$
  Revision 1.3  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.2  2002/10/18 11:41:26  heto
  Replaced Assertion.assert with Assertion.assertMsg

  Java 1.4 have a keyword "assert".

  Revision 1.1.1.1  2002/10/16 18:14:07  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.1  2001/04/24 09:34:18  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:45  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.2.4  2001/04/19 09:51:58  frob
  Changed class comment.

  Revision 1.1.2.3  2001/04/19 09:48:46  frob
  Renamed the Parse() method to parseInputFile() and made it protected.

  Revision 1.1.2.2  2001/04/03 11:32:01  frob
  Added a call to the inherited Parse(), had been removed by misstake.

  Revision 1.1.2.1  2001/04/03 10:30:21  frob
  Class created as part of the new hierachy for parser classes.

*/

package se.arexis.agdb.util.FileImport;

import java.io.*;
import java.util.*; 
import se.arexis.agdb.util.*;


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
 * <P>When the data rows are parsed, the number of values found on each row
 * is compared to the number of columns. If this is not the same value, the
 * parsing is aborted, as there are errors on the row (too many or too few
 * columns of data). Before the parsing is aborted, the class tries to fix
 * the error. 
 *
 * <P>Subclasses may access the data by using the protected <I>values()</I>
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
public abstract class AbstractValueFileParser extends AbstractFileParser
{
   
   /**
    * An array with the names of the columns in the file
    */
   private String[] mColumnTitles;

   /**
    * A matrix with all data values read from the input file
    */
   private String[][] mValues;


   
   //////////////////////////////////////////////////////////////////////
   //
   // Constructors
   //
   //////////////////////////////////////////////////////////////////////


   /**
    * Default constructor, should never be used
    *
    */
   public AbstractValueFileParser()
   {
      super();
   }
   

   /**
    * Creates a new AbstractValueFileParser instance.
    *
    * @param fileName The file this object should read information from
    * @exception AssertionException If no filename is given
    */
   public AbstractValueFileParser(String fileName)
      throws AssertionException
   {
      super(fileName);
   }

   

   //////////////////////////////////////////////////////////////////////
   //
   // Public section
   //
   //////////////////////////////////////////////////////////////////////


   
   /**
    * Returns an array with the column names of the file
    *
    * @return An array with the column names
    */
   public String[] columnTitles()
   {
      return mColumnTitles;
   }


   //////////////////////////////////////////////////////////////////////
   //
   // Protected section
   //
   //////////////////////////////////////////////////////////////////////

   

   /**
    * Checks whether the given row is a valid data row or not.
    *
    * @param fileRow The row to verify.
    * @param rowNumber The index of the row within the file
    * @return true if the row is a valid data row<BR>
    *         false if the row is not a data row
    */
   protected boolean isDataRow(String fileRow, int rowNumber)
   {
      // If this is second row, we are checking the the title row which is
      // not a data row
      if (super.isDataRow(fileRow, rowNumber) && rowNumber != 1)
      {
         return true;
      }
      return false;
   }   

   
   /**
    * Returns the matrix with data read from the file
    *
    * @return The matrix with data
    */
   protected String[][] values()
   {
      return mValues;
   }

   
   /**
    * Returns the number of columns of data in the file. By default, this
    * is the same number as there are column titles
    *
    * @return The number of columns of data in the file
    */
   protected int columns()
   {
      return countColumnTitles();
   }


   /**
    * Returns the number of column titles read from file
    *
    * @return The number of column titles
    */
   protected int countColumnTitles()
   {
      return mColumnTitles.length;
   }
   

   /**
    * Looks up the given string in the array of column titles and returns
    * the position of the string within the array.
    *
    * @param columnName The column name to look up
    * @return -1 if the string was not found in the array<BR>
    *         The position of the string in the column array
    */
   protected int columnIndex(String columnName)
   {
      for (int aIndex = 0; aIndex < countColumnTitles(); aIndex++)
      {
         if (columnTitles()[aIndex].equalsIgnoreCase(columnName))
         {
            return aIndex;
         }
      }
      return -1;
   }

      
   /**
    * This method parses the file and builds a matrix of string objects
    * containg all the data in the file.
    * 
    * @exception InputDataFileException If any errors when reading the file
    */
   protected void parseInputFile() throws
      InputDataFileException
   {
      try
      {
         // Run the inherited parse() method to read all rows of the
         // file. Also parses the header section
         super.parseInputFile();
         
         // Parse the information on the second row (column names)
         parseColumnTitles(fileData()[1]);
         
         // Create the matrix used for storing the values read from the file 
         values(dataRows(), columns());

         // Variables used in the loop below
         String aRow;             // The current row from the data array
         int aCurrentDataRow = 0; // The row in value-matrix values should
                                  // be placed at
                  
         // Loop all rows in the data array. Start on third row
         for (int aRowCount = 2; aRowCount < fileRows(); aRowCount++)
         {
            // Get the row and check its size. If it's empty or a comment,
            // continue with next row
            aRow = fileData()[aRowCount];
            if (aRow.length() == 0 || aRow.charAt(0) == commentChar())
            {
               continue;
            }

            // Verifies that the row has the correct number of columns. If
            // not, it tries to fix the row. 
            aRow = ParserUtils.fixColumnsOnRow(aRow, (aRowCount + 1),
                                               columns(), fieldDelimiter()); 

            // Copy data from the row to the value matrix
            copyDataFromRow(aRow, aCurrentDataRow++);
         }
      }
      catch (AssertionException e)
      {
         throw new InputDataFileException(e.getMessage());
      }
   }

   

   //////////////////////////////////////////////////////////////////////
   //
   // Private section
   //
   //////////////////////////////////////////////////////////////////////


   /**
    * Sets the array with the names of the columns in the file
    *
    * @param columnTitles An array with column names
    */
   private void columnTitles(String[] columnTitles)
   {
      mColumnTitles = columnTitles;
   }


   /**
    * Creates the matrix used for the data read from the input
    * file. Each element is initialised with an empty string
    *
    * @param rows The number of rows in the matrix
    * @param cols The number of columns in the matrix
    */
   private void values(int rows, int cols)
   {
      mValues = new String[rows][cols];
      for (int r = 0; r < mValues.length; r++)
         for (int c = 0; c < mValues[0].length; c++)
            mValues[r][c] = "";
   }


   /**
    * Parses the given line in order to determine the column names. The
    * names are stored in the columnTitles array
    *
    * @param columnRow The string to parse column titles from
    * @exception InputDataFileException if second row of the file is a
    * comment.
    */
   private void parseColumnTitles(String columnRow)
      throws InputDataFileException
   {
      try 
      {
         // Ensure there is not a comment on the line
         Assertion.assertMsg(columnRow.charAt(0) != commentChar(),
                          "Second line should not be a comment");

         // Build a tokenizer based on the fieldDelimiter 
         StringTokenizer aTokenizer =
            new StringTokenizer(columnRow, fieldDelimiter().toString(),
                                false); 
      
         // Get the number of column titles found of the row and build an
         // array with that size
         columnTitles(new String[aTokenizer.countTokens()]);
      
         // Now get the tokens (eg the column names) from the row and add them
         // to the array
         int aTitleIndex = 0;
         while (aTokenizer.hasMoreElements())
         {
            columnTitles()[aTitleIndex++] = aTokenizer.nextToken();
         }
      }
      catch (AssertionException e)
      {
         throw new InputDataFileException(e.getMessage());
      }
   }



   /**
    * Parses the given string to get the data. The data is inserted in the
    * value matrix on the given row.
    *
    * @param dataRow The row to get data from
    * @param rowIndex The row in the value matrix to insert data into
    */
   private void copyDataFromRow(String dataRow, int rowIndex)
   {
      // Create a tokenizer based on the row. Each token will represent one
      // column of data
      StringTokenizer aTokenizer =
         new StringTokenizer(dataRow, fieldDelimiter().toString(), false); 

      // For each column on the row, add the contents of the column to the
      // correct column on the given row in the value matrix
      for (int aColumnIndex = 0; aColumnIndex < columns() &&
              aTokenizer.hasMoreTokens(); aColumnIndex++)
      {
         values()[rowIndex][aColumnIndex] = aTokenizer.nextToken();
      }
   }   



}
