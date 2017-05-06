package controler;

import bean.Categorie;
import bean.TauxTaxe;
import bean.TauxTaxeRetard;
import controler.util.JsfUtil;
import controler.util.JsfUtil.PersistAction;
import service.CategorieFacade;

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
import service.JournalFacade;
import service.TauxTaxeRetardFacade;

@Named("categorieController")
@SessionScoped
public class CategorieController implements Serializable {

    @EJB
    private TauxTaxeRetardFacade tauxTaxeRetardFacade;
    @EJB
    private service.CategorieFacade ejbFacade;
    @EJB
    private service.JournalFacade journalFacade;
    private List<Categorie> items = null;
    private Categorie selected;
    private TauxTaxe tauxTaxe;
    private TauxTaxeRetard tauxTaxeRetard;

    public CategorieController() {
    }

    public Categorie getSelected() {
        if (selected == null) {
            selected = new Categorie();
        }
        return selected;
    }

    public void setSelected(Categorie selected) {
        this.selected = selected;
    }

    public void findTaux(Categorie categorie) {
        setTauxTaxe(ejbFacade.searche(categorie));
        setTauxTaxeRetard(tauxTaxeRetardFacade.findByCategorie(categorie));
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    public TauxTaxeRetardFacade getTauxTaxeRetardFacade() {
        return tauxTaxeRetardFacade;
    }

    public void setTauxTaxeRetardFacade(TauxTaxeRetardFacade tauxTaxeRetardFacade) {
        this.tauxTaxeRetardFacade = tauxTaxeRetardFacade;
    }

    public CategorieFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(CategorieFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public JournalFacade getJournalFacade() {
        return journalFacade;
    }

    public void setJournalFacade(JournalFacade journalFacade) {
        this.journalFacade = journalFacade;
    }

    private CategorieFacade getFacade() {
        return ejbFacade;
    }

    public TauxTaxeRetard getTauxTaxeRetard() {
        if (tauxTaxeRetard == null) {
            tauxTaxeRetard = new TauxTaxeRetard();
        }
        return tauxTaxeRetard;
    }

    public void setTauxTaxeRetard(TauxTaxeRetard tauxTaxeRetard) {
        this.tauxTaxeRetard = tauxTaxeRetard;
    }

    public TauxTaxe getTauxTaxe() {
        if (tauxTaxe == null) {
            tauxTaxe = new TauxTaxe();
        }
        return tauxTaxe;
    }

    public void setTauxTaxe(TauxTaxe tauxTaxe) {
        this.tauxTaxe = tauxTaxe;
    }

    public Categorie prepareCreate() {
        selected = new Categorie();
        initializeEmbeddableKey();
        return selected;
    }

    public void prepareEdite(Categorie categorie) {
        selected = categorie;

    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("CategorieCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("CategorieUpdated"));
    }

    public void destroy(Categorie categorie) {
        selected = categorie;
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("CategorieDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection

            items.remove(categorie);    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Categorie> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                Categorie oldvalue = new Categorie();
                if (persistAction != PersistAction.CREATE) {
                    oldvalue = getFacade().find(selected.getId());
                }
                switch (persistAction) {
                    case CREATE:
                        getFacade().edit(selected);
                        journalFacade.journalUpdate("Categorie", 1, "", selected.toString());
                        JsfUtil.addSuccessMessage("Categorie bien crée");
                        break;
                    case UPDATE:
                        getFacade().edit(selected);
                        journalFacade.journalUpdate("Categorie", 2, oldvalue.toString(), selected.toString());
                        JsfUtil.addSuccessMessage(successMessage);
                        break;
                    default:
                        getFacade().remove(selected);
                        journalFacade.journalUpdate("Categorie", 3, oldvalue.toString(),"");
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

    public Categorie getCategorie(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Categorie> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Categorie> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Categorie.class)
    public static class CategorieControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CategorieController controller = (CategorieController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "categorieController");
            return controller.getCategorie(getKey(value));
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
            if (object instanceof Categorie) {
                Categorie o = (Categorie) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Categorie.class.getName()});
                return null;
            }
        }

    }

}
