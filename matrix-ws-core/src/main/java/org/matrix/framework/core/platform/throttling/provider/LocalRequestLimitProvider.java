package org.matrix.framework.core.platform.throttling.provider;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.matrix.framework.core.util.DateUtils;

/**
 * 单机限流实现.并不能完全精确
 * 
 * @Usage 很明显此类需要保持单例.
 * @author pankai 2015年9月20日
 */
public class LocalRequestLimitProvider implements RequestLimitProvider {

    private final ConcurrentHashMap<String, RequestLimitValue> values = new ConcurrentHashMap<String, LocalRequestLimitProvider.RequestLimitValue>();

    @Override
    public Long incrementAndGet(String key, Long expire) {
        RequestLimitValue value = values.get(key);
        // 初次访问
        if (value == null) {
            values.putIfAbsent(key, new RequestLimitValue(expirationTime(expire), new AtomicLong(0L)));
            return values.get(key).getCount().incrementAndGet();
        } else {
            // 已经过期,重新初始化为0.可能会多次重置为0.
            if (new Date().after(value.getExpiredDate())) {
                value.getCount().set(0);
                value.setExpiredDate(expirationTime(expire));
                return values.get(key).getCount().incrementAndGet();
            } else {
                // 拿到旧值,放入新值
                return value.getCount().incrementAndGet();
            }
        }
    }

    @Override
    public void set(String key, Long expire) {
        // do nothing
    }

    private Date expirationTime(Long expire) {
        // 提防由long转为int的时候,如果long值已经针对int溢出的话,这里获得的过期时间为立即.限流功能会失效.
        return DateUtils.add(new Date(), Calendar.MILLISECOND, expire.intValue());
    }

    private static class RequestLimitValue {
        private Date expiredDate;
        private AtomicLong count;

        public RequestLimitValue(Date expiredDate, AtomicLong count) {
            this.expiredDate = expiredDate;
            this.count = count;
        }

        public Date getExpiredDate() {
            return expiredDate;
        }

        public void setExpiredDate(Date expiredDate) {
            this.expiredDate = expiredDate;
        }

        public AtomicLong getCount() {
            return count;
        }

    }

}
