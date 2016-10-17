package org.matrix.framework.core.lock;

import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

import org.matrix.framework.core.database.memcached.MatrixMemcachedFactory;
import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.platform.exception.MatrixException;
import org.matrix.framework.core.util.ExceptionUtils;
import org.matrix.framework.core.util.TimeLength;
import org.slf4j.Logger;

/**
 * 使用memcached实现的分布式锁(注意末尾已经定义了使用的memcached分组为LOCKGROUP)
 * 
 * @author pankai 2015年6月19日
 */
public class DistributeLock {
    private final Logger logger = LoggerFactory.getLogger(DistributeLock.class);

    private static final TimeLength MAX_WAIT_TIME = TimeLength.seconds(180L);

    private static final TimeLength SLEEP_TIME = TimeLength.milliseconds(200L);
    private MatrixMemcachedFactory matrixMemcachedFactory;

    public boolean lock(LockStack lockStack, String lockActionName) {
        int tryTimes = 0;
        boolean result = true;
        try {
            do {
                /**
                 * Add key-value item to memcached, success only when the key is
                 * not exists in memcached. 所以如果操作成功的话,表明加锁成功.
                 */
                result = getClient().add(generatedLockKey(lockStack), (int) MAX_WAIT_TIME.toSeconds(), lockActionName);
                if (tryTimes > 0) {
                    Thread.sleep(SLEEP_TIME.toMilliseconds());
                }

                tryTimes = (int) (tryTimes + SLEEP_TIME.toMilliseconds());
                if (tryTimes > MAX_WAIT_TIME.toMilliseconds())
                    return false;
            } while (!result);
        } catch (Exception e) {
            LoggerFactory.trace(DistributeLock.class, e);
            return false;
        }
        return true;
    }

    public boolean lock(LockStack lockStack, String lockActionName, int waitTime) {
        int waitTimeMax = waitTime > (int) MAX_WAIT_TIME.toMilliseconds() ? (int) MAX_WAIT_TIME.toMilliseconds()
                : waitTime;
        int tryTimes = 0;
        boolean result = true;
        try {
            do {
                result = getClient().add(generatedLockKey(lockStack), (int) TimeLength.minutes(3L).toSeconds(),
                        lockActionName);
                if (tryTimes > 0) {
                    Thread.sleep(SLEEP_TIME.toMilliseconds());
                }

                tryTimes = (int) (tryTimes + SLEEP_TIME.toMilliseconds());
                if (tryTimes > waitTimeMax)
                    return false;
            } while (!result);
        } catch (Exception e) {
            LoggerFactory.trace(DistributeLock.class, e);
            return false;
        }
        return true;
    }

    public void unLock(LockStack lockStack) {
        try {
            /**
             * 删除掉指定的key,这样标志以此key为锁的状态为已解除.
             */
            getClient().delete(generatedLockKey(lockStack));
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            this.logger.error(ExceptionUtils.stackTrace(e));
            throw new MatrixException("Operition memcached exception,detail in log file.");
        }
    }

    protected String generatedLockKey(LockStack lockStack) {
        return "DISTRIBUTELOCK:" + lockStack.getKey();
    }

    private MemcachedClient getClient() {
        return this.matrixMemcachedFactory.getClient("LOCKGROUP");
    }

    public void setMemcachedFactory(MatrixMemcachedFactory matrixMemcachedFactory) {
        this.matrixMemcachedFactory = matrixMemcachedFactory;
    }
}