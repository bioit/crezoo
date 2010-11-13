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

  Revision 1.1.2.8  2001/04/19 06:56:02  frob
  New file type definitions: VARIABLESET, UVARIABLESET

  Revision 1.1.2.7  2001/04/18 06:42:15  frob
  Added some object type definitions.

  Revision 1.1.2.6  2001/04/12 09:52:53  frob
  Changed some object type names.
  Added a format type name for mappings.

  Revision 1.1.2.5  2001/04/12 05:50:24  frob
  Added new object type name: MARKERSET.
  Changed some comment layout.

  Revision 1.1.2.4  2001/04/11 06:36:19  frob
  Changed the names of the string constants.

  Revision 1.1.2.3  2001/04/10 13:00:33  frob
  Defined constant representations for object- and format type names.

  Revision 1.1.2.2  2001/04/10 08:20:24  frob
  Fixed file format (Unix -> DOS).

  Revision 1.1.2.1  2001/04/10 08:15:45  frob
  First checkin.

*/

package se.arexis.agdb.util.FileImport;

import se.arexis.agdb.util.*;

/**
 * Instances of this class holds information about a file type
 * definiton. Each definition has a object type name, a format type name
 * and a version. Object type name and format type name must be one of the
 * known names stored in the static arrays contained in this class.
 *
 * @author frob
 * @see Object
 */
public class FileTypeDefinition extends Object
{

   /** The name of this object type. */
   private String mObjectTypeName;

   /** The name of this format type. */
   private String mFormatTypeName;

   /** The version of the format type for the object type. */ 
   private int mVersion;

   /** The definition of object type name for individuals. */
   static public final String INDIVIDUAL = "INDIVIDUAL";
   
   /** The definition of object type name for groupings. */
   static public final String GROUPING = "GROUPING";
   
   /** The definition of object type name for samples. */
   static public final String SAMPLE = "SAMPLE";
   
   /** The definition of object type name for markers. */
   static public final String MARKER = "MARKER";
   
   /** The definition of object type name for markerset. */
   static public final String MARKERSET = "MARKERSET";
   
   /** The definition of object type name for unified markers. */
   static public final String UMARKER = "UMARKER";

   /** The definition of object type name for unified marker sets */ 
   static public final String UMARKERSET = "UMARKERSET";
   
   /** The definition of object type name for library markers. */
   static public final String LMARKER = "LMARKER";

   /** The definition of object type name for genotypes. */
   static public final String GENOTYPE = "GENOTYPE";
   
   /** The definition of object type name for variables. */
   static public final String VARIABLE = "VARIABLE";

   /** The definition of object type name for variable sets */
   static public final String VARIABLESET = "VARIABLESET";

   /** The definition of object type name for unified variables. */
   static public final String UVARIABLE = "UVARIABLE";
   
   /** The definition of object type name for unified variable sets */ 
   static public final String UVARIABLESET = "UVARIABLESET";
   
   /** The definition of object type name for phenotypes. */
   static public final String PHENOTYPE = "PHENOTYPE";
   
   /** The definition of object type name for analysises. */
   static public final String ANALYSIS = "ANALYSIS";
   
   /** The definition of object type name for chromosomes. */
   static public final String CHROMOSOME = "CHROMOSOME";
   
   /** The definition of object type name for roles. */
   static public final String ROLE = "ROLE";
   
   /** All known object type names. */
   static private final String mObjectTypeNames[] = { INDIVIDUAL, GROUPING, 
                                                      SAMPLE, MARKER, 
                                                      MARKERSET, UMARKER, 
                                                      UMARKERSET, LMARKER, 
                                                      GENOTYPE, VARIABLE, 
                                                      VARIABLESET,
                                                      UVARIABLE,  
                                                      UVARIABLESET,
                                                      PHENOTYPE, ANALYSIS,
                                                      CHROMOSOME, ROLE };  
   

   /** The definition of format type name for lists. */
   static public final String LIST = "LIST";

   /** The definition of format type name for matrixes. */
   static public final String MATRIX = "MATRIX";

   /** The definition of format type name for mapping files */
   static public final String MAPPING = "MAPPING";
   
   
   /** All known format type names. */
   static private final String mFormatTypeNames[] = { LIST, MATRIX, MAPPING }; 
   
   
   //////////////////////////////////////////////////////////////////////
   //
   // Constructors
   //
   //////////////////////////////////////////////////////////////////////


   /**
    * Creates a new FileTypeDefinition instance.
    *
    * @param objectTypeName The object type name of the definition.
    * @param formatTypeName The format type name of the definition.
    * @param version The version of the definition.
    * @exception FileTypeDefinitionException If the FileTypeDefinition
    *            could not be created.
    */
   public FileTypeDefinition(String objectTypeName, String formatTypeName,  
                             int version)
      throws FileTypeDefinitionException
   {
      try
      {
         // Ensure object type name is given
         Assertion.assertMsg(objectTypeName != null &&
                          objectTypeName.length() > 0,
                          "Requires an object type name.");
         
         // Ensure given object type name is valid
         Assertion.assertMsg(isKnownObjectType(objectTypeName),
                          "Object type name " + objectTypeName +
                          " is unknown."); 
      
         // Ensure format type name is given
         Assertion.assertMsg(formatTypeName != null &&
                          formatTypeName.length() > 0,
                          "Requires a format type name.");
         
         // Ensure given format type name is valid
         Assertion.assertMsg(isKnownFormatType(formatTypeName),
                          "Format type name " + formatTypeName +
                          " is unknown."); 
         
         // Store the information
         mObjectTypeName = objectTypeName;
         mFormatTypeName = formatTypeName;
         mVersion = version;
      }
      catch (AssertionException e)
      {
         throw new
            FileTypeDefinitionException("Exception when creating a new " + 
                                        "FileTypeDefinition instance: " +
                                        e.getMessage());
      }
   }
   

   //////////////////////////////////////////////////////////////////////
   //
   // Public section
   //
   //////////////////////////////////////////////////////////////////////

   
   /**
    * Returns the object type name of this object.
    *
    * @return The object type name.
    */
   public String objectTypeName()
   {
      return mObjectTypeName;
   }


   /**
    * Returns the format type name of this object.
    *
    * @return The format type name.
    */
   public String formatTypeName()
   {
      return mFormatTypeName;
   }


   /**
    * Returns the version of this format type of the object type.
    *
    * @return The version.
    */
   public int version()
   {
      return mVersion;
   }


   /**
    * Compares two FileTypeDefinition objects to find out if they are
    * equal. Two objects are equal if they have the same object- and format
    * type name and the same version.
    *
    * @param definition The FileTypeDefinition to compare to this object.
    * @return True if the objects are equal.
    *         False if the objects are unequal.
    */
   public boolean equals(FileTypeDefinition definition)
   {
      if (objectTypeName().equals(definition.objectTypeName()) &&
          formatTypeName().equals(definition.formatTypeName()) && 
          version() == definition.version())
      {
         return true;
      }
      return false;
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
   
      
   /**
    * Checks if the given name is the name of a known object type name.
    *
    * @param objectTypeName The name to check.
    * @return True if the given name is valid object type name.
    *         False is the given name is not a valid object type name.
    */
   private boolean isKnownObjectType(String objectTypeName)
   {
      for (int i = 0; i < mObjectTypeNames.length; i++)
      {
         if (mObjectTypeNames[i].equalsIgnoreCase(objectTypeName))
         {
            return true;
         }
      }
      return false;
   }

   
   /**
    * Checks if the given name is the name of a known format type name. 
    *
    * @param formatTypeName The name to check.
    * @return True if the given name is a valid format type name.
    *         False if the given name is not a valid format type name.
    */
   private boolean isKnownFormatType(String formatTypeName)
   {
      for (int i = 0; i < mFormatTypeNames.length; i++)
      {
         if (mFormatTypeNames[i].equalsIgnoreCase(formatTypeName))
         {
            return true;
         }
      }
      return false;
   }
   
   
}
