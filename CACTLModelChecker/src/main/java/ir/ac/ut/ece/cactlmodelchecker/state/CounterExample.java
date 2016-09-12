/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.state;

import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author ashkan
 */
public class CounterExample implements Serializable {
    public String first, last;
    public Set<Integer> topologies;

    public CounterExample(String first, String last, Set<Integer> topologies) {
        this.first = first;
        this.last = last;
        this.topologies = topologies;
    }
}
