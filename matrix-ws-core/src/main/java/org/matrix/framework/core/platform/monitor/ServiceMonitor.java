package org.matrix.framework.core.platform.monitor;

public abstract interface ServiceMonitor {

    public abstract ServiceStatus getServiceStatus() throws Exception;

    public abstract String getServiveName();
}
