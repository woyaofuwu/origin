/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DataTypeDBImpl.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 上午11:09:07 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class DataTypeDBImpl implements DataTypeDB
{

    private String name;

    private String maxLength;

    private String decimal;

    private String javaDataType;

    public DataTypeDBImpl()
    {
        this.name = "";
        this.maxLength = "";
        this.decimal = "";
    }

    public DataTypeDBImpl(String aName)
    {
        this.name = aName;
        this.maxLength = "";
        this.decimal = "";
    }

    public DataTypeDBImpl(String aName, String aMaxLen)
    {
        this.name = aName;
        this.maxLength = aMaxLen;
        this.decimal = "";
    }

    public DataTypeDBImpl(String aName, String aMaxLen, String aDec)
    {
        this.name = aName;
        this.maxLength = aMaxLen;
        this.decimal = aDec;
    }

    public DataTypeDB clones()
    {
        DataTypeDBImpl reObj = new DataTypeDBImpl();
        return reObj;
    }

    public String getDecimal()
    {
        String sRe = "0";
        if ((this.decimal != null) && (!(this.decimal.equals(""))))
            sRe = this.decimal;
        return sRe;
    }

    public String getJavaDataType()
    {
        return this.javaDataType;
    }

    public String getMaxLength()
    {
        String sRe = "0";
        if ((this.maxLength != null) && (!(this.maxLength.equals(""))))
            sRe = this.maxLength;
        return sRe;
    }

    public String getName()
    {
        return this.name;
    }

    public boolean setDecimal(String aDec)
    {
        this.decimal = aDec;
        return true;
    }

    public void setJavaDataType(String aJavaDataType)
    {
        this.javaDataType = aJavaDataType;
    }

    public boolean setMaxLength(String aMaxLen)
    {
        this.maxLength = aMaxLen;
        return true;
    }

    public boolean setName(String aName)
    {
        this.name = aName;
        return true;
    }

    public String toXmlString()
    {
        return null;
    }

}
