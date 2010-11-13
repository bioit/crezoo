/*
 * SimpleCSVImporter.java
 *
 * Created on September 22, 2005, 12:27 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.io;

import org.tgdb.util.TabularData;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class for reading a CSV file into a string matrix
 * @author lami
 */
public class SimpleCSVImporter {
    private String[][] csvData;
    
    private boolean checkedTextDelimiter;
    private boolean useTextDelimiter;
    
    /** Creates a new instance of SimpleCSVImporter */
    public SimpleCSVImporter() {
    }
    
    /**
     * Returns the data matrix
     * @return The data from the CSV file
     */
    public String[][] getData() { return csvData; }
    
    /**
     * Returns a TabularData object for csv data that has a header row.
     */
    public TabularData getTabularData() {
        return new TabularData(csvData);
    }
    
    /**
     * Returns the number of columns in the CSV file
     * @return The number of columns in the CSV file
     */
    public int getNumCols() { return csvData[0].length; }
    
    /**
     * Returns the number of rows in the CSV file, including the header.
     * @return The number of rows in the CSV file
     */
    public int getNumRows() { return csvData.length; }
    
    public void useTextDelimiter(boolean val) {
        useTextDelimiter = val;
    }
    
    
//    private void getCSVSize(String fileName, String separator, boolean equalColumnSizeOnly) throws IOException {
//        try {
//            BufferedReader in = new BufferedReader(new FileReader(fileName));
//            
//            
//            //String[] headers = in.readLine().trim().split(separator);
//            String[] headers = splitRow(in.readLine().trim(), separator);
//            int rows = 1;
//            int cols = 0;
//            if(equalColumnSizeOnly)
//                cols = headers.length;
//            
//            String str_row = "";
//            String[] row = null;
//            int max = 0;
//            
//            while(in.ready()) {
//                if(equalColumnSizeOnly)
//                    in.readLine();
//                else {
//                    //row = in.readLine().trim().split(separator);
//                    str_row = getLine(in);
//                    row = splitRow(str_row, separator);
//                }
//                
//                if(row != null) {
//                    if(row.length > max)
//                        max = row.length;
//                }
//                
//                rows++;
//            }
//            
//            in.close();
//            
//            if(equalColumnSizeOnly)
//                csvData = new String[rows][cols];
//            else
//                csvData = new String[rows][max];
//        } catch (IOException ioe) {
//            //System.out.println("DOH! An IO error appear to have occured when fetching CSV size!\n"+ioe.getMessage());
//            throw new IOException("An IO error appear to have occured when fetching CSV size!\n"+ioe.getMessage());
//        }
//    }
    
    /** 
     * Print a String array to std out. This is for debugging.
     */
    public void printCSV(String[] str, String delim) {
        String out = str.length+"#";
        for (int i=0;i<str.length;i++) {
            if (i!=0)
                out += delim;
            out += str[i];
        }
        System.out.println(out);
    }
    
    /**
     * For CSV data in a String array...
     */
    private void getCSVSize(String[] csvText, String separator, boolean equalColumnSizeOnly) throws IOException {
        try {
            //String[] headers = csvText[0].trim().split(separator);
            String[] headers = splitRow(csvText[0].trim(), separator);
            //printCSV(headers,";");
            int rows = 1;
            int cols = 0;
            if(equalColumnSizeOnly)
                cols = headers.length;
            
            String[] row = null;
            int max = 0;
            
            for (int i=0;i<csvText.length;i++) {
                
                //row = csvText[i].trim().split(separator);
                row = splitRow(csvText[i].trim(), separator);
                
                if(row != null) {
                    if(row.length > max)
                        max = row.length;
                }
                rows++;
            }
            
            if(equalColumnSizeOnly)
                csvData = new String[rows-1][cols];
            else
                csvData = new String[rows-1][max];
        } catch (Exception e) {
            //System.out.println("DOH! An IO error appear to have occured when fetching CSV size!\n"+e.getMessage());
            e.printStackTrace();
            throw new IOException("An IO error appear to have occured when fetching CSV size!\n"+e.getMessage());
        }
    }
    
    /**
     * Get a "line" of csv data. This rutine ignores \n inside text delimitors
     */
    private String getLine(BufferedReader in) throws IOException {
        String row = "";
        int i=0;
        
        boolean done = false;
        while (!done) {
            row += in.readLine().trim()+"\n";
//            System.out.println("checkUnfinishedRow="+checkUnfinishedRow(row));
            
            if (checkUnfinishedRow(row)==false)
                done = true;
            
            if (i>100)
                throw new IOException("Unterminated text string found. Aborting.");
            i++;
        }
//        System.out.println("getLine, row="+row);
        return row;
    }
    
    
    
    /**
     * Reads the CSV file
     * @param fileName The file to read
     * @param separator The separator, i.e. comma, semicolon etc.
     * @param equalColumnSizeOnly Set to true if columns should be of equals size.
     * @throws java.io.IOException If something went wrong during the process.
     */
    public void importCSV(String fileName, String separator, boolean equalColumnSizeOnly) throws IOException {
        //getCSVSize(fileName, separator, equalColumnSizeOnly);
        
        
        BufferedReader in = new BufferedReader(new FileReader(fileName));
        ArrayList temp = new ArrayList();
        
        String row = "";
        while (in.ready())
        {
            row = in.readLine();
            temp.add(row);
        }
        in.close();
        
        String[] csvText = ArrayList2StringArray(temp);
        importCSV(csvText, separator, equalColumnSizeOnly);
    }
    
    private String[] ArrayList2StringArray(ArrayList arr)
    {
        String[] out = new String[arr.size()];
        for (int i=0;i<arr.size();i++)
        {
            out[i] = (String)arr.get(i);
        }
        return out;
    }
    
    /**
     * Check if a row starts with a text delimiter but never closes it again.
     * This means that a cvs cell contains a \n and it needs to be handled.
     *
     */
    public boolean checkUnfinishedRow(String row) {
        boolean insideTextDelim = false;
        char c;
        for (int i=0;i<row.length();i++) {
            c = row.charAt(i);
            
            if (c=='\"' && insideTextDelim==false) {
                insideTextDelim=true;
            } else if (c=='\"' && insideTextDelim==true) {
                insideTextDelim=false;
            }
        }
        return insideTextDelim;
    }
    
    /**
     * Fix an arraylist of csv text with line brakes within a textdelimiter. 
     * Concatenate the two lines and separate them with a \n.
     *
     */
    private String[] fixMultiLine(String[] csvText)
    {
        ArrayList arr = new ArrayList();
        
        for (int i=0;i<csvText.length;i++)
        {
            String row = csvText[i];
            while (checkUnfinishedRow(row)==true)
            {
                row += "\n"+csvText[++i];
            }
            arr.add(row);
        }

        // Compile a new array.
        String[] out = new String[arr.size()];
        for (int i=0;i<arr.size();i++)
        {
            out[i] = (String)arr.get(i);
        }
        return out;
    }
    
    /**
     * Reads CSV Data from a String array
     * @param csvText A String array with unparsed CSV data
     * @param separator The separator, i.e. comma, semicolon etc.
     * @param equalColumnSizeOnly Set to true if columns should be of equals size.
     * @throws java.io.IOException If something went wrong during the process.
     */
    public void importCSV(String[] csvText, String separator, boolean equalColumnSizeOnly) throws IOException {
        
        csvText = fixMultiLine(csvText);
        
        getCSVSize(csvText, separator, equalColumnSizeOnly);
        
        
        //BufferedReader in = new BufferedReader(new FileReader(fileName));
        String[] splitted = null;
        
        int i = 0;
        
        int cols = csvData[0].length;
        String row = "";
        
        
        for (int j=0;j<csvText.length;j++) {
            row = csvText[j].trim();
            
            if(row.length() > 0) {
                //splitted = row.split(separator);
                splitted = splitRow(row, separator);
                if(equalColumnSizeOnly) {
                    if(cols == splitted.length) {
                        cols = splitted.length;
                    } else {
                        throw new IOException("Columns are not equal on row "+i+"! Current col:"+splitted.length+" Previous col:"+cols);
                    }
                }

                csvData[i] = splitted;

                i++;
            }
        }
    }
    
    /**
     * Split a row into parts. This rutine can have text separators in the
     * raw text, for example \"hej hå\",\"kkj\"
     *
     * Possible input:
     *   "hej jag kan använda komma, i detta test","blaha","iru,."
     *
     * Output:
     *   [hej jag kan använda komma, i detta test],[blaha],[iru,.]
     */
    public String[] splitRow(String str, String delimiter) {
//        System.out.println("SplitRow; str="+str);
//        if (!checkedTextDelimiter)
//            checkTextDelimiter(str, delimiter);
        
        if (useTextDelimiter)
            return splitWithTextDelimiter(str, delimiter);
        else
            return split(str, delimiter);
    }
    
    /**
     * Split an ordinary csv text row
     */
    private String[] split(String str, String delimiter) {
        String[] temp = str.split(delimiter);
        for (int i=0;i<temp.length;i++) {
            temp[i] = temp[i].trim();
        }
        return temp;
    }
    
    private String[] splitWithTextDelimiter(String str, String delimiter) {
        String[] out = null;
        
        ArrayList list = new ArrayList();
        
        char delim = delimiter.charAt(0);
        
        boolean textDelim = false;
        String tmp = "";
        char c;
        char last = '\0';
        for (int i=0;i<str.length();i++) {
            c = str.charAt(i);
            
            if (c=='\"' && textDelim==false) {
                // Start new text delim section
//                System.out.println("Start text delim i="+i);
                textDelim=true;
            } else if (c=='\"' && textDelim==true) {
                // End of text delim
//                System.out.println("Stop text delim i="+i);
                textDelim=false;
//                list.add(tmp);
//                System.out.println("Add to list, c="+c);
//                tmp = "";
            }
            
            else if (c==delim && textDelim==false) {
                // this is a delimiter
//                if  (last!='\"')
                {
                    list.add(tmp);
//                    System.out.println("Add to list, c="+c);
                }
                tmp = "";
            } else {
                // Add character
                tmp += c;
            }
            last = c;
        }
        // Add last element
        list.add(tmp);
//        System.out.println("Add to list last element");
        
        int cols = list.size();
        out = new String[cols];
        
        for (int i=0;i<cols;i++) {
            out[i] = (String)list.get(i);
        }
        
        
        return out;
    }
}
