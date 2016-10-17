package org.matrix.framework.core.platform.monitor.web;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.matrix.framework.core.mail.MailSender;
import org.matrix.framework.core.platform.SpringObjectFactory;
import org.matrix.framework.core.platform.monitor.Pass;
import org.matrix.framework.core.platform.monitor.web.view.Health;
import org.matrix.framework.core.platform.web.rest.MatrixRestController;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 邮件发送监控
 * @author pankai
 * Nov 3, 2015
 */
@Controller
public class MailHealthController extends MatrixRestController {

    private SpringObjectFactory springObjectFactory;

    @RequestMapping(value = "/monitor/mail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Pass
    public Health doHealthCheck(HttpServletRequest request) {
        Health.Builder builder = new Health.Builder();
        try {
            MailSender mailSender = springObjectFactory.getBean(MailSender.class);
            JavaMailSenderImpl sender = mailSender.getSender();
            sender.testConnection();
            return builder.up().build();
        } catch (Exception e) {
            return builder.down().build();
        }
    }

    @Inject
    public void setSpringObjectFactory(SpringObjectFactory springObjectFactory) {
        this.springObjectFactory = springObjectFactory;
    }

}
