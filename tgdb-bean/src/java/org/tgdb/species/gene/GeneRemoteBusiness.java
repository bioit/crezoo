package org.tgdb.species.gene;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.project.project.ProjectRemote;
import org.tgdb.project.user.UserRemote;
import org.tgdb.species.chromosome.ChromosomeRemote;
import java.sql.Date;
import java.util.Collection;


public interface GeneRemoteBusiness {
    boolean isAssigned(int eid, String distinguish) throws java.rmi.RemoteException;
    
    int getGaid() throws java.rmi.RemoteException;

    String getName() throws java.rmi.RemoteException;

    void setName(java.lang.String name) throws java.rmi.RemoteException;

    String getDriver_note() throws java.rmi.RemoteException;

    void setDriver_note(java.lang.String driver_note) throws java.rmi.RemoteException;

    String getMolecular_note() throws java.rmi.RemoteException;

    void setMolecular_note(String molecular_note) throws java.rmi.RemoteException;

    String getMolecular_note_link() throws java.rmi.RemoteException;

    void setMolecular_note_link(String molecular_note_link) throws java.rmi.RemoteException;

    String getCommon_name() throws java.rmi.RemoteException;

    void setCommon_name(String common_name) throws java.rmi.RemoteException;

    String getDistinguish() throws java.rmi.RemoteException;

    void setDistinguish(String distinguish) throws java.rmi.RemoteException;

    String getComm() throws java.rmi.RemoteException;

    void setComm(java.lang.String comm) throws java.rmi.RemoteException;

    Date getTs() throws java.rmi.RemoteException;

    UserRemote getUser() throws java.rmi.RemoteException;

    void setCaller(TgDbCaller caller) throws java.rmi.RemoteException;

    Collection getModels() throws java.rmi.RemoteException;
    
    int getModelsNum() throws java.rmi.RemoteException;

    ProjectRemote getProject() throws ApplicationException, java.rmi.RemoteException;
    
    java.lang.String getMgiid() throws java.rmi.RemoteException;

    void setMgiid(String mgiid) throws java.rmi.RemoteException;

    java.lang.String getGenesymbol() throws java.rmi.RemoteException;

    void setGenesymbol(String genesymbol) throws java.rmi.RemoteException;

    java.lang.String getGeneexpress() throws java.rmi.RemoteException;

    void setGeneexpress(String geneexpress) throws java.rmi.RemoteException;

    java.lang.String getIdgene() throws java.rmi.RemoteException;

    void setIdgene(String idgene) throws java.rmi.RemoteException;

    java.lang.String getIdensembl() throws java.rmi.RemoteException;

    void setIdensembl(String idensembl) throws java.rmi.RemoteException;

    org.tgdb.species.chromosome.ChromosomeRemote getChromosome() throws java.rmi.RemoteException;

    void setChromosome(ChromosomeRemote chromosome) throws java.rmi.RemoteException;
    
}
