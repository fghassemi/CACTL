package ir.ac.ut.ece.cactlmodelchecker;

import java.util.Hashtable;

import org.junit.Test;

public class HashTest {

    @Test
    public void puttest() {
        Hashtable<String, NetworkConstraint> htb = new Hashtable<String, NetworkConstraint>();
        htb.put("s0", new NetworkConstraint());
        NetworkConstraint nc = new NetworkConstraint();
        System.out.println(htb.get("s0"));
        nc.add(new Pair("A", "B", false));
        htb.put("s0", nc);
        System.out.println(htb.get("s0"));
        System.out.println(htb.get("s1"));
    }
}
