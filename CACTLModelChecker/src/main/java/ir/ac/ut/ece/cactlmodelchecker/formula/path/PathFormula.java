/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.formula.path;

import com.google.gson.Gson;
import ir.ac.ut.ece.cactlmodelchecker.formula.state.BasicStateFormula;
import ir.ac.ut.ece.cactlmodelchecker.ConstraintLabeledTransitionSystem;
import ir.ac.ut.ece.cactlmodelchecker.LabeledTransition;
import ir.ac.ut.ece.cactlmodelchecker.NetworkConstraint;
import ir.ac.ut.ece.cactlmodelchecker.Pair;
import ir.ac.ut.ece.cactlmodelchecker.formula.action.ActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.action.StringActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.state.StateFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.topology.TopologyFormula;
import ir.ac.ut.ece.cactlmodelchecker.utils.IOUtils;
import ir.ac.ut.ece.cactlmodelchecker.utils.TreeDepthIndicator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.jgrapht.graph.DirectedSubgraph;

/**
 *
 * @author ashkan
 */
public abstract class PathFormula {
    public static final String basicStateFormulaClassName = BasicStateFormula.class.getName();
    public StateFormula phi1;
    public StateFormula phi2;
    public ActionFormula chi1;
    public ActionFormula chi2;
    TopologyFormula mu;
    static boolean[][][] topologies;
    static int numofNodes;
    static int numofTopo;
    public Set<Integer> rtopologies;
    static {
        PathFormula.numofNodes = 4;
        PathFormula.numofTopo = power(4, (PathFormula.numofNodes * PathFormula.numofNodes - PathFormula.numofNodes) / 2);
        ;
        PathFormula.topologies = new boolean[PathFormula.numofTopo][PathFormula.numofNodes][PathFormula.numofNodes];
        fillTopologies();
    }

    public PathFormula(int dim, TopologyFormula _mu) {
        if (dim != PathFormula.numofNodes) {
            PathFormula.numofNodes = dim;
            PathFormula.numofTopo = power(4, (dim * dim - dim) / 2);
            PathFormula.topologies = new boolean[PathFormula.numofTopo][dim][dim];
            fillTopologies();
        }
        //if (!_mu.equals(mu))
        {
            //System.out.println("mu is not equal....");
            rtopologies = new HashSet<Integer>();
            mu = _mu;
            refineTopologies(mu);
        }
    }

    public void refineTopologies(TopologyFormula mu) {
        boolean[][] topo_n = new boolean[PathFormula.numofNodes][PathFormula.numofNodes];
        boolean[][] topo_o = new boolean[PathFormula.numofNodes][PathFormula.numofNodes];
        boolean[][] r = new boolean[PathFormula.numofNodes][PathFormula.numofNodes];
        rtopologies.clear();
        for (int n = 0; n < PathFormula.numofTopo; n++) {
            for (int j = 0; j < PathFormula.numofNodes; j++) {
                for (int k = 0; k < PathFormula.numofNodes; k++) {
                    topo_n[j][k] = PathFormula.topologies[n][j][k];
                }
            }
            if (mu.satisfy(topo_n, PathFormula.numofNodes)) {
                boolean stop = false;
                for (Iterator<Integer> ii = rtopologies.iterator(); ii.hasNext() && !stop;) {
                    Integer o = ii.next();
                    //finding old topology
                    for (int j = 0; j < PathFormula.numofNodes; j++) {
                        for (int k = 0; k < PathFormula.numofNodes; k++) {
                            topo_o[j][k] = PathFormula.topologies[o][j][k];
                        }
                    }
                    //And of old and new topology
                    for (int j = 0; j < PathFormula.numofNodes; j++) {
                        for (int k = 0; k < PathFormula.numofNodes; k++) {
                            r[j][k] = topo_n[j][k] && topo_o[j][k];
                        }
                    }
                    //compare result to old
                    boolean equalO = true;
                    for (int j = 0; j < PathFormula.numofNodes && equalO; j++) {
                        for (int k = 0; k < PathFormula.numofNodes && equalO; k++) {
                            equalO = (r[j][k] == topo_o[j][k]);
                        }
                    }
                    //compare result to new in case result was not equal to old
                    boolean equalN = !equalO;
                    for (int j = 0; j < PathFormula.numofNodes && equalN; j++) {
                        for (int k = 0; k < PathFormula.numofNodes && equalN; k++) {
                            equalN = (r[j][k] == topo_n[j][k]);
                        }
                    }
                    if (equalO) {
                        stop = true;
                    } else if (equalN) {
                        rtopologies.remove(o);
                    }
                }
                if (!stop) {
                    rtopologies.add(new Integer(n));
                }
            }
        }
    }

    public abstract Set<String> findState(Set<String> initial, ConstraintLabeledTransitionSystem CLTS, NetworkConstraint zeta, Boolean counterExampleMode, TreeDepthIndicator depthIndicator);

    public boolean OverInvalidPath(NetworkConstraint temp) {
        // returns true if temp is an invalid path for mu
        // if there is no topology in topology formula that conforms to temp
        boolean hasNoValid = true;
        boolean[][] topo = new boolean[PathFormula.numofNodes][PathFormula.numofNodes];
        int index = 0;
        for (Iterator<Integer> i = rtopologies.iterator(); i.hasNext();) {
            index = i.next().intValue();
            for (int j = 0; j < PathFormula.numofNodes; j++) {
                for (int k = 0; k < PathFormula.numofNodes; k++) {
                    topo[j][k] = PathFormula.topologies[index][j][k];
                }
            }
            //if (mu.satisfy(topo,numofNodes) && temp.conforms(extract_connectivity(topo)))
            if (temp.conforms(extract_connectivity(topo))) {
                hasNoValid = false;
                break;
            }
        }
        return hasNoValid;
    }

    public boolean Valid(NetworkConstraint temp, Integer index) {
        boolean[][] topo = new boolean[PathFormula.numofNodes][PathFormula.numofNodes];
        for (int j = 0; j < PathFormula.numofNodes; j++) {
            for (int k = 0; k < PathFormula.numofNodes; k++) {
                topo[j][k] = PathFormula.topologies[index][j][k];
            }
        }
        return temp.conforms(extract_connectivity(topo));
    }

    private static NetworkConstraint extract_connectivity(boolean[][] topo) {
        NetworkConstraint nc = new NetworkConstraint();
        for (int i = 0; i < PathFormula.numofNodes; i++) {
            for (int j = 0; j < PathFormula.numofNodes; j++) {
                if (topo[i][j]) {
                    String src;
                    String dst;
                    dst = (i == 0) ? "A" : (i == 1) ? "B" : (i == 2) ? "C" : (i == 3) ? "D" : "E";
                    src = (j == 0) ? "A" : (j == 1) ? "B" : (j == 2) ? "C" : (j == 3) ? "D" : "E";
                    Pair p = new Pair(src, dst, true);
                    nc.add(p);
                }
            }
        }
        return nc;
    }

    private static void fillTopologies() {
        int flatness = PathFormula.numofNodes * PathFormula.numofNodes - PathFormula.numofNodes;
        int num = PathFormula.numofTopo;
        boolean[][] flat = new boolean[num][flatness];
        fill(flat, flatness, 0, 0, num / 2, false);
        fill(flat, flatness, 0, num / 2, num, true);
        //Unflattering
        int count;
        for (int i = 0; i < num; i++) {
            count = 0;
            for (int j = 0; j < PathFormula.numofNodes; j++) {
                for (int k = 0; k < PathFormula.numofNodes; k++) {
                    if (j == k) {
                        PathFormula.topologies[i][j][k] = false;
                    } else {
                        PathFormula.topologies[i][j][k] = flat[i][count];
                        count++;
                    }
                }
            }
        }
    }

    private static void fill(boolean[][] flat, int flatness, int i, int j, int k, boolean b) {
        for (int l = j; l < k && b; l++) {
            flat[l][i] = b;
        }
        if (i + 1 < flatness) {
            fill(flat, flatness, i + 1, j, (j + k) / 2, false);
            fill(flat, flatness, i + 1, (j + k) / 2, k, true);
        }
    }

    /*	static private int factorial(int n)
    {
    if ((n==0)|(n==1)) return 1;
    else return n*factorial(n-1);
    }*/
    private static int power(int n, int m) {
        if (m == 0) {
            return 1;
        } else if (m == 1) {
            return n;
        } else {
            return n * power(n, m - 1);
        }
    }

    public static NetworkConstraint Accumulate(DirectedSubgraph<String, LabeledTransition> sg) {
        NetworkConstraint l = new NetworkConstraint();
        Set<LabeledTransition> trans_t = sg.edgeSet();
        for (Iterator<LabeledTransition> j = trans_t.iterator(); j.hasNext();) {
            LabeledTransition tr = j.next();
            l.update(tr.label.nc);
        }
        return l;
    }
    
    protected void generateCounterExamplesCLTSAndSaveIt(ConstraintLabeledTransitionSystem InitialCLTS, Set<String> visited, Set<String> T, NetworkConstraint zeta, String fileName) {
        /*
        backwards = clts / visited
        from backwards, while its not empty
        pop one and add it to new clts
        also add its incoming transitions to the new clts, and the edges
        and the states that do not have any incoming edge, they are 
        included in the initial states of the counterExamplesCLTS
        */
        Set<String> backwardStates = new HashSet<>(InitialCLTS.vertexSet());
        Set<String> counterExamplesStates = new HashSet<>();
        Set<LabeledTransition> counterExamplesTransitions = new HashSet<>();
        Set<String> counterExamplesInitialStates = new HashSet<>();
        backwardStates.removeAll(visited);
        while (!backwardStates.isEmpty()) {
            String s = backwardStates.iterator().next();
            backwardStates.remove(s);
            counterExamplesStates.add(s);
            Set<LabeledTransition> trans_t = InitialCLTS.incomingEdgesOf(s);
            /* If there are no incoming edges to this state, then
               it is one of our initial states. */
            if (trans_t.isEmpty()) {
                counterExamplesInitialStates.add(s);
            }
            
            //for all (t,(c,a),s)\in CLTS.Trans that t|= phi1 and (a|= \chi_1 or (c={} and a=tau)))
            for (Iterator<LabeledTransition> j = trans_t.iterator(); j.hasNext();) {
                LabeledTransition tr = j.next();
                String t = tr.getSrc();
                if (T.contains(t) && chi1.satisfy(new StringActionFormula(tr.label.act)) && zeta.conforms(tr.label.nc)) {
                    if (!visited.contains(t)) {
                        backwardStates.add(t);
                        counterExamplesTransitions.add(tr);
                    }
                }
            }
        }
        ConstraintLabeledTransitionSystem counterExamplesCLTS =
                new ConstraintLabeledTransitionSystem(
                        counterExamplesTransitions, counterExamplesStates, counterExamplesInitialStates);
        Object[] objects = new Object[1];
        objects[0] = counterExamplesCLTS;
        Gson gson = new Gson();
        IOUtils.writeOnDisk(gson.toJson(objects), fileName, IOUtils.FILE_DIRECTORY);
    }
    
    public void calculateTreeSize(TreeDepthIndicator depthIndicator) {
        depthIndicator.incrementDepth();
        phi1.calculateTreeSize(depthIndicator);
        depthIndicator.incrementDepth();
        phi2.calculateTreeSize(depthIndicator);
    };
    
}
