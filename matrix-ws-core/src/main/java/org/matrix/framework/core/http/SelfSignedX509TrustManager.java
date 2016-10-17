package org.matrix.framework.core.http;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class SelfSignedX509TrustManager implements X509TrustManager {
    public void checkClientTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
    }

    public void checkServerTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
    }

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}