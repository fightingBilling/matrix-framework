package org.matrix.framework.core.database;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.matrix.framework.core.database.redis.RedisAccess;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisAccessTest {

    private RedisAccess redisAccess;

    @Before
    public void before() {
        redisAccess = new RedisAccess();
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(2);
        config.setMaxWaitMillis(2000L);
        config.setTestOnBorrow(true);
        JedisPool pool = new JedisPool(config, "192.168.2.119", 6379, 2000, "admin");
        redisAccess.setJedisPool(pool);
    }

    @After
    public void after() {
        redisAccess.getJedisPool().destroy();
    }

    @Test
    public void x0() {
        System.out.println(redisAccess.set("key", "value"));
    }

    @Test
    public void x1() {
        System.out.println(redisAccess.exist(null));
    }

    @Test
    public void x2() {
        System.out.println(redisAccess.increment("integer"));
    }

    @Test
    public void x3() {
        System.out.println(redisAccess.set("integer2", "1"));
    }

    @Test
    public void x4() {
        System.out.println(redisAccess.incrementBy("integer", -1));
    }

    @Test
    public void x5() {
        Map<String, Long> map = new HashMap<String, Long>();
        map.put("integer2", -1L);
        map.put("integer", -1L);
        redisAccess.pipeLineIncrementBy(map);
    }

    @Test
    public void x6() {
        System.out.println(redisAccess.get("integer"));
        System.out.println(redisAccess.get("integer2"));
    }

    @Test
    public void x7() {
    }

}
