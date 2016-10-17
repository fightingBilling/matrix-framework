package org.matrix.framework.core.settings;

public interface CDNSettings {

    String[] getCDNHosts();

    String[] getNFSHosts();

    String getLocalPath();

    boolean supportHTTPS();

    boolean supportS3();

    String getBucketName();
}
