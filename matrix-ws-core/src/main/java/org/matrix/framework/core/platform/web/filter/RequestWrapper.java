package org.matrix.framework.core.platform.web.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.matrix.framework.core.util.IOUtils;
import org.springframework.util.StringUtils;

/**
 * 配合matrix-filter,存在一些问题.
 * @author pankai
 * Jan 27, 2016
 */
public class RequestWrapper extends HttpServletRequestWrapper {
    private ServletInputStream inputStream;
    private String originalBody;
    private String body;
    private BufferedReader reader;
    private boolean secure;
    private String scheme;
    private boolean proxied;
    private String remoteAddr;
    private Map<String, String> headers = new ConcurrentHashMap<String, String>();

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        if (isMultipart()) {
            preLoadBody(request);
        }
        parseProxyInfo(request);
    }

    public final boolean isMultipart() {
        String contentType = getContentType();
        return (null != contentType) && (contentType.toLowerCase().startsWith("multipart/"));
    }

    private void preLoadBody(HttpServletRequest request) throws IOException {
        Charset charset = Charset.forName(getCharacterEncoding());
        byte[] bodyBytes = IOUtils.bytes(request.getInputStream());
        this.originalBody = new String(bodyBytes, charset);
        this.body = getParameter("_body");
        if (null == this.body) {
            this.body = this.originalBody;
        }
        this.inputStream = new RequestCachingInputStream(this.body.getBytes(charset));
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            this.headers.put(headerName, request.getHeader(headerName));
        }
    }

    private void parseProxyInfo(HttpServletRequest request) {
        this.secure = request.isSecure();
        this.scheme = request.getScheme();
        String forwardedProtocol = request.getHeader("x-forwarded-proto");
        if (StringUtils.hasText(forwardedProtocol)) {
            this.proxied = true;
            this.scheme = forwardedProtocol.toLowerCase();
            this.secure = "https".equals(this.scheme);
            String forwardedForInfo = request.getHeader("x-forwarded-for");
            this.remoteAddr = (StringUtils.hasText(forwardedForInfo) ? forwardedForInfo.trim().split(",")[0] : super.getRemoteAddr());
        }
    }

    public final String getCharacterEncoding() {
        String defaultEncoding = super.getCharacterEncoding();
        return null == defaultEncoding ? "UTF-8" : defaultEncoding;
    }

    public String getOriginalBody() {
        if (isMultipart())
            throw new IllegalStateException("multipart request does not support preloaded body");
        return this.originalBody;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public ServletInputStream getInputStream() {
        return this.inputStream;
    }

    private static class RequestCachingInputStream extends ServletInputStream {
        private final ByteArrayInputStream inputStream;

        public RequestCachingInputStream(byte[] bytes) {
            this.inputStream = new ByteArrayInputStream(bytes);
        }

        public int read() throws IOException {
            return this.inputStream.read();
        }
    }
}