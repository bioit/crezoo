/*
 * MolImporter.java
 *
 * Created on September 21, 2005, 7:59 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.io;
//import com.sun.rsasign.m;
import org.tgdb.util.SimpleMolecule;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Class for importing molecules
 * @author lami
 */
public class MolImporter {
    private ArrayList mols;
    private HashMap map;
    
    /**
     * Creates a new instance of MolImporter 
     */
    public MolImporter() {
        mols = new ArrayList();
        map = new HashMap();
    }
    
    /**
     * Returns the imported molecules
     * @return An arraylist of imported molecules in SimpleMolecule format
     */
    public ArrayList getMols() { return mols; }
    
    /**
     * Returns the molecule with the specified name
     * @param name The name of the molecule to return
     * @return The choosen molecule in SimpleMolecule format
     */
    public SimpleMolecule getMol(String name) { 
        Integer i = (Integer)map.get(name);
        if(i == null)
            return null;
        return (SimpleMolecule)mols.get(i.intValue());
    }
    
    public boolean molExists(String name) {
       Integer i = (Integer)map.get(name); 
        if(i == null)
            return false;       
        else
            return true;
    }
    
    /**
     * Returns the selected molecule
     * @param index The index of the molecule to return
     * @return The selected SimpleMolecule
     */
    public SimpleMolecule getMol(int index) { 
        if(index > mols.size()) {
            System.out.println("Index is to large.");
            return null;
        }
            
        return (SimpleMolecule)mols.get(index); 
    }
    
    /**
     * Imports a file with molecules. Only 'SD' is supported now.
     * @param fileName 
     * @param format 
     */
    public void importFile(String fileName, String format) {
        if(format.equals("SD"))
            importSDFile(fileName);
        else
            System.out.println("Import format not supported.");
    }    

    // Imports an file with molecules in SD format.
    private void importSDFile(String fileName) {
        try{
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            String row = null;
            StringBuffer buff = new StringBuffer();
            int i = 1;
            SimpleMolecule mol = null;

            while(in.ready()){
                String r1, r2, r3;
                r1 = in.readLine().trim();  // Molecule Name
                r2 = in.readLine().trim();  // Data line
                r3 = in.readLine().trim();  // Comment line
                
                //while(row.length() == 0)
                //    row = in.readLine().trim();

                mol = new SimpleMolecule(r1, r2, r3);  
                
                // Read counts line
                row = in.readLine().trim();
                
                
                buff.append(row+"@");
                while((!row.startsWith("M  END") && in.ready()) && 
                        (!row.startsWith("M END") && in.ready()) &&
                        (!row.startsWith("$$$$"))){
                    row = in.readLine();
                    if (!row.equals("$$$$"))
                        buff.append(row+"@");
                    else
                        buff.append("M  END");
                }

                mol.addRawData(buff.toString());
                
                
               

                while(!row.startsWith("$$$$") && in.ready()){
                    row = in.readLine().trim();
                    if(row.length() > 0 && row.startsWith(">")) {
                        mol.addTag(row, in.readLine().trim());
                    }

                } 
                
                //mol.getMolInfo();

                mols.add(mol);
                map.put(mol.getName(), new Integer(i-1));

                buff = new StringBuffer();
                ++i;
                
            }
            in.close();
        } catch (IOException ioe) {
            System.out.println("Error importing: "+ioe.getMessage());
        }             
    }
    
    // Imports an file with molecules in SD format.
    public void importSDFileSpec(String fileName, String[][] ids) {
        try{
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            String row = null;
            StringBuffer buff = new StringBuffer();
            int i = 1;
            SimpleMolecule mol = null;
            ArrayList ext = new ArrayList();

            while(in.ready()){
                row = in.readLine().trim();
                while(row.length() == 0)
                    row = in.readLine().trim();

                mol = new SimpleMolecule(row);  
                
                
                buff.append(row+"@");
                while((!row.startsWith("M  END") && in.ready()) && (!row.startsWith("M END") && in.ready())){
                    row = in.readLine();
                    buff.append(row+"@");
                }

                mol.addRawData(buff.toString());

                while(!row.startsWith("$$$$") && in.ready()){
                    row = in.readLine().trim();
                    if(row.length() > 0 && row.startsWith(">")) {
                        mol.addTag(row, in.readLine().trim());
                    }

                }            

                for(int k=1;k<ids.length;k++) {
                    if(ids[k][2].equals(mol.getName())) {
                        mols.add(mol);
                        ext.add(ids[k]);
                        break;
                    }
                }

                map.put(mol.getName(), new Integer(i-1));

                buff = new StringBuffer();
                ++i;
                
            }
            
            MolExporter exp = new MolExporter();
            exp.export("cd_ext.sdf", "SD", mols);
            
            BufferedWriter out = new BufferedWriter(new FileWriter("detected.txt"));
            Iterator ctr = ext.iterator();
            while(ctr.hasNext()) {
                String[] arow = (String[])ctr.next();
                out.write(arow[0]+";"+arow[1]+";"+arow[2]);
                out.newLine();
            }
            out.close();
            
            in.close();
        } catch (IOException ioe) {
            System.out.println("Error importing: "+ioe.getMessage());
        }             
    }    
}
