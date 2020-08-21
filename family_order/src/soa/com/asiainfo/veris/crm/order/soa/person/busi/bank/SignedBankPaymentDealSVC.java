
package com.asiainfo.veris.crm.order.soa.person.busi.bank;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SignedBankPaymentDealSVC extends CSBizService
{
    private static Logger logger = Logger.getLogger(SignedBankPaymentDealSVC.class);

    private static final long serialVersionUID = 1L;

    public IDataset getBankInfo(IData input) throws Exception
    {
        SignedBankPaymentDealBean bean = (SignedBankPaymentDealBean) BeanManager.createBean(SignedBankPaymentDealBean.class);

        IDataset ret = bean.getBankPaymentInfo(input);
        return ret;
    }

}
