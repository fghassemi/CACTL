/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.state;

import java.io.Serializable;

/**
 *
 * @author ashkan
 */
public class TreeDepthIndicator implements Serializable {

    public TreeDepthIndicator(Integer depth) {
        this.depth = depth;
    }

    public Integer depth;

    public void setDepth(Integer depth) {
        this.depth = depth;
    }
    
    public void incrementDepth() {
        this.depth++;
    }
}
