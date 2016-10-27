package full;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.JdkFutureAdapters;
import com.google.common.util.concurrent.ListenableFuture;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Coordinate;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.matrix.framework.core.collection.converter.JSONConverter;
import org.matrix.framework.core.mail.Mail;
import org.matrix.framework.core.mail.MailSender;
import org.matrix.framework.core.util.DateUtils;
import org.matrix.framework.core.util.DigestUtils;
import org.matrix.framework.core.util.StopWatch;
import org.matrix.framework.core.util.TimeLength;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import sun.misc.Unsafe;

import javax.imageio.ImageIO;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Timestamp;
import java.text.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Created by pankai on 2016/10/26.
 */
public class EmptyTest {

    @Test
    public void empty() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.forEach(o -> {
            System.out.println(o);
        });
        //list.parallelStream().filter(s -> s.equals(new Integer(1))).forEach(o -> System.out.println(o));
        list.stream().filter(o -> o > 1);
        list.stream().filter(o -> o == 1);
        System.out.println(list.size());
    }

    @Test
    public void empty1() {
        String key = "SDY_AD_COUNT_ID_1111";
        System.out.println(key.substring(16, key.length()));
    }

    @Test
    public void test() {
        System.out.println(new Timestamp(1465888830000L));
    }

    @Test
    public void test1() {
        int i = 24;
        System.out.println(i >> 3);
    }

    @Test
    public void test2() {
        System.out.println(System.currentTimeMillis() / 1000);
    }

    @Test
    public void test3() {
        System.out.println(System.getProperty("user.dir"));
    }

    @Test
    public void test4() {
        int src = 0;
        int mask = 1 << 2;
        src = src | mask;
        System.out.println(src);
    }

    @Test
    public void test5() {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        System.out.println(sb.toString());
    }

    @Test
    public void test6() {
        class Student {
            private String name;
            private Integer age;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Integer getAge() {
                return age;
            }

            public void setAge(Integer age) {
                this.age = age;
            }
        }
        Student s = new Student();
        s.setName("潘凯");
        s.setAge(25);
        Map<String, Student> map = new ConcurrentHashMap<String, Student>();
        map.put("key", s);
        Student student = map.get("key");
        student.setName("周星驰");
        student.setAge(66);
        System.out.println(map.get("key").getName());
        System.out.println(map.get("key").getAge());
    }

    @Test
    public void test7() throws InterruptedException {
        class Task {

            public synchronized void xxx() throws InterruptedException {
                System.out.println("xxx");
                Thread.sleep(5000L);
            }

            public synchronized void yyy() throws InterruptedException {
                System.out.println("yyy");
                Thread.sleep(5000L);
            }

        }
        Task task1 = new Task();
        Task task2 = new Task();
        Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    task1.xxx();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    task2.yyy();
                } catch (InterruptedException e) {
                }
            }
        });
        t1.start();
        t2.start();
        Thread.sleep(10000L);
    }

    @Test
    public void test8() {
        System.out.println((1 + 2) + (5 * 6 - 7) + 3 / 4);
    }

    @Test
    public void test9() throws InterruptedException {
        Exchanger<String> exchanger = new Exchanger<String>();
        Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                String A = "银行流水A";
                try {
                    System.out.println("A银行收到B银行交换过来的信息:" + exchanger.exchange(A));
                } catch (InterruptedException e) {
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                String B = "银行流水B";
                try {
                    String A = exchanger.exchange(B);
                    System.out.println("A和B数据是否一致:" + A.equals(B) + ",A录入的是:" + A + ", B录入的是:" + B);
                } catch (InterruptedException e) {
                }
            }
        });
        t1.start();
        t2.start();
        Thread.sleep(2000L);
    }

    @Test
    public void test10() throws InterruptedException {
        Semaphore s = new Semaphore(5);
        AtomicInteger a = new AtomicInteger();
        for (int i = 0; i < 20; i++) {
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        s.acquire();
                        System.out.println(a.getAndIncrement());
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                    } finally {
                        s.release();
                    }

                }
            });
            t.start();
        }
        Thread.sleep(6000L);
    }

    @Test
    public void test11() {
        System.out.println(method("BarackObama"));
    }

    private boolean method(String iniString) {
        int length = iniString.length();
        if (length > 255) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            char temp = iniString.charAt(i);
            for (int j = i + 1; j < length; j++) {
                char target = iniString.charAt(j);
                if (temp == target) {
                    return false;
                }
            }
        }
        return true;
    }

    @Test
    public void test12() {
        int i = 0;
        Integer j = new Integer(0);
        System.out.println(i == j);
        System.out.println(j.equals(i));
    }

    @Test
    public void test13() {
        ListNode node1 = new ListNode(1);
        ListNode node2 = new ListNode(2);
        ListNode node3 = new ListNode(3);
        ListNode node4 = new ListNode(2);
        ListNode node5 = new ListNode(1);
        node1.next = node2;
        node2.next = node3;
        node3.next = node4;
        node4.next = node5;
        System.out.println(isPalindrome(node1));
    }

    public boolean isPalindrome(ListNode pHead) {
        if (pHead == null) {
            return false;
        }
        if (pHead.next == null) {
            return true;
        }
        ListNode original = pHead;
        ListNode first = pHead;
        ListNode last = pHead;
        int i = 0;
        while (null != pHead.next) {
            i++;
            pHead = pHead.next;
        }
        for (int j = 0; j < i; j++) {
            for (int j2 = j; j2 > 0; j2--) {
                // 首元素
                first = first.next;
            }
            for (int j2 = j; j2 < i; j2++) {
                // 后元素
                last = last.next;
            }
            if (first.val != last.val) {
                return false;
            } else {
                // 重置元素
                first = original;
                last = original;
            }
        }
        return true;
    }

    @Test
    public void test14() {
        BigDecimal bd1 = new BigDecimal("100.5");
        BigDecimal bd2 = new BigDecimal("0.01");
        System.out.println(bd1.multiply(bd2).setScale(3, RoundingMode.HALF_UP).doubleValue());
    }

    @Test
    public void test15() {
        int i = 1;
        i += 1;
        System.out.println(i);
    }

    @Test
    public void test16() {
        System.out.println(System.currentTimeMillis());
    }

    @Test
    public void test17() {
        System.out.println(Integer.MAX_VALUE);
    }

    @Test
    public void test18() throws IllegalArgumentException, IllegalAccessException {
        Field unsafeField = Unsafe.class.getDeclaredFields()[0];
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        System.out.println(unsafe);
    }

    @Test
    public void test19() {
        Long longValue = Long.MAX_VALUE;
        System.out.println(longValue.intValue());
    }

    @Test
    public void test20() {
        short v1 = 18;
        Long v2 = new Long("18");
        Long v3 = new Long(18);
        System.out.println(v1 == v2);
        System.out.println(v2 == v3);
    }

    @Test
    public void test21() {
        System.out.println(DigestUtils.md5DigestAsHex("{\"where\":{\"and\":{\"send_company\":\"谢小毛\"}},\"page\":1,\"per\":20}zfyBXTSPLhpemSIT".getBytes()));
    }

    @Test
    public void test22() {
        List<ListNode> list = new ArrayList<ListNode>();
        list.add(new ListNode());
        list.forEach(System.out::println);
    }

    @Test
    public void test23() {
        List<String> strings = new ArrayList<String>();
        strings.add("aaa");
        strings.add("bbb");
        System.out.println(strings.stream().filter(o -> o.length() > 1).count());
        Stream<String> stream = strings.parallelStream();
        System.out.println(stream.getClass());
    }

    //不使用buffer读取文件.
    @Test
    public void test24() throws FileNotFoundException, IOException {
        try (FileInputStream fis = new FileInputStream("D:\\download\\kafka_2.10-0.8.2.1.tgz")) {
            @SuppressWarnings("unused")
            int n;
            int sum = 0;
            StopWatch stopWatch = new StopWatch();
            while ((n = fis.read()) >= 0) {
                //System.out.println(sum);
                //sum += n;
            }
            System.out.println("sum:" + sum + ",elapsedTime:" + stopWatch.elapsedTime());
        }
    }

    //使用buffer
    @Test
    public void test25() throws FileNotFoundException, IOException {
        try (FileInputStream fis = new FileInputStream("D:\\download\\Sybase.12.5.1.rar");//
             BufferedInputStream bis = new BufferedInputStream(fis, 8192);) {
            int n;
            long sum = 0;
            StopWatch stopWatch = new StopWatch();
            while ((n = bis.read()) >= 0) {
                //System.out.println(sum);
                sum += n;
            }
            System.out.println("sum:" + sum + ",elapsedTime:" + stopWatch.elapsedTime());
        }
    }

    //使用MMAP
    @Test
    public void test26() throws FileNotFoundException, IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile("D:\\download\\Sybase.12.5.1.rar", "rw")) {
            long length = randomAccessFile.length();
            MappedByteBuffer mappedByteBuffer = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, length);
            int n;
            long sum = 0;
            StopWatch stopWatch = new StopWatch();
            for (int i = 0; i < length; i++) {
                //mappedByteBuffer.get(i);
                n = 0x000000ff & mappedByteBuffer.get(i);
                sum += n;
            }
            System.out.println("sum:" + sum + ",elapsedTime:" + stopWatch.elapsedTime());
        }
    }

    @Test
    public void test27() {
        String[] strings = ",a,,b,".split(",");
        for (String string : strings) {
            System.out.println(string);
        }
        System.out.println(strings.length);
    }

    @Test
    public void test28() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Future<Object> future = executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return (Object) "heheda!";
            }
        });
        ListenableFuture<Object> listenableFuture = JdkFutureAdapters.listenInPoolThread(future);
        listenableFuture.addListener(() -> {
            try {
                Object obj = listenableFuture.get();
                System.out.println(obj);
            } catch (Exception e) {
            }
        }, executorService);
    }

    @Test
    public void test29() {
        int i = -123;
        BigDecimal bigDecimal = new BigDecimal(i);
        System.out.println(bigDecimal.divide(new BigDecimal(-100), 2, RoundingMode.HALF_DOWN).toString());
    }

    @Test
    public void test30() {
        File file = new File(".");
        System.out.println(file.getFreeSpace() / 1048576 / 1024);
    }

    @Test
    public void test31() {
        Maps.newHashMap();
        HashBasedTable.create();
    }

    @Test
    public void test32() {
        Date today = DateUtils.truncateTime(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_YEAR, 366);
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(today);
        System.out.println((calendar.getTimeInMillis() - todayCalendar.getTimeInMillis()) / (1000 * 60 * 60 * 24));
    }

    @Test
    public void test33() {
        System.out.println(new Integer(1).equals(1));
    }

    @Test
    public void test34() {
        class Student {
            private String name;
            private Integer age;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            @SuppressWarnings("unused")
            public Integer getAge() {
                return age;
            }

            public void setAge(Integer age) {
                this.age = age;
            }
        }
        ConcurrentHashMap<String, Student> map = new ConcurrentHashMap<String, Student>();
        Student stu = new Student();
        stu.setName("潘凯");
        stu.setAge(26);
        map.put("1", stu);
        Student fromMap = map.get("1");
        Student result = new Student();
        BeanUtils.copyProperties(fromMap, result);
        result.setName("周星驰");
        System.out.println(map.get("1").getName());
    }

    @Test
    public void test35() {
        Pattern pattern = Pattern.compile("^+?[1-9][0-9]*$");
        Matcher matcher = pattern.matcher("asdasd");
        System.out.println(matcher.matches());
    }

    @Test
    public void test36() {
        Long l = new Long(0);
        System.out.println(l.equals(new Long(0)));
    }

    @Test
    public void test37() {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        System.out.println(sb.toString());
    }

    @Test
    public void test38() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yyyy");
        System.out.println(simpleDateFormat.format(date));
    }

    @Test
    public void test39() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        System.out.println(calendar.getTime());
    }

    @Test
    public void test40() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 2);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        System.out.println(calendar.getTime());
    }

    @Test
    public void test41() {
        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.get(Calendar.WEEK_OF_MONTH));
    }

    @Test
    public void test42() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = simpleDateFormat.parse("2015-12-01");
        Date endDate = simpleDateFormat.parse("2016-06-27");
        System.out.println(startDate);
        System.out.println(endDate);
        System.out.println();
        long x = endDate.getTime() - startDate.getTime();
        long day = x / (1000 * 60 * 60 * 24);
        System.out.println(day);
    }

    @Test
    public void test43() {
        System.out.println(1450682515000L / (1000 * 60 * 60 * 24 * 365));
    }

    @Test
    public void test44() throws ClientProtocolException, IOException {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        HttpClientBuilder builder = HttpClientBuilder.create().setConnectionManager(connectionManager);
        CloseableHttpClient httpClient = builder.build();
        CloseableHttpResponse response = httpClient.execute(new HttpGet("http://www.baidu.com"));
        System.out.println(response.getStatusLine().getStatusCode());
        connectionManager.closeExpiredConnections();
    }

    @Test
    public void test45() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        System.out.println(calendar.getTime());
    }

    @Test
    public void test46() {
        List<Integer> cityIds = Lists.newArrayList(1, 2, 3, 4);
        StringBuilder sql = new StringBuilder("select distinct thirdparty_billboard_id from sudiyi_business.thirdparty_billboard_area where area_id in (");
        List<Object> params = new ArrayList<Object>();
        for (Integer cityId : cityIds) {
            sql.append("?,");
            params.add(cityId);
        }
        sql.replace(sql.length() - 1, sql.length(), ")");
        System.out.println(sql.toString());
    }

    @Test
    public void test47() {
        File f = new File("TileTest.doc");
        String fileName = f.getName();
        String prefix = fileName.substring(0, fileName.lastIndexOf("."));
        System.out.println(prefix);
    }

    @Test
    public void test48() {
        NumberFormat formatter = new DecimalFormat("#0");
        Float f = new Float(150);
        System.out.println(formatter.format(f));
    }

    @Test
    public void php() {
        File file = new File("C:\\Users\\GS70\\Desktop\\php");
        changeFileName(file);
    }

    private void changeFileName(File file) {
        if (file.exists() && file.isFile()) {
            String filename = file.getAbsolutePath();
            if (filename.indexOf(".") >= 0) {
                if (filename.endsWith(".java")) {
                    String newName = filename.substring(0, filename.lastIndexOf(".")) + ".php";
                    file.renameTo(new File(newName));
                }
            }
        } else {
            for (File f : file.listFiles()) {
                changeFileName(f);
            }
        }
    }

    @Test
    public void test49() {
        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.get(Calendar.DAY_OF_WEEK));
    }

    @Test
    public void test50() {
        System.out.println(".".indexOf(46));
        System.out.println("_".hashCode());
        System.out.println("l".hashCode());
    }

    /**
     * CompletableFuture使用示例.
     */
    @Test
    public void test51() {
        //返回一个新的CompletableFuture,这个task(本例的blockingOperation方法)会在ForkJoinPool.commonPool() 异步的完成.
        CompletableFuture<String> content = CompletableFuture.supplyAsync(() -> blockingOperation());
        //thenApply()为结果提供一个函数
        content.thenApply(o -> {
            System.out.println(Thread.currentThread().getName());
            return o + "!";
        })//
                //thenAccept()与thenApply()类似,不过thenAccept()的结果为Void类型.
                .thenAccept(o -> System.out.println(o));
        System.out.println("here.......");
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
        }
        System.out.println("Over....");

    }

    private String blockingOperation() {
        try {
            System.out.println(Thread.currentThread().getName());
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
        }
        return "Hello World";
    }

    @Test
    public void test52() {
        System.out.println(UUID.randomUUID().toString());
    }

    /**
     * 服务器端图片缩放与剪裁的例子
     */
    @Test
    public void test53() throws Exception {
        String originalImgPath = "C:\\Users\\GS70\\Desktop\\屏保广告\\picture.jpg";
        ImageIO.read(new File(originalImgPath));
        //处理后的宽度
        long imageFileWidth = 100;
        //处理后的高度
        long imageFileHeight = 100;
        //剪裁起始点x坐标
        int x = 500;
        //剪裁起始点y坐标
        int y = 15;
        int width = 576;
        int height = 576;
        String destImgPath = "C:\\Users\\GS70\\Desktop\\屏保广告\\picture_java";
        //png兼顾了显示效果和文件大小.
        Thumbnails.of(originalImgPath).sourceRegion(new Coordinate(x, y), width, height).size((int) imageFileWidth, (int) imageFileHeight).outputFormat("png").toFile(destImgPath);
    }

    @Test
    public void test54() {
        String filename = "helloword.111";
        String prefix = filename.substring(filename.lastIndexOf(".") + 1);
        System.out.println(prefix);
    }

    @Test
    public void test55() {
        File srcDirectory = new File("D:\\code\\alex\\MPAGFS");
        p(srcDirectory);
    }

    private void p(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                p(f);
            }
        } else {
            String filename = file.getName();
            if (filename.endsWith(".java")) {
                System.out.println(file.getAbsolutePath().replaceAll("D:\\\\code\\\\alex\\\\MPAGFS\\\\", ""));
            }
        }
    }

    @Test
    public void test56() {
        File file = new File("D:/MyCareer/studyworkspace/matrix-netty-server/target/");
        System.out.println(file.exists());
    }

    @Test
    public void test57() throws IOException {
        InetAddress address = InetAddress.getLocalHost();
        System.out.println(address.isReachable(5000));
        System.out.println(address.getHostAddress());
    }

    @Test
    public void test58() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH) - 7);
        SimpleDateFormat spdf = new SimpleDateFormat("yyyy/MM/dd");
        String nowDateBeforeSeven = spdf.format(now.getTime());
        StringBuilder sql = new StringBuilder();
        sql.append("select b.GF_bkgRef_x from GF_chrgDtl c ");
        sql.append("left join GF_bkg b on c.GF_bkg_n=b.GF_bkg_n ");
        sql.append("left join GF_sapFnalBill s on b.GF_bkgRef_x=s.GF_bkgRef_x ");
        sql.append("where c.GF_chrgCalSt_c='POSTED' and c.GF_post_dt is not null and CONVERT(VARCHAR(10),c.GF_post_dt,111) <='" + nowDateBeforeSeven + "'");
        sql.append("and s.GF_billTotalAmt_n is null ");
        sql.append("and c.GF_crt_dt > '2016-05-02' ");
        System.out.println(sql.toString());
    }

    @Test
    public void test59() {
        MailSender sender = new MailSender();
        sender.setHost("119.81.66.210");
        sender.setUsername("pankai@wormwood.com.sg");
        sender.setPassword("pk2016");
        //sender.disableSSL();
        sender.setPort(465);
        sender.setTimeout(TimeLength.seconds(5L));
        Mail mail = new Mail();
        mail.addTo("pktczwd@qq.com");
        mail.setSubject("test");
        mail.setFrom("pankai@wormwood.com.sg");
        mail.setHTMLBody("test");
        mail.setReplyTo("pankai@wormwood.com.sg");
        sender.send(mail);
        System.out.println("1111111111");
    }

    @Test
    public void test60() throws InterruptedException {
        for (; ; ) {
            InputStream in = null;
            try {
                Properties properties = System.getProperties();
                in = this.getClass().getResourceAsStream("/url.properties");
                properties.load(in);

                String marineturl = properties.get("marinetlogin").toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (null != in) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Test
    public void test61() {
        int max = 1000;
        int min = 0;
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        System.out.println(s);
    }

    @Test
    public void test62() {
        for (; ; ) {
            try {
                InputStream in = new FileInputStream("asdasdasdasda");
            } catch (Exception e) {
            }
        }
    }

    @Test
    public void test63() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("MM/yyyy");
        Date date = format.parse("10/2016");
        System.out.println(getStartDate(date));
        System.out.println(getEndDate(date));
    }

    private Date getStartDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Integer dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (!dayOfWeek.equals(2)) {
            if (dayOfWeek.equals(1)) {
                calendar.add(Calendar.DAY_OF_YEAR, -7);
            }
            calendar.add(Calendar.DAY_OF_YEAR, 2 - dayOfWeek);
        }
        return calendar.getTime();
    }

    private Date getEndDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Integer dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (!dayOfWeek.equals(2)) {
            calendar.add(Calendar.DAY_OF_YEAR, 2 - dayOfWeek);
        }
        return calendar.getTime();
    }

    @Test
    public void test64() {
        long YEAR_OFFSET = 100_0000_0000L;
        long MONTH_OFFSET = 10000_0000L;
        long DAY_OFFSET = 100_0000L;
        LocalDate now = LocalDate.now();
        long id = now.getYear() * YEAR_OFFSET + now.getMonthValue() * MONTH_OFFSET + now.getDayOfMonth() * DAY_OFFSET;
        System.out.println(id);
    }

    @Test
    public void test65() {
        System.out.println(3 << 3);
    }

    @Test
    public void test66() {
        List<String> list = new ArrayList<String>();
        list.add("a");
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < list.size(); i++) {
                System.out.println(list.get(i));
                list.remove(i);
                i--;
            }
        }

    }

    @Test
    public void test67() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put("1", "1");
        map.put("1", "2");
        map.put("2", "3");
        for (String str : map.values()) {
            System.out.println(str);
        }

        for (java.util.Map.Entry<String, String> entry : map.entries()) {
            System.out.println("key=" + entry.getKey());
            System.out.println("value=" + entry.getValue());
        }
    }

    @Test
    public void test68() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            System.out.println(random.nextInt(5) + 1);
        }
    }

    @Test
    public void test69() throws InterruptedException {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(30);
        executor.setKeepAliveSeconds(120);
        executor.setQueueCapacity(50);
        executor.setThreadFactory(new ThreadFactory() {

            private AtomicInteger count = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("AlertPoolThread-" + count.getAndIncrement());
                return thread;
            }
        });
        executor.afterPropertiesSet();
        for (int i = 0; i < 80; i++) {

            try {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            System.out.println(Thread.currentThread().getName());
                            Random random = new Random();
                            Thread.sleep(1000L * (random.nextInt(5) + 1));
                        } catch (InterruptedException e) {

                        }
                        System.out.println("hello world.");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Thread.sleep(50000L);
        System.out.println("Active count(当前活动的线程数量) = " + executor.getActiveCount());
        System.out.println("Core pool size(线程池核心线程数量) = " + executor.getCorePoolSize());
        System.out.println("Keep alive seconds(线程保持空闲的时间) = " + executor.getKeepAliveSeconds());
        System.out.println("Max pool size(允许的最大线程数) = " + executor.getMaxPoolSize());
        System.out.println("Pool size(线程池中当前线程数) = " + executor.getPoolSize());
        System.out.println("Task count(大概被提交的任务数量) = " + executor.getThreadPoolExecutor().getTaskCount());
        System.out.println("Completed task count(大概完成的任务数量) = " + executor.getThreadPoolExecutor().getCompletedTaskCount());
        System.out.println("当前任务队列大小 = " + executor.getThreadPoolExecutor().getQueue().size());

    }

    @Test
    public void test70() throws ParseException {
        DateFormat format2 = new SimpleDateFormat("E dd/MM/yyyy HH:mm", Locale.US);//GS 2015-11-16 change format
        String date = "";
        System.out.println(format2.parse(null));
    }

    @Test
    public void test71() {
        System.out.println(new Float(0).equals(0));
        System.out.println(new Float(0).equals(0.0f));
    }

    @Test
    public void test72() {
        System.out.println(UUID.randomUUID().toString());
    }

    @Test
    public void test73() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        Float float1 = new Float("12");
        System.out.println(decimalFormat.format(float1));
    }

    private Long getDifferentValue(Date now, Date targetDate) {
        Date fixNow = truncateTime(now);
        Date fixTargetDate = truncateTime(targetDate);
        return (fixNow.getTime() - fixTargetDate.getTime()) / (1000 * 60 * 60 * 24);
    }

    private Date truncateTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    @Test
    public void test74() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date targetDate = dateFormat.parse("2016/07/16");
        System.out.println(getDifferentValue(new Date(), targetDate));
    }

    @Test
    public void test75() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH");
        Date targetDate = dateFormat.parse("2016/07/16 00");
        System.out.println(targetDate);
        System.out.println(targetDate.getHours());
    }

    @Test
    public void test76() {
        float f = 0.5f;
        System.out.println((int) f);
    }

    @Test
    public void test77() {
        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.get(Calendar.YEAR));
        System.out.println(calendar.get(Calendar.MONTH));

        SimpleDateFormat format = new SimpleDateFormat("yyMM");
        System.out.println(format.format(calendar.getTime()));
    }

    @Test
    public void test78() {
        System.out.println(String.format("%04d", 1));
    }

    @Test
    public void test79() {
        //for windows
        String windowsFilePath = "D:\\temp\\27082016\\27082016_fundipro_001.pem";
        System.out.println(windowsFilePath);
        String[] strs = windowsFilePath.split(File.separator + File.separator);
        System.out.println(strs[strs.length - 1]);

        //for linux
        String linuxFilePath = "/temp/27082016/27082016_fundipro_001.pem";
        System.out.println(linuxFilePath);
        String[] strs2 = linuxFilePath.split("/");
        System.out.println(strs2[strs2.length - 1]);
    }

    @Test
    public void test80() {
        String completedFilePath = "D:\\temp\\27082016\\27082016_fundipro_001.pem";
        ;
        String systemName = System.getProperty("os.name");
        String pattern;
        if (systemName.contains("Windows")) {
            pattern = "\\\\";
        } else {
            pattern = "/";
        }
        String[] strings = completedFilePath.split(pattern);
        System.out.println(strings[strings.length - 1]);
    }

    @Test
    public void test81() {
        String str = "1023";
        System.out.println(NumberUtils.isNumber(str));
        System.out.println(NumberUtils.compare(1023, 0));
    }

    @Test
    public void test82() throws Exception {
        CallbackRequest request = new CallbackRequest();
        request.setOrderId("13456132");
        JSONConverter jsonConverter = new JSONConverter();
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("http://121.196.244.197:8095/v1/dada/order/status");
        AbstractHttpEntity entity = new StringEntity(jsonConverter.toString(request), "UTF-8");
        entity.setContentType("application/json; charset=UTF-8");
        entity.setChunked(false);
        post.setEntity(entity);
        HttpResponse response = client.execute(post);
        System.out.println(response.getStatusLine().getStatusCode());
        System.out.println(EntityUtils.toString(response.getEntity(), "UTF-8"));
    }

    private class CallbackRequest {
        @JsonProperty("client_id")
        private String clientId;
        @JsonProperty("order_id")
        private String orderId;
        @JsonProperty("order_status")
        private Integer orderStatus;
        @JsonProperty("cancel_reason")
        private String cancelReason;
        @JsonProperty("dm_id")
        private Integer dmId;
        @JsonProperty("dm_name")
        private String dmName;
        @JsonProperty("dm_mobile")
        private String dmMobile;
        @JsonProperty("update_time")
        private Long updateTime;
        private String signature;

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public Integer getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(Integer orderStatus) {
            this.orderStatus = orderStatus;
        }

        public String getCancelReason() {
            return cancelReason;
        }

        public void setCancelReason(String cancelReason) {
            this.cancelReason = cancelReason;
        }

        public Integer getDmId() {
            return dmId;
        }

        public void setDmId(Integer dmId) {
            this.dmId = dmId;
        }

        public String getDmName() {
            return dmName;
        }

        public void setDmName(String dmName) {
            this.dmName = dmName;
        }

        public String getDmMobile() {
            return dmMobile;
        }

        public void setDmMobile(String dmMobile) {
            this.dmMobile = dmMobile;
        }

        public Long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Long updateTime) {
            this.updateTime = updateTime;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }
    }

    @Test
    @SuppressWarnings("all")
    public void test83() {
        System.out.println("http://112.124.50.175:9995/api/v1/blacklist/18011314686?business_type=7&sign=aa9b49241c4c1915cb3ac5e84201b75d&data=" + URLEncoder.encode("{\"operator\":\"sdy_admin\",\"agent\": \"bms\"}"));
    }


}
