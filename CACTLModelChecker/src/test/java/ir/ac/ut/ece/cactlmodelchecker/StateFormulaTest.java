package ir.ac.ut.ece.cactlmodelchecker;

import ir.ac.ut.ece.cactlmodelchecker.formula.action.OrActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.action.StringActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.path.UntilFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.state.AUStateFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.state.BasicStateFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.state.ExistStateFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.state.NegStateFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.state.StateFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.topology.AndTopologyFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.topology.ConntivityFormula;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StateFormulaTest {

    private static ConstraintLabeledTransitionSystem testCase1() {
        ConstraintLabeledTransitionSystem g
                = new ConstraintLabeledTransitionSystem(LabeledTransition.class);

        String v1 = "v1";
        String v2 = "v2";
        String v3 = "v3";
        String v4 = "v4";

        // add the vertices
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);

        //setting the initial state
        g.setAsInitial(v1);

        // add edges to create a circuit
        NetworkConstraint nc1 = new NetworkConstraint();
        nc1.add(new Pair("A", "B", true));
        nc1.add(new Pair("A", "C", true));
        g.addEdge(v1, v2, new LabeledTransition(new Label(nc1, "a")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("B", "C", true));
        nc1.add(new Pair("B", "D", false));
        g.addEdge(v2, v3, new LabeledTransition(new Label(nc1, "a")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("C", "A", false));
        nc1.add(new Pair("C", "D", false));
        g.addEdge(v3, v4, new LabeledTransition(new Label(nc1, "b")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("B", "C", true));
        nc1.add(new Pair("B", "D", false));
        g.addEdge(v4, v1, new LabeledTransition(new Label(nc1, "c")));

        return g;
    }

    private static ConstraintLabeledTransitionSystem testCase2() {
        ConstraintLabeledTransitionSystem g
                = new ConstraintLabeledTransitionSystem(LabeledTransition.class);

        String v1 = "v1";
        String v2 = "v2";

        // add the vertices
        g.addVertex(v1);
        g.addVertex(v2);

        //setting the initial state
        g.setAsInitial(v1);

        // add edges to create a circuit
        NetworkConstraint nc1 = new NetworkConstraint();
        nc1.add(new Pair("A", "B", true));
        g.addEdge(v1, v2, new LabeledTransition(new Label(nc1, "a")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("A", "B", false));
        g.addEdge(v2, v1, new LabeledTransition(new Label(nc1, "a")));

        return g;
    }

    private static ConstraintLabeledTransitionSystem testCase3() {
        ConstraintLabeledTransitionSystem g
                = new ConstraintLabeledTransitionSystem(LabeledTransition.class);

        String v1 = "v1";
        String v2 = "v2";
        String v3 = "v3";
        String v4 = "v4";

        // add the vertices
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);

        //setting the initial state
        g.setAsInitial(v1);

        // add edges to create a circuit
        NetworkConstraint nc1 = new NetworkConstraint();
        nc1.add(new Pair("A", "B", true));
        g.addEdge(v1, v2, new LabeledTransition(new Label(nc1, "a")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("B", "C", true));
        g.addEdge(v2, v3, new LabeledTransition(new Label(nc1, "a")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("B", "C", false));
        g.addEdge(v3, v2, new LabeledTransition(new Label(nc1, "a")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("A", "D", true));
        g.addEdge(v2, v4, new LabeledTransition(new Label(nc1, "b")));

        return g;
    }

    private static ConstraintLabeledTransitionSystem testCase4() {
        ConstraintLabeledTransitionSystem g
                = new ConstraintLabeledTransitionSystem(LabeledTransition.class);

        String v1 = "v1";
        String v2 = "v2";
        String v3 = "v3";
        String v4 = "v4";
        String v5 = "v5";
        String v6 = "v6";

        // add the vertices
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);
        g.addVertex(v6);

        //setting the initial state
        g.setAsInitial(v1);

        // add edges to create a circuit
        NetworkConstraint nc1 = new NetworkConstraint();
        nc1.add(new Pair("A", "B", true));
        g.addEdge(v1, v2, new LabeledTransition(new Label(nc1, "a")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("B", "C", true));
        g.addEdge(v2, v3, new LabeledTransition(new Label(nc1, "a")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("D", "E", true));
        g.addEdge(v3, v4, new LabeledTransition(new Label(nc1, "a")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("B", "C", false));
        g.addEdge(v4, v5, new LabeledTransition(new Label(nc1, "a")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("C", "D", true));
        g.addEdge(v5, v2, new LabeledTransition(new Label(nc1, "a")));

        nc1 = new NetworkConstraint();
        g.addEdge(v3, v6, new LabeledTransition(new Label(nc1, "b")));

        return g;
    }

    public static ConstraintLabeledTransitionSystem testCase5() {
        ConstraintLabeledTransitionSystem g
                = new ConstraintLabeledTransitionSystem(LabeledTransition.class);

        String M0 = "M0";
        String M11 = "M11";
        String M2 = "M2";
        String M12 = "M12";
        String M3 = "M3";
        String M4 = "M4";
        String M5 = "M5";
        String M6 = "M6";
        String M7 = "M7";

        // add the vertices
        g.addVertex(M0);
        g.addVertex(M11);
        g.addVertex(M12);
        g.addVertex(M2);
        g.addVertex(M3);
        g.addVertex(M4);
        g.addVertex(M5);
        g.addVertex(M6);
        g.addVertex(M7);

        //setting the initial state
        g.setAsInitial(M0);

        // add edges to create a circuit
        NetworkConstraint nc1 = new NetworkConstraint();
        g.addEdge(M0, M11, new LabeledTransition(new Label(nc1, "a")));

        nc1 = new NetworkConstraint();
        g.addEdge(M0, M12, new LabeledTransition(new Label(nc1, "a")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("D", "B", true));
        g.addEdge(M11, M2, new LabeledTransition(new Label(nc1, "tau")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("D", "B", true));
        nc1.add(new Pair("D", "C", false));
        g.addEdge(M12, M2, new LabeledTransition(new Label(nc1, "tau")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("A", "D", true));
        g.addEdge(M2, M3, new LabeledTransition(new Label(nc1, "tau")));

        nc1 = new NetworkConstraint();
        g.addEdge(M3, M4, new LabeledTransition(new Label(nc1, "b")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("B", "C", true));
        g.addEdge(M4, M5, new LabeledTransition(new Label(nc1, "tau")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("D", "C", true));
        g.addEdge(M5, M6, new LabeledTransition(new Label(nc1, "tau")));

        nc1 = new NetworkConstraint();
        g.addEdge(M6, M7, new LabeledTransition(new Label(nc1, "c")));

        return g;
    }

    private static ConstraintLabeledTransitionSystem testCase6() {
        ConstraintLabeledTransitionSystem g
                = new ConstraintLabeledTransitionSystem(LabeledTransition.class);

        String v1 = "v1";
        String v2 = "v2";
        String v3 = "v3";
        String v4 = "v4";

        // add the vertices
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);

        //setting the initial state
        g.setAsInitial(v1);

        // add edges to create a circuit
        NetworkConstraint nc1 = new NetworkConstraint();
        g.addEdge(v1, v2, new LabeledTransition(new Label(nc1, "init")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("A", "B", true));
        g.addEdge(v2, v3, new LabeledTransition(new Label(nc1, "tau")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("B", "A", false));
        g.addEdge(v3, v4, new LabeledTransition(new Label(nc1, "tau")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("A", "B", false));
        g.addEdge(v2, v4, new LabeledTransition(new Label(nc1, "tau")));

        return g;
    }

    private static ConstraintLabeledTransitionSystem testCase7() {
        ConstraintLabeledTransitionSystem g
                = new ConstraintLabeledTransitionSystem(LabeledTransition.class);

        String v1 = "1";
        String v2 = "2";
        String v3 = "3";
        String v4 = "4";
        String v5 = "0";

        // add the vertices
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);

        //setting the initial state
        g.setAsInitial(v1);

        // add edges to create a circuit
        NetworkConstraint nc1 = new NetworkConstraint();
        g.addEdge(v1, v2, new LabeledTransition(new Label(nc1, "init")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("A", "B", true));
        g.addEdge(v2, v3, new LabeledTransition(new Label(nc1, "tau")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("B", "A", false));
        g.addEdge(v3, v4, new LabeledTransition(new Label(nc1, "tau")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("A", "B", false));
        g.addEdge(v2, v4, new LabeledTransition(new Label(nc1, "tau")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("A", "B", true));
        g.addEdge(v3, v5, new LabeledTransition(new Label(nc1, "tau")));

        nc1 = new NetworkConstraint();
        g.addEdge(v5, v1, new LabeledTransition(new Label(nc1, "succ")));

        return g;
    }

    public static ConstraintLabeledTransitionSystem testCase8() {
        ConstraintLabeledTransitionSystem g
                = new ConstraintLabeledTransitionSystem(LabeledTransition.class);

        String v0 = "0";
        String v1 = "1";
        String v2 = "2";
        String v3 = "3";
        String v4 = "4";
        String v5 = "5";
        String v6 = "6";
        String v7 = "7";
        String v8 = "8";
        String v9 = "9";

        // add the vertices
        g.addVertex(v0);
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);
        g.addVertex(v6);
        g.addVertex(v7);
        g.addVertex(v8);
        g.addVertex(v9);

        //setting the initial state
        g.setAsInitial(v0);
        g.setAsInitial(v5);

        // add edges to create a circuit
        NetworkConstraint nc1 = new NetworkConstraint();
        g.addEdge(v0, v1, new LabeledTransition(new Label(nc1, "a")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("D", "A", false));
        nc1.add(new Pair("D", "B", false));
        nc1.add(new Pair("D", "C", true));
        g.addEdge(v1, v2, new LabeledTransition(new Label(nc1, "tau")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("C", "A", true));
        nc1.add(new Pair("C", "D", true));
        nc1.add(new Pair("C", "B", false));
        g.addEdge(v2, v3, new LabeledTransition(new Label(nc1, "tau")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("C", "A", true));
        nc1.add(new Pair("C", "B", true));
        nc1.add(new Pair("C", "D", false));
        g.addEdge(v2, v4, new LabeledTransition(new Label(nc1, "tau")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("D", "A", true));
        nc1.add(new Pair("D", "B", true));
        nc1.add(new Pair("D", "C", false));
        g.addEdge(v5, v4, new LabeledTransition(new Label(nc1, "tau")));

        nc1 = new NetworkConstraint();
        g.addEdge(v4, v6, new LabeledTransition(new Label(nc1, "b")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("B", "A", false));
        nc1.add(new Pair("B", "C", false));
        nc1.add(new Pair("B", "D", false));
        g.addEdge(v6, v8, new LabeledTransition(new Label(nc1, "tau")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("B", "C", true));
        nc1.add(new Pair("B", "A", false));
        nc1.add(new Pair("B", "D", false));
        g.addEdge(v6, v7, new LabeledTransition(new Label(nc1, "tau")));

        nc1 = new NetworkConstraint();
        g.addEdge(v7, v9, new LabeledTransition(new Label(nc1, "c")));

        return g;
    }

    @Test
    public void testSimpleExistUntilFormula_untilpath() {
        ConstraintLabeledTransitionSystem CLTS = testCase1();
        StateFormula varphi = new ExistStateFormula(
                new UntilFormula(new BasicStateFormula(true), new BasicStateFormula(true),
                        new StringActionFormula("a"), new StringActionFormula("b"), new ConntivityFormula("A", "B"), 3));
        assertTrue("testing the until path on test case 1 ", CACTLMC.modelCheck(CLTS, varphi, new NetworkConstraint()));
    }

    @Test
    public void testSimpleExistUntilFormula_topology() {
        ConstraintLabeledTransitionSystem CLTS = testCase1();
        StateFormula varphi = new ExistStateFormula(
                new UntilFormula(new BasicStateFormula(true), new BasicStateFormula(true),
                        new StringActionFormula("a"), new StringActionFormula("b"), new ConntivityFormula("A", "C"), 3));
        assertTrue("testing topology on test case 1", CACTLMC.modelCheck(CLTS, varphi, new NetworkConstraint()));
    }

    @Test
    public void testSimpleExistUntilFormula_onlySimpleLoop() {
        ConstraintLabeledTransitionSystem CLTS = testCase2();
        StateFormula varphi = new ExistStateFormula(
                new UntilFormula(new BasicStateFormula(true), new BasicStateFormula(true),
                        new StringActionFormula("a"), new StringActionFormula("b"), new ConntivityFormula("A", "B"), 3));
        assertTrue("just a loop on test case 2", !CACTLMC.modelCheck(CLTS, varphi, new NetworkConstraint()));
    }

    @Test
    public void testSimpleExistUntilFormula_simpleLoop() {
        ConstraintLabeledTransitionSystem CLTS = testCase3();
        StateFormula varphi = new ExistStateFormula(
                new UntilFormula(new BasicStateFormula(true), new BasicStateFormula(true),
                        new StringActionFormula("a"), new StringActionFormula("b"), new ConntivityFormula("A", "C"), 3));
        assertTrue("loop in middle on test case 3", CACTLMC.modelCheck(CLTS, varphi, new NetworkConstraint()));
    }

    @Test
    public void testSimpleExistUntilFormula_ComplexTopology() {
        ConstraintLabeledTransitionSystem CLTS = testCase4();
        StateFormula varphi = new ExistStateFormula(
                new UntilFormula(new BasicStateFormula(true), new BasicStateFormula(true),
                        new StringActionFormula("a"), new StringActionFormula("b"), new ConntivityFormula("A", "D"), 4));
        assertTrue("loop in middle on test case 4", CACTLMC.modelCheck(CLTS, varphi, new NetworkConstraint()));
    }

    @Test
    public void testSimpleExistUntilFormula_InvalidPath() {
        ConstraintLabeledTransitionSystem CLTS = testCase6();
        StateFormula varphi = new ExistStateFormula(
                new UntilFormula(new BasicStateFormula(true), new BasicStateFormula(true),
                        new OrActionFormula(new StringActionFormula("init"), new StringActionFormula("tau")), new StringActionFormula("succ"),
                        new AndTopologyFormula(new ConntivityFormula("A", "B"), new ConntivityFormula("B", "A")), 2));
        assertTrue("model of invalid paths", !CACTLMC.modelCheck(CLTS, varphi, new NetworkConstraint()));
    }

    @Test
    public void testSimpleExistUntilFormula_OrAction() {
        ConstraintLabeledTransitionSystem CLTS = testCase5();
        StateFormula varphi = new ExistStateFormula(
                new UntilFormula(new BasicStateFormula(true), new BasicStateFormula(true),
                        new OrActionFormula(new StringActionFormula("a"), new StringActionFormula("tau")), new StringActionFormula("b"), new ConntivityFormula("A", "B"), 3));
        assertTrue("finding topology while we have or of action over test case 5", CACTLMC.modelCheck(CLTS, varphi, new NetworkConstraint()));
    }

    //@Test
    public void testNestedExistUntilFormula_OrAction() {
        ConstraintLabeledTransitionSystem CLTS = testCase5();
        StateFormula varphi1 = new ExistStateFormula(
                new UntilFormula(new BasicStateFormula(true), new BasicStateFormula(true),
                        new OrActionFormula(new StringActionFormula("a"), new StringActionFormula("tau")), new StringActionFormula("c"), new ConntivityFormula("A", "C"), 3));
        StateFormula varphi2 = new ExistStateFormula(
                new UntilFormula(new BasicStateFormula(true), varphi1,
                        new OrActionFormula(new StringActionFormula("a"), new StringActionFormula("tau")), new StringActionFormula("b"), new ConntivityFormula("A", "B"), 3));

        assertTrue("nesting exist until formula over test case 5", CACTLMC.modelCheck(CLTS, varphi2, new NetworkConstraint()));
    }

    @Test
    public void testAllUntilFormula() {
        ConstraintLabeledTransitionSystem CLTS = testCase7();
        StateFormula varphi = new AUStateFormula(
                new UntilFormula(new BasicStateFormula(true), new BasicStateFormula(true),
                        new OrActionFormula(new StringActionFormula("init"), new StringActionFormula("tau")),
                        new StringActionFormula("succ"),
                        new AndTopologyFormula(new ConntivityFormula("A", "B"), new ConntivityFormula("B", "A")), 2));

        assertTrue("all until formula over test case 7", CACTLMC.modelCheck(CLTS, varphi, new NetworkConstraint()));
    }

    @Test
    public void testNestedExistUntilFormula() {
        ConstraintLabeledTransitionSystem CLTS = testCase8();
        StateFormula varphi1 = new ExistStateFormula(
                new UntilFormula(new BasicStateFormula(true), new BasicStateFormula(true),
                        new OrActionFormula(new StringActionFormula("a"), new StringActionFormula("tau")), new StringActionFormula("c"), new ConntivityFormula("B", "C"), 4));
        StateFormula varphi2 = new ExistStateFormula(
                new UntilFormula(new BasicStateFormula(true), varphi1,
                        new OrActionFormula(new StringActionFormula("a"), new StringActionFormula("tau")), new StringActionFormula("b"), new ConntivityFormula("D", "B"), 4));

        assertTrue("nesting two exist until formule over test case 8", CACTLMC.modelCheck(CLTS, varphi2, new NetworkConstraint()));
    }

    @Test
    public void testNestedAllExistUntilFormula() {
        ConstraintLabeledTransitionSystem CLTS = testCase8();
        StateFormula varphi1 = new ExistStateFormula(
                new UntilFormula(new BasicStateFormula(true), new BasicStateFormula(true),
                        new OrActionFormula(new StringActionFormula("a"), new StringActionFormula("tau")), new StringActionFormula("c"), new ConntivityFormula("B", "C"), 4));
        StateFormula varphi2 = new AUStateFormula(
                new UntilFormula(new BasicStateFormula(true), varphi1,
                        new OrActionFormula(new StringActionFormula("a"), new StringActionFormula("tau")), new StringActionFormula("b"), new ConntivityFormula("D", "B"), 4));

        assertTrue("nesting an exist until in all formule over test case 8", !CACTLMC.modelCheck(CLTS, varphi2, new NetworkConstraint()));
    }

//?	@Test
    public void testNestedAllNotExistUntilFormula() {
        ConstraintLabeledTransitionSystem CLTS = testCase5();
        StateFormula varphi1 = new NegStateFormula(new ExistStateFormula(
                new UntilFormula(new BasicStateFormula(true), new BasicStateFormula(true),
                        new OrActionFormula(new StringActionFormula("a"), new StringActionFormula("tau")), new StringActionFormula("c"), new ConntivityFormula("A", "C"), 3)));
        StateFormula varphi2 = new AUStateFormula(
                new UntilFormula(new BasicStateFormula(true), varphi1,
                        new OrActionFormula(new StringActionFormula("a"), new StringActionFormula("tau")), new StringActionFormula("b"), new ConntivityFormula("A", "B"), 3));

        assertTrue("nesting all and exist until formula over test case 5", !CACTLMC.modelCheck(CLTS, varphi2, new NetworkConstraint()));
    }

    @Test
    public void testNestedAllUntilFormula() {
        ConstraintLabeledTransitionSystem CLTS = testCase8();
        StateFormula varphi1 = new AUStateFormula(
                new UntilFormula(new BasicStateFormula(true), new BasicStateFormula(true),
                        new OrActionFormula(new StringActionFormula("a"), new StringActionFormula("tau")), new StringActionFormula("c"), new ConntivityFormula("B", "C"), 4));
        StateFormula varphi2 = new AUStateFormula(
                new UntilFormula(new BasicStateFormula(true), varphi1,
                        new OrActionFormula(new StringActionFormula("a"), new StringActionFormula("tau")), new StringActionFormula("b"), new ConntivityFormula("D", "B"), 4));

        assertTrue("nesting two all until formule over test case 8", !CACTLMC.modelCheck(CLTS, varphi2, new NetworkConstraint()));
    }

}
