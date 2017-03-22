/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Locale;
import bean.TaxeAnnuel;
import bean.TaxeTrim;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author HP
 */
@Stateless
public class TaxeAnnuelFacade extends AbstractFacade<TaxeAnnuel> {
    
    @PersistenceContext(unitName = "projet_java_taxPU")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public TaxeAnnuelFacade() {
        super(TaxeAnnuel.class);
    }
    
    public void create(Locale locale, int annee) {
        TaxeAnnuel taxeAnnuel = findByLocaleAndAnnee(locale, annee);
        System.out.println("search taxeAnnuel");
        if (taxeAnnuel == null) {
            System.out.println("searchinh nullllll");
            taxeAnnuel = new TaxeAnnuel();
            taxeAnnuel.setAnnee(annee);
            taxeAnnuel.setNbrTrimesterPaye(0);
            taxeAnnuel.setLocale(locale);
            taxeAnnuel.setTaxeTotale(0D);
            create(taxeAnnuel);
            System.out.println("tcriyat taxeannuell");
        }
    }
    
    public TaxeAnnuel findByLocaleAndAnnee(Locale locale, int annee) {
        String requette = "SELECT tax FROM TaxeAnnuel tax where 1=1";
        requette += " AND tax.annee =" + annee;
        requette += " AND tax.locale.id =" + locale.getId();
        List<TaxeAnnuel> list = em.createQuery(requette).getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public void calculer(TaxeAnnuel taxeAnnuel) {
        List<TaxeTrim> list = taxeAnnuel.getTaxeTrims();
        double t = 0;
        for (TaxeTrim ta : list) {
            t += ta.getMontantTotal();
        }
        taxeAnnuel.setTaxeTotale(t);
        edit(taxeAnnuel);
    }
    
    public void clone(TaxeAnnuel taxeAnnuelSource, TaxeAnnuel taxeAnnuelDestaination) {
        taxeAnnuelDestaination.setId(taxeAnnuelSource.getId());
        taxeAnnuelDestaination.setAnnee(taxeAnnuelSource.getAnnee());
        taxeAnnuelDestaination.setLocale(taxeAnnuelSource.getLocale());
        taxeAnnuelDestaination.setNbrTrimesterPaye(taxeAnnuelSource.getNbrTrimesterPaye());
        taxeAnnuelDestaination.setTaxeTotale(taxeAnnuelSource.getTaxeTotale());
        
    }
    
    public TaxeAnnuel clone(TaxeAnnuel taxeAnnuel) {
        TaxeAnnuel cloned = new TaxeAnnuel();
        clone(taxeAnnuel, cloned);
        return cloned;
    }
    
}
