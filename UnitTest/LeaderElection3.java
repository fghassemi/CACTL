import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;


public class LeaderElection3 {
	static ConstraintLabeledTransitionSystem CLTS;
	@BeforeClass
	public static void setUpClass() throws Exception {
		CLTS = CACTLMC.loadCLTS("..\\Node3.aut");
	}
	@Test
	public void ConvergeAll(){
    	TopologyFormula mu1 = new AndTopologyFormula(new ConntivityFormula("A","C"), 
                new AndTopologyFormula(new ConntivityFormula("C","A"),
                new AndTopologyFormula(new ConntivityFormula("B","C"),
                new ConntivityFormula("C","B"))));
    	StateFormula p1 = new AUStateFormula(new FinallyFormula(mu1, new BasicActionFormula(true), 
    	          new AndStateFormula(new ExistStateFormula(new NextFormula( new StringActionFormula("finish(C,A)"), new BasicStateFormula(true), 3)), 
    	          new AndStateFormula(new ExistStateFormula(new NextFormula( new StringActionFormula("finish(C,B)"), new BasicStateFormula(true), 3)),
    	        		  			  new ExistStateFormula(new NextFormula( new StringActionFormula("finish(C,C)"), new BasicStateFormula(true), 3)))),3));

	  	assertTrue("All nodes converge to C",CACTLMC.modelCheck(CLTS, p1, new NetworkConstraint()));

	}
	@Test
	public void MergeTwoComponent(){
    	TopologyFormula mu2 = new AndTopologyFormula(new ConntivityFormula("A","B"), 
                new AndTopologyFormula(new ConntivityFormula("B","A"),
                new AndTopologyFormula(new ConntivityFormula("B","C"),
                new ConntivityFormula("C","B"))));
    	
    	StateFormula sf12 = new AndStateFormula(new ExistStateFormula(new NextFormula( new StringActionFormula("finish(B,A)"), new BasicStateFormula(true), 3)),
	  			  new ExistStateFormula(new NextFormula( new StringActionFormula("finish(B,B)"), new BasicStateFormula(true), 3)));
    	StateFormula sf22 = new AUStateFormula(new FinallyFormula(mu2, new BasicActionFormula(true), 
    	          new AndStateFormula(new ExistStateFormula(new NextFormula( new StringActionFormula("finish(C,A)"), new BasicStateFormula(true), 3)), 
    	                  new AndStateFormula(new ExistStateFormula(new NextFormula( new StringActionFormula("finish(C,B)"), new BasicStateFormula(true), 3)),
    	                		  			  new ExistStateFormula(new NextFormula( new StringActionFormula("finish(C,C)"), new BasicStateFormula(true), 3)))),3));
    	StateFormula p2 = new AWStateFormula(new GenerallyFormula(
    			new ImplyStateFormula(sf12, sf22)
    			, new BasicActionFormula(true),3));
	  	assertTrue("Merge two components",CACTLMC.modelCheck(CLTS, p2, new NetworkConstraint()));
	}
	
	@Test
	public void MergeThreeComponent()
	{
    	TopologyFormula mu3 = new AndTopologyFormula(new ConntivityFormula("C","A"),
                new ConntivityFormula("C","A"));
    	
    	TopologyFormula mu4 = new AndTopologyFormula(new ConntivityFormula("C","A"), 
                new AndTopologyFormula(new ConntivityFormula("A","C"),
                new AndTopologyFormula(new ConntivityFormula("B","A"),
                new ConntivityFormula("A","B"))));

    	StateFormula sf13 = new ExistStateFormula(new NextFormula( new StringActionFormula("finish(A,A)"), new BasicStateFormula(true), 3));
    	StateFormula sf33 = new AUStateFormula(new FinallyFormula(mu4, new BasicActionFormula(true), 
  	          new AndStateFormula(new ExistStateFormula(new NextFormula( new StringActionFormula("finish(C,A)"), new BasicStateFormula(true), 3)), 
  	                  new AndStateFormula(new ExistStateFormula(new NextFormula( new StringActionFormula("finish(C,B)"), new BasicStateFormula(true), 3)),
  	                		  			  new ExistStateFormula(new NextFormula( new StringActionFormula("finish(C,C)"), new BasicStateFormula(true), 3)))),3));

    	StateFormula sf23 = new AUStateFormula(new FinallyFormula(mu3, new BasicActionFormula(true), 
    			new ImplyStateFormula(new AndStateFormula(new ExistStateFormula(new NextFormula( new StringActionFormula("finish(C,A)"), new BasicStateFormula(true), 3)), 
    	                   			  new ExistStateFormula(new NextFormula( new StringActionFormula("finish(C,C)"), new BasicStateFormula(true), 3))), sf33),3));
    	StateFormula p3 = new AWStateFormula(new GenerallyFormula(new ImplyStateFormula(sf13, sf23), new BasicActionFormula(true),3));
	  	assertTrue("Merge three components",CACTLMC.modelCheck(CLTS, p3, new NetworkConstraint()));
		
	}
	@Test
	public void MissingLeader()
	{
    	StateFormula p14 = new NegStateFormula(new ExistStateFormula(
	               new FinallyFormula(new ConntivityFormula("C","A"), 
	            		new OrActionFormula(new StringActionFormula("finish(B,A)"), new StringActionFormula("finish(A,A")), 
	            		   new BasicStateFormula(true), 3)));    	
    	StateFormula p4 = new AUStateFormula(new FinallyFormula(new AndTopologyFormula(new ConntivityFormula("A","C"), new ConntivityFormula("C","A")), 
    			new StringActionFormula("finish(C,A)"), p14,3));
	  	assertTrue("Missing a leader",CACTLMC.modelCheck(CLTS, p4, new NetworkConstraint()));
		
	}

}
