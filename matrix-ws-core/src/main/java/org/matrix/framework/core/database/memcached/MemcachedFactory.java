package org.matrix.framework.core.database.memcached;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.utils.AddrUtil;

@SuppressWarnings({ "all" })
public abstract class MemcachedFactory {

    protected final Map<String, MemcachedClient> memcachedClientMap = new ConcurrentHashMap();

    protected final Map<String, MemcachedAccess> memcachedAccessMap = new ConcurrentHashMap();

    protected final Map<String, String> serverMap = new ConcurrentHashMap();

    public abstract List<CacheGroup> getCacheGroups();

    public abstract boolean binarySupport();

    public MemcachedClient getClient(String group) {
        return (MemcachedClient) this.memcachedClientMap.get(group);
    }

    public MemcachedAccess getAccess(String group) {
        return (MemcachedAccess) this.memcachedAccessMap.get(group);
    }

    @PostConstruct
    public void initialize() throws Exception {
        for (CacheGroup cacheGroup : getCacheGroups()) {
            String groupName = cacheGroup.getGroupName();
            String servers = cacheGroup.getServers();
            if (!this.memcachedClientMap.containsKey(groupName)) {
                MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(servers));
                builder.setFailureMode(true);
                if (binarySupport()) {
                    builder.setCommandFactory(new BinaryCommandFactory());
                }
                builder.setConnectionPoolSize(10);
                MemcachedClient memcachedClient = builder.build();
                MemcachedAccess memcachedAccess = new MemcachedAccess(memcachedClient);
                this.memcachedClientMap.put(groupName, memcachedClient);
                this.memcachedAccessMap.put(groupName, memcachedAccess);
                this.serverMap.put(groupName, servers);
            }
        }
    }

    @PreDestroy
    public void shutdown() throws IOException {
        Set entrySet = this.memcachedClientMap.entrySet();
        Iterator iterater = entrySet.iterator();
        while (iterater.hasNext()) {
            Map.Entry entry = (Map.Entry) iterater.next();
            MemcachedClient memcachedClient = (MemcachedClient) entry.getValue();
            closeClient(memcachedClient);
            iterater.remove();
        }
    }

    private void closeClient(MemcachedClient client) throws IOException {
        if (null != client)
            client.shutdown();
    }

    public String getServers(String group) {
        return (String) this.serverMap.get(group);
    }
}