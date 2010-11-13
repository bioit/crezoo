package org.tgdb.resource.resourcemanager;

import org.tgdb.frame.DTO;
import org.tgdb.resource.resource.ResourceRemote;

public class ResourceDTO extends DTO {
    private String resourceName, resourceLink, resourceType, resourceComment, user;
    private int resourceId, userId;
    private int categoryId;
    private String categoryName;
    
    /** Creates a new instance of ResourceDTO */
    public ResourceDTO(ResourceRemote resource) {
        try {
            resourceName = resource.getName();
            resourceLink = processURl(resource.getResourceLink());
            resourceType = resource.getResourceType();
            resourceId = resource.getResourceId();
            resourceComment = resource.getComment();
            user = resource.getUser().getUsr();
            userId = resource.getUser().getId();
            
            categoryId = resource.getResourceCategory().getResourceCategoryId();
            categoryName = resource.getResourceCategory().getName();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getUser() {
        return user;
    }
    
    public String getUserId() {
        return ""+userId;
    }    

    public String getResourceName() {
        return resourceName;
    }

    public String getResourceLink() {
        return resourceLink;
    }

    public String getResourceType() {
        return resourceType;
    }

    public int getResourceId() {
        return resourceId;
    }
    
    public String getResourceComment() {
        return resourceComment;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
}
