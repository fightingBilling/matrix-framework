package org.matrix.framework.core.platform.web.session;

public class SecureSessionContext extends SessionContext {

    protected boolean underSecureRequest;

    void underSecureRequest() {
        this.underSecureRequest = true;
    }

    public boolean isUnderSecureRequest() {
        return this.underSecureRequest;
    }
}