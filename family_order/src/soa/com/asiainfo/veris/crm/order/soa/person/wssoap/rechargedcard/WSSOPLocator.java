/**
 * WSSOPLocator.java This file was auto-generated from WSDL by the Apache Axis WSDL2Java emitter.
 */

package com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard;

public class WSSOPLocator extends org.apache.axis.client.Service implements WSSOP_Service
{

    // gSOAP 2.7.9f generated service definition

    // Use to get a proxy class for WSSOP
    private java.lang.String WSSOP_address = "";

    // The WSDD service name defaults to the port name.
    private java.lang.String WSSOPWSDDServiceName = "WSSOP";

    private java.util.HashSet ports = null;

    public WSSOPLocator(String WSSOP_address)
    {
        this.WSSOP_address = WSSOP_address;
    }

    /**
     * For the given interface, get the stub implementation. If this service has no port for the given interface, then
     * ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException
    {
        try
        {
            if (WSSOPPortType.class.isAssignableFrom(serviceEndpointInterface))
            {
                WSSOPStub _stub = new WSSOPStub(new java.net.URL(WSSOP_address), this);
                _stub.setPortName(getWSSOPWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t)
        {
            javax.xml.rpc.ServiceException ex = new javax.xml.rpc.ServiceException(t);
            throw ex;
        }
        javax.xml.rpc.ServiceException ex = new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
        throw ex;
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
        String inputPortName = portName.getLocalPart();
        if ("WSSOP".equals(inputPortName))
        {
            return getWSSOP();
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
            ports.add(new javax.xml.namespace.QName("WSSOP"));
        }
        return ports.iterator();
    }

    public javax.xml.namespace.QName getServiceName()
    {
        return new javax.xml.namespace.QName("http://192.168.15.174:20000/", "WSSOP");
    }

    public WSSOPPortType getWSSOP() throws javax.xml.rpc.ServiceException
    {
        java.net.URL endpoint;
        try
        {
            endpoint = new java.net.URL(WSSOP_address);
        }
        catch (java.net.MalformedURLException e)
        {
            javax.xml.rpc.ServiceException ex = new javax.xml.rpc.ServiceException(e);
            throw ex;
        }
        return getWSSOP(endpoint);
    }

    public WSSOPPortType getWSSOP(java.net.URL portAddress) throws javax.xml.rpc.ServiceException
    {
        try
        {
            WSSOPStub _stub = new WSSOPStub(portAddress, this);
            _stub.setPortName(getWSSOPWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e)
        {
            return null;
        }
    }

    public java.lang.String getWSSOPAddress()
    {
        return WSSOP_address;
    }

    public java.lang.String getWSSOPWSDDServiceName()
    {
        return WSSOPWSDDServiceName;
    }

    public void setWSSOPWSDDServiceName(java.lang.String name)
    {
        WSSOPWSDDServiceName = name;
    }

}
