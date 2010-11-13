/*
 * GraphExporter.java
 *
 * Created on October 26, 2005, 8:57 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.util.graph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author lami
 */
public class GraphExporter  implements Serializable {
    private BufferedWriter out;
    /** Creates a new instance of GraphExporter */
    public GraphExporter() {
    }
    
    public String exportGML(Graph g) {
        ArrayList edges = g.getEdges();
        ArrayList nodes = g.getNodes();
        Iterator i = nodes.iterator();
        DirectedEdge edge = null;
        Node node = null;
        String message = "graph [\n";
        
        // Write nodes
        while(i.hasNext()) {
            node = (Node)i.next();
            if(node != null) {
                message += "\tnode [\n";
                message += "\t\tid "+node.getId()+"\n";
                message += "\t\tlabel \""+node.getContents()+"\"\n";
                message += "\t]\n"; 
            }                        
        }
        
        i = edges.iterator();
        
        // Write edges
        while(i.hasNext()) {
            edge = (DirectedEdge)i.next();            

            if(edge != null) {
                // Root
                if(edge.getFrom() != null) {
                    message += "\tedge [\n";
                    message += "\t\tlabel \""+edge.getLabel()+"\"\n";
                    message += "\t\tsource "+edge.getFrom().getId()+"\n";
                    message += "\t\ttarget "+edge.getTo().getId()+"\n";
                    message += "\t]\n";
                }  
            }                        
        }
        
        message += "]";
        return message;          
    }
    
    public void exportGML(Graph g, String fileName) throws GraphException {
        try {
            ArrayList edges = g.getEdges();
            ArrayList nodes = g.getNodes();
            Iterator i = nodes.iterator();
            DirectedEdge edge = null;
            Node node = null;
            out = new BufferedWriter(new FileWriter(fileName));
            write("graph [", 0);          

            // Write nodes
            while(i.hasNext()) {
                node = (Node)i.next();
                if(node != null) {
                    write("node [", 1);
                    write("id "+node.getId(), 2);
                    write("label \""+node.getContents()+"\"", 2);
                    String shape = node.getShape();
                    if(shape != null && shape.length() > 0) {
                        write("graphics [", 2);
                        write("type \""+node.getShape()+"\"", 3);                        
                        write("fill \""+node.getColor()+"\"", 3);
                        write("]", 2);                    
                    }
                    write("]", 1);
                }                        
            }

            i = edges.iterator();        
            
            // Write edges
            while(i.hasNext()) {
                edge = (DirectedEdge)i.next();            

                if(edge != null) {
                    write("edge [", 1);                    
                    
                    // Root
                    if(edge.getFrom() != null) {
                        write("source "+edge.getFrom().getId(), 2);
                        write("target "+edge.getTo().getId(), 2);                                              
                    }  
                    else {
                        write("target "+edge.getTo().getId(), 2);                                               
                    }
                    
                    write("label \""+edge.getLabel()+"\"", 2);  
                    write("LabelGraphics [", 2);
                    write("text \""+edge.getLabel()+"\"", 3);
                    write("fontSize 12", 3);
                    write("fontName \"Dialog\"", 3);
                    write("model \"siz_pos\"", 3);
                    write("postion \"tail\"", 3);
                    write("]", 2);
                    write("]", 1);                    
                }                        
            }

            write("]", 0);
            out.close();
        } catch(IOException ioe) {
            throw new GraphException("Failed to export graph!\nCause: \n\t"+ioe.getMessage());
        }
    }
    
    private void write(String line, int indentLevel) throws IOException {
        for(int i=0;i<indentLevel;i++)
            out.write("\t");
        out.write(line);
        out.newLine();
    }
    
    private String append(String line, int indentLevel) {
        String indent = "";
        for(int i=0;i<indentLevel;i++)
            indent += "\t";
        return indent+line+"\n";
    }
    
    public void exportGXL(Graph g, String fileName) throws GraphException {
        try {
            ArrayList edges = g.getEdges();
            ArrayList nodes = g.getNodes();
            Iterator i = nodes.iterator();
            DirectedEdge edge = null;
            Node node = null;
            out = new BufferedWriter(new FileWriter(fileName));          
            
            String header = "";
            header += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
            header += "<!DOCTYPE gxl SYSTEM \"http://www.gupro.de/GXL/gxl-1.0.dtd\">\n";
            header += "<!-- Graph -->\n";
            header += "<gxl xmlns:xlink=\" http://www.w3.org/1999/xlink\">\n";
            header += "<graph id=\"IndividualRelationGraph\" edgeids=\"true\" edgemode=\"directed\" hypergraph=\"false\">";
            write(header, 0);          

            // Write nodes
            while(i.hasNext()) {
                node = (Node)i.next();
                if(node != null) {
                    write("<node id=\""+node.getId()+"\">", 1);
                                        
                    String shape = node.getShape();
                    if(shape != null && shape.length() > 0) {
                        write("<attr name=\"type\">", 2);
                        write("<string>"+node.getShape()+"</string>", 3);                        
                        write("</attr>", 2);
                        write("<attr name=\"fill\">", 2);
                        write("<string>"+node.getColor()+"</string>", 3);                                                    
                        write("</attr>", 2);
                    }
                    write("</node>", 1);
                }                        
            }

            i = edges.iterator();        
            int edgeId = 0;
            // Write edges
            while(i.hasNext()) {
                edge = (DirectedEdge)i.next();            
                if(edge != null) {                                                            
                    // Root
                    if(edge.getFrom() != null) {
                        write("<edge id=\""+edgeId+"\" to=\""+edge.getTo().getId()+"\" from=\""+edge.getFrom().getId()+"\">", 1);
                    }  
                    else {
                        write("<edge id=\""+edgeId+"\" to=\""+edge.getTo().getId()+"\">", 1);                                               
                    }                    
                    write("</edge>", 1);                    
                }       
                edgeId++;
            }

            write("</graph>", 0);
            write("</gxl>", 0);
            out.close();
        } catch(IOException ioe) {
            throw new GraphException("Failed to export graph!\nCause: \n\t"+ioe.getMessage());
        }        
    }
    
    public void exportXGML(Graph g, String fileName) throws GraphException {
        try {
            ArrayList edges = g.getEdges();
            ArrayList nodes = g.getNodes();
            Iterator i = nodes.iterator();
            DirectedEdge edge = null;
            Node node = null;
            out = new BufferedWriter(new FileWriter(fileName));          
            
            String header = "<section name=\"xgml\">\n";
            header += "\t<attribute key=\"Creator\" type=\"String\">Arx graph exporter</attribute>\n";
            header += "\t<attribute key=\"Version\" type=\"String\">1.0.0</attribute>\n";
            header += "\t<section name=\"graph\">\n";
            header += "\t\t<attribute key=\"hierarchic\" type=\"int\">1</attribute>\n";
            header += "\t\t<attribute key=\"directed\" type=\"int\">1</attribute>";
            write(header, 0);          

            // Write nodes
            while(i.hasNext()) {
                node = (Node)i.next();
                if(node != null) {
                    write("<section name=\"node\">", 2);
                    write("<attribute key=\"id\" type=\"int\">"+node.getId()+"</attribute>", 3);
                    write("<attribute key=\"label\" type=\"String\">"+node.getContents()+"</attribute>", 3);

                    String shape = node.getShape();
                    if(shape != null && shape.length() > 0) {
                        write("<section name=\"graphics\">", 3);
                        write("<attribute key=\"type\" type=\"String\">"+shape+"</attribute>", 4);
                        write("<attribute key=\"fill\" type=\"String\">"+node.getColor()+"</attribute>", 4);
                        write("</section>", 3);
                    }
                    write("</section>", 2);
                }                        
            }

            i = edges.iterator();        
            
            // Write edges
            while(i.hasNext()) {
                edge = (DirectedEdge)i.next();            

                if(edge != null) {
                    write("<section name=\"edge\">", 2);                    
                    write("<attribute key=\"label\" type=\"String\">"+edge.getLabel()+"</attribute>", 3);                                        
                    // Root
                    if(edge.getFrom() != null) {
                        write("<attribute key=\"target\" type=\"int\">"+edge.getTo().getId()+"</attribute>", 3);
                        write("<attribute key=\"source\" type=\"int\">"+edge.getFrom().getId()+"</attribute>", 3);
                    }  
                    else {
                        write("<attribute key=\"target\" type=\"int\">"+edge.getTo().getId()+"</attribute>", 3);                                              
                    }

                    write("</section>", 2);                    
                }                        
            }

            write("</section>", 1);
            write("</section>", 0);
            out.close();
        } catch(IOException ioe) {
            throw new GraphException("Failed to export graph!\nCause: \n\t"+ioe.getMessage());
        }        
    }    
    
    public String exportXGML(Graph g) throws GraphException {
        try {
            g.makeConnectedOnly();
            ArrayList edges = g.getEdges();
            ArrayList nodes = g.getNodes();
            Iterator i = nodes.iterator();
            DirectedEdge edge = null;
            Node node = null;
            String output = "";
            
            String header = "<section name=\"xgml\">\n";
            header += "\t<attribute key=\"Creator\" type=\"String\">Arx graph exporter</attribute>\n";
            header += "\t<attribute key=\"Version\" type=\"String\">1.0.0</attribute>\n";
            header += "\t<section name=\"graph\">\n";
            header += "\t\t<attribute key=\"hierarchic\" type=\"int\">1</attribute>\n";
            header += "\t\t<attribute key=\"directed\" type=\"int\">1</attribute>";
            output += append(header, 0);          

            // Write nodes
            while(i.hasNext()) {
                node = (Node)i.next();
                if(node != null) {
                    output += append("<section name=\"node\">", 2);
                    output += append("<attribute key=\"id\" type=\"int\">"+node.getId()+"</attribute>", 3);
                    output += append("<attribute key=\"label\" type=\"String\">"+node.getContents()+"</attribute>", 3);

                    String shape = node.getShape();
                    if(shape != null && shape.length() > 0) {
                        output += append("<section name=\"graphics\">", 3);
                        output += append("<attribute key=\"type\" type=\"String\">"+shape+"</attribute>", 4);
                        output += append("<attribute key=\"fill\" type=\"String\">"+node.getColor()+"</attribute>", 4);
                        output += append("</section>", 3);
                    }
                    output += append("</section>", 2);
                }                        
            }

            i = edges.iterator();        
            
            // Write edges
            while(i.hasNext()) {
                edge = (DirectedEdge)i.next();            

                if(edge != null) {
                    output += append("<section name=\"edge\">", 2);                    
                    output += append("<attribute key=\"label\" type=\"String\">"+edge.getLabel()+"</attribute>", 3);                                        
                    // Root
                    if(edge.getFrom() != null) {
                        output += append("<attribute key=\"target\" type=\"int\">"+edge.getTo().getId()+"</attribute>", 3);
                        output += append("<attribute key=\"source\" type=\"int\">"+edge.getFrom().getId()+"</attribute>", 3);
                    }  
                    else {
                        output += append("<attribute key=\"target\" type=\"int\">"+edge.getTo().getId()+"</attribute>", 3);                                              
                    }

                    output += append("</section>", 2);                    
                }                        
            }

            output += append("</section>", 1);
            output += append("</section>", 0);
            
            return output;
        } catch(Exception ioe) {
            throw new GraphException("Failed to export graph!\nCause: \n\t"+ioe.getMessage());
        } 
    }
    
    public String exportGXL(Graph g) throws GraphException {
        try {
            ArrayList edges = g.getEdges();
            ArrayList nodes = g.getNodes();
            Iterator i = nodes.iterator();
            DirectedEdge edge = null;
            Node node = null;
            String out = "";
            
            String header = "";
            header += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
            header += "<!DOCTYPE gxl SYSTEM \"http://www.gupro.de/GXL/gxl-1.0.dtd\">\n";
            header += "<!-- Graph -->\n";
            header += "<gxl xmlns:xlink=\" http://www.w3.org/1999/xlink\">\n";
            header += "<graph id=\"IndividualRelationGraph\" edgeids=\"true\" edgemode=\"directed\" hypergraph=\"false\">";
            out += append(header, 0);          

            // Write nodes
            while(i.hasNext()) {
                node = (Node)i.next();
                if(node != null) {
                    out += append("<node id=\""+node.getId()+"\">", 1);
                                        
                    String shape = node.getShape();
                    if(shape != null && shape.length() > 0) {
                        out += append("<attr name=\"type\">", 2);
                        out += append("<string>"+node.getShape()+"</string>", 3);                        
                        out += append("</attr>", 2);
                        out += append("<attr name=\"fill\">", 2);
                        out += append("<string>"+node.getColor()+"</string>", 3);                                                    
                        out += append("</attr>", 2);
                    }
                    out += append("</node>", 1);
                }                        
            }

            i = edges.iterator();        
            int edgeId = 0;
            // Write edges
            while(i.hasNext()) {
                edge = (DirectedEdge)i.next();            
                if(edge != null) {                                                            
                    // Root
                    if(edge.getFrom() != null) {
                        out += append("<edge id=\""+edgeId+"\" to=\""+edge.getTo().getId()+"\" from=\""+edge.getFrom().getId()+"\">", 1);
                    }  
                    else {
                        out += append("<edge id=\""+edgeId+"\" to=\""+edge.getTo().getId()+"\">", 1);                                               
                    }                    
                    out += append("</edge>", 1);                    
                }       
                edgeId++;
            }

            out += append("</graph>", 0);
            out += append("</gxl>", 0);
            return out;
        } catch(Exception e) {
            throw new GraphException("Failed to export graph!\nCause: \n\t"+e.getMessage());
        }   
    }   
    
    public void exportGraphML(Graph g, String fileName) throws GraphException {
        try {
            ArrayList edges = g.getEdges();
            ArrayList nodes = g.getNodes();
            Iterator i = nodes.iterator();
            DirectedEdge edge = null;
            Node node = null;
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
            
            String header = "";
            header += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
            header += "<!-- Graph -->\n";            
            header += "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" \n";
            header += "<xmlns:xsi=\" http://www.w3.org/2001/XMLSchema-instance\">\n";
            header += "xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns\n";
            header += "http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd>\n";
            header += "\t<key id=\"d0\" for=\"node\" attr.name=\"color\" attr.type=\"string\">yellow</key>\n";
            header += "\t<key id=\"d1\" for=\"node\" attr.name=\"type\" attr.type=\"string\">rectangle</key>\n";
            header += "\t<graph id=\"IndividualRelationGraph\" edgeDefaut=\"directed\">";
            write(header, 0);          
           
            // Write nodes
            while(i.hasNext()) {
                node = (Node)i.next();
                if(node != null) {
                    write("<node id=\""+node.getId()+"\">", 1);
                                        
                    String shape = node.getShape();
                    if(shape != null && shape.length() > 0) {
                        write("<data key=\"d0\">"+node.getShape()+"</data>", 2);                        
                        write("<data key=\"d1\">"+node.getColor()+"</data>", 2);                        
                    }
                    write("</node>", 1);
                }                        
            }

            i = edges.iterator();        
            int edgeId = 0;
            // Write edges
            while(i.hasNext()) {
                edge = (DirectedEdge)i.next();            
                if(edge != null) {                                                            
                    // Root
                    if(edge.getFrom() != null) {
                        write("<edge id=\""+edgeId+"\" directed=\"true\" target=\""+edge.getTo().getId()+"\" source=\""+edge.getFrom().getId()+"\">", 1);
                    }  
                    else {
                        write("<edge id=\""+edgeId+"\"  directed=\"true\" target=\""+edge.getTo().getId()+"\">", 1);                                               
                    }                    
                    write("</edge>", 1);                    
                }       
                edgeId++;
            }

            write("</graph>", 1);
            write("</graphml>", 0);
            out.close();
        } catch(Exception e) {
            throw new GraphException("Failed to export graph!\nCause: \n\t"+e.getMessage());
        }           
    }
    
    public String exportGraphML(Graph g) throws GraphException {
        try {
            ArrayList edges = g.getEdges();
            ArrayList nodes = g.getNodes();
            Iterator i = nodes.iterator();
            DirectedEdge edge = null;
            Node node = null;
            String out = "";
            
            String header = "";
            header += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
            header += "<!-- Graph -->\n";            
            header += "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" \n";
            header += "<xmlns:xsi=\" http://www.w3.org/2001/XMLSchema-instance\">\n";
            header += "xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns\n";
            header += "http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd>\n";
            header += "\t<key id=\"d0\" for=\"node\" attr.name=\"color\" attr.type=\"string\">yellow</key>\n";
            header += "\t<key id=\"d1\" for=\"node\" attr.name=\"type\" attr.type=\"string\">rectangle</key>\n";
            header += "\t<graph id=\"IndividualRelationGraph\" edgeDefaut=\"directed\">";
            out += append(header, 0);          
           
            // Write nodes
            while(i.hasNext()) {
                node = (Node)i.next();
                if(node != null) {
                    out += append("<node id=\""+node.getId()+"\">", 1);
                                        
                    String shape = node.getShape();
                    if(shape != null && shape.length() > 0) {
                        out += append("<data key=\"d0\">"+node.getShape()+"</data>", 2);                        
                        out += append("<data key=\"d1\">"+node.getColor()+"</data>", 2);                        
                    }
                    out += append("</node>", 1);
                }                        
            }

            i = edges.iterator();        
            int edgeId = 0;
            // Write edges
            while(i.hasNext()) {
                edge = (DirectedEdge)i.next();            
                if(edge != null) {                                                            
                    // Root
                    if(edge.getFrom() != null) {
                        out += append("<edge id=\""+edgeId+"\" directed=\"true\" target=\""+edge.getTo().getId()+"\" source=\""+edge.getFrom().getId()+"\">", 1);
                    }  
                    else {
                        out += append("<edge id=\""+edgeId+"\"  directed=\"true\" target=\""+edge.getTo().getId()+"\">", 1);                                               
                    }                    
                    out += append("</edge>", 1);                    
                }       
                edgeId++;
            }

            out += append("</graph>", 1);
            out += append("</graphml>", 0);
            return out;
        } catch(Exception e) {
            throw new GraphException("Failed to export graph!\nCause: \n\t"+e.getMessage());
        }   
    }
}
