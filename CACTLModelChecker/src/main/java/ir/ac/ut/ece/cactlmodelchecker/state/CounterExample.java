/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.state;

import ir.ac.ut.ece.cactlmodelchecker.LabeledTransition;
import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author ashkan
 */
public class CounterExample implements Serializable {

    Set<LabeledTransition> path;

    public CounterExample(Set<LabeledTransition> path) {
        this.path = path;
    }
}
