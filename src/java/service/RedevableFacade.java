/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Redevable;
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
public class RedevableFacade extends AbstractFacade<Redevable> {

    @PersistenceContext(unitName = "projet_java_taxPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RedevableFacade() {
        super(Redevable.class);
    }

    public List<Redevable> findByCinOrRc(Redevable redevable) {
        String requette = "SELECT r FROM Redevable r WHERE 1=1";
        // requette += SearchUtil.addConstraint("r", "id", "=", redevable.getId());
        if (!redevable.getCin().equals("")) {
            requette += SearchUtil.addConstraint("r", "cin", "=", redevable.getCin());
        }
        if (!redevable.getRc().equals("")) {
            requette += SearchUtil.addConstraint("r", "rc", "=", redevable.getRc());
        }
        if (!"".equals(redevable.getPattente())) {
            requette += SearchUtil.addConstraint("r", "pattente", "=", redevable.getPattente());
        }
        if (!"".equals(redevable.getNom()) && redevable.getNom() != null) {
            requette += " AND r.nom LIKE '" + redevable.getNom() + "%'";
        }
        if (redevable.getNature() != 0) {
            requette += SearchUtil.addConstraint("r", "nature", "=", redevable.getNature());
        }
        if (!"".equals(redevable.getEmail()) && redevable.getEmail() != null) {
            requette += " AND r.email LIKE '" + redevable.getEmail() + "%'";
        }
        System.out.println(requette);
        return em.createQuery(requette).getResultList();
    }

    public List<Redevable> redevableHasRcOrCin(Redevable redevable) {
        String requette = "SELECT r FROM Redevable r WHERE 1=1";
        if (redevable.getCin() != null) {
            if (!redevable.getCin().equals("")) {
                requette += SearchUtil.addConstraint("r", "cin", "=", redevable.getCin());
            }
        }
        if (redevable.getRc() != null) {
            if (!redevable.getRc().equals("")) {
                requette += SearchUtil.addConstraint("r", "rc", "=", redevable.getRc());
            }
        }
        System.out.println(requette);
        return em.createQuery(requette).getResultList();
    }

    public List<Redevable> findByCin(String redevable) {
        return em.createQuery("SELECT r FROM Redevable r WHERE r.cin= '" + redevable + "'").getResultList();
    }

    public Redevable findOnebyCin(String redevable) {
        return (Redevable) em.createQuery("SELECT r FROM Redevable r WHERE r.cin= '" + redevable + "'").getSingleResult();
    }

    public List<Redevable> findByRc(String redevable) {
        return em.createQuery("SELECT r FROM Redevable r WHERE r.rc='" + redevable + "'").getResultList();
    }

    public Redevable findOnebyRc(String redevable) {
        return (Redevable) em.createQuery("SELECT r FROM Redevable r WHERE r.rc='" + redevable + "'").getSingleResult();
    }

    public Redevable findByCinRc(String redevable) {
        if (!redevable.equals("")) {
            List<Redevable> list = findByRc(redevable);
            if (list != null && !list.isEmpty()) {
                return list.get(0);
            } else {
                list = findByCin(redevable);
                if (list != null && !list.isEmpty()) {
                    return list.get(0);
                } else {
                    return new Redevable(redevable);
                }
            }

        }
        return null;
    }

    public Object[] compare(Redevable nouveau, Redevable auncienne, int type) {
        String newVal = "";
        String oldVal = "";
        switch (type) {
            case 1:
                newVal = "" + nouveau.getNom() + " " + nouveau.getPrenom();
                break;
            case 2:
                if (!nouveau.getNom().equals(auncienne.getNom())) {
                    oldVal += "Nom => " + auncienne.getNom();
                    newVal += "Nom => " + nouveau.getNom();
                }
                if (!nouveau.getPrenom().equals(auncienne.getPrenom())) {
                    oldVal += ", Prenom =>" + auncienne.getPrenom();
                    newVal += ", prenom =>" + nouveau.getPrenom();
                }
                if (!nouveau.getCin().equals(auncienne.getCin())) {
                    oldVal += " ,CIN => " + auncienne.getCin();
                    newVal += " ,CIN => " + nouveau.getCin();
                }
                if (!nouveau.getRc().equals(auncienne.getRc())) {
                    oldVal += ", RC =>" + auncienne.getRc();
                    newVal += ", RC =>" + nouveau.getRc();
                }
                if (!nouveau.getEmail().equals(auncienne.getEmail())) {
                    oldVal += ", Email =>" + auncienne.getEmail();
                    newVal += ", Email =>" + nouveau.getEmail();
                }

                break;
            case 3:
                oldVal = "" + auncienne.getNom() + " " + auncienne.getPrenom();
                ;
                break;
        }
        return new Object[]{newVal, oldVal};
    }

    public void clone(Redevable redevableSource, Redevable redevableDestaination) {
        redevableDestaination.setAdresse(redevableSource.getAdresse());
        redevableDestaination.setNom(redevableSource.getNom());
        redevableDestaination.setCin(redevableSource.getCin());
        redevableDestaination.setRc(redevableSource.getRc());
        redevableDestaination.setEmail(redevableSource.getEmail());
        redevableDestaination.setFax(redevableSource.getFax());
        redevableDestaination.setNature(redevableSource.getNature());
        redevableDestaination.setPattente(redevableSource.getPattente());
        redevableDestaination.setPrenom(redevableSource.getPrenom());

    }

    public Redevable clone(Redevable redevable) {
        Redevable cloned = new Redevable();
        clone(redevable, cloned);
        return cloned;
    }
}
