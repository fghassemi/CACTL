package ir.ac.ut.ece.cactlmodelchecker;

import org.jgrapht.graph.DefaultEdge;

public class LabeledTransition extends DefaultEdge {

    private static final long serialVersionUID = 1L;
    public Label label;

    public LabeledTransition(Label _label) {
        label = _label;
    }

    public String getSrc() {
        return (String) this.getSource();
    }

    public String getDst() {
        return (String) this.getTarget();
    }

    public String toString() {
        return label.toString();
    }
}
