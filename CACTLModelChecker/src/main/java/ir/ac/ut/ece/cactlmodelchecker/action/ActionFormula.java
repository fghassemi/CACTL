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
public interface ActionFormula {

    public boolean satisfy(StringActionFormula eta);

    public Set<String> toSet(Set<String> Act);
    
}
