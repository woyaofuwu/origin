
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetfeereg.cttqryfeereg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CttQryBankFeeRegSVC extends CSBizService
{
    public IDataset qryBankFeeRegCTT(IData input) throws Exception
    {
        CttQryBankFeeRegBean bean = (CttQryBankFeeRegBean) BeanManager.createBean(CttQryBankFeeRegBean.class);
        return bean.qryBankFeeRegCTT(input, this.getPagination());
    }
}
