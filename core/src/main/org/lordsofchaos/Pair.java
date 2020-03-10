package org.lordsofchaos;

public class Pair {
    private Object left;
    private Object right;
    
    public Pair(Object left, Object right) {
        this.left = left;
        this.right = right;
    }
    
    public Object getKey() {
        return this.left;
    }
    
    public Object getValue() {
        return this.right;
    }
}
