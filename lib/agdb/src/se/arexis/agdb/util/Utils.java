/*
  $Log$
  Revision 1.2  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.1.1.1  2002/10/16 18:14:07  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.1  2001/06/13 05:59:29  frob
  Initial checkin.

*/

package se.arexis.agdb.util;


/**
 * A utilities class with common helper methods which doesn't fit anywhere else.
 *
 * @author frob
 * @see Object
 */
public class Utils extends Object
{

   //////////////////////////////////////////////////////////////////////
   //
   // Public section
   //
   //////////////////////////////////////////////////////////////////////


   /**
    * Checks if an object is assigned a value (ie is not null) or not. 
    *
    * @param object The object to test.
    * @return True if object is assigned a value.
    *         False if object is null.
    */
   public static boolean assigned(Object object)
   {
      return object != null;
   }


   /**
    * Checks if a string is assigned a value, ie the string is not a blank
    * string. This method does NOT check if  the string is null, it only
    * checks if the string is blank or not.
    *
    * @param string The string to test.
    * @return True if string is blank.
    *         False if string has a value.
    */
   public static boolean blank(String string)
   {
      return string.length() == 0;
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
