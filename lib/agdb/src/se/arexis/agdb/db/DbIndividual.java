/*
  Copyright (C) 2000 by Prevas AB. All rights reserved.

  $Log$
  Revision 1.14  2005/03/24 15:12:44  heto
  Working with removing oracle dep.

  Revision 1.13  2005/03/22 16:22:59  heto
  Removing CallableStatement.
  Fixed bugs in GUI

  Revision 1.12  2005/02/21 11:55:42  heto
  Converting Genotypes to PostgreSQL

  Revision 1.11  2005/02/08 16:03:21  heto
  DbIndividual is now complete. Some bug tests are done.
  DbSamplingunit is converted. No bugtest.
  All transactions should now be handled in the GUI (yuck..)

  Revision 1.10  2005/02/07 15:54:01  heto
  Converted DbIndividual to PostgreSQL
  Now some transaction problem occures with Groupings (update)

  Revision 1.9  2005/02/04 15:58:40  heto
  Converting from Oracle to PostgreSQL or somewhat more SQL server independence.

  Revision 1.8  2004/04/05 10:55:15  wali
  Added log messages.

  Revision 1.7  2004/04/02 08:11:25  heto
  Loading data to test objects from db

  Revision 1.6  2004/04/01 15:03:13  heto
  Added Grouping format

  Revision 1.5  2004/03/09 14:19:21  heto
  Fixed alot of bugs in else if clauses then checking syntax for values

  Revision 1.4  2003/05/08 13:00:39  heto
  Added functions to support the check-system for file import.

  Revision 1.3  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.2  2002/11/13 09:08:24  heto
  Changed a stupid if-clause.

  Revision 1.1.1.1  2002/10/16 18:14:06  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.5  2001/05/14 06:01:57  frob
  Bugfixes: added missing checks of the string returned from the database.

  Revision 1.4  2001/05/11 08:10:18  frob
  Bugfix in createGrouping().

  Revision 1.3  2001/04/24 09:34:06  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:39  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.1.1.2.4  2001/04/11 09:44:03  frob
  Added a file type definition.

  Revision 1.1.1.1.2.3  2001/04/11 08:56:59  frob
  Added static initializer which registers known file type definitions.

  Revision 1.1.1.1.2.2  2001/03/29 11:12:49  frob
  Changed calls to buildErrorString. All calls now passes the
  result from the dataRow2FileRow method as the row parameter.
  Added header and fixed indentation.

  Revision 1.1.1.1.2.1  2001/03/28 12:51:59  frob
  Changed calls to FileParser.getTitles() to FileParser.columnTitles()
  and FileParser.getRows() to FileParser.dataRows().
  Indeted the file and added the log header.


*/
package se.arexis.agdb.db;

import java.util.*;
import java.sql.*;
import se.arexis.agdb.db.TableClasses.IndividualDO;
import se.arexis.agdb.db.TableClasses.MarkerDO;
import se.arexis.agdb.util.*;
import se.arexis.agdb.util.FileImport.*;

/**
 * This class provides an api of methods to
 * handle individuals in the database.
 * @author Tomas Bjorklund, Prevas AB
 */
public class DbIndividual extends DbObject
{
   static
   {
      try
      {
         // Register known FileTypeDefinitions
         FileTypeDefinitionList.add(FileTypeDefinition.INDIVIDUAL ,
                                    FileTypeDefinition.LIST, 1);
         FileTypeDefinitionList.add(FileTypeDefinition.GROUPING,
                                    FileTypeDefinition.LIST, 1);
         FileTypeDefinitionList.add(FileTypeDefinition.SAMPLE,
                                    FileTypeDefinition.LIST, 1);
      }
      catch (FileTypeDefinitionException e)
      {
         System.err.println("Construction of new FileTypeDefinition " +
                            "failed: " + e.getMessage());
         System.exit(1);
      }
   }

   
   private Vector m_grps = null;
   private Vector m_grp = null;

   /**
    * Constructor
    */
   public DbIndividual() 
   {
      m_grps = new Vector();
      m_grp = new Vector();
   }
   
   /**
    * Check the individuals father and mother, also check the birth dates for 
    * meaningful values.
    */
   public int checkIndividual(Connection conn, int iid)
    throws DbException
   {
       int status = 0;
       try
       {
           //Errors.logDebug("iid="+iid);
           
           IndividualDO ind = getIndividual(conn,iid);
           
           //Errors.logDebug("fatherId="+ind.getFather());
           //Errors.logDebug("motherId="+ind.getMother());
           
           
           if (ind.getFather() != 0)
           {
               IndividualDO father = getIndividual(conn,ind.getFather());
               
               if (father==null)
                   throw new DbException("father is null! ["+ind.getFather()+"]");
               
               if (father!=null && !father.getSex().equals("M")) // Male
                   status = status + 1;
               if (father!=null && !father.getStatus().equals("E"))
                   status = status + 16;
               if (father!=null && father.getBirthDate() != null 
                       && ind.getBirthDate() != null 
                       && (father.getBirthDate().after(ind.getBirthDate()) 
                            || father.getBirthDate().equals(ind.getBirthDate()))) 
                   status = status + 2;
           }
           
           if (ind.getMother() != 0)
           {
               IndividualDO mother = getIndividual(conn,ind.getMother());
               
               if (mother==null)
                    throw new DbException("mother is null! ["+ind.getMother()+"]");
               
               if (mother!=null && !mother.getSex().equals("F")) // Female
                   status = status + 4;
               if (mother!=null && !mother.getStatus().equals("E"))
                   status = status + 32;
               if (mother!=null && mother.getBirthDate() != null 
                       && ind.getBirthDate() != null 
                       && (mother.getBirthDate().after(ind.getBirthDate()) 
                            || mother.getBirthDate().equals(ind.getBirthDate()))) 
                   status = status + 8;
           }
       }
       catch (DbException e)
       {
           throw e;
       }
       catch (Exception e)
       {
           e.printStackTrace();
           throw new DbException("Internal error. Failed to check individual in the database.\n"+e.getMessage());
       }
       return status;
   }
   
   
   /**
    * Delete the link between Individuals and groups
    * @param conn The connection
    * @param iid the identity integer value
    * @param gid the group integer value
    * @param id the user id preforming the operation
    * @throws se.arexis.agdb.db.DbException Throws messages to the GUI
    */
   public void createGroupLink(Connection conn, int iid, int gid, int id)
    throws DbException
   {
       String sql = "";
       Statement stmt = null;
       try
       {    
           stmt = conn.createStatement();
           
           sql = "insert into R_Ind_Grp values("+iid+", "+gid+", "+id+", "+getSQLDate()+")";
           stmt.execute(sql);          
       }
       catch (Exception e)
       {
           e.printStackTrace();
           Errors.logError("SQL="+sql);
           
           
           if (e.getMessage().indexOf("iid") != 0)
                throw new DbException("Error on individual: "+e.getMessage());
           else
                throw new DbException("Unable to create the GroupLink in the database: "+e.getMessage());
       }
       finally 
       {
           try
           {
               stmt.close();
           }
           catch (Exception ignore)
           {}
       }
   }

   /**
    * Check the fileformat of the groupings file
    * @param titles The titles from the fileparser object. Check that all columns are present.
    * @throws se.arexis.agdb.db.DbException If file format is not ok, an exeption is thrown
    */
   private void checkGroupingsFileFormat(String titles[]) throws DbException
   {
      // First we better check that the file is in a valid grouping file
      if (titles.length < 2 || (!titles[0].equals("IDENTITY") && !titles[0].equals("ALIAS"))) 
      {
         String errStr="Illegal headers.<BR>"+
            "Required file headers: IDENTITY/ALIAS GROUPING1 GROUPING2 ...<BR>"+
            "Headers found in file:";
         for (int j=0; j<titles.length;j++) 
         {
            errStr = errStr+ " " + titles[j];
         }

         throw new DbException(errStr);             
      }
   }
   
   /**
    * Creates a batch of groupings and groups from file.
    * The file should be wrapped by a FileParser and the
    * fileparsers <code>parse</code> method should alraedy
    * have been called.
    * @param fp The FileParser object for import data.
    * @param conn The connection to the database
    * @param suid The sampling unit id
    * @param id The users id preforming the operation
    * @throws se.arexis.agdb.db.DbException If errors occurs this throws error messages to display to the user
    */
   public void CreateGroupings(FileParser fp, Connection conn, int suid, int id) 
        throws DbException
   {
      String ind, group_name, indId;
      String grouping_names[];
      String titles[];      
      int grouping_id;
      int group_id;
      boolean knownIdentity;
      
      grouping_names = fp.columnTitles(); // This first element is "IDENTITY"
      titles = fp.columnTitles();
      checkGroupingsFileFormat(titles);
              
      if (titles[0].equals("IDENTITY")) 
      {
         knownIdentity = true;
         indId = "IDENTITY";
      } 
      else 
      {
         knownIdentity = false;
         indId = "ALIAS";
      }

      // Let us first create the groupings!
      try 
      {         
         for (int gps = 1; gps < grouping_names.length; gps++) 
         {
            grouping_id = CreateGrouping(conn,id,grouping_names[gps],null,suid);
            addGrouping(grouping_names[gps], grouping_id);
         }
         
         // Outer loop traverses the rows
         for (int row = 0; row < fp.dataRows(); row++) 
         {
            ind = fp.getValue(indId, row);
            
            // Inner loop traverses the cols
            for (int col = 1; col < grouping_names.length; col++) 
            {
               group_name = fp.getValue(grouping_names[col], row);
               if (!"".equals(group_name.trim())) 
               {
                  grouping_id = getGroupingId(grouping_names[col]);
                  if (getGroupId(grouping_names[col], group_name) < 0) 
                  {
                     group_id = CreateGroup(conn,id,group_name,null,grouping_id);
                     addGroup(grouping_names[col], group_name, group_id);
                  }
                 
                  int iid = 0;
                  if (knownIdentity)
                  {
                      iid = getIID(conn,ind,null,suid);
                  }
                  else
                  {
                      // Alias
                      iid = getIID(conn,null,ind,suid);
                  }
                  int gid = getGroupId(grouping_names[col], group_name);
                  createGroupLink(conn,iid,gid,id);
               }
            } // End of inner loop
         } // End of outer loop

      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         buildErrorString("Internal error. Failed to call PL/SQL procedure\n(" +
                          e.getMessage() + ")");
      } 
   }
   
   /**
    * Set the parents (father_id and mother_id) to an individual (ind_id).
    * @param conn The database connection
    * @param father_id the fathers iid value
    * @param mother_id the mothers iid value
    * @param ind_id the individuals iid value (whos parents are to be changed)
    * @throws se.arexis.agdb.db.DbException Throws error messages to the user
    */
   public void SetParents(Connection conn, int father_id, int mother_id, int ind_id)
        throws DbException
   {
       String sql = "";
       Statement stmt = null;
       try
       {
           stmt = conn.createStatement();
           sql = "update Individuals set father = "+father_id+", mother = "+mother_id+" where iid = "+ind_id;
           stmt.execute(sql);
       }
       catch (Exception e)
       {
           e.printStackTrace();
           Errors.logError("SQL="+sql);
           
           throw new DbException("Could not set Parents information for individual ["+ind_id+"]");
       }
       finally
       {
           try
           {
               if (stmt!=null)
                    stmt.close();
           }
           catch (Exception ignore)
           {}
       }
   }
   
   /**
    * Check if the fileformat is ok with all columns. If not throw DbException
    * @param titles The titles in the file parser object
    * @throws se.arexis.agdb.db.DbException If format is not ok, DbException is thrown to display messages to the user
    */
   private void checkIndividualFileFormat(String[] titles) throws DbException
   {
       // Check file format
      if ((!titles[0].equals("IDENTITY") ||
               !titles[1].equals("ALIAS") ||
               !titles[2].equals("FATHER") ||
               !titles[3].equals("MOTHER") ||
               !titles[4].equals("SEX") ||
               !titles[5].equals("BIRTH_DATE") ||
               !titles[6].equals("COMMENT")) || 
               (titles.length < 7))
      {
         String errStr="Illegal headers.<BR>"+
            "Required file headers: IDENTITY ALIAS FATHER MOTHER SEX BIRTH_DATE COMMENT<BR>"+
            "Headers found in file:";
         for (int j=0; j<titles.length;j++) {
            errStr = errStr+ " " + titles[j];
         }
         
         throw new DbException(errStr);
      }
   }

   /**
    * Creates a batch of individuals in a sampling unit.
    * The file containing the individuals should be wrapped in
    * a FileParser object. The fileparse should be prepared in the
    * way that the method <code>parse</code> should already have been
    * called.
    * @param fp The fileParser object for import
    * @param conn The connection to the database
    * @param suid The sampling unit id
    * @param id The id of the user importing the file
    * @throws se.arexis.agdb.db.DbException Throws DbException to display error messages to the user
    */
   public void CreateIndividuals(FileParser fp, Connection conn, int suid, int id)
        throws DbException
   {
        String identity, alias, sex, comment;
        int groupingid;
        int groupid, iid, bd_year, bd_month, bd_day; //, father, mother;
        String father, mother, temp; //, family_name, generation_name;
        String group_name;
        String[] grouping_names;
        int maxNoOfGroupings;

        //java.sql.Date birth_date = null;

        String[] titles;
        titles = fp.columnTitles();
        checkIndividualFileFormat(titles);
      

    
        // The file should follow this format:
        // IDENTITY | ALIAS | FATHER | MOTHER | SEX | BIRTH_DATE | COMMENT | grouping1 | grouping2 etc
        // Which means that everyting above 7 columns must be groupings
        maxNoOfGroupings = titles.length - 7;
        Errors.logDebug("maxNoOfGroupings="+maxNoOfGroupings);
        grouping_names = new String[maxNoOfGroupings];
        for (int i = 0; i < maxNoOfGroupings; i++)
            grouping_names[i] = new String(titles[i + 7]);
         
        try 
        {
            for (int i = 0; i < grouping_names.length; i++) 
            {
                // Create the objects in the db.
                groupingid = CreateGrouping(conn,id, grouping_names[i], null,suid);
                
                // Add grouping names and id's to an array
                addGrouping(grouping_names[i], groupingid); 
            }
            
            for (int i = 0; i < fp.dataRows(); i++)
            {
               identity = fp.getValue("IDENTITY", i);
               alias = fp.getValue("ALIAS", i);
               sex = fp.getValue("SEX", i);
               temp = fp.getValue("BIRTH_DATE", i);
               comment = fp.getValue("COMMENT", i);
               
               checkIndValues(identity, alias, sex, temp, comment,
                                  fp.dataRow2FileRow(i) + 1);
                                  
              Errors.logDebug("Before CreateIndividuals i="+i+" dataRows="+fp.dataRows());
               
              // Father & mother is null for now. (Dependency problem)
              iid = CreateIndividual(conn, id, identity, alias, null, null, sex, temp.toString(), null, suid);
               
               for (int n = 0; n < grouping_names.length; n++) 
               {
                  group_name = fp.getValue(grouping_names[n], i);
                  int tmp_grpid = getGroupingId(group_name);
                  
                  createGroupLink(conn, iid, tmp_grpid, id);
               }
            }

            // Now, let's set the attributes 'father' and 'mother'
            for (int i = 0; i < fp.dataRows(); i++) 
            {
               identity = fp.getValue("IDENTITY", i);
               father = fp.getValue("FATHER", i);
               mother = fp.getValue("MOTHER", i);
               
               int father_id = getIID(conn,father,null,suid);
               int mother_id = getIID(conn,mother,null,suid);
               iid = getIID(conn,identity,null,suid);
               
               if (father!=null || mother!=null)
                    SetParents(conn, father_id, mother_id, iid);
            }
        } 
        catch (DbException e)
        {
            throw e;
        }
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            throw new DbException("Failed to create individuals");
        }
   }

   /**
    * Updates a batch of individuals from file. The file should be wrapped
    * by a FileParser object and the <code>parse</code> method of the
    * FileParse should already have been called.
    * @param fp 
    * @param conn 
    * @param suid 
    * @param id 
    * @throws se.arexis.agdb.db.DbException 
    */
   public void UpdateIndividuals(FileParser fp, Connection conn, int suid, int id)
        throws DbException
   {
      String identity = "", alias, sex, comment;
      String father, mother, status, temp;
      String message = null;
      
      String titles[];
      titles = fp.columnTitles();
      checkIndividualFileFormat(titles);
      titles = null;

      // The file should follow this format:
      // IDENTITY | ALIAS | FATHER | MOTHER | SEX | BIRTH_DATE | COMMENT | grouping1 | grouping2 etc
      // Which means that everyting above 7 columns must be groupings
      // However when we do an update,we don't really care about the groupings!
      try 
      {
         for (int i = 0; i < fp.dataRows(); i++)
         {
            identity = fp.getValue("IDENTITY", i);
            alias = fp.getValue("ALIAS", i);
            father = fp.getValue("FATHER", i);
            mother = fp.getValue("MOTHER", i);
            sex = fp.getValue("SEX", i);
            temp = fp.getValue("BIRTH_DATE", i);
            comment = fp.getValue("COMMENT", i);
            
            checkIndValues(identity, alias, sex, temp, comment, i);
            
            int iid = getIID(conn,identity,alias,suid);
            UpdateIndividual(conn, id, iid, identity, alias, father, mother, sex, "E", temp,comment);
         }
      }
      catch (DbException e)
      {
          throw e;
      }
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to update individual [" + 
                 identity + "]\n(" + e.getMessage() + ")");
      } 
   }
   
   

   /**
    * 
    * @param fp 
    * @param conn 
    * @param suid 
    * @param id 
    * @throws se.arexis.agdb.db.DbException 
    */
   public void CreateOrUpdateIndividuals(FileParser fp, Connection conn, int suid, int id)
        throws DbException
   {
      String identity, alias, sex, comment;
      int groupingid;
      int groupid, iid, bd_year, bd_month, bd_day; //, father, mother;
      String father, mother, temp; //, family_name, generation_name;
      String group_name;
      String[] grouping_names;
      String message = null;
      int maxNoOfGroupings;
      
      String[] titles;
      titles = fp.columnTitles();
      checkIndividualFileFormat(titles);

      // The file should follow this format:
      // IDENTITY | ALIAS | FATHER | MOTHER | SEX | BIRTH_DATE | COMMENT | grouping1 | grouping2 etc
      // Which means that everyting above 7 columns must be groupings
      maxNoOfGroupings = titles.length - 7;
      grouping_names = new String[maxNoOfGroupings];
      for (int i = 0; i < maxNoOfGroupings; i++)
         grouping_names[i] = new String(titles[i + 7]);
      
      try 
      {
         int gsid = 0;
         
         
         boolean ok = true;
         for (int i = 0; i < grouping_names.length; i++) 
         {
             gsid = getGSID(conn, suid, grouping_names[i]);
             if (gsid == 0)
                 gsid = CreateGrouping(conn, id, grouping_names[i], null, suid);
             else
                 UpdateGrouping(conn, gsid,grouping_names[i],null,id);
             
            addGrouping(grouping_names[i], gsid);
         }
         
         // First we create all the individuals (Set parents later)
         for (int i = 0; i < fp.dataRows() && ok; i++)
         {
            identity = fp.getValue("IDENTITY", i);
            alias = fp.getValue("ALIAS", i);
            sex = fp.getValue("SEX", i);
            temp = fp.getValue("BIRTH_DATE", i);
            comment = fp.getValue("COMMENT", i);
            
            checkIndValues(identity, alias, sex, temp, comment, i);
            
            iid = getIID(conn,identity,alias,suid);
            if (iid == 0)
                iid = CreateIndividual(conn,id,identity,alias,null,null,sex,temp,comment,suid);
            else
                UpdateIndividual(conn,id,iid,identity,alias,null,null,sex,"E",temp,comment);
            
            
           
            // Create group links
            int gid = 0;
            for (int n = 0; n < grouping_names.length && ok; n++) 
            {
               group_name = fp.getValue(grouping_names[n], i);
               gid = getGroupingId(group_name);
               createGroupLink(conn,iid,gid,id);
               
               
               /*
                  buildErrorString("Failed to link individual [" + identity +
                                   "] to group [" + group_name  +
                                   "] in grouping [" + grouping_names[n] +
                                   "] PL/SQL error [" + message + "]",
                                   fp.dataRow2FileRow(i) + 1);
                */
            }
         }

         // Now, let's set the attributes 'father' and 'mother'
         int father_id = 0, mother_id = 0, ind_id = 0;
         for (int i = 0; i < fp.dataRows(); i++) 
         {
            identity = fp.getValue("IDENTITY", i);
            father = fp.getValue("FATHER", i);
            mother = fp.getValue("MOTHER", i);
            
            father_id = getIID(conn,father,null,suid);
            mother_id = getIID(conn,mother,null,suid);
            ind_id = getIID(conn,father,null,suid);
            
            if (father!=null && mother!=null)
                SetParents(conn, father_id, mother_id, ind_id);
            
            /*
             *if (message != null && !message.trim().equals("")) {
               buildErrorString("Failed to set parents for individual [" +
                                identity + "] PL/SQL error [" +
                                message + "]", fp.dataRow2FileRow(i) + 1);
             */
         }
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         
         
         
         /*
         if (iid==0) //??????
         {
            buildErrorString("Failed to create or update individual [identity=" +
                               identity + "] at row " +
                               (fp.dataRow2FileRow(i) + 1) +
                               " PL/SQL error [" + message + "]",
                               fp.dataRow2FileRow(i) + 1);
         }
          */
         
         throw new DbException("Internal error. Failed to call PL/SQL procedure\n(" +
                          e.getMessage() + ")");
      } 
   }


   /**
    * 
    * @param conn 
    * @param iid 
    * @throws se.arexis.agdb.db.DbException 
    */
   public void DeleteIndividual(Connection conn, int iid)
        throws DbException
   {
      Statement stmt = null;
      String sql = "";
      try 
      {
          stmt = conn.createStatement();
          
          /*
           Delete all genotype logs
           Delete all genotypes
           Delete all phenotype logs
           Delete all phenotypes
           Delete all r_ind_grp (delete cascade)
           *delete all individual logs
           *Delete the individual !!!
           */
                  
                  
          sql = "delete from genotypes_log where iid="+iid+";";
          sql += "delete from genotypes where iid="+iid+";";
          sql += "delete from phenotypes_log where iid="+iid+";";
          sql += "delete from phenotypes where iid="+iid+";";
          sql += "delete from individuals_log where iid="+iid+";";
          sql += "delete from Individuals where iid = "+iid+";";
    
          stmt.execute(sql);
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed delete data for the individual\n(" +
                          e.getMessage() + ")");
      } 
      finally 
      {
         try 
         {
            if (stmt != null) 
                stmt.close();
         } 
         catch (SQLException ignored) 
         {}
      }
   }
   
   /**
    * Updates an individual
    * @param conn 
    * @param id 
    * @param iid 
    * @param identity 
    * @param alias 
    * @param father 
    * @param mother 
    * @param sex 
    * @param status 
    * @param birth_date 
    * @param comment 
    * @throws se.arexis.agdb.db.DbException 
    */
   public void UpdateIndividual(Connection conn,
                                int id,
                                int iid,
                                String identity,
                                String alias,
                                String father,
                                String mother,
                                String sex,
                                String status,
                                String birth_date,
                                String comment )       
                          throws DbException
   {
      Statement stmt = null;
      String sql = "";
      String sql_log = "";
      try 
      {
          
          checkIndValues(identity, alias, sex, birth_date, comment);
                                  
          
          stmt = conn.createStatement();
          
          // Save to log table
          sql_log = "insert into individuals_log " 
                   +"(iid, identity, alias, father, mother, sex, birth_date, status, suid, id, ts, comm) (select iid, identity, alias, father, mother, sex, birth_date, status, suid, id, ts, comm from individuals where iid="+iid+")";
            
          stmt.execute(sql_log);
          Errors.logInfo("Affected rows: "+stmt.getUpdateCount());
          
          sql = "update Individuals set "
                    +"identity = "+sqlString(identity)+", alias = "+sqlString(alias)+", father = "+sqlInteger(father)+", "
                    +"mother = "+sqlInteger(mother)+", sex = "+sqlString(sex)+", birth_date = "+sqlString(birth_date)+", "
                    +"status = "+sqlString(status)+", comm = "+sqlString(comment)+", id = "+id+", ts = "+getSQLDate()+ " "
                    +"where iid = "+iid;
          
          stmt.execute(sql);
          Errors.logInfo("Affected rows: "+stmt.getUpdateCount());
      } 
      catch (DbException e)
      {
          throw e;
      }
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
                  
         throw new DbException("Internal error. Failed to update individual\n(" +
                          e.getMessage() + ")");
      } 
      finally 
      {
         try 
         {
            if (stmt != null) stmt.close();
         } 
         catch (SQLException ignored) 
         {}
      }
   }
   
   /**
    * Check the group information if it is valid.
    * @param name the name of the group. 20 or less characters. No spaces
    * @param comment the comment of the group. Less or equal than 256 characters.
    * @throws se.arexis.agdb.db.DbException 
    */
   public void checkGroupValues(String name, String comment)
        throws DbException
   {
       
        if (name.length() > 20)
            throw new DbException("Name exceeds 20 characters");
        else if (name.contains(" "))
            throw new DbException("Name contains spaces");
        else if (comment!=null && comment.length()>256)
            throw new DbException("Comment exceeds 256 characters");
   }
   
   /**
    * Creates a group
    * @param conn 
    * @param id 
    * @param name 
    * @param comment 
    * @param gsid 
    */
   public int CreateGroup(Connection conn, int id, String name,
                           String comment, int gsid) 
              throws DbException
   {
      Statement stmt = null;
      String sql = "";
      int gid = 0;
      try 
      {
         stmt = conn.createStatement();
          
         checkGroupValues(name, comment);
         
         gid = getNextID(conn,"groups_seq");
         
         sql = "insert into Groups values( " +
                 gid+", "+sqlString(name)+", "+sqlString(comment)+", " +
                 gsid+", "+id+", "+getSQLDate()+");";
         
         stmt.execute(sql);
      } 
      catch (DbException e)
      {
          throw e;
      }
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         Errors.logError("SQL="+sql);
         
         
         throw new DbException("Internal error. Failed to create group\n(" +
                          e.getMessage() + ")");
      } 
      finally 
      {
         try 
         {
            if (stmt != null) stmt.close();
         } 
         catch (SQLException ignored) 
         {}
      }
      return gid;
   }
   /**
    * 
    * @param conn 
    * @param id 
    * @param from_gsid 
    * @param from_gid 
    * @param to_gsid 
    * @param name 
    * @param comment 
    */
   public void CopyGroup(Connection conn, int id, int from_gsid,
                         int from_gid, int to_gsid, String name,
                         String comment) 
                         throws DbException
   {       
      Statement stmt = null;
      String sql = "";
      try 
      {
          checkGroupValues(name,comment);
          
          // Create the group
          int to_gid = CreateGroup(conn,id,name,comment,to_gsid);
          
          stmt = conn.createStatement();
          
          sql = "insert into r_ind_grp (iid,gid,id,ts) (select iid,"+to_gid+","+id+","+getSQLDate()+" from r_ind_grp where gid="+from_gid+")";
          stmt.execute(sql);
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         Errors.logError("SQL="+sql);
         
         throw new DbException("Internal error. Failed to copy group\n(" +
                          e.getMessage() + ")");
      } 
      finally 
      {
         try 
         {
            if (stmt != null) stmt.close();
         } 
         catch (SQLException ignored) 
         {}
      }
   }

   /**
    * Updates a group
    * @param conn 
    * @param gid 
    * @param new_name 
    * @param new_comm 
    * @param id 
    */
   public void UpdateGroup(Connection conn, int gid,
                           String new_name, String new_comm, int id) 
                           throws DbException
   {
      Statement stmt = null;
      String sql = "";
      String sql_log = "";
      try 
      {
          checkGroupValues(new_name,new_comm);
          
          stmt = conn.createStatement();
          
          // Save to log table
          sql_log = "insert into groups_log " 
                   +"(gid,name,comm,id,ts) (select gid,name,comm,id,ts from groups where gid="+gid+")";
            
          stmt.execute(sql_log);
          Errors.logInfo("Affected rows: "+stmt.getUpdateCount());
                        
          sql = "update Groups set name = "+sqlString(new_name)+", comm = "+
                  sqlString(new_comm)+", id = "+id+", ts = "+getSQLDate()+
                  " where gid = "+gid;
          
          stmt.execute(sql);
          Errors.logInfo("Affected rows: "+stmt.getUpdateCount());
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         Errors.logError("SQL_LOG="+sql_log);
         Errors.logError("SQL="+sql);
         
         throw new DbException("Internal error. Failed to update group\n(" +
                          e.getMessage() + ")");
      } 
      finally 
      {
         try 
         {
            if (stmt != null) stmt.close();
         } 
         catch (SQLException ignored) 
         {}
      }
   }
   
    /**
    * Delete a group
    *
    * Delete all R_Ind_Grp (delete cascade gid)
    * Delete all group logs
    * @param conn 
    * @param gid 
    */
   public void DeleteGroupLink(Connection conn, int iid, int gid) throws DbException
   {
      Statement stmt = null;
      String sql = "";
      try 
      {
          stmt = conn.createStatement();
          
          sql = "DELETE FROM R_IND_GRP WHERE IID=" + iid + " AND GID=" + gid;
          stmt.execute(sql);
          Errors.logInfo("Rows affected: "+stmt.getUpdateCount());
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         Errors.logError("SQL="+sql);
                  
         throw new DbException("Internal error. Failed to delete group\n(" +
                          sqle.getMessage() + ")");
      } 
      finally 
      {
         try 
         {
            if (stmt != null) stmt.close();
         } 
         catch (SQLException ignored) 
         {}
      }
   }
   
   
   /**
    * Delete a group
    *
    * Delete all R_Ind_Grp (delete cascade gid)
    * Delete all group logs
    * @param conn 
    * @param gid 
    */
   public void DeleteGroup(Connection conn, int gid) throws DbException
   {
      Statement stmt = null;
      String sql = "";
      try 
      {
          stmt = conn.createStatement();
          
          sql = "delete from groups_log where gid = "+gid;
          stmt.execute(sql);
          Errors.logInfo("Rows affected: "+stmt.getUpdateCount());
          
          sql = "delete from Groups where gid = "+gid;
          stmt.execute(sql);
          Errors.logInfo("Rows affected: "+stmt.getUpdateCount());
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         Errors.logError("SQL="+sql);
         
         throw new DbException("Internal error. Failed to delete group\n(" +
                          sqle.getMessage() + ")");
      } 
      finally 
      {
         try 
         {
            if (stmt != null) stmt.close();
         } 
         catch (SQLException ignored) 
         {}
      }
   }
   
   /**
    * Creates a new grouping
    * @param conn 
    * @param id 
    * @param name 
    * @param comment 
    * @param suid 
    * @throws se.arexis.agdb.db.DbException 
    * @return 
    */
   public int CreateGrouping(Connection conn,
                              int id,
                              String name,
                              String comment,
                              int suid) 
             throws DbException
   {
      Statement stmt = null;
      String sql = "";
      int gsid = 0;
      try 
      {
          stmt = conn.createStatement();
          
          gsid = getNextID(conn, "Groupings_Seq");
          
          
          sql = "insert into Groupings values ("+gsid+", "+sqlString(name)+", "+sqlString(comment)+", "+suid+", "+id+", "+getSQLDate()+")";
          stmt.execute(sql);
          Errors.logInfo("Rows inserted: "+stmt.getUpdateCount());
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         
         throw new DbException("Internal error. Could not insert groupings to the database\n(" +
                          sqle.getMessage() + ")");
      } 
      finally 
      {
         try 
         {
            if (stmt != null) stmt.close();
         } 
         catch (SQLException ignored) {}
      }
      return gsid;
   }



   /**
    * 
    * @param conn 
    * @param id 
    * @param suid 
    * @param from_gsid 
    * @param name 
    * @param comment 
    */
   public int CopyGrouping(Connection conn, int id, int suid, int from_gsid,
                            String name,String comment) throws DbException
   {
      Statement stmt = null;
      String sql = "";
      int gsid = 0;
      try 
      {
          // Create the new grouping
          gsid = CreateGrouping(conn,id,name,comment,suid);
          
          sql = "select name,comm,gid from groups where gsid="+from_gsid;
          stmt = conn.createStatement();
          
          ResultSet rs = stmt.executeQuery(sql);
          
          String grpName = null;
          String grpComm = null;
          int gid = 0;
          while (rs.next())
          {
              grpName = rs.getString("name");
              grpComm = rs.getString("comm");
              gid = rs.getInt("gid");
              
              CopyGroup(conn, id, from_gsid, gid, gsid, grpName, grpComm);
          }
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to copy grouping\n(" +
                          e.getMessage() + ")");
      } 
      finally 
      {
         try 
         {
            if (stmt != null) stmt.close();
         } 
         catch (SQLException ignored) 
         {}
      }
      return gsid;
   }

   /**
    * Update a grouping
    * @param conn 
    * @param gsid 
    * @param new_name 
    * @param new_comm 
    * @param id 
    */
   public void UpdateGrouping(Connection conn, int gsid, String new_name,
                              String new_comm, int id) 
                              
                       throws DbException
   {
       Errors.logDebug("UpdateGrouping(conn,"+gsid+","+new_name+","+new_comm+","+id+") started");
      Statement stmt = null;
      String sql = "";
      String sql_log ="";
      try 
      {
          stmt = conn.createStatement();
          
          // Save to log table
          sql_log = "insert into groupings_log " 
                   +"(gsid,name,comm,id,ts) (select gsid,name,comm,id,ts from groupings where gsid="+gsid+")";
            
          stmt.execute(sql_log);
          Errors.logInfo("Affected rows: "+stmt.getUpdateCount());
          
          
          sql = "update Groupings set name = "+sqlString(new_name)+", comm = "+sqlString(new_comm)+", " +
                  "id = "+id+", ts = " + getSQLDate() +
                  "where gsid = "+ gsid;
          
          stmt.execute(sql);
          Errors.logInfo("Affected rows: "+stmt.getUpdateCount());
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         throw new DbException("Internal error. Failed to update grouping\n(" +
                          e.getMessage() + ")");
      } 
      finally 
      {
         try 
         {
            if (stmt != null) stmt.close();
         } 
         catch (SQLException ignored) 
         {}
      }
      Errors.logDebug("UpdateGrouping(conn,"+gsid+","+new_name+","+new_comm+","+id+") ended");
   }
   
   /**
    * Delete a grouping
    * @param conn The database connection
    * @param gsid the grouping id to be deleted.
    */
   public void DeleteGrouping(Connection conn, int gsid) 
        throws DbException
   {
      Statement stmt = null;
      String sql = "";
      try 
      {
          stmt = conn.createStatement();
          /*
           *-- Delete all R_Ind_grp (delete cascade gid, gsid)
           *-- Delete all group logs
           *-- Delete all groups
           *-- Delete all grouping logs
           *-- Delete the grouping
           */
          
          sql = "delete from groups_log where gid in (select gid from groups where gsid="+gsid+")";
          stmt.execute(sql);
          Errors.logInfo("deleted from groups_log: "+stmt.getUpdateCount());
          
          sql = "delete from groups where gsid = "+gsid;
          stmt.execute(sql);
          Errors.logInfo("deleted from groups_log: "+stmt.getUpdateCount());
          
          sql = "delete from groupings_log where gsid = "+gsid;
          stmt.execute(sql);
          Errors.logInfo("deleted from groups_log: "+stmt.getUpdateCount());
          
          sql ="delete from Groupings where gsid = "+gsid;      
          stmt.execute(sql);
          Errors.logInfo("deleted from groups_log: "+stmt.getUpdateCount());
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         Errors.logError("SQL="+sql);
         
         throw new DbException("Internal error. Failed to delete grouping\n(" +
                          sqle.getMessage() + ")");
      } 
      finally 
      {
         try 
         {    
            if (stmt != null) stmt.close();
         } 
         catch (SQLException ignored) 
         {}
      }
   }
   
 
   
    /**
     * Creates an individual
     * @param conn 
     * @param id 
     * @param identity 
     * @param alias 
     * @param father 
     * @param mother 
     * @param sex 
     * @param birth_date 
     * @param comment 
     * @param suid 
     * @throws se.arexis.agdb.db.DbException 
     * @return 
     */
    public int CreateIndividual(Connection conn, int id, String identity,
                                String alias, String father, String mother, String sex,
                                String birth_date, String comment, int suid) 
             throws DbException
    {
        Errors.logDebug("CreateIndividual("+id+","+identity+")");
        Statement stmt = null;
        String sql = "";
        int iid = 0;
        try 
        {
            checkIndValues(identity,alias,sex,birth_date,comment);

            /*
            check_iid = getIID(conn,)
            if (father does not exist)
              throw new DbException("Father does not exist");
            if (mother does not exist)
              throw new DbException("Mother does not exist");
            */
          
               
            if (birth_date != null && !birth_date.trim().equals("")) 
            {
                try 
                {
                   java.sql.Date temp;
                   temp = java.sql.Date.valueOf(birth_date.substring(0, 10));
                   if (birth_date.length() == 16) 
                   {
                      // The date is in the format YYYY-MM-DD HH:MI
                      // Add the hours and minutes to the date
                      long millis = temp.getTime();
                      int hour = Integer.parseInt(birth_date.substring(11, 13) );
                      int min = Integer.parseInt(birth_date.substring(14, 16) );
                      millis += ( (hour * 60) +
                                 min ) * 60 * 1000;
                      temp = new java.sql.Date(millis);
                   }
                   birth_date = temp.toString();
                } 
                catch (Exception e) 
                {
                   throw new DbException("Failed to parse birth date [" + birth_date + "].");
                }
            } 
            
            iid = getNextID(conn, "individuals_seq");
        
            sql = "insert into individuals (iid, identity, alias, father, mother,sex, birth_date, suid, comm, id, ts, status) "
                  +"values ("+iid+", "+sqlString(identity)+", "+sqlString(alias)+", "+sqlInteger(father)+", "
                  +sqlInteger(mother)+","+sqlString(sex)+", "+sqlString(birth_date)+", "+suid+", "+sqlString(comment)+", "+id+", "+getSQLDate()+", 'E')";

            stmt = conn.createStatement();
            Errors.logDebug("SQL="+sql);
            stmt.execute(sql);
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            Errors.logError("SQL="+sql);         
            throw new DbException("Internal error: "+e.getMessage());
        } 
        finally 
        {
            try 
            {
            if (stmt != null) stmt.close();
            } 
            catch (SQLException ignored) 
            {}
        }
        return iid;
    }


   /* creates sample */

   /**
    * 
    * @param conn 
    * @param id 
    * @param iid 
    * @param name 
    * @param tissue 
    * @param storage 
    * @param experimenter 
    * @param date 
    * @param treatment 
    * @param comm 
    * @throws se.arexis.agdb.db.DbException 
    */
   public void CreateSample(Connection conn, int id, int iid,
                            String name, String tissue, String storage, String experimenter,
                            String date, String treatment, String comm) 
                            throws DbException
   {
      String sql = "";
      Statement stmt = null;
      try 
      {

          
          sql = "insert into Samples values (nextval('Samples_Seq'), "+sqlString(name)+", "+sqlString(tissue)+", "+sqlString(experimenter)+", "+sqlString(date)+", "
                +sqlString(treatment)+", "+sqlString(storage)+", "+sqlString(comm)+", "+iid+", "+id+", "+getSQLDate()+")";
          
          stmt = conn.createStatement();
          stmt.executeUpdate(sql);
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         Errors.logError("SQL="+sql);
         
         throw new DbException("Internal error. Failed to create sample or failed to store to database.");
      } 
      finally 
      {
         try 
         {
            if (stmt != null) stmt.close();
         } 
         catch (SQLException ignored) 
         {}
      }
   }
   
  

    /**
     * updates sample
     * @param conn 
     * @param id 
     * @param said 
     * @param name 
     * @param tissue 
     * @param storage 
     * @param experimenter 
     * @param date 
     * @param treatment 
     * @param comm 
     * @throws se.arexis.agdb.db.DbException 
     */
    public void UpdateSample(Connection conn, int id, int said,
                            String name, String tissue, String storage, String experimenter,
                            String date, String treatment, String comm) 
       throws DbException                            
    {
        Statement stmt = null;
                
        String sql = "";
        String sql_log = "";
        try 
        {
            stmt = conn.createStatement();
            
            if (date != null && date.equals(""))
                date = null;
            
            // Save to log table
            sql_log = "insert into samples_log " 
                       +"(said,name,tissue_type,experimenter,date_,treatment,storage,comm,id,ts) (select said,name,tissue_type,experimenter,date_,treatment,storage,comm,id,ts from samples where said="+said+")";
            
            stmt.execute(sql_log);
            Errors.logDebug("UpdateCount="+stmt.getUpdateCount());
            
            // Update value
            sql = "update Samples set name = "+sqlString(name)+", tissue_type = "+sqlString(tissue)+", "+
                    "experimenter = "+sqlString(experimenter)+", date_ = "+sqlString(date)+", "+
                    "treatment = "+sqlString(treatment)+", storage = "+sqlString(storage)+", comm = "+sqlString(comm)+", "+
                    "id = "+id+", ts = "+getSQLDate()+" where said = "+said;
            
            stmt.executeUpdate(sql);
            Errors.logDebug("UpdateCount="+stmt.getUpdateCount());
        } 
        catch (Exception e) 
        {            
            e.printStackTrace(System.err);
            Errors.logError("SQL_LOG="+sql_log);
            Errors.logError("SQL="+sql);
            throw new DbException("Internal error. "+ e.getMessage());
        } 
        finally 
        {
            try 
            {
                if (stmt != null) stmt.close();                
            } 
            catch (SQLException ignored) 
            {}
      }
   }
    
    public IndividualDO getIndividual(Connection conn, int iid) throws DbException
    {
        Statement stmt = null;
        String sql = "";
        IndividualDO out = null;
        try
        {
            stmt = conn.createStatement();
            sql = "select * from individuals where iid = "+iid;
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next())
            {                   
                Errors.logDebug("Date="+rs.getString("birth_date"));
                java.util.Date bd = rs.getDate("birth_date");
                
                out = new IndividualDO(rs.getInt("iid"),
                        rs.getString("identity"),
                        rs.getString("alias"),
                        rs.getInt("father"),
                        rs.getInt("mother"),
                        rs.getString("sex"),
                        bd,
                        rs.getString("status"),
                        rs.getInt("suid"),
                        rs.getInt("id"),
                        rs.getString("ts"),
                        rs.getString("comm"));
            }
            rs.close();
            stmt.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Errors.logError("SQL="+sql);
            
            throw new DbException("Unable to get individual ["+iid+"]");
        }
            
        return out;
    }
    
    
    
    
    /**
     * Return the iid for either the identity or alias of an individual
     * If no individual is found, returns 0
     * If it fails it silently returns 0 and writes to the log.
     * @param conn The connection
     * @param identity Individuals identity name
     * @param alias The alias of the individual
     * @param suid The sampling unit id
     * @return returns the iid for an individual
     */
    public int getIID(Connection conn, String identity,String alias,int suid)
    {
        int iid = 0;
        String sql = "";
        Statement stmt = null;
        try 
        {
            if (alias != null)
            {
                sql = "select IID from INDIVIDUALS where ALIAS="+sqlString(alias)+" and SUID="+suid;
            }
            else if (identity != null)
            {
                sql = "select IID from INDIVIDUALS where IDENTITY="+sqlString(identity)+" and SUID="+suid;
            }
            else
            {
                Errors.logError("getIID(conn,"+identity+","+alias+") failed");
                return 0;
            }
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next())
            {
                iid = rs.getInt("iid");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Errors.logError("SQL="+sql);
            Errors.logError("identity="+identity+", alias="+alias+", suid="+suid);
        }
        finally
        {
            try
            {
                stmt.close();
            }
            catch (Exception ignore)
            {}
        }
        
        return iid;
    }
    
    
    
    /**
     * Get the sample id for a sample
     * @param conn The connection
     * @param iid The individuals iid
     * @param name The name of the sample
     * @return the said (sample id)
     */
    public int getSAID(Connection conn, int iid, String name)
    {
        int said = 0;
        String sql = "";
        Statement stmt = null;
        try 
        {
            sql = "select said from SAMPLES where IID="+iid+" and NAME="+sqlString(name);
            
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.first())
            {
                said = rs.getInt("said");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Errors.logError("SQL="+sql);
        }
        finally
        {
            try
            {
                stmt.close();
            }
            catch (Exception ignore)
            {}
        }
        
        return said;
    }
    
    /**
     * Get the id of the grouping
     */
    public int getGSID(Connection conn, int suid, String name)
    {
        int gsid = 0;
        String sql = "";
        Statement stmt = null;
        try 
        {
            sql = "select gsid from Groupings where suid = "+suid+" and name = "+sqlString(name);
            //sql = "select said from SAMPLES where IID="+iid+" and NAME="+sqlString(name);
            
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.first())
            {
                gsid = rs.getInt("gsid");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Errors.logError("SQL="+sql);
        }
        finally
        {
            try
            {
                stmt.close();
            }
            catch (Exception ignore)
            {}
        }
        
        return gsid;
    }

   /**
    * 
    * @param fp 
    * @param conn 
    * @param suid 
    * @param id 
    * @throws se.arexis.agdb.db.DbException 
    */
   public void CreateOrUpdateSamples(FileParser fp, Connection conn, int suid, int id) 
        throws DbException
   {
      Errors.logInfo("DbIndividuals.CreateOrUpdateSamples(...) started");
    
      try 
      {
         int i;
         String identity, alias, name, tissue, experimenter,date,treatment, storage, comm ,message=null;
         String [] titles = fp.columnTitles();

         for(int row = 0; row < fp.dataRows(); row++) 
         {

            if (titles[0].equalsIgnoreCase("IDENTITY"))
            {
               identity =fp.getValue("IDENTITY",row);
               alias = null;
            }
            else
            {
               alias =fp.getValue("ALIAS",row);
               identity = null;
            }
            name = fp.getValue("NAME",row);
            tissue = fp.getValue("TISSUE",row);
            experimenter = fp.getValue("EXPERIMENTER",row);
            date = fp.getValue("DATE",row);
            treatment = fp.getValue("TREATMENT",row);
            storage = fp.getValue("STORAGE",row);
            comm = fp.getValue("COMMENT",row);
            
            
            // Called Create_or_update_sample
            int iid = getIID(conn,identity,alias,suid);
            
            if (iid == 0)
                throw new DbException("Individual does not exists ["+identity+"]");
            
            int said = getSAID(conn,iid,name);
            if (said == 0)
                CreateSample(conn,id,iid,name,tissue,storage,experimenter,date,treatment,comm);
            else
            {
                UpdateSample(conn,id,said,name,tissue,storage,experimenter,date,treatment,comm);
            }
         }
       
        Errors.logInfo("DbIndividuals.CreateOrUpdateSamples(...) ended");
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
           
         throw new DbException("Internal error. Failed to create or update sample.\n(" +
                          e.getMessage() + ")");
      } 
   }


   /**
    * Delete a sample from the database.
    * @param conn The connection
    * @param said Sample ID
    * @throws se.arexis.agdb.db.DbException The DbException handles the message to the UI
    */
   public void DeleteSample(Connection conn, int said) 
        throws DbException
   {
      Statement stmt = null;
      String sql = "";
      String sql_log ="";
      try 
      {
          stmt = conn.createStatement();
          
          sql = "delete from Samples where said = "+said;
          sql_log = "delete from Samples_log where  said = "+said;
          
          stmt.execute(sql);
          Errors.logInfo("Affected rows = "+stmt.getUpdateCount());
          
          stmt.execute(sql_log);
          Errors.logInfo("Affected rows = "+stmt.getUpdateCount());
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         Errors.logError("SQL="+sql);
         Errors.logError("SQL_LOG="+sql_log);
         
         throw new DbException("Internal error. Failed to delete sample.");
      } 
      finally 
      {
         try 
         {
            if (stmt != null) stmt.close();
         } 
         catch (SQLException ignored) 
         {}
      }
   }



   /**
    * This method calculates the membership for one individual and returns
    * an object of the type ValueHolder. This object contains two vectors
    * which inturn contains the grouping and groups repectivily
    * @param conn 
    * @param pid 
    * @param iid 
    * @throws java.sql.SQLException 
    * @return 
    */
   public ValueHolder calcMembership(Connection conn, int pid, String iid)
      throws SQLException
   {
      Statement stmt_out = null;
      Statement stmt_in = null;
      ResultSet rset_out = null;
      ResultSet rset_in = null;
      String gid = null;
      Vector grps = new Vector();
      Vector grp = new Vector();
      ValueHolder ret = new ValueHolder();
      try
      {
         stmt_out = conn.createStatement();
         // NOTE !!!
         // This method selects directly from a
         // table !!!
         rset_out =
            stmt_out.executeQuery("SELECT GID FROM R_IND_GRP " +
                                  "WHERE IID=" + iid);
         stmt_in = conn.createStatement();
         while (rset_out.next() )
         {
            gid = rset_out.getString("GID");
            rset_in = stmt_in.executeQuery("SELECT NAME, GSNAME " +
                                           "FROM V_GROUPS_2 " +
                                           "WHERE GID=" + gid +
                                           " AND PID=" + pid);
            while (rset_in.next())
            {
               grps.addElement(rset_in.getString("GSNAME"));
               grp.addElement(rset_in.getString("NAME"));
            }
         }
      }
      catch (SQLException e)
      {
         throw new SQLException(e.getMessage());
      }
      finally
      {
         try
         {
            if (rset_out != null) rset_out.close();
            if (rset_in != null) rset_in.close();
            if (stmt_in != null) stmt_in.close();
            if (stmt_out != null) stmt_out.close();

         }
         catch (SQLException ignored) {}
      }
      ret.o1 = grps;
      ret.o2 = grp;
      return ret;
   }
   
     /**
    * Check values for individuals, throws DbException
    * @param identity 
    * @param alias 
    * @param sex 
    * @param birth_date 
    * @param comment 
    * @throws se.arexis.agdb.db.DbException 
    */
   public void checkIndValues(String identity, String alias,
                                  String sex, String birth_date,
                                  String comment)
                                  throws DbException
   {
      
      if (identity == null || identity.trim().equals(""))
      {
          throw new DbException("Unable to read identity.");
      }
      else if (identity.length() > 11)
      {
          throw new DbException("Identity [" + identity + "] exceeds 11 chars.");
      }
      
      if (alias != null && alias.length() > 11)
      {
         throw new DbException("Alias [" + alias + "] exceeds 11 chars.");
      }
      
      if (!sex.equals("M") && !sex.equals("F") && !sex.equals("U"))
      {
          throw new DbException("Invalid value for sex at row.");
      }
      
      if (birth_date!=null && birth_date.trim().equals(""))
          birth_date = null;
      
      if (birth_date == null || birth_date.length() != 10)
      {
          throw new DbException("Invalid birth date [" + birth_date + "].");
      }
      
      if (comment != null && comment.length() > 256)
      {
          throw new DbException("Invalid comment.");
      }
   }

   /**
    * Check values for individuals, throws DbException
    * @param identity 
    * @param alias 
    * @param sex 
    * @param birth_date 
    * @param comment 
    * @param row 
    * @throws se.arexis.agdb.db.DbException 
    */
   private void checkIndValues(String identity, String alias,
                                  String sex, String birth_date,
                                  String comment, int row)
                                  throws DbException
   {
      
      if (identity == null || identity.trim().equals(""))
      {
          throw new DbException("Unable to read identity. at row"+row);
      }
      else if (identity.length() > 11)
      {
          throw new DbException("Identity [" + identity + "] exceeds 11 chars at row "+row);
      }
      
      if (alias != null && alias.length() > 11)
      {
         throw new DbException("Alias [" + alias + "] exceeds 11 chars at row "+row);
      }
      
      if (!sex.equals("M") && !sex.equals("F") && !sex.equals("U"))
      {
          throw new DbException("Invalid value for sex at row "+ row);
      }
      
      if (birth_date == null || birth_date.length() != 10)
      {
          throw new DbException("Invalid birth date [" + birth_date + "] at row "+ row);
      }
      
      if (comment != null && comment.length() > 256)
      {
          throw new DbException("Invalid comment at row at row "+ row);
      }
   }

   /**
    * 
    * @param name 
    * @param id 
    */
   private void addGrouping(String name, int id) {
      ValueHolder temp = new ValueHolder();
      temp.o1 = name;
      temp.o2 = new Integer(id);
      m_grps.addElement(temp);
   }

   /**
    * 
    * @param name 
    * @return 
    */
   private int getGroupingId(String name) {
      Enumeration e = m_grps.elements();
      ValueHolder temp;
      while (e.hasMoreElements()) {
         temp = (ValueHolder) e.nextElement();
         if (((String) temp.o1).equals(name))
            return ((Integer)temp.o2).intValue();
      }
      return -1;
   }
   /**
    * 
    * @param grouping 
    * @param group 
    * @param id 
    */
   private void addGroup(String grouping, String group, int id) {
      ValueHolder temp = new ValueHolder();
      temp.o1 = grouping;
      temp.o2 = group;
      temp.o3 = new Integer(id);
      m_grp.addElement(temp);
   }
   /**
    * 
    * @param grouping 
    * @param group 
    * @return 
    */
   private int getGroupId(String grouping, String group) {
      Enumeration e = m_grp.elements();
      ValueHolder temp ;
      while (e.hasMoreElements()) {
         temp = (ValueHolder) e.nextElement();
         if (((String) temp.o1).equals(grouping) &&
             ((String) temp.o2).equals(group))
            return ((Integer) temp.o3).intValue();
      }
      return -1;
   }
   
    /**
     * Load the individuals to the test-objects
     * @param conn 
     * @param db 
     * @param suid 
     */
    public void loadIndividual(Connection conn, DataObject db, int suid)
    {
        Statement stmt;
       
        try
        {
            stmt = conn.createStatement();
         
            String sql = "select identity, alias from individuals where suid="+suid;
            ResultSet rs = stmt.executeQuery(sql);
           
            stmt = conn.createStatement();
            
            String identity, alias;
            while (rs.next() )
            {
                identity = rs.getString("identity");
                alias    = rs.getString("alias");
                
                db.setIndividual(identity,alias);
            }
        }
        catch (Exception e)
        {
            Errors.logError(e.getMessage());
            e.printStackTrace(System.err);
        }
   }
    
   /**
     * Load the grouping information to the test-objects
     * @param conn 
     * @param db 
     * @param suid 
     */
    public void loadGroupings(Connection conn, DataObject db, int suid)
    {
        // DbIndividual.loadGroupings(...)
        Errors.logInfo("DbIndividual.loadGroupings(...) started");
        Statement stmt;
        String sql = "";
        
        try
        {
            stmt = conn.createStatement();
            sql          = "select i.identity, g.name as grp, gs.name as grouping, i.suid, gs.suid "
                         + "from groups g, groupings gs, r_ind_grp r, individuals i "
                         + "where g.gsid=gs.gsid and r.iid=i.iid and r.gid=g.gid and gs.suid="+suid+" "
                         + "order by i.iid ";
            
            ResultSet rs = stmt.executeQuery(sql);
           
            stmt = conn.createStatement();
            
            String identity, group, grouping;
            while (rs.next() )
            {
                identity = rs.getString("identity");
                group    = rs.getString("grp");
                grouping = rs.getString("grouping");
                
                db.setGrouping(identity,group,grouping);
            }
        }
        catch (Exception e)
        {
            Errors.logError(e.getMessage());
            Errors.logError("SQL="+sql);
            e.printStackTrace(System.err);
        }
        Errors.logInfo("DbIndividual.loadGroupings(...) ended");
    }
    
    /**
     * Load the grouping information to the test-objects
     * @param conn 
     * @param db 
     * @param suid 
     */
    public void loadSamples(Connection conn, DataObject db, int suid)
    {
        // DbIndividual.loadGroupings(...)
        Errors.logInfo("DbIndividual.loadSamples(...) started");
        Statement stmt;
       
        try
        {
            stmt = conn.createStatement();
         
            String sql = "select i.identity, s.name as sample "+
                        "from individuals i, samples s "+
                        "where i.iid=s.iid and i.suid="+suid;
            
            
            ResultSet rs = stmt.executeQuery(sql);
           
            stmt = conn.createStatement();
            
            String identity, sample;
            while (rs.next() )
            {
                identity = rs.getString("identity");
                sample   = rs.getString("sample");
                
                
                db.setSample(identity,sample);
            }
        }
        catch (Exception e)
        {
            Errors.logError(e.getMessage());
            e.printStackTrace(System.err);
        }
        Errors.logInfo("DbIndividual.loadSamples(...) ended");
    }
   
}
