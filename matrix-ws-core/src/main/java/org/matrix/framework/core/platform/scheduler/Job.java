package org.matrix.framework.core.platform.scheduler;

public abstract interface Job {
    public abstract void execute() throws Exception;
}