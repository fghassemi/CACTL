package ir.ac.ut.ece.cactlmodelchecker;

import java.io.Serializable;
import java.util.*;

public class Item implements Serializable {

    public String state;
    public Set<Integer> topo;

    public Item(String _state, Set<Integer> _topo) {
        state = _state;
        topo = _topo;
    }

//    public int hasCode() {
//        return state.hashCode() + topo.hashCode();
//    }

    @Override
    public int hashCode() {
        return state.hashCode() + topo.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Item other = (Item) obj;
        if (!Objects.equals(this.state, other.state)) {
            return false;
        }
        if (!Objects.equals(this.topo, other.topo)) {
            return false;
        }
        return true;
    }

    public String toString() {
        String o;
        if (state != null) {
            o = "(" + state.toString() + ",";
        } else {
            o = "(null,";
        }
        if (topo != null) {
            o = o + topo.toString() + ")";
        } else {
            o = o + "null)";
        }
        return o;
    }

}


