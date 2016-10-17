package org.matrix.framework.core.platform.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.matrix.framework.core.util.StopWatch;
import org.matrix.framework.core.xml.XMLBuilder;

public class Status {

    Map<String, ServiceDetail> serviceDetails = new HashMap<String, ServiceDetail>();

    public void check(List<ServiceMonitor> monitors) {
        for (ServiceMonitor serviceMonitor : monitors) {
            check(serviceMonitor);
        }
    }

    private void check(ServiceMonitor monitor) {
        StopWatch watch = new StopWatch();
        ServiceDetail detail = new ServiceDetail();
        try {
            detail.setStatus(monitor.getServiceStatus());
        } catch (Exception e) {
            detail.setErrorMessage(e.getClass().getName() + "" + e.getMessage());
            detail.setStatus(ServiceStatus.DOWN);
        } finally {
            detail.setElapsedTime(watch.elapsedTime());
        }
        this.serviceDetails.put(monitor.getServiveName(), detail);
    }

    @SuppressWarnings("rawtypes")
    public String toXML() {
        XMLBuilder builder = XMLBuilder.indentedXMLBuilder();
        builder.startElement("status");
        builder.textElement("server", ServiceStatus.UP.name());
        builder.startElement("services");
        for (Map.Entry entry : this.serviceDetails.entrySet()) {
            builder.startElement("service");
            builder.attribute("name", (String) entry.getKey());
            ServiceDetail detail = (ServiceDetail) entry.getValue();
            builder.textElement("status", detail.getStatus().name());
            builder.textElement("elapsedTime", String.valueOf(detail.getElapsedTime()));
            builder.textElement("errorMessage", detail.getErrorMessage());
            builder.endElement();
        }
        builder.endElement();
        builder.endElement();
        return builder.toXML();
    }

}
