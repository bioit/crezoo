/*
 * Edge.java
 *
 * Created on October 25, 2005, 2:29 PM
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
public class Edge  implements Serializable {
    protected Node from;
    protected Node to;
    protected String label;
    
    /** Creates a new instance of Edge */
    public Edge(Node from, Node to, String label) {
        this.from = from;
        this.to = to;
        this.label = label;
        from.setConnected(true);
        to.setConnected(true);
    }
    
    public Edge(Node from, Node to) {
        this.from = from;
        this.to = to;
        from.setConnected(true);
        to.setConnected(true);        
    }
    
    public Edge() {}
    
    public Node getFrom() {
        return from;
    }
    
    public Node getTo() {
        return to;
    }
    
    public void setFrom(Node from) {
        this.from = from;
        from.setConnected(true);        
    }
    
    public void setTo(Node to) {
        this.to = to;
        to.setConnected(true);         
    }
    
    public String getLabel() {
        if(label == null)
            label = "";
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public boolean containsNode(Node node) {
        if(node.equals(from) || node.equals(to))
            return true;
        return false;
    }
}
