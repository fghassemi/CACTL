package ir.ac.ut.ece.cactlmodelchecker;

public class Mark {

    public NetworkConstraint nc;
    public String prestate;
    public Mark premark;

    public Mark(NetworkConstraint _nc, String _state, Mark pre_m) {
        nc = _nc;
        prestate = _state;
        premark = pre_m;
    }

    public Mark(Mark _mark) {
        nc = new NetworkConstraint(_mark.nc);
        prestate = _mark.prestate;
        premark = null;
    }

    public boolean equals(Object o) {
		//System.out.println(toString()+"compare");

        if (!(o instanceof Mark)) {
            return false;
        }
        Mark _mark = (Mark) o;
        boolean stateBothNull = (prestate == null) ? _mark.prestate == null : _mark.prestate != null;
        boolean ncBothNull = (nc == null) ? _mark.nc == null : _mark.nc != null;
        //boolean pmBothNull = (premark==null)? _mark.premark == null : _mark.premark != null;

        if (stateBothNull && ncBothNull) {
            // both are either null or not null
            if (prestate != null && nc != null) {
                return (prestate.equals(_mark.prestate) && nc.links.equals(_mark.nc.links));
            } else if (prestate == null && nc != null) {
                return (nc.links.equals(_mark.nc.links));
            } else if (prestate == null && nc == null) {
                return true;
            } else if (prestate != null && nc == null) {
                return (prestate.equals(_mark.prestate)); //
            }
        } else {
            return false;
        }
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

    public int hashCode() {
        String s;
        if ((prestate != null)) {
            s = "<" + nc.toString() + "," + prestate + ">";
        } else {
            s = "<" + nc.toString() + ",null>";
        }
        return s.hashCode();
    }

    public String toString() {
        if ((prestate != null) && (premark != null)) {
            return "<" + nc.toString() + "," + prestate + "," + premark.toString() + ">";
        } else if ((prestate != null) && (premark == null)) {
            return "<" + nc.toString() + "," + prestate + ",null>";
        } else if ((prestate == null) && (premark == null)) {
            return "<" + nc.toString() + ",null,null>";
        }
        //else if ((prestate==null) && (prenc!=null))
        return "<" + nc.toString() + ",null" + premark.toString() + ">";
    }
}
