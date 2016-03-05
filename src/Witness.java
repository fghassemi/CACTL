
public interface Witness {

}

class EmptySubFormula implements Witness
{
	StateFormula phi;
	public EmptySubFormula(StateFormula p)
	{
		phi = p;
	}
	public String toString()
	{
		return "No state satisfies the subformula "+phi.toString();
	}
}

class ForwardWitness implements Witness 
{
	Run r;
	public ForwardWitness(String s , Mark m)
	{
		// s is the last state over the path
		Run nxt = null;
		while (m!=null)
		{
			Run cur = new Run(s,m.nc,nxt);
			s = m.prestate;
			m = m.premark;
			nxt = cur;
			m = m.premark ;
		}
	}
	public String toString()
	{
		return r.toString();
	}
}

class BackwardWitness
{
	Run r ;
	public BackwardWitness(String s, Tag t)
	{
		// s is the first state over the path
		if (t!=null)
		{
			Run pre = new Run(s,t.nc,null);
			String nstate = t.state;
			t = t.nextTag ;
			while (t!=null)
			{
				Run cur = new Run(nstate,t.nc,null);
				nstate = t.state ;
				pre.next = cur ;
				t = t.nextTag ;
				pre = cur;
			}
		}
	}
	public String toString()
	{
		return r.toString();
	}
}
class Run
{
	public String state ; 
	public NetworkConstraint nc;
	public Run next ;
	
	public Run(){};
	public Run (String s , NetworkConstraint n, Run r)
	{
		state = s;
		nc = n;
		next = r;
	}
	public String toString()
	{
		String s = "";
		Run itr = this;
		while(itr!=null)
		{
			s = s+"("+itr.state+","+itr.nc.toString()+ ")";
			itr = itr.next;
		}
		return s;
	}
}