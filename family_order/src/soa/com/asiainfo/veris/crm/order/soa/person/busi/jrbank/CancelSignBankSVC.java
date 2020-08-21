package com.asiainfo.veris.crm.order.soa.person.busi.jrbank;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.RelationBankInfoQry;

/**
 * @CREATED
 */
public class CancelSignBankSVC extends CSBizService
{
    private static final long serialVersionUID = 7416263045210187376L;

    
    public IDataset loadInfos(IData data) throws Exception{
    	
    	IDataset bankList = RelationBankInfoQry.querySignBankList(data.getString("USER_ID"), getTradeEparchyCode());
    	
    	if(IDataUtil.isEmpty(bankList)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户没有签约!");
    	}
    	
    	return bankList;
    }
    
    

}
