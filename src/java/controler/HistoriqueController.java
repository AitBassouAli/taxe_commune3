package controler;

import bean.Device;
import bean.Historique;
import bean.User;
import controler.util.DeviceUtil;
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
import service.DeviceFacade;

@Named("historiqueController")
@SessionScoped
public class HistoriqueController implements Serializable {

    @EJB
    private service.HistoriqueFacade ejbFacade;
    @EJB
    private service.JournalFacade journalFacade;
    @EJB
    private service.UserFacade userFacade;
    @EJB
    private DeviceFacade deviceFacade;
    private List<Historique> items = null;
    private Historique selected;
    private List<User> users;
    private User user;
    private Date dateMin;
    private Date dateMax;
    private int type;

    private int verificationDate() {
        if (dateMax != null && dateMin != null) {
            if (dateMax.getTime() > dateMin.getTime()) {
                return 1;
            }
            return -1;
        }
        return 2;
    }

    public void rechercher() {
        if (verificationDate() > 0) {
            items = ejbFacade.rechercher(dateMin, dateMax, type, user);
        }
    }

    public void findAll() {
        items = ejbFacade.findAll();
    }

    //crreation d'un historique de deconnections ali
    public String deconnection() {
        User connectedUser = SessionUtil.getConnectedUser();
        SessionUtil.unSetUser(connectedUser);
        Device device = deviceFacade.curentDevice(connectedUser, DeviceUtil.getDevice());
        selected = new Historique(new Date(), 2, connectedUser, device);
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

    public void destroy(Historique historique) {
        selected = historique;
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("HistoriqueDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items.remove(historique);    // Invalidate list of items to trigger re-query.
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
                Historique oldvalue = new Historique();
                if (persistAction != PersistAction.CREATE) {
                    oldvalue = getFacade().find(selected.getId());
                }
                switch (persistAction) {
                    case CREATE:
                        getFacade().edit(selected);
                        journalFacade.journalUpdate("Historique", 1, null, selected);
                        JsfUtil.addSuccessMessage("Historique bien crée");
                        break;
                    case UPDATE:
                        getFacade().edit(selected);
                        journalFacade.journalUpdate("Historique", 2, oldvalue, selected);
                        JsfUtil.addSuccessMessage(successMessage);
                        break;
                    default:
                        getFacade().remove(selected);
                        journalFacade.journalUpdate("Historique", 2, oldvalue, selected);
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

    public List<User> getUsers() {
        if (users == null) {
            users = userFacade.findAll();
        }
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public User getUser() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDateMin() {
        return dateMin;
    }

    public void setDateMin(Date dateMin) {
        this.dateMin = dateMin;
    }

    public Date getDateMax() {
        return dateMax;
    }

    public void setDateMax(Date dateMax) {
        this.dateMax = dateMax;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
