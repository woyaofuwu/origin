/**
 * CRMServiceLocator.java This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT)
 * WSDL2Java emitter.
 */

package com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.service;

public class CRMServiceLocator extends org.apache.axis.client.Service implements com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.service.CRMService
{

    // Use to get a proxy class for CRMServiceHttpPort
    private java.lang.String CRMServiceHttpPort_address = "http://10.199.48.62:8008/ops_hainanyidong/services/CRMService";

    // The WSDD service name defaults to the port name.
    private java.lang.String CRMServiceHttpPortWSDDServiceName = "CRMServiceHttpPort";

    private java.util.HashSet ports = null;

    public CRMServiceLocator()
    {
    }

    public CRMServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException
    {
        super(wsdlLoc, sName);
    }

    public CRMServiceLocator(org.apache.axis.EngineConfiguration config)
    {
        super(config);
    }

    public com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.service.CRMServicePortType getCRMServiceHttpPort() throws javax.xml.rpc.ServiceException
    {
        java.net.URL endpoint = null;
        try
        {
            endpoint = new java.net.URL(CRMServiceHttpPort_address);
        }
        catch (java.net.MalformedURLException e)
        {
            javax.xml.rpc.ServiceException se = new javax.xml.rpc.ServiceException(e);
            throw se;
        }
        return getCRMServiceHttpPort(endpoint);
    }

    public com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.service.CRMServicePortType getCRMServiceHttpPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException
    {
        try
        {
            com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.service.CRMServiceHttpBindingStub _stub = new com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.service.CRMServiceHttpBindingStub(portAddress, this);
            _stub.setPortName(getCRMServiceHttpPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e)
        {
            javax.xml.rpc.ServiceException se = new javax.xml.rpc.ServiceException(e);
            throw se;
        }
    }

    public java.lang.String getCRMServiceHttpPortAddress()
    {
        return CRMServiceHttpPort_address;
    }

    public java.lang.String getCRMServiceHttpPortWSDDServiceName()
    {
        return CRMServiceHttpPortWSDDServiceName;
    }

    /**
     * For the given interface, get the stub implementation. If this service has no port for the given interface, then
     * ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException
    {
        try
        {
            if (com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.service.CRMServicePortType.class.isAssignableFrom(serviceEndpointInterface))
            {
                com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.service.CRMServiceHttpBindingStub _stub = new com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.service.CRMServiceHttpBindingStub(new java.net.URL(CRMServiceHttpPort_address), this);
                _stub.setPortName(getCRMServiceHttpPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t)
        {

        }
        return null;
    }

    /**
     * For the given interface, get the stub implementation. If this service has no port for the given interface, then
     * ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException
    {
        if (portName == null)
        {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("CRMServiceHttpPort".equals(inputPortName))
        {
            return getCRMServiceHttpPort();
        }
        else
        {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public java.util.Iterator getPorts()
    {
        if (ports == null)
        {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://com.eastcompeace.ops/CRMService", "CRMServiceHttpPort"));
        }
        return ports.iterator();
    }

    public javax.xml.namespace.QName getServiceName()
    {
        return new javax.xml.namespace.QName("http://com.eastcompeace.ops/CRMService", "CRMService");
    }

    public void setCRMServiceHttpPortEndpointAddress(java.lang.String address)
    {
        CRMServiceHttpPort_address = address;
    }

    public void setCRMServiceHttpPortWSDDServiceName(java.lang.String name)
    {
        CRMServiceHttpPortWSDDServiceName = name;
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException
    {

        if ("CRMServiceHttpPort".equals(portName))
        {
            setCRMServiceHttpPortEndpointAddress(address);
        }
        else
        {

        }
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException
    {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
