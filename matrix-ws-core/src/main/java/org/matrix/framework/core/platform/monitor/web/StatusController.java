package org.matrix.framework.core.platform.monitor.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.matrix.framework.core.platform.SpringObjectFactory;
import org.matrix.framework.core.platform.monitor.Pass;
import org.matrix.framework.core.platform.monitor.ServiceMonitor;
import org.matrix.framework.core.platform.monitor.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StatusController {

    private SpringObjectFactory objectFactory;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping(value = { "/monitor/status" }, produces = { "application/xml" }, method = { RequestMethod.GET })
    @ResponseBody
    @Pass
    public String status(HttpServletRequest request) {
        Status status = new Status();
        List monitors = this.objectFactory.getBeans(ServiceMonitor.class);
        status.check(monitors);
        return status.toXML();
    }

    @Autowired
    public void setObjectFactory(SpringObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

}
