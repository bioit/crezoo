/*
 * testImportProcess.java
 *
 * Created on December 10, 2004, 1:18 PM
 */

package se.arexis.agdb.test;

import java.util.ArrayList;
import se.arexis.agdb.util.FileImport.*;
import se.arexis.agdb.db.*;
import se.arexis.agdb.db.TableClasses.*;
import java.sql.*;

/**
 *
 * @author heto
 */
public class testImportProcess 
{
    
    public static void main(String[] args) 
    {
        System.out.println("testImportProcess"); 
        
        
        ImportProcess imp = new ImportProcess();
        ArrayList<String> formats = imp.listFormats();
        System.out.println("Available formats:");
        for (int i=0;i<formats.size();i++)
        {
            System.out.println(formats.get(i));
        }
        System.out.println("-----------------------------");
        
        ArrayList<ImportFileStruct> arr = new ArrayList<ImportFileStruct>();
        
        /*
        INDIVIDUAL
        MARKER
        MARKERSET
        UMARKERSET
        UMARKER
        GENOTYPE
        VARIABLE
        VARIABLESET
        UVARIABLE
        PHENOTYPE
        GROUPING
        SAMPLES
        */
        
        arr.add(new ImportFileStruct("ind.txt","INDIVIDUAL","356"));
        arr.add(new ImportFileStruct("ind2.txt","INDIVIDUAL","358"));
        arr.add(new ImportFileStruct("marker.txt","MARKER","358"));  
        arr.add(new ImportFileStruct("markerset.txt","MARKERSET","358"));
        arr.add(new ImportFileStruct("umarkerset.txt","UMARKERSET","358"));
        arr.add(new ImportFileStruct("umarker.txt","UMARKER","358"));
        arr.add(new ImportFileStruct("geno.txt","GENOTYPE","358"));
        arr.add(new ImportFileStruct("var.txt","VARIABLE","358"));
        arr.add(new ImportFileStruct("varset.txt","VARIABLESET","358"));
        arr.add(new ImportFileStruct("uvar.txt","UVARIABLE","358"));
        arr.add(new ImportFileStruct("uvarset.txt","UVARIABLESET","358"));
        arr.add(new ImportFileStruct("pheno.txt","PHENOTYPE","358"));
        arr.add(new ImportFileStruct("grouping.txt","GROUPING","358"));
        arr.add(new ImportFileStruct("samp.txt","SAMPLES","358"));
        
        
        arr.add(new ImportFileStruct("pellefjant.txt","PELLEFJANT","358"));
        
        
        
        test(arr,"C_U_CU");
        test(arr,"C_U");
        test(arr,"C_CU");
        test(arr,"U_CU");
        test(arr,"C");
        test(arr,"U");
        test(arr,"CU");
        test(arr,"SUID");
        test(arr,"LEVEL");
        test(arr,"SPECIESID");
        test(arr,"NAME");
                

        /*
        ImportProcess imp = new ImportProcess();
        
        ArrayList<String> files = imp.listOfFiles(arr, "C_U");
        
        for (int i=0;i<files.size();i++)
        {
            System.out.println(files.get(i));
        }
         */
        
        System.out.println("-----START----------------");
        
        String dburl="jdbc:oracle:thin:@192.168.1.32:1521:agdbtest";
        String uid="gdbadm";
        String pwd="gdbadm";
        
        Connection conn = null;
        try
        {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(dburl,uid,pwd);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        
        
        ArrayList<ImportFileStruct> files = null;
        DbImportSet dbis = new DbImportSet();
        files = dbis.getImportFiles(conn, 99);
        
        for (int i=0;i<files.size();i++)
        {
            System.out.println(files.get(i).name + "\t"+files.get(i).type+"\t"+files.get(i).ifid);
        }
        
        System.out.println("--C_U_CU-------------------");
        
        ArrayList<ImportFileStruct> filenames = null;
            
        // Create, Update or Create_or_Update
        filenames = imp.listOfFiles(files, "C_U_CU");
        
        for (int i=0;i<filenames.size();i++)
        {
            System.out.println(filenames.get(i).name);
        }
        
        
        
       ArrayList<FileHeader> tmp = imp.getHeaders();
       for (int i=0;i<tmp.size();i++)
       {
           System.out.println(tmp.get(i).objectTypeName()+"\t"+
                   tmp.get(i).formatTypeName()+"\t"+
                   tmp.get(i).version());
       }
        
        
        
       try
       {
            ImportData t = imp.getImportModule(new FileHeader("GENOTYPE","LIST",1,'\t'));
            t.debug();
       }
       catch(Exception e)
       {
           e.printStackTrace();
       }
        
       
        
    }
    
    public static void test(ArrayList<ImportFileStruct> arr, String comp)
    {
        System.out.println("Comp="+comp);
        ImportProcess imp = new ImportProcess();
        
        ArrayList<ImportFileStruct> files = imp.listOfFiles(arr, comp);
        
        for (int i=0;i<files.size();i++)
        {
            System.out.println(files.get(i).name);
        }
        
        System.out.println("---------------------");
        
    }
    
    
}
