/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set.TypeCollection;

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
 * @ClassName: FieldEditTypes.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:35:52 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class FieldEditTypes extends XmlObject
{

    private static final long serialVersionUID = 1L;

    public static String _tagName = "FieldEditTypes";

    @SuppressWarnings("unchecked")
    public static FieldEditTypes unmarshal(Element elem)
    {
        if (elem == null)
        {
            return null;
        }
        FieldEditTypes __objFieldEditTypes = new FieldEditTypes();
        Iterator it = elem.elementIterator();
        while (it.hasNext())
        {
            Object __obj = it.next();
            if (__obj instanceof Element)
            {
                Element __e = (Element) __obj;
                String __name = __e.getName();
                if (__name.equals(EditType._tagName))
                {
                    EditType __objEditType = EditType.unmarshal(__e);
                    __objFieldEditTypes.addEditType(__objEditType);
                }
            }
        }
        return __objFieldEditTypes;
    }

    protected List<EditType> _objEditType = new ArrayList<EditType>();

    public List<List<EditType>> _getChildren()
    {
        List<List<EditType>> children = new ArrayList<List<EditType>>();
        if ((this._objEditType != null) && (this._objEditType.size() > 0))
        {
            children.add(this._objEditType);
        }
        return children;
    }

    public boolean addEditType(Collection<EditType> coEditType)
    {
        if (coEditType == null)
        {
            return false;
        }
        Iterator<EditType> it = coEditType.iterator();
        while (it.hasNext())
        {
            Object obj = it.next();
            if ((obj != null) && (obj instanceof XmlObject))
            {
                ((XmlObject) obj)._setParent(this);
            }
        }
        return this._objEditType.addAll(coEditType);
    }

    public boolean addEditType(EditType obj)
    {
        if (obj == null)
        {
            return false;
        }
        obj._setParent(this);
        return this._objEditType.add(obj);
    }

    public void clearEditTypeList()
    {
        this._objEditType.clear();
    }

    public String get_TagName()
    {
        return _tagName;
    }

    public EditType[] getEditType()
    {
        return this._objEditType.toArray(new EditType[0]);
    }

    public EditType getEditType(int index)
    {
        return ((EditType) this._objEditType.get(index));
    }

    public int getEditTypeCount()
    {
        return this._objEditType.size();
    }

    public List<EditType> getEditTypeList()
    {
        return Collections.unmodifiableList(this._objEditType);
    }

    public boolean isNoEditType()
    {
        return (this._objEditType.size() == 0);
    }

    public Element marshal()
    {
        return null;
    }

    public boolean removeEditType(EditType obj)
    {
        return this._objEditType.remove(obj);
    }

    public EditType removeEditType(int index)
    {
        return ((EditType) this._objEditType.remove(index));
    }

    public void setEditType(EditType[] objArray)
    {
        if ((objArray == null) || (objArray.length == 0))
        {
            this._objEditType.clear();
        }
        else
        {
            this._objEditType = new ArrayList<EditType>(Arrays.asList(objArray));
            for (int i = 0; i < objArray.length; ++i)
            {
                if (objArray[i] != null)
                    objArray[i]._setParent(this);
            }
        }
    }

    public void setEditType(int index, EditType obj)
    {
        if (obj == null)
        {
            removeEditType(index);
        }
        else
        {
            this._objEditType.set(index, obj);
            obj._setParent(this);
        }
    }

}
