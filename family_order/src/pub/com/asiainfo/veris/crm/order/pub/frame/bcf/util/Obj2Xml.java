
package com.asiainfo.veris.crm.order.pub.frame.bcf.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

/**
 * 把任意类型的数据组合(String,IData,IDataset)组装成xml格式的数据 example: 注意路径：E:/test.xml
 * 
 * @author hy
 */
public final class Obj2Xml
{

    private static Element addIData(Element element, IData idata)
    {

        Set it = idata.entrySet();
        if (it != null)
        {
            Iterator iterator = it.iterator();
            while (iterator.hasNext())
            {
                Map.Entry entry = (Entry) iterator.next();
                String key = (String) entry.getKey();
                Object value = entry.getValue();
                Element newElement = element.addElement(key);
                toXml(newElement, value);
            }
        }
        return element;
    }

    private static Element addIDataset(Element element, IDataset idataset)
    {

        for (int size = idataset.size(), i = 0; i < size; i++)
        {
            Object childObj = idataset.get(i);
            Element newElement = element.addElement("ROW_" + String.valueOf(i + 1));
            toXml(newElement, childObj);
        }
        return element;
    }

    private static Element addString(Element element, String s)
    {

        element.addAttribute("Value", s);
        return element;
    }

    public final static void toFile(String filePath, String fileName, Object obj)
    {

        Document document = DocumentHelper.createDocument();
        document.setXMLEncoding("utf-8");
        Element element = document.addElement("Object");

        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory())
        {
            dir.mkdirs();
        }

        toXml(element, obj);

        try
        {

            String sPathFileName = filePath + fileName;

            OutputFormat format = OutputFormat.createPrettyPrint();

            org.dom4j.io.XMLWriter output = new org.dom4j.io.XMLWriter(new FileOutputStream(sPathFileName), format);
            output.write(document);

            output.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void toXml(Element element, Object obj)
    {

        if (obj instanceof String)
        {
            String o = (String) obj;
            String s = String.valueOf(o.length());
            element.addAttribute("type", "String");
            element.addAttribute("length", s);
            addString(element, o);
        }
        else if (obj instanceof IData)
        {
            IData o = (IData) obj;
            String s = String.valueOf(o.size());
            element.addAttribute("type", "IData");
            element.addAttribute("count", s);
            addIData(element, o);
        }
        else if (obj instanceof IDataset)
        {
            IDataset o = (IDataset) obj;
            String s = String.valueOf(o.size());
            element.addAttribute("type", "IDataset");
            element.addAttribute("count", s);
            addIDataset(element, o);
        }
    }
}
