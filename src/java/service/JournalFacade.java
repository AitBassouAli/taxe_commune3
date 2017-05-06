/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Device;
import bean.Journal;
import bean.User;
import controler.util.DeviceUtil;
import controler.util.SearchUtil;
import controler.util.SessionUtil;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author ayoub
 */
@Stateless
public class JournalFacade extends AbstractFacade<Journal> {

    @PersistenceContext(unitName = "projet_java_taxPU")
    private EntityManager em;
    @EJB
    DeviceFacade deviceFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public JournalFacade() {
        super(Journal.class);
    }

    @Override
    public List<Journal>findAll(){
        return em.createQuery("SELECT j FROM Journal j ORDER BY j.dateAction DESC").getResultList();
    }
    public void updateDevice(Device device) {
        String rqt = "UPDATE Journal j set j.device = " + null + " WHERE j.device.id =" + device.getId();
        System.out.println(rqt);
        em.createQuery(rqt).executeUpdate();
    }

    public void journalUpdate(String beanName, int type, Object old, Object neew) {
        Journal journal;
        User user = SessionUtil.getConnectedUser();
        switch (type) {
            case 1:
                journal = new Journal(new Date(), type, beanName, user, deviceFacade.curentDevice(user, DeviceUtil.getDevice()), (String) neew);
                break;
            case 2:
                journal = new Journal(new Date(), type, (String) old, (String) neew, beanName, user, deviceFacade.curentDevice(user, DeviceUtil.getDevice()));
                break;
            default:
                journal = new Journal(new Date(), type, beanName, user, (String) old, deviceFacade.curentDevice(user, DeviceUtil.getDevice()));
                break;
        }
        create(journal);

    }

    public List<Journal> journaleSearch(Date dateMin, Date dateMax, int type, String beanName) {
        String requete = "SELECT jo FROM Journal jo WHERE 1=1  ";
        requete += SearchUtil.addConstraintMinMaxDate("jo", "dateAction", dateMin, dateMax);
        if (!beanName.equals("")) {
            requete += " AND jo.beanName='" + beanName + "'";
        }
        if (type < 4 && type > 0) {
            requete += " AND jo.type=" + type;
        }
        requete += " ORDER BY jo.dateAction DESC";
        return em.createQuery(requete).getResultList();
    }

    public void clone(Journal journalSource, Journal journalDestaination) {
        journalDestaination.setId(journalSource.getId());

    }

    public Journal clone(Journal journal) {
        Journal cloned = new Journal();
        clone(journal, cloned);
        return cloned;
    }
}
