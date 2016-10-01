/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.formula.state;

import ir.ac.ut.ece.cactlmodelchecker.utils.TreeDepthIndicator;
import ir.ac.ut.ece.cactlmodelchecker.ConstraintLabeledTransitionSystem;
import ir.ac.ut.ece.cactlmodelchecker.Item;
import ir.ac.ut.ece.cactlmodelchecker.LabeledTransition;
import ir.ac.ut.ece.cactlmodelchecker.NetworkConstraint;
import ir.ac.ut.ece.cactlmodelchecker.formula.action.BasicActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.action.StringActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.path.UnlessFormula;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author ashkan
 */
public class AlphaAWStateFormula implements StateFormula {
    public UnlessFormula arg;

    public AlphaAWStateFormula(UnlessFormula _arg) {
        arg = _arg;
    }

    @Override
    public Set<String> findState(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta, Boolean counterExampleMode, TreeDepthIndicator depthIndicator) {
        Set<String> T1 = arg.phi1.findState(null, CLTS, zeta, false, depthIndicator);
        Set<String> negT1 = new HashSet<String>(CLTS.vertexSet());
        negT1.removeAll(T1);
        Set<String> T2 = arg.phi2.findState(null, CLTS, zeta, false, depthIndicator);
        //phi2 can be a false formula , specially in AG
        Set<String> negT2 = new HashSet<String>(CLTS.vertexSet());
        if (T2 != null) {
            negT2.removeAll(T2);
        }
        Set<String> negT1T2 = new HashSet<String>(negT1);
        negT1T2.retainAll(negT2);
        //when initial is not empty, T1 should include initials, otherwise null is returned
        Set<String> copy_of_T1 = new HashSet<String>(T1);
        if (initial != null) {
            copy_of_T1.retainAll(initial);
        }
        if (initial != null && !initial.isEmpty() && copy_of_T1.isEmpty()) {
            return null;
        }
        //generating a new CLTS that all its states satisfy \phi1 and transitions satisfy \chi1 and conform to zeta
        ConstraintLabeledTransitionSystem filteredCLTS = new ConstraintLabeledTransitionSystem(CLTS);
        filteredCLTS.filterTransitions(arg.chi1, zeta);
        filteredCLTS.restrictToStates(T1);
        CLTS.filterTransitions(new BasicActionFormula(true), zeta);
        Set<String> end = new HashSet<String>();
        for (Iterator<String> i = T1.iterator(); i.hasNext();) {
            String t = i.next();
            Set<LabeledTransition> trans_t = CLTS.outgoingEdgesOf(t);
            //for all (t,(c,a),s)\in CLTS.Trans that a|= chi_1/\~chi_2 and s |= ~phi
            //or a|= (~chi_1/\~chi_2) and s |= true
            //or a|= chi_1/\chi_2 and s |= ~(phi \/ phi')
            //or a|= ~chi_1/\chi_2 and s |= ~phi'
            // a state with no zeta-path
            if (trans_t.size() < 1) {
                end.add(t);
            } else {
                //for non-deadlock states
                for (Iterator<LabeledTransition> j = trans_t.iterator(); j.hasNext();) {
                    LabeledTransition tr = j.next();
                    String dst = tr.getDst();
                    if ((arg.chi1.satisfy(new StringActionFormula(tr.label.act)) && !arg.chi2.satisfy(new StringActionFormula(tr.label.act)) && negT1.contains(dst)) || (!arg.chi1.satisfy(new StringActionFormula(tr.label.act)) && !arg.chi2.satisfy(new StringActionFormula(tr.label.act))) || (arg.chi1.satisfy(new StringActionFormula(tr.label.act)) && arg.chi2.satisfy(new StringActionFormula(tr.label.act)) && negT1T2.contains(dst)) || (!arg.chi1.satisfy(new StringActionFormula(tr.label.act)) && arg.chi2.satisfy(new StringActionFormula(tr.label.act)) && negT2.contains(dst))) {
                        end.add(t);
                        break;
                    }
                }
            }
        }
        HashMap<String, Set<Integer>> visited = new HashMap<String, Set<Integer>>();
        while (!end.isEmpty()) {
            // choose e \in start
            String c0 = end.iterator().next();
            end.remove(c0);
            //initializing the stack for backward analysis
            Stack<Item> stack = new Stack<Item>();
            Set<Integer> topo = new HashSet<Integer>(arg.rtopologies);
            if (visited.containsKey(c0)) {
                topo.removeAll(visited.get(c0));
            }
            stack.push(new Item(c0, topo));
            while (!stack.isEmpty()) {
                Item item = stack.pop();
                Set<Integer> to = new HashSet<Integer>(item.topo);
                if (visited.containsKey(item.state)) {
                    to.removeAll(visited.get(item.state));
                }
                if (!to.isEmpty()) {
                    item.topo.addAll(to);
                    visited.put(item.state, item.topo);
                    Set<LabeledTransition> tr = filteredCLTS.incomingEdgesOf(item.state);
                    for (Iterator<LabeledTransition> it = tr.iterator(); it.hasNext();) {
                        LabeledTransition tt = it.next();
                        Set<Integer> newTopo = new HashSet<Integer>();
                        for (Iterator<Integer> ii = to.iterator(); ii.hasNext();) {
                            Integer i = ii.next();
                            if (arg.Valid(tt.label.nc, i)) {
                                newTopo.add(i);
                            }
                        }
                        stack.push(new Item(tt.getSrc(), newTopo));
                    }
                }
            } //end of backward analysis
        }
        Set<String> result = new HashSet<String>();
        for (Iterator<String> it = T1.iterator(); it.hasNext();) {
            String s = it.next();
            if (!visited.containsKey(s)) {
                result.add(s);
            }
        }
        return result;
    }

    public String toString() {
        return "A (" + arg.toString() + ")";
    }

    @Override
    public StateFormula reduce() {
        return this;
    }

    @Override
    public Set<Item> findCounterExample(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta, TreeDepthIndicator depthIndicator) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
