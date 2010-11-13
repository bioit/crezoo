/*
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

  Revision 1.1.2.1  2001/04/10 13:17:37  frob
  First checkin.

*/

package se.arexis.agdb.util.FileImport;

import se.arexis.agdb.util.*;


/**
 * Instances of this class holds information read from the header row in a
 * file. This includes object type name, format type name, version and
 * delimiter. 
 *
 * @author frob
 * @see Object
 */
public class FileHeader extends Object
{
   private String mObjectTypeName;
   private String mFormatTypeName;
   private int mVersion;
   private Character mDelimiter;


   //////////////////////////////////////////////////////////////////////
   //
   // Constructor
   //
   //////////////////////////////////////////////////////////////////////


   /**
    * Constructs a new instance.
    *
    * @param objectTypeName The object type name to use.
    * @param formatTypeName The format type name to use.
    * @param version The version to use.
    * @param delimiter The delimiter char to use.
    * @exception InputDataFileException If new object could not be
    *            created. 
    */
   public FileHeader(String objectTypeName, String formatTypeName,
                     int version, char delimiter)
      throws InputDataFileException
   {
      try
      {
         // Ensure object type name is given
         Assertion.assertMsg(objectTypeName != null &&
                          objectTypeName.length() > 0,
                          "Error creating FileHeader instance: " +
                          "No object type name given.");

         // Ensure format type name is given
         Assertion.assertMsg(formatTypeName != null &&
                          formatTypeName.length() > 0,
                          "Error creating FileHeader instance: " +
                          "No format type name given.");

         // Copy data to object
         mObjectTypeName = objectTypeName;
         mFormatTypeName = formatTypeName;
         mVersion = version;
         mDelimiter = new Character(delimiter);
      }
      catch (AssertionException e)
      {
         throw new InputDataFileException(e.getMessage());
      }
   }


   //////////////////////////////////////////////////////////////////////
   //
   // Public section
   //
   //////////////////////////////////////////////////////////////////////


   /**
    * Returns the object type name.
    *
    * @return The object type name.
    */
   public String objectTypeName()
   {
      return mObjectTypeName;
   }


   /**
    * Returns the format type name.
    *
    * @return The format type name.
    */
   public String formatTypeName()
   {
      return mFormatTypeName;
   }


   /**
    * Returns the version.
    *
    * @return The version.
    */
   public int version()
   {
      return mVersion;
   }


   /**
    * Returns the delimiter.
    *
    * @return The delimiter.
    */
   public Character delimiter()
   {
      return mDelimiter;
   }
   

   //////////////////////////////////////////////////////////////////////
   //
   // Protected section
   //
   //////////////////////////////////////////////////////////////////////

   //////////////////////////////////////////////////////////////////////
   //
   // Private section
   //
   //////////////////////////////////////////////////////////////////////
   
}
