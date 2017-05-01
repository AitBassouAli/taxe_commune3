package controler;

import bean.TaxeAnnuel;
import controler.util.JsfUtil;
import controler.util.JsfUtil.PersistAction;
import service.TaxeAnnuelFacade;

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

@Named("taxeAnnuelController")
@SessionScoped
public class TaxeAnnuelController implements Serializable {

    @EJB
    private service.TaxeAnnuelFacade ejbFacade;
    @EJB
    private service.JournalFacade journalFacade;
    private List<TaxeAnnuel> items = null;
    private TaxeAnnuel selected;

    public TaxeAnnuelController() {
    }

    public TaxeAnnuel getSelected() {
        if (selected == null) {
            selected = new TaxeAnnuel();
        }
        return selected;
    }

    public void setSelected(TaxeAnnuel selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TaxeAnnuelFacade getFacade() {
        return ejbFacade;
    }

    public TaxeAnnuel prepareCreate() {
        selected = new TaxeAnnuel();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("TaxeAnnuelCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("TaxeAnnuelUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("TaxeAnnuelDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<TaxeAnnuel> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                TaxeAnnuel oldvalue = new TaxeAnnuel();
                if (persistAction != PersistAction.CREATE) {
                    oldvalue = getFacade().find(selected.getId());
                }
                switch (persistAction) {
                    case CREATE:
                        getFacade().edit(selected);
                        journalFacade.journalUpdate("TaxeAnnuel", 1, null, selected);
                        JsfUtil.addSuccessMessage("TaxeAnnuel bien crée");
                        break;
                    case UPDATE:
                        getFacade().edit(selected);
                        journalFacade.journalUpdate("TaxeAnnuel", 2, oldvalue, selected);
                        JsfUtil.addSuccessMessage(successMessage);
                        break;
                    default:
                        getFacade().remove(selected);
                        journalFacade.journalUpdate("TaxeAnnuel", 3, oldvalue, selected);
                        JsfUtil.addSuccessMessage(successMessage);
                        break;
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

    public TaxeAnnuel getTaxeAnnuel(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<TaxeAnnuel> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<TaxeAnnuel> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = TaxeAnnuel.class)
    public static class TaxeAnnuelControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TaxeAnnuelController controller = (TaxeAnnuelController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "taxeAnnuelController");
            return controller.getTaxeAnnuel(getKey(value));
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
            if (object instanceof TaxeAnnuel) {
                TaxeAnnuel o = (TaxeAnnuel) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), TaxeAnnuel.class.getName()});
                return null;
            }
        }

    }

}
