package org.matrix.framework.core.database.mybatis.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.matrix.framework.core.database.mybatis.dynamic.DBContextHolder;
import org.matrix.framework.core.database.mybatis.dynamic.DynamicDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * mybatis默认配置
 * @author pankai
 * 2016年1月16日
 */
public abstract class DefaultMybatisConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public abstract DataSource writeDataSource();

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public abstract DataSource[] reaDataSources();

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public DataSourceTransactionManager transactionManager(DynamicDataSource dynamicDataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dynamicDataSource);
        return transactionManager;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SqlSessionFactoryBean sqlSessionFactoryBean(DynamicDataSource dynamicDataSource) throws IOException {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dynamicDataSource);
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath*:db/mapper/*Mapper.xml");
        bean.setMapperLocations(resources);
        return bean;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setBasePackage(setBasePackage());
        configurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
        return configurer;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public DynamicDataSource dynamicDataSource(DataSource writeDataSource, DataSource[] reaDataSources) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setDefaultTargetDataSource(writeDataSource);
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put("master", writeDataSource);
        for (int i = 0; i < reaDataSources.length; i++) {
            map.put("slave" + i, reaDataSources[i]);
        }
        dynamicDataSource.setTargetDataSources(map);
        return dynamicDataSource;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public DBContextHolder dbContextHolder(DataSource[] reaDataSources) {
        return new DBContextHolder(reaDataSources);
    }

    public abstract String setBasePackage();

}
