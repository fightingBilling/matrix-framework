package org.matrix.framework.core.kafka.consumer.setting;

/**
 * 消息消费器配置集中管理.
 *
 * @author pankai 2015年8月17日
 */
public class MatrixConsumerSetting {

    // 服务器字符串
    private String servers;
    // 组别
    private String group;
    // 分区数量
    private Integer partitionCount;
    // 订阅的主题
    private String topic;
    // 关闭时最大等待(ms)
    private Long awaitTermination;

    /**
     * @param servers
     *            服务器字符串
     * @param group
     *            组别
     * @param partitionCount
     *            分区数量
     * @param topic
     *            订阅的主题
     * @param awaitTermination
     *            关闭时最大等待(ms)
     */
    public MatrixConsumerSetting(String servers, String group, Integer partitionCount, String topic, Long awaitTermination) {
        this.servers = servers;
        this.group = group;
        this.partitionCount = partitionCount;
        this.topic = topic;
        this.awaitTermination = awaitTermination;
    }

    public String getServers() {
        return servers;
    }

    public String getGroup() {
        return group;
    }

    public Integer getPartitionCount() {
        return partitionCount;
    }

    public String getTopic() {
        return topic;
    }

    public Long getAwaitTermination() {
        return awaitTermination;
    }

}
