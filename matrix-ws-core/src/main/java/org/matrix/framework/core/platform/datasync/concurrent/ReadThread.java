package org.matrix.framework.core.platform.datasync.concurrent;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.platform.datasync.DataSyncStarter;
import org.matrix.framework.core.platform.datasync.domain.BatchRecordModel;
import org.matrix.framework.core.platform.datasync.domain.Record;
import org.matrix.framework.core.platform.datasync.strategy.DataSyncStrategy;
import org.matrix.framework.core.util.ExceptionUtils;

/**
 * 读取数据线程.
 * 
 * @author pankai 2015年7月18日
 */
public class ReadThread extends Thread {

    private boolean stoped;

    private final DataSyncStrategy dataSyncStrategy;

    private final BlockingQueue<BatchRecordModel<?>> blockingQueue;

    private final AtomicInteger counter;

    private final CountDownLatch latch;

    private final String threadName;

    private final DataSyncStarter dataSyncStarter;

    private final int batchSize;

    private final int maxSequenceNo;

    public ReadThread(final int maxSequenceNo, final int batchSize, final DataSyncStarter dataSyncStarter, final String threadName, final DataSyncStrategy dataSyncStrategy,
            final BlockingQueue<BatchRecordModel<?>> blockingQueue, final AtomicInteger counter, final CountDownLatch latch) {
        this.blockingQueue = blockingQueue;
        this.dataSyncStrategy = dataSyncStrategy;
        this.counter = counter;
        this.latch = latch;
        this.threadName = threadName;
        this.dataSyncStarter = dataSyncStarter;
        this.batchSize = batchSize;
        this.maxSequenceNo = maxSequenceNo;
    }

    @Override
    public void run() {
        while (!isStoped()) {
            try {
                final ThreadResult result = hasfinished();
                switch (result) {
                case EXCEOPTION: {
                    Thread.sleep(1000);
                    break;
                }
                default: {
                    break;
                }
                }
            } catch (Exception e) {
                LoggerFactory.JOBLOGGER.getLogger().error("READ THREAD ERROR:");
                LoggerFactory.JOBLOGGER.getLogger().error(ExceptionUtils.stackTrace(e));
            }
        }
    }

    enum ThreadResult {
        COMPLETE, GOON, EXCEOPTION
    }

    private ThreadResult hasfinished() throws InterruptedException {
        try {
            final BatchRecordModel<?> batchRecord = dataSyncStrategy.readDataFromSource(counter, batchSize);
            final int startSequenceNo = batchRecord.getStartSequenceNo();
            final int endSequenceNo = batchRecord.getEndSequenceNo();
            final List<Record> records = batchRecord.getRecords();

            // 获取到的数据的开始序号已经大于等于读取线程的的最大序号,直接将线程标记为已完成.
            if (startSequenceNo >= maxSequenceNo) {
                stopCurrentThrad(true);
                latch.countDown();
                return ThreadResult.COMPLETE;
            }

            // 将待写入的数据填入队列中.
            if (!records.isEmpty()) {
                blockingQueue.put(batchRecord);
            }

            // 获取到的数据的结束序列号已经大于等于读取线程的最大序号,也标记线程为已完成.
            if (endSequenceNo >= maxSequenceNo) {
                stopCurrentThrad(true);
                latch.countDown();
                return ThreadResult.COMPLETE;
            }
        } catch (Exception e) {
            LoggerFactory.JOBLOGGER.getLogger().error("READ THREAD ERROR:");
            LoggerFactory.JOBLOGGER.getLogger().error(ExceptionUtils.stackTrace(e));
            return ThreadResult.EXCEOPTION;
        }
        return ThreadResult.GOON;
    }

    public boolean isStoped() {
        return stoped;
    }

    public void stopCurrentThrad(boolean stoped) {
        this.stoped = stoped;
        dataSyncStarter.remove(threadName);
    }

}