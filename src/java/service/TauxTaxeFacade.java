/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Categorie;
import bean.TauxTaxe;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author HP
 */
@Stateless
public class TauxTaxeFacade extends AbstractFacade<TauxTaxe> {

    @PersistenceContext(unitName = "projet_java_taxPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TauxTaxeFacade() {
        super(TauxTaxe.class);
    }

    public TauxTaxe findByCategorie(Categorie categorie) {
        String reqette = "SELECT t FROM TauxTaxe t WHERE t.categorie.id=" + categorie.getId();
        List<TauxTaxe> lst = em.createQuery(reqette).getResultList();
        if (lst != null && !lst.isEmpty()) {
            return lst.get(0);
        } else {
            return null;
        }
    }

    public void clone(TauxTaxe tauxTaxeSource, TauxTaxe tauxTaxeDestaination) {
        tauxTaxeDestaination.setId(tauxTaxeSource.getId());
        tauxTaxeDestaination.setCategorie(tauxTaxeSource.getCategorie());
        tauxTaxeDestaination.setTaux(tauxTaxeSource.getTaux());

    }

    public TauxTaxe clone(TauxTaxe tauxTaxe) {
        TauxTaxe cloned = new TauxTaxe();
        clone(tauxTaxe, cloned);
        return cloned;
    }

}
