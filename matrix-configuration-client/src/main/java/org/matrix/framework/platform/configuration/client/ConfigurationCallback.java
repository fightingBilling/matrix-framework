package org.matrix.framework.platform.configuration.client;

/**
 * 配置更改回调
 * @author pankai
 * Jan 12, 2016
 */
public interface ConfigurationCallback {

    /**
     * 当配置发生变化的时候,执行此方法.
     * @param newData
     */
    public void onChange(String newData);

}
