/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Locale;
import bean.TaxeAnnuel;
import bean.TaxeTrim;
import controler.util.SearchUtil;
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

    public List<TaxeAnnuel> findTaxeAnnuelByCretere(Double montantMin, Double montantMax, int nombreTaxeMin, int nombreTaxetMax, String localeName, int annee) {
        String requete = "SELECT tax FROM TaxeAnnuel tax where 1=1";
        if (annee > 0) {
            requete += " AND tax.annee =" + annee;
        }
        if (!localeName.equals("")) {
            requete += " AND tax.locale.reference='" + localeName + "'";
        }
        if (nombreTaxeMin > 0) {
            requete += " AND tax.nbrTrimesterPaye >='" + nombreTaxeMin + "'";
        }
        if (nombreTaxeMin > 0) {
            requete += " AND tax.nbrTrimesterPaye <='" + nombreTaxeMin + "'";
        }
        requete += SearchUtil.addConstraintMinMax("tax", "taxeTotale", montantMin, montantMax);

        return em.createQuery(requete).getResultList();
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

    public void delete(TaxeAnnuel taxeAnnuel) {
        if (taxeAnnuel != null && taxeAnnuel.getId() != null) {
            remove(taxeAnnuel);
        }
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
