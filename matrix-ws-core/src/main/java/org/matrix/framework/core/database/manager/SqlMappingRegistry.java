package org.matrix.framework.core.database.manager;

import java.util.HashSet;
import java.util.Set;

public class SqlMappingRegistry {

    private final Set<String> sqlMappings = new HashSet<String>();

    public void registSqlMapping(String sqlMapping) {
        this.sqlMappings.add(sqlMapping.trim());
    }

    public Set<String> getSqlMappings() {
        return this.sqlMappings;
    }
}
