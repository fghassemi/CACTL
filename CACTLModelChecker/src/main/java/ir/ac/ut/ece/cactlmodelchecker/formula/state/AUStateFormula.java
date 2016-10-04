/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.formula.state;

import ir.ac.ut.ece.cactlmodelchecker.utils.TreeDepthIndicator;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import ir.ac.ut.ece.cactlmodelchecker.ConstraintLabeledTransitionSystem;
import ir.ac.ut.ece.cactlmodelchecker.Item;
import ir.ac.ut.ece.cactlmodelchecker.LabeledTransition;
import ir.ac.ut.ece.cactlmodelchecker.NetworkConstraint;
import ir.ac.ut.ece.cactlmodelchecker.SCCInspector;
import ir.ac.ut.ece.cactlmodelchecker.SCCInspectorForUntidyGraphs;
import ir.ac.ut.ece.cactlmodelchecker.formula.action.StringActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.path.UntilFormula;
import ir.ac.ut.ece.cactlmodelchecker.state.CounterExample;
import ir.ac.ut.ece.cactlmodelchecker.state.CounterExampleWithTopologies;
import ir.ac.ut.ece.cactlmodelchecker.utils.IOUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author ashkan
 */
public class AUStateFormula implements StateFormula {

    protected static final String TOPOLOGIES_MAP_POSTFIX = "-topologiesMap";
    protected static final String STATES_POSTFIX = "-states";
    protected static final String INITIAL_STATES_POSTFIX = "-initialStates";
    protected static final String TRANSITIONS_POSTFIX = "-transitions";

    public UntilFormula arg;

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
        Set<LabeledTransition> SCCTrans = new HashSet<LabeledTransition>();
        Set<String> EXSCCStates = new HashSet<String>();
        boolean hasBadTrans = false;
        for (Iterator<String> si = T1.iterator(); si.hasNext();) {
            String s = si.next();
            hasBadTrans = false;
            Set<LabeledTransition> trans = CLTS.outgoingEdgesOf(s);
            Set<LabeledTransition> tempSCCTrans = new HashSet<LabeledTransition>();
            for (Iterator<LabeledTransition> ti = trans.iterator(); ti.hasNext() && !hasBadTrans;) {
                LabeledTransition tr = ti.next();
                String dst = tr.getDst();
                if (zeta.conforms(tr.label.nc)) {
                    if (T2.contains(dst) && arg.chi2.satisfy(new StringActionFormula(tr.label.act))) {
                        EXSCCStates.add(s);
                    } else if (T1.contains(dst) && arg.chi1.satisfy(new StringActionFormula(tr.label.act))) {
                        tempSCCTrans.add(tr);
                    } else {
                        hasBadTrans = true;
                    }
                }
            }
            if (!hasBadTrans) {
                SCCTrans.addAll(tempSCCTrans);
            }
        }
        //generating a new CLTS that all its states satisfy \phi1 and transitions satisfy \chi1 and conform to zeta
        ConstraintLabeledTransitionSystem filteredCLTS = new ConstraintLabeledTransitionSystem(SCCTrans, T1, CLTS.InitialStates());
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
                if (EXSCCStates.contains(s)) {
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
                // a (chi,phi)-SCC with no (chi',phi') state
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

        /* for counter example mode purposes only */
        HashMap<String, Set<Integer>> topologiesMap = new HashMap<>();// TODO rename this!
        Set<LabeledTransition> allTransitionsInTheWay = new HashSet<>();
        Set<String> counterExamplesInitialStates = new HashSet<>();
        Set<String> counterExamplesStates = new HashSet<>();

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
            Set<String> visitedStatesInBackwardAnalysis = new HashSet<>();
            Set<LabeledTransition> transitions = new HashSet<>();
            String lastStateInBackwardAnalysis = null;
            while (!stack.isEmpty()) {
                Item item = stack.pop();
                lastStateInBackwardAnalysis = item.state;
                visitedStatesInBackwardAnalysis.add(item.state);
                //add to violate : no need as we use range of visited
//                if (item.state.equals("0")) {
//                    System.out.println("nahaaara");
//                } else {
//                    System.out.println("over the path" + item.state);
//                }
                Set<LabeledTransition> tr = filteredCLTS.incomingEdgesOf(item.state);
                transitions.addAll(tr);
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
                    for (Map.Entry<String, Set<Integer>> topologyMap : topologiesMap.entrySet()) {
                        if (topologyMap.getKey().equals(lastStateInBackwardAnalysis)) {
                            newTopologies = topologyMap.getValue();
                            break;
                        }
                    }
                    if (newTopologies == null) {
                        // if state isn't previously visited, we simply add a new
                        // item with the calculated topologies
                        topologiesMap.put(lastStateInBackwardAnalysis, topo);
                    } else {
                        // if we have visited this state before, then we need to
                        // calculate the union of their topologies
                        newTopologies.addAll(topo);
                    }
                    allTransitionsInTheWay.addAll(transitions);
                    counterExamplesInitialStates.add(lastStateInBackwardAnalysis);
                    counterExamplesStates.addAll(visitedStatesInBackwardAnalysis);
                }
            }
        }

        if (counterExampleMode) {
            Set<LabeledTransition> counterExamplesTransitions = new HashSet<>();
            for (LabeledTransition transition : allTransitionsInTheWay) {
                if (counterExamplesStates.contains(transition.getSrc()) &&
                        counterExamplesStates.contains(transition.getDst())) {
                    counterExamplesTransitions.add(transition);
                }
            }
            Gson gson = new Gson();
            IOUtils.writeOnDisk(gson.toJson(counterExamplesTransitions), fileName + TRANSITIONS_POSTFIX, IOUtils.FILE_DIRECTORY);
            IOUtils.writeOnDisk(gson.toJson(counterExamplesInitialStates), fileName + INITIAL_STATES_POSTFIX, IOUtils.FILE_DIRECTORY);
            IOUtils.writeOnDisk(gson.toJson(counterExamplesStates), fileName + STATES_POSTFIX, IOUtils.FILE_DIRECTORY);
            IOUtils.writeOnDisk(gson.toJson(topologiesMap), fileName + TOPOLOGIES_MAP_POSTFIX, IOUtils.FILE_DIRECTORY);
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
    public CounterExample findCounterExample(Set<String> initialStates, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta, TreeDepthIndicator depthIndicator) {
        String fileName = depthIndicator.depth.toString();
        ConstraintLabeledTransitionSystem counterExamplesCLTS = this.loadCLTS(fileName);
        Map<String, Set<Integer>> topologiesMap = this.loadTopologiesMap(fileName);
        SCCInspector inspector = new SCCInspectorForUntidyGraphs(counterExamplesCLTS);
        Set<String>[] SCCs = inspector.ComputeSCCc();
        if (initialStates == null) {
            initialStates = counterExamplesCLTS.InitialStates();
        }
        Set<String> leaves = new HashSet<>();
        for (int scc = 0; scc < inspector.count; scc++) {
            Set<String> s_sg = SCCs[scc];
            for (Iterator<String> si = s_sg.iterator(); si.hasNext();) {
                leaves.add(si.next());
            }
        }

        for (String leafState : leaves) {
            String type = this.getType(counterExamplesCLTS, leafState, CLTS);
            switch (type) {
                case StateFormula.DEADLOCK:
                    return findCounterExampleForDeadlockedLeafState(leafState, initialStates, counterExamplesCLTS, topologiesMap);
                case StateFormula.NO_VALID_OUTGOING_TRANSITION:
                // find the type of the state
                // if type one, 
                case StateFormula.INFINITE_LOOP:
            }
        }
        return null;
    }

    protected ConstraintLabeledTransitionSystem loadCLTS(String fileName) {
        Gson gson = new Gson();
        Set<String> states = gson.fromJson(IOUtils.readFile(fileName + STATES_POSTFIX, IOUtils.FILE_DIRECTORY), new TypeToken<Set<String>>() {
        }.getType());
        Set<String> initialStates = gson.fromJson(IOUtils.readFile(fileName + INITIAL_STATES_POSTFIX, IOUtils.FILE_DIRECTORY), new TypeToken<Set<String>>() {
        }.getType());
        Set<LabeledTransition> transitions = gson.fromJson(IOUtils.readFile(fileName + TRANSITIONS_POSTFIX, IOUtils.FILE_DIRECTORY), new TypeToken<Set<LabeledTransition>>() {
        }.getType());
        return new ConstraintLabeledTransitionSystem(transitions, states, initialStates);
    }

    protected Map<String, Set<Integer>> loadTopologiesMap(String fileName) {
        Gson gson = new Gson();
        return gson.fromJson(IOUtils.readFile(fileName + TOPOLOGIES_MAP_POSTFIX, IOUtils.FILE_DIRECTORY), new TypeToken<Map<String, Set<Integer>>>() {
        }.getType());
    }

    protected CounterExample findCounterExampleForDeadlockedLeafState(String leafState, Set<String> initialStates, ConstraintLabeledTransitionSystem counterExamplesCLTS, Map<String, Set<Integer>> topologiesMap) {
        Set<LabeledTransition> path = new HashSet<>();
        Map<String, LabeledTransition> predecessor = new HashMap<>();
        Set<String> visitedStates = new HashSet<>();
        String initialStateReached = null;
        LinkedList<String> stack = new LinkedList<>();
        stack.addFirst(leafState);
        while (!stack.isEmpty()) {
            String state = stack.removeFirst();
            visitedStates.add(state);
            if (initialStates.contains(state)) {
                initialStateReached = state;
                break;
            }
            Set<LabeledTransition> incomingEdges = counterExamplesCLTS.incomingEdgesOf(state);
            for (LabeledTransition edge : incomingEdges) {
                if (!visitedStates.contains(edge.getSrc())) {
                    predecessor.put(edge.getSrc(), edge);
                    stack.addFirst(edge.getSrc());
                }
            }
        }
        // Generating the path for the found counter example
        String stateInPath = initialStateReached;
        LinkedList<LabeledTransition> counterExamplePath = new LinkedList<>();
        while (!stateInPath.equals(leafState)) {
            LabeledTransition edge = predecessor.get(stateInPath);
            counterExamplePath.add(edge);
            stateInPath = edge.getDst();
        }
        return new CounterExampleWithTopologies(counterExamplePath, initialStateReached, topologiesMap.get(initialStateReached));
    }

    private String getType(ConstraintLabeledTransitionSystem counterExamplesCLTS, String state, ConstraintLabeledTransitionSystem initialCLTS) { // check this function
        if (counterExamplesCLTS.outDegreeOf(state) == 0) { // type 1 or 2
            Integer outDegreeOfStateInTheMainCLTS = initialCLTS.outDegreeOf(state);
            return outDegreeOfStateInTheMainCLTS == 0 ? StateFormula.DEADLOCK : StateFormula.NO_VALID_OUTGOING_TRANSITION;
        } else {
            return StateFormula.INFINITE_LOOP;
        }
    }

    @Override
    public void calculateTreeSize(TreeDepthIndicator depthIndicator) {
        depthIndicator.incrementDepth();
        arg.phi1.calculateTreeSize(depthIndicator);
        depthIndicator.incrementDepth();
        arg.phi2.calculateTreeSize(depthIndicator);
    }
}
