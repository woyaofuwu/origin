
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.service;

public class CRMServicePortTypeProxy implements com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.service.CRMServicePortType
{
    private String _endpoint = null;

    private com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.service.CRMServicePortType cRMServicePortType = null;

    public CRMServicePortTypeProxy()
    {
        _initCRMServicePortTypeProxy();
    }

    public CRMServicePortTypeProxy(String endpoint)
    {
        _endpoint = endpoint;
        _initCRMServicePortTypeProxy();
    }

    private void _initCRMServicePortTypeProxy()
    {
        try
        {
            cRMServicePortType = (new com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.service.CRMServiceLocator()).getCRMServiceHttpPort();
            if (cRMServicePortType != null)
            {
                if (_endpoint != null)
                    ((javax.xml.rpc.Stub) cRMServicePortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
                else
                    _endpoint = (String) ((javax.xml.rpc.Stub) cRMServicePortType)._getProperty("javax.xml.rpc.service.endpoint.address");
            }

        }
        catch (javax.xml.rpc.ServiceException serviceException)
        {
        }
    }

    public java.lang.String encAssemDynData(java.lang.String in0) throws java.rmi.RemoteException
    {
        if (cRMServicePortType == null)
            _initCRMServicePortTypeProxy();
        return cRMServicePortType.encAssemDynData(in0);
    }

    public com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.service.CRMServicePortType getCRMServicePortType()
    {
        if (cRMServicePortType == null)
            _initCRMServicePortTypeProxy();
        return cRMServicePortType;
    }

    public String getEndpoint()
    {
        return _endpoint;
    }

    public java.lang.String localDecEncyPreData(java.lang.String in0) throws java.rmi.RemoteException
    {
        if (cRMServicePortType == null)
            _initCRMServicePortTypeProxy();
        return cRMServicePortType.localDecEncyPreData(in0);
    }

    public java.lang.String roamDecEncyPreData(java.lang.String in0) throws java.rmi.RemoteException
    {
        if (cRMServicePortType == null)
            _initCRMServicePortTypeProxy();
        return cRMServicePortType.roamDecEncyPreData(in0);
    }

    public void setEndpoint(String endpoint)
    {
        _endpoint = endpoint;
        if (cRMServicePortType != null)
            ((javax.xml.rpc.Stub) cRMServicePortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

    }

    public java.lang.String writeCardStatus(java.lang.String in0) throws java.rmi.RemoteException
    {
        if (cRMServicePortType == null)
            _initCRMServicePortTypeProxy();
        return cRMServicePortType.writeCardStatus(in0);
    }

}
