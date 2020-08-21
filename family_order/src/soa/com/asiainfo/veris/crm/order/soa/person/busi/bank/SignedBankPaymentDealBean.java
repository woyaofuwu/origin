
package com.asiainfo.veris.crm.order.soa.person.busi.bank;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.BankPaymentManageException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBankMainSignInfoQry;

public class SignedBankPaymentDealBean extends CSBizBean
{
    private static Logger logger = Logger.getLogger(SignedBankPaymentDealBean.class);

    public IDataset getBankPaymentInfo(IData param) throws Exception
    {
    	if(logger.isDebugEnabled()){
    		logger.debug("输入入参" + param);
    	}
        IDataset bankInfos = UserBankMainSignInfoQry.qryBankPaymentInfoList(param.getString("USER_ID"));

        if (IDataUtil.isEmpty(bankInfos))
        {
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_232);
        }

        IData bankInfo = new DataMap();

        bankInfo = bankInfos.getData(0);

        bankInfo.put("UPDATE_TIME", bankInfo.getString("UPDATE_TIME").subSequence(0, 19));
        bankInfo.put("START_TIME", bankInfo.getString("START_TIME").subSequence(0, 19));
        bankInfo.put("UPDATE_TIME", bankInfo.getString("END_TIME").subSequence(0, 19));

        return bankInfos;
    }

}
