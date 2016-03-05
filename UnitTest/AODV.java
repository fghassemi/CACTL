
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;


public class AODV {
	ConstraintLabeledTransitionSystem CLTS;
	@Before
	public void setUp() throws Exception {
		CLTS = CACTLMC.loadCLTS("..\\AODV.aut");
		System.out.println("Running tests are startd ....");
	}
	//@Test
	public void PacketDelivery(){
    	TopologyFormula mu1 = new AndTopologyFormula(new ConntivityFormula("A","C"), 
                new ConntivityFormula("C","A"));
    	StateFormula p1 = new AUStateFormula(new UntilFormula(
    			new BasicStateFormula(true),
    			new BasicStateFormula(true),
    			new OrActionFormula(new StringActionFormula("rec_newpkt(7,2)"),new StringActionFormula("tau")),
    			new StringActionFormula("rec_deliver(7)"),mu1,4));

	  	assertTrue("all packets are delivered",CACTLMC.modelCheck(CLTS, p1, new NetworkConstraint()));

	}
	@Test
	public void GeneralPacketDelivery(){
    	TopologyFormula mu1 = new AndTopologyFormula(new ConntivityFormula("A","C"), 
                new ConntivityFormula("C","A"));
    	StateFormula p1 = new AUStateFormula(new UntilFormula(
    			new BasicStateFormula(true),
    			new BasicStateFormula(true),
    			new StringActionFormula("tau"),
    			new StringActionFormula("rec_deliver(7)"),mu1,4));

    	StateFormula p2 = new AWStateFormula(new UnlessFormula(
    			new BasicStateFormula(true),
    			p1,
    			new NegActionFormula(new StringActionFormula("rec_newpkt(7,2)")),
    			new StringActionFormula("rec_newpkt(7,2)"),
    			new BasicTopologyFormula(true),4));


	  	assertTrue("all packets are delivered",CACTLMC.modelCheck(CLTS, p2, new NetworkConstraint()));

	}

}
