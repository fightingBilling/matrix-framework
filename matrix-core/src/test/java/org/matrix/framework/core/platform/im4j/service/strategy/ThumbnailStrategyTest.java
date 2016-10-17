package org.matrix.framework.core.platform.im4j.service.strategy;

import org.junit.Test;

public class ThumbnailStrategyTest {

    @Test
    public void test0() throws Exception {
        SimpleThumbnailStrategy simpleStrategy = new SimpleThumbnailStrategy();
        simpleStrategy.thumbnailImmage("C:\\Users\\GS70\\Desktop\\屏保广告\\屏保广告（最终版）.png", 1, 400, 300, "C:\\Users\\GS70\\Desktop\\屏保广告\\屏保广告（最终版）_java.png");
    }

    @Test
    public void test1() throws Exception {
        Im4jThumbnailStrategy strategy = new Im4jThumbnailStrategy();
        strategy.thumbnailImmage("C:\\Users\\GS70\\Desktop\\屏保广告\\屏保广告（最终版）.png", 1, 400, 300, "C:\\Users\\GS70\\Desktop\\屏保广告\\屏保广告（最终版）_java.png");
    }
}
