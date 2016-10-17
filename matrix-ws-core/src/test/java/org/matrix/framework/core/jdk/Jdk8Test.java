package org.matrix.framework.core.jdk;

import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;

public class Jdk8Test {

    @Test
    public void test0() {
        Stream<String> stream = Stream.<String> empty();
        System.out.println(stream);
    }

    @Test
    public void test1() {
        Stream<BigInteger> stream = Stream.iterate(BigInteger.ZERO, o -> o.add(BigInteger.ONE));
        stream.forEach(System.out::println);
    }

    @Test
    public void test2() {
        List<String> strings = new ArrayList<String>();
        strings.add("aaa");
        strings.add("bbb");
        strings.parallelStream().map(String::toUpperCase).forEach(System.out::println);
    }

    @Test
    public void test3() {
        System.out.println(Instant.now().toString());
    }

    @Test
    public void test4() {
        
    }
}
