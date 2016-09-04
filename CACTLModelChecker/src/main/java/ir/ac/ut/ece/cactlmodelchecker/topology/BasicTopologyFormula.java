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
public class BasicTopologyFormula implements TopologyFormula {
    public boolean val;

    public BasicTopologyFormula(boolean _val) {
        val = _val;
    }

    @Override
    public boolean satisfy(boolean[][] topo, int dim) {
        return val;
    }

    public String toString() {
        if (val) {
            return "true";
        } else {
            return "false";
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof BasicTopologyFormula) {
            return ((BasicTopologyFormula) obj).val == this.val;
        } else {
            return false;
        }
    }
    
}
