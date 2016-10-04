/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.formula.state;

import ir.ac.ut.ece.cactlmodelchecker.utils.TreeDepthIndicator;
import ir.ac.ut.ece.cactlmodelchecker.ConstraintLabeledTransitionSystem;
import ir.ac.ut.ece.cactlmodelchecker.NetworkConstraint;
import ir.ac.ut.ece.cactlmodelchecker.formula.path.PathFormula;
import ir.ac.ut.ece.cactlmodelchecker.state.CounterExample;
import java.util.HashSet;
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
    public CounterExample findCounterExample(Set<String> initialStates, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta, TreeDepthIndicator depthIndicator) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void calculateTreeSize(TreeDepthIndicator depthIndicator) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
