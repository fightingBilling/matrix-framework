package concurrent;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * Created by pktczwd on 2016/9/19.
 * CLH锁是一种基于链表的可扩展,高性能,公平的自旋锁,申请线程只在本地变量上自旋,它不断轮询前驱的状态,如果发现前驱释放了锁就结束自旋
 */
public class CLHLock {

    public static class CLHNode {
        /**
         * 默认是在等待锁
         */
        private boolean isLocked = true;
    }

    private volatile CLHNode tail;

    /**
     * AtomicReferenceFieldUpdater是基于反射的实用工具.可以对指定类的指定volatile字段进行原子更新.
     * 例如此例中,是CLHLock中的类型为CLHNode,名为tail的字段.
     */
    private static final AtomicReferenceFieldUpdater<CLHLock, CLHNode> UPDATER = AtomicReferenceFieldUpdater.newUpdater(CLHLock.class, CLHNode.class, "tail");

    public void lock(CLHNode currentThreadCLHNode) {
        /**
         * 把this里的tail值设置成currentThreadCLHNode.
         * getAndSet方法会返回this对象中之前tail的值
         */
        CLHNode preNode = UPDATER.getAndSet(this, currentThreadCLHNode);
        if (preNode != null) {
            /**
             * 已经有线程占用了锁,进入自旋
             */
            while (preNode.isLocked) {
                /**
                 * 参见末尾注释
                 */
            }
        }
    }

    public void unlock(CLHNode currentThreadCLHNode) {
        /**
         * 如果队列里只有当前线程,则释放对当前线程的引用(for GC)
         */
        if (!UPDATER.compareAndSet(this, currentThreadCLHNode, null)) {
            /**
             * 还有后续线程,改变状态,让后续线程结束自旋
             */
            currentThreadCLHNode.isLocked = false;
        }
    }

    /**
     * 测试while(true)会发生什么事情.
     * 结论:CPU时间大量被占用空转.浪费计算资源.
     */
    @Test
    public void test0() {
        while (true) {
        }
    }

    /**
     * 随便加点sleep会好很多.
     */
    @Test
    public void test1() {
        while (true) {
            try {
                Thread.sleep(1L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 所以自旋锁维持自旋状态代价十分高昂,只适用于锁的持有时间非常短的场景.另外,如果是单核机器,自旋锁建议不使用,因为单核处理器同一时间只能有一个线程在运行,而自旋锁本身不挂起,导致那个要获取锁的线程反而不能进入运行状态,浪费了时间.
     */

}
