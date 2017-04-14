package controler;

import bean.Device;
import bean.Historique;
import bean.User;
import controler.util.DeviceUtil;
import controler.util.JsfUtil;
import controler.util.JsfUtil.PersistAction;
import controler.util.SessionUtil;
import service.UserFacade;
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
import service.HistoriqueFacade;

@Named("userController")
@SessionScoped
public class UserController implements Serializable {

    @EJB
    private service.UserFacade ejbFacade;
    @EJB
    private HistoriqueFacade historiqueFacade;
    @EJB
    private service.JournalFacade journalFacade;
    @EJB
    private DeviceFacade deviceFacade;
    private List<User> items = null;
    private User selected = new User();
    private User connectedUser;

    public String reset() {
        User loadedUser = ejbFacade.find(selected);

        if (loadedUser.getEmail().equals(selected.getEmail()) && loadedUser.getTel().equals(selected.getTel())) {
            if (loadedUser.getNom().equals(selected.getTel()) && loadedUser.getPrenom().equals(selected.getPrenom())) {
                return loadedUser.getPassword();
            }
        }
        return "/faces/index";
    }

    public String connectAsAdmin() {
        User loadedUser = ejbFacade.find(selected);
        if (loadedUser != null) {
            if (loadedUser.isAdmine()) {
                String seConnecter = seConnecter();
                if ("/user/Home".equals(seConnecter)) {
                    JsfUtil.addSuccessMessage("Bienvenue admin");
                    return "/user/Admin";
                }
                JsfUtil.addErrorMessage("ERROR Connection");
                return "/index";
            }
            JsfUtil.addErrorMessage("ERROR Connection user not found");
            return "/index";
        } else {
            return "/user/Home";
        }
    }
    // int tentatives = 3;

    public String seConnecter() {
        Object[] res = ejbFacade.seConnecter(selected, DeviceUtil.getDevice());
        int res1 = (int) res[0];
        if (res1 < 0) {
            JsfUtil.addErrorMessage("le code de l'erreur " + res1);
            return "/index?faces-redirect=true";
        } else {
            SessionUtil.registerUser(ejbFacade.clone(selected));
            historiqueFacade.create(new Historique(new Date(), 1, ejbFacade.clone(selected), deviceFacade.curentDevice(selected, DeviceUtil.getDevice())));
            return "/user/Home?faces-redirect=true";
        }
    }
//    public String seConnecter() {
//        int res1 = ejbFacade.seConnecter(selected);
//        if (res1 == 1) {
//            SessionUtil.registerUser(selected);
//            historiqueFacade.create(new Historique(new Date(), 1, selected));
//            return "/user/Home?faces-redirect=true";
//        }
//        return "/index";
//    }

    public void adminCheaked() {
        selected.setCreateAdresse(true);
        selected.setCreateCtegorieTaux(true);
        selected.setCreateLocale(true);
        selected.setCreateRedevable(true);
        selected.setCreateTaxes(true);
        selected.setCreateUser(true);
    }

    public User getSelected() {
        if (selected == null) {
            selected = new User();
        }
        return selected;
    }

    public void setSelected(User selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private UserFacade getFacade() {
        return ejbFacade;
    }

    public User prepareCreate() {
        selected = new User();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        if (selected.isAdmine() == true) {
            adminCheaked();
        }
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("UserCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UserUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("UserDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<User> getItems() {
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
                            journalFacade.journalCreatorDelet("User", 1);
                            JsfUtil.addSuccessMessage("User bien crée");
                            break;
                        case UPDATE:
                            User oldvalue = getFacade().find(selected.getLogin());
                            getFacade().edit(selected);
                            journalFacade.journalUpdate("User", 2, oldvalue, selected);
                            JsfUtil.addSuccessMessage(successMessage);
                            break;
                        default:
                            getFacade().remove(selected);
                            journalFacade.journalCreatorDelet("User", 3);
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

    public User getUser(java.lang.String id) {
        return getFacade().find(id);
    }

    public List<User> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<User> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = User.class)
    public static class UserControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UserController controller = (UserController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userController");
            return controller.getUser(getKey(value));
        }

        java.lang.String getKey(String value) {
            java.lang.String key;
            key = value;
            return key;
        }

        String getStringKey(java.lang.String value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof User) {
                User o = (User) object;
                return getStringKey(o.getLogin());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), User.class.getName()});
                return null;
            }
        }

    }

    public User getConnectedUser() {
        if (connectedUser == null) {
            connectedUser = ejbFacade.find(SessionUtil.getConnectedUser().getLogin());
        }
        return connectedUser;
    }

    public void setConnectedUser(User connectedUser) {
        this.connectedUser = connectedUser;
    }

}
