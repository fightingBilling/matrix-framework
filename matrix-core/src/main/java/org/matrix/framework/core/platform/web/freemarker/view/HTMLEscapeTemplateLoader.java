package org.matrix.framework.core.platform.web.freemarker.view;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.matrix.framework.core.util.IOUtils;

import freemarker.cache.TemplateLoader;

/**
 * 为模板加上<#escape x as x?html>,这让模板中变量输出的特殊HTML字符用实体的替代.
 * 详情参阅freemarker文档(2.3.19)的第一部分3.3.11及第四部分2.8.1.
 * 
 * @author pankai
 *
 */
public class HTMLEscapeTemplateLoader implements TemplateLoader {

    /**
     * 应用在tomcat中的时候,实际上的实现类为FileTemplateLoader,matrix中指定在ROOT/WEB-INF/templates/目录下寻找模板
     */
    private final TemplateLoader templateLoader;

    public HTMLEscapeTemplateLoader(TemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    @Override
    public Object findTemplateSource(String name) throws IOException {
        return this.templateLoader.findTemplateSource(name);
    }

    @Override
    public long getLastModified(Object templateSource) {
        return this.templateLoader.getLastModified(templateSource);
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        Reader reader = this.templateLoader.getReader(templateSource, encoding);
        StringBuilder builder = new StringBuilder("<#escape x as x?html>");
        builder.append(IOUtils.text(reader));
        builder.append("</#escape>");
        return new StringReader(builder.toString());
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
        this.templateLoader.closeTemplateSource(templateSource);
    }

}
