/*
  $Log$
  Revision 1.2  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.1.1.1  2002/10/16 18:14:06  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.3  2001/04/24 09:34:11  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:45  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.2.1  2001/03/21 09:08:32  frob
  Created the Assertion class together with its related exception, AssertionException


*/


package org.tgdb.fileimport;

/**
 * The AssertionException is thrown whenever an assertion fails.
 *
 * @author frob
 * @see Exception
 */
public class AssertionException extends Exception
{

   /**
    * Creates a new AssertionException instance.
    *
    */
   public AssertionException()
   {
      this("AssertionException");
   }


   /**
    * Creates a new AssertionException instance with a descriptive message.
    *
    * @param errorMsg The message to display with the exception.
    */
   public AssertionException(String errorMsg)
   {
      super(errorMsg);
   }
}
