package org.matrix.framework.core.database.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlRootElement(name = "sql-config")
@XmlAccessorType(XmlAccessType.FIELD)
public class SqlConfigs implements Serializable {
    @XmlAttribute(name = "namespace", required = true)
    private String namespace;

    @XmlElement(name = "sql")
    private List<SqlContext> contexts = new ArrayList<SqlContext>();

    public List<SqlContext> getContexts() {
        return this.contexts;
    }

    public void setContexts(List<SqlContext> contexts) {
        this.contexts = contexts;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
