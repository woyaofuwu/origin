
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CheckUserVoucherSVC extends CSBizService
{

    public IDataset checkUserVoucher(IData data) throws Exception
    {
        CheckUserVoucherBean qryBean = BeanManager.createBean(CheckUserVoucherBean.class);
        qryBean.checkUserVoucher(data);
        return new DatasetList();
    }

}
