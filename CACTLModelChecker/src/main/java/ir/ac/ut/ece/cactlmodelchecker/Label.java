package ir.ac.ut.ece.cactlmodelchecker;

public class Label {

    public NetworkConstraint nc;
    public String act;

    public Label(NetworkConstraint _ns, String _act) {
        nc = _ns;
        act = _act;
    }

    @Override
    public String toString() {
        return "(" + nc + "," + act + ")";
    }
}
