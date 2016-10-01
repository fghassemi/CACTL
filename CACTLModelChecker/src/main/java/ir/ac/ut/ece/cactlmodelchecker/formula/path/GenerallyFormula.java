/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.formula.path;

import ir.ac.ut.ece.cactlmodelchecker.formula.state.BasicStateFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.action.ActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.action.BasicActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.state.StateFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.topology.BasicTopologyFormula;

/**
 *
 * @author ashkan
 */
public class GenerallyFormula extends UnlessFormula {

    public GenerallyFormula(StateFormula _phi, ActionFormula _chi, int numofnodes) {
        super(_phi, new BasicStateFormula(false), _chi, new BasicActionFormula(false), new BasicTopologyFormula(true), numofnodes);
    }

    public String toString() {
        return "G " + phi1.toString() + "{" + chi1.toString() + "}";
    }
    
}
