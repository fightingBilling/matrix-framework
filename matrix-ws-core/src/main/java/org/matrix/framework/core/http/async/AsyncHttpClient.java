package org.matrix.framework.core.http.async;

import java.io.IOException;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.matrix.framework.core.annotation.ThreadSafe;
import org.matrix.framework.core.http.HTTPRequest;
import org.matrix.framework.core.util.TimeLength;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异步的HttpClient
 * Once configured, this class is thread-safe.
 * @author pankai 2015年9月16日
 */
@ThreadSafe
public class AsyncHttpClient {

    private final Logger logger = LoggerFactory.getLogger(AsyncHttpClient.class);

    private static final TimeLength DEFAULT_TIME_OUT = TimeLength.seconds(10L);

    private CloseableHttpAsyncClient client;

    private PoolingNHttpClientConnectionManager poolingNHttpClientConnectionManager;

    private HttpAsyncClientBuilder httpAsyncClientBuilder;
    private RequestConfig.Builder requestConfigBuilder;

    @PostConstruct
    public void initalize() {
        httpAsyncClientBuilder = HttpAsyncClientBuilder.create();
        requestConfigBuilder = RequestConfig.custom();
        requestConfigBuilder.setSocketTimeout((int) DEFAULT_TIME_OUT.toMilliseconds()).setConnectTimeout((int) DEFAULT_TIME_OUT.toMilliseconds()).setConnectionRequestTimeout(
                (int) DEFAULT_TIME_OUT.toMilliseconds());
    }

    /**
     * 设置socket超时[which is the timeout for waiting for data or, put differently,a maximum period inactivity between two consecutive data packets](毫秒)
     * 
     * @Defual 0 系统默认
     * @other -1 用不超时
     */
    public void setSocketTimeOut(int socketTimeout) {
        requestConfigBuilder.setSocketTimeout(socketTimeout);
    }

    /**
     * 设置建立连接超时(毫秒)
     * 
     * @Defual 0 系统默认
     * @other -1 用不超时
     */
    public void setConnectTimeout(int connectTimeout) {
        requestConfigBuilder.setConnectTimeout(connectTimeout);
    }

    /**
     * 设置从连接管理器获取连接的超时(毫秒)
     * 
     * @Defual 0 系统默认
     * @other -1 用不超时
     */
    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeout);
    }

    /**
     * 创建或获取已创建的client,双重校验锁保持client唯一.
     */
    private CloseableHttpAsyncClient getAsyncHttpClient() {
        if (null == client) {
            synchronized (this) {
                if (null == client) {
                    client = httpAsyncClientBuilder.setDefaultRequestConfig(requestConfigBuilder.build()).setConnectionManager(poolingNHttpClientConnectionManager).build();
                    client.start();
                }
            }
        }
        return client;
    }

    /**
     * 执行方法
     */
    public Future<HttpResponse> execute(HTTPRequest request) throws IOException {
        HttpRequestBase httpRequest = request.createHTTPRequest();
        this.logger.debug("send request, url=" + httpRequest.getURI() + ", method=" + httpRequest.getMethod());
        request.logRequest();
        CloseableHttpAsyncClient client = getAsyncHttpClient();
        Future<HttpResponse> response = client.execute(httpRequest, null);
        return response;
    }

    /**
     * 如果运行在多例模式下,由于共享了连接池,调用此方法会造成连接池关闭.
     */
    @PreDestroy
    public void shutdown() throws IOException {
        this.client.close();
    }

    @Inject
    public void setPoolingNHttpClientConnectionManager(PoolingNHttpClientConnectionManager poolingNHttpClientConnectionManager) {
        this.poolingNHttpClientConnectionManager = poolingNHttpClientConnectionManager;
    }

}
