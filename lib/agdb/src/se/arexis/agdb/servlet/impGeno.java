//package src;

/*
  $Log$
  Revision 1.3  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.2  2003/04/25 12:14:46  heto
  Changed all references to axDefault.css
  Source layout fixes.

  Revision 1.1  2002/11/13 09:03:06  heto
  File created from viewGeno/impFile
  New paralell version.

*/

package se.arexis.agdb.servlet;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.oreilly.servlet.MultipartRequest;
import se.arexis.agdb.util.*;
import se.arexis.agdb.db.*;
import se.arexis.agdb.util.FileImport.*;


public class impGeno extends SecureArexisServlet implements Runnable
{
    // A background thread to prepare the import
    private Thread prepareThread;
    
    // A flag if the process is running
    private boolean threadRunning = false;
    
    // Compare data
    private boolean compare = false;
    
    // Commit data
    private boolean commit = false;
    
    // Status value percent ( 0 -> 100 %)
    private int status = 0;
    
    private class CompareTask implements Runnable
    {
        // A flag that is set then the process is started and running.
        private boolean running = false;
        
        // A flag that is set then the process is complete
        private boolean done = false;
        
        private String msg;
        
        private Thread thread;
        
        private HttpSession session;
        private HttpServletRequest request;
        
        public CompareTask(HttpServletRequest req)
        {
            request = req;
        }
        
        public boolean isRunning()
        {
            return running;
        }
        
        public boolean isDone()
        {
            return done;
        }
        
        public void startCompare()
        {
            thread = new Thread(this);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
            running = true;
        }
        
        public String getMessage()
        {
            return msg;
        }
        
        public void run() 
        {
            HttpSession session = request.getSession(true);
            boolean isOk = true;
            boolean errorFound = false;
            boolean knownIdentity=false;
            String errorMessage = null;
            String uploadMode = "";
            FileWriter fileOut=null;
            Connection connection = null;
            Vector genoDiffs = null;
            Vector fatalErrors= new Vector();
            FileParser fp = null;
            MatrixFileParser mfp = null;
            ResultSet rset=null;
            Statement stmt=null;
            String titles[];
            char delimeter;
            int nrErrors = 0;
            int nrWarnings = 0;

            int nrDeviations=0;
            int rows=0;
            String givenFileName = null;
            String systemFileName = null;
            String markers[];
            // to store genotype data
            String marker=null;
            String allele1=null;
            String allele2=null;
            String raw1=null;
            String raw2=null;
            String ref=null;
            String indId=null;
            String ind=null;
            String comm=null;
            FileHeader header=null;

            int samplingUnitId=-1;
            int userId=-1;
            int level=-1;
            try
            {
                connection = (Connection) session.getAttribute("conn");
                connection.setAutoCommit(false);
                String upPath = getUpFilePath();

                // File size is limited to 6 Megabajt
                MultipartRequest multiRequest =
                    new MultipartRequest(request, upPath, 6 * 1024 * 1024);
                samplingUnitId = Integer.parseInt(multiRequest.getParameter("suid"));
                userId = Integer.parseInt((String) session.getAttribute("UserID"));
                level = Integer.parseInt(multiRequest.getParameter("level"));
                uploadMode = multiRequest.getParameter("type");

                Enumeration files = multiRequest.getFileNames();
                // not needed here, but we need to register filetypedefinitions...
                DbGenotype dbg=new DbGenotype();

                if (files.hasMoreElements())
                {
                    stmt = connection.createStatement();
                    givenFileName = (String) files.nextElement();
                    systemFileName = multiRequest.getFilesystemName(givenFileName);
                }
                if(systemFileName == null || systemFileName.trim().equals(""))
                {
                    errorFound=true;
                    fatalErrors.addElement(" Unable to get filename.");
                }
                else
                {
                    // Get the header information from the file to determine if we
                    // are reading a list or a matrix file.
                    header = FileParser.scanFileHeader(upPath + "/" +systemFileName);

                    int dotPlace=systemFileName.indexOf(".");
                    // get rid of strange endings...
                    String newFileName=systemFileName.substring(0,dotPlace)+".txt";


                    fileOut = new FileWriter(upPath+ "/" + "checked_"+newFileName);

                    if(!header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.LIST) &&
                        !header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.MATRIX))
                    {
                        fatalErrors.addElement("Format type name should be LIST or MATRIX " +
                                                    "but found found " + header.formatTypeName());
                        errorFound=true;
                    }

                    // If file is list
                    if (header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.LIST)
                        && !errorFound)
                    {
                        fp = new FileParser(upPath + "/" +  systemFileName);
                        fp.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.GENOTYPE,
                                                                          FileTypeDefinition.LIST));
                        rows=fp.dataRows();
                        titles = fp.columnTitles();
                        errorFound=checkListTitles(titles,fatalErrors);
                        if (titles[0].equals("IDENTITY"))
                            indId="IDENTITY";
                        else
                            indId="ALIAS";

                        delimeter=header.delimiter().charValue();

                        if(!errorFound)
                        {  //write to new file
                            fileOut.write(header.objectTypeName()+
                                      "/"+header.formatTypeName()+"/"+
                                      header.version()+"/"+delimeter+"\n");

                            for (int j=0;j<titles.length;j++)
                            {
                                fileOut.write(titles[j]+delimeter);
                            }
                            fileOut.write("\n");

                            Vector errorMessages=new Vector();
                            Vector deviationMessages = new Vector();
                            Vector warningMessages = new Vector();
                            Vector databaseValues = new Vector();

                            // check all rows in file
                            for (int i = 0; i < fp.dataRows(); i++)
                            {
                                // Calculate status in percent. TH
                                status = (int)(i / fp.dataRows()); 

                                // get values
                                ind = fp.getValue(indId, i).trim();
                                marker = fp.getValue("MARKER", i).trim();
                                allele1 = fp.getValue("ALLELE1", i).trim();
                                allele2 = fp.getValue("ALLELE2", i).trim();

                                raw1 = fp.getValue("RAW1", i).trim();
                                raw2 = fp.getValue("RAW2", i).trim();
                                ref = fp.getValue("REFERENCE", i).trim();
                                comm = fp.getValue("COMMENT", i).trim();

                                // check that values exist, have correct length etc
                                checkValues(ind, marker,allele1,allele2,raw1,
                                              raw2,ref,comm, fatalErrors);

                                if (uploadMode.equals("CREATE"))
                                {
                                    checkListCreate(titles[0],ind, marker,allele1,allele2,raw1,
                                        raw2,ref,comm,samplingUnitId,errorMessages,warningMessages,stmt);
                                    //{
                                    nrErrors+=errorMessages.size();
                                    nrDeviations+=deviationMessages.size();
                                    nrWarnings+=warningMessages.size();

                                    // write row + all errors encountered to file
                                    writeListErrors(fileOut,errorMessages,warningMessages,deviationMessages,databaseValues,ind,delimeter,marker,allele1,
                                              allele2,raw1,raw2,ref,comm);
                                    //}
                                    errorMessages=new Vector();
                                    deviationMessages=new Vector();
                                    warningMessages=new Vector();
                                    databaseValues = new Vector();

                                }//if create
                                else if (uploadMode.equals("UPDATE"))
                                {
                                    checkListUpdate(titles[0],ind, marker,allele1,allele2,raw1,
                                        raw2,ref,comm,samplingUnitId,errorMessages,deviationMessages,warningMessages,
                                        databaseValues,delimeter, stmt);

                                    nrErrors+=errorMessages.size();
                                    nrDeviations+=deviationMessages.size();
                                    nrWarnings+=warningMessages.size();

                                    // write all errors encountered to file

                                    writeListErrors(fileOut,errorMessages,warningMessages,deviationMessages,databaseValues,ind,delimeter,marker,allele1,
                                      allele2,raw1,raw2,ref,comm);
                                    // clear old errors
                                    errorMessages=new Vector();
                                    deviationMessages=new Vector();
                                    warningMessages=new Vector();
                                    databaseValues = new Vector();

                                }//if update
                                else if (uploadMode.equals("CREATE_OR_UPDATE"))
                                {
                                    checkListCreateOrUpdate(titles[0],ind, marker,allele1,allele2,raw1,
                                        raw2,ref,comm,samplingUnitId,errorMessages,warningMessages,deviationMessages,
                                        databaseValues,delimeter, stmt);


                                    nrErrors+=errorMessages.size();
                                    nrDeviations+=deviationMessages.size();
                                    nrWarnings+=warningMessages.size();

                                    // write all errors encountered to file
                                    writeListErrors(fileOut,errorMessages,warningMessages, deviationMessages,databaseValues,ind,delimeter,marker,allele1,
                                        allele2,raw1,raw2,ref,comm);
                                    // clear old errors
                                    errorMessages=new Vector();
                                    deviationMessages=new Vector();
                                    warningMessages=new Vector();
                                    databaseValues = new Vector();

                                }//if createoruopdate
                            }//for datarows
                        }// not errorfound
                    }//if LIST
                    // If file is Matrix
                    else if (!errorFound && header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.MATRIX))
                    {
                        mfp = new MatrixFileParser(upPath + "/" +  systemFileName);
                        mfp.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.GENOTYPE,
                                                                            FileTypeDefinition.MATRIX));
                        rows=mfp.dataRows();
                        titles=mfp.columnTitles();
                        errorFound=checkMatrixTitles(titles,fatalErrors,samplingUnitId,stmt);
                        if (titles[0].equals("IDENTITY"))
                            indId="IDENTITY";
                        else
                            indId="ALIAS";

                        delimeter=header.delimiter().charValue();
                        markers = new String[titles.length-1];
                        for (int i = 0; i < markers.length; i++)
                            markers[i] = titles[i+1];

                        if(!errorFound)
                        {  
                            //write to new file
                            fileOut.write(header.objectTypeName()+
                                "/"+header.formatTypeName()+"/"+
                                header.version()+"/"+delimeter+"\n");

                            for (int j=0;j<titles.length;j++)
                            {
                                fileOut.write(titles[j]+delimeter);
                            }
                            fileOut.write("\n");
                            stmt = connection.createStatement();
                            String alleles[];
                            Vector errorMessages=new Vector();
                            Vector warningMessages=new Vector();
                            Vector databaseValues=new Vector();
                            Vector deviationMessages = new Vector();
                            Vector newAlleles=new Vector();
                            // check all rows in file
                            for (int row = 0; row < mfp.dataRows(); row++)
                            {
                                // Calculate status in percent. TH
                                status = (int)(row / mfp.dataRows()); 

                                ind = mfp.getValue(indId, row)[0];
                                // check the whole row
                                for (int mNum = 0; mNum < markers.length; mNum++)
                                {
                                    String old_alleles[]=null;
                                    marker = markers[mNum];
                                    alleles = mfp.getValue(marker, row);
                                    allele1 = alleles[0].trim();
                                    allele2 = alleles[1].trim();
                                    // store all alleles on this row
                                    newAlleles.addElement(allele1);
                                    newAlleles.addElement(allele2);
                                    // check that values exist, have correct length etc
                                    checkValues(ind, marker,allele1,allele2,null,
                                        null,null,null, errorMessages);

                                    if (uploadMode.equals("CREATE"))
                                    {
                                        checkMatrixCreate(titles[0],ind, marker,allele1,allele2,
                                            samplingUnitId,errorMessages,warningMessages,stmt);
                                    }
                                    else if(uploadMode.equals("UPDATE"))
                                    {
                                        checkMatrixUpdate(titles[0],ind, marker,allele1,allele2,
                                            samplingUnitId,errorMessages,warningMessages,deviationMessages,databaseValues,stmt);
                                    }
                                    else if(uploadMode.equals("CREATE_OR_UPDATE"))
                                    {
                                        checkMatrixCreateOrUpdate(titles[0],ind, marker,allele1,allele2,
                                            samplingUnitId,errorMessages,warningMessages,deviationMessages,databaseValues,stmt);
                                    }// if create or update
                                }//for markers
                                nrErrors+=errorMessages.size();
                                nrDeviations+=deviationMessages.size();
                                nrWarnings+=warningMessages.size();

                                writeMatrixErrors(fileOut,errorMessages,warningMessages,deviationMessages,databaseValues,
                                    newAlleles,ind,delimeter,marker,allele1,allele2);

                                newAlleles= new Vector();
                                databaseValues = new Vector();
                                errorMessages=new Vector();
                                warningMessages=new Vector();
                                deviationMessages=new Vector();
                            }// for rows
                        }
                    }// if no error found
                }// IF file has more elements (filename !=null);
                if(stmt!=null)
                {
                    stmt.close();
                }
            }//try
            catch (Exception e)
            {
                // Flag for error and set the errMessage if it has not been set
                isOk = false;
                e.printStackTrace(System.err);
                errorFound=true;

                if (errorMessage == null)
                {
                    errorMessage = e.getMessage();
                    fatalErrors.addElement(e.getMessage());
                }
            }
            finally 
            {
                try
                {
                    fileOut.flush();
                    fileOut.close();
                }
                catch (Exception ignore) {}
                
                //done = true;
            }

            /*
            if(fileOut!=null)
            {
                fileOut.flush();
                fileOut.close();
            }
             */

            if(!errorFound)
            {
                // store values for resultpage
                session.setAttribute("GENO_nrErrors",new Integer(nrErrors).toString());
                session.setAttribute("GENO_nrDeviations",new Integer(nrDeviations).toString());
                session.setAttribute("GENO_nrWarnings",new Integer(nrWarnings).toString());
                session.setAttribute("GENO_FILE",systemFileName);
                session.setAttribute("GENO_SUID",new Integer(samplingUnitId).toString());
                session.setAttribute("GENO_TYPE",uploadMode);
                session.setAttribute("GENO_LEVEL",new Integer(level).toString());
                session.setAttribute("GENO_ROWS",new Integer(rows).toString());
                
                done = true;

                // TH.
                //response.sendRedirect(getServletPath("impGeno/compare"));
            }
            else // Fatal error, we do not let this file pass
            {
                if(fatalErrors.size()==0)
                {
                    errorMessage="Unknown";
                }
                else
                {
                    errorMessage=(String)fatalErrors.elementAt(0);
                }
                
                // No changes done? Commit unneccesary?
                //commitOrRollback(connection, request, response,
                //                   "Genotypes.Import.CreateOrCreateUpdate.Send",
                //                   errorMessage, "viewGeno/impFile", false);
            }
        }
        
    }
    
    
    
    //
    private CompareTask compTask;
    
    
    public void init() throws ServletException
    {
        System.err.println("impGeno.init()");
        super.init();
    }
    
    public void destroy()
    {
        if (prepareThread != null)
            prepareThread.stop();
    }

   /**
    * Prints the page used for importing genotypes from a file.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @exception IOException If a writer could not be created.
    */
    public void doGet(HttpServletRequest request,
                     HttpServletResponse response)
        throws IOException, ServletException
    {
        HttpSession session = request.getSession(true);
        response.setContentType("text/html");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");

        PrintWriter out = response.getWriter();
      
        // Create the class CompareTask.
        compTask = new CompareTask(request);
      
      
        System.err.println("impGeno.doGet(...)");
        System.err.println("threadRunning="+threadRunning);
        
        String extPath = request.getPathInfo();
      
        if ((compTask.isDone() == false) && (compTask.isRunning() == false))
        {
            
          
            if (extPath == null || extPath.equals("") || extPath.equals("/")) 
            {
                // The frame is requested
                //writeFrame(req, res);
                // No process is running.
                // We can safely start a new process.
                // Show the start page for uploading a new genotype file.
                writePage(request,response);  
            }
            else if (extPath.equals("/impMultipart")) 
            {
                // Start a new comparing part.
                compTask.startCompare();             
                //compareFile(request, response);
            }
            
        }
        else if ((compTask.isDone() == true) && (compTask.isRunning() == false))
        {
            // First step is done
            
            
            if (extPath == null || extPath.equals("") || extPath.equals("/")) 
            {
                // The frame is requested
                //writeFrame(req, res);
                // No process is running.
                // We can safely start a new process.
                // Show the start page for uploading a new genotype file.
                //writePage(request,response);  
                
                out.println("Task done. /");
            }
            else if (extPath.equals("/commitMultipart")) 
            {
                out.println("Task done. /commitMultiPart");
                //createFile(request, response);
            }
            
        }
        else
        {
            // The extra thread is currently running. Show a status page 
            // instead.
          
            out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 " +
                "Transitional//EN\"");
            out.println(" \"http://www.w3.org/TR/html4/loose.dtd\">");

            out.println("<html>");
            out.println("<head>");
            HTMLWriter.css(out,getURL("style/axDefault.css"));

            out.println("<title>Import genotypes</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("Status of preparing");
            out.println("</body>");
            out.println("</html>");
          
            // Stop the execution.
            return;
        }
    }
   
    /**
    * The run method for the extra process created for the long running 
    * task.
    */
   public void run()
   {   
       System.err.println("Starting run method");
       
       if (compare)
       {
           
       }
       
       if (commit)
       {
           
       }
       
       /*
       threadRunning = true;
       while (threadRunning)
       {
           System.err.println("Ping!");
           try
           {
               prepareThread.sleep(10000);
           }
           catch (InterruptedException ignored) {}
       }
        */
       
       System.err.println("Ending run method");
   }

   /**
    * Import individuals from a file.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    */
   public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
                      throws IOException, ServletException
   {
       doGet(request,response);
   }

   private void writePage(HttpServletRequest request,
                     HttpServletResponse response)
      throws IOException
   {
      HttpSession session = request.getSession(true);
      PrintWriter out = response.getWriter();
      Connection connection = (Connection) session.getAttribute("conn");

      Statement sqlStatement = null;
      ResultSet resultSet = null;

      
      try
      {
         //connection = (Connection) session.getAttribute("conn");
         String projectId = (String) session.getAttribute("PID");

         out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 " +
                     "Transitional//EN\"");
         out.println(" \"http://www.w3.org/TR/html4/loose.dtd\">");

         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));

         out.println("<title>Import genotypes</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=0>" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Genotypes - File import</b></center>" +
                     "</td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post enctype=\"multipart/form-data\" action=\"" +
                     getServletPath("impGeno/impMultipart") + "\">");
         out.println("<table border=0>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         sqlStatement = connection.createStatement();

         resultSet = sqlStatement.executeQuery("SELECT SUID, NAME FROM V_ENABLED_SAMPLING_UNITS_2 WHERE " +
                                  "PID=" + projectId + " ORDER BY NAME");
         out.println("<td>Sampling unit<br>");
         out.println("<select name=suid style=\"WIDTH: 200px\">");

         while (resultSet.next() ) {

              out.println("<option value=\"" + resultSet.getString("SUID") + "\">" +
                          resultSet.getString("NAME") + "</option>");
         }
         out.println("</select>");
         resultSet.close();
         sqlStatement.close();
         out.println("</td></tr>");

         // File
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         out.println("<td>File<br>");
         out.println("<input type=file name=filename " +
                     "style=\"WIDTH: 350px\">");
         out.println("</td></tr>");
         // Level
         out.println("<tr><td></td><td>Level<br>");
         out.println("<select name=level style=\"WIDTH: 200px\">");
         int[] privileges = (int[]) session.getAttribute("PRIVILEGES");
         int myHighestLevel = -1;
         for (int i = 0; i < privileges.length; i++) {
            if (GENO_W9 - privileges[i] >= 0 &&
                privileges[i] - GENO_W0 >= 0 &&
                (privileges[i] - GENO_W0) > myHighestLevel) {
               myHighestLevel = privileges[i] - GENO_W0;
            }
         }

         out.println("<option selected value=\"" + 0 + "\">" +
                     0 + "</option>");

         for (int i = 1; i <= myHighestLevel; i++) {
            out.println("<option value=\"" + i + "\">" + i + "</option>");
         }

         out.println("</select>");
         out.println("</td></tr>");

         // type of upload
         out.println("<tr><td></td><td>Mode<br>");
         out.println("<select name=type width=200 style=\"WIDTH: 200px\">");
         out.println("<option value=\"CREATE\">Create</option>");
         out.println("<option value=\"CREATE_OR_UPDATE\">Create or update</option>");
         out.println("<option value=\"UPDATE\">Update</option>");
         out.println("</select>");
         out.println("</td></tr>");

         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td></td></tr>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("<table border=0><tr>");
         out.println("<td>");

/*         out.println("<input type=button value=Send " +
                     "style=\"WIDTH: 100px\" onClick='valForm()'>");
*/
         out.println("<input type=button value=Send " +
                     "style=\"WIDTH: 100px\" onClick='document.forms[0].submit()'>");

         out.println("&nbsp;</td></tr></table>");
         out.println("</td></tr>");
         out.println("</table>");
         out.println("<input type=hidden name=oper value=\"\">");
         out.println("</form>");
         out.println("</body>");
         out.println("</html>");
      }
      catch (Exception e)
      {
         e.printStackTrace(System.err);
      }
      finally
      {
         try
         {
            if (resultSet != null)
            {
               resultSet.close();
            }

            if (sqlStatement != null)
            {
               sqlStatement.close();
            }
         }
         catch (SQLException ignored)
         {
         }
      }

   }


   private void writeScript(PrintWriter out) 
   {
      out.println("<script type=\"text/javascript\">");
      out.println("<!--");
      out.println("function confirmSubmit() {");
      out.println("  var doSubmit = 1;");


      out.println("  if (document.forms[0].update[1].checked) {");
      out.println("    if (confirm('Are you sure you want to update the Individuals?')) {");
      out.println("      ;");
      out.println("    } else {");
      out.println("      doSubmit = 0;");
      out.println("    }");
      out.println("  }");

      out.println("  if (document.forms[0].update[0].checked) {");
      out.println("    if (confirm('Are you sure you want to create the Individuals?')) {");
      out.println("      ;");
      out.println("    } else {");
      out.println("      doSubmit = 0;");
      out.println("    }");
      out.println("  }");

      out.println("  if (document.forms[0].update[2].checked) {");
      out.println("    if (confirm('Are you sure you want to create and/or update the Individuals?')) {");
      out.println("      ;");
      out.println("    } else {");
      out.println("      doSubmit = 0;");
      out.println("    }");
      out.println("  }");




      out.println("  if (doSubmit != 0)");
      out.println("    document.forms[0].submit();");
      out.println("}");
      out.println("// -->");
      out.println("</script>");
   }

  
   
   /**
    * Compares genotypes from a file with those in database.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @return True if everything was ok.
    *         False if any errors.
    * @exception IOException If writing any of the pages fails.
    * @exception ServletException If writing any of the pages fails.
    */
   /*
   private boolean compareFile(HttpServletRequest request) //,
                            //  HttpServletResponse response)
   throws IOException, ServletException 
   {
        HttpSession session = request.getSession(true);
        boolean isOk = true;
        boolean errorFound = false;
        boolean knownIdentity=false;
        String errorMessage = null;
        String uploadMode = "";
        FileWriter fileOut=null;
        Connection connection = null;
        Vector genoDiffs = null;
        Vector fatalErrors= new Vector();
        FileParser fp = null;
        MatrixFileParser mfp = null;
        ResultSet rset=null;
        Statement stmt=null;
        String titles[];
        char delimeter;
        int nrErrors = 0;
        int nrWarnings = 0;

        int nrDeviations=0;
        int rows=0;
        String givenFileName = null;
        String systemFileName = null;
        String markers[];
        // to store genotype data
        String marker=null;
        String allele1=null;
        String allele2=null;
        String raw1=null;
        String raw2=null;
        String ref=null;
        String indId=null;
        String ind=null;
        String comm=null;
        FileHeader header=null;

        int samplingUnitId=-1;
        int userId=-1;
        int level=-1;
        try
        {
            connection = (Connection) session.getAttribute("conn");
            connection.setAutoCommit(false);
            String upPath = getUpFilePath();

            // File size is limited to 6 Megabajt
            MultipartRequest multiRequest =
                new MultipartRequest(request, upPath, 6 * 1024 * 1024);
            samplingUnitId = Integer.parseInt(multiRequest.getParameter("suid"));
            userId = Integer.parseInt((String) session.getAttribute("UserID"));
            level = Integer.parseInt(multiRequest.getParameter("level"));
            uploadMode = multiRequest.getParameter("type");

            Enumeration files = multiRequest.getFileNames();
            // not needed here, but we need to register filetypedefinitions...
            DbGenotype dbg=new DbGenotype();

            if (files.hasMoreElements())
            {
                stmt = connection.createStatement();
                givenFileName = (String) files.nextElement();
                systemFileName = multiRequest.getFilesystemName(givenFileName);
            }
            if(systemFileName == null || systemFileName.trim().equals(""))
            {
                errorFound=true;
                fatalErrors.addElement(" Unable to get filename.");
            }
            else
            {
                // Get the header information from the file to determine if we
                // are reading a list or a matrix file.
                header = FileParser.scanFileHeader(upPath + "/" +systemFileName);

                int dotPlace=systemFileName.indexOf(".");
                // get rid of strange endings...
                String newFileName=systemFileName.substring(0,dotPlace)+".txt";


                fileOut = new FileWriter(upPath+ "/" + "checked_"+newFileName);

                if(!header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.LIST) &&
                    !header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.MATRIX))
                {
                    fatalErrors.addElement("Format type name should be LIST or MATRIX " +
                                                "but found found " + header.formatTypeName());
                    errorFound=true;
                }

                // If file is list
                if (header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.LIST)
                    && !errorFound)
                {
                    fp = new FileParser(upPath + "/" +  systemFileName);
                    fp.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.GENOTYPE,
                                                                      FileTypeDefinition.LIST));
                    rows=fp.dataRows();
                    titles = fp.columnTitles();
                    errorFound=checkListTitles(titles,fatalErrors);
                    if (titles[0].equals("IDENTITY"))
                        indId="IDENTITY";
                    else
                        indId="ALIAS";

                    delimeter=header.delimiter().charValue();

                    if(!errorFound)
                    {  //write to new file
                        fileOut.write(header.objectTypeName()+
                                  "/"+header.formatTypeName()+"/"+
                                  header.version()+"/"+delimeter+"\n");

                        for (int j=0;j<titles.length;j++)
                        {
                            fileOut.write(titles[j]+delimeter);
                        }
                        fileOut.write("\n");

                        Vector errorMessages=new Vector();
                        Vector deviationMessages = new Vector();
                        Vector warningMessages = new Vector();
                        Vector databaseValues = new Vector();

                        // check all rows in file
                        for (int i = 0; i < fp.dataRows(); i++)
                        {
                            // Calculate status in percent. TH
                            status = (int)(i / fp.dataRows()); 
                       
                            // get values
                            ind = fp.getValue(indId, i).trim();
                            marker = fp.getValue("MARKER", i).trim();
                            allele1 = fp.getValue("ALLELE1", i).trim();
                            allele2 = fp.getValue("ALLELE2", i).trim();

                            raw1 = fp.getValue("RAW1", i).trim();
                            raw2 = fp.getValue("RAW2", i).trim();
                            ref = fp.getValue("REFERENCE", i).trim();
                            comm = fp.getValue("COMMENT", i).trim();

                            // check that values exist, have correct length etc
                            checkValues(ind, marker,allele1,allele2,raw1,
                                          raw2,ref,comm, fatalErrors);

                            if (uploadMode.equals("CREATE"))
                            {
                                checkListCreate(titles[0],ind, marker,allele1,allele2,raw1,
                                    raw2,ref,comm,samplingUnitId,errorMessages,warningMessages,stmt);
                                //{
                                nrErrors+=errorMessages.size();
                                nrDeviations+=deviationMessages.size();
                                nrWarnings+=warningMessages.size();

                                // write row + all errors encountered to file
                                writeListErrors(fileOut,errorMessages,warningMessages,deviationMessages,databaseValues,ind,delimeter,marker,allele1,
                                          allele2,raw1,raw2,ref,comm);
                                //}
                                errorMessages=new Vector();
                                deviationMessages=new Vector();
                                warningMessages=new Vector();
                                databaseValues = new Vector();

                            }//if create
                            else if (uploadMode.equals("UPDATE"))
                            {
                                checkListUpdate(titles[0],ind, marker,allele1,allele2,raw1,
                                    raw2,ref,comm,samplingUnitId,errorMessages,deviationMessages,warningMessages,
                                    databaseValues,delimeter, stmt);

                                nrErrors+=errorMessages.size();
                                nrDeviations+=deviationMessages.size();
                                nrWarnings+=warningMessages.size();

                                // write all errors encountered to file

                                writeListErrors(fileOut,errorMessages,warningMessages,deviationMessages,databaseValues,ind,delimeter,marker,allele1,
                                  allele2,raw1,raw2,ref,comm);
                                // clear old errors
                                errorMessages=new Vector();
                                deviationMessages=new Vector();
                                warningMessages=new Vector();
                                databaseValues = new Vector();

                            }//if update
                            else if (uploadMode.equals("CREATE_OR_UPDATE"))
                            {
                                checkListCreateOrUpdate(titles[0],ind, marker,allele1,allele2,raw1,
                                    raw2,ref,comm,samplingUnitId,errorMessages,warningMessages,deviationMessages,
                                    databaseValues,delimeter, stmt);


                                nrErrors+=errorMessages.size();
                                nrDeviations+=deviationMessages.size();
                                nrWarnings+=warningMessages.size();

                                // write all errors encountered to file
                                writeListErrors(fileOut,errorMessages,warningMessages, deviationMessages,databaseValues,ind,delimeter,marker,allele1,
                                    allele2,raw1,raw2,ref,comm);
                                // clear old errors
                                errorMessages=new Vector();
                                deviationMessages=new Vector();
                                warningMessages=new Vector();
                                databaseValues = new Vector();

                            }//if createoruopdate
                        }//for datarows
                    }// not errorfound
                }//if LIST
                // If file is Matrix
                else if (!errorFound && header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.MATRIX))
                {
                    mfp = new MatrixFileParser(upPath + "/" +  systemFileName);
                    mfp.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.GENOTYPE,
                                                                        FileTypeDefinition.MATRIX));
                    rows=mfp.dataRows();
                    titles=mfp.columnTitles();
                    errorFound=checkMatrixTitles(titles,fatalErrors,samplingUnitId,stmt);
                    if (titles[0].equals("IDENTITY"))
                        indId="IDENTITY";
                    else
                        indId="ALIAS";

                    delimeter=header.delimiter().charValue();
                    markers = new String[titles.length-1];
                    for (int i = 0; i < markers.length; i++)
                        markers[i] = titles[i+1];

                    if(!errorFound)
                    {  
                        //write to new file
                        fileOut.write(header.objectTypeName()+
                            "/"+header.formatTypeName()+"/"+
                            header.version()+"/"+delimeter+"\n");

                        for (int j=0;j<titles.length;j++)
                        {
                            fileOut.write(titles[j]+delimeter);
                        }
                        fileOut.write("\n");
                        stmt = connection.createStatement();
                        String alleles[];
                        Vector errorMessages=new Vector();
                        Vector warningMessages=new Vector();
                        Vector databaseValues=new Vector();
                        Vector deviationMessages = new Vector();
                        Vector newAlleles=new Vector();
                        // check all rows in file
                        for (int row = 0; row < mfp.dataRows(); row++)
                        {
                            // Calculate status in percent. TH
                            status = (int)(row / mfp.dataRows()); 
                            
                            ind = mfp.getValue(indId, row)[0];
                            // check the whole row
                            for (int mNum = 0; mNum < markers.length; mNum++)
                            {
                                String old_alleles[]=null;
                                marker = markers[mNum];
                                alleles = mfp.getValue(marker, row);
                                allele1 = alleles[0].trim();
                                allele2 = alleles[1].trim();
                                // store all alleles on this row
                                newAlleles.addElement(allele1);
                                newAlleles.addElement(allele2);
                                // check that values exist, have correct length etc
                                checkValues(ind, marker,allele1,allele2,null,
                                    null,null,null, errorMessages);

                                if (uploadMode.equals("CREATE"))
                                {
                                    checkMatrixCreate(titles[0],ind, marker,allele1,allele2,
                                        samplingUnitId,errorMessages,warningMessages,stmt);
                                }
                                else if(uploadMode.equals("UPDATE"))
                                {
                                    checkMatrixUpdate(titles[0],ind, marker,allele1,allele2,
                                        samplingUnitId,errorMessages,warningMessages,deviationMessages,databaseValues,stmt);
                                }
                                else if(uploadMode.equals("CREATE_OR_UPDATE"))
                                {
                                    checkMatrixCreateOrUpdate(titles[0],ind, marker,allele1,allele2,
                                        samplingUnitId,errorMessages,warningMessages,deviationMessages,databaseValues,stmt);
                                }// if create or update
                            }//for markers
                            nrErrors+=errorMessages.size();
                            nrDeviations+=deviationMessages.size();
                            nrWarnings+=warningMessages.size();

                            writeMatrixErrors(fileOut,errorMessages,warningMessages,deviationMessages,databaseValues,
                                newAlleles,ind,delimeter,marker,allele1,allele2);

                            newAlleles= new Vector();
                            databaseValues = new Vector();
                            errorMessages=new Vector();
                            warningMessages=new Vector();
                            deviationMessages=new Vector();
                        }// for rows
                    }
                }// if no error found
            }// IF file has more elements (filename !=null);
            if(stmt!=null)
            {
                stmt.close();
            }
        }//try
        catch (Exception e)
        {
            // Flag for error and set the errMessage if it has not been set
            isOk = false;
            e.printStackTrace(System.err);
            errorFound=true;

            if (errorMessage == null)
            {
                errorMessage = e.getMessage();
                fatalErrors.addElement(e.getMessage());
            }
        }

        if(fileOut!=null)
        {
            fileOut.flush();
            fileOut.close();
        }

        if(!errorFound)
        {
            // store values for resultpage
            session.setAttribute("GENO_nrErrors",new Integer(nrErrors).toString());
            session.setAttribute("GENO_nrDeviations",new Integer(nrDeviations).toString());
            session.setAttribute("GENO_nrWarnings",new Integer(nrWarnings).toString());
            session.setAttribute("GENO_FILE",systemFileName);
            session.setAttribute("GENO_SUID",new Integer(samplingUnitId).toString());
            session.setAttribute("GENO_TYPE",uploadMode);
            session.setAttribute("GENO_LEVEL",new Integer(level).toString());
            session.setAttribute("GENO_ROWS",new Integer(rows).toString());
            
            // TH.
            //response.sendRedirect(getServletPath("impGeno/compare"));
        }
        else // Fatal error, we do not let this file pass
        {
            if(fatalErrors.size()==0)
            {
                errorMessage="Unknown";
            }
            else
            {
                errorMessage=(String)fatalErrors.elementAt(0);
            }
            commitOrRollback(connection, request, response,
                               "Genotypes.Import.CreateOrCreateUpdate.Send",
                               errorMessage, "viewGeno/impFile", false);
        }
        return isOk;
    }
   */
   
    private void writeMatrixErrors(FileWriter fileOut,Vector errorMessages,Vector warningMessages,
            Vector deviationMessages,Vector databaseValues, Vector newAlleles, String ind,
            char delim,String marker, String allele1,
            String allele2)
{
  try
  {
      // if row contains comments
      if(errorMessages.size()>0 || deviationMessages.size()>0 || warningMessages.size()>0)
      {
        fileOut.write("#--------------------------------------------------------\n");
      }
      if(errorMessages.size()>0)
      {
         for(int i=0;i<errorMessages.size();i++)
         {
            fileOut.write("#"+ (String)errorMessages.elementAt(i)+"\n");
         }
      }

      if(warningMessages.size()>0)
      {
         for(int i=0;i<warningMessages.size();i++)
         {
            fileOut.write("#"+ (String)warningMessages.elementAt(i)+"\n");
         }
      }

      if(deviationMessages.size()>0)
      {
         for(int i=0;i<deviationMessages.size();i++)
         {
            fileOut.write("#"+ (String)deviationMessages.elementAt(i)+"\n");
         }
         //write database values
        fileOut.write("#"+ind);
        for (int i=0;i<databaseValues.size();i++)
        {
          fileOut.write(delim+ (String)databaseValues.elementAt(i));
        }
        fileOut.write("\n");
      }
      // write row from file:
      if(errorMessages.size() > 0)
      {
        fileOut.write("#");
      }
      fileOut.write(ind);
      for (int i=0;i<newAlleles.size();i++)
      {
        fileOut.write(delim+(String)newAlleles.elementAt(i));
      }
      fileOut.write("\n");
      if(errorMessages.size()>0 || deviationMessages.size()>0 || warningMessages.size()>0)
      {
        fileOut.write("#--------------------------------------------------------\n");
      }

  }//try
  catch (Exception e)
  {
    e.printStackTrace(System.err);
  }
}
   
   private boolean checkListTitles(String [] titles, Vector errorMessages){
// Check the file header
  boolean errorFound=false;
  String errorStr=null;
  if (titles.length != 8)
    errorFound = true;
  else if (!(titles[0].equals("IDENTITY") || titles[0].equals("ALIAS")) ||
            !titles[1].equals("MARKER") ||
            !titles[2].equals("ALLELE1") ||
            !titles[3].equals("ALLELE2") ||
            !titles[4].equals("RAW1") ||
            !titles[5].equals("RAW2") ||
            !titles[6].equals("REFERENCE") ||
            !titles[7].equals("COMMENT") )
            errorFound = true;
            if (errorFound)
            {
              errorStr="Illegal headers.<BR>"+
               "Required file headers: IDENTITY/ALIAS MARKER ALLELE1 ALLELE2 RAW1 RAW2 REFERENCE COMMENT<BR>"+
               "Headers found in file:";
              for (int j=0; j<titles.length;j++)
              {
                errorStr = errorStr+ " " + titles[j];
              }
              errorMessages.addElement(errorStr);
            }

            return errorFound;
  }
   
   private void checkMatrixUpdate(  String id_or_alias,String ind,String marker,
                              String allele1,String allele2,int suid,
                              Vector errorMessages,Vector warningMessages,Vector deviationMessages,
                              Vector databaseValues,Statement stmt)
{
  ResultSet rset;
  int nrErrors=0;
  String identity=null;
  boolean match=false;
  try
  {
      //First if no IDENTITY was sent, we need to get it through alias!
      if(id_or_alias.equalsIgnoreCase("ALIAS")&& ind!=null
         && !ind.trim().equalsIgnoreCase(""))
      {
        rset = stmt.executeQuery("SELECT IDENTITY FROM gdbadm.INDIVIDUALS WHERE " +
                              " ALIAS=" + "'"+ind +"'"+
                              " AND SUID=" +"'"+suid+"'");

        if(rset.next())
          identity=rset.getString("IDENTITY");
        else
          identity="-1";
      }
      else
        identity=ind;
      //compare to database, does genotype exist? can it be updated..
      rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_GENOTYPES_3 WHERE " +
                              "MNAME=" +"'"+ marker+"'" +
                              " AND IDENTITY=" + "'"+identity +"'"+
                              " AND SUID=" +"'"+suid+"'");



      if (rset.next())
      {// Genotype exists
        // do the alleles exist for these markers??
        if(allele1 != null && !allele1.trim().equalsIgnoreCase(""))
        {

          rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                  " MNAME=" + "'"+marker+"'"+
                                  " AND NAME=" +"'"+allele1+"'" +
                                  " AND SUID=" +"'"+suid+"'");
          if (!rset.next())
          {
            // the allele does not exist
            String Message =" Warning! Allele "+allele1+" does not exist for marker "+marker+". It will be created if genotype is imported!";
            errorMessages.addElement(Message);
            nrErrors ++;
          }
        }


      if(allele2 != null && !allele2.trim().equalsIgnoreCase(""))
      {
        // do the alleles exist for these markers??
        rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                " MNAME=" + "'"+marker+"'"+
                                " AND NAME=" +"'"+allele2+"'"+
                                " AND SUID=" +"'"+suid+"'");
        if (!rset.next())
        {
          // the marker does not exist
          String Message =" Warning Allele "+allele2+" does not exist for marker "+marker+". It will be created if genotype is imported!";
          errorMessages.addElement(Message);
          nrErrors ++;
        }
      }


        rset = stmt.executeQuery("SELECT IDENTITY,MNAME, A1NAME, A2NAME"+
                              " FROM gdbadm.V_GENOTYPES_3 WHERE "+
                              " MNAME=" +"'"+ marker+"'" +
                              " AND IDENTITY="+"'"+identity +"'"+
                              " AND SUID=" +"'"+suid+"'");

        if (rset.next())
        {
          // get database values
          String mname=rset.getString("MNAME");
          String a1=rset.getString("A1NAME");
          String a2=rset.getString("A2NAME");
          //if any of these are null, we need to set nothing.
          if(a1==null)
            a1="";
          if(a2==null)
            a2="";

          // compare allele values
          if(a1.equalsIgnoreCase(allele1) && a2.equalsIgnoreCase(allele2))
          {
            match = true;
          }
          else if(a1.equalsIgnoreCase(allele2) && a2.equalsIgnoreCase(allele1))
          {
            match = true;
          }
          if(!match)
          {
            deviationMessages.addElement(marker+": Genotype differs from database.");
          }
          databaseValues.addElement(a1);
          databaseValues.addElement(a2);
        }
      }
      else // genotype does not exist
      {
            errorMessages.addElement(marker+": The Genotype does not exist in database.");
      }
    rset.close();
  }// try
  catch (Exception e)
  {
         // Flag for error and set the errMessage if it has not been set
         e.printStackTrace(System.err);
  }
}
   
   /**
* Compares all values to whats already in the database
* returns the number of errors found.
**/
private void checkMatrixCreate( String id_or_alias,
                          String ind,
                          String marker,
                          String allele1,
                          String allele2,
                          int suid,
                          Vector errorMessages,
                          Vector warningMessages,
                          Statement stmt)
{

  ResultSet rset;
  int nrErrors=0;
  String identity=null;
 try
 {
   //First if no IDENTITY was sent, we need to get it through alias!
   if(id_or_alias.equalsIgnoreCase("ALIAS")&& ind!=null
       && !ind.trim().equalsIgnoreCase(""))
   {

    rset = stmt.executeQuery("SELECT IDENTITY FROM gdbadm.INDIVIDUALS WHERE " +
                            " ALIAS=" + "'"+ind +"'"+
                            " AND SUID=" +"'"+suid+"'");

      if(rset.next())
      {
        identity=rset.getString("IDENTITY");
      }
      else
      {
        identity="-1";
      }
   }
   else
   {
      identity=ind;
   }

  //compare to database, is this unique? (can it be inserted?)
  rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_GENOTYPES_3 WHERE " +
                            "MNAME=" +"'"+ marker+"'" +
                            " AND IDENTITY=" + "'"+identity +"'"+
                            " AND SUID=" +"'"+suid+"'");

  if (rset.next())
  {
    // the genotype exists

    String Message = marker +": The Genotype exists, cannot be created.";
    errorMessages.addElement(Message);
    nrErrors ++;
    }

    // Does the individual exist?
    rset = stmt.executeQuery("SELECT IID FROM gdbadm.INDIVIDUALS WHERE " +
                              " IDENTITY=" + "'"+identity +"'"+
                              " AND SUID=" +"'"+suid+"'");
    if (!rset.next())
    {
      // the Individual does not exist
      String Message =" The Individual with "+ id_or_alias+" "+ind+" does not exist.";
      errorMessages.addElement(Message);
      nrErrors ++;
    }
    // does marker exist?
    rset = stmt.executeQuery("SELECT MID FROM gdbadm.MARKERS WHERE " +
                              " NAME=" + "'"+marker+"'"+
                              " AND SUID=" +"'"+suid+"'");

    if (!rset.next())
    {
      // the marker does not exist
      String Message =" Marker "+marker+" does not exist";
      errorMessages.addElement(Message);
      nrErrors ++;
    }
    else
    {
      // do the alleles exist for these markers??
      rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                " MNAME=" + "'"+marker+"'"+
                                " AND NAME=" +"'"+allele1+"'" +
                                " AND SUID=" +"'"+suid+"'");

      if (!rset.next())
      {
        // the allele does not exist
        String Message = " Warning!"+ marker+": Allele "+allele1+" does not exist. Will be created if genotype is imported!";
        warningMessages.addElement(Message);
        //nrErrors ++;
      }

      // do the alleles exist for these markers??
      rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                               " MNAME=" + "'"+marker+"'"+
                               " AND NAME=" +"'"+allele2+"'"+
                               " AND SUID=" +"'"+suid+"'");

      if (!rset.next())
      {
        // the marker does not exist
        String Message = " Warning!"+marker+": Allele "+allele2+" does not exist. Will be created if genotype is imported!";
        warningMessages.addElement(Message);
        //nrErrors ++;
      }
      rset.close();
    }
  }
  catch (Exception e)
  {
         // Flag for error and set the errMessage if it has not been set
         e.printStackTrace(System.err);
  }
//        return nrErrors;

}
private boolean checkMatrixTitles(String [] titles, Vector errorMessages,
                                  int suid,Statement stmt)
{
  boolean errorFound=false;
  String errorStr=null;
  ResultSet rset =null;
  try
    {
   // System.err.println("checkMatrixtitles..");
    if (titles.length < 2)
      errorFound = true;
    else if (!(titles[0].equals("IDENTITY") || titles[0].equals("ALIAS")))
    {
      errorFound = true;
    }
    if (errorFound)
    {
      errorStr="Illegal headers.<BR>"+
      "Required file headers: IDENTITY/ALIAS MARKER1 MARKER2 ...<BR>"+
      "Headers found in file:";
      for (int j=0; j<titles.length;j++)
      {
        errorStr = errorStr+ " " + titles[j];
      }
      errorMessages.addElement(errorStr);
    }

    // now we check that the markers in header exists
    for (int i=1;i<titles.length;i++)
    {

      rset = stmt.executeQuery("SELECT MID FROM gdbadm.MARKERS WHERE " +
                               " NAME=" + "'"+titles[i]+"'"+
                                " AND SUID=" +"'"+suid+"'");
      if(!rset.next())
      {
        errorFound=true;
        errorStr="Marker "+titles[i]+" does not exist.";
        errorMessages.addElement(errorStr);
      }
    }


    if(rset!=null)
    rset.close();
    }
    catch (Exception e)
    {
      e.printStackTrace(System.err);
    }
    return errorFound;
}

private void writeListErrors(FileWriter fileOut,Vector errorMessages,Vector warningMessages,
                            Vector deviationMessages, Vector databaseValues,
                            String ind,char delimeter,
                            String marker, String allele1,String allele2,
                            String raw1,String raw2,String ref,String comm)
{
  try
  {

    if(errorMessages.size()>0 || deviationMessages.size()>0 || warningMessages.size()>0)
    {
      fileOut.write("#--------------------------------------------------\n");
    }
    if(errorMessages.size()>0)
    {
         for(int i=0;i<errorMessages.size();i++)
         {
            fileOut.write("#"+ (String)errorMessages.elementAt(i)+"\n");
         }
    }
    if(warningMessages.size()>0)
    {
        for (int i=0;i<warningMessages.size();i++)
        {
          fileOut.write("#"+ (String)warningMessages.elementAt(i)+"\n");
        }
    }

    if(deviationMessages.size()>0)
    {
        for (int i=0;i<deviationMessages.size();i++)
        {
          fileOut.write("#"+ (String)deviationMessages.elementAt(i)+"\n");
        }
        // write old values
        fileOut.write("#"+databaseValues.elementAt(0)+"\n");
    }
    // if there are errors, the string is "Outcommented"
    if(errorMessages.size()>0)
    {
          fileOut.write("#");
    }

    // write original string
    fileOut.write(ind+delimeter+marker+delimeter+allele1+
                  delimeter+allele2+delimeter+raw1+delimeter+
                  raw2+delimeter+ref+delimeter+comm+"\n");

    if(errorMessages.size()>0 || deviationMessages.size()>0 || warningMessages.size()>0)
    {
      fileOut.write("#--------------------------------------------------\n");
    }

  }//try
  catch (Exception e)
  {
    e.printStackTrace(System.err);
  }

}

private boolean checkValues(String identity, String marker,
                               String allele1, String allele2,
                               String raw1, String raw2,
                               String ref, String comm, Vector errMessages) {
      boolean ret = true;

      if (identity == null || identity.trim().equals(""))
      {

         errMessages.addElement("Unable to read Identity/Alias.");
         ret = false;
      }
      else if (identity.length() > 11)
      {
         errMessages.addElement("Identity/Alias [" + identity + "] exceeds 11 characters.");
         ret = false;
      }
      else if (marker == null || marker.trim().equals(""))
      {
         errMessages.addElement("Unable to read marker.");
         ret = false;
      }
      else if (marker.length() > 20)
      {
         errMessages.addElement("Marker [" + marker + "] exceeds 20 characters.");
         ret = false;
      }
      else if (allele1 != null && allele1.length() > 20)
      {
         errMessages.addElement("Allele1 [" + allele1 + "] exceeds 20 characters.");
         ret = false;
      }
      else if (allele2 != null && allele2.length() > 20)
      {
         errMessages.addElement("Allele2 [" + allele2 + "] exceeds 20 characters.");
         ret = false;
      }
      else if (raw1 != null && raw1.length() > 20)
      {
         errMessages.addElement("Raw1 [" + raw1 + "] exceeds 20 characters.");
         ret = false;
      }
      else if (raw2 != null && raw2.length() > 20)
      {
         errMessages.addElement("Raw2 [" + raw2 + "] exceeds 20 characters.");
         ret = false;
      }
      else if (ref != null && ref.length() > 32)
      {
         errMessages.addElement("Reference [" + ref + "] exceeds 32 characters.");
         ret = false;
      }
      else if (comm != null && comm.length() > 256)
      {
         errMessages.addElement("Comment exceeds 256 characters.");
         ret = false;
      }
      return ret;
   }

/**
* Checks if the genotype exists, that alleles exists etc.
* Makes certain the genotype can be updated
**/
private void checkListCreateOrUpdate(  String id_or_alias,
                          String ind,
                          String marker,
                          String allele1,
                          String allele2,
                          String raw1,
                          String raw2,
                          String reference,
                          String comment,
                          int suid,
                          Vector errorMessages,
                          Vector warningMessages,
                          Vector deviationMessages,
                          Vector databaseValues,
                          char delim,
                         Statement stmt)
{
  ResultSet rset;
  String identity=null;
  boolean match=false;

  try
  {
  String mname=null;
  String a1=null;
  String a2=null;
  String r1=null;
  String r2=null;
  String ref=null;
  String comm =null;


      //First if no IDENTITY was sent, we need to get it through alias!
      if(id_or_alias.equalsIgnoreCase("ALIAS")&& ind!=null
         && !ind.trim().equalsIgnoreCase(""))
      {
        rset = stmt.executeQuery("SELECT IDENTITY FROM gdbadm.INDIVIDUALS WHERE " +
                              " ALIAS=" + "'"+ind +"'"+
                              " AND SUID=" +"'"+suid+"'");

        if(rset.next())
          identity=rset.getString("IDENTITY");
        else
          identity="-1";
      }
      else
        identity=ind;

      //compare to database, does genotype exist? update or create?
      rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_GENOTYPES_3 WHERE " +
                              "MNAME=" +"'"+ marker+"'" +
                              " AND IDENTITY=" + "'"+identity +"'"+
                              " AND SUID=" +"'"+suid+"'");
      if (rset.next())
      {
          // Genotype exists -- check for update
          // do the alleles exist for these markers??
          //Only check if the new desired value is not null
           if(allele1 != null && !allele1.trim().equalsIgnoreCase(""))
          {
            rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                " MNAME=" + "'"+marker+"'"+
                                " AND NAME=" +"'"+allele1+"'" +
                                " AND SUID=" +"'"+suid+"'");
            if (!rset.next())
            {
              // the allele does not exist
              String Message =" Warning! Allele "+allele1+" does not exist for marker "+marker +". It will be created if genotype is imported!";
              warningMessages.addElement(Message);
            }
          }
          if(allele2 != null && !allele2.trim().equalsIgnoreCase(""))
          {

            // do the alleles exist for these markers??
            rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                  " MNAME=" + "'"+marker+"'"+
                                  " AND NAME=" +"'"+allele2+"'"+
                                  " AND SUID=" +"'"+suid+"'");
            if (!rset.next())
            {
            // the allele does not exist
            String Message =" Warning! Allele "+allele2+" does not exist for marker "+marker;
            warningMessages.addElement(Message);
            }
          }

          // compare to database
          rset = stmt.executeQuery("SELECT IDENTITY,MNAME, A1NAME, A2NAME,"+
                              " RAW1, RAW2,REFERENCE,COMM FROM gdbadm.V_GENOTYPES_3 WHERE "+
                              " MNAME=" +"'"+ marker+"'" +
                              " AND IDENTITY="+"'"+identity +"'"+
                              " AND SUID=" +"'"+suid+"'");
          if (rset.next())
          {
          // get database values
            mname=rset.getString("MNAME");
            a1=rset.getString("A1NAME");
            a2=rset.getString("A2NAME");
            r1=rset.getString("RAW1");
            r2=rset.getString("RAW1");
            ref=rset.getString("REFERENCE");
            comm =rset.getString("COMM");
            //System.err.println("mname1="+mname+"\n");
            //if any of these are null, we need to set nothing.
            if(a1==null)
            a1="";
            if(a2==null)
             a2="";
            if(r1==null)
            r1="";
            if(r2==null)
             r2="";
            if(ref==null)
            ref="";
            if(comm==null)
            comm="";
          // compare allele values
            if(a1.equalsIgnoreCase(allele1) && a2.equalsIgnoreCase(allele2))
            {
              match = true;
            }
            else if(a1.equalsIgnoreCase(allele2) && a2.equalsIgnoreCase(allele1))
            {
              match = true;
            }
            if(!match)
            {
            // genotype differs
            deviationMessages.addElement("#Genotype differs from database, se below (old top, new bottom)");

            //System.err.println("mname="+mname+"\n");
            databaseValues.addElement(ind+delim+mname+delim+a1+delim+a2+delim+r1+delim+r2+delim
                        + ref+delim+comm);

            }// match
        }// rset
      }
      else // genotype does not exist, create
      {

        // Does the individual exist?
        rset = stmt.executeQuery("SELECT IID FROM gdbadm.INDIVIDUALS WHERE " +
                                  " IDENTITY=" + "'"+identity +"'"+
                                  " AND SUID=" +"'"+suid+"'");
        if (!rset.next())
        {
          // the Individual does not exist
          String Message =" The Individual with "+ id_or_alias+" "+ind+" does not exist.";
          errorMessages.addElement(Message);
        }
        // does marker exist?
        rset = stmt.executeQuery("SELECT MID FROM gdbadm.MARKERS WHERE " +
                                  " NAME=" + "'"+marker+"'"+
                                  " AND SUID=" +"'"+suid+"'");

        if (!rset.next())
        {
          // the marker does not exist
          String Message =" Marker "+marker+" does not exist";
          errorMessages.addElement(Message);
        }
        else
        {
         if(allele1!=null && !allele1.equals(""))
         {
            // do the alleles exist for these markers??
            rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                  " MNAME=" + "'"+marker+"'"+
                                  " AND NAME=" +"'"+allele1+"'" +
                                  " AND SUID=" +"'"+suid+"'");
            if (!rset.next())
            {
              // the allele does not exist
              String Message =" Warning! Allele "+allele1+" does not exist for marker "+marker +". It will be created if genotype is created!";

              warningMessages.addElement(Message);
            }
          }

         if(allele2!=null && !allele2.equals(""))
         {
           // do the alleles exist for these markers??
            rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                   " MNAME=" + "'"+marker+"'"+
                                   " AND NAME=" +"'"+allele2+"'"+
                                   " AND SUID=" +"'"+suid+"'");

          if (!rset.next())
          {
            // the allele does not exist
            String Message =" Warning! Allele "+allele2+" does not exist for marker "+marker+". It will be created if genotype is created!";


            warningMessages.addElement(Message);
          }
        }
      }
  }
    rset.close();
  }// try
  catch (Exception e)
  {
         // Flag for error and set the errMessage if it has not been set
         e.printStackTrace(System.err);
  }
}

/**
* Checks if the genotype exists, that alleles exists etc.
* Makes certain the genotype can be updated
**/
private void checkListUpdate(  String id_or_alias,
                          String ind,
                          String marker,
                          String allele1,
                          String allele2,
                          String raw1,
                          String raw2,
                          String reference,
                          String comment,
                          int suid,
                          Vector errorMessages,
                          Vector deviationMessages,
                          Vector warningMessages,
                          Vector databaseValues,
                          char delim,
                         Statement stmt)
{
  ResultSet rset;
  String identity=null;
  boolean match=false;

  try
  {
  String mname=null;
  String a1=null;
  String a2=null;
  String r1=null;
  String r2=null;
  String ref=null;
  String comm =null;


      //First if no IDENTITY was sent, we need to get it through alias!
      if(id_or_alias.equalsIgnoreCase("ALIAS")&& ind!=null
         && !ind.trim().equalsIgnoreCase(""))
      {
        rset = stmt.executeQuery("SELECT IDENTITY FROM gdbadm.INDIVIDUALS WHERE " +
                              " ALIAS=" + "'"+ind +"'"+
                              " AND SUID=" +"'"+suid+"'");

        if(rset.next())
          identity=rset.getString("IDENTITY");
        else
          identity="-1";
      }
      else
        identity=ind;

      //compare to database, does genotype exist? can it be updated..
      rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_GENOTYPES_3 WHERE " +
                              "MNAME=" +"'"+ marker+"'" +
                              " AND IDENTITY=" + "'"+identity +"'"+
                              " AND SUID=" +"'"+suid+"'");
      if (rset.next())
      {// Genotype exists
        // do the alleles exist for these markers??
        // only check if allele not null
        if(allele1 != null && !allele1.trim().equalsIgnoreCase(""))
        {

          rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                  " MNAME=" + "'"+marker+"'"+
                                  " AND NAME=" +"'"+allele1+"'" +
                                  " AND SUID=" +"'"+suid+"'");
          if (!rset.next())
          {
            // the allele does not exist
            String Message =" Warning! Allele "+allele1+" does not exist for marker "+marker+". It will be created if genotype is imported!";
            warningMessages.addElement(Message);
          }
        }

        if(allele2 != null && !allele2.trim().equalsIgnoreCase(""))
        {

          // do the alleles exist for these markers??
          rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                  " MNAME=" + "'"+marker+"'"+
                                  " AND NAME=" +"'"+allele2+"'"+
                                  " AND SUID=" +"'"+suid+"'");
          if (!rset.next())
          {
           // the marker does not exist
           String Message =" Warning! Allele "+allele2+" does not exist for marker "+marker+". It will be created if genotype is imported!";
           warningMessages.addElement(Message);
          }
        }
        //----------------
        // compare to database
        rset = stmt.executeQuery("SELECT IDENTITY,MNAME, A1NAME, A2NAME,"+
                              " RAW1, RAW2,REFERENCE,COMM FROM gdbadm.V_GENOTYPES_3 WHERE "+
                              " MNAME=" +"'"+ marker+"'" +
                              " AND IDENTITY="+"'"+identity +"'"+
                              " AND SUID=" +"'"+suid+"'");
        if (rset.next())
        {
          // get database values
          mname=rset.getString("MNAME");
          a1=rset.getString("A1NAME");
          a2=rset.getString("A2NAME");
          r1=rset.getString("RAW1");
          r2=rset.getString("RAW1");
          ref=rset.getString("REFERENCE");
          comm =rset.getString("COMM");
          //System.err.println("mname1="+mname+"\n");
          //if any of these are null, we need to set nothing.
          if(a1==null)
           a1="";
          if(a2==null)
             a2="";
          if(r1==null)
           r1="";
          if(r2==null)
            r2="";
          if(ref==null)
            ref="";
          if(comm==null)
            comm="";
          // compare allele values
          if(a1.equalsIgnoreCase(allele1) && a2.equalsIgnoreCase(allele2))
          {
            match = true;
          }
          else if(a1.equalsIgnoreCase(allele2) && a2.equalsIgnoreCase(allele1))
          {
            match = true;
          }
          if(!match)
          {
            // genotype differs
            deviationMessages.addElement("#Genotype differs from database, se below (old top, new bottom)");

             //System.err.println("mname="+mname+"\n");
            databaseValues.addElement(ind+delim+mname+delim+a1+delim+a2+delim+r1+delim+r2+delim
                        + ref+delim+comm);

          }
        }
      }
      else // genotype does not exist
      {
            String Message ="The Genotype does not exist, cannot be updated.";
            errorMessages.addElement(Message);
      }
    rset.close();
  }// try
  catch (Exception e)
  {
         // Flag for error and set the errMessage if it has not been set
         e.printStackTrace(System.err);
  }
}
/**
* Compares all values to whats already in the database
* returns the number of errors found.
**/
private void checkListCreate( String id_or_alias,
                          String ind,
                          String marker,
                          String allele1,
                          String allele2,
                          String raw1,
                          String raw2,
                          String ref,
                          String comm,
                          int suid,
                          Vector errorMessages,
                          Vector warningMessages,
                         Statement stmt)
{

  ResultSet rset;
  int nrErrors=0;
  String identity=null;
 try
 {
   //First if no IDENTITY was sent, we need to get it through alias!
   if(id_or_alias.equalsIgnoreCase("ALIAS")&& ind!=null
       && !ind.trim().equalsIgnoreCase(""))
   {

    rset = stmt.executeQuery("SELECT IDENTITY FROM gdbadm.INDIVIDUALS WHERE " +
                            " ALIAS=" + "'"+ind +"'"+
                            " AND SUID=" +"'"+suid+"'");

      if(rset.next())
      {
        identity=rset.getString("IDENTITY");
      }
      else
      {
        identity="-1";
      }
   }
   else
   {
      identity=ind;
   }

  //compare to database, is this unique? (can it be inserted?)
  rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_GENOTYPES_3 WHERE " +
                            "MNAME=" +"'"+ marker+"'" +
                            " AND IDENTITY=" + "'"+identity +"'"+
                            " AND SUID=" +"'"+suid+"'");


  if (rset.next())
  {
     // the genotype exists
    String Message =" The Genotype already exists, cannot be created.";
    errorMessages.addElement(Message);
    nrErrors ++;
    }

    // Does the individual exist?
    rset = stmt.executeQuery("SELECT IID FROM gdbadm.INDIVIDUALS WHERE " +
                              " IDENTITY=" + "'"+identity +"'"+
                              " AND SUID=" +"'"+suid+"'");
    if (!rset.next())
    {
      // the Individual does not exist
      String Message =" The Individual with "+ id_or_alias+" "+ind+" does not exist.";
      errorMessages.addElement(Message);
    }
    // does marker exist?
    rset = stmt.executeQuery("SELECT MID FROM gdbadm.MARKERS WHERE " +
                              " NAME=" + "'"+marker+"'"+
                              " AND SUID=" +"'"+suid+"'");

    if (!rset.next())
    {
      // the marker does not exist
      String Message =" Marker "+marker+" does not exist";
      errorMessages.addElement(Message);
    }
    else
    {
      // do the alleles exist for these markers??
      rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                " MNAME=" + "'"+marker+"'"+
                                " AND NAME=" +"'"+allele1+"'" +
                                " AND SUID=" +"'"+suid+"'");
      if (!rset.next())
      {
        // the allele does not exist
        String Message =" Warning! Allele "+allele1+" does not exist for marker "+marker+". It will be created if genotype is imported!";
        warningMessages.addElement(Message);
      }

      // do the alleles exist for these markers??
      rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                               " MNAME=" + "'"+marker+"'"+
                               " AND NAME=" +"'"+allele2+"'"+
                               " AND SUID=" +"'"+suid+"'");

      if (!rset.next())
      {
        // the marker does not exist
        String Message =" Warning! Allele "+allele2+" does not exist for marker "+marker+". It will be created if genotype is imported!";
        warningMessages.addElement(Message);
      }
      rset.close();
    }
  }
  catch (Exception e)
  {
         // Flag for error and set the errMessage if it has not been set
         e.printStackTrace(System.err);
  }

}

/**
 * Compares all values to whats already in the database
 * returns the number of errors found.
 **/
private void checkMatrixCreateOrUpdate( String id_or_alias,String ind,
                                      String marker,String allele1,String allele2,
                                      int suid,Vector errorMessages,Vector warningMessages,
                                      Vector deviationMessages, Vector databaseValues,Statement stmt)
{
  ResultSet rset;
  int nrErrors=0;
  String identity=null;
  int nrDeviations=0;
  boolean match=false;
 try
 {
   //First if no IDENTITY was sent, we need to get it through alias!
   if(id_or_alias.equalsIgnoreCase("ALIAS")&& ind!=null
       && !ind.trim().equalsIgnoreCase(""))
   {

    rset = stmt.executeQuery("SELECT IDENTITY FROM gdbadm.INDIVIDUALS WHERE " +
                            " ALIAS=" + "'"+ind +"'"+
                            " AND SUID=" +"'"+suid+"'");

      if(rset.next())
      {
        identity=rset.getString("IDENTITY");
      }
      else
      {
        identity="-1";
      }
   }
   else
   {
      identity=ind;
   }

  //compare to database, should this be created?
  rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_GENOTYPES_3 WHERE " +
                            "MNAME=" +"'"+ marker+"'" +
                            " AND IDENTITY=" + "'"+identity +"'"+
                            " AND SUID=" +"'"+suid+"'");


  if (rset.next())
  {
      // Genotype exists
        // do the alleles exist for these markers??
     if(allele1 != null && !allele1.trim().equalsIgnoreCase(""))
     {

        rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                " MNAME=" + "'"+marker+"'"+
                                " AND NAME=" +"'"+allele1+"'" +
                                " AND SUID=" +"'"+suid+"'");
        if (!rset.next())
        {
          // the allele does not exist
          String Message =" Warning! Allele "+allele1+" does not exist for marker "+marker+". It will be created if genotype is imported!";
          warningMessages.addElement(Message);
          //nrErrors ++;
        }
     }
     if(allele2 != null && !allele2.trim().equalsIgnoreCase(""))
     {

        // do the alleles exist for these markers??
        rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                " MNAME=" + "'"+marker+"'"+
                                " AND NAME=" +"'"+allele2+"'"+
                                " AND SUID=" +"'"+suid+"'");
        if (!rset.next())
        {
          // the marker does not exist
          String Message =" Warning! Allele "+allele2+" does not exist for marker "+marker+". It will be created if genotype is imported!";
          warningMessages.addElement(Message);
          //nrErrors ++;
        }
     }


      rset = stmt.executeQuery("SELECT IDENTITY,MNAME, A1NAME, A2NAME"+
                              " FROM gdbadm.V_GENOTYPES_3 WHERE "+
                              " MNAME=" +"'"+ marker+"'" +
                              " AND IDENTITY="+"'"+identity +"'"+
                              " AND SUID=" +"'"+suid+"'");

        rset.next();
        // get database values
        String mname=rset.getString("MNAME");
        String a1=rset.getString("A1NAME");
        String a2=rset.getString("A2NAME");
        //if any of these are null, we need to set nothing.
        if(a1==null)
          a1="";
        if(a2==null)
          a2="";

        // compare allele values
        if(a1.equalsIgnoreCase(allele1) && a2.equalsIgnoreCase(allele2))
        {
          match = true;
        }
        else if(a1.equalsIgnoreCase(allele2) && a2.equalsIgnoreCase(allele1))
        {
          match = true;
        }
        if(!match)
        {

          deviationMessages.addElement(marker+": Genotype differs from database.");
          nrDeviations++;

        }
        databaseValues.addElement(a1);
        databaseValues.addElement(a2);
    }
    else
    {
      // the gontype does not exist-must be created
      // Does the individual exist?
      rset = stmt.executeQuery("SELECT IID FROM gdbadm.INDIVIDUALS WHERE " +
                              " IDENTITY=" + "'"+identity +"'"+
                              " AND SUID=" +"'"+suid+"'");
      if (!rset.next())
      {
        // the Individual does not exist
        String Message =" The Individual with "+ id_or_alias+" "+ind+" does not exist.";
        errorMessages.addElement(Message);
        nrErrors ++;
      }
      // does marker exist?
      rset = stmt.executeQuery("SELECT MID FROM gdbadm.MARKERS WHERE " +
                                " NAME=" + "'"+marker+"'"+
                                " AND SUID=" +"'"+suid+"'");

     if (!rset.next())
     {
       // the marker does not exist
        String Message =" Marker "+marker+" does not exist";
        errorMessages.addElement(Message);
        nrErrors ++;
      }
      else
      {
        if(allele1 !=null && !allele1.equals(""))
        {
          // do the alleles exist for these markers??
          rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                  " MNAME=" + "'"+marker+"'"+
                                  " AND NAME=" +"'"+allele1+"'" +
                                  " AND SUID=" +"'"+suid+"'");

          if (!rset.next())
          {
            // the allele does not exist
            String Message = " Warning!"+marker+": Allele "+allele1+" does not exist. Will be created if genotype is created!";
            warningMessages.addElement(Message);
            //nrErrors ++;
          }
        }

        if(allele2!=null && !allele2.equals(""))
        {
          // do the alleles exist for these markers??
          rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                 " MNAME=" + "'"+marker+"'"+
                                 " AND NAME=" +"'"+allele2+"'"+
                                 " AND SUID=" +"'"+suid+"'");

          if (!rset.next())
          {
            // the allele does not exist
            String Message = " Warning!"+marker+": Allele "+allele2+" does not exist. Will be created if genotype is imported created!";
           warningMessages.addElement(Message);
         //nrErrors ++;
        }
      }
     }
    }
        rset.close();
  }
  catch (Exception e)
  {
         // Flag for error and set the errMessage if it has not been set
         e.printStackTrace(System.err);
  }
     //   int []errAndDev = new int[2];
     //   errAndDev[0]=nrErrors;
     //   errAndDev[1]=nrDeviations;
        //return nrErrors;
      //  return errAndDev;

}

}

