/*
 * Node.java
 *
 * Created on October 25, 2005, 2:27 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.util.graph;

import java.io.Serializable;

/**
 *
 * @author lami
 */
public class Node  implements Serializable {
    private Object contents;
    private int nodeId;
    private int level;
    private String shape;
    private boolean hasEdge;
    
    public Node(Object contents, int nodeId) {
        this.contents = contents;
        this.nodeId = nodeId;
    } 
    
    public Node(Object contents, int nodeId, String shape) {
        this.contents = contents;
        this.nodeId = nodeId;
        this.shape = shape;
    }     
    
    public Node(Object contents) {
        this.contents = contents;
    }
    
    public Node(Object contents, String shape) {
        this.contents = contents;
        this.shape = shape;
    }    

    public Node(int nodeId, String shape) {
        this.nodeId = nodeId;
        this.shape = shape;
    }    
    
    public Node(String shape) { this.shape = shape; }
    
    public Node() {}
    
    public Node(int nodeId) { this.nodeId = nodeId; }    
    
    public Object getContents() { return contents; }
    
    public void setContents(Object contents) { this.contents = contents; }
    
    public int getId() { return nodeId; }
    
    public void setId(int nodeId) { this.nodeId = nodeId; }
    
    public void setConnected(boolean hasEdge) { this.hasEdge = hasEdge; }
    
    public void setLevel(int level) { this.level = level; }
    
    public int getLevel() { return level; }
    
    public boolean isConnected() { return hasEdge; }
    
    public String getShape() { return shape; }
    
    public void setShape(String shape) { this.shape = shape; }
    
    public String getColor() {
        if(shape == null)
            return "#FFFFFF";
        if(shape.equalsIgnoreCase("elipse") || shape.equalsIgnoreCase("roundrectangle"))
            return "#FFFF00";
        else if(shape.equalsIgnoreCase("rectangle"))
            return "#CCCCFF";
        else if(shape.equalsIgnoreCase("diamond"))
            return "#CCFFCC";   
        else if(shape.equalsIgnoreCase("triangle"))
            return "#FF0000";           
        else 
            return "#FFFFFF";
    }
}
