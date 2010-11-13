/*
 * DbAbstractMarker.java
 *
 * Created on February 21, 2005, 2:10 PM
 */

package se.arexis.agdb.db;

/**
 *
 * @author heto
 */
public abstract class DbAbstractMarker extends DbObject
{
    
    /** Creates a new instance of DbAbstractMarker */
    public DbAbstractMarker() 
    {
        
    }
    
   /*
    * This method checks the parameters that are passed to the proc
    * Create_Allele before the callable statement is executed.
    * If the method detects an error it builds up the message
    * attribute and returns false.
    */
   protected boolean checkAlleleValues(String name, int row, int n) 
   {
      boolean ret = true;
      if (name == null || name.trim().equals("")) 
      {
         ret = false;
         buildErrorString("Unable to read the allele (number " + n + ") at row " + row);
      } else if (name.length() > 20) 
      {
         buildErrorString("Allele [" + name + "] exceeds 20 character at row " + row);
         ret = false;
      }
      return ret;
   }
   
        
   
   /*
    * This method checks the parameters that are passed to
    * Create_Marker before the callable statement is executed. If the
    * method detects an error it builds up the message attribute and
    * returns false.
    */
   protected boolean checkMarkerValues(String name, String alias,
                                     String position, String comm, int row) 
                                     throws DbException
   {
      boolean ret = true; // old stuff left.
      
      if (name == null || name.trim().equals("")) 
      {
         throw new DbException("Unable to read the name at row " + row);
      } 
      else if (name.length() > 20) 
      {
         throw new DbException("Name [" + name + "] exceeds 20 character at row " + row);
      } 
      else if (name.contains(" "))
      {
          throw new DbException("Name contains white spaces");
      }
      else if (alias != null && alias.length() > 20) 
      {
         throw new DbException("Alias [" + alias + "] exceeds 20 character at row " + row);
      } 
      else if (comm != null && comm.length() > 256) 
      {
         throw new DbException("Comment exceeds 256 chars at row " + row);
      }
      
      checkPosition(position, row);
      
      return ret;
   }
   
   protected void checkChromosomeName(String chrom, int row)
        throws DbException
   {
      if (chrom == null || chrom.trim().equals("")) 
      {
         throw new DbException("Unable to read the chromosome at row " + row);
      } 
      else if (chrom.length() > 2) 
      {
         throw new DbException("Chromosome name exceeds 2 chars at row " + row);
      } 
   }
   
   
   /*
    * This method checks the parameters that are passed to
    * Create_Marker before the callable statement is executed. If the
    * method detects an error it builds up the message attribute and
    * returns false.
    */
   protected boolean checkMarkerValues(String chrom, String name, String alias,
                                     String position, String comm, int row) 
                                     throws DbException
   {
      boolean ret = true;
      
      checkChromosomeName(chrom, row);
      checkMarkerValues(name, alias, position, comm, row);
      
      return ret;
   }
   
   protected void checkPrimers(String p1, String p2, int row)
        throws DbException
   {
      if (p1 != null && p1.length() > 40)
      {
         throw new DbException("Primer 1 exceeds 40 chars at row " + row);   
      }
      
      if (p2 != null && p2.length() > 40)
      {
         throw new DbException("Primer 2 exceeds 40 chars at row " + row);
      }
   }
   
   protected void checkPosition(String position, int row)
        throws DbException
   {
      if (position != null && !position.trim().equals(""))
      {
         try
         {
            double d = Double.parseDouble(position);
         }
         catch (NumberFormatException nfe)
         {
            throw new DbException("Position [" + position + "] at row " + row + " is not a valid position");
         }
      }
   }
   
   /*
    * This method checks the parameters that are passed to
    * Create_Marker before the callable statement is executed. If the
    * method detects an error it builds up the message attribute and
    * returns false.
    */
   protected boolean checkMarkerValues(String name, String alias,
                                     String p1, String p2, String position,
                                     String comm, int row)
                                     throws DbException
   {
      boolean ret = true;
      
      checkMarkerValues(name, alias, position, comm, row);
      checkPrimers(p1,p2,row);
      checkPosition(position, row);
      
      return ret;
   }
        
   
    /*
    * This method checks the parameters that are passed to
    * Create_Marker before the callable statement is executed. If the
    * method detects an error it builds up the message attribute and
    * returns false.
    */
   protected boolean checkMarkerValues(String chrom, String name, String alias,
                                     String p1, String p2, String position,
                                     String comm, int row)
                                     throws DbException
   {
      boolean ret = true;
      
      checkMarkerValues(name, alias, position, comm, row);
      checkChromosomeName(chrom, row);
      checkPrimers(p1,p2,row);
      checkPosition(position, row);
      
      return ret;
   }
  
}
