/**
 * WSSOPPortType.java This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT)
 * WSDL2Java emitter.
 */

package com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard;

import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.holders.CWSVarNodeHolder;

public interface WSSOPPortType extends java.rmi.Remote
{

    /**
     * Service definition of function ns__CallWSSOP
     */
    public void callWSSOP(CWSInputData objInputData, javax.xml.rpc.holders.StringHolder mStrOrderID, javax.xml.rpc.holders.IntHolder mNOperationResult, javax.xml.rpc.holders.StringHolder mStrFinishTime,
            javax.xml.rpc.holders.StringHolder mStrErrorDescription, javax.xml.rpc.holders.IntHolder mNCMDCount, javax.xml.rpc.holders.StringHolder mStrAutoCMDList, CWSVarNodeHolder mVQueryResultList) throws java.rmi.RemoteException;
}
