/*
 * ResourceListTag.java
 *
 * Created on January 19, 2006, 2:57 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.tags;

import org.tgdb.frame.Caller;
import org.tgdb.resource.resourcemanager.ResourceCategoryDTO;
import org.tgdb.resource.resourcemanager.ResourceDTO;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Builds HTML for handling of resources...
 *
 *
 * @author lami
 */
public class ResourceListTag extends BodyTagSupport {
    private Collection categories;
    private String projectId, moduleId, editWorkflow, createLinkWorkflow, createFileWorkflow, deleteWorkflow, categoryUnAssignWorkflow, categoryDeleteWorkflow, categoryCreateWorkflow;
    
    /** Creates a new instance of ResourceListTag */
    public ResourceListTag() {
    }
    
    public void setCategories(String collectionName) {
        this.categories = (Collection)pageContext.getRequest().getAttribute(collectionName);  
    }    
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
        this.moduleId = projectId;
    }
    
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;       
    }    
    
    public void setEditWorkflow(String editWorkflow) {
        this.editWorkflow = editWorkflow;
    }
    
    public void setCreateLinkWorkflow(String createLinkWorkflow) {
        this.createLinkWorkflow = createLinkWorkflow;
    }  
    
    public void setDeleteWorkflow(String deleteWorkflow) {
        this.deleteWorkflow = deleteWorkflow;
    }
    
    public void setCreateFileWorkflow(String createFileWorkflow) {
        this.createFileWorkflow = createFileWorkflow;
    }    
    
    public void setCategoryUnAssignWorkflow(String categoryUnAssignWorkflow) {
        this.categoryDeleteWorkflow = categoryUnAssignWorkflow;
    }
    
    public void setCategoryDeleteWorkflow(String categoryDeleteWorkflow) {
        this.categoryDeleteWorkflow = categoryDeleteWorkflow;
    }    
    
    public void setCategoryCreateWorkflow(String categoryCreateWorkflow) {
        this.categoryCreateWorkflow = categoryCreateWorkflow;        
    }     
    
    /**
     * Builds the HTML code for the tag
     * @throws javax.servlet.jsp.JspException If the HTML code could not be created
     * @return The BodyTagSupport.SKIP_BODY integer value if everything went fine
     */
    public int doStartTag() throws JspException 
    {
        String data = "";
        ImageTag imageTag = new ImageTag();
        try
        {
            Caller caller = (Caller)pageContext.getSession().getAttribute("caller");
            
            data += "<table class=\"block_data\">";
            data += "<tr>";
            data += "<th class=\"block_data\" width=\"20%\">Category";
            if(caller.hasPrivilege("RESOURCE_W") || caller.isAdmin()) {
                data += "&nbsp;<a href=\"Controller?workflow="+categoryCreateWorkflow+"&pid="+projectId+"\"><img src=\"images/icons/add2.png\" title=\"Add a resource category\"></a>";
            }
            data += "</th>";
            data += "<th class=\"block_data\" width=\"30%\">Name</th>";
            data += "<th class=\"block_data\" width=\"10%\">Type</th>";
            data += "<th class=\"block_data\" width=\"20%\">Added by</th>";
            if(caller.hasPrivilege("RESOURCE_W") || caller.isAdmin()) {
                data += "<th class=\"block_data\" width=\"10%\">Edit</th>";
                data += "<th class=\"block_data\" width=\"10%\">Remove</th>";
            }
            data += "</tr>";
            
            Iterator i = categories.iterator();
            while(i.hasNext()) {
                ResourceCategoryDTO category = (ResourceCategoryDTO)i.next();
                Collection resources = category.getCatResources();
                data += "<tr>";
                data += "<td colspan=5><b>"+category.getCatName()+"</b>";
                if(caller.hasPrivilege("RESOURCE_W") || caller.isAdmin()) {
                        data += "&nbsp;<a href=\"Controller?workflow="+categoryDeleteWorkflow+"&id="+moduleId+"&catId="+category.getCatId()+"\" onClick=\"return confirm('Delete resource category? WARNING! ALL RESOURCES FOR THE CATEGORY WILL ALSO BE REMOVED!')\"><img src=\"images/icons/delete2.png\" border=0 title=\"Remove this category...\"></a>";
                        data += "&nbsp;<a href=\"Controller?workflow="+createFileWorkflow+"&id="+moduleId+"&catId="+category.getCatId()+"\"><img src=\"images/icons/add2.png\" border=0 title=\"Add a file resource\"></a>";
                        data += "&nbsp;<a href=\"Controller?workflow="+createLinkWorkflow+"&id="+moduleId+"&catId="+category.getCatId()+"\"><img src=\"images/icons/bookmark_add.png\" border=0 title=\"Add a link resource\"></a>";
                }                
                data += "</td>";
                data += "</tr>";
                int ctr = 0;
                String className = "alternatingOne";
                
                Iterator j = resources.iterator();
                while(j.hasNext()) {
                    ResourceDTO resource = (ResourceDTO)j.next();
                    String type = resource.getResourceType();
                    
                    
                    if(ctr == 1) {
                        className = "alternatingTwo";
                        ctr = 0;
                    }
                    else {
                        ctr++;
                        className = "alternatingOne";
                    }
                    
                    data += "<tr class=\""+className+"\">";
                    data += "<td width=\"20%\">&nbsp</td>";
                    
                    String target = "_blank";
                    if(type.equalsIgnoreCase("File"))
                        target = "";
                    data += "<td width=\"30%\"><a href=\""+resource.getResourceLink()+"\" title=\""+resource.getResourceComment()+"\" target=\""+target+"\">"+resource.getResourceName()+"</a></td>";
                    data += "<td width=\"10%\">"+resource.getResourceType()+"</td>";
                    data += "<td width=\"20%\"><a href=\"Controller?workflow=ViewUser&id="+resource.getUserId()+"\">"+resource.getUser()+"</a></td>";
                    if(caller.hasPrivilege("RESOURCE_W") || caller.isAdmin()) {                        
                        int resourceId = resource.getResourceId();
                        String editLink = "Controller?workflow="+editWorkflow+"Link&resourceId="+resourceId+"&id="+moduleId;
                        if(type.equalsIgnoreCase("File"))
                            editLink = "Controller?workflow="+editWorkflow+"File&resourceId="+resourceId+"&id="+moduleId;
                        data += "<td width=\"10%\"><a href=\""+editLink+"\"><img src=\"images/icons/edit.png\" border=\"0\" title=\"Edit resource\"></a></td>";
                        data += "<td width=\"10%\"><a href=\"Controller?workflow="+deleteWorkflow+"&id="+moduleId+"&resourceId="+resourceId+"\" onClick=\"return confirm('Delete resource?')\"><img src=\"images/icons/delete2.png\" border=\"0\" title=\"Remove resource\"></a></td>";
                    }           
                    data += "</tr>";
                }          
            }
            
            data += "</table>";
            
            // Print the table on the page
            pageContext.getOut().print(data);
            
        }
        catch (Exception e) {
            throw new JspTagException(e.getMessage());
        }
        
        return SKIP_BODY;
    }    
}
