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
import ir.ac.ut.ece.cactlmodelchecker.action.StringActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.state.StateFormula;
import ir.ac.ut.ece.cactlmodelchecker.topology.TopologyFormula;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author ashkan
 */
public class UntilFormula extends PathFormula {

    public UntilFormula(StateFormula _phi1, StateFormula _phi2, ActionFormula _chi1, ActionFormula _chi2, TopologyFormula _mu, int numofnodes) {
        super(numofnodes, _mu);
        phi1 = _phi1;
        phi2 = _phi2;
        chi1 = _chi1;
        chi2 = _chi2;
        mu = _mu;
    }

    @Override
    public Set<String> findState(Set<String> initial, ConstraintLabeledTransitionSystem InitialCLTS, NetworkConstraint zeta) {
        Set<String> T = phi1.findState(null, InitialCLTS, zeta);
        //when initial is not empty, T should include initials, otherwise null is returned
        Set<String> copy_of_T = new HashSet<String>(T);
        if (initial != null) {
            copy_of_T.retainAll(initial);
        }
        if (initial != null && !initial.isEmpty() && copy_of_T.isEmpty()) {
            return null;
        }
        Set<String> T2 = phi2.findState(null, InitialCLTS, zeta);
        //working on the copy of input CLTS
        //ConstraintLabeledTransitionSystem CLTS = new ConstraintLabeledTransitionSystem(InitialCLTS);
        //generating a new CLTS that all its states satisfy \phi1 and transitions satisfy \chi1 and conform to zeta
        //ConstraintLabeledTransitionSystem filteredCLTS = new ConstraintLabeledTransitionSystem(InitialCLTS);
        //filteredCLTS.filterTransitions(chi1, zeta);
        //filteredCLTS.restrictToStates(T);
        //removing transitions from CLTS that do not satisfy either \chi_1 or \chi_2 and do not conform to \zeta
        //CLTS.filterTransitions(new OrActionFormula(chi1, chi2), zeta);
        //making the set of states for backward checking
        Set<String> backwards = new HashSet<String>();
        //this case should be done in case phi2 or chi2 is not false
        StateFormula phiTemp = phi2.reduce();
        //Hashtable<String,Tag> htb = new Hashtable<String,Tag>() ;
        boolean reduction = (phiTemp.getClass().getName().equals(basicStateFormulaClassName) && !((BasicStateFormula) phiTemp).val) || (chi2.toSet(InitialCLTS.getAct()).isEmpty());
        if (!reduction && T2 != null && !T2.isEmpty()) {
            for (Iterator<String> i = T2.iterator(); i.hasNext();) {
                String t = i.next();
                Set<LabeledTransition> trans_t = InitialCLTS.incomingEdgesOf(t);
                //for all (s,(c,a),t)\in CLTS.Trans that a|= \chi_2 and s|=phi1
                for (Iterator<LabeledTransition> j = trans_t.iterator(); j.hasNext();) {
                    LabeledTransition tr = j.next();
                    String s = tr.getSrc();
                    if (T.contains(s) && chi2.satisfy(new StringActionFormula(tr.label.act)) && zeta.conforms(tr.label.nc)) {
                        backwards.add(s);
                        //htb.put(t,new Tag(tr.label.nc,d,null));
                    }
                }
            }
        }
        //ready to do the backward analysis
        Set<String> visited = new HashSet<String>();
        while (!backwards.isEmpty()) {
            // choose s \in backwards
            String s = backwards.iterator().next();
            //System.out.println("state "+s+" is checking");
            backwards.remove(s);
            visited.add(s);
            Set<LabeledTransition> trans_t = InitialCLTS.incomingEdgesOf(s);
            //for all (t,(c,a),s)\in CLTS.Trans that t|= phi1 and (a|= \chi_1 or (c={} and a=tau)))
            for (Iterator<LabeledTransition> j = trans_t.iterator(); j.hasNext();) {
                LabeledTransition tr = j.next();
                String t = tr.getSrc();
                if (T.contains(t) && chi1.satisfy(new StringActionFormula(tr.label.act)) && zeta.conforms(tr.label.nc)) {
                    if (!visited.contains(t)) {
                        backwards.add(t);
                        //htb.put(t,new Tag(tr.label.nc,s,htb.get(s)) );
                    }
                }
            }
        }
        // finish of backward analysis
        return visited;
    }

    public String toString() {
        return phi1.toString() + "{" + chi1.toString() + "}U^{" + mu.toString() + "} {" + chi2.toString() + "}" + phi2.toString();
    }
    
}
