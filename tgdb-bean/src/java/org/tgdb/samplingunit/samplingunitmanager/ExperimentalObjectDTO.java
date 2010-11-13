/*
 * ExperimentalObjectDTO.java
 *
 * Created on December 20, 2005, 1:28 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.samplingunit.samplingunitmanager;
import java.io.Serializable;

/**
 * Data transfer object for an experimental object
 * @author lami
 */
public class ExperimentalObjectDTO implements Serializable, Comparable {
    private int eid, userId;
    private String userName, identity, alias, comm, ts;
    
    ExperimentalObjectDTO()
    {
    }
    
    /**
     * Creates a new instance of ExperimentalObjectDTO
     * @param eid The experimental object id
     * @param identity The identity of the experimental object
     * @param userName The username of the user that made the last changes on the experimental object
     * @param userId The id of the user that made the last changes on the experimental object
     * @param alias The alias of the experimental object
     * @param comm The comment for the experimental object
     * @param ts The date for when the experimental object was last changed
     */
    public ExperimentalObjectDTO(int eid, String identity, String userName, int userId, String alias, String comm, String ts) {
        this.eid = eid;
        this.identity = identity;
        this.comm = comm;
        this.alias = alias;
        this.ts = ts;
        this.userId = userId;
        this.userName = userName;
    }
    
    /**
     * Compares this object to another object
     * @param anotherObject The object to compare to
     * @return Wether or not they have the same id
     */
    public int compareTo(Object anotherObject) {
        ExperimentalObjectDTO dto = (ExperimentalObjectDTO)anotherObject;
        return identity.compareTo(dto.getIdentity());
    }

    /**
     * Returns the id of the experimental object
     * @return The experimental object id
     */
    public int getEid() {
        return eid;
    }

    /**
     * Returns the id of the user that made the last changes on the experimental object
     * @return The id of the user that made the last changes on the experimental object
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Returns the username of the user that made the last changes on the experimental object
     * @return The username of the user that made the last changes on the experimental object
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Returns the identity of the experimental object
     * @return The identity of the experimental object
     */
    public String getIdentity() {
        return identity;
    }

    /**
     * Returns the alias of the experimental object
     * @return The alias of the experimental object
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Returns the comment for the experimental object
     * @return The comment for the experimental object
     */
    public String getComm() {
        return comm;
    }

    /**
     * Returns the date for when the experimental object was last changed
     * @return The date for when the experimental object was last changed
     */
    public String getTs() {
        return ts;
    }
    
}
