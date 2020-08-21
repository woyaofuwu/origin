package com.asiainfo.veris.crm.iorder.soa.group.ecintegration.check;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class EcIntegrationMebChkSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    
    public IDataset checkMemberBase(IData param) throws Exception
    {
        EcIntegrationMebChkBaseBean bean = new EcIntegrationMebChkBaseBean();
        return bean.checkEcIntegrationMember(param);
    }
    
    public IDataset checkMemberImsVpmn(IData param) throws Exception
    {
    	EcIntegrationMebChkImsVpmnBean bean = new EcIntegrationMebChkImsVpmnBean();
        return bean.checkEcIntegrationMember(param);
    }
    
    public IDataset checkMemberCloudWifi(IData param) throws Exception
    {
    	CloudWifiMebChkBean bean = new CloudWifiMebChkBean();
        return bean.checkEcIntegrationMember(param);
    }
    
    public IDataset checkMemberSumBusiness(IData param) throws Exception
    {
        SumBusinessTVMebChkBean bean = new SumBusinessTVMebChkBean();
        return bean.checkEcIntegrationMember(param);
    }
}
