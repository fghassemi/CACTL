/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.formula.path;

import ir.ac.ut.ece.cactlmodelchecker.formula.action.ActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.action.BasicActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.state.BasicStateFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.state.StateFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.topology.TopologyFormula;

/**
 *
 * @author ashkan
 */
public class FinallyFormula extends UntilFormula {

    public FinallyFormula(TopologyFormula _mu, ActionFormula _chi, StateFormula _phi, int numofnodes) {
        super(new BasicStateFormula(true), _phi, new BasicActionFormula(true), _chi, _mu, numofnodes);
    }

    public String toString() {
        return "F^{" + mu.toString() + "} {" + chi2.toString() + "}" + phi2.toString();
    }
}
