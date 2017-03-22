/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controler.util;

import bean.TaxeTrim;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Younes
 */
public class DateUtil {

    
    public  static int calculerMois(TaxeTrim taxeTrim,int annee){
        int mois=getMonthsBetweenTwoDates(getFinTrimestre(taxeTrim.getNumeroTrim(), annee),new Date());
        if(mois <0 ){
            mois=0;
        }
        return mois;
    }
    
    
       public  static  int getMonthsBetweenTwoDates(Date d1,Date d2){
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(d1);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(d2);

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
        return diffMonth;

    }
    
    
    
    public static List<int[]> nextTrim(Date dateEnvoi, int dernierTrimestre, int dernierAnnee) {
        List<int[]> res = new ArrayList<>();
        int trim = getTrimestre(dateEnvoi);
        int annee = dateEnvoi.getYear() + 1900;
        int[] next = {dernierTrimestre, dernierAnnee};
        res.add(next);
        while (true) {
            next = findNext(next[0], next[1]);
            if (next[0] == trim && next[1] == annee) {
                if(res.isEmpty()){
                    System.out.println("hahhhowa mol l mochkile ==> dernierTrimestre ="+dernierTrimestre+" dernierAnnee"+dernierAnnee);
                }
                return res;
            } else {
                res.add(next);
            }
        }
    }
    
     public static Date getDateFinValidation (Date dateDebut, int duree) {
        Date dateFin = dateDebut;
        dateFin.setYear(dateFin.getYear()+duree);
        System.out.println("daaaaate"+dateFin);
        return dateFin;
    }
    
    public static Long getDifferenceDate(Date dateDebut, Date dateFin) {
        final Long millisecParAnnee = (1000l * 60l * 60l * 24l * 12l);
        int duree = 0;
        if (dateDebut == null) {
            return -1l;
        } else if (dateFin == null) {
            return -2l;
        }

        Long diff =  Math.abs(dateFin.getTime() - dateDebut.getTime());
        System.out.println("hahia louwla getTime " + dateDebut.getTime());
        System.out.println("hahia tania getTime " + dateFin.getTime());
        Long diffEnJour = diff / millisecParAnnee;
        return diffEnJour;

    }

    public static int[] findNext(int trimestre, int annee) {
        int nextTrimestre, nextAnnee;
        if (trimestre == 4) {
            nextTrimestre = 1;
            nextAnnee = annee + 1;
        } else {
            nextTrimestre = trimestre + 1;
            nextAnnee = annee;
        }

        return new int[]{nextTrimestre, nextAnnee};
    }

    public static String findInDate(Date dateMax, Date dateMin, String bean, String dateAttributeName) {
        return findInDate(dateMax, dateMin, bean, dateAttributeName, "<=", ">=");
    }

    public static String findInDate(Date dateMax, Date dateMin, String bean, String dateAttributeName, String operation1, String operation2) {
        String requette = "";
        if (dateMax != null) {
            requette += " AND " + bean + "." + dateAttributeName + " " + operation1 + " '" + getSqlDate(dateMax) + "'";
        }
        if (dateMin != null) {
            requette += " AND " + bean + "." + dateAttributeName + " " + operation2 + " '" + getSqlDate(dateMin) + "'";
        }
        return requette;
    }

    public static Date getDateTauxTaxeAnuelle(int annee) {
        Calendar c1 = GregorianCalendar.getInstance();
        c1.set(annee + 1, Calendar.JANUARY, 01);
        return c1.getTime();
    }

    public static int getTrimestre(Date d) {
        if (d.getMonth() + 1 >= 1 && d.getMonth() + 1 < 4) {
            return 1;
        } else if (d.getMonth() + 1 >= 4 && d.getMonth() + 1 < 7) {
            return 2;

        } else if (d.getMonth() + 1 >= 7 && d.getMonth() + 1 < 10) {
            return 3;
        } else {
            return 4;
        }
    }

    public static int getTrimestreAPaye() {
        Date d = new Date();
        if (d.getMonth() + 1 >= 1 && d.getMonth() + 1 < 4) {
            return 4;
        } else if (d.getMonth() + 1 >= 4 && d.getMonth() + 1 < 7) {
            return 1;

        } else if (d.getMonth() + 1 >= 7 && d.getMonth() + 1 < 10) {
            return 2;
        } else {
            return 3;
        }
    }

    public static Date getDateTauxTaxe(int trimestre, int annee) {
        Calendar c1 = GregorianCalendar.getInstance();
        if (trimestre == 1) {
            c1.set(annee, Calendar.JANUARY, 01);  //January 30th 2000
        }
        if (trimestre == 2) {
            c1.set(annee, Calendar.APRIL, 01);  //January 30th 2000
        }
        if (trimestre == 3) {
            c1.set(annee, Calendar.JULY, 01);  //January 30th 2000
        }
        if (trimestre == 4) {
            c1.set(annee, Calendar.OCTOBER, 01);  //January 30th 2000
        }
        return c1.getTime();
    }

    public static Date getDebutMois(int mois, int annee) {
        return getDebutMoisCalendar(mois, annee).getTime();
    }

    private static Calendar getDebutMoisCalendar(int mois, int annee) {
        Calendar c1 = GregorianCalendar.getInstance();

        switch (mois) {
            case 1:
                c1.set(annee, Calendar.JANUARY, 01);  //January 30th 2000
                break;
            case 2:
                c1.set(annee, Calendar.FEBRUARY, 01);  //January 30th 2000
                break;
            case 3:
                c1.set(annee, Calendar.MARCH, 01);  //January 30th 2000
                break;
            case 4:
                c1.set(annee, Calendar.APRIL, 01);  //January 30th 2000
                break;
            case 5:
                c1.set(annee, Calendar.MAY, 01);  //January 30th 2000
                break;
            case 6:
                c1.set(annee, Calendar.JUNE, 01);  //January 30th 2000
                break;
            case 7:
                c1.set(annee, Calendar.JULY, 01);  //January 30th 2000
                break;
            case 8:
                c1.set(annee, Calendar.AUGUST, 01);  //January 30th 2000
                break;
            case 9:
                c1.set(annee, Calendar.SEPTEMBER, 01);  //January 30th 2000
                break;
            case 10:
                c1.set(annee, Calendar.OCTOBER, 01);  //January 30th 2000
                break;
            case 11:
                c1.set(annee, Calendar.NOVEMBER, 01);  //January 30th 2000
                break;
            case 12:
                c1.set(annee, Calendar.DECEMBER, 01);  //January 30th 2000
                break;
            default:
                break;
        }
        return c1;
    }

    public static Date getFinMois(int mois, int annee) {
        Calendar c1 = getDebutMoisCalendar(mois, annee);
        c1.set(Calendar.DAY_OF_MONTH, c1.getActualMaximum(Calendar.DAY_OF_MONTH));
        return c1.getTime();
    }

    public static Date getDelaiAttestationPaiement(int trimestre, int annee) {
        int myAnnee = annee;
        int myTrimestre = trimestre;
        if (trimestre == 4) {
            myAnnee++;
            myTrimestre = 1;
        } else {
            myTrimestre++;
        }
        return getFinTrimestre(myTrimestre, myAnnee);
    }

    public static Date getFinTrimestre(int trimestre, int annee) {
        Calendar c1 = GregorianCalendar.getInstance();
        switch (trimestre) {
            case 1:
                c1.set(annee, Calendar.MARCH, 31);  //January 30th 2000
                break;
            case 2:
                c1.set(annee, Calendar.JUNE, 30);  //January 30th 2000
                break;
            case 3:
                c1.set(annee, Calendar.SEPTEMBER, 30);  //January 30th 2000
                break;
            case 4:
                c1.set(annee, Calendar.DECEMBER, 31);  //January 30th 2000
                break;
            default:
                break;
        }
        return c1.getTime();
    }

    public static int DateFinTrimestre(int trimestre, Date date, int annee) {
        Calendar c1 = GregorianCalendar.getInstance();
        c1.setTime(getFinTrimestre(trimestre, annee));
        System.out.println("haaa date presentation ==> " + formateDate(date));
        System.out.println("haaa date fin trim ==> " + formateDate(c1.getTime()));
        if (date.getTime() >= c1.getTime().getTime()) {
            return 1;
        }
        return -1;

    }

    public static int differenceEnMois(Date date, int annee, int numeroTrimestre, int nbrPourCalculerRetard) {
        int nombreMois = 0, nombreAnnee = 0, dephasage = 0;
        nombreAnnee = (date.getYear() + 1900) - annee;
        System.out.println(" haaaaaa date : " + date);
        System.out.println(" haaaaaa annee : " + annee);
        System.out.println(" haaaaaa num trim : " + numeroTrimestre);
        System.out.println(" haaaaaa nbr pour calculer retard : " + nbrPourCalculerRetard);
        switch (numeroTrimestre) {
            case 1:
                dephasage = 3;
                break;
            case 2:
                dephasage = 6;
                break;
            case 3:
                dephasage = 9;
                break;
            case 4:
                dephasage = 12;
                break;
            default:
                break;
        }
        nombreMois = (date.getMonth() + 1) - (dephasage + nbrPourCalculerRetard);

        return nombreAnnee * 12 + nombreMois;

    }

    public static int differenceEnMois(Date datePresentation, int annee) {
        int nombreMois = 0, nombreAnnee = 0;

        nombreAnnee = (datePresentation.getYear() + 1900) - annee;
        if (nombreAnnee > 0) {
            nombreAnnee--;
            nombreMois = (datePresentation.getMonth() + 1);
            return nombreAnnee * 12 + nombreMois;
        }

        return -1;

    }

    public static Date dateRetard(int nbrPourCalculerRetard, int annee) {
        Date date = new Date();
        if (nbrPourCalculerRetard == 12) {
            nbrPourCalculerRetard = 0;
        }
        date.setMonth(nbrPourCalculerRetard - 1);
        date.setYear(annee + 1 - 1900);
        return date;
    }

    public static Date dateRetardTrimestrielle(int nbrPourCalculerRetard, int annee, int numeroTrimestre) {
        Date date = new Date();
        date.setDate(1);
        int dephasage = 0;
        switch (numeroTrimestre) {
            case 1:
                dephasage = 3;
                break;
            case 2:
                dephasage = 6;
                break;
            case 3:
                dephasage = 9;
                break;
            case 4:
                dephasage = 0;
                annee += 1;
                break;
            default:
                break;
        }

        date.setMonth(nbrPourCalculerRetard + dephasage);
        date.setYear(annee - 1900);
        return date;
    }

    public static java.sql.Date getSqlDate(java.util.Date date) {
        return new java.sql.Date(date.getTime());
    }

    public static java.sql.Timestamp convertFromDateToTimestamp(java.util.Date date) {
        return new java.sql.Timestamp(date.getTime());
    }

    public static java.sql.Timestamp getSqlDateTime(java.util.Date date) {
        return new java.sql.Timestamp(date.getTime());
    }

    public static String getYearOfCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        return simpleDateFormat.format(new Date());
    }

    public static String formateDate(String pattern, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    public static Date StringToDate(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return sdf.parse(strDate);
        } catch (ParseException ex) {
            Logger.getLogger(DateUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Date StringToDate2(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(strDate);
        } catch (ParseException ex) {
            Logger.getLogger(DateUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String formateDate(Date date) {
        return formateDate("dd/MM/yyyy", date);
    }

    public static int differenceEntreDeuxTrimestres(int anneeFin, int trimFin, int anneeDeb, int trimDeb) {
        return (anneeFin - anneeDeb) * 4 + (trimFin - trimDeb);
    }

    public static List<int[]> calculNbreMoisRetardPourTrimestres(Date dateEnvoi, int trimDernierPaiement, int anneeDernierPaiement, int nombreMoisPourCalculerRetard) {
        List<int[]> nbreMoisRetards = new ArrayList();
        List<int[]> nextTrims = nextTrim(dateEnvoi, trimDernierPaiement, anneeDernierPaiement);
        int oldAnnee = nextTrims.get(0)[1];
        int somme = 0;
        int nbrePremierMois = 0;
        for (int[] nextTrim : nextTrims) {
            if (oldAnnee != nextTrim[1]) {
                nbreMoisRetards.add(new int[]{oldAnnee, somme, nbrePremierMois});
                somme = 0;
                oldAnnee = nextTrim[1];
                nbrePremierMois = 0;
            }
            nbrePremierMois++;
            somme += differenceEnMois(dateEnvoi, nextTrim[1], nextTrim[0], nombreMoisPourCalculerRetard);
            System.out.println("pour " + nextTrim[0] + "/" + nextTrim[1] + " ==> " + differenceEnMois(dateEnvoi, nextTrim[1], nextTrim[0], nombreMoisPourCalculerRetard) + " ; nbr " + nbrePremierMois);

        }
        nbreMoisRetards.add(new int[]{oldAnnee, somme, nbrePremierMois});
        return nbreMoisRetards;
    }
}
