package org.matrix.framework.core.xml;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xalan.processor.TransformerFactoryImpl;
import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.util.StringUtils;
import org.slf4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public final class DOMUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DOMUtils.class);

    private static final TransformerFactory TRANSFORMER_FACTORY = new TransformerFactoryImpl();

    public static String text(Node node) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        print(node, bytes);
        return new String(bytes.toByteArray(), Charset.forName("UTF-8"));
    }

    private static void print(Node node, OutputStream out) {
        try {
            Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty("omit-xml-declaration", "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty("encoding", "UTF-8");
            transformer.transform(new DOMSource(node), new StreamResult(out));
        } catch (TransformerException e) {
            LoggerFactory.trace(DOMUtils.class, e);
            throw new XMLException(e);
        }
    }

    public static List<Element> childElements(Node node) {
        List<Element> result = new ArrayList<Element>();
        NodeList childNodes = node.getChildNodes();
        int length = childNodes.getLength();
        for (int i = 0; i < length; i++) {
            Node child = childNodes.item(i);
            if ((child instanceof Element)) {
                result.add((Element) child);
            }
        }
        return result;
    }

    public static List<Node> children(Node node) {
        List<Node> result = new ArrayList<Node>();
        NodeList childNodes = node.getChildNodes();
        int length = childNodes.getLength();
        for (int i = 0; i < length; i++) {
            Node child = childNodes.item(i);
            if ((child instanceof Text)) {
                Text text = (Text) child;
                if (StringUtils.hasText(text.getWholeText()))
                    result.add(text);
            } else {
                result.add(child);
            }

        }
        return result;
    }

    public static List<Attr> attributes(Node node) {
        List<Attr> result = new ArrayList<Attr>();
        NamedNodeMap attributes = node.getAttributes();
        int length = attributes.getLength();
        for (int i = 0; i < length; i++) {
            Attr attr = (Attr) attributes.item(i);
            result.add(attr);
        }
        return result;
    }

    public static void setText(Element element, String text) {
        NodeList childNodes = element.getChildNodes();
        if (childNodes.getLength() > 1)
            throw new XMLException("can not set text for element " + text(element));
        if (childNodes.getLength() == 0) {
            Text textNode = element.getOwnerDocument().createTextNode(text);
            element.appendChild(textNode);
        } else {
            Node textNode = element.getFirstChild();
            if (!(textNode instanceof Text))
                throw new XMLException("can not set text for element " + text(element));
            textNode.setTextContent(text);
        }
    }

    public static String getText(Element element) {
        NodeList children = element.getChildNodes();
        if (children.getLength() == 0)
            return element.getTextContent();
        if (children.getLength() == 1) {
            Node firstChild = element.getFirstChild();
            if ((firstChild instanceof Text))
                return firstChild.getTextContent();
        }

        LOGGER.error("target element is not a text element {}", new Object[] { text(element) });
        throw new XMLException("target element is not a text element " + text(element));
    }

    /**
     * 把一个具有完整格式的XML文档序列化为字符串
     * @param document
     * @return
     */
//    public static String prettyFormat(Document document) {
//        try {
//            OutputFormat format = new OutputFormat(document);
//            format.setIndenting(true);
//            format.setOmitXMLDeclaration(true);
//            format.setIndent(2);
//            Writer out = new StringWriter();
//            XMLSerializer serializer = new XMLSerializer(out, format);
//            serializer.serialize(document);
//            return out.toString();
//        } catch (IOException e) {
//            LoggerFactory.trace(DOMUtils.class, e);
//            throw new XMLException(e);
//        }
//    }
//    
//    @Test
//    public void test(){
//        Document document = null;
//        DocumentBuilderFactory factory;
//        DocumentBuilder docBuilder;      
//        FileInputStream in;
//        //The xml file.
//        String fileName = "d://logback.xml";
//        try {  
//            in = new FileInputStream(fileName);  
//            factory = DocumentBuilderFactory.newInstance();  
//            factory.setValidating(false);  
//            docBuilder = factory.newDocumentBuilder();  
//            document = docBuilder.parse(in);           
//        } catch (Exception e) {  
//           System.out.println(ExceptionUtils.stackTrace(e));
//           
//        }  
//        System.out.println("========================");
//        System.out.println(DOMUtils.prettyFormat(document));
//    }
}