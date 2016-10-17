package org.matrix.framework.core.jms;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.platform.exception.MatrixException;
import org.slf4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class JmsAccess {

    private final Logger logger = LoggerFactory.getLogger(JmsAccess.class);
    private JmsTemplate jmsTemplate;

    // 封装的发送JMS消息的方法.
    public void send(String destinationName, final Object message) {
        this.jmsTemplate.send(destinationName, new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {
                return message(session, message);
            }
        });
    }

    private Message message(Session session, Object message) throws JMSException {
        if (message instanceof Map) {
            @SuppressWarnings("unchecked")
            Set<Entry<String, Object>> entrySet = ((Map<String, Object>) message).entrySet();
            MapMessage mapMessage = session.createMapMessage();
            for (Map.Entry<String, Object> entry : entrySet) {
                mapMessage.setObject((String) entry.getKey(), entry.getValue());
            }
            return mapMessage;
        }
        if (message instanceof String) {
            return session.createTextMessage((String) message);
        }
        this.logger.debug("JMS message type error,only support string and map message.");
        throw new MatrixException("JMS message type error,only support string and map message.");
    }

    // 暴露基础方法,用此方法发送自定义的消息
    public void send(String destinationName, MessageCreator messageCreator) {
        this.jmsTemplate.send(destinationName, messageCreator);
    }

    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.jmsTemplate = new JmsTemplate();
        this.jmsTemplate.setConnectionFactory(connectionFactory);
    }

}
