package org.matrix.framework.core.xml;

import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.XMLStreamWriter2;
import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.util.StringUtils;

import com.ctc.wstx.stax.WstxOutputFactory;

public final class XMLBuilder {

    @SuppressWarnings("unused")
    private static final String NEW_LINE = "\n";
    @SuppressWarnings("unused")
    private static final String INDENT = "    ";
    final StringWriter writer;
    XMLStreamWriter2 stream;
    Node lastNode;
    boolean indented = false;
    int level = 0;

    private XMLBuilder() {
        this.writer = new StringWriter();
        XMLOutputFactory factory = new WstxOutputFactory();
        try {
            this.stream = (XMLStreamWriter2) factory.createXMLStreamWriter(this.writer);
        } catch (XMLStreamException e) {
            LoggerFactory.trace(XMLBuilder.class, e);
            throw new XMLException(e);
        }
    }

    public static XMLBuilder indentedXMLBuilder() {
        XMLBuilder builder = new XMLBuilder();
        builder.indented = true;
        return builder;
    }

    public static XMLBuilder simpleBuilder() {
        return new XMLBuilder();
    }

    public String toXML() {
        try {
            this.stream.writeEndDocument();
        } catch (XMLStreamException e) {
            LoggerFactory.trace(XMLBuilder.class, e);
            throw new XMLException(e);
        }
        return this.writer.toString();
    }

    public XMLBuilder xmlDeclaration(String encoding, String version) {
        try {
            this.stream.writeStartDocument(encoding, version);
            return this;
        } catch (XMLStreamException e) {
            LoggerFactory.trace(XMLBuilder.class, e);
            throw new XMLException(e);
        }
    }

    public XMLBuilder startElement(String localName) {
        try {
            indentForStartElement();
            this.stream.writeStartElement(localName);
            if (this.indented) {
                this.lastNode = Node.START_ELEMENT;
                this.level += 1;
            }
            return this;
        } catch (XMLStreamException e) {
            LoggerFactory.trace(XMLBuilder.class, e);

            throw new XMLException(e);
        }
    }

    public XMLBuilder emptyElement(String localName) {
        try {
            indentForStartElement();
            this.stream.writeEmptyElement(localName);
            finishIndentingForEndElement();
            return this;
        } catch (XMLStreamException e) {
            LoggerFactory.trace(XMLBuilder.class, e);
            throw new XMLException(e);
        }
    }

    private void finishIndentingForEndElement() {
        if (this.indented)
            this.lastNode = Node.END_ELEMENT;
    }

    private void indentForStartElement() throws XMLStreamException {
        if ((this.indented) && ((Node.START_ELEMENT.equals(this.lastNode)) || (Node.END_ELEMENT.equals(this.lastNode)))) {
            indent();
        }
    }

    public XMLBuilder endElement() {
        try {
            if (this.indented) {
                this.level -= 1;
                if (Node.END_ELEMENT.equals(this.lastNode))
                    indent();
            }
            this.stream.writeEndElement();
            finishIndentingForEndElement();
            return this;
        } catch (XMLStreamException e) {
            LoggerFactory.trace(XMLBuilder.class, e);
            throw new XMLException(e);
        }
    }

    static enum Node {
        START_ELEMENT, TEXT, END_ELEMENT;
    }

    private void indent() throws XMLStreamException {
        this.stream.writeCharacters("\n");
        for (int i = 0; i < this.level; i++)
            this.stream.writeCharacters("    ");
    }

    public XMLBuilder attribute(String localName, String value) {
        try {
            this.stream.writeAttribute(localName, value);
            return this;
        } catch (XMLStreamException e) {
            LoggerFactory.trace(XMLBuilder.class, e);
            throw new XMLException(e);
        }
    }

    public XMLBuilder cdata(String data) {
        if (!StringUtils.hasText(data))
            return this;
        try {
            this.stream.writeCData(data);
            if (this.indented)
                this.lastNode = Node.TEXT;
            return this;
        } catch (XMLStreamException e) {
            LoggerFactory.trace(XMLBuilder.class, e);
            throw new XMLException(e);
        }
    }

    public XMLBuilder text(String text) {
        if (!StringUtils.hasText(text))
            return this;
        try {
            this.stream.writeCharacters(text);
            if (this.indented)
                this.lastNode = Node.TEXT;
            return this;
        } catch (XMLStreamException e) {
            LoggerFactory.trace(XMLBuilder.class, e);
            throw new XMLException(e);
        }
    }

    public XMLBuilder textElement(String localName, String text) {
        return startElement(localName).text(text).endElement();
    }

    public XMLBuilder rawXML(String xml) {
        try {
            indentForStartElement();
            this.stream.writeRaw(xml);
            finishIndentingForEndElement();
        } catch (XMLStreamException e) {
            LoggerFactory.trace(XMLBuilder.class, e);
            throw new XMLException(e);
        }
        return this;
    }

}
