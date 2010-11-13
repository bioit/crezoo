/*
 * TestDependency.java
 *
 * Created on April 5, 2005, 8:49 AM
 */

package se.arexis.agdb.test;

import java.util.ArrayList;
import se.arexis.agdb.util.FileImport.Dependency;
import se.arexis.agdb.util.FileImport.ImportFileStruct;
import se.arexis.agdb.util.FileImport.ImportProcess;
import se.arexis.agdb.util.FileImport.Prefs;

/**
 *
 * @author heto
 */
public class TestDependency extends AbstractTest
{
    
    /** Creates a new instance of TestDependency */
    public TestDependency() 
    {
        super.init();
    }
    
    public void imp()
    {
        Prefs p = new Prefs();
            
        p.connViss = pg_conn_viss;
        p.connection = pg_conn;

        p.sampleUnitId = 1034;
        p.isid = 4;

        ImportProcess imp2 = new ImportProcess(p);
        imp2.initImportObjects();

        ArrayList<ImportFileStruct> tmp = imp2.sortImportFiles2();
            
            
    }
    
    public static void main(String[] args)
    {
        ImportProcess imp = new ImportProcess();
        ArrayList<String> formats = imp.listFormats();
        System.out.println("Available formats:");
        for (int i=0;i<formats.size();i++)
        {
            System.out.println(formats.get(i));
        }
        System.out.println("-----------------------------");
        
        
        try
        {
        
            ArrayList<Dependency> deps = imp.getDependencies();
            imp.sort(deps);

            for (int i=0;i<deps.size();i++)
            {
                System.out.print(deps.get(i).name+": ");
                for (int j=0;j<deps.get(i).dep.length;j++)
                {
                    System.out.print(deps.get(i).dep[j]+" ");
                }
                System.out.println("");
            }
            System.out.println("-----------------------------");
            
            TestDependency t = new TestDependency();
            t.imp();
            
            
            
                    
            

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
