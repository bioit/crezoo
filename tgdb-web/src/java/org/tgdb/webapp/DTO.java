/*
 * DAO.java
 *
 * Created on July 7, 2005, 1:03 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp;

import java.util.Collection;

/**
 * Super class for the Data Transfer Objects (DTO).
 * @author lami
 */
public class DTO {
    private Collection dto;
    
    /**
     * Creates a new instance of DAO
     * @param dao A collection of data
     */
    public DTO(Collection dao) {
        this.dto = dto;
    }
    
    /**
     * Returns the collection of data
     * @return The collection of data
     */
    public Collection getCollection() {
        return dto;
    }
}
