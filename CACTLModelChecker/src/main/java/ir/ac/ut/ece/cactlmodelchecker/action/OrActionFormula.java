/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.action;

import java.util.Set;

/**
 *
 * @author ashkan
 */
public class OrActionFormula implements ActionFormula {
    public ActionFormula arg1;
    public ActionFormula arg2;

    public OrActionFormula(ActionFormula _arg1, ActionFormula _arg2) {
        arg1 = _arg1;
        arg2 = _arg2;
    }

    @Override
    public boolean satisfy(StringActionFormula eta) {
        return arg1.satisfy(eta) || arg2.satisfy(eta);
    }

    public String toString() {
        return "(" + arg1.toString() + " \\/ " + arg2.toString() + ")";
    }

    @Override
    public Set<String> toSet(Set<String> Act) {
        Set<String> set = arg1.toSet(Act);
        set.addAll(arg2.toSet(Act));
        return set;
    }
    
}
