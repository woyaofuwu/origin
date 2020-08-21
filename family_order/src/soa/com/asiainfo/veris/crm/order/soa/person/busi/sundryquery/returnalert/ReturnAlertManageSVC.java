
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.returnalert;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ReturnAlertManageSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    public IDataset queryReturnAlert(IData data) throws Exception
    {
        ReturnAlertManageBean bean = (ReturnAlertManageBean) BeanManager.createBean(ReturnAlertManageBean.class);
        return bean.queryReturnAlert(data, getPagination());
    }

}
