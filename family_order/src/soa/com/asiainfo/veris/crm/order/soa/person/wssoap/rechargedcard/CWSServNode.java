/**
 * CWSServNode.java This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java
 * emitter.
 */

package com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard;

public class CWSServNode implements java.io.Serializable
{
    private java.lang.String mStrServName;

    private java.lang.Object __equalsCalc = null;

    private boolean __hashCodeCalc = false;

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(CWSServNode.class, true);

    static
    {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:WSSOP", "CWSServNode"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MStrServName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "m-strServName"));
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

    public CWSServNode()
    {
    }

    public CWSServNode(java.lang.String mStrServName)
    {
        this.mStrServName = mStrServName;
    }

    public synchronized boolean equals(java.lang.Object obj)
    {
        if (!(obj instanceof CWSServNode))
            return false;
        CWSServNode other = (CWSServNode) obj;
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
        _equals = true && ((this.mStrServName == null && other.getMStrServName() == null) || (this.mStrServName != null && this.mStrServName.equals(other.getMStrServName())));
        __equalsCalc = null;
        return _equals;
    }

    /**
     * Gets the mStrServName value for this CWSServNode.
     * 
     * @return mStrServName
     */
    public java.lang.String getMStrServName()
    {
        return mStrServName;
    }

    public synchronized int hashCode()
    {
        if (__hashCodeCalc)
        {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getMStrServName() != null)
        {
            _hashCode += getMStrServName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    /**
     * Sets the mStrServName value for this CWSServNode.
     * 
     * @param mStrServName
     */
    public void setMStrServName(java.lang.String mStrServName)
    {
        this.mStrServName = mStrServName;
    }

}
