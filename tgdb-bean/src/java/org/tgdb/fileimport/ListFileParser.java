package org.tgdb.fileimport;


/**
 * ListFileParser extends the MugenFileParser with a method for getting
 * data read from the file and with a test method. To access the data read
 * from the file, a method named <I>getValue</I> is used. 
 * 
 * @see AbstractValueFileParser
 */
public class ListFileParser extends MugenFileParser
{

   //////////////////////////////////////////////////////////////////////
   //
   // Constructors
   //
   //////////////////////////////////////////////////////////////////////

   /**
    * Creates a new ListFileParser instance.
    * 
    * @param fileName The file this object should read information from
    * @throws org.tgdb.fileimport.InputDataFileException if file reading fails
    * @exception AssertionException If no filename is given
    */
   public ListFileParser(String fileName)
      throws AssertionException, InputDataFileException
   {
      super(fileName);
   }

   

   //////////////////////////////////////////////////////////////////////
   //
   // Public section
   //
   //////////////////////////////////////////////////////////////////////

   

   
   /**
    * Tests the ListFileParser object by printing all its information
    * 
    */
   public void test()
   {
      System.out.println("==================================================");
      System.out.println("Testing the FileParser with file " + getFileName());
      System.out.println("--------------------------------------------------");

      System.out.println("Columns="+getNumberOfColumns());
      System.out.println("Rows="+getNumberOfDataRows());
      
      String aColName;
      
      // Loop all data rows + one title row
      for (int r = 0; r < getNumberOfDataRows() + 1; r++)
      {
         // On current row, loop all columns
         for (int h = 0; h < getNumberOfColumns(); h++)
         {
            // Get the title of the current column
            aColName = getColumnTitles()[h];
            
            // If first run, print the column title
            if (r == 0)
            {
               System.out.print(aColName);
            }

            // not first run, get the values by using the column name
            else
            {
               System.out.print(getDataValue(aColName, r - 1));
            }

            // Print a delimiter
            if (h < getNumberOfColumns() - 1)
            {
               System.out.print('-');
            }
            
         }
         System.out.println("");
      }
      System.out.println("==================================================");
   }
   
   public String getDataValue(String columnName, int row)
    {
        // Get the column index of the given column title
        int index = indexOf(columnName);
        
        return getDataValue(index, row);
    }
    
    public String getDataValue(int col, int row)
    {
        // Get the right data row excluding comments
        int index = dataRows[row];
        
        return mValues[index][col];
    }


}
