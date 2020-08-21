/**
 * CWSInputData.java This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java
 * emitter.
 */

package com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard;

public class CWSInputData implements java.io.Serializable
{
    private java.lang.String mStrOrderID;

    private java.lang.String mStrSerialNumber;

    private int nPriority;

    private java.lang.String mStrSwitchid;

    private CWSServNode[] mVServList;

    private CWSVarNode[] mVVarList;

    private java.lang.Object __equalsCalc = null;

    private boolean __hashCodeCalc = false;

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(CWSInputData.class, true);

    static
    {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:WSSOP", "CWSInputData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MStrOrderID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "m-strOrderID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MStrSerialNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "m-strSerialNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NPriority");
        elemField.setXmlName(new javax.xml.namespace.QName("", "nPriority"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MStrSwitchid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "m-strSwitchid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MVServList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "m-vServList"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:WSSOP", "CWSServNode"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MVVarList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "m-vVarList"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:WSSOP", "CWSVarNode"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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

    public CWSInputData()
    {
    }

    public CWSInputData(java.lang.String mStrOrderID, java.lang.String mStrSerialNumber, int nPriority, java.lang.String mStrSwitchid, CWSServNode[] mVServList, CWSVarNode[] mVVarList)
    {
        this.mStrOrderID = mStrOrderID;
        this.mStrSerialNumber = mStrSerialNumber;
        this.nPriority = nPriority;
        this.mStrSwitchid = mStrSwitchid;
        this.mVServList = mVServList;
        this.mVVarList = mVVarList;
    }

    public synchronized boolean equals(java.lang.Object obj)
    {
        if (!(obj instanceof CWSInputData))
            return false;
        CWSInputData other = (CWSInputData) obj;
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
        _equals = true && ((this.mStrOrderID == null && other.getMStrOrderID() == null) || (this.mStrOrderID != null && this.mStrOrderID.equals(other.getMStrOrderID())))
                && ((this.mStrSerialNumber == null && other.getMStrSerialNumber() == null) || (this.mStrSerialNumber != null && this.mStrSerialNumber.equals(other.getMStrSerialNumber()))) && this.nPriority == other.getNPriority()
                && ((this.mStrSwitchid == null && other.getMStrSwitchid() == null) || (this.mStrSwitchid != null && this.mStrSwitchid.equals(other.getMStrSwitchid())))
                && ((this.mVServList == null && other.getMVServList() == null) || (this.mVServList != null && java.util.Arrays.equals(this.mVServList, other.getMVServList())))
                && ((this.mVVarList == null && other.getMVVarList() == null) || (this.mVVarList != null && java.util.Arrays.equals(this.mVVarList, other.getMVVarList())));
        __equalsCalc = null;
        return _equals;
    }

    /**
     * Gets the mStrOrderID value for this CWSInputData.
     * 
     * @return mStrOrderID
     */
    public java.lang.String getMStrOrderID()
    {
        return mStrOrderID;
    }

    /**
     * Gets the mStrSerialNumber value for this CWSInputData.
     * 
     * @return mStrSerialNumber
     */
    public java.lang.String getMStrSerialNumber()
    {
        return mStrSerialNumber;
    }

    /**
     * Gets the mStrSwitchid value for this CWSInputData.
     * 
     * @return mStrSwitchid
     */
    public java.lang.String getMStrSwitchid()
    {
        return mStrSwitchid;
    }

    /**
     * Gets the mVServList value for this CWSInputData.
     * 
     * @return mVServList
     */
    public CWSServNode[] getMVServList()
    {
        return mVServList;
    }

    public CWSServNode getMVServList(int i)
    {
        return this.mVServList[i];
    }

    /**
     * Gets the mVVarList value for this CWSInputData.
     * 
     * @return mVVarList
     */
    public CWSVarNode[] getMVVarList()
    {
        return mVVarList;
    }

    public CWSVarNode getMVVarList(int i)
    {
        return this.mVVarList[i];
    }

    /**
     * Gets the nPriority value for this CWSInputData.
     * 
     * @return nPriority
     */
    public int getNPriority()
    {
        return nPriority;
    }

    public synchronized int hashCode()
    {
        if (__hashCodeCalc)
        {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getMStrOrderID() != null)
        {
            _hashCode += getMStrOrderID().hashCode();
        }
        if (getMStrSerialNumber() != null)
        {
            _hashCode += getMStrSerialNumber().hashCode();
        }
        _hashCode += getNPriority();
        if (getMStrSwitchid() != null)
        {
            _hashCode += getMStrSwitchid().hashCode();
        }
        if (getMVServList() != null)
        {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getMVServList()); i++)
            {
                java.lang.Object obj = java.lang.reflect.Array.get(getMVServList(), i);
                if (obj != null && !obj.getClass().isArray())
                {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getMVVarList() != null)
        {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getMVVarList()); i++)
            {
                java.lang.Object obj = java.lang.reflect.Array.get(getMVVarList(), i);
                if (obj != null && !obj.getClass().isArray())
                {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    /**
     * Sets the mStrOrderID value for this CWSInputData.
     * 
     * @param mStrOrderID
     */
    public void setMStrOrderID(java.lang.String mStrOrderID)
    {
        this.mStrOrderID = mStrOrderID;
    }

    /**
     * Sets the mStrSerialNumber value for this CWSInputData.
     * 
     * @param mStrSerialNumber
     */
    public void setMStrSerialNumber(java.lang.String mStrSerialNumber)
    {
        this.mStrSerialNumber = mStrSerialNumber;
    }

    /**
     * Sets the mStrSwitchid value for this CWSInputData.
     * 
     * @param mStrSwitchid
     */
    public void setMStrSwitchid(java.lang.String mStrSwitchid)
    {
        this.mStrSwitchid = mStrSwitchid;
    }

    /**
     * Sets the mVServList value for this CWSInputData.
     * 
     * @param mVServList
     */
    public void setMVServList(CWSServNode[] mVServList)
    {
        this.mVServList = mVServList;
    }

    public void setMVServList(int i, CWSServNode _value)
    {
        this.mVServList[i] = _value;
    }

    /**
     * Sets the mVVarList value for this CWSInputData.
     * 
     * @param mVVarList
     */
    public void setMVVarList(CWSVarNode[] mVVarList)
    {
        this.mVVarList = mVVarList;
    }

    public void setMVVarList(int i, CWSVarNode _value)
    {
        this.mVVarList[i] = _value;
    }

    /**
     * Sets the nPriority value for this CWSInputData.
     * 
     * @param nPriority
     */
    public void setNPriority(int nPriority)
    {
        this.nPriority = nPriority;
    }

}
