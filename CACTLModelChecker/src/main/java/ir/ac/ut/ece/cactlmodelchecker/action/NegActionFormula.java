/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.action;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author ashkan
 */
public class NegActionFormula implements ActionFormula {
    public ActionFormula arg;

    public NegActionFormula(ActionFormula _arg) {
        arg = _arg;
    }

    @Override
    public boolean satisfy(StringActionFormula eta) {
        return !arg.satisfy(eta);
    }

    public String toString() {
        return "~" + arg.toString();
    }

    @Override
    public Set<String> toSet(Set<String> Act) {
        Set<String> set = new HashSet<String>(Act);
        set.removeAll(arg.toSet(Act));
        return set;
    }
    
}
