package org.matrix.framework.core.kafka.consumer.concurrent;

import java.nio.charset.Charset;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

import org.matrix.framework.core.kafka.consumer.strategy.MatrixConsumerStrategy;
import org.matrix.framework.core.log.LoggerFactory;
import org.slf4j.Logger;

public class MatrixConsumerThread implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(MatrixConsumerThread.class);

    private final MatrixConsumerStrategy consumerStrategy;
    private final KafkaStream<byte[], byte[]> kafkaStream;

    public MatrixConsumerThread(KafkaStream<byte[], byte[]> kafkaStream, MatrixConsumerStrategy consumerStrategy) {
        this.kafkaStream = kafkaStream;
        this.consumerStrategy = consumerStrategy;
    }

    @Override
    public void run() {
        logger.debug("MatrixConsumerThread running....");
        ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
        // 这个方法会一直阻塞?,直到收到消息或者关闭应用.
        while (it.hasNext()) {
            MessageAndMetadata<byte[], byte[]> messageMetadata = it.next();
            String message = new String(messageMetadata.message(), Charset.forName("UTF-8"));
            long offset = messageMetadata.offset();
            boolean hasException = false;
            try {
                consumerStrategy.process(message, messageMetadata.offset());
            } catch (Exception e) {
                hasException = true;
                consumerStrategy.onException(message, offset, e);
            }
            if (!hasException) {
                consumerStrategy.onSuccess(message, offset);
            }
        }
        logger.debug("MatrixConsumerThread ending....");
    }

}
