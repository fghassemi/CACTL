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
import java.util.Set;

/**
 *
 * @author ashkan
 */
public interface StateFormula {
    
    public static final String DEADLOCK = "DEADLOCK";
    public static final String NO_VALID_OUTGOING_TRANSITION = "NO_VALID_OUTGOING_TRANSITION";
    public static final String INFINITE_LOOP = "INFINITE_LOOP";

    public Set<String> findState(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta, Boolean counterExampleMode, TreeDepthIndicator depthIndicator);
    
    public Set<Item> findCounterExample(Set<String> initialStates, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta, TreeDepthIndicator depthIndicator);

    public StateFormula reduce();
    
}
