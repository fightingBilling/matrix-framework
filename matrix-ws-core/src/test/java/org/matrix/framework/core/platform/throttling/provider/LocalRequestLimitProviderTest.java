package org.matrix.framework.core.platform.throttling.provider;

import org.junit.Test;

public class LocalRequestLimitProviderTest {

    @Test
    public void test0() throws InterruptedException {
        LocalRequestLimitProvider provider = new LocalRequestLimitProvider();
        // 指定数量线程并行
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                while (true) {
                    for (int j = 0; j < 10; j++) {
                        Long count = provider.incrementAndGet("key1", 1000 * 2L);
                        System.out.println("key1 in " + Thread.currentThread().getName() + " " + count);
                        if (count == 1) {
                            provider.set("key1", 1000 * 2L);
                        }
                        if (count > 1000) {
                            throw new RuntimeException("Execeed! The count is " + count);
                        }

                    }
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        // 指定数量线程并行
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    // System.out.println("key2:" + provider.incrementAndGet("key2", 1000 * 60 * 60L));
                }
            }).start();
        }
        Thread.sleep(1000 * 100);
    }

    @Test
    public void test1() {

    }

}
