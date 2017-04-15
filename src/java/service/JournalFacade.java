/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

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
//     public  String deconnection(){
//        User user=SessionUtil.getConnectedUser();
//        SessionUtil.unSetUser(user);
//        selected=new Historique(new Date(),2,user);
//        ejbFacade.create(selected);
//        return "/faces/index";
//    }

    public void journalUpdate(String beanName, int type, Object old, Object neew) {
        Journal journal;
        User user = SessionUtil.getConnectedUser();
        switch (type) {
            case 1:
                journal = new Journal(new Date(), type, beanName, user, deviceFacade.curentDevice(user, DeviceUtil.getDevice()), neew.toString());
                break;
            case 2:
                journal = new Journal(new Date(), type, old.toString(), neew.toString(), beanName, user);
                break;
            default:
                journal = new Journal(new Date(), type, beanName, user, old.toString(), deviceFacade.curentDevice(user, DeviceUtil.getDevice()));
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
