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
import javax.persistence.Temporal;

/**
 *
 * @author ayoub
 */
@Entity
public class TaxeTrim implements Serializable {

   
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 
    private Double montantTotal;
    private Double montantRetard;
    private Double premierMoisRetard;
    private Double autresMoisRetard;
    private Double montant;
    private int nbrMoisRetard;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date datePaiement=new Date();
    private int numeroTrim;
    private int nombreNuit;
    private int nombreClients;
    @ManyToOne
    private Locale locale;  
    @ManyToOne
    private Redevable redevable;
    @ManyToOne
    private TaxeAnnuel taxeAnnuel;
    @ManyToOne
    private User user;
    

    public Double getMontantRetard() {
        return montantRetard;
    }

    public void setMontantRetard(Double montantRetard) {
        this.montantRetard = montantRetard;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public Date getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(Date datePaiement) {
        this.datePaiement = datePaiement;
    }

    public int getNombreNuit() {
        return nombreNuit;
    }

    public void setNombreNuit(int nombreNuit) {
        this.nombreNuit = nombreNuit;
    }

    public int getNombreClients() {
        return nombreClients;
    }

    public void setNombreClients(int nombreClients) {
        this.nombreClients = nombreClients;
    }

    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    
    
    public Double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(Double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public Double getPremierMoisRetard() {
        return premierMoisRetard;
    }

    public void setPremierMoisRetard(Double premierMoisRetard) {
        this.premierMoisRetard = premierMoisRetard;
    }

    public Double getAutresMoisRetard() {
        return autresMoisRetard;
    }

    public void setAutresMoisRetard(Double autresMoisRetard) {
        this.autresMoisRetard = autresMoisRetard;
    }

    public int getNbrMoisRetard() {
        return nbrMoisRetard;
    }

    public void setNbrMoisRetard(int nbrMoisRetard) {
        this.nbrMoisRetard = nbrMoisRetard;
    }

    public int getNumeroTrim() {
        return numeroTrim;
    }

    public void setNumeroTrim(int numeroTrim) {
        this.numeroTrim = numeroTrim;
    }
    

    public TaxeTrim(Double montantTotal, Double montantRetard, Double montant, int nombreNuit, int nombreClients) {
        this.montantTotal = montantTotal;
        this.montantRetard = montantRetard;
        this.montant = montant;
        this.nombreNuit = nombreNuit;
        this.nombreClients = nombreClients;
    }
    
    

    public TaxeTrim() {
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Redevable getRedevable() {
        return redevable;
    }

    public void setRedevable(Redevable redevable) {
        this.redevable = redevable;
    }

    public TaxeAnnuel getTaxeAnnuel() {
        return taxeAnnuel;
    }

    public void setTaxeAnnuel(TaxeAnnuel taxeAnnuel) {
        this.taxeAnnuel = taxeAnnuel;
    }
    
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(object instanceof TaxeTrim)) {
            return false;
        }
        TaxeTrim other = (TaxeTrim) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bean.TaxeTrim[ id=" + id + " ]";
    }
    
}
