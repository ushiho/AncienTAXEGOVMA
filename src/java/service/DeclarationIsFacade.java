/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.DeclarationIs;
import bean.ExerciceIS;
import bean.PaiementISTr1;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author ushiho
 */
@Stateless
public class DeclarationIsFacade extends AbstractFacade<DeclarationIs> {

    @PersistenceContext(unitName = "TaxeGOVMAPU")
    private EntityManager em;

    @EJB
    private CategorieISFacade categorieISFacade;
    @EJB
    private ExerciceISFacade exerciceISFacade;
    @EJB
    private PaiementISTr1Facade paiementISFacade;
    @EJB
    private SocieteFacade societeFacade;
    @EJB
    private CompteBanquaireFacade compteBanquaireFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DeclarationIsFacade() {
        super(DeclarationIs.class);
    }

    public int save(List<ExerciceIS> exerciceISs) {
        if (testParam(exerciceISs)) {
            return -1;
        }
        DeclarationIs declarationIs = calculerResultatFiscaleEtComptable(exerciceISs);
        testExoneration(declarationIs);
        declarationIs.setExerciceISs(exerciceISs);
        initDecalarationIsParam(declarationIs);
        create(declarationIs);
        exerciceISFacade.save(exerciceISs);
        return 1;
    }

    private boolean testParam(List<ExerciceIS> exerciceISs) {
        return exerciceISs == null || exerciceISs.get(0) == null || exerciceISs.isEmpty() || exerciceISFacade.testParams(exerciceISs) < 0;
    }

    private void initDecalarationIsParam(DeclarationIs declarationIs) {
        declarationIs.setEtat(0);
        declarationIs.setDateDeclaration(new Date());
        declarationIs.setPaiementISs(null);
    }

    //test de l exoneration
    private void testExoneration(DeclarationIs declarationIs) {
        if (societeFacade.exonerer(declarationIs.getSociete())) {
            declarationIs.setMontantIs(0f);
            declarationIs.getSociete().setDeficit(0f);
        } else if (calculerDeficit(declarationIs) > 0) {
            calcMontantIS(declarationIs);
        } else {
            declarationIs.setMontantIs(0f);
        }

    }

    //le calcul de mtIS a payer 
    private void calcMontantIS(DeclarationIs declarationIs) {
        declarationIs.setTauxIs(categorieISFacade.findByMontant(declarationIs.getResultatFiscal()));
        float montant = ((declarationIs.getResultatFiscal() * declarationIs.getTauxIs()) / 100f) % .3f;
        float cm = ((declarationIs.getChiffreAffaire() * 0.5f) / 100f) % .3f;
        if (montant > cm) {
            declarationIs.setMontantIs(montant);
        } else if (cm > 1500f) {
            declarationIs.setMontantIs(cm);
        } else {
            declarationIs.setMontantIs(1500f);
        }
    }

    //ajout de deficit des annes derniers !!
    private int calculerDeficit(DeclarationIs declarationIs) {
        float rest = declarationIs.getResultatFiscal() + declarationIs.getSociete().getDeficit();
        if (rest >= 0) {
            declarationIs.getSociete().setDeficit(0f);//faire initialiser le deficit !
            declarationIs.setResultatFiscal(rest);
            societeFacade.edit(declarationIs.getSociete());
            return 1;
        } else {
            declarationIs.getSociete().setDeficit(rest);
            societeFacade.edit(declarationIs.getSociete());
            return -1;
        }

    }

    private DeclarationIs calculerResultatFiscaleEtComptable(List<ExerciceIS> exerciceISs) {
        DeclarationIs declarationIs = new DeclarationIs();
        declarationIs.setSociete(exerciceISs.get(0).getSociete());
        for (int i = 0; i < exerciceISs.size(); i++) {
            ExerciceIS exerciceIS = exerciceISs.get(i);
            declarationIs.setResultatComptable(declarationIs.getResultatComptable() + exerciceIS.getProduits() - exerciceIS.getCharges());
            declarationIs.setResultatFiscal(declarationIs.getResultatFiscal() + declarationIs.getResultatComptable()
                    - exerciceIS.getDeductibles() + exerciceIS.getNonDeductibles());
            declarationIs.setChiffreAffaire(exerciceIS.getProduits() + declarationIs.getChiffreAffaire());
        }
        return declarationIs;
    }

    public int valider(DeclarationIs declarationIs) {
        if (declarationIs == null) {
            return -1;
        }
        PaiementISTr1 paiementIS = initPaiementsParams(declarationIs);
        paiementIS.setId(generate("PaiementIS", "id"));//max id existe ds kla table
        declarationIs.setEtat(1);
        //declarationIs.setPaiementIS(paiementIS);
        paiementIS.setDeclarationIs(declarationIs);
        edit(declarationIs);
//        paiementISFacade.save(paiementIS);
        return 1;
    }

    private PaiementISTr1 initPaiementsParams(DeclarationIs declarationIs) {
        PaiementISTr1 paiementISTr1 = new PaiementISTr1(0, null, null);
        String df = "31/03/" + (declarationIs.getDateDeclaration().getYear() + 1)
                + " " + declarationIs.getDateDeclaration().getHours() + ":" + declarationIs.getDateDeclaration().getMinutes()
                + ":" + declarationIs.getDateDeclaration().getSeconds();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy hh:mm:ss");
            paiementISTr1.setDateDernierDelai(sdf.parse(df));
        } catch (ParseException ex) {
            Logger.getLogger(DeclarationIsFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        return paiementISTr1;
    }

//    public int payer(DeclarationIs declarationIs, CompteBanquaire compteBanquaire) {
//        if (declarationIs == null || !compteBanquaireFacade.findBySociete(declarationIs.getSociete()).contains(compteBanquaire)) {
//            return -1;
//        }
//        PaiementISTr1 paiementIS = declarationIs.getPaiementIS();
//        GregorianCalendar gc = new GregorianCalendar();
//        gc.setTime(paiementIS.getDateDernierDelai());
//        gc.add(GregorianCalendar.MONTH, 3);
//
//        if (new Date().compareTo(paiementIS.getDateDernierDelai()) > 0) {
//            //creation de penalite
//
//        }
//        //puis on extraire le montant
//        if (compteBanquaireFacade.crediter(compteBanquaire, declarationIs.getMontantIs() / 4) < 0) {
//            return -2;//solde insuffisant !!!
//        } else {
//            compteBanquaireFacade.debiter(compteBanquaireFacade.findByDGI(), declarationIs.getMontantIs() / 4);
//            paiementIS.setAccompteVerse(paiementIS.getAccompteVerse() + 1);
//            paiementIS.setMtTotal(paiementIS.getMtTotal() + declarationIs.getMontantIs() / 4);
//            paiementIS.setDatePaiement(new Date());
//            paiementIS.setDateDernierDelai(gc.getTime());
//            edit(declarationIs);
//            paiementISFacade.edit(paiementIS);
//        }
//        return 1;
//    }
    public int modify(DeclarationIs declarationIs, List<ExerciceIS> nvExerciceISs) {
        if (declarationIs == null || nvExerciceISs == null || nvExerciceISs.isEmpty()) {
            return -1;
        } else if (declarationIs.getEtat() == 2) {
            return -2;
            //cette declaration est déja paye c'est fini !! mais l adim de sys peut le modifier mais on va voir
        }
        delete(declarationIs);
        return save(nvExerciceISs);

    }

    public void delete(DeclarationIs declarationIs) {
        if (declarationIs == null) {
            return;
        }
        List<ExerciceIS> anExerciceISs = declarationIs.getExerciceISs();
        for (int i = 0; i < anExerciceISs.size(); i++) {
            ExerciceIS anExerciceIS = anExerciceISs.get(i);
            exerciceISFacade.remove(anExerciceIS);
        }
        //ne faut pas supp la decl ,supp paiement d'abord ms c'est pas logique!!
        //alors je le modifier ;)//suppri nsowel lostaad
//        paiementISFacade.deleteByDeclarationIS(declarationIs);
        remove(declarationIs);
    }
}
