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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author ashkan
 */
public class ImplyStateFormula implements StateFormula {
    public StateFormula arg1;
    public StateFormula arg2;

    public ImplyStateFormula(StateFormula _arg1, StateFormula _arg2) {
        arg1 = _arg1;
        arg2 = _arg2;
    }

    @Override
    public Set<String> findState(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta) {
        Set<String> T1 = new OrStateFormula(new NegStateFormula(arg1), arg2).findState(initial, CLTS, zeta);
        return T1;
    }

    public String toString() {
        return "(" + arg1.toString() + " ==> " + arg2.toString() + ")";
    }

    @Override
    public StateFormula reduce() {
        StateFormula targ1 = arg1.reduce();
        StateFormula targ2 = arg2.reduce();
        if (targ1.getClass().getName().equals(PathFormula.basicStateFormulaClassName)) {
            if (!((BasicStateFormula) targ1).val) {
                return new BasicStateFormula(true);
            } else {
                return targ2;
            }
        } else {
            return new ImplyStateFormula(targ1, targ2);
        }
    }

    @Override
    public Set<Item> findCounterExample(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
