package controler;

import bean.TauxTaxe;
import controler.util.JsfUtil;
import controler.util.JsfUtil.PersistAction;
import service.TauxTaxeFacade;

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

@Named("tauxTaxeController")
@SessionScoped
public class TauxTaxeController implements Serializable {

    @EJB
    private service.TauxTaxeFacade ejbFacade;
    @EJB
    private service.JournalFacade journalFacade;
    private List<TauxTaxe> items = null;
    private TauxTaxe selected;

    public TauxTaxeController() {
    }

    public TauxTaxe getSelected() {
        if (selected == null) {
            selected = new TauxTaxe();
        }
        return selected;
    }

    public void setSelected(TauxTaxe selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TauxTaxeFacade getFacade() {
        return ejbFacade;
    }

    public TauxTaxe prepareCreate() {
        selected = new TauxTaxe();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("TauxTaxeCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("TauxTaxeUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("TauxTaxeDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<TauxTaxe> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                TauxTaxe oldvalue = new TauxTaxe();
                if (persistAction != PersistAction.CREATE) {
                    oldvalue = getFacade().find(selected.getId());
                }
                switch (persistAction) {
                    case CREATE:
                        getFacade().edit(selected);
                        journalFacade.journalUpdate("TauxTaxe", 1, null, selected);
                        JsfUtil.addSuccessMessage("TauxTaxe bien crée");
                        break;
                    case UPDATE:
                        getFacade().edit(selected);
                        journalFacade.journalUpdate("TauxTaxe", 2, oldvalue, selected);
                        JsfUtil.addSuccessMessage(successMessage);
                        break;
                    default:
                        getFacade().remove(selected);
                        journalFacade.journalUpdate("TauxTaxe", 3, oldvalue, selected);
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

    public TauxTaxe getTauxTaxe(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<TauxTaxe> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<TauxTaxe> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = TauxTaxe.class)
    public static class TauxTaxeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TauxTaxeController controller = (TauxTaxeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "tauxTaxeController");
            return controller.getTauxTaxe(getKey(value));
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
            if (object instanceof TauxTaxe) {
                TauxTaxe o = (TauxTaxe) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), TauxTaxe.class.getName()});
                return null;
            }
        }

    }

}
