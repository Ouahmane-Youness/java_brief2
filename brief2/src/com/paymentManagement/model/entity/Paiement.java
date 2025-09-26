package com.paymentManagement.model.entity;

public class Paiement {
    private Integer idPaiement;
    private TypePaiement typePaiement;
    private BigDecimal montant;
    private LocalDate datePaiement;
    private String motif;
    private String evenement;
    private boolean conditionValidee;
    private Integer idAgent;

    public Paiement(Integer idPaiement, TypePaiement typePaiement, BigDecimal montant, LocalDate datePaiement, String motif,
                    String evenement, boolean conditionValidee, Integer idAgent) {
        this.idPaiement = idPaiement;
        this.typePaiement = typePaiement;
        this.montant = montant;
        this.datePaiement = datePaiement;
        this.motif = motif;
        this.evenement = evenement;
        this.conditionValidee = conditionValidee;
        this.idAgent = idAgent;
    }

    public Integer getIdPaiement() {
        return idPaiement;
    }

    public void setIdPaiement(Integer idPaiement) {
        this.idPaiement = idPaiement;
    }

    public TypePaiement getTypePaiement() {
        return typePaiement;
    }

    public void setTypePaiement(TypePaiement typePaiement) {
        this.typePaiement = typePaiement;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getEvenement() {
        return evenement;
    }

    public void setEvenement(String evenement) {
        this.evenement = evenement;
    }

    public boolean isConditionValidee() {
        return conditionValidee;
    }

    public void setConditionValidee(boolean conditionValidee) {
        this.conditionValidee = conditionValidee;
    }

    public Integer getIdAgent() {
        return idAgent;
    }

    public void setIdAgent(Integer idAgent) {
        this.idAgent = idAgent;
    }

    public boolean requiresConditionValidation() {
        return typePaiement == TypePaiement.BONUS || typePaiement == TypePaiement.INDEMNITE;
    }

    @Override
    public String toString() {
        return "Paiement{" +
                "idPaiement=" + idPaiement +
                ", typePaiement=" + typePaiement +
                ", montant=" + montant +
                ", datePaiement=" + datePaiement +
                ", motif='" + motif + '\'' +
                ", idAgent=" + idAgent +
                '}';
    }
}
