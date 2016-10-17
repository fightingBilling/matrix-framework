package org.matrix.framework.core.zookeeper.curator;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.EnsurePath;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.matrix.framework.core.zookeeper.ChildListener;
import org.matrix.framework.core.zookeeper.NodeListener;
import org.matrix.framework.core.zookeeper.StateListener;
import org.matrix.framework.core.zookeeper.ZookeeperClient;
import org.matrix.framework.core.zookeeper.setting.ZookeeperSettings;
import org.springframework.util.CollectionUtils;

public class CuratorZookeeperClientTest {

    private static final String NODE_PATH = "/sudiyi/test_project_name/configuration/order_logs_search_type";

    private TestingServer testingServer;
    private ZookeeperSettings zookeeperSettings;
    private ZookeeperClient zookeeperClient;

    /**
     * 测试开始时启动一个zookeeper测试服务器,启动一个curatorClient
     */
    @Before
    public void initalize() throws Exception {
        //testingServer = new TestingServer(2181, new File("D:\\zkTemp"));
        //testingServer.start();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        zookeeperSettings = new ZookeeperSettings();
        //zookeeperSettings.setConnectString(testingServer.getConnectString());
        //虚拟机上的服务端
        zookeeperSettings.setConnectString("192.168.10.16:2181");
        //连接只重试3次
        zookeeperSettings.setConnectRetryTimes(3);
        zookeeperClient = new CuratorZookeeperClient(zookeeperSettings);
        zookeeperClient.addStateListener(new StateListener() {

            @Override
            public void stateChanged(int connected) {
                //client连接成功
                if (connected == StateListener.CONNECTED) {
                    countDownLatch.countDown();
                    System.out.println("=============================zk clinet connected=================================");
                }
                //client连接挂起.
                if (connected == StateListener.SUSPENDED) {
                    System.out.println("=============================zk clinet suspended=================================");
                }
                //client失去连接
                if (connected == StateListener.DISCONNECTED) {
                    System.out.println("=============================zk clinet disconnected==============================");
                    //失去连接之后关闭这个客户端
                    zookeeperClient.close();
                }
            }
        });
        countDownLatch.await();
    }

    /**
     * 测试完成后关闭zookeeper测试服务器,关闭curatorClient.
     */
    @After
    public void shutdown() throws IOException, InterruptedException {
        //先停止5秒,再关闭client.
        //Thread.sleep(5000L);
        //testingServer.close();
        //Thread.sleep(500000L);
        zookeeperClient.close();

    }

    /**
     * 测试client的连接能力
     */
    @Test
    public void isConnectedTest() {
        Assert.assertTrue("ZookeeperClient isConnected() testing fail!", zookeeperClient.isConnected());
    }

    /**
     * 测试获取子节点0
     */
    @Test
    public void getChildrenTest0() {
        List<String> children = zookeeperClient.getChildren("/");
        children.forEach(o -> {
            System.out.println("Child found => " + o);
        });
    }

    /**
     * 测试获取子节点1
     */
    @Test
    public void getChildrenTest1() {
        List<String> children = zookeeperClient.getChildren("/zookeeper");
        children.forEach(o -> {
            System.out.println("Child found => " + o);
        });
    }

    /**
     * 测试获取子节点2
     */
    @Test
    public void getChildrenTest2() {
        List<String> children = zookeeperClient.getChildren("/zookeeper/quota");
        if (CollectionUtils.isEmpty(children)) {
            System.out.println("============Cannot find any children under /zookeeper/quota");
        } else {
            children.forEach(o -> {
                System.out.println("Child found => " + o);
            });
        }
    }

    /**
     * 测试创建非临时节点
     */
    @Test
    public void createTest0() {
        //重复创建已有的节点会报错.
        zookeeperClient.create(NODE_PATH, false);
    }

    @Test
    public void createTest1() {
        zookeeperClient.create("/test/test", true);
    }

    @Test
    public void createIfNeededTest0() {
        zookeeperClient.createWithParentsIfNeeded(NODE_PATH, false);
    }

    @Test
    public void createIfNeededTest1() {
        zookeeperClient.createWithParentsIfNeeded(NODE_PATH + "/tttttt", false);
    }

    @Test
    public void createMix0() {
        zookeeperClient.createWithParentsIfNeeded("/create_text/mix0", false);
        //zookeeperClient.createIfNeeded("/create_text/mix0/mix1", false);
    }

    /**
     * 测试删除节点0
     */
    @Test
    public void deleteTest0() {
        zookeeperClient.delete("/pankai");
    }

    /**
     * 测试删除节点1
     */
    @Test
    public void deleteTest1() {
        zookeeperClient.delete("/sudiyi");
    }

    @Test
    public void deleteTest2() {
        zookeeperClient.deleteIncludeChildren("/sudiyi");
    }

    /**
     * 测试设置数据0
     */
    @Test
    public void setDataTest0() {
        zookeeperClient.setData(NODE_PATH, "ES");
    }

    /**
     * 测试设置数据1
     */
    @Test
    public void setDataTest1() {
        zookeeperClient.setData(NODE_PATH, "DB");
    }

    /**
     * 测试配置中心设置数据
     */
    @Test
    public void setDataTest2() {
        String path = "/matrix/intranet/configuration/test";
        zookeeperClient.createWithParentsIfNeeded(path, false);
        zookeeperClient.setData(path, "TEST");
    }

    /**
     * 测试获取节点数据0
     */
    @Test
    public void getDataTest0() {
        System.out.println(zookeeperClient.getData(NODE_PATH));
    }

    /**
     * 测试获取节点数据1
     */
    @Test
    public void getDataTest1() {
        System.out.println(zookeeperClient.getData("/zookeeper"));
    }

    /**
     *  测试节点监听0
     *  节点数据变化会触发NodeListener.
     */
    @Test
    public void nodeListenerTest0() throws InterruptedException {
        zookeeperClient.createWithParentsIfNeeded(NODE_PATH, false);
        NodeListener nodeListener = new NodeListener() {

            @Override
            public void nodeChanged() {
                System.out.println("Node changes, reload the data====>" + zookeeperClient.getData(NODE_PATH));

            }
        };
        zookeeperClient.addNodeListener(NODE_PATH, nodeListener);
        zookeeperClient.setData(NODE_PATH, "DB");
        Thread.sleep(1000L);
        zookeeperClient.setData(NODE_PATH, "ES");
        Thread.sleep(1000L);
        zookeeperClient.removeNodeListener(NODE_PATH, nodeListener);
        zookeeperClient.setData(NODE_PATH, "DB");
        Thread.sleep(1000L);
        zookeeperClient.setData(NODE_PATH, "ES");
        Thread.sleep(1000L);
        zookeeperClient.deleteIncludeChildren("/sudiyi");
        Thread.sleep(1000L);
    }

    /**
     * 测试节点监听1
     * 测试结果可以监听到节点的新增,更新和删除.
     */
    @Test
    public void nodeListenerTest1() throws InterruptedException {
        //先删除节点
        zookeeperClient.deleteIncludeChildren("/sudiyi");
        NodeListener nodeListener = new NodeListener() {
            @Override
            public void nodeChanged() {
                System.out.println("Node changes, reload the data====>" + zookeeperClient.getData(NODE_PATH));
            }
        };
        //创建节点监听
        zookeeperClient.addNodeListener(NODE_PATH, nodeListener);
        //创建节点,这里会触发一次NodeListener.NodeListener接受到节点默认数据.
        zookeeperClient.createWithParentsIfNeeded(NODE_PATH, false);
        //如果不休眠而连续对Node进行操作,NodeListener观测到的结果也会存在并发"问题".
        Thread.sleep(1000L);
        //更新数据,又会触发一次NodeListener.
        zookeeperClient.setData(NODE_PATH, "ES");
        Thread.sleep(1000L);
        //更新数据,又会触发一次NodeListener.
        zookeeperClient.setData(NODE_PATH, "DB");
        Thread.sleep(1000L);
        //zookeeperClient.removeNodeListener(NODE_PATH, nodeListener);
        //zookeeperClient.deleteIncludeChildren("/sudiyi");
        zookeeperClient.delete(NODE_PATH);
        Thread.sleep(10000L);
    }

    @Test
    public void addChildListenerTest() {
        List<String> list = zookeeperClient.addChildListener(NODE_PATH, new ChildListener() {

            @Override
            public void childChanged(String path, List<String> children) {
                System.out.println(path + " changed ====> " + zookeeperClient.getData(path));
            }
        });
        for (String string : list) {
            System.out.println(string);
        }
    }

    @Test
    public void unnamed2() throws InterruptedException {
        //清除数据
        zookeeperClient.delete("/sudiyi/test_project_name/configuration/order_logs_search_type");
        zookeeperClient.delete("/sudiyi/test_project_name/configuration");
        zookeeperClient.delete("/sudiyi/test_project_name");
        zookeeperClient.delete("/sudiyi");
        zookeeperClient.create("/sudiyi/test_project_name/configuration/order_logs_search_type", true);
        zookeeperClient.addChildListener("/sudiyi/test_project_name/configuration/order_logs_search_type", new ChildListener() {

            @Override
            public void childChanged(String path, List<String> children) {
                System.out.println(path + " changed ====> " + zookeeperClient.getData(path));
            }
        });
        zookeeperClient.setData("/sudiyi/test_project_name/configuration/order_logs_search_type", "pankaihahahah");
    }

    @Test
    public void test() throws InterruptedException {
        CuratorZookeeperClient curatorZookeeperClient = (CuratorZookeeperClient) zookeeperClient;
        curatorZookeeperClient.create("/pankai/pktczwd", false);
        ChildListener childListener = new ChildListener() {

            @Override
            public void childChanged(String path, List<String> children) {
                System.out.println("Path => " + path);
                children.forEach(o -> {
                    System.out.println("Child => " + o);
                });
            }
        };
        curatorZookeeperClient.addChildListener("/pankai", childListener);
        System.out.println("delete node");
        curatorZookeeperClient.delete("/pankai/pktczwd");
        Thread.sleep(5000L);
        System.out.println("create node1");
        curatorZookeeperClient.createEphemeral("/pankai/pankai001");
        Thread.sleep(5000L);
        System.out.println("create node2");
        curatorZookeeperClient.removeChildListener("/pankai", childListener);
        curatorZookeeperClient.createEphemeral("/pankai/pankai002");
    }

    @Test
    public void empty() {

    }

}
