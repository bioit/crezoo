/*
 * DbAbstractVariable.java
 *
 * Created on February 23, 2005, 1:26 PM
 */

package se.arexis.agdb.db;

/**
 *
 * @author heto
 */
public class DbAbstractVariable extends DbObject
{
    
    /** Creates a new instance of DbAbstractVariable */
    public DbAbstractVariable() 
    {
    }
    
    protected void checkVariableValues(String name, String type, String unit, String comment)
        throws DbException
    {
        if (name.length() > 20)
            throw new DbException("Name exceeds 20 charcters");
        if (name.contains(" "))
            throw new DbException("Name contains white spaces");
       
        if (!type.equals("E") && !type.equals("D"))
            throw new DbException("Unknown type [" + type + "]");
        
        if (unit.length() > 10)
            throw new DbException("Unit exceeds 10 charcters");
            
        if (comment.length() > 256)
            throw new DbException("Comment exceeds 256 charcters");
    }
    
    protected void checkUVarMappingFileFormat(String[] titles)
        throws DbException
    {
        boolean ok = true;
        System.err.println(titles.length + titles[0]);
        if (titles.length < 1 || !titles[0].equals("MAPPING")) {
            ok = false;
        }

        if(!ok){
            String errStr="Illegal headers.<BR>"+
                    "Required file headers: MAPPING (+ the Samplig Unit Names) <BR>"+
                    "Headers found in file:";
            for (int j=0; j<titles.length;j++) {
                errStr = errStr+ " " + titles[j];
            }
            throw new DbException(errStr);
        }
    }
    
    protected void checkUVariableFileFormat(String[] titles)
        throws DbException
    {
         if(titles.length != 1 || !titles[0].equals("VARIABLE")) 
            {
                
                
                String errStr="Illegal headers.<BR>"+
                        "Required file headers: VARIABLE<BR>"+
                        "Headers found in file:";
                for (int j=0; j<titles.length;j++) {
                    errStr = errStr+ " " + titles[j];
                }
                throw new DbException(errStr);
            }
         
    }
    
    protected void checkVariableFileFormat(String[] titles)
        throws DbException
    {
        boolean ok = true;
        if(titles.length != 4) {
                ok = false;
            }
            if (ok) {
                if (!titles[0].equals("VARIABLE") ||
                        !titles[1].equals("TYPE") ||
                        !titles[2].equals("UNIT") ||
                        !titles[3].equals("COMMENT")) {
                    ok = false;
                }
            }
            if(!ok){
                String errStr="Illegal headers.<BR>"+
                        "Required file headers: VARIABLE TYPE UNIT COMMENT<BR>"+
                        "Headers found in file:";
                for (int j=0; j<titles.length;j++) {
                    errStr = errStr+ " " + titles[j];
                }
                throw new DbException(errStr);
            }
    }
}
