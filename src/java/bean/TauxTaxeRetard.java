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
import javax.persistence.OneToOne;

/**
 *
 * @author ayoub
 */
@Entity
public class TauxTaxeRetard implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Categorie categorie;
    private Double tauxPremierRetard;
    private Double tauxAutreRetard;

    public Double getTauxAutreRetard() {
        return tauxAutreRetard;
    }

    public void setTauxAutreRetard(Double tauxAutreRetard) {
        this.tauxAutreRetard = tauxAutreRetard;
    }
    

    public TauxTaxeRetard() {
    }
    

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public Double getTauxPremierRetard() {
        return tauxPremierRetard;
    }

    public void setTauxPremierRetard(Double tauxPremierRetard) {
        this.tauxPremierRetard = tauxPremierRetard;
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
        if (!(object instanceof TauxTaxeRetard)) {
            return false;
        }
        TauxTaxeRetard other = (TauxTaxeRetard) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Premier Mois :"+tauxPremierRetard+" ,Autre Mois :"+tauxAutreRetard;
    }
    
}
