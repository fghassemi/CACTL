/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.formula.topology;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author ashkan
 */
public class ConntivityFormula implements TopologyFormula {
    public String src;
    public String dst;

    public ConntivityFormula(String _src, String _dst) {
        src = _src;
        dst = _dst;
    }

    @Override
    public boolean satisfy(boolean[][] topo, int dim) {
        // by 5 dim
        int x;
        int y;
        y = (src.equals("A")) ? 0 : (src.equals("B")) ? 1 : (src.equals("C")) ? 2 : (src.equals("D")) ? 3 : 4;
        x = (dst.equals("A")) ? 0 : (dst.equals("B")) ? 1 : (dst.equals("C")) ? 2 : (dst.equals("D")) ? 3 : 4;
        Set<Integer> s = new HashSet<Integer>();
        s.add(new Integer(x * 10 + y));
        return findPath(x, y, topo, dim, s);
    }

    private boolean findPath(int x, int y, boolean[][] topo, int dim, Set<Integer> links) {
        boolean flag = topo[x][y];
        for (int i = 0; i < dim & !flag; i++) {
            Integer l1 = new Integer(x * 10 + i);
            Integer l2 = new Integer(i * 10 + y);
            if ((i != x) && (i != y) && !links.contains(l1) && !links.contains(l2)) {
                Set<Integer> myLinks = new HashSet<Integer>(links);
                myLinks.add(l1);
                myLinks.add(l2);
                flag = findPath(x, i, topo, dim, myLinks) && findPath(i, y, topo, dim, myLinks);
            }
        }
        return flag;
    }

    public String toString() {
        return src + "--->" + dst;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ConntivityFormula) {
            return ((ConntivityFormula) obj).src.equals(this.src) && ((ConntivityFormula) obj).dst.equals(this.dst);
        } else {
            return false;
        }
    }
    
}
