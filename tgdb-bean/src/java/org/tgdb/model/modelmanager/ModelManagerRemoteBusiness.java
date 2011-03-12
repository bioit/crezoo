package org.tgdb.model.modelmanager;

import java.util.Collection;
import org.tgdb.frame.PageManager;
import org.tgdb.frame.io.FileDataObject;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;

public interface ModelManagerRemoteBusiness {
    Collection getTermsByOntology(String ontology_name) throws ApplicationException, java.rmi.RemoteException;

    Collection getExperimentalModels(int suid, TgDbCaller caller, PageManager pageManager) throws ApplicationException, java.rmi.RemoteException;

    ExpModelDTO getExperimentalModel(int eid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;
    
    ExpModelDTO getExperimentalModel(int eid, TgDbCaller caller, String superscript) throws ApplicationException, java.rmi.RemoteException;

    int getNumberOfExperimentalModels(int suid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;
    
    Collection getResearchApplications(TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;
    
    Collection getAllResearchApplications(org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

//    void addHandlingFile(int eid, FileDataObject fileData, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

//    void addGenotypingFile(int eid, FileDataObject fileData, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

//    void removeGenotypingFile(int eid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

//    void removeHandlingFile(int eid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    Collection getGenesByModel(int eid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    Collection getGenesByModelAndDistinguish(int eid, String distinguish, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    Collection getGenesByDistinguish(String distinguish, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void updateGene(int gaid, String name, String comm, String mgiid, String genesymbol, String geneexpress, String idgene, String idensembl, int cid, String molecular_note, String molecular_note_url, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void removeGene(int gaid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    GeneDTO getGene(int gaid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;
    
    Collection getReferences(int eid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    public Collection getReferencesByModelAndPrimary(int eid, boolean primary, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void addLinkReference(int eid, java.lang.String name, String pubmed, boolean primary, java.lang.String comm, java.lang.String url, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void addFileReference(int eid, java.lang.String name, java.lang.String comm, FileDataObject fileData, String pubmed, boolean primary, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    ReferenceDTO getReference(int refid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void removeReference(int eid, int refid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void updateReference(int refid, String name, String comm, String pubmed, boolean primary, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void createModel(int suid, java.lang.String alias, java.lang.String geneticBackground, java.lang.String availability, int type, java.lang.String researchApplications, int contact, TgDbCaller caller, String comm, String desired_level, String donating_investigator, String inducible, String former_names) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void updateModel(int suid, int eid, java.lang.String alias, java.lang.String geneticBackground, java.lang.String availability, int type, java.lang.String researchApplications, int contact, TgDbCaller caller, String comm, String level, String desired_level, String donating_investigator, String inducible, String former_names) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void removeModel(int eid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void addFileResource(int eid, java.lang.String name, java.lang.String comm, FileDataObject fileData, int catid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void addLinkResource(int eid, java.lang.String name, java.lang.String comm, java.lang.String url, int catid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    Collection searchByGene(java.lang.String geneName, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    Collection searchByResearchApplication(java.lang.String name, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    Collection searchByProject(java.lang.String name, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    Collection searchByKeyword(java.lang.String keyword, org.tgdb.TgDbCaller caller) throws java.rmi.RemoteException, ApplicationException;

    TgDbCaller getSearchCaller() throws ApplicationException, java.rmi.RemoteException;

    Collection getGenesByProject(int pid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;
    
    Collection getUnassignedGenes(int eid, int pid, String distinguish, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    Collection getPromotersForModel(int eid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    int createPromoter(String name, String symbol, int cid, String mgiid, String driver_note, String common_name, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void updatePromoter(int gid, String name, String symbol, String mgiid, int cid, String driver_note, String common_name, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    Collection getPromoterLinks(int pid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void createPromoterLink(int pid, String repository, String externalid, String strainurl, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void deletePromoterLink(int pid, int promoter_link_id, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    int createExpressedGene(String name, String symbol, int cid, String mgiid, String comm, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void updateExpressedGene(int gid, String name, String symbol, String mgiid, int cid, String comm, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    int createTransgene(java.lang.String name, java.lang.String comm, java.lang.String mgiid, java.lang.String genesymbol, java.lang.String geneexpress, java.lang.String idgene, java.lang.String idensembl, int cid, String molecular_note, String molecular_note_url, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void addGeneToModel(int gaid, int eid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void removeGeneFromModel(int gaid, int eid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    Collection getModelsByGene(int gid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    ResearchAppDTO getResearchApplication(int raid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    int createResearchApplication(java.lang.String name, java.lang.String comment, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void updateResearchApplication(int raid, java.lang.String name, java.lang.String comment, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void removeResearchApplication(int raid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    Collection getExperimentalModelsByForm(org.tgdb.form.FormDataManager fdm, org.tgdb.TgDbCaller caller, org.tgdb.frame.PageManager pageManager) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getStrains(TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getStrainsConnectedToModels(TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getStrainsConnectedToModel(int eid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    org.tgdb.model.modelmanager.StrainDTO getStrain(int strainid, TgDbCaller caller, String superscript) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getStrainTypes(TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getStrainStates(TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    Collection getGeneticBackground(int eid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void createMutationType(String name, String abbreviation, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void updateMutationType(int mtid, String name, String abbreviation, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void deleteMutationType(int mtid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    MutationTypeDTO getMutationType(int mtid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getMutationTypes(int pid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;
    
    java.util.Collection getUnassignedMutationTypes(int strainalleleid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

//    org.tgdb.model.modelmanager.StrainDTO getStrainFromModel(int eid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;
    Collection getStrainsByModel(int eid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void assignStrainToModel(int eid, int strain, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void unassignStrainFromModel(int eid, int strain, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    Collection getUnassignedStrains(int eid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;
    
    Collection getGeneticBackgroundsByProject(int pid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void setGeneticBackgroundForModel(int eid, int dna_origin, int targeted_back, int host_back, int backcrossing_strain, String backcrosses) throws ApplicationException, java.rmi.RemoteException;

    int createGeneBackValue(java.lang.String backname, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void updateGeneBackValue(int bid, String backname, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.lang.String getGeneBackValueName(int bid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    org.tgdb.model.modelmanager.GeneticBackgroundDTO getGeneticBackgroundDTO(int eid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getBackcrossesCollection() throws java.rmi.RemoteException;

    void updateGeneticBackgroundForModel(int eid, int dna_origin, int targeted_back, int host_back, int backcrossing_strain, String backcrosses, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getStrainStatesForStrain(int strainId, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getStrainTypesForStrain(int strainId, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getAvailableStrainStatesForStrain(int strainId, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getAvailableStrainTypesForStrain(int strainId, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void addStrainAndTypeToStrain(int strainid, int typeid, int stateid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void removeTypeFromStrain(int strainid, int typeid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void removeStateFromStrain(int strainid, int stateid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void createStrain(String designation, TgDbCaller caller) throws java.rmi.RemoteException, ApplicationException;

    void updateStrain(int id, String designation, TgDbCaller caller) throws java.rmi.RemoteException, ApplicationException;

    void deleteStrain(int strainid, TgDbCaller caller) throws java.rmi.RemoteException, ApplicationException;

    void createStrainType(String name, String abbreviation, TgDbCaller caller) throws java.rmi.RemoteException, ApplicationException;

    void updateStrainType(int stid, String name, String abbreviation, TgDbCaller caller) throws java.rmi.RemoteException, ApplicationException;

    void deleteStrainType(int stid, TgDbCaller caller) throws java.rmi.RemoteException, ApplicationException;

    StrainTypeDTO getStrainType(int stid, TgDbCaller caller) throws java.rmi.RemoteException, ApplicationException;

    void createStrainState(String name, String abbreviation, TgDbCaller caller) throws java.rmi.RemoteException, ApplicationException;

    void updateStrainState(int ssid, String name, String abbreviation, TgDbCaller caller) throws java.rmi.RemoteException, ApplicationException;

    void deleteStrainState(int ssid, TgDbCaller caller) throws java.rmi.RemoteException, ApplicationException;

    StrainStateDTO getStrainState(int ssid, TgDbCaller caller) throws java.rmi.RemoteException, ApplicationException;

    Collection getStrainAlleles(TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getStrainAllelesFromStrain(int eid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    //------------DUDE_TEST_STRAIN_ALLELE
    int createStrainAllele(String symbol, String name, String mgi_id, String mgi_url, String made_by, String origin_strain, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void deleteStrainAllele(int strain_allele, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;
    
    void removeStrainAllele(int model, int strain_allele, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    org.tgdb.model.modelmanager.StrainAlleleDTO getStrainAllele(int model, int strain_allele, boolean simple, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getMutationTypesFromStrainAllele(int strainalleleid, int eid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void addGeneToStrainAllele(int aid, int gid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void removeGeneFromStrainAllele(int aid, int gid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void addMutationTypeAndAttributeToStrainAllele(int eid, int strain_allele, int mutation_type, String attribute, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void addMutationTypeToStrainAllele(int id, int strainalleleid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void removeMutationTypeFromStrainAllele(int model, int mutation_type, int strain_allele, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void updateStrainAllele(int eid, int strain_allele, String symbol, String name, String attributes, String mgi_id, String mgi_url, String made_by, String origin_strain, boolean simple, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    //interfaces for methods relevant to availability
    //<editor-fold defaultstate="collapsed">
    java.util.Collection getRepositoriesByProject(int pid) throws ApplicationException, java.rmi.RemoteException;
    
    java.util.Collection getAvailableGeneticBackgroundsByProject(int pid) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getAvailabilityForModel(int eid) throws ApplicationException, java.rmi.RemoteException;

    void addAvailabilityToModel(int eid, int rid, int aid, int stateid, int typeid, int strainid) throws ApplicationException, java.rmi.RemoteException;
    
    void removeAvailabilityFromModel(int eid, int rid, int aid, int stateid, int typeid, int strainid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;
    
    org.tgdb.model.modelmanager.RepositoriesDTO returnRepositoryById(int rid) throws ApplicationException, java.rmi.RemoteException;
    
    void updateRepositoryName(int rid, String reponame, int hasdb, String mouseurl, String repourl) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getLevelsForModel() throws java.rmi.RemoteException;
    
    int addRepository(String reponame, int hasdb, String mouseurl, String repourl, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;
    //</editor-fold>

    java.util.Collection getMutationTypeAttributes() throws java.rmi.RemoteException;

    org.tgdb.model.modelmanager.AvailableGeneticBackgroundDTO returnAvailableGeneticBackgroundById(int aid) throws ApplicationException, java.rmi.RemoteException;

    void updateAvailableGeneticBackgroundName(int aid, String avgenbackname) throws ApplicationException, java.rmi.RemoteException;

    int addAvailableGeneticBackground(String avgenbackname, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void removeRepository(int rid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void removeAvailableGeneticBackground(int aid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getParticipants() throws ApplicationException, java.rmi.RemoteException;

    void removeFileResource(int refid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getChromosomesForSpecies(int sid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    org.tgdb.adminmanager.SpeciesDTO getSpecies(int sid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getGenesByProjectForNavTag(int pid, TgDbCaller caller, PageManager pageManager) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getExperimentalModelsByFormNoDelta(org.tgdb.form.FormDataManager fdm, TgDbCaller caller, PageManager pageManager) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getOrderByTypes() throws java.rmi.RemoteException;

    java.util.Collection getResourceTreeCollection(int eid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getExperimentalModelsForIMSR(int suid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getExperimentalModelsToIMSRTable(Collection models, int suid) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getGenesForTransgenicMice(int pid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getUnassignedGenesForTransgenic(int eid, int strainid, int pid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    int getGeneAssignmentForTransgenicModel(int eid, int gaid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

//    void removeGeneFromStrainAlleles(int gaid, int eid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

//    void clearGeneFromStrainAllele(int strainallele) throws ApplicationException, java.rmi.RemoteException;

//    void removeStrainAllelesFromGene(int eid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getExperimentalModelsByFormForDissUpdate(org.tgdb.form.FormDataManager fdm, TgDbCaller caller, PageManager pageManager) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getExperimentalModelsByFormForDissUpdateNoDelta(org.tgdb.form.FormDataManager fdm, TgDbCaller caller, PageManager pageManager) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getParticipantNames() throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getOrderByTypes2() throws java.rmi.RemoteException;

    java.util.Collection getExperimentalModelsForBackcrossingListGeneration(TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    int createExpressionModel(java.lang.String exanatomy, java.lang.String excomm) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getExpressionModelsByModel(int eid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    int addFile(org.tgdb.frame.io.FileDataObject exfile, java.lang.String exfilecomm, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void addFileToExpressionModel(int exid, int fileid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void addReferenceToExpressionModel(int exid, int refid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void deleteReferenceFromExpressionModel(int exid, int refid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void addOntologyToExpressionModel(int exid, String oid, String namespace, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void deleteOntologyFromExpressionModel(int exid, String oid, String namespace, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    Collection getOntologyTerms(int exid, String namespace, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void addExpressionModelToModel(int eid, int exid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void removeExpressionModel(int exid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    org.tgdb.model.modelmanager.ExpressionModelDTO getExpressionModel(int exid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getExpressionModelFiles(int exid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void updateExpressionModel(int exid, java.lang.String exanatomy, java.lang.String excomm, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void removeFile(int fileid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    int createIntegrationCopy(java.lang.String isite, java.lang.String cnumber) throws ApplicationException, java.rmi.RemoteException;

    void addIntegrationCopyToModel(int eid, int iscmid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getIntegrationCopiesByModel(int eid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void removeIntegrationCopy(int iscmid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void updateRecombinationEfficiencyModel(int eid, java.lang.String researchApplications, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void updateAuthorsCommentModel(int eid, java.lang.String comm, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    org.tgdb.model.modelmanager.IntegrationCopyDTO getIntegrationCopy(int iscmid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void updateIntegrationCopy(int iscmid, java.lang.String isite, java.lang.String cnumber, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    boolean loadTgs(int repo, byte[] data, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    boolean loadMiceFromExcel(byte[] data, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getStrainsFromMgiid(String strainid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getStrainLinks(int strainid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void createStrainLink(int strainid, String repository, String externalid, String strainurl, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void deleteStrainLink(int strainid, int strain_link_id, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getHasdbValues() throws java.rmi.RemoteException;

    Collection getInducibleValues() throws java.rmi.RemoteException;

    java.util.Collection getRepositoriesByDB() throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getMutationTypesByAbbreviation(String abbreviation, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getStrainTypesByAbbreviation(String abbreviation, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getStrainStatesByAbbreviation(String abbreviation, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    int createModelAutomatic(String strain_id, String strain_designation, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getStrainAllelesByMgiid(String mgiid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getStrainAllelesByName(String name, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getStrainAllelesBySymbol(String symbol, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getUnassignedAlleles(int model, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    int createStrainAlleleAdvanced(String symbol, String name, String mgiid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getChromosomesByAbbreviation(String abbreviation, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getGeneBySymbol(String symbol, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getGeneByMgiid(String mgiid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getGenesByAllele(int aid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getGenesUnassignedToAllele(int aid, String distinguish, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getGeneByNameCaseSensitive(String name, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    int getExperimentalModelsByForm(org.tgdb.form.FormDataManager fdm, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getExperimentalModelsByPGM(PageManager pageManager) throws ApplicationException, java.rmi.RemoteException;

    int getGenes(int pid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.util.Collection getGenesByPGM(PageManager pageManager) throws ApplicationException, java.rmi.RemoteException;
    
    Collection getOntologyTerms(String namespace) throws ApplicationException, java.rmi.RemoteException;
    
    Collection getInducibility() throws ApplicationException, java.rmi.RemoteException;
    
    //web services methods
    //<editor-fold defaultstate="collapsed">

    java.lang.String getProjectName() throws java.rmi.RemoteException;

    java.lang.String[] getTgDbMice() throws ApplicationException, java.rmi.RemoteException;

    org.tgdb.dtos.TgDbModelDTO[] getTgDbMiceDTO() throws ApplicationException, java.rmi.RemoteException;

    org.tgdb.dtos.TgDbModelDTO[] getTgDbMiceDTOByKey(String key) throws ApplicationException, java.rmi.RemoteException;

    org.tgdb.dtos.TgDbGeneDTO[] getTgDbGenesByModel(int eid) throws ApplicationException, java.rmi.RemoteException;

    org.tgdb.dtos.TgDbAvailabilityDTO[] getTgDbAvailabilityByModel(int eid) throws ApplicationException, java.rmi.RemoteException;

    org.tgdb.dtos.TgDbBackgroundDTO[] getTgDbBackgroundByModel(int eid) throws ApplicationException, java.rmi.RemoteException;
    
    //</editor-fold>

}
