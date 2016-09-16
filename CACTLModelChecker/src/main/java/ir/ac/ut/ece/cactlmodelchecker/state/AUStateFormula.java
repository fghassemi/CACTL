/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.state;

import com.google.gson.Gson;
import ir.ac.ut.ece.cactlmodelchecker.ConstraintLabeledTransitionSystem;
import ir.ac.ut.ece.cactlmodelchecker.Item;
import ir.ac.ut.ece.cactlmodelchecker.LabeledTransition;
import ir.ac.ut.ece.cactlmodelchecker.NetworkConstraint;
import ir.ac.ut.ece.cactlmodelchecker.SCCInspector;
import ir.ac.ut.ece.cactlmodelchecker.action.StringActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.path.UntilFormula;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    public static final String FILE_PATH = "/home/ashkan/"; // TODO should be refactored and changed to a relative path!

    public AUStateFormula(UntilFormula _arg) {
        arg = _arg;
    }

    @Override
    public Set<String> findState(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta, Boolean counterExampleMode, TreeDepthIndicator depthIndicator) {
        String fileName = depthIndicator.depth.toString();
        depthIndicator.incrementDepth();
        Set<String> T1 = arg.phi1.findState(null, CLTS, zeta, counterExampleMode, depthIndicator);
        if (T1 == null) {
            return null;
        }
        Set<String> negT1 = new HashSet<String>(CLTS.vertexSet());
        negT1.removeAll(T1);
        depthIndicator.incrementDepth();
        Set<String> T2 = arg.phi2.findState(null, CLTS, zeta, counterExampleMode, depthIndicator);
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
        Set<CounterExample> counterExamples = new HashSet<>();
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
            String lastStateInBackwardAnalysis = null, firstStateInBackwardAnalysis = null;
            while (!stack.isEmpty()) {
                Item item = stack.pop();
                lastStateInBackwardAnalysis = item.state;
                if (firstStateInBackwardAnalysis == null) {
                    firstStateInBackwardAnalysis = item.state;
                }
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
            if (counterExampleMode) {
                if (T1.contains(lastStateInBackwardAnalysis)) {
                    Set<Integer> newTopologies = null;
                    for (CounterExample counterExample : counterExamples) {
                        if (counterExample.first.equals(lastStateInBackwardAnalysis)) {
                            newTopologies = counterExample.topologies;
                            break;
                        }
                    }
                    if (newTopologies == null) {
                        // if state isn't previously visited, we simply add a new
                        // item with the calculated topologies
                        counterExamples.add(new CounterExample(lastStateInBackwardAnalysis, firstStateInBackwardAnalysis, topo));
                    } else {
                        // if we have visited this state before, then we need to
                        // calculate the union of their topologies
                        newTopologies.addAll(topo);
                    }
                }
            }
        }

        if (counterExampleMode) {
            Gson gson = new Gson();
            writeOnDisk(gson.toJson(counterExamples), fileName, FILE_PATH);
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

    @Override
    public String toString() {
        return "A (" + arg.toString() + ")";
    }

    @Override
    public StateFormula reduce() {
        return this;
    }

    @Override
    public Set<Item> findCounterExample(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta) { // TODO implement!
        
        return null;
    }

    public static void writeOnDisk(String content, String fileName, String path) {
        try {
            File file = new File(path + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (IOException ex) {
            throw new RuntimeException("Couldn't write the content on disk, "
                    + "an exception occured while doing so", ex);
        }
    }

}
