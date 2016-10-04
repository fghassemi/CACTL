/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker;

import edu.princeton.cs.algorithms.Digraph;
import edu.princeton.cs.algorithms.TarjanSCC;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author ashkan
 */
public class SCCInspectorForUntidyGraphs extends SCCInspector {

    public Map<String, Integer> nameMapToIndex;
    public Map<Integer, String> indexMapToName;

    public SCCInspectorForUntidyGraphs(ConstraintLabeledTransitionSystem _CLTS) {
        // converts CLTS into its graph
        CLTS = _CLTS;
        Set<String> states = CLTS.vertexSet();
        graph = new Digraph(states.size());
        this.nameMapToIndex = new HashMap<>();
        this.indexMapToName = new HashMap<>();

        //if (E < 0) throw new IllegalArgumentException("Number of edges in a Digraph must be nonnegative");
        Integer index = 0;
        for (Iterator<String> stateIterator = states.iterator(); stateIterator.hasNext();) {
            String state = stateIterator.next();
            if (!nameMapToIndex.containsKey(state)) {
                nameMapToIndex.put(state, index);
                indexMapToName.put(index++, state);
            }
            Set<LabeledTransition> trans = CLTS.outgoingEdgesOf(state);
            for (Iterator<LabeledTransition> ti = trans.iterator(); ti.hasNext();) {
                LabeledTransition tr = ti.next();
                int src = nameMapToIndex.get(state);
                if (!nameMapToIndex.containsKey(tr.getDst())) {
                    nameMapToIndex.put(tr.getDst(), index);
                    indexMapToName.put(index++, tr.getDst());
                }
                int dst = nameMapToIndex.get(tr.getDst());
                // ((Bag)graph.adj(src)).
                graph.addEdge(src, dst);
            }
        }
    }

    @Override
    public Set<String>[] ComputeSCCc() {
        TarjanSCC SCC = new TarjanSCC(graph);
        // compute list of vertices in each strong component
        count = SCC.count();

        Set<String>[] hashedScc = (Set<String>[]) new HashSet[count];
        for (int i = 0; i < count; i++) {
            hashedScc[i] = new HashSet<String>();
        }
        for (Integer i = 0; i < graph.V(); i++) {
            hashedScc[SCC.id(i)].add(i.toString());
        }

        Set<String>[] scc = (Set<String>[]) new HashSet[hashedScc.length];
        Integer i = 0;
        for (Set<String> states : hashedScc) {
            Set<String> component = new HashSet<>();
            for (String state : states) {
                component.add(indexMapToName.get(Integer.parseInt(state)));
            }
            scc[i++] = component;
        }
        return scc;
    }

}
