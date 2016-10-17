package org.matrix.framework.core.http;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.matrix.framework.core.annotation.ThreadSafe;
import org.matrix.framework.core.util.StopWatch;
import org.matrix.framework.core.util.TimeLength;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 同步HttpClient.
 * Once configured, this class is thread-safe.
 * @author pankai
 * Nov 4, 2015
 */
@ThreadSafe
public class HTTPClient {
    /**
     * 默认超时时间设置为60秒.
     */
    private static final TimeLength DEFAULT_TIME_OUT = TimeLength.seconds(60L);
    @SuppressWarnings("unused")
    private static final TimeLength NO_TIME_OUT = TimeLength.ZERO;
    private final Logger logger = LoggerFactory.getLogger(HTTPClient.class);
    private CloseableHttpClient httpClient;
    private RequestConfig.Builder requestConfigBuilder;
    private HttpClientBuilder builder;

    //配置必须在初始化时予以明确.不允许在运行期改变.
    // 是否验证返回值
    private boolean validateStatusCode = false;
    private String basicAuthUser;
    private String basicAuthPassword;
    private boolean acceptSelfSignedCert = true;
    //配置必须在初始化时予以明确.不允许在运行期改变.

    // 连接池管理器.
    private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;

    /**
     * 初始化代码应当放在@PostConstruct注解的方法下.注解保证了依赖注入之后再执行初始化方法.
     */
    @PostConstruct
    public void initalize() {
        requestConfigBuilder = RequestConfig.custom().setConnectTimeout((int) DEFAULT_TIME_OUT.toMilliseconds()).setSocketTimeout((int) DEFAULT_TIME_OUT.toMilliseconds());
        // connectionManager可共享.指定异常不重试.
        this.builder = HttpClientBuilder.create().setConnectionManager(this.poolingHttpClientConnectionManager).setConnectionManagerShared(true).setRetryHandler(
                new DefaultHttpRequestRetryHandler(0, Boolean.FALSE)).setDefaultRequestConfig(requestConfigBuilder.build());
    }

    public HTTPResponse execute(HTTPRequest request) {
        StopWatch watch = new StopWatch();
        try {
            CloseableHttpResponse response = executeMethod(request);
            String responseText = readResponseText(response);
            HTTPStatusCode statusCode = new HTTPStatusCode(response.getStatusLine().getStatusCode());

            validateStatusCode(statusCode);
            HTTPHeaders headers = HTTPHeaders.createResponseHeaders(response);
            return new HTTPResponse(statusCode, headers, responseText);
        } catch (IOException e) {
            throw new HTTPException(e);
        } finally {
            this.logger.debug(String.format("execute finished, elapsedTime=%d(ms)", new Object[] { Long.valueOf(watch.elapsedTime()) }));
        }
    }

    public HTTPBinaryResponse download(HTTPRequest request) {
        StopWatch watch = new StopWatch();
        try {
            HttpResponse response = executeMethod(request);

            byte[] responseContent = readResponseBytes(response);

            HTTPStatusCode statusCode = new HTTPStatusCode(response.getStatusLine().getStatusCode());
            validateStatusCode(statusCode);

            HTTPHeaders headers = HTTPHeaders.createResponseHeaders(response);
            return new HTTPBinaryResponse(statusCode, headers, responseContent);
        } catch (IOException e) {
            throw new HTTPException(e);
        } finally {
            this.logger.debug(String.format("download finished, elapsedTime=%d(ms)", new Object[] { Long.valueOf(watch.elapsedTime()) }));
        }
    }

    private CloseableHttpResponse executeMethod(HTTPRequest request) throws IOException {
        HttpRequestBase httpRequest = request.createHTTPRequest();
        this.logger.debug("send request, url=" + httpRequest.getURI() + ", method=" + httpRequest.getMethod());
        request.logRequest();
        CloseableHttpClient client = getHttpClient();
        CloseableHttpResponse response = client.execute(httpRequest);
        this.logger.debug("received response, statusCode=" + response.getStatusLine().getStatusCode());
        return response;
    }

    private String readResponseText(CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        // 处理了响应之后会释放连接.
        String responseText = EntityUtils.toString(entity, "UTF-8");
        this.logger.debug("responseText=" + responseText);
        return responseText;
    }

    private byte[] readResponseBytes(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        byte[] responseContent = EntityUtils.toByteArray(entity);
        this.logger.debug("lengthOfResponseContent=" + responseContent.length);
        return responseContent;
    }

    private CloseableHttpClient getHttpClient() {
        if (null == httpClient) {
            synchronized (this) {
                if (null == httpClient) {
                    configureBasicAuth();
                    try {
                        configureHTTPS();
                    } catch (Exception e) {
                        throw new HTTPException(e);
                    }
                    httpClient = builder.build();
                }
            }
        }
        return httpClient;
    }

    private void validateStatusCode(HTTPStatusCode statusCode) {
        if (this.validateStatusCode) {
            if (statusCode.isSuccess())
                return;
            if (statusCode.isRedirect())
                return;
            throw new HTTPException("invalid response status code, statusCode=" + statusCode);
        }
    }

    private void configureBasicAuth() {
        if (this.basicAuthUser != null) {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(this.basicAuthUser, this.basicAuthPassword));
            builder.setDefaultCredentialsProvider(credentialsProvider);
        }
    }

    private void configureHTTPS() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        TrustManager[] trustManagers = null;
        if (this.acceptSelfSignedCert) {
            trustManagers = new TrustManager[] { new SelfSignedX509TrustManager() };
        }
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, trustManagers, null);
        HostnameVerifier hostnameVerifier = new DefaultHostnameVerifier();
        if (this.acceptSelfSignedCert) {
            hostnameVerifier = NoopHostnameVerifier.INSTANCE;
        }
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(context, hostnameVerifier);
        builder.setSSLSocketFactory(socketFactory);
        builder.setSchemePortResolver(DefaultSchemePortResolver.INSTANCE);
        builder.setSSLHostnameVerifier(hostnameVerifier);
    }

    public void setAcceptSelfSignedCert(boolean acceptSelfSignedCert) {
        this.acceptSelfSignedCert = acceptSelfSignedCert;
    }

    public void setBasicCredentials(String user, String password) {
        this.basicAuthUser = user;
        this.basicAuthPassword = password;
    }

    public void setCookiePolicy(String cookiePolicy) {
        requestConfigBuilder.setCookieSpec(cookiePolicy);
    }

    public void setTimeOut(TimeLength timeOut) {
        requestConfigBuilder.setConnectionRequestTimeout((int) timeOut.toMilliseconds()).setSocketTimeout((int) (timeOut.toMilliseconds()));
    }

    public void setHandleRedirect(boolean handleRedirect) {
        requestConfigBuilder.setRedirectsEnabled(handleRedirect);
    }

    public void setValidateStatusCode(boolean validateStatusCode) {
        this.validateStatusCode = validateStatusCode;
    }

    /**
     * 关闭client的时候会关闭共享的connectionManager.所以此类的所有实例应该一起销毁.
     */
    @PreDestroy
    public void shutdown() throws IOException {
        this.httpClient.close();
    }

    @Inject
    public void setPoolingHttpClientConnectionManager(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
        this.poolingHttpClientConnectionManager = poolingHttpClientConnectionManager;
    }
}