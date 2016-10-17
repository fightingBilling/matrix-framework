package org.matrix.framework.core.database.memcached;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
public class CacheGroup implements Serializable {

    @XmlAttribute(name = "name", required = true)
    private String groupName;

    @XmlElement(name = "servers", required = true)
    private String servers;

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getServers() {
        return this.servers;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }
}