package org.matrix.framework.core.platform;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.http.Consts;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.IOReactorException;
import org.matrix.framework.core.collection.converter.JSONConverter;
import org.matrix.framework.core.collection.converter.XMLConvert;
import org.matrix.framework.core.database.manager.SqlManager;
import org.matrix.framework.core.database.manager.SqlMappingRegistry;
import org.matrix.framework.core.database.memcached.MatrixMemcachedFactory;
import org.matrix.framework.core.database.memcached.MemcachedGroups;
import org.matrix.framework.core.http.HTTPClient;
import org.matrix.framework.core.http.async.AsyncHttpClient;
import org.matrix.framework.core.platform.context.Messages;
import org.matrix.framework.core.platform.context.PropertyContext;
import org.matrix.framework.core.platform.exception.handler.ErrorHandler;
import org.matrix.framework.core.platform.scheduler.Scheduler;
import org.matrix.framework.core.platform.scheduler.SchedulerImpl;
import org.matrix.framework.core.platform.service.MatrixApiClient;
import org.matrix.framework.core.platform.service.MatrixAsyncApiClient;
import org.matrix.framework.core.settings.LogSettings;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public abstract class DefaultAppConfig {

    protected Environment env;

    @Inject
    public void setEnv(Environment env) {
        this.env = env;
    }

    @Bean
    public static PropertyContext propertyContext() throws IOException {
        PropertyContext propertySource = new PropertyContext();
        propertySource.setLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:*.properties"));
        return propertySource;
    }

    @Bean
    public Messages messages() {
        Messages messages = new Messages();
        // Resource[] messageResources = new PathMatchingResourcePatternResolver().getResources("classpath*:messages/*.properties");
        //
        // String[] baseNames = new String[messageResources.length];
        // int i = 0;
        // for (int messageResourcesLength = messageResources.length; i < messageResourcesLength; i++) {
        // Resource messageResource = messageResources[i];
        // String filename = messageResource.getFilename();
        // baseNames[i] = ("messages/" + filename.substring(0, filename.indexOf(46)));
        // }
        // messages.setBasenames("classpath:messages","classpath:org/hibernate/validator/ValidationMessages");
        messages.setUseCodeAsDefaultMessage(true);
        messages.setDefaultEncoding("UTF-8");
        return messages;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SpringObjectFactory springObjectFactory() {
        return SpringObjectFactory.getInstance();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ErrorHandler errorHandler() {
        return new ErrorHandler();
    }

    // ********************************************** 同步http client支持配置 start*****************************************/

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(env.getProperty("site.http.client.max.total", Integer.class, 2000));
        manager.setDefaultMaxPerRoute(manager.getMaxTotal());
        // 设置"静止"连接的检查周期,ms
        manager.setValidateAfterInactivity(10000);
        return manager;
    }

    /**
     * @Note:matrix http client并非线程安全的类,这里使用多例.
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public HTTPClient httpClient() {
        HTTPClient client = new HTTPClient();
        return client;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public MatrixApiClient matrixApiClient() {
        return new MatrixApiClient();
    }

    // ********************************************** 同步http client支持配置 end ******************************************/

    // ********************************************** 异步http client支持配置 start*****************************************/
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public PoolingNHttpClientConnectionManager poolingNHttpClientConnectionManager() throws IOReactorException {
        PoolingNHttpClientConnectionManager manager = new PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor(IOReactorConfig.custom().build()));
        manager.setDefaultConnectionConfig(ConnectionConfig.custom().setCharset(Consts.UTF_8).build());
        manager.setMaxTotal(env.getProperty("site.http.client.max.total", Integer.class, 2000));
        manager.setDefaultMaxPerRoute(manager.getMaxTotal());
        return manager;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public AsyncHttpClient asyncHttpClient() {
        return new AsyncHttpClient();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public MatrixAsyncApiClient matrixAsyncApiClient() {
        return new MatrixAsyncApiClient();
    }

    // ********************************************** 异步http client支持配置 end ******************************************/

    @Bean
    public Scheduler scheduler() {
        return new SchedulerImpl();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public LogSettings logSettings() {
        return LogSettings.get();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public JSONConverter jsonConverter() {
        return new JSONConverter();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public XMLConvert xmlConverter() {
        return new XMLConvert();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SqlManager sqlManager() {
        SqlManager sqlManager = SqlManager.get();
        registrySqlMapping(sqlManager.getRegistry());
        return sqlManager;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public MemcachedGroups memcachedGroups() {
        return new MemcachedGroups();
    }

    // @Bean
    // @Scope("singleton")
    // public MemcachedbGroups memcachedbGroups() {
    // return new MemcachedbGroups();
    // }

    /**
     * memcached作为spring缓存机制的provider需要的工厂.
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public MatrixMemcachedFactory matrixMemcachedFactory() {
        MatrixMemcachedFactory factory = new MatrixMemcachedFactory();
        factory.setMemcachedGroups(memcachedGroups());
        return factory;
    }

    //
    // @Bean
    // @Scope("singleton")
    // public MatrixMemcachedbFactory matrixMemcacheDBFactory() {
    // MatrixMemcachedbFactory factory = new MatrixMemcachedbFactory();
    // factory.setMemcachedbGroups(memcachedbGroups());
    // return factory;
    // }
    //
    // @Bean
    // public DistributeLock distributeLock() {
    // DistributeLock distributeLock = new DistributeLock();
    // distributeLock.setMemcachedFactory(matrixMemcachedFactory());
    // return distributeLock;
    // }

    // @Bean
    // @Scope("singleton")
    // public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
    // ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    // return executor;
    // }

    protected abstract void registrySqlMapping(SqlMappingRegistry paramSqlMappingRegistry);
}