//import static org.junit.Assert.assertTrue;
import org.junit.Test;



public class MultiHopTester {
	@Test
	public void nOverInvalidPathTester() 
	{
        NetworkConstraint nc1 = new NetworkConstraint();
        nc1.add(new Pair("A","B",true));
        nc1.add(new Pair("A","C",false));
        nc1.add(new Pair("B","C",false));
    	
        //assertTrue("path tester",!PathFormula.OverInvalidPath(new ConntivityFormula("A","B"), nc1,3));
        
	
	}
	@Test
	public void OverInvalidPathTester() 
	{
        NetworkConstraint nc1 = new NetworkConstraint();
        nc1.add(new Pair("A","B",true));
        nc1.add(new Pair("A","C",false));
        nc1.add(new Pair("B","C",false));
    	
        //assertTrue("path tester",PathFormula.OverInvalidPath(new ConntivityFormula("A","C"), nc1,3));
        
	
	}

	
}
