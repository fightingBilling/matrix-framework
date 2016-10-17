package org.matrix.framework.core.search;

import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class ElasticSearchSettings {

    private String[] officialServers;
    private String[] jestServers;
    private String clusterName;

    // 发现新节点的频率(分钟)
    private long discoveryFrequency;

    public long getDiscoveryFrequency() {
        return discoveryFrequency;
    }

    public void setDiscoveryFrequency(long discoveryFrequency) {
        this.discoveryFrequency = discoveryFrequency;
    }

    public String[] getOfficialServers() {
        return officialServers;
    }

    public void setOfficialServers(String[] officialServers) {
        this.officialServers = officialServers;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String[] getJestServers() {
        return jestServers;
    }

    public void setJestServers(String[] jestServers) {
        this.jestServers = jestServers;
    }

    public InetSocketTransportAddress[] getAddresses() {
        InetSocketTransportAddress[] result = new InetSocketTransportAddress[officialServers.length];
        for (int i = 0; i < officialServers.length; i++) {
            String[] pair = officialServers[i].split(":");
            result[i] = new InetSocketTransportAddress(pair[0], Integer.parseInt(pair[1]));
        }
        return result;
    }
}