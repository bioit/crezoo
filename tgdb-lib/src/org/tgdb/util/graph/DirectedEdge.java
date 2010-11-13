/*
 * DirectedEdge.java
 *
 * Created on October 25, 2005, 2:30 PM
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
public class DirectedEdge extends Edge implements Serializable {
    private Node towards;
    
    /** Creates a new instance of DirectedEdge */
    public DirectedEdge(Node from, Node to, Node towards, String label) {
        super(from, to, label);
        this.towards = towards;   
    }
    
    public DirectedEdge(Node from, Node to, String label) {
        super(from, to, label);
        this.towards = to;
    }    
    
    public DirectedEdge(Node towards, String label) {
        this.towards = towards;
        this.label = label;
    }
    
    public DirectedEdge() {        
    }
    
    public Node getDirection() {
        return towards;
    }
    
    public void setDirection(Node towards) {
        this.towards = towards;
    }
}
