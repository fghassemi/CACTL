/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.state;

import ir.ac.ut.ece.cactlmodelchecker.ConstraintLabeledTransitionSystem;
import ir.ac.ut.ece.cactlmodelchecker.Item;
import ir.ac.ut.ece.cactlmodelchecker.NetworkConstraint;
import java.util.Set;

/**
 *
 * @author ashkan
 */
public interface StateFormula {

    public Set<String> findState(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta);
    
    public Set<Item> findCounterExample(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta);

    public StateFormula reduce();
    
}
