/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.path;

import ir.ac.ut.ece.cactlmodelchecker.state.BasicStateFormula;
import ir.ac.ut.ece.cactlmodelchecker.ConstraintLabeledTransitionSystem;
import ir.ac.ut.ece.cactlmodelchecker.LabeledTransition;
import ir.ac.ut.ece.cactlmodelchecker.NetworkConstraint;
import ir.ac.ut.ece.cactlmodelchecker.action.ActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.action.BasicActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.action.StringActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.state.StateFormula;
import ir.ac.ut.ece.cactlmodelchecker.topology.BasicTopologyFormula;
import ir.ac.ut.ece.cactlmodelchecker.utils.TreeDepthIndicator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author ashkan
 */
public class NextFormula extends UntilFormula {

    public NextFormula(ActionFormula _chi, StateFormula _phi, int numofnodes) {
        super(new BasicStateFormula(true), _phi, new BasicActionFormula(false), _chi, new BasicTopologyFormula(true), numofnodes);
    }

    @Override
    public Set<String> findState(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta, Boolean counterExampleMode, TreeDepthIndicator depthIndicator) {
        System.out.println("Verifying E" + this.toString() + " is started");
        Set<String> T2 = phi2.findState(null, CLTS, zeta, false, null);
        //making the set of states for backward checking
        Set<String> result = new HashSet<String>();
        //this case should be done in case phi2 or chi2 is not false
        StateFormula phiTemp = phi2.reduce();
        boolean reduction = (phiTemp.getClass().getName().equals(basicStateFormulaClassName) && !((BasicStateFormula) phiTemp).val) || (chi2.toSet(CLTS.getAct()).isEmpty());
        if (!reduction && T2 != null && !T2.isEmpty()) {
            for (Iterator<String> i = T2.iterator(); i.hasNext();) {
                String t = i.next();
                Set<LabeledTransition> trans_t = CLTS.incomingEdgesOf(t);
                //for all (s,(c,a),t)\in CLTS.Trans that a|= \chi_2 and s|=phi1
                for (Iterator<LabeledTransition> j = trans_t.iterator(); j.hasNext();) {
                    LabeledTransition tr = j.next();
                    String s = tr.getSrc();
                    if (chi2.satisfy(new StringActionFormula(tr.label.act)) && zeta.conforms(tr.label.nc)) {
                        result.add(s);
                    }
                }
            }
        }
        System.out.println("Verifying E" + this.toString() + " is terminated by " + result);
        return result;
    }

    public String toString() {
        return "X^{" + mu.toString() + "} {" + chi2.toString() + "}" + phi2.toString();
    }
    
}
