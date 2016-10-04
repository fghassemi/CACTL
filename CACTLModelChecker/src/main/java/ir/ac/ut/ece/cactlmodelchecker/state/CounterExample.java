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
    String startingState;

    public CounterExample(LinkedList<LabeledTransition> path, String startingState) {
        this.path = path;
        this.startingState = startingState;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("startingState={").append(startingState)
                .append("}\n");
        builder.append("path={");
        for (LabeledTransition transition : this.path) {
            if (this.path.indexOf(transition) != this.path.size()-1) {
                builder.append('[').append(transition.getSrc()).append(',')
                        .append(transition.toString()).append(',')
                        .append(transition.getDst()).append("},");
            } else {
                builder.append('[').append(transition.getSrc()).append(',')
                        .append(transition.toString()).append(',')
                        .append(transition.getDst()).append("}");
            }
        }
        builder.append('}');
        return builder.toString();
    }
}
