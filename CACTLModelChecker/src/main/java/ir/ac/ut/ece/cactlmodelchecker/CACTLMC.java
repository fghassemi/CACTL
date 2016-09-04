package ir.ac.ut.ece.cactlmodelchecker;

import ir.ac.ut.ece.cactlmodelchecker.state.StateFormula;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class CACTLMC {

    public static void main(String[] args) {
    	//System.out.println(java.lang.Runtime.getRuntime().maxMemory());
        //ConstraintLabeledTransitionSystem CLTS = loadCLTS();
        System.out.println("model checking is startd ....");
        /*StateFormula phi = new ExistStateFormula(new FinallyFormula(mu1, new BasicActionFormula(true), 
         new AndStateFormula(new ExistStateFormula(new NextFormula( new StringActionFormula("finish(C,A)"), new BasicStateFormula(true), 3)), 
         new AndStateFormula(new ExistStateFormula(new NextFormula( new StringActionFormula("finish(C,B)"), new BasicStateFormula(true), 3)),
         new ExistStateFormula(new NextFormula( new StringActionFormula("finish(C,C)"), new BasicStateFormula(true), 3)))),3));*/

    	//System.out.println(modelCheck(CLTS, phi, new NetworkConstraint()));
        //the differences between AG EF and AF : in CCS that its accumulate violate mu , but all its states does not satisfy EF will be found by AG EF
        // the state that does not satisfy EF, AF will return their preceeding states as long as mu is valid
        //ConstraintLabeledTransitionSystem result = new ConstraintLabeledTransitionSystem(LabeledTransition.class);
        //Witness("3141", CLTS, result);
        //System.out.println(result);
    }

    public static void Witness(String si,
            ConstraintLabeledTransitionSystem CLTS, ConstraintLabeledTransitionSystem result) {

        result.addVertex(si);

        Set<LabeledTransition> trans_t = CLTS.incomingEdgesOf(si);
        for (Iterator<LabeledTransition> j = trans_t.iterator(); j.hasNext();) {
            LabeledTransition tr = j.next();
            String t = tr.getSrc();

            if (!result.containsVertex(t)) {
                Witness(t, CLTS, result);
                result.addEdge(t, si, tr);
            }
        }

    }

    public static boolean modelCheck(ConstraintLabeledTransitionSystem CLTS,
            StateFormula varphi,
            NetworkConstraint zeta) {
        Set<String> init = CLTS.InitialStates();
        Set<String> allStates = new HashSet(CLTS.vertexSet());
        Set<String> satisfyingStates = varphi.findState(null, CLTS, zeta);
        System.out.println("The formula " + varphi + " is verified by states " + satisfyingStates);
    	//satisfyingStates.retainAll(init);
        //System.out.println("do all states satisfy the formula? "+satisfyingStates.containsAll(allStates));
        allStates.removeAll(satisfyingStates);
        System.out.println("These states do not satisfy the formula:" + allStates);
        boolean retVal = satisfyingStates.containsAll(init);
//        if (!retVal) {
//            for (LinkedList<Item> path : varphi.findCounterExample(null, CLTS, zeta)) {
//                System.out.println("########");
//                for (Item item : path) {
//                    System.out.print(item.state+" ");
//                }
//                System.out.println();
//            }
//        }
        return retVal;
    }

    private static ConstraintLabeledTransitionSystem creatCLTS() {
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
        nc1.add(new Pair("A", "B", true));//nc1.add(new Pair("A","C",true));
        g.addEdge(v1, v2, new LabeledTransition(new Label(nc1, "a")));

//        nc1 = new NetworkConstraint();
//        nc1.add(new Pair("A","B",false));//nc1.add(new Pair("A","C",true));
//        g.addEdge(v2, v1, new LabeledTransition(new Label(nc1, "a")));
        nc1 = new NetworkConstraint();
        nc1.add(new Pair("B", "C", true));//nc1.add(new Pair("B","D",false));
        g.addEdge(v2, v3, new LabeledTransition(new Label(nc1, "a")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("B", "C", false));//nc1.add(new Pair("C","D",false));
        g.addEdge(v3, v2, new LabeledTransition(new Label(nc1, "a")));
//                
//        nc1 = new NetworkConstraint();
//        nc1.add(new Pair("B","C",true));
//        g.addEdge(v2, v3, new LabeledTransition(new Label(nc1, "c")));

        nc1 = new NetworkConstraint();
        nc1.add(new Pair("A", "D", true));//nc1.add(new Pair("C","D",false));
        g.addEdge(v2, v4, new LabeledTransition(new Label(nc1, "b")));

//        nc1 = new NetworkConstraint();
//        nc1.add(new Pair("B","C",true));nc1.add(new Pair("B","D",false));
//        g.addEdge(v4, v1, new LabeledTransition(new Label(nc1, "c")));
        return g;
    }

    public static ConstraintLabeledTransitionSystem loadCLTS(String file) {
        int selfloops = 0;
        ConstraintLabeledTransitionSystem g
                = new ConstraintLabeledTransitionSystem(LabeledTransition.class);
        Set<String> Act = new HashSet<String>();

        BufferedReader br = null;

        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader(file));
            if ((sCurrentLine = br.readLine()) != null) {
                //the first line is about the number of states, transitions, ...
                int first = sCurrentLine.indexOf(',');
                int second = sCurrentLine.indexOf(',', first + 1);
                //int number_Transitions = Integer.parseInt(sCurrentLine.substring(first+1, second));
                int number_states = Integer.parseInt(sCurrentLine.substring(second + 2, sCurrentLine.indexOf(')')));
                for (int i = 0; i < number_states; i++) {
                    g.addVertex(String.valueOf(i));
                }
            }
            while ((sCurrentLine = br.readLine()) != null) {
                //transition info
                int first = sCurrentLine.indexOf(',');
                int second = sCurrentLine.lastIndexOf(',');
                int third = sCurrentLine.lastIndexOf(')');
                String start_state = sCurrentLine.substring(1, first).trim();
                String end_state = sCurrentLine.substring(second + 1, third).trim();
                String network_action = sCurrentLine.substring(first + 2, second - 1).replaceAll(" ", "");
                // the action maybe snd : network_action consists of three parts msg,nc,snder
                String label = null;
                NetworkConstraint nc = new NetworkConstraint();
                LabeledTransition trans = null;
                boolean isConnected;
                if (network_action.startsWith("snd") || network_action.startsWith("sen")
                        || network_action.startsWith("\"msg")) {
					// retrieving the label
                    // TODO how I should find the label 
                    //label = (network_action.substring(0,network_action.indexOf('[')-2));
                    //--label = (network_action.substring(0,network_action.indexOf(",in")));
                    // caution : address of the sender consists of one character
                    //String sender = network_action.substring(network_action.indexOf(']')+3,network_action.indexOf(']')+4);
                    //--String sender = network_action.substring(network_action.length()-2,network_action.length()-1);
                    //--label = label.concat(","+sender+")");
                    //System.out.println(label);
                    label = (network_action.startsWith("\"msg(rec_newpkt(7,2)")) ? "rec_newpkt(7,2)"
                            : (network_action.startsWith("\"msg(rec_deliver(7)")) ? "rec_deliver(7)" : "tau";
					//retrieving the network constraint
                    // TODO how I should find the constraint 
                    //String constraint = network_action.substring(network_action.indexOf('['),network_action.indexOf(']')+1);
                    String constraint = network_action.substring(network_action.indexOf(",in") + 1, network_action.lastIndexOf(','));

					// TODO regarding the conn and disconn
                    //int startlink = constraint.indexOf("pair");
                    int startLink = -1;
                    int connLink = constraint.indexOf("(conn");
                    int disconnLink = constraint.indexOf("(disconn");
                    if (connLink < 0) {
                        isConnected = false;
                        startLink = disconnLink;
                    } else if (disconnLink < 0) {
                        isConnected = true;
                        startLink = connLink;
                    } else {
                        startLink = (connLink < disconnLink) ? connLink : disconnLink;
                        isConnected = (connLink < disconnLink) ? true : false;
                    }
                    while (connLink > 0 || disconnLink > 0) {
                        //get the first pair 
                        int adr1 = constraint.indexOf('(', startLink + 1);
                        int adr2 = constraint.indexOf(')', startLink + 1);
                        int sep = constraint.indexOf(',', startLink + 1);
                        Pair p = new Pair(constraint.substring(adr1 + 1, sep),
                                constraint.substring(sep + 1, adr2),
                                isConnected);
                        nc.add(p);
                        constraint = constraint.substring(adr2 + 1);
                        //startlink = constraint.indexOf("pair");
                        connLink = constraint.indexOf("(conn");
                        disconnLink = constraint.indexOf("(disconn");
                        if (connLink < 0) {
                            isConnected = false;
                            startLink = disconnLink;
                        } else if (disconnLink < 0) {
                            isConnected = true;
                            startLink = connLink;
                        } else {
                            startLink = (connLink < disconnLink) ? connLink : disconnLink;
                            isConnected = (connLink < disconnLink) ? true : false;
                        }
                    }

                } else if (network_action.startsWith("disconnect")) {
                    // it is a sensing action
                    label = "tau";
                    int adr1 = network_action.indexOf('(');
                    int adr2 = network_action.indexOf(')');
                    int sep = network_action.indexOf(',');
                    Pair p = new Pair(network_action.substring(adr1 + 1, sep),
                            network_action.substring(sep + 1, adr2),
                            false);
                    nc.add(p);
                } else if (network_action.startsWith("connect")) {
                    // it is a sensing action
                    label = "tau";
                    int adr1 = network_action.indexOf('(');
                    int adr2 = network_action.indexOf(')');
                    int sep = network_action.indexOf(',');
                    Pair p = new Pair(network_action.substring(adr1 + 1, sep),
                            network_action.substring(sep + 1, adr2),
                            true);
                    nc.add(p);
                } else {
                    label = network_action;
                }
                //trans = new LabeledTransition(new Label(null, "1"));
                trans = new LabeledTransition(new Label(nc, label));
                Act.add(label);
                if (!start_state.equals(end_state) || label.startsWith("finish")) {
                    g.addEdge(start_state, end_state, trans);
                } else {
                    selfloops++;
                }
			   //System.out.println(trans.toString()+" is added to the CLTS");
                //System.out.println(g.edgeSet().size()+" number of Trans, "+selfloops+" number of self-loops");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        g.setAsInitial(String.valueOf(0));
        g.setAct(Act);
        return g;
    }
}
