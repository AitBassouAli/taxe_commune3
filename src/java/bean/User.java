/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author moulaYounes
 */
@Entity
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private String login;
    private String password;
    private String nom;
    private String prenom;
    private String email;
    private String tel;
    private int blocked;
    private int nbrCnx;
    private boolean admine;
    private boolean createUser;  //12/03/2017
    private boolean createTaxes;
    private boolean createRedevable;
    private boolean createLocale;
    private boolean createAdresse;
    private boolean createCtegorieTaux;
    @OneToMany(mappedBy = "user")
    private List<Device> devices;
    @ManyToOne
    private AnnexeAdministratif annexeAdministratif;

    public boolean isAdmine() {
        return admine;
    }

    public void setAdmine(boolean admiine) {
        this.admine = admiine;
    }

    public User(String login) {
        this.login = login;
    }

    public User() {

    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getBlocked() {
        return blocked;
    }

    public void setBlocked(int blocked) {
        this.blocked = blocked;
    }

    public int getNbrCnx() {
        return nbrCnx;
    }

    public void setNbrCnx(int nbrCnx) {
        this.nbrCnx = nbrCnx;
    }

    public boolean isCreateUser() {
        return createUser;
    }

    public void setCreateUser(boolean createUser) {
        this.createUser = createUser;
    }

    public boolean isCreateTaxes() {
        return createTaxes;
    }

    public void setCreateTaxes(boolean createTaxes) {
        this.createTaxes = createTaxes;
    }

    public boolean isCreateRedevable() {
        return createRedevable;
    }

    public void setCreateRedevable(boolean createRedevable) {
        this.createRedevable = createRedevable;
    }

    public boolean isCreateLocale() {
        return createLocale;
    }

    public void setCreateLocale(boolean createLocale) {
        this.createLocale = createLocale;
    }

    public boolean isCreateAdresse() {
        return createAdresse;
    }

    public void setCreateAdresse(boolean createAdresse) {
        this.createAdresse = createAdresse;
    }

    public boolean isCreateCtegorieTaux() {
        return createCtegorieTaux;
    }

    public void setCreateCtegorieTaux(boolean createCtegorieTaux) {
        this.createCtegorieTaux = createCtegorieTaux;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.login);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (!Objects.equals(this.login, other.login)) {
            return false;
        }
        return true;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public AnnexeAdministratif getAnnexeAdministratif() {
        return annexeAdministratif;
    }

    public void setAnnexeAdministratif(AnnexeAdministratif annexeAdministratif) {
        this.annexeAdministratif = annexeAdministratif;
    }

    @Override
    public String toString() {
        return login + "-" + nom + " " + prenom + "-" + email;
    }
}
