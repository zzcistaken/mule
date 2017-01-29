/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.util;

import static org.mule.runtime.core.api.Event.getCurrentEvent;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.message.OutputHandler;
import org.mule.runtime.core.util.xmlsecurity.XMLSecureFactories;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.dom4j.io.DOMReader;
import org.dom4j.io.DOMWriter;
import org.dom4j.io.DocumentSource;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * These only depend on standard (JSE) XML classes and are used by Spring config code. For a more extensive (sub-)class, see the
 * XMLUtils class in the XML module.
 */
public class XMLUtils {

  public static String elementToString(Element e) {
    StringBuilder buf = new StringBuilder();
    buf.append(e.getTagName()).append("{");
    for (int i = 0; i < e.getAttributes().getLength(); i++) {
      if (i > 0) {
        buf.append(", ");
      }
      Node n = e.getAttributes().item(i);
      buf.append(attributeName((Attr) n)).append("=").append(n.getNodeValue());
    }
    buf.append("}");
    return buf.toString();
  }

  public static boolean isLocalName(Element element, String name) {
    return element.getLocalName().equals(name);
  }

  public static String attributeName(Attr attribute) {
    String name = attribute.getLocalName();
    if (null == name) {
      name = attribute.getName();
    }
    return name;
  }

  public static String getTextChild(Element element) {
    NodeList children = element.getChildNodes();
    String value = null;
    for (int i = 0; i < children.getLength(); ++i) {
      Node child = children.item(i);
      if (child.getNodeType() == Node.TEXT_NODE) {
        if (null != value) {
          throw new IllegalStateException("Element " + elementToString(element) + " has more than one text child.");
        } else {
          value = child.getNodeValue();
        }
      }
    }
    return value;
  }

  // TODO(pablo.kraan): xml - fix javadoc
  // TODO(pablo.kraan): xml - review which part must be API
  /**
   * Converts a payload to a {@link org.w3c.dom.Document} representation.
   * <p>
   * Reproduces the behavior from {@link XMLUtils#toDocument(Object, MuleContext)} which works
   * converting to {@link org.dom4j.Document}.
   *
   * @param payload the payload to convert.
   * @return a document from the payload or null if the payload is not a valid XML document.
   */
  public static org.w3c.dom.Document toW3cDocument(Object payload) throws Exception {
    if (payload instanceof org.dom4j.Document) {
      DOMWriter writer = new DOMWriter();
      org.w3c.dom.Document w3cDocument = writer.write((org.dom4j.Document) payload);

      return w3cDocument;
    } else if (payload instanceof org.w3c.dom.Document) {
      return (org.w3c.dom.Document) payload;
    } else if (payload instanceof org.xml.sax.InputSource) {
      return parseXML((InputSource) payload);
    } else if (payload instanceof javax.xml.transform.Source || payload instanceof javax.xml.stream.XMLStreamReader) {
      DOMResult result = new DOMResult();
      Transformer idTransformer = getTransformer();
      Source source = (payload instanceof Source) ? (Source) payload : toXmlSource(null, true, payload);
      idTransformer.transform(source, result);
      return (Document) result.getNode();
    } else if (payload instanceof java.io.InputStream) {
      InputStreamReader input = new InputStreamReader((InputStream) payload);
      return parseXML(input);
    } else if (payload instanceof String) {
      Reader input = new StringReader((String) payload);

      return parseXML(input);
    } else if (payload instanceof byte[]) {
      // TODO Handle encoding/charset somehow
      Reader input = new StringReader(new String((byte[]) payload));
      return parseXML(input);
    } else if (payload instanceof File) {
      Reader input = new FileReader((File) payload);
      return parseXML(input);
    } else {
      return null;
    }
  }

  private static org.w3c.dom.Document parseXML(Reader source) throws Exception {
    return parseXML(new InputSource(source));
  }

  private static org.w3c.dom.Document parseXML(InputSource source) throws Exception {
    DocumentBuilderFactory factory = XMLSecureFactories.createDefault().getDocumentBuilderFactory();
    return factory.newDocumentBuilder().parse(source);
  }

  /**
   * @return a new XSLT transformer
   * @throws TransformerConfigurationException if no TransformerFactory can be located in the runtime environment.
   */
  public static Transformer getTransformer() throws TransformerConfigurationException {
    TransformerFactory tf;
    try {
      tf = TransformerFactory.newInstance();
    } catch (TransformerFactoryConfigurationError e) {
      throw new TransformerConfigurationException("Unable to instantiate a TransformerFactory");
    }

    return tf.newTransformer();
  }

  /**
   * Converts a DOM to an XML string.
   *
   * @param dom the dome object to convert
   * @return A string representation of the document
   */
  public static String toXml(Document dom) {
    return new DOMReader().read(dom).asXML();
  }

  /**
   * Convert our object to a Source type efficiently.
   */
  public static javax.xml.transform.Source toXmlSource(javax.xml.stream.XMLInputFactory xmlInputFactory, boolean useStaxSource,
                                                       Object src)
      throws Exception {
    if (src instanceof javax.xml.transform.Source) {
      return (Source) src;
    } else if (src instanceof byte[]) {
      ByteArrayInputStream stream = new ByteArrayInputStream((byte[]) src);
      return new StreamSource(stream);
    } else if (src instanceof InputStream) {
      return new StreamSource((InputStream) src);
    } else if (src instanceof String) {
      //if (useStaxSource) {
      //  return new StaxSource(xmlInputFactory.createXMLStreamReader(new StringReader((String) src)));
      //} else {
      return new StreamSource(new StringReader((String) src));
      //}
    } else if (src instanceof org.dom4j.Document) {
      return new DocumentSource((org.dom4j.Document) src);
    } else if (src instanceof org.xml.sax.InputSource) {
      return new SAXSource((InputSource) src);
    }
    // TODO MULE-3555
    else
    //  if (src instanceof XMLStreamReader) {
    //  return toXmlSource((XMLStreamReader) src);
    //} else
    if (src instanceof org.w3c.dom.Document || src instanceof org.w3c.dom.Element) {
      return new DOMSource((org.w3c.dom.Node) src);
    } else
    //  if (src instanceof DelayedResult) {
    //  DelayedResult result = ((DelayedResult) src);
    //  DOMResult domResult = new DOMResult();
    //  result.write(domResult);
    //  return new DOMSource(domResult.getNode());
    //} else
    if (src instanceof OutputHandler) {
      OutputHandler handler = ((OutputHandler) src);
      ByteArrayOutputStream output = new ByteArrayOutputStream();

      handler.write(getCurrentEvent(), output);

      return new StreamSource(new ByteArrayInputStream(output.toByteArray()));
    } else {
      return null;
    }
  }
}
