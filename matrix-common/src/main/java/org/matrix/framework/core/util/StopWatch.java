package org.matrix.framework.core.util;

public final class StopWatch {
    private long start;

    public StopWatch() {
        reset();
    }

    public void reset() {
        this.start = System.currentTimeMillis();
    }

    public long elapsedTime() {
        long end = System.currentTimeMillis();
        return end - this.start;
    }

}
