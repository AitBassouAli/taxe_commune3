package controler;

import bean.AnnexeAdministratif;
import bean.Secteur;
import controler.util.JsfUtil;
import controler.util.JsfUtil.PersistAction;
import service.AnnexeAdministratifFacade;

import java.io.Serializable;
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

@Named("annexeAdministratifController")
@SessionScoped
public class AnnexeAdministratifController implements Serializable {

    @EJB
    private service.AnnexeAdministratifFacade ejbFacade;
    @EJB
    private service.JournalFacade journalFacade;
    private List<AnnexeAdministratif> items = null;
    private AnnexeAdministratif selected;
    private Secteur secteur;
    private boolean allBtnClicked = true;

    public AnnexeAdministratifController() {
    }

    public AnnexeAdministratif getSelected() {
        if (selected == null) {
            selected = new AnnexeAdministratif();
        }
        return selected;
    }

    //pour afficher le combobox des secteure au lieu de du secteur selectione sur la table
    public void showAllSecteurs() {
        allBtnClicked = true;
        secteur = null;
    }

    public void itemSelect() {
        allBtnClicked = true;
    }

    public void setSelected(AnnexeAdministratif selected) {
        this.selected = selected;
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

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private AnnexeAdministratifFacade getFacade() {
        return ejbFacade;
    }

    public AnnexeAdministratif prepareCreate() {
        allBtnClicked = false;
        selected = new AnnexeAdministratif();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        selected.setSecteur(secteur);
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("AnnexeAdministratifCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("AnnexeAdministratifUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("AnnexeAdministratifDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<AnnexeAdministratif> getItems() {
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
                            journalFacade.journalCreatorDelet("AnnexeAdministratif", 1);
                            JsfUtil.addSuccessMessage("AnnexeAdministratif bien crée");
                            break;
                        case UPDATE:
                            AnnexeAdministratif oldvalue = getFacade().find(selected.getId());
                            getFacade().edit(selected);
                            journalFacade.journalUpdate("AnnexeAdministratif", 2, oldvalue, selected);
                            JsfUtil.addSuccessMessage(successMessage);
                            break;
                        default:
                            getFacade().remove(selected);
                            journalFacade.journalCreatorDelet("AnnexeAdministratif", 3);
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

    public AnnexeAdministratif getAnnexeAdministratif(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<AnnexeAdministratif> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<AnnexeAdministratif> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = AnnexeAdministratif.class)
    public static class AnnexeAdministratifControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AnnexeAdministratifController controller = (AnnexeAdministratifController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "annexeAdministratifController");
            return controller.getAnnexeAdministratif(getKey(value));
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
            if (object instanceof AnnexeAdministratif) {
                AnnexeAdministratif o = (AnnexeAdministratif) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), AnnexeAdministratif.class.getName()});
                return null;
            }
        }

    }

    public boolean isAllBtnClicked() {
        return allBtnClicked;
    }

    public void setAllBtnClicked(boolean allBtnClicked) {
        this.allBtnClicked = allBtnClicked;
    }

}
