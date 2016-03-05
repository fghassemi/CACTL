import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class PathFormulaTester {
	@Test
	public void refineTest3()
	{
		UntilFormula un3 = new UntilFormula(new BasicStateFormula(true),
				  new BasicStateFormula(true), 
				  new BasicActionFormula(true) , 
				  new BasicActionFormula(true),new ConntivityFormula("A","C"), 3);
		assertTrue("Number of topologies",(un3.rtopologies.size()==2));
	}
	@Test
	public void refineTest2()
	{
		UntilFormula un2 = new UntilFormula(new BasicStateFormula(true),
				  new BasicStateFormula(true), 
				  new BasicActionFormula(true) , 
				  new BasicActionFormula(true), new ConntivityFormula("A","B"), 2);
		assertTrue("Number of topologies",(un2.rtopologies.size()==1));
	}
	@Test
	public void refineTest4()
	{
		UntilFormula un4 = new UntilFormula(new BasicStateFormula(true),
				  new BasicStateFormula(true), 
				  new BasicActionFormula(true) , 
				  new BasicActionFormula(true), 
				  new ConntivityFormula("A","C"), 4);
		assertTrue("Number of topologies",(un4.rtopologies.size()==5));
	}


}
