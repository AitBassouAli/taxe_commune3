/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

/**
 *
 * @author ayoub
 */
@Entity
public class Historique implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dateConnection;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dateDeconnection;
    private int type;//1 cnx 2 decnx
    @OneToOne
    private User user;
    @OneToOne
    private Device device;

    public Historique(Date dateConnection, int type, User user) {
        this.dateConnection = dateConnection;
        this.type = type;
        this.user = user;
    }

    public Historique(int type, Date dateDeconnection, User user) {
        this.dateDeconnection = dateDeconnection;
        this.type = type;
        this.user = user;
    }

    public Historique() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getDateConnection() {
        return dateConnection;
    }

    public void setDateConnection(Date dateConnection) {
        this.dateConnection = dateConnection;
    }

    public Date getDateDeconnection() {
        return dateDeconnection;
    }

    public void setDateDeconnection(Date dateDeconnection) {
        this.dateDeconnection = dateDeconnection;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Historique)) {
            return false;
        }
        Historique other = (Historique) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "History" + id + "";
    }

}
