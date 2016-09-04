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
import ir.ac.ut.ece.cactlmodelchecker.SCCInspector;
import ir.ac.ut.ece.cactlmodelchecker.action.StringActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.path.UntilFormula;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author ashkan
 */
public class AUStateFormula implements StateFormula {
    public UntilFormula arg;

    public AUStateFormula(UntilFormula _arg) {
        arg = _arg;
    }

    @Override
    public Set<String> findState(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta) {
        Set<String> T1 = arg.phi1.findState(null, CLTS, zeta);
        if (T1 == null) {
            return null;
        }
        Set<String> negT1 = new HashSet<String>(CLTS.vertexSet());
        negT1.removeAll(T1);
        Set<String> T2 = arg.phi2.findState(null, CLTS, zeta);
        Set<String> negT2 = new HashSet<String>(CLTS.vertexSet());
        negT2.removeAll(T2);
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
        //find (x,phi) transitions while states with a non-(x,phi) and non-(x',phi') transition become deadlock
        // this implements clean
        Set<LabeledTransition> CCSTrans = new HashSet<LabeledTransition>();
        Set<String> EXCCSStates = new HashSet<String>();
        boolean hasBadTrans = false;
        for (Iterator<String> si = T1.iterator(); si.hasNext();) {
            String s = si.next();
            hasBadTrans = false;
            Set<LabeledTransition> trans = CLTS.outgoingEdgesOf(s);
            Set<LabeledTransition> tempCCSTrans = new HashSet<LabeledTransition>();
            for (Iterator<LabeledTransition> ti = trans.iterator(); ti.hasNext() && !hasBadTrans;) {
                LabeledTransition tr = ti.next();
                String dst = tr.getDst();
                if (zeta.conforms(tr.label.nc)) {
                    if (T2.contains(dst) && arg.chi2.satisfy(new StringActionFormula(tr.label.act))) {
                        EXCCSStates.add(s);
                    } else if (T1.contains(dst) && arg.chi1.satisfy(new StringActionFormula(tr.label.act))) {
                        tempCCSTrans.add(tr);
                    } else {
                        hasBadTrans = true;
                    }
                }
            }
            if (!hasBadTrans) {
                CCSTrans.addAll(tempCCSTrans);
            }
        }
        //generating a new CLTS that all its states satisfy \phi1 and transitions satisfy \chi1 and conform to zeta
        ConstraintLabeledTransitionSystem filteredCLTS = new ConstraintLabeledTransitionSystem(CCSTrans, T1, CLTS.InitialStates());
        SCCInspector inspector = new SCCInspector(filteredCLTS);
        Set<String>[] SCCs = inspector.ComputeSCCc();
        //making the set of states that a valid path should end in
        HashMap<String, Set<Integer>> visited = new HashMap<String, Set<Integer>>();
        Set<String> end = new HashSet<String>();
        for (int scc = 0; scc < inspector.count; scc++) {
            //DirectedSubgraph<String, LabeledTransition> sg = k.next();
            //we want terminal SCCs with no (phi',chi') trans and valid accumulated network constraint
            //this implements TCCS + Start
            Set<String> s_sg = SCCs[scc];
            boolean hasAUState = false;
            boolean terminal = true;
            NetworkConstraint acc = new NetworkConstraint();
            for (Iterator<String> si = s_sg.iterator(); si.hasNext() && !hasAUState && terminal;) {
                String s = si.next();
                if (EXCCSStates.contains(s)) {
                    hasAUState = true;
                } else {
                    Set<LabeledTransition> trans = filteredCLTS.outgoingEdgesOf(s);
                    for (Iterator<LabeledTransition> ti = trans.iterator(); ti.hasNext() && terminal;) {
                        LabeledTransition tr = ti.next();
                        String dst = tr.getDst();
                        if (!s_sg.contains(dst)) {
                            terminal = false;
                        } else {
                            acc.update(tr.label.nc);
                        }
                    }
                }
            }
            if (!hasAUState && terminal && !arg.OverInvalidPath(acc)) {
                // a (chi,phi)-CCS with no (chi',phi') state
                Set<Integer> validTopo = new HashSet<Integer>();
                for (Iterator<Integer> topoi = arg.rtopologies.iterator(); topoi.hasNext();) {
                    Integer i = topoi.next();
                    if (arg.Valid(acc, i)) {
                        validTopo.add(i);
                    }
                }
                //for these topologies that violate mu, backward analysis is never examined
                // validTopo is for sure not empty as it is valid
                for (Iterator<String> si = s_sg.iterator(); si.hasNext();) {
                    String s = si.next();
                    visited.put(s, validTopo);
                    end.add(s);
                }
            }
        }
        while (!end.isEmpty()) {
            // choose e \in start
            String c0 = end.iterator().next();
            end.remove(c0);
            //initializing the stack for backward analysis
            Stack<Item> stack = new Stack<Item>();
            Set<Integer> topo;
            if (visited.containsKey(c0)) {
                topo = visited.get(c0);
            } else {
                topo = new HashSet<Integer>(arg.rtopologies);
                visited.put(c0, topo);
            }
            stack.push(new Item(c0, topo));
            //System.out.println("backward analysis starts at" + c0);
            while (!stack.isEmpty()) {
                Item item = stack.pop();
                //add to violate : no need as we use range of visited
//                if (item.state.equals("0")) {
//                    System.out.println("nahaaara");
//                } else {
//                    System.out.println("over the path" + item.state);
//                }
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
        Set<String> T1 = arg.phi1.findState(null, CLTS, zeta);
        if (T1 == null) {
            return null;
        }
        Set<String> negT1 = new HashSet<String>(CLTS.vertexSet());
        negT1.removeAll(T1);
        Set<String> T2 = arg.phi2.findState(null, CLTS, zeta);
        Set<String> negT2 = new HashSet<String>(CLTS.vertexSet());
        negT2.removeAll(T2);
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
        //find (x,phi) transitions while states with a non-(x,phi) and non-(x',phi') transition become deadlock
        // this implements clean
        Set<LabeledTransition> CCSTrans = new HashSet<LabeledTransition>();
        Set<String> EXCCSStates = new HashSet<String>();
        boolean hasBadTrans = false;
        for (Iterator<String> si = T1.iterator(); si.hasNext();) {
            String s = si.next();
            hasBadTrans = false;
            Set<LabeledTransition> trans = CLTS.outgoingEdgesOf(s);
            Set<LabeledTransition> tempCCSTrans = new HashSet<LabeledTransition>();
            for (Iterator<LabeledTransition> ti = trans.iterator(); ti.hasNext() && !hasBadTrans;) {
                LabeledTransition tr = ti.next();
                String dst = tr.getDst();
                if (zeta.conforms(tr.label.nc)) {
                    if (T2.contains(dst) && arg.chi2.satisfy(new StringActionFormula(tr.label.act))) {
                        EXCCSStates.add(s);
                    } else if (T1.contains(dst) && arg.chi1.satisfy(new StringActionFormula(tr.label.act))) {
                        tempCCSTrans.add(tr);
                    } else {
                        hasBadTrans = true;
                    }
                }
            }
            if (!hasBadTrans) {
                CCSTrans.addAll(tempCCSTrans);
            }
        }
        //generating a new CLTS that all its states satisfy \phi1 and transitions satisfy \chi1 and conform to zeta
        ConstraintLabeledTransitionSystem filteredCLTS = new ConstraintLabeledTransitionSystem(CCSTrans, T1, CLTS.InitialStates());
        SCCInspector inspector = new SCCInspector(filteredCLTS);
        Set<String>[] SCCs = inspector.ComputeSCCc();
        //making the set of states that a valid path should end in
        HashMap<String, Set<Integer>> visited = new HashMap<String, Set<Integer>>();
        Set<String> end = new HashSet<String>();
        for (int scc = 0; scc < inspector.count; scc++) {
            //DirectedSubgraph<String, LabeledTransition> sg = k.next();
            //we want terminal SCCs with no (phi',chi') trans and valid accumulated network constraint
            //this implements TCCS + Start
            Set<String> s_sg = SCCs[scc];
            boolean hasAUState = false;
            boolean terminal = true;
            NetworkConstraint acc = new NetworkConstraint();
            for (Iterator<String> si = s_sg.iterator(); si.hasNext() && !hasAUState && terminal;) {
                String s = si.next();
                if (EXCCSStates.contains(s)) {
                    hasAUState = true;
                } else {
                    Set<LabeledTransition> trans = filteredCLTS.outgoingEdgesOf(s);
                    for (Iterator<LabeledTransition> ti = trans.iterator(); ti.hasNext() && terminal;) {
                        LabeledTransition tr = ti.next();
                        String dst = tr.getDst();
                        if (!s_sg.contains(dst)) {
                            terminal = false;
                        } else {
                            acc.update(tr.label.nc);
                        }
                    }
                }
            }
            if (!hasAUState && terminal && !arg.OverInvalidPath(acc)) {
                // a (chi,phi)-CCS with no (chi',phi') state
                Set<Integer> validTopo = new HashSet<Integer>();
                for (Iterator<Integer> topoi = arg.rtopologies.iterator(); topoi.hasNext();) {
                    Integer i = topoi.next();
                    if (arg.Valid(acc, i)) {
                        validTopo.add(i);
                    }
                }
                //for these topologies that violate mu, backward analysis is never examined
                // validTopo is for sure not empty as it is valid
                for (Iterator<String> si = s_sg.iterator(); si.hasNext();) {
                    String s = si.next();
                    visited.put(s, validTopo);
                    end.add(s);
                }
            }
        }
        
        Set<Item> phiPrimeCounterExamples = this.arg.phi2.findCounterExample(null, CLTS, zeta); // ask about the arguments for calling this method
        Set<Item> counterExamples = new HashSet<>();
        while (!end.isEmpty()) {
            //Item counterExamplePathTopologies = new Item(null, null);
            // choose e \in start
            String c0 = end.iterator().next();
            end.remove(c0);
            //initializing the stack for backward analysis
            Stack<Item> stack = new Stack<Item>();
            Set<Integer> topo;
            if (visited.containsKey(c0)) {
                topo = visited.get(c0);
            } else {
                topo = new HashSet<Integer>(arg.rtopologies);
                // if type is Item, then add it with items topologies
                visited.put(c0, topo);
            }
            // checking that if the current Item we are about to do backward on
            // is also in the phiPrimesCounterExamples, the topologies should be
            // the union of their topologies
            for (Item phiPrimeCounterExample : phiPrimeCounterExamples) {
                if (c0.equals(phiPrimeCounterExample.state)) {
                    for (Integer topol : phiPrimeCounterExample.topo) {
                        if (!topo.contains(topol)) {
                            topo.add(topol);
                        }
                    }
                    break;
                }
            }
            
            Item lastItemInBackwardAnalysis = null;
            stack.push(new Item(c0, topo));
            //System.out.println("backward analysis starts at" + c0);
            while (!stack.isEmpty()) {
                Item item = stack.pop();
                lastItemInBackwardAnalysis = item;
                //add to violate : no need as we use range of visited
//                if (item.state.equals("0")) {
//                    System.out.println("nahaaara");
//                } else {
//                    System.out.println("over the path" + item.state);
//                }
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
                        
                        // adding the new topologies to the union of all
                        for (Integer topol : newTopo) {
                            if (!topo.contains(topol)) {
                                topo.add(topol);
                            }
                        }
                    }
                }
            } //end of backward analysis
            // adding the states that end with one of the initial states as 
            // counter examples with the union topology
            if (T1.contains(lastItemInBackwardAnalysis.state)) {
                Item previouslyVisitedState = null;
                for (Item item : counterExamples) {
                    if (item.state.equals(lastItemInBackwardAnalysis.state)) {
                        previouslyVisitedState = item;
                        break;
                    }
                }
                if (previouslyVisitedState == null) {
                    // if state isn't previously visited, we simply add a new
                    // item with the calculated topologies
                    counterExamples.add(new Item(lastItemInBackwardAnalysis.state, topo));
                } else {
                    // if we have visited this state before, then we need to
                    // calculate the union of their topologies
                    previouslyVisitedState.topo.addAll(topo);
                }
            }
        }
        
        return counterExamples;
    }
    
}
