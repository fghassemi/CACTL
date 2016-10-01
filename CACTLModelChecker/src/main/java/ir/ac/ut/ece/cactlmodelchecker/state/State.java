package ir.ac.ut.ece.cactlmodelchecker.state;

import java.io.Serializable;

public class State implements Serializable {

    public String name;

    public State(String name) {
        this.name = name;
    }

}
