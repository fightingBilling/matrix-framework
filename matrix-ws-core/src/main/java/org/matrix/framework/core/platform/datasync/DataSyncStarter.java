package org.matrix.framework.core.platform.datasync;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.platform.datasync.concurrent.ReadThread;
import org.matrix.framework.core.platform.datasync.concurrent.WriteThread;
import org.matrix.framework.core.platform.datasync.domain.BatchRecordModel;
import org.matrix.framework.core.platform.datasync.domain.DataSyncSetting;
import org.matrix.framework.core.platform.datasync.strategy.DataSyncStrategy;
import org.matrix.framework.core.util.ExceptionUtils;
import org.matrix.framework.core.util.StopWatch;

/**
 * matrix数据同步器
 * 
 * @Note 这个类必须以单例的形式存在
 * @Usage 一个数据同步器对应一种同步策略.根据不同业务的同步策略需要不同的数据同步器.
 */
public abstract class DataSyncStarter {

    private final BlockingQueue<BatchRecordModel<?>> blockingQueue = new ArrayBlockingQueue<BatchRecordModel<?>>(200);

    private final Map<String, ReadThread> readThreadMap = new ConcurrentHashMap<String, ReadThread>();

    private DataSyncStrategy dataSyncStrategy;

    private DataSyncStrategyManager dataSyncStrategyManager;

    private DataSyncSetting dataSyncSetting;

    @PostConstruct
    public void initialize() {
        // 同步器初始化之后就立即开始执行写线程
        dataSyncStrategy = dataSyncStrategyManager.getDataSyncStrategy(getStrategyType());
        for (int k = 0; k < dataSyncSetting.getWriteThreadCounter(); k++) {
            final WriteThread writeThread = new WriteThread(dataSyncStrategy, blockingQueue);
            writeThread.start();
        }
    }

    /**
     * 开始往队列加入待写的数据.
     * 
     * @Usage 需要为此方法设置定时任务以便持续周期性同步数据.
     */
    public void startRead(String modelName) {
        final StopWatch stopWatch = new StopWatch();
        try {
            LoggerFactory.JOBLOGGER.getLogger().info("Module [" + modelName + "] begin enter DataSyncStarter.startRead method, current time is " + System.currentTimeMillis());
            dataSyncStrategy.beforeSync();
            // 如果还有读线程没有执行完毕的话立即结束此次任务.
            if (!readThreadMap.isEmpty()) {
                LoggerFactory.JOBLOGGER.getLogger().info("Module [" + modelName + "] read thread map is not empty , stop current task, cost time: " + stopWatch.elapsedTime());
                return;
            }
            final int maxSequenceNo = dataSyncStrategy.getMaxSequenceNo();
            final CountDownLatch latch = new CountDownLatch(dataSyncSetting.getReadThreadCounter());
            final AtomicInteger counter = new AtomicInteger(0);
            for (int k = 0; k < dataSyncSetting.getReadThreadCounter(); k++) {
                String threadName = "[READTHREAD-" + k + "]";
                final ReadThread readThread = new ReadThread(maxSequenceNo, dataSyncSetting.getBatchSize(), this, threadName, dataSyncStrategy, blockingQueue, counter, latch);
                readThread.start();
                readThreadMap.put(threadName, readThread);
            }
            latch.await();
            dataSyncStrategy.afterComplation();
        } catch (InterruptedException e) {
            LoggerFactory.JOBLOGGER.getLogger().error("Module [" + modelName + "] has problem, Exception detail s:" + ExceptionUtils.stackTrace(e));
        } finally {
            LoggerFactory.JOBLOGGER.getLogger().info("Module [" + modelName + "] completed sync data, cost time: " + stopWatch.elapsedTime());
        }
    }

    public void remove(String threadName) {
        readThreadMap.remove(threadName);
    }

    /**
     * 该数据同步器该使用何种策略由此指定.
     */
    public abstract Enum<?> getStrategyType();

    @Inject
    public void setDataSyncStrategyManager(DataSyncStrategyManager dataSyncStrategyManager) {
        this.dataSyncStrategyManager = dataSyncStrategyManager;
    }

    @Inject
    public void setDataSyncSetting(DataSyncSetting dataSyncSetting) {
        this.dataSyncSetting = dataSyncSetting;
    }

}