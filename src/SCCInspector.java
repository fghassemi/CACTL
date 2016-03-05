import edu.princeton.cs.algs4.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SCCInspector {
	// the constraint to use this class is that the states of the CLTS should be numbered form 0 and consequently
	Digraph graph ;
	ConstraintLabeledTransitionSystem CLTS;
	public int count; 
	public SCCInspector(ConstraintLabeledTransitionSystem _CLTS)
	{
		// converts CLTS into its graph
		CLTS = _CLTS ; 
    	Set<String> states = CLTS.vertexSet();
    	graph = new Digraph(states.size());
        
        //if (E < 0) throw new IllegalArgumentException("Number of edges in a Digraph must be nonnegative");
		for (Iterator<String> si=states.iterator(); si.hasNext();)
		{
		   String s = si.next();
		   Set<LabeledTransition> trans = CLTS.outgoingEdgesOf(s);
		   for (Iterator<LabeledTransition> ti=trans.iterator(); ti.hasNext();)
		   {
			   LabeledTransition tr = ti.next();
			   int src = Integer.parseInt(tr.getSrc());
			   int dst = Integer.parseInt(tr.getDst());
			  // ((Bag)graph.adj(src)).
			   graph.addEdge(src , dst);
			}         
		}
		
	}
	public Set<String>[] ComputeSCCc()
	{
		TarjanSCC CCS = new TarjanSCC(graph);
        // compute list of vertices in each strong component
        count = CCS.count();

        Set<String>[] components = (Set<String>[]) new HashSet[count];
        for (int i = 0; i < count; i++) {
            components[i] = new HashSet<String>();
        }
    	Set<String> states = CLTS.vertexSet();
		for (Iterator<String> si=states.iterator(); si.hasNext();)
		{
		   String s = si.next();
            components[CCS.id(Integer.parseInt(s))].add(s);
        }
	        
		return components;
	}

}
