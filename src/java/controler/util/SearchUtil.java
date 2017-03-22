package controler.util;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import static controler.util.SearchUtil.decimalFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 *
 * @author HP
 */
public class SearchUtil {

    public static Date convertStringToDate(String date) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return simpleDateFormat.parse(date);
        } catch (ParseException ex) {
            return null;
        }

    }

    public static java.sql.Date convertUtilToSql(java.util.Date date) {
        if (date != null) {
            return new java.sql.Date(date.getTime());
        } else {
            return null;
        }
    }

    public static java.util.Date convertSqlToUtil(java.sql.Date date) {

        return new java.util.Date(date.getTime());
    }

    public static String addConstraint(String beanAbrev, String atributeName, String operator, Object value) {
        if (value != null) {
            return " AND " + beanAbrev + "." + atributeName + " " + operator + " '" + value + "'";
        }
        return "";
    }

    public static String addConstraintMinMax(String beanAbrev, String atributeName, Object valueMin, Object valueMax) {
        String requette = "";
        if (valueMin != null) {
            requette += " AND " + beanAbrev + "." + atributeName + " >= '" + valueMin + "'";
        }
        if (valueMax != null) {
            requette += " AND " + beanAbrev + "." + atributeName + " <= '" + valueMax + "'";
        }
        return requette;
    }

    public static String addConstraintDate(String beanAbrev, String atributeName, String operator, Date value) {
        return addConstraint(beanAbrev, atributeName, operator, convertUtilToSql(value));
    }

    public static String addConstraintMinMaxDate(String beanAbrev, String atributeName, Date valueMin, Date valueMax) {
        return addConstraintMinMax(beanAbrev, atributeName, convertUtilToSql(valueMin), convertUtilToSql(valueMax));
    }

    public static Date MyconvertStringToDate(String date) throws ParseException {
        if (!"".equals(date) && date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return simpleDateFormat.parse(date);
        } else {
            return null;
        }

    }
   
    public static String MyconvertDateToString(Date date) throws ParseException {
        if (date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            return simpleDateFormat.format(date);
        } else {
            return "";
        }

    }

    public static Date stringToDateHours(String d) throws ParseException {
        if (d != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            return simpleDateFormat.parse(d);
        } else {
            return null;
        }
    }

    public static Date localDateToDate(LocalDate date) throws ParseException {
        if (date != null) {
            return MyconvertStringToDate(date.toString());
        } else {
            return null;
        }
    }
     public static Optional<ButtonType> alerter(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait();
    }
     public static Double decimalFormat(Double number){
         DecimalFormat format=new DecimalFormat("#.##");
         return new Double(format.format(number));
     } 
}
