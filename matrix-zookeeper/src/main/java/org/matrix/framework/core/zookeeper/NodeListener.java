package org.matrix.framework.core.zookeeper;

/**
 * 节点监听器,考虑重写equals()和hashCode()或者在订阅与取消订阅时使用同一实例;
 * @author pankai
 * Jan 6, 2016
 */
public interface NodeListener {

    public abstract void nodeChanged();

}
