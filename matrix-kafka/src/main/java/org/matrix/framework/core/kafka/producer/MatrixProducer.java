package org.matrix.framework.core.kafka.producer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.StringSerializer;
import org.matrix.framework.core.kafka.producer.setting.MatrixProducerSetting;

/**
 * matrixframework 基于kafka new api的消息发送器
 * 
 * @author pankai 2015年8月18日
 */
public class MatrixProducer {

    private KafkaProducer<Object, Object> kafkaProducer;
    private MatrixProducerSetting setting;

    /**
     * 根据配置初始化kafkaProducer
     */
    @PostConstruct
    public void initalize() {
        // 默认情况下同步发送.
        Map<String, Object> configs = new HashMap<String, Object>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, setting.getServers());
        configs.put(ProducerConfig.ACKS_CONFIG, setting.getAcks());
        if (StringUtils.isNumeric(setting.getMetadataFetchTimeout())) {
            configs.put(ProducerConfig.METADATA_FETCH_TIMEOUT_CONFIG, setting.getMetadataFetchTimeout());
        }
        if (StringUtils.isNumeric(setting.getMetadataMaxAge())) {
            configs.put(ProducerConfig.METADATA_MAX_AGE_CONFIG, setting.getMetadataMaxAge());
        }
        if (StringUtils.isNumeric(setting.getBatchSize())) {
            configs.put(ProducerConfig.BATCH_SIZE_CONFIG, setting.getBatchSize());
        }
        if (StringUtils.isNumeric(setting.getBufferMemory())) {
            configs.put(ProducerConfig.BUFFER_MEMORY_CONFIG, setting.getBufferMemory());
        }
        if (StringUtils.isNumeric(setting.getTimeout())) {
            configs.put(ProducerConfig.TIMEOUT_CONFIG, setting.getTimeout());
        }
        if (StringUtils.isNotBlank(setting.getClientId())) {
            configs.put(ProducerConfig.CLIENT_ID_CONFIG, setting.getClientId());
        }
        if (!Boolean.parseBoolean(setting.getBolckOnBufferFull())) {
            configs.put(ProducerConfig.BLOCK_ON_BUFFER_FULL_CONFIG, "false");
        }
        if (StringUtils.isNumeric(setting.getRetries())) {
            configs.put(ProducerConfig.RETRIES_CONFIG, setting.getRetries());
        }
        if (StringUtils.isNotBlank(setting.getCompressType())) {
            configs.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, setting.getCompressType());
        }
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        kafkaProducer = new KafkaProducer<Object, Object>(configs);
    }

    /**
     * 应用关闭时关闭kafkaProducer
     */
    @PreDestroy
    public void shutdown() {
        kafkaProducer.close();
    }

    /**
     * Please refer to send(String topic, String value, Callback callback).
     * 
     * @param topic
     * @param value
     */
    public void send(String topic, String value) {
        this.send(topic, value, null);
    }

    /**
     * 发送消息.考虑使用异步方式发送数据并且用回调来记录消息发送失败的记录.再通过其他手段重发失败的消息.
     * 
     * @param topic
     *            主题
     * @param value
     *            值
     * @param callback
     *            回调
     */
    public void send(String topic, String value, Callback callback) {
        kafkaProducer.send(new ProducerRecord<Object, Object>(topic, value), callback);
    }

    /**
     * 获得消息发送器的所有度量.
     * 
     * @note 并没有返回所有信息.
     */
    public String printMetric() {
        StringBuilder sb = new StringBuilder();
        Map<MetricName, ? extends Metric> map = kafkaProducer.metrics();
        for (Entry<MetricName, ? extends Metric> entry : map.entrySet()) {
            sb.append("key:").append(entry.getKey().name());
            sb.append("\r\n");
            sb.append("group:").append(entry.getKey().group());
            sb.append("\r\n");
            sb.append("description:").append(entry.getKey().description());
            sb.append("\r\n");
            sb.append("value:").append(entry.getValue().value());
            sb.append("\r\n");
            sb.append("==================");
            sb.append("\r\n");
        }
        return sb.toString();
    }

    /**
     * 获取指定topic的分区信息.
     */
    public String getLeaderId(String topic) {
        StringBuilder sb = new StringBuilder();
        List<PartitionInfo> list = kafkaProducer.partitionsFor(topic);
        for (PartitionInfo partitionInfo : list) {
            sb.append(partitionInfo.toString());
            sb.append("\r\n");
        }
        return sb.toString();
    }

    @Inject
    public void setSetting(MatrixProducerSetting setting) {
        this.setting = setting;
    }

}
