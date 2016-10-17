package org.matrix.framework.core.zookeeper.setting;

import org.matrix.framework.core.zookeeper.StateListener;

/**
 * zookeeper的配置类
 * 
 * @author pankai 2015年9月29日
 */
public class ZookeeperSettings {

    /**
     * 连接字符串
     */
    private String connectString;
    /**
     * 验证字符串
     */
    private String authority;
    /**
     * 连接重试次数.这个参数会影响到客户端的状态更改.例如,在重试次数设置很大的情况下,client的状态也许会很快变更为{@link StateListener.SUSPENDED}状态,但是会经历很长的时间才会变更为{@link StateListener.DISCONNECTED}状态.
     * @see {@link StateListener}
     */
    private Integer connectRetryTimes;

    public String getConnectString() {
        return connectString;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public Integer getConnectRetryTimes() {
        return connectRetryTimes;
    }

    public void setConnectRetryTimes(Integer connectRetryTimes) {
        this.connectRetryTimes = connectRetryTimes;
    }

}
