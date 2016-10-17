package org.matrix.framework.core.http;

public class HTTPStatusCode {

    private final int statusCode;

    public HTTPStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public boolean isRedirect() {
        return (this.statusCode == 302) || (this.statusCode == 301) || (this.statusCode == 307) || (this.statusCode == 303);
    }

    public boolean isSuccess() {
        return (this.statusCode >= 200) && (this.statusCode <= 207);
    }

    public boolean isServerError() {
        return (this.statusCode >= 500) && (this.statusCode <= 507);
    }

    public String toString() {
        return String.valueOf(this.statusCode);
    }
}