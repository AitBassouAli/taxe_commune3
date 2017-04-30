/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.AnnexeAdministratif;
import bean.Categorie;
import bean.Locale;
import bean.PositionLocale;
import bean.Quartier;
import bean.Redevable;
import bean.Rue;
import bean.Secteur;
import controler.util.SearchUtil;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author HP
 */
@Stateless
public class LocaleFacade extends AbstractFacade<Locale> {

    @EJB
    private QuartierFacade quartierFacade;
    @EJB
    private AnnexeAdministratifFacade annexeAdministratifFacade;
    @EJB
    private SecteurFacade secteurFacade;
    @EJB
    private PositionLocaleFacade positionLocaleFacade;

    @PersistenceContext(unitName = "projet_java_taxPU")
    private EntityManager em;

    public Locale attachLocaleToPosition(Locale locale, double lat, double lng) {
        PositionLocale positionLocale = new PositionLocale();
        positionLocale.setLat(lat);
        positionLocale.setLng(lng);
        positionLocaleFacade.create(positionLocale);
        long idPos = positionLocaleFacade.generateId("PositionLocale", "id");
        PositionLocale loaded = positionLocaleFacade.find(idPos);
        locale.setPositionLocale(loaded);
        return locale;
    }

    public List<Locale> findByRue(Rue rue) {
        return em.createQuery("SELECT l FROM Locale l where l.rue.id = " + rue.getId()).getResultList();
    }

    public List<String> findAllActivities() {
        return em.createQuery("SELECT distinct(l.activite) FROM Locale l").getResultList();
    }

    public List<Locale> findByRedevableCin(String redevable) {
        return em.createQuery("SELECT l FROM Locale l WHERE l.proprietaire.cin='" + redevable + "' OR l.gerant.cin='" + redevable + "'").getResultList();
    }

    public List<Locale> findByRedevableRc(String redevable) {
        if (!redevable.equals("")) {
            String rqt = "SELECT l FROM Locale l WHERE l.proprietaire.rc= '" + redevable + "' And l.gerant.rc='" + redevable + "'";
            System.out.println(rqt);
            return em.createQuery(rqt).getResultList();
        } else {
            return null;
        }
    }
    public List<Locale> findByRedevable(Redevable redevable) {
        if (redevable!=null) {
            String rqt = "SELECT l FROM Locale l WHERE l.proprietaire.id= '" + redevable.getId() + "' OR l.gerant.id='" + redevable.getId() + "'";
            System.out.println(rqt);
            return em.createQuery(rqt).getResultList();
        } else {
            return null;
        }
    }

    public List<Locale> findByGerantOrProprietaire(Categorie categorie, Redevable proprietaire, String activite, String reference, Redevable gerant) {
        String requette = "SELECT l FROM Locale l WHERE 1=1";

        if (proprietaire != null) {
            if (!proprietaire.getCin().equals("")) {
                requette += SearchUtil.addConstraint("l", "proprietaire.cin", "=", proprietaire.getCin());
            } else {
                requette += SearchUtil.addConstraint("l", "proprietaire.id", "=", proprietaire.getId());
            }
        }
        if (gerant != null) {
            if (!gerant.getCin().equals("")) {
                requette += SearchUtil.addConstraint("l", "gerant.cin", "=", gerant.getCin());
            } else {
                requette += SearchUtil.addConstraint("l", "gerant.id", "=", gerant.getId());
            }
        }
        if (categorie != null) {
            requette += SearchUtil.addConstraint("l", "categorie.id", "=", categorie.getId());
        }
        if (!activite.equals("")) {
            requette += SearchUtil.addConstraint("l", "activite", "=", activite);
        }
        if (!reference.equals("")) {
            requette += SearchUtil.addConstraint("l", "reference", "=", reference);
        }
        System.out.println(requette);
        return em.createQuery(requette).getResultList();
    }

    public List<Locale> findByGerantOrProprietaire2(Categorie categorie, Redevable proprietaire, String reference, Redevable gerant) {
        String requette = "SELECT l FROM Locale l WHERE 1=1";

        if (proprietaire != null) {
            if (!proprietaire.getCin().equals("")) {
                requette += SearchUtil.addConstraint("l", "proprietaire.cin", "=", proprietaire.getCin());
            } else {
                requette += SearchUtil.addConstraint("l", "proprietaire.id", "=", proprietaire.getId());
            }
        }
        if (gerant != null) {
            if (!gerant.getCin().equals("")) {
                requette += SearchUtil.addConstraint("l", "gerant.cin", "=", gerant.getCin());
            } else {
                requette += SearchUtil.addConstraint("l", "gerant.id", "=", gerant.getId());
            }
        }
        if (categorie != null) {
            requette += SearchUtil.addConstraint("l", "categorie.id", "=", categorie.getId());
        }

        if (!reference.equals("")) {
            requette += SearchUtil.addConstraint("l", "reference", "=", reference);
        }
        System.out.println(requette);
        return em.createQuery(requette).getResultList();
    }

    public List<Redevable> findByReference(Locale locale) {
        return em.createQuery("SELECT l FROM Locale l WHERE l.reference='" + locale.getReference() + "'").getResultList();
    }

    public List<Locale> findByCinOrRc(Redevable redevable, String activite) {
        String requette = "SELECT l FROM Locale l WHERE 1=1";
        requette += SearchUtil.addConstraint("l", "rdevable.id", "=", redevable.getId());
        if (!"".equals(redevable.getCin())) {
            requette += SearchUtil.addConstraint("l", "activite", "=", activite);
        }
        if (!"".equals(redevable.getRc())) {
            requette += SearchUtil.addConstraint("l", "rc", "=", redevable.getRc());
        }
        return em.createQuery(requette).getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void generateReference(Rue rue, Locale locale) {
        Quartier quartier = quartierFacade.find(rue.getQuartier().getId());
        AnnexeAdministratif annexeAdministratif = annexeAdministratifFacade.find(quartier.getAnnexeAdministratif().getId());
        Secteur secteur = secteurFacade.find(annexeAdministratif.getSecteur().getId());
        locale.setReference(rue.getNumAbreviation() + "-" + quartier.getNumAbreviation() + "-" + annexeAdministratif.getAbreviation() + "-" + secteur.getAbreviation());
    }

    public LocaleFacade() {
        super(Locale.class);
    }

    public void clone(Locale localeSource, Locale localeDestaination) {
        localeDestaination.setId(localeSource.getId());
        localeDestaination.setActivite(localeSource.getActivite());
        localeDestaination.setNom(localeSource.getNom());
        localeDestaination.setCategorie(localeSource.getCategorie());
        localeDestaination.setComplementAdresse(localeSource.getComplementAdresse());
        localeDestaination.setDescription(localeSource.getDescription());
        localeDestaination.setGerant(localeSource.getGerant());
        localeDestaination.setProprietaire(localeSource.getProprietaire());
        localeDestaination.setReference(localeSource.getReference());
        localeDestaination.setRue(localeSource.getRue());

    }

    public Locale clone(Locale locale) {
        Locale cloned = new Locale();
        clone(locale, cloned);
        return cloned;
    }

}
