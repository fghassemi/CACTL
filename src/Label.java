
public class Label {
	public NetworkConstraint nc ;
	public String act;
	
	public Label(NetworkConstraint _ns, String _act)
	{
		nc = _ns;
		act = _act;
	}
	public String toString()
	{
		return "("+ nc +","+ act+")";
	}
}

