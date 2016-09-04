package ir.ac.ut.ece.cactlmodelchecker;

import ir.ac.ut.ece.cactlmodelchecker.action.ActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.action.AndActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.action.BasicActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.action.NegActionFormula;
import ir.ac.ut.ece.cactlmodelchecker.path.PathFormula;
import ir.ac.ut.ece.cactlmodelchecker.state.AndStateFormula;
import ir.ac.ut.ece.cactlmodelchecker.state.BasicStateFormula;
import ir.ac.ut.ece.cactlmodelchecker.state.NegStateFormula;
import ir.ac.ut.ece.cactlmodelchecker.state.StateFormula;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class ReduceTest {

    @Test
    public void reduceNegStateFormula() {
        StateFormula statef = new NegStateFormula(new BasicStateFormula(true));
        statef = statef.reduce();
        assertTrue("testing neg state Formula", statef.getClass().getName().equals(PathFormula.basicStateFormulaClassName) && !((BasicStateFormula) statef).val);
    }

    @Test
    public void reduceAndStateFormula() {
        StateFormula statef = new AndStateFormula(new NegStateFormula(new BasicStateFormula(true)), new NegStateFormula(new BasicStateFormula(true)));
        statef = statef.reduce();
        assertTrue("testing and state Formula", statef.getClass().getName().equals(PathFormula.basicStateFormulaClassName) && !((BasicStateFormula) statef).val);
    }

    @Test
    public void reduceNestedAndStateFormula() {
        StateFormula phi = new AndStateFormula(new NegStateFormula(new BasicStateFormula(true)), new NegStateFormula(new BasicStateFormula(true)));
        StateFormula statef = new AndStateFormula(new NegStateFormula(new BasicStateFormula(true)), phi);

        statef = statef.reduce();
        assertTrue("testing and state Formula", statef.getClass().getName().equals(PathFormula.basicStateFormulaClassName) && !((BasicStateFormula) statef).val);
    }

    @Test
    public void reduceNegActionFormula() {
        Set<String> labels = new HashSet<String>();
        labels.add("a");
        labels.add("c");
        labels.add("d");
        labels.add("e");
        ActionFormula af = new NegActionFormula(new BasicActionFormula(true));
        assertTrue("testing neg action Formula", af.toSet(labels).isEmpty());
    }

    @Test
    public void reduceAndActionFormula() {
        Set<String> labels = new HashSet<String>();
        labels.add("a");
        labels.add("c");
        labels.add("d");
        labels.add("e");
        ActionFormula af = new AndActionFormula(new NegActionFormula(new BasicActionFormula(true)), new NegActionFormula(new BasicActionFormula(true)));
        assertTrue("testing and action Formula", af.toSet(labels).isEmpty());
    }

}
