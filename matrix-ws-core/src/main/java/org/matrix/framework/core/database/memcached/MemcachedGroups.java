package org.matrix.framework.core.database.memcached;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlRootElement(name = "memcached-groups")
@XmlAccessorType(XmlAccessType.FIELD)
public class MemcachedGroups implements Serializable {

    @XmlElement(name = "group")
    private List<CacheGroup> cacheGroups = new ArrayList<CacheGroup>();

    public List<CacheGroup> getCacheGroups() {
        return this.cacheGroups;
    }

    public void setCacheGroups(List<CacheGroup> cacheGroups) {
        this.cacheGroups = cacheGroups;
    }
}