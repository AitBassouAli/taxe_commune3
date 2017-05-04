package controler;

import bean.TaxeAnnuel;
import bean.TaxeTrim;
import controler.util.JsfUtil;
import controler.util.JsfUtil.PersistAction;
import java.io.IOException;
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
import net.sf.jasperreports.engine.JRException;

@Named("taxeAnnuelController")
@SessionScoped
public class TaxeAnnuelController implements Serializable {

    @EJB
    private service.TaxeAnnuelFacade ejbFacade;
    @EJB
    private service.JournalFacade journalFacade;
    @EJB
    private service.TaxeTrimFacade taxeTrimFacade;
    private List<TaxeAnnuel> items = null;
    private TaxeAnnuel selected;
    private Double montantMin;
    private Double montantMax;
    private int nombreTaxeMin;
    private int nombreTaxetMax;
    private int annee;
    private String localeName;
    private TaxeAnnuel taxeAnnuel;
    private List<TaxeTrim> taxeTrims;

    public TaxeAnnuelController() {
    }

    public void findTaxTrim(TaxeAnnuel t) {
        taxeAnnuel.setTaxeTrims(taxeTrimFacade.findByTaxAnnuel(t));
    }
    
    // jasper
    public void generatPdf(TaxeAnnuel t) throws JRException, IOException {
        ejbFacade.printPdf(t);
        FacesContext.getCurrentInstance().responseComplete();
    }

    public void findByCreteria() {
        //appelle 3la lmethode dyal recherch     
        items = ejbFacade.findTaxeAnnuelByCretere(montantMin, montantMax, nombreTaxeMin, nombreTaxetMax, localeName, annee);
    }

    public void prepareView(TaxeAnnuel taxeAnnuel) {
        selected = taxeAnnuel;

        taxeAnnuel = new TaxeAnnuel();
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

    public Double getMontantMin() {
        return montantMin;
    }

    public void setMontantMin(Double montantMin) {
        this.montantMin = montantMin;
    }

    public Double getMontantMax() {
        return montantMax;
    }

    public void setMontantMax(Double montantMax) {
        this.montantMax = montantMax;
    }

    public int getNombreTaxeMin() {
        return nombreTaxeMin;
    }

    public void setNombreTaxeMin(int nombreTaxeMin) {
        this.nombreTaxeMin = nombreTaxeMin;
    }

    public int getNombreTaxetMax() {
        return nombreTaxetMax;
    }

    public void setNombreTaxetMax(int nombreTaxetMax) {
        this.nombreTaxetMax = nombreTaxetMax;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public String getLocaleName() {
        return localeName;
    }

    public void setLocaleName(String localeName) {
        this.localeName = localeName;
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

    public void destroy(TaxeAnnuel taxeAnnuel) {
        selected = taxeAnnuel;
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("TaxeAnnuelDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items.remove(taxeAnnuel);    // Invalidate list of items to trigger re-query.
        }
    }

    public List<TaxeAnnuel> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public void setItems(List<TaxeAnnuel> items) {
        this.items = items;
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

    public TaxeAnnuel getTaxeAnnuel() {
        if (taxeAnnuel == null) {
            taxeAnnuel = new TaxeAnnuel();
        }
        return taxeAnnuel;
    }

    public void setTaxeAnnuel(TaxeAnnuel taxeAnnuel) {
        this.taxeAnnuel = taxeAnnuel;
    }

    public List<TaxeTrim> getTaxeTrims() {
        return taxeTrims;
    }

    public void setTaxeTrims(List<TaxeTrim> taxeTrims) {
        this.taxeTrims = taxeTrims;
    }

}
