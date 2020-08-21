
package com.asiainfo.veris.crm.order.soa.person.busi.bank;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBankMainSignInfoQry;

public class SignedBankPaymentBean extends CSBizBean
{
    private static Logger logger = Logger.getLogger(SignedBankPaymentBean.class);

    public IDataset getBankInfo(IData param) throws Exception
    {
    	if(logger.isDebugEnabled()){
    		logger.debug("输入入参" + param);
    	}
        return UserBankMainSignInfoQry.qryBankInfoList(param.getString("SERIAL_NUMBER"));
    }

}
