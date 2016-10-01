/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.formula.topology;

/**
 *
 * @author ashkan
 */
class NegTopologyFormula implements TopologyFormula {
    public TopologyFormula arg;

    public NegTopologyFormula(TopologyFormula _arg) {
        arg = _arg;
    }

    @Override
    public boolean satisfy(boolean[][] topo, int dim) {
        return !arg.satisfy(topo, dim);
    }

    public String toString() {
        return "~" + arg.toString();
    }
    
}
