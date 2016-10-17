package org.matrix.framework.core.platform.throttling;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.platform.throttling.provider.RequestLimitProvider;
import org.slf4j.Logger;

/**
 * 根据request.getRemoteHost()的限流器
 *
 * @usage 很明显此类需要保持单例
 * @author pankai 2015年9月20日
 */
@Aspect
public class RequestLimitContract {

    private final Logger logger = LoggerFactory.getLogger(RequestLimitContract.class);
    private RequestLimitProvider requestLimitProvider;

    @Before("within(@org.springframework.stereotype.Controller *) && @annotation(limit)")
    public void requestLimit(final JoinPoint joinPoint, RequestLimit limit) throws RequestLimitException {
        try {
            if (limit.time() > Integer.MAX_VALUE) {
                throw new RequestLimitException("时限不能超过Integer的最大值!");
            }
            Object[] args = joinPoint.getArgs();
            HttpServletRequest request = null;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof HttpServletRequest) {
                    request = (HttpServletRequest) args[i];
                    break;
                }
            }
            if (request == null) {
                throw new RequestLimitException("方法中缺失HttpServletRequest参数!");
            }
            String ip = request.getRemoteAddr();
            String url = request.getRequestURL().toString();
            String key = "req_limit_".concat(url).concat(ip);
            long count = requestLimitProvider.incrementAndGet(key, limit.time());
            if (count == 1) {
                requestLimitProvider.set(key, limit.time());
            }
            if (count > limit.count()) {
                logger.info("用户IP[" + ip + "]访问地址[" + url + "]超过了限定的次数[" + limit.count() + "]");
                throw new RequestLimitException("用户IP[" + ip + "]访问地址[" + url + "]超过了限定的次数[" + limit.count() + "]");
            }
        } catch (RequestLimitException e) {
            throw e;
        } catch (Exception e) {
            logger.error("发生异常: ", e);
        }
    }

    @Inject
    public void setRequestLimitProvider(RequestLimitProvider requestLimitProvider) {
        this.requestLimitProvider = requestLimitProvider;
    }

}
