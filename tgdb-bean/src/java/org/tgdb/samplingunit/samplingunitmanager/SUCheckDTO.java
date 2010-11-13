/*
 * SUCheckDTO.java
 *
 * Created on October 20, 2005, 2:46 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.samplingunit.samplingunitmanager;

import java.io.Serializable;

/**
 * Data transfer object for information when performing an individaul check
 * @author lami
 */
public class SUCheckDTO implements Serializable {
    private String identity;
    private String fNotMale;
    private String fDisabled;
    private String fTooYoung;
    private String mNotFemale;
    private String mDisabled;
    private String mTooYoung;    
          
    private SUCheckDTO()
    {}
    
    /**
     * Creates a new instance of SUCheckDTO
     * @param identity The identity of the individual
     */
    public SUCheckDTO(String identity) {
        this.identity = identity;
        fNotMale = "";
        fDisabled = "";
        fTooYoung = "";
        mNotFemale = "";
        mDisabled = "";
        mTooYoung = "";    
    }

    /**
     * Returns the identity of the individual
     * @return The identity of the individual
     */
    public String getIdentity() {
        return identity;
    }

    /**
     * Sets the identity of the individual
     * @param identity The identity
     */
    public void setIdentity(String identity) {
        this.identity = identity;
    }

    /**
     * Returns wether or not the father is male
     * @return A string mark if not male
     */
    public String getFNotMale() {
        return fNotMale;
    }

    /**
     * Sets wether or not the father is male
     * @param fNotMale The string mark for if father is male
     */
    public void setFNotMale(String fNotMale) {
        this.fNotMale = fNotMale;
    }

    /**
     * Returns wether or not the father is disabled
     * @return A string mark if father is disabled
     */
    public String getFDisabled() {
        return fDisabled;
    }

    /**
     * Sets a string mark for if the father is disabled
     * @param fDisabled The string mark for father disabled
     */
    public void setFDisabled(String fDisabled) {
        this.fDisabled = fDisabled;
    }

    /**
     * Returns a string mark if the father is too young
     * @return A string mark if the father is too young
     */
    public String getFTooYoung() {
        return fTooYoung;
    }

    /**
     * Sets a string mark if showing that the father is too young
     * @param fTooYoung A string mark
     */
    public void setFTooYoung(String fTooYoung) {
        this.fTooYoung = fTooYoung;
    }

    /**
     * Returns a string mark if the mother is not female
     * @return A string mark
     */
    public String getMNotFemale() {
        return mNotFemale;
    }

    /**
     * Sets a string mark for mother not female
     * @param mNotFemale A string mark
     */
    public void setMNotFemale(String mNotFemale) {
        this.mNotFemale = mNotFemale;
    }

    /**
     * Returns a string mark if mother is disabled
     * @return A string mark if mother is disabled
     */
    public String getMDisabled() {
        return mDisabled;
    }

    /**
     * Sets a string mark if mother is disabled
     * @param mDisabled A string mark
     */
    public void setMDisabled(String mDisabled) {
        this.mDisabled = mDisabled;
    }

    /**
     * Returns a string mark if mother is too young
     * @return A string mark
     */
    public String getMTooYoung() {
        return mTooYoung;
    }

    /**
     * Sets a string mark for mother is too young
     * @param mTooYoung A string mark
     */
    public void setMTooYoung(String mTooYoung) {
        this.mTooYoung = mTooYoung;
    }
    
}
