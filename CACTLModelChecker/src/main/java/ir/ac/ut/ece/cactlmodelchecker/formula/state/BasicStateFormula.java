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
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author ashkan
 */
public class BasicStateFormula implements StateFormula {
    public boolean val;

    public BasicStateFormula(boolean _val) {
        val = _val;
    }

    @Override
    public Set<String> findState(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta, Boolean counterExampleMode, TreeDepthIndicator depthIndicator) {
        if (val) {
            if ((initial == null) || initial.isEmpty()) {
                return CLTS.vertexSet();
            } else {
                return initial;
            }
        } else {
            return null;
        }
    }

    public String toString() {
        if (val) {
            return "true";
        } else {
            return "false";
        }
    }

    @Override
    public StateFormula reduce() {
        StateFormula temp = new BasicStateFormula(val);
        return temp;
    }

    @Override
    public Set<Item> findCounterExample(Set<String> initialStates, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta, TreeDepthIndicator depthIndicator) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}