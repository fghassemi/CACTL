/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.formula.state;

import ir.ac.ut.ece.cactlmodelchecker.utils.TreeDepthIndicator;
import ir.ac.ut.ece.cactlmodelchecker.ConstraintLabeledTransitionSystem;
import ir.ac.ut.ece.cactlmodelchecker.Item;
import ir.ac.ut.ece.cactlmodelchecker.NetworkConstraint;
import ir.ac.ut.ece.cactlmodelchecker.formula.path.PathFormula;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author ashkan
 */
public class AndStateFormula implements StateFormula {
    public StateFormula arg1;
    public StateFormula arg2;

    public AndStateFormula(StateFormula _arg1, StateFormula _arg2) {
        arg1 = _arg1;
        arg2 = _arg2;
    }

    @Override
    public Set<String> findState(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta, Boolean counterExampleMode, TreeDepthIndicator depthIndicator) {
        //System.out.println("Computing ");//+this+"....");
        Set<String> T1 = arg1.findState(initial, CLTS, zeta, false, depthIndicator);
        //System.out.println(arg1+" is satisfied by "+T1+" under "+zeta+" for initial "+initial);
        Set<String> T2 = arg2.findState(initial, CLTS, zeta, false, depthIndicator);
        //System.out.println(arg2+" is satisfied by "+T2+" under "+zeta+" for initial "+initial);
        if (T1 != null && T2 != null) {
            T1.retainAll(T2);
        }
        return T1;
    }

    public String toString() {
        return "(" + arg1.toString() + " /\\ " + arg2.toString() + ")";
    }

    @Override
    public StateFormula reduce() {
        StateFormula targ1 = arg1.reduce();
        StateFormula targ2 = arg2.reduce();
        if (targ1.getClass().getName().equals(PathFormula.basicStateFormulaClassName)) {
            if (((BasicStateFormula) targ1).val) {
                return targ2;
            } else {
                return targ1;
            }
        } else if (targ2.getClass().getName().equals(PathFormula.basicStateFormulaClassName)) {
            if (((BasicStateFormula) targ2).val) {
                return targ1;
            } else {
                return targ2;
            }
        } else {
            return new AndStateFormula(targ1, targ2);
        }
    }

    @Override
    public Set<Item> findCounterExample(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta, TreeDepthIndicator depthIndicator) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
