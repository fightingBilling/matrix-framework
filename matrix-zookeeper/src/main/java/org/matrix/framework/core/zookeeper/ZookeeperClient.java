package org.matrix.framework.core.zookeeper;

import java.util.List;

public interface ZookeeperClient {

    /**
     * 创建节点.
     * 支持递归创建.
     * zookeepr规定了所有的非叶子节点多必须是叶子节点.故当创建一个多级节点且临时节点属性为true的时候,只有数据节点是临时节点,所有父节点都是持久节点.
     * 重复创建已有的节点会报错.
     * 
     * @param path
     * @param ephemeral 是否为临时节点
     */
    void create(String path, boolean ephemeral);

    /**
     * 推荐使用的另外一种创建模式,需要创建parents的时候才创建.
     * 相较于上一种方法,减少了节点已存在异常的发生.
     * @param path
     * @param ephemeral
     */
    void createWithParentsIfNeeded(String path, boolean ephemeral);

    /**
     * 为指定节点设置数据
     * @param path
     * @param data
     */
    void setData(String path, String data);

    /**
     * 返回指定节点上的数据
     * @param path
     * @return
     */
    String getData(String path);

    /**
     * 删除节点.
     * 
     * @param path
     */
    void delete(String path);

    /**
     * 删除节点及其子节点
     * @param path
     */
    void deleteIncludeChildren(String path);

    /**
     * 获取子节点
     * 
     * @param path
     * @return
     */
    List<String> getChildren(String path);

    /**
     * 增加子节点监听器
     * 
     * @param path
     * @param listener
     * @return
     */
    List<String> addChildListener(String path, ChildListener listener);

    /**
     * 移除子节点监听器
     * 
     * @param path
     * @param listener
     */
    void removeChildListener(String path, ChildListener listener);

    /**
     * 增加节点监听器
     * @param path
     * @param listener
     */
    void addNodeListener(String path, NodeListener listener);

    /**
     * 移除节点监听器
     * @param path
     * @param listener
     */
    void removeNodeListener(String path, NodeListener listener);

    /**
     * 增加连接状态监听器
     * 
     * @param listener
     */
    void addStateListener(StateListener listener);

    /**
     * 移除连接状态监听器
     * 
     * @param listener
     */
    void removeStateListener(StateListener listener);

    /**
     * 返回连接状态
     * 
     * @return
     */
    boolean isConnected();

    /**
     * 关闭连接
     */
    void close();

}
