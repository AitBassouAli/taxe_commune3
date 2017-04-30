package controler;

import bean.AnnexeAdministratif;
import bean.Categorie;
import bean.Quartier;
import bean.Redevable;
import bean.Rue;
import bean.Secteur;
import bean.TaxeTrim;
import bean.User;
import controler.util.JsfUtil;
import controler.util.JsfUtil.PersistAction;
import controler.util.SessionUtil;
import java.io.IOException;
import service.TaxeTrimFacade;
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
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.DonutChartModel;
import org.primefaces.model.chart.LineChartModel;
import service.AnnexeAdministratifFacade;
import service.LocaleFacade;
import service.QuartierFacade;
import service.RedevableFacade;
import service.RueFacade;
import service.SecteurFacade;
import service.TaxeAnnuelFacade;
import service.UserFacade;

@Named("taxeTrimController")
@SessionScoped
public class TaxeTrimController implements Serializable {

    @EJB
    private service.TaxeTrimFacade ejbFacade;
    @EJB
    private RedevableFacade redevableFacade;
    @EJB
    private LocaleFacade localeFacade;
    @EJB
    private RueFacade rueFacade;
    @EJB
    private SecteurFacade secteurFacade;
    @EJB
    private QuartierFacade quartierFacade;
    @EJB
    private AnnexeAdministratifFacade annexeAdministratifFacade;
    @EJB
    private TaxeAnnuelFacade taxeAnnuelFacade;
    @EJB
    private UserFacade userFacade;
    @EJB
    private service.JournalFacade journalFacade;
    private User connectedUser;

    private Quartier quartier;
    private AnnexeAdministratif annexeAdministratif;
    private Secteur secteur;
    private Rue rue;
    private List<TaxeTrim> items = null;
    private TaxeTrim selected;

    //CREATE A NEW TaxeTrim variables
    private String cin;
    private String rc;
    private Redevable redevable;
    private int annee;
    private boolean rendred;
    //attribut de recherche taxeTrim souhail
    private Date dateMin;
    private Date dateMax;
    private Double montantMin;
    private Double montantMax;
    private int nombreNuitMin;
    private int nombreNuitMax;
    private String localeName;
    private String redevableName;
    private Categorie categorie;
    //recherche pour le graph de fatima
    private List<String> activities;
    private String activite;
    private int firstYear;
    private int secondYear;
    private BarChartModel modele = null;
    private LineChartModel lineModel;
    private DonutChartModel donutChartModel;
    private List<TaxeTrim> taxes;
    private int typeGraphe = 1;
    private boolean editRedevableBtn;

    // jasper
    public void generatPdf() throws JRException, IOException {
        ejbFacade.printPdf(selected);
        FacesContext.getCurrentInstance().responseComplete();
    }
    //with Item in the view
    public void generatPdf2(TaxeTrim taxeTrim) throws JRException, IOException  {
       selected=taxeTrim;
       generatPdf();
    }

    public void editRedevableBtnClicked() {
        editRedevableBtn = true;
        selected.getRedevable().setNom("");
    }

    public void prepareEdit(TaxeTrim taxeTrim) {
        selected=taxeTrim;
        editRedevableBtn = false;
        cin = "";
        rc = "";
        redevable = new Redevable();
    }

    public void editRedevableCinjOfTaxe() {
        redevable = getRedevable();
        findRedevableByCin();
        if (redevable != null) {
            selected.setRedevable(redevable);
        } else {
            selected.setRedevable(new Redevable());
        }
    }

    public void editRedevableRcjOfTaxe() {
        redevable = getRedevable();
        findRedevableByRc();
        if (redevable != null) {
            selected.setRedevable(redevable);
        } else {
            selected.setRedevable(new Redevable());
        }
    }

    public void itemSelect() {
        rendred = false;
    }

    public BarChartModel initBarCharModel() {
        BarChartModel model = ejbFacade.initBarModel(ejbFacade.findAll(), 0, 0);
        model.setTitle("Statistique");
        model.setLegendPosition("ne");
        Axis xAxis = model.getAxis(AxisType.X);
        xAxis.setLabel("Les trimestres");
        Axis yAxis = model.getAxis(AxisType.Y);
        yAxis.setLabel("Montant");
        yAxis.setMin(0);
        yAxis.setMax(20000);
        return model;
    }

    //apl au methode de recherches destaxTrm par criter pour construire un graphe
    public void createBarModel() {
        taxes = ejbFacade.findTaxByCritere(activite, firstYear, secondYear, rue, quartier, annexeAdministratif, secteur);
        modele = ejbFacade.initBarModel(taxes, firstYear, secondYear);
        modele.setTitle("Statistique");
        modele.setLegendPosition("ne");
        Axis xAxis = modele.getAxis(AxisType.X);
        xAxis.setLabel("Les trimestres");
        Axis yAxis = modele.getAxis(AxisType.Y);
        yAxis.setLabel("Montant");
        yAxis.setMin(0);
        yAxis.setMax(20000);
    }

    // bar de graphe
    public void createModel() {
        taxes = ejbFacade.findTaxByCritere(activite, firstYear, secondYear, rue, quartier, annexeAdministratif, secteur);
        switch (typeGraphe) {
            case 3:
                donutChartModel = ejbFacade.initDonuModel(taxes, firstYear, secondYear);
                donutChartModel.setTitle("Statistique");
                donutChartModel.setLegendPosition("ne");
                donutChartModel.setSliceMargin(5);
                donutChartModel.setShowDataLabels(true);
                donutChartModel.setDataFormat("value");
                donutChartModel.setShadow(true);
                break;
            case 1: {
                modele = ejbFacade.initBarModel(taxes, firstYear, secondYear);
                modele.setTitle("Statistique");
                modele.setLegendPosition("ne");
                modele.setAnimate(true);
                Axis xAxis = modele.getAxis(AxisType.X);
                xAxis.setLabel("Les trimestres");
                Axis yAxis = modele.getAxis(AxisType.Y);
                yAxis.setLabel("Montant");
                yAxis.setMin(0);
                yAxis.setMax(ejbFacade.maxY(taxes, firstYear, secondYear) * 1.1);
                break;
            }
            case 2: {
                lineModel = ejbFacade.initLineModel(taxes, firstYear, secondYear);
                lineModel.setTitle("Statistique");
                lineModel.setLegendPosition("ne");
                lineModel.setAnimate(true);
                Axis xAxis = lineModel.getAxis(AxisType.X);
                lineModel.getAxes().put(AxisType.X, new CategoryAxis(""));
                xAxis.setLabel("Les trimestres");
                Axis yAxis = lineModel.getAxis(AxisType.Y);
                yAxis.setLabel("Montant");
                yAxis.setMin(0);
                yAxis.setMax(ejbFacade.maxY(taxes, firstYear, secondYear) * 1.1);
                break;
            }
            default:
                break;
        }

    }

    public void findByCreteria() {
        //appelle 3la lmethode dyal recherch     
        items = ejbFacade.findLocaleByCretere(dateMin, dateMax, montantMin, montantMax, nombreNuitMin, nombreNuitMax, localeName, redevableFacade.findByCinRc(redevableName), categorie, secteur, annexeAdministratif, quartier, rue);
    }

    public void findAnnexs() {
        if (secteur == null) {
            getSecteur().setAnnexeAdministratifs(new ArrayList<>());
        } else {
            secteur.setAnnexeAdministratifs(annexeAdministratifFacade.findBySecteur(secteur));
        }
    }

    public void findQuartiers() {
        if (annexeAdministratif == null) {
            getAnnexeAdministratif().setQuartiers(new ArrayList<>());
        } else {
            annexeAdministratif.setQuartiers(quartierFacade.findByAnnexe(annexeAdministratif));
        }

    }

    public void findRues() {
        if (quartier == null) {
            getQuartier().setRues(new ArrayList<>());
        } else {
            quartier.setRues(rueFacade.findByQuartier(quartier));
        }
    }

    public void findRedevableByCin() {
        selected.setLocale(null);
        // redevable.setLocales(localeFacade.findByRedevableCin(cin));
        rc = "";
        redevable = findRedevable();
        redevable.setLocales(localeFacade.findByRedevable(redevable));
        rendred = false;
    }

    public void findRedevableByRc() {
        selected.setLocale(null);
        //redevable.setLocales(localeFacade.findByRedevableRc(rc));
        cin = "";
        redevable = findRedevable();
        redevable.setLocales(localeFacade.findByRedevable(redevable));
        rendred = false;
    }

    public Redevable findRedevable() {
        if (!rc.equals("") || !cin.equals("")) {
            Redevable redevable1 = new Redevable(rc, cin);
            List<Redevable> lst = redevableFacade.findByCinOrRc(redevable1);
            if (lst != null && !lst.isEmpty()) {
                return lst.get(0);
            } else {
                return null;
            }

        } else {
            return null;
        }
    }

    public TaxeTrimController() {
    }

    public TaxeTrim getSelected() {
        if (selected == null) {
            selected = new TaxeTrim();
        }
        return selected;
    }

    public void setSelected(TaxeTrim selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TaxeTrimFacade getFacade() {
        return ejbFacade;
    }

    public TaxeTrim prepareCreate() {
        rendred = false;
        selected = new TaxeTrim();
        initializeEmbeddableKey();
        return selected;
    }

    public void simuler() {
        rendred = true;
        Object[] res = ejbFacade.create(ejbFacade.clone(selected), annee, true);
        if ((int) res[0] == 1) {
            System.out.println("simulation ...");
            selected = ejbFacade.clone((TaxeTrim) res[1]);
        } else {
            switch ((int) res[0]) {
                case -1:
                    JsfUtil.addErrorMessage("TaxeTrim deja payé !!");
            }
        }
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void create() throws JRException, IOException {
        Object[] res = ejbFacade.create(ejbFacade.clone(selected), annee, false);
        if ((int) res[0] == 1) {
            System.out.println("persiting...");
            selected = ejbFacade.clone((TaxeTrim) res[1]);
            selected.setRedevable(redevable);
            selected.setUser(getConnectedUser());
            persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("TaxeTrimCreated"));
          
        } else {
            switch ((int) res[0]) {
                case -1:
                    JsfUtil.addErrorMessage("TaxeTrim deja payé !!");
            }
        }
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        selected = ejbFacade.update(ejbFacade.clone(selected));
        if (selected != null) {
            TaxeTrim oldTaxeTrim = ejbFacade.find(selected.getId());
            persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("TaxeTrimUpdated"));
            if (oldTaxeTrim.getTaxeAnnuel().getNbrTrimesterPaye() == 0 && oldTaxeTrim.getTaxeAnnuel().getTaxeTotale() == 0.0) {
                taxeAnnuelFacade.delete(oldTaxeTrim.getTaxeAnnuel());
            }
            items = null;
        } else {
            JsfUtil.addErrorMessage("TaxeTrim erroor !!");
        }
    }

    public void destroy(TaxeTrim taxeTrim) {
        selected=taxeTrim;
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("TaxeTrimDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items.remove(taxeTrim);    // Invalidate list of items to trigger re-query.
        }
    }

    public List<TaxeTrim> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                TaxeTrim oldvalue = new TaxeTrim();
                if (persistAction != PersistAction.CREATE) {
                    oldvalue = getFacade().find(selected.getId());
                }
                switch (persistAction) {
                    case CREATE:
                        getFacade().edit(selected);
                        journalFacade.journalUpdate("TaxeTrim", 1, null, selected);
                        JsfUtil.addSuccessMessage("TaxeTrim bien crée");
                        break;
                    case UPDATE:
                        getFacade().edit(selected);
                        journalFacade.journalUpdate("TaxeTrim", 2, oldvalue, selected);
                        JsfUtil.addSuccessMessage(successMessage);
                        break;
                    default:
                        getFacade().remove(selected);
                        journalFacade.journalUpdate("TaxeTrim", 3, oldvalue, selected);
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

    public TaxeTrim getTaxeTrim(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<TaxeTrim> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<TaxeTrim> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public RedevableFacade getRedevableFacade() {
        if (redevableFacade == null) {
            redevableFacade = new RedevableFacade();
        }
        return redevableFacade;
    }

    public void setRedevableFacade(RedevableFacade redevableFacade) {
        this.redevableFacade = redevableFacade;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getRc() {
        return rc;
    }

    public void setRc(String rc) {
        this.rc = rc;
    }

    @FacesConverter(forClass = TaxeTrim.class)
    public static class TaxeTrimControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TaxeTrimController controller = (TaxeTrimController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "taxeTrimController");
            return controller.getTaxeTrim(getKey(value));
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
            if (object instanceof TaxeTrim) {
                TaxeTrim o = (TaxeTrim) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), TaxeTrim.class.getName()});
                return null;
            }
        }

    }

    public LocaleFacade getLocaleFacade() {
        return localeFacade;
    }

    public void setLocaleFacade(LocaleFacade localeFacade) {
        this.localeFacade = localeFacade;
    }

    public Redevable getRedevable() {
        if (redevable == null) {
            redevable = new Redevable();
        }
        return redevable;
    }

    public void setRedevable(Redevable redevable) {
        this.redevable = redevable;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public TaxeTrimFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(TaxeTrimFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public RueFacade getRueFacade() {
        return rueFacade;
    }

    public void setRueFacade(RueFacade rueFacade) {
        this.rueFacade = rueFacade;
    }

    public SecteurFacade getSecteurFacade() {
        return secteurFacade;
    }

    public void setSecteurFacade(SecteurFacade secteurFacade) {
        this.secteurFacade = secteurFacade;
    }

    public QuartierFacade getQuartierFacade() {
        return quartierFacade;
    }

    public void setQuartierFacade(QuartierFacade quartierFacade) {
        this.quartierFacade = quartierFacade;
    }

    public AnnexeAdministratifFacade getAnnexeAdministratifFacade() {
        return annexeAdministratifFacade;
    }

    public void setAnnexeAdministratifFacade(AnnexeAdministratifFacade annexeAdministratifFacade) {
        this.annexeAdministratifFacade = annexeAdministratifFacade;
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

    public int getNombreNuitMin() {
        return nombreNuitMin;
    }

    public void setNombreNuitMin(int nombreNuitMin) {
        this.nombreNuitMin = nombreNuitMin;
    }

    public int getNombreNuitMax() {
        return nombreNuitMax;
    }

    public void setNombreNuitMax(int nombreNuitMax) {
        this.nombreNuitMax = nombreNuitMax;
    }

    public String getLocaleName() {
        return localeName;
    }

    public void setLocaleName(String localeName) {
        this.localeName = localeName;
    }

    public String getRedevableName() {
        return redevableName;
    }

    public void setRedevableName(String redevableName) {
        this.redevableName = redevableName;
    }

    public Quartier getQuartier() {
        if (quartier == null) {
            quartier = new Quartier();
        }
        return quartier;
    }

    public void setQuartier(Quartier quartier) {
        this.quartier = quartier;
    }

    public AnnexeAdministratif getAnnexeAdministratif() {
        if (annexeAdministratif == null) {
            annexeAdministratif = new AnnexeAdministratif();
        }
        return annexeAdministratif;
    }

    public void setAnnexeAdministratif(AnnexeAdministratif annexeAdministratif) {
        this.annexeAdministratif = annexeAdministratif;
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

    public Rue getRue() {
        return rue;
    }

    public void setRue(Rue rue) {
        this.rue = rue;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public List<String> getActivities() {
        return activities = localeFacade.findAllActivities();
    }

    public void setActivities(List<String> activities) {
        this.activities = activities;
    }

    public String getActivite() {
        return activite;
    }

    public void setActivite(String activite) {
        this.activite = activite;
    }

    public int getFirstYear() {
        return firstYear;
    }

    public void setFirstYear(int firstYear) {
        this.firstYear = firstYear;
    }

    public int getSecondYear() {
        return secondYear;
    }

    public void setSecondYear(int secondYear) {
        this.secondYear = secondYear;
    }

    public BarChartModel getModele() {
        if (modele == null) {
            modele = initBarCharModel();
        }
        return modele;
    }

    public void setModele(BarChartModel modele) {
        this.modele = modele;
    }

    public List<TaxeTrim> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<TaxeTrim> taxes) {
        this.taxes = taxes;
    }

    public int getTypeGraphe() {
        return typeGraphe;
    }

    public void setTypeGraphe(int typeGraphe) {
        this.typeGraphe = typeGraphe;
    }

    public LineChartModel getLineModel() {
        if (lineModel == null) {
            lineModel = new LineChartModel();
        }
        return lineModel;
    }

    public void setLineModel(LineChartModel lineModel) {
        this.lineModel = lineModel;
    }

    public DonutChartModel getDonutChartModel() {
        if (donutChartModel == null) {
            donutChartModel = new DonutChartModel();
        }
        return donutChartModel;
    }

    public void setDonutChartModel(DonutChartModel donutChartModel) {
        this.donutChartModel = donutChartModel;
    }

    public User getConnectedUser() {
        if (connectedUser == null) {
            connectedUser = userFacade.find(SessionUtil.getConnectedUser().getLogin());
        }
        return connectedUser;
    }

    public void setConnectedUser(User connectedUser) {
        this.connectedUser = connectedUser;
    }

    public boolean isRendred() {
        return rendred;
    }

    public void setRendred(boolean rendred) {
        this.rendred = rendred;
    }

    public boolean isEditRedevableBtn() {
        return editRedevableBtn;
    }

    public void setEditRedevableBtn(boolean editRedevableBtn) {
        this.editRedevableBtn = editRedevableBtn;
    }

}
