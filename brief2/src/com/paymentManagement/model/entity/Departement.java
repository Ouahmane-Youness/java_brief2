package com.paymentManagement.model.entity;

public class Departement {
    private Integer idDepartement;
    private String nom;
    private Integer responsableId;

    public Departement(Integer idDepartement, String nom, Integer responsableId) {
        this.idDepartement = idDepartement;
        this.nom = nom;
        this.responsableId = responsableId;
    }

    public Departement(String nom) {
        this.nom = nom;
    }

    public Integer getIdDepartement() {
        return idDepartement;
    }

    public void setIdDepartement(Integer idDepartement) {
        this.idDepartement = idDepartement;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(Integer responsableId) {
        this.responsableId = responsableId;
    }


}
