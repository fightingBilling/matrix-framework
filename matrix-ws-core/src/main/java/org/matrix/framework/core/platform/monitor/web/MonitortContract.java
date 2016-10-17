package org.matrix.framework.core.platform.monitor.web;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.matrix.framework.core.platform.monitor.Pass;
import org.matrix.framework.core.platform.monitor.PassException;

/**
 * 只有我才能够访问.
 * @author pankai
 * Nov 5, 2015
 */
@Aspect
public class MonitortContract {

    private static final String AUTHOR = "pankai";

    @Before("within(@org.springframework.stereotype.Controller *) && @annotation(pass)")
    public void authorOnly(final JoinPoint joinPoint, Pass pass) {
        Object[] args = joinPoint.getArgs();
        HttpServletRequest request = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof HttpServletRequest) {
                request = (HttpServletRequest) args[i];
                break;
            }
        }
        if (request == null) {
            throw new PassException("方法中缺失HttpServletRequest参数!");
        }
        String name = request.getParameter("name");
        if (!AUTHOR.equals(name)) {
            throw new PassException();
        }
    }

}
