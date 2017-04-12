package controler;

import bean.Redevable;
import controler.util.JsfUtil;
import controler.util.JsfUtil.PersistAction;
import service.RedevableFacade;

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

@Named("redevableController")
@SessionScoped
public class RedevableController implements Serializable {

    @EJB
    private service.RedevableFacade ejbFacade;
    @EJB
    private service.JournalFacade journalFacade;
    private List<Redevable> items = null;
    private List<Redevable> itemsAvaible;
    private Redevable selected;

    public void findByRCorCIN() {
        itemsAvaible = ejbFacade.findByCinOrRc(selected);
    }
    public void findByCin(){
        selected=ejbFacade.findOnebyCin(selected.getCin());
    }
     public void findByRc(){
        selected=ejbFacade.findOnebyRc(selected.getRc());
    }

    public RedevableController() {
    }

    public Redevable getSelected() {
        if (selected == null) {
            selected = new Redevable();
        }
        return selected;
    }

    public void setSelected(Redevable selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private RedevableFacade getFacade() {
        return ejbFacade;
    }

    public List<Redevable> getItemsAvaible() {
        if (itemsAvaible == null) {
            itemsAvaible = new ArrayList();
        }
        return itemsAvaible;
    }

    public void setItemsAvaible(List<Redevable> itemsAvaible) {
        this.itemsAvaible = itemsAvaible;
    }

    public Redevable prepareCreate() {
        selected = new Redevable();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("RedevableCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("RedevableUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("RedevableDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Redevable> getItems() {
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
                            if (getFacade().findByCinOrRc(selected).isEmpty()) {
                                getFacade().edit(selected);
                                journalFacade.journalCreatorDelet("Redevable", 1);
                                JsfUtil.addSuccessMessage("Redevable bien crée");
                            } else {
                                JsfUtil.addErrorMessage("redevable existe deja dans la base !!");
                            }
                            break;
                        case UPDATE:
                            Redevable oldvalue = getFacade().find(selected.getId());
                            getFacade().edit(selected);
                            journalFacade.journalUpdate("Redevable", 2, oldvalue, selected);
                            JsfUtil.addSuccessMessage(successMessage);
                            break;
                        default:
                            getFacade().remove(selected);
                            journalFacade.journalCreatorDelet("Redevable", 3);
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

    public Redevable getRedevable(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Redevable> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Redevable> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Redevable.class)
    public static class RedevableControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RedevableController controller = (RedevableController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "redevableController");
            return controller.getRedevable(getKey(value));
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
            if (object instanceof Redevable) {
                Redevable o = (Redevable) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Redevable.class.getName()});
                return null;
            }
        }

    }

}
