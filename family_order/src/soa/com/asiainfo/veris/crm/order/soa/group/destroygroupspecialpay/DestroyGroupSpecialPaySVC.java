
package com.asiainfo.veris.crm.order.soa.group.destroygroupspecialpay;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class DestroyGroupSpecialPaySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset crtBat(IData inParam) throws Exception
    {
        DestroyGroupSpecialPayBean bean = new DestroyGroupSpecialPayBean();

        return bean.crtBat(inParam);
    }

}
