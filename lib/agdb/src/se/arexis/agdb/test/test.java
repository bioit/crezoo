/*
 * test.java
 *
 * Created on February 8, 2005, 8:34 AM
 */

package se.arexis.agdb.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import se.arexis.agdb.db.DbImportFile;
import se.arexis.agdb.db.DbResult;

/**
 *
 * @author heto
 */
public class test {
    
    /** Creates a new instance of test */
    public test() 
    {
    }
    
    public static void main(String args[])
    {
        Connection conn = null;
        
        try
        {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://192.168.1.32/agdb", "gdbadm", "gdbadm");
       
        
            //System.out.println("out="+args.length);


            if (args.length == 3 && args[0].equals("write"))
            {
                DbImportFile f = new DbImportFile();

                int id = Integer.valueOf(args[1]).intValue();
                String filename = args[2];

                File in_file = new File(filename);

                f.saveImportFile(conn, id, in_file);        
            }
            else if (args.length == 3 && args[0].equals("read"))
            {
                DbImportFile f = new DbImportFile();

                int id = Integer.valueOf(args[1]).intValue();
                String filename = args[2];

                f.getImportFile(conn, id, filename);
            }


            else if (args.length == 2 && args[0].equals("print"))
            {
                //System.out.println("Test2: Array of bytes");
                DbImportFile f = new DbImportFile();

                int id = Integer.valueOf(args[1]).intValue();

                byte[] test = f.getImportFile(conn,id);
                String out = new String(test);
                System.out.print(out);
            }
            else if (args.length == 2 && args[0].equals("header"))
            {
                
                
                DbImportFile f = new DbImportFile();
                
                System.out.println(f.getImportFileHeader(conn, args[1]));
                

            }
            
            else if (args.length == 2 && args[0].equals("filestream"))
            {
                int id = Integer.valueOf(args[1]).intValue();
                DbResult r = new DbResult();
                InputStream is = r.getResultFileStream(conn, id);
                
                FileOutputStream out = new FileOutputStream("file.out");
                
                int c = 0;
                while ((c = is.read()) != -1)
                {
                    out.write(c);
                }
                out.flush();
                out.close();
            }
            
            else if (args.length == 3 && args[0].equals("setfilestream"))
            {
                int id = Integer.valueOf(args[1]).intValue();
                DbResult r = new DbResult();
                
                File file = new File(args[2]);
                
                FileInputStream fis = new FileInputStream(file);
                
                r.setResultFileStream(conn, id, fis,(int)file.length());
                
                fis.close();
            }

            else
            {
                System.out.println("agdb-test help\n\tThis help message\n");
                System.out.println("agdb-test write <id> <filename>\n\tWrite the file to the database\n");

                System.out.println("agdb-test read <id> <filename>\n\tRead the file from the database\n");

                System.out.println("agdb-test print <id>\n\tPrint the file from the database\n");

            }
        
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        
        //System.out.println("test");
    }
    
    
    
}
