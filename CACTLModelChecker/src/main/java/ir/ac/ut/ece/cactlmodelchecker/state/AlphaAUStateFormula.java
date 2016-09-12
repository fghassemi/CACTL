/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.state;

import ir.ac.ut.ece.cactlmodelchecker.ConstraintLabeledTransitionSystem;
import ir.ac.ut.ece.cactlmodelchecker.Item;
import ir.ac.ut.ece.cactlmodelchecker.Label;
import ir.ac.ut.ece.cactlmodelchecker.LabeledTransition;
import ir.ac.ut.ece.cactlmodelchecker.NetworkConstraint;
import ir.ac.ut.ece.cactlmodelchecker.action.BasicActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.action.StringActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.path.UntilFormula;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import org.jgrapht.alg.KosarajuStrongConnectivityInspector;
import org.jgrapht.graph.DirectedSubgraph;

/**
 *
 * @author ashkan
 */
public class AlphaAUStateFormula implements StateFormula {
    public UntilFormula arg;

    public AlphaAUStateFormula(UntilFormula _arg) {
        arg = _arg;
    }

    @Override
    public Set<String> findState(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta, Boolean counterExampleMode, TreeDepthIndicator depthIndicator) {
        Set<String> T1 = arg.phi1.findState(null, CLTS, zeta, false, depthIndicator);
        Set<String> negT1 = new HashSet<String>(CLTS.vertexSet());
        negT1.removeAll(T1);
        Set<String> T2 = arg.phi2.findState(null, CLTS, zeta, false, depthIndicator);
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
        //computing T states : states with no zeta-transition that is not of (chi,phi) form
        Set<String> T = new HashSet<String>();
        boolean hasBadTrans = false;
        for (Iterator<String> si = T1.iterator(); si.hasNext();) {
            String s = si.next();
            hasBadTrans = false;
            Set<LabeledTransition> tr = CLTS.outgoingEdgesOf(s);
            for (Iterator<LabeledTransition> ti = tr.iterator(); ti.hasNext() && !hasBadTrans;) {
                LabeledTransition t = ti.next();
                String dst = t.getDst();
                if (zeta.conforms(t.label.nc) && (!T1.contains(dst) || !arg.chi1.satisfy(new StringActionFormula(t.label.act)))) {
                    hasBadTrans = true;
                }
            }
            //T includes states with either no zeta- transition or states with only zeta-transitions that are of (chi,phi) form
            if (!hasBadTrans) {
                T.add(s);
            }
        }
        //generating a new CLTS that all its states satisfy \phi1 and transitions satisfy \chi1 and conform to zeta
        ConstraintLabeledTransitionSystem filteredCLTS1 = new ConstraintLabeledTransitionSystem(CLTS);
        filteredCLTS1.filterTransitions(arg.chi1, zeta);
        filteredCLTS1.restrictToStates(T1);
        ConstraintLabeledTransitionSystem filteredCLTS2 = new ConstraintLabeledTransitionSystem(filteredCLTS1);
        filteredCLTS2.restrictToStates(T);
        CLTS.filterTransitions(new BasicActionFormula(true), zeta);
        KosarajuStrongConnectivityInspector<String, LabeledTransition> sci = new KosarajuStrongConnectivityInspector<String, LabeledTransition>(filteredCLTS2);
        List<DirectedSubgraph<String, LabeledTransition>> stronglyConnectedSubgraphs = sci.stronglyConnectedSubgraphs();
        //making the set of states that a valid path should end in
        HashMap<String, Set<Integer>> visited = new HashMap<String, Set<Integer>>();
        Set<String> end = new HashSet<String>();
        int num = 0; //counting SCC
        Hashtable<String, Set<String>> mapSCC = new Hashtable<String, Set<String>>();
        for (Iterator<DirectedSubgraph<String, LabeledTransition>> k = stronglyConnectedSubgraphs.iterator(); k.hasNext();) {
            //System.out.println("SCC is collapsed: "+k);
            DirectedSubgraph<String, LabeledTransition> sg = k.next();
            boolean hasOutTrans = false;
            if (sg.edgeSet().size() > 0) {
                NetworkConstraint l = UntilFormula.Accumulate(sg);
                Set<LabeledTransition> tr_sg = sg.edgeSet();
                Set<String> s_sg = sg.vertexSet();
                if (!arg.OverInvalidPath(l)) {
                    // l does not violate mu
                    //if it is a terminal SCC : if one of its states has an out-going trans to out of SCC, it is not terminal
                    hasOutTrans = false;
                    for (Iterator<String> si = s_sg.iterator(); si.hasNext() && !hasOutTrans;) {
                        String s = si.next();
                        Set<LabeledTransition> tr = CLTS.outgoingEdgesOf(s);
                        for (Iterator<LabeledTransition> ti = tr.iterator(); ti.hasNext() && !hasOutTrans;) {
                            LabeledTransition t = ti.next();
                            String dst = t.getDst();
                            if (!s_sg.contains(dst)) {
                                hasOutTrans = true;
                            }
                        }
                    }
                    if (!hasOutTrans) {
                        //sg is a terminal SCC that should be collapsed
                        //collapsing the SCC ...
                        String s = new String("scc" + (new Integer(num)).toString());
                        mapSCC.put(s, s_sg);
                        //remove transitions of SCC from filtered CLTS
                        for (Iterator<LabeledTransition> i = tr_sg.iterator(); i.hasNext();) {
                            LabeledTransition tr = i.next();
                            filteredCLTS1.removeEdge(tr);
                        }
                        filteredCLTS1.addVertex(s);
                        Set<LabeledTransition> tr_fi = new HashSet<LabeledTransition>(filteredCLTS1.edgeSet());
                        Set<LabeledTransition> tr_in = new HashSet<LabeledTransition>();
                        //Set<LabeledTransition> tr_out = new HashSet<LabeledTransition>();
                        //TODO::use states of sg to find incomming trans
                        for (Iterator<LabeledTransition> i = tr_fi.iterator(); i.hasNext();) {
                            LabeledTransition tr = i.next();
                            String dst = tr.getDst();
                            if (s_sg.contains(dst)) {
                                tr_in.add(tr);
                            }
                        }
                        for (Iterator<LabeledTransition> i = tr_in.iterator(); i.hasNext();) {
                            LabeledTransition tr = i.next();
                            filteredCLTS1.removeEdge(tr);
                            filteredCLTS1.addEdge(tr.getSrc(), s, new LabeledTransition(new Label(tr.label.nc, tr.label.act)));
                        }
                        for (Iterator<String> i = s_sg.iterator(); i.hasNext();) {
                            filteredCLTS1.removeVertex(i.next());
                        }
                        num++;
                        //refactor :: visited.put(s, new HashSet<Integer>(arg.rtopologies));
                        Set<Integer> newTopo = new HashSet<Integer>();
                        for (Iterator<Integer> topoi = arg.rtopologies.iterator(); topoi.hasNext();) {
                            Integer i = topoi.next();
                            if (!arg.Valid(l, i)) {
                                newTopo.add(i);
                            }
                        }
                        //for these topologies that violate mu, backward analysis is never examined
                        visited.put(s, newTopo);
                        end.add(s);
                    } //end of collaspsing
                }
            }
        }
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
                    Set<LabeledTransition> tr = filteredCLTS1.incomingEdgesOf(item.state);
                    for (Iterator<LabeledTransition> it = tr.iterator(); it.hasNext();) {
                        LabeledTransition tt = it.next();
                        Set<Integer> newTopo = new HashSet<Integer>();
                        for (Iterator<Integer> ii = to.iterator(); ii.hasNext();) {
                            Integer i = ii.next();
                            if (arg.Valid(tt.label.nc, i)) //refactor : promote the condition
                            {
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
            Enumeration<Set<String>> SCCs = mapSCC.elements();
            boolean withinSCC = false;
            while (SCCs.hasMoreElements()) {
                if (((Set<String>) SCCs.nextElement()).contains(s)) {
                    withinSCC = true;
                    break;
                }
            }
            if (!visited.containsKey(s) && !withinSCC) {
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
