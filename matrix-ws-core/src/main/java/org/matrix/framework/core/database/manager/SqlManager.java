package org.matrix.framework.core.database.manager;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.matrix.framework.core.collection.converter.XMLConvert;
import org.matrix.framework.core.util.IOUtils;
import org.matrix.framework.core.util.StringUtils;
import org.matrix.framework.core.xml.DOMUtils;
import org.matrix.framework.core.xml.XMLParser;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Document;

public final class SqlManager {

    private static final SqlManager INSTANCE = new SqlManager();
    private final Map<String, String> values = new ConcurrentHashMap<String, String>();
    private final SqlMappingRegistry registry = new SqlMappingRegistry();
    private XMLConvert xmlConvert;

    public static SqlManager get() {
        return INSTANCE;
    }

    public String getSqlText(String aliasName, boolean multiPartition, Object[] partitions) {
        return StringUtils.format((String) this.values.get(aliasName), multiPartition, partitions);
    }

    public String getSqlText(String aliasName) {
        return this.values.get(aliasName);
    }

    private String getAliasName(String namespace, String name) {
        return namespace.trim() + "." + name.trim();
    }

    @PostConstruct
    public void initialize() {
        if (!this.values.isEmpty()) {
            return;
        }
        Set<String> classPaths = this.registry.getSqlMappings();
        if (CollectionUtils.isEmpty(classPaths)) {
            return;
        }
        for (String classPath : classPaths) {
            InputStream instream = null;
            try {
                Document document = new XMLParser().parse(IOUtils.bytes(Thread.currentThread().getContextClassLoader().getResourceAsStream(classPath)));
                String xml = DOMUtils.text(document);
                SqlConfigs configs = this.xmlConvert.fromString(SqlConfigs.class, xml);
                List<SqlContext> contexts = configs.getContexts();
                String namespace = configs.getNamespace();
                for (SqlContext sqlContext : contexts) {
                    this.values.put(getAliasName(namespace, sqlContext.getAliasName()), sqlContext.getValue());
                }
            } finally {
                IOUtils.close(instream);
            }
        }
    }

    public SqlMappingRegistry getRegistry() {
        return this.registry;
    }

    @Inject
    public void setXmlConvert(XMLConvert xmlConvert) {
        this.xmlConvert = xmlConvert;
    }

}
