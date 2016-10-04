/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.state;

import ir.ac.ut.ece.cactlmodelchecker.LabeledTransition;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author ashkan
 */
public class CounterExampleWithTopologies extends CounterExample {
    
    public Set<Integer> topologies;
    
    public CounterExampleWithTopologies(LinkedList<LabeledTransition> path, String startingState, Set<Integer> topologies) {
        super(path, startingState);
        this.topologies = topologies;
    }

    @Override
    public String toString() {
        return "topologies={" + topologies + "}\n" + super.toString();
    }
    
}
