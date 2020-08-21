/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set.toolkit;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.dom4j.Attribute;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: XmlAttribute.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:36:38 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class XmlAttribute implements Serializable
{

    private static final long serialVersionUID = 1L;

    public static XmlAttribute unmarshal(Attribute attribute)
    {
        if (attribute == null)
        {
            return null;
        }
        return new XmlAttribute(attribute.getName(), attribute.getValue());
    }

    private String name;

    private String type;

    private String defaultType;

    private String defaultValue;

    private String value;

    public XmlAttribute(String paramString)
    {
        this.name = paramString;
    }

    public XmlAttribute(String paramString1, String paramString2)
    {
        this.name = paramString1;
        this.value = paramString2;
    }

    public XmlAttribute(String paramString1, String paramString2, String paramString3, String paramString4)
    {
        this.name = paramString1;
        this.type = paramString2;
        this.defaultType = paramString3;
        this.defaultValue = paramString4;
    }

    public String[] getChoices()
    {
        if (this.type.indexOf(40) == -1)
        {
            return new String[0];
        }
        List<String> localArrayList = new ArrayList<String>();
        StringTokenizer localStringTokenizer = new StringTokenizer(this.type, "(| ");
        while (localStringTokenizer.hasMoreTokens())
        {
            localArrayList.add(localStringTokenizer.nextToken());
        }
        return ((String[]) localArrayList.toArray(new String[0]));
    }

    public String getDefaultType()
    {
        return this.defaultType;
    }

    public String getDefaultValue()
    {
        return this.defaultValue;
    }

    public String getName()
    {
        return this.name;
    }

    public String getType()
    {
        return this.type;
    }

    public String getValue()
    {
        return this.value;
    }

    public BigDecimal getValueBigDecimal() throws NumberFormatException
    {
        return new BigDecimal(this.value);
    }

    public BigInteger getValueBigInteger() throws NumberFormatException
    {
        return new BigInteger(this.value);
    }

    public boolean getValueBoolean()
    {
        return new Boolean(this.value).booleanValue();
    }

    public double getValueDouble() throws NumberFormatException
    {
        return ((this.value == null) ? 0.0D : Double.parseDouble(this.value));
    }

    public float getValueFloat() throws NumberFormatException
    {
        return ((this.value == null) ? 0.0F : Float.parseFloat(this.value));
    }

    public int getValueInt() throws NumberFormatException
    {
        return ((this.value == null) ? 0 : Integer.parseInt(this.value));
    }

    public long getValueLong() throws NumberFormatException
    {
        return ((this.value == null) ? 0L : Long.parseLong(this.value));
    }

    public short getValueShort() throws NumberFormatException
    {
        return ((this.value == null) ? 0 : Short.parseShort(this.value));
    }

    public boolean isEnumerated()
    {
        return (this.type.indexOf(40) != -1);
    }

    public Attribute marshal()
    {
        return null;
    }

    public void setValue(Attribute attribute)
    {
        this.value = (attribute == null) ? null : attribute.getValue();
    }

    public void setValue(String paramString)
    {
        this.value = paramString;
    }

    public void setValueBigDecimal(BigDecimal paramBigDecimal)
    {
        this.value = ((paramBigDecimal == null) ? null : paramBigDecimal.toString());
    }

    public void setValueBigInteger(BigInteger paramBigInteger)
    {
        this.value = ((paramBigInteger == null) ? null : paramBigInteger.toString());
    }

    public void setValueBoolean(boolean paramBoolean)
    {
        this.value = new Boolean(paramBoolean).toString();
    }

    public void setValueDouble(double paramDouble)
    {
        this.value = Double.toString(paramDouble);
    }

    public void setValueFloat(float paramFloat)
    {
        this.value = Float.toString(paramFloat);
    }

    public void setValueInt(int paramInt)
    {
        this.value = Integer.toString(paramInt);
    }

    public void setValueLong(long paramLong)
    {
        this.value = Long.toString(paramLong);
    }

    public void setValueShort(short paramShort)
    {
        this.value = Short.toString(paramShort);
    }
}
