/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.state;

import ir.ac.ut.ece.cactlmodelchecker.ConstraintLabeledTransitionSystem;
import ir.ac.ut.ece.cactlmodelchecker.Item;
import ir.ac.ut.ece.cactlmodelchecker.NetworkConstraint;
import ir.ac.ut.ece.cactlmodelchecker.path.PathFormula;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author ashkan
 */
public class NegStateFormula implements StateFormula {
    public StateFormula arg;

    public NegStateFormula(StateFormula _arg) {
        arg = _arg;
    }

    @Override
    public Set<String> findState(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta, Boolean counterExampleMode, TreeDepthIndicator depthIndicator) {
        Set<String> states = new HashSet<String>(CLTS.vertexSet());
        Set<String> neg_states = arg.findState(null, CLTS, zeta, false, depthIndicator);
        if (neg_states != null) {
            states.removeAll(neg_states);
        }
        if (initial != null) {
            states.retainAll(initial);
        }
        return states;
    }

    public String toString() {
        return "~" + arg.toString();
    }

    @Override
    public StateFormula reduce() {
        StateFormula temp = arg.reduce();
        if (temp.getClass().getName().equals(PathFormula.basicStateFormulaClassName)) {
            ((BasicStateFormula) temp).val = !((BasicStateFormula) temp).val;
            return temp;
        } else {
            return new NegStateFormula(temp);
        }
    }

    @Override
    public Set<Item> findCounterExample(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
