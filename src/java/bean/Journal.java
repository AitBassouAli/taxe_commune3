/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

/**
 *
 * @author ayoub
 */
@Entity
public class Journal implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateAction;
    private int type;   //1 delete 2 upate 3 create
    private String ancienValeur;
    private String nouvelleValeur;
    private String beanName;
    @ManyToOne
    private User user;
    @OneToOne
    private Device device;

    public Journal() {
    }

    public int getType() {
        return type;
    }

    public Journal(Date dateAction, int type, String beanName, User user, Device device, String nouvelleValeur) {
        this.dateAction = dateAction;
        this.type = type;
        this.beanName = beanName;
        this.user = user;
        this.device = device;
        this.nouvelleValeur = nouvelleValeur;
    }

    public Journal(Date dateAction, int type, String beanName, User user, String ancienValeur, Device device) {
        this.dateAction = dateAction;
        this.type = type;
        this.beanName = beanName;
        this.user = user;
        this.device = device;
        this.ancienValeur = ancienValeur;
    }

    public Journal(Date dateAction, int type, String ancienValeur, String nouvelleValeur, String beanName, User user) {
        this.dateAction = dateAction;
        this.type = type;
        this.ancienValeur = ancienValeur;
        this.nouvelleValeur = nouvelleValeur;
        this.beanName = beanName;
        this.user = user;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAncienValeur() {
        return ancienValeur;
    }

    public void setAncienValeur(String ancienValeur) {
        this.ancienValeur = ancienValeur;
    }

    public String getNouvelleValeur() {
        return nouvelleValeur;
    }

    public void setNouvelleValeur(String nouvelleValeur) {
        this.nouvelleValeur = nouvelleValeur;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateAction() {
        return dateAction;
    }

    public void setDateAction(Date dateAction) {
        this.dateAction = dateAction;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.id);
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
        final Journal other = (Journal) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

}
