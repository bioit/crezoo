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


  Revision 1.1  2001/04/24 09:34:19  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:46  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.1.1.2.3  2001/04/03 10:30:48  frob
  Rewritten in accordance to new superclasses.

  Revision 1.1.1.1.2.2  2001/03/19 13:29:47  frob
  Added a comment, just testing

  Revision 1.1.1.1.2.1  2001/03/19 13:19:10  frob
  Removed ^M and corrected the layout


*/

package se.arexis.agdb.util.FileImport;

import java.io.*;
import java.util.*; 
import se.arexis.agdb.util.*;


/**
 * FileParser extends the AbstractValueFileParser with a method for getting
 * data read from the file and with a test method. To access the data read
 * from the file, a method named <I>getValue</I> is used. 
 *
 * @author frob
 * @see AbstractValueFileParser
 */
public class FileParser extends AbstractValueFileParser
{

   //////////////////////////////////////////////////////////////////////
   //
   // Constructors
   //
   //////////////////////////////////////////////////////////////////////


   /**
    * Default constructor, should never be used
    *
    */
   public FileParser()
   {
      super();
   }
   

   /**
    * Creates a new FileParser instance.
    *
    * @param fileName The file this object should read information from
    * @exception AssertionException If no filename is given
    */
   public FileParser(String fileName)
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
    * Gets the value from the column with the given title on the given
    * row. If value could not be found, an empty string is returned.
    *
    * @param columnTitle The name of the column to get data from 
    * @param row The row in the column to get data from
    * @return The value found at the given [row][col]
    */
   public String getValue(String columnTitle, int row)
   {
      try
      {
         // Ensure we try to access a valid row
         Assertion.assertMsg(row > -1 && row < dataRows(),
                          "Trying to acces row " + row + " in the " + 
                          "FileParser which only has " + dataRows() +
                          " rows"); 

         // Get the column index of the given column title
         int aColumnIndex = columnIndex(columnTitle);
         
         // Verify that column namn was found
         Assertion.assertMsg(aColumnIndex > -1, "Column title " + columnTitle +
                          "was not found in the FileParser");

         // Return the value in on the given row on the found column
         return values()[row][aColumnIndex];
      }
      catch (AssertionException e)
      {
         // If value could not be found, return an empty string
         return "";
      }
   }
   
   
   /**
    * Tests the FileParser object by printing all its information
    *
    * @exception FileParserException If error when accessing FileParser
    * object 
    */
   public void test()
      throws FileParserException
   {
      System.out.println("==================================================");
      System.out.println("Testing the FileParser with file " + fileName());
      System.out.println("--------------------------------------------------");

      String aColName;
      
      // Loop all data rows + one title row
      for (int r = 0; r < dataRows() + 1; r++)
      {
         // On current row, loop all columns
         for (int h = 0; h < columns(); h++)
         {
            // Get the title of the current column
            aColName = columnTitles()[h];
            
            // If first run, print the column title
            if (r == 0)
            {
               System.out.print(aColName);
            }

            // not first run, get the values by using the column name
            else
            {
               System.out.print(getValue(aColName, r - 1));
            }

            // Print a delimiter
            if (h < columns() - 1)
            {
               System.out.print('-');
            }
            
         }
         System.out.println("");
      }
      System.out.println("==================================================");
   }


}
