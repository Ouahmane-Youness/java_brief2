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


    public Agent() {
        super();
        this.paiements = new ArrayList<>();
    }

    public void setTypeAgent(TypeAgent typeAgent) {
        this.typeAgent = typeAgent;
    }

    public Integer getIdDepartement() {
        return idDepartement;
    }

    public void setIdDepartement(Integer idDepartement) {
        this.idDepartement = idDepartement;
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


    public void addPaiement(Paiement paiement)
    {
        if(paiements == null)
        {
            paiements = new ArrayList<>();
        }

        paiements.add(paiement);
        paiement.setIdAgent(this.idAgent);
    }
    public int getNombrePaiements() {
        return paiements != null ? paiements.size() : 0;
    }

    public void removePaiement(Paiement paiement)
    {
        this.paiements.remove(paiement);
    }

    @Override
    public String toString() {
        return "Agent{" +
                "idAgent=" + idAgent +
                ", nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", typeAgent=" + typeAgent +
                ", idDepartement=" + idDepartement +
                ", nombrePaiements=" + getNombrePaiements() +
                '}';
    }

    public TypeAgent getTypeAgent() {
        return typeAgent;
    }
}
