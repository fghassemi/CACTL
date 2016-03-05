import java.util.HashSet;
import java.util.Set;

public interface ActionFormula {
	public boolean satisfy(StringActionFormula eta);	
	public Set<String> toSet(Set<String> Act);
}

class BasicActionFormula implements ActionFormula
{
	public boolean val;
	
	public BasicActionFormula(boolean _val)
	{
		val = _val;
	}

	@Override
	public boolean satisfy(StringActionFormula eta) {		
		return val;
	}
	public String toString()
	{
		if (val) return "true"; else return "false";
	}

	@Override
	public Set<String> toSet(Set<String> Act) {
		// TODO Auto-generated method stub
		if (val) return Act;
		else return new HashSet<String>();
	}	
}

class StringActionFormula implements ActionFormula
{
	public String val;
	
	public StringActionFormula(String _val)
	{
		val = _val;
	}

	@Override
	public boolean satisfy(StringActionFormula eta) {		
		return val.equals(eta.val);
	}	
	public String toString()
	{
		return val;
	}

	@Override
	public Set<String> toSet(Set<String> Act) {
		Set<String> set = new HashSet<String>();
		set.add(val);
		return set;
	}
}

class NegActionFormula implements ActionFormula
{
	public ActionFormula arg;
	
	public NegActionFormula(ActionFormula _arg)
	{
		arg = _arg;
	}

	@Override
	public boolean satisfy(StringActionFormula eta) {		
		return (!arg.satisfy(eta));
	}	
	public String toString()
	{
		return "~"+arg.toString();
	}

	@Override
	public Set<String> toSet(Set<String> Act) {
		Set<String> set = new HashSet<String>(Act);		
		set.removeAll(arg.toSet(Act));
		return set;
	}
}

class OrActionFormula implements ActionFormula
{
	public ActionFormula arg1;
	public ActionFormula arg2;
	
	public OrActionFormula(ActionFormula _arg1, ActionFormula _arg2)
	{
		arg1 = _arg1;
		arg2 = _arg2;
	}

	@Override
	public boolean satisfy(StringActionFormula eta) {		
		return (arg1.satisfy(eta) || arg2.satisfy(eta));
	}	
	public String toString()
	{
		return "("+arg1.toString()+" \\/ "+arg2.toString()+")";
	}

	@Override
	public Set<String> toSet(Set<String> Act) {
		Set<String> set = arg1.toSet(Act);
		set.addAll(arg2.toSet(Act));
		return set;
	}
}

	class AndActionFormula implements ActionFormula
	{
		public ActionFormula arg1;
		public ActionFormula arg2;
		
		public AndActionFormula(ActionFormula _arg1, ActionFormula _arg2)
		{
			arg1 = _arg1;
			arg2 = _arg2;
		}

		@Override
		public boolean satisfy(StringActionFormula eta) {		
			return (arg1.satisfy(eta) && arg2.satisfy(eta));
		}
		public String toString()
		{
			return "("+arg1.toString()+" /\\ "+arg2.toString()+")";
		}

		@Override
		public Set<String> toSet(Set<String> Act) {
			Set<String> set = arg1.toSet(Act);
			set.retainAll(arg2.toSet(Act));
			return set;
		}	
	}
