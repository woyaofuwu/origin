
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryusernpinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class NoOweFeeSVC extends CSBizService
{
    public IDataset getNpOutInfo(IData param) throws Exception
    {
        NoOweFeeBean bean = (NoOweFeeBean) BeanManager.createBean(NoOweFeeBean.class);
        return bean.getNpOutInfo(param, getPagination());
    }
}
