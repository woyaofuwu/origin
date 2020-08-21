/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set.TypeCollection;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

import com.asiainfo.veris.crm.order.pub.frame.bcf.set.toolkit.XmlObject;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: TypeCollection.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:36:08 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class TypeCollection extends XmlObject
{

    private static final long serialVersionUID = 1L;

    public static String _tagName = "TypeCollection";

    @SuppressWarnings("unchecked")
    public static TypeCollection unmarshal(Element elem)
    {
        if (elem == null)
        {
            return null;
        }
        TypeCollection __objTypeCollection = new TypeCollection();
        Iterator it = elem.elementIterator();
        while (it.hasNext())
        {
            Object __obj = it.next();
            if (__obj instanceof Element)
            {
                Element __e = (Element) __obj;
                String __name = __e.getName();
                if (__name.equals(FieldEditTypes._tagName))
                {
                    FieldEditTypes __objFieldEditTypes = FieldEditTypes.unmarshal(__e);
                    __objTypeCollection.setFieldEditTypes(__objFieldEditTypes);
                }
                if (__name.equals(DataTypes._tagName))
                {
                    DataTypes __objDataTypes = DataTypes.unmarshal(__e);
                    __objTypeCollection.setDataTypes(__objDataTypes);
                }
            }
        }
        return __objTypeCollection;
    }

    public static TypeCollection unmarshal(File file) throws DocumentException
    {
        return unmarshal(new SAXReader().read(file).getRootElement());
    }

    public static TypeCollection unmarshal(File file, String domParserClass, boolean validation) throws DocumentException
    {
        // Element elem = XmlUtil.getDocRootElement(file, domParserClass,
        // validation);
        Element elem = new SAXReader().read(file).getRootElement();
        return unmarshal(elem);
    }

    public static TypeCollection unmarshal(File file, String saxParserClass, boolean validation, EntityResolver entityResolver, DTDHandler dtdHandler, ErrorHandler errorHandler) throws DocumentException
    {
        // Element elem = XmlUtil.getDocRootElement(file, saxParserClass,
        // validation, entityResolver, dtdHandler, errorHandler);
        Element elem = new SAXReader().read(file).getRootElement();
        return unmarshal(elem);
    }

    public static TypeCollection unmarshal(InputStream in) throws DocumentException
    {
        return unmarshal(new SAXReader().read(in).getRootElement());
    }

    public static TypeCollection unmarshal(InputStream in, String domParserClass, boolean validation) throws DocumentException
    {
        Element elem = new SAXReader().read(in).getRootElement();
        return unmarshal(elem);
    }

    public static TypeCollection unmarshal(InputStream in, String saxParserClass, boolean validation, EntityResolver entityResolver, DTDHandler dtdHandler, ErrorHandler errorHandler) throws DocumentException
    {
        // Element elem = XmlUtil.getDocRootElement(in, saxParserClass,
        // validation, entityResolver, dtdHandler, errorHandler);
        Element elem = new SAXReader().read(in).getRootElement();
        return unmarshal(elem);
    }

    public static TypeCollection unmarshal(Reader reader) throws DocumentException
    {
        return unmarshal(new SAXReader().read(reader).getRootElement());
    }

    public static TypeCollection unmarshal(Reader reader, String saxParserClass, boolean validation, EntityResolver entityResolver, DTDHandler dtdHandler, ErrorHandler errorHandler) throws DocumentException
    {
        // Element elem = XmlUtil.getDocRootElement(reader, saxParserClass,
        // validation, entityResolver, dtdHandler, errorHandler);

        Element elem = new SAXReader().read(reader).getRootElement();
        return unmarshal(elem);
    }

    public static TypeCollection unmarshal(String fileName) throws DocumentException
    {
        return unmarshal(new SAXReader().read(fileName).getRootElement());
    }

    public static TypeCollection unmarshal(String fileName, String domParserClass, boolean validation) throws DocumentException
    {
        // Element elem = XmlUtil.getDocRootElement(fileName, domParserClass,
        // validation);
        Element elem = new SAXReader().read(fileName).getRootElement();
        return unmarshal(elem);
    }

    public static TypeCollection unmarshal(String fileName, String saxParserClass, boolean validation, EntityResolver entityResolver, DTDHandler dtdHandler, ErrorHandler errorHandler) throws DocumentException
    {
        // Element elem = XmlUtil.getDocRootElement(fileName, saxParserClass,
        // validation, entityResolver, dtdHandler, errorHandler);
        Element elem = new SAXReader().read(fileName).getRootElement();
        return unmarshal(elem);
    }

    public static TypeCollection unmarshal(URL url) throws DocumentException
    {
        return unmarshal(new SAXReader().read(url).getRootElement());
    }

    public static TypeCollection unmarshal(URL url, String domParserClass, boolean validation) throws DocumentException
    {
        // Element elem = XmlUtil.getDocRootElement(url, domParserClass,
        // validation);
        Element elem = new SAXReader().read(url).getRootElement();
        return unmarshal(elem);
    }

    public static TypeCollection unmarshal(URL url, String saxParserClass, boolean validation, EntityResolver entityResolver, DTDHandler dtdHandler, ErrorHandler errorHandler) throws DocumentException
    {
        // Element elem = XmlUtil.getDocRootElement(url, saxParserClass,
        // validation, entityResolver, dtdHandler, errorHandler);
        Element elem = new SAXReader().read(url).getRootElement();
        return unmarshal(elem);
    }

    protected FieldEditTypes _objFieldEditTypes;

    protected DataTypes _objDataTypes;

    protected String publicId = "";

    protected String systemId = "";

    public List<Object> _getChildren()
    {
        List<Object> children = new ArrayList<Object>();

        if (this._objFieldEditTypes != null)
        {
            children.add(this._objFieldEditTypes);
        }
        if (this._objDataTypes != null)
        {
            children.add(this._objDataTypes);
        }
        return children;
    }

    public String get_TagName()
    {
        return _tagName;
    }

    public DataTypes getDataTypes()
    {
        return this._objDataTypes;
    }

    public FieldEditTypes getFieldEditTypes()
    {
        return this._objFieldEditTypes;
    }

    public String getPublicId()
    {
        return this.publicId;
    }

    public String getSystemId()
    {
        return this.systemId;
    }

    public Element marshal()
    {
        return null;
    }

    public void setDataTypes(DataTypes obj)
    {
        this._objDataTypes = obj;
        if (obj == null)
        {
            return;
        }
        obj._setParent(this);
    }

    public void setFieldEditTypes(FieldEditTypes obj)
    {
        this._objFieldEditTypes = obj;
        if (obj == null)
        {
            return;
        }
        obj._setParent(this);
    }

    public void setPublicId(String publicId)
    {
        this.publicId = publicId;
    }

    public void setSystemId(String systemId)
    {
        this.systemId = systemId;
    }

}
