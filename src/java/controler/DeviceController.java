package controler;

import bean.Device;
import bean.User;
import controler.util.JsfUtil;
import controler.util.JsfUtil.PersistAction;
import service.DeviceFacade;

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

@Named("deviceController")
@SessionScoped
public class DeviceController implements Serializable {

    @EJB
    private service.DeviceFacade ejbFacade;
    @EJB
    private service.JournalFacade journalFacade;
    @EJB
    private service.HistoriqueFacade historiqueFacade;
    private List<Device> items = null;
    private Device selected;
    private User user;
    private String browser;
    private String categorie;

    public void search() {
        items = ejbFacade.search(user, browser, categorie);
    }

    public DeviceController() {
    }

    public Device getSelected() {
        return selected;
    }

    public void setSelected(Device selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private DeviceFacade getFacade() {
        return ejbFacade;
    }

    public Device prepareCreate() {
        selected = new Device();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("DeviceCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("DeviceUpdated"));
    }

    public void destroy(Device device) {
        selected = device;
        journalFacade.updateDevice(selected);
        historiqueFacade.updateDevice(selected);
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("DeviceDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items.remove(device);    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Device> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                switch (persistAction) {
                    case CREATE:
                        getFacade().edit(selected);
                        JsfUtil.addSuccessMessage("Device bien crée");
                        break;
                    case UPDATE:
                        getFacade().edit(selected);
                        JsfUtil.addSuccessMessage(successMessage);
                        break;
                    default:
                        getFacade().remove(selected);
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

    public Device getDevice(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Device> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Device> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Device.class)
    public static class DeviceControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DeviceController controller = (DeviceController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "deviceController");
            return controller.getDevice(getKey(value));
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
            if (object instanceof Device) {
                Device o = (Device) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Device.class.getName()});
                return null;
            }
        }

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

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

}
