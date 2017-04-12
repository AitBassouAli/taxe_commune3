package controler;

import bean.Historique;
import bean.User;
import controler.util.JsfUtil;
import controler.util.JsfUtil.PersistAction;
import controler.util.SessionUtil;
import service.HistoriqueFacade;

import java.io.Serializable;
import java.util.Date;
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

@Named("historiqueController")
@SessionScoped
public class HistoriqueController implements Serializable {

    @EJB
    private service.HistoriqueFacade ejbFacade;
    @EJB
    private service.JournalFacade journalFacade;
    private List<Historique> items = null;
    private Historique selected;
    
    
    
    //crreation d'un historique de deconnections ali
    public  String deconnection(){
        User user=SessionUtil.getConnectedUser();
        SessionUtil.unSetUser(user);
        selected=new Historique(new Date(),2,user);
        ejbFacade.create(selected);
        return "/index?faces-redirect=true";
    }

    public HistoriqueController() {
    }

    public Historique getSelected() {
        return selected;
    }

    public void setSelected(Historique selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private HistoriqueFacade getFacade() {
        return ejbFacade;
    }

    public Historique prepareCreate() {
        selected = new Historique();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("HistoriqueCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("HistoriqueUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("HistoriqueDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Historique> getItems() {
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
                            journalFacade.journalCreatorDelet("Historique", 1);
                            JsfUtil.addSuccessMessage("Historique bien crée");
                            break;
                        case UPDATE:
                            Historique oldvalue = getFacade().find(selected.getId());
                            getFacade().edit(selected);
                            journalFacade.journalUpdate("Historique", 2, oldvalue, selected);
                            JsfUtil.addSuccessMessage(successMessage);
                            break;
                        default:
                            getFacade().remove(selected);
                            journalFacade.journalCreatorDelet("Historique", 3);
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

    public Historique getHistorique(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Historique> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Historique> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Historique.class)
    public static class HistoriqueControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            HistoriqueController controller = (HistoriqueController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "historiqueController");
            return controller.getHistorique(getKey(value));
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
            if (object instanceof Historique) {
                Historique o = (Historique) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Historique.class.getName()});
                return null;
            }
        }

    }

}
