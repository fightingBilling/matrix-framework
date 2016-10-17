package org.matrix.framework.core.zookeeper;


/**
 * @author pankai 2015年9月30日
 * @deprecated use {@link org.matrix.framework.core.zookeeper.curator.CuratorZookeeperClient}
 */
@Deprecated
public class ZooKeeperAccess {

//    private final Logger logger = LoggerFactory.getLogger(ZooKeeperAccess.class);
//
//    private Environment env;
//
//    private ZooKeeper zk;
//
//    private List<ACL> acl = new ArrayList<ACL>();
//
//    @PostConstruct
//    public void initalize() {
//        this.connect();
//    }
//
//    @PreDestroy
//    public void shutdown() {
//        this.close();
//    }
//
//    /**
//     * 连接zk;最大时间为10秒.
//     */
//    private void connect() {
//        CountDownLatch connectionLatch = new CountDownLatch(1);
//        createZookeeper(connectionLatch);
//        try {
//            connectionLatch.await(10, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            throw new MatrixZooKeeperException("[connect]Exception occurred!", e);
//        }
//    }
//
//    private void createZookeeper(final CountDownLatch connectionLatch) {
//        try {
//            // 这个构造函数指定了一个默认的watcher
//            zk = new ZooKeeper(env.getRequiredProperty("matrix.zookeeper.connectString"), env.getProperty("matrix.zookeeper.sessionTimeout", Integer.class, 2000), new Watcher() {
//                public void process(WatchedEvent event) {
//                    sessionEvent(event, connectionLatch);
//                }
//            });
//        } catch (Exception e) {
//            throw new MatrixZooKeeperException(e);
//        }
//        String authString = env.getRequiredProperty("matrix.zookeeper.username") + ":" + env.getRequiredProperty("matrix.zookeeper.password");
//        zk.addAuthInfo("digest", authString.getBytes());
//        acl.clear();
//        try {
//            // 指定用户名密码的用户拥有所有权限．
//            acl.add(new ACL(ZooDefs.Perms.ALL, new Id("digest", DigestAuthenticationProvider.generateDigest(authString))));
//        } catch (NoSuchAlgorithmException e) {
//            throw new MatrixZooKeeperException("[createZookeeper]Exception occurred!", e);
//        }
//        // 所有人可读
//        acl.add(new ACL(ZooDefs.Perms.READ, Ids.ANYONE_ID_UNSAFE));
//    }
//
//    private void sessionEvent(WatchedEvent event, CountDownLatch connectionLatch) {
//        if (event.getState() == KeeperState.SyncConnected) {
//            logger.info("收到ZK连接成功事件!");
//            connectionLatch.countDown();
//        } else if (event.getState() == KeeperState.Expired) {
//            logger.error("会话超时，等待重新建立ZK连接...");
//            try {
//                reConnection();
//            } catch (Exception e) {
//                logger.error(e.getMessage(), e);
//            }
//        }
//        // Disconnected：Zookeeper会自动处理Disconnected状态重连
//        else if (event.getState() == KeeperState.Disconnected) {
//            logger.info("Zookeeper Disconnected，等待重新建立ZK连接...");
//            try {
//                reConnection();
//            } catch (Exception e) {
//                logger.error(e.getMessage(), e);
//            }
//        } else {
//            logger.info("Zookeeper 会话有其他状态的值，event.getState() =" + event.getState() + ", event  value=" + event.toString());
//        }
//    }
//
//    /**
//     * 重连zookeeper
//     */
//    public synchronized void reConnection() {
//        if (this.zk != null) {
//            try {
//                this.zk.close();
//            } catch (InterruptedException e) {
//                throw new MatrixZooKeeperException("[reConnection]Exception occurred!", e);
//            }
//            this.zk = null;
//            this.connect();
//        }
//    }
//
//    public boolean checkZookeeperState() {
//        return zk != null && zk.getState() == States.CONNECTED;
//    }
//
//    public void close() {
//        logger.info("关闭zookeeper连接!");
//        if (zk == null) {
//            return;
//        }
//        try {
//            this.zk.close();
//        } catch (InterruptedException e) {
//            throw new MatrixZooKeeperException(e);
//        }
//    }
//
//    public ZooKeeper getZooKeeper() {
//        if (this.checkZookeeperState() == false) {
//            reConnection();
//        }
//        return this.zk;
//    }
//
//    // Zookeeper operation below...
//
//    public String getTree(ZooKeeper zk, String path, String lineSplitChar) {
//        try {
//            StringBuilder sb = new StringBuilder();
//            String[] list = getTree(zk, path);
//            Stat stat = new Stat();
//            for (String name : list) {
//                byte[] value = zk.getData(name, false, stat);
//                if (value == null) {
//                    sb.append(name + lineSplitChar);
//                } else {
//                    sb.append(name + "[v." + stat.getVersion() + "]" + "[" + new String(value) + "]" + lineSplitChar);
//                }
//            }
//            return sb.toString();
//        } catch (Exception e) {
//            throw new MatrixZooKeeperException("[getTree]Exception occurred!", e);
//        }
//
//    }
//
//    public String[] getTree(ZooKeeper zk, String path) {
//        try {
//            if (zk.exists(path, false) == null) {
//                return new String[0];
//            }
//            List<String> dealList = new ArrayList<String>();
//            dealList.add(path);
//            int index = 0;
//            while (index < dealList.size()) {
//                String tempPath = dealList.get(index);
//                List<String> children = zk.getChildren(tempPath, false);
//                if (tempPath.equalsIgnoreCase("/") == false) {
//                    tempPath = tempPath + "/";
//                }
//                Collections.sort(children);
//                for (int i = children.size() - 1; i >= 0; i--) {
//                    dealList.add(index + 1, tempPath + children.get(i));
//                }
//                index++;
//            }
//            return (String[]) dealList.toArray(new String[0]);
//        } catch (Exception e) {
//            throw new MatrixZooKeeperException("[getTree]Exception occurred!", e);
//        }
//    }
//
//    public void deleteTree(ZooKeeper zk, String path) throws Exception {
//        String[] list = getTree(zk, path);
//        for (int i = list.length - 1; i >= 0; i--) {
//            zk.delete(list[i], -1);
//        }
//    }
//
//    /**
//     * 递归创建指定的path
//     * 
//     * @note 如果path已经存在,则不会创建;
//     * @param zk
//     *            执行操作的客户端
//     * @param path
//     *            待创建的path
//     * @param createMode
//     *            创建模式
//     * @param acl
//     *            访问策略
//     */
//    public void createPath(ZooKeeper zk, String path, CreateMode createMode, List<ACL> acl) {
//        try {
//            String[] list = path.split("/");
//            String zkPath = "";
//            for (String str : list) {
//                if (StringUtils.isNotBlank(str)) {
//                    zkPath = zkPath + "/" + str;
//                    if (zk.exists(zkPath, false) == null) {
//                        zk.create(zkPath, null, acl, createMode);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            throw new MatrixZooKeeperException("[createPath]Exception occurred!", e);
//        }
//    }
//
//    public Boolean exists(ZooKeeper zk, String path) {
//        try {
//            Stat stat = zk.exists(path, null);
//            return null == stat ? Boolean.FALSE : Boolean.TRUE;
//        } catch (Exception e) {
//            throw new MatrixZooKeeperException("[exists]Exception occurred!", e);
//        }
//    }
//
//    @Inject
//    public void setEnv(Environment env) {
//        this.env = env;
//    }
//    

}
