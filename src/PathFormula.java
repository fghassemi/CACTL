import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.DirectedSubgraph;

public abstract class PathFormula 
{
	 StateFormula phi1;
	 StateFormula phi2;
	
	 ActionFormula chi1;
	 ActionFormula chi2;
	
	 TopologyFormula mu;
	 static boolean[][][] topologies ;
	 static int numofNodes ;
	 static int numofTopo ;
	 Set<Integer> rtopologies;

	 static 
	 {
		numofNodes = 4 ;
		numofTopo = power(4,(numofNodes*numofNodes-numofNodes)/2);;
		topologies = new boolean[numofTopo][numofNodes][numofNodes] ;
		fillTopologies();
	 }
	 
	 public PathFormula(int dim,TopologyFormula _mu)
	 {
		if (dim!=numofNodes)
		{
			numofNodes = dim;
			numofTopo = power(4,(dim*dim-dim)/2);
			topologies = new boolean[numofTopo][dim][dim] ;
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
		boolean[][] topo_n = new boolean[numofNodes][numofNodes];
		boolean[][] topo_o = new boolean[numofNodes][numofNodes];		
		boolean[][] r = new boolean[numofNodes][numofNodes];
		
		rtopologies.clear();
		for (int n=0; n<numofTopo; n++)
		{
			for(int j=0; j<numofNodes; j++)
				for (int k=0; k<numofNodes; k++)
					topo_n[j][k] = topologies[n][j][k];
			if (mu.satisfy(topo_n,numofNodes))
			{
				boolean stop = false;
				for(Iterator<Integer> ii = rtopologies.iterator(); ii.hasNext() && !stop;)
				{
					Integer o = ii.next();
					//finding old topology
					for(int j=0; j<numofNodes; j++)
						for (int k=0; k<numofNodes; k++)
							topo_o[j][k] = topologies[o][j][k];
					//And of old and new topology
					for(int j=0; j<numofNodes; j++)
						for (int k=0; k<numofNodes; k++)
							r[j][k] = topo_n[j][k] && topo_o[j][k];
					//compare result to old
					boolean equalO = true;
					for(int j=0; j<numofNodes && equalO; j++)
						for (int k=0; k<numofNodes && equalO; k++)
							equalO = (r[j][k] == topo_o[j][k]);
					//compare result to new in case result was not equal to old
					boolean equalN = !equalO;
					for(int j=0; j<numofNodes && equalN; j++)
						for (int k=0; k<numofNodes && equalN; k++)
							equalN = (r[j][k] == topo_n[j][k]);
 					if (equalO) stop=true;
					else if (equalN) rtopologies.remove(o);
				}
				if (!stop) rtopologies.add(new Integer(n));
			}
		}
	}

	abstract Set<String> findState(Set<String> initial, ConstraintLabeledTransitionSystem CLTS,
			NetworkConstraint zeta);

	 public boolean OverInvalidPath(NetworkConstraint temp) {
		// returns true if temp is an invalid path for mu
		 // if there is no topology in topology formula that conforms to temp

		boolean hasNoValid = true;
		boolean[][] topo = new boolean[numofNodes][numofNodes];
		int index =0 ;
		for(Iterator<Integer> i=rtopologies.iterator(); i.hasNext();)
		{
			index = i.next().intValue();
			for(int j=0; j<numofNodes; j++)
				for (int k=0; k<numofNodes; k++)
					topo[j][k] = topologies[index][j][k];
			//if (mu.satisfy(topo,numofNodes) && temp.conforms(extract_connectivity(topo)))
			if (temp.conforms(extract_connectivity(topo)))
			{
				hasNoValid = false;
				break;
			}
		}
		return hasNoValid;
	}
	 
	 public boolean Valid(NetworkConstraint temp, Integer index) {
		boolean[][] topo = new boolean[numofNodes][numofNodes];
		for(int j=0; j<numofNodes; j++)
			for (int k=0; k<numofNodes; k++)
				topo[j][k] = topologies[index][j][k];
		return temp.conforms(extract_connectivity(topo));
	}

	static private NetworkConstraint extract_connectivity(boolean[][] topo) {
		NetworkConstraint nc = new NetworkConstraint();
		for(int i=0; i<numofNodes; i++)
			for (int j=0; j<numofNodes; j++)
			{
				if (topo[i][j])
				{
					String src,dst;
					dst = (i==0) ?  "A" :
							(i==1)?  "B" :
								(i==2) ?  "C" :
										(i==3) ? "D" : "E";
					src = (j==0) ?  "A" :
							(j==1)?  "B" :
								(j==2) ?  "C" :
									(j==3) ? "D" : "E";
					Pair p = new Pair(src,dst,true);
					nc.add(p);
				}
			}
		return nc;
	}

	static private void fillTopologies() {
		int flatness = numofNodes*numofNodes-numofNodes;
		int num = numofTopo;
		boolean[][] flat = new boolean[num][flatness];
		fill(flat,flatness,0,0,num/2,false);
		fill(flat,flatness,0,num/2,num,true);
		//Unflattering
		int count ;
		for (int i=0; i<num; i++)
		{
			count = 0;
			for(int j=0; j<numofNodes; j++)
				for(int k=0; k<numofNodes; k++)
					if (j==k) topologies[i][j][k]=false;
					else 
					{
						topologies[i][j][k] = flat[i][count];
						count++;
					}
		}
	}
	static private void fill(boolean[][] flat, int flatness, int i, int j, int k,
			boolean b) {
		for (int l=j; l<k && b; l++)
			flat[l][i]= b;
		if (i+1<flatness)
		{
			fill(flat,flatness,i+1,j,(j+k)/2,false);
			fill(flat,flatness,i+1,(j+k)/2,k,true);
		}		
	}

/*	static private int factorial(int n)
	{
		if ((n==0)|(n==1)) return 1;
		else return n*factorial(n-1);
	}*/
	static private int power(int n,int m)
	{
		if (m==0) return 1;
		else if (m==1) return n;
		else return n*power(n,m-1);
	}

	public static NetworkConstraint Accumulate(DirectedSubgraph<String, LabeledTransition> sg) 
	{
		NetworkConstraint l = new NetworkConstraint();
		Set<LabeledTransition> trans_t = sg.edgeSet();
		for (Iterator<LabeledTransition> j=trans_t.iterator();  j.hasNext();)
		{
			LabeledTransition tr = j.next();
			l.update(tr.label.nc);
		}
		return l;
	}
	
}
class UntilFormula extends PathFormula
{
	
	public UntilFormula(StateFormula _phi1,StateFormula _phi2,ActionFormula _chi1,ActionFormula _chi2,TopologyFormula _mu, int numofnodes )
	{
		super(numofnodes,_mu);
		phi1 = _phi1;
		phi2 = _phi2;
		chi1 = _chi1;
		chi2 = _chi2;
		mu = _mu;
	}

	@Override
	public Set<String> findState(Set<String> initial, ConstraintLabeledTransitionSystem InitialCLTS,
			NetworkConstraint zeta) 
	{
		Set<String> T = phi1.findState(null,InitialCLTS, zeta);
		
		//when initial is not empty, T should include initials, otherwise null is returned
		Set<String> copy_of_T = new HashSet<String>(T);
		if (initial!=null) copy_of_T.retainAll(initial);		
		if (initial!=null && !initial.isEmpty() && copy_of_T.isEmpty()) 
			return null;

		Set<String> T2 = phi2.findState(null,InitialCLTS, zeta);
		
		//working on the copy of input CLTS
		//ConstraintLabeledTransitionSystem CLTS = new ConstraintLabeledTransitionSystem(InitialCLTS);

		//generating a new CLTS that all its states satisfy \phi1 and transitions satisfy \chi1 and conform to zeta
		//ConstraintLabeledTransitionSystem filteredCLTS = new ConstraintLabeledTransitionSystem(InitialCLTS);
		//filteredCLTS.filterTransitions(chi1, zeta);
		//filteredCLTS.restrictToStates(T);		
		

        //removing transitions from CLTS that do not satisfy either \chi_1 or \chi_2 and do not conform to \zeta
        //CLTS.filterTransitions(new OrActionFormula(chi1, chi2), zeta);
        
        
        //making the set of states for backward checking
        Set<String> backwards = new HashSet<String>();
        
        //this case should be done in case phi2 or chi2 is not false
        StateFormula phiTemp = phi2.reduce();
        //Hashtable<String,Tag> htb = new Hashtable<String,Tag>() ;
        boolean reduction = ((phiTemp.getClass().getName().equals("BasicStateFormula") && !((BasicStateFormula)phiTemp).val)
            	|| (chi2.toSet(InitialCLTS.getAct()).isEmpty()));
        if (!reduction && T2!=null && !T2.isEmpty())
        {
	        for (Iterator<String> i=T2.iterator(); i.hasNext();)
	        {
	        	String t = i.next();
	        	Set<LabeledTransition> trans_t = InitialCLTS.incomingEdgesOf(t);
	        	//for all (s,(c,a),t)\in CLTS.Trans that a|= \chi_2 and s|=phi1
	    		for (Iterator<LabeledTransition> j=trans_t.iterator(); j.hasNext();)
	    		{
	    			LabeledTransition tr = j.next();
	    			String s = tr.getSrc();
	    			if (T.contains(s) && chi2.satisfy(new StringActionFormula(tr.label.act))  && zeta.conforms(tr.label.nc))
	    			{
	    				backwards.add(s);
	    				//htb.put(t,new Tag(tr.label.nc,d,null));
	    			}
	    		}
            }
        }        
        //ready to do the backward analysis
        
        
        Set<String> visited = new HashSet<String>();
        while (!backwards.isEmpty())
        {
        	// choose s \in backwards
        	String s = backwards.iterator().next();
        	//System.out.println("state "+s+" is checking"); 
        	backwards.remove(s);
        	visited.add(s);
        	
        	Set<LabeledTransition> trans_t = InitialCLTS.incomingEdgesOf(s);
        	//for all (t,(c,a),s)\in CLTS.Trans that t|= phi1 and (a|= \chi_1 or (c={} and a=tau))) 
    		for (Iterator<LabeledTransition> j=trans_t.iterator(); j.hasNext();)
    		{
    			LabeledTransition tr = j.next();
    			String t = tr.getSrc();
    			if (T.contains(t) && chi1.satisfy(new StringActionFormula(tr.label.act)) && zeta.conforms(tr.label.nc))
    			{
    				if (!visited.contains(t)) 
    				{
    					backwards.add(t);
    					//htb.put(t,new Tag(tr.label.nc,s,htb.get(s)) );
    				}
    			}
    		}        	
        }
        // finish of backward analysis
		return visited;
	}
	

	public String toString()
	{
		return phi1.toString()+"{"+chi1.toString()+"}U^{"+mu.toString()+"} {"+chi2.toString()+"}"+phi2.toString();
	}
	
}
class UnlessFormula extends PathFormula
{
	
	public UnlessFormula(StateFormula _phi1,StateFormula _phi2,ActionFormula _chi1,ActionFormula _chi2,TopologyFormula _mu, int numofnodes )
	{
		super(numofnodes,_mu);
		phi1 = _phi1;
		phi2 = _phi2;
		chi1 = _chi1;
		chi2 = _chi2;
		mu = _mu;
	}

	@Override
	Set<String> findState(Set<String> initial,
			ConstraintLabeledTransitionSystem InitialCLTS, NetworkConstraint zeta) {
		Set<String> T = phi1.findState(null,InitialCLTS, zeta);
		
		//when initial is not empty, T should include initials, otherwise null is returned
		Set<String> copy_of_T = new HashSet<String>(T);
		if (initial!=null) copy_of_T.retainAll(initial);		
		if (initial!=null && !initial.isEmpty() && copy_of_T.isEmpty()) 
			return null;

		Set<String> T2 = phi2.findState(null,InitialCLTS, zeta);
		
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
        boolean reduction = ((phiTemp.getClass().getName().equals("BasicStateFormula") && !((BasicStateFormula)phiTemp).val)
            	|| (chi2.toSet(CLTS.getAct()).isEmpty()));
        if (!reduction && T2!=null && !T2.isEmpty())
        {
	        for (Iterator<String> i=T2.iterator(); i.hasNext();)
	        {
	        	String t = i.next();
	        	Set<LabeledTransition> trans_t = CLTS.incomingEdgesOf(t);
	        	//for all (s,(c,a),t)\in CLTS.Trans that a|= \chi_2 and s|=phi1
	    		for (Iterator<LabeledTransition> j=trans_t.iterator(); j.hasNext();)
	    		{
	    			LabeledTransition tr = j.next();
	    			String s = tr.getSrc();
	    			if (T.contains(s) && chi2.satisfy(new StringActionFormula(tr.label.act)))
	    			{
	    				backwards.add(s);
	    				//htb.put(t,new Tag(tr.label.nc,d,null));
	    			}
	    		}
            }
        }        
        //ready to do the backward analysis
        
        
        Set<String> visited = new HashSet<String>();
        while (!backwards.isEmpty())
        {
        	// choose s \in backwards
        	String s = backwards.iterator().next();
        	//System.out.println("state "+s+" is checking"); 
        	backwards.remove(s);
        	visited.add(s);
        	
        	Set<LabeledTransition> trans_t = filteredCLTS.incomingEdgesOf(s);
        	//for all (t,(c,a),s)\in CLTS.Trans that t|= phi1 and (a|= \chi_1 or (c={} and a=tau))) 
    		for (Iterator<LabeledTransition> j=trans_t.iterator(); j.hasNext();)
    		{
    			LabeledTransition tr = j.next();
    			String t = tr.getSrc();
    			//if (T.contains(t)) //&&(chi1.satisfy(new StringActionFormula(tr.label.act))))
    			{
    				if (!visited.contains(t)) 
    				{
    					backwards.add(t);
    					//htb.put(t,new Tag(tr.label.nc,s,htb.get(s)) );
    				}
    			}
    		}        	
        }
        // finish of backward analysis
        

        // start of second backward analysis to find states with an invalid path for mu
        filteredCLTS.removeAllVertices(visited);
        StrongConnectivityInspector<String, LabeledTransition> sci =
            new StrongConnectivityInspector<String, LabeledTransition>(filteredCLTS);
        List<DirectedSubgraph<String, LabeledTransition>> stronglyConnectedSubgraphs = sci.stronglyConnectedSubgraphs();
        //Collapsing SCCs to a state
    	int num = 0; //counting SCC
        Hashtable<String,Set<String>> mapSCC = new Hashtable<String,Set<String>>() ;
    	for (Iterator <DirectedSubgraph<String, LabeledTransition>> k=stronglyConnectedSubgraphs.iterator(); k.hasNext();)
        {
    		//System.out.println("SCC is collapsed: "+k);
        	DirectedSubgraph<String, LabeledTransition> sg = k.next();
        	if (sg.edgeSet().size()>0) // a non-trivial SCC
        	{
        		//NetworkConstraint l = Accumulate(sg); 
        		Set<LabeledTransition> tr_sg = sg.edgeSet();
        		Set<String> s_sg = sg.vertexSet();
        		//remove transitions of SCC from filtered CLTS
	            for (Iterator<LabeledTransition> i=tr_sg.iterator(); i.hasNext();)
	            {
	            	LabeledTransition tr = i.next();
	            	filteredCLTS.removeEdge(tr);
	            }
	            //collapsing the SCC ... 
	            String s = new String("scc"+(new Integer(num)).toString());
	            mapSCC.put(s, s_sg);
	            filteredCLTS.addVertex(s);
	            Set<LabeledTransition> tr_fi = new HashSet<LabeledTransition>(filteredCLTS.edgeSet());
	            Set<LabeledTransition> tr_in = new HashSet<LabeledTransition>();
	            Set<LabeledTransition> tr_out = new HashSet<LabeledTransition>();
	            for (Iterator<LabeledTransition> i=tr_fi.iterator(); i.hasNext();)
	            {
	            	LabeledTransition tr = i.next();
	            	//classifies transitions in terms of incoming/outcoming to/from the SCC 
	            
	            	String dst = tr.getDst();
	            	String src = tr.getSrc();
            		if (s_sg.contains(dst))
	            	{
            			tr_in.add(tr);
	            	}
	            	else if (s_sg.contains(src))
	            	{ 
	            		tr_out.add(tr);
	            	}
	            }
	            for (Iterator<LabeledTransition> i=tr_out.iterator(); i.hasNext();)
	            {
	            	LabeledTransition tr = i.next();
            		filteredCLTS.removeEdge(tr);
            		filteredCLTS.addEdge(s, tr.getDst(), new LabeledTransition(new Label(tr.label.nc,tr.label.act)));	            
	            }
	            for (Iterator<LabeledTransition> i=tr_in.iterator(); i.hasNext();)
	            {
	            	LabeledTransition tr = i.next();
            		filteredCLTS.removeEdge(tr);
            		filteredCLTS.addEdge(tr.getSrc(), s,new LabeledTransition(new Label( tr.label.nc,tr.label.act)));	            
	            }
	            for (Iterator<String> i=s_sg.iterator(); i.hasNext();)
	            {
		            filteredCLTS.removeVertex(i.next());
	            }
	            num ++;
	            //TODO: deleting in/out transitions
        	}
        }
        
        Set<String> backwardAnalysis =  new HashSet<String>(filteredCLTS.vertexSet());
        backwardAnalysis.removeAll(visited);
   
        //Hashtable<String,NetworkConstraint> ht = new Hashtable<String,NetworkConstraint>() ;
    	Set<LabeledTransition> trans = null;
        for (Iterator<String> i=backwardAnalysis.iterator(); i.hasNext();)
        {
        	String si = i.next();
        	trans = filteredCLTS.outgoingEdgesOf(si);
        	//visited is evolving ....
            if (!visited.contains(si) && trans.isEmpty())
            {
            	Set<LabeledTransition> tr = (!mapSCC.containsKey(si))? CLTS.outgoingEdgesOf(si) : null;
            	// start from a SCC or a deadlock state
            	if (tr==null || tr.isEmpty()) 	            
        			pred(si,filteredCLTS,visited,mapSCC);
        	}
            
        }
        
		return visited;
	}
	public String toString()
	{
		return phi1.toString()+"{"+chi1.toString()+"}W^{"+mu.toString()+"} {"+chi2.toString()+"}"+phi2.toString();
	}

	private static void pred(String si,
			ConstraintLabeledTransitionSystem CLTS,Set<String> result,Hashtable<String,Set<String>> mapSCC) {
		//System.out.println("Preceding of "+si+" is computed");

		if (mapSCC.containsKey(si)) result.addAll(mapSCC.get(si)); 
		else result.add(si);
		
		Set<LabeledTransition> trans_t = CLTS.incomingEdgesOf(si);
		for (Iterator<LabeledTransition> j=trans_t.iterator();  j.hasNext();)
		{
			LabeledTransition tr = j.next();
			String t = tr.getSrc();
			if (!result.contains(t)) pred(t,CLTS,result,mapSCC);
		}
		//System.out.println("The preceding of "+si+" is "+result);

	}

}


class GenerallyFormula extends UnlessFormula
{
	
	
	public GenerallyFormula( StateFormula _phi, ActionFormula _chi, int numofnodes )
	{
		super(_phi,new BasicStateFormula(false),_chi,new BasicActionFormula(false),new BasicTopologyFormula(true),numofnodes);
	}

	public String toString()
	{
		return "G "+phi1.toString()+"{"+chi1.toString()+"}";
	}
}


class NextFormula extends UntilFormula
{
	
	
	public NextFormula( ActionFormula _chi, StateFormula _phi, int numofnodes )
	{
		super(new BasicStateFormula(true),_phi,new BasicActionFormula(false),_chi,new BasicTopologyFormula(true),numofnodes);
	}
	
	@Override
	public Set<String> findState(Set<String> initial, ConstraintLabeledTransitionSystem CLTS,
			NetworkConstraint zeta) 
	{
		System.out.println("Verifying E"+this.toString()+" is started");
		Set<String> T2 = phi2.findState(null,CLTS, zeta);
        
        
        //making the set of states for backward checking
        Set<String> result = new HashSet<String>();
        
        //this case should be done in case phi2 or chi2 is not false
        StateFormula phiTemp = phi2.reduce();
        boolean reduction = ((phiTemp.getClass().getName().equals("BasicStateFormula") && !((BasicStateFormula)phiTemp).val)
            	|| (chi2.toSet(CLTS.getAct()).isEmpty()));
        if (!reduction && T2!=null && !T2.isEmpty())
        {
	        for (Iterator<String> i=T2.iterator(); i.hasNext();)
	        {
	        	String t = i.next();
	        	Set<LabeledTransition> trans_t = CLTS.incomingEdgesOf(t);
	        	//for all (s,(c,a),t)\in CLTS.Trans that a|= \chi_2 and s|=phi1
	    		for (Iterator<LabeledTransition> j=trans_t.iterator(); j.hasNext();)
	    		{
	    			LabeledTransition tr = j.next();
	    			String s = tr.getSrc();
	    			if (chi2.satisfy(new StringActionFormula(tr.label.act))  && zeta.conforms(tr.label.nc))
	    			{
	    				result.add(s);
	    			}
	    		}
            }
        }        
		System.out.println("Verifying E"+this.toString()+" is terminated by "+result);
		return result;
		
	}

	public String toString()
	{
		return "X^{"+mu.toString()+"} {"+chi2.toString()+"}"+phi2.toString();
	}
}

class FinallyFormula extends UntilFormula
{
	
	
	public FinallyFormula(TopologyFormula _mu, ActionFormula _chi, StateFormula _phi, int numofnodes )
	{
		super(new BasicStateFormula(true),_phi,new BasicActionFormula(true),_chi,_mu,numofnodes);
	}

	public String toString()
	{
		return "F^{"+mu.toString()+"} {"+chi2.toString()+"}"+phi2.toString();
	}
}

