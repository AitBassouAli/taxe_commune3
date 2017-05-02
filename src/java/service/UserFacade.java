/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.AnnexeAdministratif;
import bean.Device;
import bean.Secteur;
import bean.User;
import controler.util.EmailUtil;
import controler.util.HashageUtil;
import controler.util.JsfUtil;
import controler.util.RandomStringUtil;
import controler.util.SearchUtil;
import controler.util.SessionUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Younes
 */
@Stateless
public class UserFacade extends AbstractFacade<User> {

    @PersistenceContext(unitName = "projet_java_taxPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    @EJB
    DeviceFacade deviceFacade;

    public UserFacade() {
        super(User.class);
    }

    @Override
    public User find(Object id) {
        try {
            User user = (User) em.createQuery("select u from User u where u.login = '" + id + "'").getSingleResult();
            if (user != null) {
                return user;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public List<User> findByCreteria(User selected, Secteur secteur) {
        String requette = "SELECT u FROM User u WHERE 1=1";
        if (selected.getBlocked() > 0) {
            requette += SearchUtil.addConstraint("u", "blocked", "=", selected.getBlocked());
        }
        if (!selected.getNom().equals("")) {
            requette += SearchUtil.addConstraint("u", "nom", "=", selected.getNom());
        }
         if (!selected.getEmail().equals("")) {
            requette += SearchUtil.addConstraint("u", "email", "LIKE", "%"+selected.getEmail()+"%");
        }
        if (selected.getAnnexeAdministratif() == null) {
            if (secteur != null) {
                requette += SearchUtil.addConstraint("u", "annexeAdministratif.secteur.id", "=", secteur.getId());
            }
        } else {
            requette += SearchUtil.addConstraint("u", "annexeAdministratif.id", "=", selected.getAnnexeAdministratif().getId());
        }
        System.out.println(requette);
        return em.createQuery(requette).getResultList();
    }

    public User findByEmail(String email) {
        try {
            User user = (User) em.createQuery("select u from User u where u.email LIKE '" + email + "'").getSingleResult();
            if (user != null) {
                return user;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public int sendPW(String email) {
        User user = findByEmail(email);
        if (user == null) {
            return -1;
        } else {
            String pw = RandomStringUtil.generate();
            String msg = "Bienvenu Mr. " + user.getNom() + ",<br/>"
                    + "D'après votre demande de reinitialiser le mot de passe de votre compte TaxeSejour, nous avons generer ce mot de passe pour vous.\n"
                    + "<br/><br/>"
                    + "      Nouveau Mot de Passe: <br/><center><b>"
                    + pw
                    + "</b></center><br/><br/><b><i>PS:</i></b>  SVP changer ce mot de passe apres que vous avez connecter pour des raison du securité .<br/> Cree votre propre mot de passe";
            try {
                EmailUtil.sendMail("pfetaxe@gmail.com", "taxeCommune", msg, email, "Demande de reanitialisation du mot de pass");
            } catch (MessagingException ex) {
                //  Logger.getLogger(UserFacade.class.getName()).log(Level.SEVERE, null, ex);
                return -2;
            }

            user.setBlocked(0);
            user.setPassword(HashageUtil.sha256(pw));
            edit(user);
            return 1;
        }
    }

    public int changePassword(String login, String oldPassword, String newPassword, String newPasswordConfirmation) {
        System.out.println("voila hana dkhalt le service verifierPassword");
        User loadedeUser = find(login);

        if (!newPasswordConfirmation.equals(newPassword)) {
            return -1;
        } else if (!loadedeUser.getPassword().equals(HashageUtil.sha256(oldPassword))) {
            return -2;
        } else if (oldPassword.equals(newPassword)) {
            return -3;
        }
        loadedeUser.setPassword(HashageUtil.sha256(newPassword));
        edit(loadedeUser);
        return 1;
    }

    public void changeData(User user) {
        User loadedUser = find(user.getLogin());
        cloneData(user, loadedUser);
        edit(loadedUser);
    }

    public void cloneData(User userSource, User userDestination) {
        userDestination.setNom(userSource.getNom());
        userDestination.setPrenom(userSource.getPrenom());
        userDestination.setTel(userSource.getTel());
        userDestination.setEmail(userSource.getEmail());
    }

    public Object[] seConnecter(User user, Device device) {
        if (user == null || user.getLogin() == null) {
            JsfUtil.addErrorMessage("Veuilliez saisir votre login");
            return new Object[]{-5, null};
        } else {
            User loadedUser = find(user.getLogin());
            if (loadedUser == null) {
                return new Object[]{-4, null};
            } else if (!loadedUser.getPassword().equals(HashageUtil.sha256(user.getPassword()))) {
                if (loadedUser.getNbrCnx() < 3) {
                    System.out.println("hana loadedUser.getNbrCnx() < 3 ::: " + loadedUser.getNbrCnx());
                    loadedUser.setNbrCnx(loadedUser.getNbrCnx() + 1);
                    edit(loadedUser);
                    return new Object[]{-7, null};
                } else {//(loadedUser.getNbrCnx() >= 3)
                    System.out.println("hana loadedUser.getNbrCnx() >= 3::: " + loadedUser.getNbrCnx());
                    loadedUser.setBlocked(1);
                    edit(loadedUser);
                    return new Object[]{-3, null};
                }
            } else if (loadedUser.getBlocked() == 1) {
                JsfUtil.addErrorMessage("Cet utilisateur est bloqué");
                return new Object[]{-2, null};
            } else {
                loadedUser.setNbrCnx(0);
                edit(loadedUser);
                user = clone(loadedUser);
                user.setPassword(null);
                int resDevice = deviceFacade.checkDevice(loadedUser, device);
                device.setDateCreation(new Date());
                switch (resDevice) {
                    case 3:
                        deviceFacade.save(device, loadedUser);
                        return new Object[]{3, loadedUser};
                    case 1:
                        deviceFacade.save(device, loadedUser);
                        return new Object[]{1, loadedUser};
                    default:
                        return new Object[]{2, loadedUser};
                }
            }
        }
    }

    public List<User> findByAnnexe(User connectedUser) {
        AnnexeAdministratif annexeAdministratif=find(connectedUser.getLogin()).getAnnexeAdministratif();
        if (annexeAdministratif != null && annexeAdministratif.getId() != null) {
            return em.createQuery("SELECT u FROM User u WHERE  u.annexeAdministratif.id=" + annexeAdministratif.getId()).getResultList();
        }
        return new ArrayList();
    }

    public User clone(User user) {
        User clone = new User();
        clone.setLogin(user.getLogin());
        clone.setBlocked(user.getBlocked());
        clone.setNbrCnx(user.getNbrCnx());
        clone.setNom(user.getNom());
        clone.setPassword(user.getPassword());
        clone.setPrenom(user.getPrenom());
        clone.setTel(user.getTel());
        clone.setAdmine(user.isAdmine());
        return clone;
    }

    @Override
    public void create(User user) {
        user.setAnnexeAdministratif(SessionUtil.getCurrentAnnexe());
        super.create(user);
        SessionUtil.getCurrentAnnexe().getUsers().add(user);

    }

    public String findLogin(User user) {
        return (String) em.createQuery("SELECT u.login FROM User u WHERE u.login='" + user.getLogin() + "'").getSingleResult();
    }

    //-----------------------------------------------------------------------------------
    public Object[] addUser(User user) {

        if ("".equals(user.getLogin()) || user.getLogin() == null) {
            return new Object[]{-1, null};
        } else if ("".equals(user.getPassword()) || user.getPassword() == null) {
            return new Object[]{-2, null};
        }
        User loadedUser = find(user.getLogin());
        if (loadedUser != null) {
            return new Object[]{-3, null};
        } else {
            user.setNbrCnx(0);
            user.setBlocked(0);
            user.setPassword(HashageUtil.sha256(user.getPassword()));
            create(user);
            return new Object[]{1, user};
        }

    }

}
