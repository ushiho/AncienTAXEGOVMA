/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

/**
 *
 * @author User
 */
@Entity
public class DeclarationTva implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateDeclaration;
    @OneToOne
    private Employe utilisateur;//mn session
    private Float taxeDue = new Float(0);
    private Float taxeDeductible = new Float(0);
    private Float taxeAverser = new Float(0);
    private Float taxeAreporter = new Float(0);
    private int etat;
    @ManyToOne
    private Societe societe;
    @OneToOne(mappedBy = "declarationTva")
    private ExerciceTVA exerciceTVA;

    public DeclarationTva() {
    }

    public DeclarationTva(String id) {
        this.id = id;
    }

    public DeclarationTva(String id, Date dateDeclaration, int etat) {
        this.id = id;
        this.dateDeclaration = dateDeclaration;
        this.etat = etat;
    }

    public Date getDateDeclaration() {
        return dateDeclaration;
    }

    public void setDateDeclaration(Date dateDeclaration) {
        this.dateDeclaration = dateDeclaration;
    }

    public Employe getUtilisateur() {
        if (utilisateur == null) {
            utilisateur = new Employe();
        }
        return utilisateur;
    }

    public void setUtilisateur(Employe utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Societe getSociete() {
        if (societe == null) {
            societe = new Societe();
        }
        return societe;
    }

    public void setSociete(Societe societe) {
        this.societe = societe;
    }

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    public ExerciceTVA getExerciceTVA() {
        if (exerciceTVA == null) {
            exerciceTVA = new ExerciceTVA();
        }
        return exerciceTVA;
    }

    public void setExerciceTVA(ExerciceTVA exerciceTVA) {
        this.exerciceTVA = exerciceTVA;
    }

    public Float getTaxeDue() {
        return taxeDue;
    }

    public void setTaxeDue(Float taxeDue) {
        this.taxeDue = taxeDue;
    }

    public Float getTaxeDeductible() {
        return taxeDeductible;
    }

    public void setTaxeDeductible(Float taxeDeductible) {
        this.taxeDeductible = taxeDeductible;
    }

    public Float getTaxeAverser() {
        return taxeAverser;
    }

    public void setTaxeAverser(Float taxeAverser) {
        this.taxeAverser = taxeAverser;
    }

    public Float getTaxeAreporter() {
        return taxeAreporter;
    }

    public void setTaxeAreporter(Float taxeAreporter) {
        this.taxeAreporter = taxeAreporter;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DeclarationTva)) {
            return false;
        }
        DeclarationTva other = (DeclarationTva) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DeclarationTva{" + "id=" + id + ", dateCreation=" + dateDeclaration + ", taxeDue=" + taxeDue + ", taxeDeductible=" + taxeDeductible + ", taxeAverser=" + taxeAverser + ", taxeAreporter=" + taxeAreporter + '}';
    }

}
