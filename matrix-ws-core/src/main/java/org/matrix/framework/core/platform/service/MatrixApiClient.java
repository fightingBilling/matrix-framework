package org.matrix.framework.core.platform.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.collections.MapUtils;
import org.matrix.framework.core.http.Delete;
import org.matrix.framework.core.http.FormPost;
import org.matrix.framework.core.http.Get;
import org.matrix.framework.core.http.HTTPClient;
import org.matrix.framework.core.http.HTTPRequest;
import org.matrix.framework.core.http.HTTPResponse;
import org.matrix.framework.core.http.HTTPStatusCode;
import org.matrix.framework.core.http.TextPost;
import org.matrix.framework.core.json.JSONBinder;
import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.platform.exception.WsClientInValidateException;
import org.matrix.framework.core.platform.web.rest.ErrorResponse;
import org.matrix.framework.core.util.DigestUtils;
import org.matrix.framework.core.util.ExceptionUtils;
import org.matrix.framework.core.util.StopWatch;
import org.matrix.framework.core.util.StringUtils;
import org.slf4j.Logger;

//发送http请求的客户端
public class MatrixApiClient {

    private HTTPClient httpClient;

    private final Logger logger = LoggerFactory.getLogger(MatrixApiClient.class);

    private Locale locale = Locale.CHINA;

    /**
     * matrix get方法
     */
    public <T> T get(EndPointBuilder<T> builder) {
        HTTPClient httpClient = createHTTPClient();
        Get get = new Get(builder.getService());
        get.setAccept("application/json");
        get.addHeader("WS-APPKEY", builder.getAppKey());
        addLocaleHeader(get);
        addHeader(get, builder.getHeaders());
        return convertResponse(builder, httpClient.execute(get));
    }

    /**
     * sdy get方法
     */
    public <T> T getSdy(EndPointBuilder<T> builder) {
        HTTPClient httpClient = createHTTPClient();
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
        logger.info("Executing sdy get request, {}", new Object[] { url });
        get.setAccept("application/json");
        addLocaleHeader(get);
        addHeader(get, builder.getHeaders());
        return convertResponse(builder, httpClient.execute(get));
    }

    /**
     * matrix post方法
     */
    public <T> T post(EndPointBuilder<T> builder) {
        HTTPClient httpClient = createHTTPClient();
        TextPost post = new TextPost(builder.getService());
        post.setAccept("application/json");
        post.setContentType("application/json");
        post.addHeader("WS-APPKEY", builder.getAppKey());
        addLocaleHeader(post);
        addHeader(post, builder.getHeaders());
        if (StringUtils.hasText(builder.getBodyContent())) {
            post.setBody(builder.getBodyContent());
        }
        return convertResponse(builder, httpClient.execute(post));
    }

    /**
     * sdy post 方法
     */
    public <T> T postSdy(EndPointBuilder<T> builder) {
        HTTPClient httpClient = createHTTPClient();
        TextPost post = new TextPost(builder.getService());
        String sign = DigestUtils.md5DigestAsHex((builder.getBodyContent() + builder.getAppKey()).getBytes(Charset.forName("utf-8"))).toLowerCase();
        String head = "business_type=" + builder.getBusinessType() + "&sign=" + sign + "&data=";
        post.setAccept("application/json");
        post.setContentType("application/x-www-form-urlencoded");
        addLocaleHeader(post);
        addHeader(post, builder.getHeaders());
        post.setBody(head + builder.getBodyContent());
        logger.info("Executing sdy post request, url is {}, body is {}.", new Object[] { post.getUrl(), post.getBody() });
        return convertResponse(builder, httpClient.execute(post));
    }

    /**
     * sdy delete 方法
     */
    public <T> T deleteSdy(EndPointBuilder<T> builder) {
        HTTPClient httpClient = createHTTPClient();
        Delete delete = new Delete(builder.getService());
        String sign = DigestUtils.md5DigestAsHex((builder.getData() + builder.getAppKey()).getBytes(Charset.forName("utf-8"))).toLowerCase();
        delete.setAccept("application/json");
        addLocaleHeader(delete);
        addHeader(delete, builder.getHeaders());
        delete.addParameter("sign", sign);
        delete.addParameter("business_type", builder.getBusinessType());
        delete.addParameter("data", builder.getData());
        logger.info("Executing sdy delete request, url is {}, data is {}.", new Object[] { delete.getUrl(), builder.getData() });
        return convertResponse(builder, httpClient.execute(delete));
    }

    public <T> T convertResponse(EndPointBuilder<T> builder, HTTPResponse response) {
        HTTPStatusCode statusCode = response.getStatusCode();
        logger.debug("response status code => {}", Integer.valueOf(statusCode.getStatusCode()));
        String responseText = response.getResponseText();
        logger.debug("response Text =>{}", responseText);
        if (!statusCode.isSuccess()) {
            throw new WsClientInValidateException("Failed to call api service, responseText=" + responseText + ", statusCode=" + statusCode.getStatusCode());
        }
        validateResponse(responseText);
        return JSONBinder.binder(builder.getResponseClass(), builder.getElementClasses()).fromJSON(responseText);
    }

    /**
     * matrix validateResponse方法
     */
    public void validateResponse(String responseText) {
        if (responseText.startsWith("{\"status\":\"FAILED\"")) {
            ErrorResponse errorResponse = (ErrorResponse) JSONBinder.binder(ErrorResponse.class, new Class[0]).fromJSON(responseText);
            String error = errorResponse.getMessage();
            throw new WsClientInValidateException(error);
        }
    }

    /**
     * sdy validateResponse方法
     */
    public void validateResponseSdy(String responseText) {
        if (!responseText.startsWith("{\"code\":0,")) {
            ErrorResponse errorResponse = (ErrorResponse) JSONBinder.binder(ErrorResponse.class, new Class[0]).fromJSON(responseText);
            String error = errorResponse.getMessage();
            throw new WsClientInValidateException(error);
        }
    }

    private HTTPClient createHTTPClient() {
        // The default value is false. No need to set it again.
        // httpClient.setValidateStatusCode(false);
        return httpClient;
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

    private void addParam(FormPost post, Map<String, String> params) {
        if (MapUtils.isEmpty(params))
            return;
        Set<Entry<String, String>> entrySet = params.entrySet();

        for (Map.Entry<String, String> entry : entrySet)
            post.addParameter((String) entry.getKey(), (String) entry.getValue());
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    // 此方法用于sudiyi_med_java项目转发post的表单提交
    public String redirectFormPost(EndPointBuilder<?> builder) {
        HTTPClient httpClient = createHTTPClient();
        TextPost post = new TextPost(builder.getService());
        addHeader(post, builder.getHeaders());
        // 终端以post方式提交表单
        post.addHeader("Content-Type", "application/x-www-form-urlencoded");
        if (StringUtils.hasText(builder.getBodyContent())) {
            post.setBody(builder.getBodyContent());
        }
        StopWatch stopWatch = new StopWatch();
        HTTPResponse response;
        try {
            response = httpClient.execute(post);
        } catch (Exception e) {
            logger.error("Redirect post fail, wrong path is {}, full stack {}", new Object[] { post.getUrl(), ExceptionUtils.stackTrace(e) });
            throw new WsClientInValidateException("Redirect post fail!");
        }

        long elapsedTime = stopWatch.elapsedTime();
        if (!response.getStatusCode().isSuccess()) {
            logger.error("wrong path is {},elapsedTime = {}.post headers is {}, post body is {}, response status code is {}, responsetext is {}.", new Object[] { post.getUrl(),
                    Long.valueOf(elapsedTime), post.getHeaders().getHeadsString(), post.getBody(), response.getStatusCode().getStatusCode(), response.getResponseText() });
        } else {
            logger.info("correct path is {},elapsedTime = {}", new Object[] { post.getUrl(), Long.valueOf(elapsedTime) });
        }
        return pureStringResponse(response);
    }

    // 此方法用于sudiyi_med_java项目转发get请求
    public String redirectGet(EndPointBuilder<?> builder) {
        HTTPClient httpClient = createHTTPClient();
        Get get = new Get(builder.getService());
        if (MapUtils.isNotEmpty(builder.getParams())) {
            get.addParameters(builder.getParams());
        }
        get.setAccept("application/json");
        addLocaleHeader(get);
        addHeader(get, builder.getHeaders());
        StopWatch stopWatch = new StopWatch();
        HTTPResponse response;
        try {
            response = httpClient.execute(get);
        } catch (Exception e) {
            logger.error("Redirect get fail, wrong path is {}, full stack {}", new Object[] { get.getUrl(), ExceptionUtils.stackTrace(e) });
            throw new WsClientInValidateException("Redirect get fail!", e);
        }

        long elapsedTime = stopWatch.elapsedTime();
        if (!response.getStatusCode().isSuccess()) {
            logger.error("wrong path is {},elapsedTime = {}.get headers is {}, response status code is {}, responsetext is {}.", new Object[] { get.getUrl(), Long.valueOf(elapsedTime),
                    get.getHeaders().getHeadsString(), response.getStatusCode().getStatusCode(), response.getResponseText() });
        } else {
            logger.info("correct path is {},elapsedTime = {}", new Object[] { get.getUrl(), Long.valueOf(elapsedTime) });
        }
        return pureStringResponse(response);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public String postForm(EndPointBuilder builder) {
        HTTPClient httpClient = createHTTPClient();
        FormPost post = new FormPost(builder.getService());
        post.setAccept("application/json");
        addLocaleHeader(post);
        addHeader(post, builder.getHeaders());
        addParam(post, builder.getParams());
        return pureStringResponse(httpClient.execute(post));
    }

    /**
     * 如果http响应状态码是2XX则返回响应的纯文本
     * 
     * @param response
     * @return
     */
    private String pureStringResponse(HTTPResponse response) {
        HTTPStatusCode statusCode = response.getStatusCode();
        this.logger.debug("response status code => {}", statusCode.getStatusCode());
        String responseText = response.getResponseText();
        // 非2XX响应就报错.
        if (!statusCode.isSuccess()) {
            throw new WsClientInValidateException("Failed to call api service, responseText=" + responseText + ", statusCode=" + statusCode.getStatusCode());
        }
        return responseText;
    }

    @Inject
    public void setHttpClient(HTTPClient httpClient) {
        this.httpClient = httpClient;
    }

}
