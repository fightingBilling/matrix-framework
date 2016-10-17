package org.matrix.framework.core.platform;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.xml.transform.Source;

import org.matrix.framework.core.database.manager.SqlManager;
import org.matrix.framework.core.json.JSONBinder;
import org.matrix.framework.core.platform.exception.handler.ErrorHandler;
import org.matrix.framework.core.platform.exception.handler.ExceptionTrackingHandler;
import org.matrix.framework.core.platform.monitor.web.BeansEndpointController;
import org.matrix.framework.core.platform.monitor.web.CheckHealthController;
import org.matrix.framework.core.platform.monitor.web.DataSourceHealthController;
import org.matrix.framework.core.platform.monitor.web.DiskSpaceHealthController;
import org.matrix.framework.core.platform.monitor.web.EnvironmentControlller;
import org.matrix.framework.core.platform.monitor.web.MailHealthController;
import org.matrix.framework.core.platform.monitor.web.MemoryUsageController;
import org.matrix.framework.core.platform.monitor.web.MonitortContract;
import org.matrix.framework.core.platform.monitor.web.StatusController;
import org.matrix.framework.core.platform.monitor.web.ThreadInfoController;
import org.matrix.framework.core.platform.monitor.web.URLMappingController;
import org.matrix.framework.core.platform.web.form.AnnotationFormArgumentResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

@EnableWebMvc
public class DefaultWebConfig extends WebMvcConfigurerAdapter {

    @Inject
    protected Environment env;

    @Inject
    ErrorHandler errorHandler;

    @Inject
    SqlManager sqlManager;

    private List<HttpMessageConverter<?>> createMessageConverters() {
        List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
        converters.add(new ByteArrayHttpMessageConverter());
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        stringConverter.setWriteAcceptCharset(false);
        ArrayList<MediaType> textTypes = new ArrayList<MediaType>();
        textTypes.add(MediaType.TEXT_PLAIN);
        textTypes.add(MediaType.TEXT_HTML);
        textTypes.add(MediaType.TEXT_XML);
        textTypes.add(MediaType.APPLICATION_XML);
        stringConverter.setSupportedMediaTypes(textTypes);
        converters.add(stringConverter);
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new SourceHttpMessageConverter<Source>());
        converters.add(new AllEncompassingFormHttpMessageConverter());
        converters.add(new Jaxb2RootElementHttpMessageConverter());
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(JSONBinder.createMapper());
        converters.add(jsonConverter);
        return converters;
    }

    /**
     * Override this method to add custom {@link HttpMessageConverter}s to use with the {@link RequestMappingHandlerAdapter} and the {@link ExceptionHandlerExceptionResolver}. Adding converters to the
     * list turns off the default converters that would otherwise be registered by default. Also see {@link #addDefaultHttpMessageConverters(List)} that can be used to add default message converters.
     * 
     * @param converters
     *            a list to add message converters to; initially an empty list.
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.addAll(createMessageConverters());
    }

    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        ExceptionTrackingHandler exceptionTrackingHandler = new ExceptionTrackingHandler();
        exceptionTrackingHandler.setErrorHandler(this.errorHandler);
        exceptionResolvers.add(exceptionTrackingHandler);
        registerexceptionResolvers(exceptionResolvers);
        ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver = new ExceptionHandlerExceptionResolver();
        exceptionHandlerExceptionResolver.setMessageConverters(createMessageConverters());
        exceptionHandlerExceptionResolver.afterPropertiesSet();
        exceptionResolvers.add(exceptionHandlerExceptionResolver);
        exceptionResolvers.add(new ResponseStatusExceptionResolver());
        exceptionResolvers.add(new DefaultHandlerExceptionResolver());
    }

    protected void registerexceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
    }

    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AnnotationFormArgumentResolver());
    }

    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer config) {
        config.enable();
    }

    @Bean
    public StatusController statusController() {
        return new StatusController();
    }

    @Bean(name = { "URLMappingController" })
    public URLMappingController urlMappingController() {
        return new URLMappingController();
    }

    @Bean
    public ThreadInfoController threadInfoController() {
        return new ThreadInfoController();
    }

    @Bean
    public MemoryUsageController memoryUsageController() {
        return new MemoryUsageController();
    }

    @Bean
    public CheckHealthController checkHealthController() {
        return new CheckHealthController();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public DiskSpaceHealthController diskSpaceHealthController() {
        return new DiskSpaceHealthController();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public DataSourceHealthController dataSourceHealthController() {
        return new DataSourceHealthController();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public MailHealthController mailHealthController() {
        return new MailHealthController();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public BeansEndpointController beansEndpointController() {
        return new BeansEndpointController();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public EnvironmentControlller environmentControlller() {
        return new EnvironmentControlller();
    }

    @Bean
    public MonitortContract monitortContract() {
        return new MonitortContract();
    }

    //
    // @Bean
    // public BacodeController bacodeController() {
    // return new BacodeController();
    // }
    //
    // @Bean
    // public ExceptionInterceptor exceptionInterceptor() {
    // return new ExceptionInterceptor();
    // }
}