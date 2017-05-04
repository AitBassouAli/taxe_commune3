/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.AnnexeAdministratif;
import bean.Categorie;
import bean.Locale;
import bean.Quartier;
import bean.Redevable;
import bean.Rue;
import bean.Secteur;
import bean.TaxeAnnuel;
import bean.TaxeTrim;
import controler.util.DateUtil;
import controler.util.PdfUtil;
import controler.util.SearchUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.DonutChartModel;
import org.primefaces.model.chart.LineChartModel;

/**
 *
 * @author HP
 */
@Stateless
public class TaxeTrimFacade extends AbstractFacade<TaxeTrim> {

    @PersistenceContext(unitName = "projet_java_taxPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TaxeTrimFacade() {
        super(TaxeTrim.class);
    }
    @EJB
    private TaxeAnnuelFacade taxeAnnuelFacade;
    @EJB
    private TauxTaxeFacade tauxTaxeFacade;
    @EJB
    private TauxTaxeRetardFacade tauxTaxeRetardFacade;
    @EJB
    private LocaleFacade localeFacade;

    //update taxeTrim   ali
    public TaxeTrim update(TaxeTrim taxeTrim) {
        System.out.println("updating...");
        TaxeTrim loadedTaxe = findTaxeByTaxeAnnuel(taxeTrim, taxeTrim.getTaxeAnnuel().getAnnee());
        if (loadedTaxe != null) {
            if (!Objects.equals(loadedTaxe.getId(), taxeTrim.getId())) {
                System.out.println("nulll  2");
                return null;
            } else {
                System.out.println("switch ...");
                int mois = DateUtil.calculerMois(taxeTrim, taxeTrim.getTaxeAnnuel().getAnnee());
                taxeTrim = calculerRetard(taxeTrim, mois);
                if (taxeTrim == null) {
                    return null;//impossible de payer trimester nest pas encore terminee!!
                }
                System.out.println("setters ...");
                taxeTrim.setMontantTotal(taxeTrim.getMontant() + taxeTrim.getMontantRetard());
                taxeTrim.setNbrMoisRetard(mois - 1);
                TaxeTrim oldTaxeTrim = find(taxeTrim.getId());

                taxeAnnuelFacade.create(taxeTrim.getLocale(), taxeTrim.getTaxeAnnuel().getAnnee());//si il n'existe une taxeAnnuel avec ce locale et l'annee il va le creer 
                TaxeAnnuel taxeAnnuel = taxeAnnuelFacade.findByLocaleAndAnnee(taxeTrim.getLocale(), taxeTrim.getTaxeAnnuel().getAnnee());//100% taxeTrim existe dans la base de donnees
                if (!Objects.equals(taxeAnnuel.getId(), oldTaxeTrim.getTaxeAnnuel().getId())) {
                    if (taxeAnnuel.getNbrTrimesterPaye() >= 4) {//tous les taxeTrim sont paye pour cette annee e ce locale
                        System.out.println("3ndha kter mn 3 deja mkhlsinn");
                        return null;
                    } else {
                        System.out.println("incrementation");
                        taxeAnnuel.setNbrTrimesterPaye(taxeAnnuel.getNbrTrimesterPaye() + 1);
                        taxeAnnuel.setTaxeTotale(taxeAnnuel.getTaxeTotale() + taxeTrim.getMontantTotal());
                        oldTaxeTrim.getTaxeAnnuel().setNbrTrimesterPaye(oldTaxeTrim.getTaxeAnnuel().getNbrTrimesterPaye() - 1);
                        oldTaxeTrim.getTaxeAnnuel().setTaxeTotale(oldTaxeTrim.getTaxeAnnuel().getTaxeTotale() - oldTaxeTrim.getMontantTotal());
                        taxeAnnuelFacade.edit(oldTaxeTrim.getTaxeAnnuel());
                        taxeAnnuelFacade.edit(taxeAnnuel);
                    }
                    taxeTrim.setTaxeAnnuel(taxeAnnuel);
                } else {
                    System.out.println(" meme taxe Anuull ");
                    TaxeAnnuel taxeAnuel = taxeTrim.getTaxeAnnuel();
                    taxeAnuel.setTaxeTotale(taxeAnuel.getTaxeTotale() + (taxeTrim.getMontantTotal() - oldTaxeTrim.getMontantTotal()));
                    taxeAnnuelFacade.edit(taxeAnuel);
                    //2 b7ql b7qll
                }
            }
            return taxeTrim;
        }
        return null;
    }
    //creation d'une taxeTrim  ali

    public Object[] create(TaxeTrim taxeTrim, int annee, boolean simuler) {//false=save;true=simuler
        TaxeTrim loadedTaxe = findTaxeByTaxeAnnuel(taxeTrim, annee);
        if (loadedTaxe != null) {
            return new Object[]{-1, null};
        } else {
            //faire les calcules sans les enregistre dans la base de donnees
            if (DateUtil.getFinTrimestre(taxeTrim.getNumeroTrim(), annee).getTime() > new Date().getTime()) {
                System.out.println("la trimester nest pas encore terminee !!");
                return new Object[]{-3, null};
            }
            int mois = DateUtil.calculerMois(taxeTrim, annee);
            taxeTrim = calculerRetard(taxeTrim, mois);
            if (taxeTrim == null) {
                return new Object[]{-4, null};//impossible de payer trimester nest pas encore terminee!!
            } else {
                System.out.println("les setters");
                taxeTrim.setMontantTotal(taxeTrim.getMontant() + taxeTrim.getMontantRetard());
                taxeTrim.setNbrMoisRetard(mois - 1);
                System.out.println("salaw les setters");
            }
            if (simuler == false) {
                taxeTrim = attachToTaxeAnnuel(taxeTrim, annee);
                if (taxeTrim == null) {
                    return new Object[]{-2, null};
                }
            }
        }
        System.out.println("kolchi howa hadak");
        return new Object[]{1, taxeTrim};
    }

    public TaxeTrim attachToTaxeAnnuel(TaxeTrim taxeTrim, int annee) {
        taxeAnnuelFacade.create(taxeTrim.getLocale(), annee);//si il n'existe une taxeAnnuel avec ce locale et l'annee il va le creer 
        TaxeAnnuel taxeAnnuel = taxeAnnuelFacade.findByLocaleAndAnnee(taxeTrim.getLocale(), annee);//100% taxeTrim existe dans la base de donnees
        if (taxeAnnuel.getNbrTrimesterPaye() >= 4) {//tous les taxeTrim sont paye pour cette annee e ce locale
            System.out.println("3ndha kter mn 3 deja mkhlsinn");
            return null;
        }
        System.out.println("incrementation");
        taxeAnnuel.setNbrTrimesterPaye(taxeAnnuel.getNbrTrimesterPaye() + 1);
        taxeAnnuel.setTaxeTotale(taxeAnnuel.getTaxeTotale() + taxeTrim.getMontantTotal());
        taxeAnnuelFacade.edit(taxeAnnuel);
        System.out.println("attachement");
        taxeTrim.setTaxeAnnuel(taxeAnnuel);
        return taxeTrim;
    }

    public TaxeTrim calculerRetard(TaxeTrim taxeTrim, int mois) {
        switch (mois) {
            case 0: {
                System.out.println("la trimester nest pas encore terminee olla mazal mawslnaha  !!");
                return null;//impossible de payer trimester nest pas encore terminee!!
            }
            default: {
                System.out.println("montant   mois  " + mois);
                taxeTrim.setMontant(tauxTaxeFacade.findByCategorie(taxeTrim.getLocale().getCategorie()).getTaux() * taxeTrim.getNombreNuit() / 1D);
                taxeTrim.setMontantRetard(0D);
                if (mois > 1) {
                    System.out.println("reatardddddd");
                    taxeTrim.setPremierMoisRetard(tauxTaxeRetardFacade.findByCategorie(taxeTrim.getLocale().getCategorie()).getTauxPremierRetard() * taxeTrim.getNombreNuit() / 1D);
                    taxeTrim.setAutresMoisRetard(tauxTaxeRetardFacade.findByCategorie(taxeTrim.getLocale().getCategorie()).getTauxAutreRetard() * taxeTrim.getNombreNuit() * (mois - 2) / 1D);
                    taxeTrim.setMontantRetard((taxeTrim.getAutresMoisRetard() + taxeTrim.getPremierMoisRetard()) / 1D);
                }
            }
            return taxeTrim;
        }
    }

    public void printPdf(TaxeTrim t) throws JRException, IOException {
        List myList = new ArrayList();
        myList.add(t);
        Quartier q = t.getLocale().getRue().getQuartier();
        String nature;
        if (t.getRedevable().getNature() == 1) {
            nature = "Gerant";
        } else {
            nature = "proprietaire";
        }
        String redevable;
        if (!t.getRedevable().getCin().equals("")) {
            redevable = t.getRedevable().getCin();
        } else {
            redevable = t.getRedevable().getRc();
        }
        Map<String, Object> params = new HashMap();
        params.put("nomLocale", t.getLocale().getNom());
        params.put("adresse", t.getLocale().getRue() + " " + q + " " + q.getAnnexeAdministratif() + " " + q.getAnnexeAdministratif().getSecteur());
        params.put("cinRc", redevable);
        params.put("exploitant", nature);
        params.put("annee", t.getTaxeAnnuel().getAnnee());
        params.put("numTrim", t.getNumeroTrim());
        params.put("datePaiement", t.getDatePaiement());
        System.out.println(params);
        System.out.println(t);
        PdfUtil.generatePdf(myList, params, "recu" + t.getId() + ".pdf", "/jasper/taxPaiement.jasper");
    }

//la recherche d'une taxeTrim avec TaxeAnnuel,locale,numero
    public TaxeTrim findTaxeByTaxeAnnuel(TaxeTrim taxeTrim, int annee) {
        String requette = "SELECT tax FROM TaxeTrim tax where 1=1";
        requette += " AND tax.taxeAnnuel.annee =" + annee;
        requette += " AND tax.locale.id =" + taxeTrim.getLocale().getId();
        requette += " AND tax.numeroTrim =" + taxeTrim.getNumeroTrim();
        System.out.println(requette);
        List<TaxeTrim> list = em.createQuery(requette).getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public List<TaxeTrim> findTaxesByLocale(Locale locale) {
        return em.createQuery("SELECT tax FROM TaxeTrim tax where tax.locale.id='" + locale.getId() + "'").getResultList();
    }

    // recherche des taxes pour extraire un graphe
    public List<TaxeTrim> findTaxByCritere(String activite, int firstYear, int secondYear, Rue rue, Quartier quartier, AnnexeAdministratif annex, Secteur secteur) {
        String rqt = "SELECT tax FROM TaxeTrim tax where 1=1 ";
        if (activite != null) {
            rqt += SearchUtil.addConstraint("tax.locale", "activite", "=", activite);
        }
        if (firstYear > 0 && secondYear > 0) {
            rqt += "AND tax.taxeAnnuel.annee in (" + firstYear + "," + secondYear + ")";
        }
        if (rue == null) {
            if (quartier == null) {
                if (annex == null) {
                    if (secteur != null) {
                        rqt += SearchUtil.addConstraint("tax.locale", "rue.quartier.annexeAdministratif.secteur.id", "=", secteur.getId());
                    }
                } else {
                    rqt += SearchUtil.addConstraint("tax.locale", "rue.quartier.annexeAdministratif.id", "=", annex.getId());
                }
            } else {
                rqt += SearchUtil.addConstraint("tax.locale", "rue.quartier.id", "=", quartier.getId());
            }
        } else {
            rqt += SearchUtil.addConstraint("tax.locale", "rue.id", "=", rue.getId());
        }
        System.out.println(rqt);
        return em.createQuery(rqt).getResultList();
    }

    //pour construire la sereis des coordonnees pour les bar
    public BarChartModel initBarModel(List<TaxeTrim> taxes, int firstYear, int secondYear) {
        ChartSeries firstYearTaxe = new ChartSeries();
        ChartSeries secondYearTaxe = new ChartSeries();
        BarChartModel model1 = new BarChartModel();
        firstYearTaxe.setLabel("" + firstYear);
        secondYearTaxe.setLabel("" + secondYear);
        int x;
        for (x = 1; x < 5; x++) {
            Double a = 0.0;
            Double b = 0.0;
            for (TaxeTrim taxeTrim : taxes) {
                if (taxeTrim.getTaxeAnnuel().getAnnee() == firstYear && taxeTrim.getNumeroTrim() == x) {
                    a += taxeTrim.getMontantTotal();
                }
                if (taxeTrim.getTaxeAnnuel().getAnnee() == secondYear && taxeTrim.getNumeroTrim() == x) {
                    b += taxeTrim.getMontantTotal();
                }
            }
            firstYearTaxe.set("Trimestre " + x, a);
            secondYearTaxe.set("Trimestre " + x, b);
        }
        model1.addSeries(firstYearTaxe);
        model1.addSeries(secondYearTaxe);
        return model1;
    }

    // Chart-Donut
    public DonutChartModel initDonuModel(List<TaxeTrim> taxes, int firstYear, int secondYear) {
        DonutChartModel donutMoel = new DonutChartModel();
        Map<String, Number> firstCircle = new LinkedHashMap<>();
        Map<String, Number> secondCircle = new LinkedHashMap<>();
        int x;
        for (x = 1; x < 5; x++) {
            Double a = 0.0;
            Double b = 0.0;
            for (TaxeTrim taxeTrim : taxes) {
                if (taxeTrim.getTaxeAnnuel().getAnnee() == firstYear && taxeTrim.getNumeroTrim() == x) {
                    a += taxeTrim.getMontantTotal();
                }
                if (taxeTrim.getTaxeAnnuel().getAnnee() == secondYear && taxeTrim.getNumeroTrim() == x) {
                    b += taxeTrim.getMontantTotal();
                }
            }
            firstCircle.put("Trimestre " + x + "-" + firstYear, a);
            secondCircle.put("Trimestre " + x + "-" + secondYear, b);
        }

        donutMoel.addCircle(firstCircle);
        donutMoel.addCircle(secondCircle);
        return donutMoel;
    }

    //initialiser les series des coordonnees de lineChart
    public LineChartModel initLineModel(List<TaxeTrim> taxes, int firstYear, int secondYear) {
        ChartSeries firstSerie = new ChartSeries();
        ChartSeries secondSerie = new ChartSeries();
        int x;
        for (x = 1; x < 5; x++) {
            Double a = 0.0;
            Double b = 0.0;
            for (TaxeTrim taxeTrim : taxes) {
                if (taxeTrim.getTaxeAnnuel().getAnnee() == firstYear && taxeTrim.getNumeroTrim() == x) {
                    a += taxeTrim.getMontantTotal();
                }
                if (taxeTrim.getTaxeAnnuel().getAnnee() == secondYear && taxeTrim.getNumeroTrim() == x) {
                    b += taxeTrim.getMontantTotal();
                }
            }
            firstSerie.set("Trimestre " + x, a);
            secondSerie.set("Trimestre " + x, b);
        }
        firstSerie.setLabel("" + firstYear);
        secondSerie.setLabel("" + secondYear);
        LineChartModel modelLine = new LineChartModel();
        modelLine.addSeries(firstSerie);
        modelLine.addSeries(secondSerie);
        modelLine.setShowPointLabels(true);
        return modelLine;
    }

    public Double maxY(List<TaxeTrim> taxes, int firstYear, int secondYear) {
        int x;
        Double max = 0.0;
        for (x = 1; x < 5; x++) {
            Double a = 0.0;
            Double b = 0.0;
            for (TaxeTrim taxeTrim : taxes) {
                if (taxeTrim.getTaxeAnnuel().getAnnee() == firstYear && taxeTrim.getNumeroTrim() == x) {
                    a += taxeTrim.getMontantTotal();
                }
                if (taxeTrim.getTaxeAnnuel().getAnnee() == secondYear && taxeTrim.getNumeroTrim() == x) {
                    b += taxeTrim.getMontantTotal();
                }
            }
            if (a > max) {
                max = a;
            }
            if (b > max) {
                max = b;
            }
        }
        return max;
    }

    // hadi recherche ta3 taxetrime bga3 les crétére 
    public List<TaxeTrim> findLocaleByCretere(Date dateMin, Date dateMax, Double montantMin, Double montantMax, int nombreNuitMin, int nombreNuitMax, String local, Redevable redevable, Categorie categorie, Secteur secteur, AnnexeAdministratif annexeAdministratif, Quartier quartier, Rue rue) {
        {
            String requete = "SELECT ta FROM TaxeTrim ta WHERE 1=1  ";
            requete += SearchUtil.addConstraintMinMaxDate("ta", "datePaiement", dateMin, dateMax);
            if (!local.equals("")) {
                requete += " AND ta.locale.reference='" + local + "'";
            }
            if (redevable != null) {
                if (!redevable.getCin().equals("")) {
                    requete += SearchUtil.addConstraint("ta", "redevable.cin", "=", redevable.getCin());
                } else {
                    requete += SearchUtil.addConstraint("ta", "redevable.id", "=", redevable.getId());
                }
            }
            if (categorie != null) {
                requete += " AND ta.locale.categorie.id='" + categorie.getId() + "'";
            }
            if (rue == null) {
                if (quartier == null) {
                    if (annexeAdministratif == null) {
                        if (secteur != null) {
                            requete += SearchUtil.addConstraint("ta.locale", "rue.quartier.annexeAdministratif.secteur.id", "=", secteur.getId());
                        }
                    } else {
                        requete += SearchUtil.addConstraint("ta.locale", "rue.quartier.annexeAdministratif.id", "=", annexeAdministratif.getId());
                    }
                } else {
                    requete += SearchUtil.addConstraint("ta.locale", "rue.quartier.id", "=", quartier.getId());
                }
            } else {
                requete += SearchUtil.addConstraint("ta.locale", "rue.id", "=", rue.getId());
            }
            requete += SearchUtil.addConstraintMinMax("ta", "montantTotal", montantMin, montantMax);
            if (nombreNuitMin > 0) {
                requete += " AND ta.nombreNuit >='" + nombreNuitMin + "'";
            }
            if (nombreNuitMax > 0) {
                requete += " AND ta.nombreNuit <='" + nombreNuitMax + "'";
            }
            System.out.println(requete);
            return em.createQuery(requete).getResultList();
        }
    }

    public Object[] compare(TaxeTrim nouveau, TaxeTrim auncienne, int type) {
        String newVal = "";
        String oldVal = "";
        switch (type) {
            case 1:
                newVal = "" + nouveau.getNumeroTrim() + "-" + nouveau.getTaxeAnnuel().getAnnee() + "-" + nouveau.getLocale().getNom() + "-" + nouveau.getMontantTotal();
                break;
            case 2:
                if (nouveau.getNumeroTrim() != auncienne.getNumeroTrim()) {
                    oldVal += "N° => " + auncienne.getNumeroTrim();
                    newVal += "N° => " + nouveau.getNumeroTrim();
                }

                if (!Objects.equals(nouveau.getTaxeAnnuel().getId(), auncienne.getTaxeAnnuel().getId())) {
                    TaxeAnnuel newAnnee = taxeAnnuelFacade.find(nouveau.getTaxeAnnuel().getId());
                    TaxeAnnuel oldAnnee = taxeAnnuelFacade.find(auncienne.getTaxeAnnuel().getId());
                    oldVal += " , Annee => : " + newAnnee.getAnnee();
                    newVal += " , Annee => : " + oldAnnee.getAnnee();
                }
                if (!Objects.equals(nouveau.getLocale().getId(), auncienne.getLocale().getId())) {
                    Locale newloc = localeFacade.find(nouveau.getLocale().getId());
                    Locale oldLoc = localeFacade.find(auncienne.getLocale().getId());
                    oldVal += " , Locale => : " + newloc.getNom();
                    newVal += " , Locale => : " + oldLoc.getNom();
                }

                if (nouveau.getNombreNuit() != auncienne.getNombreNuit()) {
                    oldVal += "NbrNuits => " + auncienne.getNombreNuit();
                    newVal += "NbrNuits => " + nouveau.getNombreNuit();
                }
                if (!Objects.equals(nouveau.getMontantTotal(), auncienne.getMontantTotal())) {
                    oldVal += "MontantTotal => " + auncienne.getMontantTotal();
                    newVal += "MontantTotal => " + nouveau.getMontantTotal();
                }
                break;
            case 3:
                oldVal = "" + auncienne.getNumeroTrim() + "-" + auncienne.getTaxeAnnuel().getAnnee() + "-" + auncienne.getLocale().getNom() + "-" + auncienne.getMontantTotal();
                ;
                break;
        }
        return new Object[]{newVal, oldVal};
    }

    public void clone(TaxeTrim taxeTrimSource, TaxeTrim taxeTrimDestaination) {
        taxeTrimDestaination.setId(taxeTrimSource.getId());
        taxeTrimDestaination.setAutresMoisRetard(taxeTrimSource.getAutresMoisRetard());
        taxeTrimDestaination.setPremierMoisRetard(taxeTrimSource.getPremierMoisRetard());
        taxeTrimDestaination.setLocale(taxeTrimSource.getLocale());
        taxeTrimDestaination.setDatePaiement(taxeTrimSource.getDatePaiement());
        taxeTrimDestaination.setMontant(taxeTrimSource.getMontant());
        taxeTrimDestaination.setMontantRetard(taxeTrimSource.getMontantRetard());
        taxeTrimDestaination.setMontantTotal(taxeTrimSource.getMontantTotal());
        taxeTrimDestaination.setNbrMoisRetard(taxeTrimSource.getNbrMoisRetard());
        taxeTrimDestaination.setNombreClients(taxeTrimSource.getNombreClients());
        taxeTrimDestaination.setNombreNuit(taxeTrimSource.getNombreNuit());
        taxeTrimDestaination.setNumeroTrim(taxeTrimSource.getNumeroTrim());
        taxeTrimDestaination.setRedevable(taxeTrimSource.getRedevable());
        taxeTrimDestaination.setTaxeAnnuel(taxeTrimSource.getTaxeAnnuel());

    }

    public TaxeTrim clone(TaxeTrim taxeTrim) {
        TaxeTrim cloned = new TaxeTrim();
        clone(taxeTrim, cloned);
        return cloned;
    }

}
