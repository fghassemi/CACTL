/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.formula.action;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author ashkan
 */
public class StringActionFormula implements ActionFormula {
    public String val;

    public StringActionFormula(String _val) {
        val = _val;
    }

    @Override
    public boolean satisfy(StringActionFormula eta) {
        return val.equals(eta.val);
    }

    public String toString() {
        return val;
    }

    @Override
    public Set<String> toSet(Set<String> Act) {
        Set<String> set = new HashSet<String>();
        set.add(val);
        return set;
    }
    
}
