package org.matrix.framework.core.zookeeper;


public class ZooKeeperAccessTest {

//    ZooKeeperAccess zooKeeperAccess = new ZooKeeperAccess();
//
//    // server string in office env
//    private static final String CONNECTSTRING = "192.168.2.121:2181";
//    private static final String USERNAME = "pankai";
//    private static final String PASSWORD = "192.168.2.121:2181";
//    private static final String MATRIX_ZOOKEEPER_CONNECTSTRING_KEY = "matrix.zookeeper.connectString";
//    private static final String MATRIX_ZOOKEEPER_USERNAME_KEY = "matrix.zookeeper.username";
//    private static final String MATRIX_ZOOKEEPER_PASSWORD_KEY = "matrix.zookeeper.password";
//
//    @Before
//    public void initalize() {
//        MockEnvironment mockEnvironment = new MockEnvironment();
//        mockEnvironment.setProperty(MATRIX_ZOOKEEPER_CONNECTSTRING_KEY, CONNECTSTRING);
//        mockEnvironment.setProperty(MATRIX_ZOOKEEPER_USERNAME_KEY, USERNAME);
//        mockEnvironment.setProperty(MATRIX_ZOOKEEPER_PASSWORD_KEY, PASSWORD);
//        zooKeeperAccess.setEnv(mockEnvironment);
//        zooKeeperAccess.initalize();
//    }
//
//    @After
//    public void shutdown() {
//        zooKeeperAccess.shutdown();
//    }
//
//    @Test
//    public void test0() {
//        try {
//            Thread.sleep(1000L);
//        } catch (InterruptedException e) {
//        }
//        ZooKeeper zk = zooKeeperAccess.getZooKeeper();
//        System.out.println(zooKeeperAccess.getTree(zk, "/", ";"));
//    }
//
//    @Test
//    public void test1() {
//        try {
//            Thread.sleep(1000L);
//        } catch (InterruptedException e) {
//        }
//        ZooKeeper zk = zooKeeperAccess.getZooKeeper();
//        System.out.println(zk.getSessionId());
//    }
//
//    @Test
//    public void test2() throws KeeperException, InterruptedException {
//        try {
//            Thread.sleep(1000L);
//        } catch (InterruptedException e) {
//        }
//        ZooKeeper zk = zooKeeperAccess.getZooKeeper();
//        Stat stat = new Stat();
//        zk.getACL("/pankai", stat);
//        System.out.println(stat.getCzxid());
//    }
//
//    @Test
//    public void test3() {
//        try {
//            Thread.sleep(1000L);
//        } catch (InterruptedException e) {
//        }
//        ZooKeeper zk = zooKeeperAccess.getZooKeeper();
//        List<ACL> acl = new ArrayList<ACL>();
//        acl.add(new ACL(ZooDefs.Perms.READ, Ids.ANYONE_ID_UNSAFE));
//        zooKeeperAccess.createPath(zk, "/pankai/hello/chachacha", CreateMode.PERSISTENT, acl);
//    }
//
//    @Test
//    public void test4() {
//        try {
//            Thread.sleep(1000L);
//        } catch (InterruptedException e) {
//        }
//        ZooKeeper zk = zooKeeperAccess.getZooKeeper();
//        System.out.println(zooKeeperAccess.exists(zk, "/pankai"));
//    }

}
