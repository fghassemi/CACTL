/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.formula.state;

import ir.ac.ut.ece.cactlmodelchecker.utils.TreeDepthIndicator;
import ir.ac.ut.ece.cactlmodelchecker.ConstraintLabeledTransitionSystem;
import ir.ac.ut.ece.cactlmodelchecker.NetworkConstraint;
import ir.ac.ut.ece.cactlmodelchecker.state.CounterExample;
import java.util.Set;

/**
 *
 * @author ashkan
 */
public interface StateFormula {
    
    public static final String DEADLOCK = "DEADLOCK";
    public static final String NO_VALID_OUTGOING_TRANSITION = "NO_VALID_OUTGOING_TRANSITION";
    public static final String INFINITE_LOOP = "INFINITE_LOOP";
    public static final String NO_INNER_COUNTER_EXAMPLE_NEEDED = "NO_INNER_COUNTER_EXAMPLE_NEEDED";
    public static final String COUNTER_EXAMPLE_FOR_FIRST_ARGUMENT_NEEDED = "COUNTER_EXAMPLE_FOR_FIRST_ARGUMENT_NEEDED";
    public static final String COUNTER_EXAMPLE_FOR_SECOND_ARGUMENT_NEEDED = "COUNTER_EXAMPLE_FOR_SECOND_ARGUMENT_NEEDED";
    public static final String COUNTER_EXAMPLE_NEEDED_FOR_BOTH_ARGUMENTS = "COUNTER_EXAMPLE_NEEDED_FOR_BOTH_ARGUMENTS";

    public Set<String> findState(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta, Boolean counterExampleMode, TreeDepthIndicator depthIndicator);
    
    public CounterExample findCounterExample(Set<String> initialStates, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta, TreeDepthIndicator depthIndicator);

    public StateFormula reduce();
    
    public void calculateTreeSize(TreeDepthIndicator depthIndicator);
    
}
