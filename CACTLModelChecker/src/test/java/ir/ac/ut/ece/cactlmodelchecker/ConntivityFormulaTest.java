package ir.ac.ut.ece.cactlmodelchecker;

import ir.ac.ut.ece.cactlmodelchecker.formula.topology.ConntivityFormula;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class ConntivityFormulaTest {

    @Test
    public void SatisfyTester() {
//		boolean[][] topo = new boolean[4][4];
//		//topo[0][0] = false; topo[1][1] = false; topo[2][2] = false; topo[3][3] = false;
//		topo[2][0] = true; topo[3][1] = true;topo[1][2] = true;
//		Set<Pair> links = new HashSet<Pair>();
//		links.add(new Pair("A","B",true));
//		links.add(new Pair("A","C",true));
//		links.add(new Pair("B","C",true));
//		links.add(new Pair("B","D",false));
//		links.add(new Pair("C","A",false));
//		links.add(new Pair("C","D",false));
//		ConntivityFormula CF0 = new ConntivityFormula("A","B");
//		assertTrue("recursive call of satisfy",CF0.satisfy(topo,4));
//		ConntivityFormula CF1 = new ConntivityFormula("A","D");
//		assertTrue("recursive call of satisfy",CF1.satisfy(topo,4));
//		ConntivityFormula CF2 = new ConntivityFormula("C","A");
//		assertTrue("recursive call of satisfy",!CF2.satisfy(topo,4));	
        NetworkConstraint nc = new NetworkConstraint();
        nc.add(new Pair("B", "A", false));
        nc.add(new Pair("B", "C", false));
        nc.add(new Pair("B", "D", false));
        //System.out.println(PathFormula.OverInvalidPath(nc));
        System.out.println(new ConntivityFormula("B", "C").equals(new ConntivityFormula("B", "D")));
    }

    //@Test
    public void conformTesterTrue() {
        NetworkConstraint nc1 = new NetworkConstraint();
        nc1.add(new Pair("A", "B", true));
        nc1.add(new Pair("A", "C", true));

        NetworkConstraint nc2 = new NetworkConstraint();
        nc2.add(new Pair("A", "B", true));

        assertTrue("x", nc1.conforms(nc2));
    }

    //@Test
    public void conformTesterFalse() {
        NetworkConstraint nc1 = new NetworkConstraint();
        nc1.add(new Pair("A", "B", true));
        nc1.add(new Pair("A", "C", true));

        NetworkConstraint nc2 = new NetworkConstraint();
        nc2.add(new Pair("A", "B", false));

        assertTrue("x", !nc2.conforms(nc1));
    }

    //@Test
    public void retainAllTester() {
        Set<String> l1 = new HashSet<String>();
        l1.add("1");
        l1.add("2");
        l1.add("3");
        l1.add("4");
        Set<String> l2 = new HashSet<String>();
        l2.add("1");
        l2.add("2");
        l2.add("5");
        l2.add("6");
        Set<String> l3 = new HashSet<String>();
        l2.add("7");
        l2.add("8");

        l1.retainAll(l2);
        System.out.println("has in common" + l1);
        l1.retainAll(l3);
        System.out.println("has no value in common" + l1);
    }

}
