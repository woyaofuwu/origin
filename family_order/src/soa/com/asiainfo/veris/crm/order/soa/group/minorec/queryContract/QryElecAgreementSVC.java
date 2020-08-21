package com.asiainfo.veris.crm.order.soa.group.minorec.queryContract;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QryElecAgreementSVC extends CSBizService{

    private static final long serialVersionUID = -5015406099262325272L;

    public IDataset queryAreaInfos(IData param) throws Exception{
        return QryElecAgreementBean.queryAreaInfos(param);
    }
    
    public IData queryOrgByKeyWord(IData param) throws Exception{
        return QryElecAgreementBean.queryOrgByKeyWord(param, getPagination());
    }
    
    public IDataset queryOrgInfosByAreaCode(IData param) throws Exception{
        return QryElecAgreementBean.queryOrgInfosByAreaCode(param);
    }
    
    public IDataset queryOrgInfosByParentOrgId(IData param) throws Exception{
        return QryElecAgreementBean.queryOrgInfosByParentOrgId(param);
    }
    

}
