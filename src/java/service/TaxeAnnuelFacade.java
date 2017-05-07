/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Locale;
import bean.Quartier;
import bean.TaxeAnnuel;
import bean.User;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIConversion;
import controler.util.FrenchNumberToWords;
import controler.util.PdfUtil;
import controler.util.SearchUtil;
import controler.util.SessionUtil;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author HP
 */
@Stateless
public class TaxeAnnuelFacade extends AbstractFacade<TaxeAnnuel> {

    @PersistenceContext(unitName = "projet_java_taxPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TaxeAnnuelFacade() {
        super(TaxeAnnuel.class);
    }
    @EJB
    private TaxeTrimFacade taxeTrimFacade;
    @EJB
    private UserFacade userFacade;

    public void printPdf(TaxeAnnuel t) throws JRException, IOException {
        String cinRc;
        Quartier q = t.getLocale().getRue().getQuartier();
        if (t.getLocale().getProprietaire().getCin().equals("")) {
            cinRc = t.getLocale().getProprietaire().getRc();
        } else {
            cinRc = t.getLocale().getProprietaire().getCin();
        }
        User user = userFacade.find(SessionUtil.getConnectedUser().getLogin());
        Map<String, Object> params = new HashMap();
        params.put("adresse",q.getNom()+" "+ t.getLocale().getRue().getNom() + " " +t.getLocale().getComplementAdresse());
        params.put("complementAdrs", t.getLocale().getComplementAdresse());
        params.put("nomLocale", t.getLocale().getNom());
        params.put("exploitant", t.getLocale().getProprietaire().toString());
        params.put("cin", cinRc);
        params.put("tel", t.getLocale().getProprietaire().getFax());
        params.put("somme", t.getTaxeTotale());
        params.put("lettre", FrenchNumberToWords.convert(t.getTaxeTotale()));
        params.put("numDeclarat", t.getId());
        params.put("datSystem", new Date());
        params.put("user", user.getNom() + " " + user.getPrenom());
        params.put("annee", t.getAnnee());
        System.out.println(params);
        System.out.println(t);
        PdfUtil.generatePdf(taxeTrimFacade.findByTaxAnnuel(t), params, "taxAnnuel_detail" + t.getId() + ".pdf", "/jasper/taxAnnuel_detail.jasper");
    }

    public void create(Locale locale, int annee) {
        TaxeAnnuel taxeAnnuel = findByLocaleAndAnnee(locale, annee);
        System.out.println("search taxeAnnuel");
        if (taxeAnnuel == null) {
            System.out.println("searchinh nullllll");
            taxeAnnuel = new TaxeAnnuel();
            taxeAnnuel.setAnnee(annee);
            taxeAnnuel.setNbrTrimesterPaye(0);
            taxeAnnuel.setLocale(locale);
            taxeAnnuel.setTaxeTotale(0D);
            create(taxeAnnuel);
            System.out.println("tcriyat taxeannuell");
        }
    }

    public List<TaxeAnnuel> findTaxeAnnuelByCretere(Double montantMin, Double montantMax, int nombreTaxeMin, int nombreTaxetMax, String localeName, int annee) {
        String requete = "SELECT tax FROM TaxeAnnuel tax where 1=1";
        if (annee > 0) {
            requete += " AND tax.annee =" + annee;
        }
        if (!localeName.equals("")) {
            requete += " AND tax.locale.reference='" + localeName + "'";
        }
        if (nombreTaxeMin > 0) {
            requete += " AND tax.nbrTrimesterPaye >='" + nombreTaxeMin + "'";
        }
        if (nombreTaxetMax > 0) {
            requete += " AND tax.nbrTrimesterPaye <='" + nombreTaxetMax + "'";
        }
        requete += SearchUtil.addConstraintMinMax("tax", "taxeTotale", montantMin, montantMax);

        return em.createQuery(requete).getResultList();
    }

    public TaxeAnnuel findByLocaleAndAnnee(Locale locale, int annee) {
        String requette = "SELECT tax FROM TaxeAnnuel tax where 1=1";
        requette += " AND tax.annee =" + annee;
        requette += " AND tax.locale.id =" + locale.getId();
        List<TaxeAnnuel> list = em.createQuery(requette).getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public void delete(TaxeAnnuel taxeAnnuel) {
        if (taxeAnnuel != null && taxeAnnuel.getId() != null) {
            remove(taxeAnnuel);
        }
    }

    public void clone(TaxeAnnuel taxeAnnuelSource, TaxeAnnuel taxeAnnuelDestaination) {
        taxeAnnuelDestaination.setId(taxeAnnuelSource.getId());
        taxeAnnuelDestaination.setAnnee(taxeAnnuelSource.getAnnee());
        taxeAnnuelDestaination.setLocale(taxeAnnuelSource.getLocale());
        taxeAnnuelDestaination.setNbrTrimesterPaye(taxeAnnuelSource.getNbrTrimesterPaye());
        taxeAnnuelDestaination.setTaxeTotale(taxeAnnuelSource.getTaxeTotale());

    }

    public TaxeAnnuel clone(TaxeAnnuel taxeAnnuel) {
        TaxeAnnuel cloned = new TaxeAnnuel();
        clone(taxeAnnuel, cloned);
        return cloned;
    }

}
