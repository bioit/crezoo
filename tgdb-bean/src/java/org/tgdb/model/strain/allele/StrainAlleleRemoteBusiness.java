package org.tgdb.model.strain.allele;

import org.tgdb.exceptions.ApplicationException;
import org.tgdb.model.strain.mutationtype.MutationTypeRemote;


public interface StrainAlleleRemoteBusiness {
    boolean isAssigned(int eid, int mutation_type, String attribute) throws ApplicationException, java.rmi.RemoteException;

    int getId() throws java.rmi.RemoteException;

    java.lang.String getSymbol() throws java.rmi.RemoteException;

    void setSymbol(String symbol) throws java.rmi.RemoteException;

    java.lang.String getName() throws java.rmi.RemoteException;

    void setName(String name) throws java.rmi.RemoteException;

    String getMgiId() throws java.rmi.RemoteException;

    void setMgiId(String imsrid) throws java.rmi.RemoteException;

    java.util.Collection getMutationTypes(int strain_id) throws java.rmi.RemoteException;

    void addGene(int gid) throws ApplicationException, java.rmi.RemoteException;

    void deleteGene(int gid) throws ApplicationException, java.rmi.RemoteException;

    void addMutationTypeAndAttribute(int strain, int mutation_type, String attribute) throws ApplicationException, java.rmi.RemoteException;

    void addMutationType(MutationTypeRemote mutationType) throws ApplicationException, java.rmi.RemoteException;

    void removeMutationType(int model, int mutation_type) throws ApplicationException, java.rmi.RemoteException;

    void unassign(int model) throws ApplicationException, java.rmi.RemoteException;

//    void setGene(GeneRemote gene) throws java.rmi.RemoteException;

    java.lang.String getAttributes(int model) throws java.rmi.RemoteException;
//
    void setAttributes(int model, String attributes) throws java.rmi.RemoteException;

//    void setGeneToNULL(int strainallele) throws ApplicationException, java.rmi.RemoteException;

    void setMade_by(String made_by) throws ApplicationException, java.rmi.RemoteException;

    String getMade_by() throws ApplicationException, java.rmi.RemoteException;

    void setOrigin_strain(String origin_strain) throws ApplicationException, java.rmi.RemoteException;

    String getOrigin_strain() throws ApplicationException, java.rmi.RemoteException;

    void setMgi_url(String mgi_url) throws ApplicationException, java.rmi.RemoteException;

    String getMgi_url() throws ApplicationException, java.rmi.RemoteException;
    
    String getTransgeneExpression() throws ApplicationException, java.rmi.RemoteException;
    
    String getTransgeneMolecular() throws ApplicationException, java.rmi.RemoteException;
    
    String getTransgeneChromosome() throws ApplicationException, java.rmi.RemoteException;
}
