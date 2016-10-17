package org.matrix.framework.core.platform.datasync.concurrent;

import java.util.concurrent.BlockingQueue;

import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.platform.datasync.domain.BatchRecordModel;
import org.matrix.framework.core.platform.datasync.strategy.DataSyncStrategy;
import org.matrix.framework.core.util.ExceptionUtils;

public class WriteThread extends Thread {

    private boolean stoped;

    private final DataSyncStrategy dataSyncStrategy;

    private final BlockingQueue<BatchRecordModel<?>> blockingQueue;

    public WriteThread(final DataSyncStrategy dataSyncStrategy, final BlockingQueue<BatchRecordModel<?>> blockingQueue) {
        this.blockingQueue = blockingQueue;
        this.dataSyncStrategy = dataSyncStrategy;
    }

    @Override
    public void run() {
        while (!isStoped()) {
            BatchRecordModel<?> batchRecord = null;
            try {
                // 从队列中获取数据
                batchRecord = blockingQueue.take();
                // 写入数据
                dataSyncStrategy.writeDataToTarget(batchRecord);
            } catch (Exception e) {
                LoggerFactory.JOBLOGGER.getLogger().error("WRITE THREAD ERROR:");
                LoggerFactory.JOBLOGGER.getLogger().error(ExceptionUtils.stackTrace(e));
                if (null != batchRecord) {
                    LoggerFactory.JOBLOGGER.getLogger().error(batchRecord.toString());
                }
            }
        }
    }

    public boolean isStoped() {
        return stoped;
    }

    public void stopCurrentThrad(boolean stoped) {
        this.stoped = stoped;
    }

}