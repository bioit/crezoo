/*
  $Log$
  Revision 1.4  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.3  2003/04/25 12:14:46  heto
  Changed all references to axDefault.css
  Source layout fixes.

  Revision 1.2  2003/04/25 09:11:14  heto
  Error page function added

  Revision 1.1.1.1  2002/10/16 18:14:07  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.13  2001/06/18 09:06:30  frob
  Some new methods, also adopted to changes in Defaults.java.

  Revision 1.12  2001/06/13 09:30:23  frob
  Modified interfact of comment method in HTMLWriter, caused updates in several files.

  Revision 1.11  2001/06/13 06:06:26  frob
  Changed the structure of the header table produced in HTMLWriter. From now, the table
  has only two rows. Any other stuff has to be placed within a content table (also
  produced byt the HTMLWriter). This modification caused updates in several servlets.

  Revision 1.10  2001/05/31 07:07:16  frob
  Implemented the writeBottomDefault method in HTMLWriter and removed it from all
  servlets. The servlets now uses the method in HTMLWriter.

  Revision 1.9  2001/05/30 09:19:22  frob
  Rewrote the statistics part of viewProj. Fixed some CSS stuff in HTMLWriter.
  Some keys added to Defaults.properties and Errors.properties

  Revision 1.8  2001/05/28 14:37:23  frob
  New methods, restructured.

  Revision 1.7  2001/05/28 06:32:28  frob
  Restructuring, some new elements.

  Revision 1.6  2001/05/22 06:17:08  roca
  Backfuncktionality fixed for all (?) servlets/subpages

  Revision 1.5  2001/05/21 07:03:41  frob
  Changed debug text in Header table to be consistent. Renamed mainTable to contentTable.

  Revision 1.4  2001/05/18 06:18:07  frob
  New method for writing frameset doctype.

  Revision 1.3  2001/05/17 08:09:40  frob
  Added comment method + methods for navigator page.

  Revision 1.2  2001/05/15 12:15:55  frob
  Moved the default CSS part to a serparate method.

  Revision 1.1  2001/04/27 12:44:49  frob
  Initial checkin.

*/

package se.arexis.agdb.util;


import java.io.*;
import java.util.*;

/**
 * HTMLWriter is a class that helps to write HTML code. It contains several
 * methods which will make it easier to generate HTML code from the
 * application. 
 *
 * <P>All tables starts with a header table which includes the title of the
 * page. The table may also includ additional information such as buttons,
 * text etc. The header table should be created by calling the headerTable
 * method. 
 *
 * <P>Most pages then continue with a content table which holds the rest of
 * the elements of the page. This means that all elements on a page should
 * be positioned within the content table. There are two ways to create the
 * content table. Either use the contentTable method, and pass the contents
 * of the page as a parameter. Or use the methods contentTableStart and
 * contentTableEnd to start/finish the table. The first alternative is
 * suitable when few elements are used on the page. If there are many items
 * on the page, the second alternative is better.
 *
 * @author frob
 * @see Object
 */
public class HTMLWriter extends Object
{

   //////////////////////////////////////////////////////////////////////
   //
   // Public section
   //
   //////////////////////////////////////////////////////////////////////
   
    
   /**
    * Write a message to the webpage
    */
   static public void writeErrorPage(PrintWriter out, String head, String message)
   {
       out.println("<p><b>" + head + "</b><br>\n"+message+"\n</p>\n");
   }

   /**
    * Writes a DOCTYPE tag.
    *
    * @param out The PrintWriter to write to
    */
   static public void doctype(PrintWriter out)
   {
      out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 " +
                  "Transitional//EN\"");
      out.println(" \"http://www.w3.org/TR/html4/loose.dtd\">");
   }

   
   /**
    * Writes a frameset DOCTYPE tag.
    *
    * @param out The PrintWriter to write to.
    */
   static public void framesetDoctype(PrintWriter out)
   {
      out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 "
                  + "Frameset//EN\"\n"
                  + "  \"http://www.w3.org/TR/REC-html40/frameset.dtd\">");
   }


   
   /**
    * Writes a comment tag to the PrintWriter.
    *
    * @param out The PrintWriter to write to.
    * @param comment The comment to write.
    * @param NLbefore If set to true, a new line is written before the text.
    * @param NLafter If set to true, a new line is written after the text.
    */
   static public void comment(PrintWriter out,
                              String comment,
                              boolean NLbefore,
                              boolean NLafter)
   {
      if (NLbefore)
      {
         out.print("\n");
      }
      out.println("<!-- " + comment + " -->");
      if (NLafter)
      {
         out.print("\n");
      }
   }
   

   /**
    * Opens the HTML tag.
    *
    * @param out The PrintWriter to write to.
    */
   static public void openHTML(PrintWriter out)
   {
      out.println("<HTML>");
   }

   
   /**
    * Closes the HTML tag.
    *
    * @param out The PrintWriter to write to.
    */
   static public void closeHTML(PrintWriter out)
   {
      out.println("</HTML>");
   }

   
   /**
    * Opens the HEAD tag and writes a title tag.
    *
    * @param out The PrintWriter to write to.
    * @param pageTitle The title of the page.
    */
   static public void openHEAD(PrintWriter out,
                               String pageTitle)
   {
      out.println("<HEAD>");
      out.println("  <TITLE>" + pageTitle + "</TITLE>");
   }

   
   /**
    * Closes the HEAD tag.
    *
    * @param out The PrintWriter to write to.
    */
   static public void closeHEAD(PrintWriter out)
   {
      out.println("</HEAD>");
   }

   
   /**
    * Opens the BODY tag.
    *
    * @param out The PrintWriter to write to.
    * @param bodyParams Additional parameters to the body tag.
    */
   static public void openBODY(PrintWriter out,
                               String bodyParams)
   {
      out.println("<BODY " + bodyParams + " >");
   }

   
   /**
    * Closes the BODY tag.
    *
    * @param out The PrintWriter to write to.
    */
   static public void closeBODY(PrintWriter out)
   {
      out.println("</BODY>");
   }


   /**
    * Prints the default CSS settings to the given print writer.
    *
    * @param out The PrintWrite to write to.
    */
   static public void defaultCSS(PrintWriter out)
   {
      out.println("  <STYLE TYPE=\"text/css\">\n" +
                  "    BODY\n" +
                  "    {\n"+
                  "      BACKGROUND-COLOR: " + Defaults.BACKGROUND_COLOR + ";\n" +
                  "      COLOR: black;\n" +
                  "      FONT-FAMILY: Arial, Helvetica, Verdana;\n" +
                  "      FONT-SIZE: 10pt;\n" +
                  "    }\n" +
                  "\n" +
                  "    H1\n" +
                  "    {\n"+
                  "      COLOR: black;\n" +
                  "      FONT-FAMILY: Arial, Helvetica, Verdana;\n" +
                  "      FONT-SIZE: 18pt;\n" +
                  "      FONT-WEIGHT: bolder;\n" +
                  "    }\n" +
                  "\n" +
                  "    H2\n" +
                  "    {\n"+
                  "      COLOR: black;\n" +
                  "      FONT-FAMILY: Arial, Helvetica, Verdana;\n" +
                  "      FONT-SIZE: 14pt;\n" +
                  "      FONT-WEIGHT: bolder;\n" +
                  "    }\n" +
                  "\n" +
                  "    H3\n" +
                  "    {\n"+
                  "      COLOR: black;\n" +
                  "      FONT-FAMILY: Arial, Helvetica, Verdana;\n" +
                  "      FONT-SIZE: 12pt;\n" +
                  "    }\n" +
                  "\n" +
                  "    H4\n" +
                  "    {\n"+
                  "      COLOR: black;\n" +
                  "      FONT-FAMILY: Arial, Helvetica, Verdana;\n" +
                  "      FONT-SIZE: 10pt;\n" +
                  "      FONT-WEIGHT: bolder;\n" +
                  "    }\n" +
                  "\n" +
                  "    TD\n" +
                  "    {\n" +
                  "      COLOR: black;\n" +
                  "      FONT-FAMILY: Arial, Helvetica, Verdana;\n" +
                  "      FONT-SIZE: 10pt;\n" +
                  "      FONT-WEIGHT: bold;\n" +
                  "    }\n" +
                  "\n" +
                  "    INPUT\n" +
                  "    {\n" +
                  "      FONT-FAMILY: Arial, Helvetica, Verdana;\n" +
                  "      FONT-SIZE: 9pt;\n" +
                  "      FONT-WEIGHT: normal;\n" +
                  "    }\n" +
                  "\n" +
                  "    SELECT\n" +
                  "    {\n" +
                  "      FONT-FAMILY: Arial, Helvetica, Verdana;\n" +
                  "      FONT-SIZE: 9pt;\n" +
                  "      FONT-WEIGHT: normal;\n" +
                  "      HEIGHT: 22px;\n" +
                  "    }\n" +
                  "\n" +
                  "    OPTION\n" +
                  "    {\n" +
                  "      FONT-FAMILY: Arial, Helvetica, Verdana;\n" +
                  "      FONT-SIZE: 9pt;\n" +
                  "      FONT-WEIGHT: normal;\n" +
                  "    }\n" +
                  "  </STYLE>");
   }
   
 
   /**
    * Writes the CSS section used on the navigator page.
    *
    * @param out The PrintWriter object to write to.
    */
   static public void navigatorCSS(PrintWriter out)
   {
      out.println("  <STYLE TYPE=\"text/css\">\n" +
                  "    A\n" +
                  "    {\n" +
                  "      TEXT-DECORATION: none;\n" +
                  "      COLOR: white;\n" +
                  "    }\n" +
                  "\n" +
                  "    TD\n" +
                  "    {\n" +
                  "      FONT-FAMILY: Verdana, Helvetica;\n" +
                  "      FONT-SIZE: 10pt;\n" +
                  "      COLOR: white;\n" +
                  "    }\n" +
                  "\n" +
                  "    BODY\n" +
                  "    {\n" +
                  "      BACKGROUND-COLOR: white;\n" +
                  "      FONT-WEIGHT: bolder;\n" +
                  "    }\n" +
                  "  </STYLE>");
   }


   /**
    * Writes the CSS for middle frames to the given PrintWriter.
    *
    * @param out The PrintWriter to write to.
    */
   static public void middleFrameCSS(PrintWriter out)
   {
      out.println("  <STYLE TYPE=\"text/css\">\n" +
                  "    BODY\n" +
                  "    {\n" +
                  "      BACKGROUND-COLOR: " + Defaults.BACKGROUND_COLOR + ";\n" +
                  "      FONT-FAMILY: Verdana;\n" +
                  "      FONT-SIZE: 10pt;\n" +
                  "    }\n" +
                  "\n" +
                  "    TABLE\n" +
                  "    {\n"+
                  "      FONT-FAMILY: Verdana;\n" +
                  "      FONT-SIZE: 8pt;\n" +
                  "      MARGIN-RIGHT: 0px;\n" +
                  "      MARGIN-LEFT: 2px;\n" +
                  "      PADDING-RIGHT: 0px;\n" +
                  "    }\n" +
                  "\n" +
                  "    TD\n" +
                  "    {\n" +
                  "      FONT-FAMILY: Verdana;\n" +
                  "      FONT-SIZE: 8pt;\n" +
                  "    }\n" +
                  "\n" +
                  "    A\n" +
                  "    {\n" +
                  "      COLOR: " + Defaults.BACKGROUND_COLOR + ";\n" +
                  "      FONT-FAMILY: Verdana;\n" +
                  "      FONT-SIZE: 8pt;\n" +
                  "      TEXT-DECORATION: none;\n" +
                  "    }\n" +
                  "\n" +
                  "  </STYLE>");
   }


   static public void bottomFrameCSS(PrintWriter out)
   {
      out.println("  <STYLE TYPE=\"text/css\">\n" +
                  "    BODY\n" +
                  "    {\n" +
                  "      BACKGROUND-COLOR: " + Defaults.BACKGROUND_COLOR + ";\n" +
                  "      FONT-FAMILY: Verdana;\n" +
                  "      FONT-SIZE: 10pt;\n" +
                  "    }\n" +
                  "\n" +
                  "    TABLE\n" +
                  "    {\n"+
                  "      FONT-FAMILY: Verdana;\n" +
                  "      FONT-SIZE: 8pt;\n" +
                  "      MARGIN-RIGHT: 0px;\n" +
                  "      MARGIN-LEFT: 2px;\n" +
                  "      PADDING-RIGHT: 0px;\n" +
                  "    }\n" +
                  "\n" +
                  "    TD\n" +
                  "    {\n" +
                  "      FONT-FAMILY: Verdana;\n" +
                  "      FONT-SIZE: 8pt;\n" +
                  "    }\n" +
                  "\n" +
                  "  </STYLE>");
   }
   
   
   /**
    * Writes a link to a CSS.
    *
    * @param out The PrintWriter to write to.
    * @param cssURL The URL to the CSS to use.
    */
   static public void css(PrintWriter out, String cssURL)
   {
      out.println("  <LINK rel=\"stylesheet\" type=\"text/css\" href=\"" +
                  cssURL + "\">");
   }

   /**
    * Generates HTML to create a button with a onClick event.
    *
    * @param buttonName The name (value) of the button.
    * @param eventString The event to be linked with the button.
    * @param buttonWidth The width of the button.
    * @return The generated HTML.
    */
   static public String button(String buttonName, String eventString,
                               int buttonWidth)
   {
      return "<INPUT type=button value=" + buttonName +
         " style=\"WIDTH: " + Integer.toString(buttonWidth) + "px\" " +
         "onClick='" + eventString + "'>";
   }


   /**
    * Generates HTML to create a Back button linked to a onClick event.
    *
    * @param eventString The event to be linked to the button.
    * @return The generated HTML.
    */
   static public String backButton(String eventString)
   {
      return button("Back", eventString,
                    Integer.parseInt(Defaults.DEFAULT_BUTTONWIDTH)); 
   }


   /**
    * Writes the topmost table on a page. The table has the following
    * layout:
    *
    *  +---+-----------------------------------+
    *  |   |          pageTitle                |
    *  |   +-----------------------------------+
    *  |   |                                   |
    *  +---+-----------------------------------+
    *  
    * @param out The PrintWriter to write to.
    * @param borderWidth The width of the table border.
    * @param pageTitle The text to be used as pageTitle.
    */
   static public void headerTable(PrintWriter out, int borderWidth,
                                  String pageTitle)
   {
      comment(out, "++++++++++ Header table method, start ++++++++++",
              true, false);
      out.println("<TABLE width=846 border=" + 
                  Integer.toString(borderWidth) + " cellspacing=0 cellpadding=0>");
      comment(out, "Header table, r1", true, false);
      out.println("  <TR>");
      out.println("    <TD width=14 rowspan=2></TD>");
      out.println("    <TD width=736 height=15>\n" +
                  "      <CENTER>\n" +
                  "      <B style=\"font-size: 15pt\">" + pageTitle +
                  "</B>\n" + 
                  "      </CENTER>\n" +
                  "    </TD>");
      out.println("  </TR>");

      comment(out, "Header table, r2", true, false);
      out.println("  <TR>");
      out.println("    <TD height=2 bgcolor=\"#008B8B\">&nbsp;</TD>");
      out.println("  </TR>");
      out.println("</TABLE>");
      comment(out, "++++++++++ Header table method, end ++++++++++", false,
              false);
   }
   
   
   /**
    * Writes the content table on a page. This table should be placed after
    * the header table. The table has the following structure:
    * <P>
    *  +---+-----------------------------------+
    *  |   |          pageContent              |
    *  +---+-----------------------------------+
    * <P>
    *
    * @param out The PrintWriter to write to.
    * @param borderWidth The border width of the table.
    * @param pageContent The text to be used as pageContent.
    */
   static public void contentTable(PrintWriter out, int borderWidth,
                                   String pageContent)
   {
      contentTableStart(out, borderWidth);
      out.println(pageContent);
      contentTableEnd(out);
   }


   /**
    * Opens the TABLE tag used for the content table. Opens the table,
    * opens the first (and only) row. Writes the first column which is
    * empty and opens the second column which is where the content elements
    * should be printed.
    *
    * @param out The PrintWriter to write to.
    * @param borderWidth The border of the table.
    */
   static public void contentTableStart(PrintWriter out, int borderWidth)
   {
      comment(out, "++++++++++ contentTableStart, start ++++++++++", true,
              false); 
      out.println("<TABLE border= " + Integer.toString(borderWidth) +
                  " cellspacing=0 cellpadding=0>");
      out.println("  <TR>\n" +
                  "    <TD width=20></TD>\n" +
                  "    <TD>\n" +
                  "    <!-- ====== pageContent start ====== -->\n");
   }


   /**
    * Closes the TABLE tag used for the content table. Closes the second
    * column on the row an closes the row. Finally, the table is closed.
    *
    * @param out The PrintWriter to write to.
    */
   static public void contentTableEnd(PrintWriter out)
   {
      out.println("");
      out.println("    <!-- ====== pageContent end ====== -->\n" +
                  "    </TD>\n" +
                  "  </TR>\n" +
                  "</TABLE>");
      out.println("<!-- ++++++++++ contentTableEnd, end ++++++++++ -->\n");
   }


   /**
    * Writes the start of the top table, ie the table used in the top
    * frame. Opens the TABLE and TR tags
    *
    * @param out The PrintWriter to write to.
    * @param borderWidth The width of the table border.
    */
   static public void topTableStart(PrintWriter out, int borderWidth)
   {
      comment(out, "++++++++++ Top table, start ++++++++++", true, false);
      out.println("<TABLE width=800 border=" + borderWidth +
                  " cellpadding=0 cellspacing=0>\n" +
                  "<TR>\n");
   }


   /**
    * Writes the end of the top table. Closes the TR and TABLE tags. 
    *
    * @param out The PrintWriter to write to.
    */
   static public void topTableEnd(PrintWriter out)
   {
      out.println("</TR>\n" +
                  "</TABLE>");
      comment(out, "++++++++++ Top table, end ++++++++++", false, true);
   }
   
      
   /**
    * Writes a selected navigator text.
    *
    * @param out The PrintWriter to write to.
    * @param requestedPage The page to display when the link is clicked. 
    * @param linkText The text to display as the link.
    */
   static public void navigatorSelected(PrintWriter out,
                                        String requestedPage,
                                        String linkText)
   {
      out.println("          <TD width=\"11%\">\n" +
                  "            <A HREF=\"mainPage?PAGE=" +
                  requestedPage + "\" target=_top>\n" +
                  "              <FONT color=\"#00CED1\">\n" +
                  "                <B>" + linkText + "</B>\n" +
                  "              </FONT>\n" +
                  "            </A>\n" +
                  "          </TD>");
   }


   /**
    * Writes a non selected navigator text.
    *
    * @param out The PrintWriter to write to.
    * @param requestedPage The page to display when the link is clicked. 
    * @param linkText The text to display as the link.
    */
   static public void navigatorNotSelected(PrintWriter out,
                                           String requestedPage,
                                           String linkText)
   {
      out.println("          <TD width=\"11%\" >\n" +
                  "            <A HREF=\"mainPage?PAGE=" +
                  requestedPage + "\" target=_top>\n" +
                  "              <B>" + linkText + "</B>\n" +
                  "            </A>\n" +
                  "          </TD>");
   }
   

   /**
    * Writes the opening TABLE tag for the details table.
    *
    * @param out The PrintWriter to write to.
    * @param borderSize The size of the table border.
    */
   static public void openDetailsTable(PrintWriter out,
                                       int borderSize)
   {
      comment(out, "++++++++++ Detatils table, start ++++++++++", true, false);
      out.println("<TABLE width=\"100%\" border=" + borderSize + ">");
   }


   /**
    * Writes the static table on a separate row in the details table. 
    *
    * @param out The PrintWriter to write to.
    * @param data[][] The data to print.
    * @param borderSize The size of the table border.
    */
   static public void detailsStaticDataTable(PrintWriter out,
                                             String data[][],
                                             int borderSize)
   {
      out.println("<TR>\n" +
                  "  <TD>");
      comment(out, "++++++++++ Details static data table, start ++++++++++",
              true, false); 
      out.println("    <TABLE border= " + borderSize + " cellSpacing=3>\n" +
                  "    <TR>\n" +
                  "      <TD nowrap colspan=2 bgcolor=lightgrey>\n" +
                  "        <FONT size=\"+1\">Static data</FONT>\n" +
                  "      </TD>\n" +
                  "    </TR>");
            
      for (int i = 0; i < data.length; i ++)
      {
         out.println("    <TR>\n" +
                     "      <TD nowrap>" + data[i][0] + ": </TD>\n" +
                     "      <TD nowrap>" + data[i][1] + "</TD>\n" +
                     "    </TR>");
      }
      
      out.println("    </TABLE>");
      out.println("<!-- ++++++++++ Details, static data table, end ++++++++++ -->\n");
      out.println("  </TD>\n" +
                  "</TR>\n");
   }


   /**
    * Writes the data table on a separate row in the detail table.
    *
    * @param out The PrintWriter to write to.
    * @param columnTitles The column titles to use.
    * @param dataVector The data to print.
    * @param borderSize The size of the table border.
    */
   static public void detailsDataTable(PrintWriter out,
                                       String[] columnTitles,
                                       Vector dataVector,
                                       int borderSize)
   {
      out.println("\n<TR>\n" +
                  "  <TD>");
      
      comment(out, "++++++++++ Details data table, start ++++++++++", true,
              false);
      out.println("    <TABLE align=center border=" + borderSize +
                  " cellSpacing=0 width=800px>");
      
      comment(out, "Data table, r1: table header", true, false);
      out.println("    <TR bgcolor=Black>\n" +
                  "      <TD align=center colspan=10 nowrap>\n" +
                  "        <FONT color=\"#ffffff\">Current Data</FONT>\n" +
                  "      </TD>\n" +
                  "    </TR>");

      comment(out, "Data table, r2: column headers", true, false);
      out.println("    <TR bgcolor= \"#008B8B\">");
      for (int i = 0; i < columnTitles.length; i++)
      {
         out.println("      <TD nowrap>" + columnTitles[i] + "</TD>");
      }
      out.println("    </TR>");

      // Loop all data in the dataVector and print it
      String[] currentData, previousData;
      comment(out, "Data table, r3: current data", true, false);
      for (int dataRow = 0; dataRow < dataVector.size(); dataRow++)
      {
         // Get the data at the current row and remove any null values. 
         currentData = (String[]) dataVector.elementAt(dataRow);
         currentData = clearNullDetailData(currentData);
         
         // If there is previous data, get it. Also clear any null values. 
         if (dataRow + 1 < dataVector.size())
         {
            previousData = (String[]) dataVector.elementAt(dataRow + 1);
            previousData = clearNullDetailData(previousData);
         }
         else
         {
            previousData = null;
         }
         
         // Write the detail row
         writeDetailRow(out, currentData, previousData, dataRow);

         // If this is the first row, write the history header
         if (dataRow == 0)
         {
            comment(out, "Data table, r4: history header", true, false);
            out.println("    <TR bgcolor=Black>\n" +
                        "      <TD align=center colspan=10>\n" +
                        "        <FONT color=\"#ffffff\">History</FONT>\n" +
                        "      </TD>\n" +
                        "    </TR>");
            comment(out, "Data table, r5 - rx: history data", true, false);
         }
      }
      out.println("    </TABLE>");
      out.println("<!-- ++++++++++ Details data table, end ++++++++++ -->\n");
      out.println("  </TD>\n" +
                  "</TR>\n");
   }


   /**
    * Writes a buttons table on a separate row in the detatails table.
    *
    * @param out The PrintWriter to write to.
    * @param backButtonOnClick The event string to be used with the back
    *                          button. 
    * @param additionalHTML Any additional HTML to be written in the
    *                       table. 
    * @param borderSize The size of the table border.
    */
   public static void detailsButtonTable(PrintWriter out,
                                         String backButtonOnClick,
                                         String additionalHTML,
                                         int borderSize)
   {
      out.println("\n<TR>\n" +
                  "  <TD>");
      comment(out, "++++++++++ Details button table, start ++++++++++",
              true, false); 
      out.println("    <TABLE cellspacing=0 cellpadding=0 border=" +
                  borderSize + ">\n" +
                  "    <TR>\n" +
                  "      <TD>\n" + 
                  "        <FORM action=\"\">\n" +
                  backButton(backButtonOnClick) + "\n" +
                  additionalHTML + "\n" +
                  "        </FORM>\n" +
                  "      </TD>\n" +
                  "    </TR>\n" +
                  "    </TABLE>");
      comment(out, "++++++++++ Details button table, end ++++++++++",
              false, true);  
      out.println("  </TD>\n" +
                  "</TR>");
   }
   
   
   /**
    * Writes an emtpy table row.
    *
    * @param out The PrintWriter to write to.
    */
   static public void emptyTableRow(PrintWriter out)
   {
      out.println("<TR>\n" +
                  "  <TD>&nbsp</TD>\n" +
                  "</TR>");
   }


   static public void writeBottomDefault(PrintWriter out)
   {
      doctype(out);
      openHEAD(out, "");
      defaultCSS(out);
      closeHEAD(out);
      openBODY(out, "");
      
      out.println("<TABLE width=700>\n" +
                  "<TR>\n" +
                  "  <TD>\n" +
                  "    <CENTER><H4>Nothing selected</H4></CENTER>\n" +
                  "  </TD>\n" +
                  "</TR>\n" +
                  "</TABLE>");
      closeBODY(out);
      closeHTML(out);
   }


   /**
    * Writes the start of a top page. This includes the doctype, the
    * header, the CCS. Furhtermore, the body tag is opened as well as the
    * form tag.
    *
    * @param out The PrintWriter to write to.
    * @param servletPath The path to use with the action parameter in the
    *                    form tag.
    */
   static public void startTopPage(PrintWriter out, String servletPath)
   {
         // Write the start of the page.
      doctype(out);
      openHTML(out);
      openHEAD(out, "");
      defaultCSS(out);

      // Any new pages should be placed in a frame named "content",
      // defined by the frameset in mainPage
      out.println("<BASE target=\"content\">");
      closeHEAD(out);
      comment(out, "", true, false);

      // Start writing the body. 
      openBODY(out, "");
      out.println("<FORM method=get action=\"" + servletPath + "\">"); 
   }


   /**
    * Writes the start of the middle page. Writes the doctype, header, CSS,
    * opens the body and writes the info line.
    *
    * @param out The PrintWriter to write to.
    * @param action The current action.
    * @param startIndex The index of the first item to display.
    * @param itemsToDisplay Total number of items to display.
    * @param maxRows Max number of rows on each page.
    */
   static public void startMiddlePage(PrintWriter out, String action,
                                      int startIndex, int itemsToDisplay,
                                      int maxRows) 
   {
      doctype(out);
      openHEAD(out, "");
      middleFrameCSS(out);
      out.println("  <BASE target=\"content\">");
      closeHEAD(out);
      openBODY(out, "");
      out.println("&nbsp;" + buildInfoLine(action, startIndex, itemsToDisplay,
                                           maxRows));  
   }


   /**
    * Opens the table used in the middle page. Opens the table, the first
    * row and writes an emtpy first column.
    *
    * @param out The PrintWriter to write to.
    * @param borderWidth The size of the border of the table.
    * @param tableWidth The width of the table.
    */
   static public void startMiddlePageTable(PrintWriter out,
                                           int borderWidth, int tableWidth) 
   {
      out.println("<TABLE bgcolor=\"" + Defaults.TABLEHEADER_COLOR +
                  "\" border=" + borderWidth + " cellpadding=0" +
                  " cellspacing=0 width=" + tableWidth + ">\n" +
                  "<TR>");
      out.println("  <TD width=5 height=20></TD>");
   }


   /**
    * Writes a column header in the middle table.
    *
    * @param out The PrinWriter to write to.
    * @param columnWidth The width of the current colum.
    * @param servletPath The path to the servlet to be realted with the
    *                    link on the column header.
    * @param queryString The query string to add to the servlet path. 
    * @param currentOrderBy The current value of the orderBy parameter. 
    * @param columnText The text to be displayed in the header.
    * @param newOrderBy The orderBy value to use in the query string. 
    */
   public static void writeMiddleDetailsHeader(PrintWriter out,
                                               int columnWidth, 
                                               String servletPath,
                                               String queryString, 
                                               String currentOrderBy,
                                               String columnText,
                                               String newOrderBy)
   {
      // Start writing the column. The column will contain a HREF element
      // with ACTION and ORDERBY parameter.
      out.println("  <TD width=" + columnWidth + ">\n" +
                  "    <A HREF=\"" +
                  servletPath + "?ACTION=" +
                  Defaults.ACTION_DISPLAY + "&" +  
                  queryString + "&ORDERBY=" + newOrderBy + "\">");
      
      if (newOrderBy.equals(currentOrderBy))
      {
         out.println("      <FONT color=\"" +
                     Defaults.MIDDLE_SELECTED_COLOR + "\">\n" +
                     "        <B>" + columnText + "</B>\n" +
                     "      </FONT>\n" + 
                     "    </A>\n" +
                     "  </TD>");
      }
      else
      {
         out.println("      " + columnText + "\n" +
                     "    </A>\n" +
                     "  </TD>");
      }
   }


   static public void startBottomPage(PrintWriter out, int borderWidth) 
   {
      doctype(out);
      openHTML(out);
      openHEAD(out, "");
      bottomFrameCSS(out);
      closeHEAD(out);
      openBODY(out, "");
      out.println("<TABLE border=0 cellpadding=0 cellspacing=0 width=" +
                  borderWidth + ">");
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
    * Replaces any null elements in the array with blanks ("").
    *
    * @param detailRow The array to modify.
    * @return The modified array.
    */
   static private String[] clearNullDetailData(String[] detailRow)
   {
      for (int col = 0; col < detailRow.length; col++)
      {
         if (detailRow[col] == null)
         {
            detailRow[col] = "";
         }
      }
      return detailRow;
   }

   
   /**
    * Writes a row in the detail table. Current data is compared to the
    * previous data and any modified value will be displayed in red to
    * highlihgt the modification.
    *
    * @param out The PrintWriter to write to.
    * @param currentData An array with current data.
    * @param previousData An array with previous data.
    * @param rowNumber The current row of data.
    */
   static private void writeDetailRow(PrintWriter out, String[] currentData, 
                                      String[] previousData, int rowNumber)  
   {
      // Set background depending on row number
      if (rowNumber == 0 || rowNumber == 1 || rowNumber % 2 != 0)
      {
         out.println("    <TR bgcolor=white>");
      }
      else
      {
         out.println("    <TR bgcolor=lightgrey>");
      }

      // No current data given, this is a serious error!
      if (currentData == null)
      {
         Errors.logError("HTMLWriter.writeDetailRow: currentData is null!");
      }

      // No previous data given, means this is the last row to write
      else if (previousData == null)
      {
         for(int cols = 0; cols < currentData.length; cols++)
         {
            writeDetailCell(out, true, currentData[cols]);
         }
      }
      
      // Both current and previous data given
      else 
      {
         String currentDataCol; // Holds data from the current column.
         boolean unmodifiedValue; // Is value unmodified since previous version?

         // Loop columns in current data 
         for (int col = 0; col < currentData.length; col++)
         {
            currentDataCol = currentData[col];

            // If this is the last column of data (which is the time
            // stamp), indicate that the value should be printed as
            // unmodified as this value will always change. 
            if ((col + 1) == currentData.length)
            {
               unmodifiedValue = true;
            }

            // This is not the timestampt. Compare current value with
            // previous value and set unmodifiedValue.
            else
            {
               // There are some history date
               unmodifiedValue =
                  currentDataCol.equalsIgnoreCase(previousData[col]);
            }

            // Write the data cell
            writeDetailCell(out, unmodifiedValue, currentDataCol);
         }
      }
      out.println("    </TR>"); 
   }


   /**
    * Writes one cell of data in the detail table.
    *
    * @param out The PrintWriter to write to.
    * @param unmodifiedValue Is value unmodified or not?
    * @param cellValue The value of the cell.
    */
   static private void writeDetailCell(PrintWriter out,
                                       boolean unmodifiedValue,
                                       String cellValue)
   {

      if (cellValue == null || cellValue == "")
      {
         cellValue = "&nbsp;";
      }
         
      // If value is modified, add FONT-tag to the value.
      if (!unmodifiedValue)
      {
         cellValue = "\n        <FONT color=red>" + cellValue +
            "</FONT>\n      ";
      }
         
      out.println("      <TD nowrap>" + cellValue + "</TD>");
   }

   
   /**
    * Builds the infoline to be displayed in the middle frame. The line
    * contains information about the object to be displayed in the bottom frame.
    *
    * @param requestedAction The current action.
    * @param startIndex The index to start with.
    * @param rows Total number of rows to display.
    * @param maxRows Maximum number of rows to display on one page.
    * @return The info line.
    */
   private static String buildInfoLine(String requestedAction,
                                       int startIndex, 
                                       int rows, int maxRows) 
   {
      String resultString = null;
      
      // Calculate the upper limit, ie the index of the last item to
      // display 
      int upperLimit = startIndex + maxRows - 1;

      // If upperlimit to high, adjust it
      if (upperLimit > rows)
      {
         upperLimit = rows;
      }

      // If no rows to display, adjust startIndex
      if (rows == 0)
      {
         startIndex = 0;
      }

      if (!Utils.assigned(requestedAction) || Utils.blank(requestedAction))
      {
         resultString = "&nbsp;";
      }
      
      // Build the result string by calculating the row numbers to
      // display. Is done differently depending on the requested action
      else if (requestedAction.equals(Defaults.ACTION_FIRST) ||
               requestedAction.equals(Defaults.ACTION_PREV) ||
               requestedAction.equals(Defaults.ACTION_NEXT) ||
               requestedAction.equals(Defaults.ACTION_LAST) ||
               requestedAction.equals(Defaults.ACTION_DISPLAY))
      {
         resultString = "Displaying " + startIndex + "-" + upperLimit +
            " of " + rows + " rows.";
      }
      
      else if (requestedAction.equals(Defaults.ACTION_COUNT))
      {
         // Count the number of rows this filter will return
         resultString = "Query will return " + rows + " row(s).";
      }

      // Unknown action
      else
      {
         resultString = new String("?" + requestedAction + "?");
      }

      return resultString;
   }
}

      
   
  


