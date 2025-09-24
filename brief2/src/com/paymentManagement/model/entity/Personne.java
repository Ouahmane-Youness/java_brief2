package com.paymentManagement.model.entity;

public abstract class Personne {

    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    protected Personne() {
        // Default constructor intentionally empty
        // Subclasses will initialize fields through setters or other constructors
    }

    protected Personne(String nom, String prenom, String email, String motDePasse) {
        setNom(nom);
        setPrenom(prenom);
        setEmail(email);
        setMotDePasse(motDePasse);
    }

    public String getNom() {
        return nom;
    }


    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {

        this.prenom = prenom;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getNomComplet() {
        if (prenom != null && nom != null) {
            return prenom + " " + nom;

        }
        else return "personne inconnu";
    }


    @Override
    public String toString() {
        return String.format("Personne{nom='%s', prenom='%s', email='%s'}",
                nom != null ? nom : "null",
                prenom != null ? prenom : "null",
                email != null ? email : "null");
    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//
//        if (obj == null) {
//            return false;
//        }
//
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//
//        Personne other = (Personne) obj;
//
//        if (email == null) {
//            return other.email == null;
//        } else {
//            return email.equals(other.email);
//        }
//    }
//
//    @Override
//    public int hashCode() {
//        return email != null ? email.hashCode() : 0;
//    }
//}