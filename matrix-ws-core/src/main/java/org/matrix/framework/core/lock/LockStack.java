package org.matrix.framework.core.lock;

public class LockStack {
    private final String key;

    public LockStack(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}