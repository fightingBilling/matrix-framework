package org.matrix.framework.core.kafka.consumer.strategy;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.matrix.framework.core.platform.SpringObjectFactory;

/**
 * 消息处理策略管理器
 *
 * @usage 保持单例,将所有的跟业务相关的消息消费策略注册进这个管理器.
 * @author pankai 2015年8月17日
 */
public class MatrixConsumerStrategyManager {

    private SpringObjectFactory springObjectFactory;

    private final Map<String, MatrixConsumerStrategy> matrixConsumerStrategyMap = new ConcurrentHashMap<String, MatrixConsumerStrategy>();
    private final Map<String, Class<? extends MatrixConsumerStrategy>> matrixConsumerStrategyClassMap = new ConcurrentHashMap<String, Class<? extends MatrixConsumerStrategy>>();

    @PostConstruct
    public void initialize() {
        if (!matrixConsumerStrategyClassMap.isEmpty()) {
            for (Entry<String, Class<? extends MatrixConsumerStrategy>> entry : matrixConsumerStrategyClassMap.entrySet()) {
                final String beanName = entry.getKey();
                final Class<? extends MatrixConsumerStrategy> clazz = entry.getValue();
                // 将业务策略注册到spring容器.
                springObjectFactory.registerSingletonBean(beanName, clazz);
                final MatrixConsumerStrategy instance = springObjectFactory.getBean(beanName, MatrixConsumerStrategy.class);
                matrixConsumerStrategyMap.put(beanName, instance);
            }
        }
    }

    /**
     * 注册业务消费策略.
     *
     * @note 实例化之后应当立即注册业务策略.
     * @param strategyType
     * @param clazz
     */
    public void registerConsumerStrategy(Enum<?> strategyType, Class<? extends MatrixConsumerStrategy> clazz) {
        final String beanName = strategyType.toString();
        matrixConsumerStrategyClassMap.put(beanName, clazz);
    }

    /**
     * 获取消息消费策略
     *
     * @param strategyType
     * @return
     */
    public MatrixConsumerStrategy getConsumerStrategy(Enum<?> strategyType) {
        String beanName = strategyType.toString();
        return matrixConsumerStrategyMap.get(beanName);
    }

    @Inject
    public void setSpringObjectFactory(SpringObjectFactory springObjectFactory) {
        this.springObjectFactory = springObjectFactory;
    }

}
