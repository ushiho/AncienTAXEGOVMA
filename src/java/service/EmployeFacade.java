/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Email;
import util.EmailUtil;
import bean.Societe;
import bean.Employe;
import java.util.PrimitiveIterator;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.commonutils.util.SHA1Hash;
import util.PassUtil;

/**
 *
 * @author ushiho
 */
@Stateless
public class EmployeFacade extends AbstractFacade<Employe> {

    private PrimitiveIterator.OfDouble randomIterator;
    private SHA1Hash sHA1Hash;
    private PassUtil passUtil;
    private EmailUtil emailUtil;

    @PersistenceContext(unitName = "TaxeGOVMAPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @EJB
    private SocieteFacade societeFacade;
    @EJB
    private EmployeFacade utilisateurFacade;
    @EJB
    private EmailFacade emailFacade;

    public EmployeFacade() {
        super(Employe.class);
    }

    public int sAdherer(Employe contribuable) {
        if ((contribuable.getLogin()).length() == 12) {
            return testIDFiscalEtPass(contribuable);
        }
        return -3;
    }

    private int testIDFiscalEtPass(Employe contribuable) {
        Societe societe = societeFacade.find(contribuable.getLogin());
        if (societe == null) {
            return -1;
        } else if (!passUtil.testTwoPasswords(contribuable.getMotDePasse(), societe.getPassword())) {
            return -2;
        }
        return 1;
    }

    public int seConnecter(Employe utilisateur) {
        Employe existe = findByLogin(utilisateur.getLogin());
        if (existe == null) {
            return -1;
        } else if (!passUtil.testTwoPasswords(utilisateur.getMotDePasse(), existe.getMotDePasse())) {
            return -2;
        } else {
            return 1;
        }
    }

    public int save(Employe utilisateur) {
        if (utilisateur == null) {
            return -1;
        }
        create(utilisateur);
        return 1;
    }

    public int addUtilisateur(Employe utilisateur, Employe contribuable) {
        if (testParams(utilisateur, contribuable)) {
            return -1;
        }
        setPassAndLogin(utilisateur);
        Email email = emailFacade.creerMsgGenererPass(utilisateur.getLogin(), utilisateur.getMotDePasse(), 1);
        if (emailUtil.sendEmail(email, utilisateur) < 0) {
            return -2;
        }
        create(utilisateur);
        return 1;

    }

    private void setPassAndLogin(Employe utilisateur) {
        String login = passUtil.generate(12, 1);
        String pass = passUtil.generatePassAndHash(6, 4);
        while (findByLogin(login) != null) {
            login = passUtil.generatePassAndHash(12, 1);
        }
        utilisateur.setMotDePasse(pass);
        utilisateur.setLogin(login);
    }

    private boolean testParams(Employe utilisateur, Employe contribuable) {
        return utilisateur == null || contribuable == null || contribuable.getDroitFiscale() != 0;
    }

    public int modify(Employe nvUtilisateur, Employe anUtilisateur) {
        if (nvUtilisateur == null || find(anUtilisateur.getId()) == null) {
            return -1;
        }
        anUtilisateur = new Employe(nvUtilisateur.getNom(), nvUtilisateur.getpNom(), nvUtilisateur.getCIN(),
                nvUtilisateur.getNumTele(), nvUtilisateur.getEmail(), nvUtilisateur.getProfession());
        edit(anUtilisateur);
        return 1;
    }

    public int resetPassword(Employe utilisateur) {
        if (find(utilisateur.getId()) == null) {
            return -1;
        }
        utilisateur.setMotDePasse(passUtil.generatePassAndHash(6, 4));
        Email email = emailFacade.creerMsgGenererPass(utilisateur.getLogin(), utilisateur.getMotDePasse(), 3);
        if (emailUtil.sendEmail(email, utilisateur) < 0) {
            return -1;
        }
        return 1;
    }

    public int deleteFromSimplSer(Employe contribuable, Employe utilisateur) {
        if (testParams(find(utilisateur.getId()), contribuable)) {
            return -1; //c'est pas un contri , 
        }
        remove(utilisateur);
        return 1;
    }

    public String droit(int num) {
        String droit;
        switch (num) {
            case 0:
                droit = "redacteur";
                break;
            case 1:
                droit = "responsable de validation";
                break;
            case 2:
                droit = "responsable de paiement";
                break;
            default:
                droit = null;
                break;
        }
        return droit;
    }

    public Employe findByLogin(String login) {
        return getUniqueResult("SELECT u FROM Utilisateur u WHERE u.login like '" + login + "'");
    }

    public int deleteBySociete(Societe societe) {
        if (societe != null) {
            return em.createQuery("DELETE FROM Utilisateur u WHERE u.societe.idFiscal ='" + societe.getIdFiscal() + "'").executeUpdate();
        }
        return -1;
    }

}
