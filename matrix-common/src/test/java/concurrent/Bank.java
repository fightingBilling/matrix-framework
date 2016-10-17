package concurrent;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by pktczwd on 2016/9/20.
 */
public class Bank {

    private AtomicInteger actionTime = new AtomicInteger(0);
    private Account account = new Account(new AtomicLong(0));
    private CountDownLatch latch = new CountDownLatch(12);
    private Semaphore semaphore = new Semaphore(12);
    private Object monitor = new Object();


    public class DrawTask implements Runnable {
        @Override
        public void run() {
            synchronized (monitor) {
                //竞争到锁之后先检查余额是否大于0.
                if (!account.getAvailable()) {
                    try {
                        //如果余额不大于0,则让出锁,让存钱动作先执行.
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                account.draw();
                System.out.println("取钱动作->" + account + ",当前动作次数" + actionTime.incrementAndGet());
                latch.countDown();
                monitor.notify();
            }
        }
    }


    public class SaveTask implements Runnable {
        @Override
        public void run() {
            synchronized (monitor) {
                //考虑存钱任务连续竞争到锁的情况下,如果已经有钱了,则让出锁,让取钱任务执行.
                if (account.banlance.get() > 0) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                account.save();
                System.out.println("存钱动作->" + account + ",当前动作次数" + actionTime.incrementAndGet());
                latch.countDown();
                //存一次钱,就通知一次取钱线程可以工作了.
                monitor.notify();
            }
        }
    }

    private class Account {

        private AtomicLong banlance;

        public Account(AtomicLong banlance) {
            this.banlance = banlance;
        }

        public Boolean getAvailable() {
            return banlance.get() > 0;
        }

        /**
         * 存一块钱
         */
        public long save() {
            return banlance.incrementAndGet();
        }

        /**
         * 取一块钱
         */
        public long draw() {
            return banlance.decrementAndGet();
        }

        @Override
        public String toString() {
            return new StringBuilder("账户当前余额为：").append(banlance.intValue()).append(",是否有钱：").append(getAvailable()).toString();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Bank bank = new Bank();
        ExecutorService e1 = Executors.newSingleThreadExecutor();
        ExecutorService e2 = Executors.newSingleThreadExecutor();

        for (int i = 0; i < 100; i++) {
            e1.submit(bank.new DrawTask());
        }
        for (int i = 0; i < 100; i++) {
            e2.submit(bank.new SaveTask());
        }
        bank.latch.await();
    }


//    @Test
//    public void test0() throws InterruptedException {
//        ExecutorService e1 = Executors.newSingleThreadExecutor();
//        ExecutorService e2 = Executors.newSingleThreadExecutor();
//        for (int i = 0; i < 100; i++) {
//            e1.submit(new DrawTask());
//        }
//        for (int i = 0; i < 100; i++) {
//            e2.submit(new SaveTask());
//        }
//        latch.await();
//    }
}
