package org.matrix.framework.core.database.manager;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
public class SqlContext implements Serializable {
    @XmlAttribute(name = "name", required = true)
    private String aliasName;

    @XmlElement(name = "value", required = true)
    private String value;

    public SqlContext() {
    }

    public SqlContext(String aliasName, String value) {
        this.aliasName = aliasName;
        this.value = value;
    }

    public String getAliasName() {
        return this.aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
