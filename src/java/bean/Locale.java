/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author ayoub
 */
@Entity
public class Locale implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Rue rue;
    private String complementAdresse;
    private String description;
    @ManyToOne
    private Redevable proprietaire;
    @ManyToOne
    private Redevable gerant;
    private String reference;
    private String activite;
    private String nom;
    private int dernierTrimestrePaiement;
    private int dernierAnneePaiement;
    @ManyToOne
    private Categorie categorie;

    public int getDernierTrimestrePaiement() {
        return dernierTrimestrePaiement;
    }

    public void setDernierTrimestrePaiement(int dernierTrimestrePaiement) {
        this.dernierTrimestrePaiement = dernierTrimestrePaiement;
    }

    public int getDernierAnneePaiement() {
        return dernierAnneePaiement;
    }

    public void setDernierAnneePaiement(int dernierAnneePaiement) {
        this.dernierAnneePaiement = dernierAnneePaiement;
    }

    public String getActivite() {
        return activite;
    }

    public void setActivite(String activite) {
        this.activite = activite;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public Locale(String complementAdresse, String description, String reference, String activite) {
        this.complementAdresse = complementAdresse;
        this.description = description;
        this.reference = reference;
        this.activite = activite;
    }

    public Locale() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Rue getRue() {
        return rue;
    }

    public void setRue(Rue rue) {
        this.rue = rue;
    }

    public String getComplementAdresse() {
        return complementAdresse;
    }

    public void setComplementAdresse(String complementAdresse) {
        this.complementAdresse = complementAdresse;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Redevable getProprietaire() {
        return proprietaire;
    }

    public void setProprietaire(Redevable proprietaire) {
        this.proprietaire = proprietaire;
    }

    public Redevable getGerant() {
        return gerant;
    }

    public void setGerant(Redevable gerant) {
        this.gerant = gerant;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Locale)) {
            return false;
        }
        Locale other = (Locale) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return reference + " " + complementAdresse;
    }

}
