/**
 * WSSOPStub.java This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java
 * emitter.
 */

package com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.holders.CWSVarNodeHolder;

public class WSSOPStub extends org.apache.axis.client.Stub implements WSSOPPortType
{
    private static final Logger Log = Logger.getLogger(WSSOPStub.class);

    private java.util.Vector cachedSerClasses = new java.util.Vector();

    private java.util.Vector cachedSerQNames = new java.util.Vector();

    private java.util.Vector cachedSerFactories = new java.util.Vector();

    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    public ArrayList al;

    static org.apache.axis.description.OperationDesc[] _operations;

    static
    {
        _operations = new org.apache.axis.description.OperationDesc[1];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1()
    {
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CallWSSOP");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "objInputData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:WSSOP", "CWSInputData"), CWSInputData.class, false,
                false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "m-strOrderID"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"),
                java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "m-nOperationResult"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"),
                int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "m-strFinishTime"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"),
                java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "m-strErrorDescription"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"),
                java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "m-nCMDCount"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class,
                false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "m-strAutoCMDList"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"),
                java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "m-vQueryResultList"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("urn:WSSOP", "CWSVarNode"), CWSVarNode.class, false,
                false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[0] = oper;

    }

    public WSSOPStub() throws org.apache.axis.AxisFault
    {
        this(null);
    }

    public WSSOPStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault
    {
        this(service);
        super.cachedEndpoint = endpointURL;
    }

    public WSSOPStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault
    {
        if (service == null)
        {
            super.service = new org.apache.axis.client.Service();
        }
        else
        {
            super.service = service;
        }
        ((org.apache.axis.client.Service) super.service).setTypeMappingVersion("1.2");
        java.lang.Class cls;
        javax.xml.namespace.QName qName;
        javax.xml.namespace.QName qName2;
        java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
        java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
        java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
        java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
        java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
        java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
        java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
        java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
        java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
        java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
        qName = new javax.xml.namespace.QName("urn:WSSOP", "CWSInputData");
        cachedSerQNames.add(qName);
        cls = CWSInputData.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("urn:WSSOP", "CWSServNode");
        cachedSerQNames.add(qName);
        cls = CWSServNode.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("urn:WSSOP", "CWSVarNode");
        cachedSerQNames.add(qName);
        cls = CWSVarNode.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

    }

    public void callWSSOP(CWSInputData objInputData, javax.xml.rpc.holders.StringHolder mStrOrderID, javax.xml.rpc.holders.IntHolder mNOperationResult, javax.xml.rpc.holders.StringHolder mStrFinishTime,
            javax.xml.rpc.holders.StringHolder mStrErrorDescription, javax.xml.rpc.holders.IntHolder mNCMDCount, javax.xml.rpc.holders.StringHolder mStrAutoCMDList, CWSVarNodeHolder mVQueryResultList) throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            org.apache.axis.NoEndPointException ex = new org.apache.axis.NoEndPointException();
            throw ex;
        }

        org.apache.axis.client.Call _call = createCall();

        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:WSSOP", "CallWSSOP"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[]
            { objInputData });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                java.util.Map _output;
                _output = _call.getOutputParams();
                try
                {
                    mStrOrderID.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("", "m-strOrderID"));
                }
                catch (java.lang.Exception _exception)
                {
                    mStrOrderID.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("", "m-strOrderID")), java.lang.String.class);
                }
                try
                {
                    mNOperationResult.value = ((java.lang.Integer) _output.get(new javax.xml.namespace.QName("", "m-nOperationResult"))).intValue();
                }
                catch (java.lang.Exception _exception)
                {
                    mNOperationResult.value = ((java.lang.Integer) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("", "m-nOperationResult")), int.class)).intValue();
                }
                try
                {
                    mStrFinishTime.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("", "m-strFinishTime"));
                }
                catch (java.lang.Exception _exception)
                {
                    mStrFinishTime.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("", "m-strFinishTime")), java.lang.String.class);
                }
                try
                {
                    mStrErrorDescription.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("", "m-strErrorDescription"));
                }
                catch (java.lang.Exception _exception)
                {
                    mStrErrorDescription.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("", "m-strErrorDescription")), java.lang.String.class);
                }
                try
                {
                    mNCMDCount.value = ((java.lang.Integer) _output.get(new javax.xml.namespace.QName("", "m-nCMDCount"))).intValue();
                }
                catch (java.lang.Exception _exception)
                {
                    mNCMDCount.value = ((java.lang.Integer) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("", "m-nCMDCount")), int.class)).intValue();
                }
                try
                {
                    mStrAutoCMDList.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("", "m-strAutoCMDList"));
                }
                catch (java.lang.Exception _exception)
                {
                    mStrAutoCMDList.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("", "m-strAutoCMDList")), java.lang.String.class);
                }
                try
                {
                    this.al = (ArrayList) _output.get(new javax.xml.namespace.QName("", "m-vQueryResultList"));
                    mVQueryResultList.value = (CWSVarNode) _output.get(new javax.xml.namespace.QName("", "m-vQueryResultList"));
                    // this.al = mVQueryResultList;
                }
                catch (java.lang.Exception _exception)
                {
                    mVQueryResultList.value = (CWSVarNode) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("", "m-vQueryResultList")), CWSVarNode.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault e)
        {
            org.apache.axis.AxisFault ex = new org.apache.axis.AxisFault();
            throw ex;
        }
    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException
    {
        try
        {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet)
            {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null)
            {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null)
            {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null)
            {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null)
            {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null)
            {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements())
            {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this)
            {
                if (firstCall())
                {
                    // must set encoding style before registering serializers
                    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
                    _call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);
                    for (int i = 0; i < cachedSerFactories.size(); ++i)
                    {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName = (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class)
                        {
                            java.lang.Class sf = (java.lang.Class) cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class) cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory)
                        {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory) cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory) cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t)
        {
            org.apache.axis.AxisFault ex = new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
            throw ex;
        }
    }

}
