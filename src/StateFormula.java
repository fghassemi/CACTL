import java.util.*;

import org.jgrapht.alg.KosarajuStrongConnectivityInspector;
import org.jgrapht.graph.DirectedSubgraph;


public interface StateFormula {
	public Set<String> findState(Set<String> initial,ConstraintLabeledTransitionSystem CLTS,NetworkConstraint zeta);
	public StateFormula reduce();
}

class BasicStateFormula implements StateFormula
{
	public boolean val;
	public BasicStateFormula(boolean _val)
	{
		val = _val;
	}
	@Override
	public Set<String> findState(Set<String> initial, ConstraintLabeledTransitionSystem CLTS,
			NetworkConstraint zeta) {
		
		if (val)
		{
			if ((initial==null) || initial.isEmpty()) return CLTS.vertexSet();
			else return initial;				
		}
		else 
			return null;
	}
	public String toString()
	{
		if (val) return "true"; else return "false";
	}
	@Override
	public StateFormula reduce() {
		StateFormula temp = new BasicStateFormula(val);
		return temp;
	}
}

class NegStateFormula implements StateFormula
{
	public StateFormula arg ;

	public NegStateFormula(StateFormula _arg)
	{
		arg = _arg;
	}

	@Override
	public Set<String> findState(Set<String> initial,ConstraintLabeledTransitionSystem CLTS,
			NetworkConstraint zeta) {
				
		Set <String> states = new HashSet<String>(CLTS.vertexSet());
		Set <String> neg_states = arg.findState(null,CLTS, zeta);
 		if (neg_states!=null) states.removeAll(neg_states);
		if (initial!=null) states.retainAll(initial);
		return states;
	}
	public String toString()
	{
		return "~"+arg.toString();
	}

	@Override
	public StateFormula reduce() {
		StateFormula temp = arg.reduce();
		if (temp.getClass().getName().equals("BasicStateFormula"))
		{
			((BasicStateFormula)temp).val = !((BasicStateFormula)temp).val;
			return temp;
		}
		else return new NegStateFormula(temp);
	}
}

class AndStateFormula implements StateFormula
{
	public StateFormula arg1 ;
	public StateFormula arg2 ;
	
	public AndStateFormula(StateFormula _arg1, StateFormula _arg2)
	{
		arg1 = _arg1;
		arg2 = _arg2;
	}

	@Override
	public Set<String> findState(Set<String> initial,ConstraintLabeledTransitionSystem CLTS,
			NetworkConstraint zeta) {

		//System.out.println("Computing ");//+this+"....");
		Set<String> T1 = arg1.findState(initial,CLTS, zeta);
		//System.out.println(arg1+" is satisfied by "+T1+" under "+zeta+" for initial "+initial);
		Set<String> T2 = arg2.findState(initial,CLTS, zeta);
		//System.out.println(arg2+" is satisfied by "+T2+" under "+zeta+" for initial "+initial);
		
		if (T1!=null && T2!=null) T1.retainAll(T2);
		return T1;
	}
	public String toString()
	{
		return "("+arg1.toString()+" /\\ "+arg2.toString()+")";
	}

	@Override
	public StateFormula reduce() {
		StateFormula targ1 = arg1.reduce();
		StateFormula targ2 = arg2.reduce();
		if (targ1.getClass().getName().equals("BasicStateFormula"))
		{
			if (((BasicStateFormula)targ1).val)
				return targ2;
			else return targ1;
		}
		else if (targ2.getClass().getName().equals("BasicStateFormula"))
			{
				if (((BasicStateFormula)targ2).val)
					return targ1;
				else return targ2;
			}
		else return new AndStateFormula( targ1, targ2);
	}
}

class OrStateFormula implements StateFormula
{
	public StateFormula arg1 ;
	public StateFormula arg2 ;
	
	public OrStateFormula(StateFormula _arg1, StateFormula _arg2)
	{
		arg1 = _arg1;
		arg2 = _arg2;
	}

	@Override
	public Set<String> findState(Set<String> initial,ConstraintLabeledTransitionSystem CLTS,
			NetworkConstraint zeta) {

		
		Set<String> T1 = arg1.findState(initial,CLTS, zeta);
		Set<String> T2 = arg2.findState(initial,CLTS, zeta);
		
		if (T1==null) T1 = T2;
		if (T1!=null && T2!=null) T1.addAll(T2);
		return T1;
	}
	public String toString()
	{
		return "("+arg1.toString()+" \\/ "+arg2.toString()+")";
	}

	@Override
	public StateFormula reduce() {
		StateFormula targ1 = arg1.reduce();
		StateFormula targ2 = arg2.reduce();
		if (targ1.getClass().getName().equals("BasicStateFormula"))
		{
			if (((BasicStateFormula)targ1).val)
				return targ1;
			else return targ2;
		}
		else if (targ2.getClass().getName().equals("BasicStateFormula"))
			{
				if (((BasicStateFormula)targ2).val)
					return targ2;
				else return targ1;
			}
		else return new OrStateFormula(targ1, targ2);
	}	
}


class ImplyStateFormula implements StateFormula
{
	public StateFormula arg1 ;
	public StateFormula arg2 ;
	
	public ImplyStateFormula(StateFormula _arg1, StateFormula _arg2)
	{
		arg1 = _arg1;
		arg2 = _arg2;
	}

	@Override
	public Set<String> findState(Set<String> initial,ConstraintLabeledTransitionSystem CLTS,
			NetworkConstraint zeta) {

		
		Set<String> T1 = new OrStateFormula(new NegStateFormula(arg1),arg2).findState(initial, CLTS, zeta);
		return T1;
	}
	public String toString()
	{
		return "("+arg1.toString()+" ==> "+arg2.toString()+")";
	}

	@Override
	public StateFormula reduce() {
		StateFormula targ1 = arg1.reduce();
		StateFormula targ2 = arg2.reduce();
		if (targ1.getClass().getName().equals("BasicStateFormula"))
		{
			if (!((BasicStateFormula)targ1).val)
				return new BasicStateFormula(true);
			else return targ2;
		}
		else return new ImplyStateFormula(targ1, targ2);
	}	
}





class ExistStateFormula implements StateFormula
{
	public PathFormula arg ;
	
	public ExistStateFormula(PathFormula _arg)
	{
		arg = _arg;
	}

	@Override
	public Set<String> findState(Set<String> initial,ConstraintLabeledTransitionSystem CLTS,
			NetworkConstraint zeta) {
		
		return arg.findState(initial, CLTS, zeta);
	}
	public String toString()
	{
		return "E ("+arg.toString()+")";
	}

	@Override
	public StateFormula reduce() {
		
		return this;
	}
}



class AlphaAUStateFormula implements StateFormula
{
	public UntilFormula arg ;
	
	public AlphaAUStateFormula(UntilFormula _arg)
	{
		arg = _arg;
	}

	@Override
	public Set<String> findState(Set<String> initial,ConstraintLabeledTransitionSystem CLTS,
			NetworkConstraint zeta) {
		Set<String> T1 = arg.phi1.findState(null,CLTS, zeta);
		Set<String> negT1 = new HashSet<String>(CLTS.vertexSet()); 
		negT1.removeAll(T1);
		Set<String> T2 = arg.phi2.findState(null,CLTS, zeta);
		Set<String> negT2 = new HashSet<String>(CLTS.vertexSet()); negT2.removeAll(T2);
		Set<String> negT1T2 = new HashSet<String>(negT1); negT1T2.retainAll(negT2);
		
		
		//when initial is not empty, T1 should include initials, otherwise null is returned
		Set<String> copy_of_T1 = new HashSet<String>(T1);
		if (initial!=null) copy_of_T1.retainAll(initial);		
		if (initial!=null && !initial.isEmpty() && copy_of_T1.isEmpty()) 
			return null;
		
		//computing T states : states with no zeta-transition that is not of (chi,phi) form
		Set<String> T =  new HashSet<String>();
		boolean hasBadTrans = false;
		for (Iterator<String> si=T1.iterator(); si.hasNext();)
		{
		   String s = si.next();
		   hasBadTrans = false;
		   Set<LabeledTransition> tr = CLTS.outgoingEdgesOf(s);
		   for (Iterator<LabeledTransition> ti=tr.iterator(); ti.hasNext() && !hasBadTrans;)
		   {
			   LabeledTransition t = ti.next();
			   String dst = t.getDst();
			   if (zeta.conforms(t.label.nc) && (!T1.contains(dst) || !arg.chi1.satisfy(new StringActionFormula(t.label.act))))
				   hasBadTrans = true;
	     	}
		   //T includes states with either no zeta- transition or states with only zeta-transitions that are of (chi,phi) form
		   if (!hasBadTrans) T.add(s);
		 }         

		//generating a new CLTS that all its states satisfy \phi1 and transitions satisfy \chi1 and conform to zeta
		ConstraintLabeledTransitionSystem filteredCLTS1 = new ConstraintLabeledTransitionSystem(CLTS);
		filteredCLTS1.filterTransitions(arg.chi1, zeta);
		filteredCLTS1.restrictToStates(T1);

		ConstraintLabeledTransitionSystem filteredCLTS2 = new ConstraintLabeledTransitionSystem(filteredCLTS1);
		filteredCLTS2.restrictToStates(T);
		
		CLTS.filterTransitions(new BasicActionFormula(true), zeta);
		
		KosarajuStrongConnectivityInspector<String, LabeledTransition> sci =
            new KosarajuStrongConnectivityInspector<String, LabeledTransition>(filteredCLTS2);
        List<DirectedSubgraph<String, LabeledTransition>> stronglyConnectedSubgraphs = sci.stronglyConnectedSubgraphs();

        //making the set of states that a valid path should end in
        HashMap<String, Set<Integer>> visited = new HashMap<String,Set<Integer>>();
        Set<String> end = new HashSet<String>() ;
    	int num = 0; //counting SCC
        Hashtable<String,Set<String>> mapSCC = new Hashtable<String,Set<String>>() ;
    	for (Iterator <DirectedSubgraph<String, LabeledTransition>> k=stronglyConnectedSubgraphs.iterator(); k.hasNext();)
        {
    		//System.out.println("SCC is collapsed: "+k);
        	DirectedSubgraph<String, LabeledTransition> sg = k.next();
        	boolean hasOutTrans = false;
        	if (sg.edgeSet().size()>0) // a non-trivial SCC
        	{
        		NetworkConstraint l = UntilFormula.Accumulate(sg); 
        		Set<LabeledTransition> tr_sg = sg.edgeSet();
        		Set<String> s_sg = sg.vertexSet();
        		if (!arg.OverInvalidPath(l))
        		{   
        			// l does not violate mu 
        			
        			//if it is a terminal SCC : if one of its states has an out-going trans to out of SCC, it is not terminal
        			hasOutTrans = false;
        			for (Iterator<String> si=s_sg.iterator(); si.hasNext() && !hasOutTrans;)
        			{
        				String s = si.next();
        				Set<LabeledTransition> tr = CLTS.outgoingEdgesOf(s);
        				for (Iterator<LabeledTransition> ti=tr.iterator(); ti.hasNext() && !hasOutTrans;)
        				{
        					LabeledTransition t = ti.next();
        					String dst = t.getDst();
        					if (!s_sg.contains(dst))
        						hasOutTrans = true;
        				}
        			}
        			if (!hasOutTrans)
        			{
        				//sg is a terminal SCC that should be collapsed 

        				//collapsing the SCC ... 
        	            String s = new String("scc"+(new Integer(num)).toString());
        	            mapSCC.put(s, s_sg);
                		//remove transitions of SCC from filtered CLTS
        	            for (Iterator<LabeledTransition> i=tr_sg.iterator(); i.hasNext();)
        	            {
        	            	LabeledTransition tr = i.next();
        	            	filteredCLTS1.removeEdge(tr);
        	            }
        	            filteredCLTS1.addVertex(s);
        	            Set<LabeledTransition> tr_fi = new HashSet<LabeledTransition>(filteredCLTS1.edgeSet());
        	            Set<LabeledTransition> tr_in = new HashSet<LabeledTransition>();
        	            //Set<LabeledTransition> tr_out = new HashSet<LabeledTransition>();
        	            
        	            //TODO::use states of sg to find incomming trans 
        	            for (Iterator<LabeledTransition> i=tr_fi.iterator(); i.hasNext();)
        	            {
        	            	LabeledTransition tr = i.next();        	            
        	            	String dst = tr.getDst();
                    		if (s_sg.contains(dst))
        	            	{
                    			tr_in.add(tr);
        	            	}
        	            }
        	            for (Iterator<LabeledTransition> i=tr_in.iterator(); i.hasNext();)
        	            {
        	            	LabeledTransition tr = i.next();
                    		filteredCLTS1.removeEdge(tr);
                    		filteredCLTS1.addEdge(tr.getSrc(), s,new LabeledTransition(new Label(tr.label.nc,tr.label.act)));	            
        	            }
        	            for (Iterator<String> i=s_sg.iterator(); i.hasNext();)
        	            {
        		            filteredCLTS1.removeVertex(i.next());
        	            }
        	            num ++;        				
        	            //refactor :: visited.put(s, new HashSet<Integer>(arg.rtopologies));
		        		Set<Integer> newTopo = new HashSet<Integer>();
		        		for (Iterator<Integer> topoi = arg.rtopologies.iterator(); topoi.hasNext();)
		        		{
		        			Integer i = topoi.next();
		        			if (!arg.Valid(l, i))
		        				newTopo.add(i);		    	        	
		        		}
		        		//for these topologies that violate mu, backward analysis is never examined
        				visited.put(s, newTopo);
        				
        				end.add(s);
        				
        			}//end of collaspsing
        		}
        	}
        }
        
        
        for (Iterator<String> i=T1.iterator(); i.hasNext();)
        {
        	String t = i.next();
        	Set<LabeledTransition> trans_t = CLTS.outgoingEdgesOf(t);
        	//for all (t,(c,a),s)\in CLTS.Trans that a|= chi_1/\~chi_2 and s |= ~phi 
        	//or a|= (~chi_1/\~chi_2) and s |= true 
        	//or a|= chi_1/\chi_2 and s |= ~(phi \/ phi') 
        	//or a|= ~chi_1/\chi_2 and s |= ~phi' 
    		// a state with no zeta-path  
            if (trans_t.size()<1 )	
            {            	
            	end.add(t);			
            }
            else
            {
            	//for non-deadlock states
            	
	            for (Iterator<LabeledTransition> j=trans_t.iterator(); j.hasNext();)
	        	{
	        		LabeledTransition tr = j.next();
	        		String dst = tr.getDst();
	        		if ((arg.chi1.satisfy(new StringActionFormula(tr.label.act)) && !arg.chi2.satisfy(new StringActionFormula(tr.label.act)) && negT1.contains(dst)) || 
	        			(!arg.chi1.satisfy(new StringActionFormula(tr.label.act)) && !arg.chi2.satisfy(new StringActionFormula(tr.label.act)) ) ||
	        			(arg.chi1.satisfy(new StringActionFormula(tr.label.act)) && arg.chi2.satisfy(new StringActionFormula(tr.label.act)) && negT1T2.contains(dst)) ||
	        			(!arg.chi1.satisfy(new StringActionFormula(tr.label.act)) && arg.chi2.satisfy(new StringActionFormula(tr.label.act)) && negT2.contains(dst)))
	        		{
	        			end.add(t);
	        			break;
	        		}
	        	}
            	
            }
        }

       
        while (!end.isEmpty())
        {
        	// choose e \in start
        	String c0 = end.iterator().next();
        	end.remove(c0);
        	
        	//initializing the stack for backward analysis
        	Stack<Item> stack = new Stack<Item>();
        	Set<Integer> topo = new HashSet<Integer>(arg.rtopologies); 
        	if (visited.containsKey(c0)) 
        	{
        		topo.removeAll(visited.get(c0));
        	}
        	stack.push(new Item(c0,topo));
        	while (!stack.isEmpty())
        	{
        		Item item = stack.pop();
            	Set<Integer> to = new HashSet<Integer>(item.topo); 
            	if (visited.containsKey(item.state)) 
            	{
            		to.removeAll(visited.get(item.state));
            	}
            	if (!to.isEmpty())
            	{
            		item.topo.addAll(to);
            		visited.put(item.state, item.topo);
            		Set<LabeledTransition> tr = filteredCLTS1.incomingEdgesOf(item.state);
		        	for(Iterator<LabeledTransition> it=tr.iterator(); it.hasNext();)
		        	{
		        		LabeledTransition tt = it.next();
		        		Set<Integer> newTopo = new HashSet<Integer>();
		        		for (Iterator<Integer> ii = to.iterator(); ii.hasNext();)
		        		{
		        			Integer i = ii.next();
		        			if (arg.Valid(tt.label.nc, i))// && (visited.containsKey(tt.getSrc()) && !visited.get(c0).contains(i)))
		        				//refactor : promote the condition
		        				newTopo.add(i);		    	        	
		        		}
		        		stack.push(new Item(tt.getSrc(),newTopo));
		        	}            	
	        	}
	        	
        	}//end of backward analysis
            
        }
        
        Set<String> result = new HashSet<String>();
    	for(Iterator<String> it=T1.iterator(); it.hasNext();)
    	{
    		String s = it.next();
    		Enumeration<Set<String>> SCCs = mapSCC.elements();
    		boolean withinSCC = false;
    		while(SCCs.hasMoreElements()) {
    	         if (((Set<String>) SCCs.nextElement()).contains(s)) 
    	         {
    	        	 withinSCC = true;
    	        	 break;
    	         }
    	      }
    		if (!visited.containsKey(s) && !withinSCC)
    			result.add(s);
    	}
		return result;
	}
	

	public String toString()
	{
		return "A ("+arg.toString()+")";
	}

	@Override
	public StateFormula reduce() {
		return this;
	}
	
}


class AlphaAWStateFormula implements StateFormula
{
	public UnlessFormula arg ;
	
	public AlphaAWStateFormula(UnlessFormula _arg)
	{
		arg = _arg;
	}

	@Override
	public Set<String> findState(Set<String> initial,ConstraintLabeledTransitionSystem CLTS,
			NetworkConstraint zeta) {
		Set<String> T1 = arg.phi1.findState(null,CLTS, zeta);
		Set<String> negT1 = new HashSet<String>(CLTS.vertexSet()); 
		negT1.removeAll(T1);
		Set<String> T2 = arg.phi2.findState(null,CLTS, zeta);
		//phi2 can be a false formula , specially in AG
		Set<String> negT2 = new HashSet<String>(CLTS.vertexSet()); 
		if (T2!=null) negT2.removeAll(T2);
		Set<String> negT1T2 = new HashSet<String>(negT1); negT1T2.retainAll(negT2);
				
		//when initial is not empty, T1 should include initials, otherwise null is returned
		Set<String> copy_of_T1 = new HashSet<String>(T1);
		if (initial!=null) copy_of_T1.retainAll(initial);		
		if (initial!=null && !initial.isEmpty() && copy_of_T1.isEmpty()) 
			return null;
		

		//generating a new CLTS that all its states satisfy \phi1 and transitions satisfy \chi1 and conform to zeta
		ConstraintLabeledTransitionSystem filteredCLTS = new ConstraintLabeledTransitionSystem(CLTS);
		filteredCLTS.filterTransitions(arg.chi1, zeta);
		filteredCLTS.restrictToStates(T1);
		
		CLTS.filterTransitions(new BasicActionFormula(true), zeta);
		
        Set<String> end = new HashSet<String>() ;      
        for (Iterator<String> i=T1.iterator(); i.hasNext();)
        {
        	String t = i.next();
        	Set<LabeledTransition> trans_t = CLTS.outgoingEdgesOf(t);
        	//for all (t,(c,a),s)\in CLTS.Trans that a|= chi_1/\~chi_2 and s |= ~phi 
        	//or a|= (~chi_1/\~chi_2) and s |= true 
        	//or a|= chi_1/\chi_2 and s |= ~(phi \/ phi') 
        	//or a|= ~chi_1/\chi_2 and s |= ~phi' 
    		// a state with no zeta-path  
            if (trans_t.size()<1 )	
            {            	
            	end.add(t);			
            }
            else
            {
            	//for non-deadlock states
            	
	            for (Iterator<LabeledTransition> j=trans_t.iterator(); j.hasNext();)
	        	{
	        		LabeledTransition tr = j.next();
	        		String dst = tr.getDst();
	        		if ((arg.chi1.satisfy(new StringActionFormula(tr.label.act)) && !arg.chi2.satisfy(new StringActionFormula(tr.label.act)) && negT1.contains(dst)) || 
	        			(!arg.chi1.satisfy(new StringActionFormula(tr.label.act)) && !arg.chi2.satisfy(new StringActionFormula(tr.label.act)) ) ||
	        			(arg.chi1.satisfy(new StringActionFormula(tr.label.act)) && arg.chi2.satisfy(new StringActionFormula(tr.label.act)) && negT1T2.contains(dst)) ||
	        			(!arg.chi1.satisfy(new StringActionFormula(tr.label.act)) && arg.chi2.satisfy(new StringActionFormula(tr.label.act)) && negT2.contains(dst)))
	        		{
	        			end.add(t);
	        			break;
	        		}
	        	}
            	
            }
        }

        HashMap<String, Set<Integer>> visited = new HashMap<String,Set<Integer>>();
       
        while (!end.isEmpty())
        {
        	// choose e \in start
        	String c0 = end.iterator().next();
        	end.remove(c0);
        	
        	//initializing the stack for backward analysis
        	Stack<Item> stack = new Stack<Item>();
        	Set<Integer> topo = new HashSet<Integer>(arg.rtopologies); 
        	if (visited.containsKey(c0)) 
        	{
        		topo.removeAll(visited.get(c0));
        	}
        	stack.push(new Item(c0,topo));
        	while (!stack.isEmpty())
        	{
        		Item item = stack.pop();
            	Set<Integer> to = new HashSet<Integer>(item.topo); 
            	if (visited.containsKey(item.state)) 
            	{
            		to.removeAll(visited.get(item.state));
            	}
            	if (!to.isEmpty())
            	{
            		item.topo.addAll(to);
            		visited.put(item.state, item.topo);
            		Set<LabeledTransition> tr = filteredCLTS.incomingEdgesOf(item.state);
		        	for(Iterator<LabeledTransition> it=tr.iterator(); it.hasNext();)
		        	{
		        		LabeledTransition tt = it.next();
		        		Set<Integer> newTopo = new HashSet<Integer>();
		        		for (Iterator<Integer> ii = to.iterator(); ii.hasNext();)
		        		{
		        			Integer i = ii.next();
		        			if (arg.Valid(tt.label.nc, i))
		        				newTopo.add(i);		    	        	
		        		}
		        		stack.push(new Item(tt.getSrc(),newTopo));
		        	}            	
	        	}
	        	
        	}//end of backward analysis
            
        }
        
        Set<String> result = new HashSet<String>();
    	for(Iterator<String> it=T1.iterator(); it.hasNext();)
    	{
    		String s = it.next();
    		if (!visited.containsKey(s))
    			result.add(s);
    	}		
    	return result;
	}
	

	public String toString()
	{
		return "A ("+arg.toString()+")";
	}

	@Override
	public StateFormula reduce() {
		return this;
	}
	
}



class Item
{
	public String state;
	public Set<Integer> topo;
	
	public Item(String _state,Set<Integer> _topo)
	{
		state = _state;
		topo = _topo;
	}
	public int hasCode()
	{
		return state.hashCode()+ topo.hashCode();
	}
	public String toString()
	{
		String o ;
		if (state!=null) o = "("+state.toString()+","; 
		else o = "(null,";
		if (topo!=null) o = o +topo.toString()+")";
		else o = o + "null)";
		return o;
	}

}



class AUStateFormula implements StateFormula
{
	public UntilFormula arg ;
	
	public AUStateFormula(UntilFormula _arg)
	{
		arg = _arg;
	}

	@Override
	public Set<String> findState(Set<String> initial,ConstraintLabeledTransitionSystem CLTS,
			NetworkConstraint zeta) {
		Set<String> T1 = arg.phi1.findState(null,CLTS, zeta);
		if (T1==null) return null;
		Set<String> negT1 = new HashSet<String>(CLTS.vertexSet()); 
		negT1.removeAll(T1);
		Set<String> T2 = arg.phi2.findState(null,CLTS, zeta);
		Set<String> negT2 = new HashSet<String>(CLTS.vertexSet()); negT2.removeAll(T2);
		Set<String> negT1T2 = new HashSet<String>(negT1); negT1T2.retainAll(negT2);
		
		
		//when initial is not empty, T1 should include initials, otherwise null is returned
		Set<String> copy_of_T1 = new HashSet<String>(T1);
		if (initial!=null) copy_of_T1.retainAll(initial);		
		if (initial!=null && !initial.isEmpty() && copy_of_T1.isEmpty()) 
			return null;
		
		//find (x,phi) transitions while states with a non-(x,phi) and non-(x',phi') transition become deadlock
		// this implements clean
		Set<LabeledTransition> CCSTrans = new HashSet<LabeledTransition>();
		Set<String> EXCCSStates = new HashSet<String>();
		boolean hasBadTrans = false;
		for (Iterator<String> si=T1.iterator(); si.hasNext();)
		{
		   String s = si.next();
		   hasBadTrans = false;
		   Set<LabeledTransition> trans = CLTS.outgoingEdgesOf(s);
		   Set<LabeledTransition> tempCCSTrans = new HashSet<LabeledTransition>();
		   for (Iterator<LabeledTransition> ti=trans.iterator(); ti.hasNext() && !hasBadTrans;)
		   {
			   LabeledTransition tr = ti.next();
			   String dst = tr.getDst();
			   if (zeta.conforms(tr.label.nc))
				   if (T2.contains(dst) && arg.chi2.satisfy(new StringActionFormula(tr.label.act)))
			    	   EXCCSStates.add(s);
				   else if (T1.contains(dst) && arg.chi1.satisfy(new StringActionFormula(tr.label.act)))
					   tempCCSTrans.add(tr);
			       else hasBadTrans = true;
				   
	     	}
		   if (!hasBadTrans) CCSTrans.addAll(tempCCSTrans);
		 }         

		//generating a new CLTS that all its states satisfy \phi1 and transitions satisfy \chi1 and conform to zeta
		ConstraintLabeledTransitionSystem filteredCLTS = new ConstraintLabeledTransitionSystem(CCSTrans,T1,CLTS.InitialStates());
		
		SCCInspector inspector = new SCCInspector(filteredCLTS);
		Set<String>[] SCCs = inspector.ComputeSCCc();

        //making the set of states that a valid path should end in
        HashMap<String, Set<Integer>> visited = new HashMap<String,Set<Integer>>();
        Set<String> end = new HashSet<String>() ;
        for (int scc = 0; scc < inspector.count; scc++) 
        {
        	//DirectedSubgraph<String, LabeledTransition> sg = k.next();
        	
    		//we want terminal SCCs with no (phi',chi') trans and valid accumulated network constraint 
        	//this implements TCCS + Start        	
        	    		
    		Set<String> s_sg = SCCs[scc];
    		boolean hasAUState = false;
    		boolean terminal = true;
   			NetworkConstraint acc = new NetworkConstraint();
   			for (Iterator<String> si=s_sg.iterator(); si.hasNext() && !hasAUState && terminal;)
			{
				String s = si.next();
				if (EXCCSStates.contains(s))
					hasAUState = true;
				else 					
				{
					Set<LabeledTransition> trans = filteredCLTS.outgoingEdgesOf(s);
					for (Iterator<LabeledTransition> ti=trans.iterator(); ti.hasNext() && terminal;)
					   {
						   LabeledTransition tr = ti.next();
						   String dst = tr.getDst();
						   if (!s_sg.contains(dst))
							   terminal = false;
						   else 
							   acc.update(tr.label.nc);
					   }
				}
			}   		

    		if (!hasAUState && terminal && !arg.OverInvalidPath(acc))
    		{
    			// a (chi,phi)-CCS with no (chi',phi') state
    			Set<Integer> validTopo = new HashSet<Integer>();
        		for (Iterator<Integer> topoi = arg.rtopologies.iterator(); topoi.hasNext();)
        		{
        			Integer i = topoi.next();
        			if (arg.Valid(acc, i))
        				validTopo.add(i);		    	        	
        		}
        		//for these topologies that violate mu, backward analysis is never examined
        		// validTopo is for sure not empty as it is valid
       			for (Iterator<String> si=s_sg.iterator(); si.hasNext();)
    			{
       				String s = si.next();
    				visited.put(s, validTopo);
    				end.add(s);
    			}
    		}
    	}
        while (!end.isEmpty())
        {
        	// choose e \in start
        	String c0 = end.iterator().next();
        	end.remove(c0);
        	
        	//initializing the stack for backward analysis
        	Stack<Item> stack = new Stack<Item>();
        	Set<Integer> topo ; 
        	if (visited.containsKey(c0)) 
        		topo = visited.get(c0);
        	else {
        		topo = new HashSet<Integer>(arg.rtopologies);
        		visited.put(c0, topo);
        	}
        	stack.push(new Item(c0,topo));
        	System.out.println("backward analysis starts at"+ c0);
        	while (!stack.isEmpty())
        	{
        		Item item = stack.pop();            	
        		//add to violate : no need as we use range of visited
        		if (item.state.equals("0"))
        			System.out.println("nahaaara");
        		else
        			System.out.println("over the path"+ item.state);
        		Set<LabeledTransition> tr = filteredCLTS.incomingEdgesOf(item.state);
	        	for(Iterator<LabeledTransition> it=tr. iterator(); it.hasNext();)
	        	{
	        		LabeledTransition tt = it.next();
	        		String src = tt.getSrc();
	        		Set<Integer> newTopo = new HashSet<Integer>();
	        		//we filter topologies by the nc of transition regarding visited
	        		for (Iterator<Integer> ii = item.topo.iterator(); ii.hasNext();)
	        		{
	        			Integer i = ii.next();		        			
	        			if (arg.Valid(tt.label.nc, i) && (!visited.containsKey(src) || !visited.get(src).contains(i)))
	        				newTopo.add(i);		    	        	
	        		}
	        		if (!newTopo.isEmpty())
	        		{
		        		stack.push(new Item(src,newTopo));
		        		Set<Integer> updTopo ;
		        		if (visited.containsKey(src))
		        		{
		        			updTopo = visited.get(src) ;
		        			updTopo.addAll(newTopo);
		        		}
		        		else updTopo = newTopo;
		        		visited.put(src, updTopo);
	        		}
	        	}
	        	
        	}//end of backward analysis            
        }
        
        Set<String> result = new HashSet<String>();
    	for(Iterator<String> it=T1.iterator(); it.hasNext();)
    	{
    		String s = it.next();
    		if (!visited.containsKey(s))
    			result.add(s);
    	}
		return result;
	}
	

	public String toString()
	{
		return "A ("+arg.toString()+")";
	}

	@Override
	public StateFormula reduce() {
		return this;
	}
	
}

class AWStateFormula implements StateFormula
{
	public UnlessFormula arg ;
	
	public AWStateFormula(UnlessFormula _arg)
	{
		arg = _arg;
	}

	@Override
	public Set<String> findState(Set<String> initial,ConstraintLabeledTransitionSystem CLTS,
			NetworkConstraint zeta) {
		Set<String> T1 = arg.phi1.findState(null,CLTS, zeta);
		if (T1==null) return null;
		Set<String> negT1 = new HashSet<String>(CLTS.vertexSet()); 
		negT1.removeAll(T1);
		
		Set<String> T2 = arg.phi2.findState(null,CLTS, zeta);		
		
		//when initial is not empty, T1 should include initials, otherwise null is returned
		Set<String> copy_of_T1 = new HashSet<String>(T1);
		if (initial!=null) copy_of_T1.retainAll(initial);		
		if (initial!=null && !initial.isEmpty() && copy_of_T1.isEmpty()) 
			return null;
		
		//find (x,phi) transitions while states with a non-(x,phi) and non-(x',phi') transition are detected
		Set<LabeledTransition> CCSTrans = new HashSet<LabeledTransition>();
        Set<String> end = new HashSet<String>() ;
        boolean AddedBefore = false;
		for (Iterator<String> si=T1.iterator(); si.hasNext();)
		{
		   String s = si.next();
		   AddedBefore = false;
		   Set<LabeledTransition> trans = CLTS.outgoingEdgesOf(s);
		   Set<LabeledTransition> tempCCSTrans = new HashSet<LabeledTransition>();
		   for (Iterator<LabeledTransition> ti=trans.iterator(); ti.hasNext();)
		   {
			   LabeledTransition tr = ti.next();
			   String dst = tr.getDst();
			   if (zeta.conforms(tr.label.nc))
				   if (T1.contains(dst) && arg.chi1.satisfy(new StringActionFormula(tr.label.act)))
					   tempCCSTrans.add(tr);
			       else if (T2!=null && !(T2.contains(dst) && arg.chi2.satisfy(new StringActionFormula(tr.label.act))))
			    	   		if (!AddedBefore) 
			    	   		{
			    	   			end.add(s);
			    	   			AddedBefore = true;
			    	   		}

				   
	     	}
		   if (trans.size()<1)
			  //deadlock states do not satisfy AW
			   end.add(s);
		   else 
			   CCSTrans.addAll(tempCCSTrans);
		 }         

		//generating a new CLTS that all its states satisfy \phi1 and transitions satisfy \chi1 and conform to zeta
		ConstraintLabeledTransitionSystem filteredCLTS = new ConstraintLabeledTransitionSystem(CCSTrans,T1,CLTS.InitialStates());
        HashMap<String, Set<Integer>> visited = new HashMap<String,Set<Integer>>();

        while (!end.isEmpty())
        {
        	// choose e \in start
        	String c0 = end.iterator().next();
        	end.remove(c0);
        	
        	//initializing the stack for backward analysis
        	Stack<Item> stack = new Stack<Item>();
        	Set<Integer> topo = new HashSet<Integer>(arg.rtopologies);    
        	visited.put(c0, topo);
        	stack.push(new Item(c0,topo));
        	//System.out.println("backward analysis starts at"+ c0);
        	while (!stack.isEmpty())
        	{
        		Item item = stack.pop();            	
        		//add to violate : no need as we use range of visited
//        		if (item.state.equals("0"))
//        			System.out.println("nahaaara");
//        		else
//        			System.out.println("over the path"+ item.state);
        		Set<LabeledTransition> tr = filteredCLTS.incomingEdgesOf(item.state);
	        	for(Iterator<LabeledTransition> it=tr.iterator(); it.hasNext();)
	        	{
	        		LabeledTransition tt = it.next();
	        		String src = tt.getSrc();
	        		Set<Integer> newTopo = new HashSet<Integer>();
	        		//we filter topologies by the nc of transition regarding visited
	        		for (Iterator<Integer> ii = item.topo.iterator(); ii.hasNext();)
	        		{
	        			Integer i = ii.next();		        			
	        			if (arg.Valid(tt.label.nc, i) && (!visited.containsKey(src) || !visited.get(src).contains(i)))
	        				newTopo.add(i);		    	        	
	        		}
	        		if (!newTopo.isEmpty())
	        		{
		        		stack.push(new Item(src,newTopo));
		        		Set<Integer> updTopo ;
		        		if (visited.containsKey(src))
		        		{
		        			updTopo = visited.get(src) ;
		        			updTopo.addAll(newTopo);
		        		}
		        		else updTopo = newTopo;
		        		visited.put(src, updTopo);
	        		}
	        	}
	        	
        	}//end of backward analysis            
        }
        
        Set<String> result = new HashSet<String>();
    	for(Iterator<String> it=T1.iterator(); it.hasNext();)
    	{
    		String s = it.next();
    		if (!visited.containsKey(s))
    			result.add(s);
    	}
		return result;
	}
	

	public String toString()
	{
		return "A ("+arg.toString()+")";
	}

	@Override
	public StateFormula reduce() {
		return this;
	}
	
}


