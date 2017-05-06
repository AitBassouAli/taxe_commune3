/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Device;
import bean.User;
import controler.util.SearchUtil;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author ayoub
 */
@Stateless
public class DeviceFacade extends AbstractFacade<Device> {

    @PersistenceContext(unitName = "projet_java_taxPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<Device> findAll() {
        return em.createQuery("SELECT d FROM Device d ORDER BY d.dateCreation DESC").getResultList();
    }

    public Device curentDevice(User user, Device device) {
        String rqt = "SELECT d FROM Device d WHERE 1=1";
        if (user != null) {
            rqt += SearchUtil.addConstraint("d", "user.login", "=", user.getLogin());
        }
        if (device != null) {
            rqt += " AND d.browser like '" + device.getBrowser() + "' and d.deviceCategorie like '" + device.getDeviceCategorie() + "' and d.operatingSystem like '" + device.getOperatingSystem() + "'";
        }
        System.out.println(rqt);
        return (Device) em.createQuery(rqt).getSingleResult();
    }

    public List<Device> search(User user, String browser, String categorie) {
        String rqt = "SELECT d FROM Device d WHERE 1=1";
        if (user != null) {
            rqt += SearchUtil.addConstraint("d", "user.login", "=", user.getLogin());
        }
        if (!browser.equals("-")) {
            if (browser.equals("autre")) {
                rqt += " AND d.browser not like 'chrome' and d.browser  not like 'firefox'";
            } else {
                rqt += SearchUtil.addConstraint("d", "browser", "=", browser);
            }
        }
        if (!categorie.equals("-")) {
            rqt += SearchUtil.addConstraint("d", "deviceCategorie", "=", categorie);
        }
        rqt += " ORDER BY d.dateCreation DESC";
        System.out.println(rqt);
        return em.createQuery(rqt).getResultList();
    }

    public int checkDevice(User user, Device connectedDevice) {
        List<Device> list = findDeviceByUtilisateur(user);
        if (list.isEmpty()) { //1ERE FOIS QUE CE USER CONNECT
            return 1;
        } else {
            for (int i = 0; i < list.size(); i++) {
                Device device = list.get(i);
                if (device.getDeviceCategorie().equals(connectedDevice.getDeviceCategorie())
                        && device.getBrowser().equals(connectedDevice.getBrowser())
                        && device.getOperatingSystem().equals(connectedDevice.getOperatingSystem())) {
                    return 2;
                }
            }
        }
        return 3;  //LIST PLEINE AND A MEW DEVICE
    }

    public void save(Device device, User user) {
        device.setUser(user);
        create(device);
    }

    public List<Device> findDeviceByUtilisateur(User user) {
        if (user == null || user.getLogin() == null) {
            return new ArrayList<>();
        } else {
            String req = "SELECT d FROM Device d WHERE d.user.login='" + user.getLogin() + "'";
            return em.createQuery(req).getResultList();
        }
    }

    public DeviceFacade() {
        super(Device.class);
    }

    public void clone(Device deviceSource, Device deviceDestaination) {
        deviceDestaination.setId(deviceSource.getId());

    }

    public Device clone(Device device) {
        Device cloned = new Device();
        clone(device, cloned);
        return cloned;
    }

}
