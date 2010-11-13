/*
  $Log$
  Revision 1.3  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.2  2002/12/13 15:05:27  heto
  No change (removed empty lines)

  Revision 1.1.1.1  2002/10/16 18:14:07  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.2  2001/05/14 09:17:57  frob
  Added method for printing contents of list.

  Revision 1.1  2001/04/24 09:34:20  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:47  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.2.1  2001/04/10 08:23:07  frob
  Initial checkin.

*/

package se.arexis.agdb.util.FileImport;

import java.util.*;

/**
 * A static class used for storing all known
 * FileTypeDefinitions. Each servlet adds the file type definitions that
 * the servlet needs to the list. This means that this class will allways
 * contain all the know file type definitions.
 *
 * @author frob
 * @see Object
 */
public class FileTypeDefinitionList extends Object
{

   /**
    * A static list of known FileTypeDefinitions
    */
   static final private Vector mFileTypeDefinitions = new Vector();


   //////////////////////////////////////////////////////////////////////
   //
   // Constructors
   //
   //////////////////////////////////////////////////////////////////////
   
   
   //////////////////////////////////////////////////////////////////////
   //
   // Public section
   //
   //////////////////////////////////////////////////////////////////////

   
   /**
    * Returns an iterator for the known file type definitions.
    *
    * @return An iterator.
    */
   static public Iterator iterator()
   {
      return mFileTypeDefinitions.iterator();
   }

   
   /**
    * Loops all items in the file type definition collection and returns a
    * Vector with items that match the given parameters.
    *
    * @param objectTypeName The object type name too look for.
    * @param formatTypeName The format type name to look for.
    * @return A Vector with matching file type definitions.
    */
   static public Vector matchingDefinitions(String objectTypeName,
                                            String formatTypeName)
   {
      
      Iterator iterator = iterator();   // Iterates over the collection
      Vector matchingFTD = new Vector(); // The matching definitions
      FileTypeDefinition foundFTD;       // Temporary definition
            
      // Loop all known file type definitions. Compare each definition with
      // the given parameters. If the values match, add the found
      // definition to the result vector.
      while (iterator.hasNext())
      {
         foundFTD = (FileTypeDefinition) iterator.next();
         if (foundFTD.objectTypeName().equalsIgnoreCase(objectTypeName) &&
             foundFTD.formatTypeName().equalsIgnoreCase(formatTypeName))
         {
            matchingFTD.add(foundFTD);
         }
      }
      return matchingFTD;
   }
   

   /**
    * Creates a new file type definition based on the parameters. If this
    * results in a completely new definition, the definition is added to
    * the collection of file type definitions.
    *
    * @param objectTypeName The object type name of the file type
    *                       definition. 
    * @param formatTypeName The format type name of the file type
    *                       definition
    * @param formatVersion The version of the file type definition 
    * @exception FileTypeDefinitionException If a new file type definition
    *            could not be created.
    */
   static public void add(String objectTypeName, String formatTypeName,
                           int formatVersion)
      throws FileTypeDefinitionException
   {
      // Create a new FileTypeDefinitionException based on parameters
      FileTypeDefinition newDef = 
         new FileTypeDefinition(objectTypeName, formatTypeName,
                                formatVersion);

      // Check if this really is a new file type definition. If it is, add
      // it to the collection. 
      if (isNew(newDef))
      {
         mFileTypeDefinitions.add(newDef);
      }
   }


   /**
    * Prints all known file type definitions to System.err
    *
    */
   static public void printContent()
   {
      Iterator defIterator = iterator();
      FileTypeDefinition currentDef;
      System.err.println("Known file type definitions (" +
                         mFileTypeDefinitions.size() + ") :"); 
      while (defIterator.hasNext())
      {
         currentDef = (FileTypeDefinition) defIterator.next();
         System.err.println(currentDef.objectTypeName() + " " +
                            currentDef.formatTypeName());
      }
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
    * Checks if the given FileTypeDefinition object is a new object by
    * comparing it to all known objects. The FileTypeDefinitions are
    * compared using their <I>equals()</I> method.
    *
    * @param definition A FileTypeDefinition to examine.
    * @return True if the given object is not known.
    *         False if the given object is know.
    */
   static private boolean isNew(FileTypeDefinition definition) 
   {
      Iterator iterator = mFileTypeDefinitions.iterator();
      FileTypeDefinition tempDefinition;

      // Loop all known definitions. Copy each definition to a temporary
      // definition object and compare them. 
      while (iterator.hasNext())
      {
         tempDefinition = (FileTypeDefinition) iterator.next();
         if (tempDefinition.equals(definition))
         {
            return false;
         }
      }
      return true;
   }
}
