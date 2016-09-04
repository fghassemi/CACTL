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
public interface TopologyFormula {

    public boolean satisfy(boolean[][] topo, int dim);
    
}
