/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Societe;
import bean.Employe;
import java.util.PrimitiveIterator;
import java.util.Random;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.commonutils.util.SHA1Hash;

/**
 *
 * @author ushiho
 */
@Stateless
public class EmployeFacade extends AbstractFacade<Employe> {

    private PrimitiveIterator.OfDouble randomIterator;
    private SHA1Hash sHA1Hash;
    private static final String ALPHA_CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHA = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMERIC = "0123456789";

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
    private SendEmail sendEmail;

    public EmployeFacade() {
        super(Employe.class);
        randomIterator = new Random().doubles(10).iterator();
    }

    public double newGenerate() {
        return randomIterator.nextDouble();
    }

    public int seConnecter(Employe utilisateur) {
        if ((utilisateur.getLogin() + "").length() == 10) {
            //donc cet un idFiscal et pas un tel login pour un utili qcq
            long idFiscal = utilisateur.getLogin();
            Societe societe = societeFacade.find(idFiscal);
            if (societe == null) {
                return -1;
                //cette societe n'existe pas !
                //les soci sont deja enregistrer ds la bd de DGI
            } else if (!sHA1Hash.getStringHash(utilisateur.getMotDePasse()).equals(sHA1Hash.getStringHash(societe.getPassword()))) {
                return -2;
                //le mot de pass entreé par contribuable 
                //c'est pas le m generee et existe dans
                //la BD de DGI 
            } else {
                return 1;//c est un contribuable
            }
        } else {
            Employe existe = findByLogin(utilisateur.getLogin());
            if (existe == null) {
                return -1;
            } else if (!sHA1Hash.getStringHash(utilisateur.getMotDePasse()).equals(sHA1Hash.getStringHash(utilisateur.getMotDePasse()))) {
                return -2;
            } else {
                return 2;//c est un utili normal
            }
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
        if (utilisateur == null || contribuable == null || contribuable.getDroitFiscale() != 0) {
            return -1; //vous n avez pas le droit
        } else {
            //service pour generer passs + login + envoyer vers email + creation ds BD
            Long login = new Long(sendEmail.generatePassword(12, NUMERIC));
            String pass = sendEmail.generatePassword(6, ALPHA + ALPHA_CAPS + NUMERIC);
            while (findByLogin(login) != null) {
                login = new Long(sendEmail.generatePassword(12, NUMERIC));
            }
            String message = "Salut ,Mr " + utilisateur.getNom() + " , le Mr " + contribuable.getNom() + " vous ajoutée comme "
                    + " un " + droit(utilisateur.getDroitFiscale()) + " , à son compte Simpl-Téléservices , Alors votre "
                    + " login est '" + login + "' et le mot de passe est '" + pass + "'";
            utilisateur.setMotDePasse(sHA1Hash.getStringHash(pass));
            utilisateur.setLogin(login);
            if (sendEmail.sendEmail(utilisateur.getEmail(), "Ajout au Simpl-Téléservices", message) < 0) {
                return -2;
            }
            create(utilisateur);
            return 1;
        }
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
        String pass = sendEmail.generatePassword(6, NUMERIC);
        String message = "Salut Mr " + utilisateur.getNom() + " vous avez demander de restaurer votre mot de passe ,"
                + " Voila le nouveau c'est : '" + pass + "'";
        if (sendEmail.sendEmail(utilisateur.getEmail(), "Changemant de mot de passe", message) < 0) {
            return -1;
        }
        return 1;
    }

    public int deleteFromSimplSer(Employe contribuable, Employe utilisateur) {
        if (contribuable == null || contribuable.getDroitFiscale() != 0 || find(utilisateur.getId()) == null) {
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

    public Employe findByLogin(Long login) {
        String req = "SELECT u FROM Utilisateur u WHERE u.login like '" + login + "'";
        return getUniqueResult(req);
    }

    public int deleteBySociete(Societe societe) {
        if (societe != null) {
            return em.createQuery("DELETE FROM Utilisateur u WHERE u.societe.idFiscal ='" + societe.getIdFiscal() + "'").executeUpdate();
        }
        return -1;
    }

}
