package org.matrix.framework.core.xml;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import org.matrix.framework.core.log.LoggerFactory;
import org.w3c.dom.Document;

public final class XMLBinder<T> {
    private JAXBContext context;
    private Class<T> beanClass;
    private boolean hasXMLRootElementAnnotation;

    public static <T> XMLBinder<T> binder(Class<T> beanClass) {
        return new XMLBinder<T>(beanClass);
    }

    private XMLBinder(Class<T> beanClass) {
        try {
            this.beanClass = beanClass;
            this.hasXMLRootElementAnnotation = beanClass.isAnnotationPresent(XmlRootElement.class);
            this.context = JAXBContext.newInstance(new Class[] { beanClass });
        } catch (JAXBException e) {
            LoggerFactory.trace(XMLBinder.class, e);
            throw new XMLException(e);
        }
    }

    public String toXML(T bean) {
        try {
            Marshaller marshaller = this.context.createMarshaller();
            marshaller.setProperty("jaxb.fragment", Boolean.TRUE);
            StringWriter writer = new StringWriter();
            if (this.hasXMLRootElementAnnotation)
                marshaller.marshal(bean, writer);
            else {
                marshaller.marshal(new JAXBElement(new QName("", this.beanClass.getSimpleName()),
                        this.beanClass, bean), writer);
            }
            return writer.toString();
        } catch (JAXBException e) {
            LoggerFactory.trace(XMLBinder.class, e);
            throw new XMLException(e);
        }
    }

    public T fromXML(String xml) {
        try {
            Unmarshaller unmarshaller = this.context.createUnmarshaller();
            Document document = new XMLParser().setNamespaceAware(true).parse(xml);
            JAXBElement<T> element = unmarshaller.unmarshal(document, this.beanClass);
            return element.getValue();
        } catch (JAXBException e) {
            LoggerFactory.trace(XMLBinder.class, e);
            throw new XMLException(e);
        }
    }
}