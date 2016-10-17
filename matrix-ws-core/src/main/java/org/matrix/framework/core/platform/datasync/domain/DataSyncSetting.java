package org.matrix.framework.core.platform.datasync.domain;

/**
 * 数据同步设置.
 * 
 * @author pankai 2015年7月18日
 */
public class DataSyncSetting {

    /**
     * 读取线程数量配置
     */
    private int readThreadCounter;

    /**
     * 写入线程数量配置
     */
    private int writeThreadCounter;

    /**
     * 一次执行多少条数据
     */
    private int batchSize;

    public int getReadThreadCounter() {
        return readThreadCounter;
    }

    public void setReadThreadCounter(int readThreadCounter) {
        this.readThreadCounter = readThreadCounter;
    }

    public int getWriteThreadCounter() {
        return writeThreadCounter;
    }

    public void setWriteThreadCounter(int writeThreadCounter) {
        this.writeThreadCounter = writeThreadCounter;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

}
