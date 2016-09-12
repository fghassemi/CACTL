/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.path;

import ir.ac.ut.ece.cactlmodelchecker.state.BasicStateFormula;
import ir.ac.ut.ece.cactlmodelchecker.ConstraintLabeledTransitionSystem;
import ir.ac.ut.ece.cactlmodelchecker.Label;
import ir.ac.ut.ece.cactlmodelchecker.LabeledTransition;
import ir.ac.ut.ece.cactlmodelchecker.NetworkConstraint;
import ir.ac.ut.ece.cactlmodelchecker.action.ActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.action.OrActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.action.StringActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.state.StateFormula;
import ir.ac.ut.ece.cactlmodelchecker.topology.TopologyFormula;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.DirectedSubgraph;

/**
 *
 * @author ashkan
 */
public class UnlessFormula extends PathFormula {

    public UnlessFormula(StateFormula _phi1, StateFormula _phi2, ActionFormula _chi1, ActionFormula _chi2, TopologyFormula _mu, int numofnodes) {
        super(numofnodes, _mu);
        phi1 = _phi1;
        phi2 = _phi2;
        chi1 = _chi1;
        chi2 = _chi2;
        mu = _mu;
    }

    @Override
    public Set<String> findState(Set<String> initial, ConstraintLabeledTransitionSystem InitialCLTS, NetworkConstraint zeta) {
        Set<String> T = phi1.findState(null, InitialCLTS, zeta, false, null);
        //when initial is not empty, T should include initials, otherwise null is returned
        Set<String> copy_of_T = new HashSet<String>(T);
        if (initial != null) {
            copy_of_T.retainAll(initial);
        }
        if (initial != null && !initial.isEmpty() && copy_of_T.isEmpty()) {
            return null;
        }
        Set<String> T2 = phi2.findState(null, InitialCLTS, zeta, false, null);
        //working on the copy of input CLTS
        ConstraintLabeledTransitionSystem CLTS = new ConstraintLabeledTransitionSystem(InitialCLTS);
        //generating a new CLTS that all its states satisfy \phi1 and transitions satisfy \chi1 and conform to zeta
        ConstraintLabeledTransitionSystem filteredCLTS = new ConstraintLabeledTransitionSystem(InitialCLTS);
        filteredCLTS.filterTransitions(chi1, zeta);
        filteredCLTS.restrictToStates(T);
        //removing transitions from CLTS that do not satisfy either \chi_1 or \chi_2 and do not conform to \zeta
        CLTS.filterTransitions(new OrActionFormula(chi1, chi2), zeta);
        //making the set of states for backward checking
        Set<String> backwards = new HashSet<String>();
        //this case should be done in case phi2 or chi2 is not false
        StateFormula phiTemp = phi2.reduce();
        //Hashtable<String,Tag> htb = new Hashtable<String,Tag>() ;
        boolean reduction = (phiTemp.getClass().getName().equals(basicStateFormulaClassName) && !((BasicStateFormula) phiTemp).val) || (chi2.toSet(CLTS.getAct()).isEmpty());
        if (!reduction && T2 != null && !T2.isEmpty()) {
            for (Iterator<String> i = T2.iterator(); i.hasNext();) {
                String t = i.next();
                Set<LabeledTransition> trans_t = CLTS.incomingEdgesOf(t);
                //for all (s,(c,a),t)\in CLTS.Trans that a|= \chi_2 and s|=phi1
                for (Iterator<LabeledTransition> j = trans_t.iterator(); j.hasNext();) {
                    LabeledTransition tr = j.next();
                    String s = tr.getSrc();
                    if (T.contains(s) && chi2.satisfy(new StringActionFormula(tr.label.act))) {
                        backwards.add(s);
                        //htb.put(t,new Tag(tr.label.nc,d,null));
                    }
                }
            }
        }
        //ready to do the backward analysis
        Set<String> visited = new HashSet<String>();
        while (!backwards.isEmpty()) {
            // choose s \in backwards
            String s = backwards.iterator().next();
            //System.out.println("state "+s+" is checking");
            backwards.remove(s);
            visited.add(s);
            Set<LabeledTransition> trans_t = filteredCLTS.incomingEdgesOf(s);
            //for all (t,(c,a),s)\in CLTS.Trans that t|= phi1 and (a|= \chi_1 or (c={} and a=tau)))
            for (Iterator<LabeledTransition> j = trans_t.iterator(); j.hasNext();) {
                LabeledTransition tr = j.next();
                String t = tr.getSrc();
                //if (T.contains(t)) //&&(chi1.satisfy(new StringActionFormula(tr.label.act))))
                {
                    if (!visited.contains(t)) {
                        backwards.add(t);
                        //htb.put(t,new Tag(tr.label.nc,s,htb.get(s)) );
                    }
                }
            }
        }
        // finish of backward analysis
        // start of second backward analysis to find states with an invalid path for mu
        filteredCLTS.removeAllVertices(visited);
        StrongConnectivityInspector<String, LabeledTransition> sci = new StrongConnectivityInspector<String, LabeledTransition>(filteredCLTS);
        List<DirectedSubgraph<String, LabeledTransition>> stronglyConnectedSubgraphs = sci.stronglyConnectedSubgraphs();
        //Collapsing SCCs to a state
        int num = 0; //counting SCC
        Hashtable<String, Set<String>> mapSCC = new Hashtable<String, Set<String>>();
        for (Iterator<DirectedSubgraph<String, LabeledTransition>> k = stronglyConnectedSubgraphs.iterator(); k.hasNext();) {
            //System.out.println("SCC is collapsed: "+k);
            DirectedSubgraph<String, LabeledTransition> sg = k.next();
            if (sg.edgeSet().size() > 0) {
                //NetworkConstraint l = Accumulate(sg);
                Set<LabeledTransition> tr_sg = sg.edgeSet();
                Set<String> s_sg = sg.vertexSet();
                //remove transitions of SCC from filtered CLTS
                for (Iterator<LabeledTransition> i = tr_sg.iterator(); i.hasNext();) {
                    LabeledTransition tr = i.next();
                    filteredCLTS.removeEdge(tr);
                }
                //collapsing the SCC ...
                String s = new String("scc" + (new Integer(num)).toString());
                mapSCC.put(s, s_sg);
                filteredCLTS.addVertex(s);
                Set<LabeledTransition> tr_fi = new HashSet<LabeledTransition>(filteredCLTS.edgeSet());
                Set<LabeledTransition> tr_in = new HashSet<LabeledTransition>();
                Set<LabeledTransition> tr_out = new HashSet<LabeledTransition>();
                for (Iterator<LabeledTransition> i = tr_fi.iterator(); i.hasNext();) {
                    LabeledTransition tr = i.next();
                    //classifies transitions in terms of incoming/outcoming to/from the SCC
                    String dst = tr.getDst();
                    String src = tr.getSrc();
                    if (s_sg.contains(dst)) {
                        tr_in.add(tr);
                    } else if (s_sg.contains(src)) {
                        tr_out.add(tr);
                    }
                }
                for (Iterator<LabeledTransition> i = tr_out.iterator(); i.hasNext();) {
                    LabeledTransition tr = i.next();
                    filteredCLTS.removeEdge(tr);
                    filteredCLTS.addEdge(s, tr.getDst(), new LabeledTransition(new Label(tr.label.nc, tr.label.act)));
                }
                for (Iterator<LabeledTransition> i = tr_in.iterator(); i.hasNext();) {
                    LabeledTransition tr = i.next();
                    filteredCLTS.removeEdge(tr);
                    filteredCLTS.addEdge(tr.getSrc(), s, new LabeledTransition(new Label(tr.label.nc, tr.label.act)));
                }
                for (Iterator<String> i = s_sg.iterator(); i.hasNext();) {
                    filteredCLTS.removeVertex(i.next());
                }
                num++;
                //TODO: deleting in/out transitions
            }
        }
        Set<String> backwardAnalysis = new HashSet<String>(filteredCLTS.vertexSet());
        backwardAnalysis.removeAll(visited);
        //Hashtable<String,NetworkConstraint> ht = new Hashtable<String,NetworkConstraint>() ;
        Set<LabeledTransition> trans = null;
        for (Iterator<String> i = backwardAnalysis.iterator(); i.hasNext();) {
            String si = i.next();
            trans = filteredCLTS.outgoingEdgesOf(si);
            //visited is evolving ....
            if (!visited.contains(si) && trans.isEmpty()) {
                Set<LabeledTransition> tr = (!mapSCC.containsKey(si)) ? CLTS.outgoingEdgesOf(si) : null;
                // start from a SCC or a deadlock state
                if (tr == null || tr.isEmpty()) {
                    pred(si, filteredCLTS, visited, mapSCC);
                }
            }
        }
        return visited;
    }

    public String toString() {
        return phi1.toString() + "{" + chi1.toString() + "}W^{" + mu.toString() + "} {" + chi2.toString() + "}" + phi2.toString();
    }

    private static void pred(String si, ConstraintLabeledTransitionSystem CLTS, Set<String> result, Hashtable<String, Set<String>> mapSCC) {
        //System.out.println("Preceding of "+si+" is computed");
        if (mapSCC.containsKey(si)) {
            result.addAll(mapSCC.get(si));
        } else {
            result.add(si);
        }
        Set<LabeledTransition> trans_t = CLTS.incomingEdgesOf(si);
        for (Iterator<LabeledTransition> j = trans_t.iterator(); j.hasNext();) {
            LabeledTransition tr = j.next();
            String t = tr.getSrc();
            if (!result.contains(t)) {
                pred(t, CLTS, result, mapSCC);
            }
        }
        //System.out.println("The preceding of "+si+" is "+result);
    }
    
}
