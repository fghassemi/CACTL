package ir.ac.ut.ece.cactlmodelchecker;

public class Pair {

    public char src; //sender
    public char dst; //receiver
    public boolean isConn;

    //added by hassan
    public Pair(char _src, char _dst, boolean _isConn) {
        src = _src;
        dst = _dst;
        isConn = _isConn;
    }

    public Pair(String _src, String _dst, boolean _isConn) {
        src = _src.charAt(0);
        dst = _dst.charAt(0);
        isConn = _isConn;
    }

    public Pair() {
        src = '\0';
        dst = '\0';
    }

    public String toString() {
        if (isConn) {
            return src + "-->" + dst;
        } else {
            return src + "-/->" + dst;
        }
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public boolean equals(Object o) {
		//System.out.println(toString()+"compare");

//		if(!(o instanceof Pair))
//			return false;
//		Pair _pair = (Pair)o;
//
//		boolean srcBothNull = (src==null)? _pair.src == null : _pair.src != null;
//		boolean dstBothNull = (dst==null)? _pair.dst == null : _pair.dst != null;
//		if (srcBothNull && dstBothNull)
//		{
//			if (src!=null && dst!=null)
//				return (src.equals(_pair.src) && dst.equals(_pair.dst) && isConn==_pair.isConn );
//			else if (src!=null && dst==null)
//				return (src.equals(_pair.src) &&  isConn==_pair.isConn );
//			else if (src==null && dst!=null)
//				return (dst.equals(_pair.dst) && isConn==_pair.isConn );
//			else if (src==null && dst==null)
//				return (isConn==_pair.isConn );
//		}
//		else 
//			return false ;
//		return false;
		//added by hassan
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair _pair = (Pair) o;

        boolean srcBothNull = (src == '\0') ? _pair.src == '\0' : _pair.src != '\0';
        boolean dstBothNull = (dst == '\0') ? _pair.dst == '\0' : _pair.dst != '\0';
        if (srcBothNull && dstBothNull) {
            if (src != '\0' && dst != '\0') {
                return (src == _pair.src && dst == _pair.dst && isConn == _pair.isConn);
            } else if (src != '\0' && dst == '\0') {
                return (src == _pair.src && isConn == _pair.isConn);
            } else if (src == '\0' && dst != '\0') {
                return (dst == _pair.dst && isConn == _pair.isConn);
            } else if (src == '\0' && dst == '\0') {
                return (isConn == _pair.isConn);
            }
        } else {
            return false;
        }
        return false;
    }
}
