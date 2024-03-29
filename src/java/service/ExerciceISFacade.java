/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.ExerciceIS;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.DateUtil;

/**
 *
 * @author ushiho
 */
@Stateless
public class ExerciceISFacade extends AbstractFacade<ExerciceIS> {

    @PersistenceContext(unitName = "TaxeGOVMAPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExerciceISFacade() {
        super(ExerciceIS.class);
    }

    public int save(List<ExerciceIS> exerciceISs) {
        if (testExercices(exerciceISs) < 0) {
            return -1;
        }
        for (int i = 0; i < exerciceISs.size(); i++) {
            ExerciceIS exerciceIS = exerciceISs.get(i);
            create(exerciceIS);
        }
        return 1;
    }

    public int testExercices(List<ExerciceIS> exerciceISs) {
        if (exerciceISs == null || exerciceISs.isEmpty()) {
            return -1;
        }
        for (int i = 0; i < exerciceISs.size(); i++) {
            ExerciceIS exerciceIS = exerciceISs.get(i);
            if (testParams(exerciceIS)<0) {
                return -2;
            }
        }
        return 1;
    }

    public int testParams(ExerciceIS exerciceIS) {
        if (exerciceIS.getCharges() < 0 || exerciceIS.getNonDeductibles() < 0 || exerciceIS.getDeductibles() < 0
                || exerciceIS.getProduits() < 0 || exerciceIS.getDateDebut().compareTo(exerciceIS.getDateFin()) > 0
                || DateUtil.compareTwoDates(exerciceIS.getDateDebut(), exerciceIS.getDateFin()) < 0
                ||exerciceIS.getSociete()==null) {
            return -1;
        }
        return 1;
    }
    public int delete(ExerciceIS exerciceIS) {
        if (exerciceIS == null) {
            return -1;
        }
        remove(exerciceIS);
        return 1;
    }
}
