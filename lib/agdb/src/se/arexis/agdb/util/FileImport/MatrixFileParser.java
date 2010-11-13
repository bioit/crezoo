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


  Revision 1.2  2001/09/06 13:01:04  roca
  Major changes to Genotype import handling.
  modified Linkage output format for Post makeped and allele numbering.
  Bug when deleting markersets fixed

  Revision 1.1  2001/04/24 09:34:21  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:49  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.2.1  2001/04/03 10:31:39  frob
  Class created.


*/

package se.arexis.agdb.util.FileImport;

import se.arexis.agdb.util.*;

/**
 * MatrixFileParser extends the AbstractValueFileParser with methods for
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
 * <P>When accessing a section in the matrix, both values will be
 * returned. Eg, accessing row 2, column 2 will return an array with the
 * values [data_5a, data_5b]. The exception is when the first column is
 * accessed. As this column only holds one value, just one value is
 * returned. 
 *
 * <P>
 * @author frob
 * @see AbstractValueFileParser
 */
public class MatrixFileParser extends AbstractValueFileParser
{


   //////////////////////////////////////////////////////////////////////
   //
   // Constructors
   //
   //////////////////////////////////////////////////////////////////////
   

   /**
    * Creates a new MatrixFileParser instance.
    *
    * @param fileName The name of the file this object reads data from
    * @exception AssertionException If inherited constructor could not be run
    */
   public MatrixFileParser(String fileName)
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
    * Returns the value found in the column with the given name and at the
    * given row. As this class represents a matrix of data, it should
    * return not just one value, but two. Hence, this method returns an
    * array of two values. The exception is when the first column is 
    * accessed. This column is not a part of the matrix and holds just one
    * value. This means that when this column is accessed, the returned
    * array does only hold one value. If column or row index are invalid,
    * an empty string is returned
    *
    * @param columnName The title of the column to get data from.
    * @param row The row to get data from.
    * @return A string-array of values (1 or 2 values)
    */
   public String[] getValue(String columnName, int row)
   {
      try 
      {
         // Get the index of the column title and ensure it is a valid
         // index 
         int aColumnIndex = columnIndex(columnName);
         Assertion.assertMsg(aColumnIndex > -1, null);

         // If it is the first column, return just one value
         if (aColumnIndex == 0)
         {
            return new String[] { values()[row][aColumnIndex]};
         }

         // If it is not the first column, return two values
         else 
         {
            // The columnIndex is the index of the given column name in the
            // column matrix. This index can not be used when accessing the
            // data matrix, as there are twice as many data columns as
            // there are header columns. Hence we have to recalculate the
            // column index. As there are twice as many data columns as
            // there are headers, we dublicate the column index. Then we
            // decrease the value with one to compensate for the first
            // column which is not a part of the matrix 
            aColumnIndex = (aColumnIndex * 2) - 1;

            // Return the values found in the two columns
            return new String[] { values()[row][aColumnIndex],
                                  values()[row][aColumnIndex + 1]};
         }
      }
      catch (AssertionException e)
      {
         // If no value found, return empty sting
         return new String[] { "" };
      }
   }


   /**
    * Tests the MatrixFileParser object by printint all its information
    *
    */
   public void test()
   {
      System.out.println("==================================================");
      System.out.println("Testing the MatrixFileParser using " + fileName());
      System.out.println("--------------------------------------------------");

      String aColName;
      
      // Loop all data rows + one title row
      for (int row = 0; row < dataRows() + 1; row++)
      {
         // On current row, loop all columns
         for (int col = 0; col < columns(); col++)
         {
            // If this is first row and there are column titles left to
            // print, print title
            if (row == 0 && col < countColumnTitles())
            {
               System.out.print(columnTitles()[col]);

               // If there are more titles to print, print delimiter
               if (col < countColumnTitles() - 1)
               {
                  System.out.print('-');
               }
               
            }

            // If this is not first row
            else if (row > 0 )
            {
               aColName = columnTitles()[(col + 1) / 2];

               // If this is the first column, get the single value and
               // print it
               if (col == 0)
               {
                  System.out.print(getValue(aColName, row - 1)[0]);
               }

               // if not first column, get the tuple of values and print them
               else
               {
                  String[] aValue = getValue(aColName, row - 1);
                  System.out.print(aValue[0]);
                  System.out.print('-');
                  System.out.print(aValue[1]);
                  
                  // As we use values from two columns, we must increase
                  // the current column or we will print it two times
                  col++;
               }
               
               // if more columns left, print a delimiter
               if (col < columns() - 1)
               {
                   System.out.print('-');
               }
            }
         }
         System.out.println("");
      }
      System.out.println("==================================================");
   }

   
   //////////////////////////////////////////////////////////////////////
   //
   // Protected section
   //
   //////////////////////////////////////////////////////////////////////


   /**
    * Returns the number of data columns in the file. The value is
    * calculated from the number of columns on the header row. However, as
    * there are twice as many data columns as columns in the header, we
    * must multiply that value with 2. Befor this is done, the value must be
    * decreased with one so the first column is not duplicated. Finlly we
    * add 1 to the value so the first column is included in the total
    * number of data columns
    *
    * @return The number of data columns in the file
    */
   protected int columns()
   {
      return 1 + ((super.columns() - 1) * 2);
   }





}
