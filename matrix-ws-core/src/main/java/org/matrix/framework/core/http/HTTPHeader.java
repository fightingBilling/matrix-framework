package org.matrix.framework.core.http;

public class HTTPHeader {

    private final String name;
    private final String value;

    public HTTPHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }
}