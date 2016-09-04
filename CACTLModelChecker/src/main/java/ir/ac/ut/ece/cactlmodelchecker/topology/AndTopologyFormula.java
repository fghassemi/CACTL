/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.topology;

/**
 *
 * @author ashkan
 */
public class AndTopologyFormula implements TopologyFormula {

    public TopologyFormula arg1;
    public TopologyFormula arg2;

    public AndTopologyFormula(TopologyFormula _arg1, TopologyFormula _arg2) {
        arg1 = _arg1;
        arg2 = _arg2;
    }

    @Override
    public boolean satisfy(boolean[][] topo, int dim) {
        return (arg1.satisfy(topo, dim) && arg2.satisfy(topo, dim));
    }

    public String toString() {
        return arg1.toString() + " /\\ " + arg2.toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof AndTopologyFormula) {
            return ((AndTopologyFormula) obj).arg1.equals(this.arg1) && ((AndTopologyFormula) obj).arg2.equals(this.arg2);
        } else {
            return false;
        }
    }
}
