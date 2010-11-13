/*
 * Graph.java
 *
 * Created on October 25, 2005, 2:26 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.util.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author lami
 */
public class Graph  implements Serializable {
    protected ArrayList nodes;
    protected ArrayList edges;    
    
    /** Creates a new instance of Graph */
    public Graph() {
        this.nodes = new ArrayList();
        this.edges = new ArrayList();
    }    
    
    public void makeConnectedOnly() throws GraphException {
        Iterator i = nodes.iterator();
        Node node = null;
        ArrayList removeThese = new ArrayList();        
        System.out.println("Nodes size: "+nodes.size());
        while(i.hasNext()) {
            node = (Node)i.next();
            if(!node.isConnected())
                removeThese.add(node);
        }
        
        i = removeThese.iterator();
        
        while(i.hasNext()) {
            removeNode((Node)i.next());
        }
      
        System.out.println("Nodes size: "+nodes.size());
    }
    
    public void addNode(Node node) {
        nodes.add(node);
    }
    
    public Node getNode(int index) throws GraphException {
        if(index > 0 || index < nodes.size())
            return (Node)nodes.get(index);
        else
            throw new GraphException("Node index out of bounds.");
    }
    
    public Node getNode(Node node) {
        return (Node)nodes.get(nodes.indexOf(node));
    }
    
    public Node getNodeById(int id) {
        Iterator i = nodes.iterator();
        Node tmp = null;
        while(i.hasNext()) {
            tmp = (Node)i.next();
            if(tmp.getId() == id)
                return tmp;
        }
            
        return null;
    }
    
    public void removeNode(int index) throws GraphException {
        if(index > 0 || index < nodes.size())
            nodes.remove(index);
        else
            throw new GraphException("Node index out of bounds.");        
    }
    
    public void removeEdge(Edge edge) {
        edges.remove(edges.indexOf(edge));
    }
    
    public void removeEdge(int index) throws GraphException {
        if(index > 0 || index < edges.size())
            edges.remove(index);
        else
            throw new GraphException("Edge index out of bounds.");        
    }
    
    public void removeNode(Node node) throws GraphException {
        int index = nodes.indexOf(node);
        if(index > 0 || index < nodes.size())
            nodes.remove(index);
        else
            throw new GraphException("Node index out of bounds."); 
    }    
    
    public void clear() {
        nodes.clear();
        edges.clear();
    }
    
    public boolean containsNode(Node node) {
        return nodes.contains(node);
    }
    
    public boolean containsEdge(Edge edge) {
        return edges.contains(edge);
    }    
    
    public int numberOfNodes() {
        return nodes.size();
    }
    
    public int numberOfEdges() {
        return edges.size();
    }    
    
    public ArrayList getNodes() {
        return nodes;
    }
    
    public ArrayList getEdges() {
        return edges;
    }    
    
    public Edge getEdge(int index) throws GraphException {
        if(index < 0 || index > edges.size())
            throw new GraphException("Edge index out of bounds.");
        return (Edge)edges.get(index);
    }
    
    public Edge getEdge(Edge edge) {
        return (Edge)edges.get(edges.indexOf(edge));        
    }
    
    public Edge getEdge(Node from, Node to) {
        Edge e = null;
        Iterator i = edges.iterator();
        while(i.hasNext()) {
            e = (Edge)i.next();
            if(from.equals(e.getFrom()) && to.equals(e.getTo()))
                return e;
        }
        
        return null;
    } 
    
    public void addEdge(Edge edge) throws GraphException {
        edges.add(edge);
    }
    
    public ArrayList getEdgesConnectingTo(Node node) {
        ArrayList list = new ArrayList();
        Iterator i = edges.iterator();
        Edge edge = null;
        while(i.hasNext()) {
            edge = (Edge)i.next();
            if(node.equals(edge.getTo()))
                list.add(edge);
        }
        
        return list;
    }
    
    public ArrayList getNodesConnectingTo(Node node) {
        ArrayList list = new ArrayList();
        Iterator i = edges.iterator();
        Edge edge = null;
        while(i.hasNext()) {
            edge = (Edge)i.next();
            if(node.equals(edge.getFrom()))
                list.add(edge.getTo());
        }
        
        return list;
    }    
}
