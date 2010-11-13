/*
  $Log$
  Revision 1.2  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.1.1.1  2002/10/16 18:14:06  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.4  2001/06/18 09:05:49  frob
  Modified the class, it is no longer a bundle, it is an ordinarly class.

  Revision 1.3  2001/06/13 13:07:36  frob
  Checked in wrong version in previous checkin, this is the correct one :-P

  Revision 1.1  2001/05/28 14:36:58  frob
  Initial checkin.

*/

package se.arexis.agdb.util;

import java.util.*;

/**
 * Defaults is a class with default values. 
 *
 * @author frob
 * @see Object
 */
public class Defaults extends Object
{
   static final public String ACTION_FIRST = "<<";
   static final public String ACTION_PREV = "<";
   static final public String ACTION_NEXT = ">";
   static final public String ACTION_LAST = ">>";
   static final public String ACTION_COUNT = "Count";
   static final public String ACTION_DISPLAY = "Display";
   
   static final public String ORDERBY_NAME_TEXT = "Name";
   static final public String ORDERBY_NAME_SQL = "NAME";
   static final public String ORDERBY_COMMENT_TEXT = "Comment";
   static final public String ORDERBY_COMMENT_SQL = "COMM";
   static final public String ORDERBY_INDIVIDUALS_TEXT = "Individuals";
   static final public String ORDERBY_INDIVIDUALS_SQL = "INDS";
   static final public String ORDERBY_USER_TEXT = "User";
   static final public String ORDERBY_USER_SQL = "USR";
   static final public String ORDERBY_TS_TEXT = "Updated";
   static final public String ORDERBY_TS_SQL = "TS";
   
   static final public String DEFAULT_SUID = "-1";
   static final public String DEFAULT_GSID = "-1";
   static final public String DEFAULT_GID = "-1";
   static final public String DEFAULT_STARTINDEX = "1";
   static final public String DEFAULT_ORDERBY = ORDERBY_NAME_SQL;
   static final public String DEFAULT_ACTION = "";
   static final public String DEFAULT_BUTTONWIDTH = "100";
      
   static final public String BACKGROUND_COLOR = "#FDF5E6";
   static final public String TABLEHEADER_COLOR = "#008B8B";
   static final public String MIDDLE_SELECTED_COLOR = "#8B4513";
   
   

   //////////////////////////////////////////////////////////////////////
   //
   // Public section
   //
   //////////////////////////////////////////////////////////////////////



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
