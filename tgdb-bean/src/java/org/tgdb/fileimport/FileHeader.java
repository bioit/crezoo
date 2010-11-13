package org.tgdb.fileimport;

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
   
   /**
    * Constructs a new instance by parsing the header row of a file.
    *
    * @param row the file row to parse for information
    * @exception InputDataFileException If new object could not be
    *            created. 
    */
   public FileHeader(String row) throws InputDataFileException
   {
       String[] tmp = row.split("/");
       
       mObjectTypeName = tmp[0];
       mFormatTypeName = tmp[1];
       mVersion = new Integer(tmp[2]).intValue();
       mDelimiter = new Character(tmp[3].charAt(0));       
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
