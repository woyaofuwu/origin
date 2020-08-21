
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetfeereg.cttqryfeereg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CttQryFeeRegSVC extends CSBizService
{
    public IDataset qryFeeRegCTT(IData input) throws Exception
    {
        CttQryFeeRegBean bean = (CttQryFeeRegBean) BeanManager.createBean(CttQryFeeRegBean.class);
        return bean.qryFeeRegCTT(input, this.getPagination());
    }
}
