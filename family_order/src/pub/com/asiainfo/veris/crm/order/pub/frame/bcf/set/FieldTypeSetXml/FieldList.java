/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set.FieldTypeSetXml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

import com.asiainfo.veris.crm.order.pub.frame.bcf.set.toolkit.XmlObject;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: FieldList.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:37:43 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class FieldList extends XmlObject
{

    private static final long serialVersionUID = 1L;

    public static String _tagName = "FieldList";

    @SuppressWarnings("unchecked")
    public static FieldList unmarshal(Element elem)
    {
        if (elem == null)
        {
            return null;
        }
        FieldList __objFieldList = new FieldList();
        Iterator it = elem.elementIterator();
        while (it.hasNext())
        {
            Object __obj = it.next();
            if (__obj instanceof Element)
            {
                Element __e = (Element) __obj;
                String __name = __e.getName();
                if (__name.equals(Field._tagName))
                {
                    Field __objField = Field.unmarshal(__e);
                    __objFieldList.addField(__objField);
                }
            }
        }
        return __objFieldList;
    }

    protected List<Field> _objField = new ArrayList<Field>();

    public List<List<Field>> _getChildren()
    {
        List<List<Field>> children = new ArrayList<List<Field>>();
        if ((this._objField != null) && (this._objField.size() > 0))
        {
            children.add(this._objField);
        }
        return children;
    }

    public boolean addField(Collection<Field> coField)
    {
        if (coField == null)
        {
            return false;
        }
        Iterator<Field> it = coField.iterator();
        while (it.hasNext())
        {
            Object obj = it.next();
            if ((obj != null) && (obj instanceof XmlObject))
                ((XmlObject) obj)._setParent(this);
        }
        return this._objField.addAll(coField);
    }

    public boolean addField(Field obj)
    {
        if (obj == null)
        {
            return false;
        }
        return this._objField.add(obj);
    }

    public void clearFieldList()
    {
        this._objField.clear();
    }

    public String get_TagName()
    {
        return _tagName;
    }

    public Field[] getField()
    {
        return this._objField.toArray(new Field[0]);
    }

    public Field getField(int index)
    {
        return ((Field) this._objField.get(index));
    }

    public int getFieldCount()
    {
        return this._objField.size();
    }

    public List<Field> getFieldList()
    {
        return Collections.unmodifiableList(this._objField);
    }

    public boolean isNoField()
    {
        return (this._objField.size() == 0);
    }

    public Element marshal()
    {
        return null;
    }

    public boolean removeField(Field obj)
    {
        return this._objField.remove(obj);
    }

    public Field removeField(int index)
    {
        return this._objField.remove(index);
    }

    public void setField(Field[] objArray)
    {
        if ((objArray == null) || (objArray.length == 0))
        {
            this._objField.clear();
        }
        else
        {
            this._objField = new ArrayList<Field>(Arrays.asList(objArray));
        }
    }

    public void setField(int index, Field obj)
    {
        if (obj == null)
        {
            removeField(index);
        }
        else
        {
            this._objField.set(index, obj);
        }
    }

}
