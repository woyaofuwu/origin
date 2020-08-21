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
 * @ClassName: DataTypes.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:35:25 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class DataTypes extends XmlObject
{

    private static final long serialVersionUID = 1L;

    public static String _tagName = "DataTypes";

    @SuppressWarnings("unchecked")
    public static DataTypes unmarshal(Element elem)
    {
        if (elem == null)
        {
            return null;
        }
        DataTypes __objDataTypes = new DataTypes();
        Iterator it = elem.elementIterator();
        while (it.hasNext())
        {
            Object __obj = it.next();
            if (__obj instanceof Element)
            {
                Element __e = (Element) __obj;
                String __name = __e.getName();
                if (__name.equals(DType._tagName))
                {
                    DType __objDType = DType.unmarshal(__e);
                    __objDataTypes.addDType(__objDType);
                }
            }
        }
        return __objDataTypes;
    }

    protected List<DType> _objDType = new ArrayList<DType>();

    public List<List<DType>> _getChildren()
    {
        List<List<DType>> children = new ArrayList<List<DType>>();
        if ((this._objDType != null) && (this._objDType.size() > 0))
        {
            children.add(this._objDType);
        }
        return children;
    }

    public boolean addDType(Collection<DType> coDType)
    {
        if (coDType == null)
        {
            return false;
        }
        Iterator<DType> it = coDType.iterator();
        while (it.hasNext())
        {
            Object obj = it.next();
            if ((obj != null) && (obj instanceof XmlObject))
                ((XmlObject) obj)._setParent(this);
        }
        return this._objDType.addAll(coDType);
    }

    public boolean addDType(DType obj)
    {
        if (obj == null)
        {
            return false;
        }
        obj._setParent(this);
        return this._objDType.add(obj);
    }

    public void clearDTypeList()
    {
        this._objDType.clear();
    }

    public String get_TagName()
    {
        return _tagName;
    }

    public DType[] getDType()
    {
        return this._objDType.toArray(new DType[0]);
    }

    public DType getDType(int index)
    {
        return ((DType) this._objDType.get(index));
    }

    public int getDTypeCount()
    {
        return this._objDType.size();
    }

    public List<DType> getDTypeList()
    {
        return Collections.unmodifiableList(this._objDType);
    }

    public boolean isNoDType()
    {
        return (this._objDType.size() == 0);
    }

    public Element marshal()
    {
        return null;
    }

    public boolean removeDType(DType obj)
    {
        return this._objDType.remove(obj);
    }

    public DType removeDType(int index)
    {
        return ((DType) this._objDType.remove(index));
    }

    public void setDType(DType[] objArray)
    {
        if ((objArray == null) || (objArray.length == 0))
        {
            this._objDType.clear();
        }
        else
        {
            this._objDType = new ArrayList<DType>(Arrays.asList(objArray));
            for (int i = 0; i < objArray.length; ++i)
            {
                if (objArray[i] != null)
                    objArray[i]._setParent(this);
            }
        }
    }

    public void setDType(int index, DType obj)
    {
        if (obj == null)
        {
            removeDType(index);
        }
        else
        {
            this._objDType.set(index, obj);
            obj._setParent(this);
        }
    }

}
