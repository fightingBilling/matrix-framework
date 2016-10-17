package org.matrix.framework.core.kafka.producer;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.matrix.framework.core.kafka.producer.setting.MatrixProducerSetting;

public class MatrixProducerTest {

    private MatrixProducer matrixProducer = new MatrixProducer();
    private static final String SERVERS_VALUE = "192.168.2.139:9092";
    private static final String ACKS_VALUE = "1";

    @Before
    public void before() {
        MatrixProducerSetting setting = new MatrixProducerSetting(SERVERS_VALUE, ACKS_VALUE);
        matrixProducer.setSetting(setting);
        matrixProducer.initalize();
    }

    @After
    public void after() {
        matrixProducer.shutdown();
    }

    @Test
    public void test0() throws InterruptedException, ExecutionException {
        matrixProducer.send("test", "一条消息", new Callback() {

            public void onCompletion(RecordMetadata metadata, Exception exception) {
                System.out.println("helloworld");
                if (exception != null) {
                    System.out.println(exception.getMessage());
                } else {
                    System.out.println("Callbakc start=============");
                    System.out.println(metadata.topic());
                    System.out.println(metadata.partition());
                    System.out.println(metadata.offset());
                    System.out.println("Callbakc end=============");
                }

            }
        });
        Thread.sleep(10000L);
    }

    @Test
    public void test1() {
        System.out.println(matrixProducer.printMetric());
    }

    @Test
    public void test2() {
        System.out.println(matrixProducer.getLeaderId("test"));
    }

}
