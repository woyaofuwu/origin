/**
 * CWSVarNode.java This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java
 * emitter.
 */

package com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard;

import java.util.ArrayList;

public class CWSVarNode extends ArrayList implements java.io.Serializable
{
    private java.lang.String mStrName;

    private java.lang.String mStrValue;

    private java.lang.Object __equalsCalc = null;

    private boolean __hashCodeCalc = false;

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(CWSVarNode.class, true);

    static
    {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:WSSOP", "CWSVarNode"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MStrName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "m-strName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MStrValue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "m-strValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(java.lang.String mechType, java.lang.Class _javaType, javax.xml.namespace.QName _xmlType)
    {
        return new org.apache.axis.encoding.ser.BeanDeserializer(_javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(java.lang.String mechType, java.lang.Class _javaType, javax.xml.namespace.QName _xmlType)
    {
        return new org.apache.axis.encoding.ser.BeanSerializer(_javaType, _xmlType, typeDesc);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc()
    {
        return typeDesc;
    }

    public CWSVarNode()
    {
    }

    public CWSVarNode(java.lang.String mStrName, java.lang.String mStrValue)
    {
        this.mStrName = mStrName;
        this.mStrValue = mStrValue;
    }

    public synchronized boolean equals(java.lang.Object obj)
    {
        if (!(obj instanceof CWSVarNode))
            return false;
        CWSVarNode other = (CWSVarNode) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null)
        {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && ((this.mStrName == null && other.getMStrName() == null) || (this.mStrName != null && this.mStrName.equals(other.getMStrName())))
                && ((this.mStrValue == null && other.getMStrValue() == null) || (this.mStrValue != null && this.mStrValue.equals(other.getMStrValue())));
        __equalsCalc = null;
        return _equals;
    }

    /**
     * Gets the mStrName value for this CWSVarNode.
     * 
     * @return mStrName
     */
    public java.lang.String getMStrName()
    {
        return mStrName;
    }

    /**
     * Gets the mStrValue value for this CWSVarNode.
     * 
     * @return mStrValue
     */
    public java.lang.String getMStrValue()
    {
        return mStrValue;
    }

    public synchronized int hashCode()
    {
        if (__hashCodeCalc)
        {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getMStrName() != null)
        {
            _hashCode += getMStrName().hashCode();
        }
        if (getMStrValue() != null)
        {
            _hashCode += getMStrValue().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    /**
     * Sets the mStrName value for this CWSVarNode.
     * 
     * @param mStrName
     */
    public void setMStrName(java.lang.String mStrName)
    {
        this.mStrName = mStrName;
    }

    /**
     * Sets the mStrValue value for this CWSVarNode.
     * 
     * @param mStrValue
     */
    public void setMStrValue(java.lang.String mStrValue)
    {
        this.mStrValue = mStrValue;
    }

}
