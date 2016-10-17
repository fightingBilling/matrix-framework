package org.matrix.framework.core.platform.datasync;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.matrix.framework.core.platform.SpringObjectFactory;
import org.matrix.framework.core.platform.datasync.strategy.DataSyncStrategy;

/**
 * 数据同步管理器.
 * 
 * @Note 这个类应该以单例存在.
 * @author pankai 2015年7月18日
 */
public class DataSyncStrategyManager {

    private SpringObjectFactory springObjectFactory;

    private final Map<String, DataSyncStrategy> dataSyncStrategyMap = new ConcurrentHashMap<String, DataSyncStrategy>();

    private final Map<String, Class<? extends DataSyncStrategy>> dataSyncStrategyClassMap = new ConcurrentHashMap<String, Class<? extends DataSyncStrategy>>();

    @PostConstruct
    public void initialize() {
        if (!dataSyncStrategyClassMap.isEmpty()) {
            for (Entry<String, Class<? extends DataSyncStrategy>> entry : dataSyncStrategyClassMap.entrySet()) {
                final String beanName = entry.getKey();
                final Class<? extends DataSyncStrategy> clazz = entry.getValue();
                // 将业务策略注册到spring容器.
                springObjectFactory.registerSingletonBean(beanName, clazz);
                final DataSyncStrategy instance = springObjectFactory.getBean(beanName, DataSyncStrategy.class);
                dataSyncStrategyMap.put(beanName, instance);
            }
        }
    }

    /**
     * 实例化之后应当立即注册业务策略.
     * 
     * @param strategyType
     * @param clazz
     */
    public void registerDataSyncStrategy(Enum strategyType, Class<? extends DataSyncStrategy> clazz) {
        final String beanName = strategyType.toString();
        dataSyncStrategyClassMap.put(beanName, clazz);
    }

    public DataSyncStrategy getDataSyncStrategy(Enum strategyType) {
        String beanName = strategyType.toString();
        return dataSyncStrategyMap.get(beanName);
    }

    @Inject
    public void setSpringObjectFactory(SpringObjectFactory springObjectFactory) {
        this.springObjectFactory = springObjectFactory;
    }

}