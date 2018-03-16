/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import service.*;
import bean.DateDernierDelai;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author ushiho
 */
@Stateless
public class DateDernierDelaiFacade extends AbstractFacade<DateDernierDelai> {

    @PersistenceContext(unitName = "TaxeGOVMAPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DateDernierDelaiFacade() {
        super(DateDernierDelai.class);
    }

    public DateDernierDelai findDatePaiementByAccompte(int accompte) {
        String req = "SELECT d FROM DateDernierDelai d WHERE d.type = '1' AND d.accompteAverse = '" + accompte + "'";
        return getUniqueResult(req);
    }
}
