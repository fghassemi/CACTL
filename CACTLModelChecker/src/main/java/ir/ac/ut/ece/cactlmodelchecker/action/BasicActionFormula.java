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
public class BasicActionFormula implements ActionFormula {

    public boolean val;

    public BasicActionFormula(boolean _val) {
        val = _val;
    }

    @Override
    public boolean satisfy(StringActionFormula eta) {
        return val;
    }

    public String toString() {
        if (val) {
            return "true";
        } else {
            return "false";
        }
    }

    @Override
    public Set<String> toSet(Set<String> Act) {
        // TODO Auto-generated method stub
        if (val) {
            return Act;
        } else {
            return new HashSet<String>();
        }
    }
}