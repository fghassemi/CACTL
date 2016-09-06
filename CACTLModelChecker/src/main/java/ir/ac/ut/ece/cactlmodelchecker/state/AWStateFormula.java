/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.state;

import ir.ac.ut.ece.cactlmodelchecker.ConstraintLabeledTransitionSystem;
import ir.ac.ut.ece.cactlmodelchecker.Item;
import ir.ac.ut.ece.cactlmodelchecker.LabeledTransition;
import ir.ac.ut.ece.cactlmodelchecker.NetworkConstraint;
import ir.ac.ut.ece.cactlmodelchecker.action.StringActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.path.UnlessFormula;
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
public class AWStateFormula implements StateFormula {
    public UnlessFormula arg;

    public AWStateFormula(UnlessFormula _arg) {
        arg = _arg;
    }

    @Override
    public Set<String> findState(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta, Boolean counterExampleMode) {
        Set<String> T1 = arg.phi1.findState(null, CLTS, zeta, false);
        if (T1 == null) {
            return null;
        }
        Set<String> negT1 = new HashSet<String>(CLTS.vertexSet());
        negT1.removeAll(T1);
        Set<String> T2 = arg.phi2.findState(null, CLTS, zeta, false);
        //when initial is not empty, T1 should include initials, otherwise null is returned
        Set<String> copy_of_T1 = new HashSet<String>(T1);
        if (initial != null) {
            copy_of_T1.retainAll(initial);
        }
        if (initial != null && !initial.isEmpty() && copy_of_T1.isEmpty()) {
            return null;
        }
        //find (x,phi) transitions while states with a non-(x,phi) and non-(x',phi') transition are detected
        Set<LabeledTransition> CCSTrans = new HashSet<LabeledTransition>();
        Set<String> end = new HashSet<String>();
        boolean AddedBefore = false;
        for (Iterator<String> si = T1.iterator(); si.hasNext();) {
            String s = si.next();
            AddedBefore = false;
            Set<LabeledTransition> trans = CLTS.outgoingEdgesOf(s);
            Set<LabeledTransition> tempCCSTrans = new HashSet<LabeledTransition>();
            for (Iterator<LabeledTransition> ti = trans.iterator(); ti.hasNext();) {
                LabeledTransition tr = ti.next();
                String dst = tr.getDst();
                if (zeta.conforms(tr.label.nc)) {
                    if (T1.contains(dst) && arg.chi1.satisfy(new StringActionFormula(tr.label.act))) {
                        tempCCSTrans.add(tr);
                    } else if (T2 != null && !(T2.contains(dst) && arg.chi2.satisfy(new StringActionFormula(tr.label.act)))) {
                        if (!AddedBefore) {
                            end.add(s);
                            AddedBefore = true;
                        }
                    }
                }
            }
            if (trans.size() < 1) {
                end.add(s);
            } else {
                CCSTrans.addAll(tempCCSTrans);
            }
        }
        //generating a new CLTS that all its states satisfy \phi1 and transitions satisfy \chi1 and conform to zeta
        ConstraintLabeledTransitionSystem filteredCLTS = new ConstraintLabeledTransitionSystem(CCSTrans, T1, CLTS.InitialStates());
        HashMap<String, Set<Integer>> visited = new HashMap<String, Set<Integer>>();
        while (!end.isEmpty()) {
            // choose e \in start
            String c0 = end.iterator().next();
            end.remove(c0);
            //initializing the stack for backward analysis
            Stack<Item> stack = new Stack<Item>();
            Set<Integer> topo = new HashSet<Integer>(arg.rtopologies);
            visited.put(c0, topo);
            stack.push(new Item(c0, topo));
            //System.out.println("backward analysis starts at"+ c0);
            while (!stack.isEmpty()) {
                Item item = stack.pop();
                //add to violate : no need as we use range of visited
                //        		if (item.state.equals("0"))
                //        			System.out.println("nahaaara");
                //        		else
                //        			System.out.println("over the path"+ item.state);
                Set<LabeledTransition> tr = filteredCLTS.incomingEdgesOf(item.state);
                for (Iterator<LabeledTransition> it = tr.iterator(); it.hasNext();) {
                    LabeledTransition tt = it.next();
                    String src = tt.getSrc();
                    Set<Integer> newTopo = new HashSet<Integer>();
                    //we filter topologies by the nc of transition regarding visited
                    for (Iterator<Integer> ii = item.topo.iterator(); ii.hasNext();) {
                        Integer i = ii.next();
                        if (arg.Valid(tt.label.nc, i) && (!visited.containsKey(src) || !visited.get(src).contains(i))) {
                            newTopo.add(i);
                        }
                    }
                    if (!newTopo.isEmpty()) {
                        stack.push(new Item(src, newTopo));
                        Set<Integer> updTopo;
                        if (visited.containsKey(src)) {
                            updTopo = visited.get(src);
                            updTopo.addAll(newTopo);
                        } else {
                            updTopo = newTopo;
                        }
                        visited.put(src, updTopo);
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
    public Set<Item> findCounterExample(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
