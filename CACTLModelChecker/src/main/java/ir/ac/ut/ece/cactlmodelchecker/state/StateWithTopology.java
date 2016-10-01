/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.state;

import java.util.Set;

/**
 *
 * @author ashkan
 */
public class StateWithTopology extends State {

    public Set<Integer> topologies;

    public StateWithTopology(String name, Set<Integer> topologies) {
        super(name);
        this.topologies = topologies;
    }

}
