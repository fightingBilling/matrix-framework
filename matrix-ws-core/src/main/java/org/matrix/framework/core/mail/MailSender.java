package org.matrix.framework.core.mail;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.mail.internet.MimeMessage;

import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.platform.scheduler.Job;
import org.matrix.framework.core.platform.scheduler.Scheduler;
import org.matrix.framework.core.util.StringUtils;
import org.matrix.framework.core.util.TimeLength;
import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.CollectionUtils;

/**
 * @author pankai
 * Nov 3, 2015
 */
public class MailSender {

    private final Logger logger = LoggerFactory.getLogger(MailSender.class);

    private Scheduler scheduler;

    private final JavaMailSenderImpl sender = new JavaMailSenderImpl();

    public MailSender() {
        sender.setDefaultEncoding("UTF-8");
        sender.getJavaMailProperties().put("mail.smtp.starttls.enable", "true");
    }

    public void disableSSL() {
        sender.getJavaMailProperties().put("mail.smtp.starttls.enable", "false");
    }

    public void send(Mail mail) {
        try {
            MimeMessage message = createMimeMessage(mail);
            logger.debug("start sending email");
            sender.send(message);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new MailException(e);
        } finally {
            logger.debug("finish sending email");
        }
    }

    public void sendAsync(final Mail mail) {
        logger.debug("start async sending email");
        scheduler.triggerOnce(new Job() {
            @Override
            public void execute() throws Exception {
                send(mail);
                logger.debug("finish async sending email");
            }
        });
    }

    private MimeMessage createMimeMessage(Mail mail) throws Exception {
        MimeMessageHelper message = new MimeMessageHelper(sender.createMimeMessage(), true);
        logger.debug("subject={}", mail.getSubject());
        message.setSubject(mail.getSubject());
        logger.debug("from={}", mail.getFrom());
        message.setFrom(mail.getFrom(), mail.getNickName());
        String[] toAddresses = toAddressArray(mail.getToAddresses());
        logger.debug("to={}", Arrays.toString(toAddresses));
        message.setTo(toAddresses);
        message.setCc(toAddressArray(mail.getCCAddresses()));
        message.setBcc(toAddressArray(mail.getBCCAddresses()));
        message.setText(mail.getBody(), Mail.CONTENT_TYPE_HTML.equals(mail.getContentType()));
        message.setReplyTo(mail.getReplyTo());
        //内联元素
        if (!CollectionUtils.isEmpty(mail.getInlines())) {
            for (Entry<String, Resource> entry : mail.getInlines().entrySet()) {
                message.addInline(entry.getKey(), entry.getValue());
            }
        }
        return message.getMimeMessage();
    }

    private String[] toAddressArray(List<String> addresses) {
        return addresses.toArray(new String[addresses.size()]);
    }

    public void setHost(String host) {
        if (StringUtils.hasText(host))
            sender.setHost(host);
    }

    public void setPort(Integer port) {
        if (port != null)
            sender.setPort(port);
    }

    public void setUsername(String username) {
        if (StringUtils.hasText(username)) {
            sender.setUsername(username);
            sender.getJavaMailProperties().put("mail.smtp.auth", "true");
        }
    }

    public void setPassword(String password) {
        if (StringUtils.hasText(password))
            sender.setPassword(password);
    }

    public void setTimeout(TimeLength timeout) {
        if (timeout != null)
            sender.getJavaMailProperties().put("mail.smtp.timeout", timeout.toMilliseconds());
    }

    public JavaMailSenderImpl getSender() {
        return sender;
    }

    @Inject
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

}