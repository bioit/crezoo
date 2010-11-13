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

  Revision 1.2  2001/04/24 06:31:46  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.2.1  2001/03/21 09:15:30  frob
  File created and added to cvs


*/


package org.tgdb.fileimport;


/**
 * The FileParserException is thrown whenever an error occours when the
 * FileParser object is accessed.
 *
 * @author frob
 * @see Exception
 */
class FileParserException extends Exception
{

   /**
    * Creates a new FileParserException instance.
    *
    */
   public FileParserException()
   {
      this("FileParserException");
   }


   /**
    * Creates a new FileParserException instance with a descriptive text.
    *
    * @param errorMsg The text to be displayed with the exception.
    */
   public FileParserException(String errorMsg)
   {
      super(errorMsg);
   }
}
