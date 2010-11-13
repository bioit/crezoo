/*
  $Log$
  Revision 1.2  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.1.1.1  2002/10/16 18:14:07  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.1  2001/04/24 09:34:21  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:50  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.2.1  2001/04/03 10:31:40  frob
  Class created.

*/

package se.arexis.agdb.util.FileImport;

import java.util.*;
import se.arexis.agdb.util.*;


/**
 * A collection of static methods to simplify the parsing of rows.
 *
 * @author frob
 * @see Object
 */
public class ParserUtils extends Object
{


   //////////////////////////////////////////////////////////////////////
   //
   // Public section
   //
   //////////////////////////////////////////////////////////////////////
   
   
   /**
    * Count the number of columns on the given row. Columns are separated
    * by the given fieldDelimiter character
    *
    * @param fileRow The row to count column on.
    * @param fieldDelimiter The filed delimiter.
    * @return The number of columns found on the row.
    */
   public static int columnsOnRow(String fileRow, Character fieldDelimiter)  
   {
      StringTokenizer aTokenizer =
         new StringTokenizer(fileRow, fieldDelimiter.toString(), false);

      return aTokenizer.countTokens();
   }

   
   /**
    * Replaces any empty tokens by a null token. Eg if the given delimiter
    * is ; the row
    * <P><PRE>data1;data2;;data4;
    *
    * <P>will be transformed to
    *
    * <P>data1;data2;\0;data4;\0
    *
    * @param fileRow The row to repace tokens on.
    * @param fieldDelimiter The field delimiter.
    * @return A "fixed" version of the given row.
    */
   public static String replaceEmptyTokens(String fileRow,
                                           Character fieldDelimiter) 
   {
      // Define the empty token and the null token
      String aEmptyToken = fieldDelimiter.toString() +
         fieldDelimiter.toString(); 
      String aNullToken = fieldDelimiter.toString() + "\0" +
         fieldDelimiter.toString(); 

      // If last token is empty (ie, last char is the field delimiter),
      // append a null char at the end of the row
      if (fileRow.substring(fileRow.length() -
                            1).equalsIgnoreCase(fieldDelimiter.toString()))   
      {
         fileRow = fileRow + "\0";
      }
      
      // Copy data to a buffer so it can be modified. Then find the 
      // position of the first empty token
      StringBuffer fileRowAsBuffer = new StringBuffer(fileRow);
      int aTokenPos = fileRow.indexOf(aEmptyToken);

      // While there are empty tokens
      while (aTokenPos > -1)
      {
         // Replace the empty token with a null token
         fileRowAsBuffer.replace(aTokenPos, aTokenPos + 2, aNullToken);

         // Copyt data back to string and look for more empty tokens
         fileRow = fileRowAsBuffer.toString();
         aTokenPos = fileRow.indexOf(aEmptyToken);
      }
      return fileRow;
   }
   

   /**
    * Counts the number of columns on  the given row and compares it to the
    * given number of columns. Before the comparision, empty tokens are
    * replaced. If there are more or less columns than expected, an
    * exception is thrown.
    *
    * @param fileRow The row to check
    * @param rowNumber The position of the given row within the
    *                  file. Should not be zero based as this value is used
    *                  to construct error messages.
    * @param correctColumns The correct number of columns to be found on
    *                       the row.
    * @return The file row with empty tokens replaces
    * @exception AssertionException If there are too many or too few
    *                               columns on the row.
    */
   public static String fixColumnsOnRow(String fileRow, int rowNumber,
                                        int correctColumns,
                                        Character fieldDelimiter) 
      throws AssertionException
   {
      
      // Replace any empty tokens with null tokens
      fileRow = replaceEmptyTokens(fileRow, fieldDelimiter);
 
      // Count the number of columns on the row
      int aFoundColumns = columnsOnRow(fileRow, fieldDelimiter);

      // Correct number of columns found
      if (aFoundColumns == correctColumns)
      {
         return fileRow;
      }
         
      // Too many columns found
      else if (aFoundColumns > correctColumns)
      {
         throw new AssertionException("Line " + rowNumber +
                                      " has more data columns than " +
                                      "expected.");
      }
      
      // Too few columns found
      else 
      {
         throw new AssertionException("Line " + rowNumber +
                                      " has less data columns than " +
                                         "expected.");
      }
   }


}
