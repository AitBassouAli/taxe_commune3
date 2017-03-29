package controler;

import bean.AnnexeAdministratif;
import bean.Quartier;
import bean.Rue;
import bean.Secteur;
import bean.User;
import controler.util.JsfUtil;
import controler.util.JsfUtil.PersistAction;
import controler.util.SessionUtil;
import service.RueFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import service.AnnexeAdministratifFacade;
import service.QuartierFacade;
import service.SecteurFacade;
import service.UserFacade;

@Named("rueController")
@SessionScoped
public class RueController implements Serializable {
    
    @EJB
    private service.RueFacade ejbFacade;
    @EJB
    private QuartierFacade quartierFacade;
    @EJB
    private AnnexeAdministratifFacade annexeAdministratifFacade;
    @EJB
    private SecteurFacade secteurFacade;
    @EJB
    private UserFacade userFacade;
    private User user;
    private List<Rue> items = null;
    private Rue selected;
    //pour la page adressage
    private Quartier quartier;
    private AnnexeAdministratif annexeAdministratif;
    private Secteur secteur;
    private String nomAnnex;
    private String nomQuartier;
    private String nomRue;
    private boolean allQuartiersClicked = false;
    private boolean allAnnexesClicked = false;
    private boolean allSecteursClicked = false;
    private Quartier quartierCombo;
    private AnnexeAdministratif annexeAdministratifCombo;
    private Secteur secteurCombo;
    private List<Secteur> secteurs;
    
    public void saveSecteur() {
        secteurFacade.create(secteurFacade.clone(secteur));
        secteurs.add(secteurFacade.clone(secteur));
    }
    
    public void editSecteur() {
        secteurFacade.edit(secteurCombo);
    }
    
    public void prepareCreateSecteur() {
        secteur = new Secteur();
    }
    
    public void prepareEditSecteur(Secteur secteur1) {
        secteurCombo = secteur1;
    }
    
    public void destroySecteur(Secteur secteur1) {
        secteurFacade.remove(secteur1);
        secteurs.remove(secteur1);
        secteur = new Secteur();
    }

    //les methodes concernant la foeme des annexeAdministaratifs
    public void saveAnnex() {
        annexeAdministratif.setSecteur(secteurCombo);
        annexeAdministratifFacade.create(annexeAdministratifFacade.clone(annexeAdministratif));
        long id = annexeAdministratifFacade.generateId("AnnexeAdministratif", "id");
        secteurCombo.getAnnexeAdministratifs().add(annexeAdministratifFacade.find(id));
        secteur.setAnnexeAdministratifs(secteurCombo.getAnnexeAdministratifs());
        secteur = secteurCombo;
    }
    
    public void editAnnexe() {
        annexeAdministratifCombo.setSecteur(secteurCombo);
        annexeAdministratifFacade.edit(annexeAdministratifCombo);
    }
    
    public void showAllSecteurs() {
        secteurCombo = null;
    }
    
    public void secteurSelect() {
        allSecteursClicked = true;
    }
    
    public void prepareCreateAnnexe() {
        annexeAdministratif = new AnnexeAdministratif();
        allSecteursClicked = false;
    }
    
    public void prepareEditAnnexe(AnnexeAdministratif annexeAdministratif1) {
        annexeAdministratifCombo = annexeAdministratif1;
        secteurCombo = annexeAdministratifCombo.getSecteur();
        allSecteursClicked = false;
    }
    
    public void destroyAnnexe(AnnexeAdministratif annexeAdministratif1) {
        annexeAdministratifFacade.remove(annexeAdministratif1);
        secteur.getAnnexeAdministratifs().remove(annexeAdministratif1);
        annexeAdministratif = new AnnexeAdministratif();
    }
//les methodes concernant la foeme des Quartiers

    public void saveQuartier() {
        quartier.setAnnexeAdministratif(annexeAdministratifCombo);
        quartierFacade.create(quartierFacade.clone(quartier));
        long id = annexeAdministratifFacade.generateId("Quartier", "id");
        annexeAdministratifCombo.getQuartiers().add(quartierFacade.find(id));
        annexeAdministratif.setQuartiers(annexeAdministratifCombo.getQuartiers());
        annexeAdministratif = annexeAdministratifCombo;
    }
    
    public void editQuartier() {
        quartierCombo.setAnnexeAdministratif(annexeAdministratifCombo);
        quartierFacade.edit(quartierCombo);
    }
    
    public void showAllAnnexes() {
        annexeAdministratifCombo = null;
    }
    
    public void annexSelect() {
        allAnnexesClicked = true;
    }
    
    public void prepareCreateQuartier() {
        quartier = new Quartier();
        allAnnexesClicked = false;
    }
    
    public void PrepareEditQuartier(Quartier quartier1) {
        allAnnexesClicked = false;
        quartierCombo = quartier1;
        annexeAdministratifCombo = quartierCombo.getAnnexeAdministratif();
    }
    
    public void destroyQuartier(Quartier quartier1) {
        quartierFacade.remove(quartier1);
        annexeAdministratif.getQuartiers().remove(quartier1);
        quartier = new Quartier();
    }
    
    public void showAllQuartiers() {
        allQuartiersClicked = true;
        quartierCombo = null;
    }

    public void prepareEditRue(Rue rue1) {
        selected = rue1;
        quartierCombo = selected.getQuartier();
        allQuartiersClicked = false;
    }
    
    public void findAnnexByName() {
        secteur.setAnnexeAdministratifs(annexeAdministratifFacade.findByName(nomAnnex));
    }
    
    public void findQuartierByName() {
        annexeAdministratif.setQuartiers(quartierFacade.findByName(nomQuartier));
    }
    
    public void findRueByName() {
        quartier.setRues(ejbFacade.findByName(nomRue));
    }
    
    public void findAnnexs() {
        if (secteurCombo == null) {
            getSecteur().setAnnexeAdministratifs(new ArrayList<>());
        } else {
            secteurCombo.setAnnexeAdministratifs(annexeAdministratifFacade.findBySecteur(secteurCombo));
        }
        allAnnexesClicked = true;
    }
    
    public void findQuartiers() {
        if (annexeAdministratifCombo == null) {
            getAnnexeAdministratif().setQuartiers(new ArrayList<>());
        } else {
            annexeAdministratifCombo.setQuartiers(quartierFacade.findByAnnexe(annexeAdministratifCombo));
        }
        showAllQuartiers();
    }
    
    public void findRues() {
        if (quartierCombo == null) {
            getQuartier().setRues(new ArrayList<>());
        } else {
            quartierCombo.setRues(ejbFacade.findByQuartier(quartierCombo));
        }
    }
    
    public void findAnnexs(Secteur secteur1) {
        secteurCombo = secteurFacade.clone(secteur1);
        secteur.setAnnexeAdministratifs(annexeAdministratifFacade.findBySecteur(secteurCombo));
    }
    
    public void findQuartiers(AnnexeAdministratif annexeAdministratif1) {
        annexeAdministratifCombo = annexeAdministratifFacade.clone(annexeAdministratif1);
        annexeAdministratif.setQuartiers(quartierFacade.findByAnnexe(annexeAdministratifCombo));
    }
    
    public void findRues(Quartier quartier1) {
        quartierCombo = quartier1;
        quartier.setRues(ejbFacade.findByQuartier(quartierCombo));
    }
    
    public RueController() {
    }
    
    public Rue getSelected() {
        if (selected == null) {
            selected = new Rue();
        }
        return selected;
    }
    
    public void setSelected(Rue selected) {
        this.selected = selected;
    }
    
    protected void setEmbeddableKeys() {
    }
    
    protected void initializeEmbeddableKey() {
    }
    
    private RueFacade getFacade() {
        return ejbFacade;
    }
    
    public Rue prepareCreate() {
        allQuartiersClicked = false;
        selected = new Rue();
        initializeEmbeddableKey();
        return selected;
    }
    
    public void create() {
        selected.setQuartier(quartierCombo);
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("RueCreated"));
        if (!JsfUtil.isValidationFailed()) {
            long id = annexeAdministratifFacade.generateId("Rue", "id");
            quartierCombo.getRues().add(ejbFacade.find(id));
            quartier.setRues(quartierCombo.getRues());
        }
    }
    
    public void update() {
        selected.setQuartier(quartierCombo);
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("RueUpdated"));
    }
    
    public void destroy(Rue rue) {
        selected = rue;
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("RueDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            quartier.getRues().remove(rue);
        }
    }
    
    public List<Rue> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }
    
    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }
    
    public Rue getRue(java.lang.Long id) {
        return getFacade().find(id);
    }
    
    public List<Rue> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }
    
    public List<Rue> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }
    
    @FacesConverter(forClass = Rue.class)
    public static class RueControllerConverter implements Converter {
        
        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RueController controller = (RueController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "rueController");
            return controller.getRue(getKey(value));
        }
        
        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }
        
        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }
        
        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Rue) {
                Rue o = (Rue) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Rue.class.getName()});
                return null;
            }
        }
        
    }
    
    public Quartier getQuartier() {
        if (quartier == null) {
            quartier = new Quartier();
        }
        return quartier;
    }
    
    public void setQuartier(Quartier quartier) {
        this.quartier = quartier;
    }
    
    public AnnexeAdministratif getAnnexeAdministratif() {
        if (annexeAdministratif == null) {
            annexeAdministratif = new AnnexeAdministratif();
        }
        return annexeAdministratif;
    }
    
    public void setAnnexeAdministratif(AnnexeAdministratif annexeAdministratif) {
        this.annexeAdministratif = annexeAdministratif;
    }
    
    public Secteur getSecteur() {
        if (secteur == null) {
            secteur = new Secteur();
        }
        return secteur;
    }
    
    public void setSecteur(Secteur secteur) {
        this.secteur = secteur;
    }
    
    public String getNomAnnex() {
        return nomAnnex;
    }
    
    public void setNomAnnex(String nomAnnex) {
        this.nomAnnex = nomAnnex;
    }
    
    public String getNomQuartier() {
        return nomQuartier;
    }
    
    public void setNomQuartier(String nomQuartier) {
        this.nomQuartier = nomQuartier;
    }
    
    public String getNomRue() {
        return nomRue;
    }
    
    public void setNomRue(String nomRue) {
        this.nomRue = nomRue;
    }
    
    public User getUser() {
        if (user == null) {
            user = userFacade.find(SessionUtil.getConnectedUser().getLogin());
        }
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public boolean isAllQuartiersClicked() {
        return allQuartiersClicked;
    }
    
    public void setAllQuartiersClicked(boolean allQuartiersClicked) {
        this.allQuartiersClicked = allQuartiersClicked;
    }
    
    public boolean isAllAnnexesClicked() {
        return allAnnexesClicked;
    }
    
    public void setAllAnnexesClicked(boolean allAnnexesClicked) {
        this.allAnnexesClicked = allAnnexesClicked;
    }
    
    public boolean isAllSecteursClicked() {
        return allSecteursClicked;
    }
    
    public void setAllSecteursClicked(boolean allSecteursClicked) {
        this.allSecteursClicked = allSecteursClicked;
    }
    
    public Quartier getQuartierCombo() {
        return quartierCombo;
    }
    
    public void setQuartierCombo(Quartier quartierCombo) {
        this.quartierCombo = quartierCombo;
    }
    
    public AnnexeAdministratif getAnnexeAdministratifCombo() {
        return annexeAdministratifCombo;
    }
    
    public void setAnnexeAdministratifCombo(AnnexeAdministratif annexeAdministratifCombo) {
        this.annexeAdministratifCombo = annexeAdministratifCombo;
    }
    
    public Secteur getSecteurCombo() {
        return secteurCombo;
    }
    
    public void setSecteurCombo(Secteur secteurCombo) {
        this.secteurCombo = secteurCombo;
    }
    
    public List<Secteur> getSecteurs() {
        if (secteur == null) {
            secteurs = secteurFacade.findAll();
        }
        return secteurs;
    }
    
    public void setSecteurs(List<Secteur> secteurs) {
        this.secteurs = secteurs;
    }
    
}
