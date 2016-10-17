package org.matrix.framework.core.zookeeper;

import java.util.List;

/**
 * @Note 实现类必须重写equals()和hashCode()方法.
 * @author pankai 2015年9月30日
 */
public interface ChildListener {
    /**
     * @param path 指定path的子节点发生了变化
     * @param children 指定path下还有的子节点列表
     */
    void childChanged(String path, List<String> children);

}
