/*
 * Copyright (c) 2008-2009 Yahoo! Inc.  All rights reserved.
 * The copyrights to the contents of this file are licensed under the MIT License (http://www.opensource.org/licenses/mit-license.php)
 */


package hudson.plugins.graph.series;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Build;

import org.kohsuke.stapler.DataBoundConstructor;

import org.xml.sax.InputSource;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static javax.xml.xpath.XPathConstants.*;

/**
 * Represents a plot data series configuration from an XML file.
 * 
 * @author Allen Reese
 *
 */
public class XMLSeries extends Series 
{
    private String xpath;
    
    private String label;
    
    private String nodeType;
    
    /**
     * 
     * @param file xml file to take data from
     * @param xpath xpath expression to obtain series point
     * @param nodeType 
     * @param url
     * @param label 
     */
    @DataBoundConstructor
    public XMLSeries(String file, String xpath, String nodeType, String url, String label)
    {
        super(url, file);
        
        this.url = url;
        this.xpath = xpath;
        this.label = label;
        this.nodeType = nodeType;
    }

    public String getType()
    {
        return Type.XML.toString();
    }

    public String getXpath()
    {
        return xpath;
    }

    public String getLabel()
    {
        return label;
    }

    public String getNodeType()
    {
        return nodeType;
    }

    private QName getNodeQName()
    {
        return new QName("http://www.w3.org/1999/XSL/Transform", nodeType);
    }
    
    private String nodeToString(Object node)
    {
        if (BOOLEAN.equals(getNodeQName()))
        {
            return (Boolean) node ? "1" : "0";
        }
        
        return node.toString().trim();
    }
    
    private SeriesValue createPlotPoint(String value, String label, AbstractBuild build)
    {
        String localUrl = getUrl(label, 0);
        
        return new SeriesValue(value, localUrl, label, build);
    }

    @Override
    public List<SeriesValue> loadSeries(AbstractBuild build) throws IOException
    {
        List<SeriesValue> values = new ArrayList<SeriesValue>();
        
        FilePath seriesFile = new FilePath(build.getWorkspace(), getFile());
        
        try
        {
            if (!seriesFile.exists())
            {
                return values;
            }
            
            XPathExpression expression = XPathFactory.newInstance().newXPath().compile(xpath);
            
            InputSource source = null;
            
            try
            {
                source = new InputSource(seriesFile.read());
                
                Object xml = expression.evaluate(source, getNodeQName());
                
                if (NODESET.equals(getNodeQName()))
                {
                    NodeList list = (NodeList) xml;
                    
                    for (int nodeIndex = 0; nodeIndex < list.getLength(); nodeIndex++)
                    {
                        Node node = list.item(nodeIndex);
                        
                        String localLabel = node.getLocalName(), value = node.getTextContent();
                        
                        if (localLabel != null && value != null)
                        {
                            values.add(createPlotPoint(value.trim(), localLabel, build));
                        }
                    }
                }
                else
                {
                    values.add(createPlotPoint(nodeToString(xml), label, build));
                }
            }
            finally
            {
                if (source != null)
                {
                    source.getByteStream().close();
                }
            }
            
            return values;
        }
        catch (XPathExpressionException e)
        {
            throw new RuntimeException(e);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("XMLSeries{");
        
        sb.append("file=").append(file).append(", ");
        sb.append("url=").append(url).append(", ");
        sb.append("xpath=").append(xpath).append(", ");
        sb.append("label=").append(label).append(", ");
        sb.append("nodeType=").append(nodeType);
        
        return sb.append("}").toString();
    }
}
