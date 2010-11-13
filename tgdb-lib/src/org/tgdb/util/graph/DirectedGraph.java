/*
 * DirectedGraph.java
 *
 * Created on October 25, 2005, 3:07 PM
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
public class DirectedGraph extends Graph implements Serializable {
    
    /** Creates a new instance of DirectedGraph */
    public DirectedGraph() {
    }
    
    public void addEdge(Edge edge) throws GraphException {
        if(edge instanceof DirectedEdge)
            edges.add(edge);
        else
            throw new GraphException("Edge is not directed.");
    }
}
