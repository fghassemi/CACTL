import java.util.HashSet;
import java.util.Set;



public interface TopologyFormula {

	public boolean satisfy(boolean[][] topo, int dim);	
}


class BasicTopologyFormula implements TopologyFormula
{
	public boolean val ;
	public BasicTopologyFormula(boolean _val)
	{
		val = _val;
	}
	@Override
	public boolean satisfy(boolean[][] topo, int dim) {
		return val;
	}
	public String toString()
	{
		if (val) return "true"; else return "false";
	}
	public boolean equals(Object obj)
	{
		if (obj instanceof BasicTopologyFormula) return ((BasicTopologyFormula)obj).val==this.val; 
		else return false;
	}
}

class ConntivityFormula implements TopologyFormula
{
	public String src;
	public String dst;
	
	public ConntivityFormula(String _src, String _dst)
	{
		src = _src;
		dst = _dst;
	}

	@Override
	public boolean satisfy(boolean[][] topo, int dim) 
	{ // by 5 dim 
		int x,y;
		y = (src.equals("A")) ?  0 :
				(src.equals("B"))?  1 :
					(src.equals("C")) ?  2 :
							(src.equals("D")) ? 3 : 4;
		
		x = (dst.equals("A")) ?  0 :
				(dst.equals("B"))?  1 :
					(dst.equals("C")) ?  2 :
						(dst.equals("D")) ? 3 : 4;
		Set<Integer> s = new HashSet<Integer>();
		s.add(new Integer(x*10+y));
		return findPath(x,y,topo,dim,s);
	}	
	

	private boolean findPath(int x, int y, boolean[][] topo, int dim, Set<Integer> links) {
		boolean flag = topo[x][y];
		for(int i=0; i<dim & !flag; i++)
		{
			Integer l1 = new Integer(x*10+i);
			Integer l2 = new Integer(i*10+y);
			if ((i!=x) && (i!=y) && !links.contains(l1) && !links.contains(l2))
			{
				Set<Integer> myLinks = new HashSet<Integer>(links);
				myLinks.add(l1); myLinks.add(l2);
				flag = findPath(x,i,topo,dim,myLinks) && findPath(i,y,topo,dim,myLinks);
			}
		}
		return flag;
	}

	public String toString()
	{
		return src+"--->"+dst;
	}
	public boolean equals(Object obj)
	{
		if (obj instanceof ConntivityFormula) return ((ConntivityFormula)obj).src.equals(this.src) && ((ConntivityFormula)obj).dst.equals(this.dst); 
		else return false;
	}
}

class NegTopologyFormula implements TopologyFormula
{
	public TopologyFormula arg ;
	public NegTopologyFormula(TopologyFormula _arg)
	{
		arg = _arg;
	}
	@Override
	public boolean satisfy(boolean[][] topo, int dim) {
		return (!arg.satisfy(topo,dim));
	}
	public String toString()
	{
		return "~"+arg.toString();
	}
}

class AndTopologyFormula implements TopologyFormula
{
	public TopologyFormula arg1 ;
	public TopologyFormula arg2 ;

	public AndTopologyFormula(TopologyFormula _arg1, TopologyFormula _arg2)
	{
		arg1 = _arg1;
		arg2 = _arg2;
	}

	@Override
	public boolean satisfy(boolean[][] topo, int dim) {
		return (arg1.satisfy(topo,dim) && arg2.satisfy(topo,dim));
	}
	public String toString()
	{
		return arg1.toString()+" /\\ "+arg2.toString();
	}
	public boolean equals(Object obj)
	{
		if (obj instanceof AndTopologyFormula) return ((AndTopologyFormula)obj).arg1.equals(this.arg1) && ((AndTopologyFormula)obj).arg2.equals(this.arg2); 
		else return false;
	}
}