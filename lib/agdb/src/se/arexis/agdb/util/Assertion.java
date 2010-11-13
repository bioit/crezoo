/*
  $Log$
  Revision 1.3  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.2  2002/10/18 11:41:20  heto
  Replaced Assertion.assert with Assertion.assertMsg

  Java 1.4 have a keyword "assert".

  Revision 1.1.1.1  2002/10/16 18:14:06  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.2  2001/04/24 06:31:45  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.2.2  2001/03/27 13:03:15  frob
  Added a String parameter in the assertion method. This makes it possible to throw a meaningfull exception from within the method.

  Revision 1.1.2.1  2001/03/21 09:08:32  frob
  Created the Assertion class together with its related exception, AssertionException


*/

package se.arexis.agdb.util;

/**
 * The Assertion class is used to assert a statement. 
 *
 * @author frob
 */
public class Assertion
{

   /**
    * Creates a new Assertion instance. Should never be run!
    *
    */
   Assertion()
   {
   }


   /**
    * Asserts a value. If the given value is false, the assertion fails and
    * a AssertionException is raised.
    *
    * @param assertValue The value to examine.
    * @param message The text to be included in the exception
    * @exception AssertionException if given value is false.
    */
   public static void assertMsg(boolean assertValue, String message)
      throws AssertionException 
   {
      if (!assertValue)
      {
         if (message == null || message.length() == 0)
         {
            throw new AssertionException("Failed assertion");
         }
         else
         {
            throw new AssertionException(message);
         }
      }
   }
}

   
