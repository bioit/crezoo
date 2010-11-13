/*
 * ResourceCategoryDTO.java
 *
 * Created on January 18, 2006, 2:33 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.resource.resourcemanager;

import org.tgdb.resource.resource.ResourceRemote;
import org.tgdb.resource.resourcecategory.ResourceCategoryRemote;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author lami
 */
public class ResourceBranchDTO implements Comparable, Serializable {
    private String catName, comm;
    private int catId, projectId, numberOfResources;
    private Collection catResources;
    
    /** Creates a new instance of ResourceCategoryDTO */
    public ResourceBranchDTO(ResourceCategoryRemote category) {
        try {
            Collection tmp = category.getResources();
            catResources = new ArrayList();
            Iterator i = tmp.iterator();
            while(i.hasNext()) {
                catResources.add(new ResourceDTO((ResourceRemote)i.next()));
            }
            numberOfResources = catResources.size();    
            catName = category.getName();
            catId = category.getResourceCategoryId();
            comm = category.getComment();
            projectId = category.getProject().getPid();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public ResourceBranchDTO(ResourceCategoryRemote category, Collection resources) {
        try {
            catResources = new ArrayList();
            Iterator i = resources.iterator();
            while(i.hasNext()) {
                catResources.add(new ResourceDTO((ResourceRemote)i.next()));
            }
            numberOfResources = catResources.size();    
            catName = category.getName();
            catId = category.getResourceCategoryId();
            comm = category.getComment();
            projectId = category.getProject().getPid();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }    

    public String getCatName() {
        return catName;
    }
    
    public int getNumberOfResources() {
        return numberOfResources;
    }
    
    public String getComm() {
        return comm;
    }    

    public int getCatId() {
        return catId;
    }
    
    public int getProjectId() {
        return projectId;
    }

    public Collection getCatResources() {
        return catResources;
    }
    
    public boolean equals(Object o) {
        if(o instanceof ResourceCategoryDTO)
            return catId == ((ResourceCategoryDTO)o).getCatId();
        return false;
    }
    
    public int compareTo(Object o) {
        if(o instanceof ResourceCategoryDTO)
            return catId - ((ResourceCategoryDTO)o).getCatId();  
        return -1;
    }
}
