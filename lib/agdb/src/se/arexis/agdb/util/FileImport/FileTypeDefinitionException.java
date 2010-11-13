/*
  $Log$
  Revision 1.2  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.1.1.1  2002/10/16 18:14:07  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.1  2001/04/24 09:34:19  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:47  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.2.1  2001/04/10 08:21:29  frob
  Initial checkin.

*/


package se.arexis.agdb.util.FileImport;


/**
 * The FileTypeDefinitionException is thrown whenever an error occours when a
 * FileTypeDefinitionException instance is accessed.
 *
 * @author frob
 * @see Exception
 */
public class FileTypeDefinitionException extends Exception
{

   /**
    * Creates a new FileTypeDefinitionException instance.
    *
    */
   public FileTypeDefinitionException()
   {
      this("FileTypeDefinitionException");
   }


   /**
    * Creates a new FileTypeDefinitionException instance with a descriptive text.
    *
    * @param errorMsg The text to be displayed with the exception.
    */
   public FileTypeDefinitionException(String errorMsg)
   {
      super(errorMsg);
   }
}
