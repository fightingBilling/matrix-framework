package org.matrix.test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.matrix.springboot.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class RedisTest {

    private static final String LIST_KEY = "list";
    private static final String ZSET_KEY = "zset";
    private static final String HASH_KEY = "hash";

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setTemplate(RedisConnectionFactory factory) {
        this.stringRedisTemplate = new StringRedisTemplate(factory);
    }

    @Test
    public void test0() {
        System.out.println(stringRedisTemplate.boundValueOps("20160708").get());
    }

    // ==========================用list来实现一个队列=========================

    @Test
    public void test1() {
        for (int i = 0; i < 5; i++) {
            Long listSize = stringRedisTemplate.boundListOps(LIST_KEY).leftPush("pankai" + i);
            System.out.println(listSize);
        }
    }

    @Test
    public void test2() {
        String string = stringRedisTemplate.boundListOps(LIST_KEY).rightPop();
        System.out.println(string);
    }

    @Test
    public void test3() {
        String string = stringRedisTemplate.boundListOps(LIST_KEY).rightPop(5L, TimeUnit.SECONDS);
        System.out.println(string);
    }

    @Test
    public void test4() throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " start..");
                String string = stringRedisTemplate.boundListOps(LIST_KEY).rightPop(5L, TimeUnit.SECONDS);
                System.out.println(Thread.currentThread().getName() + string);
            }
        }, "t1");
        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " start..");
                System.out.println(Thread.currentThread().getName() + stringRedisTemplate.boundValueOps("20160708").get());
            }
        }, "t2");
        t1.start();
        Thread.sleep(2000L);
        t2.start();
        Thread.sleep(10000L);
    }

    @Test
    public void test5() {
        try {
            // 处理最后一个元素
            String string = stringRedisTemplate.boundListOps(LIST_KEY).index(-1);
            System.out.println(string);
        } catch (Exception e) {
            // do nothing
        }
        // 处理完毕之后弹出最右的数据
        stringRedisTemplate.boundListOps(LIST_KEY).rightPop();
    }

    @Test
    public void test6() {
        stringRedisTemplate.boundListOps(LIST_KEY).trim(-3, 0);
    }

    @Test
    public void test7() {
        // 从第一位开始,包括第二位
        List<String> list = stringRedisTemplate.boundListOps(LIST_KEY).range(0, 1);
        for (String string : list) {
            System.out.println(string);
        }
    }

    // ==========================用订单按时间排列的例子在说明zset的用法=========================
    // http://www.leoox.com/?p=343&from=timeline&isappinstalled=0
    // 相较于list结构,zset可以获取指定成员的排名情况.例如,某个用户想知道自己前面还有多少订单;排行榜.

    /**
     * 3条订单按时间顺序提交.
     */
    @Test
    public void test8() throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            Thread.sleep(500L);
            stringRedisTemplate.boundZSetOps(ZSET_KEY).add(String.valueOf(i), System.currentTimeMillis());
        }
    }

    /**
     * score按从小到大排列,2号订单前面还有1号,2号,共2条订单.
     */
    @Test
    public void test9() {
        // zrank key member 返回有序集key中成员menber的排名.其中有序集成员按score值递增(从小到大)顺序排列
        System.out.println(stringRedisTemplate.boundZSetOps(ZSET_KEY).rank("2"));
    }

    /**
     * score按从大到小排列,2号订单前面没有订单.
     */
    @Test
    public void test10() {
        // zrevrank key member 返回有序集key中成员menber的排名.其中有序集成员按score值递减(从大到小)顺序排列
        System.out.println(stringRedisTemplate.boundZSetOps(ZSET_KEY).reverseRank("2"));
    }

    /**
     * 当前共有3个排序的订单
     */
    @Test
    public void test11() {
        // zcard 返回key的有序元素个数
        System.out.println(stringRedisTemplate.boundZSetOps(ZSET_KEY).zCard());
    }

    /**
     * Redis Zrange 返回有序集中，指定区间内的成员。 其中成员的位置按分数值递增(从小到大)来排序。 具有相同分数值的成员按字典序(lexicographical order )来排列。 如果你需要成员按
     * 值递减(从大到小)来排列，请使用 ZREVRANGE 命令。
     */
    @Test
    public void test12() {
        // 这里的含义是取出第一条至最后一条订单,即所有.
        Set<String> set = stringRedisTemplate.boundZSetOps(ZSET_KEY).range(0, -1);
        for (String string : set) {
            System.out.println(string);
        }
    }

    /**
     * 取出最早提交的订单进行处理,处理完毕之后将这个订单移出待处理的队列.
     */
    @Test
    public void test13() {
        // 这里只会取出一条数据.
        Set<String> set = stringRedisTemplate.boundZSetOps(ZSET_KEY).range(0, 0);
        for (String string : set) {
            System.out.println("取出的订单号:" + string);
            System.out.println("删除的元素个数:" + stringRedisTemplate.boundZSetOps(ZSET_KEY).remove(string));
        }
    }

    // ======================以操作用户为例说明hash的使用=========================
    // 为什么不直接存储对象? --> 序列化反序列化存在开销.
    // 为什么不多过多组key-value形式保存对象? --> 浪费内存.

    @Test
    public void test14() {
        stringRedisTemplate.boundHashOps(HASH_KEY).put("name", "pankai");
    }

    @Test
    public void test15() {
        System.out.println(stringRedisTemplate.boundHashOps(HASH_KEY).get("name"));
    }

    // =======================利用list保存最近的10条==========================

    @Test
    public void test16() {
        String key = "cw1";
        for (int i = 0; i < 20; i++) {
            stringRedisTemplate.boundListOps(key).leftPush(String.valueOf(i));
        }
        // 0-9下标,10个元素
        stringRedisTemplate.boundListOps(key).trim(0, 9);
        List<String> list = stringRedisTemplate.boundListOps(key).range(0, -1);
        for (String string : list) {
            System.out.println(string);
        }
    }

    // =======================list lrem命令的使用=============================

    @Test
    public void test17() {
        String key = "cw2";
        BoundListOperations<String, String> operations = stringRedisTemplate.boundListOps(key);
        // 先清空list
        operations.trim(1, 0);
        operations.leftPush("1");
        operations.leftPush("2");
        operations.leftPush("3");
        operations.leftPush("2");
        operations.leftPush("1");
        // 删除所有1
        operations.remove(0, "1");
        List<String> list = stringRedisTemplate.boundListOps(key).range(0, -1);
        for (String string : list) {
            System.out.println(string);
        }
    }

    @Test
    public void test18() {
        String key = "cw2";
        BoundListOperations<String, String> operations = stringRedisTemplate.boundListOps(key);
        // 先清空list
        operations.trim(1, 0);
        operations.leftPush("1");
        operations.leftPush("2");
        operations.leftPush("3");
        operations.leftPush("2");
        operations.leftPush("4");
        // 从头往尾移除1个2. 右边是头,左边是尾
        System.out.println("删除了" + operations.remove(1, "2") + "个元素");
        List<String> list = stringRedisTemplate.boundListOps(key).range(0, -1);
        for (String string : list) {
            System.out.println(string);
        }
    }

    @Test
    public void test19() {
        String key = "cw2";
        BoundListOperations<String, String> operations = stringRedisTemplate.boundListOps(key);
        // 先清空list
        operations.trim(1, 0);
        operations.leftPush("1");
        operations.leftPush("2");
        operations.leftPush("3");
        operations.leftPush("2");
        operations.leftPush("4");
        // 从尾往头移除1个2. 右边是头,左边是尾
        System.out.println("删除了" + operations.remove(-1, "2") + "个元素");
        List<String> list = stringRedisTemplate.boundListOps(key).range(0, -1);
        for (String string : list) {
            System.out.println(string);
        }
    }

}
