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
import java.util.Objects;
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
    @EJB
    private RedevableFacade redevableFacade;

    @PersistenceContext(unitName = "projet_java_taxPU")
    private EntityManager em;

    public void updateRue(Rue rue) {
        String rqt = "UPDATE Locale l set l.rue = " + null + " WHERE l.rue.id =" + rue.getId();
        System.out.println(rqt);
        em.createQuery(rqt).executeUpdate();
    }

    public List<Locale> findForMap(String complement, Redevable proprietaire, Redevable gerant, Rue rue, Quartier quartier, AnnexeAdministratif annex, Secteur secteur) {
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
        requette += findByAdress(rue, quartier, annex, secteur);
        if (!complement.equals("")) {
            requette += SearchUtil.addConstraint("l", "complementAdresse", "=", complement);
        }
        System.out.println(requette);
        return em.createQuery(requette).getResultList();
    }

    public List<Locale> mapByAdresse(Rue rue, Quartier quartier, AnnexeAdministratif annex, Secteur secteur) {
        String requette = "SELECT l FROM Locale l WHERE 1=1";
        requette += findByAdress(rue, quartier, annex, secteur);
        System.out.println(requette);
        return em.createQuery(requette).getResultList();
    }

    public String findByAdress(Rue rue, Quartier quartier, AnnexeAdministratif annex, Secteur secteur) {
        String requette = " ";
        if (rue == null) {
            if (quartier == null) {
                if (annex == null) {
                    if (secteur != null) {
                        requette += SearchUtil.addConstraint("l", "rue.quartier.annexeAdministratif.secteur.id", "=", secteur.getId());
                    }
                } else {
                    requette += SearchUtil.addConstraint("l", "rue.quartier.annexeAdministratif.id", "=", annex.getId());
                }
            } else {
                requette += SearchUtil.addConstraint("l", "rue.quartier.id", "=", quartier.getId());
            }
        } else {
            requette += SearchUtil.addConstraint("l", "rue.id", "=", rue.getId());
        }
        return requette;
    }

    public Locale attachLocaleToPosition(Locale locale, double lat, double lng) {
        PositionLocale positionLocale = new PositionLocale();
        positionLocale.setLat(lat);
        positionLocale.setLng(lng);
        long idPos = positionLocaleFacade.generateId("PositionLocale", "id");
        positionLocale.setId(idPos);
        positionLocaleFacade.create(positionLocale);
        PositionLocale loaded = positionLocaleFacade.find(idPos);
        locale.setPositionLocale(loaded);
        edit(locale);
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
        if (redevable != null) {
            String rqt = "SELECT l FROM Locale l WHERE l.proprietaire.id= '" + redevable.getId() + "' OR l.gerant.id='" + redevable.getId() + "'";
            System.out.println(rqt);
            return em.createQuery(rqt).getResultList();
        } else {
            return null;
        }
    }

    public List<Locale> findByGerantOrProprietaire(Categorie categorie, Redevable proprietaire, String activite, Redevable gerant, Rue rue, Quartier quartier, AnnexeAdministratif annex, Secteur secteur, Locale reference) {
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
            requette += SearchUtil.addConstraint("l", "activite", "LIKE", "%" + activite + "%");
        }
        requette += findByAdress(rue, quartier, annex, secteur);
        if (reference != null) {
            requette += SearchUtil.addConstraint("l", "id", "=", reference.getId());
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

    public Object[] compare(Locale nouveau, Locale auncienne, int type) {
        String newVal = "";
        String oldVal = "";
        switch (type) {
            case 1:
                newVal = "" + nouveau.getReference() + " " + nouveau.getNom();
                break;
            case 2:
                if (!nouveau.getNom().equals(auncienne.getNom())) {
                    oldVal += "Nom => " + auncienne.getNom();
                    newVal += "Nom => " + nouveau.getNom();
                }
                if (!nouveau.getActivite().equals(auncienne.getActivite())) {
                    oldVal += ", Activité =>" + auncienne.getActivite();
                    newVal += ", Activité =>" + nouveau.getActivite();
                }
                if (!Objects.equals(nouveau.getProprietaire().getId(), auncienne.getProprietaire().getId())) {
                    Redevable newprop = redevableFacade.find(nouveau.getProprietaire().getId());
                    Redevable oldprop = redevableFacade.find(auncienne.getProprietaire().getId());
                    oldVal += " , Proprietaire => : " + newprop.getNom();
                    newVal += " , Proprietaire => : " + oldprop.getNom();
                }
                if (!Objects.equals(nouveau.getGerant().getId(), auncienne.getGerant().getId())) {
                    Redevable newGerant = redevableFacade.find(nouveau.getGerant().getId());
                    Redevable oldGerant = redevableFacade.find(auncienne.getGerant().getId());
                    oldVal += " , Gerant => : " + newGerant.getNom();
                    newVal += " , Gearnt => : " + oldGerant.getNom();
                }
                break;
            case 3:
                oldVal = "" + nouveau.getReference() + " " + nouveau.getNom();
                ;
                break;
        }
        return new Object[]{newVal, oldVal};
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
