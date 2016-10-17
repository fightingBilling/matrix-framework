package org.matrix.framework.core.platform.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.commons.collections.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.matrix.framework.core.http.Get;
import org.matrix.framework.core.http.HTTPRequest;
import org.matrix.framework.core.http.TextPost;
import org.matrix.framework.core.http.async.AsyncHttpClient;
import org.matrix.framework.core.json.JSONBinder;
import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.platform.exception.WsClientInValidateException;
import org.matrix.framework.core.platform.web.rest.SdyErrorResponse;
import org.matrix.framework.core.util.DigestUtils;
import org.matrix.framework.core.util.StringUtils;
import org.matrix.framework.core.util.TimeLength;
import org.slf4j.Logger;

/**
 * 异步请求的api
 * 
 * @target 实际线程安全的类,保持单例.
 * @author pankai 2015年9月17日
 */
public class MatrixAsyncApiClient {

    private final Logger logger = LoggerFactory.getLogger(MatrixAsyncApiClient.class);

    private AsyncHttpClient asyncHttpClient;

    private final Locale locale = Locale.CHINA;
    private final static TimeLength DEFAULT_GET_TIMEOUT = TimeLength.seconds(10L);
    private long getTimeout;
    private boolean throwException;

    /**
     * sdy get方法
     */
    public <T> Future<HttpResponse> getSdy(EndPointBuilder<T> builder) {
        AsyncHttpClient httpClient = asyncHttpClient;
        String url = null;
        try {
            if (StringUtils.hasText(builder.getAppKey())) {
                String sign = DigestUtils.md5DigestAsHex((builder.getData() + builder.getAppKey()).getBytes(Charset.forName("utf-8"))).toLowerCase();
                url = builder.getService() + "?business_type=" + builder.getBusinessType() + "&sign=" + sign + "&data=" + URLEncoder.encode(builder.getData(), "utf-8");
            } else {
                url = builder.getService() + "?data=" + URLEncoder.encode(builder.getData(), "utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            throw new WsClientInValidateException("UnsupportedEncoding!");
        }
        Get get = new Get(url);
        //logger.info("Going to async execute reqest, {}", new Object[] { url });
        get.setAccept("application/json");
        addLocaleHeader(get);
        addHeader(get, builder.getHeaders());
        try {
            return httpClient.execute(get);
        } catch (IOException e) {
            throw new WsClientInValidateException("MatrixApiAsyncClient async execute error occured!", e);
        }
    }

    /**
     * sdy post 方法
     */
    public <T> Future<HttpResponse> postSdy(EndPointBuilder<T> builder) {
        AsyncHttpClient httpClient = asyncHttpClient;
        TextPost post = new TextPost(builder.getService());
        String sign = null;
        try {
            sign = DigestUtils.md5DigestAsHex((URLDecoder.decode(URLEncoder.encode(builder.getBodyContent(), "utf-8"), "utf-8") + builder.getAppKey()).getBytes()).toLowerCase();
        } catch (UnsupportedEncodingException e) {
            throw new WsClientInValidateException("Generate the sign failed!", e);
        }
        String head = "business_type=" + builder.getBusinessType() + "&sign=" + sign + "&data=";
        post.setAccept("application/json");
        post.setContentType("application/x-www-form-urlencoded");
        addLocaleHeader(post);
        addHeader(post, builder.getHeaders());
        post.setBody(head + builder.getBodyContent());
        try {
            return httpClient.execute(post);
        } catch (IOException e) {
            throw new WsClientInValidateException("MatrixApiAsyncClient async execute error occured!", e);
        }
    }

    public <T> T getResultSdy(Future<HttpResponse> future, EndPointBuilder<T> builder) {
        try {
            return convertResponseSdy(builder, future.get(getTimeout == 0L ? DEFAULT_GET_TIMEOUT.toMilliseconds() : getTimeout, TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            if (throwException) {
                throw new WsClientInValidateException("MatrixApiAsyncClient get error occured!", e);
            }
            logger.error("Async reqeust execution error occured!", e);
            future.cancel(true);
            return null;
        }
    }

    private <T> T convertResponseSdy(EndPointBuilder<T> builder, HttpResponse response) throws ParseException, IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        this.logger.debug("response status code => {}", statusCode);
        HttpEntity entity = response.getEntity();
        String responseText = EntityUtils.toString(entity, "UTF-8");
        this.logger.debug("responseText=" + responseText);
        if (!((statusCode >= 200) && (statusCode <= 207))) {
            if (throwException) {
                throw new WsClientInValidateException("Failed to call api service, responseText=" + responseText + ", statusCode=" + statusCode);
            } else {
                logger.warn("MatrixApiAsyncClient called {} failed! The statusCode is {}.", new Object[] { builder.getService(), statusCode });
                return null;
            }
        }
        if (!validateResponseSdy(responseText, builder)) {
            return null;
        }
        return JSONBinder.binder(builder.getResponseClass(), builder.getElementClasses()).fromJSON(responseText);
    }

    public boolean validateResponseSdy(String responseText, EndPointBuilder<?> builder) {
        if (!responseText.startsWith("{\"code\":0,")) {
            SdyErrorResponse errorResponse = (SdyErrorResponse) JSONBinder.binder(SdyErrorResponse.class, new Class[0]).fromJSON(responseText);
            String error = errorResponse.getMessage();
            if (throwException) {
                throw new WsClientInValidateException(error);
            } else {
                logger.warn("MatrixApiAsyncClient called {} failed! The code is {}, message is {}.", new Object[] { builder.getService(), errorResponse.getCode(), errorResponse.getMessage() });
                return false;
            }
        } else {
            return true;
        }
    }

    private void addLocaleHeader(HTTPRequest request) {
        request.addHeader("Accept-Language", this.locale.getLanguage() + "-" + this.locale.getCountry());
    }

    private void addHeader(HTTPRequest request, Map<String, String> headers) {
        if (MapUtils.isEmpty(headers))
            return;
        Set<Entry<String, String>> entrySet = headers.entrySet();

        for (Map.Entry<String, String> entry : entrySet)
            request.addHeader((String) entry.getKey(), (String) entry.getValue());
    }

    public void setGetTimeout(long getTimeout) {
        this.getTimeout = getTimeout;
    }

    public void setThrowException(boolean throwException) {
        this.throwException = throwException;
    }

    public long getGetTimeout() {
        return getTimeout;
    }

    public boolean isThrowException() {
        return throwException;
    }

    @Inject
    public void setAsyncHttpClient(AsyncHttpClient asyncHttpClient) {
        this.asyncHttpClient = asyncHttpClient;
    }

}
