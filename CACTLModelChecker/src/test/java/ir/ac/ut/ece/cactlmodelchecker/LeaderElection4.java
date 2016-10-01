package ir.ac.ut.ece.cactlmodelchecker;

import ir.ac.ut.ece.cactlmodelchecker.formula.action.BasicActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.action.OrActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.action.StringActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.path.FinallyFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.path.GenerallyFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.path.NextFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.state.AUStateFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.state.AWStateFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.state.AndStateFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.state.BasicStateFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.state.ExistStateFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.state.ImplyStateFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.state.NegStateFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.state.StateFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.topology.AndTopologyFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.topology.ConntivityFormula;
import ir.ac.ut.ece.cactlmodelchecker.formula.topology.TopologyFormula;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

public class LeaderElection4 {

    static ConstraintLabeledTransitionSystem CLTS;

    @BeforeClass
    public static void setUpClass() throws Exception {
        CLTS = CACTLMC.loadCLTS("..\\Node4.aut");
        System.out.println("Running tests are startd ....");
    }

    @Test
    public void ConvergeAllShort() {
        TopologyFormula mu1 = new AndTopologyFormula(new ConntivityFormula("A",
                "B"), new AndTopologyFormula(new ConntivityFormula("B", "C"),
                        new AndTopologyFormula(new ConntivityFormula("C", "D"),
                                new ConntivityFormula("D", "A"))));
        StateFormula p1 = new AUStateFormula(new FinallyFormula(mu1,
                new BasicActionFormula(true), new AndStateFormula(
                        new ExistStateFormula(new NextFormula(
                                        new StringActionFormula("finish(D,A)"),
                                        new BasicStateFormula(true), 4)),
                        new AndStateFormula(new ExistStateFormula(
                                        new NextFormula(new StringActionFormula(
                                                        "finish(D,B)"), new BasicStateFormula(
                                                        true), 4)), new AndStateFormula(
                                        new ExistStateFormula(new NextFormula(
                                                        new StringActionFormula("finish(D,C)"),
                                                        new BasicStateFormula(true), 4)),
                                        new ExistStateFormula(new NextFormula(
                                                        new StringActionFormula("finish(D,D)"),
                                                        new BasicStateFormula(true), 4))))), 4));

        assertTrue("All nodes converge to D",
                CACTLMC.modelCheck(CLTS, p1, new NetworkConstraint()));

    }

    // @Test
    public void ConvergeAll() {
        TopologyFormula mu1 = new AndTopologyFormula(new ConntivityFormula("A",
                "D"), new AndTopologyFormula(new ConntivityFormula("D", "A"),
                        new AndTopologyFormula(new ConntivityFormula("B", "D"),
                                new AndTopologyFormula(new ConntivityFormula("D", "B"),
                                        new AndTopologyFormula(new ConntivityFormula(
                                                        "C", "D"), new ConntivityFormula("D",
                                                        "C"))))));
        StateFormula p1 = new AUStateFormula(new FinallyFormula(mu1,
                new BasicActionFormula(true), new AndStateFormula(
                        new ExistStateFormula(new NextFormula(
                                        new StringActionFormula("finish(D,A)"),
                                        new BasicStateFormula(true), 4)),
                        new AndStateFormula(new ExistStateFormula(
                                        new NextFormula(new StringActionFormula(
                                                        "finish(D,B)"), new BasicStateFormula(
                                                        true), 4)), new AndStateFormula(
                                        new ExistStateFormula(new NextFormula(
                                                        new StringActionFormula("finish(D,C)"),
                                                        new BasicStateFormula(true), 4)),
                                        new ExistStateFormula(new NextFormula(
                                                        new StringActionFormula("finish(D,D)"),
                                                        new BasicStateFormula(true), 4))))), 4));

        assertTrue("All nodes converge to D",
                CACTLMC.modelCheck(CLTS, p1, new NetworkConstraint()));

    }

    @Test
    public void MergeTwoComponents() {

        TopologyFormula mu2 = new AndTopologyFormula(new ConntivityFormula("A",
                "B"), new AndTopologyFormula(new ConntivityFormula("B", "A"),
                        new AndTopologyFormula(new ConntivityFormula("B", "C"),
                                new AndTopologyFormula(new ConntivityFormula("C", "B"),
                                        new AndTopologyFormula(new ConntivityFormula(
                                                        "A", "C"), new ConntivityFormula("C",
                                                        "A"))))));

        StateFormula sf12 = new AndStateFormula(new ExistStateFormula(
                new NextFormula(new StringActionFormula("finish(B,A)"),
                        new BasicStateFormula(true), 4)), new AndStateFormula(
                        new ExistStateFormula(new NextFormula(new StringActionFormula(
                                                "finish(B,B)"), new BasicStateFormula(true), 4)),
                        new AndStateFormula(new ExistStateFormula(new NextFormula(
                                                new StringActionFormula("finish(D,C)"),
                                                new BasicStateFormula(true), 4)),
                                new ExistStateFormula(new NextFormula(
                                                new StringActionFormula("finish(D,C)"),
                                                new BasicStateFormula(true), 4)))));

        StateFormula sf22 = new AUStateFormula(new FinallyFormula(mu2,
                new BasicActionFormula(true), new AndStateFormula(
                        new ExistStateFormula(new NextFormula(
                                        new StringActionFormula("finish(D,A)"),
                                        new BasicStateFormula(true), 4)),
                        new AndStateFormula(new ExistStateFormula(
                                        new NextFormula(new StringActionFormula(
                                                        "finish(D,B)"), new BasicStateFormula(
                                                        true), 4)), new AndStateFormula(
                                        new ExistStateFormula(new NextFormula(
                                                        new StringActionFormula("finish(D,C)"),
                                                        new BasicStateFormula(true), 4)),
                                        new ExistStateFormula(new NextFormula(
                                                        new StringActionFormula("finish(D,D)"),
                                                        new BasicStateFormula(true), 4))))), 4));

        StateFormula p2 = new AWStateFormula(new GenerallyFormula(
                new ImplyStateFormula(sf12, sf22),
                new BasicActionFormula(true), 4));
        assertTrue("Merge two components",
                CACTLMC.modelCheck(CLTS, p2, new NetworkConstraint()));
    }

    @Test
    public void MergeThreeComponents() {
        TopologyFormula mu3 = new AndTopologyFormula(new ConntivityFormula("C",
                "A"), new AndTopologyFormula(new ConntivityFormula("C", "A"),
                        new AndTopologyFormula(new ConntivityFormula("D", "C"),
                                new ConntivityFormula("C", "D"))));

        TopologyFormula mu4 = new AndTopologyFormula(new ConntivityFormula("C",
                "A"), new AndTopologyFormula(new ConntivityFormula("C", "A"),
                        new AndTopologyFormula(new ConntivityFormula("D", "C"),
                                new AndTopologyFormula(new ConntivityFormula("C", "D"),
                                        new AndTopologyFormula(new ConntivityFormula(
                                                        "B", "C"), new ConntivityFormula("C",
                                                        "B"))))));

        StateFormula sf13 = new AndStateFormula(new ExistStateFormula(
                new NextFormula(new StringActionFormula("finish(C,A)"),
                        new BasicStateFormula(true), 4)), new AndStateFormula(
                        new ExistStateFormula(new NextFormula(new StringActionFormula(
                                                "finish(C,C)"), new BasicStateFormula(true), 4)),
                        new AndStateFormula(new ExistStateFormula(new NextFormula(
                                                new StringActionFormula("finish(B,B)"),
                                                new BasicStateFormula(true), 4)),
                                new ExistStateFormula(new NextFormula(
                                                new StringActionFormula("finish(D,D)"),
                                                new BasicStateFormula(true), 4)))));

        StateFormula sf33 = new AUStateFormula(new FinallyFormula(mu4,
                new BasicActionFormula(true), new AndStateFormula(
                        new ExistStateFormula(new NextFormula(
                                        new StringActionFormula("finish(D,A)"),
                                        new BasicStateFormula(true), 4)),
                        new AndStateFormula(new ExistStateFormula(
                                        new NextFormula(new StringActionFormula(
                                                        "finish(D,B)"), new BasicStateFormula(
                                                        true), 4)), new AndStateFormula(
                                        new ExistStateFormula(new NextFormula(
                                                        new StringActionFormula("finish(D,C)"),
                                                        new BasicStateFormula(true), 4)),
                                        new ExistStateFormula(new NextFormula(
                                                        new StringActionFormula("finish(D,D)"),
                                                        new BasicStateFormula(true), 4))))), 4));

        StateFormula sf23 = new AUStateFormula(new FinallyFormula(mu3,
                new BasicActionFormula(true), new ImplyStateFormula(
                        new AndStateFormula(new ExistStateFormula(
                                        new NextFormula(new StringActionFormula(
                                                        "finish(D,A)"), new BasicStateFormula(
                                                        true), 4)), new AndStateFormula(
                                        new ExistStateFormula(new NextFormula(
                                                        new StringActionFormula("finish(D,C)"),
                                                        new BasicStateFormula(true), 4)),
                                        new ExistStateFormula(new NextFormula(
                                                        new StringActionFormula("finish(D,D)"),
                                                        new BasicStateFormula(true), 4)))),
                        sf33), 4));

        StateFormula p3 = new AWStateFormula(new GenerallyFormula(
                new ImplyStateFormula(sf13, sf23),
                new BasicActionFormula(true), 4));
        assertTrue("Merge three components",
                CACTLMC.modelCheck(CLTS, p3, new NetworkConstraint()));

    }

    @Test
    public void MissingLeader() {
        StateFormula p14 = new NegStateFormula(new ExistStateFormula(
                new FinallyFormula(new ConntivityFormula("D", "A"),
                        new OrActionFormula(new StringActionFormula(
                                        "finish(C,A)"), new OrActionFormula(
                                        new StringActionFormula("finish(B,A)"),
                                        new StringActionFormula("finish(A,A"))),
                        new BasicStateFormula(true), 4)));
        StateFormula p4 = new AUStateFormula(new FinallyFormula(
                new AndTopologyFormula(new ConntivityFormula("A", "D"),
                        new ConntivityFormula("D", "A")),
                new StringActionFormula("finish(D,A)"), p14, 4));
        assertTrue("Missing a leader",
                CACTLMC.modelCheck(CLTS, p4, new NetworkConstraint()));

    }

}
