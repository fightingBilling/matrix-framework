package org.matrix.framework.core.database.redis;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.matrix.framework.core.log.LoggerFactory;
import org.slf4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

/**
 * 封装jedis的常用操作
 *
 * @TODO 实现读写分离.使用两个ConcurrentHashMap分别保存写JedisPool和读JedisPool.读操作使用读JedisPool,写操作使用写JedisPool.
 * @author pankai 2015年6月30日
 */
public class RedisAccess {

    private final Logger logger = LoggerFactory.getLogger(RedisAccess.class);

    private JedisPool jedisPool;

    /**
     * 设置String类型的值.
     * 
     * @return 状态码
     */
    public String set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.set(key, value);
        } finally {
            if (null != jedis) {
                this.jedisPool.returnResource(jedis);
                this.logger.debug("set, key={}, value={}", new Object[] { key, value });
            }
        }
    }

    public String set(String key, String value, int seconds) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            String statusCode = jedis.set(key, value);
            jedis.expire(key, seconds);
            return statusCode;
        } finally {
            if (null != jedis) {
                this.jedisPool.returnResource(jedis);
                this.logger.debug("set, key={}, value={}", new Object[] { key, value });
            }
        }
    }

    /**
     * 批量设置String类型的值.
     */
    public void pipeLineSet(Map<String, String> keysvalues) {
        if (null == keysvalues || keysvalues.isEmpty()) {
            return;
        }
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            Pipeline pipeline = jedis.pipelined();
            for (Entry<String, String> entry : keysvalues.entrySet()) {
                pipeline.set(entry.getKey(), entry.getValue());
            }
            pipeline.sync();
        } finally {
            if (null != jedis) {
                this.jedisPool.returnResource(jedis);
                this.logger.debug("pipeLine set, keysvalues={}", new Object[] { keysvalues });
            }
        }
    }

    /**
     * 使用指定的key获取一个string类型的值
     */
    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.get(key);
        } finally {
            if (null != jedis) {
                this.jedisPool.returnResource(jedis);
                this.logger.debug("get, key={}", new Object[] { key });
            }
        }
    }

    /**
     * Get string and set a new expiration time for it.
     */
    public String get(final String key, int seconds) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            jedis.expire(key, seconds);
            return jedis.get(key);
        } finally {
            if (null != jedis) {
                this.jedisPool.returnResource(jedis);
                this.logger.debug("getAndTouch, key={},seconds={}", new Object[] { key, seconds });
            }
        }
    }

    /**
     * 批量获取指定key集合的值
     */
    public List<Object> pipeLinedGet(List<String> keys) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            Pipeline p = jedis.pipelined();
            for (String key : keys) {
                p.get(key);
            }
            List<Object> results = p.syncAndReturnAll();
            p.sync();
            return results;
        } finally {
            if (null != jedis) {
                this.jedisPool.returnResource(jedis);
                this.logger.debug("pipeLine get, keys=", new Object[] { keys });
            }
        }
    }

    /**
     * 检查指定的key是否存在.
     */
    public boolean exist(String key) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.exists(key).booleanValue();
        } finally {
            if (null != jedis) {
                this.jedisPool.returnResource(jedis);
                this.logger.debug("exist, key={}", new Object[] { key });
            }
        }
    }

    /**
     * 删除单个键
     */
    public Long del(String key) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.del(key);
        } finally {
            if (null != jedis) {
                this.jedisPool.returnResource(jedis);
                this.logger.debug("del, key={}", new Object[] { key });
            }
        }
    }

    /**
     * 为指定key存储的值加1.
     * 
     * @return 增加之后的值
     */
    public Long increment(String key) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.incr(key);
        } finally {
            if (null != jedis) {
                this.jedisPool.returnResource(jedis);
                this.logger.debug("increment, key={}", new Object[] { key });
            }
        }
    }

    /**
     * 为指定的key增加指定的数值.
     */
    public Long incrementBy(String key, long value) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.incrBy(key, value);
        } finally {
            if (null != jedis) {
                this.jedisPool.returnResource(jedis);
                this.logger.debug("increment by, key={}, by={}", new Object[] { key, Long.valueOf(value) });
            }
        }
    }

    /**
     * 批量增加指定数值.
     */
    public void pipeLineIncrementBy(Map<String, Long> keysvalues) {
        if (null == keysvalues || keysvalues.isEmpty()) {
            return;
        }
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            Pipeline pipeline = jedis.pipelined();
            for (Entry<String, Long> entry : keysvalues.entrySet()) {
                pipeline.incrBy(entry.getKey(), entry.getValue());
            }
            pipeline.sync();
        } finally {
            if (null != jedis) {
                this.jedisPool.returnResource(jedis);
                this.logger.debug("pipeLine set, keysvalues=", new Object[] { keysvalues });
            }
        }
    }

    /**
     * 设置sorted set(带有排序功能的set).
     */
    public Long zadd(String key, Map<String, Double> scoreMembers) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.zadd(key, scoreMembers);
        } finally {
            if (null != jedis) {
                this.jedisPool.returnResource(jedis);
                this.logger.debug("zadd, key={}, scoreMembers={}", new Object[] { key, scoreMembers });
            }
        }
    }

    /**
     * 
     */
    public Set<String> zrange(String key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.zrange(key, start, end);
        } finally {
            if (null != jedis) {
                this.jedisPool.returnResource(jedis);
                this.logger.debug("zrange, key={}, start={}, end={}", new Object[] { key, Long.valueOf(start), Long.valueOf(end) });
            }
        }
    }

    /**
     * @return 名称为key的set的元素个数
     */
    public Long zcard(String key) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.zcard(key);
        } finally {
            if (null != jedis) {
                this.jedisPool.returnResource(jedis);
                this.logger.debug("zcard, key={}", new Object[] { key });
            }
        }
    }

    /**
     * 删除sorted set中的一个或者多个成员.
     */
    public Long zrem(String key, String... members) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.zrem(key, members);
        } finally {
            if (null != jedis) {
                this.jedisPool.returnResource(jedis);
                this.logger.debug("zrem, key={}, members={}", new Object[] { key, members });
            }
        }
    }

    /**
     * Returns all the keys matching the glob-style pattern as space separated strings
     */
    public Set<String> keys(final String pattern) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.keys(pattern);
        } finally {
            if (null != jedis) {
                this.jedisPool.returnResource(jedis);
                this.logger.debug("keys, pattern={}", new Object[] { pattern });
            }
        }
    }

    /**
     * Test if the connection is connected.
     */
    public boolean isConnected() {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.isConnected();
        } finally {
            if (null != jedis) {
                this.jedisPool.returnResource(jedis);
                this.logger.debug("isConnected.");
            }

        }
    }

    public Jedis getClient() {
        return jedisPool.getResource();
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

}
