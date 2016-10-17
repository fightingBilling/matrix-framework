package org.matrix.framework.core.platform.datasync.strategy;

import java.util.concurrent.atomic.AtomicInteger;
import org.matrix.framework.core.platform.datasync.domain.BatchRecordModel;

/**
 * 数据同步策略抽象,matrix的数据同步应当继承此抽象类.
 * 
 * @author pankai 2015年7月18日
 */
public abstract class DataSyncStrategy {

    /**
     * 该线程能够读取的最大序列号
     * 
     * @Usage 与readDataFromSource()配合.
     */
    public abstract int getMaxSequenceNo();

    /**
     * 获取数据
     * 
     * @Usage 与getMaxSequenceNo()配合.
     */
    public abstract BatchRecordModel<?> readDataFromSource(final AtomicInteger value, final int batchSize);

    /**
     * 写入数据
     */
    public abstract void writeDataToTarget(final BatchRecordModel<?> batchRecord);

    /**
     * 同步之前执行的操作.
     */
    public void beforeSync() {
    }

    /**
     * 所有读线程任务执行完毕之后执行的操作.
     * 
     * @Note 写操作这个时候可能还未执行完毕.
     */
    public void afterComplation() {
    }

}
