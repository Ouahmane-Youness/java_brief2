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
        this.idDepartementId = idDepartementId;
        this.paiements = paiements;
    }
}
