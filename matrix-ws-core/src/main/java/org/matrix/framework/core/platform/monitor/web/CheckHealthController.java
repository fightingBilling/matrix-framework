package org.matrix.framework.core.platform.monitor.web;

import org.matrix.framework.core.platform.monitor.web.view.SiteHealth;
import org.matrix.framework.core.platform.web.rest.MatrixRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CheckHealthController extends MatrixRestController {

    @RequestMapping(value = "/monitor/checkhealth", produces = "application/xml", method = RequestMethod.GET)
    @ResponseBody
    public SiteHealth checkHealth() {
        SiteHealth siteHealth = new SiteHealth();
        siteHealth.setSiteStatus("UP");
        return siteHealth;
    }
}
