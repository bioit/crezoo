/*
 * MolExporter.java
 *
 * Created on September 21, 2005, 9:43 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.io;

import org.tgdb.util.SimpleMolecule;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class for export of one or several molecules.
 * @author lami
 */
public class MolExporter {
    
    /**
     * Creates a new instance of MolExporter 
     */
    public MolExporter() {
    }
    
    /**
     * Exports one molecule to a file
     * @param fileName The name of the destination file
     * @param format The format to export in (currently only 'SD' is accepted)
     * @param mol The molecule to export
     */
    public void export(String fileName, String format, SimpleMolecule mol) {
        if(format.equals("SD"))
            exportToSDFile(fileName, mol);
        else
            System.out.println("Export format not supported.");
    }
    
    /**
     * Exports molecules to a file
     * @param fileName The name of the destination file
     * @param format The format to export in (currently only 'SD' is accepted)
     * @param mols The molecules to export
     */
    public void export(String fileName, String format, ArrayList mols) {
        if(format.equals("SD"))
            exportToSDFile(fileName, mols);
        else
            System.out.println("Export format not supported.");
    }    
        
    private void exportToSDFile(String fileName, SimpleMolecule mol) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
            Iterator tagItr = null;
            Iterator valItr = null;

            String[] rawData = null;
            ArrayList[] tags = null;
            ArrayList tag = null;
            ArrayList value = null;

            rawData = mol.getRawData();
            
            out.write(mol.getName()+"\n");
            out.write(mol.getData()+"\n");
            out.write(mol.getComment()+"\n");
                  
            
            // First, write all raw data
            for(int j = 0;j<rawData.length;j++) {
                out.write(rawData[j]);
                out.newLine();
            }
            
            // Get the tags and print them
            tags = mol.getTags();
            tag = tags[0];
            value = tags[1];
            tagItr = tag.iterator();
            valItr = value.iterator();
            while(tagItr.hasNext()) {
                out.write((String)tagItr.next());
                out.newLine();
                out.write((String)valItr.next());
                out.newLine();
                out.newLine();
            }

            // Write entry ending
            out.write("$$$$");            
            out.close();
        } catch (IOException ioe) {
            System.out.println("Error exporting molecule: "+ioe.getMessage());
        }             
    }
    
    private void exportToSDFile(String fileName, ArrayList mols) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
            Iterator i = mols.iterator();
            Iterator tagItr = null;
            Iterator valItr = null;

            SimpleMolecule mol = null;
            String[] rawData = null;
            ArrayList[] tags = null;
            ArrayList tag = null;
            ArrayList value = null;

            // For each molecule
            while(i.hasNext()) {
                mol = (SimpleMolecule)i.next();
                
                out.write(mol.getName()+"\n");
                out.write(mol.getData()+"\n");
                out.write(mol.getComment()+"\n");
                              
                rawData = mol.getRawData();

                // Write the raw data
                for(int j = 0;j<rawData.length;j++) {
                    out.write(rawData[j]);
                    out.newLine();
                }

                // Get the tags and print them
                tags = mol.getTags();
                tag = tags[0];
                value = tags[1];
                tagItr = tag.iterator();
                valItr = value.iterator();
                while(tagItr.hasNext()) {
                    out.write((String)tagItr.next());
                    out.newLine();
                    out.write((String)valItr.next());
                    out.newLine();
                    out.newLine();
                }

                // Write the entry ending
                out.write("$$$$");
                out.newLine();
            }
            // And we are done
            out.close();
        } catch (IOException ioe) {
            System.out.println("Error exporting molecule: "+ioe.getMessage());
        }            
    }
}
