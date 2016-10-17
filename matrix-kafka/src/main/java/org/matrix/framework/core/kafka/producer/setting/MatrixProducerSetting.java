package org.matrix.framework.core.kafka.producer.setting;

/**
 * matrix kafka message producer settings.
 *
 * @Warning 提供错误的值将会被直接忽略而不会给出任何提示.
 * @author pankai 2015年8月18日
 */
public class MatrixProducerSetting {

    /**
     * 服务器列表.host1:port1,host2:port2,...
     * 
     * @Required
     */
    private String servers;
    /**
     * 消息持久化控制.
     * 
     * @Required
     * @ExpectValue 0:只要数据写入socket buffer即刻返回.不提供可靠性保证.
     * @ExpectValue 1:leader将消息写入本地日志后即刻返回.[推荐]
     * @ExpectValue all:所有节点同步消息后返回.
     * @ExpectValue 其他数字:指定数量的节点同步.
     */
    private String acks;

    /**
     * 首次获取server元数据的超时时间ms
     *
     * @Defaut 60*1000
     */
    private String metadataFetchTimeout;

    /**
     * server元数据的最大时间ms.超过这个时间,即使没有分区主导改变,也会刷新server元数据.
     * 
     * @Default 60*1000*5
     */
    private String metadataMaxAge;

    /**
     * 批量发送数据的大小bytes.将若干消息批量一次发送给server,以降低请求次数.
     * 
     * @Note 0 means disable
     * @Default 16384bytes=16kb
     */
    private String batchSize;

    /**
     * 缓存待发送给服务器消息的最大内存bytes.
     *
     * @Defaut 32 * 1024 * 1024= 32mb
     */
    private String bufferMemory;

    /**
     * 确认消息超时时间ms.
     * 
     * @Default 30*1000
     */
    private String timeout;

    /**
     * 发送端id.用于标记发送端的一个逻辑名字.
     */
    private String clientId;

    /**
     * 当发送消息缓存已满时的策略.
     *
     * @Default true
     * @ExpectValue true:阻塞
     * @ExpectValue false:抛出BufferExhaustedException
     */
    private String bolckOnBufferFull;

    /**
     * 消息发送失败时的重试次数
     * 
     * @Default 0
     */
    private String retries;

    /**
     * 数据压缩方式.
     * 
     * @Default none
     * @Expect none
     * @Expect gzip
     * @Expect snappy
     * @Expect lz4 速度最快
     */
    private String compressType;

    public MatrixProducerSetting(String servers, String acks) {
        super();
        this.servers = servers;
        this.acks = acks;
    }

    public String getServers() {
        return servers;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }

    public String getAcks() {
        return acks;
    }

    public void setAcks(String acks) {
        this.acks = acks;
    }

    public String getMetadataFetchTimeout() {
        return metadataFetchTimeout;
    }

    public void setMetadataFetchTimeout(String metadataFetchTimeout) {
        this.metadataFetchTimeout = metadataFetchTimeout;
    }

    public String getMetadataMaxAge() {
        return metadataMaxAge;
    }

    public void setMetadataMaxAge(String metadataMaxAge) {
        this.metadataMaxAge = metadataMaxAge;
    }

    public String getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(String batchSize) {
        this.batchSize = batchSize;
    }

    public String getBufferMemory() {
        return bufferMemory;
    }

    public void setBufferMemory(String bufferMemory) {
        this.bufferMemory = bufferMemory;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getBolckOnBufferFull() {
        return bolckOnBufferFull;
    }

    public void setBolckOnBufferFull(String bolckOnBufferFull) {
        this.bolckOnBufferFull = bolckOnBufferFull;
    }

    public String getRetries() {
        return retries;
    }

    public void setRetries(String retries) {
        this.retries = retries;
    }

    public String getCompressType() {
        return compressType;
    }

    public void setCompressType(String compressType) {
        this.compressType = compressType;
    }

}
