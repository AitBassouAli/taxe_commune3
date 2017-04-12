package controler;

import bean.TauxTaxeRetard;
import controler.util.JsfUtil;
import controler.util.JsfUtil.PersistAction;
import service.TauxTaxeRetardFacade;

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

@Named("tauxTaxeRetardController")
@SessionScoped
public class TauxTaxeRetardController implements Serializable {

    @EJB
    private service.TauxTaxeRetardFacade ejbFacade;
    @EJB
    private service.JournalFacade journalFacade;
    private List<TauxTaxeRetard> items = null;
    private TauxTaxeRetard selected;

    public TauxTaxeRetardController() {
    }

    public TauxTaxeRetard getSelected() {
        if (selected == null) {
            selected = new TauxTaxeRetard();
        }

        return selected;
    }

    public void setSelected(TauxTaxeRetard selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TauxTaxeRetardFacade getFacade() {
        return ejbFacade;
    }

    public TauxTaxeRetard prepareCreate() {
        selected = new TauxTaxeRetard();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("TauxTaxeRetardCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("TauxTaxeRetardUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("TauxTaxeRetardDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<TauxTaxeRetard> getItems() {
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
                            journalFacade.journalCreatorDelet("TauxTaxeRetard", 1);
                            JsfUtil.addSuccessMessage("TauxTaxeRetard bien crée");
                            break;
                        case UPDATE:
                            TauxTaxeRetard oldvalue = getFacade().find(selected.getId());
                            getFacade().edit(selected);
                            journalFacade.journalUpdate("TauxTaxeRetard", 2, oldvalue, selected);
                            JsfUtil.addSuccessMessage(successMessage);
                            break;
                        default:
                            getFacade().remove(selected);
                            journalFacade.journalCreatorDelet("TauxTaxeRetard", 3);
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

    public TauxTaxeRetard getTauxTaxeRetard(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<TauxTaxeRetard> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<TauxTaxeRetard> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = TauxTaxeRetard.class)
    public static class TauxTaxeRetardControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TauxTaxeRetardController controller = (TauxTaxeRetardController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "tauxTaxeRetardController");
            return controller.getTauxTaxeRetard(getKey(value));
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
            if (object instanceof TauxTaxeRetard) {
                TauxTaxeRetard o = (TauxTaxeRetard) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), TauxTaxeRetard.class.getName()});
                return null;
            }
        }

    }

}
