
public class Tag {
	public NetworkConstraint nc ;
	public String state ;
	public Tag nextTag;
	
	
	public Tag(NetworkConstraint _nc, String _state,Tag t)
	{
		nc = _nc ;
		state = _state ;
		nextTag = t;
	}
	
	public boolean equals(Object o)
	{    	
		//System.out.println(toString()+"compare");
		
		if(!(o instanceof Tag))
			return false;
		Tag _t = (Tag)o;
		boolean stateBothNull = (state==null)? _t.state == null : _t.state != null;
		boolean ncBothNull = (nc==null)? _t.nc == null : _t.nc != null;
		//boolean pmBothNull = (premark==null)? _mark.premark == null : _mark.premark != null;

		if (stateBothNull && ncBothNull)
		{
			// both are either null or not null
			if (state !=null && nc!=null)
				return (state.equals(_t.state) && nc.links.equals(_t.nc.links));
			else if (state ==null && nc!=null)
				return (nc.links.equals(_t.nc.links));
			else if (state ==null && nc==null)
				return true;
			else if (state !=null && nc==null)
				return (state.equals(_t.state)); //
		}
		else return false;
		return false;
		
		
		
		/*if (stateBothNull && ncBothNull && pmBothNull)
		{
			// both are either null or not null
			if (prestate !=null && nc!=null && premark!=null)
				return (prestate.equals(_mark.prestate) && nc.links.equals(_mark.nc.links) && premark.equals(_mark.premark));
			else if (prestate ==null && nc!=null && premark==null)
				return (nc.links.equals(_mark.nc.links));
			else if (prestate ==null && nc==null && premark==null)
				return true;
			else if (prestate !=null && nc==null  && premark==null)
				return (prestate.equals(_mark.prestate)); //
			else if (prestate ==null && nc!=null && premark!=null)
				return (nc.links.equals(_mark.nc.links) && premark.equals(_mark.premark));
			else if (prestate ==null && nc==null && premark!=null)
				return (premark.equals(_mark.premark));
			else if (prestate !=null && nc==null  && premark!=null)
				return (prestate.equals(_mark.prestate)&& premark.equals(_mark.premark));
			else if (prestate !=null && nc!=null  && premark==null)
				return (prestate.equals(_mark.prestate) && nc.links.equals(_mark.nc.links));
		}	
		else return false;
		return false;*/
	}
	
	public int hashCode() 
	{
		String s ;
		if ((state!=null))
			s= "<"+nc.toString()+","+state+">";
		else s= "<"+nc.toString()+",null>";
		return s.hashCode();
	}
	
	public String toString()
	{
		if ((state!=null) && (nextTag!=null))
			return "<"+nc.toString()+","+state+","+nextTag.toString()+">";
		else if ((state!=null) && (nextTag==null))
			return "<"+nc.toString()+","+state+",null>";
		else if ((state==null) && (nextTag==null))
			return "<"+nc.toString()+",null,null>";
		return "<"+nc.toString()+",null"+nextTag.toString()+">";
	}

}
