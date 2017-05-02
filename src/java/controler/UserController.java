package controler;

import bean.Historique;
import bean.Secteur;
import bean.User;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.urlPatternType;
import controler.util.DeviceUtil;
import controler.util.JsfUtil;
import controler.util.JsfUtil.PersistAction;
import controler.util.SessionUtil;
import service.UserFacade;
import java.io.Serializable;
import java.util.ArrayList;
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
import service.AnnexeAdministratifFacade;
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
    @EJB
    private AnnexeAdministratifFacade annexeAdministratifFacade;
    private List<User> items = null;
    private User selected = new User();
    private User oldUser;
    private User connectedUser;
    //la page profile + changer mdp
    private String oldPassword;
    private String changePassword;
    private String changeRepetePassword;
    private boolean afficheProfile = true;
    private boolean changerPasswrd = false;
    private boolean changerAutreInfos = false;
    //la recherche
    private Secteur secteur;

    public void findByCreteria() {
        items = ejbFacade.findByCreteria(selected, secteur);
    }

    public void refresh() {
        selected = null;
        secteur = null;
        items = null;
    }

    public void findAnnexs() {
        if (secteur == null) {
            getSecteur().setAnnexeAdministratifs(new ArrayList<>());
        } else {
            secteur.setAnnexeAdministratifs(annexeAdministratifFacade.findBySecteur(secteur));
        }
    }

    public String genaratePasswrd() {
        if (!selected.getLogin().equals("")) {
            int res = ejbFacade.sendPW(selected.getLogin());
            if (res < 0) {
                JsfUtil.addErrorMessage("there is a problem");
            } else {
                JsfUtil.addSuccessMessage("loook your email");
                return "/inaccessible/user/Home?faces-redirect=true";
            }
        }
        return null;
    }

    public void aficherProfil(boolean profile, boolean password, boolean autreInfos) {
        afficheProfile = profile;
        changerPasswrd = password;
        changerAutreInfos = autreInfos;
        oldPassword = "";
        changePassword = "";
        changeRepetePassword = "";
    }

    public void changePass() {
        int res = ejbFacade.changePassword(getConnectedUser().getLogin(), oldPassword, changePassword, changeRepetePassword);
        showMessage(res);
    }

    public void changeInformation() {
        ejbFacade.changeData(connectedUser);
        JsfUtil.addSuccessMessage("Modification avec succes");
    }

    private void showMessage(int res) {
        if (res == -1) {
            JsfUtil.addErrorMessage("la confirmation de votre mot de passe n'est pas correct");
        } else if (res == -2) {
            JsfUtil.addErrorMessage("l'ancient mot de passe ne correspond pas au mot de passe de la base de données");
        } else if (res == -3) {
            JsfUtil.addErrorMessage("le nouveau mot de passe ne doit pas etre l'ancient");
        } else {
            JsfUtil.addSuccessMessage("Modification avec succes ");
        }
    }

    public void preparUpdate(User user) {
        oldUser = ejbFacade.find(user.getLogin());
        selected = ejbFacade.find(user.getLogin());
    }

    public void bloquer(User user) {
        String msg = "";
        oldUser = ejbFacade.find(user.getLogin());
        selected = ejbFacade.find(user.getLogin());
        if (selected.getBlocked() == 1) {
            selected.setBlocked(0);
            msg += "Utilisateur est mintenant Debloquée";
        } else {
            selected.setBlocked(1);
            msg += "Utilisateur est mintenant Bloquée";
        }
        update();
        JsfUtil.addSuccessMessage(msg);
    }
    
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
            SessionUtil.registerUser(selected);
            historiqueFacade.create(new Historique(new Date(), 1, ejbFacade.clone(selected), deviceFacade.curentDevice(selected, DeviceUtil.getDevice())));
            return "/inaccessible/user/Home?faces-redirect=true";
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
        items=null;
//        items.set(items.indexOf(oldUser), selected);
//        oldUser = null;
    }

    public void destroy(User user) {
        selected = user;
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("UserDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items.remove(user);    // Invalidate list of items to trigger re-query.
        }
    }

    public List<User> getItems() {
        if (items == null) {
            items = getFacade().findByAnnexe(getConnectedUser());
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                User oldvalue = new User();
                if (persistAction != PersistAction.CREATE) {
                    oldvalue = getFacade().find(selected.getLogin());
                }
                switch (persistAction) {
                    case CREATE:
                        getFacade().addUser(selected);
                        journalFacade.journalUpdate("User", 1, null, selected);
                        JsfUtil.addSuccessMessage("User bien crée");
                        break;
                    case UPDATE:
                        getFacade().edit(selected);
                        journalFacade.journalUpdate("User", 2, oldvalue, selected);
                        JsfUtil.addSuccessMessage(successMessage);
                        break;
                    default:
                        getFacade().remove(selected);
                        journalFacade.journalUpdate("User", 3, oldvalue, selected);
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

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getChangePassword() {
        return changePassword;
    }

    public void setChangePassword(String changePassword) {
        this.changePassword = changePassword;
    }

    public String getChangeRepetePassword() {
        return changeRepetePassword;
    }

    public void setChangeRepetePassword(String changeRepetePassword) {
        this.changeRepetePassword = changeRepetePassword;
    }

    public boolean isAfficheProfile() {
        return afficheProfile;
    }

    public void setAfficheProfile(boolean afficheProfile) {
        this.afficheProfile = afficheProfile;
    }

    public boolean isChangerPasswrd() {
        return changerPasswrd;
    }

    public void setChangerPasswrd(boolean changerPasswrd) {
        this.changerPasswrd = changerPasswrd;
    }

    public boolean isChangerAutreInfos() {
        return changerAutreInfos;
    }

    public void setChangerAutreInfos(boolean changerAutreInfos) {
        this.changerAutreInfos = changerAutreInfos;
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

    public User getOldUser() {
        if (oldUser == null) {
            oldUser = new User();
        }
        return oldUser;
    }

    public void setOldUser(User oldUser) {
        this.oldUser = oldUser;
    }

}
