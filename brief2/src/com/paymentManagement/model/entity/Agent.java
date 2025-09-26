package com.paymentManagement.model.entity;

import java.util.List;

public class Agent extends Personne {
    Integer idAgent;
    Departement departement;
    Integer idDepartementId;
    List<Paiement> paiements;

    public Agent(String nom, String prenom, String email, String motDePasse, Integer idAgent, Departement departement, Integer idDepartementId, List<Paiement> paiements) {
        super(nom, prenom, email, motDePasse);
        this.idAgent = idAgent;
        this.departement = departement;
        this.idDepartement = idDepartementId;
        this.paiements = new ArrayList<>();
    }

    public Integer getIdAgent() {
        return idAgent;
    }

    public void setIdAgent(Integer idAgent) {
        this.idAgent = idAgent;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public Integer getIdDepartementId() {
        return idDepartement;
    }

    public void setIdDepartementId(Integer idDepartementId) {
        this.idDepartement = idDepartementId;
    }

    public List<Paiement> getPaiements() {
        return paiements;
    }

    public void setPaiements(List<Paiement> paiements) {
        this.paiements = paiements;
    }
}
