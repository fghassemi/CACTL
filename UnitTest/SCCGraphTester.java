import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.alg.KosarajuStrongConnectivityInspector;
import org.jgrapht.graph.DirectedSubgraph;
import org.junit.Before;
import org.junit.Test;
import edu.princeton.cs.algs4.*;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;

public class SCCGraphTester {
	ConstraintLabeledTransitionSystem CLTS;
	@Before
	public void setUp() throws Exception {
		CLTS = CACTLMC.loadCLTS("D:\\leader\\tempr.aut");
		System.out.println("Running tests are startd ....");

		/*NetworkConstraint zeta = new NetworkConstraint();
		ActionFormula chi = new BasicActionFormula(true);
		Set<String> T1 = CLTS.vertexSet();
        Set<LabeledTransition> CCSTrans = new HashSet<LabeledTransition>();
		for (Iterator<String> si=T1.iterator(); si.hasNext();)
		{
		   String s = si.next();
		   Set<LabeledTransition> trans = CLTS.outgoingEdgesOf(s);
		   for (Iterator<LabeledTransition> ti=trans.iterator(); ti.hasNext();)
		   {
			   LabeledTransition tr = ti.next();
			   String dst = tr.getDst();
			   if (zeta.conforms(tr.label.nc))
				   if (T1.contains(dst) && chi.satisfy(new StringActionFormula(tr.label.act)))
					   CCSTrans.add(tr);
		   }         
		}*/
	}
	//@Test
	public void SCCMaker()
	{
		//generating a new CLTS that all its states satisfy \phi1 and transitions satisfy \chi1 and conform to zeta
		ConstraintLabeledTransitionSystem filteredCLTS = new ConstraintLabeledTransitionSystem(CLTS.edgeSet(),CLTS.vertexSet(),CLTS.InitialStates());
		
		KosarajuStrongConnectivityInspector<String, LabeledTransition> sci =
            new KosarajuStrongConnectivityInspector<String, LabeledTransition>(filteredCLTS);
        List<DirectedSubgraph<String, LabeledTransition>> stronglyConnectedSubgraphs = sci.stronglyConnectedSubgraphs();
        System.out.println(stronglyConnectedSubgraphs.size());

	}
	
	@Test
	public void TarjonTest()
	{
		SCCInspector ins = new SCCInspector(CLTS);
		Set<String>[] comps = ins.ComputeSCCc();
        for (int i = 0; i < ins.count; i++) {
            System.out.println(comps[i]); 
        }
	}
}
