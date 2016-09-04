package ir.ac.ut.ece.cactlmodelchecker;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class NetworkConstraint {
	// the sequence of pairs is important

    public Set<Pair> links;

    public NetworkConstraint() {
        links = new HashSet<Pair>();
    }

    public NetworkConstraint(NetworkConstraint _nc) {
        links = new HashSet<Pair>();
        for (Iterator<Pair> i = _nc.links.iterator(); i.hasNext();) {
            Pair p = i.next();
            Pair np = new Pair(p.src, p.dst, p.isConn);
            links.add(np);
        }
    }

    public void add(Pair p) {
        Pair negP = new Pair(p.src, p.dst, !p.isConn);
        //Pair PairToBeRemoved = new Pair(); 
        if (!links.contains(p)) {
            /*Pair tempP = new Pair();
             for (ListIterator<Pair> i = links.listIterator(); i.hasNext(); )
             {
             tempP = i.next();
             if (tempP.equals(negP)) 
             PairToBeRemoved=tempP;
             }*/
            if (links.contains(negP)) {
                links.remove(negP);
            }
            links.add(p);
        }
    }

    public boolean update(NetworkConstraint nc) {
        //returns true if it changes
        boolean flag = false;
        for (Iterator<Pair> i = nc.links.iterator(); i.hasNext();) //add(i.next());
        {
            flag = links.add(i.next()) || flag;
        }
        return flag;
    }

    public boolean include(NetworkConstraint nc) {
        return links.containsAll(nc.links);
    }

    private void neg(NetworkConstraint nc) {
        for (Iterator<Pair> i = nc.links.iterator(); i.hasNext();) {
            Pair p = i.next();
            p.isConn = !p.isConn;
        }
    }

    public boolean conforms(NetworkConstraint zeta) {
        // nc conforms to zeta , if nc \cap \neg zeta = true
        NetworkConstraint temp = new NetworkConstraint(zeta);
        neg(temp);
        boolean flag = true;
        for (Iterator<Pair> i = temp.links.iterator(); i.hasNext();) {
            if (this.links.contains(i.next())) {
                flag = false;
                break;
            }
        }
        return flag;
        /*temp.links.retainAll(this.links);
         return temp.links.isEmpty();*/
    }

    public String toString() {
        String prettyPrint = "[";
        for (Iterator<Pair> i = links.iterator(); i.hasNext();) {
            prettyPrint = prettyPrint + i.next().toString();
            if (i.hasNext()) {
                prettyPrint = prettyPrint + ",";
            }
        }
        return prettyPrint + "]";
    }

    public boolean equals(Object o) {
		//System.out.println(toString()+"compare");

        if (!(o instanceof NetworkConstraint)) {
            return false;
        }
        NetworkConstraint nc = (NetworkConstraint) o;
        return nc.links.containsAll(this.links) && this.links.containsAll(nc.links);
    }
}
///////////////////////////////////////////////////////////////////////

//public class NetworkConstraint {
//	// the sequence of pairs is important
	//public Set<Pair> links;
//	static int Num = 5;
//	public  Pair[] links = new Pair[5];
//	
//	public NetworkConstraint()
//	{
//	}
//	public NetworkConstraint(NetworkConstraint _nc)
//	{
//		for(int i = 0; i < _nc.links.length; i++)
//		{
//			if(_nc.links[i] == null)
//				links[i] = null;
//			else
//			{
//				links[i] = new Pair(_nc.links[i].src,_nc.links[i].dst,_nc.links[i].isConn);
//			}
//		}
//	}
//	
//	public void add(Pair p)
//	{
//		// replaces the previously \neg p or the first empty index
//		Pair negP = new Pair(p.src,p.dst,!p.isConn);
//		
//		if (!contains(p))
//		{
//			int index=-1;
//			for(int i = 0; i < links.length && index>=0; i++)
//				if(links[i].equals(negP))
//					index = i;
//				else if(links[i] == null && index <0)
//					index = i;
//			links[index] = p;
//		}		
//	}
//	
//	public boolean contains(Pair p)
//	{
//		boolean containsP = false;
//		for(int i = 0; i < links.length && !containsP; i++)
//			if(links[i].equals(p))
//				containsP = true;
//		return containsP;
//	}
//	
//
//	public boolean update(NetworkConstraint nc)
//	{
//		//returns true if it changes
////		boolean flag = false ;
////		for (Iterator<Pair> i = nc.links.iterator(); i.hasNext(); )
////			//add(i.next());
////			flag = links.add(i.next()) || flag;
////		return flag;
//		
//		//hassan
//		boolean flag = false ;
//		for(int i = 0; i < nc.links.length; i++)
//			if(!contains(nc.links[i]))
//			{
//				add(nc.links[i]);
//				flag = true;
//			}
//		return flag;
//	}
//	
//	public boolean include(NetworkConstraint nc)
//	{
//		//return links.containsAll(nc.links);
//		
//		//hassan
//		boolean flag = true;
//		for(int i = 0; i < nc.links.length; i++)
//			if(nc.links[i]!=null && !contains(nc.links[i]))
//				flag  = false;
//		return flag;		
//	}
//
//	private void neg(NetworkConstraint nc)
//	{
////		for (Iterator<Pair> i = nc.links.iterator(); i.hasNext(); )
////		{
////			Pair p = i.next();
////			p.isConn = ! p.isConn;
////		}
//		
//		//hassan
//		for(int i = 0; i < nc.links.length; i++)
//			if(nc.links[i]!= null)	
//				nc.links[i].isConn = ! nc.links[i].isConn;
//		
//	}
//	
//	public boolean conforms(NetworkConstraint zeta)
//	{
////		// nc conforms to zeta , if nc \cap \neg zeta = true
////		NetworkConstraint temp = new NetworkConstraint(zeta);
////		neg(temp);
////		boolean flag = true ;
////		for (Iterator<Pair> i = temp.links.iterator(); i.hasNext(); )
////			if (this.links.contains(i.next()))
////			{
////				flag = false ;
////				break;
////			}
////		return flag;
////		/*temp.links.retainAll(this.links);
////		return temp.links.isEmpty();*/
//		
//		//hassan
//		NetworkConstraint temp = new NetworkConstraint(zeta);
//		neg(temp);
//		boolean flag = true ;
//		for (int i = 0; i< temp.links.length; i++)
//			if (contains(temp.links[i]))
//			{
//				flag = false ;
//				break;
//			}
//		return flag;	
//	}
//	
//	public String toString()
//	{
////		String prettyPrint = "[";
////		for (Iterator<Pair> i = links.iterator(); i.hasNext();)
////		{
////			prettyPrint = prettyPrint + i.next().toString();
////			if (i.hasNext()) 
////				prettyPrint = prettyPrint + ",";
////		}
////		return prettyPrint+"]";
//		
//		//hassan
//		String prettyPrint = "[";
//		String seperator = "";
//		for(int i = 0; i < links.length; i++)
//			if(links[i]!= null)
//			{
//				prettyPrint = prettyPrint + seperator+ links[i].toString();
//				seperator = ",";
//			}
//		return prettyPrint+"]";
//	}
//	
//	public boolean equals(Object o)
//	{    	
//		//System.out.println(toString()+"compare");
//		
////		if(!(o instanceof NetworkConstraint))
////			return false; 
////		NetworkConstraint nc = (NetworkConstraint)o;
////		return nc.links.containsAll(this.links) && this.links.containsAll(nc.links);
//		
//		//hassan
//		if(!(o instanceof NetworkConstraint))
//			return false;
//		NetworkConstraint nc = (NetworkConstraint)o;
//		return nc.include(this) && this.include(nc);		
//	}
//}
