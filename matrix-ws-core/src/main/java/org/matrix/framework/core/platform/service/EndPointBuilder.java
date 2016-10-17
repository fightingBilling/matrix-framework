package org.matrix.framework.core.platform.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.matrix.framework.core.platform.exception.BusinessProcessException;
import org.matrix.framework.core.util.StringUtils;

public final class EndPointBuilder<T> {
    private Enum<?> endPoint;
    private String action;
    private String appKey;
    private String bodyContent;
    private String businessType;
    private String data;
    private Class<T> responseClass;
    private Class<?>[] elementClasses;
    private final Map<String, String> params = new ConcurrentHashMap<String, String>();
    private final Map<String, String> headers = new ConcurrentHashMap<String, String>();

    public static <T> EndPointBuilder<T> create(Class<T> targetClass) {
        if (null == targetClass)
            throw new BusinessProcessException("targetClass不能为空.");
        EndPointBuilder<T> endPointBuilder = new EndPointBuilder<T>();
        endPointBuilder.target(targetClass);
        return endPointBuilder;
    }

    public static EndPointBuilder<?> create() {
        return new EndPointBuilder<>();
    }

    public EndPointBuilder<T> elementTypes(Class<?>... elementClasses) {
        this.elementClasses = elementClasses;
        return this;
    }

    public EndPointBuilder<T> appKey(String appKey) {
        this.appKey = appKey;
        return this;
    }

    public EndPointBuilder<T> endpoint(Enum<?> endPoint) {
        this.endPoint = endPoint;
        return this;
    }

    public EndPointBuilder<T> action(String action) {
        this.action = action;
        return this;
    }

    public EndPointBuilder<T> arguments(Object[] arguments) {
        action(String.format(this.action, arguments));
        return this;
    }

    public EndPointBuilder<T> body(String bodyContent) {
        this.bodyContent = bodyContent;
        return this;
    }

    public EndPointBuilder<T> data(String data) {
        this.data = data;
        return this;
    }

    public EndPointBuilder<T> businessType(String businessType) {
        this.businessType = businessType;
        return this;
    }

    private EndPointBuilder<T> target(Class<T> responseClass) {
        this.responseClass = responseClass;
        return this;
    }

    public EndPointBuilder<T> addHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public EndPointBuilder<T> headers(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public EndPointBuilder<T> addParam(String key, String value) {
        this.params.put(key, value);
        return this;
    }

    public EndPointBuilder<T> params(Map<String, String> params) {
        this.params.putAll(params);
        return this;
    }

    public String getService() {
        // if (!StringUtils.hasText(this.appKey))
        // throw new BusinessProcessException("appKey不能为空.");
        if (null == this.endPoint)
            throw new BusinessProcessException("endPoint不能为空.");
        if (!StringUtils.hasText(this.action))
            throw new BusinessProcessException("action不能为空.");
        // if (!StringUtils.hasText(this.bussinessType)) {
        // throw new BusinessProcessException("bussinessType不能为空.");
        // }
        return this.endPoint.toString() + this.action;
    }

    public String getAppKey() {
        return this.appKey;
    }

    public String getBodyContent() {
        return this.bodyContent;
    }

    public Class<T> getResponseClass() {
        return this.responseClass;
    }

    public Class<?>[] getElementClasses() {
        return this.elementClasses;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getBusinessType() {
        return businessType;
    }

    public String getData() {
        return data;
    }

}