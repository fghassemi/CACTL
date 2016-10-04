/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.state;

import ir.ac.ut.ece.cactlmodelchecker.LabeledTransition;
import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 * @author ashkan
 */
public class CounterExample implements Serializable {

    LinkedList<LabeledTransition> path;

    public CounterExample(LinkedList<LabeledTransition> path) {
        this.path = path;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (LabeledTransition transition : this.path) {
            if (this.path.indexOf(transition) != this.path.size()-1) {
                builder.append(transition.toString()).append(',');
            } else {
                builder.append(transition.toString());
            }
        }
        return builder.toString();
    }
}
