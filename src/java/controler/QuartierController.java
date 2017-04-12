package controler;

import bean.AnnexeAdministratif;
import bean.Quartier;
import bean.Secteur;
import controler.util.JsfUtil;
import controler.util.JsfUtil.PersistAction;
import service.QuartierFacade;

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
import service.RueFacade;

@Named("quartierController")
@SessionScoped
public class QuartierController implements Serializable {

    @EJB
    private service.QuartierFacade ejbFacade;
    @EJB
    private service.AnnexeAdministratifFacade annexAdministratifFacade;
    @EJB
    private service.JournalFacade journalFacade;
    private List<Quartier> items = null;
    private Quartier selected;
    private Secteur secteur;
    private AnnexeAdministratif annexe;
    private boolean allBtnClicked = false;

    //pour afficher le combobox des annexes au lieu de du l'annexe selectione sur la table
    public void showAllAnnexes() {
        allBtnClicked = true;
        annexe = null;
    }

    public void findAnnexs() {
        if (secteur == null) {
            getSecteur().setAnnexeAdministratifs(new ArrayList<>());
        } else {
            secteur.setAnnexeAdministratifs(annexAdministratifFacade.findBySecteur(secteur));
        }
        showAllAnnexes();
    }

    public void detail() {
        secteur.setAnnexeAdministratifs(annexAdministratifFacade.findBySecteur(secteur));
    }

    public void setAnnexAdministratifFacade(AnnexeAdministratifFacade annexAdministratifFacade) {
        this.annexAdministratifFacade = annexAdministratifFacade;
    }

    public void initialise(Quartier quartier) {
        selected = quartier;
    }

    public QuartierController() {
    }

    public Quartier getSelected() {
        if (selected == null) {
            selected = new Quartier();
        }
        return selected;
    }

    public void setSelected(Quartier selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private QuartierFacade getFacade() {
        return ejbFacade;
    }

    public Quartier prepareCreate() {
        allBtnClicked = false;
        selected = new Quartier();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        selected.setAnnexeAdministratif(annexe);
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("QuartierCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("QuartierUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("QuartierDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Quartier> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (null != persistAction) {
                    switch (persistAction) {
                        case CREATE:
                            getFacade().edit(selected);
                            journalFacade.journalCreatorDelet("Quartier", 1);
                            JsfUtil.addSuccessMessage("Quartier bien crée");
                            break;
                        case UPDATE:
                            Quartier oldvalue = getFacade().find(selected.getId());
                            getFacade().edit(selected);
                            journalFacade.journalUpdate("Quartier", 2, oldvalue, selected);
                            JsfUtil.addSuccessMessage(successMessage);
                            break;
                        default:
                            getFacade().remove(selected);
                            journalFacade.journalCreatorDelet("Quartier", 3);
                            JsfUtil.addSuccessMessage(successMessage);
                            break;
                    }
                }

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

    public Quartier getQuartier(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Quartier> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Quartier> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Quartier.class)
    public static class QuartierControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            QuartierController controller = (QuartierController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "quartierController");
            return controller.getQuartier(getKey(value));
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
            if (object instanceof Quartier) {
                Quartier o = (Quartier) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Quartier.class.getName()});
                return null;
            }
        }

    }

    public AnnexeAdministratif getAnnexe() {
        if (annexe == null) {
            annexe = new AnnexeAdministratif();
        }
        return annexe;
    }

    public void setAnnexe(AnnexeAdministratif annexe) {
        this.annexe = annexe;
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

    public boolean isAllBtnClicked() {
        return allBtnClicked;
    }

    public void setAllBtnClicked(boolean allBtnClicked) {
        this.allBtnClicked = allBtnClicked;
    }

}
