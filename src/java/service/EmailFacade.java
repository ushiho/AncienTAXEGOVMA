/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Email;
import bean.Employe;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author ushiho
 */
@Stateless
public class EmailFacade extends AbstractFacade<Email> {

    @PersistenceContext(unitName = "TaxeGOVMAPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EmailFacade() {
        super(Email.class);
    }

    public Email creerMsgGenererPass(Employe employe) {
        Email email = findByType(2);
        email.setContenu(email.getContenu()
                + ", Votre login :'" + employe.getLogin() + "' , mot de passe : '" + employe.getMotDePasse() + "'");
        return email;
    }

    public Email creerMsgResetPass(Employe employe) {
        Email email = findByType(3);
        email.setContenu(email.getContenu()
                + ", Votre login :'" + employe.getLogin() + "' , mot de passe restauré : '" + employe.getMotDePasse() + "'");
        return email;
    }

    public Email findByType(int type) {
        return getUniqueResult("SELECT e FROM Email e WHERE e.type = '" + type + "'");
    }
}
