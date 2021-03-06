package controler;

import bean.AnnexeAdministratif;
import bean.Categorie;
import bean.Locale;
import bean.PositionLocale;
import bean.Quartier;
import bean.Redevable;
import bean.Rue;
import bean.Secteur;
import bean.TaxeTrim;
import com.google.gson.Gson;
import static com.sun.javafx.logging.PulseLogger.addMessage;
import controler.util.JsfUtil;
import controler.util.JsfUtil.PersistAction;
import service.LocaleFacade;

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
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.primefaces.context.RequestContext;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import service.AnnexeAdministratifFacade;
import service.QuartierFacade;
import service.RedevableFacade;
import service.RueFacade;
import service.TaxeTrimFacade;

@Named("localeController")
@SessionScoped
public class LocaleController implements Serializable {

    @EJB
    private service.LocaleFacade ejbFacade;
    @EJB
    private RueFacade rueFacade;
    @EJB
    private TaxeTrimFacade taxeTrimFacade;
    @EJB
    private QuartierFacade quartierFacade;
    @EJB
    private AnnexeAdministratifFacade annexeAdministratifFacade;
    @EJB
    private RedevableFacade redevableFacade;
    @EJB
    private service.JournalFacade journalFacade;
    @EJB
    private service.PositionLocaleFacade positionLocaleFacade;
    private List<Locale> items = null;
    private List<Locale> itemsRecherche;
    private Locale selected;
    //pour list des taxe pour une locale search
    private List<TaxeTrim> taxeTrims = null;
    //create
    private Quartier quartier;
    private AnnexeAdministratif annexeAdministratif;
    private Secteur secteur;
    //chercher le redevable d'un locale dans la creation $serach locale
    private Rue rue;
    private Categorie categorie;
    private String gerantCinRc;
    private String proprietaireCinRc;
    private String gson;
    private Double lat = 0D;
    private Double lng = 0D;
    private Locale locale;
    //edit locale
    private boolean editerGerant = false;
    private boolean editerProprietaire = false;
    //creation Locale&Redevable
    private int typeRedevable = 1;
    private Redevable gerant;
    private Redevable proprietaire;
    private MapModel emptyModel;

    public  void refresh(){
        selected=null;
        rue=null;
        quartier=null;
        annexeAdministratif=null;
        secteur=null;
        categorie=null;
        locale=null;
        proprietaireCinRc="";
        gerantCinRc="";
    }
    public String addLocalesMarkersToMap1() {
        items = ejbFacade.findByGerantOrProprietaire(categorie, redevableFacade.findByCinRc(proprietaireCinRc), selected.getActivite(), redevableFacade.findByCinRc(gerantCinRc), rue, quartier, annexeAdministratif, secteur, locale);
        MapModel localsModel = new DefaultMapModel();
        if (items == null || items.isEmpty()) {
            JsfUtil.addErrorMessage("Aucun locale trouvée");
        }
        for (Locale item : items) {
            System.out.println(item.getId());
            if (item.getPositionLocale() != null) {
                PositionLocale positionLocale = positionLocaleFacade.find(item.getPositionLocale().getId());
                if (positionLocale.getId() != null) {
                    System.out.println(" lat :" + positionLocale.getLat() + ", lng :" + positionLocale.getLng());
                    localsModel.addOverlay(new Marker(new LatLng(positionLocale.getLat(), positionLocale.getLng())));
                }
            }
        }
        setEmptyModel(localsModel);
        return "MapByCretere?faces-redirect=true";
    }

    public String addOneLocaleMarkersToMap(Locale item) {
        selected = item;
        MapModel localsModel = new DefaultMapModel();
        System.out.println(item.getId());
        if (item.getPositionLocale() != null) {
            PositionLocale positionLocale = positionLocaleFacade.find(item.getPositionLocale().getId());
            if (positionLocale.getId() != null) {
                System.out.println(" lat :" + positionLocale.getLat() + ", lng :" + positionLocale.getLng());
                localsModel.addOverlay(new Marker(new LatLng(positionLocale.getLat(), positionLocale.getLng())));
            }
        } else {
            JsfUtil.addSuccessMessage("Veuillez selectinner une position pour le locale");
        }
        lat = 0D;
        lng = 0D;
        setEmptyModel(localsModel);
        return "Search?faces-redirect=true";
    }

    public MapModel getEmptyModel() {
        return emptyModel;
    }

    public void setEmptyModel(MapModel emptyModel) {
        this.emptyModel = emptyModel;
    }

    public String prepareCreateLocaleAndRedevable() {
        prepareCreate();
        proprietaire = new Redevable();
        gerant = new Redevable();
        gerantCinRc = "";
        proprietaireCinRc = "";
        return "createLocaleAndRedevable";
    }

    public void createGerant() {
        if (gerant != null) {
            if (redevableFacade.redevableHasRcOrCin(gerant).isEmpty()) {
                Object[] newOldCreate = redevableFacade.compare(gerant, new Redevable(), 1);
                gerant.setNature(1);
                redevableFacade.edit(gerant);
                journalFacade.journalUpdate("Redevable", 1, newOldCreate[1], newOldCreate[0]);
                JsfUtil.addSuccessMessage("Gerant bien crée");
            } else {
                JsfUtil.addErrorMessage("redevable existe deja dans la base !!");
            }
        }
    }

    public void createProprietaire() {
        if (proprietaire != null) {
            if (redevableFacade.redevableHasRcOrCin(proprietaire).isEmpty()) {
                Object[] newOldCreate = redevableFacade.compare(proprietaire, new Redevable(), 1);
                gerant.setNature(2);
                redevableFacade.edit(proprietaire);
                journalFacade.journalUpdate("Redevable", 1, newOldCreate[1], newOldCreate[0]);
                JsfUtil.addSuccessMessage("Proprietaire bien crée");
            } else {
                JsfUtil.addErrorMessage("redevable existe deja dans la base !!");
            }
        }
    }

    public void mapOneLocale() {
        setGson(new Gson().toJson(selected.getPositionLocale()));
        System.out.println("ha gson : " + gson);
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("saved", true);    //basic parameter
        context.addCallbackParam("user", getGson());     //pojo as json
        //execute javascript oncomplete
        context.execute("createLocalesMarkersInOverlay1(" + getGson() + ");");
        context.execute("console.log('Hani Salit dyal 1 locale');");
    }

    public void attachPositionToLocale() {
        ejbFacade.attachLocaleToPosition(selected, lat, lng);
        JsfUtil.addSuccessMessage("Locale bien positionnée");
    }

    public void onPointSelect() {
        JsfUtil.addSuccessMessage("succsess ");
        addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "Point Selected", "Lat:" + getLat() + ", Lng:" + getLng()) + "");
        System.out.println(getLat());
        System.out.println(getLng());
    }

    public void addLocalesMarkersToMap() {
        setGson(new Gson().toJson(findPositionLocales()));
        System.out.println("ha gson : " + gson);
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("saved", true);    //basic parameter
        context.addCallbackParam("user", getGson());     //pojo as json
        //execute javascript oncomplete
        context.execute("createLocalesMarkersInOverlay(" + getGson() + ");");
        context.execute("console.log('Hani Salit');");
    }

    public List<PositionLocale> findPositionLocales() {
        items = ejbFacade.findForMap(selected.getComplementAdresse(), redevableFacade.findByCinRc(proprietaireCinRc), redevableFacade.findByCinRc(gerantCinRc), selected.getRue(), quartier, annexeAdministratif, secteur);
        List<PositionLocale> positionLocales = new ArrayList<>();
        if (items == null) {
            positionLocales.add(createNewPositionLocal());
        } else {
            items.stream().filter((local) -> (local.getPositionLocale() != null)).forEach((local) -> {
                positionLocales.add(local.getPositionLocale());
            });
        }
        return positionLocales;
    }
//    public List<PositionLocale> findPositionLocales() {
//        List<PositionLocale> positionLocales = new ArrayList<>();
//        if (selected.getRue() == null) {
//            positionLocales.add(createNewPositionLocal());
//            setGson(new Gson().toJson(positionLocales));
//        } else {
//            List<Locale> ls = ejbFacade.findByRue(selected.getRue());
//            for (int i = 0; i < ls.size(); i++) {
//                Locale get = ls.get(i);
//                positionLocales.add(get.getPositionLocale());
//            }
//            setGson(new Gson().toJson(positionLocales));
//        }
//        System.out.println("ha gson : " + gson);
//        return positionLocales;
//    }

    public PositionLocale createNewPositionLocal() {
        PositionLocale positionLocale = new PositionLocale();
        positionLocale.setId(1L);
        positionLocale.setLat(0D);
        positionLocale.setLng(0D);
        return positionLocale;
    }

    public String getGson() {
        return gson;
    }

    public void setGson(String gson) {
        this.gson = gson;
    }

    public void findProprietaireByCinOrRc() {
        Redevable redevable = redevableFacade.findByCinRc(proprietaireCinRc);
        if (redevable == null || redevable.getId() == null) {
            JsfUtil.addErrorMessage("Aucun Redevable trouvée aves ces donnéés");
        }
        selected.setProprietaire(redevable);
    }

    public void findGerantByCinOrRc() {
        Redevable redevable = redevableFacade.findByCinRc(gerantCinRc);
        if (redevable == null || redevable.getId() == null) {
            JsfUtil.addErrorMessage("Aucun Redevable trouvée aves ces donnéés");
        }
        selected.setGerant(redevable);
    }

    //la recherche des locale avec plusieurs criteres
    public void findByCreteria() {
        itemsRecherche = ejbFacade.findByGerantOrProprietaire(categorie, redevableFacade.findByCinRc(proprietaireCinRc), selected.getActivite(), redevableFacade.findByCinRc(gerantCinRc), rue, quartier, annexeAdministratif, secteur, locale);
    }

    public void findByCreteria2() {
        itemsRecherche = ejbFacade.findByGerantOrProprietaire2(categorie, redevableFacade.findByCinRc(proprietaireCinRc), selected.getReference(), redevableFacade.findByCinRc(gerantCinRc));
    }

    public void findtaxes(Locale locale) {
        List<TaxeTrim> taxes = taxeTrimFacade.findTaxesByLocale(locale);
        if (taxes.isEmpty() == true) {
            JsfUtil.addErrorMessage("aucune taxe trimestrielle trouvé pour ce local");
        } else {
            setTaxeTrims(taxeTrimFacade.findTaxesByLocale(locale));
        }
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

    public LocaleController() {
    }

    public Locale getSelected() {
        if (selected == null) {
            selected = new Locale();
        }
        return selected;
    }

    public void setSelected(Locale selected) {
        this.selected = selected;
    }

    public List<Locale> getItemsRecherche() {
        if (itemsRecherche == null) {
            itemsRecherche = ejbFacade.findAll();
        }
        return itemsRecherche;
    }

    public void setItemsRecherche(List<Locale> itemsRecherche) {
        this.itemsRecherche = itemsRecherche;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private LocaleFacade getFacade() {
        return ejbFacade;
    }

    public RedevableFacade getRedevableFacade() {
        return redevableFacade;
    }

    public void setRedevableFacade(RedevableFacade redevableFacade) {
        this.redevableFacade = redevableFacade;
    }

    public List<TaxeTrim> getTaxeTrims() {
        if (taxeTrims == null) {
            taxeTrims = new ArrayList<>();
        }
        return taxeTrims;
    }

    public void setTaxeTrims(List<TaxeTrim> taxeTrims) {
        this.taxeTrims = taxeTrims;
    }

    public LocaleFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(LocaleFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public RueFacade getRueFacade() {
        return rueFacade;
    }

    public void setRueFacade(RueFacade rueFacade) {
        this.rueFacade = rueFacade;
    }

    public Locale prepareCreate() {
        selected = new Locale();
        initializeEmbeddableKey();
        return selected;
    }

    public String create() {
        getFacade().generateReference(selected.getRue(), selected);
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("LocaleCreated"));
        if (!JsfUtil.isValidationFailed()) {
            itemsRecherche = null;
            return "List?faces-redirect=true";// Invalidate list of items to trigger re-query.
        }
        return null;
    }

    public void editGerant() {
        editerGerant = true;
        selected.setGerant(new Redevable());
    }

    public void editProprietaire() {
        editerProprietaire = true;
        selected.setProprietaire(new Redevable());
    }

    public String preparMap(Locale locale) {
        selected = ejbFacade.find(locale.getId());
        return "Search?faces-redirect=true";
    }

    public void preparUpdate(Locale locale) {
        proprietaireCinRc = "";
        gerantCinRc = "";
        selected = ejbFacade.find(locale.getId());
        editerGerant = false;
        editerProprietaire = false;
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("LocaleUpdated"));
        items = null;
    }

    public void destroy(Locale locale) {
        selected = locale;
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("LocaleDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items.remove(locale);    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Locale> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                Locale oldvalue = new Locale();
                if (persistAction != PersistAction.CREATE) {
                    oldvalue = getFacade().find(selected.getId());
                }
                switch (persistAction) {
                    case CREATE:
                        Object[] newOldCreate = ejbFacade.compare(selected, oldvalue, 1);
                        getFacade().edit(selected);
                        journalFacade.journalUpdate("Locale", 1, newOldCreate[1], newOldCreate[0]);
                        JsfUtil.addSuccessMessage("Locale bien crée");
                        break;
                    case UPDATE:
                        Object[] newOldUpdate = ejbFacade.compare(selected, oldvalue, 2);
                        getFacade().edit(selected);
                        journalFacade.journalUpdate("Locale", 2, newOldUpdate[1], newOldUpdate[0]);
                        JsfUtil.addSuccessMessage(successMessage);
                        break;
                    default:
                        Object[] newOldDelete = ejbFacade.compare(selected, oldvalue, 3);
                        getFacade().remove(selected);
                        journalFacade.journalUpdate("Locale", 3, newOldDelete[1], newOldDelete[0]);
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

    public Locale getLocale(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Locale> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Locale> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Locale.class)
    public static class LocaleControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            LocaleController controller = (LocaleController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "localeController");
            return controller.getLocale(getKey(value));
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
            if (object instanceof Locale) {
                Locale o = (Locale) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Locale.class.getName()});
                return null;
            }
        }

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

    public String getGerantCinRc() {
        return gerantCinRc;
    }

    public void setGerantCinRc(String gerantCinRc) {
        this.gerantCinRc = gerantCinRc;
    }

    public String getProprietaireCinRc() {
        return proprietaireCinRc;
    }

    public void setProprietaireCinRc(String proprietaireCinRc) {
        this.proprietaireCinRc = proprietaireCinRc;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public boolean isEditerGerant() {
        return editerGerant;
    }

    public void setEditerGerant(boolean editerGerant) {
        this.editerGerant = editerGerant;
    }

    public boolean isEditerProprietaire() {
        return editerProprietaire;
    }

    public void setEditerProprietaire(boolean editerProprietaire) {
        this.editerProprietaire = editerProprietaire;
    }

    public int getTypeRedevable() {
        return typeRedevable;
    }

    public void setTypeRedevable(int typeRedevable) {
        this.typeRedevable = typeRedevable;
    }

    public Redevable getGerant() {
        if (gerant == null) {
            gerant = new Redevable();
        }
        return gerant;
    }

    public void setGerant(Redevable gerant) {
        this.gerant = gerant;
    }

    public Redevable getProprietaire() {
        if (proprietaire == null) {
            proprietaire = new Redevable();
        }
        return proprietaire;
    }

    public void setProprietaire(Redevable proprietaire) {
        this.proprietaire = proprietaire;
    }

    public Rue getRue() {
        return rue;
    }

    public void setRue(Rue rue) {
        this.rue = rue;
    }

    public Locale getLocale() {
        if (locale == null) {
            locale = new Locale();
        }
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

}
