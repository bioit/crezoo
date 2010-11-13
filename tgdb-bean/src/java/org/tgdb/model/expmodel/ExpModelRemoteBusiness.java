
package org.tgdb.model.expmodel;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.exceptions.PermissionDeniedException;
import org.tgdb.model.reference.ReferenceRemote;
import org.tgdb.model.researchapplication.ResearchApplicationRemote;
import org.tgdb.project.user.UserRemote;
import org.tgdb.resource.resource.ResourceRemote;
import org.tgdb.samplingunit.samplingunit.SamplingUnitRemote;
import java.rmi.RemoteException;
import java.util.Collection;

public interface ExpModelRemoteBusiness {
    String getDonating_investigator() throws java.rmi.RemoteException;
    void setDonating_investigator(String donating_investigator) throws java.rmi.RemoteException;
    String getInducible() throws java.rmi.RemoteException;
    void setInducible(String inducible) throws java.rmi.RemoteException;
    String getFormer_names() throws java.rmi.RemoteException;
    void setFormer_names(String former_names) throws java.rmi.RemoteException;
    UserRemote getContact() throws java.rmi.RemoteException;

    void setContact(org.tgdb.project.user.UserRemote usr) throws java.rmi.RemoteException;

    void setAvailability(java.lang.String availability) throws java.rmi.RemoteException;

    String getAvailability() throws java.rmi.RemoteException;

    String getResearchApplicationText() throws java.rmi.RemoteException;

    void setResearchApplicationText(java.lang.String researchApplicationText) throws java.rmi.RemoteException;

    String getGeneticBackground() throws java.rmi.RemoteException;

    void setGeneticBackground(java.lang.String geneticBackground) throws java.rmi.RemoteException;

    ResearchApplicationRemote getResearchApplication() throws java.rmi.RemoteException;

    void setResearchApplication(org.tgdb.model.researchapplication.ResearchApplicationRemote ra) throws RemoteException;

    SamplingUnitRemote getSamplingUnit() throws java.rmi.RemoteException, ApplicationException;
    
    void setCaller(TgDbCaller caller) throws java.rmi.RemoteException, PermissionDeniedException;    
    
    String getStatus() throws java.rmi.RemoteException;

    UserRemote getUser() throws java.rmi.RemoteException;

    void setStatus(java.lang.String status) throws java.rmi.RemoteException;

    java.sql.Date getTs() throws java.rmi.RemoteException;

    String getComm() throws java.rmi.RemoteException;

    void setComm(java.lang.String comm) throws java.rmi.RemoteException;

    String getIdentity() throws java.rmi.RemoteException;

    void setIdentity(java.lang.String identity) throws java.rmi.RemoteException;

    String getAlias() throws java.rmi.RemoteException;

    void setAlias(java.lang.String alias) throws java.rmi.RemoteException;    
    
    int getEid() throws java.rmi.RemoteException;   
    
    void setSuid(int suid) throws java.rmi.RemoteException;   

//    FileRemote getGenotypingFile() throws java.rmi.RemoteException;
//
//    FileRemote getHandlingFile() throws java.rmi.RemoteException;
//
//    void setGenotypingFile(int fileid) throws java.rmi.RemoteException;
//
//    void setHandlingFile(int fileid) throws java.rmi.RemoteException;

    Collection getGeneAffected() throws java.rmi.RemoteException;

    void addReference(org.tgdb.model.reference.ReferenceRemote ref) throws ApplicationException, java.rmi.RemoteException;

    Collection getReferences() throws java.rmi.RemoteException;

    void removeReference(ReferenceRemote reference) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void addResource(ResourceRemote res) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    int getNumberOfPhenotypes() throws ApplicationException, java.rmi.RemoteException;

    void addGene(org.tgdb.species.gene.GeneRemote gene) throws ApplicationException, java.rmi.RemoteException;

    void removeGene(org.tgdb.species.gene.GeneRemote gene) throws ApplicationException, java.rmi.RemoteException;

    void addStrain(int strain) throws ApplicationException, java.rmi.RemoteException;

    void clearStrain(int strain) throws ApplicationException, java.rmi.RemoteException;

    int getLevel() throws java.rmi.RemoteException;

    void setLevel(int level) throws java.rmi.RemoteException;

    java.lang.String getMutationTypesForModel() throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getResources() throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getAvailabilityForModel(int eid) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getStrainAlleleInfo() throws ApplicationException, java.rmi.RemoteException;

    int IMSRSubmit(int eid) throws ApplicationException, java.rmi.RemoteException;

    int getMutationDistinctionParameter() throws ApplicationException, java.rmi.RemoteException;

    void unassignGeneFromStrainAlleles(int strainId, int geneId) throws ApplicationException, java.rmi.RemoteException;

    void unassignStrainAllelesFromGene(int eid, int strainid) throws ApplicationException, java.rmi.RemoteException;

    int getDesiredLevel() throws java.rmi.RemoteException;

    void setDesiredLevel(int desired_level) throws java.rmi.RemoteException;

    java.util.Collection getGeneticBackgroundInfo() throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getExpressionModels() throws java.rmi.RemoteException;

    void addExpressionModel(org.tgdb.expression.expressionmodel.ExpressionModelRemote expression) throws ApplicationException, java.rmi.RemoteException;

    void addIntegrationCopy(org.tgdb.genome.integrationcopy.IntegrationCopyRemote ic) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getIntegrationCopies() throws java.rmi.RemoteException;
}
