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
import java.util.Set;

/**
 *
 * @author ashkan
 */
public class ExistStateFormula implements StateFormula {

    public PathFormula arg;

    public ExistStateFormula(PathFormula _arg) {
        arg = _arg;
    }

    @Override
    public Set<String> findState(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta, Boolean counterExampleMode, TreeDepthIndicator depthIndicator) {
        return arg.findState(initial, CLTS, zeta, counterExampleMode, depthIndicator);
    }

    public String toString() {
        return "E (" + arg.toString() + ")";
    }

    @Override
    public StateFormula reduce() {
        return this;
    }

    @Override
    public Set<Item> findCounterExample(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta, TreeDepthIndicator depthIndicator) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
