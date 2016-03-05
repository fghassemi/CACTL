import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jgrapht.graph.DirectedPseudograph;


public class ConstraintLabeledTransitionSystem extends DirectedPseudograph<String, LabeledTransition>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7606197493400636788L;
	private Set<String> initials ;
	private Set<String> Act ;
	// We assumed there is only one initial state 
	public ConstraintLabeledTransitionSystem(
			Class<? extends LabeledTransition> arg0) {
		super(arg0);
		
		initials = new HashSet<String>();
	}

	public ConstraintLabeledTransitionSystem(
			ConstraintLabeledTransitionSystem clts) {
		super(LabeledTransition.class);
		
		Set<String> states = clts.vertexSet();
		Set<LabeledTransition> trans = clts.edgeSet();
		initials = clts.InitialStates();
		Act = new HashSet<String>();

		for(Iterator<String> i=states.iterator(); i.hasNext();)
			addVertex(i.next());
		for(Iterator<LabeledTransition> i=trans.iterator(); i.hasNext();)
		{
			LabeledTransition t = i.next();
			addEdge(t.getSrc(),t.getDst(),t);
			Act.add(t.label.act);
		}
	}
	
	public ConstraintLabeledTransitionSystem(
			Set<LabeledTransition> trans,Set<String> states,Set<String> init) {
		super(LabeledTransition.class);
		//generate a CLTS with given the transitions and states
		//however init are restricted to the states
		
		init.retainAll(states);
		initials = init;

		Act = new HashSet<String>();

		for(Iterator<String> i=states.iterator(); i.hasNext();)
			addVertex(i.next());
		for(Iterator<LabeledTransition> i=trans.iterator(); i.hasNext();)
		{
			LabeledTransition t = i.next();
			addEdge(t.getSrc(),t.getDst(),t);
			Act.add(t.label.act);
		}
	}
	public Set<String> InitialStates()
	{
		return initials;
	}
	
	public boolean setAsInitial(String state)	
	{
		//this will add a previously added states to the set of initial states
		if (!vertexSet().contains(state)) return false;
		else 
		{
			return initials.add(state);			
		}
	}
	
	public void filterTransitions(ActionFormula chi, NetworkConstraint zeta)
	{
		Set<LabeledTransition> trans = edgeSet();
		Set<LabeledTransition> toBeRemovedTrans = new HashSet<LabeledTransition>();
		
		for (Iterator<LabeledTransition> i = trans.iterator(); i.hasNext();)
		{
			LabeledTransition t = i.next();
			
			NetworkConstraint updZeta = new NetworkConstraint(zeta);
			updZeta.update(t.label.nc);
			if (!(chi.satisfy(new StringActionFormula(t.label.act)) && zeta.conforms(t.label.nc)))
				toBeRemovedTrans.add(t);			
		}
		removeAllEdges(toBeRemovedTrans);
	}
	
	public void restrictToStates(Set<String> newState)
	{
		// we assume that newState is a subset of currentStates
		Set<String> currentStates = vertexSet(); 
		Set<String> toBeRemoved = new HashSet<String>();
		for (Iterator<String> i = currentStates.iterator(); i.hasNext();)			
		{
			String s = i.next();
			if (!newState.contains(s)) toBeRemoved.add(s);		
		}
		for (Iterator<String> i = toBeRemoved.iterator(); i.hasNext();)			
		{
			String s = i.next();		
			removeVertex(s);
		}
	}
	public Set<String> getAct()
	{
		return Act;
	}
	public void setAct(Set<String> _Act)
	{
		Act = _Act;
	}
}


