package org.matrix.framework.core.kafka.consumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import kafka.consumer.Consumer;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import org.matrix.framework.core.kafka.consumer.concurrent.MatrixConsumerThread;
import org.matrix.framework.core.kafka.consumer.setting.MatrixConsumerSetting;
import org.matrix.framework.core.kafka.consumer.strategy.MatrixConsumerStrategy;
import org.matrix.framework.core.kafka.consumer.strategy.MatrixConsumerStrategyManager;
import org.matrix.framework.core.log.LoggerFactory;
import org.slf4j.Logger;

/**
 * matrix kafka message consumer starter.
 *
 * @usage 每种消息类型实例化一个starter.使用org.matrix.framework.core.kafka.MatrixConsumerStrategyManager获取实际的消费策略.
 * @Note 考虑取消自动提交,在业务事务完成之后手动提交.防止由于消息消费业务bug而造成消息丢失.然而consumer高级api似乎不支持此功能.
 * @incompletedsolution props.put( "auto.commit.enable", "false" ); connector.commitOffsets( true );
 * @author pankai 2015年8月17日
 */
public abstract class MatrixConsumerStarter {

    private final Logger logger = LoggerFactory.getLogger(MatrixConsumerStarter.class);

    private MatrixConsumerStrategyManager manager;
    private MatrixConsumerStrategy strategy;
    private MatrixConsumerSetting setting;

    private ConsumerConnector consumer;
    private ExecutorService executor;
    private Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
    private Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap;

    @PostConstruct
    public void initalize() {
        strategy = manager.getConsumerStrategy(getStrategyType());
        Properties configs = new Properties();
        configs.put("zookeeper.connect", setting.getServers());
        configs.put("group.id", setting.getGroup());
        // configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put("zookeeper.session.timeout.ms", "400");
        configs.put("zookeeper.sync.time.ms", "200");
        configs.put("auto.commit.interval.ms", "1000");
        // 如果zookeeper没有offset值或offset值超出范围,那么就给个初始的offset,设置为最小,重新开始读取数据.因此消费消息的业务端得有针对重复消息处理的逻辑.
        configs.put("auto.offset.reset", "smallest");
        kafka.consumer.ConsumerConfig consumerConfig = new kafka.consumer.ConsumerConfig(configs);
        consumer = Consumer.createJavaConsumerConnector(consumerConfig);
        // 这里有一个限制,分区的数量限制了消费端并行的数量.
        executor = Executors.newFixedThreadPool(setting.getPartitionCount());
        topicCountMap.put(setting.getTopic(), setting.getPartitionCount());
        consumerMap = consumer.createMessageStreams(topicCountMap);
        start();
    }

    @PreDestroy
    public void shutdown() {
        if (consumer != null) {
            consumer.shutdown();
        }
        if (executor != null) {
            executor.shutdown();
        }
        try {
            if (!executor.awaitTermination(setting.getAwaitTermination(), TimeUnit.MILLISECONDS)) {
                logger.warn("Timed out waiting for consumer threads to shut down, exiting uncleanly!");
            }
        } catch (InterruptedException e) {
            logger.warn("Interrupted during shutdown, exiting uncleanly!");
        }
        logger.info("MatrixConsumerStarter shutdown already.");
    }

    public void start() {
        logger.info("MatrixConsumerStarter running...");
        List<KafkaStream<byte[], byte[]>> steams = consumerMap.get(setting.getTopic());
        for (KafkaStream<byte[], byte[]> kafkaStream : steams) {
            executor.submit(new MatrixConsumerThread(kafkaStream, strategy));
        }
    }

    /**
     * 该消息消费器该使用何种策略由此指定.
     */
    public abstract Enum<?> getStrategyType();

    @Inject
    public void setManager(MatrixConsumerStrategyManager manager) {
        this.manager = manager;
    }

    @Inject
    public void setSetting(MatrixConsumerSetting setting) {
        this.setting = setting;
    }

}
